package org.mb.rps.rockpaperscissors

import org.mb.rps.rockpaperscissors.GameSymbol.*
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.UUID.randomUUID

enum class GameSymbol { ROCK, PAPER, SCISSORS }

data class Game(
    val players: List<String> = listOf(randomUUID(), randomUUID()).map { it.toString() },
    val moves: List<Move> = listOf(),
    val scores: Map<String, Int> = computeScores(players, moves)
)

fun Game.nextPlayers() = when {
    moves.isEmpty() -> players
    else -> players.filter { it != moves.last().by }
}

fun computeScores(players: List<String>, moves: List<Move>) = moves
    .chunked(players.size)
    .filter { it.size == players.size }
    .fold(players.associateWith { 0 }.toMutableMap()) { acc, moveList ->
        for (player in players) for (otherPlayer in players) {
            if (player > otherPlayer) {
                val playerSymbol = moveList.single { it.by == player }.symbol
                val otherPlayerSymbol = moveList.single { it.by == otherPlayer }.symbol
                if (playerSymbol == otherPlayerSymbol) continue

                val playerWon = when (playerSymbol) {
                    ROCK -> otherPlayerSymbol == SCISSORS
                    PAPER -> otherPlayerSymbol == ROCK
                    SCISSORS -> otherPlayerSymbol == PAPER
                }

                acc.compute(if (playerWon) player else otherPlayer) { _, acc -> (acc ?: 0) + 1 }
            }
        }
        acc
    }

fun Game.makeMove(move: Move): Game {
    if (move.by !in nextPlayers()) throw IllegalStateException()
    val newMoves = moves + move
    return copy(moves = newMoves, scores = computeScores(players, newMoves))
}

data class Move(val by: String, val symbol: GameSymbol)

@RestController
@RequestMapping("/rps")
@CrossOrigin
class RpsController(private val service: RpsService) {

    data class GameCreatedResponse(val id: String, val game: Game)

    @PostMapping("/games")
    fun createGame(): ResponseEntity<GameCreatedResponse> =
        ok(service.createGame().let { (id, game) -> GameCreatedResponse(id, game) })

    data class MakeMovePayload(val player: String, val symbol: GameSymbol)

    @PostMapping("/games/{gameId}/moves")
    fun makeMove(@PathVariable gameId: String, @RequestBody move: MakeMovePayload): ResponseEntity<Game> {
        val game = service.getGame(gameId) ?: throw ResponseStatusException(NOT_FOUND)
        if (move.player !in game.players) throw ResponseStatusException(BAD_REQUEST, "player is unknown")
        if (move.player !in game.nextPlayers()) throw ResponseStatusException(BAD_REQUEST, "player has already moved")
        val nextGame = service.makeMove(gameId, move.player, move.symbol)
        return ok(nextGame)
    }
}

@Service
class RpsService(private val repo: RpsRepository) {
    fun createGame() = repo.createGame()
    fun getGame(gameId: String) = repo.getGame(gameId)

    fun makeMove(gameId: String, player: String, symbol: GameSymbol): Game {
        val oldGame = repo.getGame(gameId) ?: throw IllegalStateException()
        val nextGame = oldGame.makeMove(Move(player, symbol))
        repo.updateGame(gameId, nextGame)
        return nextGame
    }
}

@Service
class RpsRepository {
    private val games = mutableMapOf<String, Game>()

    fun createGame(): Pair<String, Game> = randomUUID().toString().let {
        val game = Game()
        games[it] = game
        it to game
    }

    fun getGame(gameId: String): Game? = games[gameId]

    fun updateGame(gameId: String, game: Game) {
        games[gameId] = game
    }
}
