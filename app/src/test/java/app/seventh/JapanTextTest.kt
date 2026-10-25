package app.seventh

import app.seventh.domain.Engine
import app.seventh.domain.Event
import app.seventh.domain.Japan
import app.seventh.domain.Lang
import app.seventh.domain.Live
import app.seventh.domain.Occ
import app.seventh.domain.Opt
import app.seventh.domain.Secret
import app.seventh.domain.Traits
import app.seventh.domain.autoplay
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Japan speaks both languages: in English mode nothing that comes from the Japan pack may contain Cyrillic. */
class JapanTextTest {
    private val cyr = Regex("[А-Яа-яЁё]")
    /** Text the shared Rus.kt helpers and the engine add (not bilingual in this clone yet). */
    private val engineDeeds = listOf("Ушёл на службу", "Ушла на службу", "Вернулся со службы", "Вернулась со службы", "Ушёл в монастырь", "Ушла в монастырь")
    private val engineCauses = listOf("на службе", "в сражении", "в госпитале", "родильная горячка")
    private val redPrefix = "Красный боб — проклятие помнит вас. "

    @After fun reset() { Lang.ru = true }

    private fun isHelper(o: Opt) = o.cand != null || o.t == "Уйти в монастырь"

    private fun collectOcc(e: Engine, occ: Occ, out: MutableList<String>): List<Opt> {
        out.add(occ.ev.t)
        out.add(occ.ev.txt(e, occ))
        val opts = occ.ev.opts(e, occ)
        for (o in opts) if (!isHelper(o)) { out.add(o.t); o.tag?.let { out.add(it) } }
        return opts
    }

    private fun collectState(e: Engine, out: MutableList<String>) {
        val g = e.g
        out += listOf(g.stem, g.place, Japan.cur, Japan.moneyLabel, Japan.helpFeud, Japan.helpHeart, Japan.rival.g, Japan.rival.i, Japan.rival.n)
        out += Japan.dvor + Japan.families + Japan.maleNames + Japan.femaleNames
        out += Japan.traitNames.values.flatMap { listOf(it.first, it.second) }
        out += Japan.intro(e, g.people.getValue(g.founder))
        out.add(Japan.curseLog(e, g.people.getValue(g.founder)))
        val n = Japan.nakaz
        out += listOf(n.money(10), n.glory, n.dvor("x"), n.gram, n.feud)
        g.nakaz?.let { out.add(it.t) }
        for (p in g.people.values) {
            out.add(p.name)
            if (p.cause.isNotEmpty() && engineCauses.none { p.cause.contains(it) }) out.add(p.cause)
            for (d in p.deeds) if (engineDeeds.none { d.t.startsWith(it) }) out.add(d.t)
        }
        for (s in g.secrets) out.add(s.t)
        for (r in g.relics) { out.add(r.n); r.lines.firstOrNull()?.let { out.add(it.t) } }
    }

    /** Random play like EngineTest, collecting every text the Japan pack produces. */
    private fun play(seed: Int, out: MutableList<String>) {
        val rnd = Random(seed)
        val e = Engine.newGame("edo", rnd)
        val start = e.g.year
        while (!e.g.over && e.g.year < start + 320) {
            when (val l = e.live()) {
                is Live.Happens -> {
                    val opts = collectOcc(e, l.occ, out).filter { it.cost <= e.g.money }
                    val o = opts[rnd.nextInt(opts.size)]
                    val res = if (o.roll != null) e.draw(l.occ, o, e.beans(e.rollInfo(o, l.occ))).third else e.choose(l.occ, o)
                    out.add(res.text.removePrefix(redPrefix))
                    if (!e.alive(e.h) || e.h.monk) e.succession()?.let { s -> e.takeHeir(s.heirs[rnd.nextInt(s.heirs.size)].p, s.eldest) }
                }
                is Live.HeadGone -> e.succession()?.let { s -> e.takeHeir(s.heirs[rnd.nextInt(s.heirs.size)].p, s.eldest) }
                is Live.Quiet -> {}
            }
        }
        collectState(e, out)
    }

    /** A family some decades in, with an unmarried grown son, a secret and a last wish, so every event can be shown. */
    private fun family(seed: Int, year: Int): Engine {
        val rnd = Random(seed)
        var e: Engine
        var s = seed
        do { e = Engine.newGame("edo", Random(s++)); e.autoplay(40, rnd) } while (e.g.over)
        val g = e.g
        val h = e.h
        g.year = year // ages of the old family go odd; only the two sons below matter
        val kid = e.reg(e.newP(e.pickName(true), true, born = g.year - 20, blood = true, gen = h.gen + 1, father = if (h.male) h.id else null, mother = if (h.male) null else h.id))
        kid.edu = null
        e.reg(e.newP(e.pickName(true), true, born = g.year - 22, blood = true, gen = h.gen + 1, father = if (h.male) h.id else null, mother = if (h.male) null else h.id))
        g.secrets.add(0, Secret("—", "—", g.year - 5))
        if (g.nakaz == null) g.nakaz = e.makeNakaz(h)
        g.grudge = kid.id
        g.money = 100
        return e
    }

    /** Shows every event in every year band and plays every branch of every choice on a fresh family. */
    private fun sweep(out: MutableList<String>) {
        val all: List<Event> = Japan.forced + Japan.events + Japan.history
        for (yearCase in listOf(0, 1)) for (ev in all) {
            fun fresh(): Pair<Engine, Occ> {
                val e = family(11, if (ev.y0 > 0) ev.y0 else if (yearCase == 0) 1700 else 1890)
                val kid = e.house().first { it.id != e.g.head && e.g.year - it.born == 20 }
                return e to Occ(ev, e.h, kid, e.g.secrets.first())
            }
            val (e0, occ0) = fresh()
            val n = collectOcc(e0, occ0, out).size
            for (i in 0 until n) for (branch in 0..1) {
                val (e, occ) = fresh()
                val o = ev.opts(e, occ)[i]
                val text = when {
                    o.roll == null -> if (branch == 0) e.choose(occ, o).text else continue
                    else -> e.draw(occ, o, listOf(if (branch == 0) 'w' else 'b')).third.text
                }
                out.add(text)
                collectState(e, out)
            }
        }
    }

    @Test fun englishHasNoCyrillic() {
        Lang.ru = false
        val out = mutableListOf<String>()
        repeat(40) { play(it * 13 + 1, out) }
        sweep(out)
        // Shared trait names (Traits.kt) are not bilingual in this clone yet.
        val traitWords = Traits.all.values.flatMap { listOf(it.m, it.f) }.sortedByDescending { it.length }
        fun strip(s: String) = traitWords.fold(s) { acc, w -> acc.replace(w, "").replace(w.replaceFirstChar { it.uppercase() }, "") }
        val bad = out.map(::strip).filter { cyr.containsMatchIn(it) }.distinct()
        assertTrue("Cyrillic in English mode:\n" + bad.take(30).joinToString("\n"), bad.isEmpty())
    }

    @Test fun russianPlays() {
        Lang.ru = true
        val out = mutableListOf<String>()
        repeat(20) { play(it * 17 + 3, out) }
        sweep(out)
        assertTrue(out.all { it.isNotBlank() || it.isEmpty() })
        assertTrue(out.any { cyr.containsMatchIn(it) })
    }
}
