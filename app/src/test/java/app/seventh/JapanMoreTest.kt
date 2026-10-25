package app.seventh

import app.seventh.domain.Engine
import app.seventh.domain.Event
import app.seventh.domain.Lang
import app.seventh.domain.Occ
import app.seventh.domain.Person
import app.seventh.domain.japanMoreEvents
import app.seventh.domain.japanMoreHistory
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import kotlin.random.Random

/**
 * Opens every event of the Japan expansion in a state built to fit it (traits, ages, marks, years) and plays every option
 * with a white and a black bean, in both languages, so the rare chain links and the path events are exercised too.
 */
class JapanMoreTest {
    private val cyr = Regex("[А-Яа-яЁё]")
    private val lat = Regex("[A-Za-z]+")
    private val roman = Regex("[IVXLC]+")

    @After fun reset() { Lang.ru = true }

    private class Spec(val year: Int = 1750, val age: Int = 45, val male: Boolean = true, val tr: List<String> = emptyList(), val wed: Boolean = true, val extra: Engine.() -> Unit = {})

    /** A head of [Spec.age] with a spouse and seven children: sons 24 and 20, a married daughter 19, a daughter 17, a son 15, a girl 8, a baby. */
    private fun build(seed: Int, s: Spec): Engine {
        val e = Engine.newGame("edo", Random(seed))
        val g = e.g
        g.year = s.year
        val f = e.h
        f.died = s.year - 1
        val hd = e.reg(e.newP(e.pickName(s.male), s.male, born = s.year - s.age, blood = true, gen = 2, father = f.id.takeIf { f.male }, mother = f.id.takeIf { !f.male }, st = IntArray(6) { 5 }))
        hd.grown = true; hd.edu = "field"
        g.head = hd.id; g.heads.add(hd.id)
        s.tr.forEach { e.addTr(hd, it) }
        if (s.wed) e.mkSpouse(hd)
        fun kid(a: Int, m: Boolean): Person {
            val sp = hd.spouse
            val k = e.reg(e.newP(e.pickName(m), m, born = s.year - a, blood = true, gen = 3, father = if (s.male) hd.id else sp, mother = if (s.male) sp else hd.id))
            k.grown = a >= 13; k.edu = "field"
            return k
        }
        kid(24, true); kid(20, true); e.mkSpouse(kid(19, false)); kid(17, false); kid(15, true); kid(8, false); kid(0, true)
        g.money = 60
        s.extra(e)
        return e
    }

    private fun Engine.son24() = house().first { it.male && age(it) == 24 }

    private val specs: Map<String, List<Spec>> = mapOf(
        "tab" to listOf(Spec(tr = listOf("pyan"))),
        "c_tab" to listOf(Spec { flag("sake_tab", g.year - 2) }),
        "wrath" to listOf(Spec(tr = listOf("gnev"))),
        "girl" to listOf(Spec(tr = listOf("dobr"))),
        "c_found" to listOf(Spec { val p = reg(newP(pickName(false), false, born = g.year - 18)); flag("foundling", g.year - 12); flag("found_id", p.id) }),
        "lend" to listOf(Spec(tr = listOf("zhad"))),
        "c_loan" to listOf(Spec { flag("loan", g.year - 1) }, Spec { flag("loan", g.year - 1); flag("loan_free") }),
        "lineage" to listOf(Spec(tr = listOf("lzh"))),
        "c_line" to listOf(Spec { flag("fake_line", g.year - 9) }, Spec(year = 1880) { flag("fake_line", g.year - 9) }),
        "scribe" to listOf(Spec(tr = listOf("gram")), Spec(year = 1890, tr = listOf("gram"))),
        "c_pupil" to listOf(Spec { flag("terakoya", g.year - 13) }, Spec(year = 1890) { flag("terakoya", g.year - 13) }),
        "straw" to listOf(Spec(tr = listOf("kalek"))),
        "onsen" to listOf(Spec(tr = listOf("hvor"))),
        "rewed" to listOf(Spec(age = 55, wed = false, tr = listOf("vdov")), Spec(age = 55, male = false, wed = false, tr = listOf("vdov"))),
        "print" to listOf(Spec(age = 30, tr = listOf("kras"))),
        "senba" to listOf(Spec(tr = listOf("umn"))),
        "sumo" to listOf(Spec(age = 30, tr = listOf("bog")), Spec { addTr(son24(), "bog") }),
        "kanreki" to listOf(Spec(age = 61)),
        "sutego" to listOf(Spec(age = 42)),
        "split" to listOf(Spec(age = 50), Spec(year = 1890, age = 50)),
        "c_ronin" to listOf(Spec { flag("ronin_hid", 1740) }, Spec { flag("ronin_hid", 1741) }, Spec(year = 1890) { flag("ronin_hid", 1880) }),
        "c_paddy" to listOf(Spec { flag("hidden_paddy", g.year - 7) }, Spec(year = 1880) { flag("hidden_paddy", g.year - 7) }),
        "c_taxrice" to listOf(Spec { flag("tax_rice", g.year - 4) }),
        "c_orin" to listOf(Spec { flag("orin_boy", g.year - 15); flag("orin_f", h.id) }, Spec { flag("orin_boy", g.year - 15); flag("orin_f", g.founder) }),
        "c_oda" to listOf(Spec { val k = son24(); k.monk = true; k.adopted = true; flag("oda_son", g.year - 9); flag("oda_id", k.id) }),
        "c_ikki" to listOf(Spec { flag("ikki", g.year - 9) }),
        "c_gimin" to listOf(Spec { flag("gimin", g.year - 13); flag("gimin_id", g.founder) }),
        "c_toyama" to listOf(Spec { flag("toyama", g.year - 1) }),
        "kaiko" to listOf(Spec(), Spec(year = 1880)),
        "c_vow" to listOf(Spec { flag("vow_torii", g.year - 3) }),
        "hawaii" to listOf(Spec(year = 1890), Spec(year = 1920)),
        "c_emig" to listOf(Spec(year = 1895) { val k = son24(); k.monk = true; k.adopted = true; flag("emig", 1890); flag("emig_id", k.id) },
            Spec(year = 1925) { val k = son24(); k.monk = true; k.adopted = true; flag("emig", 1915); flag("emig_id", k.id) }),
        "c_fox" to listOf(Spec { val s = mkSpouse(son24()); flag("foxwife", g.year - 5); flag("fox_id", s.id) }),
        "w_shop" to listOf(Spec(), Spec(year = 1900)),
        "s_samurai" to listOf(Spec { flag("way_shop") }),
        "c_samurai" to listOf(Spec { flag("sam_debt", 1746) }, Spec { flag("sam_debt", 1747) }),
        "s_banto" to listOf(Spec { flag("way_shop") }),
        "s_dojima" to listOf(Spec { flag("way_shop") }),
        "s_fire" to listOf(Spec { flag("way_shop") }, Spec(year = 1900) { flag("way_shop") }),
        "w_paper" to listOf(Spec()),
        "k_monopoly" to listOf(Spec { flag("way_paper") }),
        "k_order" to listOf(Spec { flag("way_paper") }, Spec(year = 1880) { flag("way_paper") }),
        "k_cold" to listOf(Spec { flag("way_paper") }),
        "k_machine" to listOf(Spec(year = 1900) { flag("way_paper") }),
    )

    private fun texts(e: Engine): List<String> {
        val g = e.g
        val out = mutableListOf<String>()
        out += g.log.map { it.t }
        out += g.secrets.map { it.t }
        for (r in g.relics) { out += r.n; out += r.lines.map { it.t } }
        for (p in g.people.values) { out += p.name; out += p.cause; out += p.from; out += p.deeds.map { it.t } }
        return out
    }

    private fun check(ev: Event, s: String) {
        if (Lang.ru) {
            val bad = lat.findAll(s).map { it.value }.filterNot { roman.matches(it) }.toList()
            assertTrue("${ev.id}: Latin in Russian: $bad in «$s»", bad.isEmpty())
        } else assertTrue("${ev.id}: Cyrillic in English: «$s»", !cyr.containsMatchIn(s))
    }

    /** Builds the state for [spec] with the first seed at which [ev] fires. */
    private fun open(ev: Event, spec: Spec): Int {
        for (seed in 0 until 600) {
            val e = build(seed, spec)
            if (ev.cond(e, e.h) != null) return seed
        }
        fail("${ev.id} never fires in its test state")
        return -1
    }

    private fun occ(ev: Event, spec: Spec, seed: Int): Pair<Engine, Occ> {
        val e = build(seed, spec)
        val f = ev.cond(e, e.h)!!
        return e to Occ(ev, e.h, f.x, f.s)
    }

    @Test fun everyNewEventOpensAndPlays() {
        val ids = (japanMoreEvents + japanMoreHistory).map { it.id }
        assertTrue("duplicate ids", ids.size == ids.toSet().size)
        var played = 0
        for (ru in listOf(true, false)) {
            Lang.ru = ru
            for (ev in japanMoreEvents + japanMoreHistory) {
                val list = specs[ev.id] ?: listOf(Spec(year = if (ev.y0 > 0) ev.y0 else 1750))
                for (spec in list) {
                    val seed = open(ev, spec)
                    val (e0, o0) = occ(ev, spec, seed)
                    check(ev, ev.t); check(ev, ev.txt(e0, o0))
                    val opts = ev.opts(e0, o0)
                    assertTrue("${ev.id}: no free option", opts.any { it.cost == 0 })
                    for (o in opts) { check(ev, o.t); o.tag?.let { check(ev, it) } }
                    for (i in opts.indices) for (bean in listOf('w', 'b')) {
                        val (e, oc) = occ(ev, spec, seed)
                        ev.txt(e, oc)
                        val o = ev.opts(e, oc)[i]
                        if (o.roll == null && bean == 'b') continue
                        val res = if (o.roll == null) e.choose(oc, o) else e.draw(oc, o, listOf(bean)).third
                        assertTrue("${ev.id}: empty outcome", res.text.isNotBlank())
                        check(ev, res.text)
                        res.diffs.forEach { check(ev, it.t) }
                        texts(e).forEach { check(ev, it) }
                        played++
                    }
                }
            }
        }
        println("JapanMoreTest: ${ids.size} events, $played outcomes played")
    }
}
