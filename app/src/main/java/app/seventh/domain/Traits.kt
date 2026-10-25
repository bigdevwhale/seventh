package app.seventh.domain

/** nrav = temper (comes in opposed pairs), krov = blood (inherited), nazh = earned in life. */
enum class TrKind { NRAV, KROV, NAZH }

class Trait(private val ru: Pair<String, String>, private val en: Pair<String, String>, val kind: TrKind, val opp: String? = null, val mods: Map<Stat, Int> = emptyMap(), val inh: Double = 0.0, private val dRu: String, private val dEn: String) {
    val m: String get() = tl(ru, en).first
    val f: String get() = tl(ru, en).second
    val desc: String get() = t(dRu, dEn)
}

object Traits {
    private val N = TrKind.NRAV
    private val K = TrKind.KROV
    private val Z = TrKind.NAZH
    private fun tr(ru: Pair<String, String>, en: Pair<String, String>, k: TrKind, o: String?, mods: Map<Stat, Int>, inh: Double, d: Pair<String, String>) = Trait(ru, en, k, o, mods, inh, d.first, d.second)

    val all: Map<String, Trait> = linkedMapOf(
        "dobr" to tr("добрый" to "добрая", "kind" to "kind", N, "zhest", mapOf(Stat.SER to 1), 0.0, "Жестокий выбор ложится камнем на душу." to "A cruel choice lays a stone on the soul."),
        "zhest" to tr("жестокий" to "жестокая", "cruel" to "cruel", N, "dobr", mapOf(Stat.HIT to 1, Stat.SER to -1), 0.0, "Боятся, но не любят." to "Feared, not loved."),
        "shch" to tr("щедрый" to "щедрая", "generous" to "generous", N, "zhad", mapOf(Stat.SER to 1), 0.0, "Скупость ложится камнем на душу." to "Stinginess lays a stone on the soul."),
        "zhad" to tr("жадный" to "жадная", "greedy" to "greedy", N, "shch", emptyMap(), 0.0, "+1 к деньгам в год. Копейка рубль бережёт." to "+1 money a year. A penny saved."),
        "hrab" to tr("храбрый" to "храбрая", "brave" to "brave", N, "trus", mapOf(Stat.SIL to 1), 0.0, "Отступить — камень на душу." to "Backing down lays a stone on the soul."),
        "trus" to tr("трус" to "трусиха", "coward" to "coward", N, "hrab", mapOf(Stat.SIL to -1, Stat.HIT to 1), 0.0, "Бережёт шкуру, хитрит." to "Saves the skin, plays it sly."),
        "chest" to tr("честный" to "честная", "honest" to "honest", N, "lzh", mapOf(Stat.VER to 1), 0.0, "Ложь ложится камнем на душу." to "A lie lays a stone on the soul."),
        "lzh" to tr("лживый" to "лживая", "lying" to "lying", N, "chest", mapOf(Stat.HIT to 2, Stat.VER to -1), 0.0, "Врёт легко и складно." to "Lies easily and well."),
        "krot" to tr("кроткий" to "кроткая", "meek" to "meek", N, "gnev", mapOf(Stat.VER to 1), 0.0, "Тих и терпелив." to "Quiet and patient."),
        "gnev" to tr("гневный" to "гневная", "hot-tempered" to "hot-tempered", N, "krot", mapOf(Stat.SIL to 1, Stat.SER to -1), 0.0, "Вспыхивает как солома." to "Flares up like straw."),
        "bog" to tr("богатырь" to "богатырша", "giant" to "giantess", K, "hil", mapOf(Stat.SIL to 3, Stat.ZDR to 1), .35, "Кровь. Сила +3, Здоровье +1." to "Blood. Strength +3, Health +1."),
        "hil" to tr("хилый" to "хилая", "frail" to "frail", K, "bog", mapOf(Stat.SIL to -2, Stat.ZDR to -1), .3, "Кровь. Сила −2, Здоровье −1." to "Blood. Strength −2, Health −1."),
        "kras" to tr("красавец" to "красавица", "handsome" to "beautiful", K, null, mapOf(Stat.SER to 2), .35, "Кровь. Сердце +2: сваты не переводятся." to "Blood. Heart +2: suitors never stop calling."),
        "umn" to tr("светлая голова" to "светлая голова", "quick-witted" to "quick-witted", K, null, mapOf(Stat.UM to 3), .3, "Кровь. Ум +3." to "Blood. Wits +3."),
        "hvor" to tr("хворый" to "хворая", "sickly" to "sickly", K, null, mapOf(Stat.ZDR to -3), .3, "Кровь. Здоровье −3, умирает раньше." to "Blood. Health −3, dies sooner."),
        "dolg" to tr("долгожитель" to "долгожительница", "long-lived" to "long-lived", K, null, emptyMap(), .4, "Кровь. Смерть обходит стороной." to "Blood. Death passes by."),
        "bespl" to tr("бесплодный" to "бесплодная", "barren" to "barren", K, null, emptyMap(), .12, "Кровь. Детей не будет." to "Blood. There will be no children."),
        "gram" to tr("грамотный" to "грамотная", "lettered" to "lettered", Z, null, mapOf(Stat.UM to 2), 0.0, "Читает и пишет. Ум +2." to "Reads and writes. Wits +2."),
        "pyan" to tr("пьяница" to "пьяница", "drunkard" to "drunkard", Z, null, mapOf(Stat.ZDR to -2, Stat.UM to -1), 0.0, "Здоровье −2, Ум −1, пропивает деньги." to "Health −2, Wits −1, drinks the money away."),
        "kalek" to tr("калека" to "калека", "crippled" to "crippled", Z, null, mapOf(Stat.SIL to -3), 0.0, "Сила −3." to "Strength −3."),
        "shram" to tr("со шрамом" to "со шрамом", "scarred" to "scarred", Z, null, mapOf(Stat.SIL to 1, Stat.SER to -1), 0.0, "Сила +1, Сердце −1." to "Strength +1, Heart −1."),
        "vdov" to tr("вдовец" to "вдова", "widower" to "widow", Z, null, emptyMap(), 0.0, "Похоронил супруга." to "Buried a spouse."),
        "bogom" to tr("богомолец" to "богомолица", "pilgrim" to "pilgrim", Z, null, mapOf(Stat.VER to 2), 0.0, "Ходил по святым местам. Вера +2." to "Walked to holy places. Faith +2."),
        "gor" to tr("городской" to "городская", "townsman" to "townswoman", Z, null, mapOf(Stat.HIT to 1, Stat.UM to 1), 0.0, "Пожил в городе." to "Has lived in town."),
    )
    val KROV = listOf("bog", "hil", "kras", "umn", "hvor", "dolg", "bespl")
    val PAIRS = listOf("dobr" to "zhest", "shch" to "zhad", "hrab" to "trus", "chest" to "lzh", "krot" to "gnev")
    /** Acting against these tempers lays a stone on the soul. */
    val SIN = mapOf("cruel" to "dobr", "lie" to "chest", "coward" to "hrab", "greed" to "shch")
    val BAD = setOf("zhest", "zhad", "trus", "lzh", "gnev", "hil", "hvor", "bespl", "pyan", "kalek", "vdov")
}

class Perk(private val nRu: String, private val nEn: String, val cost: Int, private val dRu: String, private val dEn: String) {
    val n: String get() = t(nRu, nEn)
    val d: String get() = t(dRu, dEn)
}

val PERKS = linkedMapOf(
    "krep" to Perk("Крепкая кровь", "Strong blood", 8, "Дети умирают на треть реже.", "A third fewer children die."),
    "yaz" to Perk("Сватов не гонят", "Suitors welcome", 6, "Сердце +1 всем новорождённым, в сваты приводят лучших.", "Heart +1 for every newborn; the best matches come calling."),
    "plod" to Perk("Плодовитые", "Fruitful", 10, "Детей рождается больше.", "More children are born."),
    "dolg" to Perk("Долгожители", "Long-lived", 12, "Взрослые в роду живут дольше.", "Grown-ups of the blood live longer."),
    "luck" to Perk("Счастливая рука", "A lucky hand", 15, "+1 белый боб в каждом жребии.", "+1 white bean in every draw."),
)

private val STAT_RU = mapOf(Stat.SIL to "Сила", Stat.UM to "Ум", Stat.SER to "Сердце", Stat.HIT to "Хитрость", Stat.VER to "Вера", Stat.ZDR to "Здоровье")
private val STAT_EN = mapOf(Stat.SIL to "Strength", Stat.UM to "Wits", Stat.SER to "Heart", Stat.HIT to "Cunning", Stat.VER to "Faith", Stat.ZDR to "Health")
val STAT_NAME: Map<Stat, String> get() = tl(STAT_RU, STAT_EN)
val DVOR_COST = intArrayOf(0, 15, 30, 55, 90)
val ROMAN = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX")
fun roman(n: Int) = ROMAN.getOrElse(n) { n.toString() }
