package app.seventh.ui.art

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sin

/**
 * A small Canvas2D over android.graphics.Canvas: the prototype's drawing state (jitter, misregistration,
 * line width, ink, global alpha, composite) plus a reusable path. One instance, drawn from the UI thread.
 */
internal class Ctx {
    lateinit var cv: Canvas
    var J = 0.32
    var LW = 0.75
    var REG = true
    var INK = INK0
    var eu = false
    var year = 1700
    var alpha = 1.0
    var mode = SRC
    /** Device pixels per logical unit, set by [begin]. */
    var k = 1.0
    var W = 0.0
    var H = 0.0
    /** Draw calls since [begin], for profiling. */
    var ops = 0

    private val stA = DoubleArray(64)
    private val stM = IntArray(64)
    private var sp = 0
    private val fp = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val sp2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeJoin = Paint.Join.ROUND; strokeCap = Paint.Cap.ROUND
    }
    private val tp = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lp = Paint(Paint.ANTI_ALIAS_FLAG)
    val path = Path()
    private val rf = RectF()
    private var buf = DoubleArray(128)
    private val xfMul = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    private val xfAdd = PorterDuffXfermode(PorterDuff.Mode.ADD)
    private val xfAtop = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)

    fun begin(c: Canvas, w: Double, h: Double, px: Double) {
        cv = c; W = w; H = h; ops = 0; k = px / w; alpha = 1.0; mode = SRC; sp = 0
        J = 0.32; LW = 0.75; REG = true; INK = INK0
        sp2.pathEffect = null
        c.save(); c.scale(k.toFloat(), k.toFloat())
    }

    fun end() { cv.restore() }

    fun save() { cv.save(); stA[sp] = alpha; stM[sp] = mode; sp++ }
    fun restore() { sp--; alpha = stA[sp]; mode = stM[sp]; cv.restore() }
    inline fun fade(a: Double, fn: () -> Unit) { val o = alpha; alpha = a; try { fn() } finally { alpha = o } }

    fun translate(x: Double, y: Double) = cv.translate(x.toFloat(), y.toFloat())
    fun scale(x: Double, y: Double) = cv.scale(x.toFloat(), y.toFloat())
    fun rotate(rad: Double) = cv.rotate((rad * 180 / PI).toFloat())

    fun beginPath() { path.reset(); path.fillType = Path.FillType.WINDING }
    fun moveTo(x: Double, y: Double) = path.moveTo(x.toFloat(), y.toFloat())
    fun lineTo(x: Double, y: Double) = path.lineTo(x.toFloat(), y.toFloat())
    fun quadTo(cx: Double, cy: Double, x: Double, y: Double) = path.quadTo(cx.toFloat(), cy.toFloat(), x.toFloat(), y.toFloat())
    fun cubicTo(a: Double, b: Double, c: Double, d: Double, x: Double, y: Double) =
        path.cubicTo(a.toFloat(), b.toFloat(), c.toFloat(), d.toFloat(), x.toFloat(), y.toFloat())
    fun close() = path.close()
    fun rect(x: Double, y: Double, w: Double, h: Double) = path.addRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), Path.Direction.CW)
    fun circle(x: Double, y: Double, r: Double) = path.addCircle(x.toFloat(), y.toFloat(), r.toFloat(), Path.Direction.CW)
    fun oval(x: Double, y: Double, rx: Double, ry: Double) = path.addOval((x - rx).toFloat(), (y - ry).toFloat(), (x + rx).toFloat(), (y + ry).toFloat(), Path.Direction.CW)
    /** Canvas2D arc, clockwise from a0 to a1 (radians). */
    fun arc(x: Double, y: Double, r: Double, a0: Double, a1: Double) {
        val sweep = min(a1 - a0, 2 * PI)
        if (sweep >= 2 * PI - 1e-6) { circle(x, y, r); return }
        rf.set((x - r).toFloat(), (y - r).toFloat(), (x + r).toFloat(), (y + r).toFloat())
        path.arcTo(rf, (a0 * 180 / PI).toFloat(), (sweep * 180 / PI).toFloat(), path.isEmpty)
    }

    private fun prep(p: Paint, col: Int) {
        ops++
        p.shader = null; p.color = col
        if (alpha < 1) p.alpha = (Color.alpha(col) * alpha + 0.5).toInt()
        p.xfermode = when (mode) { MUL -> xfMul; ADD -> xfAdd; ATOP -> xfAtop; else -> null }
    }
    fun fill(col: Int) { prep(fp, col); cv.drawPath(path, fp) }
    fun stroke(col: Int, w: Double) { prep(sp2, col); sp2.strokeWidth = w.toFloat(); cv.drawPath(path, sp2) }
    fun fillRect(x: Double, y: Double, w: Double, h: Double, col: Int) {
        prep(fp, col); cv.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), fp)
    }
    fun strokeRect(x: Double, y: Double, w: Double, h: Double, col: Int, lw: Double) {
        prep(sp2, col); sp2.strokeWidth = lw.toFloat(); sp2.strokeJoin = Paint.Join.MITER
        cv.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), sp2); sp2.strokeJoin = Paint.Join.ROUND
    }
    fun fillShader(s: Shader, x: Double, y: Double, w: Double, h: Double) {
        prep(fp, -0x1000000); fp.shader = s
        cv.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), fp); fp.shader = null
    }
    fun clip(evenOdd: Boolean = false) {
        path.fillType = if (evenOdd) Path.FillType.EVEN_ODD else Path.FillType.WINDING
        cv.clipPath(path)
    }
    fun clipRect(x: Double, y: Double, w: Double, h: Double) { cv.clipRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat()) }
    fun dash(on: Double, off: Double) { sp2.pathEffect = DashPathEffect(floatArrayOf(on.toFloat(), off.toFloat()), 0f) }
    fun noDash() { sp2.pathEffect = null }
    fun dot(x: Double, y: Double, r: Double, col: Int) {
        prep(fp, col); cv.drawCircle(x.toFloat(), y.toFloat(), maxOf(0.05, r).toFloat(), fp)
    }

    fun text(s: String, x: Double, y: Double, face: Typeface, size: Double, col: Int, align: Paint.Align, halo: Int = 0) {
        tp.typeface = face; tp.textSize = size.toFloat(); tp.textAlign = align; tp.color = col
        if (halo != 0) tp.setShadowLayer((size * 0.2).toFloat(), 0f, 0f, halo) else tp.clearShadowLayer()
        cv.drawText(s, x.toFloat(), y.toFloat(), tp)
        if (halo != 0) cv.drawText(s, x.toFloat(), y.toFloat(), tp)
        tp.clearShadowLayer()
    }
    fun textMid(face: Typeface, size: Double): Double {
        tp.typeface = face; tp.textSize = size.toFloat(); val m = tp.fontMetrics; return -(m.ascent + m.descent) / 2.0
    }

    /** Offscreen layer: everything until [restore] composites with [p] (alpha / colour filter). */
    fun layer(x: Double, y: Double, w: Double, h: Double, p: Paint) {
        cv.saveLayer(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), p)
        stA[sp] = alpha; stM[sp] = mode; sp++
    }

    /** The prototype's path(): a hand-wobbled polyline, optionally smoothed through the midpoints. [ox],[oy] shift it (misregistration). */
    fun poly(pts: DoubleArray, closed: Boolean, sm: Boolean, ox: Double = 0.0, oy: Double = 0.0) {
        beginPath()
        val n = pts.size / 2
        if (buf.size < pts.size) buf = DoubleArray(pts.size * 2)
        val q = buf
        for (i in 0 until n) {
            val x = pts[2 * i]; val y = pts[2 * i + 1]
            if (J != 0.0) { q[2 * i] = x + (hsh(x, y) - 0.5) * J + ox; q[2 * i + 1] = y + (hsh(y + 0.37, x) - 0.5) * J + oy }
            else { q[2 * i] = x + ox; q[2 * i + 1] = y + oy }
        }
        if (!sm || n < 3) {
            moveTo(q[0], q[1]); for (i in 1 until n) lineTo(q[2 * i], q[2 * i + 1]); if (closed) close(); return
        }
        if (closed) {
            moveTo((q[2 * n - 2] + q[0]) / 2, (q[2 * n - 1] + q[1]) / 2)
            for (i in 0 until n) {
                val j = (i + 1) % n
                quadTo(q[2 * i], q[2 * i + 1], (q[2 * i] + q[2 * j]) / 2, (q[2 * i + 1] + q[2 * j + 1]) / 2)
            }
            close()
        } else {
            moveTo(q[0], q[1])
            for (i in 1 until n - 1) quadTo(q[2 * i], q[2 * i + 1], (q[2 * i] + q[2 * i + 2]) / 2, (q[2 * i + 1] + q[2 * i + 3]) / 2)
            lineTo(q[2 * n - 2], q[2 * n - 1])
        }
    }

    // paper: a seeded 256px texture multiplied over everything, tiled in device pixels like the canvas pattern
    private val pm = Matrix()
    private val paperP = Paint(Paint.ANTI_ALIAS_FLAG).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY) }
    private val paperF = HashMap<Int, ColorMatrixColorFilter>()
    fun paper(a: Double = 0.9, x: Double = 0.0, y: Double = 0.0, w: Double = W, h: Double = H) {
        val sh = Paper.shader
        pm.setScale((1 / k).toFloat(), (1 / k).toFloat()); sh.setLocalMatrix(pm)
        val key = (a * 1000).toInt()
        // multiply at alpha a == multiply by the texture lerped toward white
        paperP.colorFilter = paperF.getOrPut(key) {
            val s = a.toFloat(); val o = (1 - s) * 255
            ColorMatrixColorFilter(ColorMatrix(floatArrayOf(s, 0f, 0f, 0f, o, 0f, s, 0f, 0f, o, 0f, 0f, s, 0f, o, 0f, 0f, 0f, 0f, 255f)))
        }
        paperP.shader = sh
        cv.drawRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat(), paperP)
    }

    // static, jitter-free patterns (gold leaf, diaper) are painted once per size into a bitmap and blitted after
    private val cache = object : LinkedHashMap<String, Bitmap>(16, 0.75f, true) {
        override fun removeEldestEntry(e: MutableMap.MutableEntry<String, Bitmap>) = size > 12
    }
    private val blitP = Paint(Paint.FILTER_BITMAP_FLAG)
    private val blitR = RectF()
    inline fun cached(key: String, x: Double, y: Double, w: Double, h: Double, fn: () -> Unit) {
        val bmp = cachedGet(key, w, h) ?: run {
            val b = cachedNew(key, x, y, w, h); val old = cv; val oa = alpha; val om = mode
            cv = Canvas(b); cv.scale(k.toFloat(), k.toFloat()); cv.translate(-x.toFloat(), -y.toFloat()); alpha = 1.0; mode = SRC
            try { fn() } finally { cv = old; alpha = oa; mode = om }
            b
        }
        blit(bmp, x, y, w, h)
    }
    fun cachedGet(key: String, w: Double, h: Double): Bitmap? = cache["$key|${(w * k).toInt()}x${(h * k).toInt()}"]
    fun cachedNew(key: String, x: Double, y: Double, w: Double, h: Double): Bitmap {
        val b = Bitmap.createBitmap(maxOf(1, kotlin.math.ceil(w * k).toInt()), maxOf(1, kotlin.math.ceil(h * k).toInt()), Bitmap.Config.ARGB_8888)
        cache["$key|${(w * k).toInt()}x${(h * k).toInt()}"] = b; return b
    }
    fun blit(b: Bitmap, x: Double, y: Double, w: Double, h: Double) {
        blitP.alpha = (alpha * 255 + 0.5).toInt()
        blitR.set(x.toFloat(), y.toFloat(), (x + b.width / k).toFloat(), (y + b.height / k).toFloat())
        cv.save(); cv.clipRect(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat()); cv.drawBitmap(b, null, blitR, blitP); cv.restore()
    }

    companion object {
        const val SRC = 0; const val MUL = 1; const val ADD = 2; const val ATOP = 3
        val INK0 = 0xFF1F1712.toInt()
        fun hsh(a: Double, b: Double): Double { val s = sin(a * 127.1 + b * 311.7) * 43758.5453; return s - floor(s) }
    }
}

internal object Paper {
    val bitmap: Bitmap by lazy {
        val n = 256; val r = Rng(9); val px = IntArray(n * n)
        fun cl(v: Double) = minOf(255, maxOf(0, round(v).toInt()))
        for (i in px.indices) {
            val v = 238 + r.next() * 17 - (if (r.next() < 0.03) 26 else 0)
            px[i] = Color.argb(255, cl(v), cl(v - 5), cl(v - 16))
        }
        val b = Bitmap.createBitmap(n, n, Bitmap.Config.ARGB_8888)
        b.setPixels(px, 0, n, 0, 0, n, n)
        val c = Canvas(b)
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 0.7f; color = Color.argb(41, 110, 80, 40) }
        val path = Path()
        repeat(220) {
            val x = r.next() * n; val y = r.next() * n; val a = r.next() * 6.28; val l = 4 + r.next() * 14
            path.reset(); path.moveTo(x.toFloat(), y.toFloat())
            path.quadTo((x + kotlin.math.cos(a + 0.5) * l * 0.5).toFloat(), (y + sin(a + 0.5) * l * 0.5).toFloat(),
                (x + kotlin.math.cos(a) * l).toFloat(), (y + sin(a) * l).toFloat())
            c.drawPath(path, p)
        }
        b
    }
    val shader: BitmapShader by lazy { BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT) }
}

/** mulberry32, bit for bit as in the prototype. */
internal class Rng(seed: Int) {
    private var a = seed
    fun next(): Double {
        a += 0x6D2B79F5
        var t = (a xor (a ushr 15)) * (1 or a)
        t = (t + (t xor (t ushr 7)) * (61 or t)) xor t
        return ((t xor (t ushr 14)).toLong() and 0xFFFFFFFFL) / 4294967296.0
    }
}

internal fun pt(vararg v: Double) = v
