package app.seventh

import app.seventh.domain.Engine
import app.seventh.domain.Event
import app.seventh.domain.Lang
import app.seventh.domain.Occ
import app.seventh.domain.europeMoreEvents
import app.seventh.domain.europeMoreHistory
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Opens every new Europe event in a state built to fit it and plays every option with a white and a black bean, in both languages. */
class EuropeMoreTest {
    private val cyr = Regex("[А-Яа-яЁё]+")
    private val lat = Regex("[A-Za-zÀ-ÿ]+")
    private val roman = Regex("[IVXLC]+")

    private val chainKeys = listOf("eu_blow", "eu_fakerel", "eu_loan", "eu_found", "eu_vow", "eu_grain", "eu_peronnelle", "eu_templar", "eu_fairy", "eu_lombard", "eu_crone", "eu_meja", "eu_rebel", "eu_town", "way_guild", "way_free", "way_abbey")

    /** A household with a head of [headAge], every temper the new events look for, children of all ages, a monk, a dead ancestor; marks by [mode]. */
    private fun state(ev: Event, seed: Int, headAge: Int, mode: Int): Engine {
        val e = Engine.newGame("eu", Random(seed))
        val g = e.g
        val founder = e.h
        g.year = if (ev.y0 > 0) ev.y0 else 1300
        founder.died = g.year - 5
        val h = e.reg(e.newP(e.pickName(true), true, born = g.year - headAge, blood = true, gen = 3, st = IntArray(6) { 5 }))
        h.grown = true
        g.head = h.id
        for (t in listOf("pyan", "gnev", "dobr", "zhad", "lzh", "bogom", "gram", "kalek", "hvor", "vdov", "kras", "bog", "hrab", "umn")) e.addTr(h, t)
        val w = e.mkSpouse(h)
        h.wed = g.year
        g.money = 60
        for ((a, male) in listOf(0 to true, 3 to false, 7 to true, 10 to true, 12 to false, 15 to true, 17 to false, 20 to true, 24 to true, 28 to false, 30 to true)) {
            val c = e.reg(e.newP(e.pickName(male), male, born = g.year - a, blood = true, gen = 4, father = h.id, mother = w.id, st = IntArray(6) { 5 }))
            c.grown = a >= 13
            if (a == 17) e.addTr(c, "kras")
            if (a == 28) e.mkSpouse(c)
            if (a == 30) c.monk = true
        }
        val past = g.year - 40
        if (mode >= 1) for (k in chainKeys) e.flag(k, past)
        if (mode == 1) { e.flag("eu_usury", past); e.flag("eu_rout_hid", past) }
        if (mode == 2) { e.flag("eu_rout_sold", past); e.unflag("way_guild") }
        return e
    }

    private fun open(ev: Event, seed: Int, headAge: Int, mode: Int): Pair<Engine, Occ>? {
        val e = state(ev, seed, headAge, mode)
        val f = ev.cond(e, e.h) ?: return null
        return e to Occ(ev, e.h, f.x, f.s)
    }

    private fun play(ru: Boolean, out: MutableList<String>, fired: MutableSet<String>) {
        val was = Lang.ru
        Lang.ru = ru
        try {
            for (ev in europeMoreEvents + europeMoreHistory) for (mode in 0..2) for (headAge in listOf(35, 62)) {
                val seed = (0 until 300).firstOrNull { open(ev, it, headAge, mode) != null } ?: continue
                fired.add(ev.id)
                val n = open(ev, seed, headAge, mode)!!.let { (e, occ) -> ev.opts(e, occ).size }
                for (i in 0 until n) for (bean in listOf('w', 'b')) {
                    val (e, occ) = open(ev, seed, headAge, mode)!!
                    out += ev.t
                    out += ev.txt(e, occ)
                    val opts = ev.opts(e, occ)
                    if (i >= opts.size) continue
                    val o = opts[i]
                    out += o.t; o.tag?.let { out += it }
                    if (o.roll == null && bean == 'b') continue
                    out += if (o.roll != null) e.draw(occ, o, listOf(bean)).third.text else e.choose(occ, o).text
                    if (!e.alive(e.h) || e.h.monk) e.succession()?.let { s -> e.takeHeir(s.heirs.first().p, s.eldest) }
                    out += e.g.log.map { it.t }
                    for (p in e.g.people.values) { out += p.cause; p.deeds.forEach { out += it.t } }
                    e.g.secrets.forEach { out += it.t }
                    e.g.relics.forEach { r -> out += r.n; r.lines.forEach { out += it.t } }
                }
            }
        } finally {
            Lang.ru = was
        }
    }

    @Test fun everyNewEventPlaysInBothLanguages() {
        val all = (europeMoreEvents + europeMoreHistory).map { it.id }
        assertTrue("duplicate ids", all.size == all.toSet().size)
        for (ru in listOf(true, false)) {
            val out = mutableListOf<String>()
            val fired = mutableSetOf<String>()
            play(ru, out, fired)
            assertTrue("never opened: ${all - fired}", fired.containsAll(all))
            val bad = sortedMapOf<String, String>()
            for (s in out) {
                if (ru) { for (m in lat.findAll(s)) if (!roman.matches(m.value)) bad.putIfAbsent(m.value, s) }
                else for (m in cyr.findAll(s)) bad.putIfAbsent(m.value, s)
            }
            bad.forEach { (w, s) -> println("${if (ru) "Latin in RU" else "RU in EN"}: «$w» in: $s") }
            assertTrue("mixed language (ru=$ru): ${bad.keys}", bad.isEmpty())
        }
    }
}
