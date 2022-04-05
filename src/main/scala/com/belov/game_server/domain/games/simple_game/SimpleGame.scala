package com.belov.game_server.domain.games.simple_game

import cats.Parallel
import cats.effect.std.Queue
import cats.effect.{Async, Ref, Sync}
import cats.implicits._
import com.belov.game_server.domain.cards.Deck
import com.belov.game_server.domain.cards_dealing.CardDealer
import com.belov.game_server.domain.games.GameResults._
import com.belov.game_server.domain.games.Games.{Game, GameId}
import com.belov.game_server.domain.games.{GameResultLogic, GameTable}
import com.belov.game_server.domain.players.GamePlayer._
import com.belov.game_server.domain.players.{ConnectedPlayer, Decision}
import com.belov.game_server.messages.service.ServiceMessages._

class SimpleGame[F[_] : Async : Parallel](
  id: GameId,
  table: GameTable[F],
  playersDecisions: Queue[F, PlayerDecision],
  finishedGamesQueue: Queue[F, GameId],
  cardsNumber: Int,
  resultLogic: GameResultLogic
) extends Game[F] {

  private val connectedPlayers: Seq[ConnectedPlayer[F]] = table.players
  private val dealer: CardDealer[F] = table.dealer
  private val deck: Ref[F, Deck.Deck] = table.deck

  override def gameId(): F[GameId] = Sync[F].pure(id)

  def start(): fs2.Stream[F, Unit] = {
    val players = connectedPlayers.map(_.player)

    fs2.Stream.eval(
      for {
        playersHands <-
          players
            .parTraverse(player => dealer.deal(cardsNumber, deck).map(cards => PlayerHand(player, Hand(cards))))
        _ <- connectedPlayers.parTraverse(
          connectedPlayer =>
            playersHands
              .find(_.player == connectedPlayer.player)
              .map(hand => connectedPlayer.handleMessage(TakeCards(id.id, hand.cards, Decision.values)))
              .getOrElse(Sync[F].unit)
        )
      } yield playersHands
    ).flatMap(run)
  }

  def handleDecision(playerDecision: PlayerDecision): F[Unit] = playersDecisions.offer(playerDecision)

  /** simplification */
  private def run(hands: PlayersHands): fs2.Stream[F, Unit] =
    fs2.Stream
      .fromQueueUnterminated(playersDecisions)
      .chunkN(connectedPlayers.size, allowFewer = false)
      .flatMap {
        decisions =>
          for {
            _ <- fs2.Stream.eval(Sync[F].delay(println(s"Players decisions are $decisions")))
            _ <- handleResult(result(hands, decisions.toList))
          } yield ()
      }

  private def handleResult(result: GameResult): fs2.Stream[F, Unit] =
    result match {
      case Finished(_) if result.drawOccurred => gameDraw()
      case Finished(playersResults) => finishGame(playersResults.toSet)
      case Error(reason) => gameError(reason)
    }

  private def gameDraw(): fs2.Stream[F, Unit] =
    for {
      _ <- fs2.Stream.eval(sendToPlayers(_ => DrawOccurred(id.id)))
      _ <- start()
    } yield ()

  private def finishGame(results: Set[PlayedGameResult]) =
    fs2.Stream.eval(
      for {
        _ <- results.toSeq.parTraverse(result => updatePlayersBalances(result).getOrElse(Sync[F].unit))
        _ <- finish()
        _ <- sendToPlayers(player => ChooseGame(player.id, GameType.values))
      } yield ()
    )

  private def sendToPlayers(messageF: ConnectedPlayer[F] => ServiceMessage) =
    connectedPlayers.parTraverse(player => player.handleMessage(messageF(player)))

  private def gameError(reason: String) =
    fs2.Stream.eval(Sync[F].delay(println(s"Unexpected result $reason")) *> finish())

  private def finish(): F[Unit] = finishedGamesQueue.offer(id)

  private def updatePlayersBalances(result: PlayedGameResult): Option[F[Unit]] =
    connectedPlayers
      .find(_.player == result.player)
      .map(
        connectedPlayer =>
          for {
            balanceChanged <- connectedPlayer.updateBalance(result.tokens)
            _ <- connectedPlayer.handleMessage(GameFinished(id.id, result, balanceChanged))
          } yield ()
      )

  private def result(hands: PlayersHands, decisions: PlayersDecisions): GameResult = {
    val playersHandsDecisions: Seq[Option[PlayerHandDecision]] =
      for {
        playerDecision <- decisions
        hand = hands.find(_.player == playerDecision.player)
      } yield hand.map(PlayerHandDecision(_, playerDecision.decision))

    resultLogic.run(playersHandsDecisions.flatten)
  }
}
