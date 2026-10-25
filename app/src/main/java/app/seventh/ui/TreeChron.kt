package app.seventh.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.seventh.R
import app.seventh.domain.roman
import app.seventh.ui.art.Art
import app.seventh.ui.theme.LocalPal
import kotlin.math.abs

@Composable
fun TreeScreen(st: GameState) {
    val P = LocalPal.current
    val e = st.e
    val ctx = LocalContext.current
    val d = remember(st.rev, e) { e.treeData(ctx) }
    val den = LocalDensity.current
    val hs = rememberScrollState()
    val vs = rememberScrollState()
    Box(Modifier.fillMaxSize().background(Color(0xFFF0E2BC))) {
        Box(Modifier.fillMaxSize().verticalScroll(vs).horizontalScroll(hs)) {
            val w = with(den) { (d.w * TREE_SCALE).dp }
            val h = with(den) { (d.h * TREE_SCALE).dp }
            Canvas(Modifier.size(w, h).clipToBounds().pointerInput(d) {
                detectTapGestures { o ->
                    val s = TREE_SCALE * density
                    val mx = o.x / s
                    val my = o.y / s
                    d.nodes.firstOrNull { abs(it.x - mx) <= 15 && abs(it.y - my) <= 15 }?.let { n -> e.g.people[n.id]?.let { st.sheet = Sheet.Card(it, Sheet.None) } }
                }
            }) {
                val land = e.g.land
                drawIntoCanvas { Art.tree(it.nativeCanvas, TREE_SCALE * density, land, d) }
            }
        }
        LaunchedEffect(d, hs.viewportSize > 0, vs.viewportSize > 0) {
            if (hs.viewportSize == 0 || vs.viewportSize == 0) return@LaunchedEffect
            val head = d.nodes.firstOrNull { it.id == e.g.head } ?: return@LaunchedEffect
            val s = TREE_SCALE * den.density
            hs.scrollTo((head.x * s - hs.viewportSize / 2f).toInt().coerceAtLeast(0))
            vs.scrollTo((head.y * s - vs.viewportSize / 2f).toInt().coerceAtLeast(0))
        }
        Column(Modifier.align(Alignment.BottomStart).padding(10.dp).background(P.paper.copy(alpha = .94f), RoundedCornerShape(5.dp)).border(1.5.dp, P.ink, RoundedCornerShape(5.dp)).padding(horizontal = 9.dp, vertical = 5.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Dot(P.ochre); Text(stringResource(R.string.leg_head), style = P.b(12.sp))
                Dot(Color(0xFF5A5248)); Text(stringResource(R.string.leg_dead), style = P.b(12.sp))
                Dot(Color(0xFFD0402E)); Text(stringResource(R.string.leg_curse), style = P.b(12.sp))
            }
            Text(stringResource(R.string.leg_tap), style = P.b(12.sp))
        }
    }
}

@Composable
private fun Dot(c: Color) {
    val P = LocalPal.current
    Box(Modifier.padding(start = 4.dp).size(11.dp).background(c, CircleShape).border(1.5.dp, P.ink, CircleShape))
}

@Composable
fun ChronicleScreen(st: GameState) {
    val P = LocalPal.current
    val e = st.e
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PaperCard {
            H3(stringResource(R.string.heads)); Gap(6.dp)
            for (id in e.g.heads) {
                val p = e.g.people[id] ?: continue
                OptionBox(onClick = { st.sheet = Sheet.Card(p, Sheet.None) }) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        PortraitView(e, p, 48.dp)
                        Column(Modifier.weight(1f)) {
                            Text("${roman(p.gen)}. ${e.fullName(p)}", style = P.b(16.sp))
                            Text("${p.born}–${p.died ?: if (p.monk) "монастырь" else "жив" + e.sx(p, "", "а")}", style = P.b(12.5.sp, P.muted))
                            Text(p.epi.ifEmpty { p.tr.joinToString(", ") { e.tname(it, p) } }, style = P.b(12.5.sp, P.muted))
                        }
                    }
                }
                Gap(8.dp)
            }
        }
        PaperCard { H3(stringResource(R.string.chronicle)); Gap(4.dp); LogList(e.g.log.reversed()) }
    }
}

/** The tappable paper slip used for choices and heirs. */
@Composable
fun OptionBox(modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit, content: @Composable () -> Unit) {
    val P = LocalPal.current
    Box(
        modifier.fillMaxWidth()
            .alpha(if (enabled) 1f else .45f)
            .background(P.ink, RoundedCornerShape(7.dp))
            .padding(end = 2.dp, bottom = 2.dp)
            .background(P.paper2, RoundedCornerShape(7.dp))
            .border(2.dp, P.ink, RoundedCornerShape(7.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 9.dp),
    ) { Box(Modifier.fillMaxWidth().then(if (enabled) Modifier else Modifier)) { content() } }
}
