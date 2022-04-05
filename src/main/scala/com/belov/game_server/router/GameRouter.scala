package com.belov.game_server.router

import cats.effect.{Async, Concurrent, LiftIO}
import cats.implicits._
import com.belov.game_server.domain.players.ConnectedPlayer
import com.belov.game_server.domain.players.service.PlayersService
import com.belov.game_server.messages.service.ServiceMessages.ErrorOccurred
import com.belov.game_server.processor.MessagesProcessor
import fs2.io.file.{Path => FilePath}
import fs2.{Pipe, Stream}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text
import org.http4s.{HttpRoutes, StaticFile}

class GameRouter[F[_]: Async: Concurrent: LiftIO](
  playersService: PlayersService[F],
  messageProcessor: MessagesProcessor[F]
) extends Http4sDsl[F] {

  def routes(wsb: WebSocketBuilder2[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request@GET -> Root =>
        StaticFile
          .fromPath(FilePath("static/index.html"), Some(request))
          .getOrElseF(NotFound())

      case request@GET -> Root / "game.js" =>
        StaticFile
          .fromPath(FilePath("static/game.js"), Some(request))
          .getOrElseF(NotFound())

      case GET -> Root / "ws" =>
        for {
          connectedPlayer <- playersService.createPlayer()
          _ = println(s"Player connected ${connectedPlayer.player}")
          res <-
            wsb.build(
              send = Stream.fromQueueUnterminated(connectedPlayer.messages).map(msg => Text(msg.asString)),
              receive = receive(connectedPlayer)
            )
        } yield res

    }

  private def receive(player: ConnectedPlayer[F]): Pipe[F, WebSocketFrame, Unit] =
    _.evalMap {
      case text: WebSocketFrame.Text => messageProcessor.process(player, text.str)
      case _ => player.handleMessage(ErrorOccurred("Unexpected message format"))
    }
}
