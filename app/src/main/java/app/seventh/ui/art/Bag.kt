package app.seventh.ui.art

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/* the bag of beans, the red thread, relics */

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
        "katana" -> {
            ln(pt(4.6, 11.4, 9.2, 6.6, 14.2, 2.0), INK, 1.9, true); ln(pt(4.6, 11.4, 9.2, 6.6, 14.2, 2.0), hex(0xFFDFE4E8), 1.0, true)
            ln(pt(6.6, 9.8, 9.6, 6.6, 13.4, 3.0), hex(0xFFA9B2BA), 0.3, true)
            el(4.8, 11.2, 1.9, 1.9, hex(0xFF3A3530), 0.45); dot(4.8, 11.2, 0.5, OCH)
            ln(pt(1.4, 14.8, 4.0, 12.0), INK, 2.0); ln(pt(1.4, 14.8, 4.0, 12.0), hex(0xFFE8E0CC), 1.1)
            for (i in 0 until 3) dot(1.9 + i * 0.75, 14.2 - i * 0.8, 0.3, INK)
        }
        "omamori" -> {
            val B = pt(4.4, 15.0, 4.4, 5.6, 5.4, 4.0, 8.0, 3.4, 10.6, 4.0, 11.6, 5.6, 11.6, 15.0)
            sh(B, KIN2, 0.6, sm = false)
            inside(B) { for (j in 0 until 6) for (i in 0 until 4) dot(5.0 + i * 2.0 + (j % 2), 5.0 + j * 2.0, 0.35, OCH) }
            quad(6.2, 7.0, 3.6, 6.4, WHT, 0.35, false)
            ln(pt(8.0, 8.0, 8.0, 12.4), INK, 0.45); ln(pt(7.0, 9.4, 9.0, 9.2), INK, 0.4); ln(pt(7.2, 11.0, 8.8, 11.4), INK, 0.35)
            ln(pt(6.6, 3.8, 6.0, 1.6, 8.0, 2.4, 10.0, 1.6, 9.4, 3.8), OCH, 0.6, true); dot(8.0, 2.8, 0.7, OCH)
        }
        "fan" -> {
            val p = Pb()
            for (i in 0..12) { val a = -PI * (0.86 - i * 0.06); p.add(8 + cos(a) * 7.4, 13.4 + sin(a) * 11.2) }
            for (i in 12 downTo 0) { val a = -PI * (0.86 - i * 0.06); p.add(8 + cos(a) * 2.6, 13.4 + sin(a) * 3.8) }
            val F = p.arr(); sh(F, hex(0xFFF3E9D2), 0.6, sm = false)
            inside(F) { quad(0.0, 6.6, 16.0, 2.4, BLU, 0.0, false); dot(8.0, 4.4, 1.6, KIN) }
            for (i in 0..12 step 2) { val a = -PI * (0.86 - i * 0.06); ln(pt(8 + cos(a) * 2.6, 13.4 + sin(a) * 3.8, 8 + cos(a) * 7.4, 13.4 + sin(a) * 11.2), INK, 0.25) }
            for (i in 0..12 step 2) { val a = -PI * (0.86 - i * 0.06); ln(pt(8.0, 14.4, 8 + cos(a) * 2.6, 13.4 + sin(a) * 3.8), BRN2, 0.5) }
            dot(8.0, 14.2, 0.7, INK)
        }
        "inro" -> {
            ln(pt(8.0, 5.0, 8.0, 1.2), KIN, 0.5); el(8.0, 3.4, 0.9, 0.9, KIN2, 0.3); el(8.0, 1.2, 1.6, 1.0, BRN, 0.35)
            val B = pt(4.6, 5.6, 5.4, 4.8, 10.6, 4.8, 11.4, 5.6, 11.4, 13.6, 10.6, 14.6, 5.4, 14.6, 4.6, 13.6)
            sh(B, hex(0xFF1E1A1C), 0.6)
            for (yy in doubleArrayOf(7.4, 10.0, 12.4)) ln(pt(4.6, yy, 11.4, yy), OCH, 0.3)
            ln(pt(5.8, 11.6, 7.2, 9.0, 8.6, 10.4, 10.2, 8.2), OCH, 0.45, true); dot(9.4, 6.2, 0.9, KIN)
        }
        else -> el(8.0, 8.0, 5.0, 5.0, OCH)
    }
    paper(0.7)
}
