package com.belov.game_server.domain.games

import com.belov.game_server.domain.players.ConnectedPlayer.Tokens
import com.belov.game_server.domain.players.GamePlayer.Player

object GameResults {

  case class PlayedGameResult(player: Player, result: PlayerTokensResult) {
    val hasDraw: Boolean = result match {
      case DrawTokens(_) => true
      case _ => false
    }

    val tokens: Tokens = result match {
      case WinTokens(tokens) => tokens
      case LoseTokens(tokens) => -tokens
      case DrawTokens(tokens) => tokens
    }
  }

  sealed trait GameResult {
    def drawOccurred: Boolean
  }

  case class Finished(results: Seq[PlayedGameResult]) extends GameResult {
    val drawOccurred: Boolean = results.exists(_.hasDraw)
  }

  case class Error(reason: String) extends GameResult {
    val drawOccurred: Boolean = false
  }

  sealed trait PlayerTokensResult

  case class WinTokens(tokens: Tokens) extends PlayerTokensResult
  case class LoseTokens(tokens: Tokens) extends PlayerTokensResult
  case class DrawTokens(tokens: Tokens) extends PlayerTokensResult

}

