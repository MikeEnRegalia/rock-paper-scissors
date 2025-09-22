package org.mb.rps

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mb.rps.GameSymbol.PAPER
import org.mb.rps.GameSymbol.ROCK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus.*
import java.util.UUID.randomUUID
import kotlin.test.expect

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    fun playMatch() {
        val match = createMatch()

        assertThat(match.players).hasSize(2)
        assertThat(match.players.toSet()).hasSize(2)
        assertThat(match.playedGames).isEmpty()

        val player1 = match.players[0]
        val player2 = match.players[1]

        with(makeMove(match.id, player1, ROCK).body!!) {
            assertThat(playedGames).isEmpty()
            assertThat(openMoves).hasSize(1)
            with(openMoves[0]) {
                assertThat(player).isEqualTo(player1)
                assertThat(symbol).isEqualTo(ROCK)
            }
        }

        with(makeMove(match.id, player2, PAPER).body!!.playedGames.first()) {
            assertThat(wins.map { it.winner }).containsOnly(player2)
        }
    }

    @Test
    fun loadNonExistingMatch() {
        val response = restTemplate.getForEntity(
            "http://localhost:$port/rps/matches/${randomUUID()}",
            String::class.java
        )
        expect(NOT_FOUND) { response.statusCode }
    }

    @Test
    fun makeIllegalMoves() {
        val match = createMatch()
        assertThat(makeIllegalMove(match.id, randomUUID().toString()).statusCode).isEqualTo(BAD_REQUEST)

        val player = match.players[0]
        assertThat(makeMove(match.id, player, ROCK).statusCode).isEqualTo(OK)
        assertThat(makeIllegalMove(match.id, player).statusCode).isEqualTo(BAD_REQUEST)
    }

    private fun createMatch() = restTemplate.postForObject(
        "http://localhost:$port/rps/matches",
        "",
        Match::class.java
    )

    private fun makeMove(id: String, player: String, symbol: GameSymbol) = restTemplate.postForEntity(
        "http://localhost:$port/rps/matches/$id/moves",
        RpsController.MovePayload(player, symbol),
        Match::class.java
    )

    private fun makeIllegalMove(id: String, player: String) = restTemplate.postForEntity(
        "http://localhost:$port/rps/matches/$id/moves",
        RpsController.MovePayload(player, ROCK),
        String::class.java
    )
}
