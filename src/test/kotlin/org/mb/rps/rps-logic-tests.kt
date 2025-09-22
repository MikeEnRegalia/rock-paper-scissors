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
        expect(WIN, "$player beats $opponent") { player.playedAgainst(opponent) }
        expect(LOSS, "$opponent is beaten by $player") { opponent.playedAgainst(player) }
        expect(DRAW, "$player vs $player is a draw") { player.playedAgainst(player) }
    }

    @Test
    fun testMatchFundamentals() {
        val player1 = "A"
        val player2 = "B"

        var match = Match("1", listOf(player1, player2))
        assertThat(match.playedGames).isEmpty()

        assertThat(match.canMove(player1)).isTrue
        assertThat(match.canMove(player2)).isTrue
        assertThat(match.canMove("C")).isFalse

        val firstMove = Move(player1, ROCK)
        match = match.play(firstMove)

        assertThat(match.openMoves).containsExactly(firstMove)

        assertThat(match.canMove(player1)).isFalse
        assertThrows(IllegalStateException::class.java) { match.play(firstMove) }

        val secondMove = Move(player2, SCISSORS)
        match = match.play(secondMove)

        assertThat(match.playedGames).hasSize(1)
        assertThat(match.playedGames.first().moves).containsExactly(firstMove, secondMove)
        assertThat(match.playedGames.first().wins).hasSize(1)
        assertThat(match.playedGames.first().wins.single()).isEqualTo(Win(player1, player2))

        assertThat(match.openMoves).isEmpty()

        match = match.play(Move(player2, PAPER))
        match = match.play(Move(player1, PAPER))

        assertThat(match.playedGames).hasSize(2)
        assertThat(match.playedGames.last().wins).isEmpty()
    }
}
