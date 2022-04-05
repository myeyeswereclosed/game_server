package com.belov.game_server.domain.cards_dealing

import cats.effect.Ref
import com.belov.game_server.domain.cards.Deck.{Cards, Deck}

trait CardDealer[F[_]] {

  def deal(cardsNumber: Int, deck: Ref[F, Deck]): F[Cards]

}
