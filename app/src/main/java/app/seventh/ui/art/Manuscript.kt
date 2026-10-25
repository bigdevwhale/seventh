package app.seventh.ui.art

import android.graphics.LinearGradient
import android.graphics.Shader
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/* Europe art: illuminated manuscript (gold leaf, diaper, ultramarine, vermilion) */
internal object Eu {
    val EI = hex(0xFF2A1C14); val UM = hex(0xFF2C4F9E); val UM2 = hex(0xFF1D3672); val VM = hex(0xFFCF3F2A)
    val GD = hex(0xFFD6A93C); val GDL = hex(0xFFF3DC8E); val GDD = hex(0xFFA8792A); val VG = hex(0xFF3F7F5F)
    val VG2 = hex(0xFF2A5A40); val VGL = hex(0xFF86B37C); val RS = hex(0xFFD98C8A); val WL = hex(0xFFF7F1E2)
    val SA = hex(0xFFDDD3C2); val SB = hex(0xFFB3A790); val BR = hex(0xFF7A4E2C); val BR2 = hex(0xFF4E321C); val MAIL = hex(0xFF959AA2)
    private val LIT = hex(0xFFF6D777); private val DARKW = hex(0xFF2D4570); private val HOLE = hex(0xFF2A2018)

    inline fun Ctx.style(fn: () -> Unit) {
        val j = J; val lw = LW; val reg = REG; val ink = INK
        J = 0.14; LW = 0.55; REG = false; INK = EI
        try { fn() } finally { J = j; LW = lw; REG = reg; INK = ink }
    }
    fun Ctx.gold(x: Double, y: Double, w: Double, h: Double, f: Double?) {
        cached("gold", x, y, w, h) {
            fillRect(x, y, w, h, GD)
            fillShader(LinearGradient(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(),
                intArrayOf(rgba(255, 240, 180, 0.35), rgba(255, 255, 255, 0.0), rgba(120, 80, 20, 0.28)), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP), x, y, w, h)
            var j = 0
            while (j < h + 6) {
                val o = if ((j / 5) % 2 == 1) 2.5 else 0.0
                var i = 0
                while (i < w + 6) { dot(x + i + o, y + j, 0.42, GDD); dot(x + i + 2.5 - o, y + j + 2.5, 0.28, GDL); i += 5 }
                j += 5
            }
        }
        if (f != null) {
            val sx = x + ((f * 16) % (w + 80)) - 40
            fillShader(LinearGradient((sx - 16).toFloat(), 0f, (sx + 16).toFloat(), 0f,
                intArrayOf(rgba(255, 250, 220, 0.0), rgba(255, 250, 225, 0.38), rgba(255, 250, 220, 0.0)), floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP), x, y, w, h)
        }
    }
    fun Ctx.diaper(x: Double, y: Double, w: Double, h: Double, a: Int, b: Int, s: Double) = cached("dp$a.$b.$s", x, y, w, h) {
        fillRect(x, y, w, h, a)
        var j = 0
        while (j * s < h + s) { var i = 0; while (i * s < w + s) { if ((i + j) % 2 == 1) fillRect(x + i * s, y + j * s, s, s, b); i++ }; j++ }
        beginPath()
        var i = 0; while (i * s <= w + s) { moveTo(x + i * s, y); lineTo(x + i * s, y + h); i++ }
        j = 0; while (j * s <= h + s) { moveTo(x, y + j * s); lineTo(x + w, y + j * s); j++ }
        stroke(GD, 0.35)
        j = 0
        while (j * s < h + s) {
            i = 0
            while (i * s < w + s) {
                val cx = x + i * s + s / 2; val cy = y + j * s + s / 2; dot(cx, cy, 0.5, GDL)
                dot(cx + s * 0.22, cy, 0.28, GDL); dot(cx - s * 0.22, cy, 0.28, GDL); dot(cx, cy + s * 0.22, 0.28, GDL); dot(cx, cy - s * 0.22, 0.28, GDL)
                i++
            }
            j++
        }
    }
    fun Ctx.star6(x: Double, y: Double, s: Double, col: Int) {
        val p = DoubleArray(24)
        for (i in 0 until 12) { val a = i / 12.0 * 6.283 - 1.571; val r = if (i % 2 == 1) s * 0.42 else s; p[2 * i] = x + cos(a) * r; p[2 * i + 1] = y + sin(a) * r }
        sh(p, col, 0.25)
    }
    fun Ctx.swallow(x: Double, y: Double, f: Double, i: Int) {
        val a = sin(f * 6 + i * 2) * 0.8
        still { save(); translate(x, y); sh(pt(-3.0, -1.4 * a, -1.0, 0.0, 0.0, 0.6, 1.0, 0.0, 3.0, -1.4 * a, 1.0, -0.4, 0.0, -0.2, -1.0, -0.4), EI, 0.0); restore() }
    }
    fun Ctx.sky(W: Double, hz: Double, k: String, f: Double) {
        if (k == "day" || k == "dawn") {
            gold(0.0, 0.0, W, hz + 3, f)
            fillRect(0.0, 0.0, W, 4.5, UM)
            var x = -2.0; while (x < W + 4) { beginPath(); arc(x, 4.5, 3.0, 0.0, PI); fill(UM); x += 6 }
            x = -2.0; while (x < W + 4) { beginPath(); arc(x, 4.5, 2.2, 0.0, PI); stroke(WL, 0.45); x += 6 }
            if (k == "dawn") fillShader(LinearGradient(0f, (hz - 18).toFloat(), 0f, (hz + 3).toFloat(), rgba(217, 140, 138, 0.0), rgba(217, 140, 138, 0.75), Shader.TileMode.CLAMP), 0.0, hz - 18, W, 21.0)
            for (i in 0 until 3) { val sx = ((i * 61 + f * (6 + i * 2)) % (W + 30)) - 15; swallow(sx, 16.0 + i * 7 + sin(f + i) * 2, f, i) }
        } else if (k == "night") fillRect(0.0, 0.0, W, hz + 3, UM2)
        else if (k == "dusk") diaper(0.0, 0.0, W, hz + 3, VM, UM, 7.0)
        else diaper(0.0, 0.0, W, hz + 3, hex(0xFF8D92A0), hex(0xFF6E7482), 7.0)
    }
    fun Ctx.nstars(W: Double, hz: Double, f: Double) {
        val r = Rng(14)
        for (i in 0 until 26) { val x = r.next() * W; val y = 3 + r.next() * hz * 0.8; val s = 0.9 + r.next() * 0.7; val tw = 0.75 + 0.25 * sin(f * 1.5 + i * 1.9); star6(x, y, s * tw, GD) }
        save(); beginPath(); rect(W - 34, 2.0, 24.0, 24.0); circle(W - 19, 12.0, 5.0); clip(true); el(W - 22, 13.0, 6.0, 6.0, GD, off = false); restore()
    }
    private fun shifted(p: DoubleArray, dy: Double): DoubleArray { val q = p.copyOf(); var i = 1; while (i < q.size) { q[i] += dy; i += 2 }; return q }
    fun Ctx.hill(W: Double, base: Double, col: Int, seed: Int, amp: Double, freq: Double) {
        val r = Rng(seed); val p1 = r.next() * 6; val p2 = r.next() * 6; val p = Pb()
        var x = -6.0; while (x <= W + 6) { p.add(x, base - amp * (0.5 + 0.5 * sin(x * freq + p1)) * (0.75 + 0.25 * sin(x * freq * 2.7 + p2))); x += 5 }
        val pts = p.arr()
        poly(p.add(W + 6, base + 30).add(-6.0, base + 30).arr(), true, true); fill(col)
        fade(0.35) { for (k in 1 until 4) ln(shifted(pts, k * 2.4), EI, 0.3, true) }
        ln(pts, EI, 0.55, true); fade(0.8) { ln(shifted(pts, 0.9), WL, 0.35, true) }
    }
    fun Ctx.ground(W: Double, H: Double, y0: Double, col: Int, seed: Int, snow: Boolean = false, flowers: IntArray? = null) {
        val r = Rng(seed); val p = Pb()
        var x = -6.0; while (x <= W + 6) { p.add(x, y0 + sin(x * 0.05 + seed) * 1.1); x += 6 }
        val pts = p.arr()
        poly(p.add(W + 6, H + 3).add(-6.0, H + 3).arr(), true, false); fill(col); ln(pts, EI, 0.55, true)
        val n = jr(W * (H - y0) / 55).toInt()
        for (i in 0 until n) {
            val gx = r.next() * W; val gy = y0 + 4 + r.next() * (H - y0 - 4)
            if (snow) { fade(0.4) { ln(pt(gx - 1.2, gy, gx + 1.2, gy), hex(0xFF8A9AB8), 0.35) }; continue }
            fade(0.55) { ln(pt(gx, gy, gx, gy - 2.2), VG2, 0.35); ln(pt(gx, gy - 1, gx - 1, gy - 2), VG2, 0.3) }
            if (flowers != null && i % 2 == 0) { val fc = flowers[i % flowers.size]; dot(gx - 0.6, gy - 2.4, 0.5, fc); dot(gx + 0.6, gy - 2.4, 0.5, fc); dot(gx, gy - 3.2, 0.5, fc) }
        }
    }
    fun Ctx.ltree(x: Double, y: Double, h: Double, s: String) {
        quad(x - 0.8, y - h * 0.55, 1.6, h * 0.55, BR, 0.4)
        if (s == "winter") { for ((a, l) in listOf(-2.2 to 0.3, -1.1 to 0.28, -1.9 to 0.22, -1.3 to 0.18)) ln(pt(x, y - h * 0.5, x + cos(a) * h * l, y - h * 0.5 + sin(a) * h * l), BR2, 0.6); return }
        val col = if (s == "autumn") hex(0xFFD08A2C) else if (s == "spring") VGL else VG; val cr = h * 0.3; val cy = y - h * 0.7
        el(x, cy, cr, cr * 1.05, col, 0.6)
        save(); poly(ellP(x, cy, cr, cr * 1.05), true, true); clip(); alpha = 0.5
        for (j in -3..3) for (i in -3..3) {
            val ax = x + i * cr * 0.33 + (if (j % 2 != 0) cr * 0.16 else 0.0); val ay = cy + j * cr * 0.3
            beginPath(); arc(ax, ay, cr * 0.18, PI * 0.1, PI * 0.9); stroke(VG2, 0.35)
        }
        restore()
        for (i in 0 until 5) dot(x - cr * 0.45 + i * 0.9, cy - cr * 0.6 + (i % 2) * 0.8, 0.4, WL)
        if (s == "summer") for (i in 0 until 4) dot(x - cr * 0.5 + i * cr * 0.35, cy + (if (i % 2 == 1) cr * 0.3 else -cr * 0.1), 0.65, VM)
    }
    fun Ctx.cypr(x: Double, y: Double, h: Double, snow: Boolean = false) {
        sh(pt(x - 0.8, y, x - h * 0.12, y - h * 0.35, x - h * 0.08, y - h * 0.75, x, y - h, x + h * 0.08, y - h * 0.75, x + h * 0.12, y - h * 0.35, x + 0.8, y), VG2, sm = true)
        fade(0.5) { for (k in 1 until 6) { val yy = y - h * k / 6.5; ln(pt(x - h * 0.07, yy + 1, x, yy, x + h * 0.07, yy + 1), VGL, 0.35) } }
        if (snow) for (k in 1 until 5) el(x, y - h * k / 5.5, h * 0.06, 0.5, WL, 0.25, false)
    }
    fun Ctx.house(x: Double, y: Double, w: Double, f: Double = 0.0, lit: Boolean = false, chimney: Boolean = false, smoky: Boolean = false,
                  dark: Boolean = false, tile: Boolean = false, snow: Boolean = false) {
        val hh = w * 0.42; val L = x - w / 2; val T = y - hh; val rh = w * 0.42
        if (chimney) quad(x + w * 0.22, T - rh * 0.75, 3.4, rh * 0.6, SB, 0.45)
        quad(L, T, w, hh, if (dark) hex(0xFFB4A284) else WL, 0.6)
        if (dark) fade(0.35) {
            var i = 1; while (i < w / 2.2) { ln(pt(L + i * 2.2, T + 0.4, L + i * 2.2 + 0.4, y - 0.4), EI, 0.3); i++ }
            for (j in 1 until 4) ln(pt(L, T + j * hh / 4, L + w, T + j * hh / 4 + 0.4), EI, 0.3, true)
        } else {
            val bw = 1.1
            for (px in doubleArrayOf(L + bw / 2, x, L + w - bw / 2)) ln(pt(px, T, px, y), BR2, bw)
            ln(pt(L, T + hh * 0.5, L + w, T + hh * 0.5), BR2, bw)
            for (s in intArrayOf(-1, 1)) { ln(pt(x + s * w * 0.5, T + hh * 0.5, x + s * w * 0.22, T), BR2, 0.9); ln(pt(x + s * w * 0.5, y, x + s * w * 0.25, T + hh * 0.5), BR2, 0.9) }
        }
        val dx = x - w * 0.33
        sh(pt(dx - 2, y, dx - 2, y - hh * 0.42, dx, y - hh * 0.55, dx + 2, y - hh * 0.42, dx + 2, y), BR, 0.45)
        val wx = x + w * 0.18; val wy = T + hh * 0.2
        quad(wx - 2.4, wy, 4.8, hh * 0.26, if (lit) LIT else DARKW, 0.45); ln(pt(wx, wy, wx, wy + hh * 0.26), EI, 0.35)
        val R = pt(L - 2.2, T + 0.3, L + w * 0.14, T - rh, L + w * 0.86, T - rh, L + w + 2.2, T + 0.3)
        sh(R, if (dark) hex(0xFF7D6A4A) else if (tile) VM else hex(0xFFC99A48), 0.6)
        inside(R) {
            alpha = 0.45
            if (tile) for (j in 0 until 6) {
                val yy = T - rh + j * rh / 5.5 + 1.6
                var i = 0.0; while (i < w + 6) { beginPath(); arc(L - 2 + i + (if (j % 2 == 1) 1.3 else 0.0), yy, 1.3, 0.0, PI); stroke(EI, 0.3); i += 2.6 }
            } else { var i = -2; while (i < w / 1.6 + 4) { ln(pt(L + i * 1.6, T - rh, L - 3 + i * 1.75, T + 1), EI, 0.3); i++ } }
        }
        if (snow) sh(pt(L - 2.6, T + 0.6, L + w * 0.14, T - rh - 0.6, L + w * 0.86, T - rh - 0.6, L + w + 2.6, T + 0.6, L + w + 0.4, T + 1.8, L + w * 0.84, T - rh + 2.4, L + w * 0.5, T - rh + 1.6, L + w * 0.16, T - rh + 2.6, L - 0.4, T + 1.8), WL, 0.45)
        if (chimney) smoke(x + w * 0.22 + 1.7, T - rh * 0.8, f, 5)
        if (smoky) smoke(x, T - rh, f, 5, true)
    }
    fun Ctx.mill(x: Double, y: Double, f: Double, snow: Boolean) {
        quad(x - 0.8, y - 14, 1.6, 14.0, BR2, 0.4); ln(pt(x - 6, y, x, y - 8, x + 6, y), BR2, 0.8)
        quad(x - 5, y - 28, 10.0, 14.0, WL, 0.55); ln(pt(x - 5, y - 21, x + 5, y - 21), BR2, 0.8); ln(pt(x - 5, y - 28, x + 5, y - 14), BR2, 0.6)
        sh(pt(x - 6, y - 28, x, y - 34, x + 6, y - 28), if (snow) WL else VM, 0.55)
        still {
            save(); translate(x, y - 24); rotate(f * 0.6)
            for (i in 0 until 4) {
                rotate(PI / 2); ln(pt(0.0, 0.0, 0.0, -17.0), BR2, 0.8); sh(pt(0.6, -4.0, 3.4, -4.0, 3.4, -17.0, 0.6, -17.0), WL, 0.35, false)
                fade(0.6) { for (k in 0 until 4) ln(pt(0.6, -6.6 - k * 3, 3.4, -6.6 - k * 3), EI, 0.25) }
            }
            restore(); dot(x, y - 24, 1.1, BR2)
        }
    }
    fun Ctx.towerHouse(x: Double, y: Double, w: Double, lit: Boolean) {
        val hh = w * 0.42; val L = x - w / 2; val T = y - hh
        val tw = w * 0.24; val tx = L + 2; val tT = T - hh * 0.9
        quad(tx, tT, tw, y - tT, SA, 0.6); for (i in 0 until 4) quad(tx + i * tw / 3.5, tT - 2.4, tw / 7, 2.4, SA, 0.4)
        quad(L + tw + 2, T, w - tw - 2, hh, SA, 0.6)
        fade(0.3) { for (j in 1 until 6) ln(pt(L, T + j * hh / 6, L + w, T + j * hh / 6), EI, 0.3) }
        sh(pt(L + tw, T + 0.3, L + tw + 4, T - hh * 0.55, L + w - 3, T - hh * 0.55, L + w + 2, T + 0.3), VM, 0.6)
        val wc = if (lit) LIT else DARKW
        for (wx in doubleArrayOf(L + tw + 8, L + tw + 18, L + w - 7)) sh(pt(wx - 1.8, T + hh * 0.75, wx - 1.8, T + hh * 0.3, wx, T + hh * 0.15, wx + 1.8, T + hh * 0.3, wx + 1.8, T + hh * 0.75), wc, 0.45)
        val c = tx + tw / 2
        sh(pt(c - 1.6, tT + 10, c - 1.6, tT + 5, c, tT + 3.4, c + 1.6, tT + 5, c + 1.6, tT + 10), wc, 0.45)
    }
    fun Ctx.pennant(x: Double, y: Double, f: Double, col: Int = VM) {
        ln(pt(x, y, x, y - 6), EI, 0.5)
        still {
            val p = Pb().add(x, y - 6)
            for (i in 1..6) p.add(x + i * 1.1, y - 6 + sin(f * 3 + i * 0.8) * 0.6 * (i / 6.0) + i * 0.12)
            p.add(x, y - 4); sh(p.arr(), col, 0.35, false)
        }
    }
    fun Ctx.castle(x: Double, y: Double, s: Double, f: Double) {
        save(); translate(x, y); scale(s, s)
        quad(-22.0, -16.0, 44.0, 16.0, SA, 0.6); for (i in 0 until 9) quad(-21.0 + i * 5, -18.4, 2.6, 2.4, SA, 0.4)
        quad(-7.0, -30.0, 14.0, 30.0, SA, 0.6); for (i in 0 until 4) quad(-6.4 + i * 3.8, -32.4, 2.0, 2.4, SA, 0.4)
        sh(pt(-3.6, 0.0, -3.6, -7.0, 0.0, -10.4, 3.6, -7.0, 3.6, 0.0), HOLE, 0.5)
        fade(0.6) { for (i in -2..2) ln(pt(i * 1.4, -9.0, i * 1.4, 0.0), MAIL, 0.35) }
        for (tx in doubleArrayOf(-24.0, 24.0)) {
            quad(tx - 5, -26.0, 10.0, 26.0, SA, 0.6); sh(pt(tx - 6.4, -26.0, tx, -38.0, tx + 6.4, -26.0), UM, 0.6); pennant(tx, -38.0, f + tx, VM)
            sh(pt(tx - 1, -17.0, tx - 1, -21.0, tx, -22.4, tx + 1, -21.0, tx + 1, -17.0), HOLE, 0.35)
        }
        fade(0.25) { for (j in 1 until 4) ln(pt(-22.0, -16.0 + j * 4, 22.0, -16.0 + j * 4), EI, 0.3) }
        pennant(0.0, -32.4, f, GD); restore()
    }
    fun Ctx.church(x: Double, y: Double, s: Double) {
        save(); translate(x, y); scale(s, s)
        quad(-16.0, -20.0, 26.0, 20.0, SA, 0.6); sh(pt(-18.0, -19.6, -3.0, -29.0, 12.0, -19.6), UM2, 0.55)
        for (wx in doubleArrayOf(-11.0, -4.0, 3.0)) sh(pt(wx - 1.4, -5.0, wx - 1.4, -13.0, wx, -15.6, wx + 1.4, -13.0, wx + 1.4, -5.0), UM, 0.45)
        for (bx in doubleArrayOf(-16.0, -8.6, -1.4, 6.0)) sh(pt(bx - 0.8, 0.0, bx - 0.8, -14.0, bx + 0.8, -16.0, bx + 2, -14.0, bx + 2, 0.0), SB, 0.4)
        quad(10.0, -30.0, 12.0, 30.0, SA, 0.6); sh(pt(9.0, -30.0, 16.0, -52.0, 23.0, -30.0), UM2, 0.6)
        ln(pt(16.0, -52.0, 16.0, -56.0), GD, 0.9); ln(pt(14.6, -54.6, 17.4, -54.6), GD, 0.8)
        el(16.0, -22.0, 3.0, 3.0, VM, 0.5, false); for (i in 0 until 4) { val a = i / 4.0 * 6.283; dot(16 + cos(a) * 1.5, -22 + sin(a) * 1.5, 0.7, GD) }
        sh(pt(13.4, 0.0, 13.4, -7.0, 16.0, -10.6, 18.6, -7.0, 18.6, 0.0), BR, 0.5); restore()
    }
    fun Ctx.well(x: Double, y: Double) {
        quad(x - 5, y - 6, 10.0, 6.0, SA, 0.5)
        fade(0.4) { ln(pt(x - 5, y - 3, x + 5, y - 3), EI, 0.3); ln(pt(x - 1, y - 6, x - 1, y - 3), EI, 0.3); ln(pt(x + 2, y - 3, x + 2, y), EI, 0.3) }
        quad(x - 5, y - 17, 1.1, 11.0, BR, 0.35); quad(x + 3.9, y - 17, 1.1, 11.0, BR, 0.35); sh(pt(x - 7, y - 16, x, y - 21, x + 7, y - 16), VM, 0.5)
        ln(pt(x, y - 16, x, y - 11), EI, 0.3); quad(x - 1.2, y - 11, 2.4, 2.2, BR, 0.3)
    }
    fun Ctx.boar(x: Double, y: Double, f: Double) {
        save(); translate(x, y + sin(f * 2) * 0.3); val b = hex(0xFF5C4A40)
        for (lx in doubleArrayOf(-6.0, -2.0, 4.0, 7.0)) quad(lx, -5.0, 1.8, 5.0, b, 0.4)
        sh(pt(-11.0, -7.0, -9.0, -12.0, -2.0, -14.6, 6.0, -13.6, 11.0, -10.0, 11.0, -5.0, 6.0, -3.6, -6.0, -3.6, -10.0, -4.6), b, sm = true)
        fade(0.7) { for (i in 0 until 9) ln(pt(-6 + i * 1.8, -14.4 + abs(i - 4) * 0.15, -5.4 + i * 1.8, -16.4 + abs(i - 4) * 0.2), EI, 0.4) }
        sh(pt(-10.0, -11.0, -13.6, -9.0, -15.0, -7.0, -12.0, -6.0), b, 0.45); el(-15.0, -7.2, 0.9, 1.2, RS, 0.3, false)
        sh(pt(-12.6, -7.0, -14.0, -9.8, -12.0, -8.0), WL, 0.3, false); dot(-10.4, -10.4, 0.5, LIT)
        sh(pt(-8.6, -12.6, -7.4, -15.0, -6.6, -12.4), b, 0.35); ln(pt(11.0, -9.0, 13.0, -11.0), EI, 0.5); restore()
    }
    fun Ctx.pyre(x: Double, y: Double, f: Double) {
        ln(pt(x, y - 4, x, y - 30), EI, 1.8); ln(pt(x, y - 4, x, y - 30), BR, 1.0)
        for (j in 0 until 3) for (i in -3 + j..3 - j) { val lx = x + i * 3.2; val ly = y - 1.6 - j * 2.6; el(lx, ly, 1.8, 1.3, BR, 0.4, false, 8); dot(lx, ly, 0.5, BR2) }
        flames(x, y - 7, 6.0, 14.0, f); flames(x - 6, y - 4, 4.0, 8.0, f + 1); flames(x + 6, y - 4, 4.0, 8.0, f + 2); smoke(x, y - 26, f, 6, true)
    }
    fun Ctx.interior(W: Double, Hh: Double, f: Double, tavern: Boolean = false, night: Boolean = false, notable: Boolean = false) {
        fillRect(0.0, 0.0, W, 80.0, SA)
        fade(0.35) { for (j in 0 until 12) { val y = j * 7.0; ln(pt(0.0, y, W, y), EI, 0.35); for (i in 0 until 14) { val xx = i * 14.0 + (if (j % 2 == 1) 7 else 0); ln(pt(xx, y, xx, y + 7), EI, 0.3) } } }
        if (!tavern) {
            diaper(40.0, 10.0, 64.0, 46.0, VM, UM, 6.0); quad(40.0, 10.0, 64.0, 46.0, 0, 0.7); quad(40.0, 8.0, 64.0, 2.4, GD, 0.5)
            ln(pt(22.0, 10.0, 22.0, 30.0), BR2, 1.4); ln(pt(17.0, 15.0, 27.0, 15.0), BR2, 1.2); el(22.0, 13.0, 1.0, 1.0, hex(0xFFE8C49A), 0.3, false); dot(22.0, 8.6, 0.9, GD)
        } else {
            quad(100.0, 55.0, 76.0, 2.6, BR, 0.45)
            var x = 104
            while (x < 170) {
                val xd = x.toDouble()
                sh(pt(xd - 1.6, 55.0, xd - 2, 51.0, xd - 1, 49.0, xd - 1, 47.6, xd + 1, 47.6, xd + 1, 49.0, xd + 2, 51.0, xd + 1.6, 55.0), if (x % 2 == 1) hex(0xFFB8643A) else hex(0xFFC9B48A), 0.4)
                x += 7
            }
        }
        val wx = if (tavern) 70.0 else 118.0
        val gw = pt(wx - 6, 34.0, wx - 6, 16.0, wx, 8.0, wx + 6, 16.0, wx + 6, 34.0)
        sh(gw, if (night) hex(0xFF22335A) else hex(0xFFA9C4D6), 0.6)
        inside(gw) { alpha = 0.5; for (i in -4..4) { ln(pt(wx + i * 3 - 10, 40.0, wx + i * 3 + 10, 6.0), EI, 0.3); ln(pt(wx + i * 3 + 10, 40.0, wx + i * 3 - 10, 6.0), EI, 0.3) } }
        if (!tavern) {
            val hx = 146.0
            sh(pt(hx - 16, 24.0, hx - 12, 8.0, hx + 12, 8.0, hx + 16, 24.0), SB, 0.6); quad(hx - 15, 24.0, 30.0, 3.0, SA, 0.5)
            sh(pt(hx - 11, 80.0, hx - 11, 40.0, hx + 11, 40.0, hx + 11, 80.0), HOLE, 0.6); quad(hx - 14, 27.0, 4.0, 53.0, SA, 0.5); quad(hx + 10, 27.0, 4.0, 53.0, SA, 0.5)
            ln(pt(hx, 40.0, hx, 62.0), EI, 0.4); el(hx, 66.0, 4.0, 3.6, hex(0xFF3A3028), 0.45)
        } else for ((bx, by) in listOf(14.0 to 79.0, 26.0 to 79.0, 20.0 to 68.0, 150.0 to 79.0, 162.0 to 79.0)) barrel(bx, by)
        quad(-1.0, 63.0, W + 2, 2.4, BR, 0.5)
        for (j in 0 until 4) for (i in 0 until 18) fillRect(i * 10.0 + (if (j % 2 == 1) 5 else 0), 80.0 + j * 5, 10.0, 5.0, if ((i + j) % 2 == 1) hex(0xFFB8643A) else hex(0xFFD8B06A))
        ln(pt(0.0, 80.0, W, 80.0), EI, 0.6); fade(0.35) { for (j in 1 until 4) ln(pt(0.0, 80.0 + j * 5, W, 80.0 + j * 5), EI, 0.3) }
        if (!notable) {
            val tx = 44.0; val ty = 70.0
            quad(tx + 3, ty + 8, 2.0, 11.0, BR2, 0.35); quad(tx + 47, ty + 8, 2.0, 11.0, BR2, 0.35); sh(pt(tx, ty, tx + 52, ty, tx + 53, ty + 9, tx - 1, ty + 9), WL, 0.5)
            for (i in 0 until 13) dot(tx + 2 + i * 4, ty + 7.4, 0.55, UM); ln(pt(tx, ty + 2.4, tx + 52, ty + 2.4), GD, 0.4)
            el(tx + 12, ty - 1.6, 4.2, 2.2, hex(0xFFC8843E), 0.5)
            sh(pt(tx + 36, ty, tx + 35, ty - 5, tx + 36.6, ty - 7, tx + 39.4, ty - 7, tx + 41, ty - 5, tx + 40, ty), hex(0xFFB8643A), 0.45)
        }
        if (!tavern) hearth(f)
    }
    fun Ctx.hearth(f: Double) = flames(146.0, 74.0, 5.0, 9.0, f)
    fun Ctx.frame(W: Double, H: Double) {
        strokeRect(1.2, 1.2, W - 2.4, H - 2.4, UM, 2.4); strokeRect(0.1, 0.1, W - 0.2, H - 0.2, EI, 0.5); strokeRect(2.5, 2.5, W - 5, H - 5, EI, 0.5)
        strokeRect(3.1, 3.1, W - 6.2, H - 6.2, GD, 0.7)
        var x = 4.0; while (x < W - 2) { dot(x, 1.2, 0.4, WL); dot(x, H - 1.2, 0.4, WL); x += 4 }
        var y = 4.0; while (y < H - 2) { dot(1.2, y, 0.4, WL); dot(W - 1.2, y, 0.4, WL); y += 4 }
        for ((cx, cy) in listOf(2.0 to 2.0, W - 2 to 2.0, 2.0 to H - 2, W - 2 to H - 2)) { quad(cx - 2.4, cy - 2.4, 4.8, 4.8, VM, 0.4, false); dot(cx, cy, 1.2, GD) }
    }

    /* people */
    private val HOSE = intArrayOf(hex(0xFF6B4A32), hex(0xFF3C5A86), hex(0xFF8A3A2A), hex(0xFF5A6A3A))
    private val KER0 = hex(0xFFF4ECD8)
    fun Ctx.euPerson(L: Fig, kidFace: Boolean) {
        val a = L.age; val k = L.kind; val fem = L.sex == 'f'
        if (a < 14 && !kidFace) { val s = if (a < 4) 0.5 else if (a < 9) 0.62 else 0.72; save(); scale(s, s); person(L, true); restore(); return }
        val sk = L.skin; val hr = L.hair; val old = a > 55; val kid = kidFace; val sd = if (L.seed == 0) 1 else L.seed
        val cl = when (k) { "soldier" -> WL; "barin" -> UM; "priest" -> WL; "monk" -> hex(0xFF6B4A32); "beggar" -> hex(0xFF857A64); else -> L.cloth }
        val alt = intArrayOf(VM, UM, VG, GD, hex(0xFF8A5A3A))[sd.mod(5)]
        val hose = if (k == "barin") VM else if (k == "soldier") MAIL else HOSE[sd.mod(4)]
        val hood = !fem && !kid && k.isEmpty() && sd.mod(2) == 1
        if (!fem) {
            val long = k == "barin" || k == "priest" || k == "monk" || k == "beggar"
            if (!long) { quad(-3.6, -12.0, 2.8, 10.6, hose, 0.45); quad(0.8, -12.0, 2.8, 10.6, hose, 0.45) }
            sh(pt(-4.2, -1.8, -1.0, -1.8, -0.6, 0.0, -6.2, 0.0), EI, 0.3, false); sh(pt(1.0, -1.8, 4.2, -1.8, 6.2, 0.0, 0.6, 0.0), EI, 0.3, false)
            val hem = if (long) -0.8 else -10.6; val hw = if (long) 7.6 else 7.0
            for (s in intArrayOf(-1, 1)) sh(pt(s * 4.4, -26.6, s * 7.2, -23.0, s * 7.8, -15.6, s * 5.8, -15.0, s * 5.2, -20.6), if (k == "soldier") MAIL else cl, sm = true)
            val B = pt(-5.0, -27.6, 5.0, -27.6, 5.8, -20.0, hw, hem, -hw, hem, -5.8, -20.0); sh(B, if (k == "soldier") MAIL else cl)
            if (k == "soldier") {
                inside(B) {
                    alpha = 0.5; var y = -27.0
                    beginPath() // one path for the mail rings: the dots never touch
                    while (y < -10) { var x = -8.0; while (x < 8) { circle(x + (if (jr(y * 5).toInt() % 2 != 0) 0.6 else 0.0), y, 0.22); x += 1.2 }; y += 1.2 }
                    fill(EI)
                }
                sh(pt(-4.4, -26.0, 4.4, -26.0, 5.4, -8.0, -5.4, -8.0), WL, 0.45); quad(-0.9, -25.6, 1.8, 17.2, VM, 0.0, false); quad(-4.6, -20.4, 9.2, 1.8, VM, 0.0, false)
                ln(pt(9.2, 0.0, 9.2, -44.0), EI, 1.1); ln(pt(9.2, 0.0, 9.2, -44.0), BR, 0.6); sh(pt(8.0, -44.0, 9.2, -48.0, 10.4, -44.0), hex(0xFFCFD4D8), 0.35)
            } else if (k == "barin") {
                inside(B) { quad(-9.0, -3.6, 18.0, 3.0, WL, 0.35, false); var i = -7.0; while (i <= 7) { ln(pt(i, -3.2, i, -1.6), EI, 0.5); i += 2.4 } }
                sh(pt(-5.0, -27.6, 5.0, -27.6, 3.6, -24.4, -3.6, -24.4), WL, 0.4); for (i in 0 until 5) dot(-2.0 + i, -23 + abs(i - 2) * 0.6, 0.45, GD)
            } else if (k == "priest") {
                sh(pt(-5.4, -26.6, 5.4, -26.6, 6.4, -9.0, 0.0, -5.6, -6.4, -9.0), VM, 0.5, sm = true); ln(pt(0.0, -26.0, 0.0, -7.0), GD, 1.0); ln(pt(-4.0, -25.0, 0.0, -20.0, 4.0, -25.0), GD, 0.9)
            } else if (k == "monk") {
                quad(-6.0, -17.6, 12.0, 1.0, WL, 0.3); ln(pt(2.4, -17.0, 2.8, -10.0), WL, 0.5); sh(pt(-4.6, -28.0, 4.6, -28.0, 3.4, -25.0, -3.4, -25.0), cl, 0.4)
            } else if (k == "beggar") {
                inside(B) { quad(-6.0, -16.0, 3.4, 3.0, hex(0xFFA89878), 0.35); quad(2.0, -9.0, 3.0, 3.0, hex(0xFF6F6655), 0.35) }
                ln(pt(9.4, 1.0, 9.4, -37.0), EI, 1.1); ln(pt(9.4, 1.0, 9.4, -37.0), BR2, 0.5)
            } else {
                quad(-6.4, -17.6, 12.8, 1.3, BR2, 0.3); el(3.6, -15.2, 1.3, 1.7, BR, 0.35); ln(pt(0.0, -11.6, 0.0, -14.0), EI, 0.35)
                if (hood) sh(pt(-6.6, -25.0, -5.4, -28.6, 0.0, -30.0, 5.4, -28.6, 6.6, -25.0, 3.0, -23.6, 0.0, -24.4, -3.0, -23.6), alt, 0.5, sm = true)
            }
        } else {
            for (s in intArrayOf(-1, 1)) el(s * 2.6, -0.6, 1.8, 0.8, EI, 0.25, false)
            val gown = if (k == "bride") UM else if (k == "beggar") hex(0xFF857A64) else cl
            for (s in intArrayOf(-1, 1)) sh(pt(s * 3.6, -26.4, s * 7.0, -22.6, s * 7.4, -16.2, s * 8.8, -12.0, s * 6.4, -13.6, s * 5.2, -15.6, s * 4.6, -20.6), gown, sm = true)
            val B = pt(-4.0, -27.0, 4.0, -27.0, 4.8, -19.0, 9.8, -0.6, -9.8, -0.6, -4.8, -19.0); sh(B, gown)
            if (k != "beggar") {
                inside(B) {
                    quad(-11.0, -3.4, 22.0, 2.8, if (k == "bride") GD else if (alt == cl) GD else alt, 0.35, false)
                    if (k == "bride") { var y = -24.0; while (y < -5) { var x = -8.0; while (x <= 8) { star6(x + (if (jr(y).toInt() % 2 != 0) 1.5 else 0.0), y, 0.7, GD); x += 3 }; y += 3 } }
                }
                ln(pt(-4.8, -19.2, 4.8, -19.2), GD, 0.7); ln(pt(1.4, -19.0, 2.0, -10.0), GD, 0.5)
            } else { ln(pt(9.4, 1.0, 9.4, -37.0), EI, 1.1); ln(pt(9.4, 1.0, 9.4, -37.0), BR2, 0.5) }
        }
        for (s in intArrayOf(-1, 1)) el(s * (if (fem) 7.8 else 7.2), if (fem) -13.4 else -14.4, 1.3, 1.3, sk, 0.4, n = 8)
        quad(-1.2, -29.6, 2.4, 2.6, sk, 0.35)
        save(); translate(0.0, -33.5); if (kid) scale(1.12, 1.12)
        val wimp = fem && !kid && k != "bride"; val kc = if (k == "beggar") hex(0xFF6F6655) else if (L.kerch == KER0) UM else L.kerch
        if (wimp) {
            sh(pt(0.0, -7.0, -6.4, -3.0, -7.0, 5.0, -5.0, 8.6, 5.0, 8.6, 7.0, 5.0, 6.4, -3.0), kc, 0.5, sm = true)
            sh(pt(0.0, -5.6, -5.2, -2.4, -5.4, 2.4, -3.8, 6.4, 0.0, 7.4, 3.8, 6.4, 5.4, 2.4, 5.2, -2.4), WL, 0.45, sm = true)
        } else if (fem) sh(pt(-4.4, -2.0, -5.4, 4.0, -5.0, 10.6, 5.0, 10.6, 5.4, 4.0, 4.4, -2.0), hr, 0.5, sm = true)
        if (hood) sh(pt(0.0, -6.8, -5.8, -3.0, -6.0, 3.6, -4.6, 6.0, 4.6, 6.0, 6.0, 3.6, 5.8, -3.0), alt, 0.5, sm = true)
        if (k == "soldier") el(0.0, 0.6, 5.2, 5.6, MAIL, 0.45)
        el(0.0, 0.2, if (kid) 4.2 else 3.8, if (kid) 4.3 else 4.6, sk, 0.55)
        if (!fem && !hood && k != "soldier") {
            if (k == "priest" || k == "monk") {
                sh(pt(-4.0, 1.4, -4.2, -2.6, -3.0, -3.8, -2.2, -2.8, -3.0, 1.4), hr, 0.4); sh(pt(4.0, 1.4, 4.2, -2.6, 3.0, -3.8, 2.2, -2.8, 3.0, 1.4), hr, 0.4)
                fade(0.6) { ln(pt(-2.6, -3.6, 0.0, -4.4, 2.6, -3.6), hr, 0.6, true) }
            } else sh(pt(-4.3, 2.6, -4.6, -2.0, -3.0, -4.6, 0.0, -5.2, 3.0, -4.6, 4.6, -2.0, 4.3, 2.6, 3.6, -0.2, 3.2, -2.4, -3.2, -2.4, -3.6, -0.2), hr, 0.5, sm = true)
        } else if (fem && !wimp) sh(pt(-4.0, 1.0, -4.2, -2.0, -2.6, -4.4, 0.0, -4.8, 2.6, -4.4, 4.2, -2.0, 4.0, 1.0, 2.6, -2.4, 0.0, -3.0, -2.6, -2.4), hr, 0.45, sm = true)
        if (fem && (k == "bride" || kid && a >= 6)) for (i in 0 until 7) { val an = PI * (1.1 + i * 0.13); dot(cos(an) * 4, -0.6 + sin(an) * 4.4, 0.65, if (i % 2 == 1) WL else VM) }
        if (hood) sh(pt(-4.6, 1.0, -4.4, -2.8, 0.0, -5.0, 4.4, -2.8, 4.6, 1.0, 3.8, -1.2, 0.0, -2.6, -3.8, -1.2), alt, 0.45, sm = true)
        if (k == "soldier") { el(0.0, -3.8, 6.4, 1.2, hex(0xFFA7ACB4), 0.45, false); sh(pt(-4.4, -3.8, -4.0, -6.8, 0.0, -8.2, 4.0, -6.8, 4.4, -3.8), hex(0xFFC0C5CC), 0.45, sm = true) }
        if (k == "barin") {
            el(0.0, -4.4, 5.4, 2.0, VM, 0.45); sh(pt(-3.6, -5.0, -3.0, -8.6, 3.0, -8.6, 3.6, -5.0), VM, 0.45)
            ln(pt(4.8, -4.0, 6.6, 4.0, 6.0, 9.0), EI, 1.5, true); ln(pt(4.8, -4.0, 6.6, 4.0, 6.0, 9.0), VM, 0.9, true)
        }
        val ey = -0.4
        for (s in intArrayOf(-1, 1)) { dot(s * 1.5, ey, 0.45, EI); ln(pt(s * 0.8, ey - 1.3, s * 1.6, ey - 1.7, s * 2.4, ey - 1.3), EI, 0.3, true); fade(0.35) { dot(s * 2.2, 1.6, 0.9, RS) } }
        ln(pt(0.0, -0.6, 0.5, 1.6, -0.1, 1.9), EI, 0.3)
        if (old) fade(0.5) { ln(pt(-1.4, -2.4, 1.4, -2.4), EI, 0.25) }
        if (!fem && !kid && a >= 22 && k != "soldier" && k != "monk") {
            val len = if (old) 4.2 else if (k == "priest") 2.4 else 1.2 + sd.mod(3) * 0.6
            sh(pt(-3.6, 1.0, -3.2, 3.4, -1.2, 5.2 + len * 0.6, 0.0, 6 + len, 1.2, 5.2 + len * 0.6, 3.2, 3.4, 3.6, 1.0, 2.0, 2.4, 0.0, 2.2, -2.0, 2.4), hr, 0.45, sm = true)
            ln(pt(-0.7, 3.2, 0.7, 3.2), VM, 0.5)
        } else ln(pt(-0.7, 3.0, 0.0, 3.2, 0.7, 3.0), VM, 0.5, true)
        restore()
    }
    fun Ctx.pbg(bg: PBg) = when (bg) {
        PBg.HEAD -> gold(0.0, 0.0, 24.0, 24.0, null)
        PBg.BLOOD -> diaper(0.0, 0.0, 24.0, 24.0, UM, UM2, 4.0)
        PBg.INLAW -> diaper(0.0, 0.0, 24.0, 24.0, VM, hex(0xFFA8301F), 4.0)
    }

    /* scenes */
    fun Ctx.scene(key: String, d: SceneData, f: Double) = style {
        val W = 176.0; val Hh = 100.0
        val sp = d.spouse; val xp = d.other; val hl = d.hero
        val HZ = 62.0; val HL = hex(0xFF9FB6A0); val GR = hex(0xFF7AA65A)
        when (key) {
            "forest" -> {
                sky(W, HZ + 4, "day", f); hill(W, HZ, HL, 1, 8.0, 0.03); ground(W, Hh, HZ + 3, GR, 4, flowers = intArrayOf(WL, VM))
                listOf(Triple(130.0, 72.0, 30.0), Triple(30.0, 74.0, 32.0), Triple(150.0, 78.0, 42.0), Triple(10.0, 80.0, 40.0), Triple(166.0, 82.0, 36.0))
                    .forEachIndexed { i, (a, b, h2) -> if (i % 2 == 1) cypr(a, b, h2) else ltree(a, b, h2, "summer") }
                boar(120.0, 94.0, f); figure(hl, 62.0, 98.0)
                ln(pt(70.0, 62.0, 103.0, 74.0), EI, 1.3); ln(pt(70.0, 62.0, 100.0, 72.8), BR, 0.7); sh(pt(100.0, 72.0, 106.0, 75.0, 101.0, 74.6), hex(0xFFCFD4D8), 0.4)
            }
            "fire" -> {
                sky(W, HZ + 6, "night", f); hill(W, HZ + 4, hex(0xFF56627E), 2, 6.0, 0.04); ground(W, Hh, HZ + 6, hex(0xFF4A5A42), 8)
                house(104.0, 90.0, 60.0, f, lit = true); figure(hl, 40.0, 98.0); figure(sp, 54.0, 98.0)
                nightWash(W, Hh, 0.42, pt(104.0, 58.0, 72.0, 1.2, 0.0, 104.0, 84.0, 34.0, 0.5, 0.0)); nstars(W, HZ, f)
                for (k in 0 until 6) flames(80.0 + k * 9, 66 - abs(k - 2.5) * 4, 4.0, 11.0 + (k % 2) * 4, f + k)
                smoke(100.0, 38.0, f, 6, true)
            }
            "fair" -> {
                sky(W, HZ, "day", f); hill(W, HZ - 2, HL, 5, 6.0, 0.025); castle(150.0, HZ + 1, 0.42, f); ground(W, Hh, HZ, hex(0xFFC9A46A), 6)
                booth(6.0, 84.0, 30.0, VM); booth(64.0, 80.0, 30.0, UM); booth(124.0, 84.0, 30.0, VG)
                figure(npc("woman", 3), 46.0, 92.0); figure(npc("man", 4), 112.0, 94.0, true); figure(npc("kid", 5), 100.0, 96.0); figure(hl, 78.0, 99.0)
            }
            "road" -> {
                sky(W, HZ, "dusk", f); hill(W, HZ, hex(0xFF8F9A7A), 3, 9.0, 0.03); ground(W, Hh, HZ + 1, GR, 9, flowers = intArrayOf(WL))
                sh(pt(93.0, HZ + 1.5, 99.0, HZ + 1.5, 116.0, Hh + 2, 36.0, Hh + 2), hex(0xFFCFB07A), 0.5)
                house(24.0, 72.0, 34.0, f, lit = true); sh(pt(150.0, 90.0, 150.0, 70.0, 152.0, 70.0, 152.0, 90.0), SA, 0.45)
                ln(pt(146.0, 74.0, 156.0, 74.0), EI, 0.9); ln(pt(151.0, 70.0, 151.0, 66.0), EI, 0.9)
                figure(npc("beggar", 7), 118.0, 94.0, true); figure(hl, 72.0, 98.0)
            }
            "field", "field_dry", "klad", "meja" -> {
                val dry = key == "field_dry"
                sky(W, HZ - 6, if (dry) "gray" else "day", f); hill(W, HZ - 6, if (dry) hex(0xFFA8A690) else HL, 4, 7.0, 0.03); if (!dry) castle(30.0, HZ - 4, 0.35, f)
                field(W, Hh, HZ - 3, dry, f); if (!dry) { sheaf(150.0, 86.0); sheaf(160.0, 90.0); sheaf(140.0, 92.0) }
                if (key == "klad") pot(118.0, 94.0, f)
                if (key == "meja") { el(120.0, 80.0, 4.0, 3.2, SB, 0.5); ln(pt(90.0, 66.0, 164.0, 86.0), hex(0xFF8A7A5A), 0.5, true) }
                if (dry) for (k in 0 until 3) crow(40.0 + k * 30 + sin(f * 0.5 + k) * 6, 20.0 + k * 6, f + k)
                figure(hl, 60.0, 96.0); figure(xp, 78.0, 96.0)
            }
            "church" -> {
                sky(W, HZ, "day", f); hill(W, HZ - 2, HL, 6, 6.0, 0.03); ground(W, Hh, HZ, GR, 12, flowers = intArrayOf(WL)); church(92.0, 86.0, 1.15)
                cypr(18.0, 86.0, 40.0); ltree(162.0, 86.0, 36.0, "summer")
                figure(hl, 52.0, 98.0); figure(npc("priest", 9), 132.0, 98.0, true)
            }
            "interior" -> { interior(W, Hh, f); figure(hl, 34.0, 98.0); if (xp != null) figure(xp, 108.0, 98.0, true) else figure(sp, 108.0, 98.0, true) }
            "wedding" -> {
                interior(W, Hh, f); candle(52.0, 70.0, f); candle(86.0, 70.0, f)
                still { for (i in 0 until 6) { val ph = (f * 0.15 + i / 6.0) % 1; fade(1 - ph) { star6(40.0 + i * 14 + sin(ph * 6 + i) * 3, 10 + ph * 50, 1.0, if (i % 2 == 1) VM else GD) } } }
                figure(hl, 34.0, 98.0); figure(npc("bride", 13), 108.0, 98.0, true)
            }
            "night_int" -> {
                interior(W, Hh, f, night = true)
                ln(pt(94.0, 0.0, 95.0, 57.0), EI, 0.35); ln(pt(104.0, 0.0, 103.0, 57.0), EI, 0.35)
                sh(pt(92.0, 56.0, 106.0, 56.0, 105.0, 61.0, 93.0, 61.0), hex(0xFFC8A070), 0.5); quad(92.0, 55.0, 14.0, 1.4, WL, 0.35)
                figure(hl, 34.0, 98.0); if (xp != null && xp.age < 14) figure(xp, 99.0, 74.0)
                nightWash(W, Hh, 0.36, pt(67.0, 61.0, 56.0, 0.95, 0.0, 146.0, 68.0, 34.0, 0.6, 0.0, 118.0, 22.0, 18.0, 0.2, 1.0)); candle(66.0, 70.0, f); hearth(f)
            }
            "tavern" -> {
                interior(W, Hh, f, tavern = true, night = true, notable = true)
                quad(60.0, 72.0, 40.0, 3.0, BR, 0.5); quad(62.0, 75.0, 2.0, 10.0, BR2, 0.35); quad(96.0, 75.0, 2.0, 10.0, BR2, 0.35)
                sh(pt(69.0, 72.0, 68.0, 68.0, 69.4, 66.0, 70.6, 66.0, 72.0, 68.0, 71.0, 72.0), hex(0xFFB8643A), 0.4)
                figure(hl, 48.0, 98.0); figure(npc("man", 21), 114.0, 98.0, true); figure(npc("soldier", 22), 138.0, 98.0, true)
                nightWash(W, Hh, 0.4, pt(83.0, 63.0, 62.0, 1.0, 0.0, 130.0, 40.0, 44.0, 0.35, 0.0)); candle(82.0, 72.0, f)
            }
            "winter" -> {
                sky(W, HZ, "night", f); hill(W, HZ, hex(0xFFA6B2D0), 1, 8.0, 0.03); ground(W, Hh, HZ, hex(0xFFF2F0EA), 2, snow = true)
                for ((a, b, h2) in listOf(Triple(120.0, 70.0, 26.0), Triple(136.0, 74.0, 36.0), Triple(168.0, 76.0, 32.0), Triple(152.0, 80.0, 42.0))) cypr(a, b, h2, true)
                house(32.0, 80.0, 50.0, f, lit = true, snow = true, chimney = true); fence(58.0, 100.0, 86.0, true)
                wolf(122.0, 92.0, f + 1, true); wolf(96.0, 96.0, f, false); figure(hl, 62.0, 100.0)
                nightWash(W, Hh, 0.36, pt(38.0, 66.0, 30.0, 0.9, 0.0, 146.0, 16.0, 120.0, 0.3, 1.0)); nstars(W, HZ, f)
                wolf(122.0, 92.0, f + 1, true, true); wolf(96.0, 96.0, f, false, true); snowfall(W, Hh, f, 40)
            }
            "war" -> {
                sky(W, HZ, "dusk", f); hill(W, HZ, hex(0xFF8F8A6A), 8, 7.0, 0.03); ground(W, Hh, HZ, hex(0xFF8A8A5C), 5); castle(150.0, HZ + 2, 0.4, f)
                smoke(30.0, 50.0, f, 6, true); ln(pt(80.0, 92.0, 80.0, 48.0), EI, 0.9)
                still {
                    val p = Pb().add(80.4, 48.0)
                    for (i in 1..8) p.add(80.4 + i * 2, 48 + sin(f * 2.2 + i * 0.5) * 1.2 * (i / 8.0))
                    for (i in 8 downTo 1) p.add(80.4 + i * 2, 58 + sin(f * 2.2 + i * 0.5) * 1.2 * (i / 8.0) - (if (i > 6) (i - 6) * 2.0 else 0.0))
                    p.add(80.4, 58.0); sh(p.arr(), UM, 0.45, false)
                    for (k in 0 until 3) star6(85.0 + k * 4, 52.0 + (k % 2) * 2, 1.1, GD)
                }
                for (i in 0 until 6) figure(npc("soldier", 30 + i), 88.0 + i * 14, 92.0 + (i % 2) * 2, true)
                figure(hl, 36.0, 98.0); figure(xp, 54.0, 98.0)
            }
            "plague" -> {
                sky(W, HZ, "gray", f); hill(W, HZ, hex(0xFF9A9C96), 4, 6.0, 0.03); ground(W, Hh, HZ, hex(0xFF7F8062), 7)
                house(30.0, 80.0, 46.0, f, dark = true, smoky = true); ltree(156.0, 84.0, 40.0, "winter"); ltree(64.0, 78.0, 26.0, "winter")
                for ((a, b) in listOf(96.0 to 86.0, 128.0 to 86.0, 112.0 to 90.0, 144.0 to 92.0, 104.0 to 96.0)) cross(a, b)
                for (k in 0 until 4) crow(30.0 + k * 36 + sin(f * 0.6 + k) * 5, 14.0 + k * 5 + sin(f * 0.9 + k) * 1.5, f + k)
                figure(hl, 70.0, 98.0); figure(npc("monk", 61), 88.0, 97.0, true)
            }
            "barin" -> {
                sky(W, HZ, "day", f); hill(W, HZ - 2, HL, 9, 6.0, 0.03); ground(W, Hh, HZ, GR, 15, flowers = intArrayOf(WL, VM)); castle(118.0, 86.0, 1.15, f)
                cypr(8.0, 84.0, 40.0); cypr(170.0, 86.0, 36.0); figure(npc("barin", 41), 100.0, 97.0, true); figure(hl, 52.0, 98.0)
            }
            "dream" -> {
                interior(W, Hh, f, night = true); figure(hl, 40.0, 98.0)
                nightWash(W, Hh, 0.28, pt(112.0, 70.0, 46.0, 0.75, 1.0, 146.0, 68.0, 26.0, 0.45, 0.0)); hearth(f)
                save(); translate(0.0, sin(f * 1.2)); figure(npc("man", 51), 112.0, 94.0, true, ghost = true); restore()
            }
            "dawn" -> {
                sky(W, HZ, "dawn", f); hill(W, HZ, hex(0xFF9A9AB6), 7, 8.0, 0.03); ground(W, Hh, HZ, GR, 9, flowers = intArrayOf(WL, GD))
                house(36.0, 84.0, 54.0, f, chimney = true, lit = true, tile = true); ltree(150.0, 88.0, 40.0, "spring")
                var xx = 84.0; for (p in d.family.take(5)) { figure(p, xx, 99.0); xx += if (p.age < 14) 11 else 15 }
            }
            "pyre" -> {
                sky(W, HZ, "dusk", f); hill(W, HZ, hex(0xFF7F7A6A), 3, 7.0, 0.03); church(36.0, HZ + 4, 0.55); ground(W, Hh, HZ, hex(0xFF8F8A5C), 11)
                house(150.0, 76.0, 36.0, f); pyre(96.0, 90.0, f)
                figure(npc("woman", 71), 62.0, 96.0); figure(npc("man", 72), 128.0, 96.0, true); figure(npc("priest", 73), 144.0, 97.0, true)
                figure(npc("soldier", 74), 112.0, 94.0, true); figure(hl, 44.0, 99.0)
            }
            else -> { // village, and anything unknown
                sky(W, HZ, "day", f); hill(W, HZ - 2, HL, 2, 7.0, 0.03); church(150.0, HZ + 2, 0.5); ground(W, Hh, HZ, GR, 3, flowers = intArrayOf(WL, VM, UM))
                house(96.0, 76.0, 40.0, f, chimney = true, tile = true); house(26.0, 80.0, 44.0, f, smoky = true); ltree(64.0, 80.0, 30.0, "summer"); well(140.0, 88.0); chicken(120.0, 96.0, f)
                figure(hl, 84.0, 99.0); figure(xp, 100.0, 99.0)
            }
        }
        paper(0.6); frame(W, Hh)
    }

    fun Ctx.home(d: HomeData, f: Double) = style {
        val W = 176.0; val Hh = 96.0; val s = if (d.over) "winter" else SEASONS[d.season.mod(4)]; val nightM = d.over; val snow = s == "winter"; val HZ = 58.0
        sky(W, HZ, if (nightM) "night" else if (snow) "gray" else if (s == "autumn") "dawn" else "day", f)
        hill(W, HZ, if (snow) hex(0xFFC4CCD8) else if (s == "autumn") hex(0xFFB8A070) else hex(0xFF9FB6A0), d.seed, 8.0, 0.03)
        ground(W, Hh, HZ + 1, when { snow -> hex(0xFFF2F0EA); s == "autumn" -> hex(0xFFC9A85A); s == "spring" -> hex(0xFF97C06A); else -> hex(0xFF7AA65A) }, 21,
            snow = snow, flowers = if (s == "summer") intArrayOf(WL, VM, UM) else if (s == "spring") intArrayOf(WL, GD) else null)
        ltree(164.0, 86.0, 40.0, s); if (!snow) cypr(8.0, 86.0, 28.0)
        if (d.dvor >= 3) mill(140.0, 80.0, f, snow)
        if (d.dvor >= 2) barn(112.0, 82.0, 22.0, snow)
        if (d.dvor >= 4) towerHouse(52.0, 82.0, 64.0, nightM)
        else house(48.0, 82.0, if (d.dvor == 0) 44.0 else 54.0, f, chimney = d.dvor >= 1, smoky = d.dvor == 0, dark = d.dvor == 0, tile = d.dvor >= 2, snow = snow, lit = nightM || snow)
        if (!d.over) {
            var xx = 86.0; for (p in d.family.take(7)) { figure(p, xx, 95.0); xx += if (p.age < 14) 10 else 13 }
            if (s == "summer" || s == "spring") { chicken(24.0, 94.0, f); chicken(34.0, 92.0, f + 2, true) }
        }
        if (nightM) { nightWash(W, Hh, 0.35, pt(48.0, 66.0, 26.0, 0.6, 0.0)); nstars(W, HZ, f) }
        if (snow) snowfall(W, Hh, f, 26)
        paper(0.6); frame(W, Hh)
    }
}
