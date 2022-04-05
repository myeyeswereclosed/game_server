package com.belov.game_server.domain.cards

import com.belov.game_server.domain.cards.Deck.Rank.{Five, Four, Three, Two}
import enumeratum.EnumEntry.Lowercase
import enumeratum._

object Deck {

  sealed trait Suit extends EnumEntry with Lowercase

  case object Suit extends Enum[Suit] with CirceEnum[Suit] {

    case object Clubs extends Suit
    case object Diamonds extends Suit
    case object Hearts extends Suit
    case object Spades extends Suit

    override def values: IndexedSeq[Suit] = findValues
  }

  sealed trait Rank extends EnumEntry with Lowercase

  case object Rank extends Enum[Rank] with CirceEnum[Rank] {

    implicit val ordering: Ordering[Rank] =
      Ordering.fromLessThan((first, second) => Rank.valuesToIndex(first) > Rank.valuesToIndex(second))

    case object Two extends Rank
    case object Three extends Rank
    case object Four extends Rank
    case object Five extends Rank
    case object Six extends Rank
    case object Seven extends Rank
    case object Eight extends Rank
    case object Nine extends Rank
    case object Ten extends Rank
    case object Jack extends Rank
    case object Queen extends Rank
    case object King extends Rank
    case object Ace extends Rank

    override def values: IndexedSeq[Rank] = findValues
  }

  case class Card(rank: Rank, suit: Suit) {
    val rankByValue: Int = Rank.valuesToIndex(rank)
  }

  type Cards = Seq[Card]

  case class Deck(cards: Cards)

  object Deck {
    def full: Deck =
      new Deck(
        for {
          rank <- Rank.values
          suit <- Suit.values
        } yield Card(rank, suit)
      )

    /** example */
    def short: Deck =
      new Deck(
        for {
          rank <- Rank.values.filterNot(Seq(Two, Three, Four, Five).contains)
          suit <- Suit.values
        } yield Card(rank, suit)
      )
  }

}
