package app.seventh.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import app.seventh.R

/** One land's look: the lubok's cinnabar and ochre, or the manuscript's ultramarine and gold. */
@Immutable
class Pal(
    val land: String,
    val paper: Color,
    val paper2: Color,
    val paper3: Color,
    val ink: Color,
    val red: Color,
    val dred: Color,
    val green: Color,
    val ochre: Color,
    val blue: Color,
    val bg: Color,
    val muted: Color,
    val bar: Color,
    val barText: Color,
    /** The big button: cinnabar in Rus, ultramarine in Europe. */
    val big: Color,
    /** The two colours of the ornament band. */
    val ornA: Color,
    val ornB: Color,
    val display: FontFamily,
    val displayWeight: FontWeight,
    val body: FontFamily,
) {
    val good = Color(0xFFC4DCA4)
    val mid = Color(0xFFF0D88E)
    val bad = Color(0xFFEFB4A2)
    val warn = Color(0xFFF4CDBC)
    val birth = Color(0xFF2F5426)
    val death = Color(0xFF4A3A30)
    val window = Color(0xFF1F1712)

    fun d(size: TextUnit, color: Color = red) = TextStyle(fontFamily = display, fontWeight = displayWeight, fontSize = size, lineHeight = size * 1.15f, color = color)
    fun b(size: TextUnit, color: Color = ink) = TextStyle(fontFamily = body, fontSize = size, lineHeight = size * 1.35f, color = color)
    val text get() = b(15.5.sp)
    val small get() = b(13.sp, muted)
    val tiny get() = b(12.sp, muted)
    val italic get() = b(15.5.sp).copy(fontStyle = FontStyle.Italic)
}

private val Ruslan = FontFamily(Font(R.font.ruslan_display))
private val Kurale = FontFamily(Font(R.font.kurale))
private val Zen = FontFamily(Font(R.font.zen_old_mincho))
private val ZenBlack = FontFamily(Font(R.font.zen_old_mincho_black, FontWeight.Black))
private val AlegreyaSC = FontFamily(Font(R.font.alegreya_sc_extrabold, FontWeight.ExtraBold))
@OptIn(ExperimentalTextApi::class)
private val Alegreya = FontFamily(
    Font(R.font.alegreya, FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.alegreya, FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(R.font.alegreya, FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

val RusPal = Pal(
    "rus", paper = Color(0xFFF5EACB), paper2 = Color(0xFFECDCB2), paper3 = Color(0xFFCDB487), ink = Color(0xFF1F1712), red = Color(0xFFC9372C), dred = Color(0xFF8E2622),
    green = Color(0xFF3F7A44), ochre = Color(0xFFE2A83A), blue = Color(0xFF3F6EA6), bg = Color(0xFFDCC797), muted = Color(0xFF6E5A3E),
    bar = Color(0xFF1F1712), barText = Color(0xFFCDB487), big = Color(0xFFC9372C), ornA = Color(0xFFC9372C), ornB = Color(0xFFE2A83A),
    display = Ruslan, displayWeight = FontWeight.Normal, body = Kurale,
)

val EuPal = Pal(
    "eu", paper = Color(0xFFF5ECD6), paper2 = Color(0xFFEBDFC2), paper3 = Color(0xFFC9B68E), ink = Color(0xFF2A1C14), red = Color(0xFFB8321F), dred = Color(0xFF7D2016),
    green = Color(0xFF3F7A44), ochre = Color(0xFFD6A93C), blue = Color(0xFF2C4F9E), bg = Color(0xFFD6C8A4), muted = Color(0xFF6A5A44),
    bar = Color(0xFF1B2A56), barText = Color(0xFFC9B68E), big = Color(0xFF2C4F9E), ornA = Color(0xFF2C4F9E), ornB = Color(0xFFB8321F),
    display = AlegreyaSC, displayWeight = FontWeight.ExtraBold, body = Alegreya,
)

val JpPal = Pal(
    "edo", paper = Color(0xFFF4ECD8), paper2 = Color(0xFFEADFC4), paper3 = Color(0xFFC7B48C), ink = Color(0xFF1D1A24), red = Color(0xFFC0392B), dred = Color(0xFF8A2A1E),
    green = Color(0xFF4F7A4A), ochre = Color(0xFFDDB15A), blue = Color(0xFF1F3A5F), bg = Color(0xFFDCCFB2), muted = Color(0xFF6A5D4C),
    bar = Color(0xFF1C2B42), barText = Color(0xFFC7B48C), big = Color(0xFFB8372A), ornA = Color(0xFF1F3A5F), ornB = Color(0xFFF4ECD8),
    display = ZenBlack, displayWeight = FontWeight.Black, body = Zen,
)

fun palOf(land: String) = when (land) { "eu" -> EuPal; "edo" -> JpPal; else -> RusPal }

val LocalPal = staticCompositionLocalOf { RusPal }

@Composable
fun SeventhTheme(pal: Pal, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalPal provides pal, content = content)
}
