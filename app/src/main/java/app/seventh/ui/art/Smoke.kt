package app.seventh.ui.art

import android.graphics.LinearGradient
import android.graphics.Shader
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/*
 * Smoke: one rising column, not balls. Its edges are scalloped bumps that climb with time, it widens and drifts with
 * a gentle wind and fades out with height. Thin and pale from a chimney, thick and dark from a fire. Each land inks
 * it in its own way: lubok outlines it and curls ink scrolls inside, the manuscript adds flowing bands, ukiyo-e
 * grades it softly and curls the ends of its bumps.
 */

private val smokeShaders = HashMap<String, Shader>()
private fun fadeUp(key: String, y: Double, h: Double, col: Int, a0: Double, a1: Double = 0.0) = smokeShaders.getOrPut("$key|$y|$h|$col|$a0") {
    LinearGradient(0f, y.toFloat(), 0f, (y - h).toFloat(), intArrayOf(Jp.al(col, a0), Jp.al(col, (a0 + a1) * 0.55), Jp.al(col, a1)), floatArrayOf(0f, 0.55f, 1f), Shader.TileMode.CLAMP)
}

/** The column's centre and half-width at height s above the source. */
private class Column(val x: Double, val y: Double, val f: Double, val dark: Boolean) {
    val H = if (dark) 44.0 else 30.0
    private val w0 = if (dark) 2.6 else 1.2
    private val w1 = if (dark) 17.0 else 7.4
    val v = if (dark) 0.5 else 0.32
    fun cx(s: Double) = x + s.pow(1.3) * (if (dark) 0.14 else 0.18) + sin(f * 0.7 + s * 0.13) * s * 0.05
    fun hw(u: Double) = (w0 + (w1 - w0) * u.pow(0.75)) / 2
    /** the scallops: bumps that climb, a little out of step on the two sides */
    fun bump(u: Double, side: Int) = 1 + 0.24 * abs(sin(PI * (u * 4.4 - f * v) + side * 0.9))
    fun outline(n: Int = 26, grow: Double = 1.0): DoubleArray {
        val L = Pb(64); val R = Pb(64)
        for (j in 0..n) {
            val u = j / n.toDouble(); val s = u * H; val c = cx(s); val w = hw(u) * grow
            L.add(c - w * bump(u, 0), y - s); R.add(c + w * bump(u, 1), y - s)
        }
        return cat(L, R)
    }
}

internal fun Ctx.smoke(x: Double, y: Double, f: Double, n: Int = 5, dark: Boolean = false) = run {
    val c = Column(x, y, f, dark)
    val j = J; J = 0.0
    try {
        when {
            edo -> smokeUkiyoe(c)
            eu -> smokeManuscript(c)
            else -> smokeLubok(c)
        }
    } finally { J = j }
}

/** a little ink scroll: an open spiral, turning [turns] times */
private fun Ctx.curl(cx: Double, cy: Double, r: Double, dir: Double, turns: Double = 1.3) {
    beginPath()
    for (i in 0..12) {
        val t = i / 12.0; val a = dir * t * turns * 2 * PI; val rr = r * (1 - t * 0.7)
        val px = cx + cos(a + PI) * rr * dir; val py = cy + sin(a + PI) * rr
        if (i == 0) moveTo(px, py) else lineTo(px, py)
    }
}

private fun Ctx.smokeLubok(c: Column) {
    val body = if (c.dark) hex(0xFF5F5850) else hex(0xFFEDE7DA)
    poly(c.outline(), true, true)
    fillS(fadeUp("lb", c.y, c.H, body, 0.92))
    strokeS(fadeUp("li", c.y, c.H, INK, 0.75), 0.42)
    // ink scrolls rising inside the column
    for (i in 0 until 3) {
        val u = (i / 3.0 + c.f * c.v * 0.23) % 1; val s = u * c.H; val a = (1 - u) * (if (c.dark) 0.7 else 0.55)
        val r = c.hw(u) * 0.5
        fade(a) { curl(c.cx(s) + (if (i % 2 == 0) -r * 0.4 else r * 0.4), c.y - s, r, if (i % 2 == 0) 1.0 else -1.0); stroke(if (c.dark) hex(0xFF2A2420) else INK, 0.32) }
    }
}

private fun Ctx.smokeManuscript(c: Column) {
    val body = if (c.dark) hex(0xFF57524C) else hex(0xFFE8E4DE)
    poly(c.outline(), true, true)
    fillS(fadeUp("mb", c.y, c.H, body, 0.9))
    strokeS(fadeUp("mi", c.y, c.H, Eu.EI, 0.7), 0.35)
    // two stylised bands flowing up the column
    val band = if (c.dark) hex(0xFF8E877E) else hex(0xFFB9B2A8)
    for (side in intArrayOf(-1, 1)) {
        beginPath()
        for (j in 2..24) {
            val u = j / 26.0; val s = u * c.H
            val px = c.cx(s) + side * c.hw(u) * (0.38 + 0.16 * sin(PI * (u * 4.4 - c.f * c.v) + side))
            if (j == 2) moveTo(px, c.y - s) else lineTo(px, c.y - s)
        }
        strokeS(fadeUp("mbd$side", c.y, c.H, band, 0.9), 0.4)
    }
    for (i in 0 until 2) {
        val u = (i / 2.0 + 0.25 + c.f * c.v * 0.23) % 1; val s = u * c.H; val r = c.hw(u) * 0.42; val side = if (i == 0) -1.0 else 1.0
        fade((1 - u) * 0.6) { curl(c.cx(s) + side * c.hw(u) * 0.75, c.y - s, r, side); stroke(Eu.EI, 0.3) }
    }
}

private fun Ctx.smokeUkiyoe(c: Column) {
    val body = if (c.dark) hex(0xFF4A4642) else hex(0xFFF1EEE6)
    // bokashi: a soft wide halo, then the body
    poly(c.outline(grow = 1.25), true, true); fillS(fadeUp("ub2", c.y, c.H, body, if (c.dark) 0.35 else 0.4))
    poly(c.outline(), true, true); fillS(fadeUp("ub", c.y, c.H, body, 0.85))
    strokeS(fadeUp("ui", c.y, c.H, Jp.SUMI, 0.35), 0.25)
    // curled ends on the bumps, Hokusai style
    for (i in 0 until 4) {
        val u = (i / 4.0 + 0.1 + c.f * c.v * 0.227) % 1; val s = u * c.H; val side = if (i % 2 == 0) -1.0 else 1.0
        val r = c.hw(u) * 0.36
        fade((1 - u) * 0.65) { curl(c.cx(s) + side * c.hw(u) * c.bump(u, if (side < 0) 0 else 1) * 0.92, c.y - s, r, side, 1.1); stroke(Jp.SUMI, 0.28) }
    }
}
