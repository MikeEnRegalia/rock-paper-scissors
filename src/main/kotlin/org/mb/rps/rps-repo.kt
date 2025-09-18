package org.mb.rps

import org.springframework.stereotype.Service

@Service
class RpsRepository {
    private val games = mutableMapOf<String, Game>()

    fun createGame(id: String, players: List<String>) = id to Game(players = players)
        .also { games[id] = it }

    fun getGame(gameId: String): Game? = games[gameId]

    fun updateGame(gameId: String, game: Game) {
        games[gameId] = game
    }
}
