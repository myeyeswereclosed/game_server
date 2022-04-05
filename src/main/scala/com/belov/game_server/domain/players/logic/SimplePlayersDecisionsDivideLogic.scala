package com.belov.game_server.domain.players.logic

import com.belov.game_server.domain.players.Decision.{Fold, Play}
import com.belov.game_server.domain.players.GamePlayer.PlayersHandsDecisions
import com.belov.game_server.domain.players.logic.PlayersDividing._

class SimplePlayersDecisionsDivideLogic extends PlayersDecisionsDivideLogic {

  def divide(playerHandDecisions: PlayersHandsDecisions): PlayersDecisionsDivided =
    if (playerHandDecisions.forall(_.decision == Fold))
      AllFold
    else if (playerHandDecisions.forall(_.decision == Play))
      AllPlay
    else
      (PlaysAndFolds.apply _).tupled(playerHandDecisions.partition(_.decision == Play))

}
