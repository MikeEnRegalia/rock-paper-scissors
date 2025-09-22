package org.mb.rps

import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rps")
@CrossOrigin
class RpsController(private val repo: RpsRepository) {

    @PostMapping("/matches")
    fun createMatch(): ResponseEntity<Match> = ok(Match().also { repo.storeNewMatch(it) })

    @GetMapping("/matches/{id}")
    fun getMatch(@PathVariable("id") id: String): ResponseEntity<Match> =
        repo.getMatch(id)?.let { ok(it) } ?: notFound().build()

    @PostMapping("/matches/{matchId}/moves")
    fun postMove(@PathVariable matchId: String, @RequestBody move: Move): ResponseEntity<Match> {
        val match = repo.getMatch(matchId) ?: throw ResponseStatusException(NOT_FOUND)
        if (move.player !in match.players) throw ResponseStatusException(BAD_REQUEST, "player is unknown")
        if (!match.canMove(move.player)) throw ResponseStatusException(BAD_REQUEST, "player has already moved")

        return match.play(move)
            .also { repo.storeUpdatedMatch(it) }
            .let { ok(it) }
    }
}
