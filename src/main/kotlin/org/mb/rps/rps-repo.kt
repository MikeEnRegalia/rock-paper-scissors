package org.mb.rps

import org.springframework.stereotype.Service
import java.util.UUID.randomUUID

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
