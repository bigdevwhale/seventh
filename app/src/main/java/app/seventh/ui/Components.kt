package app.seventh.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.seventh.domain.Engine
import app.seventh.domain.Person
import app.seventh.domain.Stat
import app.seventh.domain.STAT_NAME
import app.seventh.domain.TrKind
import app.seventh.domain.Traits
import app.seventh.domain.t
import app.seventh.ui.theme.LocalPal

/** The ornament band: diamonds in Rus, a checker in Europe. */
@Composable
fun Orn(modifier: Modifier = Modifier, h: Dp = 10.dp) {
    val P = LocalPal.current
    Canvas(modifier.fillMaxWidth().height(h + 4.dp)) {
        val b = 2.dp.toPx()
        drawRect(P.ink)
        val top = b
        val hh = size.height - 2 * b
        drawRect(P.ornB, Offset(0f, top), Size(size.width, hh))
        val s = hh
        val n = (size.width / s).toInt().coerceAtLeast(1)
        var x = (size.width - n * s) / 2f
        var i = 0
        while (i < n) {
            if (P.land == "edo") {
                drawRect(P.ornA, Offset(x, top), Size(s, hh))
                for (r in listOf(.5f, .3f)) drawArc(P.ornB, 180f, 180f, false, Offset(x + s / 2 - s * r, top + hh - s * r), Size(2 * s * r, 2 * s * r), style = Stroke(1.2f * density))
            } else if (P.land == "eu") {
                drawRect(P.ornA, Offset(x, top), Size(s / 2, hh / 2))
                drawRect(P.ornA, Offset(x + s / 2, top + hh / 2), Size(s / 2, hh / 2))
            } else {
                val p = Path().apply { moveTo(x + s / 2, top); lineTo(x + s, top + hh / 2); lineTo(x + s / 2, top + hh); lineTo(x, top + hh / 2); close() }
                drawPath(p, P.ornA)
            }
            x += s; i++
        }
    }
}

/** A paper card with an ink border and a thin inner rule, as in a printed sheet. */
@Composable
fun PaperCard(modifier: Modifier = Modifier, tint: Color? = null, pad: Dp = 13.dp, content: @Composable ColumnScope.() -> Unit) {
    val P = LocalPal.current
    val bg = tint ?: P.paper
    Column(
        modifier
            .fillMaxWidth()
            .drawBehind {
                val r = 5.dp.toPx()
                drawRoundRect(Color(0x471F1712), Offset(3.dp.toPx(), 3.dp.toPx()), size, CornerRadius(r))
                drawRoundRect(bg, cornerRadius = CornerRadius(r))
                drawRoundRect(P.ink, cornerRadius = CornerRadius(r), style = Stroke(2.dp.toPx()))
                val ins = 5.dp.toPx()
                drawRect(if (P.land == "eu") P.ochre else if (P.land == "edo") P.red.copy(alpha = .55f) else P.ink.copy(alpha = .4f), Offset(ins, ins), Size(size.width - 2 * ins, size.height - 2 * ins), style = Stroke(1.dp.toPx()))
            }
            .padding(pad),
        content = content,
    )
}

@Composable
fun H3(text: String, modifier: Modifier = Modifier, color: Color? = null) {
    val P = LocalPal.current
    Text(text, modifier, style = P.d(17.sp, color ?: P.red))
}

/** A card heading with something small on the right. */
@Composable
fun CardHead(title: String, right: String? = null, onRight: (() -> Unit)? = null, help: Boolean = false) {
    val P = LocalPal.current
    Row(Modifier.fillMaxWidth().padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        H3(title, Modifier.weight(1f))
        if (right != null) Row(Modifier.then(if (onRight != null) Modifier.clickable(onClick = onRight) else Modifier), verticalAlignment = Alignment.CenterVertically) {
            Text(right, style = P.b(12.5.sp, P.muted))
            if (help) Q()
        }
    }
}

/** The little «?» that explains a thing. */
@Composable
fun Q(modifier: Modifier = Modifier) {
    val P = LocalPal.current
    Box(modifier.padding(start = 5.dp).size(17.dp).clip(CircleShape).background(P.paper2).border(1.5.dp, P.ink, CircleShape), contentAlignment = Alignment.Center) {
        Text("?", style = P.b(11.sp).copy(lineHeight = 11.sp))
    }
}

@Composable
fun Pill(text: String, bg: Color, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, size: Float = 12f) {
    val P = LocalPal.current
    Box(modifier.clip(RoundedCornerShape(10.dp)).background(bg).border(1.5.dp, P.ink, RoundedCornerShape(10.dp)).then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier).padding(horizontal = 8.dp, vertical = 1.dp)) {
        Text(text, style = P.b(size.sp))
    }
}

/** Trait chips; tapping one tells what it does. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Chips(e: Engine, p: Person, onInfo: (String) -> Unit) {
    val P = LocalPal.current
    FlowRow(Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        for (t in p.tr) {
            val tr = Traits.all[t] ?: continue
            val bg = when (tr.kind) { TrKind.NRAV -> Color(0xFFF0C0A8); TrKind.KROV -> Color(0xFFC2D2E8); TrKind.NAZH -> Color(0xFFF0D68E) }
            Pill(e.tname(t, p), bg, onClick = { onInfo(e.cap(e.tname(t, p)) + ": " + tr.desc) })
        }
        if (p.bast) Pill(t("незаконн" + e.sx(p, "ый", "ая"), "born out of wedlock"), P.paper2)
    }
}

/** Six stats as rows of twelve beans: black = own, green = lifted by traits, red = pulled down by them. */
@Composable
fun StatBars(e: Engine, p: Person, onInfo: (Stat) -> Unit) {
    val P = LocalPal.current
    Column(Modifier.padding(top = 9.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        for (s in Stat.entries) {
            val b = p[s]
            val v = e.eff(p, s)
            Row(Modifier.fillMaxWidth().clickable { onInfo(s) }, verticalAlignment = Alignment.CenterVertically) {
                Text(STAT_NAME.getValue(s), Modifier.width(92.dp), style = P.b(14.sp))
                Canvas(Modifier.weight(1f).height(10.dp)) {
                    val d = size.height
                    val gap = (size.width - 12 * d) / 11f
                    for (i in 0 until 12) {
                        val c = when {
                            i < minOf(b, v) -> P.ink
                            i < v -> P.green
                            i < b -> P.red
                            else -> null
                        }
                        val cx = i * (d + gap) + d / 2
                        if (c != null) drawCircle(c, d / 2, Offset(cx, d / 2))
                        else { drawCircle(Color(0xFFE6D5AA), d / 2, Offset(cx, d / 2)); drawCircle(Color(0xFFB89C6C), d / 2 - .5f, Offset(cx, d / 2), style = Stroke(1f)) }
                    }
                }
                Text("$v", Modifier.width(28.dp), style = P.b(15.sp), textAlign = TextAlign.End)
            }
        }
    }
}

/** n of 5 round marks. */
@Composable
fun Marks(n: Int, fill: Color, wide: Boolean = false) {
    val P = LocalPal.current
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(5) { i ->
            Box(Modifier.size(if (wide) 13.dp else 10.dp, 10.dp).clip(CircleShape).background(if (i < n) fill else Color(0xFFE6D5AA)).border(1.5.dp, if (i < n) P.ink else Color(0xFF9C8458), CircleShape))
        }
    }
}

/** A button with an ink shadow that sinks when pressed. */
@Composable
fun Btn(text: String, modifier: Modifier = Modifier, sub: String? = null, big: Boolean = false, small: Boolean = false, enabled: Boolean = true, onClick: () -> Unit) {
    val P = LocalPal.current
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val sh = if (pressed) 0.dp else if (small) 1.5.dp else 2.dp
    val bg = if (big) P.big else P.ochre
    val fg = if (big) P.paper else P.ink
    Box(
        modifier
            .alpha(if (enabled) 1f else .45f)
            .offset(if (pressed) 2.dp else 0.dp, if (pressed) 2.dp else 0.dp)
            .drawBehind {
                val r = CornerRadius(7.dp.toPx())
                drawRoundRect(P.ink, Offset(sh.toPx(), sh.toPx()), size, r)
                drawRoundRect(bg, cornerRadius = r)
                drawRoundRect(P.ink, cornerRadius = r, style = Stroke(2.dp.toPx()))
            }
            .clickable(src, null, enabled = enabled, onClick = onClick)
            .padding(horizontal = if (small) 10.dp else 12.dp, vertical = if (small) 3.dp else if (big) 10.dp else 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, style = if (big) P.d(20.sp, fg) else P.b(if (small) 13.sp else 17.sp, fg), textAlign = TextAlign.Center)
            if (sub != null) Text(sub, style = P.b(12.sp, fg.copy(alpha = .85f)), textAlign = TextAlign.Center)
        }
    }
}

/** `**bold**` and `[[warning]]` inside game text. */
fun rich(s: String, warn: Color): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < s.length) {
        when {
            s.startsWith("**", i) -> { val j = s.indexOf("**", i + 2).takeIf { it > 0 } ?: s.length; withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(s.substring(i + 2, j)) }; i = j + 2 }
            s.startsWith("[[", i) -> { val j = s.indexOf("]]", i + 2).takeIf { it > 0 } ?: s.length; withStyle(SpanStyle(color = warn)) { append(s.substring(i + 2, j)) }; i = j + 2 }
            else -> { append(s[i]); i++ }
        }
    }
}

@Composable
fun Para(s: String, style: TextStyle? = null, modifier: Modifier = Modifier) {
    val P = LocalPal.current
    Text(rich(s, P.dred), modifier, style = style ?: P.text)
}

@Composable
fun Gap(h: Dp = 8.dp) = Spacer(Modifier.height(h))

@Composable
fun RowScope.Grow() = Spacer(Modifier.weight(1f))

/** Thin dashed separator between list rows. */
fun Modifier.dashBelow(c: Color) = drawBehind {
    val y = size.height - 1f
    var x = 0f
    while (x < size.width) { drawLine(c, Offset(x, y), Offset(minOf(x + 4f, size.width), y), 1.5f); x += 8f }
}
