package app.seventh

import app.seventh.domain.Engine
import app.seventh.domain.Game
import app.seventh.domain.Live
import app.seventh.domain.Lands
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class EngineTest {
    /** Plays a whole dynasty with random choices; returns the engine at the end. */
    private fun play(land: String, seed: Int): Engine {
        val rnd = Random(seed)
        val e = Engine.newGame(land, rnd)
        val start = e.g.year
        while (!e.g.over && e.g.year < start + 320) {
            when (val l = e.live()) {
                is Live.Happens -> {
                    val opts = l.occ.ev.opts(e, l.occ).filter { it.cost <= e.g.money }
                    val o = opts[rnd.nextInt(opts.size)]
                    if (o.roll != null) e.draw(l.occ, o, e.beans(e.rollInfo(o, l.occ))) else e.choose(l.occ, o)
                    if (!e.alive(e.h) || e.h.monk) e.succession()?.let { s -> e.takeHeir(s.heirs[rnd.nextInt(s.heirs.size)].p, s.eldest) }
                }
                is Live.HeadGone -> e.succession()?.let { s -> e.takeHeir(s.heirs[rnd.nextInt(s.heirs.size)].p, s.eldest) }
                is Live.Quiet -> {}
            }
        }
        return e
    }

    @Test fun dynastiesPlayOutInEveryLand() {
        for (land in Lands.all) {
            var won = 0
            val n = 200
            repeat(n) { i -> if (play(land.id, i * 31 + 7).g.won) won++ }
            val rate = won * 100 / n
            println("${land.id}: random play lifts the curse in $rate%")
            assertTrue("${land.id} win rate $rate%", rate in 12..70)
        }
    }

    @Test fun everyEventTextRenders() {
        for (land in Lands.all) {
            val e = play(land.id, 99)
            for (ev in land.forced + land.events + land.history) assertTrue(ev.t.isNotBlank())
            assertTrue(e.g.log.isNotEmpty())
        }
    }

    @Test fun saveRoundTrips() {
        val e = play("eu", 5)
        val json = Json { encodeDefaults = false }
        val s = json.encodeToString(Game.serializer(), e.g)
        val back = json.decodeFromString(Game.serializer(), s)
        assertEquals(e.g.year, back.year)
        assertEquals(e.g.people.size, back.people.size)
        assertEquals(json.encodeToString(Game.serializer(), back), s)
    }
}
