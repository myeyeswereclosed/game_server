package com.belov.game_server.domain.players.logic

import com.belov.game_server.domain.players.GamePlayer.PlayerHandDecision

object PlayersDividing {

  sealed trait PlayersDecisionsDivided

  case object AllFold extends PlayersDecisionsDivided
  case object AllPlay extends PlayersDecisionsDivided
  case class PlaysAndFolds(plays: Seq[PlayerHandDecision], folds: Seq[PlayerHandDecision]) extends PlayersDecisionsDivided

}
