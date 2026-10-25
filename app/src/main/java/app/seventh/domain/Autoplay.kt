package app.seventh.domain

import kotlin.random.Random

/**
 * Lives [years] years on random choices, avoiding the worst ones (no curses on the poor, no deadly gambles below 35%),
 * so a demo family reaches a few generations. Stops early if the line ends.
 */
fun Engine.autoplay(years: Int, rnd: Random) {
    val end = g.year + years
    while (!g.over && g.year < end) {
        when (val l = live()) {
            is Live.Happens -> {
                val opts = l.occ.ev.opts(this, l.occ).filter { it.cost <= g.money }
                val good = opts.filter { o -> o.roll == null || rollInfo(o, l.occ).pct >= 35 }.ifEmpty { opts }
                val o = good[rnd.nextInt(good.size)]
                if (o.roll != null) draw(l.occ, o, beans(rollInfo(o, l.occ))) else choose(l.occ, o)
                if (!alive(h) || h.monk) heirByAge(rnd)
            }
            is Live.HeadGone -> heirByAge(rnd)
            is Live.Quiet -> {}
        }
    }
}

private fun Engine.heirByAge(rnd: Random) {
    val s = succession() ?: return
    val adult = s.heirs.filter { age(it.p) >= 16 }
    takeHeir((s.heirs.firstOrNull { it.eldest } ?: adult.firstOrNull() ?: s.heirs[rnd.nextInt(s.heirs.size)]).p, s.eldest)
}
