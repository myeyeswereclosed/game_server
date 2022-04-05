package com.belov.game_server.config

import com.belov.game_server.domain.players.ConnectedPlayer.Tokens
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object AppConfig {

  /** simplification */
  def load(): Config = ConfigSource.default.loadOrThrow[Config]

  case class Config(
    server: ServerConfig,
    player: PlayerConfig,
    simpleGames: SimpleGamesConfig
  )

  case class SimpleGamesConfig(
    oneCard: OneCardSimpleGameConfig,
    twoCards: TwoCardSimpleGameConfig,
  )

  case class OneCardSimpleGameConfig(
    results: ResultsConfig,
    playersNumber: Int
  )

  case class TwoCardSimpleGameConfig(
    results: ResultsConfig,
    playersNumber: Int
  )

  case class ServerConfig(host: String, port: Int)

  case class PlayerConfig(initialBalance: Int)

  case class ResultsConfig(
    fold: FoldResultsConfig,
    play: PlayResultsConfig
  ) {
    val foldCost: Tokens = fold.foldCost
    val drawByFoldCost: Tokens = fold.drawByFoldsCost

    val winCost: Tokens = play.winCost
    val lossCost: Tokens = play.lossCost
    val drawCost: Tokens = play.drawCost
  }

  case class PlayResultsConfig(lossCost: Tokens, winCost: Tokens, drawCost: Tokens = 0)

  case class FoldResultsConfig(foldCost: Tokens, drawByFoldsCost: Tokens)
}
