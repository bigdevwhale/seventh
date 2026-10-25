package app.seventh.ui

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import app.seventh.BuildConfig
import app.seventh.R
import app.seventh.domain.Engine
import app.seventh.domain.Looks
import app.seventh.domain.Person
import app.seventh.ui.art.Art
import app.seventh.ui.art.Fig
import app.seventh.ui.art.HomeData
import app.seventh.ui.art.PBg
import app.seventh.ui.art.Ring
import app.seventh.ui.art.SceneData
import app.seventh.ui.art.TreeData
import app.seventh.ui.art.TreeEdge
import app.seventh.ui.art.TreeNode
import app.seventh.ui.theme.LocalPal

/** Seconds since the view appeared, ticking at about 30 fps while [running]; screenshots hold one frame. */
@Composable
fun clock(running: Boolean = true): State<Float> {
    val t = remember { mutableFloatStateOf(1f) }
    LaunchedEffect(running) {
        if (!running || BuildConfig.SHOTS) return@LaunchedEffect
        val t0 = withFrameNanos { it }
        var last = 0L
        while (true) withFrameNanos { n -> if (n - last > 30_000_000L) { last = n; t.floatValue = 1f + (n - t0) / 1e9f } }
    }
    return t
}

fun Engine.fig(p: Person) = Fig(if (p.male) 'm' else 'f', age(p), p.look.skin, Looks.hairAt(p.look.hair, age(p)), p.look.cloth, p.look.kerch, p.id)

fun Engine.scene(hero: Person?, other: Person? = null, family: List<Person> = emptyList()) =
    SceneData(hero?.let { fig(it) }, hero?.takeIf { married(it) }?.let { spouseOf(it) }?.let { fig(it) }, other?.let { fig(it) }, family.map { fig(it) }, g.year)

@Composable
fun SceneView(land: String, key: String, data: SceneData, modifier: Modifier = Modifier, running: Boolean = true) {
    val t = clock(running)
    Canvas(modifier.fillMaxWidth().aspectRatio(176f / 100f).clipToBounds().border(2.dp, LocalPal.current.ink, RoundedCornerShape(4.dp))) {
        val tt = t.value
        drawIntoCanvas { Art.scene(it.nativeCanvas, size.width, land, key, data, tt) }
    }
}

@Composable
fun HomeView(land: String, data: HomeData, modifier: Modifier = Modifier, running: Boolean = true) {
    val t = clock(running)
    Canvas(modifier.fillMaxWidth().aspectRatio(176f / 96f).clipToBounds().border(2.dp, LocalPal.current.ink, RoundedCornerShape(4.dp))) {
        val tt = t.value
        drawIntoCanvas { Art.home(it.nativeCanvas, size.width, land, data, tt) }
    }
}

fun Engine.bgOf(p: Person) = if (p.id in g.heads) PBg.HEAD else if (p.blood) PBg.BLOOD else PBg.INLAW

@Composable
fun PortraitView(e: Engine, p: Person, size: Dp, modifier: Modifier = Modifier) {
    val fig = e.fig(p)
    val bg = e.bgOf(p)
    val faded = p.died != null || p.monk
    val land = e.g.land
    Canvas(modifier.size(size).clipToBounds().border(1.5.dp, LocalPal.current.ink, RoundedCornerShape(4.dp))) {
        drawIntoCanvas { Art.portrait(it.nativeCanvas, this.size.width, land, fig, bg, faded) }
    }
}

@Composable
fun ThreadView(gen: Int, won: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier.size(116.dp, 18.dp).clipToBounds()) { drawIntoCanvas { Art.thread(it.nativeCanvas, size.width, gen, won) } }
}

@Composable
fun RelicView(icon: String, modifier: Modifier = Modifier) {
    Canvas(modifier.size(36.dp).clipToBounds().border(1.5.dp, LocalPal.current.ink, RoundedCornerShape(4.dp))) { drawIntoCanvas { Art.relic(it.nativeCanvas, size.width, icon) } }
}

const val TREE_SCALE = 2f

/** Lays the bloodline out bottom-up: founder at the root, each generation one row higher (as in the prototype). */
fun Engine.treeData(ctx: Context): TreeData {
    val root = g.people.getValue(g.founder)
    val sw = 46f
    val rh = 62f
    var slot = 0
    val px = HashMap<Int, Float>()
    val pd = HashMap<Int, Int>()
    var maxD = 0
    fun place(p: Person, d: Int) {
        maxD = maxOf(maxD, d)
        val ks = kidsOf(p)
        if (ks.isEmpty()) px[p.id] = (slot++ * sw)
        else { ks.forEach { place(it, d + 1) }; val xs = ks.map { px.getValue(it.id) }; px[p.id] = Math.round((xs.min() + xs.max()) / 2).toFloat() }
        pd[p.id] = d
    }
    place(root, 0)
    val rows = maxOf(maxD + 1, 7)
    val w = maxOf(200f, slot * sw + 70)
    val h = rows * rh + 80
    val x = { id: Int -> 48 + px.getValue(id) }
    val y = { id: Int -> h - 54 - pd.getValue(id) * rh }
    val lv = HashMap<Int, Boolean>()
    fun liv(p: Person): Boolean = lv.getOrPut(p.id) { (alive(p) && !p.monk) or kidsOf(p).map { liv(it) }.any { it } }
    val main = HashSet<Int>()
    for (i in 1 until g.heads.size) {
        var c = g.people[g.heads[i]]
        val prev = g.heads[i - 1]
        var n = 0
        while (c != null && c.id != prev && n++ < 12) {
            main.add(c.id)
            val f = person(c.father); val m = person(c.mother)
            c = if (f != null && f.blood) f else if (m != null && m.blood) m else null
        }
    }
    val all = g.people.values.filter { it.blood && px.containsKey(it.id) }
    val nodes = all.map { p ->
        val ring = when { p.id == g.head -> Ring.HEAD; p.id in g.heads -> Ring.WAS; alive(p) && !p.monk -> Ring.ALIVE; else -> Ring.DEAD }
        TreeNode(p.id, x(p.id), y(p.id), fig(p), bgOf(p), p.died != null || p.monk, ring, liv(p), alive(p) && !p.monk,
            spouseOf(p)?.let { alive(it) }, p.bast, p.id in g.heads, p.name, "${p.born}–${p.died ?: if (p.monk) "мон." else ""}")
    }
    val edges = all.mapNotNull { p ->
        val par = listOf(person(p.father), person(p.mother)).firstOrNull { it != null && it.blood && px.containsKey(it.id) } ?: return@mapNotNull null
        TreeEdge(par.id, p.id, p.id in main)
    }
    val eu = g.land == "eu"
    val num = ResourcesCompat.getFont(ctx, if (eu) R.font.alegreya_sc_extrabold else R.font.ruslan_display)!!
    val lab = ResourcesCompat.getFont(ctx, if (eu) R.font.alegreya else R.font.kurale)!!
    return TreeData(w, h, rows, List(rows) { y(root.id) - it * rh }, root.id, nodes, edges, g.won, g.year % 4 == 2, num, lab)
}
