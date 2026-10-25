package app.seventh

import android.app.Application
import app.seventh.data.SaveStore
import app.seventh.domain.Engine
import app.seventh.domain.autoplay
import app.seventh.ui.GameState
import kotlin.random.Random

class SeventhApp : Application() {
    /** One family per install; it outlives the activity so rotation or a dark screen never drops an open event. */
    val state: GameState by lazy {
        GameState(SaveStore(this)).also { if (BuildConfig.DEMO && it.engine == null) it.adopt(demoFamily()) }
    }

    /** The demo build starts on a Rus family ninety years and three generations in, line still alive. */
    private fun demoFamily(): Engine {
        var seed = 7
        while (true) {
            val rnd = Random(seed++)
            val e = Engine.newGame("rus", rnd)
            e.autoplay(90, rnd)
            if (!e.g.over && e.h.gen >= 3 && e.desc(e.h).size >= 3) return e
        }
    }
}
