package org.mb.rps

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mb.rps.GameSymbol.ROCK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RepoTests {

    @Autowired
    private lateinit var repo: RpsRepository

    @Test
    fun itStoresMatches() {
        val match = Match("41")
        assertDoesNotThrow { repo.storeNewMatch(match) }
        assertThrows<IllegalArgumentException> { repo.storeNewMatch(match) }
    }

    @Test
    fun itGetsMatches() {
        val match = Match(id = "42")
        repo.storeNewMatch(match)
        assertThat(repo.getMatch(match.id)).isEqualTo(match)
        assertThat(repo.getMatch("43")).isNull()
    }

    @Test
    fun itUpdatesMatches() {
        val match1 = Match(id = "43")
        val match2 = Match(id = "44")
        listOf(match1, match2).forEach { repo.storeNewMatch(it) }

        val match1Updated = match1.copy(openMoves = listOf(Move(match1.players.first(), ROCK)))
        repo.storeUpdatedMatch(match1Updated)

        assertThat(repo.getMatch(match1.id)?.openMoves).hasSize(1)
        assertThat(repo.getMatch(match2.id)?.openMoves).hasSize(0)

        assertThrows<IllegalArgumentException> { repo.storeUpdatedMatch(Match("45")) }

    }

}
