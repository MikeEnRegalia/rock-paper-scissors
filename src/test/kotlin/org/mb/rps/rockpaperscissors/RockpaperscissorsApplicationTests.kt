package org.mb.rps.rockpaperscissors

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mb.rps.rockpaperscissors.RpsController.GameCreatedResponse
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
    fun gameIsCreated() {
        val (id, game) = restTemplate.postForObject(
                "http://localhost:$port/rps/games",
                "",
                GameCreatedResponse::class.java)

        assertThat(game.players).hasSize(2)
        assertThat(game.moves).isEmpty()


    }

}
