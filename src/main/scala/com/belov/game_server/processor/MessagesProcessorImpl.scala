package com.belov.game_server.processor

import com.belov.game_server.domain.games.system.service.GamesService
import com.belov.game_server.domain.players.ConnectedPlayer
import com.belov.game_server.messages.player.PlayerMessages.{DecisionMade, StartGame}
import com.belov.game_server.messages.service.ServiceMessages.ErrorOccurred
import com.belov.game_server.processor.parser.PlayerMessageParser

class MessagesProcessorImpl[F[_]](
  messageParser: PlayerMessageParser,
  gamesService: GamesService[F]
) extends MessagesProcessor[F] {

  def process(player: ConnectedPlayer[F], message: String): F[Unit] =
    messageParser
      .parse(message)
      .map {
        case StartGame(gameType) => gamesService.addGamePlayer(gameType, player)
        case DecisionMade(decision) => gamesService.processDecision(decision, player)
      }
      .getOrElse(player.handleMessage(ErrorOccurred(s"Unexpected message $message")))
}
