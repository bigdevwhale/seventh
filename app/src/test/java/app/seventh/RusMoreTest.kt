package app.seventh

import app.seventh.domain.Engine
import app.seventh.domain.Event
import app.seventh.domain.Fire
import app.seventh.domain.Lang
import app.seventh.domain.Occ
import app.seventh.domain.Traits
import app.seventh.domain.rusMoreEvents
import app.seventh.domain.rusMoreHistory
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import kotlin.random.Random

/**
 * Opens every new Rus event in a state built to fit it (traits, flags, ages, years) and plays every option
 * with a white and a black bean in both languages: chain links and path events that random play rarely reaches.
 */
class RusMoreTest {
    private val cyr = Regex("[А-Яа-яЁё]+")
    private val lat = Regex("[A-Za-z]+")
    private val roman = Regex("[IVXLC]+")
    private val marks = listOf("zarok", "deserter", "grain", "agr_son", "skit", "found", "debt", "vow", "koz_saved", "koz_hurt", "way_trade", "guild", "way_icon", "klad", "bunt", "town")

    @After fun reset() { Lang.ru = true }

    /** A fresh family shaped by [seed]: head of any age and sex, a spouse, children of every age, random traits and marks. */
    private fun build(ev: Event, seed: Int): Pair<Engine, Fire?> {
        val rnd = Random(seed)
        val e = Engine.newGame("rus", rnd)
        val g = e.g
        g.year = if (ev.y0 > 0) e.ri(ev.y0, ev.y1) else e.ri(1695, 1940)
        val male = e.chance(.6)
        val h = e.reg(e.newP(e.pickName(male), male, born = g.year - e.ri(20, 72), blood = true, gen = 2, st = e.rstats(2, 8)))
        h.grown = true
        g.head = h.id
        val traits = Traits.all.keys.toList()
        repeat(e.ri(1, 5)) { e.addTr(h, e.pick(traits)) }
        if (e.chance(.8)) {
            val s = e.reg(e.newP(e.pickName(!male), !male, born = g.year - e.ri(18, 48), st = e.rstats(2, 8), from = e.pick(e.land.families)))
            s.grown = true; s.spouse = h.id; h.spouse = s.id; s.last = g.year - 3
        } else if (e.chance(.5)) e.addTr(h, "vdov")
        val wife = if (male) e.spouseOf(h) else h
        for (a in listOf(1, 2, 5, 8, 12, 15, 16, 19, 21, 24, 30)) {
            if (!e.chance(.6)) continue
            val km = e.chance(.5)
            val k = e.reg(e.newP(e.pickName(km), km, born = g.year - a, blood = true, gen = 3, father = if (male) h.id else h.spouse, mother = if (male) wife?.id else h.id, st = e.rstats(2, 9)))
            k.grown = a >= 13
            if (e.chance(.3)) e.addTr(k, e.pick(listOf("bog", "kras", "umn", "hvor", "gnev", "dobr")))
            if (a >= 17 && e.chance(.35)) e.mkSpouse(k)
        }
        g.money = if (e.chance(.2)) 3 else 60
        g.feud = e.ri(0, 4)
        g.dvor = e.ri(0, 4)
        for (m in marks) if (e.chance(.45)) e.flag(m, g.year - e.ri(0, 25))
        if (e.flagged("zarok")) e.flag("zarok_by", h.id)
        if (e.flagged("town")) e.kidIn(13, 40).firstOrNull()?.let { e.flag("town_id", it.id) } ?: e.unflag("town")
        return e to ev.cond(e, h)
    }

    private fun check(out: List<String>, ru: Boolean, ev: Event) {
        for (s in out) {
            if (ru) { for (m in lat.findAll(s)) if (!roman.matches(m.value)) fail("${ev.id}: Latin «${m.value}» in: $s") }
            else cyr.find(s)?.let { fail("${ev.id}: Cyrillic «${it.value}» in: $s") }
        }
    }

    private fun texts(e: Engine): List<String> {
        val out = mutableListOf<String>()
        out += e.g.log.map { it.t }
        for (p in e.g.people.values) { out += p.cause; out += p.epi; p.deeds.forEach { out += it.t } }
        e.g.secrets.forEach { out += it.t }
        e.g.relics.forEach { r -> out += r.n; r.lines.forEach { out += it.t } }
        return out
    }

    @Test fun everyNewEventOpensAndPlays() {
        val all = rusMoreEvents + rusMoreHistory
        val ids = all.map { it.id }
        assertTrue("duplicate ids", ids.size == ids.toSet().size)
        for (ev in all) {
            val seed = (0 until 6000).firstOrNull { build(ev, it).second != null } ?: run { fail("${ev.id}: never fires"); 0 }
            for (ru in listOf(true, false)) {
                Lang.ru = ru
                val nOpts = build(ev, seed).let { (e, f) -> ev.opts(e, Occ(ev, e.h, f!!.x, f.s)).size }
                for (i in 0 until nOpts) for (bean in listOf('w', 'b')) {
                    val (e, f) = build(ev, seed)
                    val occ = Occ(ev, e.h, f!!.x, f.s)
                    val out = mutableListOf(ev.t, ev.txt(e, occ))
                    val opts = ev.opts(e, occ)
                    opts.forEach { out += it.t; it.tag?.let { t -> out += t } }
                    val o = opts[i]
                    if (o.roll == null) { if (bean == 'b') continue; out += e.choose(occ, o).text }
                    else { e.rollInfo(o, occ); out += e.draw(occ, o, listOf(bean)).third.text }
                    out += texts(e)
                    if (!e.alive(e.h) || e.h.monk) e.succession()
                    check(out, ru, ev)
                }
            }
        }
    }

    @Test fun chainsCloseTheirMarks() {
        val opens = mapOf("zarok_end" to "zarok", "soldat" to "deserter", "ambar" to "grain", "agrsyn" to "agr_son", "naiden2" to "found", "dolg2" to "debt", "obet" to "vow", "skit" to "skit", "klad2" to "klad", "gorod2" to "town", "usmir" to "bunt")
        for ((id, mark) in opens) {
            val ev = rusMoreEvents.first { it.id == id }
            val seed = (0 until 6000).first { build(ev, it).second != null }
            val (e0, f0) = build(ev, seed)
            val n = ev.opts(e0, Occ(ev, e0.h, f0!!.x, f0.s)).size
            for (i in 0 until n) {
                val (e, f) = build(ev, seed)
                val occ = Occ(ev, e.h, f!!.x, f.s)
                val o = ev.opts(e, occ)[i]
                if (o.roll == null) e.choose(occ, o) else e.draw(occ, o, listOf('w'))
                assertTrue("$id option $i leaves $mark set", !e.flagged(mark))
            }
        }
    }

}
