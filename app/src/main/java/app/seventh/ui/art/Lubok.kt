package app.seventh.ui.art

import android.graphics.RadialGradient
import android.graphics.Shader
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/* lubok art: flat colour, ink line, paper */

internal fun hex(v: Long) = v.toInt()
internal fun rgba(r: Int, g: Int, b: Int, a: Double) = ((a * 255 + 0.5).toInt() shl 24) or (r shl 16) or (g shl 8) or b

internal val WHT = hex(0xFFFBF5E6); internal val KIN = hex(0xFFC9372C); internal val KIN2 = hex(0xFF962A26)
internal val OCH = hex(0xFFE2A83A); internal val YEL = hex(0xFFF2D062); internal val GRN = hex(0xFF5A8F4A)
internal val GRN2 = hex(0xFF2F5D3A); internal val LGR = hex(0xFF9CC25E); internal val BLU = hex(0xFF3F6EA6)
internal val BRN = hex(0xFFA8733F); internal val BRN2 = hex(0xFF5E3B22); internal val GRY = hex(0xFF8F897C)
internal val WHITE = hex(0xFFFFFFFF)

internal const val D = -1.0 // default line width (LW)

internal class Pb(cap: Int = 32) {
    var a = DoubleArray(cap); var n = 0
    fun add(x: Double, y: Double): Pb { if (n + 2 > a.size) a = a.copyOf(a.size * 2); a[n++] = x; a[n++] = y; return this }
    fun arr(): DoubleArray = a.copyOf(n)
}

internal fun jr(v: Double) = floor(v + 0.5) // JS Math.round

internal inline fun Ctx.still(fn: () -> Unit) { val j = J; J = 0.0; try { fn() } finally { J = j } }

/** shape: colour printed slightly off the ink line, like a hand-coloured print */
internal fun Ctx.sh(p: DoubleArray, col: Int, lw: Double = D, off: Boolean = true, sm: Boolean = false, ink: Int = 0) {
    if (col != 0) { val o = off && REG; poly(p, true, sm, if (o) 0.5 else 0.0, if (o) 0.38 else 0.0); fill(col) }
    if (lw != 0.0) { poly(p, true, sm); stroke(if (ink != 0) ink else INK, if (lw < 0) LW else lw) }
}
internal fun ellP(x: Double, y: Double, rx: Double, ry: Double, n0: Int = 0): DoubleArray {
    val n = if (n0 > 0) n0 else max(10.0, min(26.0, jr((rx + ry) * 1.8))).toInt()
    val p = DoubleArray(n * 2)
    for (i in 0 until n) { val a = i.toDouble() / n * 6.2832; p[2 * i] = x + cos(a) * rx; p[2 * i + 1] = y + sin(a) * ry }
    return p
}
internal fun Ctx.el(x: Double, y: Double, rx: Double, ry: Double, col: Int, lw: Double = D, off: Boolean = true, n: Int = 0, ink: Int = 0) =
    sh(ellP(x, y, rx, ry, n), col, lw, off, true, ink)
internal fun Ctx.ln(p: DoubleArray, col: Int = 0, w: Double = D, sm: Boolean = false) {
    poly(p, false, sm); stroke(if (col != 0) col else INK, if (w <= 0) LW else w)
}
internal fun Ctx.quad(x: Double, y: Double, w: Double, h: Double, col: Int, lw: Double = D, off: Boolean = true) =
    sh(pt(x, y, x + w, y, x + w, y + h, x, y + h), col, lw, off)
internal inline fun Ctx.inside(p: DoubleArray, sm: Boolean = false, fn: () -> Unit) { save(); poly(p, true, sm); clip(); try { fn() } finally { restore() } }
internal fun Ctx.blobs(cs: DoubleArray, col: Int, lw0: Double = D, sq: Double = 1.0) {
    val lw = if (lw0 > 0) lw0 else LW
    var i = 0
    while (i < cs.size) { poly(ellP(cs[i], cs[i + 1], cs[i + 2], cs[i + 2] * sq), true, true); stroke(INK, lw * 2); i += 3 }
    i = 0
    while (i < cs.size) { poly(ellP(cs[i], cs[i + 1], cs[i + 2], cs[i + 2] * sq), true, true); fill(col); i += 3 }
}
internal fun Ctx.hatch(p: DoubleArray, sm: Boolean = false, sp: Double = 1.5, sl: Double = 0.7, col: Int = 0, a: Double = 0.4, w: Double = 0.35) {
    save(); poly(p, true, sm); clip()
    var x0 = 1e9; var x1 = -1e9; var y0 = 1e9; var y1 = -1e9
    for (i in 0 until p.size / 2) { x0 = min(x0, p[2 * i]); x1 = max(x1, p[2 * i]); y0 = min(y0, p[2 * i + 1]); y1 = max(y1, p[2 * i + 1]) }
    val dy = y1 - y0 + 2
    alpha = a; beginPath()
    var x = x0 - dy * sl - 2
    while (x < x1 + 2) { moveTo(x, y1 + 1); lineTo(x + dy * sl, y0 - 1); x += sp }
    stroke(if (col != 0) col else INK, w); restore()
}
internal fun Ctx.frame(W: Double, H: Double) {
    strokeRect(0.9, 0.9, W - 1.8, H - 1.8, INK, 1.1); strokeRect(2.6, 2.6, W - 5.2, H - 5.2, INK, 0.45)
    for ((x, y) in listOf(2.6 to 2.6, W - 2.6 to 2.6, 2.6 to H - 2.6, W - 2.6 to H - 2.6)) {
        dot(x, y, 1.4, KIN); beginPath(); circle(x, y, 1.4); stroke(INK, 0.4)
    }
}

/* sky, weather, land */
internal val SKY = mapOf(
    "day" to intArrayOf(hex(0xFFF3E8C4), hex(0xFFD9E4D2), hex(0xFFB4CFD6)),
    "gray" to intArrayOf(hex(0xFFDCD5BD), hex(0xFFC8C3AE), hex(0xFFB0AD9C)),
    "dusk" to intArrayOf(hex(0xFFF2C27E), hex(0xFFE09276), hex(0xFF9A6680)),
    "night" to intArrayOf(hex(0xFF7884B8), hex(0xFF5A66A0), hex(0xFF454F88)),
    "dawn" to intArrayOf(hex(0xFFF7DC9C), hex(0xFFEEAB8C), hex(0xFFA685A6)),
)
internal fun Ctx.sky(W: Double, hz: Double, k: String, f: Double) {
    val s = SKY.getValue(k); fillRect(0.0, 0.0, W, hz + 3, s[2])
    for (i in 0..1) {
        val y0 = hz * (0.34 + i * 0.3); val p = Pb()
        var x = -8.0; while (x <= W + 8) { p.add(x, y0 + sin(x * 0.06 + i * 2.1) * 1.5); x += 8 }
        p.add(W + 8, hz + 6).add(-8.0, hz + 6)
        poly(p.arr(), true, true); fill(s[1 - i])
    }
    if (k == "day") sun(W - 24, 15.0, 6.5, f, YEL)
    if (k == "dusk") sun(34.0, hz - 3, 7.0, f, hex(0xFFF0A050))
    if (k == "dawn") sun(W - 40, hz - 2, 7.0, f, YEL)
    if (k != "night") {
        val cc = when (k) { "gray" -> hex(0xFFE6E1D2); "dusk" -> hex(0xFFF4CFB0); "dawn" -> hex(0xFFFBE8CC); else -> WHT }
        for (i in 0..2) {
            val sp = 1.1 + i * 0.5; val x = ((i * 67 + f * sp) % (W + 50)) - 25; val y = 9.0 + i * 7 + (i % 2) * 3
            save(); translate(x, y); cloud(1 + (i % 2) * 0.3, cc); restore()
        }
    }
}
internal fun Ctx.sun(x: Double, y: Double, r: Double, f: Double, col: Int) {
    save(); translate(x, y); rotate(f * 0.1)
    for (i in 0 until 12) { rotate(PI / 6); sh(pt(r + 0.4, -1.3, r + 3.8 + (i % 2) * 1.6, 0.0, r + 0.4, 1.3), if (i % 2 == 1) KIN else col, 0.45) }
    restore()
    el(x, y, r, r, col, 0.7); dot(x - r * 0.35, y - r * 0.12, 0.55, INK); dot(x + r * 0.35, y - r * 0.12, 0.55, INK)
    dot(x - r * 0.55, y + r * 0.3, r * 0.17, rgba(201, 55, 44, 0.5)); dot(x + r * 0.55, y + r * 0.3, r * 0.17, rgba(201, 55, 44, 0.5))
    ln(pt(x - r * 0.28, y + r * 0.36, x, y + r * 0.5, x + r * 0.28, y + r * 0.36), INK, 0.45, true)
}
internal fun Ctx.cloud(s: Double, col: Int) {
    blobs(pt(0.0, 0.0, 3 * s, 4.2 * s, -1.8 * s, 3.8 * s, 8.6 * s, 0.0, 3.2 * s, 4.4 * s, 0.9 * s, 3 * s), col, 0.5, 0.8)
    fade(0.4) { ln(pt(2 * s, 0.4 * s, 3.4 * s, -0.6 * s, 5 * s, -0.2 * s), INK, 0.35, true) }
}
internal fun Ctx.stars(W: Double, hz: Double, f: Double) {
    val r = Rng(12)
    for (i in 0 until 22) {
        val x = r.next() * W; val y = 4 + r.next() * hz * 0.75; val s = 0.7 + r.next() * 0.8; val tw = 0.7 + 0.3 * sin(f * 1.6 + i * 1.7)
        save(); translate(x, y); scale(tw, tw)
        sh(pt(0.0, -s * 1.7, s * 0.35, -s * 0.35, s * 1.7, 0.0, s * 0.35, s * 0.35, 0.0, s * 1.7, -s * 0.35, s * 0.35, -s * 1.7, 0.0, -s * 0.35, -s * 0.35), YEL, 0.3, false)
        restore()
    }
    moon(W - 24, 15.0, 6.0)
}
internal fun Ctx.moon(x: Double, y: Double, r: Double) {
    save(); beginPath(); rect(x - r - 3, y - r - 3, 2 * r + 6, 2 * r + 6); circle(x + r * 0.55, y - r * 0.25, r * 0.82); clip(true)
    el(x, y, r, r, YEL, off = false); restore()
    save(); beginPath(); circle(x, y, r); clip(); beginPath(); circle(x + r * 0.55, y - r * 0.25, r * 0.82); stroke(INK, LW); restore()
    dot(x - r * 0.55, y - r * 0.12, 0.5, INK); dot(x - r * 0.62, y + r * 0.38, r * 0.13, rgba(201, 55, 44, 0.55))
}
/** lights: x, y, r, intensity, cold (0/1) per light */
internal fun Ctx.nightWash(W: Double, H: Double, amb: Double, lights: DoubleArray) {
    save(); mode = Ctx.MUL
    fillRect(0.0, 0.0, W, H, rgba(jr(70 + amb * 150).toInt(), jr(76 + amb * 140).toInt(), jr(130 + amb * 110).toInt(), 1.0))
    mode = Ctx.ADD
    var i = 0
    while (i < lights.size) {
        val x = lights[i]; val y = lights[i + 1]; val r = lights[i + 2]; val k = lights[i + 3]; val cold = lights[i + 4] > 0
        val (cr, cg, cb) = if (cold) Triple(140, 160, 220) else Triple(255, 165, 60)
        val g = RadialGradient(x.toFloat(), y.toFloat(), r.toFloat(),
            intArrayOf(rgba(cr, cg, cb, min(1.0, 0.42 * k)), rgba(cr, cg, cb, 0.12 * k), rgba(cr, cg, cb, 0.0)), floatArrayOf(0f, 0.6f, 1f), Shader.TileMode.CLAMP)
        fillShader(g, x - r, y - r, r * 2, r * 2); i += 5
    }
    restore()
}
internal fun Ctx.snowfall(W: Double, H: Double, f: Double, n: Int) {
    val r = Rng(77)
    for (i in 0 until n) { val bx = r.next() * W; val by = r.next() * H; val sp = 3 + r.next() * 4; dot(bx + sin(f * 1.1 + i) * 2, (by + f * sp) % H, 0.55 + r.next() * 0.4, WHITE) }
}
internal fun Ctx.hill(W: Double, base: Double, col: Int, seed: Int, amp: Double, freq: Double) {
    val r = Rng(seed); val p1 = r.next() * 6; val p2 = r.next() * 6; val p = Pb()
    var x = -6.0; while (x <= W + 6) { p.add(x, base - amp * (0.5 + 0.5 * sin(x * freq + p1)) * (0.75 + 0.25 * sin(x * freq * 2.7 + p2))); x += 5 }
    val pts = p.arr()
    poly(p.add(W + 6, base + 30).add(-6.0, base + 30).arr(), true, true); fill(col); ln(pts, INK, 0.55, true)
    fade(0.35) { var i = 1; while (i < pts.size / 2) { val px = pts[2 * i]; val py = pts[2 * i + 1]; ln(pt(px - 1, py + 2.6, px, py + 1.4, px + 1, py + 2.6), INK, 0.3); i += 3 } }
}
internal fun Ctx.treeline(W: Double, y: Double, col: Int, seed: Int) {
    val r = Rng(seed); val cs = Pb(96)
    var x = -4.0; while (x < W + 6) { cs.add(x, y - 1.5 - r.next() * 2.5); cs.addR(2.4 + r.next() * 1.6); x += 4.5 }
    fillRect(-2.0, y - 2, W + 4, 6.0, col); blobs(cs.arr(), col, 0.45)
}
internal fun Pb.addR(v: Double): Pb { if (n + 1 > a.size) a = a.copyOf(a.size * 2); a[n++] = v; return this }
internal fun Ctx.ground(W: Double, H: Double, y0: Double, col: Int, seed: Int, snow: Boolean = false, tufts: Boolean = false, flowers: IntArray? = null) {
    val r = Rng(seed); val p = Pb()
    var x = -6.0; while (x <= W + 6) { p.add(x, y0 + sin(x * 0.05 + seed) * 1.2); x += 6 }
    val pts = p.arr()
    poly(p.add(W + 6, H + 3).add(-6.0, H + 3).arr(), true, false); fill(col); ln(pts, INK, 0.6, true)
    val n = jr(W * (H - y0) / 70).toInt()
    fade(0.42) {
        for (i in 0 until n) {
            val gx = r.next() * W; val gy = y0 + 3 + r.next() * (H - y0 - 3)
            if (snow) ln(pt(gx - 1.4, gy, gx + 1.4, gy), hex(0xFF7A8CB0), 0.4)
            else if (tufts) { ln(pt(gx - 1.2, gy, gx - 0.4, gy - 1.8), INK, 0.35); ln(pt(gx, gy, gx, gy - 2.2), INK, 0.35); ln(pt(gx + 1.2, gy, gx + 0.4, gy - 1.8), INK, 0.35) }
            else dot(gx, gy, 0.4, INK)
        }
    }
    if (flowers != null) {
        var i = 0
        while (i < n / 3.0) {
            val fx = r.next() * W; val fy = y0 + 5 + r.next() * (H - y0 - 5); val fc = flowers[i % flowers.size]
            for (k in 0 until 5) { val a = k / 5.0 * 6.28; dot(fx + cos(a) * 0.9, fy + sin(a) * 0.9, 0.62, fc) }
            dot(fx, fy, 0.42, INK); i++
        }
    }
}
internal fun Ctx.field(W: Double, H: Double, y0: Double, dry: Boolean, f: Double) {
    ground(W, H, y0, if (dry) hex(0xFFC9B27C) else hex(0xFFE3B64A), 31)
    fade(0.3) { for (i in -7..7) ln(pt(W / 2 + i * 6, y0 + 1, W / 2 + i * 30, H + 2), INK, 0.4) }
    if (dry) {
        val r = Rng(4)
        fade(0.55) { repeat(16) { val x = r.next() * W; val y = y0 + 6 + r.next() * (H - y0 - 6); ln(pt(x, y, x + 2, y + 1, x + 3, y - 0.5, x + 5, y + 0.5), INK, 0.35) } }
        return
    }
    still {
        for (i in 0 until 22) {
            val x = i * 8.4 + 2; val b = H + 1; val h = 10.0 + (i % 3) * 2; val sw = sin(f * 1.4 + i * 0.7) * 1.3
            ln(pt(x, b, x + sw * 0.5, b - h * 0.6, x + sw, b - h), hex(0xFF8A6420), 0.5, true)
            for (j in 0 until 4) { val t = 0.72 + j * 0.09; el(x + sw * t, b - h * t - 1, 0.7, 1.3, YEL, 0.35, false, 8) }
        }
    }
}
internal fun Ctx.sheaf(x: Double, y: Double) {
    sh(pt(x - 3, y, x - 1.4, y - 6, x - 3.4, y - 12, x + 3.4, y - 12, x + 1.4, y - 6, x + 3, y), hex(0xFFE3B64A), 0.5)
    fade(0.55) { for (i in -2..2) ln(pt(x + i * 0.7, y - 0.4, x + i * 1.4, y - 11.6), INK, 0.3) }
    quad(x - 1.8, y - 7, 3.6, 1.4, KIN, 0.35)
}
internal fun Ctx.pine(x: Double, y: Double, h: Double, snow: Boolean = false) {
    quad(x - 1, y - h * 0.2, 2.0, h * 0.2, BRN2, 0.45)
    for (i in 0 until 4) {
        val t = i / 4.0; val yb = y - h * 0.14 - t * h * 0.76; val w = h * 0.3 * (1 - t * 0.72); val th = h * 0.34
        sh(pt(x - w, yb, x - w * 0.32, yb - th * 0.52, x, yb - th, x + w * 0.32, yb - th * 0.52, x + w, yb, x, yb - 1.2), GRN2)
        if (snow) sh(pt(x - w * 0.6, yb - th * 0.4, x - w * 0.3, yb - th * 0.62, x, yb - th, x + w * 0.3, yb - th * 0.62, x + w * 0.6, yb - th * 0.4, x, yb - th * 0.5), WHT, 0.4)
    }
}
internal fun Ctx.birch(x: Double, y: Double, h: Double, s: String) {
    val tw = 1.6
    sh(pt(x - tw, y, x - tw * 0.55, y - h, x + tw * 0.55, y - h, x + tw, y), WHT)
    for (i in 0 until 7) { val yy = y - 3 - i * h / 8; val dx = if (i % 2 == 1) 0.2 else -1.2; ln(pt(x + dx, yy, x + dx + 1.1, yy - 0.2), INK, 0.6) }
    ln(pt(x, y - h * 0.58, x - h * 0.2, y - h * 0.84), INK, 0.55); ln(pt(x, y - h * 0.7, x + h * 0.22, y - h * 0.92), INK, 0.55)
    if (s == "winter") return
    val col = if (s == "autumn") OCH else if (s == "spring") LGR else GRN
    val r = Rng(jr(x * 7 + h).toInt()); val cs = DoubleArray(21)
    for (i in 0 until 7) { cs[3 * i] = x + (r.next() - 0.5) * h * 0.5; cs[3 * i + 1] = y - h * 0.62 - r.next() * h * 0.42; cs[3 * i + 2] = h * 0.11 + r.next() * h * 0.06 }
    blobs(cs, col)
    fade(0.5) { for (i in 0 until 7) { val cx = cs[3 * i]; val cy = cs[3 * i + 1]; val q = cs[3 * i + 2]; ln(pt(cx - q * 0.4, cy + q * 0.2, cx, cy - q * 0.3, cx + q * 0.4, cy + q * 0.2), INK, 0.35, true) } }
}
internal fun Ctx.bareTree(x: Double, y: Double, h: Double) {
    sh(pt(x - 2, y, x - 1, y - h * 0.6, x + 1, y - h * 0.6, x + 2, y), BRN2)
    fun br(x0: Double, y0: Double, a: Double, l: Double, d: Int) {
        if (d > 3) return
        val x1 = x0 + cos(a) * l; val y1 = y0 + sin(a) * l
        ln(pt(x0, y0, x1, y1), INK, 1.4 - d * 0.3); br(x1, y1, a - 0.45, l * 0.68, d + 1); br(x1, y1, a + 0.42, l * 0.66, d + 1)
    }
    br(x, y - h * 0.55, -PI / 2, h * 0.32, 0)
}
internal fun Ctx.bush(x: Double, y: Double, r: Double, col: Int = GRN) {
    blobs(pt(x - r * 0.6, y - r * 0.5, r * 0.6, x, y - r * 0.9, r * 0.7, x + r * 0.6, y - r * 0.5, r * 0.6), col, 0.5)
    for (i in 0 until 4) dot(x - r * 0.6 + i * r * 0.4, y - r * 0.7 + (i % 2) * r * 0.3, 0.6, KIN)
}

/* buildings */
internal fun Ctx.win(cx: Double, cy: Double, w: Double, h: Double, lit: Boolean = false, shutter: Int = 0, frame: Int = WHT, glass: Int = 0) {
    val fr = frame
    if (shutter != 0) {
        quad(cx - w / 2 - 1 - w * 0.45, cy - h / 2, w * 0.45, h, shutter, 0.45); quad(cx + w / 2 + 1, cy - h / 2, w * 0.45, h, shutter, 0.45)
        for (sx in doubleArrayOf(cx - w / 2 - 1 - w * 0.225, cx + w / 2 + 1 + w * 0.225)) sh(pt(sx, cy - 1.4, sx + 1, cy, sx, cy + 1.4, sx - 1, cy), WHT, 0.3, false)
    }
    sh(pt(cx - w / 2 - 1.6, cy - h / 2, cx - w / 2 - 1, cy - h / 2 - 2, cx, cy - h / 2 - 4.4, cx + w / 2 + 1, cy - h / 2 - 2, cx + w / 2 + 1.6, cy - h / 2), fr, 0.5); dot(cx, cy - h / 2 - 2, 0.7, KIN)
    quad(cx - w / 2 - 1, cy - h / 2, w + 2, h + 1.2, fr, 0.5)
    quad(cx - w / 2, cy - h / 2 + 0.8, w, h - 0.8, if (lit) YEL else if (glass != 0) glass else hex(0xFF3C5A86), 0.45, false)
    ln(pt(cx, cy - h / 2 + 0.8, cx, cy + h / 2), INK, 0.45); ln(pt(cx - w / 2, cy + 0.2, cx + w / 2, cy + 0.2), INK, 0.45)
    quad(cx - w / 2 - 2, cy + h / 2 + 0.4, w + 4, 1.6, fr, 0.45); for (i in 0 until 4) dot(cx - w / 2 + i * w / 3, cy + h / 2 + 2.9, 0.5, KIN)
}
internal fun Ctx.izba(
    x: Double, y: Double, w: Double, f: Double = 0.0, lit: Boolean = false, shutter: Int = 0, plank: Boolean = false,
    chimney: Boolean = false, snow: Boolean = false, smoky: Boolean = false, dark: Boolean = false,
) {
    val wh = w * 0.46; val L = x - w / 2; val T = y - wh; val ov = w * 0.08; val rh = w * 0.5
    val wall = if (dark) hex(0xFF7D6C5A) else hex(0xFFB47C46)
    val roofC = if (dark) hex(0xFF6F6658) else if (plank) hex(0xFF8A5A34) else hex(0xFFD9AA4C)
    val ends = if (dark) hex(0xFFA39480) else hex(0xFFDCB071)
    val chx = x + w * 0.24; val chy = T - rh * (1 - (w * 0.24) / (w / 2 + ov)) + 1
    if (chimney) quad(chx - 2, chy - 7, 4.0, 9.0, hex(0xFFB8483A), 0.5)
    quad(L, T, w, wh, wall)
    fade(0.7) { var yy = T + 2.8; while (yy < y - 0.5) { ln(pt(L + 0.3, yy, L + w - 0.3, yy), INK, 0.38); yy += 2.8 } }
    var yy = T + 1.4
    while (yy < y) { el(L - 0.6, yy, 1.2, 1.3, ends, 0.35, false, 8); el(L + w + 0.6, yy, 1.2, 1.3, ends, 0.35, false, 8); yy += 2.8 }
    val nw = if (w > 50) 2 else 1
    for (i in 0 until nw) win(if (nw == 1) x else x + (if (i == 1) 1 else -1) * w * 0.22, T + wh * 0.5, w * 0.15, wh * 0.34, lit,
        if (shutter != 0) shutter else if (dark) 0 else KIN2, if (dark) hex(0xFFCFC4B0) else WHT)
    val R = pt(L - ov, T + 0.5, x, T - rh, L + w + ov, T + 0.5); sh(R, roofC)
    inside(R) {
        alpha = 0.42
        if (plank) for (i in -14..14) ln(pt(x + i * 1.2, T - rh, x + i * w * 0.08, T + 1), INK, 0.35)
        else {
            for (i in -16..16) ln(pt(x + i * 0.6, T - rh + 1, x + i * w * 0.07, T + 1), INK, 0.3)
            for (k in 1 until 4) { val y2 = T - rh + k * rh / 4 + 1; ln(pt(L - ov, y2 + rh * 0.12, x, y2 - 1, L + w + ov, y2 + rh * 0.12), INK, 0.3, true) }
        }
    }
    val gw = w * 0.34; val gh = rh * 0.7; val G3 = pt(x - gw, T, x, T - gh, x + gw, T); sh(G3, wall, 0.5)
    inside(G3) { alpha = 0.55; for (i in -8..8) ln(pt(x + i * 1.8, T - gh, x + i * 1.8, T), INK, 0.3) }
    el(x, T - gh * 0.36, w * 0.045, w * 0.055, if (lit) YEL else hex(0xFF3C5A86), 0.45)
    for (s in intArrayOf(-1, 1)) {
        sh(pt(x + s * (gw + 1.6), T + 0.4, x, T - gh - 1.8, x, T - gh + 0.2, x + s * gw, T + 0.4), WHT, 0.45)
        var t = 0.15; while (t < 0.95) { dot(x + s * (gw + 0.8) * (1 - t), T - (gh + 0.8) * t + 0.3, 0.42, KIN); t += 0.17 }
    }
    sh(pt(x - 0.9, T - gh, x + 0.9, T - gh, x + 0.9, T - gh + 5, x, T - gh + 6.2, x - 0.9, T - gh + 5), WHT, 0.4); dot(x, T - gh + 3, 0.5, KIN)
    sh(pt(x - 1, T - rh - 0.3, x - 0.2, T - rh - 3, x + 1.8, T - rh - 3.4, x + 1.2, T - rh - 2.2, x + 0.8, T - rh - 0.3), if (dark) hex(0xFF6F6658) else BRN2, 0.4)
    if (snow) {
        val p = Pb(64).add(L - ov - 0.6, T + 0.9).add(x, T - rh - 0.9).add(L + w + ov + 0.6, T + 0.9)
        var t = 1.0; while (t >= -0.001) { p.add(x + (w / 2 + ov) * t, T - rh * (1 - t) + 2.3 + sin(t * 23) * 0.7); t -= 0.1 }
        t = 0.1; while (t <= 1.001) { p.add(x - (w / 2 + ov) * t, T - rh * (1 - t) + 2.3 + sin(t * 19) * 0.7); t += 0.1 }
        sh(p.arr(), WHT, 0.5); if (chimney) el(chx, chy - 7, 2.4, 0.9, WHT, 0.35, false)
    }
    if (chimney) smoke(chx, chy - 8, f, 5)
    if (smoky) smoke(x + w * 0.1, T - rh * 0.6, f, 6, true)
}
internal fun Ctx.smoke(x: Double, y: Double, f: Double, n: Int = 5, dark: Boolean = false) {
    still {
        for (i in 0 until n) {
            val ph = (f * 0.2 + i.toDouble() / n) % 1; val yy = y - ph * 30; val xx = x + sin(ph * 5 + i) * 2.4 + ph * 6; val r = 1.3 + ph * 3.4
            val o = alpha; alpha = (1 - ph) * 0.85
            el(xx, yy, r, r * 0.8, if (dark) hex(0xFF6A645C) else hex(0xFFECE6D8), 0.4, false, 12)
            alpha *= 0.6; ln(pt(xx - r * 0.4, yy, xx, yy - r * 0.3, xx + r * 0.3, yy + 0.1), INK, 0.3, true); alpha = o
        }
    }
}
internal fun Ctx.barn(x: Double, y: Double, w: Double, snow: Boolean = false) {
    val h = w * 0.62; val L = x - w / 2; val T = y - h
    quad(L, T, w, h, hex(0xFF8F5E34))
    fade(0.55) { var i = 1; while (i < w / 2.5) { ln(pt(L + i * 2.5, T + 0.5, L + i * 2.5, y - 0.3), INK, 0.3); i++ } }
    quad(x - w * 0.22, y - h * 0.7, w * 0.44, h * 0.7, hex(0xFFB07A42), 0.5)
    ln(pt(x - w * 0.22, y - h * 0.7, x + w * 0.22, y), INK, 0.5); ln(pt(x + w * 0.22, y - h * 0.7, x - w * 0.22, y), INK, 0.5)
    sh(pt(L - 2, T + 0.5, L + w * 0.15, T - h * 0.42, L + w * 0.85, T - h * 0.42, L + w + 2, T + 0.5), if (snow) WHT else hex(0xFF6E4A2A))
}
internal fun Ctx.fence(x0: Double, x1: Double, y: Double, snow: Boolean = false) {
    var x = x0 + 2
    while (x < x1) { quad(x - 0.6, y - 10, 1.2, 10.5, BRN2, 0.35); if (snow) el(x, y - 10.2, 1.1, 0.6, WHT, 0.3, false); x += 6 }
    for (k in 0 until 3) {
        val yy = y - 2.6 - k * 2.6; val top = Pb(); val bot = Pb()
        var xx = x0; while (xx <= x1 + 0.1) { val s = sin(xx * 1.05 + k * 1.3) * 0.6; top.add(xx, yy - 1 + s); bot.add(xx, yy + 1 + s); xx += 3 }
        sh(cat(top, bot), if (snow && k == 2) WHT else hex(0xFFB88C55), 0.4)
    }
}
/** top followed by bot reversed */
internal fun cat(top: Pb, bot: Pb): DoubleArray {
    val r = DoubleArray(top.n + bot.n); System.arraycopy(top.a, 0, r, 0, top.n)
    var j = top.n; var i = bot.n - 2
    while (i >= 0) { r[j++] = bot.a[i]; r[j++] = bot.a[i + 1]; i -= 2 }
    return r
}
internal fun Ctx.well(x: Double, y: Double) {
    quad(x + 7.4, y - 20, 1.4, 20.0, BRN2, 0.4); quad(x - 5, y - 6, 10.0, 6.0, BRN)
    fade(0.7) { ln(pt(x - 5, y - 3, x + 5, y - 3), INK, 0.35) }
    el(x, y - 6, 5.0, 1.2, hex(0xFF2A2018), 0.4, false)
    ln(pt(x - 9, y - 15, x + 22, y - 25), INK, 1.5); ln(pt(x - 9, y - 15, x + 22, y - 25), BRN, 0.7)
    ln(pt(x - 8, y - 15, x - 8, y - 9.6), INK, 0.35); quad(x - 9.5, y - 10, 3.0, 3.0, BRN2, 0.35)
}
internal fun Ctx.onion(x: Double, y: Double, r: Double, col: Int) =
    sh(pt(x - r * 0.75, y, x - r * 1.2, y - r * 0.8, x - r * 0.95, y - r * 1.55, x - r * 0.3, y - r * 2.05, x, y - r * 2.6, x + r * 0.3, y - r * 2.05, x + r * 0.95, y - r * 1.55, x + r * 1.2, y - r * 0.8, x + r * 0.75, y), col, 0.55, sm = true)
internal fun Ctx.crossT(x: Double, y: Double, s: Double) {
    ln(pt(x, y, x, y - s * 3.2), INK, 0.8); ln(pt(x - s, y - s * 2.3, x + s, y - s * 2.3), INK, 0.7); ln(pt(x - s * 0.6, y - s * 1.1, x + s * 0.6, y - s * 0.7), INK, 0.55)
}
internal fun Ctx.church(x: Double, y: Double, s: Double) {
    save(); translate(x, y); scale(s, s)
    quad(-15.0, -24.0, 30.0, 24.0, WHT)
    for (wx in doubleArrayOf(-9.0, 9.0)) sh(pt(wx - 2, -7.0, wx - 2, -15.0, wx, -17.5, wx + 2, -15.0, wx + 2, -7.0), hex(0xFF3A4F78), 0.5)
    sh(pt(-3.5, 0.0, -3.5, -9.0, 0.0, -12.0, 3.5, -9.0, 3.5, 0.0), hex(0xFF7A4A2A), 0.6)
    for (zx in doubleArrayOf(-10.0, 0.0, 10.0)) sh(pt(zx - 5, -24.0, zx - 5, -26.0, zx, -30.0, zx + 5, -26.0, zx + 5, -24.0), GRN)
    for ((dx, dr, dc) in listOf(Triple(-10.0, 3.2, BLU), Triple(10.0, 3.2, BLU), Triple(0.0, 4.6, OCH))) {
        val dy = if (dx != 0.0) -28.0 else -31.0
        quad(dx - dr * 0.6, dy - 6, dr * 1.2, 6.5, WHT, 0.5); onion(dx, dy - 6, dr, dc)
        if (dc == BLU) for (i in 0 until 3) dot(dx - 1.3 + i * 1.3, dy - 6 - dr * (0.8 + (i % 2) * 0.5), 0.45, YEL)
        crossT(dx, dy - 6 - dr * 2.6, dr * 0.55)
    }
    restore()
}
internal fun Ctx.manor(x: Double, y: Double, w: Double, lit: Boolean = false) {
    val h = w * 0.34; val L = x - w / 2; val T = y - h
    sh(pt(L - 3, T + 0.5, L + 4, T - h * 0.45, L + w - 4, T - h * 0.45, L + w + 3, T + 0.5), hex(0xFF5F8F5A)); quad(L, T, w, h, hex(0xFFEFCF7A))
    val nw = jr(w / 11).toInt()
    for (i in 0 until nw) {
        val wx = L + 5 + i * (w - 10) / (nw - 1); if (abs(wx - x) < w * 0.17) continue
        for (wy in doubleArrayOf(T + h * 0.2, T + h * 0.58)) { quad(wx - 1.8, wy, 3.6, h * 0.26, if (lit) YEL else hex(0xFF3C5A86), 0.45); ln(pt(wx, wy, wx, wy + h * 0.26), INK, 0.3) }
    }
    sh(pt(x - w * 0.18, T + 1, x, T - h * 0.48, x + w * 0.18, T + 1), WHT); el(x, T - h * 0.14, 1.6, 1.3, OCH, 0.4, false)
    for (i in 0 until 4) { val cx = x - w * 0.13 + i * w * 0.0867; quad(cx - 1, T + 1, 2.0, h - 2, WHT, 0.45) }
    quad(x - w * 0.2, y - 2, w * 0.4, 2.0, hex(0xFFD8D0C0), 0.4)
}

/* fire, beasts, things */
internal fun Ctx.tongue(x: Double, y: Double, w: Double, h: Double, sw: Double, col: Int) =
    sh(pt(x - w, y, x - w * 0.9, y - h * 0.35, x - w * 0.3 + sw * 0.4, y - h * 0.65, x + sw, y - h, x + w * 0.35 + sw * 0.3, y - h * 0.55, x + w * 0.85, y - h * 0.3, x + w, y), col, 0.5, false, true)
internal fun Ctx.flames(x: Double, y: Double, w: Double, h: Double, f: Double) {
    still {
        for (i in intArrayOf(-1, 1, 0)) {
            val hh = h * (if (i != 0) 0.7 else 1.0) * (0.85 + 0.15 * sin(f * 6 + i * 2 + x)); val sw = sin(f * 4.5 + i * 1.7 + x * 0.3) * w * 0.35; val xx = x + i * w * 0.55
            tongue(xx, y, w * 0.62, hh, sw, KIN); tongue(xx, y, w * 0.34, hh * 0.62, sw * 0.7, YEL)
        }
        for (k in 0 until 3) { val ph = (f * 0.8 + k / 3.0 + x * 0.013) % 1; dot(x + sin(ph * 9 + k) * w, y - h - ph * h * 1.2, 0.7 * (1 - ph) + 0.2, YEL) }
    }
}
internal fun Ctx.bear(x: Double, y: Double, f: Double, fur: Int = hex(0xFF7A4A26), belly: Int = hex(0xFFA8703C), snout: Int = hex(0xFFB07A48)) {
    save(); translate(x, y)
    sh(pt(-2.0, -9.0, -4.0, 0.0, 1.0, 0.0, 2.0, -6.0), fur); sh(pt(3.0, -9.0, 4.0, 0.0, 9.0, 0.0, 8.0, -7.0), fur)
    translate(0.0, sin(f * 1.8) * 0.6)
    sh(pt(-6.0, -10.0, -8.0, -20.0, -5.0, -28.0, 2.0, -30.0, 8.0, -24.0, 9.0, -12.0, 4.0, -7.0), fur, sm = true)
    hatch(pt(2.0, -30.0, 8.0, -24.0, 9.0, -12.0, 4.0, -7.0, 3.0, -20.0), sm = true, a = 0.3)
    el(0.0, -17.0, 3.4, 6.0, belly, 0.4)
    sh(pt(-5.0, -26.0, -11.0, -31.0, -13.4, -29.4, -8.0, -22.6), fur, sm = true)
    for (i in 0 until 3) ln(pt(-13.2 + i * 0.9, -31 + i * 0.7, -15 + i * 0.9, -31.4 + i * 0.7), WHT, 0.45)
    el(-1.0, -35.6, 1.4, 1.4, fur, 0.45); el(-5.0, -35.8, 1.4, 1.4, fur, 0.45)
    el(-3.0, -32.0, 4.6, 4.0, fur); el(-6.8, -30.8, 2.6, 1.7, snout, 0.5)
    dot(-9.0, -31.2, 0.7, INK); dot(-4.6, -33.0, 0.6, INK); ln(pt(-8.2, -29.5, -6.4, -29.7), KIN, 0.5); restore()
}
internal fun Ctx.wolf(x: Double, y: Double, f: Double, flip: Boolean, eyesOnly: Boolean = false) {
    save(); translate(x, y); if (flip) scale(-1.0, 1.0)
    val g = hex(0xFF8E8A84); val h = sin(f * 1.3 + x) * 0.4
    if (eyesOnly) {
        translate(0.0, h); mode = Ctx.ADD; dot(-6.0, -15.8, 1.8, rgba(255, 210, 80, 0.35)); mode = Ctx.SRC; dot(-6.0, -15.8, 0.6, YEL); restore(); return
    }
    sh(pt(6.0, -1.0, 12.0, -3.0, 13.0, -6.0, 10.0, -5.0, 7.0, -4.0), g, sm = true)
    sh(pt(-3.0, 0.0, -4.0, -8.0, -2.0, -14.0, 3.0, -13.0, 7.0, -7.0, 8.0, 0.0), g, sm = true)
    hatch(pt(3.0, -13.0, 7.0, -7.0, 8.0, 0.0, 3.0, 0.0), a = 0.3); ln(pt(-2.0, 0.0, -2.5, -6.0), INK, 0.4)
    translate(0.0, h)
    sh(pt(-3.6, -16.4, -3.0, -19.6, -1.6, -16.5), g, 0.45); sh(pt(-2.0, -16.0, -0.6, -18.8, 0.0, -15.4), g, 0.45)
    sh(pt(-1.6, -13.0, -3.6, -17.0, -9.0, -16.4, -10.8, -15.2, -7.0, -14.0, -3.0, -11.4), g)
    dot(-6.0, -15.8, 0.6, INK); dot(-10.4, -15.4, 0.5, INK); restore()
}
internal fun Ctx.chicken(x: Double, y: Double, f: Double, flip: Boolean = false) {
    val pk = ((sin(f * 2 + x) - 0.55) / 0.45).coerceIn(0.0, 1.0) * 1.6
    save(); translate(x, y); if (flip) scale(-1.0, 1.0)
    ln(pt(-1.0, -3.0, -1.4, 0.0), OCH, 0.55); ln(pt(1.0, -3.0, 1.4, 0.0), OCH, 0.55)
    for ((col, a) in listOf(GRN2 to -0.9, BLU to -0.5, KIN to -0.1)) sh(pt(-2.0, -5.0, -6 + a * 2, -10 + a * 1.5, -7.5 + a, -7 + a, -4.0, -4.0), col, 0.45, sm = true)
    el(0.0, -5.0, 3.6, 2.6, WHT, 0.5); dot(-1.0, -5.2, 0.5, KIN); dot(0.6, -4.4, 0.5, KIN)
    translate(pk * 0.6, pk); el(3.0, -8.0, 1.6, 1.8, WHT, 0.45)
    sh(pt(2.2, -9.6, 2.8, -11.0, 3.6, -10.0, 4.2, -11.0, 4.4, -9.4), KIN, 0.35, false); sh(pt(4.4, -8.2, 6.0, -7.6, 4.4, -7.2), OCH, 0.3, false)
    dot(3.5, -8.4, 0.4, INK); dot(3.6, -6.6, 0.55, KIN); restore()
}
internal fun Ctx.crow(x: Double, y: Double, f: Double) {
    val a = sin(f * 5 + x * 0.3)
    still {
        save(); translate(x, y)
        sh(pt(-2.2, 0.0, -4.4, -0.9, -4.4, 0.9), INK, 0.0, false); el(0.0, 0.0, 2.4, 1.1, INK, 0.0, false); dot(2.3, -0.4, 0.9, INK)
        sh(pt(3.0, -0.6, 4.4, -0.2, 3.0, 0.1), hex(0xFF5A4A3A), 0.0, false)
        sh(pt(-1.0, 0.0, -2.6, -4 * a - 0.4, 1.4, -3.4 * a, 1.0, 0.0), INK, 0.0, false); restore()
    }
}
internal fun Ctx.cross(x: Double, y: Double) {
    el(x, y, 4.0, 1.2, hex(0xFF6D6450), 0.4); quad(x - 0.6, y - 11, 1.2, 11.0, BRN2, 0.4); quad(x - 3, y - 8.6, 6.0, 1.1, BRN2, 0.35); quad(x - 1.8, y - 10.4, 3.6, 0.8, BRN2, 0.3)
    sh(pt(x - 2, y - 4.4, x + 2, y - 5.6, x + 2, y - 4.8, x - 2, y - 3.6), BRN2, 0.3)
}
internal fun Ctx.pot(x: Double, y: Double, f: Double) {
    sh(pt(x - 4, y - 1, x - 5.6, y - 5, x - 4.4, y - 8.4, x - 3.2, y - 9.2, x + 3.2, y - 9.2, x + 4.4, y - 8.4, x + 5.6, y - 5, x + 4, y - 1), hex(0xFFB8643A), sm = true)
    ln(pt(x - 4.6, y - 6.4, x + 4.6, y - 6.4), INK, 0.35); el(x, y - 9.2, 3.4, 1.0, hex(0xFF3A2416), 0.45, false)
    for ((dx, dy) in listOf(-1.6 to -9.8, 1.0 to -10.4, 2.2 to -9.6, -0.2 to -11.0)) el(x + dx, y + dy, 1.1, 0.7, YEL, 0.3, false, 8)
    val tw = (sin(f * 3) + 1) / 2
    still { save(); translate(x + 1.6, y - 12.5); scale(0.5 + tw * 0.6, 0.5 + tw * 0.6); sh(pt(0.0, -2.4, 0.5, -0.5, 2.4, 0.0, 0.5, 0.5, 0.0, 2.4, -0.5, 0.5, -2.4, 0.0, -0.5, -0.5), WHT, 0.25, false); restore() }
}
internal fun Ctx.verst(x: Double, y: Double) {
    quad(x - 1.4, y - 20, 2.8, 20.0, WHT)
    inside(pt(x - 1.4, y - 20, x + 1.4, y - 20, x + 1.4, y, x - 1.4, y)) {
        for (i in 0 until 7) sh(pt(x - 2, y - 19 + i * 3.2, x + 2, y - 20.8 + i * 3.2, x + 2, y - 19.2 + i * 3.2, x - 2, y - 17.4 + i * 3.2), INK, 0.0, false)
    }
    quad(x - 2.2, y - 23, 4.4, 3.0, WHT, 0.4); ln(pt(x - 1.2, y - 21.5, x + 1.2, y - 21.5), INK, 0.3)
}
internal fun Ctx.booth(x: Double, y: Double, w: Double, col: Int) {
    quad(x + 1, y - 24, 1.2, 24.0, BRN2, 0.4); quad(x + w - 2.2, y - 24, 1.2, 24.0, BRN2, 0.4); quad(x, y - 10, w, 10.0, hex(0xFFB88C55))
    fade(0.6) { ln(pt(x, y - 5, x + w, y - 5), INK, 0.3) }
    for (i in 0 until 4) {
        val gx = x + 4 + i * (w - 8) / 3
        if (i % 2 == 1) el(gx, y - 11.6, 1.8, 1.6, hex(0xFFD8A04C), 0.4)
        else { beginPath(); circle(gx, y - 12, 1.6); stroke(INK, 1.1); stroke(hex(0xFFD8A04C), 0.55) }
    }
    val n = 5; val sw = w / n
    for (i in 0 until n) { val ax = x + i * sw; sh(pt(ax, y - 26, ax + sw, y - 26, ax + sw, y - 21, ax + sw * 0.5, y - 19.6, ax, y - 21), if (i % 2 == 1) WHT else col, 0.5) }
    sh(pt(x - 1, y - 26, x + w / 2, y - 31, x + w + 1, y - 26), col, 0.55); dot(x + w / 2, y - 31.6, 0.8, YEL)
}
internal fun Ctx.barrel(x: Double, y: Double) {
    sh(pt(x - 3.4, y, x - 4.2, y - 5, x - 3.4, y - 10, x + 3.4, y - 10, x + 4.2, y - 5, x + 3.4, y), hex(0xFF9A6438))
    for (yy in doubleArrayOf(y - 2, y - 8)) ln(pt(x - 3.9, yy, x + 3.9, yy), INK, 0.6)
    el(x, y - 10, 3.4, 0.8, hex(0xFFB07A48), 0.4, false)
}
internal fun Ctx.samovar(x: Double, y: Double) {
    val b = hex(0xFFD9A43A); quad(x - 2.6, y - 1.4, 5.2, 1.4, b, 0.4)
    sh(pt(x - 1.4, y - 1.4, x - 3.8, y - 4, x - 4, y - 8, x - 2.4, y - 10, x + 2.4, y - 10, x + 4, y - 8, x + 3.8, y - 4, x + 1.4, y - 1.4), b, 0.5, sm = true)
    quad(x - 1.2, y - 12, 2.4, 2.0, b, 0.4); el(x, y - 12.4, 1.8, 0.7, hex(0xFFB88A30), 0.35, false)
    for (s in intArrayOf(-1, 1)) ln(pt(x + s * 3.8, y - 8.4, x + s * 5, y - 8, x + s * 4.4, y - 6.4), INK, 0.5)
    ln(pt(x - 1, y - 4.4, x - 2.6, y - 4), INK, 0.6); dot(x + 0.8, y - 7, 0.6, hex(0xFFFFF2C0))
}
internal fun Ctx.candle(x: Double, y: Double, f: Double) {
    val cx = x + 1; quad(cx - 0.9, y - 6, 1.8, 6.0, WHT, 0.35); val fl = sin(f * 8 + x) * 0.35
    still { save(); translate(cx, y - 6.3); sh(pt(-0.9, 0.0, -0.6, -1.4, fl, -3.6, 0.6, -1.4, 0.9, 0.0), YEL, 0.3, false, true); dot(0.0, -0.7, 0.35, KIN); restore() }
}
internal fun Ctx.waveFlag(x: Double, y: Double, f: Double) {
    quad(x - 0.5, y, 1.0, 40.0, BRN2, 0.4)
    still {
        val cols = intArrayOf(WHT, BLU, KIN)
        for (k in 0 until 3) {
            val top = Pb(); val bot = Pb()
            var i = 0; while (i <= 16) { val w = sin(f * 2.2 + i * 0.45) * 1.2 * (i / 16.0); top.add(x + 0.5 + i, y + k * 3.4 + w); bot.add(x + 0.5 + i, y + (k + 1) * 3.4 + w); i += 2 }
            sh(cat(top, bot), cols[k], 0.45, false)
        }
    }
}
