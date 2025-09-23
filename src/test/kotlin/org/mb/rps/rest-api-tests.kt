package org.mb.rps

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mb.rps.GameSymbol.PAPER
import org.mb.rps.GameSymbol.ROCK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus.*
import java.util.UUID.randomUUID

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ApiTests {

    @LocalServerPort
    private var port: Int? = null

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun playMatch() {
        val match = createMatch()

        assertThat(match.players).hasSize(2)
        assertThat(match.players.toSet()).hasSize(2)
        assertThat(match.playedGames).isEmpty()

        val player1 = match.players[0]
        val player2 = match.players[1]

        with(play(match.id, player1, ROCK).body!!) {
            assertThat(playedGames).isEmpty()
            assertThat(openMoves).hasSize(1)
            with(openMoves[0]) {
                assertThat(player).isEqualTo(player1)
                assertThat(symbol).isEqualTo(ROCK)
            }
        }

        with(play(match.id, player2, PAPER).body!!.playedGames.first()) {
            assertThat(wins.map { it.winner }).containsOnly(player2)
        }
    }

    @Test
    fun loadMatch() {

        assertThat(
            restTemplate.getForEntity(
                "http://localhost:${port}/rps/matches/${randomUUID()}",
                Match::class.java
            ).statusCode
        ).isEqualTo(NOT_FOUND)

        val existingMatch = createMatch()
        assertThat(
            restTemplate.getForObject(
                "http://localhost:${port}/rps/matches/${existingMatch.id}",
                Match::class.java
            )
        ).isEqualTo(existingMatch)
    }

    @Test
    fun playIllegalMoves() {
        assertThat(playIllegalMove("42", randomUUID().toString()).statusCode).isEqualTo(NOT_FOUND)

        val match = createMatch()
        assertThat(playIllegalMove(match.id, randomUUID().toString()).statusCode).isEqualTo(BAD_REQUEST)

        val player = match.players[0]
        assertThat(play(match.id, player, ROCK).statusCode).isEqualTo(OK)
        assertThat(playIllegalMove(match.id, player).statusCode).isEqualTo(BAD_REQUEST)
    }

    private fun createMatch() = restTemplate.postForObject(
        "http://localhost:$port/rps/matches",
        "",
        Match::class.java
    )

    private fun play(id: String, player: String, symbol: GameSymbol) = restTemplate.postForEntity(
        "http://localhost:$port/rps/matches/$id/moves",
        Move(player, symbol),
        Match::class.java
    )

    private fun playIllegalMove(id: String, player: String) = restTemplate.postForEntity(
        "http://localhost:$port/rps/matches/$id/moves",
        Move(player, ROCK),
        String::class.java
    )
}
