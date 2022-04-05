package com.belov.game_server.domain.games.rules

import com.belov.game_server.domain.cards.Deck.{Card, Cards}

object CardsCombinations {

  sealed trait CardsCombinationRule {
    def combine(cards: Cards): CardsCombinations
  }

  case object SimpleCombination extends CardsCombinationRule {
    def combine(cards: Cards): Seq[JustCard] = cards.map(JustCard)
  }


  sealed trait CardsCombination {
    val rank: Int
  }

  type CardsCombinations = Seq[CardsCombination]

  case class JustCard(card: Card) extends CardsCombination {
    val rank: Int = card.rankByValue
  }

}
