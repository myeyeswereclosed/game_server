package com.belov.game_server.domain.games

import cats.effect.Ref
import com.belov.game_server.domain.cards.Deck.Deck
import com.belov.game_server.domain.cards_dealing.CardDealer
import com.belov.game_server.domain.players.ConnectedPlayer

case class GameTable[F[_]](
  players: Seq[ConnectedPlayer[F]],
  dealer: CardDealer[F],
  deck: Ref[F, Deck]
)
