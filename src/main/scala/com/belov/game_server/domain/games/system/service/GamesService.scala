package com.belov.game_server.domain.games.system.service

import com.belov.game_server.domain.games.simple_game.GameType
import com.belov.game_server.domain.players.{ConnectedPlayer, Decision}

trait GamesService[F[_]] {

  def addGamePlayer(gameType: GameType, player: ConnectedPlayer[F]): F[Unit]

  def processDecision(decision: Decision, connectedPlayer: ConnectedPlayer[F]): F[Unit]

}
