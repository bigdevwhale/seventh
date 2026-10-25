package app.seventh.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.seventh.data.SaveStore
import app.seventh.domain.Engine
import app.seventh.domain.Live
import app.seventh.domain.Occ
import app.seventh.domain.Opt
import app.seventh.domain.Outcome
import app.seventh.domain.Person
import app.seventh.domain.RollInfo
import app.seventh.domain.Since
import app.seventh.domain.Succession
import java.util.concurrent.Executors

/** What lies over the home screen. */
sealed class Sheet {
    data object None : Sheet()
    data object Lands : Sheet()
    data object Intro : Sheet()
    class Event(val occ: Occ, val since: Since, val text: String, val opts: List<Opt>) : Sheet() {
        var phase by mutableStateOf<Phase>(Phase.Choose)
    }
    class Succ(val s: Succession, val since: Since?) : Sheet()
    class Quiet(val since: Since) : Sheet()
    class Card(val p: Person, val back: Sheet) : Sheet()
    data object Menu : Sheet()
    class HowTo(val back: Sheet) : Sheet()
    data object Victory : Sheet()
    data object Over : Sheet()
}

sealed class Phase {
    data object Choose : Phase()
    class Bag(val o: Opt, val info: RollInfo, val beans: List<Char>) : Phase() {
        /** Set once the bean is drawn: which one, where it lay, and what came of it. */
        var drawn by mutableStateOf<Triple<Char, Int, Outcome>?>(null)
    }
    class Done(val o: Outcome) : Phase()
}

/** The family in play and what is on screen; the engine is mutable, so [rev] is bumped to redraw after every move. */
class GameState(private val store: SaveStore) {
    var engine by mutableStateOf<Engine?>(null)
        private set
    var rev by mutableIntStateOf(0)
        private set
    var sheet by mutableStateOf<Sheet>(Sheet.None)
    var tab by mutableIntStateOf(0)
    var toast by mutableStateOf<String?>(null)
    private val io = Executors.newSingleThreadExecutor()

    init {
        val g = store.load()
        if (g != null) engine = Engine(g) else sheet = Sheet.Lands
    }

    val e: Engine get() = engine!!

    /** Puts a family already in progress into play (the demo build and the screen renders). */
    fun adopt(en: Engine) { engine = en; sheet = Sheet.None; touch() }

    fun touch() {
        rev++
        val g = engine?.g ?: return
        val s = store.encode(g)
        io.execute { runCatching { store.saveRaw(s) } }
    }

    fun newGame(land: String) {
        engine = Engine.newGame(land)
        tab = 0
        sheet = Sheet.Intro
        touch()
    }

    fun live() {
        if (e.g.over) return
        when (val l = e.live()) {
            is Live.Happens -> sheet = Sheet.Event(l.occ, l.since, l.occ.ev.txt(e, l.occ), l.occ.ev.opts(e, l.occ))
            is Live.HeadGone -> startSuccession(l.since)
            is Live.Quiet -> sheet = Sheet.Quiet(l.since)
        }
        touch()
    }

    private fun startSuccession(since: Since?) {
        val s = e.succession()
        sheet = if (s == null) Sheet.Over else Sheet.Succ(s, since)
    }

    fun choose(ev: Sheet.Event, o: Opt) {
        if (o.roll == null) {
            ev.phase = Phase.Done(e.choose(ev.occ, o))
            touch()
        } else {
            val info = e.rollInfo(o, ev.occ)
            ev.phase = Phase.Bag(o, info, e.beans(info))
        }
    }

    fun draw(ev: Sheet.Event, bag: Phase.Bag) {
        if (bag.drawn != null) return
        bag.drawn = e.draw(ev.occ, bag.o, bag.beans)
        touch()
    }

    fun eventDone() {
        sheet = Sheet.None
        val h = e.h
        if (!e.alive(h) || h.monk) startSuccession(null)
        touch()
    }

    fun takeHeir(s: Succession, p: Person) {
        val won = e.takeHeir(p, s.eldest)
        sheet = if (won) Sheet.Victory else Sheet.None
        touch()
    }

    fun close() {
        sheet = when (val s = sheet) {
            is Sheet.Card -> s.back
            is Sheet.HowTo -> s.back
            else -> Sheet.None
        }
    }
}
