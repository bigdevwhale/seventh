package app.seventh.domain

/** nrav = temper (comes in opposed pairs), krov = blood (inherited), nazh = earned in life. */
enum class TrKind { NRAV, KROV, NAZH }

class Trait(val m: String, val f: String, val kind: TrKind, val opp: String? = null, val mods: Map<Stat, Int> = emptyMap(), val inh: Double = 0.0, val desc: String)

object Traits {
    private fun t(m: String, f: String, k: TrKind, o: String? = null, mods: Map<Stat, Int> = emptyMap(), inh: Double = 0.0, d: String) = Trait(m, f, k, o, mods, inh, d)
    private val N = TrKind.NRAV
    private val K = TrKind.KROV
    private val Z = TrKind.NAZH

    val all: Map<String, Trait> = linkedMapOf(
        "dobr" to t("добрый", "добрая", N, "zhest", mapOf(Stat.SER to 1), d = "Жестокий выбор ложится камнем на душу."),
        "zhest" to t("жестокий", "жестокая", N, "dobr", mapOf(Stat.HIT to 1, Stat.SER to -1), d = "Боятся, но не любят."),
        "shch" to t("щедрый", "щедрая", N, "zhad", mapOf(Stat.SER to 1), d = "Скупость ложится камнем на душу."),
        "zhad" to t("жадный", "жадная", N, "shch", d = "+1 к деньгам в год. Копейка рубль бережёт."),
        "hrab" to t("храбрый", "храбрая", N, "trus", mapOf(Stat.SIL to 1), d = "Отступить — камень на душу."),
        "trus" to t("трус", "трусиха", N, "hrab", mapOf(Stat.SIL to -1, Stat.HIT to 1), d = "Бережёт шкуру, хитрит."),
        "chest" to t("честный", "честная", N, "lzh", mapOf(Stat.VER to 1), d = "Ложь ложится камнем на душу."),
        "lzh" to t("лживый", "лживая", N, "chest", mapOf(Stat.HIT to 2, Stat.VER to -1), d = "Врёт легко и складно."),
        "krot" to t("кроткий", "кроткая", N, "gnev", mapOf(Stat.VER to 1), d = "Тих и терпелив."),
        "gnev" to t("гневный", "гневная", N, "krot", mapOf(Stat.SIL to 1, Stat.SER to -1), d = "Вспыхивает как солома."),
        "bog" to t("богатырь", "богатырша", K, "hil", mapOf(Stat.SIL to 3, Stat.ZDR to 1), .35, "Кровь. Сила +3, Здоровье +1."),
        "hil" to t("хилый", "хилая", K, "bog", mapOf(Stat.SIL to -2, Stat.ZDR to -1), .3, "Кровь. Сила −2, Здоровье −1."),
        "kras" to t("красавец", "красавица", K, null, mapOf(Stat.SER to 2), .35, "Кровь. Сердце +2: сваты не переводятся."),
        "umn" to t("светлая голова", "светлая голова", K, null, mapOf(Stat.UM to 3), .3, "Кровь. Ум +3."),
        "hvor" to t("хворый", "хворая", K, null, mapOf(Stat.ZDR to -3), .3, "Кровь. Здоровье −3, умирает раньше."),
        "dolg" to t("долгожитель", "долгожительница", K, null, emptyMap(), .4, "Кровь. Смерть обходит стороной."),
        "bespl" to t("бесплодный", "бесплодная", K, null, emptyMap(), .12, "Кровь. Детей не будет."),
        "gram" to t("грамотный", "грамотная", Z, null, mapOf(Stat.UM to 2), d = "Читает и пишет. Ум +2."),
        "pyan" to t("пьяница", "пьяница", Z, null, mapOf(Stat.ZDR to -2, Stat.UM to -1), d = "Здоровье −2, Ум −1, пропивает деньги."),
        "kalek" to t("калека", "калека", Z, null, mapOf(Stat.SIL to -3), d = "Сила −3."),
        "shram" to t("со шрамом", "со шрамом", Z, null, mapOf(Stat.SIL to 1, Stat.SER to -1), d = "Сила +1, Сердце −1."),
        "vdov" to t("вдовец", "вдова", Z, null, d = "Похоронил супруга."),
        "bogom" to t("богомолец", "богомолица", Z, null, mapOf(Stat.VER to 2), d = "Ходил по святым местам. Вера +2."),
        "gor" to t("городской", "городская", Z, null, mapOf(Stat.HIT to 1, Stat.UM to 1), d = "Пожил в городе."),
    )
    val KROV = listOf("bog", "hil", "kras", "umn", "hvor", "dolg", "bespl")
    val PAIRS = listOf("dobr" to "zhest", "shch" to "zhad", "hrab" to "trus", "chest" to "lzh", "krot" to "gnev")
    /** Acting against these tempers lays a stone on the soul. */
    val SIN = mapOf("cruel" to "dobr", "lie" to "chest", "coward" to "hrab", "greed" to "shch")
    val BAD = setOf("zhest", "zhad", "trus", "lzh", "gnev", "hil", "hvor", "bespl", "pyan", "kalek", "vdov")
}

class Perk(val n: String, val cost: Int, val d: String)

val PERKS = linkedMapOf(
    "krep" to Perk("Крепкая кровь", 8, "Дети умирают на треть реже."),
    "yaz" to Perk("Сватов не гонят", 6, "Сердце +1 всем новорождённым, в сваты приводят лучших."),
    "plod" to Perk("Плодовитые", 10, "Детей рождается больше."),
    "dolg" to Perk("Долгожители", 12, "Взрослые в роду живут дольше."),
    "luck" to Perk("Счастливая рука", 15, "+1 белый боб в каждом жребии."),
)

val STAT_NAME = mapOf(Stat.SIL to "Сила", Stat.UM to "Ум", Stat.SER to "Сердце", Stat.HIT to "Хитрость", Stat.VER to "Вера", Stat.ZDR to "Здоровье")
val DVOR_COST = intArrayOf(0, 15, 30, 55, 90)
val ROMAN = listOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX")
fun roman(n: Int) = ROMAN.getOrElse(n) { n.toString() }
