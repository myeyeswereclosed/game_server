package com.belov.game_server.domain.games

import com.belov.game_server.domain.players.GamePlayer.Player

object DecisionResults {

  sealed trait DecisionResult

  case object Loss extends DecisionResult
  case object Win extends DecisionResult
  case object Draw extends DecisionResult

  case class PlayerResult(player: Player, result: DecisionResult)

  type PlayersResults = Seq[PlayerResult]

}
