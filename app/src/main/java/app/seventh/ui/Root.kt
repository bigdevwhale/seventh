package app.seventh.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.seventh.R
import app.seventh.domain.roman
import app.seventh.ui.theme.LocalPal
import app.seventh.ui.theme.SeventhTheme
import app.seventh.ui.theme.palOf
import kotlinx.coroutines.delay

@Composable
fun SeventhRoot(st: GameState) {
    val land = st.engine?.g?.land ?: "rus"
    SeventhTheme(palOf(land)) {
        val P = LocalPal.current
        @Suppress("UNUSED_VARIABLE") val rev = st.rev
        Box(Modifier.fillMaxSize().background(P.window), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 520.dp).fillMaxSize().background(P.bg)) {
                val e = st.engine
                if (e != null) {
                    Header(st)
                    Orn()
                    Box(Modifier.weight(1f).fillMaxWidth()) {
                        when (st.tab) {
                            0 -> HomeScreen(st)
                            1 -> TreeScreen(st)
                            else -> ChronicleScreen(st)
                        }
                    }
                    if (st.tab == 0) {
                        Box(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            if (e.g.over) Btn(stringResource(R.string.new_lot), Modifier.fillMaxWidth(), big = true) { st.sheet = Sheet.Lands }
                            else Btn(stringResource(R.string.live_on), Modifier.fillMaxWidth(), sub = stringResource(R.string.live_on_sub), big = true) { st.live() }
                        }
                    }
                    Orn()
                    Tabs(st)
                }
            }
            SheetHost(st)
            Toast(st)
        }
        BackHandler(st.sheet is Sheet.Card || st.sheet is Sheet.HowTo || st.sheet is Sheet.Menu) { st.close() }
        BackHandler(st.sheet == Sheet.None && st.tab != 0) { st.tab = 0 }
        BackHandler(st.sheet == Sheet.Lands && st.engine != null) { st.close() }
    }
}

@Composable
private fun Header(st: GameState) {
    val P = LocalPal.current
    val e = st.e
    val h = e.h
    Row(Modifier.fillMaxWidth().background(P.bar).statusBarsPadding().padding(start = 14.dp, end = 8.dp, top = 10.dp, bottom = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.family_of, e.land.family(e.g)), style = P.d(21.sp, P.ochre), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(e.g.place.substringBefore(','), style = P.b(13.sp, P.paper.copy(alpha = .8f)), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("${e.g.year}", style = P.d(24.sp, P.paper))
            ThreadView(h.gen, e.g.won, Modifier.padding(vertical = 2.dp))
            Text(if (e.g.won) stringResource(R.string.curse_lifted) else stringResource(R.string.gen_of_seven, roman(h.gen)), style = P.b(12.sp, P.paper))
        }
        Text("≡", Modifier.clip(RoundedCornerShape(6.dp)).clickable { st.sheet = Sheet.Menu }.padding(horizontal = 8.dp), style = P.b(28.sp, P.paper))
    }
}

@Composable
private fun Tabs(st: GameState) {
    val P = LocalPal.current
    val names = listOf(if (st.e.g.land != "rus") stringResource(R.string.tab_home_eu) else stringResource(R.string.tab_home), stringResource(R.string.tab_tree), stringResource(R.string.tab_chron))
    Row(Modifier.fillMaxWidth().background(P.bar).navigationBarsPadding().padding(horizontal = 6.dp, vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        names.forEachIndexed { i, n ->
            val on = st.tab == i
            Box(Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(if (on) P.ochre else P.bar).clickable { st.tab = i }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text(n, style = P.b(15.sp, if (on) P.ink else P.barText))
            }
        }
    }
}

@Composable
private fun Toast(st: GameState) {
    val P = LocalPal.current
    val t = st.toast
    LaunchedEffect(t) { if (t != null) { delay(4200); if (st.toast == t) st.toast = null } }
    Box(Modifier.fillMaxSize().navigationBarsPadding().padding(bottom = 96.dp), contentAlignment = Alignment.BottomCenter) {
        AnimatedVisibility(t != null, enter = fadeIn(), exit = fadeOut()) {
            Text(t ?: "", Modifier.padding(horizontal = 24.dp).widthIn(max = 420.dp).clip(RoundedCornerShape(7.dp)).background(P.ink).border(1.5.dp, P.ochre, RoundedCornerShape(7.dp)).clickable { st.toast = null }.padding(horizontal = 13.dp, vertical = 8.dp),
                style = P.b(14.sp, P.paper), textAlign = TextAlign.Center)
        }
    }
}
