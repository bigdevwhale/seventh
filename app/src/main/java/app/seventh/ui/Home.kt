package app.seventh.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.seventh.R
import app.seventh.domain.DVOR_COST
import app.seventh.domain.Engine
import app.seventh.domain.LogLine
import app.seventh.domain.PERKS
import app.seventh.domain.Person
import app.seventh.domain.Stat
import app.seventh.domain.roman
import app.seventh.ui.art.HomeData
import app.seventh.ui.theme.LocalPal

/** Explanations behind the «?» marks; feud and heart depend on the land. */
object Help {
    const val STONES = "Камни на душе ложатся, когда человек поступает против своего нрава: добрый — жестоко, честный — лжёт, храбрый — бежит. На пятом камне случается срыв."
    const val BAG = "Исход каждого дела решает боб из мешка: белый — удача, чёрный — беда, красный — проклятие. Белых тем больше, чем выше нужный показатель и полезные черты."
    const val SECRETS = "Тайны рода рано или поздно всплывают — бывает, что и через поколение."
    const val HEIRS = "Наследник — любой живой потомок главы рода. Когда глава умрёт, вы выберете, за кого играть дальше. Нет потомков — род пресёкся."
    const val GLORY = "Слава копится за подвиги, честность и исполненные наказы. Её тратят на свойства, которые останутся у рода навсегда."
    fun stat(e: Engine, s: Stat) = when (s) {
        Stat.SIL -> "Сила — звери, драки, пожар, тяжёлый труд."
        Stat.UM -> "Ум — суд, хитрые затеи, учёба, лекари."
        Stat.SER -> e.land.helpHeart
        Stat.HIT -> "Хитрость — обман, торг, спрятать и спрятаться."
        Stat.VER -> "Вера — молитва, искушения, мор, покаяние."
        Stat.ZDR -> "Здоровье — болезни, долгая жизнь, дальняя дорога."
    }
}

fun Engine.statusOf(p: Person): String = when {
    p.monk -> "в монастыре"
    p.died != null -> "${p.born}–${p.died}, ${p.cause}"
    p.away != null -> "в отъезде"
    else -> yrs(age(p))
}

fun Engine.heirsOf(h: Person) = desc(h).filter { alive(it) && !it.monk }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(st: GameState) {
    val P = LocalPal.current
    val e = st.e
    val g = e.g
    val h = e.h
    val s = e.spouseOf(h)
    val heirs = e.heirsOf(h)
    val fam = (listOfNotNull(s?.takeIf { e.alive(it) }) + heirs).take(14)
    val nk = g.nakaz?.takeIf { !it.done }
    val info = { t: String -> st.toast = t }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val season = if (g.over) 3 else g.year % 4
        HomeView(g.land, HomeData(g.dvor, season, g.over, e.house().filter { it.away == null }.take(7).map { e.fig(it) }, g.stem.length, g.year), running = st.sheet == Sheet.None)
        if (g.over) PaperCard(tint = P.warn) { H3(stringResource(R.string.line_ended), color = P.dred); Gap(4.dp); Para(g.overTxt) }
        else if (heirs.isEmpty()) PaperCard(Modifier.clickable { info(Help.HEIRS) }, tint = P.warn) {
            Row(verticalAlignment = Alignment.CenterVertically) { H3(stringResource(R.string.no_heir), color = P.dred); Q() }
            Gap(4.dp)
            Para("Если ${e.sx(h, "глава рода умрёт", "хозяйка рода умрёт")} сейчас, род пресечётся. " + when {
                e.married(h) -> "Дети ещё будут — берегите семью."
                e.age(h) < 16 -> "Ребёнку нужно вырасти и обзавестись семьёй."
                else -> "Пора " + e.sx(h, "жениться", "замуж") + " — " + (if (g.land == "eu") "тётушка Аэлис придёт сама." else "сваха придёт сама.")
            })
        }
        PaperCard {
            CardHead(if (g.over) stringResource(R.string.last_of_line) else stringResource(R.string.head_of_family), stringResource(R.string.gen_n, roman(h.gen)) + if (h.gen <= 7) " " + stringResource(R.string.of_seven) else "")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PortraitView(e, h, 72.dp, Modifier.clickable { st.sheet = Sheet.Card(h, Sheet.None) })
                Column(Modifier.weight(1f)) {
                    Text(e.fullName(h), style = P.b(20.sp))
                    Text(e.statusOf(h) + " · " + (if (s != null) (if (e.alive(s)) e.sx(h, "жена", "муж") + " " + s.name else e.sx(h, "вдовец", "вдова")) else e.sx(h, "не женат", "не замужем")), style = P.small)
                    Chips(e, h, info)
                }
            }
            StatBars(e, h) { info(Help.stat(e, it)) }
            Row(Modifier.fillMaxWidth().padding(top = 10.dp).clickable { info(Help.STONES) }, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.stones), style = P.text); Q(); Grow(); Marks(h.stones, P.ink.copy(alpha = .75f), wide = true)
            }
        }
        PaperCard {
            CardHead(stringResource(R.string.family), if (heirs.isNotEmpty()) stringResource(R.string.heirs_n, heirs.size) else stringResource(R.string.heirs_none), { info(Help.HEIRS) }, help = true)
            if (fam.isEmpty()) Text(stringResource(R.string.nobody_yet), style = P.small)
            fam.forEachIndexed { i, p ->
                val isSp = p === s
                Row(Modifier.fillMaxWidth().clickable { st.sheet = Sheet.Card(p, Sheet.None) }.then(if (i < fam.size - 1) Modifier.dashBelow(P.paper3) else Modifier).padding(vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    PortraitView(e, p, 48.dp)
                    Column(Modifier.weight(1f)) {
                        Text(p.name, style = P.b(16.sp))
                        val rel = if (isSp) e.sx(h, "жена", "муж") else e.relation(p, h)
                        Text("$rel, ${e.yrs(e.age(p))}" + if (p.tr.isNotEmpty()) " · " + p.tr.take(3).joinToString(", ") { e.tname(it, p) } else "", style = P.b(12.5.sp, P.muted))
                        FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (!isSp) Pill(stringResource(R.string.heir), P.good)
                            if (p.away != null) Pill(stringResource(R.string.away), P.mid)
                            if (p.bast) Pill("незаконн" + e.sx(p, "ый", "ая"), P.paper2)
                            if (e.married(p) && !isSp) Pill(stringResource(R.string.married), P.paper2)
                        }
                    }
                }
            }
        }
        if (nk != null) PaperCard {
            H3(stringResource(R.string.nakaz)); Gap(4.dp)
            Para("«${nk.t}»"); Text("${nk.by}, ${nk.y}. " + stringResource(R.string.nakaz_reward), style = P.small)
        }
        PaperCard {
            H3(stringResource(R.string.household)); Gap(4.dp)
            KV(e.land.moneyLabel) { Text("${g.money} ${e.land.cur}", style = P.d(15.sp, P.ink)) }
            KV(stringResource(R.string.dvor_is, e.land.dvor[g.dvor])) {
                if (g.dvor < 4 && !g.over) Btn(stringResource(R.string.build_for, DVOR_COST[g.dvor + 1], e.land.cur), small = true, enabled = g.money >= DVOR_COST[g.dvor + 1]) { if (e.buildDvor()) st.touch() }
            }
            KV(stringResource(R.string.feud_with, e.land.rival.i), help = { info(e.land.helpFeud) }) { Marks(g.feud, P.red) }
            KV(stringResource(R.string.bag), help = { info(Help.BAG) }) { Text(if (e.curseBeans() > 0) stringResource(R.string.red_n, e.curseBeans()) else stringResource(R.string.curse_lifted), style = P.small) }
            if (g.secrets.isNotEmpty()) KV(stringResource(R.string.secrets), help = { info(Help.SECRETS) }) { Text("${g.secrets.size}", style = P.d(15.sp, P.red)) }
        }
        PaperCard {
            CardHead(stringResource(R.string.legacy), stringResource(R.string.glory_n, g.glory), { info(Help.GLORY) }, help = true)
            PERKS.entries.forEachIndexed { i, (k, v) ->
                val own = k in g.perks
                Row(Modifier.fillMaxWidth().then(if (i < PERKS.size - 1) Modifier.dashBelow(P.paper3) else Modifier).padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(v.n, style = P.b(15.sp, if (own) P.green else P.ink)); Text(v.d, style = P.tiny) }
                    if (own) Text(stringResource(R.string.owned), style = P.b(14.sp, P.green))
                    else Btn(stringResource(R.string.glory_cost, v.cost), small = true, enabled = g.glory >= v.cost && !g.over) { if (e.buyPerk(k)) st.touch() }
                }
            }
        }
        if (g.relics.isNotEmpty()) PaperCard {
            H3(stringResource(R.string.relics)); Gap(4.dp)
            for (r in g.relics) Row(Modifier.padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                RelicView(r.ic)
                Column {
                    Text(r.n, style = P.b(15.sp))
                    Text(r.b.entries.joinToString(", ") { "${app.seventh.domain.STAT_NAME[it.key]} +${it.value}" } + " " + stringResource(R.string.in_draws), style = P.tiny)
                    Text(r.lines.takeLast(3).joinToString("\n") { "${it.y} — ${it.t}" }, style = P.tiny)
                }
            }
        }
        PaperCard { H3(stringResource(R.string.recent)); Gap(4.dp); LogList(g.log.takeLast(5).reversed()) }
        Gap(4.dp)
    }
}

@Composable
private fun KV(label: String, help: (() -> Unit)? = null, right: @Composable () -> Unit) {
    val P = LocalPal.current
    Row(Modifier.fillMaxWidth().then(if (help != null) Modifier.clickable(onClick = help) else Modifier).padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f, fill = false), style = P.b(15.sp)); if (help != null) Q(); Grow(); right()
    }
}

@Composable
fun LogList(lines: List<LogLine>) {
    val P = LocalPal.current
    lines.forEachIndexed { i, l ->
        val c = when (l.k) { "death" -> P.death; "birth" -> P.birth; "big" -> P.dred; else -> P.ink }
        Row(Modifier.fillMaxWidth().then(if (i < lines.size - 1) Modifier.dashBelow(P.paper3) else Modifier).padding(vertical = 4.dp)) {
            Text("${l.y}", Modifier.width(44.dp), style = P.d(13.sp))
            Text(l.t, style = P.b(15.sp, c).copy(fontStyle = if (l.k == "death") FontStyle.Normal else FontStyle.Normal))
        }
    }
}
