package com.belov.game_server.domain.players

import java.util.UUID

import com.belov.game_server.domain.cards.Deck.{Card, Cards}
import com.belov.game_server.domain.games.rules.CardsCombinations.CardsCombination

object GamePlayer {

  case class Player(id: UUID)

  case class Hand(cards: Seq[Card])

  case class PlayerCardsCombination(player: Player, combination: CardsCombination)

  case class PlayerHand(player: Player, hand: Hand) {
    val cards: Cards = hand.cards
  }

  case class PlayerDecision(player: Player, decision: Decision)

  case class PlayerHandDecision(playerHand: PlayerHand, decision: Decision) {
    val player: Player = playerHand.player
  }

  type Players = Seq[Player]
  type PlayersHands = Seq[PlayerHand]
  type PlayersDecisions = Seq[PlayerDecision]
  type PlayersHandsDecisions = Seq[PlayerHandDecision]
  type PlayersCardsCombinations = Seq[PlayerCardsCombination]

}

