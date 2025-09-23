package org.mb.rps

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mb.rps.GameResult.*
import org.mb.rps.GameSymbol.*
import kotlin.test.Test
import kotlin.test.expect

class RpsLogicTests {

    @ParameterizedTest
    @CsvSource("ROCK,SCISSORS", "SCISSORS,PAPER", "PAPER,ROCK")
    fun testBasicGameLogic(player: GameSymbol, opponent: GameSymbol) {
        expect(WIN, "$player beats $opponent") { player.meets(opponent) }
        expect(LOSS, "$opponent is beaten by $player") { opponent.meets(player) }
        expect(DRAW, "$player vs $player is a draw") { player.meets(player) }
    }

    @Test
    fun testMatchFundamentals() {
        val player1 = "A"
        val player2 = "B"

        var match = Match("1", listOf(player1, player2))
        with(match) {
            assertThat(playedGames).isEmpty()

            assertThat(canPlay(player1)).isTrue
            assertThat(canPlay(player2)).isTrue
            assertThat(canPlay("C")).isFalse
        }

        val firstMove = Move(player1, ROCK)
        match = match.play(firstMove)

        with(match) {
            assertThat(openMoves).containsExactly(firstMove)

            assertThat(canPlay(player1)).isFalse
            assertThrows(IllegalStateException::class.java) { play(firstMove) }
        }

        val secondMove = Move(player2, SCISSORS)
        match = match.play(secondMove)

        with(match) {
            assertThat(playedGames).hasSize(1)
            assertThat(playedGames.first().moves).containsExactly(firstMove, secondMove)
            assertThat(playedGames.first().wins).hasSize(1)
            assertThat(playedGames.first().wins.single()).isEqualTo(Win(player1, player2))

            assertThat(openMoves).isEmpty()
        }

        match = match.play(Move(player2, PAPER))
        match = match.play(Move(player1, PAPER))

        with(match) {
            assertThat(playedGames).hasSize(2)
            assertThat(playedGames.last().wins).isEmpty()
        }
    }
}
