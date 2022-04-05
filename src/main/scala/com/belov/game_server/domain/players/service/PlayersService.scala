package com.belov.game_server.domain.players.service

import com.belov.game_server.domain.players.ConnectedPlayer

trait PlayersService[F[_]] {

  def createPlayer(): F[ConnectedPlayer[F]]

}
