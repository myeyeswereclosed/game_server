package com.belov.game_server.messages.player

import com.belov.game_server.domain.games.simple_game.GameType
import com.belov.game_server.domain.players.Decision

object PlayerMessages {

  sealed trait PlayerMessage

  case class StartGame(gameType: GameType) extends PlayerMessage

  case class DecisionMade(decision: Decision) extends PlayerMessage

}


