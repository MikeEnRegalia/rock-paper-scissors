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
        expect(WIN, "$player beats $opponent") { player.playAgainst(opponent) }
        expect(LOSS, "$opponent is beaten by $player") { opponent.playAgainst(player) }
        expect(DRAW, "$player vs $player is a draw") { player.playAgainst(player) }
    }

    @Test
    fun testMatchFundamentals() {
        val player1 = "A"
        val player2 = "B"

        var match = Match(listOf(player1, player2))
        assertThat(match.playedGames).isEmpty()

        assertThat(match.canMove(player1)).isTrue
        assertThat(match.canMove(player2)).isTrue
        assertThat(match.canMove("C")).isFalse

        val firstMove = Move(player1, ROCK)
        match = match.makeMove(firstMove)

        assertThat(match.currentGame.moves).containsExactly(firstMove)

        assertThat(match.canMove(player1)).isFalse
        assertThrows(IllegalStateException::class.java) { match.makeMove(firstMove) }

        val secondMove = Move(player2, SCISSORS)
        match = match.makeMove(secondMove)

        assertThat(match.playedGames).hasSize(1)
        assertThat(match.playedGames.first().moves).containsExactly(firstMove, secondMove)
        assertThat(match.playedGames.first().wins).hasSize(1)
        assertThat(match.playedGames.first().wins.single()).isEqualTo(Win(player1, player2))

        assertThat(match.currentGame.moves).isEmpty()

        match = match.makeMove(Move(player2, PAPER))
        match = match.makeMove(Move(player1, PAPER))

        assertThat(match.playedGames).hasSize(2)
        assertThat(match.playedGames.last().wins).isEmpty()
    }
}
