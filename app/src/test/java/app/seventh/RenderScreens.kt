package app.seventh

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import app.seventh.data.SaveStore
import app.seventh.domain.Engine
import app.seventh.domain.Live
import app.seventh.domain.autoplay
import app.seventh.ui.GameState
import app.seventh.ui.Phase
import app.seventh.ui.SeventhRoot
import app.seventh.ui.Sheet
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import kotlin.random.Random

/** The app's screens over a family a few generations deep, drawn to PNGs (run with `-Prender`), for each land. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "ru-w393dp-h851dp-xxhdpi")
abstract class ScreensBase(private val land: String, private val ru: Boolean = true) {
    @org.junit.Before fun lang() { app.seventh.domain.Lang.ru = ru }

    @get:Rule val rule = createAndroidComposeRule<ComponentActivity>()
    private val out = File(System.getProperty("render.out") ?: "build/art", "screens").apply { mkdirs() }

    /** A family 75 years in whose line is still going. */
    private fun state(): GameState {
        val st = GameState(SaveStore(ApplicationProvider.getApplicationContext()))
        var seed = 3
        while (true) {
            val rnd = Random(seed++)
            val e = Engine.newGame(land, rnd)
            e.autoplay(75, rnd)
            if (!e.g.over && e.desc(e.h).size >= 2) { st.adopt(e); return st }
        }
    }

    private fun shot(name: String, st: GameState, setup: GameState.() -> Unit = {}) {
        rule.mainClock.autoAdvance = false
        st.setup()
        rule.setContent { SeventhRoot(st) }
        rule.mainClock.advanceTimeBy(3_000)
        rule.runOnUiThread {
            val view = rule.activity.window.decorView
            val image = android.graphics.Bitmap.createBitmap(view.width, view.height, android.graphics.Bitmap.Config.ARGB_8888)
            view.draw(android.graphics.Canvas(image))
            File(out, "$name.png").outputStream().use { image.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    /** Lives on until an event with a draw comes up. */
    private fun GameState.openEvent(roll: Boolean) {
        repeat(200) {
            live()
            val s = sheet
            if (s is Sheet.Event && (!roll || s.opts.any { it.roll != null && it.cost <= e.g.money })) return
            if (s is Sheet.Event) { choose(s, s.opts.first { it.roll == null || true }); if (s.phase is Phase.Bag) draw(s, s.phase as Phase.Bag); eventDone() }
            else if (s is Sheet.Succ) takeHeir(s.s, s.s.heirs.first().p) else close()
        }
    }

    private fun both(name: String, setup: GameState.() -> Unit = {}) = shot("$land${if (ru) "" else "-en"}-$name", state(), setup)

    @Test fun lands() = shot("$land${if (ru) "" else "-en"}-lands", state()) { sheet = Sheet.Lands }
    @Test fun intro() = shot("$land${if (ru) "" else "-en"}-0-intro", GameState(SaveStore(ApplicationProvider.getApplicationContext()))) { newGame(land) }
    @Test fun home() = both("1-home")
    @Test fun settings() = both("9-settings") { sheet = Sheet.Menu }
    @Test fun onboarding() = both("00-onboarding") { sheet = Sheet.Onboarding }
    @Test fun event() = both("2-event") { openEvent(true) }
    @Test fun bag() = both("3-bag") { openEvent(true); val s = sheet as Sheet.Event; choose(s, s.opts.first { it.roll != null && it.cost <= e.g.money }) }
    @Test fun result() = both("4-result") { openEvent(true); val s = sheet as Sheet.Event; val o = s.opts.first { it.roll != null && it.cost <= e.g.money }; choose(s, o); s.phase = Phase.Done(e.draw(s.occ, o, e.beans(e.rollInfo(o, s.occ))).third) }
    @Test fun tree() = both("5-tree") { tab = 1 }
    @Test fun chronicle() = both("6-chron") { tab = 2 }
    @Test fun card() = both("7-card") { sheet = Sheet.Card(e.h, Sheet.None) }
    @Test fun succession() = both("8-succession") {
        val en = e
        repeat(400) {
            val l = en.live()
            if (l is Live.HeadGone) { en.succession()?.let { sheet = Sheet.Succ(it, l.since) }; return@both }
            if (l is Live.Happens) { val o = l.occ.ev.opts(en, l.occ).first { it.roll == null || it.cost <= en.g.money }; if (o.roll != null) en.draw(l.occ, o, en.beans(en.rollInfo(o, l.occ))) else en.choose(l.occ, o); if (!en.alive(en.h) || en.h.monk) { en.succession()?.let { sheet = Sheet.Succ(it, null) }; return@both } }
        }
    }
}

class ScreensRus : ScreensBase("rus")
class ScreensEu : ScreensBase("eu")
class ScreensJp : ScreensBase("edo")

@Config(sdk = [34], qualifiers = "en-w393dp-h851dp-xxhdpi")
class ScreensRusEn : ScreensBase("rus", false)
@Config(sdk = [34], qualifiers = "en-w393dp-h851dp-xxhdpi")
class ScreensJpEn : ScreensBase("edo", false)
