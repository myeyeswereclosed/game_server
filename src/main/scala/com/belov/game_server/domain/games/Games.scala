package com.belov.game_server.domain.games

import java.util.UUID

import com.belov.game_server.domain.players.GamePlayer.{Player, PlayerDecision, Players}

object Games {

  trait Game[F[_]] {

    def gameId(): F[GameId]

    def start(): fs2.Stream[F, Unit]

    def handleDecision(decision: PlayerDecision): F[Unit]

  }

  /** players here is simplification/trick to find game by player */
  case class GameId(id: UUID, players: Players) {

    def ofPlayer(player: Player): Boolean = players.contains(player)

  }

}
