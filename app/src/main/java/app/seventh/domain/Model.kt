package app.seventh.domain

import kotlinx.serialization.Serializable

/** Six stats; every roll leans on one of them. */
enum class Stat { SIL, UM, SER, HIT, VER, ZDR }

@Serializable
class Look(val skin: Int, val hair: Int, val cloth: Int, val kerch: Int)

@Serializable
class Note(val y: Int, val t: String)

/** A trait the matchmaker kept quiet about, surfacing in year [at]. */
@Serializable
class Hidden(val t: String, val at: Int)

@Serializable
class Person(
    var id: Int,
    val name: String,
    val male: Boolean,
    val born: Int,
    var died: Int? = null,
    var cause: String = "",
    val father: Int? = null,
    val mother: Int? = null,
    var spouse: Int? = null,
    val blood: Boolean = false,
    val gen: Int = 0,
    val st: IntArray = IntArray(6) { 4 },
    val tr: MutableList<String> = mutableListOf(),
    val look: Look,
    var stones: Int = 0,
    var edu: String? = null,
    val deeds: MutableList<Note> = mutableListOf(),
    var grown: Boolean = false,
    var hidden: Hidden? = null,
    var bast: Boolean = false,
    var monk: Boolean = false,
    var away: Int? = null,
    var awayDie: Int? = null,
    var last: Int = -9,
    var wed: Int? = null,
    var from: String = "",
    var epi: String = "",
    var squire: Boolean = false,
    /** Left to be adopted into another house: gone from the line like a monk, but not in a monastery. */
    var adopted: Boolean = false,
    /** Dowry (+) or bride price (−) of a match candidate. */
    var dw: Int = 0,
) {
    operator fun get(s: Stat) = st[s.ordinal]
    operator fun set(s: Stat, v: Int) { st[s.ordinal] = v }
}

@Serializable
class Relic(val n: String, val ic: String, val b: Map<Stat, Int>, val lines: MutableList<Note>)

@Serializable
class Secret(val t: String, val who: String, val y: Int)

@Serializable
class LogLine(val y: Int, val t: String, val k: String = "")

/** The dead head's last wish; kinds: money, dvor, gram, feud, glory. */
@Serializable
class Nakaz(val k: String, val v: Int = 0, val t: String, val by: String, val y: Int, var done: Boolean = false, var nag: Int? = null)

@Serializable
class Game(
    val land: String,
    var year: Int,
    var stem: String,
    var place: String,
    var nid: Int = 1,
    val people: MutableMap<Int, Person> = mutableMapOf(),
    var money: Int = 6,
    var glory: Int = 0,
    var feud: Int = 1,
    var dvor: Int = 0,
    val perks: MutableSet<String> = mutableSetOf(),
    val relics: MutableList<Relic> = mutableListOf(),
    val secrets: MutableList<Secret> = mutableListOf(),
    val log: MutableList<LogLine> = mutableListOf(),
    val recent: MutableList<String> = mutableListOf(),
    val fired: MutableSet<String> = mutableSetOf(),
    val heads: MutableList<Int> = mutableListOf(),
    var nakaz: Nakaz? = null,
    var grudge: Int? = null,
    var bless: Int = 0,
    var hex: Int = 0,
    var won: Boolean = false,
    var over: Boolean = false,
    var noMatch: Int = 0,
    var founder: Int = 0,
    var head: Int = 0,
    var foundName: String = "",
    var overTxt: String = "",
    /** Marks that events leave for later ones to read: a key and, usually, the year it was set. Chains and the family's path live here. */
    val flags: MutableMap<String, Int> = mutableMapOf(),
)
