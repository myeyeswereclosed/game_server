package com.belov.game_server.domain.games.simple_game

import com.belov.game_server.domain.games.DecisionResults._
import com.belov.game_server.domain.games.OpenCardsResultLogic
import com.belov.game_server.domain.games.rules.{DecisionsPartitioned, GameOpenCardsRules}
import com.belov.game_server.domain.players.GamePlayer.{PlayerCardsCombination, Players, PlayersCardsCombinations, PlayersHands}

class SimpleGameOpenCardsResultLogic(openCardsRules: GameOpenCardsRules) extends OpenCardsResultLogic {

  def run(playerHands: PlayersHands): PlayersResults = {
    val playersCardsCombinations: Seq[PlayersCardsCombinations] =
      openCardsRules
        .appliedToHands(playerHands)
        .transpose

    results(List.empty[PlayerResult], playersCardsCombinations.toList, playersCardsCombinations.head.map(_.player))
  }

  private val combinationToResult = (decisionResult: DecisionResult) =>
    (combination: PlayerCardsCombination) => PlayerResult(combination.player, decisionResult)

  private val oneWinnerF = (combinationToResult(Win), combinationToResult(Loss))
  private val loseF = combinationToResult(Loss)
  private val drawF = combinationToResult(Draw)

  private def results(
    acc: List[PlayerResult],
    playerCardsCombinations: List[PlayersCardsCombinations],
    playersStayed: Players
  ): List[PlayerResult] =
    playerCardsCombinations match {
      case head :: Nil =>
        filteredByPlayers(head, playersStayed)
          .mapToList(
            forSingleTop = oneWinnerF,
            forMultipleTops =
              (
                (withHighestRank: List[PlayerCardsCombination]) => withHighestRank.map(drawF),
                loseF
              )
          )
      case head :: next =>
        filteredByPlayers(head, playersStayed)
          .mapToList(
            forSingleTop = oneWinnerF,
            forMultipleTops =
              (
                (withHighestRank: List[PlayerCardsCombination]) => results(acc, next, withHighestRank.map(_.player)),
                loseF
              )
          )
      case Nil => acc
    }

  private def filteredByPlayers(combinations: PlayersCardsCombinations, filter: Players) =
    combinationsPartitioned(combinations.filter(combination => filter.contains(combination.player)).toList)

  private def combinationsPartitioned(
    combinations: List[PlayerCardsCombination]
  ): DecisionsPartitioned[PlayerCardsCombination] = {
    val highestRank = openCardsRules.sorting.sort(combinations)(_.combination).head.combination.rank

    (DecisionsPartitioned.apply[PlayerCardsCombination] _)
      .tupled(combinations.partition(_.combination.rank == highestRank))
  }

}
