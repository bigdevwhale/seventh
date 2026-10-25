package app.seventh.domain

import app.seventh.domain.Stat.HIT
import app.seventh.domain.Stat.SER
import app.seventh.domain.Stat.SIL
import app.seventh.domain.Stat.UM
import app.seventh.domain.Stat.VER
import app.seventh.domain.Stat.ZDR

/** Japan 1660–1950, drawn as a woodblock print: the white fox of Inari, the lord's steward, famines, the Black Ships, Meiji, the wars. */
object Japan : Land("edo") {
    override val maleNames get() = tl(
        listOf("Гомбэй", "Кюбэй", "Тасукэ", "Хэйкити", "Дзимбэй", "Сакити", "Мохэй", "Ёхэй", "Тёбэй", "Китидзо", "Гэндзо", "Токуэмон", "Ясабуро", "Синсукэ", "Камбэй", "Рокубэй", "Матадзо", "Сосукэ", "Хатиро", "Токити", "Сэйбэй", "Дзэнкити", "Ихэй", "Косукэ", "Бундзо", "Гиндзи", "Тэцудзо", "Мансаку", "Ёсаку", "Дэмбэй"),
        listOf("Gonbei", "Kyubei", "Tasuke", "Heikichi", "Jinbei", "Sakichi", "Mohei", "Yohei", "Chobei", "Kichizo", "Genzo", "Tokuemon", "Yasaburo", "Shinsuke", "Kanbei", "Rokubei", "Matazo", "Sosuke", "Hachiro", "Tokichi", "Seibei", "Zenkichi", "Ihei", "Kosuke", "Bunzo", "Ginji", "Tetsuzo", "Mansaku", "Yosaku", "Denbei"),
    )
    override val femaleNames get() = tl(
        listOf("Хана", "Кику", "Цуру", "Камэ", "Умэ", "Мацу", "Такэ", "Сато", "Ёнэ", "Канэ", "Томэ", "Ито", "Фуку", "Сидзу", "Тами", "Мицу", "Хару", "Тоё", "Сэн", "Нацу", "Тиё", "Яэ", "Тацу", "Кура", "Саё"),
        listOf("Hana", "Kiku", "Tsuru", "Kame", "Ume", "Matsu", "Take", "Sato", "Yone", "Kane", "Tome", "Ito", "Fuku", "Shizu", "Tami", "Mitsu", "Haru", "Toyo", "Sen", "Natsu", "Chiyo", "Yae", "Tatsu", "Kura", "Sayo"),
    )
    override val stems get() = tl(
        listOf("Мацуи", "Огава", "Исибаси", "Хаяси", "Мураками", "Иноуэ", "Сакаи", "Номура", "Хирано", "Тагути", "Окада", "Кодама"),
        listOf("Matsui", "Ogawa", "Ishibashi", "Hayashi", "Murakami", "Inoue", "Sakai", "Nomura", "Hirano", "Taguchi", "Okada", "Kodama"),
    )
    override val places get() = tl(
        listOf("Провинция Мусаси, деревня Кодайра", "Провинция Сагами, деревня Хадано", "Провинция Симоса, деревня Сирои", "Провинция Каи, деревня Исава", "Провинция Синано, деревня Сиода", "Провинция Кодзукэ, деревня Камбара"),
        listOf("Musashi Province, Kodaira village", "Sagami Province, Hadano village", "Shimosa Province, Shiroi village", "Kai Province, Isawa village", "Shinano Province, Shioda village", "Kozuke Province, Kanbara village"),
    )
    override val families get() = tl(
        listOf("дома Ямада", "дома Мори", "дома Уэно", "дома Кикути", "дома Сасаки", "дома Ота", "дома Курокава"),
        listOf("the Yamada house", "the Mori house", "the Ueno house", "the Kikuchi house", "the Sasaki house", "the Ota house", "the Kurokawa house"),
    )
    override val dvor get() = tl(
        listOf("лачуга арендатора", "крестьянский дом под соломой", "дом старосты", "сакэварня", "городской дом с каменной кладовой"),
        listOf("a tenant's shack", "a thatched farmhouse", "the headman's house", "a sake brewery", "a town house with a fireproof storehouse"),
    )
    override val years = 1662..1688
    /** Upbringing keys stay the same in both languages: they are saved with the person. */
    override val edu0 = "field"
    override val eduBias = mapOf("field" to listOf("hrab", "gnev"), "tera" to listOf("chest", "krot"), "shop" to listOf("lzh", "zhad"), "herb" to listOf("dobr", "shch"))
    override val traitNames get() = tl(
        mapOf("bog" to ("силач" to "силачка"), "bogom" to ("паломник" to "паломница"), "gor" to ("горожанин" to "горожанка")),
        mapOf("bog" to ("strongman" to "strongwoman"), "bogom" to ("pilgrim" to "pilgrim"), "gor" to ("townsman" to "townswoman")),
    )
    override val rival get() = tl(Rival("дома Курокава", "домом Курокава", "Курокава"), Rival("the Kurokawa house", "the Kurokawas", "the Kurokawas"))
    override val cur get() = t("мон", "mon")
    override val moneyLabel get() = t("Медь", "Copper")
    override val introScene = "inari"
    override val nakaz get() = NakazText(
        { t("Скопите $it мон — хватит жить впроголодь", "Save $it mon: enough of living hand to mouth") },
        t("Пусть о нас говорят по всей провинции", "Let the whole province speak of us"),
        { t("Поставьте новый дом — $it", "Build a better house: $it") },
        t("Пусть в роду будет грамотный", "Let someone in the family learn to read"),
        t("Помиритесь с Курокавой", "Make peace with the Kurokawas"),
    )
    override val helpFeud get() = t("Вражда с соседями Курокава. Чем она выше, тем чаще они мстят. Помирить может свадьба или уступка.",
        "The feud with the Kurokawas next door. The higher it runs, the more often they take revenge. A wedding or a concession can mend it.")
    override val helpHeart get() = t("Сердце — люди, староста, торг, мир с соседями.", "Heart: people, the headman, trade, peace with the neighbors.")

    override fun family(g: Game) = g.stem
    override fun surname(g: Game, p: Person) = g.stem
    override fun curseLog(e: Engine, f: Person) = t("Белая лиса Инари прокляла род ${e.g.stem} до седьмого колена.", "The white fox of Inari cursed the ${e.g.stem} line to the seventh generation.")
    override fun cause(e: Engine, p: Person): String = with(e) {
        val a = age(p)
        when {
            a < 5 -> pick(tl(listOf("оспа", "жар", "корь", "горловая болезнь", "судороги"), listOf("smallpox", "fever", "measles", "the throat sickness", "convulsions")))
            a < 15 -> pick(tl(listOf("оспа", "жар", "утонул" + sx(p, "", "а") + " в оросительной канаве", "лихорадка"), listOf("smallpox", "fever", "drowned in the irrigation ditch", "ague")))
            a > 66 -> pick(tl(listOf("старость", "старость", "удар", "сердце"), listOf("old age", "old age", "a stroke", "the heart")))
            else -> pick(tl(listOf("чахотка", "бери-бери", "лихорадка", "водянка", "надорвал" + sx(p, "ся", "ась") + " на посадке риса", "простуда"),
                listOf("consumption", "beriberi", "fever", "dropsy", "worn out by the rice planting", "a chill")))
        }
    }
    override fun mort(year: Int) = (if (year > 1900) .75 else 1.0) * (if (year > 1945) .55 else 1.0)
    override fun income(year: Int) = if (year > 1873) 1 else 0
    override fun intro(e: Engine, f: Person) = with(e) {
        listOf(
            t("${g.year} год. ${g.place}.", "${g.year}. ${g.place}."),
            t("Жребий выпал: **${f.name} ${surname(f)}**, ${yrs(age(f))}, ${sx(f, "сын", "дочь")} арендатора. ${cap(f.tr.joinToString(", ") { tname(it, f) })}.",
                "The lot has fallen: **${f.name} ${surname(f)}**, ${age(f)}, a tenant farmer's ${sx(f, "son", "daughter")}. ${cap(f.tr.joinToString(", ") { tname(it, f) })}."),
            t("У красных ворот святилища Инари на краю деревни жила белая лиса. Старики говорили: лисы — посланницы Инари, их не трогают. Но она таскала цыплят, и ${f.name} ${sx(f, "убил", "убила")} её камнем. Ночью над рисовыми полями горели лисьи огни, и голос сказал: «Твой род — до седьмого колена».",
                "A white fox lived by the red gate of the Inari shrine at the edge of the village. Foxes are Inari's messengers, the old people said; you do not touch them. But this one took chickens, and ${f.name} killed it with a stone. That night fox-fires burned over the paddies, and a voice said: “Your line, to the seventh generation.”"),
        )
    }

    private fun Engine.yr(n: Int) = t(yrs(n), if (n == 1) "one year" else "$n years")

    private fun conscript(e: Engine, x: Person, war: String) = with(e) {
        val l = mutableListOf<Opt>()
        if (g.year < 1889) l.add(Opt(t("Откупиться", "Pay the exemption"), cost = 12, tag = t("12 мон", "12 mon"),
            fx = { money(-12); t("Закон пока позволяет откупиться. Управа взяла деньги и вписала в список чужого сына.", "The law still lets a family buy its son out. The district office took the money and wrote someone else's son on the list.") }))
        l.add(Opt(t("Отдать: ${x.name}", "Let ${x.name} go"),
            fx = { sendAway(x, war); t("Деревня провожает призывников с флажками до околицы. Мать стоит у ворот до темноты.", "The village sees the conscripts off with paper flags as far as the last house. ${x.name}'s mother stands at the gate until dark.") }))
        l.add(Opt(t("Спрятать в горах у углежогов", "Hide with the charcoal-burners in the hills"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1, "trus" to 1)),
            ok = { t("${x.name} пересидел${sx(x, "", "а")} зиму в горах у углежогов. Призыв прошёл мимо.", "${x.name} sat out the winter with the charcoal-burners. The call-up passed by.") },
            bad = { sendAway(x, war); money(-4); t("${x.name} нашли и увели под конвоем, а вас оштрафовали.", "They found ${x.name} and marched ${sx(x, "him", "her")} off under guard, and you were fined.") }))
        l
    }

    private fun plague(e: Engine, sev: Boolean, pox: Boolean) = with(e) {
        val die = if (pox) t("оспа", "smallpox") else t("холера", "cholera")
        listOf(
            Opt(t("Увести семью в горную хижину", "Take the family up to the hill hut"), roll = Roll(UM, if (sev) -2 else 0, mapOf("gram" to 1)),
                ok = { t("Пересидели в горах, пока не утихло. Вернулись все.", "You waited it out in the hills. Everyone came back.") },
                bad = {
                    val c = killOne(true)
                    if (c == null) t("Болезнь дошла и до гор, но все выжили.", "The sickness climbed the hill too, but everyone lived.")
                    else {
                        kill(c, die)
                        val c2 = if (sev) killOne(true) else null
                        if (c2 != null && chance(.5)) { kill(c2, die); t("Болезнь дошла и до гор. Не стало двоих: ${c.name} и ${c2.name}.", "The sickness climbed the hill too. Two are gone: ${c.name} and ${c2.name}.") }
                        else t("Болезнь дошла и до гор. Не стало: ${c.name}.", "The sickness climbed the hill too. ${c.name} is gone.")
                    }
                }),
            if (pox) Opt(t("Развесить красные обереги от бога оспы", "Hang red charms against the smallpox god"), roll = Roll(VER, if (sev) -2 else 0, mapOf("bogom" to 1, "chest" to 1)),
                ok = { t("Красная бумага на дверях, красные рубашки на детях. Бог оспы не любит красного — и прошёл мимо.", "Red paper on the doors, red shirts on the children. The smallpox god dislikes red, and he passed you by.") },
                bad = { val c = killOne(true); if (c != null) { kill(c, die); t("Не уберегли: ${c.name}.", "Red did not help. ${c.name} is gone.") } else t("Переболели все, но выжили. Рябины останутся.", "Everyone caught it and everyone lived. The pockmarks will stay.") })
            else Opt(t("Молиться Якуси, будде-целителю", "Pray to Yakushi, the healing Buddha"), roll = Roll(VER, if (sev) -2 else 0, mapOf("bogom" to 1, "chest" to 1)),
                ok = { t("Вы жгли благовония и кипятили воду, как велел монах. Холера обошла ваш дом.", "You burned incense and boiled the water, as the monk told you. The cholera passed your house.") },
                bad = { val c = killOne(true); if (c != null) { kill(c, die); t("Не уберегли: ${c.name}.", "Prayer was not enough. ${c.name} is gone.") } else t("Переболели все, но выжили.", "Everyone caught it and everyone lived.") }),
            Opt(t("Ходить за больными соседями", "Nurse the sick next door"), roll = Roll(ZDR, -2, mapOf("dobr" to 2, "hrab" to 1)),
                ok = { o -> glory(3); deed(o.h, t(sx(o.h, "Выхаживал", "Выхаживала") + " соседей в поветрие", "Nursed the neighbors through the sickness")); t("Вас обходит даже поветрие. В деревне говорят: святой человек.", "Even the sickness steps around you. The village says you must be a saint.") },
                bad = { o -> if (chance(.5)) { kill(o.h, die); t("Заразились у соседей.", "You caught it next door.") } else { addTr(o.h, "hvor"); t("Заразились, но выжили. Здоровье уже не то.", "You caught it and lived, but your health is not what it was.") } }),
        )
    }

    override val forced = listOf(
        Event("match", "wedding", Tx("Накодо", "The Go-Between"), cond = { h -> fire(age(h) in 16..49 && !married(h) && h.away == null && g.year >= g.noMatch) },
            txt = { o -> pick(listOf(
                t("Накодо, тётушка Сигэ, кладёт на циновку три свёртка с именами — по одному на ${sx(o.h, "каждую невесту", "каждого жениха")}. «Выбирай, ${o.h.name}. Что видно — то видно. А что за ширмой — узнаешь после свадебной чарки».",
                    "Aunt Shige, the go-between, lays three folded papers on the mat, one for each ${sx(o.h, "bride", "groom")}. “Choose, ${o.h.name}. What you can see, you can see. What is behind the screen you will learn after the wedding cup.”"),
                t("Тётушка Сигэ пришла с рисовыми лепёшками и тремя свёртками за пазухой. «Одному в доме не прожить, ${o.h.name}: и рис некому сажать, и предков некому поминать. Вот, смотри. Что за ширмой — узнаешь после свадебной чарки».",
                    "Aunt Shige has come with rice cakes and three folded papers in her robe. “No one can live alone in a house, ${o.h.name}: there's no one to plant the rice and no one to remember the ancestors. Here, look. What is behind the screen you will learn after the wedding cup.”"),
                t("Накодо обмахивается веером и выкладывает три свёртка, как карты. «По всей долине искала, ${o.h.name}. Лицо и норов — что видно, то видно. А остальное, как водится, за ширмой».",
                    "The go-between fans herself and lays out three folded papers like playing cards. “I searched the whole valley, ${o.h.name}. Face and temper, what you see is what you get. The rest, as always, is behind the screen.”"),
            )) },
            opts = { o -> matchOpts(this, o, t("мон", "mon"), t("что-то за ширмой", "something behind the screen")) { p, _ ->
                var s = t("Трижды по три глотка из свадебных чашечек — и ${p.name} ${sx(p, "вошёл в дом", "вошла в дом")}${if (p.dw > 0) " с приданым" else ""}.",
                    "Three sips from three wedding cups, and ${p.name} came into the house${if (p.dw > 0) " with a dowry" else ""}.")
                if (p.from == rival.g) { feud(-2); s += t(" Курокава пришли на свадьбу — впервые за много лет.", " The Kurokawas came to the wedding, the first time in years.") }
                s } +
                Opt(t("Повременить", "Wait"), tag = t("Накодо придёт через три года", "The go-between will come back in three years"),
                    fx = { g.noMatch = g.year + 3; t("Тётушка Сигэ поджала губы, допила чай и ушла.", "Aunt Shige pursed her lips, finished her tea and left.") }) }),
        Event("edu", "interior", Tx("К какому делу?", "What Work?"), cond = { fire(kidIn(7, 11) { it.edu == null }.firstOrNull()) },
            txt = { o -> val x = o.xx
                val b = if (x.bast) t(" (незаконн" + sx(x, "ый", "ая") + ")", " (born out of wedlock)") else ""
                pick(listOf(
                    t("${x.name}$b уже ${yrs(age(x))}. Пора решать, к какому делу ${sx(x, "его", "её")} приставить. От этого зависит, ${sx(x, "каким он", "какой она")} вырастет.",
                        "${x.name}$b is ${age(x)} now. Time to decide what work to put ${sx(x, "him", "her")} to. It will shape what kind of ${sx(x, "man", "woman")} ${sx(x, "he", "she")} grows into."),
                    t("${x.name}$b ${sx(x, "вырос", "выросла")} из детской куртки и целыми днями гоняет стрекоз у канала. Пора к делу: какое дело — таким и человек.",
                        "${x.name}$b has outgrown the little jacket and spends whole days chasing dragonflies by the canal. Time for some work: the work makes the person."),
                    t("Старики говорят: до семи лет дитя принадлежит богам, а после — людям. ${x.name}$b ${sx(x, "вошёл", "вошла")} в этот возраст. Куда ${sx(x, "его", "её")} отдать?",
                        "The old people say a child belongs to the gods until seven, and to people after that. ${x.name}$b has reached that age. Where shall ${sx(x, "he", "she")} be put to work?"),
                )) },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("На рисовые чеки", "Into the paddies"), tag = t("Сила +2, Здоровье +1 · чаще храбрые и гневные", "Strength +2, Health +1 · often brave and hot-tempered"),
                        fx = { x.edu = "field"; x[SIL] += 2; x[ZDR] += 1; t("${x.name} с рассвета по колено в воде. Спина уже не гнётся, а руки — как у взрослого.", "${x.name} stands knee-deep in the paddy from dawn. ${sx(x, "His", "Her")} back is stiff and ${sx(x, "his", "her")} hands are a grown-up's.") }),
                    Opt(t("В храмовую школу", "To the temple school"), cost = 2, tag = t("2 мон · Ум +2, может выучиться грамоте · чаще честные и кроткие", "2 mon · Wits +2, may learn to read · often honest and meek"),
                        fx = { money(-2); x.edu = "tera"; x[UM] += 2; if (chance(.6)) { addTr(x, "gram"); t("${x.name} уже пишет кистью своё имя и щёлкает на счётах.", "${x.name} can already brush ${sx(x, "his", "her")} own name and click the abacus.") } else t("Иероглифы ${x.name} не даются, зато счёт — хорошо.", "The characters will not stick, but ${x.name} counts well enough.") }),
                    Opt(t("В лавку, на побегушки", "Errand child at a shop"), tag = t("Хитрость +2 · чаще лживые и жадные", "Cunning +2 · often lying and greedy"),
                        fx = { x.edu = "shop"; x[HIT] += 2; t("${x.name} бегает при лавке в городе и уже знает, когда поклониться пониже, а когда отмерить покороче.", "${x.name} runs errands at a shop in town and already knows when to bow lower and when to measure shorter.") }),
                    Opt(t("К травнице при святилище", "To the herb-woman at the shrine"), tag = t("Сердце +1, Вера +1, Здоровье +1 · чаще добрые и щедрые", "Heart +1, Faith +1, Health +1 · often kind and generous"),
                        fx = { x.edu = "herb"; x[SER]++; x[VER]++; x[ZDR]++; t("${x.name} знает уже сорок трав и какой ками за что отвечает.", "${x.name} knows forty herbs already, and which god of the place looks after what.") }),
                )
            }),
        Event("stones", "night_int", Tx("Камни на душе", "Stones on the Soul"), cond = { h -> fire(h.stones >= 5 && age(h) >= 14) },
            txt = { t("Пять камней лежат на душе. Ночами не спится, а днём всё валится из рук. Так долго не протянуть.", "Five stones lie on your soul. You cannot sleep at night, and by day everything slips from your hands. This cannot go on.") },
            opts = { o ->
                val l = mutableListOf(
                    Opt(t("Залить сакэ", "Drown it in sake"), tag = t("станет пьяницей", "will take to drink"), fx = { addTr(o.h, "pyan"); o.h.stones = 2; t("Харчевня у моста приняла вас как родного. Камни полегчали, кошелёк тоже.", "The eating-house by the bridge took you in like family. The stones got lighter, and so did the purse.") }),
                    Opt(t("Сорвать злобу на домашних", "Take it out on the family"), tag = t("станет гневным, слава −1", "turns hot-tempered, glory −1"), fx = { addTr(o.h, "gnev"); o.h.stones = 1; glory(-1); t("Дети замолкают, когда вы входите в дом.", "The children go quiet when you come into the house.") }),
                    Opt(t("Обойти восемьдесят восемь храмов Сикоку", "Walk the eighty-eight temples of Shikoku"), roll = Roll(VER, -1, mapOf("krot" to 1, "chest" to 1)),
                        ok = { o.h.stones = 0; o.h[VER]++; addTr(o.h, "bogom"); t("Белые одежды, посох, соломенная шляпа. Полгода пути — и вы вернулись налегке.", "White robes, a staff, a straw hat. Half a year on the road, and you came home light.") },
                        bad = { o.h.stones = 4; o.h[ZDR]--; t("На горных тропах вы простудились. Камни остались, а кашель прибавился.", "You caught a chill on the mountain paths. The stones stayed, and a cough came with them.") }),
                )
                if (desc(o.h).any { alive(it) && it.away == null }) l.add(monkOpt(this, o, t("Ворота храма закрылись за вами, голову обрили. Дом и род теперь — на детях.", "The temple gate closed behind you and they shaved your head. The house and the line are the children's now.")))
                l
            }),
        Event("grudge", "interior", Tx("Обида старшего", "The Eldest's Grudge"), cond = { val gp = person(g.grudge); fire(if (gp != null && alive(gp) && !gp.monk && chance(.35)) gp else null) },
            txt = { o -> val x = o.xx
                t("${x.name} так и не ${sx(x, "простил", "простила")}, что дом перешёл не к ${sx(x, "нему", "ней")}. «Старшему положено! Отдай мою долю!» — кричит ${sx(x, "он", "она")} на всю деревню.",
                    "${x.name} has never forgiven being passed over for the house. “It goes to the eldest! Give me my share!” ${sx(x, "he", "she")} shouts for the whole village to hear.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Отдать треть денег", "Give a third of the money"), tag = t("−${g.money / 3} мон", "−${g.money / 3} mon"),
                        fx = { money(-(g.money / 3)); g.grudge = null; deed(x, t(sx(x, "Ушёл", "Ушла") + " со своей долей в город", "Took a share and left for town")); t("${x.name} ${sx(x, "забрал", "забрала")} долю и ${sx(x, "ушёл", "ушла")} в город. Расстались без слёз, но и без проклятий.", "${x.name} took the share and went to town. No tears at parting, but no curses either.") }),
                    Opt(t("Отказать", "Refuse"), roll = Roll(SER, -1, mapOf("kras" to 1, "shch" to 1)),
                        ok = { g.grudge = null; t("${x.name} поворчал${sx(x, "", "а")} и остал${sx(x, "ся", "ась")} при доме. Кажется, обида прошла.", "${x.name} grumbled and stayed on. The grudge seems to have passed.") },
                        bad = { money(-part(.3)); feud(2); g.grudge = null; t("Ночью сгорел сарай с рисом. ${x.name} ушл${sx(x, "", "а")} к Курокаве и там рассказывает про вас всякое.", "That night the rice shed burned. ${x.name} has gone over to the Kurokawas and tells them all sorts of things about you.") }),
                    Opt(t("Выгнать из дома", "Throw ${sx(x, "him", "her")} out"), sin = "cruel",
                        fx = { g.grudge = null; g.hex = g.year + 6; t("${x.name} ушл${sx(x, "", "а")}, прокляв вас у ворот. В мешке с бобами на шесть лет прибавился второй красный.", "${x.name} left, cursing you at the gate. For six years the bag of beans holds a second red one.") }),
                )
            }),
        Event("dream", "dream", Tx("Сон", "A Dream"), cond = { fire(dreamDue()) },
            txt = { val n = g.nakaz!!; n.nag = g.year
                pick(listOf(
                    t("Снится покойный предок — ${n.by}. Сидит у домашнего алтаря, смотрит с укором: «${n.t}». И уходит, не оглядываясь.", "You dream of ${n.by}, long dead. ${n.by} sits by the house altar and looks at you in reproach: “${n.t}.” Then gets up and goes, without looking back."),
                    t("Во сне вы стоите по колено в рисовом чеке, а на меже — покойный предок, ${n.by}, такой же, как при жизни, только без тени. «${n.t}», — говорит предок и уходит в туман.", "In the dream you stand knee-deep in a paddy, and on the bank stands ${n.by}, long dead, just as in life, only casting no shadow. “${n.t},” says your ancestor, and walks off into the mist."),
                    t("Ночью кто-то черпает воду из колодца. Вы выглядываете — это ${n.by}, давно покойный предок. Не оборачиваясь, предок говорит: «${n.t}». Ведро падает в колодец, и вы просыпаетесь.", "In the night someone is drawing water from the well. You look out: it is ${n.by}, long dead. Without turning round, your ancestor says: “${n.t}.” The bucket falls into the well, and you wake."),
                )) },
            opts = { o ->
                listOf(
                    Opt(t("Проснуться в холодном поту", "Wake in a cold sweat"), tag = t("камень на душу", "a stone on the soul"), fx = { o.h.stones++; t("До утра так и не уснули.", "You did not sleep again till morning.") }),
                    Opt(t("Заказать поминальную службу", "Pay for a memorial service"), cost = 2, tag = t("2 мон", "2 mon"), fx = { money(-2); t("Монах прочёл сутру, благовония догорели. Сны прекратились — пока.", "The monk read the sutra and the incense burned down. The dreams stopped, for now.") }),
                )
            }),
    )

    override val events = listOf(
        Event("fire", "fire", Tx("Пожар", "Fire"), cond = { yes },
            txt = { pick(listOf(
                t("Ночью у соседей опрокинулась жаровня. Ветер несёт искры прямо на вашу соломенную крышу.", "In the night a brazier tipped over next door. The wind is carrying sparks straight onto your thatch."),
                t("В грозу молния ударила в старую криптомерию у дома, и горящие ветки сыплются на вашу соломенную крышу.", "In a thunderstorm lightning struck the old cedar by the house, and burning branches are falling onto your thatch."),
                t("Из соседской бани вырвался огонь. Сухой зимний ветер гонит искры прямо на вашу соломенную крышу.", "Fire has broken out of the bathhouse next door. The dry winter wind is driving the sparks straight onto your thatch."),
            )) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Лезть на крышу, сбивать огонь", "Climb up and beat out the sparks"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)),
                        ok = { deed(h, t(sx(h, "Отстоял", "Отстояла") + " дом от пожара", "Saved the house from fire")); t("Крышу отстояли. Обгорели брови, зато цел дом.", "The roof held. You lost your eyebrows and kept the house.") },
                        bad = { money(-part(.4)); if (g.dvor > 0) g.dvor--; addTr(h, "shram"); t("Дом сгорел дотла, вы сами еле спрыгнули. Ставить всё заново.", "The house burned to the ground and you barely jumped clear. Everything must be built again.") }),
                    Opt(t("Выносить детей и домашний алтарь", "Carry out the children and the house altar"), roll = Roll(VER, 2, mapOf("dobr" to 1)),
                        ok = { money(-part(.3)); t("Все живы, таблички предков целы. Дом сгорел, но соседи помогли бамбуком и балками.", "Everyone is alive and the ancestor tablets are safe. The house is gone, but the neighbors brought bamboo and beams.") },
                        bad = { money(-part(.3)); val v = house().filter { it.id != g.head && age(it) < 10 }
                            if (v.isNotEmpty()) { val c = pick(v); kill(c, t("сгорел" + sx(c, "", "а") + " на пожаре", "burned in a fire")); t("В дыму не нашли ребёнка — ${c.name}…", "In the smoke you could not find ${c.name}…") }
                            else t("Таблички предков сгорели. Старики говорят, это худой знак.", "The ancestor tablets burned. The old people say that is a bad sign.") }),
                    Opt(t("Бить в пожарный колокол", "Ring the fire bell"), roll = Roll(SER, 0, mapOf("dobr" to 1, "kras" to 1)),
                        ok = { glory(1); t("Деревня встала цепью с вёдрами от канала. Сгорел только соседский сарай.", "The village formed a bucket line from the canal. Only the neighbors' shed burned.") },
                        bad = { money(-part(.3)); t("Пока сбегались, занялся и ваш дом.", "By the time they came running, your house had caught too.") }),
                )
            }),
        Event("fair", "fair", Tx("Храмовая ярмарка", "The Temple Fair"), cond = { h -> fire(age(h) >= 14) },
            txt = { pick(listOf(
                t("У храма праздник и ярмарка: хлопок, лаковые гребни, сушёный кальмар, кукольный театр, а продавец жабьего масла режет себе руку и тут же мажет — зажило!",
                    "Festival day at the temple, and a fair: cotton, lacquer combs, dried squid, a puppet show, and a man selling toad oil who cuts his own arm and rubs it in. Healed!"),
                t("Новогодний торг у храма: соломенные обереги для ворот, сушёная хурма, бумажные змеи, барабаны. Скупщик из ${if (g.year < 1868) "Эдо" else "Токио"} берёт хлопок оптом.",
                    "The New Year market at the temple: straw charms for the gate, dried persimmons, paper kites, drums. A buyer from ${if (g.year < 1868) "Edo" else "Tokyo"} is taking cotton in bulk."),
                t("Осенняя ярмарка: гадатель с черепахой, акробаты на шестах, жареные каштаны. Перекупщики ходят меж рядов и щупают ваш хлопок, как лошадь на торгу.",
                    "The autumn fair: a fortune-teller with a tortoise, acrobats on poles, roast chestnuts. The dealers walk the rows and finger your cotton like a horse at market."),
            )) },
            opts = { o ->
                listOf(
                    Opt(t("Торговаться до хрипоты", "Haggle till you're hoarse"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)),
                        ok = { money(6); t("Продали хлопок втридорога. Перекупщик из Эдо ещё и кланялся.", "You sold your cotton at three times the price, and the dealer from Edo bowed to you besides.") },
                        bad = { money(-2); t("Перекупщик расплатился связкой монет, где не хватало трети.", "The dealer paid with a string of coins a third short.") }),
                    Opt(t("Продать честно", "Sell honestly"), roll = Roll(SER, 2, mapOf("chest" to 1)),
                        ok = { money(3); glory(1); t("Купцы запомнили честного продавца и звали приезжать ещё.", "The merchants remembered an honest seller and asked you to come again.") },
                        bad = { money(1); t("Честность нынче не в цене. Продали за бесценок.", "Honesty is cheap this year. You sold for next to nothing.") }),
                    Opt(t("Гулять на все", "Spend it all"), sin = "greed",
                        fx = { money(-minOf(5, g.money)); if (chance(.35) && addTr(o.h, "pyan")) t("Гуляли три дня. С ярмарки вы вернулись с больной головой и новой привычкой.", "Three days of it. You came home with a sore head and a new habit.") else t("Гуляли три дня. Купили детям бумажные вертушки и леденцового журавля.", "Three days of it. You bought the children paper pinwheels and a candy crane.") }),
                )
            }),
        Event("beggar", "road", Tx("Монахиня у ворот", "A Nun at the Gate"), cond = { h -> fire(age(h) >= 14) },
            txt = { pick(listOf(
                t("Странствующая монахиня в соломенной шляпе просит горсть риса. Глаза у неё жёлтые — такие, говорят, были у той лисы.", "A wandering nun in a straw hat asks for a handful of rice. Her eyes are amber; the fox's were that color, they say."),
                t("В сумерках у ворот звякает посох: странствующая монахиня просит горсть риса. Тень у неё на стене длиннее, чем надо, и будто с хвостом. Или показалось.", "At dusk a staff jingles at the gate: a wandering nun asks for a handful of rice. Her shadow on the wall is longer than it should be, and seems to have a tail. Or so it seemed."),
                t("Под дождём у ворот стоит монахиня, совсем молоденькая, с колокольчиком. Просит горсть риса и ночлег. Собака на неё не лает — только смотрит.", "A nun stands in the rain at the gate, very young, with a little bell. She asks for a handful of rice and a place to sleep. The dog does not bark at her; it only stares."),
            )) },
            opts = { o ->
                listOf(
                    Opt(t("Накормить и оставить ночевать", "Feed her and give her a bed"), cost = 1, tag = t("1 мон", "1 mon"),
                        fx = { money(-1); glory(1); if (chance(.5)) { g.bless = 1; t("Утром её не было, а на пороге лежала красная нитка. В следующем жребии будет лишний белый боб.", "In the morning she was gone, and a red thread lay on the doorstep. There will be an extra white bean in the next draw.") } else t("Монахиня поела, прочла над домом сутру и ушла до света.", "The nun ate, chanted a sutra over the house and left before light.") }),
                    Opt(t("Дать горсть риса и проводить", "Give her the rice and see her off"), fx = { t("Монахиня поклонилась и ушла, не оглядываясь.", "The nun bowed and left without looking back.") }),
                    Opt(t("Прогнать собаками", "Set the dogs on her"), sin = "cruel",
                        fx = { g.hex = g.year + 5; t("На повороте она обернулась и засмеялась — коротко, будто тявкнула. В мешке с бобами на пять лет прибавился второй красный.", "At the bend she turned and laughed, short and sharp, like a bark. For five years the bag of beans holds a second red one.") }),
                )
            }),
        Event("meja", "meja", Tx("Межевые колья", "The Boundary Stakes"), w = 2, cond = { h -> fire(age(h) >= 15) },
            txt = { pick(listOf(
                t("Курокава ночью переставили межевые колья на шаг в ваш рисовый чек.", "In the night the Kurokawas moved the boundary stakes a step into your paddy."),
                t("Утром межевые колья стоят на шаг глубже в вашем чеке, а у Курокавы на сандалиях свежая грязь.", "This morning the boundary stakes stand a step deeper in your paddy, and there is fresh mud on the Kurokawas' sandals."),
                t("Курокава перекопали канаву между чеками и заодно передвинули колья: «так вода лучше идёт». Идёт она теперь по вашему шагу земли.", "The Kurokawas have re-dug the ditch between the paddies and moved the stakes while they were at it: “the water runs better this way.” It runs over a step of your land now."),
            )) },
            opts = { o ->
                listOf(
                    Opt(t("Идти к старосте и в управу", "Take it to the headman and the steward"), roll = Roll(UM, 0, mapOf("gram" to 2)),
                        ok = { feud(1); glory(1); t("Управа велела вернуть колья. Курокава затаили злобу.", "The steward's office ordered the stakes put back. The Kurokawas will remember.") },
                        bad = { money(-3); t("Писарь взял монету с вас и две с них. Колья остались где стояли.", "The clerk took a coin from you and two from them. The stakes stayed where they were.") }),
                    Opt(t("Выдернуть колья, а Курокаве — вправить ум", "Pull the stakes and knock sense into Kurokawa"), sin = "cruel", roll = Roll(SIL, 0, mapOf("gnev" to 1, "bog" to 2)),
                        ok = { feud(2); glory(1); t("Чек ваш. Старший Курокава теперь ходит кривой.", "The paddy is yours. The elder Kurokawa walks crooked now.") },
                        bad = { feud(1); addTr(o.h, "shram"); t("Курокава было четверо.", "There were four Kurokawas.") }),
                    Opt(t("Уступить шаг", "Give up the step"), sin = "coward", fx = { feud(-1); glory(-1); t("Мир дороже шага грязи. Над вами посмеиваются, зато Курокава кланяются.", "Peace is worth more than a step of mud. People laugh at you, but the Kurokawas bow.") }),
                )
            }),
        Event("peace", "interior", Tx("Мировая", "Making Peace"), w = 3, cond = { fire(if (g.feud >= 2) kidIn(15, 24) { it.spouse == null }.firstOrNull() else null) },
            txt = { o -> val x = o.xx
                t("Старый Курокава пришёл и опустился на колени у порога: «Хватит нам грызться. У меня ${sx(x, "внучка на выданье", "внук в женихах")}. Поженим их — и делу конец». Речь о том, кто у вас зовётся ${x.name}.",
                    "Old Kurokawa came and knelt at your door. “Enough of this. I have a ${sx(x, "granddaughter of an age to marry", "grandson of an age to marry")}. Let us marry them and be done.” He means your ${x.name}.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Сосватать", "Agree to the match"), fx = { val s = mkSpouse(x, rival.g); g.feud = 0; glory(1); t("Два дома выпили из одних чашечек. ${x.name} и ${s.name} — и вражде конец.", "Two houses drank from the same cups. ${x.name} and ${s.name}, and the feud is over.") }),
                    Opt(t("Указать на дверь", "Show him the door"), fx = { feud(1); t("Вражда достанется и внукам.", "The feud will pass to the grandchildren.") }),
                )
            }),
        Event("revenge", "night_int", Tx("Месть Курокавы", "The Kurokawas' Revenge"), w = 3, cond = { fire(g.feud >= 3) },
            txt = { pick(listOf(
                t("Ночью кто-то подпёр вашу дверь колом и бросил на соломенную крышу горящий пук.", "In the night someone wedged your door shut and threw a burning bundle onto the thatch."),
                t("Ночью в сарае вспыхнула солома, а у плетня мелькнули две тени — в сторону дома Курокавы.", "In the night the straw in your shed went up in flames, and two shadows slipped past the fence, heading for the Kurokawas' house."),
            )) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Выломать дверь и догнать", "Break out and give chase"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)),
                        ok = { feud(-1); glory(1); t("Догнали младшего Курокаву у моста. Больше не сунутся.", "You caught the youngest Kurokawa at the bridge. They won't try that again.") },
                        bad = { money(-4); addTr(h, "shram"); t("В темноте вы налетели на мотыгу.", "In the dark you ran onto a hoe.") }),
                    Opt(t("Жаловаться в управу", "Complain to the steward's office"), roll = Roll(UM, 0, mapOf("gram" to 2)),
                        ok = { feud(-1); money(2); t("Курокаву оштрафовали в вашу пользу.", "The Kurokawas were fined, and the fine came to you.") },
                        bad = { feud(1); t("Свидетелей нет. Курокава посмеиваются.", "No witnesses. The Kurokawas smirk.") }),
                    Opt(t("Ответить тем же", "Pay them back in kind"), sin = "cruel", roll = Roll(HIT, 0, mapOf("lzh" to 1, "zhest" to 1)),
                        ok = { money(3); addSecret(t("${h.name} ${sx(h, "поджёг", "подожгла")} сарай Курокавы", "${h.name} set fire to the Kurokawas' shed"), h); t("Сарай Курокавы сгорел. Никто ничего не видел.", "The Kurokawas' shed burned down. Nobody saw a thing.") },
                        bad = { feud(2); glory(-2); t("Вас видели. Теперь об этом знает вся округа.", "You were seen. Now the whole district knows.") }),
                )
            }),
        Event("levy", "war", Tx("Призыв", "The Call-Up"), cond = { h -> fire(if (g.year in 1873..1945) sonOf(h).firstOrNull() else null) },
            txt = { o -> if (g.year < 1889) t("Новый закон: каждый двадцатилетний — на три года в армию, как бы ни звали его отца. Жребий пал на ${o.xx.name}.", "A new law: every man of twenty serves three years in the army, whoever his father is. The lot has fallen on ${o.xx.name}.")
                else t("Из управы пришла повестка: ${o.xx.name} — в армию.", "A notice came from the district office: ${o.xx.name} is called to the army.") },
            opts = { o -> conscript(this, o.xx, t("призыв", "the call-up")) }),
        Event("plague", "plague", Tx("Оспа", "Smallpox"), cond = { yes },
            txt = { t("В округе оспа. В соседней деревне за месяц сожгли на погребальных кострах сорок человек.", "Smallpox in the district. In the next village they have cremated forty people this month.") },
            opts = { plague(this, false, true) }),
        Event("hunger", "field_dry", Tx("Недород", "A Lean Year"), cond = { yes },
            txt = { pick(listOf(
                t("Холодное лето: рис так и не налился. До весны не дотянуть.", "A cold summer: the rice never filled out. There is not enough to last till spring."),
                t("В августе налетела саранча. Рис стоит пустой, одни метёлки. До весны не дотянуть.", "In August the locusts came. The rice stands empty, nothing but husks. There is not enough to last till spring."),
                t("Дожди шли всё лето, и рис сгнил на корню. В амбаре пусто, а до весны далеко.", "It rained all summer, and the rice rotted where it stood. The storehouse is empty, and spring is far off."),
            )) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Продать вола", "Sell the ox"), tag = t("−4 мон", "−4 mon"), fx = { money(-4); t("Вола увели на рынок. Детей кормили кашей из проса и корнями папоротника.", "The ox went to market. The children lived on millet gruel and fern root.") }),
                ) + (if (flagged("imo")) listOf(Opt(t("Перебиться сладким картофелем", "Get by on sweet potatoes"), tag = t("−1 мон", "−1 mon"),
                    fx = { money(-1); t("Клубни со склона, что когда-то посадили в годы Кёхо, кормили всю зиму. Вол остался в хлеву.", "The tubers from the slope, first planted in the Kyoho years, fed you all winter. The ox stayed in the byre.") })) else emptyList()) + listOf(
                    Opt(t("Просить в долг у богатого дома", "Ask the rich house for a loan"), roll = Roll(SER, 0, mapOf("krot" to 1, "kras" to 1)),
                        ok = { money(2); t("Хозяин сакэварни дал риса в долг под осеннюю работу.", "The brewer lent you rice against next autumn's work.") },
                        bad = { money(-3); val c = killOne(true); if (c != null && chance(.5)) { kill(c, t("голод", "hunger")); t("Хозяин был не в духе. К весне не стало: ${c.name}.", "The brewer was in a bad mood. By spring ${c.name} was gone.") } else t("Хозяин был не в духе. Пережили на рисовых отрубях.", "The brewer was in a bad mood. You lived on rice bran.") }),
                    Opt(t("Ночью взять из налоговой кладовой", "Take from the tax storehouse at night"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)),
                        ok = { money(4); flag("tax_rice"); addSecret(t("${h.name} в недород ${sx(h, "воровал", "воровала")} рис из налоговой кладовой", "${h.name} stole rice from the tax storehouse in the lean year"), h); t("Три мешка. Никто не хватился.", "Three sacks. Nobody missed them.") },
                        bad = { money(-3); addTr(h, "shram"); glory(-2); t("Поймали. Били палками перед управой, а имя вывесили на доске.", "Caught. Beaten with staves in front of the office, your name posted on the notice board.") }),
                )
            }),
        Event("klad", "klad", Tx("Кувшин в борозде", "A Jar in the Furrow"), cond = { h -> fire(age(h) >= 14) },
            txt = { t("Мотыга звякнула о глиняный кувшин, а в нём — связки старых медных монет, позеленевших, с полустёртыми знаками.", "The hoe rang against a clay jar, and inside were strings of old copper coins, gone green, the characters half worn away.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Унести тайком", "Take it home quietly"), sin = "lie", fx = { money(15); addSecret(t("${h.name} ${sx(h, "утаил", "утаила")} клад из поля", "${h.name} kept a hoard found in the field"), h); t("Пятнадцать связок — под половицу.", "Fifteen strings of coin, under the floorboards.") }),
                    Opt(t("Отнести старосте: по закону клад не ваш", "Take it to the headman: by law it isn't yours"), fx = { money(3); glory(2); deed(h, t(sx(h, "Отдал", "Отдала") + " найденный клад по закону", "Handed in a found hoard, as the law says")); t("Староста похвалил и отсыпал горсть монет.", "The headman praised you and gave you a handful back.") }),
                    Opt(t("Отдать храму", "Give it to the temple"), fx = { glory(3); addRelic(t("Храмовый оберег", "The temple charm"), "omamori", mapOf(VER to 1), t("${h.name}: клад отдан храму, настоятель подарил оберег", "${h.name}: the hoard went to the temple; the abbot gave this charm")); t("Настоятель благословил вас и подарил оберег в парчовом мешочке. Вера +1 в жребиях.", "The abbot blessed you and gave you a charm in a brocade bag. Faith +1 in draws.") }),
                )
            }),
        Event("secret", "interior", Tx("Старая тайна", "An Old Secret"), w = 2, cond = { val s = g.secrets.firstOrNull(); if (s != null && g.year - s.y >= 3) Fire(s = s) else null },
            txt = { o -> val x = o.s!!
                t("По деревне шепчутся: ${x.t}. Дело было ${yrs(g.year - x.y)} назад${if (x.who != o.h.name) ", и того человека давно нет, но спрашивают с вас" else ""}.",
                    "The village is whispering: ${x.t}. It was ${yr(g.year - x.y)} ago${if (x.who != o.h.name) ", and that person is long gone, but it is you they ask" else ""}.") },
            opts = { o ->
                listOf(
                    Opt(t("Отпереться", "Deny it"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                        ok = { g.secrets.removeAt(0); t("Пошептались и забыли.", "They whispered and forgot.") },
                        bad = { g.secrets.removeAt(0); glory(-3); feud(1); t("Нашлись свидетели. Слава рода поблёкла.", "Witnesses turned up. The family's name is tarnished.") }),
                    Opt(t("Повиниться перед всей деревней", "Confess before the village"), roll = Roll(VER, 2, mapOf("chest" to 1, "krot" to 1)),
                        ok = { g.secrets.removeAt(0); glory(1); o.h.stones = maxOf(0, o.h.stones - 1); t("Деревня простила. Даже уважать стали.", "The village forgave you, and even thinks better of you.") },
                        bad = { g.secrets.removeAt(0); glory(-2); t("Деревня не простила, но хоть на душе легче.", "The village did not forgive, but your heart is lighter.") }),
                    Opt(t("Заплатить, чтобы молчали", "Pay for silence"), cost = 8, tag = t("8 мон", "8 mon"), fx = { money(-8); g.secrets.removeAt(0); t("Деньги заткнули самые громкие рты.", "Money shut the loudest mouths.") }),
                )
            }),
        Event("bastard", "road", Tx("Ребёнок вдовы", "The Widow's Child"), cond = { h -> fire(h.male && married(h) && age(h) in 20..50) },
            txt = { t("У вдовы О-Рин, что держит чайную у моста, родился сын. Люди говорят, что глаза у него ваши.", "O-Rin, the widow who keeps the tea stall by the bridge, has had a son. People say he has your eyes.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Признать и взять в дом", "Own him and take him in"), fx = { val c = bastard(this, h); val s = spouseOf(h); t("${c.name} теперь спит у очага с остальными.${if (s != null) " " + s.name + " неделю не разговаривала с вами." else ""}", "${c.name} sleeps by the hearth with the others now.${if (s != null) " " + s.name + " did not speak to you for a week." else ""}") }),
                    Opt(t("Отпереться", "Deny it"), sin = "lie", fx = { flag("orin_boy"); flag("orin_f", h.id); addSecret(t("сын вдовы О-Рин — от главы дома ${g.stem}", "O-Rin's boy was fathered by the head of the ${g.stem} house"), h); t("О-Рин промолчала. Но мальчик растёт, и глаза у него всё те же.", "O-Rin said nothing. But the boy is growing, and his eyes are still the same.") }),
                    Opt(t("Тайком помогать деньгами", "Help with money, secretly"), cost = 4, tag = t("4 мон", "4 mon"), fx = { money(-4); flag("orin_boy"); flag("orin_f", h.id); addSecret(t("кто-то из дома ${g.stem} тайком носит деньги вдове О-Рин", "someone from the ${g.stem} house slips money to the widow O-Rin"), h); t("О-Рин взяла молча.", "O-Rin took it without a word.") }),
                )
            }),
        Event("kabak", "tavern", Tx("Сакэ", "Sake"), cond = { h -> fire(age(h) >= 16) },
            txt = { o -> if (has(o.h, "pyan")) t("Ноги сами несут к харчевне у моста. Сосед уже машет из-за занавески.", "Your feet carry you to the eating-house by the bridge on their own. Your neighbor is already waving from behind the curtain.")
                else pick(listOf(
                    t("Сосед зовёт в харчевню «на одну чашечку». Вы знаете, чем кончаются одни чашечки.", "Your neighbor asks you to the eating-house for “just one cup”. You know how one cup ends."),
                    t("В харчевне у моста открыли новый бочонок из Нады, и вся деревня уже там. Сосед стучит в ставень: «Одну чашечку — и домой».", "The eating-house by the bridge has broached a new keg from Nada, and the whole village is there already. Your neighbor knocks at the shutter: “One cup and home.”"),
                    t("Свадьба у мельника, позвали всех. Чашечки там наливают раньше, чем успеешь поставить пустую.", "The miller is having a wedding, and everyone is invited. There they fill your cup before you can set the empty one down."),
                )) },
            opts = { o ->
                listOf(
                    Opt(t("Пойти", "Go"), roll = Roll(VER, 0, mapOf("pyan" to -3, "krot" to 1, "bogom" to 1)),
                        ok = { t("Выпили по чашечке, поговорили о погоде и разошлись.", "One cup each, some talk of the weather, and home.") },
                        bad = { if (!addTr(o.h, "pyan")) { money(-4); t("Пропили соломенный плащ и обе сандалии.", "You drank away your straw raincoat and both sandals.") } else t("Одна чашечка, вторая, десятая. Харчевня стала вам вторым домом.", "One cup, then two, then ten. The eating-house is your second home now.") }),
                    Opt(t("Отказаться", "Say no"), fx = { if (has(o.h, "pyan")) { o.h.stones++; t("Отказались. Руки трясутся до вечера. Камень на душу.", "You said no. Your hands shook till evening. A stone on the soul.") } else t("Сосед обиделся, но к Новому году отошёл.", "Your neighbor sulked, but came round by the New Year.") }),
                )
            }),
        Event("gramota", "church", Tx("Храмовая школа", "The Temple School"), cond = { h -> fire(!has(h, "gram") && age(h) in 12..45) },
            txt = { if (g.year < 1872) t("Монах при храмовой школе берётся выучить вас читать, писать и считать на счётах — за три мона и мешок проса.", "The monk who keeps the temple school offers to teach you reading, writing and the abacus, for three mon and a sack of millet.")
                else t("Деревенский учитель по вечерам учит взрослых читать. Берёт три мона и мешок проса — на дрова для школы.", "The village schoolteacher teaches grown-ups to read in the evenings. He asks three mon and a sack of millet, for the school's firewood.") },
            opts = { o ->
                listOf(
                    Opt(t("Учиться", "Learn"), cost = 3, tag = t("3 мон", "3 mon"), roll = Roll(UM, 1, mapOf("umn" to 1)),
                        ok = { money(-3); addTr(o.h, "gram"); deed(o.h, t(sx(o.h, "Выучился", "Выучилась") + " грамоте", "Learned to read and write")); t("К весне вы уже читали указы на доске у управы и считали подать быстрее писаря.", "By spring you could read the notices on the board and reckon the tax faster than the clerk.") },
                        bad = { money(-3); t("Знаки так и не сложились в слова. Проса жалко.", "The characters never turned into words. A pity about the millet.") }),
                    Opt(t("Не до того", "No time"), fx = { t("Учитель ушёл учить детей Курокавы.", "The teacher went off to teach the Kurokawa children.") }),
                )
            }),
        Event("wolves", "winter", Tx("Горные псы", "Mountain Dogs"), cond = { fire(g.year < 1905) },
            txt = { pick(listOf(
                t("Зима лютая, снег по пояс. Горные псы — так здесь зовут волков — спустились к деревне и роют подкоп под хлев.", "A hard winter, snow to the waist. The mountain dogs, as the wolves are called here, have come down to the village and are digging under the byre."),
                t("Ночью в хлеву закричал вол. Горные псы — так здесь зовут волков — пришли по снегу из леса и кружат у стены.", "In the night the ox bellowed in the byre. The mountain dogs, as the wolves are called here, have come out of the woods across the snow and are circling the wall."),
            )) },
            opts = { o ->
                listOf(
                    Opt(t("Сторожить с топором", "Keep watch with an axe"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)),
                        ok = { glory(1); money(1); t("Положили двух волков. Шкуры — на тёплые куртки младшим.", "You killed two of them. Their pelts went into padded jackets for the little ones.") },
                        bad = { money(-4); if (chance(.3)) addTr(o.h, "shram"); t("Волки зарезали вола, пока вы отбивались от вожака.", "The pack killed the ox while you were fighting off the leader.") }),
                    Opt(t("Вырыть ловчие ямы", "Dig pit traps"), roll = Roll(UM),
                        ok = { money(3); t("Три волка в ямах. Шкуры продали в городе.", "Three wolves in the pits. You sold the pelts in town.") },
                        bad = { money(-2); t("В яму упала соседская собака.", "The neighbors' dog fell in.") }),
                    Opt(t("Запереться и молиться", "Bar the door and pray"), fx = { money(-3); t("Утром хлев был пуст.", "In the morning the byre was empty.") }),
                )
            }),
        Event("sick", "night_int", Tx("Жар", "Fever"), w = 2, cond = { val k = kidIn(0, 9); fire(if (k.isEmpty()) null else pick(k)) },
            txt = { o -> pick(listOf(
                t("${o.xx.name} горит в жару третью ночь. Не ест, не узнаёт мать.", "${o.xx.name} has burned with fever three nights now. Won't eat, doesn't know ${sx(o.xx, "his", "her")} mother."),
                t("${o.xx.name} кашляет так, что слышно через всю деревню, и горяч${sx(o.xx, "ий", "ая")}, как камень из очага.", "${o.xx.name} coughs so hard the whole village can hear, and is as hot as a stone from the hearth."),
                t("У ${o.xx.name} сыпь и жар. Ребёнок бредит и зовёт кого-то, кого в доме нет.", "${o.xx.name} has a rash and a fever, and in delirium calls for someone who isn't in the house."),
            )) },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Позвать травницу", "Call the herb-woman"), cost = 1, tag = t("1 мон", "1 mon"), roll = Roll(ZDR, 1, ofX = true),
                        ok = { money(-1); t("Травница поила отваром из корня и шептала до рассвета. Жар спал.", "The herb-woman gave root broth and murmured till dawn. The fever broke.") },
                        bad = { money(-1); kill(x, t("жар", "fever")); t("Травница развела руками.", "The herb-woman spread her hands.") }),
                    Opt(t("Молиться Якуси, будде-целителю", "Pray to Yakushi, the healing Buddha"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)),
                        ok = { t("К утру ${x.name} ${sx(x, "попросил", "попросила")} рисового отвара.", "By morning ${x.name} asked for rice water.") },
                        bad = { kill(x, t("жар", "fever")); t("Благовония догорели раньше, чем кончилась ночь.", "The incense burned out before the night did.") }),
                    Opt(if (g.year < 1868) t("Везти в город к лекарю, что учился у голландцев", "Take the child to the town doctor who learned from the Dutch") else t("Везти в городскую больницу", "Take the child to the town hospital"),
                        cost = 6, tag = t("6 мон", "6 mon"), roll = Roll(UM, 4, mapOf("gram" to 1, "gor" to 1)),
                        ok = { money(-6); t("Лекарь дал горьких порошков. Через неделю — на ногах.", "The doctor gave bitter powders. A week later the child was up.") },
                        bad = { money(-6); kill(x, t("жар", "fever")); t("Не довезли.", "The child died on the way.") }),
                )
            }),
        Event("ronin", "night_int", Tx("Ночной гость", "A Night Visitor"), cond = { h -> fire(age(h) >= 16) },
            txt = { if (g.year < 1868) t("Ночью в ставень стучит ронин — самурай без господина, раненый, в рваном хакама. За ним гонятся люди князя. Просит спрятать до весны.", "In the night a ronin knocks at the shutter: a samurai with no lord, wounded, his trousers in rags. The lord's men are after him. He asks you to hide him till spring.")
                else t("Ночью в ставень стучит солдат, сбежавший из казармы. Просит спрятать до весны.", "In the night a soldier who ran from the barracks knocks at the shutter. He asks you to hide him till spring.") },
            opts = { o ->
                val h = o.h
                val old = g.year < 1868
                listOf(
                    Opt(t("Спрятать на сеновале", "Hide him in the loft"), roll = Roll(HIT, 0, mapOf("dobr" to 1, "lzh" to 1)),
                        ok = {
                            flag("ronin_hid")
                            if (old) addRelic(t("Короткий меч ронина", "The ronin's short sword"), "katana", mapOf(SIL to 1), t("ронин оставил его в благодарность", "the ronin left it in thanks"))
                            else addRelic(t("Солдатский штык", "A soldier's bayonet"), "sword", mapOf(SIL to 1), t("беглый солдат оставил его в благодарность", "the runaway soldier left it in thanks"))
                            deed(h, t(sx(h, "Спрятал", "Спрятала") + " беглеца", "Hid a fugitive"))
                            if (old) t("Весной он ушёл и оставил вам свой короткий меч. Сила +1 в жребиях.", "In spring he left, and left you his short sword. Strength +1 in draws.") else t("Весной он ушёл и оставил вам штык. Сила +1 в жребиях.", "In spring he left, and left you his bayonet. Strength +1 in draws.") },
                        bad = { addTr(h, "shram"); glory(-1); t("Его нашли у вас. Вас били палками перед управой.", "They found him in your house. You were beaten in front of the office.") }),
                    Opt(t("Выдать властям", "Turn him in"), sin = "cruel", fx = { money(3); if (old) t("Дали три мона. Его голову выставили у заставы.", "They paid you three mon. His head was put on show at the barrier gate.") else t("Дали три мона. Его увели в кандалах.", "They paid you three mon. He was taken away in irons.") }),
                    Opt(t("Дать риса и прогнать", "Give him rice and send him off"), fx = { t("Он ушёл в метель.", "He went off into the snow.") }),
                )
            }),
        Event("steward", "barin", Tx("Новый управляющий", "The New Steward"), cond = { h -> fire(g.year < 1868 && age(h) >= 16) },
            txt = { t("Князь прислал из замка нового управляющего. Тот объявил: отныне подать — шесть мешков риса из десяти, а не пять.", "The lord has sent a new steward from the castle. He announces that from now on the tax is six sacks of rice in ten, not five.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Платить", "Pay"), tag = t("−8 мон", "−8 mon"), fx = { money(-8); t("Затянули пояса.", "You tightened your belts.") }),
                    Opt(t("Подать прошение всей деревней", "Petition as a village"), roll = Roll(SER, 0, mapOf("kras" to 1, "krot" to 1)),
                        ok = { money(-4); glory(1); t("Управляющий уступил половину. Деревня кланялась вам в пояс.", "The steward gave back half. The village bowed to you to the waist.") },
                        bad = { money(-8); t("Управляющий даже не развернул свиток.", "The steward did not even unroll the scroll.") }),
                    Opt(t("Поднять деревню", "Raise the village"), roll = Roll(SIL, -3, mapOf("hrab" to 2, "gnev" to 1, "bog" to 1)),
                        ok = { glory(3); flag("ikki"); deed(h, t(sx(h, "Поднял", "Подняла") + " деревню против управляющего", "Led the village against the steward")); t("Тысяча крестьян с бамбуковыми копьями стояла у замка три дня. Управляющего отозвали, подать вернули прежнюю.", "A thousand farmers with bamboo spears stood at the castle for three days. The steward was recalled and the old tax restored.") },
                        bad = { if (chance(.4)) { flag("gimin"); flag("gimin_id", h.id); kill(h, t("казнён" + sx(h, "", "а") + " как зачинщи" + sx(h, "к", "ца"), "executed as a ringleader")); t("Подать вернули прежнюю, а зачинщиков казнили у перекрёстка. Деревня до сих пор носит туда рисовые лепёшки.", "The old tax came back, and the ringleaders were executed at the crossroads. The village still leaves rice cakes there.") } else { addTr(h, "kalek"); glory(1); flag("ikki"); t("Зачинщиков били палками. Вы выжили, но ходите с клюкой.", "The ringleaders were beaten with staves. You lived, but you walk with a stick.") } }),
                )
            }),
        Event("ise", "church", Tx("Паломничество в Исэ", "To Ise"), cond = { h -> fire(age(h) >= 16 && !has(h, "bogom")) },
            txt = { t("Соседи идут в Исэ, к святилищу богини солнца. Говорят, кто хоть раз там побывал, тому и умирать не страшно. Зовут с собой.", "The neighbors are walking to Ise, to the shrine of the sun goddess. They say whoever has been there once is not afraid to die. They ask you along.") },
            opts = { o ->
                listOf(
                    Opt(t("Идти", "Go"), roll = Roll(ZDR, 2, mapOf("krot" to 1)),
                        ok = { addTr(o.h, "bogom"); o.h.stones = 0; glory(1); if (chance(.5)) addRelic(t("Оберег из Исэ", "An Ise charm"), "omamori", mapOf(VER to 1), t("принесён из Исэ", "brought back from Ise")); t("Вернулись через два месяца с бумажным оберегом за пазухой. Камни с души как рукой сняло.", "You came back two months later with a paper charm inside your robe. The stones fell from your soul.") },
                        bad = { o.h[ZDR]--; t("Простудились на тракте и вернулись с полпути.", "You caught a chill on the highway and turned back halfway.") }),
                    Opt(t("Не время", "Not now"), fx = { t("Рис сам себя не посадит.", "The rice will not plant itself.") }),
                )
            }),
        Event("smith", "village", Tx("Мечник Канэсада", "Kanesada the Swordsmith"), cond = { fire(g.relics.none { it.ic == "axe" }) },
            txt = { t("Мечник Канэсада сидит без заказов: самураям новые клинки нынче ни к чему. Он берётся сковать вам топор «из той же стали, что мечи».", "Kanesada the swordsmith has no orders: the samurai have little use for new blades these days. He offers to forge you an axe “from the same steel as a sword.”") },
            opts = {
                listOf(
                    Opt(t("Заказать", "Order it"), cost = 4, tag = t("4 мон", "4 mon"), fx = { money(-4); addRelic(t("Топор мечника", "The swordsmith's axe"), "axe", mapOf(SIL to 1), t("скован для главы дома", "forged for the head of the house")); t("Топор лёг в руку как влитой. Сила +1 в жребиях.", "The axe sits in the hand as if it grew there. Strength +1 in draws.") }),
                    Opt(t("Обойдёмся старым", "The old one will do"), fx = { t("Старый топор ещё послужит.", "The old axe has some years in it yet.") }),
                )
            }),
        Event("town", "village", Tx("В большой город", "To the City"), cond = { fire(kidIn(16, 25) { !has(it, "gor") && it.spouse == null }.firstOrNull()) },
            txt = { o -> if (g.year < 1868) t("${o.xx.name} рвётся в Эдо: «Там каждый день праздник, а тут — грязь по колено».", "${o.xx.name} is desperate to go to Edo. “Every day is a festival there, and here it's mud to the knee.”")
                else t("${o.xx.name} рвётся в Токио: «Там фабрики, газеты, электричество. Что мне тут — грязь месить?»", "${o.xx.name} is desperate to go to Tokyo. “There are factories there, newspapers, electric light. Am I to tread mud here all my life?”") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Отпустить", "Let ${sx(x, "him", "her")} go"), fx = { addTr(x, "gor"); x[HIT] += 1; x.away = g.year + ri(3, 7); t("${x.name} ${sx(x, "ушёл", "ушла")} с погонщиками вьючных лошадей. Письма приходят редко.", "${x.name} left with the packhorse drivers. Letters come rarely.") }),
                    Opt(t("Не пускать", "Forbid it"), fx = { x.stones++; t("${x.name} остал${sx(x, "ся", "ась")}, но смотрит волком.", "${x.name} stayed, but glares at you.") }),
                )
            }),
        Event("daimyo", "road", Tx("Княжеский поезд", "The Lord's Procession"), cond = { h -> fire(g.year < 1868 && age(h) >= 12) },
            txt = { t("По тракту идёт в Эдо поезд князя: копья, сундуки, носилки, триста человек. Всем у дороги — на колени, лицом в землю. «Сита-ни! Сита-ни!» — вниз, вниз.", "The lord's procession is passing on its way to Edo: spears, chests, a palanquin, three hundred men. Everyone by the road must kneel, face in the dust. “Shita ni! Shita ni!” Down, down.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Пасть ниц и ждать", "Kneel and wait"), fx = { t("Мимо прошли триста пар ног. Шея затекла, зато голова на месте.", "Three hundred pairs of feet went by. Your neck is stiff, but your head is still on it.") }),
                    Opt(t("Продавать носильщикам сандалии и чай", "Sell sandals and tea to the porters"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)),
                        ok = { money(5); t("Носильщики стёрли ноги ещё в Киото. Распродали всё до последней сандалии.", "The porters had worn their feet raw since Kyoto. You sold every last sandal.") },
                        bad = { money(-2); t("Носильщики разобрали сандалии и забыли заплатить. Спорить с ними никто не стал.", "The porters took the sandals and forgot to pay. Nobody argued.") }),
                    Opt(t("Поднять глаза на князя", "Raise your eyes to the lord"), roll = Roll(HIT, -1, mapOf("hrab" to 1)),
                        ok = { glory(1); t("Видели князя: обычный человек, только скучает. Рассказываете об этом внукам.", "You saw the lord: an ordinary man, just bored. You tell your grandchildren about it.") },
                        bad = { addTr(h, "shram"); glory(-1); t("Самурай из охраны ударил вас древком копья по лицу. Легко отделались.", "A guard struck you across the face with a spear shaft. You got off lightly.") }),
                )
            }),
        Event("kemi", "field", Tx("Осмотр полей", "The Inspector"), cond = { h -> fire(g.year < 1873 && age(h) >= 16) },
            txt = { t("Перед жатвой из управы приехал смотритель — на глаз определить урожай, а с ним и подать. Сидит у вас под навесом и ждёт угощения.", "Before the harvest an inspector has come from the office to judge the crop by eye, and the tax with it. He is sitting under your eaves, waiting to be fed.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Угостить как следует", "Treat him well"), cost = 3, tag = t("3 мон", "3 mon"), roll = Roll(SER, 1, mapOf("kras" to 1, "shch" to 1)),
                        ok = { money(4); t("Смотритель ел, пил и насчитал вам урожай вдвое меньше настоящего.", "The inspector ate, drank and reckoned your crop at half what it is.") },
                        bad = { money(-3); t("Смотритель ел, пил и насчитал как есть.", "The inspector ate, drank and reckoned it exactly.") }),
                    Opt(t("Показать всё как есть", "Show him everything as it is"), roll = Roll(SER, 2, mapOf("chest" to 1)),
                        ok = { money(2); glory(1); t("Он записал вас в честные люди, и подать вышла справедливая.", "He wrote you down as honest people, and the tax came out fair.") },
                        bad = { money(-3); t("Честность обошлась в десятую часть урожая.", "Honesty cost you a tenth of the crop.") }),
                    Opt(t("Спрятать дальний чек", "Hide the far paddy"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)),
                        ok = { money(5); flag("hidden_paddy"); addSecret(t("у ${h.name} есть чек, которого нет в книгах управы", "${h.name} has a paddy that isn't in the tax books"), h); t("Смотритель не пошёл за ручей. Дальний чек кормит вас без подати.", "The inspector did not cross the stream. The far paddy feeds you tax-free.") },
                        bad = { money(-6); glory(-2); t("Курокава показали смотрителю тропку за ручей. Штраф — и позор на всю округу.", "The Kurokawas showed the inspector the path across the stream. A fine, and shame for the whole district to see.") }),
                )
            }),
        Event("flood", "field", Tx("Дамба", "The Dyke"), cond = { yes },
            txt = { pick(listOf(
                t("Тайфун третий день гонит дождь. Река поднялась вровень с дамбой, и дамба потекла.", "The typhoon has driven rain for three days. The river is level with the dyke, and the dyke has started to leak."),
                t("Ливень в горах: река вздулась за одну ночь и несёт брёвна, мостки и чьи-то ворота. Дамба потекла.", "A cloudburst in the hills: the river rose in a single night and is carrying logs, footbridges and somebody's gate. The dyke has started to leak."),
            )) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Идти со всей деревней таскать мешки", "Haul sandbags with the village"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)),
                        ok = { glory(2); deed(h, t(sx(h, "Держал", "Держала") + " дамбу в тайфун", "Held the dyke in the typhoon")); t("Всю ночь по пояс в воде. К рассвету дамба выстояла.", "All night waist-deep in water. By dawn the dyke had held.") },
                        bad = { if (chance(.2)) { kill(h, t("унесло рекой", "swept away by the river")); t("Дамба прорвалась прямо под ногами.", "The dyke gave way under your feet.") } else { money(-4); t("Дамбу прорвало. Чеки затянуло илом.", "The dyke broke. The paddies are buried in silt.") } }),
                    Opt(t("Прокопать сток на поле Курокавы", "Cut a channel into the Kurokawas' field"), sin = "cruel", roll = Roll(HIT, 0, mapOf("lzh" to 1, "zhest" to 1)),
                        ok = { feud(1); money(2); addSecret(t("${h.name} в тайфун ${sx(h, "спустил", "спустила")} воду на поле Курокавы", "${h.name} drained the flood onto the Kurokawas' field in the typhoon"), h); t("Вода ушла к Курокаве. Ваши чеки целы.", "The water went to the Kurokawas. Your paddies are safe.") },
                        bad = { feud(2); glory(-2); t("Вас видели с лопатой. Курокава этого не забудут.", "You were seen with the spade. The Kurokawas will not forget.") }),
                    Opt(t("Уносить детей и семенной рис на холм", "Carry the children and the seed rice up the hill"), fx = { money(-part(.3)); t("Все живы, семена сухие. Остальное взяла река.", "Everyone is alive and the seed is dry. The river took the rest.") }),
                )
            }),
        Event("kitsune", "inari", Tx("Лисьи огни", "Fox-Fires"), cond = { h -> fire(age(h) >= 14) },
            txt = { t("Осенней ночью над рисовыми полями зажглись огоньки — цепочкой, как свадебное шествие. Старики говорят: лисы выдают дочь замуж. И считают, сколько вас ещё осталось.", "On an autumn night lights came on over the paddies, in a line, like a wedding procession. The old people say the foxes are marrying off a daughter. And counting how many of you are left.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Отнести на алтарь Инари жареный тофу", "Leave fried tofu at the Inari shrine"), cost = 1, tag = t("1 мон", "1 mon"),
                        fx = { money(-1); if (chance(.5)) { g.bless = 1; t("Утром тофу на алтаре не было, а на вашем пороге лежала красная нитка. В следующем жребии будет лишний белый боб.", "By morning the tofu was gone from the altar, and a red thread lay on your doorstep. There will be an extra white bean in the next draw.") } else t("Утром тофу на алтаре не было. Кто съел — собака или лиса, — не узнать.", "By morning the tofu was gone. Dog or fox, there is no telling.") }),
                    Opt(t("Пойти за огнями", "Follow the lights"), roll = Roll(VER, -1, mapOf("hrab" to 1, "bogom" to 1)),
                        ok = { if (g.hex > g.year) { g.hex = 0; t("Огни довели вас до красных ворот святилища и погасли. Второй красный боб пропал из мешка.", "The lights led you to the shrine's red gate and went out. The second red bean is gone from the bag.") } else { h.stones = maxOf(0, h.stones - 1); glory(1); t("Огни довели вас до красных ворот святилища и погасли. Домой вы вернулись на рассвете — спокойнее, чем когда-либо.", "The lights led you to the shrine's red gate and went out. You came home at dawn, calmer than you have ever been.") } },
                        bad = { h[ZDR]--; t("Огни водили вас по полям до рассвета. Вернулись мокрые по пояс и с кашлем.", "The lights led you round the paddies till dawn. You came home soaked to the waist, with a cough.") }),
                    Opt(t("Закрыть ставни", "Close the shutters"), fx = { t("Огни горели до утра. В грязи у ворот — лисьи следы.", "The lights burned till morning. In the mud by the gate: fox tracks.") }),
                )
            }),
        Event("yoshi", "interior", Tx("Приёмный зять", "A Son for Another House"), cond = { h ->
                val sons = kidIn(14, 45) { it.male }
                val c = kidIn(17, 28) { it.male && it.spouse == null && !it.bast }
                fire(if (sons.size >= 2 && c.isNotEmpty()) c.last() else null) },
            txt = { o -> val x = o.xx
                t("В соседней деревне богатый дом Ода: три дочери и ни одного сына. Накодо пришла просить вашего ${x.name} в приёмные зятья. Он возьмёт их имя, их поля и их старшую дочь. Вам он будет уже не сын.",
                    "The Oda house in the next village is rich, with three daughters and no son. The go-between has come to ask for your ${x.name} as an adopted son-in-law. He would take their name, their fields and their eldest daughter. To you he would be a son no longer.") },
            opts = { o ->
                val x = o.xx
                val go: Engine.() -> Unit = { x.monk = true; x.adopted = true; flag("oda_son"); flag("oda_id", x.id); deed(x, t("Ушёл приёмным зятем в дом Ода", "Married into the Oda house and took its name")) }
                listOf(
                    Opt(t("Согласиться", "Agree"), fx = { go(); money(10); glory(1); t("Свадьбу сыграли у них. Теперь ${x.name} кланяется вам как гость.", "The wedding was at their house. Now ${x.name} bows to you as a guest.") }),
                    Opt(t("Торговаться о подарках", "Bargain over the gifts"), roll = Roll(HIT, 0, mapOf("zhad" to 1, "gor" to 1)),
                        ok = { go(); money(16); t("Ода дали вдвое против обычного. ${x.name} ушёл с сундуком и не обернулся.", "The Odas paid twice the usual. ${x.name} left with a chest and did not look back.") },
                        bad = { glory(-1); t("Ода обиделись и взяли зятя из другого дома. Над вами смеются.", "The Odas took offence and found a son-in-law elsewhere. People laugh at you.") }),
                    Opt(t("Оставить сына при себе", "Keep your son"), fx = { t("Накодо допила чай и ушла.", "The go-between finished her tea and left.") }),
                )
            }),
    ) + japanMoreEvents

    override val history = listOf(
        Event("ako", "village", Tx("Сорок семь", "The Forty-Seven"), y0 = 1703, y1 = 1704, p = .8, cond = { h -> fire(age(h) >= 14) },
            txt = { t("Сорок семь ронинов из Ако отомстили за своего князя: два года ждали, потом взяли голову обидчика. Теперь им велено вспороть себе животы, а сказители по всей стране поют о них.", "Forty-seven masterless samurai of Ako have avenged their lord. They waited two years, then took his enemy's head. Now they have been ordered to cut their bellies, and storytellers all over the country sing of them.") },
            opts = { o ->
                listOf(
                    Opt(t("Сказать детям: обид не прощают", "Tell the children: a wrong is never forgiven"), fx = { feud(1); glory(1); t("Дети запомнили. Курокава тоже.", "The children remembered. So did the Kurokawas.") }),
                    Opt(t("Сказать: месть съедает обоих", "Say: revenge eats both sides"), roll = Roll(SER, 0, mapOf("krot" to 1, "dobr" to 1)),
                        ok = { feud(-1); glory(1); t("Вы отнесли Курокаве кувшин сакэ. Пили молча, но пили.", "You took a jar of sake to the Kurokawas. You drank in silence, but you drank.") },
                        bad = { feud(1); t("Курокава решили, что вы над ними смеётесь.", "The Kurokawas thought you were mocking them.") }),
                    Opt(t("Пойти послушать сказителя", "Go and hear the storyteller"), cost = 1, tag = t("1 мон", "1 mon"), fx = { money(-1); t("Сказитель плакал. Вы тоже.", "The storyteller wept. So did you.") }),
                )
            }),
        Event("fuji", "field_dry", Tx("Пепел Фудзи", "Ash from Fuji"), y0 = 1707, y1 = 1708, p = 1.0, cond = { yes },
            txt = { t("Фудзи проснулась. Две недели с неба сыплется серый пепел, днём горят фонари, поля лежат как под грязным снегом.", "Fuji has woken. For two weeks gray ash has fallen from the sky; lanterns burn at noon, and the fields lie under something like dirty snow.") },
            opts = { o ->
                listOf(
                    Opt(t("Счищать пепел с полей", "Scrape the ash off the fields"), roll = Roll(SIL, 0, mapOf("bog" to 1, "hrab" to 1)),
                        ok = { t("Всю зиму носили пепел корзинами в овраг. К весне чеки задышали.", "All winter you carried the ash off in baskets to the ravine. By spring the paddies could breathe.") },
                        bad = { money(-part(.3)); t("С каждым дождём пепел возвращался. Поля оживут лет через десять.", "With every rain the ash came back. The fields will take ten years to recover.") }),
                    Opt(t("Просить помощи у властей", "Petition for relief"), roll = Roll(UM, 0, mapOf("gram" to 2)),
                        ok = { money(4); t("Сёгун собрал на помощь налог со всех княжеств. Кое-что даже дошло до вас.", "The shogun levied a relief tax on every domain. Some of it even reached you.") },
                        bad = { money(-2); t("Бумагу приняли, а деньги осели где-то по дороге.", "They took your petition, and the money stuck somewhere on the way.") }),
                    Opt(t("Уйти на заработки в Эдо", "Go to Edo to work"), fx = { addTr(o.h, "gor"); money(4); t("Носили воду и месили глину на стройках. Живые деньги.", "Hauling water and treading clay on building sites. Real money.") }),
                )
            }),
        Event("tenmei", "field_dry", Tx("Голодные годы Тэммэй", "The Tenmei Famine"), y0 = 1783, y1 = 1786, p = 1.0, cond = { yes },
            txt = { t("Летом изверглась Асама, пепел лёг на поля. Потом одно холодное лето за другим. По дорогам бредут люди с пустыми глазами, а в городах громят рисовые лавки.", "In summer Mount Asama erupted and ash covered the fields. Then came one cold summer after another. People with empty eyes walk the roads, and in the towns crowds are smashing the rice shops.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Делить последнее поровну", "Share the last of it equally"), roll = Roll(SER, 0, mapOf("dobr" to 1, "shch" to 1)),
                        ok = { t("Дотянули до весны на корнях и соломенной муке.", "You lasted till spring on roots and straw meal.") },
                        bad = { val c = killOne(true); if (c != null) { kill(c, t("голод", "hunger")); t("Не стало: ${c.name}.", "${c.name} is gone.") } else t("Дотянули, но еле.", "You lasted, barely.") }),
                    Opt(t("Идти громить лавку рисоторговца", "Go and smash the rice dealer's shop"), roll = Roll(SIL, -1, mapOf("hrab" to 1, "gnev" to 1)),
                        ok = { money(3); glory(1); addSecret(t("${h.name} ${sx(h, "громил", "громила")} рисовую лавку в Тэммэй", "${h.name} smashed a rice shop in the Tenmei years"), h); t("Рис рассыпали по улице, ничего не взяли — таков обычай. Наутро торговцы снизили цену.", "You scattered the rice in the street and took nothing; that is the custom. Next morning the dealers lowered their prices.") },
                        bad = { if (chance(.3)) { kill(h, t("казнён" + sx(h, "", "а") + " за погром", "executed for rioting")); t("Зачинщиков схватили и казнили.", "The ringleaders were caught and executed.") } else { addTr(h, "shram"); t("Стражники разогнали толпу палками.", "The guards broke up the crowd with staves.") } }),
                )
            }),
        Event("oshio", "war", Tx("Осио Хэйхатиро", "Oshio Heihachiro"), y0 = 1836, y1 = 1837, p = .7, cond = { h -> fire(age(h) >= 16) },
            txt = { t("Третий год голода. В Осаке бывший судейский чин Осио Хэйхатиро продал свои книги, раздал деньги голодным и поднял восстание: «Спасём народ!». Полгорода сгорело. И в ваших горах деревни встают, громят дома богачей. Толпа с соломенными знамёнами уже у соседей.",
                "The third year of famine. In Osaka a former magistrate's officer, Oshio Heihachiro, sold his books, gave the money to the hungry and rose in revolt: “Save the people!” Half the city burned. In your hills too the villages are rising and wrecking rich men's houses. A crowd with straw banners is at the next village.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Пристать к восставшим", "Join the rising"), roll = Roll(SIL, -2, mapOf("hrab" to 2, "gnev" to 1)),
                        ok = { money(6); glory(2); addSecret(t("${h.name} ходил${sx(h, "", "а")} с бунтовщиками в голодный год", "${h.name} marched with the rioters in the famine year"), h); t("Вернулись с рисом из амбара богача. Об этом лучше молчать.", "You came home with rice from a rich man's storehouse. Best not to talk about it.") },
                        bad = { if (chance(.5)) { kill(h, t("казнён" + sx(h, "", "а") + " за мятеж", "executed for rebellion")); t("Восстание разбили. Зачинщиков казнили у заставы.", "The rising was crushed. The ringleaders were executed at the barrier.") } else { addTr(h, "kalek"); t("Восстание разбили. Еле ушли, с перебитой ногой.", "The rising was crushed. You got away, barely, with a broken leg.") } }),
                    Opt(t("Спрятать семью рисоторговца", "Hide the rice dealer's family"), roll = Roll(HIT, 0, mapOf("dobr" to 1)),
                        ok = { money(5); glory(2); addRelic(t("Инро рисоторговца", "The rice dealer's inro"), "inro", mapOf(HIT to 1), t("подарено спасённым торговцем", "a gift from the dealer you saved")); t("Торговец выжил и подарил вам лаковую коробочку для лекарств, что носят на поясе. Хитрость +1 в жребиях.", "The dealer lived, and gave you the lacquered medicine case he wore at his belt. Cunning +1 in draws.") },
                        bad = { addTr(h, "shram"); t("Толпа нашла торговца — и вам досталось за компанию.", "The crowd found the dealer, and you got a beating along with him.") }),
                    Opt(t("Сидеть тихо", "Keep your head down"), fx = { t("Беда прошла стороной.", "The trouble passed you by.") }),
                )
            }),
        Event("kurofune", "road", Tx("Чёрные корабли", "The Black Ships"), y0 = 1853, y1 = 1854, p = 1.0, cond = { yes },
            txt = { t("В залив Эдо вошли чёрные корабли американцев — дымят и ходят без парусов. Говорят, пушки у них больше колокола. Сёгун строит береговые батареи и нанимает людей.", "Black American ships have sailed into Edo Bay. They smoke, and move without sails; their cannon are bigger than a temple bell, they say. The shogun is building shore batteries and hiring men.") },
            opts = { o ->
                listOf(
                    Opt(t("Наняться на стройку батарей", "Hire on to build the batteries"), fx = { money(4); addTr(o.h, "gor"); t("Полгода таскали землю в Синагаве. Варваров так и не увидели, а заработали хорошо.", "Half a year carrying earth at Shinagawa. You never saw a foreigner, but the pay was good.") }),
                    Opt(t("Купить у разносчика гравюру с кораблём", "Buy a print of the ship from a pedlar"), cost = 1, tag = t("1 мон", "1 mon"), fx = { money(-1); glory(1); t("На гравюре у корабля лицо демона. Дети боятся, соседи ходят смотреть.", "On the print the ship has a demon's face. The children are afraid of it, and the neighbors come round to look.") }),
                    Opt(t("Жить как жили", "Carry on as before"), fx = { t("Рису всё равно, кто там в заливе.", "The rice does not care who is in the bay.") }),
                )
            }),
        Event("korori", "plague", Tx("Холера", "Cholera"), y0 = 1858, y1 = 1859, p = 1.0, cond = { yes },
            txt = { t("Холера — здесь её зовут «короро»: покатился, и нет человека. Пришла из Нагасаки с иностранного корабля. В Эдо не хватает гробов.", "Cholera; here they call it korori, the thing that topples you. It came from Nagasaki on a foreign ship. In Edo they are running out of coffins.") },
            opts = { plague(this, true, false) }),
        Event("boshin", "war", Tx("Война Босин", "The Boshin War"), y0 = 1868, y1 = 1869, p = 1.0, cond = { h -> fire(age(h) in 16..50) },
            txt = { t("Сёгуну конец: войска императора идут на север по тракту, а северные князья стоят за старый порядок. Армии нужны носильщики и рис.", "The shogun has fallen. The emperor's army is marching north along the highway, and the northern lords are holding out for the old order. The army needs porters and rice.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Идти носильщиком", "Go as a porter"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)),
                        ok = { glory(3); money(2); addRelic(t("Боевой веер", "A war fan"), "fan", mapOf(SER to 1), t("${h.name}: подарен офицером в войну Босин", "${h.name}: a gift from an officer in the Boshin War")); deed(h, t(sx(h, "Ходил", "Ходила") + " с армией на север", "Marched north with the army")); t("Дошли до Айдзу и вернулись. Офицер на прощание подарил свой веер. Сердце +1 в жребиях.", "You got as far as Aizu and came home. The officer gave you his fan in parting. Heart +1 in draws.") },
                        bad = { if (chance(.5)) { kill(h, t("погиб" + sx(h, "", "ла") + " в войне Босин", "killed in the Boshin War")); t("Под Айдзу шальная пуля не разбирала, кто носильщик.", "At Aizu a stray bullet did not ask who was a porter.") } else { addTr(h, "kalek"); glory(1); t("Вернулись без пальцев на ноге, но живыми.", "You came back missing toes, but alive.") } }),
                    Opt(t("Отдать рис армии", "Give rice to the army"), cost = 5, tag = t("5 мон", "5 mon"), fx = { money(-5); glory(1); t("Рис ушёл на север. Расписку дали — на новой бумаге с новым гербом.", "The rice went north. They gave you a receipt, on new paper with a new crest.") }),
                    Opt(t("Уйти в горы", "Go up into the hills"), sin = "coward", fx = { t("Война прошла стороной. А соседи запомнили.", "The war passed by. The neighbors remembered.") }),
                )
            }),
        Event("chiso", "village", Tx("Земельная грамота", "The Land Certificate"), y0 = 1873, y1 = 1875, p = 1.0, cond = { yes },
            txt = { t("Новое правительство заново обмерило поля. Кто их пашет, тому выдают грамоту на землю, а подать берут деньгами, а не рисом.", "The new government has measured the fields again. Whoever farms the land gets a certificate for it, and the tax is now paid in money, not rice.") },
            opts = { o ->
                listOf(
                    Opt(t("Записать поля на себя", "Register the fields in your name"), cost = 10, tag = t("10 мон, дом растёт", "10 mon, the house grows"), fx = { money(-10); g.dvor = minOf(4, g.dvor + 1); glory(1); t("Земля теперь ваша — на бумаге с печатью.", "The land is yours now, on paper, with a seal.") }),
                    Opt(t("Податься в город на прядильню", "Go to town, to the spinning mill"), fx = { money(6); addTr(o.h, "gor"); t("Шёлкопрядильня, двенадцать часов в день. Зато живые деньги.", "A silk mill, twelve hours a day. But real money.") }),
                    Opt(t("Жить как жили", "Carry on as before"), fx = { t("Всё как было, только подать теперь в иенах.", "All as it was, except the tax is in yen now.") }),
                )
            }),
        Event("satsuma", "war", Tx("Сацума", "Satsuma"), y0 = 1877, y1 = 1877, p = .6, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Сайго Такамори поднял Сацуму. Против самураев правительство шлёт армию призывников — крестьянских сыновей. ${o.xx.name} в списке.", "Saigo Takamori has raised Satsuma in revolt. Against the samurai the government is sending an army of conscripts: farmers' sons. ${o.xx.name} is on the list.") },
            opts = { o -> conscript(this, o.xx, t("Сацумское восстание", "the Satsuma Rebellion")) }),
        Event("nichiro", "war", Tx("Война с Россией", "War with Russia"), y0 = 1904, y1 = 1905, p = .7, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Война с Россией. Под Порт-Артуром полки ложатся целиком, и деревни провожают всё новых. Повестка пришла и вам: ${o.xx.name}.", "War with Russia. At Port Arthur whole regiments are falling, and the villages keep seeing off more. The notice has come to you as well: ${o.xx.name}.") },
            opts = { o -> conscript(this, o.xx, t("Русско-японская война", "the Russo-Japanese War")) }),
        Event("kome", "village", Tx("Рисовые бунты", "The Rice Riots"), y0 = 1918, y1 = 1918, p = 1.0, cond = { h -> fire(age(h) >= 16) },
            txt = { t("Рис подорожал вчетверо. В Тояме рыбачки вышли к рисовым складам, и за месяц бунты пошли по всей стране.", "Rice costs four times what it did. In Toyama the fishermen's wives marched on the rice warehouses, and within a month there were riots all over the country.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Пойти с женщинами к рисоторговцу", "Go with the women to the rice dealer"), roll = Roll(SIL, -1, mapOf("hrab" to 1, "dobr" to 1)),
                        ok = { money(3); glory(1); t("Торговец открыл склад и продавал по старой цене.", "The dealer opened his warehouse and sold at the old price.") },
                        bad = { money(-4); addTr(h, "shram"); t("Приехала полиция, потом солдаты. Вам разбили голову.", "The police came, then the soldiers. Your head was split open.") }),
                    Opt(t("Продать свой рис по новой цене", "Sell your own rice at the new price"), sin = "greed", fx = { money(6); glory(-1); t("Деньги хорошие. Соседи запомнили.", "Good money. The neighbors remembered.") }),
                    Opt(t("Поделиться рисом с соседями", "Share your rice with the neighbors"), cost = 3, tag = t("3 мон", "3 mon"), fx = { money(-3); glory(2); t("В вашем переулке никто не голодал.", "Nobody in your lane went hungry.") }),
                )
            }),
        Event("kanto", "fire", Tx("Великое землетрясение", "The Great Earthquake"), y0 = 1923, y1 = 1923, p = .8, cond = { h -> fire(age(h) >= 14) },
            txt = { t("Первое сентября, перед полуднем, когда во всех домах разжигали жаровни к обеду. Земля ходила волнами. Токио и Иокогама горят. К вечеру ползут слухи, будто корейцы травят колодцы, и на дорогах встают дружины с бамбуковыми копьями.", "The first of September, just before noon, when every house was lighting its brazier for lunch. The ground moved in waves. Tokyo and Yokohama are burning. By evening a rumor is spreading that Koreans are poisoning the wells, and vigilante gangs with bamboo spears are stopping people on the roads.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Откапывать соседей из-под балок", "Dig the neighbors out of the rubble"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)),
                        ok = { glory(2); deed(h, t(sx(h, "Откапывал", "Откапывала") + " людей после землетрясения", "Dug people out after the earthquake")); t("Вытащили шестерых. Двое из них — дети Курокавы.", "You pulled out six people. Two were Kurokawa children.") },
                        bad = { if (chance(.3)) { kill(h, t("погиб" + sx(h, "", "ла") + " при землетрясении", "killed in the earthquake")); t("Второй толчок обрушил крышу.", "An aftershock brought the roof down.") } else { addTr(h, "shram"); money(-4); t("Балка рухнула вам на плечо. Дом треснул.", "A beam came down on your shoulder. The house is cracked through.") } }),
                    Opt(t("Бежать с детьми на открытое место", "Run with the children to open ground"), roll = Roll(UM, 0, mapOf("gor" to 1)),
                        ok = { t("Все живы. За спиной два дня горел город.", "Everyone is alive. Behind you the city burned for two days.") },
                        bad = { money(-part(.5)); val c = killOne(false); if (c != null) { kill(c, t("погиб" + sx(c, "", "ла") + " в пожаре после землетрясения", "died in the fires after the earthquake")); t("В толпе потеряли: ${c.name}.", "In the crowd you lost ${c.name}.") } else t("Огонь забрал половину добра.", "The fire took half of what you owned.") }),
                    Opt(t("Спрятать соседа-корейца от дружинников", "Hide your Korean neighbor from the vigilantes"), roll = Roll(HIT, 0, mapOf("dobr" to 1, "hrab" to 1)),
                        ok = { glory(1); deed(h, t(sx(h, "Спрятал", "Спрятала") + " соседа от самосуда", "Hid a neighbor from a lynch mob")); t("Три дня он сидел у вас в дровяном сарае. Дружинники приходили дважды и ушли ни с чем.", "For three days he sat in your woodshed. The gang came twice and left with nothing.") },
                        bad = { addTr(h, "shram"); t("Его нашли. Вас избили за то, что прятали, его увели. Вы так и не узнали, что с ним стало.", "They found him. You were beaten for hiding him, and he was taken away. You never learned what became of him.") }),
                )
            }),
        Event("senso", "war", Tx("Красная бумага", "The Red Paper"), y0 = 1941, y1 = 1944, p = .8, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Повестка на красной бумаге: ${o.xx.name} — в армию. Женщины на углу просят прохожих сделать по стежку на поясе в тысячу стежков: говорят, он бережёт от пуль.", "The call-up came on red paper: ${o.xx.name} to the army. Women on the street corner ask passers-by for a stitch each on a belt of a thousand stitches; they say it turns bullets.") },
            opts = { o -> listOf(Opt(t("Проводить", "See him off"), fx = { sendAway(o.xx, t("Тихоокеанская война", "the Pacific War")); t("Флажки, станция, поезд. Мать до последнего держит его за рукав.", "Flags, the station, the train. His mother holds his sleeve to the last.") })) }),
        Event("nochi", "field", Tx("Земельная реформа", "The Land Reform"), y0 = 1946, y1 = 1948, p = 1.0, cond = { yes },
            txt = { if (g.dvor >= 3) t("Война кончилась. Новый закон: у тех, кто сдаёт землю в аренду, её выкупают почти даром и отдают арендаторам. Ваши поля тоже в списках.", "The war is over. A new law: land that is rented out is bought up for almost nothing and handed to the tenants. Your fields are on the lists too.")
                else t("Война кончилась. Новый закон: землю, которую вы арендовали, теперь продают вам — почти даром.", "The war is over. A new law: the land you have rented all these years is to be sold to you, for almost nothing.") },
            opts = { o ->
                if (g.dvor >= 3) listOf(
                    Opt(t("Отдать по закону", "Give it up as the law says"), fx = { g.dvor = 2; glory(2); t("Землю раздали. Бывшие арендаторы кланяются вам на улице — уже как равные.", "The land was handed out. Your old tenants bow to you in the street, as equals now.") }),
                    Opt(t("Спорить в земельной комиссии", "Fight it in the land commission"), roll = Roll(UM, -1, mapOf("gram" to 2, "lzh" to 1)),
                        ok = { money(4); t("Отстояли сакэварню и два поля. Остальное ушло.", "You kept the brewery and two fields. The rest went.") },
                        bad = { g.dvor = 1; glory(-1); t("Комиссия решила не в вашу пользу. Ушло больше, чем ушло бы.", "The commission ruled against you. You lost more than you would have.") }),
                ) else listOf(
                    Opt(t("Выкупить свой надел", "Buy your plot"), cost = 3, tag = t("3 мон, дом растёт", "3 mon, the house grows"), fx = { money(-3); g.dvor = minOf(4, g.dvor + 1); glory(1); t("Впервые за три сотни лет земля под ногами — ваша.", "For the first time in three hundred years the ground under your feet is yours.") }),
                    Opt(t("Уехать в город", "Move to the city"), fx = { money(5); addTr(o.h, "gor"); t("Город отстраивается из пепла. Руки нужны везде.", "The city is rebuilding from the ashes. Every pair of hands is wanted.") }),
                )
            }),
    ) + japanMoreHistory
}
