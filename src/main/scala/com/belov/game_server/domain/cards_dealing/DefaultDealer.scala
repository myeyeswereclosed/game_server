package com.belov.game_server.domain.cards_dealing

import cats.effect.std.Random
import cats.effect.{Ref, Sync}
import cats.implicits._
import com.belov.game_server.domain.cards.Deck.{Cards, Deck}

class DefaultDealer[F[_]: Sync] extends CardDealer[F] {

  def deal(cardsNumber: Int, deck: Ref[F, Deck]): F[Cards] =
    for {
      initial <- deck.get
      random <- Random.scalaUtilRandom
      deckShuffled <- random.shuffleVector(initial.cards.toVector)
      _ <- deck.update(_ => Deck(deckShuffled))
      cards <- deck.modify(deck => (Deck(deck.cards.drop(cardsNumber)), deck.cards.take(cardsNumber)))
    } yield cards

}
