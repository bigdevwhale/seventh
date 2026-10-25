package app.seventh.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.seventh.R
import app.seventh.domain.t
import app.seventh.domain.Engine
import app.seventh.domain.STAT_NAME
import app.seventh.domain.Since
import app.seventh.domain.Stat
import app.seventh.domain.roman
import app.seventh.ui.art.Art
import app.seventh.ui.theme.LocalPal
import app.seventh.ui.theme.SeventhTheme
import app.seventh.ui.theme.palOf
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun SheetHost(st: GameState) {
    val s = st.sheet
    if (s == Sheet.None) return
    if (s == Sheet.Lands) { LandsScreen(st); return }
    if (s == Sheet.Onboarding) { OnboardingScreen(st); return }
    val P = LocalPal.current
    Box(Modifier.fillMaxSize().background(Color(0xB8140C08)).clickable(remember { MutableInteractionSource() }, null) {}, contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxSize().verticalScroll(remember(s) { ScrollState(0) }).systemBarsPadding().padding(horizontal = 10.dp, vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                Modifier.widthIn(max = 500.dp).fillMaxWidth().drawBehind {
                    val r = CornerRadius(7.dp.toPx())
                    drawRoundRect(Color(0x73000000), Offset(5.dp.toPx(), 5.dp.toPx()), size, r)
                    drawRoundRect(P.paper, cornerRadius = r)
                    drawRoundRect(P.ink, cornerRadius = r, style = Stroke(2.dp.toPx()))
                    val ins = 6.dp.toPx()
                    drawRect(if (P.land == "eu") P.ochre else if (P.land == "edo") P.red.copy(alpha = .55f) else P.ink.copy(alpha = .5f), Offset(ins, ins), Size(size.width - 2 * ins, size.height - 2 * ins), style = Stroke(1.dp.toPx()))
                }.padding(horizontal = 16.dp, vertical = 18.dp),
            ) {
                when (s) {
                    Sheet.Intro -> Intro(st)
                    is Sheet.Event -> EventSheet(st, s)
                    is Sheet.Succ -> SuccSheet(st, s)
                    is Sheet.Quiet -> { Kick("${st.e.g.year}"); SheetTitle(stringResource(R.string.quiet_years)); SinceBox(st.e, s.since); Btn(stringResource(R.string.go_on), Modifier.fillMaxWidth()) { st.close() } }
                    is Sheet.Card -> PersonCard(st, s)
                    Sheet.Menu -> SettingsSheet(st)
                    is Sheet.HowTo -> HowTo(st)
                    Sheet.Victory -> Victory(st)
                    Sheet.Over -> Over(st)
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun Kick(t: String) {
    val P = LocalPal.current
    Text(t, Modifier.fillMaxWidth(), style = P.tiny, textAlign = TextAlign.Center)
}

@Composable
fun SheetTitle(t: String) {
    val P = LocalPal.current
    Text(t, Modifier.fillMaxWidth().padding(top = 4.dp), style = P.d(26.sp), textAlign = TextAlign.Center)
    Box(Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 10.dp), contentAlignment = Alignment.Center) { Orn(Modifier.widthIn(max = 110.dp), 6.dp) }
}

@Composable
private fun SinceBox(e: Engine, s: Since?) {
    if (s == null) return
    val P = LocalPal.current
    val n = s.y - s.y0
    if (n <= 1 && s.lines.isEmpty()) return
    Column(Modifier.fillMaxWidth().padding(bottom = 10.dp).background(Color(0x4DFFFFFF), RoundedCornerShape(6.dp)).border(1.5.dp, Color(0xFFA88C5C), RoundedCornerShape(6.dp)).padding(horizontal = 11.dp, vertical = 7.dp)) {
        Text(if (n <= 1) t("${s.y} год", "The year ${s.y}") else t("Прошло ", "") + e.yrs(n) + t("", " pass") + " · ${s.y0 + 1}–${s.y}", style = P.d(14.sp, P.dred))
        for (l in s.lines.takeLast(6)) Text(l.t, style = P.b(14.sp, when (l.k) { "death" -> P.death; "birth" -> P.birth; else -> P.ink }))
        if (s.lines.size > 6) Text(t("…и ещё ${s.lines.size - 6} в летописи", "…and ${s.lines.size - 6} more in the chronicle"), style = P.small)
        if (s.lines.isEmpty()) Text(stringResource(R.string.nothing_special), style = P.small)
    }
}

private fun pctClass(pc: Int) = if (pc >= 60) 0 else if (pc >= 35) 1 else 2

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventSheet(st: GameState, s: Sheet.Event) {
    val P = LocalPal.current
    val e = st.e
    val occ = s.occ
    val h = occ.h
    Kick("${e.g.year} · ${e.fullName(h)}, ${e.yrs(e.age(h))}")
    SheetTitle(occ.ev.t)
    SinceBox(e, s.since)
    SceneView(e.g.land, occ.ev.sc, e.scene(h, occ.x))
    Gap(10.dp)
    Para(s.text)
    Gap(12.dp)
    when (val ph = s.phase) {
        Phase.Choose -> {
            if (st.tips > 0) {
                Column(Modifier.fillMaxWidth().padding(bottom = 10.dp).background(P.mid.copy(alpha = .5f), RoundedCornerShape(6.dp)).border(1.5.dp, P.ochre, RoundedCornerShape(6.dp)).clickable { st.tipShown() }.padding(horizontal = 11.dp, vertical = 8.dp)) {
                    Para(t("**Подсказка.** Процент — шанс вытянуть белый боб: чем выше, тем лучше. «Без жребия» — исход известен. «Камень на душу» — поступок против нрава.",
                        "**A tip.** The percentage is the chance of a white bean: the higher the better. “No draw” means the outcome is certain. “A stone on the soul” means acting against one's temper."), P.b(14.sp))
                    Text(t("нажмите, чтобы скрыть", "tap to hide"), style = P.tiny)
                }
            }
            var anyRoll = false
            for (o in s.opts) {
                val sinT = e.sinTrait(o, h)
                val dis = o.cost > 0 && e.g.money < o.cost
                OptionBox(Modifier.padding(bottom = 8.dp), enabled = !dis, onClick = { st.choose(s, o) }) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        o.cand?.let { PortraitView(e, it, 48.dp) }
                        Column {
                            Para(o.t)
                            FlowRow(Modifier.padding(top = 5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (o.roll != null) {
                                    anyRoll = true
                                    val pc = e.rollInfo(o, occ).pct
                                    Pill("${STAT_NAME[o.roll.st]}${if (o.roll.ofX) " (${occ.xx.name})" else ""} · $pc%", listOf(P.good, P.mid, P.bad)[pctClass(pc)])
                                } else if (o.cand == null) Pill(stringResource(R.string.no_draw), Color(0xFFF3E6C0))
                                if (o.tag != null) RichPill(o.tag) else if (o.cost > 0) Pill("−${o.cost} ${e.land.cur}", Color(0xFFF3E6C0))
                                if (sinT != null) Pill(stringResource(R.string.stone_for, e.tname(sinT, h)), Color(0xFFE2BED0))
                                if (dis) Pill(stringResource(R.string.no_money), P.bad)
                            }
                        }
                    }
                }
            }
            if (anyRoll) Row(Modifier.clickable { st.toast = Help.BAG }, verticalAlignment = Alignment.CenterVertically) { Text(stringResource(R.string.pct_legend), style = P.b(12.5.sp, P.muted)); Q() }
        }
        is Phase.Bag -> BagPhase(st, s, ph)
        is Phase.Done -> Result(st, ph.o)
    }
}

@Composable
private fun RichPill(t: String) {
    val P = LocalPal.current
    Box(Modifier.background(Color(0xFFF3E6C0), RoundedCornerShape(10.dp)).border(1.5.dp, P.ink, RoundedCornerShape(10.dp)).padding(horizontal = 8.dp, vertical = 1.dp)) {
        Text(rich(t, P.dred), style = P.b(12.sp))
    }
}

@Composable
private fun BagPhase(st: GameState, s: Sheet.Event, bag: Phase.Bag) {
    val P = LocalPal.current
    val e = st.e
    val info = bag.info
    val pc = info.pct
    val anim = remember(bag) { Animatable(0f) }
    val drawn = bag.drawn
    LaunchedEffect(drawn) {
        if (drawn == null) return@LaunchedEffect
        anim.animateTo(1f, tween(1150, easing = LinearEasing))
        delay(250)
        s.phase = Phase.Done(drawn.third)
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(Modifier.fillMaxWidth().background(listOf(P.good, P.mid, P.bad)[pctClass(pc)], RoundedCornerShape(7.dp)).border(2.dp, P.ink, RoundedCornerShape(7.dp)).padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.chance), style = P.b(15.sp))
            Text("$pc%", style = P.d(32.sp, P.ink))
        }
        val p = anim.value
        val land = e.g.land
        Canvas(Modifier.fillMaxWidth().widthIn(max = 360.dp).aspectRatio(120f / 56f).clipToBounds().border(2.dp, P.ink, RoundedCornerShape(5.dp))) {
            drawIntoCanvas {
                if (drawn == null) Art.bag(it.nativeCanvas, size.width, land, bag.beans, 0f, -1, null, 0f)
                else if (p < .5f) { val k = p / .5f; Art.bag(it.nativeCanvas, size.width, land, bag.beans, (sin(k * PI * 4) * 2 * (1 - k)).toFloat(), -1, null, 0f) }
                else { val k = ((p - .5f) / .5f).coerceIn(0f, 1f); Art.bag(it.nativeCanvas, size.width, land, bag.beans, 0f, drawn.second, drawn.first, 1 - (1 - k).pow(3)) }
            }
        }
        var open by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.why_chance) + if (open) " ▴" else " ▾", Modifier.clickable { open = !open }.padding(vertical = 4.dp), style = P.b(13.5.sp, P.muted))
            if (open) {
                for ((n, v) in info.parts) KVRow(n, (if (v > 0) "+" else "") + v)
                KVRow(stringResource(R.string.white_beans), "${info.white}")
                KVRow(stringResource(R.string.black_beans), "${info.black}")
                KVRow(stringResource(R.string.red_beans), "${info.red}", P.red)
            }
        }
        Btn(stringResource(R.string.draw_bean), Modifier.fillMaxWidth(), big = true, enabled = drawn == null) { st.draw(s, bag) }
    }
}

@Composable
private fun KVRow(k: String, v: String, c: Color? = null) {
    val P = LocalPal.current
    Row(Modifier.fillMaxWidth().dashBelow(P.paper3).padding(vertical = 3.dp)) { Text(k, Modifier.weight(1f), style = P.b(14.sp, c ?: P.ink)); Text(v, style = P.d(14.sp, c ?: P.ink)) }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Result(st: GameState, o: app.seventh.domain.Outcome) {
    val P = LocalPal.current
    val good = o.good
    val (label, bg, fg) = when (good) {
        true -> Triple(stringResource(R.string.v_luck), P.good, Color(0xFF1E3A14))
        false -> Triple(if (o.red) stringResource(R.string.v_curse) else stringResource(R.string.v_bad), P.bad, Color(0xFF5A1010))
        null -> Triple(stringResource(R.string.v_done), P.paper2, P.ink)
    }
    Text(label, Modifier.fillMaxWidth().background(bg, RoundedCornerShape(7.dp)).border(2.dp, P.ink, RoundedCornerShape(7.dp)).padding(5.dp), style = P.d(26.sp, fg), textAlign = TextAlign.Center)
    Gap(8.dp)
    val bar = if (good == false) P.red else P.green
    Column(Modifier.fillMaxWidth().background(bar.copy(alpha = .1f)).drawBehind { drawRect(bar, size = Size(4.dp.toPx(), size.height)) }.padding(start = 15.dp, end = 11.dp, top = 9.dp, bottom = 9.dp)) {
        Para(o.text)
        if (o.stone != null) Text(o.stone, Modifier.padding(top = 6.dp), style = P.b(13.sp, P.dred))
    }
    Gap(8.dp)
    if (o.diffs.isNotEmpty()) FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) { for (d in o.diffs) Pill(d.t, if (d.good) P.good else P.bad, size = 13f) }
    Gap(12.dp)
    Btn(stringResource(R.string.go_on), Modifier.fillMaxWidth(), big = true) { st.eventDone() }
}

@Composable
private fun SuccSheet(st: GameState, s: Sheet.Succ) {
    val P = LocalPal.current
    val e = st.e
    val d = s.s.dead
    Kick("${e.g.year}")
    SinceBox(e, s.since)
    SheetTitle(if (d.monk) t(e.sx(d, "Ушёл от мира", "Ушла от мира"), "Left the world") else t("Преставил" + e.sx(d, "ся", "ась"), "Gone to God"))
    SceneView(e.g.land, "plague", e.scene(d))
    Gap(10.dp)
    Para("**${e.fullName(d)}**, ${d.born}–${if (d.monk) "…" else "${d.died}, ${d.cause}"}.")
    Gap(6.dp)
    Text(d.epi, style = P.italic)
    e.g.nakaz?.let { n ->
        Text(t("Перед ${if (d.monk) "уходом" else "смертью"} ${e.sx(d, "наказал", "наказала")}: «${n.t}».", "Before ${if (d.monk) "leaving" else "dying"}, ${e.sx(d, "he", "she")} left a wish: “${n.t}”."), Modifier.fillMaxWidth().padding(vertical = 10.dp).border(2.dp, P.red, RoundedCornerShape(6.dp)).padding(horizontal = 11.dp, vertical = 9.dp), style = P.text)
    }
    Para(stringResource(R.string.choose_heir))
    Gap(10.dp)
    for (hr in s.s.heirs) {
        val p = hr.p
        OptionBox(Modifier.padding(bottom = 8.dp), onClick = { st.takeHeir(s.s, p) }) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                PortraitView(e, p, 48.dp)
                Column {
                    Text(p.name + if (hr.eldest) " " + stringResource(R.string.eldest_son) else "", style = P.b(16.sp))
                    val top = Stat.entries.sortedByDescending { e.eff(p, it) }.take(2).joinToString(", ") { "${STAT_NAME[it]} ${e.eff(p, it)}" }
                    Text("${e.relation(p, d)}, ${e.yrs(e.age(p))} · " + t("колено ", "generation ") + roman(p.gen) + " · $top", style = P.b(12.5.sp, P.muted))
                    Chips(e, p) { st.toast = it }
                    if (hr.warn != null) Text(hr.warn, Modifier.padding(top = 3.dp), style = P.b(12.5.sp, P.dred))
                }
            }
        }
    }
}

@Composable
private fun PersonCard(st: GameState, s: Sheet.Card) {
    val P = LocalPal.current
    val e = st.e
    val p = s.p
    val sp = e.spouseOf(p)
    val ks = e.kidsOf(p)
    Kick(when { p.id == e.g.head -> t("глава рода", "head of the family"); p.id == e.g.founder -> t("основатель рода", "founder of the family"); p.blood -> t("колено ", "generation ") + roman(p.gen); else -> t(e.sx(p, "вошёл в род", "вошла в род"), "married in") })
    Gap(10.dp)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PortraitView(e, p, 96.dp)
        Column(Modifier.weight(1f)) {
            Text(e.fullName(p), style = P.b(20.sp))
            Text("${p.born}${p.died?.let { "–$it" } ?: ""} · ${e.statusOf(p)}", style = P.small)
            Chips(e, p) { st.toast = it }
        }
    }
    StatBars(e, p) { st.toast = Help.stat(e, it) }
    Gap(10.dp)
    if (sp != null) Para(t(e.sx(p, "Жена", "Муж"), e.sx(p, "Wife", "Husband")) + ": ${sp.name}${if (sp.from.isNotEmpty()) t(" из ", " of ") + sp.from else ""}${if (e.alive(sp)) "" else " (†${sp.died})"}")
    if (ks.isNotEmpty()) Para(t("Дети: ", "Children: ") + ks.joinToString(", ") { it.name + if (e.alive(it)) "" else " †" })
    if (p.deeds.isNotEmpty()) { Gap(6.dp); Para(p.deeds.joinToString("\n") { "${it.y} — ${it.t}" }) }
    if (p.epi.isNotEmpty()) { Gap(6.dp); Text(p.epi, style = P.italic) }
    Gap(12.dp)
    Btn(stringResource(R.string.close), Modifier.fillMaxWidth()) { st.close() }
}

@Composable
private fun Intro(st: GameState) {
    val e = st.e
    val f = e.g.people.getValue(e.g.founder)
    Kick(stringResource(R.string.lot_cast))
    SheetTitle(stringResource(R.string.app_name))
    SceneView(e.g.land, e.land.introScene, e.scene(f))
    Gap(10.dp)
    for (p in e.land.intro(e, f) + listOf(t("Доведите кровь до [[седьмого колена]] — и проклятие спадёт. Умрёт последний в роду бездетным — род пресечётся.", "Carry the blood to the [[seventh generation]] and the curse falls away. If the last of the line dies childless, the family ends."),
        t("Исход каждого дела решает мешок с бобами: белый — удача, чёрный — беда, красный — проклятие. Чем сильнее человек, тем больше белых.", "Every deed is settled by the bag of beans: white is luck, black is misfortune, red is the curse. The stronger the person, the more white beans."))) { Para(p); Gap(8.dp) }
    Gap(4.dp)
    Btn(stringResource(R.string.accept_lot), Modifier.fillMaxWidth(), big = true) { st.close() }
    Gap(8.dp)
    Btn(stringResource(R.string.how_to), Modifier.fillMaxWidth()) { st.sheet = Sheet.HowTo(Sheet.Intro) }
    Gap(8.dp)
    Btn(stringResource(R.string.other_land), Modifier.fillMaxWidth(), small = true) { st.sheet = Sheet.Lands }
}

@Composable
private fun HowTo(st: GameState) {
    SheetTitle(stringResource(R.string.how_to))
    for (l in listOf(
        t("**Цель** — довести кровь до VII колена. Колено — это поколение: дети главы — следующее колено.", "**The aim** is to carry the blood to generation VII. The head's children are the next generation."),
        t("**«Жить дальше»** проматывает годы, пока не случится событие. Рождаются и умирают люди — это видно в сводке.", "**Live on** runs the years until something happens. People are born and die; the summary shows who."),
        t("**Событие** — выберите поступок. Процент — шанс вытянуть белый боб. Его повышают показатели, черты, реликвии.", "**An event**: choose what to do. The percentage is the chance of a white bean; stats, traits and relics raise it."),
        t("**Камни на душе** — за поступки против нрава. Пять камней — срыв.", "**Stones on the soul** come from acting against one's temper. Five stones and something breaks."),
        t("**Смерть главы** — выберите наследника среди потомков. Нет потомков — род пресёкся.", "**When the head dies**, choose an heir among the descendants. No descendants, and the line ends."),
        t("**Слава** копится за подвиги и наказы предков; тратьте её на «Наследие рода».", "**Glory** gathers from brave deeds and ancestors' wishes; spend it on the family legacy."),
    ).withIndex()) { Para("${l.index + 1}. ${l.value}"); Gap(6.dp) }
    Gap(6.dp)
    Btn(stringResource(R.string.understood), Modifier.fillMaxWidth(), big = true) { st.close() }
}

@Composable
private fun Victory(st: GameState) {
    val e = st.e
    val h = e.h
    Kick("${e.g.year}")
    SheetTitle(stringResource(R.string.app_name))
    SceneView(e.g.land, "dawn", e.scene(h, family = e.house().take(5)))
    Gap(10.dp)
    Para(t("${h.name} — седьм${e.sx(h, "ой", "ая")} в роду от основателя. Утром на пороге нашли красную нитку, перекушенную пополам.", "${h.name} is the seventh generation from the founder. In the morning a red thread was found on the doorstep, bitten in two.")); Gap(8.dp)
    Para(t("Проклятие снято. Красного боба в мешке больше нет. Слава рода +10.", "The curse is lifted. There is no red bean in the bag any more. Glory +10.")); Gap(8.dp)
    Para(t("Род может жить дальше — сколько выдержит кровь.", "The family may live on, as long as the blood holds."))
    Gap(12.dp)
    Btn(stringResource(R.string.live_on), Modifier.fillMaxWidth(), big = true) { st.close() }
}

@Composable
private fun Over(st: GameState) {
    val P = LocalPal.current
    val e = st.e
    val d = e.h
    Kick("${e.g.year}")
    SheetTitle(stringResource(R.string.line_ended))
    SceneView(e.g.land, "plague", e.scene(d))
    Gap(10.dp)
    Para(e.g.overTxt); Gap(8.dp)
    Text(d.epi, style = P.italic); Gap(8.dp)
    Para(stringResource(R.string.over_chest))
    Gap(12.dp)
    Btn(stringResource(R.string.see_tree), Modifier.fillMaxWidth()) { st.close(); st.tab = 1 }
    Gap(8.dp)
    Btn(stringResource(R.string.new_lot), Modifier.fillMaxWidth(), big = true) { st.sheet = Sheet.Lands }
}

private class LandCard(val id: String, val name: String, val years: String, val style: String, val desc: String, val on: Boolean)

@Composable
private fun LandsScreen(st: GameState) {
    val P = LocalPal.current
    val lands = listOf(
        LandCard("rus", t("Русь", "Rus"), "1700 — 1950", t("лубок", "lubok print"), t("Изба и барин, рекрутчина, воля, колхоз. Нищенка проклинает род до седьмого колена.", "The izba and the barin, the recruit levy, emancipation, the collective farm. A beggar woman curses the family to the seventh generation."), true),
        LandCard("eu", t("Средневековая Европа", "Medieval Europe"), "1180 — 1460", t("книжная миниатюра", "illuminated manuscript"), t("Сеньор и виллан, крестовые походы, Чёрная смерть, Жакерия, Дева. Сожжённая травница проклинает род.", "Seigneur and villein, crusades, the Black Death, the Jacquerie, the Maid. A herbwoman on the pyre curses the family."), true),
        LandCard("edo", t("Япония", "Japan"), "1660 — 1950", t("гравюра укиё-э", "ukiyo-e print"), t("Рисовая подать даймё, паломничество в Исэ, чёрные корабли, Мэйдзи. Белая лиса из святилища Инари проклинает род.", "The daimyo's rice tax, the pilgrimage to Ise, the Black Ships, Meiji. A white fox from an Inari shrine curses the family."), true),
        LandCard("scot", t("Шотландия", "Scotland"), "1690 — 1950", t("книжная гравюра", "book engraving"), t("Кланы и якобиты, расчистка земель, пароход в Америку.", "Clans and Jacobites, the Clearances, a steamer to America."), false),
        LandCard("ital", t("Италия", "Italy"), "1450 — 1750", t("фреска", "fresco"), t("Гильдии, чума, кондотьеры, долги у банкиров.", "Guilds, plague, condottieri, debts to bankers."), false),
    )
    Box(Modifier.fillMaxSize().background(Color(0xFF1F1712)), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.widthIn(max = 520.dp).fillMaxSize().systemBarsPadding().background(P.bg).verticalScroll(rememberScrollState()).padding(12.dp)) {
            Kick(stringResource(R.string.new_family))
            SheetTitle(stringResource(R.string.where_begins))
            Para(stringResource(R.string.where_note))
            Gap(12.dp)
            for (l in lands) SeventhTheme(palOf(l.id)) {
                val Q = LocalPal.current
                Column(Modifier.fillMaxWidth().padding(bottom = 12.dp).background(if (l.on) Q.paper2 else Color(0xFFE4D6B4), RoundedCornerShape(7.dp)).border(2.dp, Q.ink, RoundedCornerShape(7.dp))
                    .clickable { if (l.on) st.newGame(l.id) else st.toast = t("${l.name} пока в работе.", "${l.name} is still being made.") }) {
                    if (l.on) SceneView(l.id, "village", app.seventh.ui.art.SceneData(null, year = 1700))
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(l.name, Modifier.weight(1f), style = Q.d(22.sp, if (l.on) (if (l.id == "rus") Q.red else Q.blue) else Color(0xFF8A7A62)))
                            if (!l.on) Pill(stringResource(R.string.soon), Q.paper)
                        }
                        Text("${l.years} · ${l.style}", style = Q.b(12.5.sp, Q.muted))
                        Gap(4.dp)
                        Text(l.desc, style = Q.b(14.5.sp, if (l.on) Q.ink else Q.muted))
                    }
                }
            }
        }
    }
}
