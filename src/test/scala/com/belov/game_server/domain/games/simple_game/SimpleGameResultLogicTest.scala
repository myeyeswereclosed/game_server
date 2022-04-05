package com.belov.game_server.domain.games.simple_game

import java.util.UUID

import com.belov.game_server.config.AppConfig.{FoldResultsConfig, PlayResultsConfig, ResultsConfig}
import com.belov.game_server.domain.cards.Deck.Card
import com.belov.game_server.domain.cards.Deck.Rank.{Ace, Two}
import com.belov.game_server.domain.cards.Deck.Suit.{Clubs, Spades}
import com.belov.game_server.domain.games.GameResults
import com.belov.game_server.domain.games.GameResults._
import com.belov.game_server.domain.games.rules.CardsCombinations.SimpleCombination
import com.belov.game_server.domain.games.rules.CardsCombinationsSorting.HighestFirst
import com.belov.game_server.domain.games.rules.GameOpenCardsRules
import com.belov.game_server.domain.players.Decision.{Fold, Play}
import com.belov.game_server.domain.players.GamePlayer.{Hand, Player, PlayerHand, PlayerHandDecision}
import com.belov.game_server.domain.players.logic.SimplePlayersDecisionsDivideLogic
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers

class SimpleGameResultLogicTest extends AnyFreeSpecLike with Matchers {

  private val config =
    ResultsConfig(
      fold = FoldResultsConfig(foldCost = 3, drawByFoldsCost = 1),
      play = PlayResultsConfig(lossCost = 10, winCost = 15)
    )

  private val logic =
    new SimpleGameResultLogic(
      config,
      new SimplePlayersDecisionsDivideLogic,
      new SimpleGameOpenCardsResultLogic(GameOpenCardsRules(SimpleCombination, HighestFirst))
    )

  private val playerOne = Player(UUID.randomUUID())
  private val playerTwo = Player(UUID.randomUUID())

  "Simple game result logic should be" - {

    "correct for both players folds" in {
      val playersHandsDecisions = Seq(
        PlayerHandDecision(PlayerHand(playerOne, Hand(Seq(Card(Two, Clubs)))), decision = Fold),
        PlayerHandDecision(PlayerHand(playerTwo, Hand(Seq(Card(Ace, Clubs)))), decision = Fold)
      )

      val result = logic.run(playersHandsDecisions)

      result shouldBe
        Finished(
          Seq(
            PlayedGameResult(playerOne, LoseTokens(config.drawByFoldCost)),
            PlayedGameResult(playerTwo, LoseTokens(config.drawByFoldCost))
          )
        )
    }

    "correct for first player folds and second player plays" in {
      val playersHandsDecisions = Seq(
        PlayerHandDecision(PlayerHand(playerOne, Hand(Seq(Card(Two, Clubs)))), decision = Fold),
        PlayerHandDecision(PlayerHand(playerTwo, Hand(Seq(Card(Ace, Clubs)))), decision = Play)
      )

      val result = logic.run(playersHandsDecisions)

      result shouldBe
        Finished(
          Seq(
            PlayedGameResult(playerTwo, WinTokens(config.foldCost)),
            PlayedGameResult(playerOne, LoseTokens(config.foldCost)),
          )
        )
    }

    "correct for second player folds and first player plays" in {
      val playersHandsDecisions = Seq(
        PlayerHandDecision(PlayerHand(playerOne, Hand(Seq(Card(Two, Clubs)))), decision = Play),
        PlayerHandDecision(PlayerHand(playerTwo, Hand(Seq(Card(Ace, Clubs)))), decision = Fold)
      )

      val result = logic.run(playersHandsDecisions)

      result shouldBe
        Finished(
          Seq(
            PlayedGameResult(playerOne, WinTokens(config.foldCost)),
            PlayedGameResult(playerTwo, LoseTokens(config.foldCost))
          )
        )
    }

    "correct for both players play decision" in {
      val playersHandsDecisions = Seq(
        PlayerHandDecision(PlayerHand(playerOne, Hand(Seq(Card(Two, Clubs)))), decision = Play),
        PlayerHandDecision(PlayerHand(playerTwo, Hand(Seq(Card(Ace, Clubs)))), decision = Play)
      )

      val result = logic.run(playersHandsDecisions)

      result match {
        case Finished(results) => results.toSet shouldBe
          Set(
            PlayedGameResult(playerOne, LoseTokens(config.lossCost)),
            PlayedGameResult(playerTwo, WinTokens(config.winCost))
          )
        case GameResults.Error(_) => fail()
      }
    }

    "correct for draw" in {
      val playersHandsDecisions = Seq(
        PlayerHandDecision(PlayerHand(playerOne, Hand(Seq(Card(Ace, Clubs)))), decision = Play),
        PlayerHandDecision(PlayerHand(playerTwo, Hand(Seq(Card(Ace, Spades)))), decision = Play)
      )

      val result = logic.run(playersHandsDecisions)

      result shouldBe Finished(
        Seq(
          PlayedGameResult(playerOne, DrawTokens(0)),
          PlayedGameResult(playerTwo, DrawTokens(0))
        )
      )
    }

  }

}
