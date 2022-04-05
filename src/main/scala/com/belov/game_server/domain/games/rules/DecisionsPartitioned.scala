package com.belov.game_server.domain.games.rules

case class DecisionsPartitioned[T](top: List[T], others: List[T]) {

  def mapToList[R](forSingleTop: (T => R, T => R), forMultipleTops: (List[T] => List[R], T => R)): List[R] =
    if (top.size == 1)
      forSingleTop._1(top.head) :: others.map(forSingleTop._2)
    else
      forMultipleTops._1(top) ::: others.map(forMultipleTops._2)

}
