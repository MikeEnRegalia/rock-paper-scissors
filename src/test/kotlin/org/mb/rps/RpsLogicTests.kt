package org.mb.rps

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mb.rps.GameResult.*
import kotlin.test.expect

class RpsLogicTests {

    @ParameterizedTest
    @CsvSource("ROCK,SCISSORS", "SCISSORS,PAPER", "PAPER,ROCK")
    fun testBasicRockPaperScissorsLogic(winningSymbol: GameSymbol, losingSymbol: GameSymbol) {
        expect(WIN, "$winningSymbol beats $losingSymbol") {
            computeResult(winningSymbol, losingSymbol)
        }
        expect(LOSS, "$losingSymbol is beaten by $winningSymbol") {
            computeResult(losingSymbol, winningSymbol)
        }
        expect(DRAW, "$winningSymbol vs $winningSymbol is a draw") {
            computeResult(winningSymbol, winningSymbol)
        }
    }
}
