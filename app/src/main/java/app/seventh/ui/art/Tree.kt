package app.seventh.ui.art

import android.graphics.Paint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/* tree */
private val ROMAN = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX")
private val C_DEAD = hex(0xFF7A6A58); private val C_HEAD = hex(0xFF8E2622); private val C_MUT = hex(0xFF6E5A3E); private val C_HALO = hex(0xFFF5EACB)

internal fun Ctx.nodeFrame(x: Double, y: Double, ring: Ring) {
    val col = when (ring) { Ring.HEAD -> OCH; Ring.WAS -> hex(0xFFD2A050); Ring.ALIVE -> BRN; Ring.DEAD -> GRY }
    el(x, y, 15.0, 15.0, col, 0.8, n = 28)
    for (i in 0 until 16) { val a = i / 16.0 * 6.283; dot(x + cos(a) * 13.6, y + sin(a) * 13.6, 0.6, if (ring == Ring.HEAD) KIN else WHT) }
}
internal fun Ctx.leaf(x: Double, y: Double, a: Double, s: Double, col: Int) {
    save(); translate(x, y); rotate(a)
    sh(pt(0.0, 0.0, s * 0.5, -s * 0.32, s, 0.0, s * 0.5, s * 0.32), col, 0.4, sm = true); ln(pt(0.4, 0.0, s * 0.85, 0.0), INK, 0.25)
    restore()
}

internal fun Ctx.tree(d: TreeData) {
    val W = d.w.toDouble(); val Hh = d.h.toDouble()
    val byId = d.nodes.associateBy { it.id }
    val RH = if (d.rowY.size > 1) abs((d.rowY[0] - d.rowY[1]).toDouble()) else 62.0
    fillRect(0.0, 0.0, W, Hh, hex(0xFFF0E2BC))
    for (r in 0 until d.rows) {
        val y = d.rowY[r].toDouble()
        if (r % 2 == 1) fillRect(0.0, y - RH / 2, W, RH, rgba(200, 150, 80, 0.12))
        fade(0.25) { dash(1.5, 2.5); ln(pt(22.0, y, W - 4, y), INK, 0.35); noDash() }
    }
    ground(W, Hh, Hh - 12, hex(0xFF93B25E), 5, tufts = true, flowers = intArrayOf(YEL, WHT, KIN))
    val ns = if (eu) 13.0 else 12.0; val mid = textMid(d.numeralFace, ns)
    for (r in 0 until d.rows) text(ROMAN.getOrElse(r + 1) { "" }, 5.0, d.rowY[r] + mid, d.numeralFace, ns, if (r + 1 == 7) KIN else hex(0xFF9A6A30), Paint.Align.LEFT)
    val root = byId[d.rootId]
    if (root != null) {
        val rx = root.x.toDouble(); val ry = root.y.toDouble()
        val trunk = pt(rx - 5, ry + 10, rx - 7, Hh - 20, rx - 14, Hh - 9, rx + 14, Hh - 9, rx + 7, Hh - 20, rx + 5, ry + 10)
        sh(trunk, BRN, 0.8, sm = true)
        inside(trunk, true) { alpha = 0.4; for (i in -3..3) ln(pt(rx + i * 1.8, ry + 12, rx + i * 2.6, Hh - 10), INK, 0.35, true) }
        for (s in intArrayOf(-1, 1)) for (r in 0 until 3) ln(pt(rx + s * (8 + r * 2), Hh - 10, rx + s * (16 + r * 7), Hh - 6 + r * 1.6, rx + s * (24 + r * 9), Hh - 4 + r * 2), BRN2, 1.3 - r * 0.3, true)
        if (!d.won) { dash(2.0, 1.6); ln(pt(rx, ry + 12, rx + 1.2, (ry + Hh) / 2, rx, Hh - 10), KIN, 0.9, true); noDash() }
    }
    val rnd = Rng(3); val leafCol = if (d.autumn) OCH else GRN
    for (e in d.edges) {
        val par = byId[e.from] ?: continue; val ch = byId[e.to] ?: continue
        val x0 = par.x.toDouble(); val y0 = par.y - 12.0; val x1 = ch.x.toDouble(); val y1 = ch.y + 12.0; val my = (y0 + y1) / 2
        val L = ch.liveLine; val th = if (e.main) 5.0 else if (L) 3.4 else 2.4
        fun bz() { beginPath(); moveTo(x0, y0); cubicTo(x0, my, x1, my, x1, y1) }
        bz(); stroke(INK, th + 1.5); stroke(if (L) BRN else hex(0xFFB8A890), th)
        if (e.main && !d.won) { dash(2.0, 1.6); stroke(KIN, 0.9); noDash() }
        if (L) for (t in doubleArrayOf(0.35, 0.65)) {
            val u = 1 - t
            val bx = u * u * u * x0 + 3 * u * u * t * x0 + 3 * u * t * t * x1 + t * t * t * x1
            val by = u * u * u * y0 + 3 * u * u * t * my + 3 * u * t * t * my + t * t * t * y1
            val sd = if (rnd.next() < 0.5) -1 else 1
            leaf(bx, by, if (sd < 0) PI + 0.5 else -0.5, 5.5, leafCol)
        }
    }
    val tf = d.labelFace; val tm = textMid(tf, 6.5); val sm = textMid(tf, 5.5)
    for (p in d.nodes) {
        val x = p.x.toDouble(); val y = p.y.toDouble(); val r = Rng(p.id * 977); val L = p.liveLine
        val n = if (L) (if (p.alive) 9 else 5) else 3
        for (i in 0 until n) {
            val a = r.next() * PI * 2; val s = 6 + r.next() * 2
            leaf(x + cos(a) * 14, y + sin(a) * 14, a, s, if (L) (if (d.autumn) OCH else if (i % 3 != 0) GRN else LGR) else hex(0xFFA07A48))
        }
        val isH = p.ring == Ring.HEAD
        if (isH) for (i in 0 until 5) { val a = -2.4 + i * 0.4; el(x + cos(a) * 19, y + sin(a) * 19, 1.4, 1.4, KIN, 0.4, n = 8) }
        nodeFrame(x, y, p.ring)
        portrait(p.fig, p.bg, p.faded, x - 12, y - 12, true); beginPath(); circle(x, y, 12.0); stroke(INK, 0.7)
        if (p.crowned) sh(pt(x - 5, y - 15, x - 5.6, y - 21, x - 2.6, y - 18, x, y - 22, x + 2.6, y - 18, x + 5.6, y - 21, x + 5, y - 15), YEL, 0.5)
        p.spouse?.let { alive -> beginPath(); circle(x + 11, y + 11, 2.6); stroke(INK, 1.8); stroke(if (alive) OCH else GRY, 0.9) }
        if (p.bast) { save(); beginPath(); circle(x, y, 12.0); clip(); ln(pt(x - 12, y + 2, x - 2, y + 12), INK, 2.4); ln(pt(x - 12, y + 2, x - 2, y + 12), KIN, 1.5); restore() }
        // labels: like the .tl block under each medallion (13px / 11px at 2 px per unit)
        val nc = if (!p.alive) C_DEAD else if (isH) C_HEAD else INK0
        val l1 = y + 16 + 6.825 / 2
        text(p.name, x, l1 + tm, tf, 6.5, nc, Paint.Align.CENTER, C_HALO)
        if (p.years.isNotEmpty()) text(p.years, x, l1 + 6.825 / 2 + 0.5 + 5.775 / 2 + sm, tf, 5.5, C_MUT, Paint.Align.CENTER, C_HALO)
    }
    paper(0.55)
}

private val INK0 = Ctx.INK0
