package com.belov.game_server.domain.games.system

import cats.Parallel
import cats.effect.Async
import cats.effect.kernel.Sync
import cats.effect.std.Queue
import cats.implicits._
import com.belov.game_server.domain.games.Games.GameId
import com.belov.game_server.domain.games.PlayersGameType
import com.belov.game_server.domain.games.simple_game.SimpleGamesFactory
import com.belov.game_server.domain.players.ConnectedPlayer

class GamesSystem[F[_]: Async: Parallel](
  gamePlayersQueues: Map[PlayersGameType, Queue[F, ConnectedPlayer[F]]],
  finishedGamesQueue: Queue[F, GameId],
  gamesFactory: SimpleGamesFactory[F],
  runningGames: RunningGames[F]
) {

  def gamesStream: fs2.Stream[F, Unit] = {
    val playerQueuesStreams =
      gamePlayersQueues
        .toList
        .map { case (gameType, queue) => playersQueueStream(gameType, queue)}

    val finishedGamesStream =
      fs2
        .Stream
        .fromQueueUnterminated(finishedGamesQueue)
        .evalMap(game => Sync[F].delay(println(s"Removing $game")) *> runningGames.remove(game))

    val allStreams = finishedGamesStream :: playerQueuesStreams

    fs2.Stream(allStreams: _*).parJoinUnbounded
  }

  private def playersQueueStream(
    playersGameType: PlayersGameType,
    queue: Queue[F, ConnectedPlayer[F]]
  ): fs2.Stream[F, Unit] =
    fs2.Stream.emit(println(s"Starting stream for $playersGameType")) ++
      fs2.Stream
        .fromQueueUnterminated(queue)
        .chunkN(playersGameType.playersNumber, allowFewer = true)
        .flatMap {
          players =>
            for {
              game <- fs2.Stream.eval(gamesFactory.create(playersGameType.gameType, players.toList))
              _ <- fs2.Stream.eval(runningGames.add(game))
            } yield game.start()
        }.parJoinUnbounded

}
