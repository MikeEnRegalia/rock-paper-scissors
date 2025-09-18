package org.mb.rps.rockpaperscissors

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mb.rps.Game
import org.mb.rps.GameSymbol.PAPER
import org.mb.rps.GameSymbol.ROCK
import org.mb.rps.RpsController
import org.mb.rps.RpsController.GameCreatedResponse
import org.mb.rps.RpsController.MakeMovePayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort



@SpringBootTest(webEnvironment = RANDOM_PORT)
class RockpaperscissorsApplicationTests {

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
        val (id, game) = restTemplate.postForObject(
                "http://localhost:$port/rps/games",
                "",
                GameCreatedResponse::class.java)

        assertThat(game.players).hasSize(2)
        assertThat(game.players.toSet()).hasSize(2)
        assertThat(game.moves).isEmpty()
        assertThat(game.scores.keys).isEqualTo(game.players.toSet())
        assertThat(game.scores.values).allMatch { it == 0 }

        val gameAfterFirstMove = restTemplate.postForObject(
            "http://localhost:$port/rps/games/$id/moves",
            MakeMovePayload(game.players[0], ROCK),
            Game::class.java)

        assertThat(gameAfterFirstMove.moves).isNotEmpty
        assertThat(gameAfterFirstMove.scores.values).allMatch { it == 0 }

        val gameAfterSecondMove = restTemplate.postForObject(
            "http://localhost:$port/rps/games/$id/moves",
            MakeMovePayload(game.players[1], PAPER),
            Game::class.java)

        println(gameAfterSecondMove)

        assertThat(gameAfterSecondMove.scores[game.players[0]]).isEqualTo(0)
        assertThat(gameAfterSecondMove.scores[game.players[1]]).isEqualTo(1)
    }
}
