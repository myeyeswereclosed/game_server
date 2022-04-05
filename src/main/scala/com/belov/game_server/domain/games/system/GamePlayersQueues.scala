package com.belov.game_server.domain.games.system

import cats.effect.IO
import cats.effect.std.Queue
import cats.implicits._
import com.belov.game_server.config.AppConfig.Config
import com.belov.game_server.domain.games.PlayersGameType
import com.belov.game_server.domain.games.simple_game.GameType
import com.belov.game_server.domain.players.ConnectedPlayer

object GamePlayersQueues {

  def create(config: Config): IO[Map[PlayersGameType, Queue[IO, ConnectedPlayer[IO]]]] =
    GameType
      .values
      .toList
      .traverse {
        case gameType@GameType.SingleCardGame =>
          makeQueue(PlayersGameType(gameType, config.simpleGames.oneCard.playersNumber))
        case gameType@GameType.DoubleCardGame =>
          makeQueue(PlayersGameType(gameType, config.simpleGames.twoCards.playersNumber))
      }
      .map(_.toMap)

  private def makeQueue(gameType: PlayersGameType): IO[(PlayersGameType, Queue[IO, ConnectedPlayer[IO]])] =
    Queue
      .unbounded[IO, ConnectedPlayer[IO]]
      .map(queue => gameType -> queue)

}
