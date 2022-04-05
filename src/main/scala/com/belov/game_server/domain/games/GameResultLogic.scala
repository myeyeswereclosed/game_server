package com.belov.game_server.domain.games

import com.belov.game_server.domain.games.GameResults.GameResult
import com.belov.game_server.domain.players.GamePlayer.PlayersHandsDecisions

trait GameResultLogic {

  def run(playersHandsDecisions: PlayersHandsDecisions): GameResult

}
