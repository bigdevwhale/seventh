package app.seventh.domain

import app.seventh.domain.Stat.HIT
import app.seventh.domain.Stat.SER
import app.seventh.domain.Stat.SIL
import app.seventh.domain.Stat.UM
import app.seventh.domain.Stat.VER
import app.seventh.domain.Stat.ZDR

/*
 * More of northern France: tempers and ages, chains of consequences (marks `eu_*`), rare marvels, and three ways off the land —
 * the weavers' guild in town (`way_guild`), a bought charter of freedom (`way_free`), the abbey's patronage (`way_abbey`).
 */

private val WAYS = listOf("way_guild", "way_free", "way_abbey")

private fun Engine.takeWay(k: String) { for (w in WAYS) if (w != k) unflag(w); flag(k) }
private fun Engine.ripe(k: String, n: Int) = flagged(k) && (since(k) ?: 0) >= n
private fun Engine.bloodParent(c: Person): Person? = person(c.father)?.takeIf { it.blood } ?: person(c.mother)?.takeIf { it.blood }
private fun Engine.soothe(p: Person) { if (p.stones > 0) p.stones-- }
/** A chain not yet begun and never played out in this family. */
private fun Engine.fresh(k: String) = !flagged(k) && !flagged(k + "_end")
/** A chain played out: its mark gives way to a memory, so it does not begin again. */
private fun Engine.close(k: String) { if (flagged(k)) { unflag(k); flag(k + "_end") } }
/** Starts a chain from an older event, unless this family has already lived it through. */
internal fun Engine.mark(k: String) { if (!flagged(k + "_end")) flag(k) }

internal val europeMoreEvents: List<Event> = listOf(
    // ---- tempers and states of the head
    Event("eu_pledge", "tavern", Tx("Заклад", "The Pledge"), cond = { h -> fire(has(h, "pyan") && age(h) >= 18) },
        txt = { t("Вчерашний вечер вспоминается кусками: в таверне вы ударили по рукам с трактирщиком Годфруа и заложили ему пегую корову. Сегодня он стоит у ворот с верёвкой.",
            "Last night comes back in pieces: at the tavern you shook hands with Godefroy the innkeeper and pledged him the piebald cow. Today he stands at the gate with a rope.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Отдать корову: слово есть слово", "Give up the cow: a word is a word"), tag = t("−4 су", "−4 sous"), fx = { money(-4); glory(1); t("Корову увели. В деревне говорят: пьёт, но слово держит.", "The cow was led away. The village says: drinks, but keeps ${sx(h, "his", "her")} word.") }),
                Opt(t("Божиться, что никакого уговора не было", "Swear there was never any bargain"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { t("Свидетели были пьянее вас. Трактирщик ушёл с пустой верёвкой.", "The witnesses were drunker than you. The innkeeper went off with an empty rope.") },
                    bad = { money(-5); glory(-1); t("Трактирщик позвал в свидетели кюре — тот, оказывается, сидел за соседним столом. Корову увели, и пеню сверху.", "The innkeeper called the curé as a witness; it turns out he had been sitting at the next table. The cow went, and a fine on top.") }),
                Opt(t("Выкупить корову и дать зарок кюре", "Buy the cow back and swear off drink before the curé"), cost = 2, tag = t("2 су", "2 sous"), roll = Roll(VER, -2, mapOf("bogom" to 2)),
                    ok = { money(-2); h.tr.remove("pyan"); deed(h, t(sx(h, "Бросил", "Бросила") + " пить по зароку", "Swore off drink and kept the vow")); t("Корова дома, а зарок держится. Кум в таверне скучает.", "The cow is home and the vow holds. Your crony at the tavern is bored.") },
                    bad = { money(-2); h.stones++; t("Продержались до Троицы. Корова дома, а на душе — камень.", "You held out till Whitsun. The cow is home, and a stone sits on your soul.") }),
            )
        }),
    Event("eu_blow", "barin", Tx("Рука поднялась сама", "The Hand Rose of Itself"), cond = { h -> fire(has(h, "gnev") && age(h) >= 18 && fresh("eu_blow")) },
        txt = { t("Управляющий сеньора пересчитывал ваши снопы и назвал вас вором. Рука поднялась сама. Теперь управляющий Ренье сидит в борозде с разбитым носом, а вокруг стоит полдеревни.",
            "The seigneur's steward was counting your sheaves and called you a thief. Your hand rose of itself. Now Renier the steward sits in the furrow with a broken nose, and half the village stands around.") },
        opts = { o ->
            listOf(
                Opt(t("Поднять его и повиниться", "Help him up and beg his pardon"), roll = Roll(SER, -1, mapOf("kras" to 1, "shch" to 1)),
                    ok = { t("Ренье утёр кровь и сказал, что упал сам. Снопы пересчитали — сошлось.", "Renier wiped off the blood and said he had fallen. The sheaves were counted again, and they tallied.") },
                    bad = { money(-4); flag("eu_blow"); t("Ренье ушёл молча. Через неделю суд сеньора взял с вас пеню, а Ренье — запомнил.", "Renier left without a word. A week later the seigneur's court fined you, and Renier remembered.") }),
                Opt(t("Сказать при всех, что он сам обсчитывает сеньора", "Say before them all that he cheats the seigneur himself"), roll = Roll(UM, 0, mapOf("gram" to 2, "umn" to 1)),
                    ok = { glory(2); flag("eu_blow"); t("Сеньор велел проверить книги, и Ренье выгнали. Деревня неделю пила за вас. Только у Ренье остался сын.", "The seigneur had the books checked, and Renier was turned out. The village drank to you for a week. But Renier has a son.") },
                    bad = { money(-6); glory(-1); flag("eu_blow"); t("Книги у Ренье оказались чище ваших слов. Пеня — шесть су.", "Renier's books proved cleaner than your words. The fine: six sous.") }),
                Opt(t("Добавить ещё", "Give him some more"), sin = "cruel", fx = { money(-5); glory(-1); flag("eu_blow"); t("Ренье отлёживался до Михайлова дня. Сеньор взял пеню, а деревня стала обходить вас стороной.", "Renier lay abed until Michaelmas. The seigneur took a fine, and the village began to give you a wide berth.") }),
            )
        }),
    Event("eu_relic", "road", Tx("Палец святого Леонарда", "Saint Leonard's Finger"), cond = { h -> fire(has(h, "lzh") && age(h) >= 18 && fresh("eu_fakerel")) },
        txt = { t("Через деревню идут паломники в Компостелу, усталые и доверчивые. В сарае у вас лежит старая баранья кость, а язык у вас подвешен как надо.",
            "Pilgrims bound for Compostela are passing through the village, footsore and trusting. In your shed lies an old mutton bone, and your tongue is well hung.") },
        opts = { o ->
            listOf(
                Opt(t("Продать кость как палец святого Леонарда", "Sell the bone as Saint Leonard's finger"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { money(7); flag("eu_fakerel"); t("Паломники целовали кость и платили серебром. Семь су. Святой Леонард, надо думать, не в обиде.", "The pilgrims kissed the bone and paid in silver. Seven sous. Saint Leonard, one must suppose, does not mind.") },
                    bad = { money(-1); glory(-2); t("Один из паломников оказался мясником из Амьена. Кость вам вернули — в лоб.", "One of the pilgrims turned out to be a butcher from Amiens. The bone came back to you, across the forehead.") }),
                Opt(t("Продать им хлеба и воды", "Sell them bread and water"), fx = { money(2); t("Честный заработок: два су и благословение на дорогу.", "An honest profit: two sous and a blessing for the road.") }),
                Opt(t("Накормить даром", "Feed them for nothing"), fx = { money(-1); glory(1); soothe(o.h); t("Паломники пели у вас во дворе «Аве Марис Стелла». Соседи заглядывали через плетень.", "The pilgrims sang Ave Maris Stella in your yard. The neighbors peered over the fence.") }),
            )
        }),
    Event("eu_relic2", "church", Tx("Каноник из Амьена", "The Canon from Amiens"), w = 3, cond = { fire(ripe("eu_fakerel", 4)) },
        txt = { t("От епископа приехал каноник. В одной церкви под Амьеном объявился палец святого Леонарда — и не творит никаких чудес. Ниточка тянется к вашему двору.",
            "A canon has come from the bishop. A finger of Saint Leonard has turned up in a church near Amiens, and it works no miracles at all. The thread leads to your door.") },
        opts = { o ->
            listOf(
                Opt(t("Покаяться", "Confess"), roll = Roll(VER, 1, mapOf("chest" to 1, "bogom" to 1)),
                    ok = { close("eu_fakerel"); money(-3); soothe(o.h); t("Каноник наложил покаяние: три су на свечи и год поста по пятницам. На душе легче.", "The canon set a penance: three sous for candles and a year of Friday fasts. Your soul is lighter.") },
                    bad = { close("eu_fakerel"); money(-6); glory(-2); t("Покаяние вышло публичным: босиком вокруг церкви, со свечой, на глазах у Ле Гро.", "The penance was a public one: barefoot round the church with a candle, under the eyes of the Le Gros.") }),
                Opt(t("Клясться, что кость продал кто-то другой", "Swear somebody else sold the bone"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 2)),
                    ok = { close("eu_fakerel"); t("Каноник уехал искать «рыжего бродягу из Нуайона». Ищет до сих пор.", "The canon rode off in search of “a red-haired vagabond from Noyon.” He is searching still.") },
                    bad = { close("eu_fakerel"); money(-4); glory(-3); t("Паломники узнали ваш двор. Каноник записал ваше имя в книгу — не в ту, в которую хочется попасть.", "The pilgrims knew your house. The canon wrote your name in a book, and not the kind one wants to be in.") }),
                Opt(t("Пожертвовать на новый ковчежец", "Give money for a new reliquary"), cost = 6, tag = t("6 су", "6 sous"), fx = { close("eu_fakerel"); money(-6); glory(1); t("Каноник принял дар и рассудил, что чудеса, видно, ещё будут.", "The canon accepted the gift and decided that the miracles were, no doubt, still to come.") }),
            )
        }),
    Event("eu_loan", "village", Tx("Взаймы до жатвы", "A Loan till Harvest"), cond = { h -> fire(has(h, "zhad") && g.money >= 6 && fresh("eu_loan")) },
        txt = { t("Сосед Обер просит шесть су до жатвы: у него пала лошадь. Кюре говорит, что рост — грех. Но кюре не видел, какая у Обера полоса у ручья.",
            "Aubert next door asks for six sous till harvest: his horse has died. The curé says usury is a sin. But the curé has not seen Aubert's strip down by the brook.") },
        opts = { o ->
            listOf(
                Opt(t("Дать под залог его полосы", "Lend against his strip of land"), sin = "greed", tag = t("−6 су", "−6 sous"), fx = { money(-6); flag("eu_loan"); flag("eu_usury"); t("Обер поставил крест вместо подписи. Полоса у ручья теперь в закладе у вас.", "Aubert made his cross for a signature. The strip by the brook is now pledged to you.") }),
                Opt(t("Дать без роста", "Lend it without interest"), tag = t("−6 су", "−6 sous"), fx = { money(-6); flag("eu_loan"); soothe(o.h); t("Обер долго тряс вам руку. Шесть су ушли — может, и вернутся.", "Aubert shook your hand for a long time. Six sous gone; perhaps they will come back.") }),
                Opt(t("Отказать", "Refuse"), fx = { t("Обер ушёл, не попрощавшись. Весной он пахал на себе.", "Aubert left without a farewell. In spring he pulled the plow himself.") }),
            )
        }),
    Event("eu_loan2", "field", Tx("Долг Обера", "Aubert's Debt"), w = 3, cond = { fire(ripe("eu_loan", 2)) },
        txt = { if (flagged("eu_usury")) t("Жатва прошла, а Обер не несёт ни долга, ни роста. По бумаге полоса у ручья ваша — если хватит духу её взять.", "The harvest is in, and Aubert brings neither the debt nor the interest. On paper the strip by the brook is yours, if you have the stomach to take it.")
            else t("Обер принёс долг: шесть су, завязанных в тряпицу, и мешок яблок сверху.", "Aubert has brought the debt: six sous knotted in a rag, and a sack of apples on top.") },
        opts = { o ->
            fun done() { close("eu_loan"); unflag("eu_usury") }
            if (flagged("eu_usury")) listOf(
                Opt(t("Забрать полосу", "Take the strip"), sin = "cruel", fx = { done(); money(5); glory(-2); flag("eu_strip"); t("Полоса ваша. Обер ходит батрачить к Ле Гро, а дети его смотрят вам вслед.", "The strip is yours. Aubert hires himself out to the Le Gros, and his children watch you go by.") }),
                Opt(t("Подождать ещё год", "Give him another year"), roll = Roll(SER, 0, mapOf("dobr" to 1)),
                    ok = { done(); money(8); t("Через год Обер вернул всё с ростом и ещё кланялся.", "A year later Aubert paid it all back with interest, and bowed besides.") },
                    bad = { done(); t("Обер умер зимой. Вдова плачет, полоса заросла, долг пропал.", "Aubert died in the winter. The widow weeps, the strip has gone to weeds, the debt is lost.") }),
                Opt(t("Простить долг", "Forgive the debt"), fx = { done(); glory(2); soothe(o.h); t("Обер плакал. Кюре в воскресной проповеди говорил о вас, не называя имени.", "Aubert wept. In his Sunday sermon the curé spoke of you without naming you.") }),
            ) else listOf(
                Opt(t("Принять с благодарностью", "Take it with thanks"), fx = { done(); money(6); glory(1); t("Шесть су и мешок яблок. В беде Обер теперь за вас горой.", "Six sous and a sack of apples. In any trouble Aubert will stand by you now.") }),
                Opt(t("Оставить ему на лошадь", "Let him keep it for a horse"), fx = { done(); glory(2); t("Обер купил жеребёнка и назвал его вашим именем. Честь сомнительная, но от сердца.", "Aubert bought a colt and named it after you. A doubtful honor, but from the heart.") }),
            )
        }),
    Event("eu_found", "church", Tx("Подкидыш", "The Foundling"), cond = { h -> fire(age(h) >= 18 && fresh("eu_found")) },
        txt = { pick(listOf(
            t("На паперти, в корзине из-под рыбы, пищит подкидыш. Кюре разводит руками: «Кто-то должен взять. Иначе в госпиталь, а оттуда мало кто выходит».",
                "On the church steps, in a fish basket, a foundling is wailing. The curé spreads his hands: “Someone must take him. Otherwise it's the hospital, and few come out of there.”"),
            t("Утром у вашего порога нашли младенца в рогоже, а при нём щепотку соли — знак, что не крещён. Чей — никто не знает, а глядит он прямо на вас.",
                "In the morning a baby wrapped in sacking was found on your doorstep, with a pinch of salt beside him: a sign he has not been baptized. Whose he is nobody knows, and he looks straight at you."),
        )) },
        opts = { o ->
            listOf(
                Opt(t("Взять в дом", "Take him in"), cost = 2, tag = t("2 су", "2 sous"), fx = { money(-2); glory(1); flag("eu_found"); t("Окрестили Мартеном, по святому дня. Спит в корыте у очага и кричит громче своих.", "He was baptized Martin, after the saint of the day. He sleeps in a trough by the hearth and yells louder than your own.") }),
                Opt(t("Отнести в аббатство", "Carry him to the abbey"), cost = 1, tag = t("1 су", "1 sou"), fx = { money(-1); t("Монахи взяли ребёнка. Может, выйдет в братья.", "The monks took the child. Perhaps he will become one of the brothers.") }),
                Opt(t("Пройти мимо", "Walk on by"), sin = "cruel", fx = { t("К вечеру корзину унесли. Куда — никто не спрашивал.", "By evening the basket had been taken away. Where to, nobody asked.") }),
            )
        }),
    Event("eu_found2", "field", Tx("Мартен вырос", "Martin Grown"), w = 3, cond = { fire(ripe("eu_found", 15)) },
        txt = { t("Мартен-подкидыш вырос: широк в плечах, молчалив, пашет за двоих. Сегодня он стоит в дверях с шапкой в руках: «Я хочу знать, кто я вам».",
            "Martin the foundling has grown: broad in the shoulder, quiet, plows for two. Today he stands in the doorway, cap in hand: “I want to know what I am to you.”") },
        opts = { o ->
            listOf(
                Opt(t("Назвать сыном перед кюре", "Name him a son before the curé"), fx = { close("eu_found"); money(6); glory(1); t("Кюре записал его в книгу: Мартен ${g.stem}. Деревня поворчала, а Мартен в тот же год распахал пустошь за ручьём.", "The curé wrote him into the book: Martin ${g.stem}. The village grumbled, and that same year Martin broke the waste ground beyond the brook.") }),
                Opt(t("Дать денег и отпустить в ученье", "Give him money and send him to learn a trade"), cost = 4, tag = t("4 су", "4 sous"), fx = { close("eu_found"); money(-4); glory(1); t("Мартен ушёл в Реймс к каменщикам. Говорят, он тешет камень для нового собора и к Пасхе присылает поклон.", "Martin went to the masons at Reims. They say he cuts stone for the new cathedral, and sends his respects at Easter.") }),
                Opt(t("Сказать правду: найден в корзине", "Tell the truth: found in a basket"), roll = Roll(SER, 0, mapOf("chest" to 1, "dobr" to 1)),
                    ok = { close("eu_found"); money(4); t("Мартен помолчал и сказал: «Корзина так корзина. А дом — этот». И остался.", "Martin was silent for a while, then said: “A basket, then. But this is the house.” And he stayed.") },
                    bad = { close("eu_found"); t("Мартен ушёл той же ночью. Потом его будто видели с наёмниками.", "Martin left that same night. Later someone thought they saw him with the mercenaries.") }),
            )
        }),
    Event("eu_vow", "winter", Tx("Обет", "The Vow"), cond = { fire(if (!fresh("eu_vow")) null else kidIn(2, 12).firstOrNull()) },
        txt = { o -> val x = o.xx; t("${x.name} ${sx(x, "провалился", "провалилась")} под лёд на мельничном пруду. Пока бежали с жердью, вы пообещали Богородице Шартрской: спасёт — принесёте ей свечу ростом с ребёнка и дойдёте до Шартра босиком.",
            "${x.name} has gone through the ice on the millpond. As you ran with a pole, you promised Our Lady of Chartres: save the child, and you will bring her a candle as tall as ${sx(x, "him", "her")} and walk to Chartres barefoot.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Лезть в воду самим", "Go into the water yourself"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1, "trus" to -2)),
                    ok = { flag("eu_vow"); deed(o.h, t(sx(o.h, "Вытащил", "Вытащила") + " дитя из-подо льда", "Pulled a child from under the ice")); t("${x.name} вытащили синим, но живым. Обет дан — теперь его держать.", "${x.name} came out blue but alive. The vow is made; now it must be kept.") },
                    bad = { flag("eu_vow"); o.h[ZDR]--; t("Вытащили обоих. ${x.name} отогрел${sx(x, "ся", "ась")} у печи, а вас всю зиму бил кашель. Обет дан.", "Both of you were pulled out. ${x.name} warmed up by the fire, but a cough shook you all winter. The vow is made.") }),
                Opt(t("Молиться и тянуть жердь", "Pray and hold out the pole"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)),
                    ok = { flag("eu_vow"); t("${x.name} ухватил${sx(x, "ся", "ась")} за жердь. Богородица услышала; теперь слово за вами.", "${x.name} caught hold of the pole. Our Lady heard; now it is your turn to keep your word.") },
                    bad = { flag("eu_vow"); addTr(x, "hvor"); t("Вытащили, но ${x.name} с той зимы кашляет. Обет всё равно дан.", "${x.name} was pulled out, but has coughed ever since that winter. The vow is made all the same.") }),
            )
        }),
    Event("eu_vow2", "church", Tx("Обет Богородице", "A Vow to Our Lady"), w = 3, cond = { fire(ripe("eu_vow", 3)) },
        txt = { t("Свеча ростом с ребёнка так и не куплена, и до Шартра никто не дошёл. Кюре напоминает: обет Богородице — не долг трактирщику, за давностью его не спишут.",
            "The child-tall candle has never been bought, and nobody has walked to Chartres. The curé reminds you: a vow to Our Lady is not a debt to an innkeeper, and time will not cancel it.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Идти в Шартр босиком", "Walk to Chartres barefoot"), roll = Roll(ZDR, 0, mapOf("bogom" to 2, "krot" to 1)),
                    ok = { close("eu_vow"); addTr(h, "bogom"); glory(2); soothe(h); t("Шартрский собор встаёт над хлебами, как корабль над морем. Свеча горит у Богородицы, ноги сбиты в кровь, на душе светло.", "Chartres cathedral rises over the wheat like a ship over the sea. The candle burns before Our Lady, your feet are bloody, your heart is light.") },
                    bad = { close("eu_vow"); h[ZDR]--; t("Дошли, но на обратном пути слегли в странноприимном доме. Обет исполнен, а здоровье уже не то.", "You got there, but fell ill on the way home in a pilgrims' hostel. The vow is kept; your health is not what it was.") }),
                Opt(t("Купить свечу, а поход отложить", "Buy the candle and put off the walk"), cost = 4, tag = t("4 су · обет напомнит о себе", "4 sous · the vow will be back"), fx = { money(-4); flag("eu_vow"); t("Свеча горит у Богородицы. Поход — за вами.", "The candle burns before Our Lady. The walk is still owed.") }),
                Opt(t("Забыть про обет", "Forget the vow"), sin = "lie", fx = { close("eu_vow"); g.hex = g.year + 5; t("Свечу так и не купили. В мешке с бобами на пять лет прибавился второй красный.", "The candle was never bought. For five years a second red bean lies in the bag.") }),
            )
        }),
    Event("eu_scribe", "village", Tx("Писарь на всю деревню", "Scribe to the Village"), cond = { h -> fire(has(h, "gram") && age(h) >= 18) },
        txt = { t("Писать в деревне умеете только вы. С утра у порога очередь: письмо сыну в Руан, счёт мельнику, жалоба сеньору. Последним пришёл старший Ле Гро — просит написать грамоту на чужой луг.",
            "You are the only one in the village who can write. Since morning there has been a line at your door: a letter to a son in Rouen, an account for the miller, a petition to the seigneur. Last came the elder Le Gros, asking for a deed to someone else's meadow.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Писать всем, по денье за строку, кроме Ле Гро", "Write for all but the Le Gros, a denier a line"), fx = { money(3); t("К вечеру пальцы в чернилах, в кошеле — три су медью.", "By evening your fingers are inky and your purse holds three sous in copper.") }),
                Opt(t("Написать Ле Гро их грамоту", "Write the Le Gros their deed"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 1)),
                    ok = { money(6); feud(-1); addSecret(t("${h.name} ${sx(h, "написал", "написала")} Ле Гро подложную грамоту на луг", "${h.name} forged the Le Gros a deed to a meadow"), h); t("Ле Гро платят щедро и молчат. Пока.", "The Le Gros pay well and keep quiet. For now.") },
                    bad = { money(-3); glory(-3); t("Грамоту прочли на суде вслух — и узнали руку.", "The deed was read aloud in court, and the hand was recognized.") }),
                Opt(t("Отказать и предупредить хозяина луга", "Refuse and warn the meadow's owner"), roll = Roll(SER, 0, mapOf("chest" to 1)),
                    ok = { glory(2); feud(1); t("Хозяин луга обнимал вас при всех. Ле Гро плюют вам вслед.", "The meadow's owner embraced you in front of everyone. The Le Gros spit after you.") },
                    bad = { feud(2); t("Хозяин луга не поверил, а Ле Гро всё узнали.", "The meadow's owner did not believe you, and the Le Gros found out everything.") }),
            )
        }),
    Event("eu_kalek", "village", Tx("Корзинщик Ив", "Yves the Basket-Maker"), cond = { h -> fire(has(h, "kalek") && age(h) >= 16) },
        txt = { t("Калекой за плугом не походишь. Корзинщик Ив, сам без ноги, стучит костылём в вашу дверь: «Руки-то целы? Ивняка у реки на всех хватит».",
            "A cripple cannot walk behind a plow. Yves the basket-maker, one-legged himself, raps on your door with his crutch: “Your hands still work? There are willows enough by the river for everyone.”") },
        opts = { o ->
            listOf(
                Opt(t("Учиться плести", "Learn to weave baskets"), roll = Roll(UM, 1, mapOf("umn" to 2, "krot" to 1)),
                    ok = { money(4); deed(o.h, t(sx(o.h, "Плёл", "Плела") + " корзины на всю округу", "Wove baskets for the whole countryside")); t("К ярмарке у вас тридцать корзин, и ни одной кривой. Ив говорит, что ученик обогнал учителя.", "By fair time you have thirty baskets and not a crooked one among them. Yves says the pupil has outdone the master.") },
                    bad = { t("Прутья ломаются, корзины косые. Ив смеётся, но не зло.", "The rods snap and the baskets lean. Yves laughs, but not unkindly.") }),
                Opt(t("Сидеть на паперти", "Sit on the church steps"), fx = { money(2); glory(-1); t("Подают, но смотрят сверху вниз.", "People give, but they look down at you.") }),
                Opt(t("Держать счёт, пока пашут молодые", "Keep the accounts while the young plow"), fx = { val k = kidIn(14, 40).firstOrNull(); if (k != null) { k[SIL]++; t("${k.name} взял${sx(k, "", "а")} плуг, а вы — счётные палочки. Дом не пропал.", "${k.name} took the plow and you took the tally sticks. The house did not go under.") } else { money(-2); t("Пахать некому. Наняли батрака за два су.", "There is no one to plow. You hired a laborer for two sous.") } }),
            )
        }),
    Event("eu_touch", "church", Tx("Королевское касание", "The King's Touch"), cond = { h -> fire(has(h, "hvor") && age(h) >= 12) },
        txt = { t("Говорят, король исцеляет золотуху одним касанием: король касается, Бог исцеляет. Из всей округи хворые тянутся в Реймс.",
            "They say the king cures the scrofula with a touch: the king touches, God heals. From all over the countryside the sick are making their way to Reims.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Идти в Реймс", "Go to Reims"), cost = 3, tag = t("3 су", "3 sous"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)),
                    ok = { money(-3); h.tr.remove("hvor"); deed(h, t("Король коснулся " + sx(h, "его", "её"), "Touched by the king")); t("Король коснулся вашей шеи холодными пальцами и перекрестил. К осени хворь сошла.", "The king touched your neck with cold fingers and made the sign of the cross. By autumn the sickness had gone.") },
                    bad = { money(-3); t("Хворых было столько, что до короля вы не дошли. Вернулись с пустыми руками и сбитыми ногами.", "There were so many sick that you never reached the king. You came home empty-handed and footsore.") }),
                Opt(t("Пить травницыны отвары", "Drink the herbwoman's brews"), cost = 1, tag = t("1 су", "1 sou"), roll = Roll(ZDR, 1), ok = { money(-1); h[ZDR]++; t("Горько, зато к весне вы впервые за много лет дышите полной грудью.", "Bitter, but by spring you breathe deep for the first time in years.") }, bad = { money(-1); t("Горько и без толку.", "Bitter, and to no purpose.") }),
                Opt(t("Терпеть", "Bear it"), fx = { t("Терпели и раньше.", "You have borne it before.") }),
            )
        }),
    Event("eu_chariv", "night_int", Tx("Шаривари", "Charivari"), w = 3, cond = { h -> fire(has(h, "vdov") && married(h) && (h.wed ?: 0) >= g.year - 2) },
        txt = { o -> t("Не успели вы ввести в дом ${sx(o.h, "новую жену", "нового мужа")}, как под окнами грянул шаривари: деревенские парни бьют в котлы и сковороды, дудят в рога и горланят срамные песни. Так положено, когда вдовый человек снова венчается.",
            "No sooner had you brought a new ${sx(o.h, "wife", "husband")} into the house than a charivari broke out under the windows: the village lads beating pots and pans, blowing horns and bawling bawdy songs. That is the custom when a widowed soul marries again.") },
        opts = { o ->
            listOf(
                Opt(t("Дать парням на вино", "Give the lads money for wine"), cost = 2, tag = t("2 су", "2 sous"), fx = { money(-2); glory(1); t("Парни выпили за молодых, и шум переехал к соседям.", "The lads drank to the newlyweds, and the racket moved on to the neighbors.") }),
                Opt(t("Выйти с палкой", "Go out with a stick"), roll = Roll(SIL, 0, mapOf("gnev" to 1, "bog" to 2)),
                    ok = { money(1); t("Парни разбежались, а котёл бросили. В хозяйстве пригодится.", "The lads scattered and left a pot behind. It will come in handy.") },
                    bad = { glory(-1); t("Котлом досталось вам. Шаривари гремел неделю.", "You got the worst of the pot. The charivari raged for a week.") }),
                Opt(t("Терпеть: перебесятся", "Bear it: they'll tire"), fx = { glory(-1); t("Три ночи без сна. Потом вся деревня месяц напевала те песни.", "Three sleepless nights. Then the whole village hummed those songs for a month.") }),
            )
        }),
    Event("eu_song", "fair", Tx("Песня трувера", "The Trouvère's Song"), cond = { h -> fire((listOf(h) + kidIn(15, 30)).firstOrNull { has(it, "kras") && age(it) in 15..40 }) },
        txt = { o -> t("Бродячий трувер увидел ${o.xx.name} у колодца и сложил пастурель. Теперь её поют на всех ярмарках Шампани — с именем, с родинкой и с вашим двором.",
            "A wandering trouvère saw ${o.xx.name} at the well and made a pastourelle of it. Now it is sung at every fair in Champagne, with the name, the beauty mark, and your house.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Пусть поют", "Let them sing"), fx = {
                    glory(2)
                    if (x.id != g.head && x.spouse == null && age(x) >= 16 && chance(.5)) { val s = mkSpouse(x); t("Через месяц у ворот стоял сват из ${s.from}. ${x.name} и ${s.name} обвенчались к Михайлову дню.", "A month later a matchmaker from ${s.from} stood at the gate. ${x.name} and ${s.name} were wed by Michaelmas.") }
                    else t("По округе только и разговоров что о вашем дворе.", "The whole countryside talks of nothing but your house.")
                }),
                Opt(t("Заплатить, чтобы пел без имени", "Pay him to sing it without the name"), cost = 2, tag = t("2 су", "2 sous"), fx = { money(-2); glory(1); t("Трувер взял деньги и спел то же самое, только громче.", "The trouvère took the money and sang the same song, only louder.") }),
                Opt(t("Прогнать трувера", "Run the trouvère off"), fx = { glory(-1); t("Трувер сложил вторую песню — про вас. Она смешнее первой.", "The trouvère made a second song, about you. It is funnier than the first.") }),
            )
        }),
    Event("eu_wrestle", "fair", Tx("Баран в Провене", "The Ram at Provins"), cond = { h -> fire(has(h, "bog") && age(h) in 16..50) },
        txt = { t("На ярмарке в Провене борются за барана: кто положит кузнеца Арнуля, тот и уведёт. Арнуль третий год никому не уступает.",
            "At the Provins fair they wrestle for a ram: whoever throws Arnoul the smith leads it home. For three years nobody has bested Arnoul.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Выйти в круг", "Step into the ring"), roll = Roll(SIL, 1, mapOf("bog" to 1, "hrab" to 1)),
                    ok = { money(4); glory(2); deed(h, t(sx(h, "Положил", "Положила") + " кузнеца Арнуля в Провене", "Threw Arnoul the smith at Provins")); t("Арнуль лёг на лопатки под рёв всей ярмарки. Баран ваш, и пиво за счёт проигравшего.", "Arnoul went down flat on his back to the roar of the whole fair. The ram is yours, and the beer is on the loser.") },
                    bad = { glory(-1); h[ZDR]--; t("Арнуль бросил вас через бедро. Рёбра срастались до Троицы.", "Arnoul threw you over his hip. Your ribs knit by Whitsun.") }),
                Opt(t("Поставить на Арнуля", "Bet on Arnoul"), roll = Roll(HIT, 0), ok = { money(3); t("Арнуль положил бродячего силача из Фландрии. Вы в выигрыше.", "Arnoul threw a traveling strongman from Flanders. You come out ahead.") }, bad = { money(-2); t("Фламандец положил Арнуля. Ваши два су уехали во Фландрию.", "The Fleming threw Arnoul. Your two sous went off to Flanders.") }),
                Opt(t("Не позориться", "Spare yourself the shame"), fx = { t("Купили детям свистульку и ушли.", "You bought the children a whistle and went home.") }),
            )
        }),
    // ---- ages of the house
    Event("eu_retire", "interior", Tx("Хлеб до смерти", "Bread till Death"), cond = { h -> fire(if (age(h) >= 60) desc(h).firstOrNull { alive(it) && !it.monk && it.away == null && age(it) >= 18 } else null) },
        txt = { o -> t("Руки уже не держат плуг. Нотариус из города говорит, что по обычаю можно отдать двор — «с условием кормить, поить, одевать и давать место у очага до самой смерти». Глаза у всех на ${o.xx.name}.",
            "Your hands can no longer hold the plow. The notary from town says that by custom you may hand over the house “on condition of food, drink, clothing and a place by the hearth until death.” Every eye is on ${o.xx.name}.") },
        opts = { o ->
            val h = o.h
            val x = o.xx
            listOf(
                Opt(t("Купить корродий в аббатстве и доживать там", "Buy a corrody at the abbey and end your days there"), cost = 6, tag = t("6 су · [[вы выбираете наследника]]", "6 sous · [[you choose the heir]]"), fx = {
                    money(-6); h.monk = true
                    deed(h, t(sx(h, "Доживал", "Доживала") + " век в аббатстве на корродии", "Ended " + sx(h, "his", "her") + " days at the abbey on a corrody"))
                    log(t("${h.name} ${sx(h, "ушёл", "ушла")} доживать в аббатство.", "${h.name} went to end ${sx(h, "his", "her")} days at the abbey."), "big")
                    t("Келья с окном на сад, похлёбка дважды в день, колокол к вечерне. Дом теперь — на молодых.", "A cell with a window on the garden, soup twice a day, the bell for vespers. The house is the young ones' now.")
                }),
                Opt(t("Учить ${x.name} всему, что знаете", "Teach ${x.name} all you know"), fx = { x[UM]++; x[HIT]++; t("Вы сидите у очага и учите ${x.name} считать подать, чуять дождь и видеть ложь в глазах управляющего.", "You sit by the hearth teaching ${x.name} to reckon the dues, smell rain, and see the lie in a steward's eye.") }),
                Opt(t("Пока дышу — я тут хозяин", "While I breathe, I am master here"), fx = { glory(1); x.stones++; t("Молодые переглянулись и пошли пахать. Ворчат, но слушаются.", "The young ones exchanged looks and went off to plow. They grumble, but they obey.") }),
            )
        }),
    Event("eu_youth", "village", Tx("Аббат молодёжи", "The Abbot of Youth"), cond = { fire(kidIn(14, 18) { it.male }.firstOrNull()) },
        txt = { o -> val x = o.xx; t("На Новый год деревенские парни выбирают «аббата молодёжи» — заводилу шутовского братства. Выбрали ${x.name}. Теперь он водит ряженых по дворам и с каждого требует вина.",
            "At New Year the village lads choose an “abbot of youth” to lead their fool's brotherhood. They have chosen ${x.name}. Now he leads the mummers from house to house and demands wine from each.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Выставить бочонок", "Put out a keg"), cost = 2, tag = t("2 су", "2 sous"), fx = { money(-2); x[SER]++; glory(1); t("Ряженые прошли по деревне с песнями и вашим вином. ${x.name} вернулся счастливый и охрипший.", "The mummers went through the village singing, full of your wine. ${x.name} came home happy and hoarse.") }),
                Opt(t("Запретить", "Forbid it"), fx = { x.stones++; t("${x.name} не спорит, но весь праздник сидит у окна.", "${x.name} does not argue, but spends the whole feast sitting by the window.") }),
                Opt(t("Пусть возьмёт выкуп и с Ле Гро", "Let him take a toll from the Le Gros too"), roll = Roll(HIT, 0, mapOf("lzh" to 1)),
                    ok = { money(2); feud(1); t("Ле Гро выкатили ряженым бочонок, скрипя зубами.", "The Le Gros rolled out a keg for the mummers, gnashing their teeth.") },
                    bad = { feud(2); addTr(x, "shram"); t("Ле Гро спустили на ряженых собак. ${x.name} вернулся в разорванной маске и со шрамом.", "The Le Gros set the dogs on the mummers. ${x.name} came home with a torn mask and a scar.") }),
            )
        }),
    Event("eu_bell", "church", Tx("Полночный колокол", "The Midnight Bell"), cond = { val k = kidIn(6, 11); fire(if (k.isEmpty()) null else pick(k)) },
        txt = { o -> val x = o.xx; t("${x.name} на спор с ребятами Ле Гро ${sx(x, "залез", "залезла")} на колокольню и в полночь ударил${sx(x, "", "а")} в колокол. Полдеревни выбежало с вёдрами, думая, что пожар.",
            "On a dare from the Le Gros children, ${x.name} climbed the bell tower and rang the bell at midnight. Half the village ran out with buckets, thinking there was a fire.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Выпороть при всех", "Thrash the child before everyone"), fx = { glory(1); t("Деревня довольна, ${x.name} неделю ест стоя.", "The village is satisfied, and ${x.name} eats standing up for a week.") }),
                Opt(t("Свалить всё на Ле Гро", "Lay it all on the Le Gros"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 1)), ok = { feud(1); t("Пороли детей Ле Гро. ${x.name} молчит и сияет.", "It was the Le Gros children who got thrashed. ${x.name} says nothing and beams.") }, bad = { glory(-1); t("Кюре видел, кто держал верёвку.", "The curé saw who was holding the rope.") }),
                Opt(t("Отвести к кюре в звонари", "Take the child to the curé to learn the bells"), fx = { x[VER]++; t("Кюре давно искал звонаря. ${x.name} звонит к вечерне — и ни разу не сбил${sx(x, "ся", "ась")}.", "The curé had long needed a bell-ringer. ${x.name} rings for vespers now and has never once lost the beat.") }),
            )
        }),
    Event("eu_twins", "interior", Tx("Двойня", "Twins"), cond = { fire(kidIn(0, 0) { c -> bloodParent(c)?.let { p -> alive(p) && married(p) && age(p) < 50 } == true }.firstOrNull()?.takeIf { chance(.15) }) },
        txt = { o -> val x = o.xx; t("Повитуха выглянула из-за занавески: «Не одно! Ещё одно лезет!» После ${x.name} родился второй младенец. Соседки крестятся: двойня, мол, к беде, одного бы отдать в обитель.",
            "The midwife looked out from behind the curtain: “Not one! Another's coming!” After ${x.name} a second baby was born. The neighbor women cross themselves: twins mean trouble, they say; best give one to the cloister.") },
        opts = { o ->
            val x = o.xx
            fun twin(): Person? = bloodParent(x)?.let { birth(it) }
            listOf(
                Opt(t("Крестить обоих и закатить пир", "Baptize both and throw a feast"), cost = 3, tag = t("3 су", "3 sous"), fx = { money(-3); glory(2); val c = twin(); t("Крестили в один день, пели до утра. ${c?.name ?: t("Второй", "The second")} и ${x.name} спят в одной колыбели.", "Both baptized the same day, singing till morning. ${c?.name ?: t("Второй", "The second")} and ${x.name} sleep in one cradle.") }),
                Opt(t("Крестить обоих тихо", "Baptize both quietly"), fx = { val c = twin(); t("Окрестили без шума. ${c?.name ?: t("Второй", "The second")} кричит вдвое громче.", "Baptized without fuss. ${c?.name ?: t("Второй", "The second")} yells twice as loud.") }),
                Opt(t("Отдать второго в обитель", "Give the second to the cloister"), fx = { val c = twin(); if (c != null) { c.monk = true; deed(c, t("Отдан" + sx(c, "", "а") + " в обитель младенцем", "Given to the cloister as a baby")) }; glory(1); t("Монахини унесли младенца в корзине. Мать долго смотрела им вслед.", "The nuns carried the baby away in a basket. The mother watched them go for a long time.") }),
            )
        }),
    Event("eu_late", "interior", Tx("Поздний ребёнок", "A Late Child"), cond = { h -> fire(if (age(h) >= 40) kidsOf(h).firstOrNull { alive(it) && age(it) == 0 } else null) },
        txt = { o -> t("Вам ${yrs(age(o.h))}, а в колыбели снова кричат: ${o.xx.name}. Кумушки шепчутся, старшие дети не знают, смеяться или ревновать.",
            "You are ${age(o.h)} years old, and the cradle is crying again: ${o.xx.name}. The gossips whisper; the older children cannot decide whether to laugh or be jealous.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Крестины на всю деревню", "A christening for the whole village"), cost = 3, tag = t("3 су", "3 sous"), fx = { money(-3); glory(1); x[SER]++; t("Пили за ${x.name} три дня. Даже Ле Гро прислали каравай.", "They drank to ${x.name} for three days. Even the Le Gros sent a loaf.") }),
                Opt(t("Положить в колыбель отцовский нож", "Lay the family knife in the cradle"), fx = { x[SIL]++; t("Старый обычай: нож в колыбели — сила в руке. ${x.name} крепко держит палец.", "An old custom: a knife in the cradle, strength in the hand. ${x.name} grips a finger hard.") }),
                Opt(t("Позвать крёстным брата Ансельма", "Ask Brother Anselm to stand godfather"), fx = { x[UM]++; t("Брат Ансельм обещал выучить крестника грамоте. Обещание монаха — крепче печати.", "Brother Anselm promised to teach his godchild to read. A monk's promise is firmer than a seal.") }),
            )
        }),
    Event("eu_daughter", "interior", Tx("Дочь вернулась", "A Daughter Comes Home"), cond = { fire(kidIn(17, 45) { !it.male && married(it) }.firstOrNull()) },
        txt = { o -> t("${o.xx.name} пришла домой с синяком под глазом и узлом в руках: «Он пьёт и бьёт. Назад не пойду».", "${o.xx.name} has come home with a black eye and a bundle in her arms: “He drinks and he beats me. I'm not going back.”") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Оставить дома", "Keep her home"), tag = t("−2 су", "−2 sous"), fx = { money(-2); glory(-1); t("Муж приходил трижды, кюре — дважды. ${x.name} осталась и прядёт у окна.", "The husband came three times, the curé twice. ${x.name} stayed and spins by the window.") }),
                Opt(t("Поговорить с зятем по-свойски", "Have a word with the son-in-law"), roll = Roll(SIL, 0, mapOf("hrab" to 1, "gnev" to 1, "bog" to 2)),
                    ok = { glory(1); t("С тех пор зять трезв по воскресеньям и тих по будням.", "Since then the son-in-law is sober on Sundays and quiet on weekdays.") },
                    bad = { addTr(o.h, "shram"); t("Зять оказался крепче, чем думали. ${x.name} всё равно осталась дома.", "The son-in-law was tougher than you thought. ${x.name} stayed home all the same.") }),
                Opt(t("Просить у епископа раздельного житья", "Ask the bishop's court for a separation"), cost = 4, tag = t("4 су", "4 sous"), roll = Roll(UM, 0, mapOf("gram" to 2)),
                    ok = { money(-4); glory(1); t("Официал епископа разрешил раздельное житьё — «от стола и ложа». Зятю велено вернуть приданое.", "The bishop's official granted a separation “from bed and board.” The son-in-law was ordered to return the dowry.") },
                    bad = { money(-4); x.stones++; t("Официал велел жене покоряться мужу. ${x.name} вернулась к нему с сухими глазами.", "The official told the wife to obey her husband. ${x.name} went back to him dry-eyed.") }),
                Opt(t("Отвести назад: что Бог соединил…", "Take her back: what God has joined…"), sin = "cruel", fx = { x.stones += 2; t("${x.name} ушла, не оглядываясь. На Пасху она не пришла.", "${x.name} left without looking back. At Easter she did not come.") }),
            )
        }),
    Event("eu_brothers", "field", Tx("Полоса у ручья", "The Strip by the Brook"), cond = { h -> val s = kidsOf(h).filter { alive(it) && !it.monk && it.away == null && it.male && age(it) >= 18 }; fire(if (s.size >= 2) s[1] else null) },
        txt = { o -> val b = o.xx; val a = kidsOf(o.h).first { alive(it) && !it.monk && it.away == null && it.male && age(it) >= 18 }
            t("${a.name} и ${b.name} поделили отцову землю на словах, а теперь делят на кулаках: полоса у ручья досталась обоим, и каждый пашет её со своего конца.",
                "${a.name} and ${b.name} divided the family land with words, and now they are dividing it with fists: the strip by the brook went to both, and each plows it from his own end.") },
        opts = { o ->
            val b = o.xx
            listOf(
                Opt(t("Полосу — старшему, младшему — денег", "The strip to the elder, money to the younger"), cost = 4, tag = t("4 су", "4 sous"), fx = { money(-4); addTr(b, "gor"); t("${b.name} взял четыре су и ушёл в город, к шорнику. Ушёл без зла.", "${b.name} took four sous and went to town, to a saddler. He left without rancor.") }),
                Opt(t("Делить по жребию, соломинками", "Draw straws for it"), roll = Roll(UM, 0, mapOf("umn" to 1, "chest" to 1)),
                    ok = { glory(1); t("Соломинки рассудили, кюре благословил, братья выпили мировую.", "The straws decided, the curé blessed it, and the brothers drank to peace.") },
                    bad = { b.stones += 2; t("Соломинка короткая, а обида длинная. ${b.name} теперь не здоровается с братом.", "The straw was short and the grudge is long. ${b.name} no longer greets his brother.") }),
                Opt(t("Пусть решат сами", "Let them settle it themselves"), sin = "coward", fx = { addTr(b, "shram"); t("Решили у ручья, на кулаках. ${b.name} проиграл и носит шрам.", "They settled it by the brook with their fists. ${b.name} lost and carries a scar.") }),
            )
        }),
    // ---- chains off older events
    Event("eu_blow2", "road", Tx("Сын Ренье", "Renier's Son"), w = 3, cond = { fire(ripe("eu_blow", 10)) },
        txt = { t("На дороге путь вам загородил молодой сержант сеньора в новом плаще. «Я Бертен, сын Ренье. Отец до сих пор дышит ртом. Я теперь собираю подать с вашего конца».",
            "On the road a young sergeant of the seigneur's in a new cloak blocks your way. “I am Bertin, Renier's son. Father still breathes through his mouth. I collect the dues from your end of the village now.”") },
        opts = { o ->
            listOf(
                Opt(t("Предложить мировую и кувшин вина", "Offer peace and a jug of wine"), cost = 2, tag = t("2 су", "2 sous"), roll = Roll(SER, 0, mapOf("shch" to 1, "kras" to 1)),
                    ok = { money(-2); close("eu_blow"); glory(1); t("Бертен выпил, помолчал и сказал: «Отец и сам был не сахар». Счёт закрыт.", "Bertin drank, said nothing for a while, then: “Father was no honey cake either.” The account is closed.") },
                    bad = { money(-6); close("eu_blow"); t("Вино он выпил, а подать с вас теперь берёт самой строгой мерой.", "He drank the wine, and now takes your dues by the strictest measure.") }),
                Opt(t("Сказать, что вы его не боитесь", "Tell him you do not fear him"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1, "gnev" to 1)),
                    ok = { close("eu_blow"); glory(2); t("Бертен смерил вас взглядом и уехал. Больше он к вам не заворачивал.", "Bertin looked you up and down and rode off. He never turned in at your gate again.") },
                    bad = { close("eu_blow"); money(-4); addTr(o.h, "shram"); t("Бертен вернулся с двумя сержантами. Шрам на скуле останется вам на память.", "Bertin came back with two sergeants. The scar on your cheekbone will serve as a keepsake.") }),
                Opt(t("Поклониться и стерпеть", "Bow and bear it"), sin = "coward", fx = { close("eu_blow"); money(-4); t("Три года Бертен брал с вас лишнюю курицу с каждой подати. Потом ему надоело.", "For three years Bertin took an extra hen with every due. Then he tired of it.") }),
            )
        }),
    Event("eu_rout2", "road", Tx("Старый знакомый", "An Old Acquaintance"), w = 3, cond = { fire(ripe("eu_rout_hid", 6) || ripe("eu_rout_sold", 6)) },
        txt = { if (flagged("eu_rout_hid")) t("По дороге идёт рота под чужим знаменем. Впереди на сером коне — тот самый наёмник, которого ваш двор прятал на сеновале. Теперь он капитан.",
            "A company is coming down the road under a foreign banner. At its head on a grey horse rides the very mercenary your house once hid in the hayloft. He is a captain now.")
            else t("У ворот трое верховых. «Наш брат висел на перекрёстке по вашей милости», — говорит старший. Это братья выданного вами наёмника.", "Three riders at the gate. “Our brother hung at the crossroads thanks to you,” says the eldest. They are the brothers of the mercenary your house gave up.") },
        opts = { o ->
            fun done() { close("eu_rout_hid"); close("eu_rout_sold") }
            if (flagged("eu_rout_hid")) listOf(
                Opt(t("Выйти навстречу", "Go out to meet him"), fx = { done(); money(6); glory(2); t("Капитан узнал ваш двор, спешился и поклонился. Роту увёл другой дорогой, а на пороге оставил кошель.", "The captain knew your house, dismounted and bowed. He led his company off by another road and left a purse on the threshold.") }),
                Opt(t("Спрятаться на всякий случай", "Hide, just in case"), fx = { done(); money(2); t("Рота прошла мимо. У ворот нашли мешок соли и связку колбас.", "The company went by. At the gate you found a sack of salt and a string of sausages.") }),
            ) else listOf(
                Opt(t("Откупиться", "Pay them off"), cost = 8, tag = t("8 су", "8 sous"), fx = { done(); money(-8); t("Серебро братья взяли, а на прощание плюнули в колодец.", "The brothers took the silver and spat in your well on the way out.") }),
                Opt(t("Драться", "Fight"), roll = Roll(SIL, -1, mapOf("hrab" to 2, "bog" to 1)),
                    ok = { done(); glory(2); t("Соседи прибежали с вилами. Братья ускакали и больше не возвращались.", "The neighbors came running with pitchforks. The brothers galloped off and never came back.") },
                    bad = { done(); money(-part(.3)); addTr(o.h, "shram"); t("Братья сожгли сарай и оставили вам шрам — по одному на брата, сказали они.", "The brothers burned the shed and left you a scar: one for each brother, they said.") }),
                Opt(t("Бежать к сеньору", "Run to the seigneur"), roll = Roll(HIT, 0, mapOf("trus" to 1)),
                    ok = { done(); t("Сержанты сеньора выехали навстречу. Братья решили не связываться.", "The seigneur's sergeants rode out. The brothers decided it was not worth it.") },
                    bad = { done(); money(-5); t("Пока бегали, братья увели корову.", "While you were running, the brothers led off the cow.") }),
            )
        }),
    Event("eu_grain2", "barin", Tx("Амбарные книги", "The Granary Rolls"), w = 3, cond = { fire(ripe("eu_grain", 4)) },
        txt = { t("Новый управляющий сеньора перечёл старые амбарные свитки: в голодный год пропало три мешка, а покойный сторож перед смертью назвал имя вашего двора.",
            "The seigneur's new steward has gone through the old granary rolls: in the lean year three sacks went missing, and the late watchman named your house before he died.") },
        opts = { o ->
            listOf(
                Opt(t("Вернуть втрое", "Pay it back threefold"), cost = 6, tag = t("6 су", "6 sous"), fx = { close("eu_grain"); money(-6); glory(-1); t("Управляющий записал: «уплачено». Деревня — запомнила.", "The steward wrote “paid.” The village remembered.") }),
                Opt(t("Отпираться", "Deny it"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 2)),
                    ok = { close("eu_grain"); t("Мёртвый сторож — плохой свидетель. Дело закрыли.", "A dead watchman makes a poor witness. The case was closed.") },
                    bad = { close("eu_grain"); money(-5); glory(-2); t("Нашёлся живой свидетель. День у позорного столба и пеня.", "A living witness turned up. A day in the pillory and a fine.") }),
                Opt(t("Сказать как было: дети пухли с голоду", "Tell it as it was: the children were swelling with hunger"), roll = Roll(SER, 0, mapOf("chest" to 2, "dobr" to 1)),
                    ok = { close("eu_grain"); money(-2); t("Управляющий помолчал и взял два су — для порядка.", "The steward was silent a while, then took two sous, for form's sake.") },
                    bad = { close("eu_grain"); money(-6); t("Управляющего голодом не разжалобишь. Пеня — шесть су.", "Hunger does not move a steward. The fine: six sous.") }),
            )
        }),
    Event("eu_widowson", "village", Tx("Сын Перонель", "Péronnelle's Son"), w = 3, cond = { fire(ripe("eu_peronnelle", 16)) },
        txt = { t("В деревню вернулся сын вдовы Перонель — рослый, в городском плаще, подмастерье у кожевника в Бове. На паперти он спрашивает при всех, чьи у него глаза.",
            "The widow Péronnelle's son has come back to the village: tall, in a town cloak, a journeyman to a tanner in Beauvais. On the church steps he asks, in front of everyone, whose eyes he has.") },
        opts = { o ->
            listOf(
                Opt(t("Признать родство, хоть и поздно", "Own the kinship, late as it is"), fx = { close("eu_peronnelle"); glory(-1); soothe(o.h); t("Вы отдали ему старый нож рода. Он ушёл в Бове, и теперь у вашего дома там свой человек.", "You gave him the family's old knife. He went back to Beauvais, and now your house has a man of its own there.") }),
                Opt(t("Дать денег на мастерство", "Give him money toward his mastership"), cost = 6, tag = t("6 су", "6 sous"), fx = { close("eu_peronnelle"); money(-6); glory(1); t("Через три года он стал мастером и первые сапоги прислал вам.", "Three years later he was a master, and he sent you his first pair of boots.") }),
                Opt(t("Отпереться ещё раз", "Deny it once more"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { close("eu_peronnelle"); t("Он постоял, плюнул на ваш порог и ушёл.", "He stood a while, spat on your doorstep, and left.") },
                    bad = { close("eu_peronnelle"); glory(-2); t("Он рассказал всё на паперти, и мать его подтвердила.", "He told it all on the church steps, and his mother bore him out.") }),
            )
        }),
    Event("eu_templar2", "night_int", Tx("Сундук тамплиера", "The Templar's Chest"), w = 3, cond = { fire(ripe("eu_templar", 5)) },
        txt = { t("Окованный сундук брата-сержанта который год стоит под соломой на сеновале. За ним никто не пришёл. Говорят, последних тамплиеров сожгли в Париже.",
            "The Templar sergeant's iron-bound chest has stood under the straw in the loft for years. Nobody has come for it. They say the last of the Templars were burned in Paris.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Взломать замок", "Break the lock"), fx = { close("eu_templar"); money(8); addRelic(t("Чаша тамплиеров", "Templar Chalice"), "reliq", mapOf(VER to 1), t("${h.name}: найдена в сундуке брата-сержанта", "${h.name}: found in the Templar sergeant's chest")); t("Внутри — плащ с красным крестом, восемь су и серебряная чаша. Чашу поставили к распятию. Вера +1 в жребиях.", "Inside: a cloak with a red cross, eight sous, and a silver chalice. The chalice went beside the crucifix. Faith +1 in draws.") }),
                Opt(t("Отнести нетронутым в аббатство", "Carry it unopened to the abbey"), fx = { close("eu_templar"); glory(2); soothe(h); t("Аббат сам открыл сундук, долго молчал и велел молиться за брата-сержанта.", "The abbot opened the chest himself, was silent a long time, and bade you pray for the sergeant.") }),
                Opt(t("Сдать людям короля", "Hand it to the king's men"), fx = { close("eu_templar"); money(3); t("Людям короля нужно всё тамплиерское. Дали три су и расписку, которую никто не прочтёт.", "The king's men want everything that belonged to the Templars. They gave you three sous and a receipt nobody can read.") }),
            )
        }),
    Event("eu_fairy2", "forest", Tx("Суббота", "Saturday"), w = 2, cond = { fire(ripe("eu_fairy", 6) && chance(.4)) },
        txt = { t("Хлеб на камне у источника исчезает каждую субботу, и в доме всё ладится. Но младшим не терпится узнать, кто его берёт. Сегодня суббота, и они уже крадутся к лесу.",
            "Every Saturday the bread on the stone by the spring disappears, and everything in the house goes well. But the little ones are itching to know who takes it. Today is Saturday, and they are already creeping toward the woods.") },
        opts = { o ->
            listOf(
                Opt(t("Вернуть детей и отлупить", "Fetch the children back and spank them"), fx = { flag("eu_fairy"); money(2); t("Дети ревут, хлеб исчез, как всегда. В амбаре почему-то прибавилось зерна.", "The children howl, the bread is gone as always. Somehow there is more grain in the barn.") }),
                Opt(t("Пойти и посмотреть самим", "Go and look yourself"), roll = Roll(VER, -1, mapOf("bogom" to 1, "hrab" to 1)),
                    ok = { close("eu_fairy"); g.bless = 1; t("У источника купалась женщина; от пояса вниз — змеиный хвост. Она взглянула без гнева, сказала «прощай» и ушла под воду. На камне осталась красная нитка: лишний белый боб в следующем жребии.", "A woman was bathing at the spring, a serpent's tail from the waist down. She looked at you without anger, said “farewell,” and sank beneath the water. On the stone she left a red thread: an extra white bean in the next draw.") },
                    bad = { close("eu_fairy"); money(-part(.2)); t("У источника никого. С той субботы хлеб на камне черствеет нетронутым, а в доме что ни день — то убыток.", "Nobody at the spring. Since that Saturday the bread on the stone goes stale untouched, and every day brings some new loss.") }),
            )
        }),
    Event("eu_lombard2", "fair", Tx("Ломбардец", "The Lombard"), w = 3, cond = { h -> fire(ripe("eu_lombard", 3) && age(h) >= 16) },
        txt = { t("Ломбардец Бонсиньоре, что когда-то жал вашему дому руку на ярмарке, заворачивает к вам по пути из Труа. Он ищет честного человека — скупать по деревням шерсть для его обозов.",
            "Bonsignore the Lombard, who once shook your house's hand at the fair, turns in at your gate on his way from Troyes. He is looking for an honest man to buy up wool in the villages for his wagon trains.") },
        opts = { o ->
            listOf(
                Opt(t("Скупать для него шерсть", "Buy wool for him"), roll = Roll(UM, 0, mapOf("chest" to 1, "gram" to 1, "umn" to 1)),
                    ok = { close("eu_lombard"); money(7); glory(1); t("Шерсть шла через ваш двор всё лето. Бонсиньоре расплатился флоринами и звал на будущий год.", "Wool passed through your yard all summer. Bonsignore paid in florins and asked you back next year.") },
                    bad = { close("eu_lombard"); money(-3); t("Половина шерсти отсырела в сарае. Бонсиньоре вычел убыток из вашей доли и уехал, не прощаясь.", "Half the wool went damp in the shed. Bonsignore took the loss out of your share and left without a goodbye.") }),
                Opt(t("Вложить серебро в его обоз", "Put silver into his wagon train"), cost = 6, tag = t("6 су", "6 sous"), roll = Roll(HIT, 0, mapOf("gor" to 1, "zhad" to 1)),
                    ok = { close("eu_lombard"); money(6); t("Обоз дошёл до Генуи и вернулся. Ваши шесть су стали двенадцатью.", "The train reached Genoa and came back. Your six sous became twelve.") },
                    bad = { close("eu_lombard"); money(-6); t("Обоз ограбили в горах под Лионом. Бонсиньоре прислал письмо с извинениями — дорогое, на пергаменте.", "The train was robbed in the mountains near Lyon. Bonsignore sent a letter of apology, an expensive one, on parchment.") }),
                Opt(t("Отказаться: от купцов одни хлопоты", "Decline: merchants mean trouble"), fx = { close("eu_lombard"); t("Бонсиньоре пожал плечами и поехал к Ле Гро.", "Bonsignore shrugged and drove on to the Le Gros.") }),
            )
        }),
    Event("eu_crone2", "road", Tx("Внучка старухи", "The Old Woman's Granddaughter"), w = 3, cond = { fire(ripe("eu_crone", 6)) },
        txt = { t("У ворот опять та старуха, которую ваш дом когда-то накормил. Она не постарела ни на день. С ней девочка лет десяти: «Внучка. Возьмите в дом — я ухожу далеко».",
            "The old woman your house once fed is at the gate again. She has not aged a day. With her is a girl of about ten: “My granddaughter. Take her into the house. I am going far away.”") },
        opts = { o ->
            listOf(
                Opt(t("Взять девочку", "Take the girl"), fx = { close("eu_crone"); g.bless = 1; glory(1); t("Утром старухи не было. Девочка знает травы, молчит и прядёт красную нитку. В следующем жребии будет лишний белый боб.", "In the morning the old woman was gone. The girl knows herbs, says little, and spins red thread. The next draw will hold an extra white bean.") }),
                Opt(t("Дать обеим хлеба в дорогу", "Give them both bread for the road"), fx = { close("eu_crone"); money(-1); t("Старуха взяла хлеб, погладила ворота ладонью и увела девочку.", "The old woman took the bread, stroked the gatepost with her palm, and led the girl away.") }),
                Opt(t("Прогнать: лишний рот", "Send them off: another mouth to feed"), sin = "cruel", fx = { close("eu_crone"); g.hex = g.year + 3; t("Старуха ничего не сказала. Только в мешке с бобами на три года прибавился второй красный.", "The old woman said nothing. Only, for three years a second red bean lies in the bag.") }),
            )
        }),
    Event("eu_meja2", "meja", Tx("Ещё три шага", "Three Paces More"), w = 3, cond = { fire(ripe("eu_meja", 4)) },
        txt = { t("Ле Гро вошли во вкус: межевой камень опять поехал — ещё на три шага. «Вы же тогда уступили», — ухмыляется старший Ле Гро.",
            "The Le Gros have got a taste for it: the boundary stone has moved again, three paces more. “You gave way last time,” grins the elder Le Gros.") },
        opts = { o ->
            listOf(
                Opt(t("Судиться за все пять шагов", "Go to court for all five paces"), roll = Roll(UM, 0, mapOf("gram" to 2)), ok = { close("eu_meja"); feud(1); glory(2); t("Бальи велел вернуть камень туда, где он стоял при дедах. Ле Гро скрипят зубами.", "The bailiff ordered the stone put back where it stood in your grandfathers' day. The Le Gros grind their teeth.") }, bad = { close("eu_meja"); money(-3); t("Бальи сказал, что раз уступили однажды — значит, согласны. Пеня за беспокойство.", "The bailiff said that having given way once, you had agreed. A fine for the bother.") }),
                Opt(t("Вкопать камень по пояс", "Sink the stone waist-deep"), roll = Roll(SIL, 0, mapOf("bog" to 2, "hrab" to 1)), ok = { close("eu_meja"); glory(1); t("Камень вкопали так, что четверо Ле Гро не сдвинули его и на палец.", "The stone went in so deep that four Le Gros could not shift it a finger's width.") }, bad = { close("eu_meja"); feud(1); addTr(o.h, "shram"); t("Ле Гро пришли посмотреть, как вы копаете. С лопатами.", "The Le Gros came to watch you dig. With shovels.") }),
                Opt(t("Уступить и это", "Give way again"), sin = "coward", fx = { close("eu_meja"); feud(-1); glory(-2); t("Мир, конечно, дороже. Но поле теперь короче на пять шагов, а над вами смеются и дети.", "Peace is worth more, of course. But the field is five paces shorter now, and even the children laugh at you.") }),
            )
        }),
    Event("eu_rebel2", "barin", Tx("Сеньор помнит", "The Seigneur Remembers"), w = 3, cond = { fire(ripe("eu_rebel", 10)) },
        txt = { t("Сын старого сеньора вырос и сам стал сеньором. Он помнит, как мужики выгнали отцова управляющего, и помнит, кто их вёл: ваш двор.",
            "The old seigneur's son has grown up and become seigneur himself. He remembers how the peasants drove out his father's steward, and he remembers who led them: your house.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Поднести дары к замку", "Bring gifts to the castle"), cost = 5, tag = t("5 су", "5 sous"), fx = { close("eu_rebel"); money(-5); t("Сеньор принял дары и не сказал ни слова. Но и не тронул.", "The seigneur accepted the gifts without a word. But he did not touch you.") }),
                Opt(t("Напомнить, что отец его уступил сам", "Remind him that his father gave way of his own will"), roll = Roll(UM, 0, mapOf("gram" to 1, "umn" to 1)), ok = { close("eu_rebel"); glory(1); t("«Отец был мягок, — сказал сеньор. — Но слово его — моё слово». И отпустил вас.", "“Father was soft,” said the seigneur. “But his word is my word.” And he let you go.") }, bad = { close("eu_rebel"); money(-6); t("Сеньор нашёл недоимки за десять лет. Все десять пришлось платить.", "The seigneur found ten years of arrears. All ten had to be paid.") }),
                Opt(t("Снова поднять деревню", "Raise the village again"), roll = Roll(SIL, -2, mapOf("hrab" to 2, "gnev" to 1, "bog" to 1)),
                    ok = { close("eu_rebel"); glory(3); deed(h, t(sx(h, "Второй раз поднял", "Второй раз подняла") + " деревню против сеньора", "Raised the village against the seigneur a second time")); t("Деревня встала стеной. Сеньор поглядел с башни и решил, что старые обычаи — хорошие обычаи.", "The village stood like a wall. The seigneur looked down from his tower and decided the old customs were good customs.") },
                    bad = { close("eu_rebel"); addTr(h, "kalek"); t("На этот раз сеньор был готов. Вас били у позорного столба, пока не перестали держать ноги.", "This time the seigneur was ready. You were beaten at the pillory until your legs would not hold you.") }),
            )
        }),
    Event("eu_town2", "village", Tx("Родня из города", "Kin from Town"), w = 3, cond = { fire(ripe("eu_town", 5) && !flagged("way_guild")) },
        txt = { t("Родич, что когда-то ушёл в город, приехал на Пасху в суконном плаще и с перстнем: «Я теперь при цехе ткачей. Перебирайтесь — замолвлю слово, взнос возьмут вполовину».",
            "The kinsman who once left for the town came home at Easter in a broadcloth cloak and a ring: “I'm with the weavers' guild now. Move to town; I'll put in a word, and they'll take half the fee.”") },
        opts = { o ->
            listOf(
                Opt(t("Перебраться в город", "Move to town"), cost = 5, tag = t("5 су · семья уходит в город", "5 sous · the family moves to town"), fx = {
                    close("eu_town"); money(-5); takeWay("way_guild"); addTr(o.h, "gor")
                    log(t("Семья ${g.stem} ушла в Провен, в цех ткачей.", "The ${g.stem} family moved to Provins and joined the weavers' guild."), "big")
                    t("Сеньор взял отступное, родич — пирог, цех — взнос. Над мастерской пахнет шерстью и щёлоком.", "The seigneur took his release money, the kinsman a pie, the guild its fee. Above the workshop it smells of wool and lye.")
                }),
                Opt(t("Взять у него денег в долг и остаться", "Borrow from him and stay"), roll = Roll(SER, 0, mapOf("kras" to 1, "shch" to 1)), ok = { close("eu_town"); money(5); t("Родич отсчитал пять су и не взял расписки.", "The kinsman counted out five sous and asked for no receipt.") }, bad = { close("eu_town"); t("Родич вдруг вспомнил, что торопится.", "The kinsman suddenly remembered he was in a hurry.") }),
                Opt(t("Остаться на земле", "Stay on the land"), fx = { close("eu_town"); t("Родич уехал обиженный. На Пасху он больше не приезжал.", "The kinsman left offended. He never came again at Easter.") }),
            )
        }),
    // ---- rare and strange
    Event("eu_guinefort", "forest", Tx("Роща святого Гинефорта", "Saint Guinefort's Grove"), cond = { val k = kidIn(0, 3); fire(if (k.isNotEmpty() && chance(.25)) k[0] else null) },
        txt = { o -> val x = o.xx; t("${x.name} чахнет. Соседки говорят: неси в рощу святого Гинефорта. Это пёс-борзая, что спас дитя от змеи, а хозяин сгоряча его зарубил. Ребёнка оставляют на ночь у собачьей могилы между двух свечей. Кюре зовёт это бесовщиной.",
            "${x.name} is wasting away. The neighbor women say: take the child to Saint Guinefort's grove. He was a greyhound who saved a baby from a snake, and his master cut him down in haste. The child is left overnight at the dog's grave between two candles. The curé calls it devilry.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Нести в рощу", "Carry the child to the grove"), roll = Roll(ZDR, 0, ofX = true),
                    ok = { x[ZDR] += 2; t("К утру свечи догорели, а ${x.name} впервые за месяц спал${sx(x, "", "а")} спокойно.", "By morning the candles had burned down, and ${x.name} had slept peacefully for the first time in a month.") },
                    bad = { addTr(x, "hvor"); t("Ночь была холодной, у колыбели — волчьи следы. ${x.name} жив${sx(x, "", "а")}, но хвор${sx(x, "", "а")} с той ночи.", "The night was cold, and there were wolf tracks by the cradle. ${x.name} lived, but has been sickly since that night.") }),
                Opt(t("Молиться в церкви", "Pray in church"), roll = Roll(VER, 0, mapOf("bogom" to 1)), ok = { x[ZDR]++; t("Кюре сам читал над ребёнком. Через неделю ${x.name} попросил${sx(x, "", "а")} каши.", "The curé himself read over the child. A week later ${x.name} asked for porridge.") }, bad = { t("Ребёнок всё такой же тихий и бледный.", "The child is as quiet and pale as ever.") }),
                Opt(t("Донести на рощу брату-доминиканцу", "Report the grove to the Dominican friar"), fx = { glory(-1); soothe(o.h); t("Монах велел вырубить рощу и выкопать собачьи кости. Соседки с вами больше не здороваются.", "The friar had the grove cut down and the dog's bones dug up. The neighbor women no longer greet you.") }),
            )
        }),
    Event("eu_ghost", "pyre", Tx("Женщина у перекрёстка", "The Woman at the Crossroads"), cond = { h -> fire(age(h) >= 16 && chance(.15)) },
        txt = { t("В ночь на Всех Святых у перекрёстка, где жгли травницу Гильеметту, видели женщину в обгорелом платье. Она стояла и смотрела на ваш дом.",
            "On All Saints' night, at the crossroads where Guillemette the herbwoman was burned, a woman was seen in a scorched dress. She stood there looking at your house.") },
        opts = { o ->
            listOf(
                Opt(t("Заказать мессу за её душу", "Pay for a Mass for her soul"), cost = 3, tag = t("3 су", "3 sous"), fx = { money(-3); g.bless = 1; t("Кюре удивился, но служил. Утром на перекрёстке нашли венок из полыни. В следующем жребии будет лишний белый боб.", "The curé was surprised, but he sang it. In the morning a wreath of wormwood was found at the crossroads. The next draw will hold an extra white bean.") }),
                Opt(t("Выйти к ней", "Go out to her"), roll = Roll(VER, -1, mapOf("hrab" to 2, "bogom" to 1)),
                    ok = { if (g.hex > g.year) g.hex = 0; glory(1); t("Она назвала вас по имени — и растаяла. С той ночи в доме тише, а если в мешке был второй красный боб, его больше нет.", "She called you by your name and faded away. Since that night the house is quieter, and if a second red bean was in the bag, it is gone.") },
                    bad = { g.hex = g.year + 4; t("Ноги приросли к земле, а утром в волосах у вас белела прядь. Второй красный боб — на четыре года.", "Your feet took root in the ground, and by morning there was a white streak in your hair. A second red bean for four years.") }),
                Opt(t("Закрыть ставни", "Close the shutters"), fx = { t("До рассвета никто не спал. Утром у перекрёстка — пусто.", "Nobody slept until dawn. In the morning the crossroads stood empty.") }),
            )
        }),
    Event("eu_garou", "forest", Tx("Оборотень", "The Werewolf"), cond = { h -> fire(age(h) >= 16 && chance(.2)) },
        txt = { t("В округе режут овец, и по деревне ползёт слух: оборотень. Пастух Ламбер божится, что на рассвете видел, как из леса вышел бондарь Жиль — босой и с шерстью в бороде.",
            "Sheep are being slaughtered all over, and a rumor creeps through the village: a werewolf. Lambert the shepherd swears that at dawn he saw Gilles the cooper come out of the forest, barefoot and with fur in his beard.") },
        opts = { o ->
            listOf(
                Opt(t("Пойти с соседями ловить «волка»", "Go hunting the “wolf” with the neighbors"), roll = Roll(SIL, 0, mapOf("hrab" to 1, "bog" to 1)),
                    ok = { money(2); glory(1); t("Поймали волка — самого обычного, серого и старого. Жиль-бондарь поставил вам бочонок.", "You caught a wolf, an ordinary one, grey and old. Gilles the cooper stood you a keg.") },
                    bad = { money(-2); t("В темноте подстрелили мельникову собаку. Платили всей облавой.", "In the dark you shot the miller's dog. The whole hunt paid for it.") }),
                Opt(t("Донести на Жиля", "Denounce Gilles"), sin = "cruel", fx = { money(2); glory(-1); t("Жиля держат в яме у бальи, а овец режут по-прежнему. За донос вам дали два су.", "Gilles is held in the bailiff's pit, and the sheep are still being killed. You were paid two sous for the denunciation.") }),
                Opt(t("Заступиться за Жиля", "Stand up for Gilles"), roll = Roll(SER, 0, mapOf("dobr" to 1, "chest" to 1)),
                    ok = { glory(2); t("Вы привели свидетелей: Жиль ходит босой, потому что пропил башмаки. Деревня посмеялась, и слух умер.", "You brought witnesses: Gilles goes barefoot because he drank away his shoes. The village laughed, and the rumor died.") },
                    bad = { glory(-1); t("Теперь шепчутся и про вас.", "Now they whisper about you too.") }),
            )
        }),
    Event("eu_comet", "dawn", Tx("Хвостатая звезда", "The Hairy Star"), cond = { fire(chance(.15)) },
        txt = { t("Третью ночь над лесом стоит хвостатая звезда. Кюре говорит — к смерти короля, кузнец — к войне, а бабка Ода — к урожаю на репу.",
            "For the third night a hairy star hangs over the forest. The curé says it means the king's death, the smith says war, and old Granny Ode says a good year for turnips.") },
        opts = { o ->
            listOf(
                Opt(t("Каяться и поститься", "Repent and fast"), fx = { o.h[VER]++; soothe(o.h); t("Постились до самой Пасхи. Звезда ушла, король жив.", "You fasted right through to Easter. The star went away; the king lives.") }),
                Opt(t("Сеять репу", "Sow turnips"), roll = Roll(UM, 1), ok = { money(5); t("Бабка Ода была права: репы столько, что кормили и свиней, и соседей.", "Granny Ode was right: so many turnips that you fed the pigs and the neighbors too.") }, bad = { money(-1); t("Репу съела медведка. Бабка Ода говорит, что звезду вы поняли неправильно.", "Mole crickets ate the turnips. Granny Ode says you read the star wrong.") }),
                Opt(t("Торговать оберегами от звезды", "Sell charms against the star"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)), ok = { money(4); t("Тряпичные ладанки разошлись за три дня.", "The rag amulets sold out in three days.") }, bad = { glory(-2); t("Кюре прочёл о вас проповедь. Без имени, но все поняли.", "The curé preached a sermon about you. No names, but everyone understood.") }),
            )
        }),
    Event("eu_fairy", "forest", Tx("Дама у источника", "The Lady of the Spring"), cond = { h -> fire(age(h) in 16..50 && fresh("eu_fairy") && chance(.15)) },
        txt = { t("У старого источника в лесу вы встретили даму нездешней красоты. Она просит хлеба на камне каждую субботу — и чтобы по субботам никто из вашего дома не смотрел на неё. Взамен обещает, что дом будет стоять крепко.",
            "By the old spring in the forest you met a lady of unearthly beauty. She asks for bread on the stone every Saturday, and that no one of your house look upon her on a Saturday. In return she promises that your house will stand firm.") },
        opts = { o ->
            listOf(
                Opt(t("Дать слово", "Give your word"), fx = { flag("eu_fairy"); money(4); t("Каждую субботу вы кладёте хлеб на камень. Хлеб исчезает, а в доме всё ладится.", "Every Saturday you lay bread on the stone. The bread vanishes, and everything in the house goes well.") }),
                Opt(t("Перекреститься и уйти", "Cross yourself and walk away"), fx = { t("Когда вы обернулись, у источника было пусто, только змейка скользнула в воду.", "When you looked back, the spring was empty; only a little snake slid into the water.") }),
                Opt(t("Позвать кюре окропить источник", "Have the curé sprinkle the spring"), fx = { glory(1); t("Кюре окропил источник святой водой. Вода в нём с тех пор горчит.", "The curé sprinkled the spring with holy water. Its water has tasted bitter ever since.") }),
            )
        }),
    Event("eu_hunt", "winter", Tx("Свита Эллекена", "Hellequin's Troop"), cond = { h -> fire(age(h) >= 14 && chance(.2) && g.people.values.any { it.blood && it.died != null && age(it) >= 16 }) },
        txt = { o -> val d = g.people.values.last { it.blood && it.died != null && age(it) >= 16 }
            t("Зимней ночью мимо вашего двора пронеслась свита Эллекена — мёртвые всадники, кони без подков, псы без голов. Среди них вы узнали ${d.name}, и ${sx(d, "он", "она")} помахал${sx(d, "", "а")} вам рукой.",
                "On a winter night Hellequin's troop swept past your house: dead riders, unshod horses, headless hounds. Among them you knew ${d.name}, who waved to you.") },
        opts = { o ->
            listOf(
                Opt(t("Окликнуть", "Call out"), roll = Roll(VER, -1, mapOf("bogom" to 1, "hrab" to 1)),
                    ok = { g.bless = 1; t("Голос из свиты ответил: «Отслужи за нас и не бойся бобов». Утром на снегу — следы без подков. Лишний белый боб в следующем жребии.", "A voice from the troop answered: “Have a Mass said for us, and fear no beans.” In the morning there were unshod tracks in the snow. An extra white bean in the next draw.") },
                    bad = { o.h[ZDR]--; t("Холод ударил в грудь. Голос пропал на месяц, а здоровье — насовсем.", "The cold struck you in the chest. Your voice was gone for a month, and some of your health for good.") }),
                Opt(t("Закрыть глаза и читать «Отче наш»", "Shut your eyes and say the Our Father"), fx = { t("Стук копыт стих за рекой. Утром на снегу — следы без подков.", "The hoofbeats faded beyond the river. In the morning there were unshod tracks in the snow.") }),
                Opt(t("Заказать мессы за всех мёртвых рода", "Pay for Masses for all the family's dead"), cost = 4, tag = t("4 су", "4 sous"), fx = { money(-4); glory(1); soothe(o.h); t("Кюре читал имена целый вечер. Свита больше не проезжала.", "The curé read the names all evening. The troop never rode by again.") }),
            )
        }),
    // ---- everyday
    Event("eu_hail", "field", Tx("Град", "Hail"), cond = { yes },
        txt = { pick(listOf(
            t("Град с голубиное яйцо за четверть часа побил рожь и виноград. Кюре бил в колокол — говорят, звон разгоняет тучи. Не разогнал.", "Hail the size of pigeon eggs flattened the rye and the vines in a quarter of an hour. The curé rang the bell; they say the sound breaks up clouds. It did not."),
            t("В самую жатву с запада пришла чёрная туча и вывалила на поле град. Снопы лежат в грязи, как побитые.", "Right at harvest a black cloud came out of the west and emptied hail onto the field. The sheaves lie in the mud like beaten men."),
        )) },
        opts = { o ->
            listOf(
                Opt(t("Пересеять яровыми", "Resow with spring grain"), cost = 2, tag = t("2 су", "2 sous"), roll = Roll(UM, 0, mapOf("umn" to 1)), ok = { money(2); t("Овёс взошёл густо. Убыток покрыт.", "The oats came up thick. The loss is covered.") }, bad = { money(-2); t("Лето было сухое. Семена пропали.", "The summer was dry. The seed was lost.") }),
                Opt(t("Просить сеньора скостить подать", "Ask the seigneur to lower the dues"), roll = Roll(SER, 0, mapOf("kras" to 1, "krot" to 1)), ok = { money(3); t("Сеньор скостил треть. Хоть так.", "The seigneur took off a third. Better than nothing.") }, bad = { money(-2); t("«Град не мой», — сказал сеньор.", "“I didn't send the hail,” said the seigneur.") }),
                Opt(t("Собрать, что осталось", "Gather what is left"), fx = { money(-2); t("Подобрали колосья до последнего. Зима будет постной.", "You gleaned to the last ear. It will be a lean winter.") }),
            )
        }),
    Event("eu_mill", "village", Tx("Ручные жернова", "The Hand-Mill"), cond = { h -> fire(age(h) >= 16) },
        txt = { t("Сеньор велит молоть только на его мельнице и печь только в его печи: с каждого мешка — шестнадцатая мера. А у вас в подполе дедовы ручные жернова.",
            "The seigneur commands that grain be ground only at his mill and bread baked only in his oven: a sixteenth of every sack. And under your floor lie your grandfather's hand-mill stones.") },
        opts = { o ->
            listOf(
                Opt(t("Молоть дома тайком", "Grind at home in secret"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 1)), ok = { money(3); t("Жернова скрипят по ночам под одеялом. Сыты, и мельник ни при чём.", "The stones creak at night under a blanket. You eat well, and the miller has no part of it.") },
                    bad = { money(-4); t("Пристав нашёл жернова и забрал. Говорят, их вмуруют в пол замкового двора, чтобы другим неповадно было.", "The bailiff's man found the stones and took them. They say they will be set into the castle courtyard floor as a warning to others.") }),
                Opt(t("Возить к сеньору, как велено", "Haul it to the seigneur as ordered"), fx = { money(-2); t("Мельник отмерил себе с горкой. Как всегда.", "The miller measured himself a heaped share. As always.") }),
                Opt(t("Сговориться с мельником", "Come to terms with the miller"), cost = 1, tag = t("1 су", "1 sou"), roll = Roll(SER, 0, mapOf("shch" to 1)), ok = { money(1); t("Мельник мелет ваше в первую очередь и отмеряет по-божески.", "The miller grinds yours first and measures fairly.") }, bad = { money(-2); t("Мельник взял су и отмерил с горкой всё равно.", "The miller took the sou and heaped his measure anyway.") }),
            )
        }),
    Event("eu_pig", "church", Tx("Суд над свиньёй", "The Pig on Trial"), cond = { h -> fire(age(h) >= 16) },
        txt = { t("Ваша свинья вырвалась из хлева, разорила огород Ле Гро и покусала их младшего. Ле Гро требуют суда — над свиньёй. В Фалезе, говорят, свинью за такое повесили в штанах и куртке.",
            "Your sow broke out of the sty, wrecked the Le Gros' garden and bit their youngest. The Le Gros demand a trial — of the sow. In Falaise, they say, a pig was hanged for such a thing, wearing breeches and a jacket.") },
        opts = { o ->
            listOf(
                Opt(t("Защищать свинью в суде", "Defend the sow in court"), roll = Roll(UM, 0, mapOf("gram" to 2, "umn" to 1)), ok = { feud(1); glory(1); t("Вы доказали, что калитку открыли сами Ле Гро. Свинью оправдали, Ле Гро в ярости.", "You proved the Le Gros opened the gate themselves. The sow was acquitted; the Le Gros are furious.") },
                    bad = { money(-3); glory(-1); t("Свинью повесили на площади при всей деревне, а вас обязали платить палачу.", "The sow was hanged in the square before the whole village, and you were made to pay the hangman.") }),
                Opt(t("Заколоть свинью и отнести Ле Гро окорок", "Slaughter the sow and take the Le Gros a ham"), fx = { money(-2); feud(-1); t("Ле Гро взяли окорок, младший уже бегает. Мир на свиных ногах.", "The Le Gros took the ham, and the youngest is up and running. A peace on pig's trotters.") }),
                Opt(t("Заплатить пеню и ничего не признавать", "Pay the fine and admit nothing"), cost = 3, tag = t("3 су", "3 sous"), fx = { money(-3); t("Свинья жива и, кажется, ни о чём не жалеет.", "The sow lives, and seems to regret nothing.") }),
            )
        }),
    Event("eu_may", "village", Tx("Майское дерево", "The May Tree"), cond = { fire(kidIn(15, 22) { !it.male && it.spouse == null }.firstOrNull()) },
        txt = { o -> t("В ночь на первое мая под окном ${o.xx.name} кто-то поставил берёзку в лентах. По обычаю это значит — жених. По лентам видно: сын Ле Гро.",
            "On May Eve someone set a ribboned birch under ${o.xx.name}'s window. By custom it means a suitor. By the ribbons, it is a Le Gros boy.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Позволить сватовство", "Allow the courtship"), fx = { val s = mkSpouse(x, land.rival.g); feud(-2); t("${x.name} и ${s.name} обвенчались к Иванову дню. Обе семьи стояли в одной церкви и косились друг на друга.", "${x.name} and ${s.name} were wed by Midsummer. Both families stood in one church and eyed each other sideways.") }),
                Opt(t("Срубить берёзку", "Cut down the birch"), fx = { feud(1); x.stones++; t("Берёзку срубили на рассвете. ${x.name} проплакала до Троицы.", "The birch was cut down at dawn. ${x.name} wept until Whitsun.") }),
                Opt(t("Выведать, кто ещё ставил деревца", "Find out who else set out trees"), roll = Roll(HIT, 0, mapOf("umn" to 1)), ok = { val s = mkSpouse(x); glory(1); t("Под окном стояли три деревца. Выбрали то, что от ${s.from}. ${x.name} и ${s.name} обвенчались осенью.", "Three trees had been set under the window. You chose the one from ${s.from}. ${x.name} and ${s.name} were wed in the autumn.") }, bad = { t("Ничего не выведали, а берёзка засохла сама.", "You learned nothing, and the birch withered on its own.") }),
            )
        }),
    // ---- the family's way: the weavers' guild, a bought freedom, the abbey's people
    Event("eu_guild", "village", Tx("Цех ткачей", "The Weavers' Guild"), cond = { h -> fire(!flagged("way_guild") && age(h) in 18..45 && h.away == null) },
        txt = { t("Ваш кум, мастер-ткач из Провена, шлёт весточку: цеху нужны руки. Если семья переберётся в город и внесёт вступительный взнос, глава станет подмастерьем, а дети — учениками. Сеньору надо заплатить отступное.",
            "Your old friend, a master weaver in Provins, sends word: the guild needs hands. If the family moves to town and pays the entry fee, the head will be a journeyman and the children apprentices. The seigneur must be paid to let you go.") },
        opts = { o ->
            listOf(
                Opt(t("Вступить в цех", "Join the guild"), cost = 10, tag = t("10 су · семья уходит в город", "10 sous · the family moves to town"), fx = {
                    money(-10); takeWay("way_guild"); addTr(o.h, "gor"); deed(o.h, t("Увёл семью в город, в цех ткачей", "Took the family to town, into the weavers' guild"))
                    log(t("Семья ${g.stem} ушла в Провен, в цех ткачей.", "The ${g.stem} family moved to Provins and joined the weavers' guild."), "big")
                    t("Две комнаты над мастерской, стук станков от рассвета до вечерни. Сеньор, побурчав, взял отступное.", "Two rooms above the workshop, the clatter of looms from dawn till vespers. The seigneur grumbled and took the release money.")
                }),
                Opt(t("Остаться на земле", "Stay on the land"), fx = { t("Земля кормит медленно, зато наверняка.", "The land feeds you slowly, but surely.") }),
            )
        }),
    Event("eu_g_master", "village", Tx("Шедевр", "The Masterpiece"), w = 2, cond = { h -> fire(if (flagged("way_guild")) kidIn(17, 35).firstOrNull() ?: h.takeIf { age(it) in 18..50 } else null) },
        txt = { o -> val x = o.xx; t("${x.name} который год в подмастерьях. Чтобы стать мастером, надо соткать шедевр — штуку сукна без единого изъяна — и выставить старшинам цеха угощение. Старшины, как на подбор, чьи-то сыновья и зятья.",
            "${x.name} has been a journeyman for years. To become a master one must weave a masterpiece, a bolt of cloth without a single flaw, and stand the guild wardens a feast. The wardens, to a man, are somebody's sons and sons-in-law.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Ткать честно, днём и ночью", "Weave honestly, day and night"), roll = Roll(UM, 0, mapOf("umn" to 1, "chest" to 1)),
                    ok = { money(4); glory(2); deed(x, t(sx(x, "Стал мастером", "Стала мастерицей") + " цеха ткачей", "Became a master of the weavers' guild")); t("Старшины щупали сукно, смотрели на свет — и не нашли изъяна. ${x.name} теперь ${sx(x, "мастер", "мастерица")}, со своим клеймом.", "The wardens felt the cloth and held it to the light, and found no flaw. ${x.name} is a master now, with a mark of ${sx(x, "his", "her")} own.") },
                    bad = { money(-2); t("Нашли узелок на третьем локте. Ещё год в подмастерьях.", "They found a knot in the third ell. Another year as a journeyman.") }),
                Opt(t("Задобрить старшин пиром", "Win the wardens over with a feast"), cost = 6, tag = t("6 су", "6 sous"), fx = { money(-6); glory(1); deed(x, t(sx(x, "Стал мастером", "Стала мастерицей") + " цеха ткачей", "Became a master of the weavers' guild")); t("Старшины ели три часа, а сукно смотрели три минуты. ${x.name} — мастер.", "The wardens ate for three hours and looked at the cloth for three minutes. ${x.name} is a master.") }),
                Opt(t("Не спешить", "No hurry"), fx = { t("Подмастерью тоже платят. Меньше, но платят.", "A journeyman is paid too. Less, but paid.") }),
            )
        }),
    Event("eu_g_wardens", "village", Tx("Цеховая печать", "The Guild Seal"), w = 2, cond = { fire(flagged("way_guild")) },
        txt = { t("Цеховые смотрители пришли с печатью и локтем: проверяют, не тянули ли вы сукно на раме, чтобы выгадать лишний локоть. Лишний локоть у вас есть.",
            "The guild inspectors have come with their seal and their ell-rod, to see whether you stretched your cloth on the frame to gain an extra ell. You have an extra ell.") },
        opts = { o ->
            listOf(
                Opt(t("Признаться и перемерить", "Own up and remeasure"), fx = { money(-2); glory(1); t("Пеня — два су, зато печать на сукне. Смотрители кивнули: честный мастер.", "A fine of two sous, but the seal is on the cloth. The inspectors nodded: an honest master.") }),
                Opt(t("Сунуть смотрителю серебро", "Slip the inspector some silver"), sin = "lie", cost = 2, tag = t("2 су", "2 sous"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)),
                    ok = { money(2); t("Смотритель не заметил ни локтя, ни серебра. Сукно ушло фламандцам втридорога.", "The inspector saw neither the ell nor the silver. The cloth went to the Flemings at three times the price.") },
                    bad = { money(-8); glory(-2); t("Смотритель взял серебро и всё равно донёс. Сукно сожгли на площади, а вас оштрафовали.", "The inspector took the silver and reported you anyway. The cloth was burned in the square and you were fined.") }),
                Opt(t("Спорить: так тянут все", "Argue: everyone stretches it"), roll = Roll(UM, -1, mapOf("gram" to 1)), ok = { t("Смотрители переглянулись и ушли к соседу.", "The inspectors exchanged a look and went on to the neighbor.") }, bad = { money(-4); t("«Все» не попались, а вы — да. Четыре су пени.", "“Everyone” wasn't caught, and you were. Four sous fine.") }),
            )
        }),
    Event("eu_g_revolt", "fire", Tx("Колокол ткачей", "The Weavers' Bell"), cond = { h -> fire(flagged("way_guild") && age(h) >= 16) },
        txt = { t("Мэр Провена велел работать от колокола до колокола — дольше прежнего, за ту же плату. Подмастерья бросили станки и бьют в набат, у ратуши уже дымится.",
            "The mayor of Provins has ordered work from bell to bell, longer than before, for the same pay. The journeymen have left their looms and are ringing the alarm; there is smoke by the town hall already.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Идти с подмастерьями", "March with the journeymen"), roll = Roll(SIL, -1, mapOf("hrab" to 2, "gnev" to 1)),
                    ok = { glory(2); t("Мэр сбежал из города, и колокол снова звонит по-старому.", "The mayor fled the town, and the bell rings at the old hours again.") },
                    bad = { unflag("way_guild"); money(-part(.5)); addTr(h, "shram"); log(t("Семья ${g.stem} изгнана из Провена и вернулась на землю.", "The ${g.stem} family was banished from Provins and went back to the land."), "big"); t("Пришли люди графа. Зачинщиков вешали, остальных гнали из города. Вы вернулись в деревню — без станков и без цеха.", "The count's men came. The ringleaders were hanged and the rest driven from the town. You went back to the village, without looms and without a guild.") }),
                Opt(t("Запереть мастерскую и ждать", "Bolt the workshop and wait"), fx = { money(-2); t("Неделю станки стояли. Потом всё как-то улеглось.", "For a week the looms stood still. Then it all settled down somehow.") }),
                Opt(t("Встать за мастеров и мэра", "Side with the masters and the mayor"), roll = Roll(HIT, 0, mapOf("zhad" to 1, "lzh" to 1)), ok = { money(5); glory(-1); t("Мастера не забыли, кто стоял с ними. Заказов прибавилось, друзей — нет.", "The masters did not forget who stood with them. More orders; no more friends.") },
                    bad = { money(-part(.3)); t("Подмастерья вспомнили, кто стоял с мэром. Мастерская сгорела первой.", "The journeymen remembered who stood with the mayor. Your workshop burned first.") }),
            )
        }),
    Event("eu_charter", "barin", Tx("Вольная грамота", "The Charter of Freedom"), cond = { h -> fire(!flagged("way_free") && !flagged("way_guild") && age(h) >= 18) },
        txt = { t("Сеньору нужны деньги: сын его попал в плен, а за рыцаря просят выкуп. Он продаёт вольные: свобода от права мёртвой руки и брачного сбора, можно уйти куда хочешь и жениться на ком хочешь. Подать за землю останется.",
            "The seigneur needs money: his son has been taken prisoner, and a knight's ransom is dear. He is selling charters of freedom: release from mortmain and from the marriage fine, the right to leave and to marry as you please. The rent on the land remains.") },
        opts = { o ->
            val h = o.h
            fun free() { takeWay("way_free"); deed(h, t("Купил" + sx(h, "", "а") + " роду вольную у сеньора", "Bought the family's freedom from the seigneur")); log(t("Семья ${g.stem} купила вольную грамоту.", "The ${g.stem} family bought a charter of freedom."), "big") }
            listOf(
                Opt(t("Купить вольную для двора", "Buy the household's freedom"), cost = 15, tag = t("15 су", "15 sous"), fx = { money(-15); free(); glory(2); t("Грамоту с печатью зелёного воска положили на дно сундука. Вы — свободные люди, и дети ваши тоже.", "The charter with its green wax seal went to the bottom of the chest. You are free folk, and your children too.") }),
                Opt(t("Скинуться всей деревней на общую грамоту", "Pool money with the whole village for one charter"), cost = 6, tag = t("6 су", "6 sous"), roll = Roll(SER, -1, mapOf("kras" to 1, "shch" to 1, "gram" to 1)),
                    ok = { money(-6); free(); glory(1); t("Деревня собрала серебро, и сеньор подписал одну грамоту на всех. Её читали на паперти вслух, и старики плакали.", "The village gathered the silver, and the seigneur signed one charter for all. It was read aloud on the church steps, and the old men wept.") },
                    bad = { money(-6); t("Серебро собрали, но сын сеньора бежал из плена сам. Сеньор передумал, а деньги вернул — не все.", "The silver was gathered, but the seigneur's son escaped on his own. The seigneur changed his mind and gave the money back — not all of it.") }),
                Opt(t("Не тратиться", "Keep your money"), fx = { t("И отцы жили без грамоты.", "Your fathers lived without a charter too.") }),
            )
        }),
    Event("eu_f_juror", "barin", Tx("Присяжный", "The Juror"), w = 2, cond = { h -> fire(flagged("way_free") && age(h) >= 21) },
        txt = { t("Вольного человека зовут в присяжные к королевскому бальи: судить спор двух деревень о выгоне. Одна из них — деревня Ле Гро.",
            "As a free man you are summoned as a juror to the king's bailiff, to judge a quarrel between two villages over a common pasture. One of them is the Le Gros' village.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Судить по правде", "Judge by the truth"), roll = Roll(UM, 0, mapOf("chest" to 1, "gram" to 1, "umn" to 1)), ok = { glory(2); t("Бальи похвалил вас при всех. Даже Ле Гро не нашли, к чему придраться.", "The bailiff praised you before everyone. Even the Le Gros found nothing to fault.") }, bad = { feud(1); t("Решили как надо, но Ле Гро уверены, что назло им.", "You judged rightly, but the Le Gros are sure you did it to spite them.") }),
                Opt(t("Судить против Ле Гро", "Judge against the Le Gros"), sin = "lie", fx = { feud(1); money(2); t("Выгон отошёл соседям Ле Гро, а те прислали вам бочонок.", "The pasture went to the Le Gros' neighbors, who sent you a keg.") }),
                Opt(t("Взять серебро у той стороны, что богаче", "Take silver from the richer side"), sin = "greed", roll = Roll(HIT, 0, mapOf("lzh" to 1, "zhad" to 1)),
                    ok = { money(6); addSecret(t("${h.name} ${sx(h, "брал", "брала")} мзду, сидя в присяжных", "${h.name} took bribes while sitting as a juror"), h); t("Шесть су за одно слово. Слово было сказано.", "Six sous for one word. The word was spoken.") },
                    bad = { money(-4); glory(-3); t("Другая сторона тоже заплатила — бальи. Вас выгнали из присяжных с позором.", "The other side paid too — the bailiff. You were thrown off the jury in disgrace.") }),
            )
        }),
    Event("eu_f_hotes", "forest", Tx("Новые земли", "New Lands"), w = 2, cond = { h -> fire(flagged("way_free") && g.dvor < 4 && age(h) in 18..50) },
        txt = { t("Аббат Сен-Дени зовёт вольных людей на новые земли за лесом: корчевать, ставить дома, а за это десять лет без податей. Крепостным туда нельзя. Вам — можно.",
            "The abbot of Saint-Denis is calling free folk to new lands beyond the forest: clear the trees, raise houses, and pay no dues for ten years. Serfs may not go. You may.") },
        opts = { o ->
            listOf(
                Opt(t("Идти корчевать", "Go and clear the land"), cost = 5, tag = t("5 су", "5 sous"), roll = Roll(ZDR, 1, mapOf("bog" to 1, "hrab" to 1)),
                    ok = { money(-5); g.dvor = minOf(4, g.dvor + 1); glory(1); t("Два года пней, мозолей и комаров — и на месте леса стоит ваш новый двор.", "Two years of stumps, blisters and gnats, and where the forest stood now stands your new house.") },
                    bad = { money(-5); o.h[ZDR]--; t("Лес не пустил: лихорадка с болота, пожар в вырубке. Вернулись на старое место.", "The forest would not let you in: marsh fever, a fire in the clearing. You went back to the old place.") }),
                Opt(t("Отправить туда сына", "Send a son there"), fx = { val s = kidIn(16, 30) { it.male }.firstOrNull(); if (s != null) { addTr(s, "hrab"); s[SIL]++; t("${s.name} ушёл корчевать и вернулся через год — крепче, злее и с собственной делянкой.", "${s.name} went off to clear land and came back a year later, tougher, harder, and with a plot of his own.") } else t("Посылать некого. Новые земли достались другим.", "There is no one to send. The new lands went to others.") }),
                Opt(t("Остаться", "Stay"), fx = { t("Своё поле — оно и есть своё.", "Your own field is your own field.") }),
            )
        }),
    Event("eu_f_challenge", "barin", Tx("Спор о грамоте", "The Charter Disputed"), cond = { fire(ripe("way_free", 8)) },
        txt = { t("Молодой сеньор вернулся с войны и говорит, что отец его продал вольные спьяну, а печать — не его. Он требует грамоту на суд и вас — обратно в вилланы.",
            "The young seigneur has come home from the war and says his father sold the charters in his cups and that the seal is not his. He demands the charter be brought to court, and you back into villeinage.") },
        opts = { o ->
            fun lose() { unflag("way_free"); log(t("Семья ${g.stem} снова в вилланах.", "The ${g.stem} family are villeins again."), "big") }
            listOf(
                Opt(t("Нести грамоту к королевскому бальи", "Take the charter to the king's bailiff"), cost = 3, tag = t("3 су", "3 sous"), roll = Roll(UM, 0, mapOf("gram" to 2, "umn" to 1)),
                    ok = { money(-3); flag("way_free", g.year + 30); glory(2); t("Бальи сличил печать и подтвердил: грамота настоящая. Молодой сеньор уехал, хлопнув воротами.", "The bailiff compared the seal and confirmed: the charter is genuine. The young seigneur rode off, slamming the gates.") },
                    bad = { money(-3); lose(); t("Суд решил в пользу сеньора. Грамоту бросили в огонь у вас на глазах.", "The court found for the seigneur. The charter was thrown into the fire before your eyes.") }),
                Opt(t("Заплатить ещё раз", "Pay again"), cost = 8, tag = t("8 су", "8 sous"), fx = { money(-8); flag("way_free", g.year + 30); t("Молодой сеньор взял деньги и приложил к грамоте свою печать рядом с отцовской.", "The young seigneur took the money and set his own seal on the charter beside his father's.") }),
                Opt(t("Спрятать грамоту и тянуть время", "Hide the charter and play for time"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "trus" to 1)),
                    ok = { flag("way_free", g.year + 30); t("Сеньор снова ушёл на войну и, говорят, там и остался. Грамота лежит в сундуке.", "The seigneur went off to the war again and, they say, stayed there for good. The charter lies in the chest.") },
                    bad = { lose(); money(-2); t("Сундук перерыли, грамоту нашли. Вы снова вилланы.", "They turned the chest out and found the charter. You are villeins again.") }),
            )
        }),
    Event("eu_abbey", "church", Tx("Постриг", "The Tonsure"), cond = { fire(if (flagged("way_abbey")) null else kidIn(10, 16) { it.male }.firstOrNull()) },
        txt = { o -> val x = o.xx; t("Аббат Сен-Медара заметил ${x.name}: смышлён, поёт чисто. Отдадите его на постриг — обитель возьмёт ваш двор под свою руку: земля аббатства, суд аббата, хлеб в голодный год.",
            "The abbot of Saint-Médard has noticed ${x.name}: quick, and sings true. Give him to the tonsure, and the abbey will take your house under its hand: abbey land, the abbot's court, bread in a lean year.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Отдать на постриг", "Give him to the tonsure"), tag = t("[[${x.name} уходит из рода]]", "[[${x.name} leaves the line]]"), fx = {
                    x.monk = true; takeWay("way_abbey"); addTr(x, "gram"); glory(2)
                    deed(x, t("Принял постриг в Сен-Медаре", "Took the tonsure at Saint-Médard"))
                    log(t("${x.name} принял постриг. Двор ${g.stem} перешёл под руку аббатства.", "${x.name} took the tonsure. The ${g.stem} house came under the abbey's hand."), "big")
                    t("${x.name} выбрили макушку и надели рясу. Мать плакала, а аббат сам благословил ваш порог.", "They shaved ${x.name}'s crown and put him in a habit. His mother wept, and the abbot himself blessed your threshold.")
                }),
                Opt(t("Отдать в школу при аббатстве, без пострига", "Send him to the abbey school, without the tonsure"), cost = 3, tag = t("3 су", "3 sous"), fx = { money(-3); addTr(x, "gram"); t("${x.name} учит псалмы и латынь, а на ночь приходит домой.", "${x.name} learns the psalms and Latin, and comes home at night.") }),
                Opt(t("Не отдавать", "Keep him"), fx = { t("Аббат вздохнул и выбрал сына Ле Гро.", "The abbot sighed and chose a Le Gros boy instead.") }),
            )
        }),
    Event("eu_a_granary", "barin", Tx("Житница аббатства", "The Abbey Granary"), w = 2, cond = { h -> fire(flagged("way_abbey") && age(h) >= 18) },
        txt = { t("Келарь аббатства приехал за десятиной — и с предложением: аббат хочет поставить вас старостой над житницей. Счёт, ключи и все соседские мешки.",
            "The abbey's cellarer has come for the tithe, and with an offer: the abbot wants to make you keeper of the granary. The tallies, the keys, and every neighbor's sack.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Стать старостой и считать честно", "Take it and keep honest tallies"), roll = Roll(UM, 0, mapOf("gram" to 2, "chest" to 1)), ok = { money(4); glory(1); t("Счёт сходится до зерна. Келарь хвалит вас аббату.", "The tallies match to the grain. The cellarer praises you to the abbot.") }, bad = { money(-3); t("Мыши, сырость и ваша путаница в палочках. Недостачу покрыли из своих.", "Mice, damp, and your muddle with the tally sticks. You made up the shortfall from your own.") }),
                Opt(t("Отсыпать себе понемногу", "Take a little for yourself"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { money(6); addSecret(t("${h.name} ${sx(h, "воровал", "воровала")} из житницы аббатства", "${h.name} stole from the abbey granary"), h); t("По горсти с мешка — никто не заметит. Никто и не заметил.", "A handful from each sack; no one will notice. No one did.") },
                    bad = { unflag("way_abbey"); money(-4); glory(-2); log(t("Аббатство отказало дому ${g.stem} в покровительстве.", "The abbey withdrew its protection from the ${g.stem} house."), "big"); t("Келарь пересчитал. Аббат отнял у вас ключи и свою руку.", "The cellarer counted again. The abbot took back his keys and his hand.") }),
                Opt(t("Отказаться: хлопотно", "Decline: too much trouble"), fx = { t("Келарь пожал плечами. Старостой стал мельник.", "The cellarer shrugged. The miller got the keys.") }),
            )
        }),
    Event("eu_a_letter", "church", Tx("Письмо из обители", "A Letter from the Cloister"), w = 2, cond = { fire(if (flagged("way_abbey")) g.people.values.firstOrNull { it.blood && it.monk && alive(it) && age(it) >= 16 } else null) },
        txt = { o -> t("Брат ${o.xx.name} пишет из аббатства: аббат хочет отправить его в Парижский университет изучать декреталии. Нужны деньги на книги и на свечи.",
            "Brother ${o.xx.name} writes from the abbey: the abbot wants to send him to the University of Paris to study canon law. Money is needed for books and candles.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Послать денег", "Send money"), cost = 5, tag = t("5 су", "5 sous"), fx = { money(-5); glory(3); x[UM] += 2; deed(x, t("Учился в Париже, стал каноником", "Studied in Paris, became a canon")); t("Через годы брат ${x.name} стал каноником в Нуайоне. Имя рода теперь знают и в капитуле.", "Years later Brother ${x.name} became a canon at Noyon. The family's name is known in the chapter now.") }),
                Opt(t("Просить, чтобы учил грамоте племянников", "Ask him to teach the nephews to read"), fx = { val k = kidIn(7, 16) { !has(it, "gram") }.firstOrNull(); if (k != null) { addTr(k, "gram"); t("Брат ${x.name} приходил по воскресеньям с восковой табличкой. ${k.name} читает.", "Brother ${x.name} came on Sundays with a wax tablet. ${k.name} can read.") } else t("Брат обещал — когда будет кого учить.", "The brother promised, once there is someone to teach.") }),
                Opt(t("Ответить, что денег нет", "Write back that there is no money"), fx = { t("Брат ${x.name} будет переписывать псалтири в скриптории. Тоже дело.", "Brother ${x.name} will copy psalters in the scriptorium. That is work too.") }),
            )
        }),
    Event("eu_a_bread", "church", Tx("Хлеб святого Медара", "Saint Médard's Bread"), w = 2, cond = { fire(flagged("way_abbey")) },
        txt = { t("Весна выдалась голодной. У ворот аббатства раздают хлеб — но только людям святого Медара. Вы теперь из них, а Ле Гро — нет.",
            "A hungry spring. At the abbey gate they are giving out bread, but only to Saint Médard's own people. You are among them now; the Le Gros are not.") },
        opts = { o ->
            listOf(
                Opt(t("Взять свою долю", "Take your share"), fx = { money(3); t("Хлеб чёрный, с отрубями, но его вдоволь.", "Black bread full of bran, but plenty of it.") }),
                Opt(t("Поделиться с Ле Гро", "Share it with the Le Gros"), fx = { money(1); feud(-1); glory(1); t("Старый Ле Гро взял каравай и долго не знал, что сказать. Сказал «спасибо».", "Old Le Gros took the loaf and for a long time did not know what to say. He said “thank you.”") }),
                Opt(t("Продать хлеб в городе", "Sell the bread in town"), sin = "greed", fx = { money(5); glory(-1); t("Городские платят за хлеб серебром. Монахи видели, кто вёз.", "Townsfolk pay for bread in silver. The monks saw who drove the cart.") }),
            )
        }),
)

internal val europeMoreHistory: List<Event> = listOf(
    Event("albi", "war", Tx("Поход на альбигойцев", "The Albigensian Crusade"), y0 = 1209, y1 = 1211, p = .6, cond = { h -> fire(age(h) in 18..45) },
        txt = { t("Папа объявил крестовый поход на еретиков юга. Сеньор идёт за Симоном де Монфором на сорок дней службы: за них обещано отпущение грехов и добыча южных городов.",
            "The Pope has proclaimed a crusade against the heretics of the south. The seigneur is following Simon de Montfort for forty days' service: for them, the forgiveness of sins and the spoils of southern towns are promised.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Идти на сорок дней", "Go for the forty days"), roll = Roll(SIL, -1, mapOf("hrab" to 2, "bog" to 1)),
                    ok = { money(5); glory(1); deed(h, t(sx(h, "Ходил", "Ходила") + " на юг с Монфором", "Went south with Montfort")); t("Сорок дней под стенами Каркассона. Вернулись с добычей и с тем, чего детям не расскажешь.", "Forty days under the walls of Carcassonne. You came home with plunder, and with things you will not tell the children.") },
                    bad = { if (chance(.3)) { kill(h, t(sx(h, "умер", "умерла") + " на юге от лихорадки", "died of fever in the south")); t("Южное солнце и болотная лихорадка сделали то, чего не смогли еретики.", "The southern sun and the marsh fever did what the heretics could not.") } else { addTr(h, "shram"); t("Камень со стены Тулузы оставил вам шрам и отбил охоту воевать.", "A stone from the walls of Toulouse left you a scar and cured you of any taste for war.") } }),
                Opt(t("Дать денег на поход", "Give money for the crusade"), cost = 4, tag = t("4 су", "4 sous"), fx = { money(-4); glory(1); t("Кюре записал ваше имя в книгу жертвователей.", "The curé wrote your name in the book of donors.") }),
                Opt(t("Остаться: у южан тот же Бог", "Stay: the southerners have the same God"), fx = { o.h[VER]--; t("Кюре нахмурился. А потом пришли вести из Безье, и он перестал хмуриться и долго молился один.", "The curé frowned. Then the news came from Béziers, and he stopped frowning and prayed alone a long while.") }),
            )
        }),
    Event("past", "road", Tx("Пастушки", "The Shepherds"), y0 = 1251, y1 = 1251, p = .8, cond = { fire(kidIn(14, 22).firstOrNull()) },
        txt = { o -> t("По дорогам идут тысячи пастухов и бедняков. Ведёт их «Венгерский учитель»: Богородица велела простым людям освободить короля Людовика из плена. ${o.xx.name} смотрит им вслед и уже держит котомку.",
            "Thousands of shepherds and poor folk are on the roads. The “Master of Hungary” leads them: Our Lady has bidden the simple folk to free King Louis from captivity. ${o.xx.name} watches them go, bundle already in hand.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Отпустить", "Let ${sx(x, "him", "her")} go"), fx = { x.away = g.year + 2; if (chance(.35)) x.awayDie = g.year + 1; deed(x, t(sx(x, "Ушёл", "Ушла") + " с пастушками", "Went with the Shepherds")); t("${x.name} ушл${sx(x, "", "а")} с толпой на Орлеан. Потом пришли вести: в городах громили клириков и евреев, а под Буржем «учителя» убили и толпу разогнали.", "${x.name} went off with the crowd toward Orléans. Then the news came: in the towns they attacked clergy and Jews, and near Bourges the “Master” was killed and the crowd scattered.") }),
                Opt(t("Отговорить", "Talk ${sx(x, "him", "her")} out of it"), roll = Roll(SER, 0, mapOf("umn" to 1, "krot" to 1)), ok = { t("${x.name} постоял${sx(x, "", "а")} у дороги до вечера и вернулся в дом. Через месяц все узнали, чем кончилось.", "${x.name} stood by the road till evening and came back inside. A month later everyone heard how it ended.") }, bad = { x.away = g.year + 2; t("Ночью ${x.name} ушл${sx(x, "", "а")} без спроса.", "In the night ${x.name} slipped away without leave.") }),
                Opt(t("Запереть в хлеву до осени", "Lock ${sx(x, "him", "her")} in the byre till autumn"), fx = { x.stones++; t("${x.name} кричал${sx(x, "", "а")} неделю, потом затих${sx(x, "", "ла")}. Простит не скоро.", "${x.name} shouted for a week, then fell quiet. It will be a long time before you are forgiven.") }),
            )
        }),
    Event("templ", "night_int", Tx("Тамплиеры", "The Templars"), y0 = 1307, y1 = 1308, p = .8, cond = { h -> fire(age(h) >= 16) },
        txt = { t("В пятницу, тринадцатого октября, люди короля разом взяли под стражу всех тамплиеров Франции. Ночью к вам стучится брат-сержант из соседнего командорства и просит спрятать окованный сундук.",
            "On Friday the thirteenth of October the king's men arrested every Templar in France at once. In the night a sergeant-brother from the nearby commandery knocks at your door and asks you to hide an iron-bound chest.") },
        opts = { o ->
            listOf(
                Opt(t("Спрятать сундук", "Hide the chest"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "dobr" to 1)),
                    ok = { flag("eu_templar"); t("Сундук ушёл под солому. Брат поклонился и исчез в темноте — сказал, вернётся.", "The chest went under the straw. The brother bowed and vanished into the dark; he said he would come back.") },
                    bad = { money(-4); glory(-2); t("Люди короля нашли сундук наутро. Вас допрашивали два дня, сундук увезли.", "The king's men found the chest in the morning. You were questioned for two days, and the chest was taken away.") }),
                Opt(t("Выдать брата людям короля", "Hand the brother over to the king's men"), sin = "cruel", fx = { money(3); t("Его увезли в Париж. Говорят, там он под пыткой признал всё, что велели.", "He was taken to Paris. They say that there, under torture, he confessed to everything he was told to.") }),
                Opt(t("Не открывать дверь", "Don't open the door"), fx = { t("Стук стих. Утром на снегу — следы, уходящие к лесу.", "The knocking stopped. In the morning there were footprints in the frost, leading to the forest.") }),
            )
        }),
    Event("flagel", "road", Tx("Бичующиеся", "The Flagellants"), y0 = 1349, y1 = 1350, p = .8, cond = { h -> fire(age(h) >= 14) },
        txt = { t("В деревню вошла процессия: сотни людей в белом, с красными крестами, бичуют себя до крови и поют. Говорят, тридцать три дня и восемь часов покаяния отвратят мор.",
            "A procession has come into the village: hundreds of people in white with red crosses, whipping themselves bloody and singing. They say thirty-three days and eight hours of penance will turn away the pestilence.") },
        opts = { o ->
            listOf(
                Opt(t("Пристать к ним", "Join them"), roll = Roll(ZDR, -1, mapOf("bogom" to 1, "krot" to 1)), ok = { o.h.stones = 0; glory(1); t("Тридцать три дня под бичом. Спина в рубцах, а на душе — ни камешка.", "Thirty-three days under the lash. Your back is welted, and not a pebble is left on your soul.") }, bad = { addTr(o.h, "shram"); o.h[ZDR]--; t("Раны загноились. Папа тем временем объявил бичующихся еретиками.", "The wounds festered. Meanwhile the Pope declared the flagellants heretics.") }),
                Opt(t("Накормить их", "Feed them"), cost = 2, tag = t("2 су", "2 sous"), fx = { money(-2); glory(1); t("Ели молча, а уходя благословили ваш порог.", "They ate in silence, and blessed your threshold as they left.") }),
                Opt(t("Запереть ворота", "Bar the gate"), fx = { t("Процессия прошла мимо с пением. Кюре сказал, что так и надо.", "The procession went by singing. The curé said that was the right thing.") }),
            )
        }),
    Event("chev", "fire", Tx("Шевоше", "The Chevauchée"), y0 = 1355, y1 = 1359, p = .7, cond = { h -> fire(age(h) >= 16) },
        txt = { t("Английские всадники идут шевоше — набегом по стране: жгут деревни и угоняют скот, чтобы разорить французского короля. Дым уже за рекой.",
            "English horsemen are riding a chevauchée, a raid through the country: burning villages and driving off cattle to ruin the French king. The smoke is already across the river.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Угнать скот в лес", "Drive the cattle into the forest"), roll = Roll(HIT, 0, mapOf("umn" to 1, "trus" to 1)), ok = { money(-1); t("Скот пересидел в овраге. Дом подпалили, но крышу перекрыли за неделю.", "The cattle waited it out in a ravine. The house was singed, but the roof was redone within a week.") }, bad = { money(-part(.4)); t("Англичане нашли и скот, и овраг.", "The English found the cattle, and the ravine.") }),
                Opt(t("Укрыться в замке сеньора", "Take refuge in the seigneur's castle"), cost = 2, tag = t("2 су", "2 sous"), fx = { money(-2 - part(.2)); t("За стенами было тесно, но живо. Деревню сожгли наполовину.", "Inside the walls it was crowded, but alive. The village was half burned.") }),
                Opt(t("Встретить их у брода", "Meet them at the ford"), roll = Roll(SIL, -2, mapOf("hrab" to 2, "bog" to 1)),
                    ok = { glory(3); deed(h, t(sx(h, "Отбил", "Отбила") + " англичан у брода", "Beat off the English at the ford")); t("Брод узкий, берег крутой. Английский отряд повернул искать другую деревню.", "The ford is narrow, the bank steep. The English troop turned back to look for another village.") },
                    bad = { if (chance(.3)) { kill(h, t("убит" + sx(h, "", "а") + " англичанами у брода", "killed by the English at the ford")); t("Английские лучники стреляли с того берега.", "The English archers shot from across the river.") } else { addTr(h, "shram"); money(-part(.3)); t("Вас сбили в воду, а дом сожгли.", "You were knocked into the water, and the house was burned.") } }),
            )
        }),
    Event("plague2", "plague", Tx("Детский мор", "The Children's Plague"), y0 = 1361, y1 = 1363, p = .9, cond = { yes },
        txt = { t("Моровая язва вернулась. На этот раз она забирает больше детей, чем стариков, — её так и зовут: детский мор.",
            "The plague has come back. This time it takes more children than old folk, and so they call it the children's plague.") },
        opts = { Europe.plague(this, false) }),
    Event("armag", "war", Tx("Арманьяки и бургиньоны", "Armagnacs and Burgundians"), y0 = 1411, y1 = 1418, p = .5, cond = { h -> fire(age(h) >= 16) },
        txt = { t("Арманьяки и бургиньоны режут друг друга за власть при безумном короле. Сегодня в деревню пришли одни, завтра придут другие, и каждый спросит, за кого вы.",
            "The Armagnacs and the Burgundians are cutting each other's throats for power over the mad king. Today one side came to the village, tomorrow the other will, and each will ask whose side you are on.") },
        opts = { o ->
            listOf(
                Opt(t("Держать в сундуке обе метки: белую перевязь и андреевский крест", "Keep both badges in the chest: the white sash and the Saint Andrew's cross"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { money(2); t("Каждых встречали их цветом и их вином. Обе стороны ушли довольные.", "You met each side with its own colors and its own wine. Both went away content.") },
                    bad = { money(-part(.3)); addTr(o.h, "shram"); t("Бургиньоны нашли в сундуке белую перевязь.", "The Burgundians found the white sash in the chest.") }),
                Opt(t("Уйти в лес на всё лето", "Go to the forest for the whole summer"), fx = { money(-3); t("Жили в шалаше у угольщиков. Вернулись к пустому дому, но живыми.", "You lived in a hut with the charcoal burners. You came back to an empty house, but alive.") }),
                Opt(t("Не прятаться: мы за короля", "Don't hide: we are for the king"), roll = Roll(SER, -1, mapOf("hrab" to 1, "chest" to 1)), ok = { glory(2); t("Капитан засмеялся: «За короля — так за короля» — и не тронул вас.", "The captain laughed, “For the king, then,” and left you alone.") }, bad = { money(-part(.4)); t("Обе стороны решили, что вы за чужих.", "Both sides decided you were for the other.") }),
            )
        }),
)
