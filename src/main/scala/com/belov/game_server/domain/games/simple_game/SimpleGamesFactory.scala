package com.belov.game_server.domain.games.simple_game

import cats.Parallel
import cats.effect.kernel.{Async, Ref}
import cats.effect.std.{Queue, UUIDGen}
import cats.implicits._
import com.belov.game_server.config.AppConfig.{ResultsConfig, SimpleGamesConfig}
import com.belov.game_server.domain.cards.Deck.Deck
import com.belov.game_server.domain.cards_dealing.DefaultDealer
import com.belov.game_server.domain.games.GameTable
import com.belov.game_server.domain.games.Games.GameId
import com.belov.game_server.domain.games.rules.CardsCombinations.SimpleCombination
import com.belov.game_server.domain.games.rules.CardsCombinationsSorting.HighestFirst
import com.belov.game_server.domain.games.rules.GameOpenCardsRules
import com.belov.game_server.domain.players.ConnectedPlayer
import com.belov.game_server.domain.players.GamePlayer.PlayerDecision
import com.belov.game_server.domain.players.logic.SimplePlayersDecisionsDivideLogic

class SimpleGamesFactory[F[_]: Async: Parallel](
  finishedGamesQueue: Queue[F, GameId],
  config: SimpleGamesConfig
) {

  def create(gameType: GameType, players: Seq[ConnectedPlayer[F]]): F[SimpleGame[F]] =
    for {
      gameId <- UUIDGen.randomUUID.map(id => GameId(id, players.map(_.player)))
      deck <- Ref.of(Deck.full)
      playersDecisionsQueue <- Queue.unbounded[F, PlayerDecision]
      gameTable = GameTable(players, new DefaultDealer[F], deck)
    } yield
      gameType match {
        case GameType.SingleCardGame =>
          new SimpleGame(
            gameId, gameTable, playersDecisionsQueue, finishedGamesQueue, cardsNumber = 1,
            resultLogic(config.oneCard.results)
          )
        case GameType.DoubleCardGame =>
          new SimpleGame(
            gameId, gameTable, playersDecisionsQueue, finishedGamesQueue, cardsNumber = 2,
            resultLogic(config.twoCards.results)
          )
      }

  private def resultLogic(config: ResultsConfig) =
    new SimpleGameResultLogic(
      config,
      new SimplePlayersDecisionsDivideLogic,
      new SimpleGameOpenCardsResultLogic(GameOpenCardsRules(SimpleCombination, HighestFirst))
    )

}
