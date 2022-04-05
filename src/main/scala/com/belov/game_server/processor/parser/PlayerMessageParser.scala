package com.belov.game_server.processor.parser

import com.belov.game_server.messages.player.PlayerMessages.PlayerMessage

trait PlayerMessageParser {

  def parse(message: String): Option[PlayerMessage]

}
