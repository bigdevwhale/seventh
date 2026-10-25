package app.seventh

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import app.seventh.domain.Looks
import app.seventh.ui.art.Art
import app.seventh.ui.art.Fig
import app.seventh.ui.art.HomeData
import app.seventh.ui.art.PBg
import app.seventh.ui.art.Ring
import app.seventh.ui.art.SceneData
import app.seventh.ui.art.TreeData
import app.seventh.ui.art.TreeEdge
import app.seventh.ui.art.TreeNode
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

/** Contact sheets of every picture, for a look (`-Prender`); writes PNGs to app/build/art. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class RenderArt {
    private val out = File(System.getProperty("render.out") ?: "build/art").apply { mkdirs() }
    private val rusKeys = listOf("forest", "fire", "fair", "road", "field", "field_dry", "klad", "meja", "village", "church", "interior", "wedding",
        "night_int", "tavern", "winter", "war", "plague", "barin", "dream", "dawn", "nonsense")
    private val euKeys = rusKeys.dropLast(1) + listOf("pyre", "nonsense")
    private val edoKeys = rusKeys.dropLast(1) + listOf("inari", "nonsense")
    private val lands = listOf("rus", "eu", "edo")

    private fun person(sex: Char, age: Int, seed: Int, kind: String = "") = Fig(
        sex = sex, age = age, skin = Looks.SKIN[seed % 3], hair = Looks.hairAt(Looks.HAIR[seed % 6], age),
        cloth = (if (sex == 'm') Looks.CLOTH_M else Looks.CLOTH_F)[seed % 5], kerch = Looks.KERCH[seed % 5], seed = seed, kind = kind,
    )
    private val hero = person('m', 34, 7)
    private val wife = person('f', 30, 12)
    private val kid = person('m', 10, 21)
    private val family = listOf(hero, wife, kid, person('f', 6, 33), person('m', 3, 40))

    private fun save(name: String, b: Bitmap) = FileOutputStream(File(out, name)).use { b.compress(Bitmap.CompressFormat.PNG, 100, it) }
    private fun sheet(cols: Int, cw: Int, ch: Int, n: Int, draw: (Canvas, Int) -> Unit): Bitmap {
        val rows = (n + cols - 1) / cols; val gap = 6
        val b = Bitmap.createBitmap(cols * (cw + gap), rows * (ch + gap), Bitmap.Config.ARGB_8888)
        val c = Canvas(b); c.drawColor(Color.rgb(34, 34, 34))
        for (i in 0 until n) { c.save(); c.translate(((i % cols) * (cw + gap)).toFloat(), ((i / cols) * (ch + gap)).toFloat()); c.clipRect(0, 0, cw, ch); draw(c, i); c.restore() }
        return b
    }
    private fun font(id: Int): Typeface = ResourcesCompat.getFont(RuntimeEnvironment.getApplication(), id)!!

    @Test fun scenes() {
        for ((land, keys) in listOf("rus" to rusKeys, "eu" to euKeys, "edo" to edoKeys)) {
            save("scenes_$land.png", sheet(3, 528, 300, keys.size) { c, i ->
                Art.scene(c, 528f, land, keys[i], SceneData(hero = hero, spouse = wife, other = kid, family = family, year = 1702), 1f)
            })
        }
    }

    @Test fun portraits() {
        val ages = listOf(3, 9, 12, 17, 25, 40, 60, 75)
        for (land in lands) {
            val cells = mutableListOf<(Canvas) -> Unit>()
            for (bg in PBg.values()) for (sex in listOf('m', 'f')) for (a in ages) cells += { c -> Art.portrait(c, 120f, land, person(sex, a, a + (if (sex == 'f') 100 else 0)), bg, false) }
            for (sex in listOf('m', 'f')) for (a in ages) cells += { c -> Art.portrait(c, 120f, land, person(sex, a, a + 3), PBg.BLOOD, a % 2 == 1 || a > 50, round = a < 20) }
            save("portraits_$land.png", sheet(8, 120, 120, cells.size) { c, i -> cells[i](c) })
        }
    }

    @Test fun bagThreadRelics() {
        val beans = "wwwwwwwbbbbbr".toList()
        save("bag.png", sheet(3, 480, 224, 6) { c, i ->
            Art.bag(c, 480f, lands[i % 3], beans, if (i < 3) 1.5f else 0f, 3, if (i == 5) 'r' else 'w', if (i < 3) 0.5f else 1f)
        })
        save("thread.png", sheet(1, 348, 54, 3) { c, i -> Art.thread(c, 348f, i * 3 + 1, i == 2) })
        val icons = listOf("axe", "sword", "icon", "medal", "ring", "reliq", "shell", "lily", "crossr", "katana", "omamori", "fan", "inro", "x")
        save("relics.png", sheet(14, 96, 96, icons.size) { c, i -> Art.relic(c, 96f, icons[i]) })
    }

    @Test fun homes() {
        for (land in lands) {
            save("home_$land.png", sheet(5, 352, 192, 25) { c, i ->
                val dvor = i / 5; val s = i % 5
                Art.home(c, 352f, land, HomeData(dvor = dvor, season = s % 4, over = s == 4, family = family, seed = dvor + 1, year = 1700 + s), 2f)
            })
        }
    }

    @Test fun trees() {
        val SW = 46f; val RH = 62f; val rows = 7; val W = 254f; val H = rows * RH + 80
        fun X(slot: Float) = 48 + slot * SW
        fun Y(d: Int) = H - 54 - d * RH
        fun node(id: Int, slot: Float, d: Int, fig: Fig, ring: Ring, alive: Boolean, live: Boolean, bg: PBg = PBg.BLOOD, spouse: Boolean? = null, bast: Boolean = false, name: String, years: String) =
            TreeNode(id, X(slot), Y(d), fig, bg, !alive, ring, live, alive, spouse, bast, ring == Ring.HEAD || ring == Ring.WAS, name, years)
        val nodes = listOf(
            node(1, 2f, 0, person('m', 70, 1), Ring.WAS, false, true, PBg.HEAD, false, name = "Гаврила", years = "1670–1731"),
            node(2, 1f, 1, person('m', 61, 2), Ring.WAS, false, true, PBg.HEAD, true, name = "Ерёмей", years = "1701–1762"),
            node(3, 3f, 1, person('f', 4, 3), Ring.DEAD, false, false, name = "Ирина", years = "1703–1707"),
            node(4, 0f, 2, person('m', 40, 4), Ring.HEAD, true, true, PBg.HEAD, true, name = "Ефим", years = "1722–"),
            node(5, 1f, 2, person('f', 36, 5), Ring.ALIVE, true, true, bast = true, name = "Аксинья", years = "1726–"),
            node(6, 2f, 2, person('m', 30, 6), Ring.DEAD, false, false, name = "Архип", years = "1732–1739"),
            node(7, 0f, 3, person('f', 12, 7), Ring.ALIVE, true, true, name = "Прасковья", years = "1750–"),
        )
        val edges = listOf(TreeEdge(1, 2, true), TreeEdge(1, 3, false), TreeEdge(2, 4, true), TreeEdge(2, 5, false), TreeEdge(2, 6, false), TreeEdge(4, 7, false))
        for (land in lands) {
            val d = TreeData(W, H, rows, (0 until rows).map { Y(it) }, 1, nodes, edges, won = false, autumn = land == "eu",
                numeralFace = font(if (land == "rus") R.font.ruslan_display else R.font.alegreya_sc_extrabold),
                labelFace = font(if (land == "rus") R.font.kurale else R.font.alegreya))
            val b = Bitmap.createBitmap((W * 2).toInt(), (H * 2).toInt(), Bitmap.Config.ARGB_8888)
            Art.tree(Canvas(b), 2f, land, d)
            save("tree_$land.png", b)
        }
    }

    @Test fun smokeAndBagAfter() {
        val fam = listOf(hero, wife, kid)
        val shots = listOf("rus" to "village", "rus" to "fire", "rus" to "war", "rus" to "home0", "eu" to "village", "eu" to "pyre", "edo" to "village", "edo" to "fire", "edo" to "plague")
        val ts = listOf(1f, 2.5f, 4f)
        save("smoke_after.png", sheet(3, 352, 200, shots.size * 3) { c, i ->
            val (land, key) = shots[i / 3]; val t = ts[i % 3]
            if (key == "home0") Art.home(c, 352f, land, HomeData(0, 1, false, fam, 1, 1700), t)
            else Art.scene(c, 352f, land, key, SceneData(hero, other = kid, family = fam, year = 1702), t)
        })
        val beans = "wwwwwwwbbbbbr".toList()
        val ps = listOf(0f, 0.12f, 0.3f, 0.45f, 0.6f, 0.75f, 0.9f, 1f)
        save("bag_after.png", sheet(4, 360, 168, 3 * ps.size + 6) { c, i ->
            val lands = listOf("rus", "eu", "edo")
            if (i >= 3 * ps.size) { val j = i - 3 * ps.size; Art.bag(c, 360f, lands[j % 3], beans, 0f, 3, if (j < 3) 'r' else 'b', 1f); return@sheet }
            val land = lands[i / ps.size]; val p = ps[i % ps.size]
            bagAt(c, 360f, land, beans, p)
        })
    }
    /** the BagPhase animation at p 0..1 */
    private fun bagAt(c: Canvas, w: Float, land: String, beans: List<Char>, p: Float) {
        if (p == 0f) Art.bag(c, w, land, beans, 0f, -1, null, 0f)
        else if (p < BAG_SHAKE) { val k = p / BAG_SHAKE; Art.bag(c, w, land, beans, (kotlin.math.sin(k * Math.PI * 5) * 2.4 * (1 - k)).toFloat(), -1, null, 0f) }
        else { val k = ((p - BAG_SHAKE) / (1 - BAG_SHAKE)).coerceIn(0f, 1f); Art.bag(c, w, land, beans, 0f, 3, 'w', (1 - (1 - k).toDouble().pow(3)).toFloat()) }
    }
}

private const val BAG_SHAKE = 0.5f
private fun Double.pow(e: Int) = Math.pow(this, e.toDouble())

/** The launcher icon drawn from its own vectors into docs/logo.png (run with `-Prender`). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class RenderLogo {
    @Test fun logo() {
        val ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val n = 512
        val b = Bitmap.createBitmap(n, n, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val clip = android.graphics.Path().apply { addRoundRect(0f, 0f, n.toFloat(), n.toFloat(), n * .22f, n * .22f, android.graphics.Path.Direction.CW) }
        c.clipPath(clip)
        for (id in listOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground)) {
            val d = androidx.core.content.ContextCompat.getDrawable(ctx, id)!!
            d.setBounds(0, 0, n, n); d.draw(c)
        }
        val docs = java.io.File(System.getProperty("render.out") ?: "build/art").parentFile!!.parentFile!!.parentFile!!.resolve("docs").apply { mkdirs() }
        java.io.File(docs, "logo.png").outputStream().use { b.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val small = Bitmap.createScaledBitmap(b, 48 * 3, 48 * 3, true)
        java.io.File(System.getProperty("render.out") ?: "build/art", "logo_small.png").outputStream().use { small.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
