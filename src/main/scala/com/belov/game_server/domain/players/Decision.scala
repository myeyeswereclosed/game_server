package com.belov.game_server.domain.players

import enumeratum.EnumEntry.Lowercase
import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait Decision extends EnumEntry with Lowercase

case object Decision extends Enum[Decision] with CirceEnum[Decision] {

  case object Play extends Decision
  case object Fold extends Decision

  override def values: IndexedSeq[Decision] = findValues
}
