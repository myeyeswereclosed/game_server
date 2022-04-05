package com.belov.game_server.processor.parser

import com.belov.game_server.domain.games.simple_game.GameType
import com.belov.game_server.domain.players.Decision
import com.belov.game_server.messages.player.PlayerMessages.{DecisionMade, PlayerMessage, StartGame}

class SimplePlayerMessageParser extends PlayerMessageParser {

  def parse(message: String): Option[PlayerMessage] = {
    val parseFunctions: Seq[String => Option[PlayerMessage]] =
      Seq(
        message => GameType.withNameOption(message).map(StartGame),
        message => Decision.withNameOption(message).map(DecisionMade),
      )

    parseFunctions
      .collectFirst { case parseF if parseF(message).nonEmpty => parseF }
      .flatMap(_.apply(message))
  }

}
