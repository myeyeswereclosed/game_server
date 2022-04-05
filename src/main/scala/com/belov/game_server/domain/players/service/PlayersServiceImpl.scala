package com.belov.game_server.domain.players.service

import cats.effect.std.{Queue, UUIDGen}
import cats.effect.{Async, Ref}
import cats.implicits._
import com.belov.game_server.config.AppConfig.PlayerConfig
import com.belov.game_server.domain.games.simple_game.GameType
import com.belov.game_server.domain.players.ConnectedPlayer
import com.belov.game_server.domain.players.GamePlayer.Player
import com.belov.game_server.messages.service.ServiceMessages.{ChooseGame, PlayerBalance, ServiceMessage}

class PlayersServiceImpl[F[_]: Async](playerConfig: PlayerConfig) extends PlayersService[F] {

  def createPlayer(): F[ConnectedPlayer[F]] =
    for {
      playerMessages <- Queue.unbounded[F, ServiceMessage]
      player <- UUIDGen.randomUUID.map(Player)
      initialBalance = playerConfig.initialBalance
      balanceRef <- Ref.of(playerConfig.initialBalance)
      connectedPlayer = ConnectedPlayer(player, balanceRef, playerMessages)
      _ <- connectedPlayer.handleMessage(PlayerBalance(player.id, initialBalance))
      _ <- connectedPlayer.handleMessage(ChooseGame(player.id, GameType.values))
    } yield connectedPlayer

}
