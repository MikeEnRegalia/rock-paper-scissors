package org.mb.rps

import org.mb.rps.GameSymbol.*
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
