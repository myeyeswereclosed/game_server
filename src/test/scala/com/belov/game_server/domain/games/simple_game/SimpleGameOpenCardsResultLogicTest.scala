package com.belov.game_server.domain.games.simple_game

import java.util.UUID

import com.belov.game_server.domain.cards.Deck.Card
import com.belov.game_server.domain.cards.Deck.Rank.{Ace, Eight, Five, Four, Jack, King, Nine, Queen, Six, Ten, Three, Two}
import com.belov.game_server.domain.cards.Deck.Suit.{Clubs, Diamonds, Hearts, Spades}
import com.belov.game_server.domain.games.DecisionResults.{Draw, Loss, PlayerResult, Win}
import com.belov.game_server.domain.games.rules.CardsCombinations.SimpleCombination
import com.belov.game_server.domain.games.rules.CardsCombinationsSorting.HighestFirst
import com.belov.game_server.domain.games.rules.GameOpenCardsRules
import com.belov.game_server.domain.players.GamePlayer.{Hand, Player, PlayerHand}
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers

class SimpleGameOpenCardsResultLogicTest extends AnyFreeSpecLike with Matchers {

  private val playerOne = Player(UUID.randomUUID())
  private val playerTwo = Player(UUID.randomUUID())

  private val playerThree = Player(UUID.randomUUID())
  private val playerFour = Player(UUID.randomUUID())

  val logic = new SimpleGameOpenCardsResultLogic(GameOpenCardsRules(SimpleCombination, HighestFirst))

  "two players, one card per player" - {

    "win and loss" in {

      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Ace, Spades)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Two, Diamonds))))
      )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Win), PlayerResult(playerTwo, Loss)), result)
    }

    "draw" in {
      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Ten, Spades)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Ten, Diamonds))))
        )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Draw), PlayerResult(playerTwo, Draw)), result)
    }

  }

  "two players, two cards" - {

    "win, loss on first card" in {
      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Jack, Spades), Card(Six, Clubs)))),
          PlayerHand(playerTwo, Hand(Seq(Card(King, Diamonds), Card(Five, Hearts))))
        )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Loss), PlayerResult(playerTwo, Win)), result)
    }

    "win, loss on second card with equal first ones" in {
      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Jack, Spades), Card(Four, Clubs)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Jack, Diamonds), Card(Five, Hearts))))
        )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Loss), PlayerResult(playerTwo, Win)), result)
    }

    "win, loss on second card" in {
      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Five, Spades), Card(Ace, Clubs)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Jack, Diamonds), Card(Five, Hearts))))
        )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Win), PlayerResult(playerTwo, Loss)), result)
    }

    "draw" in {
      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Jack, Spades), Card(Four, Clubs)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Jack, Diamonds), Card(Four, Hearts))))
        )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Draw), PlayerResult(playerTwo, Draw)), result)
    }

  }

  "two players, multiple cards" - {

    "win, loss" in {

      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Jack, Spades), Card(Four, Clubs), Card(Nine, Diamonds)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Jack, Diamonds), Card(Four, Hearts), Card(King, Spades))))
        )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Loss), PlayerResult(playerTwo, Win)), result)

    }

    "draw" in {

      val playerHands =
        Seq(
          PlayerHand(
            playerOne,
            Hand(Seq(Card(Jack, Spades), Card(Four, Clubs), Card(Nine, Diamonds), Card(Ten, Hearts)))
          ),
          PlayerHand(
            playerTwo,
            Hand(Seq(Card(Jack, Diamonds), Card(Four, Hearts), Card(Nine, Spades), Card(Ten, Clubs)))
          )
        )

      val result = logic.run(playerHands)

      assertResults(Seq(PlayerResult(playerOne, Draw), PlayerResult(playerTwo, Draw)), result)

    }

  }

  "three players, one card per player" - {

    "one winner from different cards" in {

      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Queen, Spades)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Two, Diamonds)))),
          PlayerHand(playerThree, Hand(Seq(Card(King, Hearts)))),
        )

      val result = logic.run(playerHands)

      assertResults(
        Seq(
          PlayerResult(playerOne, Loss),
          PlayerResult(playerTwo, Loss),
          PlayerResult(playerThree, Win)
        ),
        result)

    }

    "one loser and draw" in {

      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Queen, Spades)))),
          PlayerHand(playerTwo, Hand(Seq(Card(King, Diamonds)))),
          PlayerHand(playerThree, Hand(Seq(Card(King, Diamonds)))),
        )

      val result = logic.run(playerHands)

      assertResults(
        Seq(
          PlayerResult(playerOne, Loss),
          PlayerResult(playerTwo, Draw),
          PlayerResult(playerThree, Draw)
        ), result
      )

    }

    "one winner from same cards" in {

      val playerHands =
        Seq(
          PlayerHand(playerOne, Hand(Seq(Card(Queen, Spades)))),
          PlayerHand(playerTwo, Hand(Seq(Card(Queen, Diamonds)))),
          PlayerHand(playerThree, Hand(Seq(Card(King, Diamonds)))),
        )

      val result = logic.run(playerHands)

      assertResults(
        Seq(
          PlayerResult(playerOne, Loss),
          PlayerResult(playerTwo, Loss),
          PlayerResult(playerThree, Win)
        ), result
      )

    }

    "multiple players with multiple cards" in {

      val playerHands =
        Seq(
          PlayerHand(
            playerOne,
            Hand(Seq(
              Card(Eight, Spades),
              Card(Queen, Spades),
              Card(King, Spades),
              Card(Two, Spades),
            ))
          ),
          PlayerHand(
            playerTwo,
            Hand(Seq(
              Card(King, Clubs),
              Card(Jack, Clubs),
              Card(Three, Clubs),
              Card(Ten, Clubs),
            ))
          ),
          PlayerHand(
            playerThree,
            Hand(
              Seq(
                Card(Jack, Hearts),
                Card(King, Hearts),
                Card(Queen, Hearts),
                Card(Ten, Hearts),
              )
            )
          ),
          PlayerHand(
            playerFour,
            Hand(
              Seq(
                Card(Nine, Diamonds),
                Card(Two, Diamonds),
                Card(Five, Diamonds),
                Card(Jack, Diamonds),
              )
            )
          )
        )

      val result = logic.run(playerHands)

      assertResults(
        Seq(
          PlayerResult(playerOne, Loss),
          PlayerResult(playerTwo, Loss),
          PlayerResult(playerThree, Win),
          PlayerResult(playerFour, Loss),
        ), result)

    }

  }

  private def assertResults(expected: Seq[PlayerResult], actual: Seq[PlayerResult]) = {
    actual.size shouldBe expected.size
    actual.toSet shouldBe expected.toSet
  }

}
