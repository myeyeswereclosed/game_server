package com.belov.game_server.domain.games.rules

import com.belov.game_server.domain.games.rules.CardsCombinations.CardsCombination

object CardsCombinationsSorting {

  sealed trait CombinationsSorting {
    def sort[T](entities: Seq[T])(f: T => CardsCombination): Seq[T]
  }

  case object HighestFirst extends CombinationsSorting {
    def sort[T](entities: Seq[T])(f: T => CardsCombination): Seq[T] =
      entities.sortBy(entity => -f(entity).rank)
  }

}
