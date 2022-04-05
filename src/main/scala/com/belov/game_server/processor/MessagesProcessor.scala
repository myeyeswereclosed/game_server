package com.belov.game_server.processor

import com.belov.game_server.domain.players.ConnectedPlayer

trait MessagesProcessor[F[_]] {

  def process(player: ConnectedPlayer[F], message: String): F[Unit]

}
