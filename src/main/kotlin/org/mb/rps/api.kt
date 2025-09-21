package org.mb.rps

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.UUID.randomUUID

@RestController
@RequestMapping("/rps")
@CrossOrigin
class RpsController(private val repo: RpsRepository) {

    data class MatchCreatedResponse(val id: String, val match: Match)

    @PostMapping("/matches")
    fun createMatch(): ResponseEntity<MatchCreatedResponse> = ok(
        repo.createMatch(
            randomUUID().toString(),
            Match(listOf(randomUUID(), randomUUID()).map { it.toString() })
        ).let { (id, match) -> MatchCreatedResponse(id, match) }
    )

    @GetMapping("/matches/{id}")
    fun getMatch(@PathVariable("id") id: String): ResponseEntity<Match> {
        return repo.getMatch(id)?.let { ok(it) } ?: notFound().build()
    }

    data class MakeMovePayload(val player: String, val symbol: GameSymbol)

    @PostMapping("/matches/{matchId}/moves")
    fun makeMove(@PathVariable matchId: String, @RequestBody move: MakeMovePayload): ResponseEntity<Match> {
        val match = repo.getMatch(matchId) ?: throw ResponseStatusException(NOT_FOUND)
        if (move.player !in match.players) throw ResponseStatusException(BAD_REQUEST, "player is unknown")
        if (!match.canMove(move.player)) throw ResponseStatusException(BAD_REQUEST, "player has already moved")

        val oldState = repo.getMatch(matchId) ?: throw IllegalStateException()
        val newState = oldState.makeMove(Move(move.player, move.symbol))
        repo.updateMatch(matchId, newState)
        return ok(newState)
    }
}
