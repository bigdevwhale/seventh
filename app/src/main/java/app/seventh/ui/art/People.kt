package app.seventh.ui.art

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import app.seventh.domain.Looks
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/* people */

private val npcs = HashMap<String, Fig>()

/** An extra in a scene: deterministic from the seed, in the family palettes. */
internal fun npc(kind: String, seed: Int): Fig = npcs.getOrPut(kind + seed) {
    val r = Rng(if (seed == 0) 1 else seed)
    val age = when (kind) { "beggar" -> 70; "kid" -> 8; else -> 20 + (r.next() * 30).toInt() }
    fun pick(a: IntArray) = a[(r.next() * a.size).toInt()]
    Fig(
        sex = if (kind == "woman" || kind == "beggar" || kind == "bride") 'f' else 'm', age = age,
        skin = pick(Looks.SKIN), hair = pick(Looks.HAIR), cloth = pick(Looks.CLOTH_M), kerch = pick(Looks.KERCH),
        seed = if (seed == 0) 1 else seed, kind = kind,
    )
}

internal fun Ctx.person(L: Fig, kidFace: Boolean = false) { if (eu) with(Eu) { euPerson(L, kidFace) } else rusPerson(L, kidFace) }

private val ghostP = Paint().apply { alpha = 178 }

internal fun Ctx.figure(L: Fig?, x: Double, y: Double, flip: Boolean = false, ghost: Boolean = false) {
    if (L == null) return
    if (ghost) {
        layer(x - 22, y - 50, 44.0, 52.0, ghostP)
        save(); translate(x, y); if (flip) scale(-1.0, 1.0); person(L); restore()
        mode = Ctx.ATOP; fillRect(x - 22, y - 50, 44.0, 52.0, rgba(175, 205, 245, 0.72)); mode = Ctx.SRC
        restore(); return
    }
    save(); translate(x, y); if (flip) scale(-1.0, 1.0); person(L); restore()
}

private val C_WHT2 = hex(0xFFE8E0CC)
private val C_RED = hex(0xFFB8322A)
private val C_OCH2 = hex(0xFFC8902F)
private val C_KER0 = hex(0xFFF4ECD8)

internal fun Ctx.rusPerson(L: Fig, kidFace: Boolean) {
    val a = L.age; val k = L.kind; val fem = L.sex == 'f'; val r = Rng((if (L.seed == 0) 1 else L.seed) * 17)
    if (a < 14 && !kidFace) { val s = if (a < 4) 0.5 else if (a < 9) 0.62 else 0.72; save(); scale(s, s); person(L, true); restore(); return }
    val sk = L.skin; val hr = L.hair; val old = a > 55; val pat = floor(r.next() * 3).toInt(); val kid = kidFace
    val cl = when (k) {
        "soldier" -> if (year > 1900) hex(0xFF77784C) else hex(0xFF2F5A3A); "barin" -> hex(0xFF2C4A8A)
        "priest" -> hex(0xFF2A2522); "beggar" -> hex(0xFF857A64); else -> L.cloth
    }
    if (!fem) {
        val boot = k == "soldier" || k == "barin" || k == "priest"
        val tr = if (k == "barin") hex(0xFFE8E0CC) else if (k == "soldier") hex(0xFFE8E2D0) else hex(0xFFD9D0BA)
        quad(-4.0, -11.0, 3.2, 8.6, tr, 0.5); quad(0.8, -11.0, 3.2, 8.6, tr, 0.5)
        for (s in intArrayOf(-1, 1)) {
            val fx = s * 2.4
            if (boot) { quad(fx - 1.7, -5.0, 3.4, 4.0, INK, 0.3); el(fx + s * 0.8, -1.0, 2.3, 1.1, INK, 0.3) }
            else {
                el(fx + s * 0.8, -1.2, 2.4, 1.3, hex(0xFFD8B46A), 0.45)
                fade(0.6) { ln(pt(fx - 1.4, -2.0, fx + 1.6, -0.4), INK, 0.3); ln(pt(fx - 1.4, -0.4, fx + 1.6, -2.0), INK, 0.3) }
            }
        }
        val coat = boot || k == "beggar"; val hem = if (coat) -3.5 else -10.0; val hw = if (coat) 8.4 else 7.4
        for (s in intArrayOf(-1, 1)) sh(pt(s * 4.6, -26.6, s * 7.6, -23.0, s * 8.2, -15.6, s * 6.0, -15.0, s * 5.4, -20.6), cl, sm = true)
        val B = pt(-5.4, -27.6, 5.4, -27.6, 6.6, -20.0, hw, hem, -hw, hem, -6.6, -20.0); sh(B, cl)
        if (!coat) {
            inside(B) {
                if (pat == 1) {
                    var yy = -26.0
                    while (yy < -10) {
                        var xx = -8.0
                        while (xx <= 8) {
                            dot(xx + (if (jr(yy).toInt() % 2 != 0) 1.3 else 0.0), yy, 0.42, if (cl == WHT || cl == C_WHT2) rgba(201, 55, 44, 0.7) else rgba(255, 245, 220, 0.6))
                            xx += 2.6
                        }
                        yy += 2.6
                    }
                }
                if (pat == 2) { var xx = -8.0; while (xx <= 8) { ln(pt(xx, -28.0, xx * 1.1, -9.0), rgba(31, 23, 18, 0.25), 0.5); xx += 2.2 } }
                var xx = -7.0; while (xx <= 7) { dot(xx, -11.2, 0.4, KIN); xx += 1.6 }
            }
            quad(-6.9, -17.4, 13.8, 1.6, KIN, 0.4); ln(pt(3.0, -16.0, 3.4, -13.0), KIN, 0.55); ln(pt(-1.4, -27.4, -1.4, -22.4), INK, 0.4)
            var yy = -27.0; while (yy < -22) { dot(-0.6, yy, 0.35, KIN); yy += 1.4 }
        } else if (k == "soldier") {
            for (s in intArrayOf(-1, 1)) { ln(pt(s * -5.0, -27.0, s * 6.6, -12.0), INK, 1.7); ln(pt(s * -5.0, -27.0, s * 6.6, -12.0), WHT, 1.0) }
            for (j in 0 until 3) dot(0.0, -24.0 + j * 4, 0.5, YEL)
            ln(pt(9.2, -6.0, 9.2, -41.0), INK, 1.3); ln(pt(9.2, -6.0, 9.2, -26.0), BRN, 0.7); ln(pt(9.2, -26.0, 9.2, -41.0), hex(0xFFCFD4D8), 0.6)
        } else if (k == "barin") {
            sh(pt(-2.6, -27.6, 2.6, -27.6, 0.0, -19.0), WHT, 0.4); for (j in 0 until 4) dot(1.6, -24.0 + j * 3.6, 0.5, YEL); ln(pt(9.4, -1.0, 9.4, -21.0), INK, 0.7)
        } else if (k == "priest") {
            ln(pt(0.0, -24.4, 0.0, -18.0), OCH, 1.0); ln(pt(-1.6, -22.6, 1.6, -22.6), OCH, 0.9)
        } else if (k == "beggar") {
            inside(B) { quad(-6.0, -16.0, 3.4, 3.0, hex(0xFFA89878), 0.35); quad(2.0, -9.0, 3.0, 3.0, hex(0xFF6F6655), 0.35) }
            ln(pt(9.4, 1.0, 9.4, -37.0), INK, 1.1); ln(pt(9.4, 1.0, 9.4, -37.0), BRN2, 0.5)
        }
    } else {
        for (s in intArrayOf(-1, 1)) el(s * 2.6, -0.7, 1.9, 0.9, INK, 0.3)
        val slv = if (k == "beggar") hex(0xFF9A8F7A) else WHT
        for (s in intArrayOf(-1, 1)) sh(pt(s * 3.8, -26.6, s * 7.4, -22.6, s * 8.0, -15.8, s * 5.8, -15.2, s * 4.8, -20.6), slv, sm = true)
        if (k != "beggar") for (s in intArrayOf(-1, 1)) ln(pt(s * 5.9, -16.4, s * 8.0, -16.8), KIN, 0.7)
        val sar = if (k == "bride") KIN else if (k == "beggar") hex(0xFF857A64) else L.cloth
        val B = pt(-3.8, -26.8, 3.8, -26.8, 5.0, -20.0, 9.4, -0.8, -9.4, -0.8, -5.0, -20.0); sh(B, sar)
        sh(pt(-2.4, -27.6, 2.4, -27.6, 1.6, -24.0, -1.6, -24.0), slv, 0.4)
        if (k != "beggar") {
            inside(B) {
                quad(-11.0, -4.4, 22.0, 3.6, if (sar == OCH || sar == C_OCH2) KIN2 else OCH, 0.4, false)
                var i = -8; while (i <= 8) { dot(i.toDouble(), -2.6, 0.55, if (sar == KIN || sar == C_RED) WHT else KIN); i += 2 }
                if (pat == 1) {
                    var yy = -22.0
                    while (yy < -6) { var xx = -8.0; while (xx <= 8) { dot(xx + (if (jr(yy).toInt() % 2 != 0) 1.5 else 0.0), yy, 0.45, rgba(255, 245, 220, 0.55)); xx += 3 }; yy += 3 }
                }
            }
            var yy = -24.0; while (yy < -6) { dot(0.0, yy, 0.5, YEL); yy += 2.6 }
        } else {
            inside(B) { quad(-6.0, -16.0, 3.4, 3.0, hex(0xFFA89878), 0.35); quad(2.0, -9.0, 3.0, 3.0, hex(0xFF6F6655), 0.35) }
            ln(pt(9.4, 1.0, 9.4, -37.0), INK, 1.1); ln(pt(9.4, 1.0, 9.4, -37.0), BRN2, 0.5)
        }
    }
    for (s in intArrayOf(-1, 1)) el(s * 7.2, -14.4, 1.4, 1.4, sk, 0.45, n = 8)
    quad(-1.3, -29.8, 2.6, 2.8, sk, 0.4)
    /* head */
    save(); translate(0.0, -33.5); if (kid) scale(1.12, 1.12)
    val ker = fem && !(kid && a < 10) && k != "nokerch"
    val kc = if (k == "bride") KIN else if (k == "beggar") hex(0xFF6F6655) else L.kerch
    if (fem && k == "bride") {
        sh(pt(-6.4, -1.0, -6.8, -5.0, -4.6, -8.8, 0.0, -10.6, 4.6, -8.8, 6.8, -5.0, 6.4, -1.0), KIN, sm = true)
        for (i in 0 until 9) { val an = PI * (1.1 + i * 0.1); dot(cos(an) * 6, -1 + sin(an) * 8.6, 0.55, WHT) }
        dot(0.0, -7.0, 1.0, YEL)
    }
    if (ker) sh(pt(0.0, -6.6, -5.6, -3.4, -6.2, 1.6, -4.6, 5.6, -1.6, 6.8, 1.6, 6.8, 4.6, 5.6, 6.2, 1.6, 5.6, -3.4), kc, sm = true)
    else if (fem) { sh(pt(3.2, 1.0, 5.2, 2.0, 5.4, 9.6, 4.2, 10.6, 3.4, 3.0), hr, 0.5, sm = true); sh(pt(3.6, 10.0, 2.2, 11.6, 6.2, 11.6, 4.8, 10.0), KIN, 0.35) }
    el(0.0, 0.0, if (kid) 4.4 else 4.2, if (kid) 4.4 else 4.9, sk, 0.6)
    if (!fem) {
        if (!(old && L.seed % 3 == 0)) sh(pt(-4.5, 0.6, -4.8, -2.6, -3.4, -4.9, 0.0, -5.6, 3.4, -4.9, 4.8, -2.6, 4.5, 0.6, 3.8, -1.6, 1.4, -1.8, -1.4, -1.8, -3.8, -1.6), hr, 0.55, sm = true)
        else { sh(pt(-4.5, 0.8, -4.6, -1.6, -3.6, -2.4, -3.4, 0.8), hr, 0.45); sh(pt(4.5, 0.8, 4.6, -1.6, 3.6, -2.4, 3.4, 0.8), hr, 0.45) }
    } else if (!ker) sh(pt(-4.3, -0.4, -4.4, -3.0, -2.4, -5.1, 0.0, -5.3, 2.4, -5.1, 4.4, -3.0, 4.3, -0.4, 2.2, -2.6, 0.0, -3.4, -2.2, -2.6), hr, 0.5, sm = true)
    if (ker) {
        sh(pt(-5.0, 1.2, -5.2, -2.6, -3.4, -5.4, 0.0, -6.2, 3.4, -5.4, 5.2, -2.6, 5.0, 1.2, 3.6, -1.4, 0.0, -2.2, -3.6, -1.4), kc, 0.55, sm = true)
        val pc = if (kc == C_KER0 || kc == WHT) KIN else if (kc == KIN || kc == C_RED) WHT else INK
        for ((px, py) in PATK) dot(px, py, 0.42, pc)
        sh(pt(-1.0, 4.6, 1.0, 4.6, 2.0, 6.8, 0.0, 5.9, -2.0, 6.8), kc, 0.4)
    }
    if (k == "soldier") {
        if (year > 1900) { sh(pt(-4.6, -3.4, -4.2, -6.2, 4.2, -6.2, 4.6, -3.4), hex(0xFF77784C)); quad(-5.6, -3.6, 11.2, 1.0, INK, 0.0) }
        else { quad(-4.2, -9.6, 8.4, 6.4, INK, 0.4); ln(pt(0.0, -9.6, 0.0, -12.4), WHT, 1.4); dot(0.0, -6.2, 0.7, YEL) }
    }
    if (k == "barin") { quad(-3.8, -11.0, 7.6, 7.0, INK, 0.4); quad(-5.6, -4.4, 11.2, 1.1, INK, 0.4); quad(-3.8, -5.8, 7.6, 0.9, KIN, 0.0) }
    if (k == "priest") quad(-4.2, -9.2, 8.4, 5.4, INK, 0.4)
    val ey = -0.3
    for (s in intArrayOf(-1, 1)) {
        dot(s * 1.7, ey, 0.55, INK); ln(pt(s * 0.9, ey - 1.4, s * 1.7, ey - 1.9, s * 2.6, ey - 1.5), INK, 0.35, true)
        dot(s * 2.7, 1.7, 1.0, rgba(214, 74, 58, 0.5))
    }
    ln(pt(0.1, ey + 0.3, 0.6, 1.7, -0.1, 1.9), INK, 0.35)
    if (old) fade(0.55) { ln(pt(-1.6, -2.5, 1.6, -2.5), INK, 0.3); ln(pt(-2.8, 0.8, -2.2, 1.4), INK, 0.3); ln(pt(2.8, 0.8, 2.2, 1.4), INK, 0.3) }
    if (!fem && !kid && a >= 17) {
        val len = if (k == "priest") 5.0 else if (a < 22) 0.4 else if (old) 4.0 else 1.6 + (L.seed % 3) * 0.6
        sh(pt(-4.2, 0.4, -4.0, 2.6, -2.6, 4.6 + len * 0.5, 0.0, 5.2 + len, 2.6, 4.6 + len * 0.5, 4.0, 2.6, 4.2, 0.4, 2.6, 2.0, 1.0, 1.8, 0.0, 2.4, -1.0, 1.8, -2.6, 2.0), hr, 0.5, sm = true)
        ln(pt(-0.8, 3.1, 0.0, 3.3, 0.8, 3.1), KIN, 0.55)
    } else {
        if (k == "barin") { ln(pt(0.0, 2.2, -1.6, 2.4, -2.6, 1.6), hr, 0.7, true); ln(pt(0.0, 2.2, 1.6, 2.4, 2.6, 1.6), hr, 0.7, true) }
        ln(pt(-0.8, 3.0, 0.0, 3.3, 0.8, 3.0), KIN, 0.6, true)
    }
    restore()
}
private val PATK = listOf(-3.2 to -3.0, 0.0 to -4.6, 3.2 to -3.0, -1.6 to -3.4, 1.6 to -3.4)

/* portrait: 24x24 bust */
private val sepiaP = Paint().apply {
    val r = 214 / 255f; val g = 196 / 255f; val b = 160 / 255f
    colorFilter = ColorMatrixColorFilter(ColorMatrix(floatArrayOf(
        0.3f * r, 0.59f * r, 0.11f * r, 0f, 0f,
        0.3f * g, 0.59f * g, 0.11f * g, 0f, 0f,
        0.3f * b, 0.59f * b, 0.11f * b, 0f, 0f,
        0f, 0f, 0f, 1f, 0f,
    )))
}
private val PBG_HEAD = hex(0xFFE6B552); private val PBG_BLOOD = hex(0xFFA3BB72); private val PBG_INLAW = hex(0xFF98ADC0)

internal fun Ctx.portrait(p: Fig, bg: PBg, faded: Boolean, x0: Double, y0: Double, round: Boolean) {
    val kid = p.age < 14
    save(); translate(x0, y0); beginPath(); if (round) circle(12.0, 12.0, 12.0) else rect(0.0, 0.0, 24.0, 24.0); clip()
    if (faded) layer(0.0, 0.0, 24.0, 24.0, sepiaP)
    if (eu) with(Eu) { pbg(bg) } else {
        fillRect(0.0, 0.0, 24.0, 24.0, when (bg) { PBg.HEAD -> PBG_HEAD; PBg.BLOOD -> PBG_BLOOD; PBg.INLAW -> PBG_INLAW })
        fade(0.45) {
            for (i in 0 until 14) { val an = i / 14.0 * 6.283; dot(12 + cos(an) * 10.4, 10.5 + sin(an) * 10.4, 0.65, WHT) }
            for (i in 0 until 14) { val an = (i + 0.5) / 14 * 6.283; ln(pt(12 + cos(an) * 7.8, 10.5 + sin(an) * 7.8, 12 + cos(an) * 9.4, 10.5 + sin(an) * 9.4), WHT, 0.5) }
        }
    }
    val s = 1.5; save(); translate(12.0, (if (kid) 11.4 else 10.6) + 33.5 * s); scale(s, s); person(p, kid); restore()
    if (faded) restore()
    paper(0.6, 0.0, 0.0, 24.0, 24.0); restore()
}
