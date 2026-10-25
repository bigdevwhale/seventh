package app.seventh.ui.art

import android.graphics.Canvas

/**
 * The game's pictures, ported from the prototype: lubok prints for Rus ("rus"), an illuminated manuscript for
 * Europe ("eu"). Every call paints a whole picture in logical units scaled to the requested pixel size;
 * `t` is seconds and drives the (smooth, time-based) animation. Call from one thread (the UI thread).
 */
object Art {
    private val cx = Ctx()

    /** Draw calls made by the last picture (profiling). */
    internal val lastOps get() = cx.ops

    private inline fun draw(c: Canvas, w: Double, h: Double, px: Float, land: String, year: Int = cx.year, fn: Ctx.() -> Unit) = synchronized(cx) {
        cx.begin(c, w, h, px.toDouble()); cx.eu = land == "eu"; cx.year = year
        try { cx.fn() } finally { cx.end() }
    }

    /** Event picture, logical 176×100 scaled to wPx wide (height = wPx*100/176). t = seconds, drives animation. */
    fun scene(c: Canvas, wPx: Float, land: String, key: String, d: SceneData, t: Float) =
        draw(c, 176.0, 100.0, wPx, land, d.year) { if (eu) with(Eu) { scene(key, d, t.toDouble()) } else rusScene(key, d, t.toDouble()) }

    /** Home farmstead, logical 176×96. */
    fun home(c: Canvas, wPx: Float, land: String, d: HomeData, t: Float) =
        draw(c, 176.0, 96.0, wPx, land, d.year) { if (eu) with(Eu) { home(d, t.toDouble()) } else rusHome(d, t.toDouble()) }

    /** 24×24 bust scaled to sizePx; round = clip to circle (tree medallions). faded = sepia (dead/monk). */
    fun portrait(c: Canvas, sizePx: Float, land: String, fig: Fig, bg: PBg, faded: Boolean, round: Boolean = false) =
        draw(c, 24.0, 24.0, sizePx, land) { portrait(fig, bg, faded, 0.0, 0.0, round) }

    /** Bean bag, logical 120×56. beans = list of 'w','b','r'; idx = index of the drawn bean (-1 none); res = drawn bean or null; rise 0..1. */
    fun bag(c: Canvas, wPx: Float, land: String, beans: List<Char>, shake: Float, idx: Int, res: Char?, rise: Float) =
        draw(c, 120.0, 56.0, wPx, land) { drawBag(beans, shake.toDouble(), idx, res, rise.toDouble()) }

    /** The red thread with 7 knots in the header, logical 58×9. */
    fun thread(c: Canvas, wPx: Float, gen: Int, won: Boolean) = draw(c, 58.0, 9.0, wPx, "rus") { thread(gen, won) }

    /** Relic icon, logical 16×16. icons: axe, sword, icon, medal, ring, reliq, shell, lily, crossr. */
    fun relic(c: Canvas, sizePx: Float, icon: String) = draw(c, 16.0, 16.0, sizePx, "rus") { relic(icon) }

    /** Family tree in logical units, drawn at `scale` px per unit, names and years under the medallions included. */
    fun tree(c: Canvas, scale: Float, land: String, d: TreeData) =
        draw(c, d.w.toDouble(), d.h.toDouble(), scale * d.w, land) { tree(d) }
}
