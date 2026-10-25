package app.seventh.ui.art

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/* the bag of beans, the red thread, relics */

internal fun Ctx.beanDraw(x: Double, y: Double, kind: Char, s: Double = 1.0) {
    val col = when (kind) { 'w' -> WHT; 'b' -> hex(0xFF2B231D); else -> KIN }
    el(x, y, 2.6 * s, 1.9 * s, col, 0.5, false, 12)
    dot(x - 0.9 * s, y - 0.6 * s, 0.55 * s, if (kind == 'b') hex(0xFF6A5A4C) else WHITE)
    ln(pt(x + 0.2 * s, y - 1.2 * s, x + 0.6 * s, y + 1 * s), if (kind == 'b') hex(0xFF5A4A3C) else rgba(31, 23, 18, 0.35), 0.3 * s, true)
}

internal fun Ctx.drawBag(beans: List<Char>, shake: Double, idx: Int, res: Char?, rise: Double) {
    fillRect(0.0, 0.0, 120.0, 56.0, hex(0xFFEFE0B6))
    save(); translate(27 + shake, 0.0); val cx = 0.0
    val sack = pt(cx - 6, 22.0, cx - 14, 28.0, cx - 18, 38.0, cx - 16, 48.0, cx - 8, 53.5, cx + 8, 53.5, cx + 16, 48.0, cx + 18, 38.0, cx + 14, 28.0, cx + 6, 22.0)
    sh(sack, hex(0xFFCF9F5C), 0.8, sm = true)
    hatch(pt(cx + 4, 24.0, cx + 14, 28.0, cx + 18, 38.0, cx + 16, 48.0, cx + 8, 53.5, cx + 6, 40.0), sm = true, a = 0.3, sp = 1.4)
    sh(pt(cx, 31.0, cx + 2, 35.4, cx + 6.6, 36.4, cx + 2.6, 39.0, cx + 4, 44.0, cx, 41.4, cx - 4, 44.0, cx - 2.6, 39.0, cx - 6.6, 36.4, cx - 2, 35.4), KIN, 0.45)
    dot(cx, 38.4, 1.0, YEL)
    for (i in 0 until 7) dot(cx - 9 + i * 3, 48.6, 0.6, KIN)
    sh(pt(cx - 6, 23.0, cx - 5, 17.6, cx - 7.4, 14.4, cx, 13.0, cx + 7.4, 14.4, cx + 5, 17.6, cx + 6, 23.0), hex(0xFFDCAE6C), 0.7, sm = true)
    el(cx, 14.4, 5.8, 1.8, hex(0xFF3A2614), 0.5, false)
    ln(pt(cx - 6.4, 21.0, cx, 22.6, cx + 6.4, 21.0), INK, 2.1, true); ln(pt(cx - 6.4, 21.0, cx, 22.6, cx + 6.4, 21.0), KIN, 1.3, true)
    ln(pt(cx + 5.4, 21.6, cx + 7.4, 26.0, cx + 6.6, 30.0), INK, 1.1, true); ln(pt(cx + 5.4, 21.6, cx + 7.4, 26.0, cx + 6.6, 30.0), KIN, 0.6, true)
    el(cx + 6.6, 31.0, 1.1, 1.6, KIN, 0.4)
    if (res != null) {
        val k = rise; val by = 17 - k * 9
        if (k >= 1) still { for (i in 0 until 8) { val a = i / 8.0 * 6.283; ln(pt(cx + cos(a) * 6, 8 + sin(a) * 5, cx + cos(a) * 9, 8 + sin(a) * 7), if (res == 'r') KIN else OCH, 0.7) } }
        beanDraw(cx, by, res, 1 + 0.7 * k)
    }
    restore()
    var x = 58.0; var y = 10.0; var n = 0
    beans.forEachIndexed { i, b -> if (i == idx) return@forEachIndexed; beanDraw(x, y, b); x += 8; if (++n % 8 == 0) { x = 58.0; y += 7 } }
    paper(0.6)
}

internal fun Ctx.thread(gen: Int, won: Boolean) {
    if (!won) { beginPath(); for (x in 1..57) if (x == 1) moveTo(1.0, 4.5) else lineTo(x.toDouble(), 4.5 + sin(x * 0.8) * 0.6); stroke(KIN, 0.9) }
    for (i in 0 until 7) {
        val x = 4 + i * 8.3
        el(x, 4.5, 2.5, 2.5, if (i < gen) OCH else hex(0xFF4A3424), 0.5, false, 12, hex(0xFFE8D6A8))
        if (i < gen) dot(x - 0.8, 3.7, 0.6, hex(0xFFFFF2C0))
    }
}

internal fun Ctx.relic(ic: String) {
    fillRect(0.0, 0.0, 16.0, 16.0, hex(0xFFECD9A8))
    when (ic) {
        "axe" -> { ln(pt(4.0, 14.5, 11.0, 3.0), INK, 1.8); ln(pt(4.0, 14.5, 11.0, 3.0), BRN, 1.0); sh(pt(9.0, 2.0, 14.0, 3.2, 14.6, 8.0, 11.4, 6.6, 9.6, 5.0), hex(0xFFC8CCD0), 0.5) }
        "sword" -> {
            ln(pt(3.4, 12.6, 13.0, 3.0), INK, 2.0); ln(pt(3.4, 12.6, 13.0, 3.0), hex(0xFFDFE4E8), 1.1)
            ln(pt(2.2, 10.0, 6.0, 13.8), INK, 1.6); ln(pt(2.2, 10.0, 6.0, 13.8), OCH, 0.8)
            ln(pt(1.6, 14.4, 3.6, 12.4), INK, 1.4); ln(pt(1.6, 14.4, 3.6, 12.4), BRN, 0.7)
        }
        "icon" -> {
            quad(2.5, 1.5, 11.0, 13.0, OCH, 0.6); quad(4.0, 3.0, 8.0, 10.0, hex(0xFF3A2414), 0.4, false)
            el(8.0, 6.0, 2.4, 2.4, YEL, 0.35, false); el(8.0, 6.2, 1.4, 1.7, hex(0xFFE8C49A), 0.3, false)
            sh(pt(5.6, 13.0, 6.4, 8.6, 9.6, 8.6, 10.4, 13.0), KIN, 0.35, false)
        }
        "medal" -> { sh(pt(6.0, 1.0, 10.0, 1.0, 9.4, 7.0, 6.6, 7.0), BLU, 0.4); quad(7.4, 1.0, 1.2, 6.0, WHT, 0.0); el(8.0, 10.5, 3.8, 3.8, OCH, 0.6); el(8.0, 10.5, 2.2, 2.2, 0, 0.35) }
        "ring" -> { beginPath(); oval(8.0, 10.4, 4.0, 3.2); stroke(INK, 2.0); stroke(OCH, 1.1); el(8.0, 6.0, 2.2, 1.8, hex(0xFF3A8AC8), 0.5) }
        "reliq" -> {
            sh(pt(2.5, 14.0, 2.5, 7.0, 8.0, 3.0, 13.5, 7.0, 13.5, 14.0), OCH, 0.6); quad(4.6, 8.4, 6.8, 4.6, KIN, 0.4, false)
            ln(pt(8.0, 1.0, 8.0, 4.0), INK, 0.6); ln(pt(6.8, 2.0, 9.2, 2.0), INK, 0.5); for (i in 0 until 3) dot(5.4 + i * 2.6, 10.6, 0.7, YEL)
        }
        "shell" -> {
            val p = Pb()
            for (i in 0..10) { val a = PI * (1 + i / 10.0); p.add(8 + cos(a) * 6, 10 + sin(a) * 7) }
            p.add(10.0, 13.6).add(6.0, 13.6); sh(p.arr(), hex(0xFFF3E2C0), 0.6)
            for (i in 1 until 6) { val a = PI * (1 + i / 6.0); ln(pt(8.0, 13.0, 8 + cos(a) * 5.6, 10 + sin(a) * 6.6), INK, 0.35) }
        }
        "lily" -> {
            quad(2.0, 2.0, 12.0, 12.0, BLU, 0.6)
            for ((x, y) in listOf(5.4 to 5.4, 10.6 to 5.4, 8.0 to 10.4)) { sh(pt(x, y - 2.4, x + 0.8, y, x, y + 1.6, x - 0.8, y), YEL, 0.3, false); dot(x - 1.3, y + 0.4, 0.6, YEL); dot(x + 1.3, y + 0.4, 0.6, YEL) }
        }
        "crossr" -> { quad(6.6, 1.5, 2.8, 13.0, KIN, 0.6); quad(2.5, 5.0, 11.0, 2.8, KIN, 0.6); dot(8.0, 6.4, 0.8, YEL) }
        else -> el(8.0, 8.0, 5.0, 5.0, OCH)
    }
    paper(0.7)
}
