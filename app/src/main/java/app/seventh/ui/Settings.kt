package app.seventh.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import app.seventh.BuildConfig
import app.seventh.R
import app.seventh.domain.Lang
import app.seventh.domain.Lands
import app.seventh.ui.theme.LocalPal

/** The StillCraft bot: deep links file a bug or an idea for this app, or follow its releases. */
object Community {
    private const val BOT = "https://t.me/ArcaneWhisperBot?start="
    const val SLUG = "seventh"
    const val BUG = "${BOT}bug_$SLUG"
    const val IDEA = "${BOT}wish_$SLUG"
    const val FOLLOW = "${BOT}f_$SLUG"
    const val CHAT = "https://t.me/+Q4gFEDk8O842OGVi"
    const val REPO = "https://github.com/bigdevwhale/seventh"
}

@Composable
fun SettingsSheet(st: GameState) {
    val P = LocalPal.current
    val ctx = LocalContext.current
    fun open(url: String) { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } }
    var confirm by remember { mutableStateOf(false) }
    SheetTitle(stringResource(R.string.settings))
    PaperCard {
        H3(stringResource(R.string.language))
        Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Pill("Русский", if (Lang.ru) P.ochre else P.paper2, size = 15f, onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ru")) })
            Pill("English", if (!Lang.ru) P.ochre else P.paper2, size = 15f, onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en")) })
        }
        Text(stringResource(R.string.language_note), Modifier.padding(top = 8.dp), style = P.small)
    }
    Gap(10.dp)
    PaperCard {
        H3(stringResource(R.string.game))
        Gap(6.dp)
        Btn(stringResource(R.string.how_to), Modifier.fillMaxWidth()) { st.sheet = Sheet.HowTo(Sheet.Menu) }
        Gap(8.dp)
        Btn(stringResource(R.string.show_intro), Modifier.fillMaxWidth()) { st.sheet = Sheet.Onboarding }
        Gap(8.dp)
        if (!confirm) Btn(stringResource(R.string.new_lot), Modifier.fillMaxWidth()) { if (st.engine?.g?.over != false) st.sheet = Sheet.Lands else confirm = true }
        else Btn(stringResource(R.string.new_lot_confirm), Modifier.fillMaxWidth(), big = true) { st.sheet = Sheet.Lands }
        Text(stringResource(R.string.menu_note), Modifier.padding(top = 8.dp), style = P.small)
    }
    Gap(10.dp)
    PaperCard {
        H3(stringResource(R.string.community))
        Text(stringResource(R.string.community_note), Modifier.padding(top = 4.dp, bottom = 4.dp), style = P.small)
        for ((icon, label, sub, url) in listOf(
            Link("🐞", stringResource(R.string.c_bug), stringResource(R.string.c_bug_sub), Community.BUG),
            Link("💡", stringResource(R.string.c_idea), stringResource(R.string.c_idea_sub), Community.IDEA),
            Link("🔔", stringResource(R.string.c_follow), stringResource(R.string.c_follow_sub), Community.FOLLOW),
            Link("💬", stringResource(R.string.c_chat), stringResource(R.string.c_chat_sub), Community.CHAT),
        )) {
            Row(Modifier.fillMaxWidth().clickable { open(url) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(icon, Modifier.width(34.dp), style = P.b(20.sp))
                Column(Modifier.weight(1f)) {
                    Text(label, style = P.b(16.sp))
                    Text(sub, style = P.small)
                }
                Text("›", style = P.d(20.sp))
            }
        }
    }
    Gap(10.dp)
    PaperCard {
        H3(stringResource(R.string.about))
        val n = Lands.all.sumOf { it.forced.size + it.events.size + it.history.size }
        Text(stringResource(R.string.about_text, Lands.all.size, n), Modifier.padding(top = 4.dp), style = P.small)
        Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME), Modifier.padding(top = 8.dp).clickable { open(Community.REPO) }, style = P.b(13.sp, P.red))
    }
    Gap(12.dp)
    Btn(stringResource(R.string.close), Modifier.fillMaxWidth()) { st.close() }
}

private data class Link(val icon: String, val label: String, val sub: String, val url: String)
