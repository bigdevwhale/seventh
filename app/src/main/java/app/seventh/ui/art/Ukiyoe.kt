package app.seventh.ui.art

import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.Shader
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/* Japan art: ukiyo-e woodblock print (bokashi skies, flat colour, thin key line, woodgrain, cartouche and seal) */
internal object Jp {
    val SUMI = hex(0xFF2A2420); val PB = hex(0xFF1D3B6A); val AI = hex(0xFF2C3C5C); val AI2 = hex(0xFF4A6A96)
    val SKYP = hex(0xFF9FBFD6); val WASHI = hex(0xFFF1E7CF); val GOF = hex(0xFFF8F2E4); val BENI = hex(0xFFC2382C)
    val BENI2 = hex(0xFFA8302A); val SHU = hex(0xFFD4512E); val KIN = hex(0xFFC9A24A); val MATSU = hex(0xFF2F5641)
    val MOEGI = hex(0xFF8FAE5A); val SAKURA = hex(0xFFF0C4C6); val MOMIJI = hex(0xFFC8462E); val STRAW = hex(0xFFD2B06A)
    val WOOD = hex(0xFF6E4A30); val WOOD2 = hex(0xFF4A3122); val ROOF = hex(0xFF5B5F66); val PLASTER = hex(0xFFEFE9DC)
    val STONE = hex(0xFFAAA59A); val LIT = hex(0xFFF3D27A); val THATCH = hex(0xFFC9A462); val SHOJI = hex(0xFFEAE2CC)
    val GRASS = hex(0xFF8FA85E); val SNOW = hex(0xFFF4F2EC)

    fun mix(a: Int, b: Int, t: Double): Int {
        fun ch(s: Int) = ((((a shr s) and 255) * (1 - t) + ((b shr s) and 255) * t) + 0.5).toInt()
        return (a and -0x1000000) or (ch(16) shl 16) or (ch(8) shl 8) or ch(0)
    }
    fun al(c: Int, a: Double) = (c and 0xFFFFFF) or ((a * 255 + 0.5).toInt() shl 24)

    inline fun Ctx.style(fn: () -> Unit) {
        val j = J; val lw = LW; val reg = REG; val ink = INK
        J = 0.06; LW = 0.42; REG = false; INK = SUMI
        try { fn() } finally { J = j; LW = lw; REG = reg; INK = ink }
    }

    private val shaders = HashMap<String, Shader>()
    fun vgrad(key: String, y0: Double, y1: Double, cols: IntArray, stops: FloatArray? = null): Shader = shaders.getOrPut("$key|$y0|$y1") {
        LinearGradient(0f, y0.toFloat(), 0f, y1.toFloat(), cols, stops, Shader.TileMode.CLAMP)
    }
    inline fun Ctx.batch(col: Int, w: Double, fn: () -> Unit) { beginPath(); fn(); stroke(col, w) }
    fun Ctx.seg(x0: Double, y0: Double, x1: Double, y1: Double) { moveTo(x0, y0); lineTo(x1, y1) }
    fun stadium(x0: Double, x1: Double, y: Double, h: Double): DoubleArray {
        val r = h / 2; val p = Pb(48)
        for (i in 0..8) { val a = PI / 2 + i * PI / 8; p.add(x0 + r + cos(a) * r, y + sin(a) * r) }
        for (i in 0..8) { val a = -PI / 2 + i * PI / 8; p.add(x1 - r + cos(a) * r, y + sin(a) * r) }
        return p.arr()
    }

    /* sky, mist, cloud, sun, water, land */
    private val SKIES = mapOf(
        "day" to intArrayOf(PB, hex(0xFF5F86B0), hex(0xFFC9D9DC), hex(0xFFF0E6CC), hex(0xFFE9DCB4)),
        "dusk" to intArrayOf(AI, hex(0xFF5E5878), hex(0xFFC4705C), hex(0xFFEFA66C), hex(0xFFF3CB8C)),
        "night" to intArrayOf(hex(0xFF121A30), hex(0xFF1E2A48), AI, hex(0xFF3E5078), hex(0xFF5C6E92)),
        "dawn" to intArrayOf(hex(0xFF34507E), hex(0xFF8C98AE), hex(0xFFEBD8B4), hex(0xFFF2B88C), hex(0xFFE7845E)),
        "gray" to intArrayOf(hex(0xFF5C6268), hex(0xFF8E9094), hex(0xFFC4C1B4), hex(0xFFDDD6C2), hex(0xFFE2DAC4)),
        "snow" to intArrayOf(hex(0xFF2E3442), hex(0xFF525866), hex(0xFF8A8C92), hex(0xFFB4B2AA), hex(0xFFCBC7BC)),
    )
    private val SKYST = floatArrayOf(0f, 0.16f, 0.45f, 0.78f, 1f)
    fun Ctx.sky(W: Double, hz: Double, k: String, f: Double) {
        fillShader(vgrad("sky$k", 0.0, hz + 3, SKIES.getValue(k), SKYST), 0.0, 0.0, W, hz + 3)
        when (k) {
            "day" -> {
                kasumi(-20 + sin(f * 0.07) * 6, hz * 0.62, 70.0, 4.2, al(hex(0xFFF6EAD0), 0.9))
                kasumi(W - 66 + sin(f * 0.05 + 2) * 6, hz * 0.5, 80.0, 3.6, al(hex(0xFFF6EAD0), 0.85))
                cloud(((24 + f * 1.2) % (W + 70)) - 35, 16.0, 34.0, 1.5, GOF)
                cloud(((110 + f * 0.8) % (W + 70)) - 35, 27.0, 26.0, 1.2, GOF)
            }
            "dusk" -> {
                sun(30.0, hz - 9, 7.0)
                kasumi(-10 + sin(f * 0.06) * 5, hz - 4, 90.0, 3.4, al(hex(0xFFF0B482), 0.9))
                cloud(((90 + f * 0.9) % (W + 70)) - 35, 15.0, 36.0, 1.5, hex(0xFFE9B9A0))
            }
            "dawn" -> {
                sun(W - 46, hz - 2, 8.0)
                kasumi(W - 120 + sin(f * 0.06) * 5, hz - 7, 110.0, 3.6, al(hex(0xFFF7D9B0), 0.92))
                cloud(((20 + f * 0.9) % (W + 70)) - 35, 17.0, 32.0, 1.4, hex(0xFFF3DDC6))
            }
            "night" -> { stars(W, hz, f); moon(44.0, 16.0, 5.6) }
            else -> {}
        }
    }
    fun Ctx.kasumi(x: Double, y: Double, w: Double, h: Double, col: Int) { poly(stadium(x, x + w, y, h), true, false); fill(col) }
    /** Hokusai cloud: a band with curled ends, and a smaller one on top */
    fun Ctx.cloud(x: Double, y: Double, len: Double, s: Double, col: Int) {
        for ((dx, dy, l) in listOf(Triple(0.0, 0.0, len), Triple(len * 0.22, -2.6 * s, len * 0.5))) {
            val x0 = x + dx; val yy = y + dy; val h = 3.2 * s
            sh(stadium(x0, x0 + l, yy, h), col, 0.35)
            for (e in intArrayOf(-1, 1)) {
                val cx = if (e < 0) x0 + h / 2 else x0 + l - h / 2; val p = Pb()
                for (i in 0..14) { val t = i / 14.0; val a = -PI / 2 + e * t * 2.6 * PI; val r = h * 0.42 * (1 - t * 0.75); p.add(cx + cos(a) * r * e, yy + sin(a) * r) }
                ln(p.arr(), SUMI, 0.38, true)
            }
        }
    }
    fun Ctx.sun(x: Double, y: Double, r: Double) {
        fillShader(RadialGradient(x.toFloat(), y.toFloat(), (r * 2.4).toFloat(), intArrayOf(al(hex(0xFFF2A070), 0.45), al(hex(0xFFF2A070), 0.0)), null, Shader.TileMode.CLAMP), x - r * 2.4, y - r * 2.4, r * 4.8, r * 4.8)
        el(x, y, r, r, BENI, 0.0, n = 28)
    }
    fun Ctx.stars(W: Double, hz: Double, f: Double) {
        val r = Rng(31)
        for (i in 0 until 26) { val x = r.next() * W; val y = 4 + r.next() * hz * 0.7; val s = 0.3 + r.next() * 0.35; dot(x, y, s * (0.75 + 0.25 * sin(f * 1.4 + i * 2.1)), al(hex(0xFFF3E9C8), 0.85)) }
    }
    fun Ctx.moon(x: Double, y: Double, r: Double) {
        fillShader(RadialGradient(x.toFloat(), y.toFloat(), (r * 3).toFloat(), intArrayOf(al(hex(0xFFF3E7C2), 0.35), al(hex(0xFFF3E7C2), 0.0)), null, Shader.TileMode.CLAMP), x - r * 3, y - r * 3, r * 6, r * 6)
        el(x, y, r, r, hex(0xFFF3E7C2), 0.3, n = 28)
    }
    fun Ctx.snowfall(W: Double, H: Double, f: Double, n: Int) {
        val r = Rng(78)
        for (i in 0 until n) {
            val bx = r.next() * W; val by = r.next() * H; val sp = 2.4 + r.next() * 3; val s = 0.45 + r.next() * 0.5
            dot(bx + sin(f * 0.9 + i) * 2.4, (by + f * sp) % H, s, GOF)
        }
    }
    /** distant ranges: sharp ridges, graded toward the mist at their feet */
    fun Ctx.range(W: Double, base: Double, col: Int, seed: Int, amp: Double, freq: Double) {
        val r = Rng(seed); val p1 = r.next() * 6; val p2 = r.next() * 6; val p = Pb()
        var x = -6.0
        while (x <= W + 6) { p.add(x, base - amp * (0.62 * abs(sin(x * freq + p1)).pow(0.8) + 0.38 * (0.5 + 0.5 * sin(x * freq * 2.3 + p2)))); x += 4 }
        val pts = p.arr()
        poly(p.add(W + 6, base + 12).add(-6.0, base + 12).arr(), true, true)
        fillS(vgrad("rg$col", base - amp, base + 2, intArrayOf(col, mix(col, WASHI, 0.3))))
        ln(pts, mix(col, SUMI, 0.5), 0.3, true)
        fillShader(vgrad("mist", base - 3.5, base + 2.5, intArrayOf(al(WASHI, 0.0), al(WASHI, 0.55), al(WASHI, 0.0)), floatArrayOf(0f, 0.6f, 1f)), 0.0, base - 3.5, W, 6.0)
    }
    fun Ctx.fuji(x: Double, base: Double, w: Double, h: Double, body: Int = hex(0xFF3D5F8E)) {
        val P = pt(x - w / 2, base, x - w * 0.13, base - h * 0.88, x - w * 0.05, base - h, x + w * 0.06, base - h * 0.98, x + w * 0.13, base - h * 0.87, x + w / 2, base)
        poly(P, true, false); fillS(vgrad("fuji$body", base - h, base, intArrayOf(mix(body, SUMI, 0.25), body, mix(body, WASHI, 0.6)), floatArrayOf(0f, 0.45f, 1f)))
        sh(P, 0, 0.35)
        val cap = pt(x - w * 0.13, base - h * 0.88, x - w * 0.05, base - h, x + w * 0.06, base - h * 0.98, x + w * 0.13, base - h * 0.87, x + w * 0.19, base - h * 0.72,
            x + w * 0.13, base - h * 0.74, x + w * 0.1, base - h * 0.63, x + w * 0.05, base - h * 0.71, x, base - h * 0.58, x - w * 0.04, base - h * 0.7,
            x - w * 0.09, base - h * 0.6, x - w * 0.12, base - h * 0.73, x - w * 0.19, base - h * 0.71)
        sh(cap, SNOW, 0.3)
        batch(al(SKYP, 0.9), 0.3) { for (i in -2..2) seg(x + i * w * 0.025, base - h * 0.95, x + i * w * 0.06, base - h * 0.74) }
    }
    fun Ctx.sea(W: Double, y0: Double, y1: Double, f: Double) {
        fillShader(vgrad("sea", y0, y1, intArrayOf(mix(SKYP, WASHI, 0.35), AI2, PB), floatArrayOf(0f, 0.35f, 1f)), 0.0, y0, W, y1 - y0)
        var y = y0 + 1.4; var k = 0
        while (y < y1) {
            val r = 0.8 + k * 0.45; val sp = r * 2.6; val off = ((f * (0.6 + k * 0.2) + k * 1.7) % sp)
            batch(al(GOF, 0.7), 0.25 + k * 0.05) { var x = -sp + off; while (x < W + sp) { arc(x, y + r, r, PI, 2 * PI, true); x += sp } }
            y += 1.6 + k * 1.1; k++
        }
    }
    fun Ctx.ground(W: Double, H: Double, y0: Double, col: Int, seed: Int, tufts: Boolean = true, flowers: IntArray? = null, snow: Boolean = false) {
        val r = Rng(seed); val p = Pb()
        var x = -6.0; while (x <= W + 6) { p.add(x, y0 + sin(x * 0.05 + seed) * 1.0); x += 6 }
        val pts = p.arr(); val G = p.add(W + 6, H + 3).add(-6.0, H + 3).arr()
        poly(G, true, false); fill(col)
        inside(G) { fillShader(vgrad("gb$col", y0 - 1, y0 + 12, intArrayOf(mix(col, if (snow) hex(0xFF8FA2C0) else SUMI, if (snow) 0.4 else 0.22), al(col, 0.0))), 0.0, y0 - 2, W, 14.0) }
        ln(pts, SUMI, 0.45, true)
        val n = jr(W * (H - y0) / 80).toInt()
        if (snow) batch(al(hex(0xFF8FA2C0), 0.7), 0.3) { repeat(n) { val gx = r.next() * W; val gy = y0 + 4 + r.next() * (H - y0 - 4); moveTo(gx - 1.6, gy); quadTo(gx, gy - 0.6, gx + 1.6, gy) } }
        else if (tufts) batch(al(mix(col, SUMI, 0.55), 0.75), 0.28) {
            repeat(n) { val gx = r.next() * W; val gy = y0 + 4 + r.next() * (H - y0 - 4); seg(gx, gy, gx - 0.7, gy - 1.8); seg(gx, gy, gx + 0.1, gy - 2.2); seg(gx, gy, gx + 0.9, gy - 1.6) }
        }
        if (flowers != null) for (i in 0 until n / 3) {
            val fx = r.next() * W; val fy = y0 + 5 + r.next() * (H - y0 - 5); val fc = flowers[i % flowers.size]
            for (k in 0 until 5) { val a = k / 5.0 * 6.28; dot(fx + cos(a) * 0.7, fy + sin(a) * 0.7, 0.5, fc) }
            dot(fx, fy, 0.3, KIN)
        }
    }
    fun Ctx.paddies(W: Double, y0: Double, H: Double, f: Double, dry: Boolean) {
        val ys = doubleArrayOf(y0, y0 + 3.5, y0 + 8.0, y0 + 14.5, y0 + 23.5, y0 + 36.0, H + 4)
        val bank = hex(0xFF7D8A4C)
        for (b in 0 until ys.size - 1) {
            val t = ys[b]; val bot = ys[b + 1]; if (t > H + 2) break
            if (dry) fillRect(0.0, t, W, bot - t, mix(hex(0xFFC2A472), hex(0xFFAE8D5C), b / 6.0))
            else fillShader(vgrad("pad$b", t, bot, intArrayOf(hex(0xFFB9D0D8), hex(0xFFDDE5D8))), 0.0, t, W, bot - t)
            val sx = 1.5 + b * 1.25; val th = 0.7 + b * 0.85; val rows = 1 + b / 2
            if (!dry) {
                batch(al(GOF, 0.85), 0.3) {
                    for (i in 0 until 3 + b) { val gx = ((i * 53 + b * 29 + sin(f * 0.5 + i) * 3) % W); val gy = t + (bot - t) * (0.3 + 0.5 * ((i * 7) % 5) / 5.0); seg(gx, gy, gx + 2 + b * 0.8, gy) }
                }
                batch(hex(0xFF4F7A3A), 0.22 + b * 0.07) {
                    for (row in 0 until rows) {
                        val ry = t + (bot - t) * (row + 1.0) / (rows + 0.6)
                        var x = -2.0 + (row % 2) * sx / 2
                        while (x < W + 2) { val sw = sin(f * 1.2 + x * 0.2) * th * 0.12; seg(x, ry, x + sw, ry - th); seg(x, ry, x - th * 0.4 + sw, ry - th * 0.75); seg(x, ry, x + th * 0.4 + sw, ry - th * 0.75); x += sx }
                    }
                }
            } else {
                val r = Rng(40 + b)
                batch(al(SUMI, 0.4), 0.25) { repeat(4 + b * 2) { var x = r.next() * W; var y = t + 0.6 + r.next() * (bot - t - 1.2); moveTo(x, y); repeat(3) { x += 1.2 + r.next() * 2; y += (r.next() - 0.5) * 1.4; lineTo(x, y) } } }
                batch(hex(0xFF8A6A3E), 0.25 + b * 0.05) { var x = 0.0; while (x < W) { val ry = bot - 0.6 - (x * 7 % 3) * 0.2; seg(x, ry, x + 0.2, ry - th * 0.4); x += sx * 1.6 } }
            }
            if (b > 0) { fillRect(0.0, t - 0.4, W, 0.5 + b * 0.3, bank); ln(pt(0.0, t - 0.4, W, t - 0.4), SUMI, 0.3) }
        }
        val vx = W / 2
        batch(bank, 1.0) { for (i in -5..5) seg(vx + i * 15, y0, vx + i * 54, H + 2) }
        batch(al(SUMI, 0.6), 0.25) { for (i in -5..5) seg(vx + i * 15 - 0.5, y0, vx + i * 54 - 1.4, H + 2) }
    }
    fun Ctx.hasa(x: Double, y: Double, w: Double) {
        quad(x - 0.5, y - 14, 1.0, 14.0, WOOD2, 0.3); quad(x + w - 0.5, y - 14, 1.0, 14.0, WOOD2, 0.3)
        ln(pt(x - 1.5, y - 12.4, x + w + 1.5, y - 12.4), WOOD2, 0.9)
        var i = 0.0
        while (i < w - 1) { el(x + 1.6 + i, y - 9.2, 1.0, 3.0, STRAW, 0.3, n = 10); i += 2.1 }
        batch(al(hex(0xFF9A7A3A), 0.9), 0.25) { i = 0.0; while (i < w - 1) { seg(x + 1.6 + i, y - 12, x + 1.6 + i, y - 6.6); i += 2.1 } }
    }

    /* trees */
    private fun pad(cx: Double, cy: Double, rx: Double): DoubleArray {
        val ry = rx * 0.42
        return pt(cx - rx, cy + ry * 0.3, cx - rx * 0.85, cy - ry * 0.3, cx - rx * 0.5, cy - ry * 0.75, cx - rx * 0.15, cy - ry * 0.55, cx + rx * 0.2, cy - ry * 0.95,
            cx + rx * 0.55, cy - ry * 0.62, cx + rx * 0.85, cy - ry * 0.35, cx + rx, cy + ry * 0.3, cx, cy + ry * 0.45)
    }
    fun Ctx.pine(x: Double, y: Double, h: Double, lean: Double = 0.0, snow: Boolean = false) {
        val bark = hex(0xFF6B4A34)
        sh(pt(x - 1.5, y, x - 1.0 + lean * 0.3, y - h * 0.4, x - 0.5 + lean * 0.8, y - h * 0.8, x + lean, y - h * 0.97, x + 0.6 + lean * 0.8, y - h * 0.8, x + 1.1 + lean * 0.3, y - h * 0.4, x + 1.5, y), bark, sm = true)
        batch(al(WOOD2, 0.7), 0.3) { for (i in 1..4) { val t = i / 5.0; val bx = x + lean * t * t; seg(bx - 0.8, y - h * t, bx + 0.2, y - h * t + 0.6) } }
        val pads = listOf(doubleArrayOf(lean * 0.95, -h * 0.97, h * 0.2), doubleArrayOf(-h * 0.24 + lean * 0.5, -h * 0.78, h * 0.18), doubleArrayOf(h * 0.24 + lean * 0.6, -h * 0.66, h * 0.2),
            doubleArrayOf(-h * 0.2 + lean * 0.3, -h * 0.5, h * 0.15), doubleArrayOf(h * 0.18 + lean * 0.2, -h * 0.38, h * 0.13))
        for (p in pads) { val t = -p[1] / h; ln(pt(x + lean * t * t, y + p[1] + 1, x + p[0], y + p[1]), WOOD2, 0.6) }
        for (p in pads) {
            val cx = x + p[0]; val cy = y + p[1]; val rx = p[2]
            sh(pad(cx, cy, rx), MATSU, 0.35, sm = true)
            batch(hex(0xFF5E8A5E), 0.25) { var i = -0.8; while (i <= 0.8) { val tx = cx + i * rx; seg(tx, cy - rx * 0.12, tx + 0.3, cy - rx * 0.3 + abs(i) * rx * 0.12); i += 0.2 } }
            if (snow) sh(pt(cx - rx * 0.85, cy - rx * 0.04, cx - rx * 0.5, cy - rx * 0.32, cx + rx * 0.2, cy - rx * 0.4, cx + rx * 0.85, cy - rx * 0.13, cx + rx * 0.6, cy, cx, cy - rx * 0.1, cx - rx * 0.5, cy + rx * 0.02), SNOW, 0.3, sm = true)
        }
    }
    fun Ctx.sugi(x: Double, y: Double, h: Double, snow: Boolean = false) {
        quad(x - 0.6, y - h * 0.25, 1.2, h * 0.25, WOOD2, 0.3)
        for (i in 0 until 5) {
            val t = i / 5.0; val yb = y - h * 0.18 - t * h * 0.66; val w = h * 0.17 * (1 - t * 0.7)
            sh(pt(x - w, yb, x - w * 0.2, yb - h * 0.2, x, yb - h * 0.3, x + w * 0.2, yb - h * 0.2, x + w, yb, x, yb - 1.0), hex(0xFF2C4A3A), 0.35)
            if (snow) sh(pt(x - w * 0.6, yb - h * 0.08, x, yb - h * 0.28, x + w * 0.6, yb - h * 0.08, x, yb - h * 0.14), SNOW, 0.25)
        }
    }
    /** 0 spring blossom, 1 summer leaves, 2 autumn maple, 3 winter bare (plum = red buds on the bare winter tree) */
    fun Ctx.cherry(x: Double, y: Double, h: Double, season: Int, plum: Boolean = false, f: Double = 0.0) {
        val bark = hex(0xFF4A3A32)
        sh(pt(x - 2.2, y, x - 1.4, y - h * 0.3, x - 0.4, y - h * 0.5, x + 1.2, y - h * 0.52, x + 1.0, y - h * 0.3, x + 2.0, y), bark, sm = true)
        val tips = Pb()
        fun br(x0: Double, y0: Double, a: Double, l: Double, d: Int) {
            if (d > 3) { tips.add(x0, y0); return }
            val x1 = x0 + cos(a) * l; val y1 = y0 + sin(a) * l
            ln(pt(x0, y0, (x0 + x1) / 2 + sin(d + a) * 0.6, (y0 + y1) / 2, x1, y1), bark, 1.5 - d * 0.32, true)
            br(x1, y1, a - 0.72, l * 0.74, d + 1); br(x1, y1, a + 0.62, l * 0.7, d + 1)
        }
        br(x + 0.3, y - h * 0.5, -PI / 2 - 0.1, h * 0.22, 0)
        val r = Rng(jr(x * 13 + h).toInt()); val n = tips.n / 2
        when (season) {
            0 -> {
                val cs = DoubleArray(n * 3); for (i in 0 until n) { cs[3 * i] = tips.a[2 * i] + (r.next() - 0.5) * 2; cs[3 * i + 1] = tips.a[2 * i + 1]; cs[3 * i + 2] = h * 0.085 + r.next() * h * 0.045 }
                blobs(cs, SAKURA, 0.25)
                for (i in 0 until n) for (k in 0 until 3) dot(cs[3 * i] + (r.next() - 0.5) * cs[3 * i + 2] * 1.4, cs[3 * i + 1] + (r.next() - 0.5) * cs[3 * i + 2], 0.35, if (k == 0) BENI else GOF)
                still { for (i in 0 until 5) { val ph = (f * 0.12 + i / 5.0) % 1; fade(1 - ph) { el(x - h * 0.3 + i * h * 0.15 + sin(ph * 7 + i) * 3, y - h * 0.6 + ph * h * 0.7, 0.6, 0.4, SAKURA, 0.2, false, 8) } } }
            }
            1 -> {
                val cs = DoubleArray(n * 3); for (i in 0 until n) { cs[3 * i] = tips.a[2 * i]; cs[3 * i + 1] = tips.a[2 * i + 1]; cs[3 * i + 2] = h * 0.095 + r.next() * h * 0.045 }
                blobs(cs, hex(0xFF5F8E4A), 0.3)
            }
            2 -> {
                val cs = DoubleArray(n * 3); for (i in 0 until n) { cs[3 * i] = tips.a[2 * i]; cs[3 * i + 1] = tips.a[2 * i + 1]; cs[3 * i + 2] = h * 0.085 + r.next() * h * 0.045 }
                blobs(cs, MOMIJI, 0.3)
                for (i in 0 until n) dot(cs[3 * i] + 0.6, cs[3 * i + 1] - 0.5, cs[3 * i + 2] * 0.45, hex(0xFFE39A3E))
            }
            else -> {
                if (plum) for (i in 0 until n) for (k in 0 until 2) dot(tips.a[2 * i] + (r.next() - 0.5) * 2, tips.a[2 * i + 1] + (r.next() - 0.5) * 2, 0.55, BENI)
                else batch(SNOW, 0.6) { for (i in 0 until n) seg(tips.a[2 * i] - 0.8, tips.a[2 * i + 1] - 0.3, tips.a[2 * i] + 0.6, tips.a[2 * i + 1] - 0.5) }
            }
        }
    }
    fun Ctx.bamboo(x: Double, y: Double, h: Double, n: Int, f: Double) {
        val r = Rng(jr(x * 5).toInt())
        still {
            for (i in 0 until n) {
                val sx = x + i * 2.8 + (r.next() - 0.5); val hh = h * (0.75 + r.next() * 0.25); val sw = sin(f * 0.7 + i) * 0.6
                sh(pt(sx - 0.55, y, sx + 0.55, y, sx + 0.45 + sw, y - hh, sx - 0.45 + sw, y - hh), hex(0xFF6F9A52), 0.3)
                batch(hex(0xFF3E6A36), 0.35) { var t = 0.14; while (t < 1) { val nx = sx + sw * t; seg(nx - 0.6, y - hh * t, nx + 0.6, y - hh * t); t += 0.14 } }
                for (k in 0 until 5) {
                    val t = 0.55 + k * 0.1; val lx = sx + sw * t; val ly = y - hh * t; val s = if (k % 2 == 0) 1.0 else -1.0; val len = 3.2 + r.next() * 1.4
                    save(); translate(lx, ly); rotate(s * (0.5 + r.next() * 0.5)); if (s < 0) scale(-1.0, 1.0)
                    sh(pt(0.0, 0.0, len * 0.45, -0.55, len, 0.2, len * 0.45, 0.35), hex(0xFF4E7A3E), 0.25, sm = true); restore()
                }
            }
        }
    }

    /* buildings */
    fun Ctx.minka(x: Double, y: Double, w: Double, f: Double, lit: Boolean = false, dark: Boolean = false, snow: Boolean = false, smoky: Boolean = false, vent: Boolean = false) {
        val hh = w * 0.34; val L = x - w / 2; val T = y - hh; val rh = w * 0.44; val ov = w * 0.1
        quad(L, T, w, hh, if (dark) hex(0xFF5E4C3C) else WOOD, 0.4)
        val np = maxOf(2, jr(w / 10).toInt()); val pw = w / np
        for (i in 0 until np) {
            val px = L + i * pw
            if (i == 1) { quad(px + 1, T + 1.4, pw - 2, hh - 1.4, hex(0xFF2A221C), 0.3); continue }
            quad(px + 1, T + 1.4, pw - 2, hh * 0.62, if (lit) LIT else if (dark) hex(0xFF8F7F6A) else SHOJI, 0.3, false)
            batch(al(WOOD2, 0.8), 0.22) { var gx = px + 1 + (pw - 2) / 3; while (gx < px + pw - 1.2) { seg(gx, T + 1.4, gx, T + 1.4 + hh * 0.62); gx += (pw - 2) / 3 }; var gy = T + 1.4 + hh * 0.62 / 3; while (gy < T + 1.4 + hh * 0.6) { seg(px + 1, gy, px + pw - 1, gy); gy += hh * 0.62 / 3 } }
        }
        batch(WOOD2, 0.7) { for (i in 0..np) seg(L + i * pw, T, L + i * pw, y) }
        quad(L - 1, y - 2.2, w + 2, 2.2, WOOD2, 0.35)
        val thatch = if (dark) hex(0xFF7A6A4E) else THATCH
        val R = pt(L - ov, T + 1.2, x - w * 0.26, T - rh, x + w * 0.26, T - rh, L + w + ov, T + 1.2)
        sh(R, thatch, 0.45)
        inside(R) {
            batch(al(mix(thatch, SUMI, 0.45), 0.55), 0.22) { var i = -1.0; while (i <= 1.0) { seg(x + i * w * 0.26, T - rh, x + i * (w / 2 + ov), T + 1.2); i += 0.035 } }
            fillShader(vgrad("thb$thatch", T - rh, T + 1.2, intArrayOf(al(SUMI, 0.0), al(SUMI, 0.22))), L - ov, T - rh, w + 2 * ov, rh + 1.2)
        }
        sh(pt(L - ov, T + 1.2, L + w + ov, T + 1.2, L + w + ov - 1.2, T + 3.0, L - ov + 1.2, T + 3.0), mix(thatch, SUMI, 0.3), 0.35)
        quad(x - w * 0.28, T - rh - 2.2, w * 0.56, 2.6, if (dark) hex(0xFF4A4238) else hex(0xFF3E3A34), 0.35)
        batch(hex(0xFF3E3A34), 0.5) { var i = -0.22; while (i <= 0.23) { val cx = x + i * w; seg(cx - 1, T - rh - 3.2, cx + 1, T - rh + 0.2); seg(cx + 1, T - rh - 3.2, cx - 1, T - rh + 0.2); i += 0.11 } }
        if (snow) {
            val p = Pb(64).add(L - ov - 0.5, T + 1.4).add(x - w * 0.27, T - rh - 2.6).add(x + w * 0.27, T - rh - 2.6).add(L + w + ov + 0.5, T + 1.4)
            var t = 1.0; while (t >= -1.0) { p.add(x + t * (w / 2 + ov), T + 0.4 - (1 - abs(t)) * rh * 0.55 + 2.2 + sin(t * 17) * 0.6); t -= 0.1 }
            sh(p.arr(), SNOW, 0.4, sm = false)
        }
        if (vent) smoke(x - w * 0.18, T - rh - 2, f, 5)
        if (smoky) smoke(x + w * 0.1, T - rh * 0.6, f, 6, true)
    }
    fun Ctx.tileRoof(x0: Double, x1: Double, y: Double, rh: Double, ov: Double, snow: Boolean = false) {
        val R = pt(x0 - ov - 1.2, y - 0.8, x0 - ov + 1, y + 0.6, x1 + ov - 1, y + 0.6, x1 + ov + 1.2, y - 0.8, x1 - rh * 0.4, y - rh, x0 + rh * 0.4, y - rh)
        sh(R, ROOF, 0.4)
        inside(R) { batch(al(hex(0xFF3A3D44), 0.8), 0.25) { var gx = x0 - ov; while (gx < x1 + ov) { seg(gx, y - rh, gx, y + 1); gx += 1.4 } } }
        quad(x0 + rh * 0.3, y - rh - 1.4, x1 - x0 - rh * 0.6, 1.6, hex(0xFF3A3D44), 0.3)
        if (snow) sh(pt(x0 - ov - 1.2, y - 0.8, x0 + rh * 0.4, y - rh - 1.6, x1 - rh * 0.4, y - rh - 1.6, x1 + ov + 1.2, y - 0.8, x1, y - rh * 0.35, (x0 + x1) / 2, y - rh * 0.5, x0, y - rh * 0.35), SNOW, 0.35)
    }
    fun Ctx.noren(x: Double, y: Double, w: Double, h: Double, col: Int, f: Double) {
        val n = 3; val pw = w / n
        still {
            for (i in 0 until n) {
                val sw = sin(f * 1.3 + i) * 0.4; val px = x + i * pw
                sh(pt(px + 0.15, y, px + pw - 0.15, y, px + pw - 0.15 + sw, y + h, px + 0.15 + sw, y + h), col, 0.3)
            }
            beginPath(); circle(x + w / 2 + sin(f * 1.3 + 1) * 0.2, y + h * 0.45, h * 0.22); stroke(GOF, 0.45)
        }
    }
    fun Ctx.machiya(x: Double, y: Double, w: Double, f: Double, lit: Boolean = false, snow: Boolean = false, sake: Boolean = false) {
        val L = x - w / 2; val h1 = w * 0.26; val h2 = w * 0.2; val T1 = y - h1; val T2 = T1 - 4 - h2
        quad(L + 2, T2, w - 4, h2 + 4, PLASTER, 0.4)
        for (i in 0 until 3) { val wx = L + w * (0.2 + i * 0.3); quad(wx - 3, T2 + 2.4, 6.0, h2 * 0.5, hex(0xFF3A3530), 0.3); batch(PLASTER, 0.5) { var gx = wx - 2.2; while (gx < wx + 3) { seg(gx, T2 + 2.4, gx, T2 + 2.4 + h2 * 0.5); gx += 1.1 } } }
        tileRoof(L + 2, L + w - 2, T2, 4.6, 2.5, snow)
        quad(L, T1, w, h1, WOOD, 0.4)
        if (lit) quad(L + 1, T1 + 1, w - 2, h1 - 2.6, LIT, 0.0, false)
        batch(WOOD2, 0.45) { var gx = L + 1; while (gx < L + w - 1) { seg(gx, T1 + 1, gx, y - 1.6); gx += 1.2 } }
        quad(L - 1, y - 1.6, w + 2, 1.6, WOOD2, 0.3)
        tileRoof(L, L + w, T1, 4.0, 2.4, snow)
        noren(x - w * 0.12, T1 + 0.4, w * 0.3, h1 * 0.42, AI, f)
        if (sake) {
            ln(pt(x + w * 0.3, T1 + 0.6, x + w * 0.3, T1 + 2.6), SUMI, 0.4)
            el(x + w * 0.3, T1 + 5.0, 2.6, 2.6, hex(0xFF6E7A3E), 0.4, n = 18)
            batch(al(hex(0xFF3E4A22), 0.7), 0.25) { for (i in 0 until 8) { val a = i / 8.0 * 2 * PI; seg(x + w * 0.3, T1 + 5, x + w * 0.3 + cos(a) * 2.4, T1 + 5 + sin(a) * 2.4) } }
        }
    }
    fun Ctx.komodaru(x: Double, y: Double, s: Double = 1.0) {
        sh(pt(x - 3.4 * s, y, x - 3.8 * s, y - 3.5 * s, x - 3.4 * s, y - 7 * s, x + 3.4 * s, y - 7 * s, x + 3.8 * s, y - 3.5 * s, x + 3.4 * s, y), hex(0xFFEDE3C6), 0.35, sm = true)
        batch(hex(0xFF8A6A3A), 0.4) { seg(x - 3.6 * s, y - 1.2 * s, x + 3.6 * s, y - 1.2 * s); seg(x - 3.6 * s, y - 5.8 * s, x + 3.6 * s, y - 5.8 * s) }
        quad(x - 1.6 * s, y - 5.2 * s, 3.2 * s, 3.4 * s, BENI, 0.25, false)
        batch(GOF, 0.25) { seg(x, y - 4.8 * s, x, y - 2.2 * s); seg(x - 0.8 * s, y - 4 * s, x + 0.8 * s, y - 4 * s) }
        el(x, y - 7 * s, 3.4 * s, 0.8 * s, hex(0xFFD8C8A0), 0.3, false)
    }
    fun Ctx.kura(x: Double, y: Double, w: Double, snow: Boolean = false) {
        val h = w * 0.78; val L = x - w / 2; val T = y - h
        quad(L, T, w, h, PLASTER, 0.45)
        val B = pt(L, y - h * 0.32, L + w, y - h * 0.32, L + w, y, L, y)
        sh(B, hex(0xFF3A3A40), 0.4)
        inside(B) { batch(al(GOF, 0.85), 0.35) { var i = -h; while (i < w + h) { seg(L + i, y, L + i + h * 0.32, y - h * 0.32); seg(L + i, y - h * 0.32, L + i + h * 0.32, y); i += 2.4 } } }
        quad(x - w * 0.18, y - h * 0.62, w * 0.36, h * 0.62, hex(0xFF2E2E34), 0.35)
        quad(x - 2, T + h * 0.14, 4.0, 3.4, hex(0xFF2E2E34), 0.3)
        tileRoof(L + 0.5, L + w - 0.5, T, w * 0.26, 1.6, snow)
        sh(pt(L - 2.4, T - w * 0.2, L - 1.4, T - w * 0.27, L + 0.4, T - w * 0.2), hex(0xFF3A3D44), 0.3)
        sh(pt(L + w - 0.4, T - w * 0.2, L + w + 1.4, T - w * 0.27, L + w + 2.4, T - w * 0.2), hex(0xFF3A3D44), 0.3)
    }
    fun Ctx.torii(x: Double, y: Double, h: Double, snow: Boolean = false) {
        val w = h * 0.95; val c = SHU
        for (s in intArrayOf(-1, 1)) { val px = x + s * w * 0.32; sh(pt(px - 1.1, y, px + 1.1, y, px + 0.85 - s * 0.4, y - h * 0.88, px - 0.85 - s * 0.4, y - h * 0.88), c, 0.35); quad(px - 1.3, y - 2.2, 2.6, 2.2, SUMI, 0.3) }
        quad(x - w * 0.42, y - h * 0.74, w * 0.84, h * 0.055, c, 0.35)
        quad(x - 0.9, y - h * 0.87, 1.8, h * 0.13, c, 0.3)
        sh(pt(x - w * 0.56, y - h * 0.93, x - w * 0.45, y - h * 0.87, x + w * 0.45, y - h * 0.87, x + w * 0.56, y - h * 0.93, x + w * 0.5, y - h * 0.92, x, y - h * 0.9, x - w * 0.5, y - h * 0.92), c, 0.35)
        sh(pt(x - w * 0.6, y - h * 0.985, x - w * 0.56, y - h * 0.93, x - w * 0.5, y - h * 0.92, x, y - h * 0.9, x + w * 0.5, y - h * 0.92, x + w * 0.56, y - h * 0.93, x + w * 0.6, y - h * 0.985, x, y - h * 0.955), SUMI, 0.3)
        if (snow) sh(pt(x - w * 0.58, y - h * 0.985, x - w * 0.3, y - h * 1.03, x + w * 0.3, y - h * 1.03, x + w * 0.58, y - h * 0.985, x, y - h * 0.97), SNOW, 0.3, sm = true)
    }
    fun Ctx.hokora(x: Double, y: Double, s: Double, snow: Boolean = false) {
        save(); translate(x, y); scale(s, s)
        quad(-8.0, -3.0, 16.0, 3.0, STONE, 0.4); quad(-6.6, -5.0, 13.2, 2.0, STONE, 0.35)
        quad(-5.0, -14.0, 10.0, 9.0, SHU, 0.4)
        quad(-3.4, -12.4, 6.8, 7.0, hex(0xFF3A2A22), 0.3); ln(pt(0.0, -12.4, 0.0, -5.4), KIN, 0.4)
        sh(pt(-9.4, -13.6, -8.4, -14.6, -3.0, -19.4, 3.0, -19.4, 8.4, -14.6, 9.4, -13.6, 6.0, -14.4, -6.0, -14.4), hex(0xFF4F7D6A), 0.4)
        batch(SUMI, 0.5) { seg(-3.6, -18.6, -5.2, -22.0); seg(-2.4, -18.6, -0.8, -22.0); seg(3.6, -18.6, 5.2, -22.0); seg(2.4, -18.6, 0.8, -22.0) }
        ln(pt(-6.0, -14.0, -3.0, -13.0, 0.0, -12.6, 3.0, -13.0, 6.0, -14.0), hex(0xFFD9C690), 0.9, true)
        for (i in -2..2) sh(pt(i * 2.4 - 0.5, -12.8, i * 2.4 + 0.5, -12.8, i * 2.4 - 0.2, -11.6, i * 2.4 + 0.6, -10.4, i * 2.4 - 0.4, -10.4), GOF, 0.2, false)
        if (snow) sh(pt(-9.6, -13.8, -3.0, -19.8, 3.0, -19.8, 9.6, -13.8, 5.0, -15.4, 0.0, -16.6, -5.0, -15.4), SNOW, 0.3, sm = true)
        restore()
    }
    fun Ctx.fox(x: Double, y: Double, s: Double, flip: Boolean, white: Boolean, f: Double = 0.0) {
        val c = if (white) GOF else hex(0xFFC8C3B6)
        save(); translate(x, y); scale(if (flip) -s else s, s)
        if (!white) quad(-4.4, -2.4, 8.8, 2.4, STONE, 0.3)
        val o = if (white) 0.0 else -2.4
        val tw = if (white) sin(f * 1.4) * 0.6 else 0.0
        sh(pt(2.2, -1.0 + o, 6.6 + tw, -4.0 + o, 7.4 + tw, -10.0 + o, 5.2 + tw, -12.8 + o, 4.8, -8.6 + o, 2.8, -4.0 + o), c, 0.35, sm = true)
        el(6.0 + tw, -11.6 + o, 1.0, 1.2, if (white) hex(0xFFE9E2D4) else STONE, 0.25, false, 10)
        sh(pt(-3.0, 0.0 + o, -3.4, -5.6 + o, -2.0, -9.8 + o, 1.6, -10.4 + o, 3.2, -6.0 + o, 3.2, 0.0 + o), c, 0.4, sm = true)
        sh(pt(-2.2, -9.6 + o, 2.2, -9.6 + o, 0.0, -6.6 + o), BENI, 0.3)
        val hy = -10.6 + o
        sh(pt(-2.8, hy, -3.0, hy - 3.2, -2.4, hy - 5.6, -1.2, hy - 3.6, 1.0, hy - 3.6, 2.0, hy - 5.6, 2.6, hy - 3.0, 2.0, hy, 0.0, hy + 1.6), c, 0.4)
        if (white) { batch(BENI, 0.45) { seg(-1.8, hy - 1.8, -0.6, hy - 1.4); seg(1.4, hy - 1.8, 0.4, hy - 1.4) }; dot(-2.0, hy - 4.2, 0.4, BENI); dot(1.6, hy - 4.2, 0.4, BENI) }
        else batch(SUMI, 0.35) { seg(-1.8, hy - 1.6, -0.6, hy - 1.3); seg(1.4, hy - 1.6, 0.4, hy - 1.3) }
        dot(0.0, hy + 1.2, 0.35, SUMI)
        restore()
    }
    fun Ctx.toro(x: Double, y: Double, s: Double, lit: Boolean = false, snow: Boolean = false) {
        save(); translate(x, y); scale(s, s)
        quad(-3.0, -1.6, 6.0, 1.6, STONE, 0.35); quad(-1.1, -9.0, 2.2, 7.4, STONE, 0.35); quad(-3.0, -10.6, 6.0, 1.6, STONE, 0.35)
        quad(-2.2, -15.0, 4.4, 4.4, STONE, 0.35); quad(-1.2, -14.0, 2.4, 2.4, if (lit) LIT else hex(0xFF3A3530), 0.3, false)
        sh(pt(-4.8, -14.6, -4.2, -15.2, 0.0, -18.4, 4.2, -15.2, 4.8, -14.6, 0.0, -15.6), STONE, 0.35)
        el(0.0, -19.2, 0.9, 1.1, STONE, 0.3)
        if (snow) sh(pt(-4.6, -15.0, 0.0, -18.8, 4.6, -15.0, 0.0, -16.6), SNOW, 0.3, sm = true)
        restore()
    }
    fun Ctx.pagoda(x: Double, y: Double, s: Double) {
        save(); translate(x, y); scale(s, s)
        quad(-11.0, -3.0, 22.0, 3.0, STONE, 0.4)
        for (i in 0 until 5) {
            val bw = 7.0 - i * 0.9; val rw = bw + 5.2 - i * 0.4; val y0 = -3.0 - i * 7.6
            quad(-bw, y0 - 5.4, bw * 2, 5.4, hex(0xFFB5502F), 0.35)
            quad(-bw * 0.35, y0 - 4.4, bw * 0.7, 3.6, hex(0xFF2E2622), 0.25, false)
            sh(pt(-rw - 0.8, y0 - 7.8, -rw + 1.6, y0 - 5.4, rw - 1.6, y0 - 5.4, rw + 0.8, y0 - 7.8, bw * 0.55, y0 - 9.6, -bw * 0.55, y0 - 9.6), ROOF, 0.4)
        }
        val top = -3.0 - 5 * 7.6 - 2
        ln(pt(0.0, top, 0.0, top - 13), SUMI, 0.8)
        for (k in 0 until 6) el(0.0, top - 2 - k * 1.6, 1.4 - k * 0.12, 0.35, KIN, 0.2, false)
        dot(0.0, top - 13.4, 0.8, KIN)
        restore()
    }
    fun Ctx.temple(x: Double, y: Double, s: Double) {
        save(); translate(x, y); scale(s, s)
        quad(-26.0, -4.0, 52.0, 4.0, STONE, 0.4); quad(-6.0, -2.0, 12.0, 2.0, STONE, 0.3)
        quad(-22.0, -21.0, 44.0, 17.0, PLASTER, 0.4)
        quad(-8.0, -16.0, 16.0, 12.0, hex(0xFF3A2A22), 0.35)
        batch(al(KIN, 0.8), 0.3) { seg(0.0, -16.0, 0.0, -4.0); for (i in 0 until 3) seg(-8.0, -13.0 + i * 3.4, 8.0, -13.0 + i * 3.4) }
        for (px in doubleArrayOf(-21.0, -13.0, -8.6, 7.4, 12.0, 20.0)) quad(px, -21.0, 1.6, 17.0, hex(0xFFB5502F), 0.3)
        quad(-23.0, -22.6, 46.0, 1.8, hex(0xFFB5502F), 0.3)
        val R = pt(-35.0, -21.0, -32.0, -23.2, -21.0, -32.0, 21.0, -32.0, 32.0, -23.2, 35.0, -21.0, 30.0, -22.0, -30.0, -22.0)
        sh(R, ROOF, 0.45)
        inside(R) { batch(al(hex(0xFF3A3D44), 0.8), 0.25) { var gx = -36.0; while (gx < 36) { seg(gx * 0.62, -32.0, gx, -21.0); gx += 1.6 } } }
        sh(pt(-12.0, -32.0, 0.0, -38.6, 12.0, -32.0), ROOF, 0.4)
        sh(pt(-8.6, -33.0, 0.0, -37.4, 8.6, -33.0), PLASTER, 0.3)
        quad(-22.0, -34.0, 44.0, 2.2, hex(0xFF3A3D44), 0.3)
        for (e in intArrayOf(-1, 1)) sh(pt(e * 21.0, -33.8, e * 23.6, -37.6, e * 22.4, -33.8), KIN, 0.3)
        restore()
    }
    fun Ctx.castle(x: Double, y: Double, s: Double, f: Double = 0.0) {
        save(); translate(x, y); scale(s, s)
        val base = pt(-27.0, 0.0, -22.0, -13.0, 22.0, -13.0, 27.0, 0.0)
        sh(base, STONE, 0.45)
        inside(base) { batch(al(hex(0xFF6E6A62), 0.8), 0.25) { for (j in 1 until 5) seg(-28.0, -j * 2.6, 28.0, -j * 2.6); for (j in 0 until 5) { var i = -28.0 + (j % 2) * 2.2; while (i < 28) { seg(i, -j * 2.6, i, -(j + 1) * 2.6); i += 4.4 } } } }
        fun tier(w: Double, top: Double, h: Double, rw: Double) {
            quad(-w, top, 2 * w, h, PLASTER, 0.4)
            var wx = -w + 3; while (wx < w - 2) { quad(wx - 0.8, top + h * 0.35, 1.6, h * 0.35, hex(0xFF2E2E34), 0.25, false); wx += 4.2 }
            sh(pt(-rw - 1.6, top - 1.4, -rw + 1.2, top + 0.8, rw - 1.2, top + 0.8, rw + 1.6, top - 1.4, w * 0.75, top - 4.4, -w * 0.75, top - 4.4), ROOF, 0.4)
            sh(pt(-4.0, top - 2.0, 0.0, top - 6.4, 4.0, top - 2.0), ROOF, 0.35); sh(pt(-2.6, top - 2.2, 0.0, top - 5.0, 2.6, top - 2.2), PLASTER, 0.25)
        }
        tier(18.0, -24.0, 11.0, 21.0); tier(13.0, -33.6, 9.6, 16.0); tier(8.6, -42.0, 8.4, 11.0)
        quad(-6.0, -49.0, 12.0, 2.2, hex(0xFF3A3D44), 0.3)
        for (e in intArrayOf(-1, 1)) sh(pt(e * 6.4, -48.4, e * 8.0, -51.6, e * 6.6, -51.0, e * 5.4, -48.6), KIN, 0.3)
        restore()
    }
    fun Ctx.stall(x: Double, y: Double, w: Double, col: Int, f: Double) {
        quad(x + 1, y - 26, 1.2, 26.0, WOOD2, 0.3); quad(x + w - 2.2, y - 26, 1.2, 26.0, WOOD2, 0.3)
        quad(x, y - 10, w, 10.0, WOOD, 0.4); quad(x + 1, y - 8.6, w - 2, 7.2, hex(0xFF8A6244), 0.3, false)
        for (i in 0 until 5) { val gx = x + 3 + i * (w - 6) / 4; el(gx, y - 11.2, 1.5, 1.2, if (i % 2 == 0) hex(0xFFE7C9A0) else hex(0xFF9EB86A), 0.3); dot(gx, y - 13.4, 0.8, if (i % 2 == 0) BENI else GOF) }
        sh(pt(x - 2.0, y - 26.0, x + w + 2, y - 26.0, x + w + 0.6, y - 29.0, x - 0.6, y - 29.0), WOOD2, 0.35)
        noren(x + 0.6, y - 26, w - 1.2, 6.0, col, f)
        chochin(x + w - 0.6, y - 16.6, 2.2, BENI, f)
    }
    fun Ctx.chochin(x: Double, y: Double, r: Double, col: Int, f: Double = 0.0) {
        val sw = sin(f * 1.6 + x) * 0.25
        save(); translate(x, y); rotate(sw * 0.1)
        ln(pt(0.0, -r - 2.4, 0.0, -r), SUMI, 0.3)
        el(0.0, 0.0, r * 0.78, r, col, 0.3, n = 18)
        batch(al(SUMI, 0.45), 0.2) { for (k in -2..2) { val yy = k * r * 0.33; val hw = r * 0.78 * kotlin.math.sqrt(1 - (yy / r) * (yy / r)); seg(-hw, yy, hw, yy) } }
        quad(-r * 0.45, -r - 0.6, r * 0.9, 0.8, SUMI, 0.0); quad(-r * 0.45, r - 0.2, r * 0.9, 0.8, SUMI, 0.0)
        batch(SUMI, 0.35) { seg(-0.4, -r * 0.4, 0.4, -r * 0.2); seg(0.0, -r * 0.1, 0.0, r * 0.4); seg(-0.5, r * 0.2, 0.5, r * 0.15) }
        restore()
    }
    fun Ctx.nobori(x: Double, y: Double, h: Double, col: Int, f: Double) {
        ln(pt(x, y, x, y - h), WOOD2, 0.7); ln(pt(x, y - h + 0.6, x + 4.4, y - h + 0.6), WOOD2, 0.5)
        still {
            val p = Pb()
            for (i in 0..6) { val t = i / 6.0; p.add(x + 4.4 + sin(f * 2 + t * 4) * 0.4 * t, y - h + 0.6 + t * h * 0.55) }
            for (i in 6 downTo 0) { val t = i / 6.0; p.add(x + 0.3 + sin(f * 2 + t * 4) * 0.4 * t, y - h + 0.6 + t * h * 0.55) }
            sh(p.arr(), col, 0.3)
            beginPath(); circle(x + 2.4 + sin(f * 2 + 1.2) * 0.15, y - h + 4.6, 1.2); stroke(GOF, 0.45)
        }
    }
    fun Ctx.sotoba(x: Double, y: Double, h: Double) {
        sh(pt(x - 0.9, y, x + 0.9, y, x + 0.9, y - h + 1.6, x + 0.3, y - h + 1.2, x + 0.6, y - h + 0.6, x, y - h, x - 0.6, y - h + 0.6, x - 0.3, y - h + 1.2, x - 0.9, y - h + 1.6), hex(0xFFD8C8A2), 0.3)
        batch(al(SUMI, 0.75), 0.25) { var t = y - h + 3; while (t < y - 2) { seg(x - 0.3, t, x + 0.3, t + 0.6); t += 1.8 } }
    }
    fun Ctx.jizo(x: Double, y: Double) {
        el(x, y - 3.6, 2.6, 3.8, STONE, 0.35); el(x, y - 8.6, 1.9, 2.0, STONE, 0.35)
        sh(pt(x - 2.4, y - 6.6, x + 2.4, y - 6.6, x, y - 3.2), BENI, 0.3)
        batch(SUMI, 0.25) { seg(x - 0.9, y - 8.8, x - 0.3, y - 8.7); seg(x + 0.3, y - 8.7, x + 0.9, y - 8.8) }
    }
    fun Ctx.tsuboKoban(x: Double, y: Double, f: Double) {
        sh(pt(x - 3.2, y, x - 4.8, y - 3.6, x - 4.4, y - 7.0, x - 2.4, y - 8.6, x + 2.4, y - 8.6, x + 4.4, y - 7.0, x + 4.8, y - 3.6, x + 3.2, y), hex(0xFF7A5A3E), 0.4, sm = true)
        fillShader(vgrad("tsubo", y - 8.6, y, intArrayOf(al(GOF, 0.25), al(SUMI, 0.25))), x - 5, y - 8.6, 10.0, 8.6)
        el(x, y - 8.6, 2.6, 0.8, hex(0xFF2A221C), 0.3, false)
        for ((dx, dy) in listOf(-1.6 to -9.4, 0.8 to -10.0, 2.0 to -9.2, 6.2 to -0.8, 8.2 to -1.4)) el(x + dx, y + dy, 1.4, 0.8, KIN, 0.25, false, 12)
        val tw = (sin(f * 3) + 1) / 2
        still { save(); translate(x + 1.4, y - 12.4); scale(0.5 + tw * 0.6, 0.5 + tw * 0.6); sh(pt(0.0, -2.4, 0.5, -0.5, 2.4, 0.0, 0.5, 0.5, 0.0, 2.4, -0.5, 0.5, -2.4, 0.0, -0.5, -0.5), GOF, 0.2, false); restore() }
    }
    fun Ctx.jWell(x: Double, y: Double) {
        quad(x - 4.6, y - 6.0, 9.2, 6.0, WOOD, 0.4); batch(al(WOOD2, 0.8), 0.3) { seg(x - 4.6, y - 3.0, x + 4.6, y - 3.0) }
        quad(x - 4.2, y - 18.0, 1.0, 12.0, WOOD2, 0.3); quad(x + 3.2, y - 18.0, 1.0, 12.0, WOOD2, 0.3)
        sh(pt(x - 6.0, y - 17.6, x - 5.0, y - 19.6, x + 5.0, y - 19.6, x + 6.0, y - 17.6), ROOF, 0.35)
        dot(x, y - 16.4, 0.9, WOOD2); ln(pt(x, y - 16.4, x, y - 10.0), SUMI, 0.25)
        quad(x - 1.2, y - 10.0, 2.4, 2.2, hex(0xFF9A7448), 0.3)
    }
    fun Ctx.bambooFence(x0: Double, x1: Double, y: Double, snow: Boolean = false) {
        var x = x0 + 1.5
        while (x < x1) { quad(x - 0.45, y - 8.6, 0.9, 8.6, hex(0xFFB6A15E), 0.25); x += 3.2 }
        for (k in 0 until 3) { val yy = y - 2.2 - k * 2.8; quad(x0, yy - 0.5, x1 - x0, 1.0, hex(0xFFA8944E), 0.25); if (snow && k == 2) quad(x0, yy - 1.4, x1 - x0, 0.9, SNOW, 0.2) }
        batch(al(SUMI, 0.7), 0.3) { var xx = x0 + 1.5; while (xx < x1) { for (k in 0 until 3) { val yy = y - 2.2 - k * 2.8; seg(xx - 0.7, yy - 0.6, xx + 0.7, yy + 0.6) }; xx += 3.2 } }
    }
    fun Ctx.cranes(x: Double, y: Double, f: Double) {
        still {
            for (i in 0 until 3) {
                val a = sin(f * 3 + i * 1.7); val bx = x + i * 9 + ((f * 2) % 40); val by = y + i * 3 + sin(f * 0.6 + i) * 1.2
                ln(pt(bx - 3, by - 1.6 * a, bx, by, bx + 3, by - 1.6 * a), SUMI, 0.45, true); dot(bx + 0.4, by + 0.2, 0.4, SUMI)
            }
        }
    }

    /* the frame of a print: woodgrain, a title cartouche and seals, the key-line border */
    fun Ctx.grain(W: Double, H: Double) = cached("grain", 0.0, 0.0, W, H) {
        val r = Rng(5)
        batch(rgba(80, 52, 28, 0.07), 0.3) {
            repeat(54) {
                val y0 = r.next() * H; val p1 = r.next() * 6; val p2 = r.next() * 6; val a = 0.6 + r.next() * 1.4
                var x = -2.0; moveTo(x, y0)
                while (x < W + 2) { x += 3; lineTo(x, y0 + sin(x * 0.045 + p1) * a + sin(x * 0.011 + p2) * 2.6) }
            }
            repeat(5) { val kx = r.next() * W; val ky = r.next() * H; oval(kx, ky, 2.4 + r.next() * 2, 0.8 + r.next() * 0.6); oval(kx, ky, 1.2, 0.4) }
        }
    }
    fun Ctx.cartouche(x: Double, y: Double, h: Double, seed: Int, col: Int = hex(0xFFEFE2B4)) {
        quad(x, y, 7.0, h, col, 0.45); strokeRect(x + 0.8, y + 0.8, 5.4, h - 1.6, SUMI, 0.22)
        val r = Rng(seed)
        batch(SUMI, 0.42) {
            var gy = y + 2.0
            while (gy < y + h - 5) {
                val n = 3 + (r.next() * 3).toInt()
                repeat(n) {
                    val sx = x + 1.6 + r.next() * 3.2; val sy = gy + r.next() * 3.4; val v = r.next() < 0.5
                    if (v) seg(sx, sy, sx + (r.next() - 0.5) * 0.6, sy + 1.2 + r.next()) else seg(sx, sy, sx + 1.2 + r.next(), sy + (r.next() - 0.5) * 0.6)
                }
                gy += 4.6
            }
        }
    }
    fun Ctx.seal(x: Double, y: Double, s: Double, seed: Int) {
        quad(x, y, s, s, BENI, 0.0)
        val r = Rng(seed)
        batch(al(GOF, 0.9), s * 0.09) { repeat(4) { val a = r.next(); if (a < 0.5) seg(x + s * 0.2, y + s * (0.2 + r.next() * 0.6), x + s * 0.8, y + s * (0.2 + r.next() * 0.6)) else seg(x + s * (0.2 + r.next() * 0.6), y + s * 0.2, x + s * (0.2 + r.next() * 0.6), y + s * 0.8) } }
        strokeRect(x + s * 0.1, y + s * 0.1, s * 0.8, s * 0.8, al(GOF, 0.8), s * 0.06)
    }
    fun Ctx.printFrame(W: Double, H: Double, key: String, right: Boolean = true) {
        val sd = key.hashCode()
        val cx = if (right) W - 13.0 else 6.0
        cartouche(cx, 6.0, 26.0, sd)
        seal(cx + 1.3, 33.4, 4.4, sd + 1)
        el(if (right) 9.0 else W - 9.0, H - 9.0, 1.8, 2.2, BENI, 0.0, n = 16)
        grain(W, H)
        paper(0.55)
        val m = hex(0xFFEFE5CB)
        fillRect(0.0, 0.0, W, 2.0, m); fillRect(0.0, H - 2.0, W, 2.0, m); fillRect(0.0, 0.0, 2.0, H, m); fillRect(W - 2.0, 0.0, 2.0, H, m)
        paper(0.5, 0.0, 0.0, W, 2.0); paper(0.5, 0.0, H - 2.0, W, 2.0); paper(0.5, 0.0, 2.0, 2.0, H - 4); paper(0.5, W - 2.0, 2.0, 2.0, H - 4)
        strokeRect(2.0, 2.0, W - 4.0, H - 4.0, SUMI, 0.7)
    }

    /* interiors */
    fun Ctx.room(W: Double, Hh: Double, f: Double, night: Boolean = false, tavern: Boolean = false, notable: Boolean = false) {
        val wall = if (tavern) hex(0xFF8A6448) else hex(0xFFD9CDB2)
        fillRect(0.0, 0.0, W, 76.0, wall)
        if (tavern) batch(al(WOOD2, 0.5), 0.3) { var x = 0.0; while (x < W) { seg(x, 0.0, x, 76.0); x += 5.5 } }
        quad(-1.0, 4.0, W + 2, 3.0, WOOD2, 0.4); quad(-1.0, 58.0, W + 2, 2.6, WOOD2, 0.4)
        for (px in doubleArrayOf(4.0, 58.0, 118.0, 172.0)) quad(px - 1.2, 0.0, 2.4, 76.0, WOOD, 0.35)
        if (!tavern) {
            // tokonoma: alcove, hanging scroll, a vase with a branch
            quad(8.0, 10.0, 46.0, 62.0, hex(0xFFCFC0A0), 0.35)
            quad(8.0, 66.0, 46.0, 6.0, WOOD, 0.35)
            quad(23.0, 13.0, 16.0, 44.0, hex(0xFFF0E6CE), 0.35); quad(22.0, 12.0, 18.0, 1.6, WOOD2, 0.25); quad(22.0, 56.4, 18.0, 1.6, WOOD2, 0.25)
            ln(pt(25.0, 44.0, 28.0, 36.0, 31.0, 40.0, 34.0, 32.0, 37.0, 44.0), SUMI, 0.45); dot(33.0, 22.0, 2.2, BENI)
            batch(al(SUMI, 0.7), 0.3) { seg(26.0, 48.0, 36.0, 48.0); seg(27.0, 50.4, 34.0, 50.4) }
            sh(pt(13.0, 66.0, 12.4, 62.0, 13.4, 60.0, 15.4, 60.0, 16.4, 62.0, 15.8, 66.0), hex(0xFF5A6E7A), 0.3, sm = true)
            ln(pt(14.4, 60.0, 13.0, 52.0, 16.0, 46.0), WOOD2, 0.45, true); for ((bx, by) in listOf(13.2 to 52.0, 15.6 to 47.0, 14.0 to 49.4)) dot(bx, by, 0.8, if (night) hex(0xFFB06070) else BENI)
            // shoji
            val sj = if (night) hex(0xFF56607A) else hex(0xFFF2EAD4)
            quad(60.0, 10.0, 110.0, 48.0, sj, 0.4)
            batch(al(WOOD2, 0.8), 0.25) { var x = 60.0; while (x <= 170) { seg(x, 10.0, x, 58.0); x += 5.5 }; var y = 10.0; while (y <= 58) { seg(60.0, y, 170.0, y); y += 6.0 } }
            batch(WOOD2, 0.6) { var x = 60.0; while (x <= 170) { seg(x, 10.0, x, 58.0); x += 27.5 } }
            if (!night) fillShader(vgrad("shj", 10.0, 58.0, intArrayOf(al(GOF, 0.0), al(hex(0xFFFFF4D8), 0.35))), 60.0, 10.0, 110.0, 48.0)
        } else {
            for (i in 0 until 9) { val tx = 64.0 + i * 11; quad(tx, 9.0, 6.0, 13.0, hex(0xFFEFE3C4), 0.3); batch(SUMI, 0.4) { seg(tx + 3, 11.0, tx + 3, 18.0); seg(tx + 1.8, 13.4, tx + 4.2, 13.0) } }
            quad(6.0, 6.0, 26.0, 70.0, hex(0xFF2A221C), 0.35)
            noren(6.0, 6.0, 26.0, 16.0, AI, f)
            for (i in 0 until 4) chochin(48.0 + i * 36, 34.0, 4.0, BENI, f + i)
        }
        // floor: tatami
        fillRect(0.0, 76.0, W, Hh - 76, hex(0xFFC9BE84)); ln(pt(0.0, 76.0, W, 76.0), SUMI, 0.6)
        if (tavern) fillRect(0.0, 76.0, W, Hh - 76, hex(0xFF7A6046))
        else {
            batch(al(hex(0xFF9A8E54), 0.8), 0.22) { var y = 77.0; while (y < Hh) { seg(0.0, y, W, y); y += 1.2 } }
            batch(hex(0xFF2E3A30), 1.0) { seg(0.0, 86.0, W, 86.0); for (x in doubleArrayOf(30.0, 98.0, 160.0)) seg(x, 76.0, x - (x - 88) * 0.25, 86.0); for (x in doubleArrayOf(60.0, 132.0)) seg(x, 86.0, x - (x - 88) * 0.4, Hh) }
        }
        if (!tavern) irori(140.0, 88.0, f)
        if (!notable && !tavern) { // a low table with tea
            quad(66.0, 78.0, 34.0, 2.4, hex(0xFF3A2620), 0.4); quad(68.0, 80.4, 2.0, 6.0, hex(0xFF3A2620), 0.3); quad(96.0, 80.4, 2.0, 6.0, hex(0xFF3A2620), 0.3)
            sh(pt(73.0, 78.0, 72.4, 74.6, 76.4, 74.6, 75.8, 78.0), hex(0xFF6E7A82), 0.3); sh(pt(80.0, 78.0, 79.4, 75.4, 82.6, 75.4, 82.0, 78.0), hex(0xFFE4DCC8), 0.3)
            el(91.0, 76.6, 3.4, 1.5, hex(0xFFB0563A), 0.3)
        }
    }
    fun Ctx.irori(x: Double, y: Double, f: Double) {
        ln(pt(x, 0.0, x, y - 14), SUMI, 0.4); quad(x - 0.4, y - 16, 0.8, 4.0, WOOD2, 0.2)
        sh(pt(x - 9.0, y - 2.6, x + 9.0, y - 2.6, x + 11.0, y + 2.6, x - 11.0, y + 2.6), hex(0xFF4A3A2E), 0.4)
        sh(pt(x - 7.6, y - 1.6, x + 7.6, y - 1.6, x + 9.0, y + 1.8, x - 9.0, y + 1.8), hex(0xFF9A948A), 0.3, false)
        flames(x, y + 0.6, 2.8, 5.0, f)
        sh(pt(x - 3.4, y - 6.0, x - 4.2, y - 9.4, x - 2.4, y - 12.0, x + 2.4, y - 12.0, x + 4.2, y - 9.4, x + 3.4, y - 6.0), hex(0xFF2E2A28), 0.35, sm = true)
        ln(pt(x + 3.6, y - 10.4, x + 6.0, y - 11.6), SUMI, 0.6)
    }
    fun Ctx.andon(x: Double, y: Double) {
        quad(x - 3.2, y - 1.2, 6.4, 1.2, WOOD2, 0.3)
        quad(x - 2.6, y - 13.0, 5.2, 10.0, LIT, 0.35)
        batch(al(WOOD2, 0.8), 0.25) { seg(x, y - 13.0, x, y - 3.0); seg(x - 2.6, y - 8.0, x + 2.6, y - 8.0) }
        quad(x - 3.0, y - 14.0, 6.0, 1.0, WOOD2, 0.25); batch(WOOD2, 0.45) { seg(x - 2.6, y - 3.0, x - 2.6, y - 1.2); seg(x + 2.6, y - 3.0, x + 2.6, y - 1.2) }
    }
    fun Ctx.byobu(x: Double, y: Double, w: Double, h: Double, f: Double) {
        cached("byobu$w$h", x, y, w, h) {
            val n = 6; val pw = w / n
            for (i in 0 until n) {
                val px = x + i * pw
                fillRect(px, y, pw, h, if (i % 2 == 0) hex(0xFFD8B65C) else hex(0xFFCCAA52))
                val r = Rng(70 + i)
                repeat(60) { dot(px + r.next() * pw, y + r.next() * h, 0.25 + r.next() * 0.3, al(if (r.next() < 0.5) hex(0xFFF3DC8E) else hex(0xFFA8792A), 0.7)) }
            }
            sh(pt(x + w * 0.04, y + h, x + w * 0.12, y + h * 0.78, x + w * 0.24, y + h * 0.7, x + w * 0.36, y + h * 0.76, x + w * 0.44, y + h), hex(0xFF7E8A5E), 0.3, sm = true)
            sh(pt(x + w * 0.22, y + h * 0.74, x + w * 0.2, y + h * 0.55, x + w * 0.26, y + h * 0.42, x + w * 0.33, y + h * 0.3, x + w * 0.35, y + h * 0.31, x + w * 0.29, y + h * 0.44, x + w * 0.25, y + h * 0.56, x + w * 0.26, y + h * 0.74), hex(0xFF5A4030), 0.3, sm = true)
            ln(pt(x + w * 0.22, y + h * 0.5, x + w * 0.15, y + h * 0.44), hex(0xFF5A4030), 0.8); ln(pt(x + w * 0.27, y + h * 0.4, x + w * 0.4, y + h * 0.33), hex(0xFF5A4030), 0.7)
            for ((cx, cy, rx) in listOf(Triple(x + w * 0.14, y + h * 0.43, w * 0.09), Triple(x + w * 0.41, y + h * 0.32, w * 0.1), Triple(x + w * 0.33, y + h * 0.22, w * 0.08))) sh(pad(cx, cy, rx), MATSU, 0.3, sm = true)
            fillShader(vgrad("byk", y + h * 0.7, y + h, intArrayOf(al(GOF, 0.0), al(hex(0xFFF3DC8E), 0.6))), x, y + h * 0.7, w, h * 0.3)
            batch(al(hex(0xFF8A6A2A), 0.9), 0.4) { for (i in 0..n) seg(x + i * pw, y, x + i * pw, y + h) }
            strokeRect(x, y, w, h, SUMI, 0.5)
        }
        for (i in 0 until 2) { val bx = x + w * 0.62 + i * w * 0.16 + sin(f * 0.4 + i) * 2; val by = y + h * 0.2 + i * 4; ln(pt(bx - 3.4, by - 1.0, bx, by, bx + 3.4, by - 1.0), SUMI, 0.5, true); el(bx, by + 0.4, 1.2, 0.6, GOF, 0.25, false) }
    }

    /* scenes */
    fun Ctx.scene(key: String, d: SceneData, f: Double) = style {
        val W = 176.0; val Hh = 100.0
        val sp = d.spouse; val xp = d.other; val hl = d.hero
        val HZ = 62.0; val GR = GRASS; var right = true
        when (key) {
            "forest" -> {
                sky(W, HZ + 4, "day", f); range(W, HZ - 1, hex(0xFF7D97B8), 1, 12.0, 0.035); ground(W, Hh, HZ + 3, GR, 4, flowers = intArrayOf(GOF, SAKURA))
                for ((a, b, h2) in listOf(Triple(126.0, 74.0, 32.0), Triple(140.0, 72.0, 38.0), Triple(166.0, 80.0, 44.0), Triple(152.0, 82.0, 40.0))) sugi(a, b, h2)
                bamboo(6.0, 86.0, 46.0, 6, f); pine(44.0, 78.0, 34.0, 4.0)
                bear(118.0, 95.0, f, hex(0xFF2A2624), hex(0xFF3E3632), hex(0xFF6E5A48))
                sh(pt(113.6, -21.2 + 95.0 + sin(f * 1.8) * 0.6, 118.0, -18.4 + 95.0 + sin(f * 1.8) * 0.6, 122.4, -21.2 + 95.0 + sin(f * 1.8) * 0.6, 118.0, -19.6 + 95.0 + sin(f * 1.8) * 0.6), GOF, 0.25, sm = true)
                hat = 1; figure(hl, 62.0, 98.0); hat = 0
                ln(pt(70.0, 60.0, 101.0, 73.0), SUMI, 1.2); ln(pt(70.0, 60.0, 98.0, 71.8), WOOD, 0.6); sh(pt(98.0, 71.0, 104.0, 74.4, 99.0, 73.6), hex(0xFFCFD4D8), 0.3)
            }
            "fire" -> {
                sky(W, HZ + 6, "night", f); range(W, HZ + 4, hex(0xFF34405E), 2, 9.0, 0.04); ground(W, Hh, HZ + 6, hex(0xFF3E4A3A), 8)
                minka(104.0, 90.0, 60.0, f, lit = true); figure(hl, 40.0, 98.0); figure(sp, 54.0, 98.0)
                nightWash(W, Hh, 0.32, pt(104.0, 58.0, 72.0, 1.2, 0.0, 104.0, 84.0, 34.0, 0.5, 0.0))
                for (k in 0 until 6) flames(80.0 + k * 9, 64 - abs(k - 2.5) * 5, 4.0, 11.0 + (k % 2) * 4, f + k)
                smoke(100.0, 36.0, f, 6, true)
            }
            "fair" -> {
                sky(W, HZ, "day", f); range(W, HZ - 2, hex(0xFF8FA6BE), 5, 9.0, 0.03); pagoda(150.0, HZ + 2, 0.55); ground(W, Hh, HZ, hex(0xFFC9B07A), 6, tufts = false)
                ln(pt(0.0, 52.0, 60.0, 56.0, 120.0, 54.0, 176.0, 52.0), SUMI, 0.3, true)
                for (i in 0 until 7) chochin(12.0 + i * 25, 57.0 + sin(i * 1.3) * 1.2, 1.8, if (i % 2 == 0) BENI else GOF, f + i)
                stall(6.0, 84.0, 30.0, AI, f); stall(64.0, 80.0, 30.0, BENI2, f + 1); stall(124.0, 84.0, 30.0, MATSU, f + 2)
                figure(npc("woman", 3), 46.0, 92.0); figure(npc("man", 4), 112.0, 94.0, true); figure(npc("kid", 5), 100.0, 96.0); figure(hl, 78.0, 99.0)
            }
            "road" -> {
                sky(W, HZ, "dusk", f); fuji(118.0, HZ - 1, 84.0, 36.0, hex(0xFF4A5F8A)); range(W, HZ, hex(0xFF7C6A86), 3, 7.0, 0.04)
                ground(W, Hh, HZ + 1, hex(0xFF8A9A5A), 9)
                sh(pt(93.0, HZ + 1.5, 99.0, HZ + 1.5, 116.0, Hh + 2, 36.0, Hh + 2), hex(0xFFDCC08A), 0.4)
                fade(0.35) { ln(pt(95.0, HZ + 3, 64.0, Hh), SUMI, 0.3); ln(pt(97.0, HZ + 3, 92.0, Hh), SUMI, 0.3) }
                pine(84.0, HZ + 4, 16.0, 2.0); pine(106.0, HZ + 4, 14.0, -1.6); pine(24.0, 84.0, 40.0, 6.0); pine(158.0, 92.0, 46.0, -7.0)
                toro(140.0, 90.0, 1.0); figure(npc("beggar", 7), 118.0, 94.0, true); hat = 1; figure(hl, 72.0, 98.0); hat = 0
            }
            "field", "field_dry", "klad", "meja" -> {
                val dry = key == "field_dry"
                sky(W, HZ - 6, if (dry) "gray" else "day", f)
                if (!dry) fuji(40.0, HZ - 7, 62.0, 24.0)
                range(W, HZ - 5, if (dry) hex(0xFF9A988A) else hex(0xFF7F9AB8), 4, 6.0, 0.03)
                paddies(W, HZ - 3, Hh, f, dry)
                if (!dry) { hasa(132.0, 92.0, 22.0) }
                if (key == "klad") tsuboKoban(118.0, 94.0, f)
                if (key == "meja") {
                    for (k in 0 until 5) { val sx2 = 90.0 + k * 18; val sy = 72.0 + k * 5; quad(sx2 - 0.7, sy - 12, 1.4, 12.0, WOOD, 0.3) }
                    ln(pt(90.0, 61.0, 108.0, 66.0, 126.0, 71.0, 144.0, 76.0, 162.0, 81.0), hex(0xFFD9C690), 0.7, true)
                    for (k in 0 until 4) { val sx2 = 99.0 + k * 18; val sy = 64.5 + k * 5; sh(pt(sx2 - 0.6, sy, sx2 + 0.6, sy, sx2 - 0.2, sy + 1.4, sx2 + 0.8, sy + 2.8, sx2 - 0.6, sy + 2.8), GOF, 0.2, false) }
                }
                if (dry) for (k in 0 until 3) crow(40.0 + k * 30 + sin(f * 0.5 + k) * 6, 20.0 + k * 6, f + k)
                hat = 1; figure(hl, 60.0, 96.0); figure(xp, 78.0, 96.0); hat = 0
            }
            "church" -> {
                sky(W, HZ, "day", f); range(W, HZ - 2, hex(0xFF8FA6BE), 6, 8.0, 0.03); ground(W, Hh, HZ, hex(0xFFCDBB8A), 12, tufts = false)
                pagoda(156.0, 84.0, 0.85); temple(88.0, 84.0, 1.05); pine(16.0, 86.0, 42.0, 5.0)
                toro(60.0, 88.0, 1.0); toro(120.0, 88.0, 1.0)
                figure(hl, 50.0, 98.0); figure(npc("priest", 9), 132.0, 98.0, true)
            }
            "interior" -> { room(W, Hh, f); figure(hl, 34.0, 98.0); if (xp != null) figure(xp, 108.0, 98.0, true) else figure(sp, 108.0, 98.0, true) }
            "wedding" -> {
                room(W, Hh, f, notable = true); byobu(52.0, 18.0, 76.0, 56.0, f)
                for (cx in doubleArrayOf(78.0, 102.0)) { quad(cx - 4, 74.0, 8.0, 4.0, BENI2, 0.35); el(cx, 73.4, 2.6, 0.8, BENI, 0.3); el(cx, 72.6, 1.6, 0.5, KIN, 0.25, false) }
                still { for (i in 0 until 6) { val ph = (f * 0.15 + i / 6.0) % 1; fade(1 - ph) { el(40.0 + i * 14 + sin(ph * 6 + i) * 3, 10 + ph * 50, 0.8, 0.5, if (i % 2 == 1) SAKURA else GOF, 0.2, false, 8) } } }
                figure(hl, 34.0, 98.0); figure(npc("bride", 13), 108.0, 98.0, true)
            }
            "night_int" -> {
                room(W, Hh, f, night = true)
                andon(66.0, 78.0)
                figure(hl, 34.0, 98.0)
                if (xp != null && xp.age < 14) { figure(xp, 99.0, 82.0); sh(pt(91.0, 76.0, 107.0, 76.0, 106.0, 84.0, 92.0, 84.0), STRAW, 0.4); batch(al(hex(0xFF9A7A3A), 0.9), 0.3) { var yy = 77.6; while (yy < 84) { seg(91.6, yy, 106.6, yy); yy += 1.6 } } }
                nightWash(W, Hh, 0.34, pt(66.0, 70.0, 56.0, 0.95, 0.0, 140.0, 84.0, 34.0, 0.6, 0.0, 115.0, 34.0, 50.0, 0.25, 1.0))
            }
            "tavern" -> {
                room(W, Hh, f, tavern = true, night = true, notable = true)
                quad(52.0, 72.0, 64.0, 3.0, WOOD2, 0.4); quad(54.0, 75.0, 2.0, 10.0, WOOD2, 0.3); quad(112.0, 75.0, 2.0, 10.0, WOOD2, 0.3)
                for (i in 0 until 3) { val tx = 64.0 + i * 16; sh(pt(tx - 1.2, 72.0, tx - 1.8, 68.6, tx - 0.6, 67.0, tx - 0.6, 65.8, tx + 0.6, 65.8, tx + 0.6, 67.0, tx + 1.8, 68.6, tx + 1.2, 72.0), hex(0xFFE6DCC6), 0.3); el(tx + 4.0, 71.4, 1.2, 0.6, GOF, 0.25) }
                for ((bx, by) in listOf(150.0 to 79.0, 162.0 to 79.0, 156.0 to 71.0, 144.0 to 71.0)) komodaru(bx, by, 1.2)
                figure(hl, 48.0, 98.0); figure(npc("man", 21), 114.0, 98.0, true); figure(npc("man", 22), 132.0, 98.0, true)
                nightWash(W, Hh, 0.55, pt(84.0, 50.0, 70.0, 0.9, 0.0, 48.0, 34.0, 30.0, 0.6, 0.0, 84.0, 34.0, 30.0, 0.5, 0.0, 120.0, 34.0, 30.0, 0.6, 0.0, 156.0, 34.0, 26.0, 0.5, 0.0))
            }
            "winter" -> {
                sky(W, HZ, "snow", f); range(W, HZ, hex(0xFFD4D6DC), 1, 10.0, 0.03); ground(W, Hh, HZ, SNOW, 2, snow = true)
                for ((a, b, h2) in listOf(Triple(120.0, 70.0, 24.0), Triple(136.0, 74.0, 34.0), Triple(168.0, 76.0, 30.0), Triple(152.0, 80.0, 40.0))) sugi(a, b, h2, true)
                minka(32.0, 80.0, 50.0, f, lit = true, snow = true, vent = true); bambooFence(58.0, 100.0, 86.0, true)
                wolf(122.0, 92.0, f + 1, true); wolf(96.0, 96.0, f, false)
                hat = 2; figure(hl, 62.0, 100.0); hat = 0
                nightWash(W, Hh, 0.55, pt(32.0, 70.0, 30.0, 0.8, 0.0))
                wolf(122.0, 92.0, f + 1, true, true); wolf(96.0, 96.0, f, false, true); snowfall(W, Hh, f, 70)
            }
            "war" -> {
                sky(W, HZ, "dusk", f); range(W, HZ, hex(0xFF7C6474), 8, 7.0, 0.03); castle(150.0, HZ + 2, 0.42, f); ground(W, Hh, HZ, hex(0xFF8A8A5C), 5)
                smoke(30.0, 50.0, f, 6, true); smoke(120.0, 46.0, f + 3, 6, true)
                val pre = d.year < 1870
                if (pre) for (i in 0 until 5) nobori(86.0 + i * 16, 90.0, 40.0, if (i % 2 == 0) BENI else GOF, f + i) else nobori(80.0, 92.0, 44.0, GOF, f)
                for (i in 0 until 6) figure(npc("soldier", 30 + i), 88.0 + i * 14, 92.0 + (i % 2) * 2, true)
                figure(hl, 36.0, 98.0); figure(xp, 54.0, 98.0)
            }
            "plague" -> {
                sky(W, HZ, "gray", f); range(W, HZ, hex(0xFF9A9C96), 4, 7.0, 0.03); ground(W, Hh, HZ, hex(0xFF7F8062), 7)
                minka(30.0, 80.0, 46.0, f, dark = true, smoky = true); cherry(156.0, 84.0, 40.0, 3); cherry(64.0, 78.0, 26.0, 3)
                for ((a, b) in listOf(96.0 to 86.0, 128.0 to 86.0, 112.0 to 90.0, 144.0 to 92.0)) sotoba(a, b, 14.0)
                jizo(104.0, 96.0)
                for (k in 0 until 4) crow(30.0 + k * 36 + sin(f * 0.6 + k) * 5, 14.0 + k * 5 + sin(f * 0.9 + k) * 1.5, f + k)
                figure(hl, 70.0, 98.0); figure(npc("monk", 61), 88.0, 97.0, true)
            }
            "barin" -> {
                sky(W, HZ, "day", f); range(W, HZ - 2, hex(0xFF8FA6BE), 9, 8.0, 0.03); ground(W, Hh, HZ, GR, 15, flowers = intArrayOf(GOF))
                castle(120.0, 86.0, 1.1, f); pine(10.0, 86.0, 44.0, 6.0); pine(168.0, 88.0, 38.0, -5.0)
                figure(npc("barin", 41), 100.0, 97.0, true); figure(hl, 52.0, 98.0)
            }
            "dream" -> {
                room(W, Hh, f, night = true); figure(hl, 40.0, 98.0)
                nightWash(W, Hh, 0.28, pt(112.0, 70.0, 46.0, 0.75, 1.0, 140.0, 84.0, 26.0, 0.45, 0.0))
                save(); translate(0.0, sin(f * 1.2)); figure(npc("man", 51), 112.0, 94.0, true, ghost = true); restore()
            }
            "dawn" -> {
                sky(W, HZ - 8, "dawn", f); sea(W, HZ - 8, HZ + 2, f); range(W, HZ, hex(0xFF7E7698), 7, 6.0, 0.05)
                ground(W, Hh, HZ, GR, 9, flowers = intArrayOf(GOF, SAKURA))
                minka(36.0, 84.0, 54.0, f, lit = true, vent = true); cherry(150.0, 88.0, 40.0, 0, f = f)
                var xx = 84.0; for (p in d.family.take(5)) { figure(p, xx, 99.0); xx += if (p.age < 14) 11 else 15 }
                cranes(80.0, 20.0, f)
            }
            "inari" -> {
                right = false
                sky(W, HZ - 4, "snow", f); range(W, HZ - 4, hex(0xFFCED0D8), 11, 8.0, 0.04); ground(W, Hh, HZ - 4, SNOW, 13, snow = true)
                pine(160.0, 70.0, 30.0, -3.0, true)
                hokora(150.0, 76.0, 1.15, true); fox(134.0, 77.0, 0.8, false, false); fox(166.0, 77.0, 0.8, true, false)
                torii(134.0, 70.0, 16.0, true); torii(122.0, 78.0, 26.0, true); torii(106.0, 92.0, 44.0, true)
                toro(66.0, 86.0, 1.1, lit = true, snow = true)
                cherry(22.0, 84.0, 36.0, 3, plum = true)
                fox(84.0, 96.0, 1.15, true, true, f)
                hat = 0; figure(hl, 44.0, 98.0)
                nightWash(W, Hh, 0.6, pt(66.0, 72.0, 22.0, 0.8, 0.0, 150.0, 66.0, 18.0, 0.3, 0.0))
                snowfall(W, Hh, f, 60)
            }
            else -> { // village, and anything unknown
                sky(W, HZ, "day", f); fuji(150.0, HZ - 2, 56.0, 22.0); range(W, HZ - 2, hex(0xFF8FA6BE), 2, 7.0, 0.03)
                ground(W, Hh, HZ, GR, 3, flowers = intArrayOf(GOF, SAKURA))
                minka(96.0, 76.0, 40.0, f, vent = true); minka(26.0, 80.0, 44.0, f, vent = true); cherry(64.0, 80.0, 30.0, 0, f = f)
                bambooFence(0.0, 60.0, 88.0); jWell(140.0, 88.0); chicken(120.0, 96.0, f)
                figure(hl, 84.0, 99.0); figure(xp, 100.0, 99.0)
            }
        }
        printFrame(W, Hh, key, right)
    }

    fun Ctx.home(d: HomeData, f: Double) = style {
        val W = 176.0; val Hh = 96.0; val s = if (d.over) 3 else d.season.mod(4); val night = d.over; val snow = s == 3; val HZ = 58.0
        sky(W, HZ, if (night) "night" else if (snow) "snow" else if (s == 2) "dusk" else "day", f)
        fuji(140.0, HZ - 1, 64.0, 26.0, if (night) hex(0xFF2E3A5A) else hex(0xFF3D5F8E))
        range(W, HZ, when { night -> hex(0xFF3A4462); snow -> hex(0xFFCFD2DA); s == 2 -> hex(0xFF9A7A6A); else -> hex(0xFF7F9AB8) }, d.seed, 8.0, 0.035)
        ground(W, Hh, HZ + 1, when { snow -> SNOW; s == 2 -> hex(0xFFC4A060); s == 0 -> hex(0xFF9CBA6A); else -> GRASS }, 21, snow = snow,
            flowers = if (s == 0) intArrayOf(SAKURA, GOF) else if (s == 1) intArrayOf(GOF, hex(0xFF8A7AC0)) else null)
        cherry(154.0, 86.0, 42.0, s, f = f); pine(10.0, 88.0, 36.0, 5.0, snow)
        when (d.dvor) {
            0 -> minka(48.0, 82.0, 40.0, f, dark = true, smoky = true, snow = snow, lit = night)
            1 -> minka(48.0, 82.0, 54.0, f, vent = true, snow = snow, lit = night || snow)
            2 -> { minka(46.0, 82.0, 60.0, f, vent = true, snow = snow, lit = night || snow); bambooFence(78.0, 128.0, 86.0, snow) }
            3 -> { machiya(48.0, 84.0, 56.0, f, lit = night || snow, snow = snow, sake = true); komodaru(82.0, 84.0); komodaru(88.6, 84.0); komodaru(85.3, 77.0) }
            else -> { machiya(38.0, 84.0, 52.0, f, lit = night || snow, snow = snow); kura(82.0, 84.0, 22.0, snow); bambooFence(94.0, 104.0, 86.0, snow) }
        }
        if (!d.over) {
            var xx = if (d.dvor >= 3) 104.0 else 92.0; for (p in d.family.take(7)) { figure(p, xx, 95.0); xx += if (p.age < 14) 10 else 13 }
            if (s == 0 || s == 1) { chicken(24.0, 94.0, f); chicken(34.0, 92.0, f + 2, true) }
        }
        if (night) nightWash(W, Hh, 0.4, pt(48.0, 74.0, 26.0, 0.6, 0.0))
        if (snow) snowfall(W, Hh, f, 40)
        printFrame(W, Hh, "home${d.dvor}", true)
    }
}
