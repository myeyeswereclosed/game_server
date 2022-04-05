package com.belov.game_server

import cats.effect.std.Queue
import cats.effect.{IO, _}
import com.belov.game_server.config.AppConfig
import com.belov.game_server.config.AppConfig.ServerConfig
import com.belov.game_server.domain.games.Games.GameId
import com.belov.game_server.domain.games.simple_game.SimpleGamesFactory
import com.belov.game_server.domain.games.system.service.GamesServiceImpl
import com.belov.game_server.domain.games.system.{GamePlayersQueues, GamesSystem, RunningGames}
import com.belov.game_server.domain.players.service.PlayersServiceImpl
import com.belov.game_server.processor.parser.SimplePlayerMessageParser
import com.belov.game_server.processor.{MessagesProcessor, MessagesProcessorImpl}
import com.belov.game_server.router.GameRouter
import fs2.Stream
import org.http4s.blaze.server.BlazeServerBuilder

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      config <- IO(AppConfig.load())
      games <- RunningGames.create[IO]
      finishedGamesQueue <- Queue.unbounded[IO, GameId]
      playersService = new PlayersServiceImpl[IO](config.player)
      gamesFactory = new SimpleGamesFactory[IO](finishedGamesQueue, config.simpleGames)
      gamePlayersQueues <- GamePlayersQueues.create(config)
      gamesSystem = new GamesSystem(gamePlayersQueues, finishedGamesQueue, gamesFactory, games)
      gamesService = new GamesServiceImpl(gamePlayersQueues, games)
      messageProcessor = new MessagesProcessorImpl[IO](new SimplePlayerMessageParser, gamesService)
      server = startServer[IO](config.server, playersService, messageProcessor)
      _ <- Stream(server, gamesSystem.gamesStream).parJoinUnbounded.compile.drain
    } yield ExitCode.Success

  private def startServer[F[_]: Async: LiftIO](
    config: ServerConfig,
    playersService: PlayersServiceImpl[F],
    messagesProcessor: MessagesProcessor[F]
  ): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(host = config.host, port = config.port)
      .withHttpWebSocketApp(
        new GameRouter[F](playersService, messagesProcessor)
          .routes(_)
          .orNotFound
      )
      .serve
  }
}
