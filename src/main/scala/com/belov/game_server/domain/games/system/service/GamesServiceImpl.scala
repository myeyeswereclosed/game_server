package com.belov.game_server.domain.games.system.service

import cats.Parallel
import cats.effect.Async
import cats.effect.kernel.Sync
import cats.effect.std.Queue
import cats.implicits._
import com.belov.game_server.domain.games.PlayersGameType
import com.belov.game_server.domain.games.simple_game.GameType
import com.belov.game_server.domain.games.system.RunningGames
import com.belov.game_server.domain.players.GamePlayer.PlayerDecision
import com.belov.game_server.domain.players.{ConnectedPlayer, Decision}

class GamesServiceImpl[F[_]: Async: Parallel](
  gamePlayersQueues: Map[PlayersGameType, Queue[F, ConnectedPlayer[F]]],
  games: RunningGames[F]
) extends GamesService[F] {

  def addGamePlayer(gameType: GameType, player: ConnectedPlayer[F]): F[Unit] = {
    gamePlayersQueues
      .collectFirst {
        case (playerGameType, queue) if playerGameType.gameType == gameType => queue.offer(player)
      }
      .getOrElse(Sync[F].unit)
  }

  def processDecision(decision: Decision, connectedPlayer: ConnectedPlayer[F]): F[Unit] =
    for {
      _ <- Sync[F].delay(println(s"Processing $decision"))
      maybeGame <- games.find(connectedPlayer.player)
      _ <- Sync[F].delay(println(s"Game is $maybeGame"))
      _ <-
        maybeGame
          .map(_.handleDecision(PlayerDecision(connectedPlayer.player, decision)))
          .getOrElse(Sync[F].delay(println(s"No game found fo $connectedPlayer")))
    } yield ()

}
