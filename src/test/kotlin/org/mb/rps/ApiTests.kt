package org.mb.rps

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

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
        Assertions.assertThat(rpsController).isNotNull
    }

    @Test
    fun playGame() {
        val (id, game) = createMatch()

        Assertions.assertThat(game.players).hasSize(2)
        Assertions.assertThat(game.players.toSet()).hasSize(2)
        Assertions.assertThat(game.playedGames).isEmpty()

        val player1 = game.players[0]
        val player2 = game.players[1]

        with(makeMove(id, player1, GameSymbol.ROCK)) {
            Assertions.assertThat(playedGames).isEmpty()
            Assertions.assertThat(currentGame.moves).hasSize(1)
            with(currentGame.moves[0]) {
                Assertions.assertThat(player).isEqualTo(player1)
                Assertions.assertThat(symbol).isEqualTo(GameSymbol.ROCK)
            }
        }

        with(makeMove(id, player2, GameSymbol.PAPER).playedGames.first()) {
            Assertions.assertThat(wins.map { it.winner }).containsOnly(player2)
        }
    }

    private fun createMatch() = restTemplate.postForObject(
        "http://localhost:$port/rps/matches",
        "",
        RpsController.MatchCreatedResponse::class.java
    )

    private fun makeMove(id: String, player: String, symbol: GameSymbol) = restTemplate.postForObject(
        "http://localhost:$port/rps/matches/$id/moves",
        RpsController.MakeMovePayload(player, symbol),
        Match::class.java
    )
}
