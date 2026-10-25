package app.seventh.domain

/** The language the game speaks; set from the app locale before anything is drawn. Text is chosen when it is needed. */
object Lang {
    @Volatile var ru: Boolean = true
}

/** Russian or English text, picked by [Lang]. */
fun t(ru: String, en: String) = if (Lang.ru) ru else en

/** Anything in two languages, picked by [Lang]. */
fun <T> tl(ru: T, en: T): T = if (Lang.ru) ru else en

/** Text kept in both languages and shown in the current one: event titles and other strings fixed at start-up. */
class Tx(val ru: String, val en: String) {
    override fun toString() = if (Lang.ru) ru else en
}

/** An event wants to happen: about nobody in particular, about a person [x], or about an old secret [s]. */
class Fire(val x: Person? = null, val s: Secret? = null)

/** One happening of an event: who heads the family, who it is about, and the match candidates once drawn up. */
class Occ(val ev: Event, val h: Person, val x: Person?, val s: Secret?) {
    val cands = mutableListOf<Person>()
    val xx: Person get() = x!!
}

typealias Txt = Engine.(Occ) -> String

/** A draw from the bag on [st] (of the head, or of [ofX] the person the event is about); [d] makes it easier or harder, [tb] are trait bonuses. */
class Roll(val st: Stat, val d: Int = 0, val tb: Map<String, Int> = emptyMap(), val ofX: Boolean = false)

/**
 * One choice. Without a roll, [fx] runs; with a roll, [ok] or [bad]. [sin] (cruel, lie, coward, greed) lays a stone on a soul of the
 * opposite temper. In [tag], text inside `[[…]]` is a warning.
 */
class Opt(
    val t: String,
    val tag: String? = null,
    val cost: Int = 0,
    val sin: String? = null,
    val roll: Roll? = null,
    val cand: Person? = null,
    val fx: Txt? = null,
    val ok: Txt? = null,
    val bad: Txt? = null,
)

class Event(
    val id: String,
    val sc: String,
    val tt: Tx,
    val w: Int = 1,
    /** History: fires once, in years [y0]..[y1], with probability [p]. */
    val y0: Int = 0,
    val y1: Int = 0,
    val p: Double = 1.0,
    val cond: Engine.(Person) -> Fire?,
    val txt: Txt,
    val opts: Engine.(Occ) -> List<Opt>,
) {
    val t: String get() = tt.toString()

    constructor(id: String, sc: String, t: String, w: Int = 1, y0: Int = 0, y1: Int = 0, p: Double = 1.0, cond: Engine.(Person) -> Fire?, txt: Txt, opts: Engine.(Occ) -> List<Opt>) :
        this(id, sc, Tx(t, t), w, y0, y1, p, cond, txt, opts)
}

/** The rival family in the cases the text needs: genitive («из …»), instrumental («с …»), nominative. */
class Rival(val g: String, val i: String, val n: String)

class NakazText(val money: (Int) -> String, val glory: String, val dvor: (String) -> String, val gram: String, val feud: String)

/** Everything that makes a land itself: names, places, the rival, money, history, its events and how its curse began. */
abstract class Land(val id: String) {
    abstract val maleNames: List<String>
    abstract val femaleNames: List<String>
    abstract val stems: List<String>
    abstract val places: List<String>
    /** Families spouses come from, already in the genitive. */
    abstract val families: List<String>
    abstract val dvor: List<String>
    abstract val years: IntRange
    abstract val edu0: String
    abstract val eduBias: Map<String, List<String>>
    open val traitNames: Map<String, Pair<String, String>> = emptyMap()
    abstract val rival: Rival
    abstract val cur: String
    abstract val moneyLabel: String
    abstract val introScene: String
    abstract val nakaz: NakazText
    abstract val helpFeud: String
    abstract val helpHeart: String
    abstract val forced: List<Event>
    abstract val events: List<Event>
    abstract val history: List<Event>

    abstract fun family(g: Game): String
    abstract fun surname(g: Game, p: Person): String
    abstract fun curseLog(e: Engine, f: Person): String
    abstract fun cause(e: Engine, p: Person): String
    open fun mort(year: Int): Double = 1.0
    open fun income(year: Int): Int = 0
    /** Paragraphs of the opening; `**…**` is bold. */
    abstract fun intro(e: Engine, f: Person): List<String>
}

object Lands {
    val all: List<Land> by lazy { listOf(Rus, Europe, Japan) }
    fun of(id: String): Land = all.firstOrNull { it.id == id } ?: Rus
}
