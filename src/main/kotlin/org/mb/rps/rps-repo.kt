package org.mb.rps

import org.springframework.stereotype.Service
import java.util.Collections.synchronizedMap

@Service
class RpsRepository {
    private val matches = synchronizedMap(mutableMapOf<String, Match>())

    fun storeNewMatch(match: Match) {
        synchronized(matches) {
            if (match.id in matches) throw IllegalArgumentException("match id ${match.id} already exists")
            matches[match.id] = match
        }
    }

    fun getMatch(id: String) = matches[id]

    fun storeUpdatedMatch(match: Match) {
        synchronized(matches) {
            if (match.id !in matches) throw IllegalArgumentException("match id ${match.id} does not exist")
            matches[match.id] = match
        }
    }
}
