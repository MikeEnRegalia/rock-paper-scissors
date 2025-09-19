package org.mb.rps.rockpaperscissors

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mb.rps.GameSymbol
import org.mb.rps.GameSymbol.PAPER
import org.mb.rps.GameSymbol.ROCK
import org.mb.rps.Match
import org.mb.rps.RpsController
import org.mb.rps.RpsController.MakeMovePayload
import org.mb.rps.RpsController.MatchCreatedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort


@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApiTests {

    @LocalServerPort
    private var port: Int? = null

    @Autowired
    private lateinit var rpsController: RpsController

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun contextLoads() {
        assertThat(rpsController).isNotNull
    }

    @Test
    fun playGame() {
        val (id, game) = createMatch()

        assertThat(game.players).hasSize(2)
        assertThat(game.players.toSet()).hasSize(2)
        assertThat(game.playedGames).isEmpty()

        val player1 = game.players[0]
        val player2 = game.players[1]

        with(makeMove(id, player1, ROCK)) {
            assertThat(playedGames).isEmpty()
            assertThat(currentGame.moves).hasSize(1)
            with(currentGame.moves[0]) {
                assertThat(player).isEqualTo(player1)
                assertThat(symbol).isEqualTo(ROCK)
            }
        }

        with(makeMove(id, player2, PAPER).playedGames.first()) {
            assertThat(wins.map { it.winner }).containsOnly(player2)
        }
    }

    private fun createMatch() = restTemplate.postForObject(
        "http://localhost:$port/rps/matches",
        "",
        MatchCreatedResponse::class.java
    )

    private fun makeMove(id: String, player: String, symbol: GameSymbol) = restTemplate.postForObject(
        "http://localhost:$port/rps/matches/$id/moves",
        MakeMovePayload(player, symbol),
        Match::class.java
    )
}
