package com.belov.game_server.domain.players.logic

import com.belov.game_server.domain.players.GamePlayer.PlayersHandsDecisions
import com.belov.game_server.domain.players.logic.PlayersDividing.PlayersDecisionsDivided

trait PlayersDecisionsDivideLogic {

  def divide(playerHandDecisions: PlayersHandsDecisions): PlayersDecisionsDivided

}
