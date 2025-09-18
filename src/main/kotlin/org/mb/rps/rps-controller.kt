package org.mb.rps

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.UUID.randomUUID

@RestController
@RequestMapping("/rps")
@CrossOrigin
class RpsController(private val repo: RpsRepository) {

    data class GameCreatedResponse(val id: String, val game: Game)

    @PostMapping("/games")
    fun createGame(): ResponseEntity<GameCreatedResponse> = ok(
        repo.createGame(randomUUID().toString(), listOf(randomUUID(), randomUUID()).map { it.toString() }).let { (id, game) -> GameCreatedResponse(id, game) }
    )

    data class MakeMovePayload(val player: String, val symbol: GameSymbol)

    @PostMapping("/games/{gameId}/moves")
    fun makeMove(@PathVariable gameId: String, @RequestBody move: MakeMovePayload): ResponseEntity<Game> {
        val game = repo.getGame(gameId) ?: throw ResponseStatusException(NOT_FOUND)
        if (move.player !in game.players) throw ResponseStatusException(BAD_REQUEST, "player is unknown")
        if (!game.canMove(move.player)) throw ResponseStatusException(BAD_REQUEST, "player has already moved")

        val oldState = repo.getGame(gameId) ?: throw IllegalStateException()
        val newState = oldState.makeMove(Move(move.player, move.symbol))
        repo.updateGame(gameId, newState)
        return ok(newState)
    }
}
