package app.seventh.ui.art

import android.graphics.Typeface

/**
 * What the art needs to know about the game, and nothing more: the domain fills these in,
 * the painters in this package only read them. Lands are "rus" (lubok) and "eu" (illuminated manuscript).
 */

/** One person as the painter sees them. [kind] is "" for family, or an extra: woman, man, kid, beggar, priest, soldier, barin, bride, monk. */
data class Fig(
    val sex: Char,
    val age: Int,
    val skin: Int,
    val hair: Int,
    val cloth: Int,
    val kerch: Int,
    val seed: Int,
    val kind: String = "",
)

/** The people in an event picture: the head of the family, their spouse, the person the event is about, and for "dawn" the household. */
data class SceneData(
    val hero: Fig?,
    val spouse: Fig? = null,
    val other: Fig? = null,
    val family: List<Fig> = emptyList(),
    val year: Int,
)

/** The farmstead on the home screen. [season] 0 spring, 1 summer, 2 autumn, 3 winter; [over] = the line has ended (night, snow, nobody home). */
data class HomeData(
    val dvor: Int,
    val season: Int,
    val over: Boolean,
    val family: List<Fig>,
    val seed: Int,
    val year: Int,
)

/** Portrait backgrounds: a head of the family, a blood relative, someone who married in. */
enum class PBg { HEAD, BLOOD, INLAW }

/** The ring round a medallion on the tree. */
enum class Ring { HEAD, WAS, ALIVE, DEAD }

data class TreeNode(
    val id: Int,
    val x: Float,
    val y: Float,
    val fig: Fig,
    val bg: PBg,
    /** Dead or gone to a monastery: the portrait goes sepia. */
    val faded: Boolean,
    val ring: Ring,
    /** Somebody in this branch is still alive. */
    val liveLine: Boolean,
    /** Alive and in the world (not dead, not in a monastery). */
    val alive: Boolean,
    /** null = unmarried; true = spouse alive, false = widowed. */
    val spouse: Boolean?,
    val bast: Boolean,
    val crowned: Boolean,
    val name: String,
    val years: String,
)

data class TreeEdge(val from: Int, val to: Int, val main: Boolean)

/**
 * The family tree in logical units ([w]×[h]); the painter scales by `scale`.
 * [rowY] is the y of generation row d (0 = founder); [rootId] is the founder.
 */
data class TreeData(
    val w: Float,
    val h: Float,
    val rows: Int,
    val rowY: List<Float>,
    val rootId: Int,
    val nodes: List<TreeNode>,
    val edges: List<TreeEdge>,
    val won: Boolean,
    val autumn: Boolean,
    val numeralFace: Typeface,
    val labelFace: Typeface,
)
