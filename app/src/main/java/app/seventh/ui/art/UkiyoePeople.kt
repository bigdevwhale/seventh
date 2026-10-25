package app.seventh.ui.art

import app.seventh.ui.art.Jp.AI
import app.seventh.ui.art.Jp.BENI
import app.seventh.ui.art.Jp.BENI2
import app.seventh.ui.art.Jp.GOF
import app.seventh.ui.art.Jp.KIN
import app.seventh.ui.art.Jp.STRAW
import app.seventh.ui.art.Jp.SUMI
import app.seventh.ui.art.Jp.WOOD
import app.seventh.ui.art.Jp.WOOD2
import app.seventh.ui.art.Jp.al
import app.seventh.ui.art.Jp.mix
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/* ukiyo-e people: kimono with patterns, obi, topknots and shimada, white faces for women */

private val HAIRJ = hex(0xFF1C1A1F)
private val FACEW = hex(0xFFF7EFE3)
private val PATE = hex(0xFFBCC6CB)
private val GREY1 = hex(0xFFD8D2C4); private val GREY2 = hex(0xFF9A9488)

/** The family palettes, retuned to dyes a print would use. */
private fun jcloth(c: Int): Int = when (c) {
    hex(0xFFB8322A) -> hex(0xFFA83A30); hex(0xFFE8E0CC) -> hex(0xFFD9CFB4); hex(0xFF3A5A8C) -> hex(0xFF2F4A72)
    hex(0xFF6F7A4A) -> hex(0xFF5E6A44); hex(0xFF8A5A3A) -> hex(0xFF7A4E34); hex(0xFF4E7D3A) -> hex(0xFF4D6B4A)
    hex(0xFF7A1F1A) -> hex(0xFF6E2A34); hex(0xFFC8902F) -> hex(0xFFB88A3A); else -> c
}
private fun jobi(c: Int): Int = when (c) {
    hex(0xFFF4ECD8) -> hex(0xFFE8D8A8); hex(0xFFB8322A) -> hex(0xFFB0342C); hex(0xFFD9A441) -> hex(0xFFC99A3E)
    hex(0xFF3A5A8C) -> hex(0xFF26385C); hex(0xFFE07A2A) -> hex(0xFFC8642E); else -> c
}

/** kimono patterns: 0 checks, 1 stripes, 2 shibori dots, 3 asanoha */
internal fun Ctx.kpat(pat: Int, col: Int, x0: Double, y0: Double, x1: Double, y1: Double) {
    val light = col.let { val l = ((it shr 16) and 255) * 0.3 + ((it shr 8) and 255) * 0.59 + (it and 255) * 0.11; l > 150 }
    val pc = if (light) mix(col, SUMI, 0.3) else mix(col, GOF, 0.45)
    when (pat) {
        0 -> { beginPath(); var y = y0; var j = 0; while (y < y1) { var x = x0 + (j % 2) * 1.8; while (x < x1) { rect(x, y, 1.8, 1.8); x += 3.6 }; y += 1.8; j++ }; fill(al(pc, 0.6)) }
        1 -> { beginPath(); var x = x0; while (x < x1) { moveTo(x, y0); lineTo(x, y1); x += 1.5 }; stroke(al(pc, 0.8), 0.45) }
        2 -> { beginPath(); var y = y0 + 1; var j = 0; while (y < y1) { var x = x0 + (j % 2) * 1.5; while (x < x1) { circle(x, y, 0.45); x += 3.0 }; y += 2.4; j++ }; fill(pc) }
        else -> {
            beginPath(); val s = 3.0; var y = y0; var j = 0
            while (y < y1 + s) {
                var x = x0 + (j % 2) * s / 2
                while (x < x1 + s) { for (a in 0 until 3) { val an = a * PI / 3 + PI / 6; moveTo(x - cos(an) * s * 0.5, y - sin(an) * s * 0.5); lineTo(x + cos(an) * s * 0.5, y + sin(an) * s * 0.5) }; x += s }
                y += s * 0.866; j++
            }
            stroke(al(pc, 0.8), 0.22)
        }
    }
}

internal fun Ctx.edoPerson(L: Fig, kidFace: Boolean) { with(Jp) {
    val a = L.age; val k = L.kind; val fem = L.sex == 'f'
    if (a < 14 && !kidFace) { val s = if (a < 4) 0.5 else if (a < 9) 0.62 else 0.72; save(); scale(s, s); person(L, true); restore(); return }
    val kid = kidFace; val old = a > 55; val sd = if (L.seed == 0) 1 else L.seed; val pat = (sd * 7 + 3).mod(4)
    val hr = if (L.hair == GREY1 || L.hair == GREY2) L.hair else HAIRJ
    val sk = if (fem && !kid && k != "beggar") FACEW else L.skin
    val meiji = year >= 1870
    val cl = when (k) {
        "barin" -> hex(0xFF2F3442); "soldier" -> if (meiji) hex(0xFF232E4A) else AI; "priest" -> hex(0xFF2E2A2A); "monk" -> hex(0xFF3A3430)
        "beggar" -> hex(0xFF8A857A); "bride" -> GOF; else -> jcloth(L.cloth)
    }
    val hatted = hat > 0 && !kid && (k == "" || k == "man" || k == "woman")
    val dark = mix(cl, SUMI, 0.45)
    val long = fem || k == "barin" || k == "priest" || k == "monk" || (k == "soldier" && meiji)
    if (!long) {
        val leg = if (k == "soldier") hex(0xFF2F3A52) else if (kid) L.skin else hex(0xFF34405A)
        quad(-3.8, -10.6, 2.8, 9.8, leg, 0.4); quad(1.0, -10.6, 2.8, 9.8, leg, 0.4)
        if (!kid) batch(al(SUMI, 0.4), 0.25) { seg(-3.8, -5.4, -1.0, -6.0); seg(1.0, -6.0, 3.8, -5.4) }
        for (s in intArrayOf(-1, 1)) { el(s * 2.8, -0.8, 2.2, 0.9, STRAW, 0.35); ln(pt(s * 2.0, -1.4, s * 2.8, -2.6), SUMI, 0.3) }
    }
    if (!fem) {
        when {
            k == "barin" -> {
                val H = pt(-5.6, -19.0, 5.6, -19.0, 8.2, -0.6, -8.2, -0.6); val hc = hex(0xFF56606E)
                for (s in intArrayOf(-1, 1)) el(s * 3.0, -0.6, 1.8, 0.8, GOF, 0.3)
                sh(H, hc, 0.4); inside(H) { kpat(1, hc, -9.0, -20.0, 9.0, 0.0) }; ln(pt(0.0, -19.0, 0.0, -0.6), SUMI, 0.35)
            }
            k == "priest" || k == "monk" -> {
                for (s in intArrayOf(-1, 1)) el(s * 2.6, -0.6, 1.8, 0.8, GOF, 0.3)
                val B = pt(-5.0, -27.6, 5.0, -27.6, 6.2, -18.0, 8.2, -0.6, -8.2, -0.6, -6.2, -18.0); sh(B, cl)
            }
            k == "soldier" && meiji -> {
                quad(-3.8, -12.0, 2.8, 11.0, hex(0xFFE6E2D8), 0.4); quad(1.0, -12.0, 2.8, 11.0, hex(0xFFE6E2D8), 0.4)
                for (s in intArrayOf(-1, 1)) { quad(s * 2.4 - 1.6, -4.0, 3.2, 3.4, SUMI, 0.3); el(s * 2.4 + s * 0.6, -0.8, 2.2, 0.9, SUMI, 0.3) }
            }
        }
        val sleeveLong = k == "priest" || k == "monk"
        for (s in intArrayOf(-1, 1)) {
            if (sleeveLong) sh(pt(s * 4.4, -27.0, s * 8.6, -24.0, s * 10.0, -10.0, s * 6.4, -9.2, s * 5.4, -18.0), cl, sm = true)
            else sh(pt(s * 4.6, -27.2, s * 8.6, -24.6, s * 9.6, -14.8, s * 6.2, -14.2, s * 5.4, -19.4), cl, sm = true)
        }
        val hem = when { k == "barin" -> -18.6; sleeveLong -> -0.6; k == "soldier" && meiji -> -12.0; else -> -9.8 }
        val hw = if (sleeveLong) 8.2 else 7.4
        val B = pt(-5.2, -27.6, 5.2, -27.6, 6.2, -19.0, hw, hem, -hw, hem, -6.2, -19.0)
        if (!sleeveLong) sh(B, cl)
        if (k == "" || k == "man" || (k == "kid")) inside(B) { kpat(pat, cl, -9.0, -28.0, 9.0, -9.0) }
        // collar: the white under-collar and the dark lapel band, left over right
        if (!(k == "soldier")) {
            sh(pt(-2.6, -27.8, 2.6, -27.8, 0.4, -22.0), GOF, 0.3)
            sh(pt(2.9, -27.8, 1.6, -27.8, -2.2, -18.8, -0.8, -18.8), dark, 0.3)
            sh(pt(-2.9, -27.8, -1.6, -27.8, 0.2, -22.4, -0.4, -21.2), dark, 0.3)
        }
        when (k) {
            "barin" -> {
                val kc = hex(0xFF56606E)
                for (s in intArrayOf(-1, 1)) {
                    sh(pt(s * 2.4, -27.9, s * 11.2, -29.2, s * 11.0, -27.0, s * 5.8, -22.6, s * 3.2, -19.0, s * 1.2, -19.0), kc, 0.4)
                    beginPath(); circle(s * 6.8, -26.6, 0.9); stroke(GOF, 0.35); dot(s * 6.8, -26.6, 0.3, GOF)
                }
                quad(-5.6, -19.8, 11.2, 1.3, hex(0xFF7A2E2A), 0.3)
                ln(pt(1.6, -18.8, 12.4, -13.6), SUMI, 1.3); ln(pt(1.6, -18.8, 12.4, -13.6), hex(0xFF2A2630), 0.8)
                ln(pt(1.6, -18.8, -3.0, -21.2), SUMI, 1.2); ln(pt(1.6, -18.8, -3.0, -21.2), GOF, 0.7); dot(1.6, -18.8, 0.8, KIN)
                ln(pt(2.4, -17.6, 10.6, -13.8), SUMI, 1.0); ln(pt(2.4, -17.6, -1.2, -19.6), GOF, 0.6)
            }
            "soldier" -> if (!meiji) {
                val D = pt(-5.8, -26.6, 5.8, -26.6, 6.2, -15.0, -6.2, -15.0); sh(D, hex(0xFF2A2628), 0.4)
                batch(hex(0xFFB03A2E), 0.45) { var y = -25.0; while (y < -15.4) { seg(-5.8, y, 5.8, y); y += 1.8 } }
                for (i in 0 until 3) { quad(-6.6 + i * 4.4, -15.0, 4.2, 4.6, hex(0xFF2A2628), 0.35); batch(hex(0xFFB03A2E), 0.35) { seg(-6.6 + i * 4.4, -13.4, -2.4 + i * 4.4, -13.4); seg(-6.6 + i * 4.4, -11.8, -2.4 + i * 4.4, -11.8) } }
                ln(pt(9.0, 1.0, 9.0, -50.0), WOOD2, 0.9); sh(pt(8.4, -50.0, 9.0, -56.0, 9.6, -50.0), hex(0xFFCFD4D8), 0.3); dot(9.0, -49.4, 0.8, BENI)
            } else {
                quad(-6.0, -20.0, 12.0, 1.4, GOF, 0.3); for (j in 0 until 4) dot(0.0, -25.6 + j * 2.4, 0.4, KIN)
                quad(-2.4, -27.8, 4.8, 1.4, BENI, 0.25)
                ln(pt(9.2, -4.0, 9.2, -38.0), SUMI, 1.2); ln(pt(9.2, -4.0, 9.2, -24.0), WOOD, 0.7); ln(pt(9.2, -38.0, 9.2, -45.0), hex(0xFFCFD4D8), 0.5)
            }
            "priest", "monk" -> {
                val kesa = if (k == "priest") hex(0xFFB8873A) else hex(0xFF7A5A3A)
                if (k == "priest") {
                    val K = pt(-6.6, -18.0, 4.0, -27.8, 6.0, -26.6, 7.4, -4.0, -7.4, -4.0)
                    sh(K, kesa, 0.35); inside(K) { batch(al(SUMI, 0.5), 0.3) { var x = -8.0; while (x < 8) { seg(x, -28.0, x, -4.0); x += 3.0 }; seg(-8.0, -12.0, 8.0, -12.0) } }
                } else { sh(pt(3.4, -27.8, 5.6, -27.0, -5.2, -12.0, -6.4, -14.2), kesa, 0.3); quad(-2.6, -22.0, 5.2, 5.0, kesa, 0.3) }
                for (i in 0 until 9) { val an = PI * (0.15 + i * 0.09); dot(cos(an) * 2.4, -12.4 + sin(an) * 2.0, 0.45, hex(0xFF5A3A2A)) }
            }
            "beggar" -> {}
            else -> { quad(-6.4, -18.8, 12.8, 2.4, hex(0xFF2B2A33), 0.3); ln(pt(4.0, -17.6, 5.0, -15.0), hex(0xFF2B2A33), 0.6) }
        }
    } else {
        val bride = k == "bride"; val nun = k == "beggar"
        for (s in intArrayOf(-1, 1)) { quad(s * 2.6 - 1.3, -1.0, 2.6, 1.0, WOOD, 0.25); el(s * 2.6, -1.6, 1.3, 0.8, GOF, 0.25) }
        val young = a < 25 && !nun
        for (s in intArrayOf(-1, 1)) sh(pt(s * 4.2, -26.6, s * 7.6, -23.4, s * 8.8, if (young || bride) -6.0 else -12.0, s * 6.0, if (young || bride) -5.4 else -11.4, s * 5.2, -18.0), cl, sm = true)
        val B = pt(-4.6, -27.0, 4.6, -27.0, 5.2, -19.0, 6.6, -4.0, 8.8, -0.6, -8.8, -0.6, -6.6, -4.0, -5.2, -19.0); sh(B, cl)
        inside(B) {
            if (bride) kpat(3, hex(0xFFDCD6CC), -10.0, -28.0, 10.0, 0.0) else if (!nun) kpat(pat, cl, -10.0, -28.0, 10.0, 0.0)
            if (!nun) quad(-11.0, -2.2, 22.0, 1.8, if (bride) BENI else BENI2, 0.25, false)
        }
        sh(pt(-2.4, -27.4, 2.4, -27.4, 0.3, -22.6), if (bride) BENI else GOF, 0.3)
        sh(pt(2.7, -27.4, 1.5, -27.4, -1.8, -20.6, -0.6, -20.6), if (bride) GOF else dark, 0.3)
        sh(pt(-2.7, -27.4, -1.5, -27.4, 0.2, -23.0, -0.4, -22.0), if (bride) GOF else dark, 0.3)
        if (!nun) {
            val ob = if (bride) hex(0xFFE9E2D4) else jobi(L.kerch)
            quad(-5.6, -22.4, 11.2, 5.2, ob, 0.4)
            inside(pt(-5.6, -22.4, 5.6, -22.4, 5.6, -17.2, -5.6, -17.2)) { kpat(2, ob, -6.0, -23.0, 6.0, -17.0) }
            ln(pt(-5.6, -19.8, 5.6, -19.8), if (bride) KIN else mix(ob, BENI, 0.6), 0.5); dot(0.0, -19.8, 0.6, KIN)
        } else {
            quad(-5.4, -19.4, 10.8, 1.4, hex(0xFF5A564E), 0.3)
            ln(pt(9.4, 1.0, 9.4, -40.0), SUMI, 1.0); ln(pt(9.4, 1.0, 9.4, -40.0), WOOD2, 0.5)
            beginPath(); circle(9.4, -41.8, 1.6); stroke(hex(0xFF8A8A8A), 0.4); dot(8.0, -41.0, 0.4, hex(0xFF8A8A8A)); dot(10.8, -41.0, 0.4, hex(0xFF8A8A8A))
        }
    }
    val hsk = if (fem) sk else L.skin
    for (s in intArrayOf(-1, 1)) el(s * (if (fem) 7.2 else 7.6), if (fem) -12.6 else -14.4, 1.1, 1.2, hsk, 0.35, n = 8)
    if (hatted && hat == 2) {
        sh(pt(-4.2, -28.8, 4.2, -28.8, 9.8, -14.0, 7.4, -12.0, 3.0, -11.0, -3.0, -11.0, -7.4, -12.0, -9.8, -14.0), hex(0xFFA88C52), 0.4)
        batch(al(hex(0xFF6E5A30), 0.85), 0.25) { var i = -1.0; while (i <= 1.0) { seg(i * 3.0, -28.4, i * 9.4, -11.6); i += 0.1 } }
    }
    quad(-1.2, -29.6, 2.4, 2.6, sk, 0.3)
    /* head */
    save(); translate(0.0, -33.5); if (kid) scale(1.12, 1.12)
    val nun = fem && k == "beggar"; val bride = fem && k == "bride"
    if (fem && !kid && !nun && !bride) sh(pt(-5.2, 1.4, -5.8, -1.8, -4.8, -4.8, 0.0, -6.0, 4.8, -4.8, 5.8, -1.8, 5.2, 1.4, 3.4, 3.0, -3.4, 3.0), hr, 0.4, sm = true)
    if (nun) sh(pt(-5.0, 3.8, -5.4, -1.8, -4.0, -5.2, 0.0, -6.2, 4.0, -5.2, 5.4, -1.8, 5.0, 3.8, 3.4, 5.2, -3.4, 5.2), GOF, 0.4, sm = true)
    el(0.0, 0.3, if (kid) 4.2 else if (fem) 3.7 else 3.9, if (kid) 4.3 else 4.7, sk, 0.45)
    val monkish = !fem && (k == "priest" || k == "monk")
    val soldierHat = !fem && k == "soldier"
    if (kid) {
        sh(pt(-4.5, 2.2, -4.8, -1.6, -3.4, -4.6, 0.0, -5.4, 3.4, -4.6, 4.8, -1.6, 4.5, 2.2, 3.7, 2.2, 3.6, -1.2, -3.6, -1.2, -3.7, 2.2), hr, 0.4, sm = true)
        if (fem && a >= 6) { el(3.6, -3.6, 1.2, 0.8, BENI, 0.3); el(-3.6, -3.6, 1.2, 0.8, BENI, 0.3) }
    } else if (fem && !nun && !bride) {
        sh(pt(-3.9, -0.4, -3.6, -3.6, -1.6, -4.6, 0.0, -4.7, 1.6, -4.6, 3.6, -3.6, 3.9, -0.4, 2.8, -2.4, 0.9, -3.1, 0.0, -2.4, -0.9, -3.1, -2.8, -2.4), hr, 0.4, sm = true)
        el(0.0, -7.0, if (old) 2.6 else 3.4, if (old) 1.7 else 1.9, hr, 0.4)
        if (!old) {
            quad(-1.7, -7.5, 3.4, 1.0, BENI, 0.0)
            sh(pt(-2.6, -4.9, -2.2, -6.1, 2.2, -6.1, 2.6, -4.9), hex(0xFFC08A3A), 0.3)
            ln(pt(1.6, -6.6, 5.8, -9.2), KIN, 0.45); dot(5.9, -9.3, 0.7, BENI); ln(pt(-1.4, -6.8, -5.4, -8.6), KIN, 0.45)
            if (a < 25) for (i in 0 until 3) dot(5.6 + i * 0.4, -8.0 + i * 0.9, 0.35, if (i % 2 == 0) GOF else BENI)
        }
    } else if (!fem && !soldierHat && !monkish) {
        val bald = old && sd % 3 == 0
        if (!bald) sh(pt(-3.5, -1.9, -3.2, -3.9, -1.6, -4.9, 0.0, -5.1, 1.6, -4.9, 3.2, -3.9, 3.5, -1.9, 0.0, -2.6), mix(L.skin, hex(0xFF8FA4B8), 0.38), 0.3, sm = true)
        for (s in intArrayOf(-1, 1)) if (bald) sh(pt(s * 4.1, 1.2, s * 4.2, -1.0, s * 3.5, -1.6, s * 3.2, 1.2), hr, 0.3) else sh(pt(s * 4.2, 1.4, s * 4.4, -2.2, s * 3.5, -3.6, s * 2.6, -1.6, s * 3.1, 1.6), hr, 0.35)
        if (!bald) { sh(pt(-0.8, -4.6, -0.2, -6.2, 3.4, -6.8, 3.8, -6.0, 0.8, -5.2), hr, 0.35); ln(pt(0.2, -5.6, 0.6, -6.4), GOF, 0.4) }
    } else if (monkish) {
        fillShader(Jp.vgrad("pate", -4.8, -1.0, intArrayOf(al(PATE, 0.9), al(PATE, 0.0))), -4.0, -4.8, 8.0, 3.8)
        sh(ellP(0.0, 0.3, 3.9, 4.7), 0, 0.45, sm = true)
    } else if (soldierHat && meiji) {
        for (s in intArrayOf(-1, 1)) sh(pt(s * 3.9, 0.6, s * 4.1, -2.0, s * 3.0, -2.8, s * 2.8, 0.4), hr, 0.3)
    }
    if (bride) sh(pt(-5.8, 1.6, -6.2, -2.6, -4.8, -6.2, 0.0, -7.6, 4.8, -6.2, 6.2, -2.6, 5.8, 1.6, 4.0, -0.6, 0.0, -1.6, -4.0, -0.6), GOF, 0.4, sm = true)
    if (nun) sh(pt(-4.0, -0.8, -3.6, -3.8, 0.0, -4.8, 3.6, -3.8, 4.0, -0.8, 2.4, -2.6, 0.0, -3.0, -2.4, -2.6), GOF, 0.35, sm = true)
    // the face: narrow stroke eyes, high brows, a long hooked nose, a small red mouth
    val ey = 0.0
    for (s in intArrayOf(-1, 1)) {
        if (fem) ln(pt(s * 0.8, ey, s * 2.3, ey - 0.6), SUMI, 0.45) else ln(pt(s * 0.9, ey - 0.1, s * 2.3, ey - 0.5), SUMI, 0.5)
        if (fem && !kid) ln(pt(s * 1.0, -2.3, s * 1.8, -2.75, s * 2.6, -2.6), SUMI, 0.28, true)
        else if (k == "barin") ln(pt(s * 0.8, -1.4, s * 2.7, -2.3), SUMI, 0.6)
        else ln(pt(s * 0.9, -1.7, s * 1.8, -2.2, s * 2.7, -2.0), SUMI, 0.45, true)
        if (kid) dot(s * 2.4, 1.7, 0.9, al(hex(0xFFE08A80), 0.4))
    }
    ln(pt(0.3, -0.7, 0.75, 1.5, 0.0, 1.75), SUMI, 0.32)
    if (fem || kid) el(0.0, 2.8, 0.6, 0.34, BENI, 0.0, false, 10)
    else { ln(pt(-0.7, 2.9, 0.7, 2.9), SUMI, 0.38); dot(0.0, 3.05, 0.3, BENI) }
    if (!fem && !kid && a >= 25 && !monkish && !old) sh(pt(-3.3, 1.6, -2.0, 3.9, 0.0, 4.8, 2.0, 3.9, 3.3, 1.6, 1.2, 3.4, -1.2, 3.4), al(hex(0xFF6E8296), 0.18), 0.0, sm = true)
    if (old) {
        batch(al(SUMI, 0.5), 0.25) { seg(-1.4, -3.0, 1.4, -3.0); seg(-2.8, 1.2, -2.2, 1.8); seg(2.8, 1.2, 2.2, 1.8) }
        if (!fem) sh(pt(-1.2, 4.0, 0.0, 7.0, 1.2, 4.0), hr, 0.3)
    }
    if (soldierHat) {
        if (!meiji) { sh(pt(-7.0, -1.6, 0.0, -5.8, 7.0, -1.6, 0.0, -0.8), hex(0xFF25221F), 0.4); dot(0.0, -3.2, 0.9, KIN); ln(pt(-3.4, -1.2, -2.6, 4.2), SUMI, 0.25); ln(pt(3.4, -1.2, 2.6, 4.2), SUMI, 0.25) }
        else { quad(-3.6, -7.2, 7.2, 4.4, hex(0xFF232E4A), 0.35); quad(-3.6, -4.0, 7.2, 1.1, BENI, 0.25); sh(pt(-3.8, -2.9, 4.2, -2.9, 5.4, -2.0, -2.6, -2.2), SUMI, 0.25) }
    }
    if (hatted) {
        sh(pt(-8.4, -0.4, 0.0, -6.8, 8.4, -0.4, 6.4, 0.4, -6.4, 0.4), STRAW, 0.4)
        batch(al(hex(0xFF8A6A30), 0.85), 0.25) { for (i in -4..4) seg(0.0, -6.6, i * 1.9, 0.2) }
        batch(al(hex(0xFF8A6A30), 0.6), 0.22) { seg(-5.6, -2.0, 5.6, -2.0); seg(-3.0, -4.2, 3.0, -4.2) }
        ln(pt(-3.4, 0.2, -2.4, 4.8, 0.0, 5.2, 2.4, 4.8, 3.4, 0.2), SUMI, 0.25, true)
    }
    restore()
} }

/** portrait grounds: gold mica with a seal, indigo bokashi, pale pink to green */
internal fun Ctx.edoPbg(bg: PBg) = with(Jp) {
    when (bg) {
        PBg.HEAD -> cached("mica", 0.0, 0.0, 24.0, 24.0) {
            fillShader(vgrad("mica", 0.0, 24.0, intArrayOf(hex(0xFFE2CC92), hex(0xFFCDB077))), 0.0, 0.0, 24.0, 24.0)
            val r = Rng(17); repeat(90) { dot(r.next() * 24, r.next() * 24, 0.12 + r.next() * 0.22, al(if (r.next() < 0.6) GOF else hex(0xFFA88A4A), 0.7)) }
            seal(18.2, 1.6, 4.2, 7)
        }
        PBg.BLOOD -> fillShader(vgrad("pbb", 0.0, 24.0, intArrayOf(hex(0xFF1D3B6A), hex(0xFF5F86B0), hex(0xFFC9D9DC)), floatArrayOf(0f, 0.4f, 1f)), 0.0, 0.0, 24.0, 24.0)
        PBg.INLAW -> fillShader(vgrad("pbi", 0.0, 24.0, intArrayOf(hex(0xFFEFC2C0), hex(0xFFF3E6D2), hex(0xFFC9D8B8)), floatArrayOf(0f, 0.5f, 1f)), 0.0, 0.0, 24.0, 24.0)
    }
}
