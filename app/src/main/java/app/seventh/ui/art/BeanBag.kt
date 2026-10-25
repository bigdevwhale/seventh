package app.seventh.ui.art

import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.Shader
import app.seventh.ui.art.Jp.al
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * The bag of beans, logical 120×56: the bag on the left, the beans that are in it laid out on the right.
 * shake rocks the bag on its bottom and jiggles the beans in its mouth; rise 0..1 lifts the drawn bean out,
 * growing, and it lands in a glow with rays. Each land has its own bag: an embroidered linen sack (lubok),
 * a brocade purse with a coat of arms (manuscript), an indigo furoshiki bundle (ukiyo-e).
 */

/** a kidney bean, 5.6 × 3.8 at s = 1, concave side down */
private val BEAN = pt(-2.8, 0.1, -2.5, -1.2, -1.4, -1.9, 0.2, -2.0, 1.7, -1.7, 2.7, -0.7, 2.8, 0.5, 2.2, 1.5, 1.1, 1.7, 0.3, 1.3, -0.6, 1.6, -1.8, 1.8, -2.6, 1.1)
private class BeanCol(val body: Int, val shade: Int, val hi: Int, val hil: Int)
private val BW = BeanCol(hex(0xFFF2E8D0), hex(0xFFC4AE84), hex(0xFFFFFFFF), hex(0xFFB0905E))
private val BB = BeanCol(hex(0xFF2B221E), hex(0xFF0E0A08), hex(0xFFA2ACC0), hex(0xFFE9E1CC))
private val BR = BeanCol(hex(0xFFAA2A20), hex(0xFF56100C), hex(0xFFF6B8A8), hex(0xFFF2EAD8))

internal fun Ctx.bean(x: Double, y: Double, kind: Char, s: Double = 1.0, rot: Double = 0.0) {
    val c = when (kind) { 'w' -> BW; 'b' -> BB; else -> BR }
    save(); translate(x, y); rotate(rot); scale(s, s)
    val j = J; J = 0.0
    sh(BEAN, c.body, 0.42, off = false, sm = true)
    inside(BEAN, true) { el(0.5, 1.5, 3.2, 1.5, al(c.shade, 0.55), 0.0, false) }
    el(0.25, 1.15, 0.95, 0.3, c.hil, 0.0, false); ln(pt(-0.5, 1.15, 1.0, 1.15), al(c.shade, 0.8), 0.16)
    el(-1.0, -0.95, 1.25, 0.45, al(c.hi, 0.75), 0.0, false); dot(-1.7, -1.05, 0.28, al(c.hi, 0.95))
    J = j; restore()
}

/** Dispatch by land, each in its own print style. */
internal fun Ctx.bagPicture(beans: List<Char>, shake: Double, idx: Int, res: Char?, rise: Double) = when {
    edo -> with(Jp) { style { bagIn(beans, shake, idx, res, rise) } }
    eu -> with(Eu) { style { bagIn(beans, shake, idx, res, rise) } }
    else -> bagIn(beans, shake, idx, res, rise)
}

private val BODY = pt(-7.6, 24.6, -14.6, 28.2, -18.8, 37.0, -18.0, 46.8, -11.6, 53.4, 0.0, 54.6, 11.6, 53.4, 18.0, 46.8, 18.8, 37.0, 14.6, 28.2, 7.6, 24.6)
private val NECK = pt(-7.6, 25.4, -6.2, 21.6, -8.8, 17.6, -6.6, 15.6, -3.2, 16.4, 0.0, 15.2, 3.2, 16.4, 6.6, 15.6, 8.8, 17.6, 6.2, 21.6, 7.6, 25.4)
private const val BX = 28.0

private fun Ctx.bagIn(beans: List<Char>, shake: Double, idx: Int, res: Char?, rise: Double) {
    // still: the ground and the beans laid out
    fillRect(0.0, 0.0, 120.0, 56.0, when { edo -> hex(0xFFEFE3C4); eu -> hex(0xFFF1E6CC); else -> hex(0xFFEFE0B6) })
    val shown = beans.filterIndexed { i, _ -> i != idx }
    val rows = maxOf(1, (shown.size + 7) / 8)
    tray(53.0, 4.0, 65.0, rows * 8.6 + 6.4)
    shown.forEachIndexed { i, b -> bean(60.0 + (i % 8) * 7.3, 11.0 + (i / 8) * 8.6, b, 1.0, ((i * 37) % 7 - 3) * 0.09) }
    if (edo) with(Jp) { seal(110.0, 47.0, 5.0, 3); grain(120.0, 56.0) }
    // moving: the bag, the beans in its mouth, the drawn bean
    run {
        save(); translate(BX + shake * 0.6, 54.0); rotate(shake * 0.035); translate(0.0, -54.0)
        val inMouth = beans.filterIndexed { i, _ -> i != idx }.let { l -> listOf(l.getOrNull(0), l.getOrNull(l.size / 2), l.getOrNull(l.size - 1), l.getOrNull(1)) }.filterNotNull()
        val jig = { i: Int -> -kotlin.math.abs(shake) * 0.35 * (if (i % 2 == 0) 1 else -1) }
        val early = res != null && rise <= 0.28
        val by = 16.4 - rise * 10.4; val bs = 0.85 + rise * 1.15; val brot = (1 - rise) * 0.5
        when {
            edo -> furoshiki(inMouth, jig, if (early) res else null, by, bs, brot)
            eu -> purse(inMouth, jig, if (early) res else null, by, bs, brot)
            else -> sack(inMouth, jig, if (early) res else null, by, bs, brot)
        }
        if (res != null && !early) {
            if (rise > 0.55) {
                val g = (rise - 0.55) / 0.45
                val gc = when (res) { 'w' -> hex(0xFFF6D777); 'r' -> hex(0xFFE04A30); else -> hex(0xFF7A7A9A) }
                fillShader(RadialGradient(0f, by.toFloat(), (10 * bs).toFloat(), intArrayOf(al(gc, 0.6 * g), al(gc, 0.0)), null, Shader.TileMode.CLAMP), -10 * bs, by - 10 * bs, 20 * bs, 20 * bs)
                if (rise >= 0.9) {
                    val k = (rise - 0.9) / 0.1
                    val rc = when (res) { 'w' -> if (edo) Jp.KIN else OCH; 'r' -> KIN; else -> hex(0xFF5A5A70) }
                    beginPath()
                    for (i in 0 until 12) { val a = i / 12.0 * 2 * PI + 0.13; val r0 = 4.6 * bs * 0.75; val r1 = r0 + (if (i % 2 == 0) 4.2 else 2.6) * (0.4 + 0.6 * k); moveTo(cos(a) * r0, by + sin(a) * r0 * 0.85); lineTo(cos(a) * r1, by + sin(a) * r1 * 0.85) }
                    stroke(rc, 0.65)
                }
            }
            bean(0.0, by, res, bs, brot)
        }
        restore()
    }
    paper(0.6)
}

private fun Ctx.tray(x: Double, y: Double, w: Double, h: Double) {
    val T = pt(x + 2, y, x + w - 2, y, x + w, y + 2, x + w, y + h - 2, x + w - 2, y + h, x + 2, y + h, x, y + h - 2, x, y + 2)
    when {
        edo -> with(Jp) {
            sh(T, hex(0xFFD8C28A), 0.4)
            inside(T) { batch(al(hex(0xFF9A7E44), 0.55), 0.3) { var yy = y + 1.2; while (yy < y + h) { seg(x, yy, x + w, yy); yy += 1.3 } }; batch(al(hex(0xFF9A7E44), 0.5), 0.25) { var xx = x + 8.0; while (xx < x + w) { seg(xx, y, xx, y + h); xx += 8.0 } } }
            quad(x, y - 0.6, w, 1.6, hex(0xFF2A2A3A), 0.3); quad(x, y + h - 1.0, w, 1.6, hex(0xFF2A2A3A), 0.3)
        }
        eu -> with(Eu) {
            sh(T, hex(0xFFF4EEDC), 0.45)
            inside(T) {
                beginPath(); moveTo(x, y + 1.6); lineTo(x + w, y + 1.6); moveTo(x, y + h - 1.6); lineTo(x + w, y + h - 1.6); stroke(VM, 0.6)
                beginPath(); moveTo(x, y + 2.6); lineTo(x + w, y + 2.6); moveTo(x, y + h - 2.6); lineTo(x + w, y + h - 2.6); stroke(al(UM, 0.8), 0.3)
            }
        }
        else -> {
            sh(T, hex(0xFFD6AE72), 0.6)
            inside(T) { fade(0.35) { beginPath(); var yy = y + 2.0; while (yy < y + h) { moveTo(x, yy); quadTo(x + w * 0.5, yy + 1.2, x + w, yy - 0.4); yy += 2.6 }; stroke(hex(0xFF8A5A2A), 0.3) } }
            strokeRect(x + 1.4, y + 1.4, w - 2.8, h - 2.8, al(hex(0xFF8A5A2A), 0.7), 0.35)
            for (i in 0 until 6) dot(x + 6 + i * (w - 12) / 5, y + h - 0.9, 0.5, KIN)
        }
    }
}

/** the beans in the mouth, then the front lip over their bottoms */
private fun Ctx.mouth(cx: Double, cy: Double, rx: Double, ry: Double, inMouth: List<Char>, jig: (Int) -> Double, early: Char?, by: Double, bs: Double, brot: Double, lip: Int, lipInk: Int = 0) {
    el(cx, cy, rx, ry, hex(0xFF24180F), 0.0, false)
    val xs = doubleArrayOf(-3.0, 0.4, 3.2, -1.2)
    val ys = doubleArrayOf(0.1, -0.6, 0.2, 0.6)
    inMouth.forEachIndexed { i, b -> bean(cx + xs[i] * rx / 6.6, cy - 0.5 + ys[i] + jig(i), b, 0.78, (i - 1.5) * 0.35) }
    if (early != null) bean(0.0, by, early, bs, brot)
    val p = Pb(40)
    for (i in 0..12) { val a = i / 12.0 * PI; p.add(cx + cos(a) * rx, cy + sin(a) * ry) }
    for (i in 12 downTo 0) { val a = i / 12.0 * PI; p.add(cx + cos(a) * (rx + 0.7), cy + 0.3 + sin(a) * (ry + 1.0)) }
    sh(p.arr(), lip, 0.4, off = false, sm = true, ink = lipInk)
}

private fun Ctx.folds(col: Int, a: Double) = fade(a) {
    beginPath()
    for (i in -3..3) { moveTo(i * 1.5, 18.0); lineTo(i * 1.9, 24.4) }
    for (i in -2..2) { moveTo(i * 2.4, 26.4); quadTo(i * 5.6, 32.0, i * 7.0, 41.0 + kotlin.math.abs(i) * 1.2) }
    stroke(col, 0.32)
}
private fun Ctx.shading(cx: Double) {
    inside(BODY, true) {
        fillShader(LinearGradient((-19).toFloat(), 0f, 19f, 0f, intArrayOf(al(hex(0xFFFFFFFF), 0.18), al(hex(0xFFFFFFFF), 0.0), al(INK, 0.3)), floatArrayOf(0f, 0.4f, 1f), Shader.TileMode.CLAMP), cx - 20, 24.0, 40.0, 32.0)
        fillShader(LinearGradient(0f, 44f, 0f, 55f, intArrayOf(al(INK, 0.0), al(INK, 0.22)), null, Shader.TileMode.CLAMP), cx - 20, 44.0, 40.0, 11.0)
    }
}

/* lubok: a linen sack, a red embroidered band, the eight-pointed star, a red-and-white twisted cord with a tassel */
private fun Ctx.sack(inMouth: List<Char>, jig: (Int) -> Double, early: Char?, by: Double, bs: Double, brot: Double) {
    val linen = hex(0xFFCF9F5C); val neck = hex(0xFFDCAE6C)
    sh(BODY, linen, 0.8, sm = true)
    inside(BODY, true) {
        fade(0.13) { beginPath(); var x = -20.0; while (x < 20) { moveTo(x, 24.0); lineTo(x + 3, 55.0); x += 1.3 }; var y = 25.0; while (y < 55) { moveTo(-20.0, y); lineTo(20.0, y - 0.6); y += 1.3 }; stroke(INK, 0.22) }
        // embroidery: a band of red rhombi between two rows of cross-stitch
        beginPath(); var x = -19.2
        while (x < 19) { moveTo(x, 46.2); lineTo(x + 1.3, 44.0); lineTo(x + 2.6, 46.2); lineTo(x + 1.3, 48.4); lineTo(x, 46.2); x += 2.6 }
        stroke(KIN, 0.55)
        beginPath(); x = -19.0; while (x < 19) { moveTo(x - 0.4, 42.6); lineTo(x + 0.4, 43.4); moveTo(x + 0.4, 42.6); lineTo(x - 0.4, 43.4); moveTo(x - 0.4, 49.2); lineTo(x + 0.4, 50.0); moveTo(x + 0.4, 49.2); lineTo(x - 0.4, 50.0); x += 1.6 }
        stroke(KIN2, 0.32)
        x = -17.9; while (x < 19) { dot(x, 46.2, 0.32, OCH); x += 2.6 }
    }
    shading(0.0)
    // the star of luck
    sh(pt(0.0, 28.6, 2.0, 32.8, 6.6, 33.8, 2.6, 36.4, 4.0, 41.0, 0.0, 38.6, -4.0, 41.0, -2.6, 36.4, -6.6, 33.8, -2.0, 32.8), KIN, 0.45)
    dot(0.0, 35.6, 1.1, YEL); dot(0.0, 35.6, 0.4, KIN)
    for (a in 0 until 4) { val an = a * PI / 2 + PI / 4; dot(cos(an) * 7.6, 35.4 + sin(an) * 6.0, 0.5, KIN) }
    folds(INK, 0.35)
    sh(NECK, neck, 0.7, sm = true)
    mouth(0.0, 17.0, 6.6, 2.0, inMouth, jig, early, by, bs, brot, neck)
    // the cord: red and white twist, a knot and a tassel
    val cord = pt(-7.0, 22.0, 0.0, 23.6, 7.0, 22.0)
    ln(cord, INK, 2.2, true); ln(cord, KIN, 1.4, true)
    fade(0.9) { beginPath(); var t = -6.4; while (t < 6.6) { val yy = 22.0 + (1 - (t / 7.0) * (t / 7.0)) * 1.6; moveTo(t - 0.3, yy - 0.5); lineTo(t + 0.3, yy + 0.5); t += 1.1 }; stroke(WHT, 0.45) }
    val tail = pt(5.4, 23.0, 7.6, 27.0, 6.8, 30.4)
    ln(tail, INK, 1.2, true); ln(tail, KIN, 0.7, true)
    el(5.0, 23.0, 1.3, 1.1, KIN, 0.4)
    sh(pt(5.6, 30.2, 8.0, 30.2, 8.8, 34.6, 4.8, 34.6), KIN, 0.4)
    fade(0.7) { beginPath(); for (i in 0..4) { moveTo(5.4 + i * 0.7, 31.0); lineTo(5.0 + i * 0.85, 34.4) }; stroke(KIN2, 0.25) }
}

/* manuscript: a crimson brocade purse with gold lattice, an azure shield with a gold lily, gold cord and tassels */
private fun Ctx.purse(inMouth: List<Char>, jig: (Int) -> Double, early: Char?, by: Double, bs: Double, brot: Double) = with(Eu) {
    val cr = hex(0xFF7E1F1C); val crN = hex(0xFF8E2622)
    sh(BODY, cr, 0.7, sm = true)
    inside(BODY, true) {
        beginPath(); var x = -40.0; while (x < 40) { moveTo(x, 24.0); lineTo(x + 31.0, 55.0); moveTo(x, 55.0); lineTo(x + 31.0, 24.0); x += 3.2 }; stroke(al(GD, 0.55), 0.3)
        beginPath(); var y = 25.6; var row = 0
        while (y < 55) { var xx = -19.2 + (row % 2) * 1.6; while (xx < 20) { circle(xx, y, 0.32); xx += 3.2 }; y += 1.6; row++ }
        fill(al(GDL, 0.8))
    }
    shading(0.0)
    val S = pt(-5.2, 29.0, 5.2, 29.0, 5.2, 35.4, 3.6, 38.6, 0.0, 40.8, -3.6, 38.6, -5.2, 35.4)
    sh(S, UM, 0.45); poly(S, true, false); stroke(GD, 0.7)
    sh(pt(0.0, 30.6, 0.9, 32.6, 0.6, 35.0, 0.0, 36.0, -0.6, 35.0, -0.9, 32.6), GD, 0.25)
    sh(pt(-0.6, 34.2, -2.8, 32.4, -3.4, 34.0, -1.8, 35.4), GD, 0.25); sh(pt(0.6, 34.2, 2.8, 32.4, 3.4, 34.0, 1.8, 35.4), GD, 0.25)
    quad(-2.2, 35.4, 4.4, 0.8, GD, 0.2); dot(0.0, 37.4, 0.4, GD)
    folds(EI, 0.4)
    sh(NECK, crN, 0.6, sm = true)
    inside(NECK, true) { beginPath(); var x = -9.0; while (x < 9) { moveTo(x, 15.0); lineTo(x + 0.6, 25.0); x += 1.5 }; stroke(al(GD, 0.5), 0.25) }
    mouth(0.0, 17.0, 6.6, 2.0, inMouth, jig, early, by, bs, brot, GD)
    val cord = pt(-7.0, 22.0, 0.0, 23.6, 7.0, 22.0)
    ln(cord, EI, 1.9, true); ln(cord, GD, 1.2, true)
    fade(0.8) { beginPath(); var t = -6.4; while (t < 6.6) { val yy = 22.0 + (1 - (t / 7.0) * (t / 7.0)) * 1.6; moveTo(t - 0.3, yy - 0.5); lineTo(t + 0.3, yy + 0.5); t += 1.0 }; stroke(GDD, 0.35) }
    for (s in intArrayOf(-1, 1)) {
        val tail = pt(s * 5.6, 23.0, s * 8.6, 27.4, s * 8.0, 31.0)
        ln(tail, EI, 1.0, true); ln(tail, GD, 0.6, true)
        el(s * 8.0, 31.4, 0.9, 0.8, GD, 0.3)
        sh(pt(s * 7.0, 31.8, s * 9.0, 31.8, s * 9.8, 36.0, s * 6.2, 36.0), GD, 0.35)
        fade(0.8) { beginPath(); for (i in 0..4) { moveTo(s * (6.6 + i * 0.6), 32.4); lineTo(s * (6.4 + i * 0.8), 35.8) }; stroke(GDD, 0.22) }
    }
}

/* ukiyo-e: an indigo furoshiki with white karakusa scrolls, knotted at the top into two ears */
private fun Ctx.furoshiki(inMouth: List<Char>, jig: (Int) -> Double, early: Char?, by: Double, bs: Double, brot: Double) = with(Jp) {
    val cloth = hex(0xFF26406A); val cloth2 = hex(0xFF1E3458)
    sh(BODY, cloth, 0.6, sm = true)
    inside(BODY, true) {
        batch(al(GOF, 0.85), 0.42) {
            for ((cx, cy) in listOf(-8.0 to 34.0, 6.0 to 31.0, -12.0 to 46.0, 2.0 to 44.0, 13.0 to 42.0, -3.0 to 52.0, 16.0 to 33.0)) {
                for (i in 0..18) { val t = i / 18.0; val an = t * 3.4 * PI; val rr = 3.4 * (1 - t * 0.8); val px = cx + cos(an) * rr; val py = cy + sin(an) * rr * 0.8; if (i == 0) moveTo(px, py) else lineTo(px, py) }
                moveTo(cx + 3.4, cy); quadTo(cx + 5.8, cy - 3.0, cx + 7.6, cy + 1.0)
            }
        }
    }
    shading(0.0)
    folds(al(GOF, 1.0), 0.18)
    // the ears of the knot
    sh(pt(-1.6, 19.6, -9.6, 10.2, -13.6, 11.2, -12.6, 15.6, -5.4, 21.6), cloth, 0.5, sm = true)
    sh(pt(1.6, 19.6, 8.4, 9.2, 12.8, 9.8, 12.2, 14.4, 5.4, 21.6), cloth2, 0.5, sm = true)
    batch(al(GOF, 0.7), 0.32) { moveTo(-3.4, 18.8); quadTo(-8.0, 13.2, -11.8, 12.0); moveTo(3.4, 18.8); quadTo(7.6, 12.6, 11.0, 10.8) }
    sh(pt(-9.4, 26.8, -5.0, 19.8, 5.0, 19.8, 9.4, 26.8, 0.0, 25.4), cloth, 0.5, sm = true)
    batch(al(GOF, 0.55), 0.28) { for (i in -3..3) seg(i * 1.2, 20.2, i * 2.4, 25.2) }
    mouth(0.0, 18.4, 4.6, 1.5, inMouth, jig, early, by, bs, brot, cloth)
    el(0.0, 21.8, 3.8, 2.4, cloth2, 0.5)
    ln(pt(-2.6, 21.2, 0.0, 22.8, 2.6, 21.2), al(GOF, 0.7), 0.35, true)
}
