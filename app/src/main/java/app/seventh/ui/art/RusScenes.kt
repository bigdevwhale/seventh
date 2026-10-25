package app.seventh.ui.art

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin

internal val SEASONS = arrayOf("spring", "summer", "autumn", "winter")

/* interior */
internal fun Ctx.interior(W: Double, Hh: Double, f: Double, tavern: Boolean = false, night: Boolean = false, notable: Boolean = false, samovar: Boolean = false) {
    fillRect(0.0, 0.0, W, 80.0, hex(0xFFC48E56))
    var y = 6.0
    while (y < 80) { fillRect(0.0, y - 2.6, W, 2.6, rgba(120, 70, 30, 0.22)); ln(pt(0.0, y, W * 0.5, y + 0.3, W, y), INK, 0.4); y += 7 }
    val r = Rng(5)
    repeat(14) { val x = r.next() * W; el(x, floor(r.next() * 11) * 7 + 3.4, 1.4, 1.0, hex(0xFF9A6A3A), 0.3, false, 8) }
    win(if (tavern) 70.0 else 82.0, 26.0, 16.0, 18.0, glass = if (night) hex(0xFF27355A) else hex(0xFFB9D3DC))
    if (!tavern) {
        quad(6.0, 28.0, 26.0, 2.2, BRN, 0.45)
        quad(11.0, 8.0, 16.0, 20.0, OCH, 0.6); quad(13.0, 10.0, 12.0, 16.0, hex(0xFF3A2414), 0.4, false)
        el(19.0, 14.2, 3.4, 3.4, YEL, 0.4, false); el(19.0, 14.6, 2.0, 2.4, hex(0xFFE8C49A), 0.35, false); dot(18.3, 14.4, 0.35, INK); dot(19.7, 14.4, 0.35, INK)
        sh(pt(15.0, 26.0, 16.4, 18.6, 21.6, 18.6, 23.0, 26.0), KIN, 0.4, false)
        sh(pt(4.0, 5.0, 34.0, 5.0, 34.0, 7.0, 31.6, 7.0, 31.6, 30.0, 29.8, 30.0, 29.8, 7.0, 8.2, 7.0, 8.2, 30.0, 6.4, 30.0, 6.4, 7.0, 4.0, 7.0), WHT, 0.4)
        for (tx in doubleArrayOf(7.3, 30.7)) for (j in 0 until 4) dot(tx, 24.0 + j * 1.6, 0.45, KIN)
        for (i in 0 until 9) dot(6.0 + i * 3.4, 6.0, 0.4, KIN)
        ln(pt(19.0, 30.0, 19.0, 34.0), INK, 0.3); el(19.0, 35.6, 1.6, 1.8, KIN, 0.4); dot(19.0, 33.8, 0.6, YEL)
        val sx0 = 122.0; quad(sx0, 24.0, W - sx0 + 2, 57.0, WHT); quad(sx0 - 2, 21.6, W - sx0 + 4, 2.6, WHT, 0.5)
        for ((bx, by) in listOf(sx0 + 8 to 32.0, sx0 + 30 to 36.0, sx0 + 44 to 30.0, sx0 + 14 to 46.0, sx0 + 40 to 48.0)) {
            for (k in 0 until 5) { val an = k / 5.0 * 6.28; dot(bx + kotlin.math.cos(an) * 1.2, by + sin(an) * 1.2, 0.75, BLU) }
            dot(bx, by, 0.5, YEL)
        }
        sh(pt(sx0 + 4, 74.0, sx0 + 4, 62.0, sx0 + 12, 56.0, sx0 + 20, 62.0, sx0 + 20, 74.0), hex(0xFF2A1D14), 0.6)
        quad(sx0 + 2, 74.0, 20.0, 2.0, hex(0xFFB0A898), 0.45)
        for (nx in doubleArrayOf(sx0 + 30, sx0 + 42)) sh(pt(nx, 44.0, nx - 1, 41.0, nx + 6, 41.0, nx + 5, 44.0, nx + 2.5, 45.4), hex(0xFF3A3028), 0.4)
    } else {
        for ((bx, by) in listOf(14.0 to 79.0, 26.0 to 79.0, 20.0 to 68.0, 150.0 to 79.0, 162.0 to 79.0)) barrel(bx, by)
        quad(100.0, 55.0, 76.0, 2.6, BRN, 0.45)
        var x = 104
        while (x < 170) {
            val xd = x.toDouble()
            sh(pt(xd - 1.4, 55.0, xd - 1.8, 50.0, xd - 0.6, 48.6, xd - 0.6, 46.6, xd + 0.6, 46.6, xd + 0.6, 48.6, xd + 1.8, 50.0, xd + 1.4, 55.0), if (x % 2 == 1) hex(0xFF4F7A4A) else hex(0xFF6A8A7A), 0.4)
            x += 7
        }
    }
    quad(-1.0, 63.0, W + 2, 2.6, hex(0xFF9A6438), 0.5)
    var x = 8.0; while (x < W) { quad(x, 65.6, 2.0, 14.0, BRN2, 0.35); x += 40 }
    fillRect(0.0, 80.0, W, Hh - 80, hex(0xFF8A5A34)); ln(pt(0.0, 80.0, W, 80.0), INK, 0.6)
    fade(0.5) {
        var yy = 85.0; while (yy < Hh) { ln(pt(0.0, yy, W, yy), INK, 0.35); yy += 5 }
        for (i in 0 until 12) { val y2 = 80.0 + ((i * 3) % 4) * 5; val x2 = ((i * 37) % W.toInt()).toDouble(); ln(pt(x2, y2, x2, y2 + 5), INK, 0.35) }
    }
    if (!notable) {
        val tx = 44.0; val ty = 70.0
        quad(tx + 3, ty + 8, 2.0, 11.0, BRN2, 0.35); quad(tx + 47, ty + 8, 2.0, 11.0, BRN2, 0.35)
        sh(pt(tx, ty, tx + 52, ty, tx + 53, ty + 9, tx - 1, ty + 9), WHT, 0.5)
        for (i in 0 until 18) { val xx = tx + 1 + i * 2.9; sh(pt(xx, ty + 7, xx + 1.4, ty + 5.6, xx + 2.8, ty + 7), KIN, 0.0, false) }
        ln(pt(tx, ty + 2.6, tx + 52, ty + 2.6), INK, 0.3)
        if (samovar && year > 1780) samovar(tx + 40, ty)
        el(tx + 12, ty - 1.6, 4.2, 2.2, hex(0xFFC8843E), 0.5)
        fade(0.6) { ln(pt(tx + 10, ty - 3, tx + 11, ty - 1.4), INK, 0.3); ln(pt(tx + 13, ty - 3.2, tx + 14, ty - 1.6), INK, 0.3) }
    }
    if (!tavern) stoveFire(f)
}
internal fun Ctx.stoveFire(f: Double) = flames(134.0, 73.6, 4.4, 8.0, f)

/* scenes */
internal fun Ctx.rusScene(key: String, d: SceneData, f: Double) {
    val W = 176.0; val Hh = 100.0
    val sp = d.spouse; val xp = d.other; val hl = d.hero
    val HZ = 62.0; val BLUEH = hex(0xFF8FB0B8); val GRASS = hex(0xFF86AD58)
    when (key) {
        "forest" -> {
            sky(W, HZ + 4, "day", f); hill(W, HZ, BLUEH, 1, 8.0, 0.03); treeline(W, HZ + 3, hex(0xFF4D7356), 3); ground(W, Hh, HZ + 3, GRASS, 4, tufts = true, flowers = intArrayOf(WHT, YEL))
            for ((a, b, h2) in listOf(Triple(130.0, 72.0, 28.0), Triple(30.0, 74.0, 30.0), Triple(10.0, 80.0, 40.0), Triple(150.0, 78.0, 44.0), Triple(166.0, 82.0, 36.0))) pine(a, b, h2)
            birch(52.0, 76.0, 34.0, "summer"); bush(90.0, 74.0, 6.0)
            bear(118.0, 95.0, f); figure(hl, 62.0, 98.0)
            ln(pt(70.0, 60.0, 101.0, 73.0), INK, 1.4); ln(pt(70.0, 60.0, 98.0, 71.8), BRN, 0.7); sh(pt(98.0, 71.0, 104.0, 74.4, 99.0, 73.6), hex(0xFFCFD4D8), 0.4)
        }
        "fire" -> {
            sky(W, HZ + 6, "night", f); hill(W, HZ + 4, hex(0xFF6A7AA0), 2, 6.0, 0.04); ground(W, Hh, HZ + 6, hex(0xFF5A7A4A), 8, tufts = true)
            izba(104.0, 90.0, 60.0, f, lit = true); figure(hl, 40.0, 98.0); figure(sp, 54.0, 98.0)
            nightWash(W, Hh, 0.3, pt(104.0, 58.0, 72.0, 1.2, 0.0, 104.0, 84.0, 34.0, 0.5, 0.0)); stars(W, HZ, f)
            for (k in 0 until 6) flames(80.0 + k * 9, 66 - abs(k - 2.5) * 5, 4.0, 11.0 + (k % 2) * 4, f + k)
            smoke(100.0, 38.0, f, 6, true)
        }
        "fair" -> {
            sky(W, HZ, "day", f); hill(W, HZ - 2, hex(0xFF9AB8B4), 5, 6.0, 0.025); church(150.0, HZ, 0.45); ground(W, Hh, HZ, hex(0xFFC9A46A), 6)
            booth(6.0, 84.0, 30.0, KIN); booth(64.0, 80.0, 30.0, BLU); booth(124.0, 84.0, 30.0, GRN)
            figure(npc("woman", 3), 46.0, 92.0); figure(npc("man", 4), 112.0, 94.0, true); figure(npc("kid", 5), 100.0, 96.0); figure(hl, 78.0, 99.0)
        }
        "road" -> {
            sky(W, HZ, "dusk", f); hill(W, HZ, hex(0xFF8A6A86), 3, 9.0, 0.03); treeline(W, HZ + 2, hex(0xFF5A4A62), 6); ground(W, Hh, HZ + 1, hex(0xFF7A9452), 9, tufts = true)
            sh(pt(93.0, HZ + 1.5, 99.0, HZ + 1.5, 116.0, Hh + 2, 36.0, Hh + 2), hex(0xFFCFAB70), 0.55)
            fade(0.4) { ln(pt(95.0, HZ + 3, 64.0, Hh), INK, 0.4); ln(pt(97.0, HZ + 3, 92.0, Hh), INK, 0.4) }
            izba(24.0, 72.0, 34.0, f, lit = true); verst(150.0, 90.0); figure(npc("beggar", 7), 118.0, 94.0, true); figure(hl, 72.0, 98.0)
        }
        "field", "field_dry", "klad", "meja" -> {
            val dry = key == "field_dry"
            sky(W, HZ - 6, if (dry) "gray" else "day", f); hill(W, HZ - 6, if (dry) hex(0xFFA8A690) else BLUEH, 4, 7.0, 0.03)
            treeline(W, HZ - 3, if (dry) hex(0xFF7A7258) else hex(0xFF4D7356), 7)
            field(W, Hh, HZ - 3, dry, f); if (!dry) { sheaf(150.0, 86.0); sheaf(160.0, 90.0); sheaf(140.0, 92.0) }
            if (key == "klad") pot(118.0, 94.0, f)
            if (key == "meja") {
                ln(pt(90.0, 63.0, 164.0, 80.0), hex(0xFFC8B890), 0.7, true)
                for (k in 0 until 5) { val sx2 = 90.0 + k * 18; val sy = 72.0 + k * 5; quad(sx2 - 0.7, sy - 12, 1.4, 12.0, BRN, 0.35) }
            }
            if (dry) for (k in 0 until 3) crow(40.0 + k * 30 + sin(f * 0.5 + k) * 6, 20.0 + k * 6, f + k)
            figure(hl, 60.0, 96.0); figure(xp, 78.0, 96.0)
        }
        "church" -> {
            sky(W, HZ, "day", f); hill(W, HZ - 2, BLUEH, 6, 6.0, 0.03); ground(W, Hh, HZ, GRASS, 12, tufts = true); church(96.0, 84.0, 1.1)
            birch(22.0, 84.0, 40.0, "summer"); birch(160.0, 86.0, 36.0, "summer")
            figure(hl, 52.0, 98.0); figure(npc("priest", 9), 132.0, 98.0, true)
        }
        "interior" -> { interior(W, Hh, f, samovar = true); figure(hl, 34.0, 98.0); if (xp != null) figure(xp, 108.0, 98.0, true) else figure(sp, 108.0, 98.0, true) }
        "wedding" -> {
            interior(W, Hh, f, samovar = true); candle(52.0, 70.0, f); candle(86.0, 70.0, f)
            still { for (i in 0 until 6) { val ph = (f * 0.15 + i / 6.0) % 1; fade(1 - ph) { dot(40.0 + i * 14 + sin(ph * 6 + i) * 3, 10 + ph * 50, 0.9, if (i % 2 == 1) KIN else YEL) } } }
            figure(hl, 34.0, 98.0); figure(npc("bride", 13), 108.0, 98.0, true)
        }
        "night_int" -> {
            interior(W, Hh, f, night = true)
            ln(pt(94.0, 0.0, 95.0, 57.0), INK, 0.35); ln(pt(104.0, 0.0, 103.0, 57.0), INK, 0.35)
            sh(pt(92.0, 56.0, 106.0, 56.0, 105.0, 61.0, 93.0, 61.0), hex(0xFFC8A070), 0.5); quad(92.0, 55.0, 14.0, 1.4, WHT, 0.35)
            figure(hl, 34.0, 98.0); if (xp != null && xp.age < 14) figure(xp, 99.0, 74.0)
            nightWash(W, Hh, 0.36, pt(67.0, 61.0, 56.0, 0.95, 0.0, 134.0, 68.0, 34.0, 0.6, 0.0, 19.0, 36.0, 14.0, 0.5, 0.0, 82.0, 26.0, 18.0, 0.2, 1.0))
            candle(66.0, 70.0, f); stoveFire(f)
        }
        "tavern" -> {
            interior(W, Hh, f, tavern = true, night = true, notable = true)
            quad(60.0, 72.0, 40.0, 3.0, BRN, 0.5); quad(62.0, 75.0, 2.0, 10.0, BRN2, 0.35); quad(96.0, 75.0, 2.0, 10.0, BRN2, 0.35)
            sh(pt(69.0, 72.0, 68.4, 68.0, 69.4, 66.4, 69.4, 64.6, 70.6, 64.6, 70.6, 66.4, 71.6, 68.0, 71.0, 72.0), hex(0xFF4F7A4A), 0.4)
            figure(hl, 48.0, 98.0); figure(npc("man", 21), 114.0, 98.0, true); figure(npc("man", 22), 138.0, 98.0, true)
            nightWash(W, Hh, 0.4, pt(83.0, 63.0, 62.0, 1.0, 0.0, 130.0, 40.0, 44.0, 0.35, 0.0, 30.0, 60.0, 30.0, 0.25, 0.0)); candle(82.0, 72.0, f)
        }
        "winter" -> {
            sky(W, HZ, "night", f); hill(W, HZ, hex(0xFFA6B2D0), 1, 8.0, 0.03); ground(W, Hh, HZ, hex(0xFFF2F0EA), 2, snow = true)
            for ((a, b, h2) in listOf(Triple(120.0, 70.0, 24.0), Triple(136.0, 74.0, 36.0), Triple(168.0, 76.0, 32.0), Triple(152.0, 80.0, 42.0))) pine(a, b, h2, true)
            izba(32.0, 80.0, 50.0, f, lit = true, snow = true, chimney = true); fence(58.0, 100.0, 86.0, true)
            wolf(122.0, 92.0, f + 1, true); wolf(96.0, 96.0, f, false); figure(hl, 62.0, 100.0)
            nightWash(W, Hh, 0.3, pt(26.0, 68.0, 30.0, 0.9, 0.0, 38.0, 68.0, 30.0, 0.9, 0.0, 146.0, 16.0, 120.0, 0.35, 1.0)); stars(W, HZ, f)
            wolf(122.0, 92.0, f + 1, true, true); wolf(96.0, 96.0, f, false, true); snowfall(W, Hh, f, 40)
        }
        "war" -> {
            sky(W, HZ, "dusk", f); hill(W, HZ, hex(0xFF9A6A7A), 8, 7.0, 0.03); ground(W, Hh, HZ, hex(0xFF8A8A5C), 5, tufts = true)
            smoke(30.0, 50.0, f, 6, true); smoke(150.0, 46.0, f + 3, 6, true); waveFlag(80.0, 52.0, f)
            for (i in 0 until 6) figure(npc("soldier", 30 + i), 88.0 + i * 14, 92.0 + (i % 2) * 2, true)
            figure(hl, 36.0, 98.0); figure(xp, 54.0, 98.0)
        }
        "plague" -> {
            sky(W, HZ, "gray", f); hill(W, HZ, hex(0xFF9A9C96), 4, 6.0, 0.03); ground(W, Hh, HZ, hex(0xFF7F8062), 7, tufts = true)
            izba(30.0, 80.0, 46.0, f, dark = true, smoky = true); bareTree(156.0, 84.0, 40.0); bareTree(64.0, 78.0, 26.0)
            for ((a, b) in listOf(96.0 to 86.0, 128.0 to 86.0, 112.0 to 90.0, 144.0 to 92.0, 104.0 to 96.0)) cross(a, b)
            for (k in 0 until 4) crow(30.0 + k * 36 + sin(f * 0.6 + k) * 5, 14.0 + k * 5 + sin(f * 0.9 + k) * 1.5, f + k)
            figure(hl, 70.0, 98.0)
        }
        "barin" -> {
            sky(W, HZ, "day", f); hill(W, HZ - 2, BLUEH, 9, 6.0, 0.03); ground(W, Hh, HZ, GRASS, 15, tufts = true); manor(116.0, 84.0, 92.0)
            pine(8.0, 84.0, 40.0); pine(166.0, 86.0, 36.0); figure(npc("barin", 41), 116.0, 96.0, true); figure(hl, 52.0, 98.0)
        }
        "dream" -> {
            interior(W, Hh, f, night = true); figure(hl, 40.0, 98.0)
            nightWash(W, Hh, 0.28, pt(112.0, 70.0, 46.0, 0.75, 1.0, 134.0, 68.0, 26.0, 0.45, 0.0, 40.0, 80.0, 30.0, 0.25, 1.0)); stoveFire(f)
            save(); translate(0.0, sin(f * 1.2)); figure(npc("man", 51), 112.0, 94.0, true, ghost = true); restore()
        }
        "dawn" -> {
            sky(W, HZ, "dawn", f); hill(W, HZ, hex(0xFF8A7AA6), 7, 8.0, 0.03); ground(W, Hh, HZ, GRASS, 9, tufts = true, flowers = intArrayOf(YEL, WHT))
            izba(36.0, 84.0, 54.0, f, chimney = true, lit = true); birch(150.0, 88.0, 40.0, "spring")
            var xx = 84.0; for (p in d.family.take(5)) { figure(p, xx, 99.0); xx += if (p.age < 14) 11 else 15 }
            for (k in 0 until 3) crow(100.0 + k * 14 + sin(f * 0.5 + k) * 4, 24.0 + k * 4, f + k)
        }
        else -> { // village, and anything unknown
            sky(W, HZ, "day", f); hill(W, HZ - 2, BLUEH, 2, 7.0, 0.03); church(150.0, HZ + 2, 0.55); ground(W, Hh, HZ, GRASS, 3, tufts = true, flowers = intArrayOf(YEL, WHT))
            izba(96.0, 76.0, 40.0, f, chimney = true, shutter = BLU); izba(26.0, 80.0, 44.0, f, chimney = true); birch(64.0, 80.0, 30.0, "summer")
            fence(0.0, 60.0, 88.0); well(140.0, 88.0); chicken(120.0, 96.0, f)
            figure(hl, 84.0, 99.0); figure(xp, 100.0, 99.0)
        }
    }
    paper(); frame(W, Hh)
}

internal fun Ctx.rusHome(d: HomeData, f: Double) {
    val W = 176.0; val Hh = 96.0; val s = if (d.over) "winter" else SEASONS[d.season.mod(4)]; val nightM = d.over; val snow = s == "winter"; val HZ = 58.0
    sky(W, HZ, if (nightM) "night" else if (snow) "gray" else "day", f)
    hill(W, HZ, if (snow) hex(0xFFC4CCD8) else if (s == "autumn") hex(0xFFB8A070) else hex(0xFF8FB0B8), d.seed, 8.0, 0.03)
    treeline(W, HZ + 2, if (snow) hex(0xFF7A8498) else if (s == "autumn") hex(0xFFA8743A) else hex(0xFF4D7356), 9)
    ground(W, Hh, HZ + 1, when { snow -> hex(0xFFF2F0EA); s == "autumn" -> hex(0xFFC9A85A); s == "spring" -> LGR; else -> hex(0xFF86AD58) }, 21,
        tufts = !snow, snow = snow, flowers = if (s == "summer") intArrayOf(YEL, WHT, KIN) else if (s == "spring") intArrayOf(WHT, YEL) else null)
    birch(164.0, 86.0, 40.0, s); if (!snow) bush(6.0, 84.0, 7.0, if (s == "autumn") OCH else GRN)
    if (d.dvor >= 2) barn(112.0, 80.0, 24.0, snow)
    if (d.dvor >= 4) manor(54.0, 82.0, 80.0, nightM)
    else izba(48.0, 82.0, if (d.dvor == 0) 44.0 else 54.0, f, chimney = d.dvor >= 1, smoky = d.dvor == 0, dark = d.dvor == 0, plank = d.dvor >= 2, snow = snow, lit = nightM || snow)
    if (d.dvor >= 3) { fence(0.0, 30.0, 90.0, snow); well(136.0, 84.0) }
    if (!d.over) {
        var xx = 86.0; for (p in d.family.take(7)) { figure(p, xx, 95.0); xx += if (p.age < 14) 10 else 13 }
        if (s == "summer" || s == "spring") { chicken(24.0, 94.0, f); chicken(34.0, 92.0, f + 2, true) }
    }
    if (nightM) { nightWash(W, Hh, 0.35, pt(48.0, 66.0, 26.0, 0.6, 0.0)); stars(W, HZ, f) }
    if (snow) snowfall(W, Hh, f, 26)
    paper(); frame(W, Hh)
}
