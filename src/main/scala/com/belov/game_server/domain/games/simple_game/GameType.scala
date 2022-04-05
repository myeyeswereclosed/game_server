package com.belov.game_server.domain.games.simple_game

import enumeratum.EnumEntry.Hyphencase
import enumeratum._

sealed trait GameType extends EnumEntry with Hyphencase

  case object GameType extends Enum[GameType] with CirceEnum[GameType] {

    case object SingleCardGame extends GameType
    case object DoubleCardGame extends GameType

    override def values: IndexedSeq[GameType] = findValues
  }
