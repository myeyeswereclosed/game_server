package com.belov.game_server.messages.service

import java.util.UUID

import com.belov.game_server.domain.cards.Deck.Card
import com.belov.game_server.domain.games.GameResults.PlayedGameResult
import com.belov.game_server.domain.games.simple_game.GameType
import com.belov.game_server.domain.players.ConnectedPlayer.Tokens
import com.belov.game_server.domain.players.Decision
import io.circe.generic.auto._
import io.circe.syntax._

object ServiceMessages {

  /** simplification */
  sealed trait ServiceMessage {
    def asString: String = this.asJson.noSpaces
  }

  case class PlayerBalance(playerId: UUID, balance: Tokens) extends ServiceMessage

  case class ChooseGame(playerId: UUID, gameTypes: Seq[GameType]) extends ServiceMessage

  case class TakeCards(gameId: UUID, cards: Seq[Card], decisions: Seq[Decision]) extends ServiceMessage

  case class GameFinished(gameId: UUID, result: PlayedGameResult, balance: Tokens) extends ServiceMessage

  case class DrawOccurred(gameId: UUID) extends ServiceMessage

  case class ErrorOccurred(description: String) extends ServiceMessage

}
