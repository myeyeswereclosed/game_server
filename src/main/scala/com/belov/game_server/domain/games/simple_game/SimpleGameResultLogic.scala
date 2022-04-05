package com.belov.game_server.domain.games.simple_game

import com.belov.game_server.config.AppConfig.ResultsConfig
import com.belov.game_server.domain.games.DecisionResults.{Draw, Loss, Win}
import com.belov.game_server.domain.games.GameResults._
import com.belov.game_server.domain.games.rules.DecisionsPartitioned
import com.belov.game_server.domain.games.{GameResultLogic, OpenCardsResultLogic}
import com.belov.game_server.domain.players.GamePlayer.{PlayerHand, PlayerHandDecision, PlayersHandsDecisions}
import com.belov.game_server.domain.players.logic.PlayersDecisionsDivideLogic
import com.belov.game_server.domain.players.logic.PlayersDividing.{AllFold, AllPlay, PlaysAndFolds}

class SimpleGameResultLogic(
  config: ResultsConfig,
  decisionsDivideLogic: PlayersDecisionsDivideLogic,
  openCardsLogic: OpenCardsResultLogic,
) extends GameResultLogic {

  private val decisionToResult = (tokensResult: PlayerTokensResult) => (playerHandDecision: PlayerHandDecision) =>
    PlayedGameResult(playerHandDecision.player, tokensResult)

  private val winOnFoldF = decisionToResult(WinTokens(config.foldCost))
  private val lossOnFoldF = decisionToResult(LoseTokens(config.foldCost))
  private val drawOnFoldF = decisionToResult(LoseTokens(config.drawByFoldCost))

  def run(playersHandsDecisions: PlayersHandsDecisions): GameResult =
    decisionsDivideLogic.divide(playersHandsDecisions) match {
      case AllFold => Finished(playersHandsDecisions.map(drawOnFoldF))
      case AllPlay => Finished(openCards(playersHandsDecisions.map(_.playerHand)))
      case PlaysAndFolds(plays, folds) =>
        Finished(
          DecisionsPartitioned(plays.toList, folds.toList)
            .mapToList(
              forSingleTop = (winOnFoldF, lossOnFoldF),
              forMultipleTops = (
                (decidedToPlay: List[PlayerHandDecision]) => openCards(decidedToPlay.map(_.playerHand)).toList,
                lossOnFoldF
              )
            )
        )
    }

  private def openCards(playersHands: Seq[PlayerHand]): Seq[PlayedGameResult] =
    openCardsLogic
      .run(playersHands)
      .map(
        playerResult => playerResult.result match {
          case Loss => PlayedGameResult(playerResult.player, LoseTokens(config.lossCost))
          case Win => PlayedGameResult(playerResult.player, WinTokens(config.winCost))
          case Draw => PlayedGameResult(playerResult.player, DrawTokens(config.drawCost))
        }
      )
}
