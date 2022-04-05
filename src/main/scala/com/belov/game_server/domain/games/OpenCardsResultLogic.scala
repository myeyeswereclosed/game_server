package com.belov.game_server.domain.games

import com.belov.game_server.domain.games.DecisionResults.PlayersResults
import com.belov.game_server.domain.players.GamePlayer.PlayersHands

trait OpenCardsResultLogic {

  def run(playerHands: PlayersHands): PlayersResults

}
