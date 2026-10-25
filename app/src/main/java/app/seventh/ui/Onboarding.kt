package app.seventh.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.seventh.domain.Looks
import app.seventh.domain.t
import app.seventh.ui.art.Art
import app.seventh.ui.art.Fig
import app.seventh.ui.art.SceneData
import app.seventh.ui.theme.LocalPal
import app.seventh.ui.theme.RusPal
import app.seventh.ui.theme.SeventhTheme

private class Page(val title: String, val text: List<String>, val art: @Composable () -> Unit)

/** Four pages before the first family: the curse, the bag of beans, the generations, temper and the lands. */
@Composable
fun OnboardingScreen(st: GameState) {
    SeventhTheme(RusPal) {
        val P = LocalPal.current
        val hero = Fig('m', 24, Looks.SKIN[0], Looks.HAIR[2], Looks.CLOTH_M[0], Looks.KERCH[0], 7)
        val beggar = Fig('f', 70, Looks.SKIN[1], Looks.HAIR[0], Looks.CLOTH_F[1], Looks.KERCH[3], 9, "beggar")
        val pages = listOf(
            Page(t("Седьмое колено", "Seventh Generation"), listOf(
                t("Основателя вашего рода прокляли. На Руси — нищенка, которой не подали хлеба. В Европе — травница на костре. В Японии — белая лиса у святилища.",
                    "Your family's founder was cursed: in Rus by a beggar woman turned away without bread, in Europe by a herbwoman on the pyre, in Japan by a white fox at a shrine."),
                t("Проклятие держится до **седьмого колена**. Доведите кровь до седьмого поколения — и оно спадёт. Умрёт последний в роду бездетным — род пресечётся.",
                    "The curse holds **to the seventh generation**. Carry the blood that far and it falls away. If the last of the line dies childless, the family ends."),
            )) { SceneView("rus", "road", SceneData(hero, other = beggar, year = 1700)) },
            Page(t("Мешок с бобами", "The bag of beans"), listOf(
                t("Каждое дело решает боб из мешка: **белый** — удача, **чёрный** — беда, **красный** — проклятие.",
                    "Every deed is settled by a bean from the bag: **white** is luck, **black** is misfortune, **red** is the curse."),
                t("Белых тем больше, чем выше нужный показатель — сила, ум, сердце, хитрость, вера или здоровье. Черты, реликвии и наследие рода добавляют ещё. Процент на каждом выборе — шанс вытянуть белый.",
                    "The higher the stat a deed needs (strength, wits, heart, cunning, faith or health), the more white beans. Traits, relics and the family legacy add more. The percentage on each choice is the chance of drawing white."),
            )) {
                Canvas(Modifier.fillMaxWidth().aspectRatio(120f / 56f).clipToBounds().border(2.dp, P.ink, RoundedCornerShape(5.dp))) {
                    drawIntoCanvas { Art.bag(it.nativeCanvas, size.width, "rus", List(8) { 'w' } + List(3) { 'b' } + 'r', 0f, 0, 'w', 1f) }
                }
            },
            Page(t("Колено за коленом", "Generation after generation"), listOf(
                t("**«Жить дальше»** проматывает годы до следующего события. Рождаются дети — у каждого своя кровь и свой нрав.",
                    "**Live on** runs the years until something happens. Children are born, each with their own blood and temper."),
                t("Когда глава рода умрёт, вы выбираете, за кого играть дальше: любого живого потомка. Обойдёте старшего сына — он затаит обиду. Красная нить в шапке считает колена.",
                    "When the head of the family dies, you choose whom to play next: any living descendant. Pass over the eldest son and he bears a grudge. The red thread at the top counts the generations."),
            )) {
                Box(Modifier.fillMaxWidth().padding(vertical = 18.dp), contentAlignment = Alignment.Center) {
                    Canvas(Modifier.size(290.dp, 45.dp)) { drawIntoCanvas { Art.thread(it.nativeCanvas, size.width, 4, false) } }
                }
            },
            Page(t("Нрав, слава и земли", "Temper, glory and lands"), listOf(
                t("Поступок против нрава кладёт **камень на душу**: добрый жесток, честный лжёт, храбрый бежит. Пять камней — срыв.",
                    "Acting against one's temper lays a **stone on the soul**: the kind are cruel, the honest lie, the brave run. Five stones and something breaks."),
                t("Подвиги и исполненные наказы предков приносят славу — её тратят на наследие, которое останется с родом навсегда. Начните на Руси, в средневековой Европе или в Японии: у каждой земли свои люди, своя история и свой рисунок.",
                    "Brave deeds and fulfilled wishes of the dead bring glory, spent on a legacy the family keeps forever. Begin in Rus, in medieval Europe or in Japan: each land has its own people, its own history and its own drawing."),
            )) { SceneView("edo", "village", SceneData(null, year = 1700)) },
        )
        var i by remember { mutableIntStateOf(0) }
        Box(Modifier.fillMaxSize().background(P.window), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 520.dp).fillMaxSize().systemBarsPadding().background(P.bg)) {
                AnimatedContent(i, Modifier.weight(1f), label = "page") { k ->
                    val pg = pages[k]
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp)) {
                        Text(pg.title, Modifier.fillMaxWidth().padding(top = 6.dp), style = P.d(26.sp), textAlign = TextAlign.Center)
                        Box(Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 14.dp), contentAlignment = Alignment.Center) { Orn(Modifier.widthIn(max = 110.dp), 6.dp) }
                        pg.art()
                        Gap(14.dp)
                        for (p in pg.text) { Para(p, P.b(16.5.sp)); Gap(10.dp) }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.Center) {
                    repeat(pages.size) { k -> Box(Modifier.padding(4.dp).size(10.dp).background(if (k == i) P.red else Color(0xFFE6D5AA), CircleShape).border(1.5.dp, P.ink, CircleShape)) }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (i < pages.size - 1) Btn(t("Пропустить", "Skip"), Modifier.weight(1f)) { st.onboarded() } else Spacer(Modifier.weight(1f))
                    Btn(if (i < pages.size - 1) t("Дальше", "Next") else t("Начать", "Begin"), Modifier.weight(1.4f), big = true) { if (i < pages.size - 1) i++ else st.onboarded() }
                }
            }
        }
    }
}
