package app.seventh

import app.seventh.domain.Engine
import app.seventh.domain.Lands
import app.seventh.domain.Lang
import app.seventh.domain.Live
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/** Plays dynasties in each language and checks that no text of the other language leaks into the content. */
class LangTest {
    private val cyr = Regex("[А-Яа-яЁё]+")
    private val lat = Regex("[A-Za-zÀ-ÿ]+")

    private fun collect(land: String, seed: Int, out: MutableList<String>) {
        val rnd = Random(seed)
        val e = Engine.newGame(land, rnd)
        val start = e.g.year
        val founder = e.h
        out += e.land.intro(e, founder)
        out += listOf(e.land.family(e.g), e.land.surname(e.g, founder), e.land.cur, e.land.moneyLabel, e.land.helpFeud, e.land.helpHeart, e.land.rival.g, e.land.rival.i, e.land.rival.n)
        out += e.land.maleNames + e.land.femaleNames + e.land.stems + e.land.places + e.land.families + e.land.dvor
        e.land.traitNames.values.forEach { out += it.first; out += it.second }
        val n = e.land.nakaz
        out += listOf(n.money(10), n.glory, n.dvor(e.land.dvor[1]), n.gram, n.feud)
        while (!e.g.over && e.g.year < start + 320) {
            when (val l = e.live()) {
                is Live.Happens -> {
                    val occ = l.occ
                    out += occ.ev.t
                    out += occ.ev.txt(e, occ)
                    val all = occ.ev.opts(e, occ)
                    all.forEach { out += it.t; it.tag?.let { t -> out += t } }
                    val opts = all.filter { it.cost <= e.g.money }
                    val o = opts[rnd.nextInt(opts.size)]
                    out += if (o.roll != null) e.draw(occ, o, e.beans(e.rollInfo(o, occ))).third.text else e.choose(occ, o).text
                    if (!e.alive(e.h) || e.h.monk) e.succession()?.let { s -> e.takeHeir(s.heirs[rnd.nextInt(s.heirs.size)].p, s.eldest) }
                }
                is Live.HeadGone -> e.succession()?.let { s -> e.takeHeir(s.heirs[rnd.nextInt(s.heirs.size)].p, s.eldest) }
                is Live.Quiet -> {}
            }
        }
        out += e.g.log.map { it.t }
        for (p in e.g.people.values) { out += p.cause; out += p.from; p.deeds.forEach { out += it.t } }
        e.g.secrets.forEach { out += it.t }
        e.g.relics.forEach { r -> out += r.n; r.lines.forEach { out += it.t } }
        e.g.nakaz?.let { out += it.t }
    }

    private fun texts(ru: Boolean): List<String> {
        val was = Lang.ru
        Lang.ru = ru
        try {
            val out = mutableListOf<String>()
            for (land in Lands.all) repeat(40) { i -> collect(land.id, i * 17 + 3, out) }
            return out
        } finally {
            Lang.ru = was
        }
    }

    @Test fun englishHasNoCyrillic() {
        val bad = sortedMapOf<String, String>()
        for (s in texts(false)) for (m in cyr.findAll(s)) bad.putIfAbsent(m.value, s)
        bad.forEach { (w, s) -> println("RU in EN: «$w» in: $s") }
        assertTrue("Cyrillic in English mode: ${bad.keys}", bad.isEmpty())
    }

    @Test fun russianHasNoLatin() {
        val ok = Regex("[IVXLC]+")
        val bad = sortedMapOf<String, String>()
        for (s in texts(true)) for (m in lat.findAll(s)) if (!ok.matches(m.value)) bad.putIfAbsent(m.value, s)
        bad.forEach { (w, s) -> println("Latin in RU: «$w» in: $s") }
        assertTrue("Latin in Russian mode: ${bad.keys}", bad.isEmpty())
    }
}
