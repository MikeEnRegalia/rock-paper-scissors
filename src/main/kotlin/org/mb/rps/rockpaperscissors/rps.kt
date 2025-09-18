package org.mb.rps.rockpaperscissors

import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID.randomUUID

enum class GameSymbol { ROCK, PAPER, SCISSORS }

data class Game(
    val players: List<String> = listOf(randomUUID(), randomUUID()).map { it.toString() },
    val moves: List<Move> = listOf(),
    val scores: Map<String, Int> = players.associateWith { 0 }
)

data class Move(val by: String, val symbol: GameSymbol)

@RestController
@RequestMapping("/rps")
class RpsController(private val service: RpsService) {

    data class GameCreatedResponse(val id: String, val game: Game)
    @PostMapping("/games")
    fun createGame(): ResponseEntity<GameCreatedResponse> =
        ok(service.createGame().let { (id, game) -> GameCreatedResponse(id, game) })


data class MakeMovePayload(val player: String, val symbol: GameSymbol)
    @PostMapping("/games/{gameId}/moves")
    fun makeMove(@RequestBody move: MakeMovePayload) {

    }
}

@Service
class RpsService(private val repo: RpsRepository) {
    fun createGame(): Pair<String, Game> = repo.createGame()
}

@Service
class RpsRepository {
    private val games = mutableMapOf<String, Game>()

    fun createGame(): Pair<String, Game> = randomUUID().toString().let {
        val game = Game()
        games[it] = game
        it to game
    }
}
