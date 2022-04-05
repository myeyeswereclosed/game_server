package com.belov.game_server.domain.players

import java.util.UUID

import cats.effect.std.Queue
import cats.effect.{Concurrent, Ref}
import cats.implicits._
import com.belov.game_server.domain.players.ConnectedPlayer.Tokens
import com.belov.game_server.domain.players.GamePlayer.Player
import com.belov.game_server.messages.service.ServiceMessages.ServiceMessage

case class ConnectedPlayer[F[_] : Concurrent](
  player: Player,
  balance: Ref[F, Int],
  messages: Queue[F, ServiceMessage]
) {
  val id: UUID = player.id

  def handleMessage(message: ServiceMessage): F[Unit] = messages.offer(message)

  def updateBalance(tokens: Tokens): F[Tokens] =
    balance
      .update(current => current + tokens)
      .flatMap(_ => balance.get)
}

object ConnectedPlayer {
  type Tokens = Int
}


