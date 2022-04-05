package com.belov.game_server.domain.games.system

import cats.effect.{Concurrent, Ref}
import cats.implicits._
import com.belov.game_server.domain.games.Games.{Game, GameId}
import com.belov.game_server.domain.players.GamePlayer.Player

class RunningGames[F[_]: Concurrent](gamesRef: Ref[F, Map[GameId, Game[F]]]) {

  def add(game: Game[F]): F[Game[F]] =
    for {
      gameId <- game.gameId()
      _ <- gamesRef.update(games => games + (gameId -> game))
    } yield game

  /** simplification : should search by gameId */
  def find(player: Player): F[Option[Game[F]]] =
    gamesRef
      .get
      .map(_.collectFirst { case (id, game) if id.ofPlayer(player) => game } )

  def remove(gameId: GameId): F[Unit] = gamesRef.update(_.removed(gameId))

}

object RunningGames {
  def create[F[_]: Concurrent]: F[RunningGames[F]] =
    Ref[F]
      .of(Map.empty[GameId, Game[F]])
      .map(games => new RunningGames(games))
}


