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

    @PostMapping("/matches")
    fun createMatch(): ResponseEntity<Match> = ok(
        Match(randomUUID().toString(), listOf(randomUUID(), randomUUID()).map { it.toString() })
            .also { repo.storeNewMatch(it) }
    )

    @GetMapping("/matches/{id}")
    fun getMatch(@PathVariable("id") id: String): ResponseEntity<Match> =
        repo.getMatch(id)?.let { ok(it) } ?: notFound().build()

    data class MovePayload(val player: String, val symbol: GameSymbol)

    @PostMapping("/matches/{matchId}/moves")
    fun makeMove(@PathVariable matchId: String, @RequestBody req: MovePayload): ResponseEntity<Match> {
        val match = repo.getMatch(matchId) ?: throw ResponseStatusException(NOT_FOUND)
        if (req.player !in match.players) throw ResponseStatusException(BAD_REQUEST, "player is unknown")
        if (!match.canMove(req.player)) throw ResponseStatusException(BAD_REQUEST, "player has already moved")

        val newMatch = match.makeMove(Move(req.player, req.symbol))
        repo.updateMatch(newMatch)
        return ok(newMatch)
    }
}
