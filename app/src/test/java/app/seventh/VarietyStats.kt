package app.seventh

import app.seventh.domain.Engine
import app.seventh.domain.Lands
import app.seventh.domain.Live
import org.junit.Test
import kotlin.random.Random

/** How varied a playthrough is: events per game, how many distinct, how often each repeats (printed, not asserted). */
class VarietyStats {
    @Test fun stats() {
        for (land in Lands.all) {
            val perGame = mutableListOf<Int>()
            val distinct = mutableListOf<Int>()
            val maxRep = mutableListOf<Int>()
            val years = mutableListOf<Int>()
            val everyIds = land.events.map { it.id }.toSet()
            val evRep = mutableListOf<Double>()
            val evMax = mutableListOf<Int>()
            val chains = mutableListOf<Int>()
            repeat(200) { s ->
                val rnd = Random(s)
                val e = Engine.newGame(land.id, rnd)
                val y0 = e.g.year
                val seen = HashMap<String, Int>()
                while (!e.g.over && e.g.year < y0 + 320) {
                    when (val l = e.live()) {
                        is Live.Happens -> {
                            seen.merge(l.occ.ev.id, 1, Int::plus)
                            val opts = l.occ.ev.opts(e, l.occ).filter { it.cost <= e.g.money }
                            val o = opts[rnd.nextInt(opts.size)]
                            if (o.roll != null) e.draw(l.occ, o, e.beans(e.rollInfo(o, l.occ))) else e.choose(l.occ, o)
                            if (!e.alive(e.h) || e.h.monk) e.succession()?.let { e.takeHeir(it.heirs[rnd.nextInt(it.heirs.size)].p, it.eldest) }
                        }
                        is Live.HeadGone -> e.succession()?.let { e.takeHeir(it.heirs[rnd.nextInt(it.heirs.size)].p, it.eldest) }
                        is Live.Quiet -> {}
                    }
                    if (e.g.won) break
                }
                val ev = seen.filterKeys { it in everyIds }; if (ev.isNotEmpty()) { evRep += ev.values.sum().toDouble() / ev.size; evMax += ev.values.max() }; chains += e.g.flags.size
                perGame += seen.values.sum(); distinct += seen.size; maxRep += (seen.values.maxOrNull() ?: 0); years += e.g.year - y0
            }
            val total = land.forced.size + land.events.size + land.history.size
            println("${land.id}: pool $total (forced ${land.forced.size}, everyday ${land.events.size}, history ${land.history.size}); per game until win/end: years ${years.average().toInt()}, events ${perGame.average().toInt()}, distinct ${distinct.average().toInt()}, most-repeated ${maxRep.average().toInt()}x; everyday: each seen ${"%.1f".format(evRep.average())}x on average, the most ${"%.1f".format(evMax.average())}x; marks set ${"%.1f".format(chains.average())}")
        }
    }
}
