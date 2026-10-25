package app.seventh.domain

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

/** The years between two events, as the summary shows them. */
class Since(val y0: Int, val y: Int, val lines: List<LogLine>)

sealed class Live {
    class Happens(val occ: Occ, val since: Since) : Live()
    class HeadGone(val since: Since) : Live()
    class Quiet(val since: Since) : Live()
}

class RollInfo(val parts: List<Pair<String, Int>>, val white: Int, val black: Int, val red: Int) {
    val beans: Int get() = white + black + red
    val pct: Int get() = (white * 100.0 / (12 + red)).roundToInt()
}

class Diff(val t: String, val good: Boolean)

/** What came of a choice: the text, a verdict (true luck, false misfortune, null no draw), whether the red bean came out. */
class Outcome(val text: String, val good: Boolean?, val red: Boolean, val stone: String?, val diffs: List<Diff>)

class Heir(val p: Person, val eldest: Boolean, val warn: String?)

/** A head has died or left: their last wish and who may take over. */
class Succession(val dead: Person, val heirs: List<Heir>, val eldest: Person?)

/** The rules of the game, played on a [Game]. Content (lands) calls these helpers from inside its events. */
class Engine(val g: Game, val rnd: Random = Random.Default) {
    val land: Land = Lands.of(g.land)

    // ---- dice
    fun ri(a: Int, b: Int) = a + rnd.nextInt(b - a + 1)
    fun <T> pick(l: List<T>): T = l[rnd.nextInt(l.size)]
    fun pick(a: IntArray): Int = a[rnd.nextInt(a.size)]
    fun chance(p: Double) = rnd.nextDouble() < p

    // ---- event conditions
    val yes = Fire()
    fun fire(b: Boolean) = if (b) yes else null
    fun fire(p: Person?) = p?.let { Fire(it) }

    // ---- people
    val h: Person get() = g.people.getValue(g.head)
    fun person(id: Int?) = id?.let { g.people[it] }
    fun age(p: Person) = (p.died ?: g.year) - p.born
    fun alive(p: Person) = p.died == null
    fun has(p: Person, t: String) = t in p.tr
    fun sx(p: Person, m: String, f: String) = if (p.male) m else f
    fun eff(p: Person, s: Stat): Int {
        var v = p[s]
        for (t in p.tr) v += Traits.all[t]?.mods?.get(s) ?: 0
        return v.coerceIn(0, 12)
    }
    fun tname(t: String, p: Person?): String {
        val tr = Traits.all.getValue(t)
        val (m, f) = land.traitNames[t] ?: (tr.m to tr.f)
        return if (p != null && !p.male) f else m
    }
    fun addTr(p: Person, t: String): Boolean {
        if (has(p, t)) return false
        Traits.all[t]?.opp?.let { p.tr.remove(it) }
        p.tr.add(t)
        return true
    }
    fun kidsOf(p: Person) = g.people.values.filter { it.blood && (it.father == p.id || it.mother == p.id) }.sortedWith(compareBy({ it.born }, { it.id }))
    fun desc(p: Person): List<Person> {
        val out = mutableListOf<Person>()
        val q = ArrayDeque(kidsOf(p))
        while (q.isNotEmpty()) { val c = q.removeFirst(); out.add(c); q.addAll(kidsOf(c)) }
        return out
    }
    fun house() = (listOf(h) + desc(h)).filter { alive(it) && !it.monk }
    fun spouseOf(p: Person) = person(p.spouse)
    fun married(p: Person) = spouseOf(p)?.let { alive(it) && !it.monk } == true
    fun surname(p: Person) = land.surname(g, p)
    fun fullName(p: Person) = if (p.blood) p.name + " " + surname(p) else p.name + if (p.from.isNotEmpty()) t(" из ", " of ") + p.from else ""
    fun cap(s: String) = s.replaceFirstChar { it.uppercase() }
    fun yrs(n: Int) = if (Lang.ru) "$n " + plural(n, "год", "года", "лет") else if (n == 1) "1 year" else "$n years"

    // ---- the household
    fun log(t: String, k: String = "") { g.log.add(LogLine(g.year, t, k)); if (g.log.size > 600) g.log.removeAt(0) }
    fun money(n: Int) { g.money = max(0, g.money + n) }
    fun glory(n: Int) { g.glory = max(0, g.glory + n) }
    fun feud(n: Int) { g.feud = (g.feud + n).coerceIn(0, 5) }
    fun deed(p: Person, t: String) { p.deeds.add(Note(g.year, t)) }
    fun addSecret(t: String, p: Person) { g.secrets.add(Secret(t, p.name, g.year)) }
    fun addRelic(n: String, ic: String, b: Map<Stat, Int>, line: String) { g.relics.add(Relic(n, ic, b, mutableListOf(Note(g.year, line)))) }
    fun curseBeans() = (if (g.won) 0 else 1) + if (g.hex > g.year) 1 else 0
    fun part(n: Double) = ceil(g.money * n).toInt()

    fun look(male: Boolean) = Look(pick(Looks.SKIN), pick(Looks.HAIR), pick(if (male) Looks.CLOTH_M else Looks.CLOTH_F), pick(Looks.KERCH))
    fun rstats(lo: Int, hi: Int) = IntArray(6) { ri(lo, hi) }
    fun pickName(male: Boolean, avoid: Set<String> = emptySet()): String {
        val l = if (male) land.maleNames else land.femaleNames
        var n: String
        var i = 0
        do { n = pick(l) } while (n in avoid && i++ < 40)
        return n
    }
    fun newP(name: String, male: Boolean, born: Int = g.year, blood: Boolean = false, gen: Int = 0, father: Int? = null, mother: Int? = null,
             st: IntArray = IntArray(6) { 4 }, tr: List<String> = emptyList(), look: Look? = null, from: String = "", bast: Boolean = false) =
        Person(0, name, male, born, father = father, mother = mother, blood = blood, gen = gen, st = st, tr = tr.toMutableList(), look = look ?: look(male), from = from, bast = bast)
    fun reg(p: Person): Person { p.id = g.nid++; g.people[p.id] = p; return p }

    fun relation(p: Person, from: Person): String {
        var d = 0
        var c: Person? = p
        while (c != null && d < 12) {
            val f = person(c.father)
            val m = person(c.mother)
            val up = if (f != null && f.blood) f else if (m != null && m.blood) m else null
            d++
            if (f === from || m === from) break
            c = up
            if (c == null) return t("родня", "kin")
        }
        return when (d) {
            1 -> t(sx(p, "сын", "дочь"), sx(p, "son", "daughter"))
            2 -> t(sx(p, "внук", "внучка"), sx(p, "grandson", "granddaughter"))
            3 -> t(sx(p, "правнук", "правнучка"), sx(p, "great-grandson", "great-granddaughter"))
            else -> t("пра".repeat(d - 3) + sx(p, "внук", "внучка"), "great-".repeat(d - 2) + sx(p, "grandson", "granddaughter"))
        }
    }

    fun grow(p: Person, parent: Person?): List<String> {
        val ps = Traits.PAIRS.shuffled(rnd).take(2)
        val bias = land.eduBias[p.edu] ?: emptyList()
        val got = mutableListOf<String>()
        for ((a, b) in ps) {
            var pa = .55
            if (a in bias) pa += .25
            if (b in bias) pa -= .25
            if (parent != null && chance(.35)) { if (has(parent, a)) pa = .9; if (has(parent, b)) pa = .1 }
            val t = if (chance(pa)) a else b
            addTr(p, t)
            got.add(tname(t, p))
        }
        p.grown = true
        return got
    }

    fun makeEpi(p: Person): String {
        val a = age(p)
        if (a < 3) return t(sx(p, "Прожил", "Прожила") + " " + yrs(a) + ".", "Lived " + yrs(a) + ".")
        if (a < 13) return t(sx(p, "Прожил", "Прожила") + " " + yrs(a) + ". Не " + sx(p, "успел", "успела") + " вырасти.", "Lived " + yrs(a) + ". Never grew up.")
        val tr = p.tr.filter { Traits.all[it]?.kind != TrKind.NAZH || it == "gram" || it == "pyan" }.take(3).map { tname(it, p) }
        var s = if (tr.isNotEmpty()) cap(tr.joinToString(", ")) + "." else ""
        val d = p.deeds.lastOrNull()
        s += when {
            d != null -> " " + d.t + "."
            !p.blood -> " " + t(sx(p, "Вошёл", "Вошла") + " в род со стороны.", "Married into the family.")
            else -> " " + t(sx(p, "Жил", "Жила") + " тихо, " + sx(p, "умер", "умерла") + " в своей постели.", "Lived quietly, died in " + sx(p, "his", "her") + " own bed.")
        }
        return s.trim()
    }

    fun kill(p: Person, cause: String) {
        if (!alive(p)) return
        p.died = g.year; p.cause = cause; p.epi = makeEpi(p)
        spouseOf(p)?.let { s -> if (alive(s) && s.spouse == p.id) addTr(s, "vdov") }
        log(t("${sx(p, "Умер", "Умерла")} ${fullName(p)}, ${yrs(age(p))} — $cause.", "${fullName(p)} died, aged ${age(p)}: $cause."), "death")
    }

    // ---- a year passes
    fun mortality(p: Person): Double {
        val a = age(p)
        var q = when {
            a < 1 -> .13
            a < 5 -> .045
            a < 15 -> .01
            else -> .0035 * exp((a - 20) / 11.5)
        }
        q *= 1.45 - eff(p, Stat.ZDR) * .09
        if (has(p, "hvor")) q *= 1.5
        if (has(p, "dolg")) q *= .5
        if (has(p, "pyan")) q *= 1.3
        if (a < 15) { q *= 1 - .08 * g.dvor; if ("krep" in g.perks) q *= .65 } else if ("dolg" in g.perks && p.blood) q *= .75
        if (p.id == g.head && desc(p).none { alive(it) }) q *= .6
        q *= land.mort(g.year)
        return q.coerceIn(0.0, .85)
    }

    fun birth(par: Person): Person {
        val sp = spouseOf(par)
        val fa = if (par.male) par else sp
        val mo = if (!par.male) par else sp
        val male = chance(.51)
        val avoid = kidsOf(par).filter { alive(it) }.map { it.name }.toMutableSet()
        fa?.let { avoid.add(it.name) }; mo?.let { avoid.add(it.name) }
        val st = IntArray(6) { k -> (((fa?.st?.get(k) ?: 4) + (mo?.st?.get(k) ?: 4)) / 2.0).roundToInt() + ri(-2, 2) }.map { it.coerceIn(1, 9) }.toIntArray()
        if ("yaz" in g.perks) st[Stat.SER.ordinal] = (st[Stat.SER.ordinal] + 1).coerceIn(1, 10)
        val tr = mutableListOf<String>()
        for (t in Traits.KROV) {
            var q = 0.0
            for (z in listOf(fa, mo)) if (z != null && has(z, t)) q = 1 - (1 - q) * (1 - Traits.all.getValue(t).inh)
            if (chance(q)) tr.add(t)
        }
        if (chance(.07)) { val t = pick(Traits.KROV); if (t !in tr) tr.add(t) }
        if ("bog" in tr && "hil" in tr) tr.remove(if (chance(.5)) "bog" else "hil")
        val src = (if (chance(.5)) fa else mo) ?: par
        val look = Look(src.look.skin, if (chance(.7)) src.look.hair else pick(Looks.HAIR), pick(if (male) Looks.CLOTH_M else Looks.CLOTH_F), pick(Looks.KERCH))
        val c = reg(newP(pickName(male, avoid), male, blood = true, gen = par.gen + 1, father = fa?.id, mother = mo?.id, st = st, tr = tr, look = look))
        mo?.last = g.year
        return c
    }

    fun mkSpouse(p: Person, from: String? = null): Person {
        val male = !p.male
        val s = reg(newP(pickName(male, setOf(p.name)), male, born = g.year - ri(16, 25), st = rstats(1, 8), from = from ?: pick(land.families)))
        addTr(s, pick(Traits.PAIRS).let { if (chance(.5)) it.first else it.second })
        if (chance(.25)) addTr(s, pick(Traits.KROV))
        s.spouse = p.id; p.spouse = s.id; s.wed = g.year; p.wed = g.year; s.grown = true
        return s
    }

    fun tick() {
        g.year++
        val hd = h
        val inc = 2 + g.dvor + eff(hd, Stat.UM) / 4 + (if (has(hd, "zhad")) 1 else 0) - (if (has(hd, "pyan")) 2 else 0) - (if (age(hd) < 14) 1 else 0) + land.income(g.year)
        g.money = max(0, g.money + inc)
        for (p in house()) {
            if (!married(p) || p.away != null) continue
            val s = spouseOf(p)!!
            val wife = if (!p.male) p else s
            val wa = age(wife)
            if (wa < 17 || wa > 44 || has(p, "bespl") || has(s, "bespl")) continue
            if (g.year - wife.last < 2) continue
            val pr = .36 * (if (wa > 35) .55 else 1.0) * (if (has(wife, "hvor")) .7 else 1.0) * (if ("plod" in g.perks) 1.3 else 1.0)
            if (chance(pr)) {
                val c = birth(p)
                log(t("${p.name}: ${sx(c, "родился сын", "родилась дочь")} ${c.name}", "${p.name}: a ${sx(c, "son", "daughter")}, ${c.name}, is born") + (if (c.tr.isNotEmpty()) " (" + c.tr.joinToString(", ") { tname(it, c) } + ")" else "") + ".", "birth")
                if (chance(.025)) kill(wife, t("родильная горячка", "childbed fever"))
            }
        }
        for (p in g.people.values.toList()) {
            if (!alive(p)) continue
            val aw = p.away
            if (aw != null) {
                val ad = p.awayDie
                if (ad != null && g.year >= ad) { kill(p, pick(tl(listOf("пал" + sx(p, "", "а") + " на службе", "убит" + sx(p, "", "а") + " в сражении", "умер" + sx(p, "", "ла") + " в госпитале"), listOf("fell in service", "killed in battle", "died in a field hospital")))); continue }
                if (g.year >= aw) {
                    p.away = null; p.awayDie = null; addTr(p, "shram"); glory(2); deed(p, t(sx(p, "Вернулся", "Вернулась") + " со службы", "Came back from service"))
                    log(t("${p.name} ${sx(p, "вернулся", "вернулась")} со службы — ${sx(p, "седой", "седая")} и со шрамом.", "${p.name} came back from service, grey and scarred."), "big")
                }
                continue
            }
            if (chance(mortality(p))) kill(p, land.cause(this, p))
        }
        for (p in house()) {
            if (p.blood && !p.grown && age(p) >= 13) {
                val fa = person(p.father)
                val got = grow(p, if (fa != null && fa.blood) fa else person(p.mother))
                log(t("${p.name} ${sx(p, "вырос", "выросла")}: ", "${p.name} grew up: ") + got.joinToString(", ") + ".")
            }
            if (p.id != g.head && p.blood && p.away == null && p.spouse == null && age(p) in 18..30 && chance(.22)) {
                val kz = g.feud > 0 && chance(.12)
                val s = mkSpouse(p, if (kz) land.rival.g else null)
                if (kz) { feud(-2); log(t("${p.name} ${sx(p, "женился на", "вышла за")} ${s.name} из ${land.rival.g}. Вражда поутихла.", "${p.name} married ${s.name} of ${land.rival.g}. The feud cooled."), "big") }
                else log(t("${p.name} ${sx(p, "женился на", "вышла за")} ${s.name} из ${s.from}.", "${p.name} married ${s.name} of ${s.from}."))
            }
        }
        for (p in g.people.values) {
            val hid = p.hidden ?: continue
            if (alive(p) && g.year >= hid.at) {
                addTr(p, hid.t); p.hidden = null
                log(t("Оказалось, что ${p.name} — ${tname(hid.t, p)}. Об этом при сговоре промолчали.", "It turned out ${p.name} is ${tname(hid.t, p)}. Nobody mentioned it at the betrothal."), "big")
            }
        }
        val n = g.nakaz
        if (n != null && !n.done && nakazMet(n)) {
            n.done = true; glory(3); if (h.stones > 0) h.stones--
            log(t("Исполнен наказ предка (${n.by}): «${n.t}»", "An ancestor's wish is fulfilled (${n.by}): “${n.t}”"), "big")
        }
    }

    fun nakazMet(n: Nakaz) = when (n.k) {
        "money" -> g.money >= n.v
        "dvor" -> g.dvor >= n.v
        "gram" -> house().any { has(it, "gram") }
        "feud" -> g.feud == 0
        "glory" -> g.glory >= n.v
        else -> false
    }

    fun makeNakaz(dead: Person): Nakaz {
        val N = land.nakaz
        val l = mutableListOf(Triple("money", g.money + 20, N.money(g.money + 20)), Triple("glory", g.glory + 6, N.glory))
        if (g.dvor < 4) l.add(Triple("dvor", g.dvor + 1, N.dvor(land.dvor[g.dvor + 1])))
        if (house().none { has(it, "gram") }) l.add(Triple("gram", 0, N.gram))
        if (g.feud > 0) l.add(Triple("feud", 0, N.feud))
        val (k, v, t) = pick(l)
        return Nakaz(k, v, t, dead.name, g.year)
    }

    // ---- event helpers for content
    fun kidIn(lo: Int, hi: Int, f: (Person) -> Boolean = { true }) = house().filter { it.id != g.head && it.blood && age(it) in lo..hi && it.away == null && f(it) }
    fun sonOf(h: Person) = kidIn(17, 32) { it.male && it.spouse == null }
    fun killOne(pref: Boolean): Person? {
        val l = house().filter { it.id != g.head }.sortedBy { if (age(it) < 6 || age(it) > 60) 0 else 1 }
        return if (l.isEmpty()) null else if (pref && chance(.6)) l[0] else pick(l)
    }
    fun sendAway(x: Person, war: String) {
        x.away = g.year + ri(14, 22)
        if (chance(.5)) x.awayDie = g.year + ri(1, 10)
        deed(x, t(sx(x, "Ушёл", "Ушла") + " на службу", "Went off to serve") + if (war.isNotEmpty()) " — $war" else "")
        log(t("${x.name} ${sx(x, "ушёл", "ушла")} на службу.", "${x.name} went off to serve."), "big")
    }

    fun pickEvent(): Occ? {
        val hd = h
        for (e in land.forced) { val f = e.cond(this, hd) ?: continue; return Occ(e, hd, f.x, f.s) }
        for (e in land.history) {
            if (e.id in g.fired || g.year !in e.y0..e.y1) continue
            val f = e.cond(this, hd) ?: continue
            if (chance(e.p)) { g.fired.add(e.id); return Occ(e, hd, f.x, f.s) }
        }
        if (!chance(.42)) return null
        val pool = land.events.filter { it.id !in g.recent }.mapNotNull { e -> e.cond(this, hd)?.let { e to it } }
        if (pool.isEmpty()) return null
        var r = rnd.nextDouble() * pool.sumOf { it.first.w }
        for ((e, f) in pool) {
            r -= e.w
            if (r <= 0) { g.recent.add(e.id); if (g.recent.size > 7) g.recent.removeAt(0); return Occ(e, hd, f.x, f.s) }
        }
        return null
    }

    /** Years pass until something happens (at most twenty). */
    fun live(): Live {
        val y0 = g.year
        val l0 = g.log.size
        fun since() = Since(y0, g.year, g.log.drop(l0).toList())
        repeat(20) {
            tick()
            val hd = h
            if (!alive(hd) || hd.monk) return Live.HeadGone(since())
            pickEvent()?.let { return Live.Happens(it, since()) }
        }
        return Live.Quiet(since())
    }

    // ---- choosing
    fun sinTrait(o: Opt, h: Person) = o.sin?.let { Traits.SIN[it] }?.takeIf { has(h, it) }

    fun rollInfo(o: Opt, occ: Occ): RollInfo {
        val r = o.roll!!
        val who = if (r.ofX) occ.xx else occ.h
        val parts = mutableListOf(STAT_NAME.getValue(r.st) + (if (r.ofX) " (${occ.xx.name})" else "") to eff(who, r.st), t("Божья милость", "Grace of God") to 2)
        if (r.d != 0) parts.add((if (r.d > 0) t("Дело нетрудное", "An easy matter") else t("Дело трудное", "A hard matter")) to r.d)
        for ((t, v) in r.tb) if (v != 0 && has(occ.h, t)) parts.add(cap(tname(t, occ.h)) to v)
        for (rl in g.relics) rl.b[r.st]?.let { parts.add(rl.n to it) }
        if ("luck" in g.perks) parts.add(PERKS.getValue("luck").n to 1)
        if (g.bless > 0) parts.add(t("Нитка странницы", "The wanderer's thread") to 1)
        val white = parts.sumOf { it.second }.coerceIn(1, 11)
        return RollInfo(parts, white, 12 - white, curseBeans())
    }

    fun beans(info: RollInfo): List<Char> = List(info.white) { 'w' } + List(info.black) { 'b' } + List(info.red) { 'r' }

    private class Snap(val money: Int, val glory: Int, val feud: Int, val dvor: Int, val head: Int, val stones: Int, val tr: List<String>, val alive: Set<Int>, val away: Set<Int>,
                       val nid: Int, val relics: Int, val secrets: Int, val hex: Int, val bless: Int)

    private fun snap() = Snap(g.money, g.glory, g.feud, g.dvor, h.id, h.stones, h.tr.toList(), g.people.values.filter { alive(it) }.map { it.id }.toSet(),
        g.people.values.filter { it.away != null }.map { it.id }.toSet(), g.nid, g.relics.size, g.secrets.size, g.hex, g.bless)

    private fun diffs(a: Snap): List<Diff> {
        val out = mutableListOf<Diff>()
        fun num(v: Int, l: String, up: Boolean) { if (v != 0) out.add(Diff("$l ${if (v > 0) "+" else "−"}${abs(v)}", (v > 0) == up)) }
        num(g.money - a.money, land.moneyLabel, true); num(g.glory - a.glory, t("Слава", "Glory"), true); num(g.feud - a.feud, t("Вражда", "Feud"), false); num(g.dvor - a.dvor, t("Двор", "Home"), true)
        g.people[a.head]?.let { p ->
            val st = p.stones - a.stones
            if (st != 0) out.add(Diff(t("Камни на душе", "Stones on the soul") + " ${if (st > 0) "+" else "−"}${abs(st)}", st < 0))
            for (tr in p.tr) if (tr !in a.tr) out.add(Diff(t("Черта: ", "Trait: ") + tname(tr, p), tr !in Traits.BAD))
            if (p.monk) out.add(Diff(p.name + t(" в монастыре", " in a monastery"), false))
        }
        for (id in a.alive) { val p = g.people[id]; if (p != null && !alive(p)) out.add(Diff(t(if (p.male) "Умер: " else "Умерла: ", "Died: ") + p.name, false)) }
        for (p in g.people.values) if (p.away != null && p.id !in a.away) out.add(Diff(p.name + t(" уходит из дома", " leaves home"), false))
        for (id in a.nid until g.nid) g.people[id]?.let { out.add(Diff((if (it.blood) t("В роду: ", "Born: ") else t("В семье: ", "Joins: ")) + it.name, true)) }
        if (g.relics.size > a.relics) out.add(Diff(t("Реликвия: ", "Relic: ") + g.relics.last().n, true))
        if (g.secrets.size > a.secrets) out.add(Diff(t("Тайна в сундуке", "A secret in the chest"), false))
        if (g.secrets.size < a.secrets) out.add(Diff(t("Тайна закрыта", "A secret laid to rest"), true))
        if (g.hex > a.hex && g.hex > g.year) out.add(Diff(t("Второй красный боб до ", "A second red bean until ") + g.hex, false))
        if (g.bless > a.bless) out.add(Diff(t("Лишний белый боб", "An extra white bean"), true))
        return out
    }

    private fun finish(occ: Occ, o: Opt, a: Snap, text: String, good: Boolean?, red: Boolean): Outcome {
        var stone: String? = null
        sinTrait(o, occ.h)?.let { tr -> occ.h.stones++; stone = t("Поступок не по нраву (${tname(tr, occ.h)}). Камней на душе: ${occ.h.stones} из 5.", "Against the grain (${tname(tr, occ.h)}). Stones on the soul: ${occ.h.stones} of 5.") }
        log("${occ.ev.t}: ${o.t.replaceFirstChar { it.lowercase() }}${if (good == false) t(" — неудача", " — it went badly") else if (good == true) t(" — удача", " — it went well") else ""}.")
        return Outcome(text, good, red, stone, diffs(a))
    }

    /** A choice without a draw. */
    fun choose(occ: Occ, o: Opt): Outcome {
        val a = snap()
        val text = o.fx!!.invoke(this, occ)
        return finish(occ, o, a, text, null, false)
    }

    /** Draws a bean for [o]; returns the bean drawn ('w','b','r'), its index in [beans], and the outcome. */
    fun draw(occ: Occ, o: Opt, beans: List<Char>): Triple<Char, Int, Outcome> {
        val a = snap()
        val idx = rnd.nextInt(beans.size)
        val res = beans[idx]
        g.bless = 0
        val good = res == 'w'
        var text = if (good) o.ok!!.invoke(this, occ) else o.bad!!.invoke(this, occ)
        if (res == 'r') text = t("Красный боб — проклятие помнит вас. ", "A red bean: the curse remembers you. ") + text
        if (good) for (r in g.relics) if (r.b.containsKey(o.roll!!.st)) r.lines.add(Note(g.year, "${occ.h.name}: ${occ.ev.t.lowercase()}"))
        return Triple(res, idx, finish(occ, o, a, text, good, res == 'r'))
    }

    // ---- succession
    /** Starts the succession after the head died or left; null means the line has ended (the game is over). */
    fun succession(): Succession? {
        val d = h
        if (d.monk && d.epi.isEmpty()) d.epi = makeEpi(d)
        val cands = desc(d).filter { alive(it) && !it.monk }
        g.nakaz = makeNakaz(d)
        if (cands.isEmpty()) { gameOver(d); return null }
        val eldest = kidsOf(d).firstOrNull { alive(it) && it.male && !it.bast && age(it) >= 16 && !it.monk }
        val ord = cands.sortedWith(compareBy<Person>({ if (it === eldest) 0 else 1 }, { it.gen }, { it.born }))
        val heirs = ord.map { p ->
            val w = mutableListOf<String>()
            if (eldest != null && p !== eldest && age(eldest) >= 16) w.add(t("${eldest.name} затаит обиду", "${eldest.name} will bear a grudge"))
            if (p.bast) w.add(t("незаконн${sx(p, "ый", "ая")}: Слава −2", "born out of wedlock: Glory −2"))
            if (age(p) < 14) w.add(t("дитя — двор без хозяина", "a child: the house without a master"))
            if (p.away != null) w.add(t("вернётся со службы", "will come back from service"))
            Heir(p, p === eldest, w.joinToString(" · ").ifEmpty { null })
        }
        return Succession(d, heirs, eldest)
    }

    /** Hands the family to [p]. Returns true if this lifted the curse. */
    fun takeHeir(p: Person, eldest: Person?): Boolean {
        if (eldest != null && p !== eldest && age(eldest) >= 16) {
            g.grudge = eldest.id
            log(t("${eldest.name} обойден${sx(eldest, "", "а")} наследством и затаил${sx(eldest, "", "а")} обиду.", "${eldest.name} was passed over and bears a grudge."), "big")
        }
        if (p.bast) glory(-2)
        if (p.away != null) { p.away = null; p.awayDie = null }
        g.head = p.id; g.heads.add(p.id)
        if (p.stones > 0) p.stones--
        log(t("Главой рода стал${sx(p, "", "а")} ${fullName(p)} (колено ${roman(p.gen)}).", "${fullName(p)} became head of the family (generation ${roman(p.gen)})."), "big")
        if (p.gen >= 7 && !g.won) {
            g.won = true; glory(10)
            log(t("Красная нить порвалась. Проклятие снято.", "The red thread has snapped. The curse is lifted."), "big")
            return true
        }
        return false
    }

    fun gameOver(d: Person) {
        g.over = true
        val born = g.people.values.count { it.blood }
        val span = g.year - g.people.getValue(g.founder).born
        g.overTxt = t("${fullName(d)} ${sx(d, "умер последним", "умерла последней")} в роду. Проклятие исполнилось на ${roman(d.gen)} колене. " +
            "Род прожил ${yrs(span)}, в нём ${plural(born, "родилась", "родились", "родилось")} $born ${plural(born, "душа", "души", "душ")}.",
            "${fullName(d)} died the last of the line. The curse came true in generation ${roman(d.gen)}. The family lasted ${yrs(span)}; $born ${if (born == 1) "soul was" else "souls were"} born into it.")
        log(t("Род пресёкся на ${roman(d.gen)} колене.", "The line ended in generation ${roman(d.gen)}."), "big")
    }

    // ---- home actions
    fun buildDvor(): Boolean {
        if (g.dvor >= 4 || g.over) return false
        val cost = DVOR_COST[g.dvor + 1]
        if (g.money < cost) return false
        money(-cost); g.dvor++
        log(t("Поставили новый двор: ", "Built a new home: ") + land.dvor[g.dvor] + ".", "big")
        return true
    }

    fun buyPerk(k: String): Boolean {
        val p = PERKS[k] ?: return false
        if (k in g.perks || g.glory < p.cost || g.over) return false
        g.glory -= p.cost; g.perks.add(k)
        log(t("Наследие рода: «${p.n}».", "Family legacy: “${p.n}”."), "big")
        return true
    }

    companion object {
        fun newGame(landId: String, rnd: Random = Random.Default): Engine {
            val land = Lands.of(landId)
            val g = Game(land.id, year = land.years.first + rnd.nextInt(land.years.last - land.years.first + 1), stem = land.stems.random(rnd), place = land.places.random(rnd))
            val e = Engine(g, rnd)
            val male = e.chance(.7)
            val a = e.ri(17, 20)
            val f = e.reg(e.newP(e.pickName(male), male, born = g.year - a, blood = true, gen = 1, st = e.rstats(2, 7)))
            f.edu = land.edu0
            e.grow(f, null)
            if (e.chance(.4)) e.addTr(f, e.pick(listOf("bog", "kras", "umn", "dolg", "hvor")))
            g.founder = f.id; g.head = f.id; g.heads.add(f.id); g.foundName = f.name
            e.log(land.curseLog(e, f), "big")
            return e
        }
    }
}

fun plural(n: Int, a: String, b: String, c: String): String {
    val m100 = abs(n) % 100
    val m = m100 % 10
    return when {
        m100 in 11..19 -> c
        m in 2..4 -> b
        m == 1 -> a
        else -> c
    }
}
