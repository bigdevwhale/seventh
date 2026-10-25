package app.seventh.domain

import app.seventh.domain.Stat.HIT
import app.seventh.domain.Stat.SER
import app.seventh.domain.Stat.SIL
import app.seventh.domain.Stat.UM
import app.seventh.domain.Stat.VER
import app.seventh.domain.Stat.ZDR

/** Russia 1690–1950, drawn as a lubok: the beggar woman's curse, the barin, the recruit levy, 1812, emancipation, collectivisation. */
object Rus : Land("rus") {
    override val maleNames: List<String> get() = tl(
        listOf("Иван", "Фрол", "Степан", "Михей", "Савва", "Пётр", "Тимофей", "Еремей", "Кузьма", "Лука", "Никита", "Гаврила", "Демьян", "Прохор", "Ефим", "Яков", "Афанасий", "Захар", "Митрофан", "Игнат", "Данила", "Осип", "Сидор", "Фома", "Терентий", "Василий", "Григорий", "Матвей", "Семён", "Архип"),
        listOf("Ivan", "Frol", "Stepan", "Mikhei", "Savva", "Pyotr", "Timofei", "Yeremei", "Kuzma", "Luka", "Nikita", "Gavrila", "Demyan", "Prokhor", "Yefim", "Yakov", "Afanasy", "Zakhar", "Mitrofan", "Ignat", "Danila", "Osip", "Sidor", "Foma", "Terenty", "Vasily", "Grigory", "Matvei", "Semyon", "Arkhip"))
    override val femaleNames: List<String> get() = tl(
        listOf("Марфа", "Агафья", "Устинья", "Пелагея", "Дарья", "Арина", "Аксинья", "Евдокия", "Татьяна", "Матрёна", "Федосья", "Анна", "Варвара", "Василиса", "Прасковья", "Ульяна", "Фёкла", "Мавра", "Ирина", "Лукерья", "Акулина", "Глафира", "Степанида", "Ефросинья", "Настасья"),
        listOf("Marfa", "Agafya", "Ustinya", "Pelageya", "Darya", "Arina", "Aksinya", "Yevdokiya", "Tatyana", "Matryona", "Fedosya", "Anna", "Varvara", "Vasilisa", "Praskovya", "Ulyana", "Fyokla", "Mavra", "Irina", "Lukerya", "Akulina", "Glafira", "Stepanida", "Yefrosinya", "Nastasya"))
    override val stems: List<String> get() = tl(
        listOf("Воронов", "Лапин", "Ершов", "Сорокин", "Бородин", "Кречетов", "Глухов", "Рябов", "Зимин", "Поздеев", "Сычугов", "Огарков"),
        listOf("Voronov", "Lapin", "Ershov", "Sorokin", "Borodin", "Krechetov", "Glukhov", "Ryabov", "Zimin", "Pozdeev", "Sychugov", "Ogarkov"))
    override val places: List<String> get() = tl(
        listOf("Тверская губерния, село Медное", "Вологодский уезд, деревня Кубенка", "Рязанская губерния, село Ижевское", "Вятский край, починок Ключи", "Ярославская губерния, село Вощажниково", "Костромской уезд, деревня Ширяиха"),
        listOf("Tver Province, the village of Mednoye", "Vologda District, the village of Kubenka", "Ryazan Province, the village of Izhevskoye", "Vyatka Land, the clearing of Klyuchi", "Yaroslavl Province, the village of Voshchazhnikovo", "Kostroma District, the village of Shiryaikha"))
    override val families: List<String> get() = tl(
        listOf("Михеевых", "Пахомовых", "Лукиных", "Сычёвых", "Зуевых", "Хлыновых", "Кожемякиных"),
        listOf("the Mikheevs", "the Pakhomovs", "the Lukins", "the Sychovs", "the Zuevs", "the Khlynovs", "the Kozhemyakins"))
    override val dvor: List<String> get() = tl(
        listOf("курная изба", "изба с трубой", "крепкий двор", "хутор", "усадьба"),
        listOf("smoky hut", "hut with a chimney", "sturdy farmstead", "homestead of your own", "manor house"))
    override val years = 1688..1712
    override val edu0 = "В поле и на покос"
    override val eduBias = mapOf("В поле и на покос" to listOf("hrab", "gnev"), "К дьячку" to listOf("chest", "krot"), "На торг" to listOf("lzh", "zhad"), "К знахарке" to listOf("dobr", "shch"))
    override val rival: Rival get() = tl(Rival("Кожемякиных", "Кожемякиными", "Кожемякины"), Rival("the Kozhemyakins", "the Kozhemyakins", "the Kozhemyakins"))
    override val cur: String get() = t("руб.", "rub.")
    override val moneyLabel: String get() = t("Серебро", "Silver")
    override val introScene = "winter"
    override val nakaz: NakazText get() = tl(
        NakazText({ "Скопите $it рублей серебром — хватит жить впроголодь" }, "Пусть о нас говорят по всему уезду", { "Поставьте новый двор — $it" }, "Пусть в роду будет грамотный", "Помиритесь с Кожемякиными"),
        NakazText({ "Save $it rubles in silver — enough of living half-starved" }, "Let the whole district talk of us", { "Build a new home — $it" }, "Let someone in the family learn to read", "Make peace with the Kozhemyakins"))
    override val helpFeud: String get() = t("Вражда с соседями Кожемякиными. Чем она выше, тем чаще они мстят. Помирить может свадьба или уступка.",
        "The feud with the neighboring Kozhemyakins. The higher it runs, the more often they take revenge. A wedding or a concession can end it.")
    override val helpHeart: String get() = t("Сердце — люди, барин, торг, мир с соседями.", "Heart — people, the barin, trade, peace with the neighbors.")

    override fun family(g: Game) = t(g.stem + "ых", g.stem)
    override fun surname(g: Game, p: Person) = if (p.male) g.stem else g.stem + t("а", "a")
    override fun curseLog(e: Engine, f: Person) = t("Нищенка прокляла род ${f.name}${e.sx(f, "а", "ы")} до седьмого колена.", "A beggar woman cursed the family of ${f.name} to the seventh generation.")
    override fun cause(e: Engine, p: Person): String = with(e) {
        val a = age(p)
        when {
            a < 5 -> pick(listOf(t("горячка", "fever"), t("оспа", "smallpox"), t("родимчик", "convulsions"), t("корь", "measles"), t("глотошная", "the choking sickness")))
            a < 15 -> pick(listOf(t("горячка", "fever"), t("оспа", "smallpox"), t("утонул" + sx(p, "", "а") + " в омуте", "drowned in a millpool"), t("скарлатина", "scarlet fever")))
            a > 66 -> pick(listOf(t("старость", "old age"), t("старость", "old age"), t("удар", "a stroke"), t("сердце", "the heart")))
            else -> pick(listOf(t("чахотка", "consumption"), t("лихорадка", "ague"), t("водянка", "dropsy"), t("простуда", "a chill"), t("надорвал" + sx(p, "ся", "ась") + " на покосе", "worn out at the haymaking"), t("антонов огонь", "gangrene")))
        }
    }
    override fun mort(year: Int) = (if (year > 1900) .75 else 1.0) * (if (year > 1945) .55 else 1.0)
    override fun income(year: Int) = if (year > 1861) 1 else 0
    override fun intro(e: Engine, f: Person) = with(e) {
        listOf(
            t("${g.year} год. ${g.place}.", "The year ${g.year}. ${g.place}."),
            t("Жребий выпал: **${f.name} ${surname(f)}**, ${yrs(age(f))}, ${sx(f, "крестьянский сын", "крестьянская дочь")}. ${cap(f.tr.joinToString(", ") { tname(it, f) })}.",
                "The lot has fallen on **${f.name} ${surname(f)}**, ${sx(f, "a peasant's son", "a peasant's daughter")}, ${yrs(age(f))} old. ${cap(f.tr.joinToString(", ") { tname(it, f) })}."),
            t("Зимой в избу постучалась нищенка. ${f.name} не ${sx(f, "подал", "подала")} ей хлеба. «Будь проклят твой род до седьмого колена», — сказала старуха и ушла в метель.",
                "One winter a beggar woman knocked at the door of the hut. ${f.name} gave her no bread. “Cursed be your line to the seventh generation,” said the old woman, and went off into the snowstorm."),
        )
    }

    private fun recruitOpts(e: Engine, x: Person, war: String) = with(e) {
        listOf(
            Opt(t("Откупиться", "Buy a substitute"), cost = 12, tag = t("12 руб.", "12 rub."), fx = { money(-12); t("Сход взял деньги и отправил вместо вашего охочего сироту.", "The commune took the money and sent a willing orphan in place of yours.") }),
            Opt(t("Отдать: ${x.name}", "Give up: ${x.name}"), fx = { sendAway(x, war); t("${x.name} уходит с партией рекрутов. Мать выла у околицы до темноты.", "${x.name} leaves with the draft of recruits. ${sx(x, "His", "Her")} mother wailed at the village fence until dark.") }),
            Opt(t("Спрятать в лесу", "Hide in the forest"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1, "trus" to 1)),
                ok = { t("${x.name} пересидел${sx(x, "", "а")} в скиту у раскольников. Набор прошёл мимо.", "${x.name} sat it out at an Old Believers' hermitage. The levy passed by.") },
                bad = { sendAway(x, war); addTr(h, "shram"); t("${x.name} нашли и забрили, а вас высекли на миру.", "They found ${x.name} and took ${sx(x, "him", "her")} for a soldier, and you were flogged before the whole commune.") }),
        )
    }

    private fun plagueOpts(e: Engine) = with(e) {
        listOf(
            Opt(t("Уйти всей семьёй на заимку", "Take the whole family to the forest lodge"), roll = Roll(UM, 0, mapOf("gram" to 1)),
                ok = { t("Пересидели в лесу до морозов. Вернулись все.", "You sat it out in the forest until the frosts. Everyone came back.") },
                bad = { val c = killOne(true); if (c != null) { kill(c, t("моровое поветрие", "the pestilence")); t("Мор пришёл и в лес. Не стало: ${c.name}.", "The pestilence reached the forest too. Gone: ${c.name}.") } else t("Мор пришёл и в лес, но обошёл вас стороной.", "The pestilence reached the forest too, but passed you by.") }),
            Opt(t("Молиться и окуривать избу можжевельником", "Pray and smoke the hut with juniper"), roll = Roll(VER, 0, mapOf("bogom" to 1, "chest" to 1)),
                ok = { t("Мор обошёл ваш двор стороной.", "The pestilence passed your house by.") },
                bad = { val c = killOne(true); if (c != null) { kill(c, t("моровое поветрие", "the pestilence")); t("Не уберегли: ${c.name}.", "You could not save ${c.name}.") } else t("Переболели все, но выжили.", "Everyone fell sick, and everyone lived.") }),
            Opt(t("Ходить за больными соседями", "Nurse the sick neighbors"), roll = Roll(ZDR, -2, mapOf("dobr" to 2, "hrab" to 1)),
                ok = { o -> glory(3); deed(o.h, t(sx(o.h, "Выхаживал", "Выхаживала") + " соседей в мор", "Nursed the neighbors through the pestilence")); t("Вас обходит даже мор. По деревне говорят — святой человек.", "Even the pestilence steps around you. The village says you are a holy soul.") },
                bad = { o -> if (chance(.5)) { kill(o.h, t("моровое поветрие", "the pestilence")); t("Заразились у соседей.", "You caught it from the neighbors.") } else { addTr(o.h, "hvor"); t("Заразились, но выжили. Здоровье уже не то.", "You caught it, but lived. Your health is not what it was.") } }),
        )
    }

    override val forced = listOf(
        Event("match", "wedding", Tx("Сваха", "The Matchmaker"), cond = { h -> fire(age(h) in 16..49 && !married(h) && h.away == null && g.year >= g.noMatch) },
            txt = { o -> t("Сваха Матрёна раскладывает на столе три платка — по одному на ${sx(o.h, "каждую невесту", "каждого жениха")}. «Выбирай, ${o.h.name}. Только помни: что видно — то видно, а что под платком — узнаешь после венца».",
                "Matryona the matchmaker lays three kerchiefs on the table, one for each ${sx(o.h, "bride", "groom")}. “Choose, ${o.h.name}. Only remember: what is seen is seen, and what lies under the kerchief you will learn after the wedding.”") },
            opts = { o -> matchOpts(this, o, t("сер.", "silver"), t("что-то под платком", "something under the kerchief")) { p, h ->
                    var s = t("Сыграли свадьбу. ${p.name} ${sx(p, "вошёл в дом", "вошла в дом")}${if (p.dw > 0) " с приданым" else ""}.", "The wedding was held. ${p.name} came into the house${if (p.dw > 0) " with a dowry" else ""}.")
                    if (p.from == rival.g) { feud(-2); s += t(" Кожемякины пришли на свадьбу — впервые за много лет.", " The Kozhemyakins came to the wedding, the first time in many years.") }; s } +
                Opt(t("Повременить", "Wait a while"), tag = t("Сваха придёт через три года", "The matchmaker will come back in three years"), fx = { g.noMatch = g.year + 3; t("Сваха поджала губы и ушла.", "The matchmaker pursed her lips and left.") }) }),
        Event("edu", "interior", Tx("К какому делу?", "To What Work?"), cond = { fire(kidIn(7, 11) { it.edu == null }.firstOrNull()) },
            txt = { o -> val x = o.xx; t("${x.name}${if (x.bast) " (незаконн" + sx(x, "ый", "ая") + ")" else ""} уже ${yrs(age(x))}. Пора решать, к какому делу ${sx(x, "его", "её")} приставить. От этого зависит, ${sx(x, "каким он", "какой она")} вырастет.",
                "${x.name}${if (x.bast) " (born out of wedlock)" else ""} is ${yrs(age(x))} old already. Time to decide what work to set ${sx(x, "him", "her")} to. It will decide how ${sx(x, "he", "she")} turns out.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("В поле и на покос", "To the fields and the haymaking"), tag = t("Сила +2, Здоровье +1 · чаще храбрые и гневные", "Strength +2, Health +1 · often brave and hot-tempered"), fx = { x.edu = "В поле и на покос"; x[SIL] += 2; x[ZDR] += 1; t("${x.name} с утра до ночи в поле. Руки уже в мозолях.", "${x.name} is in the fields from dawn till dark. ${sx(x, "His", "Her")} hands are already calloused.") }),
                    Opt(t("К дьячку, учиться", "To the sexton, to learn"), cost = 2, tag = t("2 сер. · Ум +2, может выучиться грамоте · чаще честные и кроткие", "2 silver · Wits +2, may learn to read · often honest and meek"), fx = { money(-2); x.edu = "К дьячку"; x[UM] += 2; if (chance(.6)) { addTr(x, "gram"); t("${x.name} уже читает Псалтирь по складам.", "${x.name} already spells out the Psalter syllable by syllable.") } else t("Буквы ${x.name} не даются, зато счёт — хорошо.", "Letters will not come to ${x.name}, but sums come well enough.") }),
                    Opt(t("На торг, при лавке", "To the market, to mind a stall"), tag = t("Хитрость +2 · чаще лживые и жадные", "Cunning +2 · often lying and greedy"), fx = { x.edu = "На торг"; x[HIT] += 2; t("${x.name} бегает при лавке и уже знает, где обвесить, а где улыбнуться.", "${x.name} runs errands for a stall and already knows when to short the weight and when to smile.") }),
                    Opt(t("К бабке-знахарке", "To the old wise woman"), tag = t("Сердце +1, Вера +1, Здоровье +1 · чаще добрые и щедрые", "Heart +1, Faith +1, Health +1 · often kind and generous"), fx = { x.edu = "К знахарке"; x[SER]++; x[VER]++; x[ZDR]++; t("${x.name} знает уже сорок трав и все заговоры от лихоманки.", "${x.name} already knows forty herbs and every charm against the ague.") }),
                )
            }),
        Event("stones", "night_int", Tx("Камни на душе", "Stones on the Soul"), cond = { h -> fire(h.stones >= 5 && age(h) >= 14) },
            txt = { o -> t("Пять камней лежат на душе. Ночами не спится, а днём всё валится из рук. Так долго не протянуть.", "Five stones lie on your soul. No sleep at night, and by day everything falls from your hands. You cannot go on like this for long.") },
            opts = { o ->
                val l = mutableListOf(
                    Opt(t("Залить вином", "Drown it in drink"), tag = t("станет пьяницей", "becomes a drunkard"), fx = { addTr(o.h, "pyan"); o.h.stones = 2; t("Кабак принял вас как родного. Камни полегчали, кошелёк тоже.", "The alehouse took you in like kin. The stones grew lighter, and so did the purse.") }),
                    Opt(t("Сорвать злобу на домашних", "Take it out on the household"), tag = t("станет гневным, слава −1", "becomes hot-tempered, Glory −1"), fx = { addTr(o.h, "gnev"); o.h.stones = 1; glory(-1); t("Дети прячутся на печи, когда вы входите в избу.", "The children hide on top of the stove when you come into the hut.") }),
                    Opt(t("Каяться на богомолье", "Go on a pilgrimage of penance"), roll = Roll(VER, -1, mapOf("krot" to 1, "chest" to 1)),
                        ok = { o.h.stones = 0; o.h[VER]++; addTr(o.h, "bogom"); t("Сорок дней пешком до Троицы. Вернулись налегке.", "Forty days on foot to the Trinity Lavra. You came back unburdened.") },
                        bad = { o.h.stones = 4; o.h[ZDR]--; t("В дороге продуло. Камни остались, а кашель прибавился.", "You caught a chill on the road. The stones stayed, and a cough came too.") }),
                )
                if (desc(o.h).any { alive(it) && it.away == null }) l.add(monkOpt(this, o, t("Ворота обители закрылись за вами. Двор и род теперь — на детях.", "The cloister gates closed behind you. The farm and the family are the children's burden now.")))
                l
            }),
        Event("grudge", "interior", Tx("Обида старшего", "The Elder's Grudge"), cond = { val gp = person(g.grudge); fire(if (gp != null && alive(gp) && !gp.monk && chance(.35)) gp else null) },
            txt = { o -> val x = o.xx; t("${x.name} так и не ${sx(x, "простил", "простила")}, что род перешёл не к ${sx(x, "нему", "ней")}. «Отцово добро надо делить по-честному!» — кричит ${sx(x, "он", "она")} на весь двор.",
                "${x.name} has never forgiven that the family passed to someone else. “Father's goods must be shared fairly!” ${sx(x, "he", "she")} shouts across the whole yard.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Отдать треть серебра", "Give up a third of the silver"), tag = t("−${g.money / 3} сер.", "−${g.money / 3} silver"), fx = { money(-(g.money / 3)); g.grudge = null; deed(x, t(sx(x, "Ушёл", "Ушла") + " со своей долей в город", "Left for the town with ${sx(x, "his", "her")} share")); t("${x.name} ${sx(x, "забрал", "забрала")} долю и ${sx(x, "уехал", "уехала")} в город. Расстались без слёз, но и без проклятий.", "${x.name} took ${sx(x, "his", "her")} share and left for the town. You parted without tears, but without curses either.") }),
                    Opt(t("Отказать", "Refuse"), roll = Roll(SER, -1, mapOf("kras" to 1, "shch" to 1)),
                        ok = { g.grudge = null; t("${x.name} поворчал${sx(x, "", "а")} и остал${sx(x, "ся", "ась")} при доме. Кажется, обида прошла.", "${x.name} grumbled and stayed at home. The grudge seems to have passed.") },
                        bad = { money(-part(.3)); feud(2); g.grudge = null; t("Ночью сгорел амбар. ${x.name} ушл${sx(x, "", "а")} к Кожемякиным и там рассказывает про вас всякое.", "At night the barn burned down. ${x.name} has gone over to the Kozhemyakins and tells them all sorts of things about you.") }),
                    Opt(t("Выгнать со двора", "Drive ${sx(x, "him", "her")} off the farm"), sin = "cruel", fx = { g.grudge = null; g.hex = g.year + 6; t("${x.name} ушл${sx(x, "", "а")}, прокляв вас у ворот. В мешке с бобами на шесть лет прибавился второй красный.", "${x.name} left, cursing you at the gate. For six years a second red bean lies in the bag.") }),
                )
            }),
        Event("dream", "dream", Tx("Сон", "A Dream"), cond = { val n = g.nakaz; fire(n != null && !n.done && g.year - n.y >= 6 && g.year - (n.nag ?: n.y) >= 5 && chance(.4)) },
            txt = { val n = g.nakaz!!; n.nag = g.year; t("Снится покойный предок — ${n.by}. Стоит у печи, смотрит с укором: «${n.t}». И уходит, не оглядываясь.", "You dream of an ancestor long dead: ${n.by}, standing by the stove with a look of reproach. “${n.t}.” Then gone, without a backward glance.") },
            opts = { o ->
                listOf(
                    Opt(t("Проснуться в холодном поту", "Wake in a cold sweat"), tag = t("камень на душу", "a stone on the soul"), fx = { o.h.stones++; t("До утра так и не уснули.", "You did not sleep again until morning.") }),
                    Opt(t("Заказать панихиду", "Pay for a requiem"), cost = 2, tag = t("2 сер.", "2 silver"), fx = { money(-2); t("Батюшка отпел, свечи догорели. Сны прекратились — пока.", "The priest sang the office, the candles burned down. The dreams stopped — for now.") }),
                )
            }),
    )

    override val events = listOf(
        Event("bear", "forest", Tx("Медведь у околицы", "A Bear at the Fence"), w = 2, cond = { h -> fire(age(h) >= 15) },
            txt = { t("Шатун задрал корову у Михеевых и теперь кружит возле вашего двора. Бабы не пускают детей за ворота.", "A sleepless winter bear killed the Mikheevs' cow and now circles your farm. The women keep the children inside the gate.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Выйти с рогатиной", "Go out with a bear spear"), roll = Roll(SIL, -1, mapOf("hrab" to 2, "bog" to 1, "trus" to -2)),
                        ok = { glory(2); money(3); deed(h, t(sx(h, "Взял", "Взяла") + " медведя на рогатину", "Took a bear on the spear")); var s = t("Рогатина вошла под лопатку. Шкуру продали в городе за три рубля, а про вас теперь говорят на три деревни.", "The spear went in under the shoulder blade. The hide fetched three rubles in town, and now three villages talk of you."); if (chance(.3) && addTr(h, "shram")) s += t(" На память остался шрам через всю щёку.", " A scar across the whole cheek remains as a keepsake."); s },
                        bad = { if (chance(.2)) { kill(h, t("задрал медведь", "mauled by a bear")); t("Медведь оказался сильнее рогатины. Мужики подоспели слишком поздно.", "The bear proved stronger than the spear. The men came running too late.") } else { addTr(h, "kalek"); t("Зверь подмял вас под себя. Мужики отбили, но левая рука с тех пор не гнётся.", "The beast pinned you under it. The men beat it off, but your left arm has not bent since.") } }),
                    Opt(t("Пугнуть огнём и трещотками", "Scare it off with fire and rattles"), roll = Roll(UM), ok = { glory(1); t("Зверь ушёл за реку и больше не возвращался.", "The beast went across the river and never came back.") }, bad = { money(-4); t("Медведь не испугался трещоток и задрал вашу корову.", "The bear did not fear the rattles and killed your cow.") }),
                    Opt(t("Запереться и переждать", "Bar the door and wait"), sin = "coward", fx = { money(-3); t("Через три дня зверь ушёл сам, прихватив вашу тёлку.", "Three days later the beast left on its own, taking your heifer with it.") }),
                )
            }),
        Event("fire", "fire", Tx("Пожар", "Fire"), w = 2, cond = { yes },
            txt = { t("Ночью занялась соседская баня. Ветер несёт искры прямо на вашу соломенную крышу.", "In the night the neighbors' bathhouse caught fire. The wind carries sparks straight onto your thatched roof.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Лезть на крышу, сбивать огонь", "Climb on the roof and beat out the flames"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)),
                        ok = { deed(h, t(sx(h, "Отстоял", "Отстояла") + " двор от пожара", "Saved the farm from fire")); t("Крышу отстояли. Обгорели брови, зато цел двор.", "The roof held. Your eyebrows are singed, but the farm stands.") },
                        bad = { money(-part(.4)); if (g.dvor > 0) g.dvor--; addTr(h, "shram"); t("Двор сгорел дотла, вы сами еле спрыгнули. Ставить всё заново.", "The farm burned to the ground; you barely jumped clear. Everything must be built anew.") }),
                    Opt(t("Выносить детей и икону", "Carry out the children and the icon"), roll = Roll(VER, 2, mapOf("dobr" to 1)),
                        ok = { money(-part(.3)); t("Все живы, икона цела. Двор сгорел, но соседи помогли лесом.", "Everyone alive, the icon whole. The farm burned, but the neighbors helped with timber.") },
                        bad = { money(-part(.3)); val v = house().filter { it.id != g.head && age(it) < 10 }; if (v.isNotEmpty()) { val c = pick(v); kill(c, t("сгорел" + sx(c, "", "а") + " на пожаре", "burned in a fire")); t("В дыму не нашли ребёнка — ${c.name}…", "In the smoke they could not find a child: ${c.name}…") } else t("Икона сгорела. Говорят, это худая примета.", "The icon burned. They say that is an ill omen.") }),
                    Opt(t("Будить всю деревню", "Rouse the whole village"), roll = Roll(SER, 0, mapOf("dobr" to 1, "kras" to 1)), ok = { glory(1); t("Деревня встала цепью с вёдрами. Сгорела только баня.", "The village formed a bucket chain. Only the bathhouse burned.") }, bad = { money(-part(.3)); t("Пока собирались, занялся и ваш двор.", "While they gathered, your farm caught too.") }),
                )
            }),
        Event("fair", "fair", Tx("Ярмарка", "The Fair"), w = 2, cond = { h -> fire(age(h) >= 14) },
            txt = { t("На Покров в уезде ярмарка: лён, кожи, свистульки, цыгане с конями, карусель и бородатая баба за копейку.", "On Intercession Day there is a fair in the district: flax, hides, clay whistles, horse-traders with their horses, a carousel, and a bearded woman for a kopeck.") },
            opts = { o ->
                listOf(
                    Opt(t("Торговаться до хрипоты", "Haggle till you are hoarse"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)), ok = { money(6); t("Продали лён втридорога. Купец ещё и руку жал.", "You sold the flax at three times its worth. The merchant even shook your hand.") }, bad = { money(-2); t("Цыган сменял вам хромую кобылу на здоровую. То есть наоборот.", "A horse-trader swapped you a lame mare for a sound one. That is, the other way round.") }),
                    Opt(t("Продать честно", "Sell honestly"), roll = Roll(SER, 2, mapOf("chest" to 1)), ok = { money(3); glory(1); t("Купцы запомнили честного продавца и звали приезжать ещё.", "The merchants remembered the honest seller and asked you to come again.") }, bad = { money(1); t("Честность нынче не в цене. Продали за бесценок.", "Honesty fetches nothing these days. You sold for a song.") }),
                    Opt(t("Гулять на все", "Spend it all on merrymaking"), sin = "greed", fx = { money(-minOf(5, g.money)); if (chance(.35) && addTr(o.h, "pyan")) t("Гуляли три дня. С ярмарки вы вернулись с больной головой и новой привычкой.", "Three days of merrymaking. You came home from the fair with a sore head and a new habit.") else t("Гуляли три дня. Купили детям петушков на палочке.", "Three days of merrymaking. You bought the children sugar cockerels on sticks.") }),
                )
            }),
        Event("beggar", "road", Tx("Странница у ворот", "A Wanderer at the Gate"), w = 2, cond = { h -> fire(age(h) >= 14) },
            txt = { t("Старуха в лохмотьях просит хлеба. Глаза у неё точь-в-точь как, говорят, были у той, что прокляла основателя рода.", "An old woman in rags begs for bread. Her eyes, they say, are the very eyes of the one who cursed the founder of the family.") },
            opts = { o ->
                listOf(
                    Opt(t("Накормить и оставить ночевать", "Feed her and give her a bed for the night"), cost = 1, tag = t("1 сер.", "1 silver"), fx = { money(-1); glory(1); if (chance(.5)) { g.bless = 1; t("Утром её не было, а на пороге лежала красная нитка. В следующем жребии будет лишний белый боб.", "In the morning she was gone, and a red thread lay on the threshold. The next draw will hold an extra white bean.") } else t("Старуха поела, перекрестила избу и ушла до света.", "The old woman ate, blessed the hut with the sign of the cross, and left before dawn.") }),
                    Opt(t("Дать корку и проводить", "Give her a crust and see her off"), fx = { t("Старуха поклонилась и ушла, не оглядываясь.", "The old woman bowed and left without looking back.") }),
                    Opt(t("Прогнать собаками", "Set the dogs on her"), sin = "cruel", fx = { g.hex = g.year + 5; t("У околицы старуха обернулась и что-то сказала. В мешке с бобами на пять лет прибавился второй красный.", "At the village fence the old woman turned and said something. For five years a second red bean lies in the bag.") }),
                )
            }),
        Event("meja", "meja", Tx("Межа", "The Boundary"), w = 2, cond = { h -> fire(age(h) >= 15) },
            txt = { t("Кожемякины ночью перенесли межевые колья на сажень в ваш покос.", "In the night the Kozhemyakins moved the boundary stakes a fathom into your hayfield.") },
            opts = { o ->
                listOf(
                    Opt(t("Идти к барину судиться", "Take it to the barin"), roll = Roll(UM, 0, mapOf("gram" to 2)), ok = { feud(1); glory(1); t("Барин велел вернуть колья. Кожемякины затаили злобу.", "The barin ordered the stakes put back. The Kozhemyakins nursed their spite.") }, bad = { money(-3); t("Приказчик взял рубль с вас и два с них. Колья остались где стояли.", "The steward took a ruble from you and two from them. The stakes stayed where they were.") }),
                    Opt(t("Выдернуть колья, а Кожемякину — вбить ума", "Pull up the stakes and knock some sense into Kozhemyakin"), sin = "cruel", roll = Roll(SIL, 0, mapOf("gnev" to 1, "bog" to 2)), ok = { feud(2); glory(1); t("Покос ваш. Старший Кожемякин теперь ходит кривой.", "The hayfield is yours. The elder Kozhemyakin walks crooked now.") }, bad = { feud(1); addTr(o.h, "shram"); t("Кожемякиных было четверо.", "There were four Kozhemyakins.") }),
                    Opt(t("Уступить сажень", "Yield the fathom"), sin = "coward", fx = { feud(-1); glory(-1); t("Мир дороже покоса. Над вами посмеиваются, зато Кожемякины кланяются.", "Peace is worth more than hay. People snicker at you, but the Kozhemyakins bow.") }),
                )
            }),
        Event("peace", "interior", Tx("Мировая", "Making Peace"), w = 3, cond = { fire(if (g.feud >= 2) kidIn(15, 24) { it.spouse == null }.firstOrNull() else null) },
            txt = { o -> val x = o.xx; t("Старый Кожемякин пришёл без шапки: «Хватит нам грызться. У меня ${sx(x, "внучка на выданье", "внук в женихах")}. Поженим их — и дело с концом». Речь о том, кто у вас зовётся ${x.name}.",
                "Old Kozhemyakin came cap in hand: “Enough of this snapping at each other. I have a ${sx(x, "granddaughter of an age to marry", "grandson of an age to wed")}. Let us marry them and be done with it.” He means your ${x.name}.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Сосватать", "Make the match"), fx = { val s = mkSpouse(x, rival.g); g.feud = 0; glory(1); t("Сыграли свадьбу на две деревни. ${x.name} и ${s.name} — и вражде конец.", "Two villages feasted at the wedding. ${x.name} and ${s.name}, and the feud is over.") }),
                    Opt(t("Выставить за дверь", "Show him the door"), fx = { feud(1); t("Вражда достанется и внукам.", "The feud will pass to the grandchildren too.") }),
                )
            }),
        Event("revenge", "night_int", Tx("Месть Кожемякиных", "The Kozhemyakins' Revenge"), w = 3, cond = { fire(g.feud >= 3) },
            txt = { t("Ночью кто-то подпёр вашу дверь колом и бросил в окно горящую паклю.", "In the night someone wedged your door shut with a stake and threw burning tow through the window.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Выломать дверь и догнать", "Break down the door and give chase"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)), ok = { feud(-1); glory(1); t("Догнали младшего Кожемякина у реки. Больше не сунутся.", "You caught the youngest Kozhemyakin by the river. They will not try it again.") }, bad = { money(-4); addTr(h, "shram"); t("В темноте вы налетели на вилы.", "In the dark you ran onto a pitchfork.") }),
                    Opt(t("Подать урядникам", "Complain to the constables"), roll = Roll(UM, 0, mapOf("gram" to 2)), ok = { feud(-1); money(2); t("Кожемякиных оштрафовали в вашу пользу.", "The Kozhemyakins were fined, and the fine went to you.") }, bad = { feud(1); t("Свидетелей нет. Кожемякины посмеиваются.", "No witnesses. The Kozhemyakins snicker.") }),
                    Opt(t("Ответить тем же", "Pay them back in kind"), sin = "cruel", roll = Roll(HIT, 0, mapOf("lzh" to 1, "zhest" to 1)), ok = { money(3); addSecret(t("${h.name} ${sx(h, "поджёг", "подожгла")} овин Кожемякиных", "${h.name} set fire to the Kozhemyakins' drying barn"), h); t("Овин Кожемякиных сгорел. Никто ничего не видел.", "The Kozhemyakins' drying barn burned. Nobody saw a thing.") }, bad = { feud(2); glory(-2); t("Вас видели. Теперь об этом знает весь уезд.", "You were seen. Now the whole district knows.") }),
                )
            }),
        Event("recruit", "war", Tx("Рекрутский набор", "The Recruit Levy"), cond = { h -> fire(if (g.year in 1722..1873) sonOf(h).firstOrNull() else null) },
            txt = { o -> t("Из уезда пришла бумага: с общины одного рекрута. Мир решил — ${o.xx.name}. Служба — двадцать пять лет, а то и навек.", "A paper came from the district: one recruit from the commune. The commune has chosen ${o.xx.name}. The service is twenty-five years, if not for good.") },
            opts = { o -> recruitOpts(this, o.xx, "") }),
        Event("plague", "plague", Tx("Моровое поветрие", "Pestilence"), cond = { yes }, txt = { t("В уезде мор. В соседнем селе за неделю вынесли сорок гробов.", "Pestilence in the district. In the next village they carried out forty coffins in a week.") }, opts = { plagueOpts(this) }),
        Event("hunger", "field_dry", Tx("Недород", "A Failed Harvest"), w = 2, cond = { yes },
            txt = { t("Рожь выгорела на корню. До весны не дотянуть.", "The rye has withered where it stood. There is no lasting till spring.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Продать скотину", "Sell the cattle"), tag = t("−4 сер.", "−4 silver"), fx = { money(-4); t("Корову свели на ярмарку. Детей кормили лебедой и молоком от соседки.", "The cow was led off to the fair. The children ate goosefoot and drank the neighbor's milk.") }),
                    Opt(t("Просить у барина в долг", "Beg a loan from the barin"), roll = Roll(SER, 0, mapOf("krot" to 1, "kras" to 1)), ok = { money(2); t("Барин дал зерна под отработку.", "The barin lent grain against labor.") },
                        bad = { money(-3); val c = killOne(true); if (c != null && chance(.5)) { kill(c, t("голод", "hunger")); t("Барин был не в духе. К весне не стало: ${c.name}.", "The barin was out of sorts. By spring ${c.name} was gone.") } else t("Барин был не в духе. Пережили на мякине.", "The barin was out of sorts. You got by on chaff.") }),
                    Opt(t("Ночью накопать в барском амбаре", "Help yourself from the master's granary at night"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)),
                        ok = { money(4); addSecret(t("${h.name} в недород ${sx(h, "воровал", "воровала")} зерно из барского амбара", "${h.name} stole grain from the master's granary in a lean year"), h); t("Три мешка. Никто не хватился.", "Three sacks. Nobody missed them.") },
                        bad = { money(-3); addTr(h, "shram"); glory(-2); t("Поймали. Высекли на конюшне при всей деревне.", "Caught. Flogged in the stables before the whole village.") }),
                )
            }),
        Event("klad", "klad", Tx("Горшок в борозде", "A Pot in the Furrow"), cond = { h -> fire(age(h) >= 14) },
            txt = { t("Плуг выворотил глиняный горшок, а в нём — серебро времён царя Алексея Михайловича.", "The plow turned up a clay pot full of silver from the days of Tsar Alexei Mikhailovich.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Унести тайком", "Carry it off in secret"), sin = "lie", fx = { money(15); addSecret(t("${h.name} ${sx(h, "утаил", "утаила")} клад из поля", "${h.name} kept a treasure found in the field"), h); t("Пятнадцать рублей серебром — под половицу.", "Fifteen rubles in silver, under the floorboard.") }),
                    Opt(t("Отнести барину", "Take it to the barin"), fx = { money(3); glory(2); deed(h, t(sx(h, "Отдал", "Отдала") + " барину найденный клад", "Gave a found treasure to the barin")); t("Барин похвалил и отсыпал горсть монет.", "The barin praised you and poured you a handful of coins.") }),
                    Opt(t("Отдать на храм", "Give it to the church"), fx = { glory(3); addRelic(t("Образ Николы", "Icon of St Nicholas"), "icon", mapOf(VER to 1), t("${h.name}: клад отдан на храм, батюшка подарил образ", "${h.name}: the treasure went to the church, and the priest gave an icon")); t("Батюшка благословил и подарил вам образ Николы Угодника. Вера +1 в жребиях.", "The priest blessed you and gave you an icon of St Nicholas the Wonderworker. Faith +1 in draws.") }),
                )
            }),
        Event("secret", "interior", Tx("Старая тайна", "An Old Secret"), w = 3, cond = { val s = g.secrets.firstOrNull(); if (s != null && g.year - s.y >= 3) Fire(s = s) else null },
            txt = { o -> val x = o.s!!; t("По деревне шепчутся: ${x.t}. Дело было ${yrs(g.year - x.y)} назад${if (x.who != o.h.name) ", и того человека давно нет, но спрашивают с вас" else ""}.",
                "The village is whispering that ${x.t}. It happened ${yrs(g.year - x.y)} ago${if (x.who != o.h.name) ", and that person is long gone, but it is you they ask" else ""}.") },
            opts = { o ->
                listOf(
                    Opt(t("Отпереться", "Deny it"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)), ok = { g.secrets.removeAt(0); t("Пошептались и забыли.", "They whispered and forgot.") }, bad = { g.secrets.removeAt(0); glory(-3); feud(1); t("Нашлись свидетели. Слава рода поблёкла.", "Witnesses turned up. The family's good name has faded.") }),
                    Opt(t("Покаяться на миру", "Repent before the commune"), roll = Roll(VER, 2, mapOf("chest" to 1, "krot" to 1)), ok = { g.secrets.removeAt(0); glory(1); o.h.stones = maxOf(0, o.h.stones - 1); t("Мир простил. Даже уважать стали.", "The commune forgave you. They even respect you for it.") }, bad = { g.secrets.removeAt(0); glory(-2); t("Мир не простил, но хоть на душе легче.", "The commune did not forgive, but at least your soul is lighter.") }),
                    Opt(t("Заплатить, чтобы молчали", "Pay them to keep quiet"), cost = 8, tag = t("8 сер.", "8 silver"), fx = { money(-8); g.secrets.removeAt(0); t("Деньги заткнули самые громкие рты.", "Money stopped the loudest mouths.") }),
                )
            }),
        Event("bastard", "road", Tx("Ребёнок вдовы", "The Widow's Child"), cond = { h -> fire(h.male && married(h) && age(h) in 20..50) },
            txt = { t("У вдовы Аграфены с края деревни родился сын. Люди говорят, что глаза у него ваши.", "The widow Agrafena at the end of the village has borne a son. People say he has your eyes.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Признать и взять в дом", "Own him and take him in"), fx = { val c = bastard(this, h); val s = spouseOf(h); t("${c.name} теперь спит на печи с остальными.${if (s != null) " " + s.name + " неделю не разговаривала с вами." else ""}", "${c.name} now sleeps on the stove with the others.${if (s != null) " " + s.name + " did not speak to you for a week." else ""}") }),
                    Opt(t("Отпереться", "Deny it"), sin = "lie", fx = { addSecret(t("сын вдовы Аграфены — от ${h.name}", "the widow Agrafena's son was fathered by ${h.name}"), h); t("Аграфена промолчала. Но мальчик растёт, и глаза у него всё те же.", "Agrafena held her tongue. But the boy is growing, and his eyes are still the same.") }),
                    Opt(t("Тайком помогать деньгами", "Help with money in secret"), cost = 4, tag = t("4 сер.", "4 silver"), fx = { money(-4); addSecret(t("кто-то из ${g.stem}ых тайком носит деньги вдове Аграфене", "one of the ${g.stem}s is secretly taking money to the widow Agrafena"), h); t("Аграфена взяла молча.", "Agrafena took it without a word.") }),
                )
            }),
        Event("kabak", "tavern", Tx("Кабак", "The Alehouse"), w = 2, cond = { h -> fire(age(h) >= 16) },
            txt = { o -> if (has(o.h, "pyan")) t("Ноги сами несут к кабаку. Кум уже машет из окна.", "Your feet carry you to the alehouse of their own accord. Your old crony is already waving from the window.") else t("Кум зовёт в кабак «на одну чарку». Вы знаете, чем кончаются одни чарки.", "Your old crony calls you to the alehouse “for just one cup.” You know how just one cup ends.") },
            opts = { o ->
                listOf(
                    Opt(t("Пойти", "Go"), roll = Roll(VER, 0, mapOf("pyan" to -3, "krot" to 1, "bogom" to 1)), ok = { t("Выпили по одной, поговорили о погоде и разошлись.", "One cup each, a word about the weather, and home.") }, bad = { if (!addTr(o.h, "pyan")) { money(-4); t("Пропили сапоги и полушубок.", "You drank away your boots and your sheepskin coat.") } else t("Одна чарка, вторая, десятая. Кабак стал вам вторым домом.", "One cup, a second, a tenth. The alehouse became your second home.") }),
                    Opt(t("Отказаться", "Refuse"), fx = { if (has(o.h, "pyan")) { o.h.stones++; t("Отказались. Руки трясутся до вечера. Камень на душу.", "You refused. Your hands shook until evening. A stone on the soul.") } else t("Кум обиделся, но к Пасхе отошёл.", "Your crony took offense, but came round by Easter.") }),
                )
            }),
        Event("gramota", "church", Tx("Дьячок", "The Sexton"), cond = { h -> fire(!has(h, "gram") && age(h) in 12..45) },
            txt = { t("Дьячок Никодим берётся выучить вас грамоте за три рубля и мешок овса.", "Nikodim the sexton offers to teach you your letters for three rubles and a sack of oats.") },
            opts = { o ->
                listOf(
                    Opt(t("Учиться", "Learn"), cost = 3, tag = t("3 сер.", "3 silver"), roll = Roll(UM, 1, mapOf("umn" to 1)), ok = { money(-3); addTr(o.h, "gram"); deed(o.h, t(sx(o.h, "Выучился", "Выучилась") + " грамоте", "Learned to read and write")); t("К весне вы уже читали Псалтирь и считали оброк лучше приказчика.", "By spring you were reading the Psalter and reckoning the quitrent better than the steward.") }, bad = { money(-3); t("Буквы так и не сложились в слова. Овса жалко.", "The letters never came together into words. A pity about the oats.") }),
                    Opt(t("Не до того", "No time for that"), fx = { t("Дьячок ушёл учить Кожемякиных.", "The sexton went off to teach the Kozhemyakins.") }),
                )
            }),
        Event("wolves", "winter", Tx("Волчья зима", "A Wolf Winter"), w = 2, cond = { yes },
            txt = { t("Зима лютая. Ночами волки воют прямо под окнами и роют подкоп под хлев.", "A cruel winter. At night the wolves howl right under the windows and dig beneath the cattle shed.") },
            opts = { o ->
                listOf(
                    Opt(t("Сторожить с топором", "Keep watch with an axe"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)), ok = { glory(1); money(1); t("Положили двух волков. Шкуры — на тулуп младшим.", "You laid two wolves low. The skins went into coats for the little ones.") }, bad = { money(-4); if (chance(.3)) addTr(o.h, "shram"); t("Волки вырезали овец, пока вы отбивались от вожака.", "The wolves slaughtered the sheep while you fought off the leader.") }),
                    Opt(t("Поставить капканы", "Set traps"), roll = Roll(UM), ok = { money(3); t("Три волка в капканах. Шкуры продали.", "Three wolves in the traps. The skins were sold.") }, bad = { money(-2); t("В капкан попала соседская собака.", "The neighbors' dog got caught in a trap.") }),
                    Opt(t("Заложить ворота и молиться", "Bar the gate and pray"), fx = { money(-3); t("Утром хлев был пуст.", "In the morning the shed was empty.") }),
                )
            }),
        Event("sick", "night_int", Tx("Жар", "Fever"), w = 3, cond = { val k = kidIn(0, 9); fire(if (k.isEmpty()) null else pick(k)) },
            txt = { o -> val x = o.xx; t("${x.name} горит в жару третью ночь. Не ест, не узнаёт мать.", "${x.name} has burned with fever for three nights. ${sx(x, "He", "She")} will not eat and does not know ${sx(x, "his", "her")} mother.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Позвать знахарку", "Call the wise woman"), cost = 1, tag = t("1 сер.", "1 silver"), roll = Roll(ZDR, 1, ofX = true), ok = { money(-1); t("Знахарка отпоила малиной и шептала до рассвета. Жар спал.", "The wise woman dosed the child with raspberry tea and whispered until dawn. The fever broke.") }, bad = { money(-1); kill(x, t("горячка", "fever")); t("Знахарка развела руками.", "The wise woman spread her hands.") }),
                    Opt(t("Молиться всю ночь", "Pray all night"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)), ok = { t("К утру ${x.name} ${sx(x, "попросил", "попросила")} молока.", "By morning ${x.name} asked for milk.") }, bad = { kill(x, t("горячка", "fever")); t("Свеча догорела раньше, чем кончилась ночь.", "The candle burned out before the night did.") }),
                    Opt(t("Везти в город к лекарю", "Take the child to a doctor in town"), cost = 6, tag = t("6 сер.", "6 silver"), roll = Roll(UM, 4, mapOf("gram" to 1, "gor" to 1)), ok = { money(-6); t("Немец-лекарь дал порошков. Через неделю — на ногах.", "The German doctor gave powders. A week later the child was up and about.") }, bad = { money(-6); kill(x, t("горячка", "fever")); t("Не довезли.", "You did not reach the town in time.") }),
                )
            }),
        Event("deserter", "night_int", Tx("Беглый солдат", "A Runaway Soldier"), cond = { h -> fire(age(h) >= 16) },
            txt = { t("Ночью в окно стучит беглый солдат. Просит спрятать до весны.", "In the night a runaway soldier taps at the window. He asks to be hidden until spring.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Спрятать в овине", "Hide him in the drying barn"), roll = Roll(HIT, 0, mapOf("dobr" to 1, "lzh" to 1)), ok = { addRelic(t("Солдатский штык", "Soldier's Bayonet"), "sword", mapOf(SIL to 1), t("беглый солдат оставил его в благодарность", "a runaway soldier left it in thanks")); deed(h, t(sx(h, "Спрятал", "Спрятала") + " беглого солдата", "Hid a runaway soldier")); t("Весной он ушёл и оставил вам штык. Сила +1 в жребиях.", "In spring he left and gave you his bayonet. Strength +1 in draws.") }, bad = { addTr(h, "shram"); glory(-1); t("Его нашли у вас. Вас высекли.", "He was found on your farm. You were flogged.") }),
                    Opt(t("Выдать уряднику", "Hand him to the constable"), sin = "cruel", fx = { money(3); t("Дали три рубля. Солдата прогнали сквозь строй.", "You were paid three rubles. The soldier was made to run the gauntlet.") }),
                    Opt(t("Дать хлеба и прогнать", "Give him bread and send him off"), fx = { t("Он ушёл в метель.", "He went off into the snowstorm.") }),
                )
            }),
        Event("obrok", "barin", Tx("Новый барин", "The New Barin"), w = 2, cond = { h -> fire(g.year < 1861 && age(h) >= 16) },
            txt = { t("Новый барин приехал из Петербурга и поднял оброк вдвое.", "The new barin has come from Petersburg and doubled the quitrent.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Платить", "Pay"), tag = t("−8 сер.", "−8 silver"), fx = { money(-8); t("Затянули пояса.", "You tightened your belts.") }),
                    Opt(t("Идти просить всем миром", "Go and plead with the whole commune"), roll = Roll(SER, 0, mapOf("kras" to 1, "krot" to 1)), ok = { money(-4); glory(1); t("Барин уступил половину. Мир кланялся вам в пояс.", "The barin let half of it go. The commune bowed to you from the waist.") }, bad = { money(-8); t("Барин даже не вышел на крыльцо.", "The barin did not even come out onto the porch.") }),
                    Opt(t("Подбить мужиков на бунт", "Stir the men to revolt"), roll = Roll(SIL, -3, mapOf("hrab" to 2, "gnev" to 1, "bog" to 1)), ok = { glory(3); deed(h, t(sx(h, "Поднял", "Подняла") + " мужиков против барина", "Raised the men against the barin")); t("Приказчика выгнали, барин уехал обратно в Петербург.", "The steward was driven out, and the barin went back to Petersburg.") },
                        bad = { if (chance(.4)) { kill(h, t("запорот" + sx(h, "", "а") + " на конюшне", "flogged to death in the stables")); t("Зачинщиков пороли до смерти.", "The ringleaders were flogged to death.") } else { addTr(h, "kalek"); glory(1); t("Зачинщиков пороли. Вы выжили, но ходите с палкой.", "The ringleaders were flogged. You lived, but you walk with a stick.") } }),
                )
            }),
        Event("pilgrim", "church", Tx("Богомолье", "A Pilgrimage"), cond = { h -> fire(age(h) >= 16 && !has(h, "bogom")) },
            txt = { t("Соседи идут пешком к Троице-Сергию. Зовут с собой.", "The neighbors are walking to the Trinity Lavra of St Sergius. They ask you along.") },
            opts = { o ->
                listOf(
                    Opt(t("Идти", "Go"), roll = Roll(ZDR, 2, mapOf("krot" to 1)), ok = { addTr(o.h, "bogom"); o.h.stones = 0; glory(1); if (chance(.4)) addRelic(t("Лаврская иконка", "Little Icon from the Lavra"), "icon", mapOf(VER to 1), t("принесена с богомолья", "brought back from a pilgrimage")); t("Вернулись через два месяца налегке. Камни с души как рукой сняло.", "You came back two months later, unburdened. The stones were lifted from your soul as if by a hand.") }, bad = { o.h[ZDR]--; t("Простудились в дороге и вернулись с полпути.", "You caught a chill on the road and turned back halfway.") }),
                    Opt(t("Не время", "Not now"), fx = { t("Покос не ждёт.", "The haymaking will not wait.") }),
                )
            }),
        Event("smith", "village", Tx("Кузнец Аким", "Akim the Smith"), cond = { fire(g.relics.none { it.ic == "axe" }) },
            txt = { t("Кузнец Аким берётся сковать топор с вашим клеймом. «Такой и внуки не сточат».", "Akim the smith offers to forge an axe with your mark on it. “Your grandchildren won't grind this one down.”") },
            opts = { o ->
                listOf(
                    Opt(t("Заказать", "Order it"), cost = 4, tag = t("4 сер.", "4 silver"), fx = { money(-4); addRelic(t("Топор с клеймом", "Marked Axe"), "axe", mapOf(SIL to 1), t("скован для главы рода", "forged for the head of the family")); t("Топор лёг в руку как влитой. Сила +1 в жребиях.", "The axe sits in the hand as if it grew there. Strength +1 in draws.") }),
                    Opt(t("Обойдёмся старым", "The old one will do"), fx = { t("Старый топор ещё послужит.", "The old axe has some years in it yet.") }),
                )
            }),
        Event("town", "village", Tx("В город", "To the Town"), cond = { fire(kidIn(16, 25) { !has(it, "gor") && it.spouse == null }.firstOrNull()) },
            txt = { o -> t("${o.xx.name} рвётся в город: «Там фабрики, там жизнь. Что мне тут — коровам хвосты крутить?»", "${o.xx.name} is straining to go to the town: “There are factories there, there's life. What is there for me here — twisting cows' tails?”") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Отпустить", "Let ${sx(x, "him", "her")} go"), fx = { addTr(x, "gor"); x[HIT] += 1; x.away = g.year + ri(3, 7); t("${x.name} ${sx(x, "уехал", "уехала")} с обозом. Пишет редко.", "${x.name} left with a wagon train. Letters come seldom.") }),
                    Opt(t("Не пускать", "Keep ${sx(x, "him", "her")} home"), fx = { x.stones++; t("${x.name} остал${sx(x, "ся", "ась")}, но смотрит волком.", "${x.name} stayed, but glowers like a wolf.") }),
                )
            }),
    )

    override val history = listOf(
        Event("north", "war", Tx("Великая Северная война", "The Great Northern War"), y0 = 1700, y1 = 1721, p = .25, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Государь воюет со шведом. Рекрутов берут с каждых двадцати дворов, и жребий пал на вашего: ${o.xx.name}.", "The Tsar is at war with the Swede. Recruits are taken from every twenty households, and the lot has fallen on yours: ${o.xx.name}.") }, opts = { o -> recruitOpts(this, o.xx, t("Северная война", "the Northern War")) }),
        Event("chuma", "plague", Tx("Чума", "The Plague"), y0 = 1770, y1 = 1772, p = .6, cond = { yes }, txt = { t("Из Москвы пришла чума. Заставы на дорогах, костры на перекрёстках, по деревням возят мертвецов.", "The plague has come from Moscow. Barriers on the roads, bonfires at the crossroads, carts of the dead going through the villages.") }, opts = { plagueOpts(this) }),
        Event("pugach", "war", Tx("Пугачёвщина", "Pugachev's Rising"), y0 = 1773, y1 = 1775, p = .6, cond = { h -> fire(age(h) >= 16) },
            txt = { t("По Волге идёт «царь Пётр Фёдорович», вешает бар и жжёт усадьбы. Его люди уже в соседнем уезде.", "Up the Volga comes “Tsar Pyotr Fyodorovich,” hanging masters and burning manors. His men are already in the next district.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Пристать к Пугачёву", "Join Pugachev"), roll = Roll(SIL, -2, mapOf("hrab" to 2, "gnev" to 1)), ok = { money(6); glory(2); addSecret(t("${h.name} ходил${sx(h, "", "а")} с Пугачёвым", "${h.name} rode with Pugachev"), h); t("Вернулись с барским добром. Об этом лучше молчать.", "You came back with the masters' goods. Better say nothing about it.") },
                        bad = { if (chance(.5)) { kill(h, t("казнён" + sx(h, "", "а") + " за бунт", "executed for rebellion")); t("Отряд разбили. Зачинщиков повесили на воротах.", "The band was routed. The ringleaders were hanged from the gates.") } else { addTr(h, "kalek"); t("Отряд разбили. Еле ушли, с перебитой ногой.", "The band was routed. You barely got away, with a broken leg.") } }),
                    Opt(t("Спрятать барина в погребе", "Hide the barin in the cellar"), roll = Roll(HIT, 0, mapOf("dobr" to 1)), ok = { money(5); glory(2); addRelic(t("Барский перстень", "The Barin's Ring"), "ring", mapOf(HIT to 1), t("подарен спасённым барином", "a gift from the barin you saved")); t("Барин выжил и подарил перстень. Хитрость +1 в жребиях.", "The barin lived and gave you a ring. Cunning +1 in draws.") }, bad = { addTr(h, "shram"); t("Пугачёвцы нашли барина и выпороли вас за компанию.", "Pugachev's men found the barin and flogged you for good measure.") }),
                    Opt(t("Сидеть тихо", "Keep your head down"), fx = { t("Беда прошла стороной.", "The trouble passed you by.") }),
                )
            }),
        Event("y1812", "war", Tx("Двенадцатый год", "The Year 1812"), y0 = 1812, y1 = 1812, p = 1.0, cond = { h -> fire(age(h) in 16..50) },
            txt = { t("Француз под Смоленском. Губерния собирает ополчение: с каждых ста душ — десять ратников.", "The French are at Smolensk. The province is raising a militia: ten men from every hundred souls.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Идти в ополчение", "Join the militia"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)), ok = { glory(4); addRelic(t("Медаль 1812 года", "Medal of 1812"), "medal", mapOf(SER to 1), t("${h.name} — ополченец", "${h.name}, militia")); deed(h, t(sx(h, "Воевал", "Воевала") + " с французом", "Fought the French")); t("Дошли до Парижа и вернулись с медалью. Сердце +1 в жребиях.", "You marched all the way to Paris and came home with a medal. Heart +1 in draws.") },
                        bad = { if (chance(.5)) { kill(h, t("пал" + sx(h, "", "а") + " под Бородином", "fell at Borodino")); t("Под Бородином.", "At Borodino.") } else { addTr(h, "kalek"); glory(2); t("Вернулись без ноги, но с почётом.", "You came home without a leg, but with honor.") } }),
                    Opt(t("Дать обоз сена и лошадь", "Give a cart of hay and a horse"), cost = 5, tag = t("5 сер.", "5 silver"), fx = { money(-5); glory(1); t("Обоз ушёл к армии.", "The cart went off to the army.") }),
                    Opt(t("Прятаться в лесу", "Hide in the forest"), sin = "coward", fx = { t("Француз до вас не дошёл. А соседи запомнили.", "The French never reached you. But the neighbors remembered.") }),
                )
            }),
        Event("volya", "village", Tx("Воля", "Freedom"), y0 = 1861, y1 = 1863, p = 1.0, cond = { yes },
            txt = { t("В церкви читают манифест: крепостной неволе конец. Землю — за выкуп, на сорок девять лет.", "In church they read the manifesto: serfdom is at an end. The land is yours for redemption payments, over forty-nine years.") },
            opts = { o ->
                listOf(
                    Opt(t("Выкупать надел", "Buy out the allotment"), cost = 10, tag = t("10 сер., двор растёт", "10 silver, the farm grows"), fx = { money(-10); g.dvor = minOf(4, g.dvor + 1); glory(1); t("Земля теперь ваша. Своя!", "The land is yours now. Your own!") }),
                    Opt(t("Податься в город на фабрику", "Go to a factory in town"), fx = { money(6); addTr(o.h, "gor"); t("Ткацкая фабрика, двенадцать часов в день. Зато живые деньги.", "A weaving mill, twelve hours a day. But the money is real.") }),
                    Opt(t("Остаться при общине", "Stay with the commune"), fx = { t("Всё как было, только барина нет.", "Everything as it was, only without the barin.") }),
                )
            }),
        Event("golod", "field_dry", Tx("Царь-голод", "Tsar Hunger"), y0 = 1891, y1 = 1892, p = 1.0, cond = { yes },
            txt = { t("Засуха на всю Россию. Земство раздаёт муку, но её не хватает.", "Drought across all Russia. The zemstvo hands out flour, but there is not enough.") },
            opts = {
                listOf(
                    Opt(t("Стоять в очереди за земской мукой", "Stand in line for zemstvo flour"), roll = Roll(SER, 0, mapOf("gram" to 1)), ok = { t("Дотянули до весны.", "You held out till spring.") }, bad = { val c = killOne(true); if (c != null) { kill(c, t("голод", "hunger")); t("Не стало: ${c.name}.", "Gone: ${c.name}.") } else t("Дотянули, но еле.", "You held out, but only just.") }),
                    Opt(t("Продать всё и уехать за Урал", "Sell everything and go beyond the Urals"), fx = { money(8); g.dvor = maxOf(1, g.dvor); g.place = t("Сибирь, переселенческий участок", "Siberia, a settlers' allotment"); t("Переселенческий вагон, три недели пути. Новая земля, чёрная и жирная.", "A settlers' railway car, three weeks on the road. New land, black and rich.") }),
                )
            }),
        Event("ww1", "war", Tx("Германская", "The German War"), y0 = 1914, y1 = 1916, p = .7, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Мобилизация. ${o.xx.name} получил повестку.", "Mobilization. ${o.xx.name} has his call-up papers.") },
            opts = { o -> val x = o.xx; listOf(Opt(t("Проводить", "See him off"), fx = { sendAway(x, t("Германская", "the German War")); t("Гармошка, слёзы, эшелон.", "An accordion, tears, a troop train.") }), Opt(t("Спрятать", "Hide him"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)), ok = { t("Отсиделся в погребе.", "He sat it out in the cellar.") }, bad = { sendAway(x, t("Германская", "the German War")); t("Нашли. Отправили в маршевую роту.", "Found. Sent off with a marching company.") })) }),
        Event("rev", "war", Tx("Продотряд", "The Grain Squad"), y0 = 1918, y1 = 1921, p = .7, cond = { h -> fire(age(h) >= 16) },
            txt = { t("Во двор въезжает продотряд: «Хлеб — государству. Излишки сдать».", "A requisition squad rides into the yard: “Grain to the state. Hand over the surplus.”") },
            opts = { o ->
                listOf(
                    Opt(t("Отдать", "Hand it over"), fx = { money(-part(.5)); t("Выгребли до зёрнышка.", "They scraped out every last grain.") }),
                    Opt(t("Спрятать зерно в яме под хлевом", "Hide grain in a pit under the shed"), roll = Roll(HIT, 0, mapOf("lzh" to 1)), ok = { t("Не нашли.", "They did not find it.") }, bad = { money(-g.money); addTr(o.h, "shram"); t("Нашли. Забрали всё, вас били прикладами.", "Found. They took everything and beat you with rifle butts.") }),
                    Opt(t("Взять вилы", "Take up the pitchfork"), sin = "cruel", roll = Roll(SIL, -4, mapOf("hrab" to 2)), ok = { glory(2); t("Отряд уехал ни с чем. Надолго ли?", "The squad rode off empty-handed. For how long?") }, bad = { kill(o.h, t("расстрелян" + sx(o.h, "", "а") + " продотрядом", "shot by a requisition squad")); t("Вилы против винтовок.", "A pitchfork against rifles.") }),
                )
            }),
        Event("kolhoz", "village", Tx("Колхоз", "The Collective Farm"), y0 = 1929, y1 = 1932, p = 1.0, cond = { h -> fire(age(h) >= 16) },
            txt = { if (g.dvor >= 3) t("Ваш двор записали в кулацкие. Скоро придут описывать.", "Your farm has been listed as a kulak's. They will soon come to take inventory.") else t("В селе организуют колхоз. Всех зовут, а кто не идёт — тех на заметку.", "A collective farm is being set up in the village. Everyone is invited, and whoever does not join is marked down.") },
            opts = { o ->
                listOf(
                    Opt(t("Сдать всё в колхоз", "Hand everything over to the kolkhoz"), fx = { g.dvor = minOf(g.dvor, 1); money(-part(.7)); t("Корову увели на общий двор. Ходите на работу за трудодни.", "The cow was led off to the common yard. You go to work for labor-days.") }),
                    Opt(t("Уехать ночью в город", "Leave for the town by night"), roll = Roll(HIT, 0, mapOf("gor" to 2, "gram" to 1)), ok = { g.dvor = 1; addTr(o.h, "gor"); g.place = t("город, барак у завода", "Town, a barracks by the works"); t("Справка из сельсовета, поезд, завод. Живы.", "A paper from the village soviet, a train, a factory. Alive.") },
                        bad = { if (chance(.5)) { kill(o.h, t("умер" + sx(o.h, "", "ла") + " в ссылке", "died in exile")); t("Сняли с поезда. Нарым.", "Taken off the train. Narym.") } else { g.dvor = 0; money(-g.money); t("Вернули обратно и отобрали всё.", "Sent back, and stripped of everything.") } }),
                )
            }),
        Event("ww2", "war", Tx("Война", "The War"), y0 = 1941, y1 = 1943, p = .8, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Двадцать второго июня по радио объявили войну. ${o.xx.name} уходит на фронт.", "On the twenty-second of June the radio announced the war. ${o.xx.name} is going to the front.") },
            opts = { o -> listOf(Opt(t("Проводить", "See him off"), fx = { sendAway(o.xx, t("Великая Отечественная", "the Great Patriotic War")); t("Ушёл с первым эшелоном.", "He left with the first troop train.") })) }),
    )
}

/**
 * Three match candidates drawn once per happening; what is seen is seen, a hidden trait surfaces after the wedding.
 * [wed] writes the wedding line for the chosen one.
 */
fun matchOpts(e: Engine, o: Occ, cur: String, hiddenNote: String, wed: Engine.(Person, Person) -> String): List<Opt> = with(e) {
    val h = o.h
    if (o.cands.isEmpty()) {
        val male = !h.male
        val old = age(h) > 26
        repeat(3) {
            val a = if (old) ri(19, 34) else ri(16, 23)
            val p = newP(pickName(male, (o.cands.map { it.name } + h.name).toSet()), male, born = g.year - a, st = rstats(1, 8), from = pick(land.families))
            for ((x, y) in Traits.PAIRS.shuffled(rnd).take(2)) addTr(p, if (chance(.5)) x else y)
            if (chance(.35)) addTr(p, pick(Traits.KROV))
            if ("yaz" in g.perks) { addTr(p, pick(listOf("dobr", "shch", "chest", "kras", "umn"))); p[Stat.ZDR] = maxOf(p[Stat.ZDR], 5) }
            if (chance(.6)) {
                val l = listOf("gnev", "zhad", "lzh", "bespl", "hvor", "dobr", "umn", "bog", "kras", "pyan").filter { !has(p, it) && Traits.all.getValue(it).opp?.let { op -> has(p, op) } != true }
                if (l.isNotEmpty()) p.hidden = Hidden(pick(l), g.year + ri(1, 4))
            }
            p.dw = ri(-6, 8) + if (p.from == land.rival.g) 4 else 0
            p.grown = true
            o.cands.add(p)
        }
    }
    o.cands.map { p ->
        val top = Stat.entries.maxBy { eff(p, it) }
        Opt(t("${p.name} из ${p.from}, ${yrs(age(p))}", "${p.name} of ${p.from}, ${yrs(age(p))}"),
            tag = "${p.tr.joinToString(", ") { tname(it, p) }} · ${STAT_NAME[top]} ${eff(p, top)} · ${if (p.dw >= 0) t("приданое +", "dowry +") + p.dw else t("выкуп ", "bride price ") + (-p.dw)} $cur${if (p.hidden != null) " · [[$hiddenNote]]" else ""}",
            cost = if (p.dw < 0) -p.dw else 0, cand = p,
            fx = { reg(p); p.spouse = h.id; h.spouse = p.id; p.wed = g.year; h.wed = g.year; money(p.dw); wed(p, h) })
    }
}

fun monkOpt(e: Engine, o: Occ, text: String) = with(e) {
    Opt(t("Уйти в монастырь", sx(o.h, "Enter a monastery", "Enter a convent")), tag = t("[[вы выбираете наследника]]", "[[you choose the heir]]"), fx = {
        o.h.monk = true
        deed(o.h, t(sx(o.h, "Ушёл", "Ушла") + " в монастырь", sx(o.h, "Entered a monastery", "Entered a convent")))
        log(t("${o.h.name} ${sx(o.h, "ушёл", "ушла")} в монастырь.", "${o.h.name} ${sx(o.h, "entered a monastery", "entered a convent")}."), "big")
        text
    })
}

/** A son born on the side, taken into the house. */
fun bastard(e: Engine, h: Person): Person = with(e) {
    val c = reg(newP(pickName(true, (kidsOf(h).map { it.name } + h.name).toSet()), true, father = h.id, blood = true, gen = h.gen + 1, bast = true,
        st = IntArray(6) { (h.st[it] + ri(-2, 1)).coerceIn(1, 9) }, look = Look(h.look.skin, h.look.hair, pick(Looks.CLOTH_M), pick(Looks.KERCH))))
    glory(-1)
    log(t("В дом взят незаконный сын главы рода — ${c.name}.", "The head of the family's natural son was taken into the house: ${c.name}."), "birth")
    c
}
