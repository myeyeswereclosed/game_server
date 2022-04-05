package com.belov.game_server.domain.games.rules

import com.belov.game_server.domain.games.rules.CardsCombinations.CardsCombinationRule
import com.belov.game_server.domain.games.rules.CardsCombinationsSorting.CombinationsSorting
import com.belov.game_server.domain.players.GamePlayer.{PlayerCardsCombination, PlayersCardsCombinations, PlayersHands}

case class GameOpenCardsRules(combinations: CardsCombinationRule, sorting: CombinationsSorting) {

  def appliedToHands(playerHands: PlayersHands): Seq[PlayersCardsCombinations] =
    playerHands
      .map(
        playerHand =>
          sorting.sort(
            combinations
              .combine(playerHand.cards)
              .map(combination => PlayerCardsCombination(playerHand.player, combination))
          )(_.combination)
      )

}
