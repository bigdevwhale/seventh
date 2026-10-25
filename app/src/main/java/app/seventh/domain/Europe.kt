package app.seventh.domain

import app.seventh.domain.Stat.HIT
import app.seventh.domain.Stat.SER
import app.seventh.domain.Stat.SIL
import app.seventh.domain.Stat.UM
import app.seventh.domain.Stat.VER
import app.seventh.domain.Stat.ZDR

/** Northern France 1180–1460, drawn as an illuminated manuscript: the burnt herbwoman's curse, crusades, the Black Death, the Jacquerie, the Maid. */
object Europe : Land("eu") {
    override val maleNames: List<String> get() = tl(
        listOf("Жан", "Пьер", "Гийом", "Робер", "Бертран", "Гуго", "Рено", "Тибо", "Анри", "Готье", "Жоффруа", "Ив", "Ламбер", "Одон", "Рауль", "Симон", "Этьен", "Жиль", "Бодуэн", "Филипп", "Матье", "Николя", "Арно", "Мартен", "Тьерри", "Обри", "Ангерран"),
        listOf("Jean", "Pierre", "Guillaume", "Robert", "Bertrand", "Hugues", "Renaud", "Thibaut", "Henri", "Gautier", "Geoffroy", "Yves", "Lambert", "Eudes", "Raoul", "Simon", "Étienne", "Gilles", "Baudouin", "Philippe", "Mathieu", "Nicolas", "Arnaud", "Martin", "Thierry", "Aubry", "Enguerrand"))
    override val femaleNames: List<String> get() = tl(
        listOf("Жанна", "Изабо", "Маго", "Аэлис", "Бланш", "Эрменгарда", "Марго", "Перонель", "Агнесса", "Беатриса", "Изабель", "Жаклин", "Эмелина", "Алиенора", "Петронилла", "Ода", "Сибилла", "Тифейна", "Гизела", "Матильда", "Эрсанда", "Клеменция"),
        listOf("Jeanne", "Isabeau", "Mahaut", "Aélis", "Blanche", "Ermengarde", "Margot", "Péronnelle", "Agnès", "Béatrice", "Isabelle", "Jacqueline", "Émeline", "Aliénor", "Pétronille", "Ode", "Sibylle", "Tiphaine", "Gisèle", "Mathilde", "Ersende", "Clémence"))
    override val stems: List<String> get() = tl(
        listOf("Лефевр", "Мюнье", "Буше", "Пастурель", "Ламбер", "Дюбуа", "Леблан", "Ланглуа", "Фурнье", "Тексье", "Шарпантье", "Лебрен"),
        listOf("Lefèvre", "Meunier", "Boucher", "Pastourel", "Lambert", "Dubois", "Leblanc", "Langlois", "Fournier", "Texier", "Charpentier", "Lebrun"))
    override val places: List<String> get() = tl(
        listOf("Шампань, деревня Сен-Мартен-о-Буа", "Нормандия, деревня Бонпор", "Пикардия, деревня Ла-Фер", "Иль-де-Франс, деревня Вильнёв", "Бургундия, деревня Мон-Сен-Жан", "Анжу, деревня Ле-Шен"),
        listOf("Champagne, the village of Saint-Martin-aux-Bois", "Normandy, the village of Bonport", "Picardy, the village of La Fère", "Île-de-France, the village of Villeneuve", "Burgundy, the village of Mont-Saint-Jean", "Anjou, the village of Le Chêne"))
    override val families: List<String> get() = tl(
        listOf("семьи Дюпон", "семьи Мартель", "семьи Ренар", "семьи Обер", "семьи Гарнье", "семьи Мерсье", "семьи Ле Гро"),
        listOf("the Duponts", "the Martels", "the Renards", "the Auberts", "the Garniers", "the Merciers", "the Le Gros"))
    override val dvor: List<String> get() = tl(
        listOf("лачуга виллана", "фахверковый дом", "крепкий двор", "своя мельница", "каменный дом с башней"),
        listOf("villein's hovel", "half-timbered house", "sturdy farmstead", "mill of your own", "stone house with a tower"))
    override val years = 1178..1196
    override val edu0 = "В поле"
    override val eduBias = mapOf("В поле" to listOf("hrab", "gnev"), "К кюре" to listOf("chest", "krot"), "В город" to listOf("lzh", "zhad"), "К травнице" to listOf("dobr", "shch"))
    override val traitNames: Map<String, Pair<String, String>> get() = tl(
        mapOf("bog" to ("силач" to "силачка"), "bogom" to ("паломник" to "паломница"), "gor" to ("горожанин" to "горожанка")),
        mapOf("bog" to ("strongman" to "strongwoman"), "bogom" to ("pilgrim" to "pilgrim"), "gor" to ("townsman" to "townswoman")))
    override val rival: Rival get() = tl(Rival("семьи Ле Гро", "семьёй Ле Гро", "Ле Гро"), Rival("the Le Gros", "the Le Gros", "the Le Gros"))
    override val cur: String get() = t("су", "sous")
    override val moneyLabel: String get() = t("Монеты", "Coin")
    override val introScene = "pyre"
    override val nakaz: NakazText get() = tl(
        NakazText({ "Скопите $it су — хватит жить впроголодь" }, "Пусть о нас говорят по всей округе", { "Поставьте новый дом — $it" }, "Пусть в роду будет грамотный", "Помиритесь с Ле Гро"),
        NakazText({ "Save $it sous — enough of living half-starved" }, "Let the whole countryside talk of us", { "Build a new house — $it" }, "Let someone in the family learn to read", "Make peace with the Le Gros"))
    override val helpFeud: String get() = t("Вражда с соседями Ле Гро. Чем она выше, тем чаще они мстят. Помирить может свадьба или уступка.",
        "The feud with the neighboring Le Gros. The higher it runs, the more often they take revenge. A wedding or a concession can end it.")
    override val helpHeart: String get() = t("Сердце — люди, сеньор, торг, мир с соседями.", "Heart — people, the seigneur, trade, peace with the neighbors.")

    override fun family(g: Game) = g.stem
    override fun surname(g: Game, p: Person) = g.stem
    override fun curseLog(e: Engine, f: Person) = t("Сожжённая травница прокляла род ${e.g.stem} до седьмого колена.", "A herbwoman burned at the stake cursed the ${e.g.stem} line to the seventh generation.")
    override fun cause(e: Engine, p: Person): String = with(e) {
        val a = age(p)
        when {
            a < 5 -> pick(listOf(t("горячка", "fever"), t("оспа", "smallpox"), t("корь", "measles"), t("глотошная", "the choking sickness"), t("родимчик", "convulsions")))
            a < 15 -> pick(listOf(t("горячка", "fever"), t("оспа", "smallpox"), t("утонул" + sx(p, "", "а") + " в мельничном пруду", "drowned in the millpond"), t("лихорадка", "ague")))
            a > 64 -> pick(listOf(t("старость", "old age"), t("старость", "old age"), t("удар", "a stroke"), t("сердце", "the heart")))
            else -> pick(listOf(t("лихорадка", "ague"), t("антонов огонь", "gangrene"), t("чахотка", "consumption"), t("водянка", "dropsy"), t("надорвал" + sx(p, "ся", "ась") + " в поле", "worn out in the fields"), t("огонь святого Антония", "Saint Anthony's fire")))
        }
    }
    override fun mort(year: Int) = 1.05
    override fun intro(e: Engine, f: Person) = with(e) {
        listOf(
            t("${g.year} год. ${g.place}.", "The year ${g.year}. ${g.place}."),
            t("Жребий выпал: **${f.name} ${surname(f)}**, ${yrs(age(f))}, ${sx(f, "сын", "дочь")} виллана. ${cap(f.tr.joinToString(", ") { tname(it, f) })}.",
                "The lot has fallen on **${f.name} ${surname(f)}**, a villein's ${sx(f, "son", "daughter")}, ${yrs(age(f))} old. ${cap(f.tr.joinToString(", ") { tname(it, f) })}."),
            t("Когда в деревне жгли травницу Гильеметту, ${f.name} ${sx(f, "свидетельствовал", "свидетельствовала")} против неё. Уже в огне она крикнула: «Будь проклят твой род до седьмого колена!»",
                "When the village burned Guillemette the herbwoman, ${f.name} bore witness against her. From the flames she cried: “Cursed be your line to the seventh generation!”"),
        )
    }

    private fun levy(e: Engine, x: Person, war: String) = with(e) {
        listOf(
            Opt(t("Откупиться", "Buy a substitute"), cost = 12, tag = t("12 су", "12 sous"), fx = { money(-12); t("Сеньор взял серебро и записал вместо вашего бродягу из таверны.", "The seigneur took the silver and enrolled a vagabond from the tavern in place of yours.") }),
            Opt(t("Отдать: ${x.name}", "Give up: ${x.name}"), fx = { sendAway(x, war); t("${x.name} уходит за знаменем сеньора. Мать стоит у ворот до темноты.", "${x.name} marches off behind the seigneur's banner. ${sx(x, "His", "Her")} mother stands at the gate until dark.") }),
            Opt(t("Спрятать в лесу у угольщиков", "Hide with the charcoal burners in the forest"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1, "trus" to 1)),
                ok = { t("${x.name} пересидел${sx(x, "", "а")} в лесу у угольщиков. Глашатай ушёл ни с чем.", "${x.name} sat it out with the charcoal burners in the forest. The crier went away empty-handed.") },
                bad = { sendAway(x, war); money(-4); t("${x.name} нашли и увели, а вас оштрафовали на суде сеньора.", "They found ${x.name} and led ${sx(x, "him", "her")} away, and the seigneur's court fined you.") }),
        )
    }

    internal fun plague(e: Engine, sev: Boolean) = with(e) {
        listOf(
            Opt(t("Запереться всей семьёй в доме", "Shut the whole family in the house"), roll = Roll(UM, if (sev) -2 else 0, mapOf("gram" to 1)),
                ok = { t("Заколотили двери и окна, ели запасы. Поветрие обошло вас.", "You nailed up the doors and windows and lived on your stores. The sickness passed you by.") },
                bad = {
                    val c = killOne(true)
                    if (c == null) t("Язва пробралась и к вам, но все выжили.", "The plague got in to you as well, but everyone lived.")
                    else {
                        kill(c, t("моровая язва", "the plague"))
                        val c2 = if (sev) killOne(true) else null
                        if (c2 != null && chance(.5)) { kill(c2, t("моровая язва", "the plague")); t("Язва пробралась и за запертую дверь. Не стало двоих: ${c.name} и ${c2.name}.", "The plague got in behind the barred door. Two are gone: ${c.name} and ${c2.name}.") }
                        else t("Язва пробралась и за запертую дверь. Не стало: ${c.name}.", "The plague got in behind the barred door. Gone: ${c.name}.")
                    }
                }),
            Opt(t("Молиться святому Себастьяну", "Pray to Saint Sebastian"), roll = Roll(VER, if (sev) -2 else 0, mapOf("bogom" to 1, "chest" to 1)),
                ok = { t("Вы обошли деревню с крестом и свечой. Поветрие отступило от вашего порога.", "You walked round the village with a cross and a candle. The sickness drew back from your door.") },
                bad = { val c = killOne(true); if (c != null) { kill(c, t("моровая язва", "the plague")); t("Не уберегли: ${c.name}.", "You could not save ${c.name}.") } else t("Переболели все, но выжили.", "Everyone fell sick, and everyone lived.") }),
            Opt(t("Ходить за больными соседями", "Nurse the sick neighbors"), roll = Roll(ZDR, -2, mapOf("dobr" to 2, "hrab" to 1)),
                ok = { o -> glory(3); deed(o.h, t(sx(o.h, "Выхаживал", "Выхаживала") + " больных в поветрие", "Nursed the sick through the plague")); t("Вас обходит даже язва. По деревне говорят: святой человек.", "Even the plague steps around you. The village says you are a holy soul.") },
                bad = { o -> if (chance(.5)) { kill(o.h, t("моровая язва", "the plague")); t("Заразились у соседей.", "You caught it from the neighbors.") } else { addTr(o.h, "hvor"); t("Заразились, но выжили. Здоровье уже не то.", "You caught it, but lived. Your health is not what it was.") } }),
        )
    }

    override val forced = listOf(
        Event("match", "wedding", Tx("Сговор", "The Betrothal"), cond = { h -> fire(age(h) in 16..49 && !married(h) && h.away == null && g.year >= g.noMatch) },
            txt = { o -> pick(listOf(t("Тётушка Аэлис, что сводит женихов и невест по всей округе, усаживает вас у очага. «Есть ${sx(o.h, "три невесты", "три жениха")}, ${o.h.name}. Лица видно, приданое известно. А что у кого на сердце, узнаешь после венчания».",
                "Old Aunt Aélis, who pairs off brides and grooms all over the countryside, sits you down by the hearth. “There are three ${sx(o.h, "brides", "grooms")}, ${o.h.name}. The faces you can see, the dowries are known. What lies in their hearts you will learn after the wedding.”"), t("Тётушка Аэлис опять на пороге — в лучшем чепце и с новостями. «Сиди, ${o.h.name}, не вставай. Я ${sx(o.h, "невест", "женихов")} привела — на словах, конечно. Трое. Выбирай, пока не разобрали».", "Aunt Aélis is at the door again, in her best cap and full of news. “Sit, ${o.h.name}, don't get up. I've brought you ${sx(o.h, "brides", "grooms")}, in words, of course. Three of them. Choose before they're taken.”"), t("После мессы кюре задержал вас на паперти: «Негоже в твои годы жить ${sx(o.h, "одному", "одной")}. Тётушка Аэлис уже всё разузнала». Аэлис выглядывает из-за его плеча и загибает три пальца.", "After Mass the curé holds you back on the church steps: “It isn't fitting to live alone at your age. Aunt Aélis has found out everything already.” Aélis peers over his shoulder and counts off three fingers."))) },
            opts = { o -> matchOpts(this, o, t("су", "sous"), t("что-то скрывает", "hiding something")) { p, _ ->
                    var s = t("Обвенчались на паперти, как велит обычай. ${p.name} ${sx(p, "вошёл в дом", "вошла в дом")}${if (p.dw > 0) " с приданым" else ""}.", "You were wed at the church door, as custom demands. ${p.name} came into the house${if (p.dw > 0) " with a dowry" else ""}.")
                    if (p.from == rival.g) { feud(-2); s += t(" Ле Гро пришли на свадьбу — впервые за много лет.", " The Le Gros came to the wedding, the first time in many years.") }; s } +
                Opt(t("Повременить", "Wait a while"), tag = t("Тётушка Аэлис придёт через три года", "Aunt Aélis will come back in three years"), fx = { g.noMatch = g.year + 3; t("Тётушка Аэлис поджала губы и ушла.", "Aunt Aélis pursed her lips and left.") }) }),
        Event("edu", "interior", Tx("К какому делу?", "To What Work?"), cond = { fire(kidIn(7, 11) { it.edu == null }.firstOrNull()) },
            txt = { o -> val x = o.xx; pick(listOf(t("${x.name}${if (x.bast) " (незаконн" + sx(x, "ый", "ая") + ")" else ""} уже ${yrs(age(x))}. Пора решать, к какому делу ${sx(x, "его", "её")} приставить. От этого зависит, ${sx(x, "каким он", "какой она")} вырастет.",
                "${x.name}${if (x.bast) " (born out of wedlock)" else ""} is ${yrs(age(x))} old already. Time to decide what work to set ${sx(x, "him", "her")} to. It will decide how ${sx(x, "he", "she")} turns out."), t("Пора приставить ${x.name} к делу: в ${yrs(age(x))} уже не дитя. Кюре, суконщик и травница — каждый уверяет, что выучит лучше прочих.", "Time ${x.name} was put to work: at ${yrs(age(x))} old, no longer a baby. The curé, the draper and the herbwoman each swear they would teach best."), t("За ужином ${x.name} спросил${sx(x, "", "а")}, кем ${sx(x, "ему", "ей")} быть. За столом замолчали: в ${yrs(age(x))} пора решать.", "At supper ${x.name} asked what ${sx(x, "he", "she")} should become. The table fell silent: at ${age(x)}, it is time to decide."))) },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("В поле, за плугом", "To the fields, behind the plow"), tag = t("Сила +2, Здоровье +1 · чаще храбрые и гневные", "Strength +2, Health +1 · often brave and hot-tempered"), fx = { x.edu = "В поле"; x[SIL] += 2; x[ZDR] += 1; t("${x.name} с рассвета ходит за плугом. Ладони уже как подошва.", "${x.name} walks behind the plow from first light. ${sx(x, "His", "Her")} palms are already tough as shoe leather.") }),
                    Opt(t("К кюре, учиться латыни", "To the curé, to learn Latin"), cost = 2, tag = t("2 су · Ум +2, может выучиться грамоте · чаще честные и кроткие", "2 sous · Wits +2, may learn to read · often honest and meek"), fx = { money(-2); x.edu = "К кюре"; x[UM] += 2; if (chance(.6)) { addTr(x, "gram"); t("${x.name} уже читает «Отче наш» по-латыни и пишет своё имя.", "${x.name} already reads the Pater Noster in Latin and can write ${sx(x, "his", "her")} own name.") } else t("Латынь не даётся, зато счёт — хорошо.", "Latin will not come, but sums come well enough.") }),
                    Opt(t("В город, к суконщику", "To the town, to a draper"), tag = t("Хитрость +2 · чаще лживые и жадные", "Cunning +2 · often lying and greedy"), fx = { x.edu = "В город"; x[HIT] += 2; t("${x.name} подметает лавку суконщика и уже знает, где отмерить локоть покороче.", "${x.name} sweeps the draper's shop and already knows when to measure the ell a little short.") }),
                    Opt(t("К травнице", "To the herbwoman"), tag = t("Сердце +1, Вера +1, Здоровье +1 · чаще добрые и щедрые", "Heart +1, Faith +1, Health +1 · often kind and generous"), fx = { x.edu = "К травнице"; x[SER]++; x[VER]++; x[ZDR]++; t("${x.name} знает уже сорок трав. Только об этом лучше не болтать — травниц нынче жгут.", "${x.name} already knows forty herbs. Best not to talk about it — they burn herbwomen these days.") }),
                )
            }),
        Event("stones", "night_int", Tx("Камни на душе", "Stones on the Soul"), cond = { h -> fire(h.stones >= 5 && age(h) >= 14) },
            txt = { t("Пять камней лежат на душе. Ночами не спится, а днём всё валится из рук. Так долго не протянуть.", "Five stones lie on your soul. No sleep at night, and by day everything falls from your hands. You cannot go on like this for long.") },
            opts = { o ->
                val l = mutableListOf(
                    Opt(t("Залить вином", "Drown it in wine"), tag = t("станет пьяницей", "becomes a drunkard"), fx = { addTr(o.h, "pyan"); o.h.stones = 2; t("Таверна приняла вас как родного. Камни полегчали, кошелёк тоже.", "The tavern took you in like kin. The stones grew lighter, and so did the purse.") }),
                    Opt(t("Сорвать злобу на домашних", "Take it out on the household"), tag = t("станет гневным, слава −1", "becomes hot-tempered, Glory −1"), fx = { addTr(o.h, "gnev"); o.h.stones = 1; glory(-1); t("Дети прячутся за очагом, когда вы входите.", "The children hide behind the hearth when you come in.") }),
                    Opt(t("Идти в Сантьяго-де-Компостелу", "Walk to Santiago de Compostela"), roll = Roll(VER, -1, mapOf("krot" to 1, "chest" to 1)),
                        ok = { o.h.stones = 0; o.h[VER]++; addTr(o.h, "bogom"); t("Год пути через Пиренеи и обратно. Вернулись с раковиной на шляпе и лёгкой душой.", "A year on the road, over the Pyrenees and back. You came home with a shell on your hat and a light heart.") },
                        bad = { o.h.stones = 4; o.h[ZDR]--; t("В горах вас ограбили и избили. Камни остались, а кашель прибавился.", "In the mountains you were robbed and beaten. The stones stayed, and a cough came too.") }),
                )
                if (desc(o.h).any { alive(it) && it.away == null }) l.add(monkOpt(this, o, t("Ворота аббатства закрылись за вами. Дом и род теперь — на детях.", "The abbey gates closed behind you. The house and the family are the children's burden now.")))
                l
            }),
        Event("grudge", "interior", Tx("Обида старшего", "The Elder's Grudge"), cond = { val gp = person(g.grudge); fire(if (gp != null && alive(gp) && !gp.monk && chance(.35)) gp else null) },
            txt = { o -> val x = o.xx; t("${x.name} так и не ${sx(x, "простил", "простила")}, что род перешёл не к ${sx(x, "нему", "ней")}. «Отцово добро надо делить по-честному!» — кричит ${sx(x, "он", "она")} на весь двор.",
                "${x.name} has never forgiven that the family passed to someone else. “Father's goods must be shared fairly!” ${sx(x, "he", "she")} shouts across the whole yard.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Отдать треть денег", "Give up a third of the money"), tag = t("−${g.money / 3} су", "−${g.money / 3} sous"), fx = { money(-(g.money / 3)); g.grudge = null; deed(x, t(sx(x, "Ушёл", "Ушла") + " со своей долей в город", "Left for the town with ${sx(x, "his", "her")} share")); t("${x.name} ${sx(x, "забрал", "забрала")} долю и ${sx(x, "ушёл", "ушла")} в город. Расстались без слёз, но и без проклятий.", "${x.name} took ${sx(x, "his", "her")} share and left for the town. You parted without tears, but without curses either.") }),
                    Opt(t("Отказать", "Refuse"), roll = Roll(SER, -1, mapOf("kras" to 1, "shch" to 1)),
                        ok = { g.grudge = null; t("${x.name} поворчал${sx(x, "", "а")} и остал${sx(x, "ся", "ась")} при доме. Кажется, обида прошла.", "${x.name} grumbled and stayed at home. The grudge seems to have passed.") },
                        bad = { money(-part(.3)); feud(2); g.grudge = null; t("Ночью сгорел амбар. ${x.name} ушл${sx(x, "", "а")} к Ле Гро и там рассказывает про вас всякое.", "At night the barn burned down. ${x.name} has gone over to the Le Gros and tells them all sorts of things about you.") }),
                    Opt(t("Выгнать со двора", "Drive ${sx(x, "him", "her")} out of the house"), sin = "cruel", fx = { g.grudge = null; g.hex = g.year + 6; t("${x.name} ушл${sx(x, "", "а")}, прокляв вас у ворот. В мешке с бобами на шесть лет прибавился второй красный.", "${x.name} left, cursing you at the gate. For six years a second red bean lies in the bag.") }),
                )
            }),
        Event("dream", "dream", Tx("Сон", "A Dream"), cond = { fire(dreamDue()) },
            txt = { val n = g.nakaz!!; n.nag = g.year; pick(listOf(t("Снится покойный предок — ${n.by}. Стоит у очага, смотрит с укором: «${n.t}». И уходит, не оглядываясь.", "You dream of an ancestor long dead: ${n.by}, standing by the hearth with a look of reproach. “${n.t}.” Then gone, without a backward glance."), t("Во сне вы снова ребёнком сидите у очага, а напротив — ${n.by}. Не ест, только смотрит и повторяет: «${n.t}».", "In the dream you are a child again by the hearth, and across from you sits ${n.by}, who does not eat, only looks at you and repeats: “${n.t}.”"), t("За ставнем — голос предка, ${n.by}, тихий, как ветер в соломе: «${n.t}». Утром на подоконнике сырая земля.", "Beyond the shutter, an ancestor's voice, ${n.by}, soft as wind in thatch: “${n.t}.” In the morning there is damp earth on the sill."))) },
            opts = { o ->
                listOf(
                    Opt(t("Проснуться в холодном поту", "Wake in a cold sweat"), tag = t("камень на душу", "a stone on the soul"), fx = { o.h.stones++; t("До утра так и не уснули.", "You did not sleep again until morning.") }),
                    Opt(t("Заказать заупокойную мессу", "Pay for a requiem Mass"), cost = 2, tag = t("2 су", "2 sous"), fx = { money(-2); t("Кюре отслужил мессу, свечи догорели. Сны прекратились — пока.", "The curé sang the Mass, the candles burned down. The dreams stopped — for now.") }),
                )
            }),
    )

    override val events = listOf(
        Event("boar", "forest", Tx("Вепрь", "The Boar"), cond = { h -> fire(age(h) >= 15) },
            txt = { pick(listOf(t("Матёрый вепрь из сеньорова леса третью ночь роет ваш ячмень. Охотиться в лесу сеньора вилланам запрещено — за это рубят руку.", "For the third night a great boar from the seigneur's forest has been rooting up your barley. Villeins may not hunt in the seigneur's forest; the penalty is a hand."), t("Сеньоров вепрь снова в вашем поле: полосы ячменя будто вспаханы заново. Лесничий сеньора разводит руками, а за вепря из сеньорова леса виллану рубят руку.", "The seigneur's boar is in your field again: the barley strips look plowed anew. The seigneur's forester spreads his hands, and for a boar from the seigneur's forest a villein loses a hand."))) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Выйти с рогатиной", "Go out with a boar spear"), roll = Roll(SIL, -1, mapOf("hrab" to 2, "bog" to 1, "trus" to -2)),
                        ok = { glory(2); money(3); deed(h, t(sx(h, "Взял", "Взяла") + " вепря на рогатину", "Took a boar on the spear")); addSecret(t("${h.name} убил${sx(h, "", "а")} вепря из сеньорова леса", "${h.name} killed a boar from the seigneur's forest"), h); var s = t("Вепрь лёг в борозде. Мясо засолили, а откуда кабан, молчит вся деревня.", "The boar fell in the furrow. The meat was salted, and the whole village keeps quiet about where it came from."); if (chance(.3) && addTr(h, "shram")) s += t(" Клык оставил шрам через всю ногу.", " A tusk left a scar the length of your leg."); s },
                        bad = { if (chance(.2)) { kill(h, t("убит" + sx(h, "", "а") + " вепрем", "killed by a boar")); t("Вепрь оказался сильнее рогатины.", "The boar proved stronger than the spear.") } else { addTr(h, "kalek"); t("Зверь распорол вам бедро. Выжили, но хромаете.", "The beast tore open your thigh. You lived, but you limp.") } }),
                    Opt(t("Просить сеньора прислать егерей", "Ask the seigneur to send his huntsmen"), roll = Roll(SER, 0, mapOf("kras" to 1)), ok = { money(-1); t("Сеньор устроил охоту с собаками и рогами. Вепря взяли, посевы вытоптали, но хоть так.", "The seigneur held a hunt with hounds and horns. The boar was taken and the crops trampled, but better than nothing.") }, bad = { money(-4); t("Сеньору не до вас. Вепрь доел ячмень.", "The seigneur has no time for you. The boar finished the barley.") }),
                    Opt(t("Не связываться", "Leave it alone"), sin = "coward", fx = { money(-3); t("Вепрь ушёл сам, когда ячменя не осталось.", "The boar left on its own once the barley was gone.") }),
                )
            }),
        Event("fire", "fire", Tx("Пожар", "Fire"), cond = { yes },
            txt = { pick(listOf(t("Ночью занялась соседская солома. Ветер несёт искры прямо на вашу крышу.", "In the night the neighbors' straw caught fire. The wind carries sparks straight onto your roof."), t("Молния ударила в стог у самого двора. Солома вспыхнула разом, и огонь уже лижет стену хлева.", "Lightning struck the haystack right by the house. The straw went up all at once, and the fire is already licking the byre wall."), t("Кто-то из детей опрокинул лучину в сено. Дым уже валит из-под стрехи.", "One of the children knocked a rushlight into the hay. Smoke is already pouring from under the eaves."))) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Лезть на крышу, сбивать огонь", "Climb on the roof and beat out the flames"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)),
                        ok = { deed(h, t(sx(h, "Отстоял", "Отстояла") + " дом от пожара", "Saved the house from fire")); t("Крышу отстояли. Обгорели брови, зато цел дом.", "The roof held. Your eyebrows are singed, but the house stands.") },
                        bad = { money(-part(.4)); if (g.dvor > 0) g.dvor--; addTr(h, "shram"); t("Дом сгорел дотла, вы сами еле спрыгнули. Ставить всё заново.", "The house burned to the ground; you barely jumped clear. Everything must be built anew.") }),
                    Opt(t("Выносить детей и распятие", "Carry out the children and the crucifix"), roll = Roll(VER, 2, mapOf("dobr" to 1)),
                        ok = { money(-part(.3)); t("Все живы, распятие цело. Дом сгорел, но соседи помогли брёвнами.", "Everyone alive, the crucifix whole. The house burned, but the neighbors helped with timber.") },
                        bad = { money(-part(.3)); val v = house().filter { it.id != g.head && age(it) < 10 }; if (v.isNotEmpty()) { val c = pick(v); kill(c, t("сгорел" + sx(c, "", "а") + " на пожаре", "burned in a fire")); t("В дыму не нашли ребёнка — ${c.name}…", "In the smoke they could not find a child: ${c.name}…") } else t("Распятие сгорело. Кюре говорит, это худой знак.", "The crucifix burned. The curé says it is an ill sign.") }),
                    Opt(t("Бить в колокол", "Ring the church bell"), roll = Roll(SER, 0, mapOf("dobr" to 1, "kras" to 1)), ok = { glory(1); t("Деревня встала цепью с вёдрами от колодца. Сгорел только хлев.", "The village formed a bucket chain from the well. Only the byre burned.") }, bad = { money(-part(.3)); t("Пока сбегались, занялся и ваш дом.", "While they came running, your house caught too.") }),
                )
            }),
        Event("fair", "fair", Tx("Ярмарка в Труа", "The Fair at Troyes"), cond = { h -> fire(age(h) >= 14) },
            txt = { pick(listOf(t("В Шампани большая ярмарка: фламандское сукно, перец из-за моря, менялы-ломбардцы, жонглёры и медведь на цепи.", "A great fair in Champagne: Flemish cloth, pepper from over the sea, Lombard money-changers, jongleurs, and a bear on a chain."), t("В Провене майская ярмарка: шелка из Лукки, кордовская кожа, сарацинский сахар, менялы за столами и кукольник с Ренаром и Изенгрином.", "The May fair at Provins: silks from Lucca, Cordovan leather, Saracen sugar, money-changers at their tables, and a puppeteer playing Renart and Isengrim."), t("Ярмарка в Ланьи: сукно со всей Фландрии, шум, давка, и каждый второй норовит срезать кошель.", "The fair at Lagny: cloth from all over Flanders, noise, crush, and every other man trying to cut a purse."))) },
            opts = { o ->
                listOf(
                    Opt(t("Торговаться до хрипоты", "Haggle till you are hoarse"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)), ok = { money(6); t("Продали шерсть втридорога. Ломбардец ещё и руку жал.", "You sold the wool at three times its worth. The Lombard even shook your hand.") }, bad = { money(-2); t("Меняла подсунул обрезанную монету. Остались в убытке.", "A money-changer slipped you a clipped coin. You came away the poorer.") }),
                    Opt(t("Продать честно", "Sell honestly"), roll = Roll(SER, 2, mapOf("chest" to 1)), ok = { money(3); glory(1); mark("eu_lombard"); t("Купцы запомнили честного продавца и звали приезжать ещё.", "The merchants remembered the honest seller and asked you to come again.") }, bad = { money(1); t("Честность нынче не в цене. Продали за бесценок.", "Honesty fetches nothing these days. You sold for a song.") }),
                    Opt(t("Гулять на все", "Spend it all on merrymaking"), sin = "greed", fx = { money(-minOf(5, g.money)); if (chance(.35) && addTr(o.h, "pyan")) t("Гуляли три дня. С ярмарки вы вернулись с больной головой и новой привычкой.", "Three days of merrymaking. You came home from the fair with a sore head and a new habit.") else t("Гуляли три дня. Купили детям медовых пряников.", "Three days of merrymaking. You bought the children honey cakes.") }),
                )
            }),
        Event("beggar", "road", Tx("Старуха у ворот", "An Old Woman at the Gate"), cond = { h -> fire(age(h) >= 14) },
            txt = { pick(listOf(t("Старуха в лохмотьях просит хлеба. Говорят, такие же глаза были у травницы, которую сожгли при основателе рода.", "An old woman in rags begs for bread. They say the herbwoman burned in the founder's day had the very same eyes."), t("У ворот сидит старуха с клюкой. Собаки на неё не лают, а куры идут к ней, как к хозяйке. Просит хлеба.", "An old woman with a stick sits by the gate. The dogs do not bark at her, and the hens walk up to her as if she were their mistress. She asks for bread."))) },
            opts = { o ->
                listOf(
                    Opt(t("Накормить и оставить ночевать", "Feed her and give her a bed for the night"), cost = 1, tag = t("1 су", "1 sou"), fx = { money(-1); glory(1); mark("eu_crone"); if (chance(.5)) { g.bless = 1; t("Утром её не было, а на пороге лежала красная нитка. В следующем жребии будет лишний белый боб.", "In the morning she was gone, and a red thread lay on the threshold. The next draw will hold an extra white bean.") } else t("Старуха поела, перекрестила дом и ушла до света.", "The old woman ate, blessed the house with the sign of the cross, and left before dawn.") }),
                    Opt(t("Дать корку и проводить", "Give her a crust and see her off"), fx = { t("Старуха поклонилась и ушла, не оглядываясь.", "The old woman bowed and left without looking back.") }),
                    Opt(t("Прогнать собаками", "Set the dogs on her"), sin = "cruel", fx = { g.hex = g.year + 5; t("У поворота старуха обернулась и что-то сказала. В мешке с бобами на пять лет прибавился второй красный.", "At the bend in the road the old woman turned and said something. For five years a second red bean lies in the bag.") }),
                )
            }),
        Event("meja", "meja", Tx("Межевой камень", "The Boundary Stone"), cond = { h -> fire(age(h) >= 15) },
            txt = { pick(listOf(t("Ле Гро ночью перекатили межевой камень на два шага в ваше поле.", "In the night the Le Gros rolled the boundary stone two paces into your field."), t("Утром межевой камень оказался на два шага ближе к вашему дому. Ночью у Ле Гро допоздна горел свет.", "In the morning the boundary stone stood two paces closer to your house. In the night a light burned late at the Le Gros'."))) },
            opts = { o ->
                listOf(
                    Opt(t("Судиться у сеньора", "Take it to the seigneur's court"), roll = Roll(UM, 0, mapOf("gram" to 2)), ok = { feud(1); glory(1); t("Суд сеньора велел вернуть камень. Ле Гро затаили злобу.", "The seigneur's court ordered the stone put back. The Le Gros nursed their spite.") }, bad = { money(-3); t("Бальи взял су с вас и два с них. Камень остался где лежал.", "The bailiff took a sou from you and two from them. The stone stayed where it lay.") }),
                    Opt(t("Вернуть камень, а Ле Гро — проучить", "Roll the stone back and teach the Le Gros a lesson"), sin = "cruel", roll = Roll(SIL, 0, mapOf("gnev" to 1, "bog" to 2)), ok = { feud(2); glory(1); t("Поле ваше. Старший Ле Гро теперь хромает.", "The field is yours. The elder Le Gros limps now.") }, bad = { feud(1); addTr(o.h, "shram"); t("Ле Гро было четверо.", "There were four of the Le Gros.") }),
                    Opt(t("Уступить два шага", "Yield the two paces"), sin = "coward", fx = { feud(-1); glory(-1); mark("eu_meja"); t("Мир дороже межи. Над вами посмеиваются, зато Ле Гро кланяются.", "Peace is worth more than a boundary. People snicker at you, but the Le Gros bow.") }),
                )
            }),
        Event("peace", "interior", Tx("Мировая", "Making Peace"), w = 3, cond = { fire(if (g.feud >= 2) kidIn(15, 24) { it.spouse == null }.firstOrNull() else null) },
            txt = { o -> val x = o.xx; t("Старый Ле Гро пришёл без шапки: «Хватит нам грызться. У меня ${sx(x, "внучка на выданье", "внук в женихах")}. Поженим их — и дело с концом». Речь о том, кто у вас зовётся ${x.name}.",
                "Old Le Gros came cap in hand: “Enough of this snapping at each other. I have a ${sx(x, "granddaughter of an age to marry", "grandson of an age to wed")}. Let us marry them and be done with it.” He means your ${x.name}.") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Сосватать", "Make the match"), fx = { val s = mkSpouse(x, rival.g); g.feud = 0; glory(1); t("Венчали две семьи в одной церкви. ${x.name} и ${s.name} — и вражде конец.", "Two families at one altar. ${x.name} and ${s.name}, and the feud is over.") }),
                    Opt(t("Выставить за дверь", "Show him the door"), fx = { feud(1); t("Вражда достанется и внукам.", "The feud will pass to the grandchildren too.") }),
                )
            }),
        Event("revenge", "night_int", Tx("Месть Ле Гро", "The Le Gros' Revenge"), w = 3, cond = { fire(g.feud >= 3) },
            txt = { pick(listOf(t("Ночью кто-то подпёр вашу дверь бревном и бросил на крышу горящий факел.", "In the night someone wedged your door shut with a log and threw a burning torch onto the roof."), t("Среди ночи залаял пёс и смолк. Дверь не поддаётся — подпёрта снаружи, а в щели тянет дымом.", "In the middle of the night the dog barked and fell silent. The door won't give: it is wedged from outside, and smoke seeps through the cracks."))) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Выломать дверь и догнать", "Break down the door and give chase"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)), ok = { feud(-1); glory(1); t("Догнали младшего Ле Гро у мельницы. Больше не сунутся.", "You caught the youngest Le Gros by the mill. They will not try it again.") }, bad = { money(-4); addTr(h, "shram"); t("В темноте вы налетели на вилы.", "In the dark you ran onto a pitchfork.") }),
                    Opt(t("Жаловаться бальи", "Complain to the bailiff"), roll = Roll(UM, 0, mapOf("gram" to 2)), ok = { feud(-1); money(2); t("Ле Гро присудили пеню в вашу пользу.", "The Le Gros were fined, and the fine went to you.") }, bad = { feud(1); t("Свидетелей нет. Ле Гро посмеиваются.", "No witnesses. The Le Gros snicker.") }),
                    Opt(t("Ответить тем же", "Pay them back in kind"), sin = "cruel", roll = Roll(HIT, 0, mapOf("lzh" to 1, "zhest" to 1)), ok = { money(3); addSecret(t("${h.name} ${sx(h, "поджёг", "подожгла")} амбар Ле Гро", "${h.name} set fire to the Le Gros' barn"), h); t("Амбар Ле Гро сгорел. Никто ничего не видел.", "The Le Gros' barn burned. Nobody saw a thing.") }, bad = { feud(2); glory(-2); t("Вас видели. Теперь об этом знает вся округа.", "You were seen. Now the whole countryside knows.") }),
                )
            }),
        Event("levy", "war", Tx("Сеньор созывает людей", "The Seigneur Calls His Men"), cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Сеньор воюет с соседним бароном и собирает пехоту. С вашего двора — один человек. Глашатай назвал: ${o.xx.name}.", "The seigneur is at war with a neighboring baron and is gathering foot soldiers. One man from your household. The crier has named ${o.xx.name}.") },
            opts = { o -> levy(this, o.xx, t("война сеньора", "the seigneur's war")) }),
        Event("plague", "plague", Tx("Поветрие", "The Sickness"), cond = { yes }, txt = { pick(listOf(t("В округе поветрие. В соседней деревне за неделю похоронили сорок человек.", "Sickness in the countryside. In the next village they buried forty people in a week."), t("Говорят, в городе поветрие: на дверях рисуют кресты, мёртвых вывозят телегами. Коробейники уже несут его по дорогам.", "They say there is pestilence in the town: crosses are painted on doors, the dead are carted away. The peddlers are already carrying it along the roads."))) }, opts = { plague(this, false) }),
        Event("hunger", "field_dry", Tx("Неурожай", "A Failed Harvest"), cond = { yes },
            txt = { pick(listOf(t("Дожди сгноили зерно на корню. До весны не дотянуть.", "The rains have rotted the grain on the stalk. There is no lasting till spring."), t("Весна, а в ларе пусто. Последнюю муку мешают с толчёной корой, дети спят днём, чтобы меньше хотелось есть.", "Spring, and the bin is empty. The last flour is mixed with pounded bark; the children sleep by day so as to feel the hunger less."), t("Всё лето ни капли дождя. Колосья пустые, как погремушки.", "All summer not a drop of rain. The ears are empty as rattles."))) },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Продать корову", "Sell the cow"), tag = t("−4 су", "−4 sous"), fx = { money(-4); t("Корову свели на рынок. Детей кормили желудями и молоком от соседки.", "The cow was led off to market. The children ate acorns and drank the neighbor's milk.") }),
                    Opt(t("Просить зерна у аббатства", "Beg grain from the abbey"), roll = Roll(SER, 0, mapOf("krot" to 1, "bogom" to 1)), ok = { money(2); t("Монахи дали зерна в долг до следующей жатвы.", "The monks lent grain until the next harvest.") },
                        bad = { money(-3); val c = killOne(true); if (c != null && chance(.5)) { kill(c, t("голод", "hunger")); t("Аббат отказал. К весне не стало: ${c.name}.", "The abbot refused. By spring ${c.name} was gone.") } else t("Аббат отказал. Пережили на отрубях.", "The abbot refused. You got by on bran.") }),
                    Opt(t("Ночью взять из амбара сеньора", "Help yourself from the seigneur's granary at night"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)),
                        ok = { money(4); mark("eu_grain"); addSecret(t("${h.name} в неурожай ${sx(h, "воровал", "воровала")} зерно из амбара сеньора", "${h.name} stole grain from the seigneur's granary in a lean year"), h); t("Три мешка. Никто не хватился.", "Three sacks. Nobody missed them.") },
                        bad = { money(-3); addTr(h, "shram"); glory(-2); t("Поймали. Выставили к позорному столбу на всю ярмарку.", "Caught. Put in the pillory for the whole market day.") }),
                )
            }),
        Event("klad", "klad", Tx("Горшок в борозде", "A Pot in the Furrow"), cond = { h -> fire(age(h) >= 14) },
            txt = { t("Плуг выворотил глиняный горшок, а в нём — римские монеты с профилями забытых императоров.", "The plow turned up a clay pot full of Roman coins bearing the heads of forgotten emperors.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Унести тайком", "Carry it off in secret"), sin = "lie", fx = { money(15); addSecret(t("${h.name} ${sx(h, "утаил", "утаила")} клад из поля", "${h.name} kept a treasure found in the field"), h); t("Пятнадцать су серебром — под камень у очага.", "Fifteen sous in silver, under the hearthstone.") }),
                    Opt(t("Отнести сеньору: по закону клад его", "Take it to the seigneur: by law, it is his"), fx = { money(3); glory(2); deed(h, t(sx(h, "Отдал", "Отдала") + " сеньору найденный клад", "Gave a found treasure to the seigneur")); t("Сеньор похвалил и отсыпал горсть монет.", "The seigneur praised you and poured you a handful of coins.") }),
                    Opt(t("Отдать аббатству", "Give it to the abbey"), fx = { glory(3); addRelic(t("Ковчежец с мощами", "Reliquary"), "reliq", mapOf(VER to 1), t("${h.name}: клад отдан аббатству, аббат подарил ковчежец", "${h.name}: the treasure went to the abbey, and the abbot gave a reliquary")); t("Аббат благословил вас и подарил ковчежец с частицей мощей святого Мартина. Вера +1 в жребиях.", "The abbot blessed you and gave you a reliquary holding a relic of Saint Martin. Faith +1 in draws.") }),
                )
            }),
        Event("secret", "interior", Tx("Старая тайна", "An Old Secret"), w = 3, cond = { val s = g.secrets.firstOrNull(); if (s != null && g.year - s.y >= 3) Fire(s = s) else null },
            txt = { o -> val x = o.s!!; t("По деревне шепчутся: ${x.t}. Дело было ${yrs(g.year - x.y)} назад${if (x.who != o.h.name) ", и того человека давно нет, но спрашивают с вас" else ""}.",
                "The village is whispering that ${x.t}. It happened ${yrs(g.year - x.y)} ago${if (x.who != o.h.name) ", and that person is long gone, but it is you they ask" else ""}.") },
            opts = { o ->
                listOf(
                    Opt(t("Отпереться", "Deny it"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)), ok = { g.secrets.removeAt(0); t("Пошептались и забыли.", "They whispered and forgot.") }, bad = { g.secrets.removeAt(0); glory(-3); feud(1); t("Нашлись свидетели. Слава рода поблёкла.", "Witnesses turned up. The family's good name has faded.") }),
                    Opt(t("Покаяться на исповеди и при всех", "Confess to the priest and before everyone"), roll = Roll(VER, 2, mapOf("chest" to 1, "krot" to 1)), ok = { g.secrets.removeAt(0); glory(1); o.h.stones = maxOf(0, o.h.stones - 1); t("Деревня простила. Даже уважать стали.", "The village forgave you. They even respect you for it.") }, bad = { g.secrets.removeAt(0); glory(-2); t("Деревня не простила, но хоть на душе легче.", "The village did not forgive, but at least your soul is lighter.") }),
                    Opt(t("Заплатить, чтобы молчали", "Pay them to keep quiet"), cost = 8, tag = t("8 су", "8 sous"), fx = { money(-8); g.secrets.removeAt(0); t("Деньги заткнули самые громкие рты.", "Money stopped the loudest mouths.") }),
                )
            }),
        Event("bastard", "road", Tx("Ребёнок вдовы", "The Widow's Child"), cond = { h -> fire(h.male && married(h) && age(h) in 20..50) },
            txt = { t("У вдовы Перонель с мельничной плотины родился сын. Люди говорят, что глаза у него ваши.", "The widow Péronnelle down by the mill dam has borne a son. People say he has your eyes.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Признать и взять в дом", "Own him and take him in"), fx = { val c = bastard(this, h); val s = spouseOf(h); t("${c.name} теперь спит у очага с остальными.${if (s != null) " " + s.name + " неделю не разговаривала с вами." else ""}", "${c.name} now sleeps by the hearth with the others.${if (s != null) " " + s.name + " did not speak to you for a week." else ""}") }),
                    Opt(t("Отпереться", "Deny it"), sin = "lie", fx = { mark("eu_peronnelle"); addSecret(t("сын вдовы Перонель — от главы рода ${g.stem}", "the widow Péronnelle's son was fathered by the head of the ${g.stem} family"), h); t("Перонель промолчала. Но мальчик растёт, и глаза у него всё те же.", "Péronnelle held her tongue. But the boy is growing, and his eyes are still the same.") }),
                    Opt(t("Тайком помогать деньгами", "Help with money in secret"), cost = 4, tag = t("4 су", "4 sous"), fx = { money(-4); addSecret(t("кто-то из семьи ${g.stem} тайком носит деньги вдове Перонель", "one of the ${g.stem} family is secretly taking money to the widow Péronnelle"), h); t("Перонель взяла молча.", "Péronnelle took it without a word.") }),
                )
            }),
        Event("kabak", "tavern", Tx("Таверна", "The Tavern"), cond = { h -> fire(age(h) >= 16) },
            txt = { o -> if (has(o.h, "pyan")) t("Ноги сами несут к таверне. Кум уже машет из окна.", "Your feet carry you to the tavern of their own accord. Your old crony is already waving from the window.") else t("Кум зовёт в таверну «на одну кружку». Вы знаете, чем кончаются одни кружки.", "Your old crony calls you to the tavern “for just one mug.” You know how just one mug ends.") },
            opts = { o ->
                listOf(
                    Opt(t("Пойти", "Go"), roll = Roll(VER, 0, mapOf("pyan" to -3, "krot" to 1, "bogom" to 1)), ok = { t("Выпили по кружке, поговорили о погоде и разошлись.", "One mug each, a word about the weather, and home.") }, bad = { if (!addTr(o.h, "pyan")) { money(-4); t("Пропили башмаки и плащ.", "You drank away your shoes and your cloak.") } else t("Одна кружка, вторая, десятая. Таверна стала вам вторым домом.", "One mug, a second, a tenth. The tavern became your second home.") }),
                    Opt(t("Отказаться", "Refuse"), fx = { if (has(o.h, "pyan")) { o.h.stones++; t("Отказались. Руки трясутся до вечера. Камень на душу.", "You refused. Your hands shook until evening. A stone on the soul.") } else t("Кум обиделся, но к Пасхе отошёл.", "Your crony took offense, but came round by Easter.") }),
                )
            }),
        Event("gramota", "church", Tx("Брат Ансельм", "Brother Anselm"), cond = { h -> fire(!has(h, "gram") && age(h) in 12..45) },
            txt = { t("Брат Ансельм из аббатства берётся выучить вас грамоте за три су и мешок ячменя.", "Brother Anselm from the abbey offers to teach you your letters for three sous and a sack of barley.") },
            opts = { o ->
                listOf(
                    Opt(t("Учиться", "Learn"), cost = 3, tag = t("3 су", "3 sous"), roll = Roll(UM, 1, mapOf("umn" to 1)), ok = { money(-3); addTr(o.h, "gram"); deed(o.h, t(sx(o.h, "Выучился", "Выучилась") + " грамоте", "Learned to read and write")); t("К весне вы уже читали Псалтирь и считали подати лучше управляющего.", "By spring you were reading the Psalter and reckoning your dues better than the steward.") }, bad = { money(-3); t("Буквы так и не сложились в слова. Ячменя жалко.", "The letters never came together into words. A pity about the barley.") }),
                    Opt(t("Не до того", "No time for that"), fx = { t("Брат Ансельм ушёл учить детей Ле Гро.", "Brother Anselm went off to teach the Le Gros children.") }),
                )
            }),
        Event("wolves", "winter", Tx("Волчья зима", "A Wolf Winter"), cond = { yes },
            txt = { pick(listOf(t("Зима лютая. Ночами волки воют под самыми стенами и роют подкоп под овчарню.", "A cruel winter. At night the wolves howl right under the walls and dig beneath the sheepfold."), t("Снег по пояс, и волки обнаглели: днём ходят по деревне, ночью скребут дверь хлева.", "Snow waist-deep, and the wolves have grown bold: by day they walk through the village, by night they scratch at the byre door."))) },
            opts = { o ->
                listOf(
                    Opt(t("Сторожить с топором", "Keep watch with an axe"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)), ok = { glory(1); money(1); t("Положили двух волков. Шкуры — на плащи младшим.", "You laid two wolves low. The skins went into cloaks for the little ones.") }, bad = { money(-4); if (chance(.3)) addTr(o.h, "shram"); t("Волки вырезали овец, пока вы отбивались от вожака.", "The wolves slaughtered the sheep while you fought off the leader.") }),
                    Opt(t("Поставить ловчие ямы", "Dig wolf pits"), roll = Roll(UM), ok = { money(3); t("Три волка в ямах. Шкуры продали.", "Three wolves in the pits. The skins were sold.") }, bad = { money(-2); t("В яму упала соседская свинья.", "The neighbors' pig fell into a pit.") }),
                    Opt(t("Заложить двери и молиться", "Bar the doors and pray"), fx = { money(-3); t("Утром овчарня была пуста.", "In the morning the sheepfold was empty.") }),
                )
            }),
        Event("sick", "night_int", Tx("Жар", "Fever"), w = 2, cond = { val k = kidIn(0, 9); fire(if (k.isEmpty()) null else pick(k)) },
            txt = { o -> val x = o.xx; pick(listOf(t("${x.name} горит в жару третью ночь. Не ест, не узнаёт мать.", "${x.name} has burned with fever for three nights. ${sx(x, "He", "She")} will not eat and does not know ${sx(x, "his", "her")} mother."), t("${x.name} кашляет так, что слышно на улице, и горит, как уголь.", "${x.name} coughs so hard it can be heard in the street, and burns like a coal."), t("${x.name} не встаёт третий день: пятна на коже, губы потрескались.", "${x.name} has not gotten up for three days: spots on the skin, lips cracked."))) },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Позвать травницу", "Call the herbwoman"), cost = 1, tag = t("1 су", "1 sou"), roll = Roll(ZDR, 1, ofX = true), ok = { money(-1); t("Травница поила отварами ивовой коры и шептала до рассвета. Жар спал.", "The herbwoman gave willow-bark brews and whispered until dawn. The fever broke.") }, bad = { money(-1); kill(x, t("горячка", "fever")); t("Травница развела руками.", "The herbwoman spread her hands.") }),
                    Opt(t("Молиться святому Роху", "Pray to Saint Roch"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)), ok = { t("К утру ${x.name} ${sx(x, "попросил", "попросила")} молока.", "By morning ${x.name} asked for milk.") }, bad = { kill(x, t("горячка", "fever")); t("Свеча догорела раньше, чем кончилась ночь.", "The candle burned out before the night did.") }),
                    Opt(t("Везти в город к лекарю", "Take the child to a physician in town"), cost = 6, tag = t("6 су", "6 sous"), roll = Roll(UM, 4, mapOf("gram" to 1, "gor" to 1)), ok = { money(-6); t("Лекарь из Парижа пустил кровь и дал горькой микстуры. Через неделю — на ногах.", "A physician from Paris let blood and gave a bitter draught. A week later the child was up and about.") }, bad = { money(-6); kill(x, t("горячка", "fever")); t("Не довезли.", "You did not reach the town in time.") }),
                )
            }),
        Event("routier", "night_int", Tx("Раненый наёмник", "A Wounded Routier"), cond = { h -> fire(age(h) >= 16) },
            txt = { t("Ночью в дверь стучит раненый наёмник из разбитого отряда. Просит спрятать до весны.", "In the night a wounded mercenary from a broken company knocks at the door. He asks to be hidden until spring.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Спрятать на сеновале", "Hide him in the hayloft"), roll = Roll(HIT, 0, mapOf("dobr" to 1, "lzh" to 1)), ok = { mark("eu_rout_hid"); addRelic(t("Меч наёмника", "Routier's Sword"), "sword", mapOf(SIL to 1), t("наёмник оставил его в благодарность", "a mercenary left it in thanks")); deed(h, t(sx(h, "Спрятал", "Спрятала") + " раненого наёмника", "Hid a wounded mercenary")); t("Весной он ушёл и оставил вам свой меч. Сила +1 в жребиях.", "In spring he left and gave you his sword. Strength +1 in draws.") }, bad = { addTr(h, "shram"); glory(-1); t("Его нашли у вас. Вас высекли у позорного столба.", "He was found in your house. You were flogged at the pillory.") }),
                    Opt(t("Выдать людям сеньора", "Hand him over to the seigneur's men"), sin = "cruel", fx = { money(3); mark("eu_rout_sold"); t("Дали три су. Его повесили на перекрёстке.", "You were paid three sous. He was hanged at the crossroads.") }),
                    Opt(t("Дать хлеба и прогнать", "Give him bread and send him off"), fx = { t("Он ушёл в метель, хромая.", "He limped off into the snowstorm.") }),
                )
            }),
        Event("obrok", "barin", Tx("Новый сеньор", "The New Seigneur"), cond = { h -> fire(age(h) >= 16) },
            txt = { t("Новый сеньор вернулся из похода с долгами и поднял подать вдвое, а барщину — до трёх дней в неделю.", "The new seigneur came home from campaign in debt. He has doubled the dues and raised the labor service to three days a week.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Платить", "Pay"), tag = t("−8 су", "−8 sous"), fx = { money(-8); t("Затянули пояса.", "You tightened your belts.") }),
                    Opt(t("Идти просить всей деревней", "Go and plead with the whole village"), roll = Roll(SER, 0, mapOf("kras" to 1, "krot" to 1)), ok = { money(-4); glory(1); t("Сеньор уступил половину. Деревня кланялась вам в пояс.", "The seigneur let half of it go. The village bowed to you from the waist.") }, bad = { money(-8); t("Сеньор даже не вышел из замка.", "The seigneur did not even come out of the castle.") }),
                    Opt(t("Подбить деревню на бунт", "Stir the village to revolt"), roll = Roll(SIL, -3, mapOf("hrab" to 2, "gnev" to 1, "bog" to 1)), ok = { glory(3); mark("eu_rebel"); deed(h, t(sx(h, "Поднял", "Подняла") + " деревню против сеньора", "Raised the village against the seigneur")); t("Управляющего выгнали с позором. Сеньор, подумав, вернул прежнюю подать.", "The steward was driven out in disgrace. The seigneur thought it over and restored the old dues.") },
                        bad = { if (chance(.4)) { kill(h, t("повешен" + sx(h, "", "а") + " за бунт", "hanged for rebellion")); t("Зачинщиков повесили на дубе у дороги.", "The ringleaders were hanged from the oak by the road.") } else { addTr(h, "kalek"); glory(1); t("Зачинщиков били палками. Вы выжили, но ходите с клюкой.", "The ringleaders were beaten with staves. You lived, but you walk with a crutch.") } }),
                )
            }),
        Event("pilgrim", "church", Tx("Паломничество", "A Pilgrimage"), cond = { h -> fire(age(h) >= 16 && !has(h, "bogom")) },
            txt = { t("Соседи идут в Сантьяго-де-Компостелу, к мощам апостола Иакова. Зовут с собой.", "The neighbors are setting out for Santiago de Compostela, to the bones of the Apostle James. They ask you along.") },
            opts = { o ->
                listOf(
                    Opt(t("Идти", "Go"), roll = Roll(ZDR, 2, mapOf("krot" to 1)), ok = { addTr(o.h, "bogom"); o.h.stones = 0; glory(1); if (chance(.5)) addRelic(t("Раковина святого Иакова", "Scallop of Saint James"), "shell", mapOf(VER to 1), t("принесена из Компостелы", "brought back from Compostela")); t("Вернулись через год с раковиной на шляпе. Камни с души как рукой сняло.", "You came home a year later with a shell on your hat. The stones were lifted from your soul as if by a hand.") }, bad = { o.h[ZDR]--; t("Простудились в горах и вернулись с полпути.", "You caught a chill in the mountains and turned back halfway.") }),
                    Opt(t("Не время", "Not now"), fx = { t("Жатва не ждёт.", "The harvest will not wait.") }),
                )
            }),
        Event("smith", "village", Tx("Кузнец Тибо", "Thibaut the Smith"), cond = { fire(g.relics.none { it.ic == "axe" }) },
            txt = { t("Кузнец Тибо берётся выковать топор с вашим знаком. «Таким и внуки будут рубить».", "Thibaut the smith offers to forge an axe with your mark on it. “Your grandchildren will still be chopping with this one.”") },
            opts = {
                listOf(
                    Opt(t("Заказать", "Order it"), cost = 4, tag = t("4 су", "4 sous"), fx = { money(-4); addRelic(t("Топор со знаком", "Marked Axe"), "axe", mapOf(SIL to 1), t("выкован для главы рода", "forged for the head of the family")); t("Топор лёг в руку как влитой. Сила +1 в жребиях.", "The axe sits in the hand as if it grew there. Strength +1 in draws.") }),
                    Opt(t("Обойдёмся старым", "The old one will do"), fx = { t("Старый топор ещё послужит.", "The old axe has some years in it yet.") }),
                )
            }),
        Event("town", "village", Tx("Городской воздух", "Town Air"), cond = { fire(kidIn(16, 25) { !has(it, "gor") && it.spouse == null }.firstOrNull()) },
            txt = { o -> t("${o.xx.name} хочет уйти в город: «В городе говорят: городской воздух делает свободным. Год и один день — и сеньор меня не вернёт».", "${o.xx.name} wants to go to the town: “In town they say town air makes you free. A year and a day, and the seigneur can't bring me back.”") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Отпустить", "Let ${sx(x, "him", "her")} go"), fx = { addTr(x, "gor"); x[HIT] += 1; mark("eu_town"); x.away = g.year + ri(3, 7); t("${x.name} ${sx(x, "ушёл", "ушла")} с купеческим обозом. Вестей почти нет.", "${x.name} left with a merchants' wagon train. News is scarce.") }),
                    Opt(t("Не пускать", "Keep ${sx(x, "him", "her")} home"), fx = { x.stones++; t("${x.name} остал${sx(x, "ся", "ась")}, но смотрит волком.", "${x.name} stayed, but glowers like a wolf.") }),
                )
            }),
        Event("squire", "barin", Tx("Слуга рыцаря", "A Knight's Page"), cond = { fire(kidIn(12, 17) { it.male && !it.squire }.firstOrNull()) },
            txt = { o -> t("Рыцарь из замка ищет мальчика в слуги и приметил вашего: ${o.xx.name}. «Будет чистить коня и носить копьё. Может, и в люди выйдет».", "A knight from the castle is looking for a boy to serve him and has his eye on yours: ${o.xx.name}. “He'll groom my horse and carry my lance. He may even make something of himself.”") },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt(t("Отпустить в замок", "Send him to the castle"), fx = { x.squire = true; x[SIL] += 2; x[SER] += 1; x.away = g.year + ri(6, 10); if (chance(.2)) x.awayDie = g.year + ri(2, 8); glory(1); deed(x, t("Служил рыцарю", "Served a knight")); t("${x.name} ушёл в замок с узелком. Через месяц прислал поклон и серебряную пряжку.", "${x.name} went off to the castle with a bundle. A month later he sent his greetings and a silver buckle.") }),
                    Opt(t("Оставить дома", "Keep him home"), fx = { x.squire = true; t("Рыцарь пожал плечами и взял сына мельника.", "The knight shrugged and took the miller's son.") }),
                )
            }),
        Event("ordeal", "church", Tx("Божий суд", "The Ordeal"), cond = { h -> fire(age(h) >= 16) },
            txt = { t("У соседа пропала овца, и он клянётся, что видел вас у его загона. Сеньор назначил божий суд: пронести раскалённое железо девять шагов.", "A neighbor has lost a sheep and swears he saw you by his pen. The seigneur has ordered the ordeal: carry red-hot iron nine paces.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Нести железо", "Carry the iron"), roll = Roll(VER, 0, mapOf("chest" to 2, "lzh" to -1)), ok = { glory(2); deed(h, t(sx(h, "Прошёл", "Прошла") + " божий суд", "Passed the ordeal")); t("Через три дня повязку сняли — рана чистая. Бог за вас.", "Three days later the bandage came off and the wound was clean. God is on your side.") }, bad = { money(-5); glory(-2); if (chance(.4)) addTr(h, "kalek"); t("Рука загноилась. Вас сочли виновным: пеня и позор.", "The hand festered. You were judged guilty: a fine and disgrace.") }),
                    Opt(t("Заплатить пеню", "Pay the fine"), cost = 5, tag = t("5 су", "5 sous"), fx = { money(-5); glory(-1); t("Заплатили — значит, виноваты. Так и запомнили.", "You paid, so you were guilty. That is how they remember it.") }),
                    Opt(t("Найти настоящего вора", "Find the real thief"), roll = Roll(HIT, 0, mapOf("umn" to 1)), ok = { glory(1); t("Овцу нашли в хлеву у самого соседа. Суд отменили.", "The sheep turned up in the neighbor's own shed. The ordeal was called off.") }, bad = { money(-3); addTr(h, "shram"); t("Не нашли. Пришлось нести железо, и рука до сих пор в рубцах.", "You found nothing. You had to carry the iron after all, and the hand is scarred to this day.") }),
                )
            }),
    ) + europeMoreEvents

    override val history = listOf(
        Event("c3", "war", Tx("Третий крестовый поход", "The Third Crusade"), y0 = 1189, y1 = 1191, p = .5, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Король Филипп принял крест и идёт за море отвоёвывать Иерусалим. Кюре зовёт молодых. ${o.xx.name} рвётся идти.", "King Philip has taken the cross and is going over the sea to win back Jerusalem. The curé is calling on the young men. ${o.xx.name} is eager to go.") },
            opts = { o -> val x = o.xx; listOf(Opt(t("Благословить", "Give him your blessing"), fx = { glory(2); sendAway(x, t("Третий крестовый поход", "the Third Crusade")); t("${x.name} уходит с красным крестом на плаще.", "${x.name} leaves with a red cross on his cloak.") }), Opt(t("Не пускать", "Keep him home"), fx = { x.stones++; t("Остал" + sx(x, "ся", "ась") + ", но по ночам смотрит на юг.", "He stayed, but at night he looks to the south.") })) }),
        Event("bouv", "war", Tx("Бувин", "Bouvines"), y0 = 1214, y1 = 1214, p = .8, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Император и англичане идут на короля. Коммуны собирают пехоту — ${o.xx.name} в списке.", "The Emperor and the English are marching on the king. The communes are raising foot soldiers, and ${o.xx.name} is on the roll.") }, opts = { o -> levy(this, o.xx, t("Бувин", "Bouvines")) }),
        Event("c7", "war", Tx("Поход короля Людовика", "King Louis's Crusade"), y0 = 1248, y1 = 1249, p = .6, cond = { h -> fire(age(h) in 18..45) },
            txt = { t("Король Людовик принял крест и идёт в Египет. Сеньор берёт с собой охотников из своих деревень.", "King Louis has taken the cross and is bound for Egypt. The seigneur is taking volunteers from his villages.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Принять крест", "Take the cross"), roll = Roll(SIL, -2, mapOf("hrab" to 2, "bogom" to 1)), ok = { glory(4); addRelic(t("Крест из Святой земли", "Cross from the Holy Land"), "crossr", mapOf(VER to 1), t("${h.name} — крестоносец", "${h.name}, crusader")); deed(h, t(sx(h, "Ходил", "Ходила") + " в Египет с королём", "Went to Egypt with the king")); t("Вернулись из Египта живыми — немногие могут так сказать. Вера +1 в жребиях.", "You came back from Egypt alive, which few can say. Faith +1 in draws.") },
                        bad = { if (chance(.5)) { kill(h, t(sx(h, "умер", "умерла") + " в Египте", "died in Egypt")); t("Короля взяли в плен под Мансурой. Вас — нет: не дожили.", "The king was taken prisoner at Mansourah. You were not: you did not live that long.") } else { addTr(h, "kalek"); glory(2); t("Вернулись без руки, но с почётом.", "You came home without an arm, but with honor.") } }),
                    Opt(t("Дать денег на поход", "Give money for the crusade"), cost = 5, tag = t("5 су", "5 sous"), fx = { money(-5); glory(1); t("Кюре записал ваше имя в книгу жертвователей.", "The curé wrote your name in the book of donors.") }),
                    Opt(t("Остаться дома", "Stay home"), fx = { t("Поле не ждёт.", "The field will not wait.") }),
                )
            }),
        Event("famine", "field_dry", Tx("Великий голод", "The Great Famine"), y0 = 1315, y1 = 1317, p = 1.0, cond = { yes },
            txt = { t("Третий год дожди. Зерно гниёт, скот дохнет. По дорогам бродят люди с пустыми глазами.", "A third year of rain. The grain rots, the cattle die. People with empty eyes wander the roads.") },
            opts = {
                listOf(
                    Opt(t("Делить последнее поровну", "Share the last of it equally"), roll = Roll(SER, 0, mapOf("dobr" to 1, "shch" to 1)), ok = { t("Дотянули до весны на желудях и крапиве.", "You held out till spring on acorns and nettles.") }, bad = { val c = killOne(true); if (c != null) { kill(c, t("голод", "hunger")); t("Не стало: ${c.name}.", "Gone: ${c.name}.") } else t("Дотянули, но еле.", "You held out, but only just.") }),
                    Opt(t("Уйти в город просить милостыню", "Go begging in the town"), roll = Roll(HIT, 0, mapOf("gor" to 2)), ok = { money(2); t("Монастырская похлёбка у городских ворот спасла всех.", "Soup from the monks at the town gate saved you all.") }, bad = { val c = killOne(true); if (c != null) { kill(c, t("голод", "hunger")); t("В городе было ещё хуже. Не стало: ${c.name}.", "It was worse still in the town. Gone: ${c.name}.") } else t("В городе было ещё хуже. Вернулись ни с чем.", "It was worse still in the town. You came back with nothing.") }),
                )
            }),
        Event("h100", "war", Tx("Англичане", "The English"), y0 = 1337, y1 = 1340, p = .6, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Английский король требует французскую корону. Сеньор созывает своих людей — назван ${o.xx.name}.", "The English king claims the crown of France. The seigneur is calling up his men, and ${o.xx.name} is named.") }, opts = { o -> levy(this, o.xx, t("война с англичанами", "the war with the English")) }),
        Event("crecy", "war", Tx("Креси", "Crécy"), y0 = 1346, y1 = 1346, p = .7, cond = { h -> fire(age(h) in 16..50) },
            txt = { t("Король Филипп собирает всех, кто может держать копьё. Англичане высадились в Нормандии.", "King Philip is summoning everyone who can hold a spear. The English have landed in Normandy.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Идти", "Go"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)), ok = { glory(3); deed(h, t(sx(h, "Был", "Была") + " под Креси", "Fought at Crécy")); t("Вернулись живыми из-под Креси, где полегло рыцарство Франции. Английских лучников вы не забудете.", "You came back alive from Crécy, where the chivalry of France was cut down. You will not forget the English archers.") },
                        bad = { if (chance(.5)) { kill(h, t("пал" + sx(h, "", "а") + " под Креси", "fell at Crécy")); t("Английские стрелы летели как град.", "The English arrows fell like hail.") } else { addTr(h, "kalek"); glory(2); t("Вернулись со стрелой в колене, но живыми.", "You came home with an arrow in the knee, but alive.") } }),
                    Opt(t("Дать коня и овса", "Give a horse and oats"), cost = 5, tag = t("5 су", "5 sous"), fx = { money(-5); glory(1); t("Конь ушёл к войску.", "The horse went off to the host.") }),
                    Opt(t("Отсидеться в лесу", "Sit it out in the forest"), sin = "coward", fx = { t("Война прошла стороной. А соседи запомнили.", "The war passed you by. But the neighbors remembered.") }),
                )
            }),
        Event("bd", "plague", Tx("Чёрная смерть", "The Black Death"), y0 = 1348, y1 = 1350, p = 1.0, cond = { yes },
            txt = { t("С юга пришла моровая язва. Люди чернеют и умирают за три дня. Священники не успевают отпевать, мёртвых хоронят во рвах.", "The plague has come up from the south. People turn black and die within three days. The priests cannot keep up with the funerals; the dead are buried in pits.") }, opts = { plague(this, true) }),
        Event("jacq", "war", Tx("Жакерия", "The Jacquerie"), y0 = 1358, y1 = 1358, p = .8, cond = { h -> fire(age(h) >= 16) },
            txt = { t("Мужики поднялись против господ: жгут замки и бьют рыцарей. Их вожак Гильом Каль зовёт и вас.", "The peasants have risen against their lords, burning castles and killing knights. Their leader, Guillaume Cale, calls on you too.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Пристать к Жакам", "Join the Jacques"), roll = Roll(SIL, -2, mapOf("hrab" to 2, "gnev" to 1)), ok = { money(6); glory(2); addSecret(t("${h.name} ${sx(h, "ходил", "ходила")} с Жаками", "${h.name} marched with the Jacques"), h); t("Вернулись с добром из замка. Об этом лучше молчать.", "You came back with goods from a castle. Better say nothing about it.") },
                        bad = { if (chance(.5)) { kill(h, t("казнён" + sx(h, "", "а") + " за мятеж", "executed for revolt")); t("Рыцари разбили мужиков под Мело. Вешали на каждом дереве.", "The knights routed the peasants at Mello. They hanged men from every tree.") } else { addTr(h, "kalek"); t("Рыцари разбили мужиков. Еле ушли, с перебитой ногой.", "The knights routed the peasants. You barely got away, with a broken leg.") } }),
                    Opt(t("Спрятать семью сеньора", "Hide the seigneur's family"), roll = Roll(HIT, 0, mapOf("dobr" to 1)), ok = { money(5); glory(2); addRelic(t("Перстень сеньора", "The Seigneur's Ring"), "ring", mapOf(HIT to 1), t("подарен спасённой госпожой", "a gift from the lady you saved")); t("Госпожа выжила и подарила перстень. Хитрость +1 в жребиях.", "The lady lived and gave you a ring. Cunning +1 in draws.") }, bad = { addTr(h, "shram"); t("Мужики нашли господ — и вам досталось за компанию.", "The peasants found the lords, and you took a beating for good measure.") }),
                    Opt(t("Сидеть тихо", "Keep your head down"), fx = { t("Беда прошла стороной.", "The trouble passed you by.") }),
                )
            }),
        Event("rout", "war", Tx("Большие роты", "The Great Companies"), y0 = 1360, y1 = 1368, p = .6, cond = { h -> fire(age(h) >= 16) },
            txt = { t("После перемирия наёмники остались без дела. Банда, что зовёт себя «Белой ротой», грабит деревни и берёт выкуп.", "After the truce the mercenaries were left without work. A band calling itself the White Company is plundering villages and taking ransom.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Заплатить выкуп", "Pay the ransom"), cost = 8, tag = t("8 су", "8 sous"), fx = { money(-8); t("Наёмники взяли серебро, вино и ушли грабить соседей.", "The mercenaries took the silver and the wine and went off to plunder the neighbors.") }),
                    Opt(t("Укрыться в церкви", "Shelter in the church"), roll = Roll(VER, 0, mapOf("bogom" to 1)), ok = { t("Отсиделись за каменными стенами. Дом разграбили, но все живы.", "You sat it out behind stone walls. The house was plundered, but everyone lives.") },
                        bad = { money(-part(.5)); val c = killOne(false); if (c != null) { kill(c, t("убит" + sx(c, "", "а") + " наёмниками", "killed by mercenaries")); t("Церковь не остановила наёмников. Не стало: ${c.name}.", "The church did not stop the mercenaries. Gone: ${c.name}.") } else t("Церковь не остановила наёмников. Забрали половину добра.", "The church did not stop the mercenaries. They took half of everything.") }),
                    Opt(t("Драться", "Fight"), roll = Roll(SIL, -2, mapOf("hrab" to 2, "bog" to 1)), ok = { glory(3); deed(h, t(sx(h, "Отбил", "Отбила") + " деревню от наёмников", "Defended the village from mercenaries")); t("Деревня встала с вилами и цепами. Роту прогнали.", "The village rose with pitchforks and flails. The company was driven off.") },
                        bad = { if (chance(.4)) { kill(h, t("убит" + sx(h, "", "а") + " наёмниками", "killed by mercenaries")); t("Вилы против мечей.", "Pitchforks against swords.") } else { addTr(h, "shram"); money(-4); t("Вас отделали и забрали всё ценное.", "You were beaten and everything of value was taken.") } }),
                )
            }),
        Event("azin", "war", Tx("Азенкур", "Agincourt"), y0 = 1415, y1 = 1415, p = .7, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> t("Король Генрих высадился у Арфлёра. Сеньор собирает людей — ${o.xx.name} в списке.", "King Henry has landed at Harfleur. The seigneur is gathering his men, and ${o.xx.name} is on the roll.") }, opts = { o -> levy(this, o.xx, t("Азенкур", "Agincourt")) }),
        Event("joan", "war", Tx("Дева", "The Maid"), y0 = 1429, y1 = 1430, p = 1.0, cond = { h -> fire(age(h) in 16..45) },
            txt = { t("По дорогам слух: дева из Лотарингии слышит голоса святых и ведёт войско на Орлеан.", "Word on the roads: a maid from Lorraine hears the voices of saints and is leading an army to Orléans.") },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt(t("Идти за Девой", "Follow the Maid"), roll = Roll(SIL, -1, mapOf("hrab" to 2, "bogom" to 1)), ok = { glory(4); addRelic(t("Лоскут знамени Девы", "Scrap of the Maid's Banner"), "lily", mapOf(SER to 1), t("${h.name} был${sx(h, "", "а")} под Орлеаном", "${h.name} was at Orléans")); deed(h, t(sx(h, "Шёл", "Шла") + " за Девой на Орлеан", "Followed the Maid to Orléans")); t("Вы были под Орлеаном, когда англичане отступили. Вернулись с лоскутом её знамени. Сердце +1 в жребиях.", "You were at Orléans when the English fell back. You came home with a scrap of her banner. Heart +1 in draws.") },
                        bad = { if (chance(.5)) { kill(h, t("пал" + sx(h, "", "а") + " у стен Парижа", "fell under the walls of Paris")); t("У стен Парижа удача отвернулась.", "Under the walls of Paris, luck turned away.") } else { addTr(h, "kalek"); glory(2); t("Вернулись раненым, но с почётом.", "You came home wounded, but with honor.") } }),
                    Opt(t("Молиться за неё", "Pray for her"), fx = { glory(1); t("Деревня молилась за Деву каждое воскресенье.", "The village prayed for the Maid every Sunday.") }),
                    Opt(t("Не верить", "Don't believe it"), fx = { t("Говорят, её потом сожгли в Руане. Как ту травницу.", "They say she was burned at Rouen afterward. Like that herbwoman.") }),
                )
            }),
        Event("end", "village", Tx("Конец войны", "The End of the War"), y0 = 1453, y1 = 1455, p = 1.0, cond = { yes },
            txt = { t("Англичан прогнали за море. Сто с лишним лет войны кончились. Поля заросли кустарником, а рук не хватает.", "The English have been driven back over the sea. More than a hundred years of war are over. The fields are overgrown with scrub, and there are too few hands.") },
            opts = { o ->
                listOf(
                    Opt(t("Распахать заброшенные поля", "Plow the abandoned fields"), cost = 6, tag = t("6 су, двор растёт", "6 sous, the farm grows"), fx = { money(-6); g.dvor = minOf(4, g.dvor + 1); glory(1); t("Сеньор отдал пустоши почти даром. Земли теперь вдвое.", "The seigneur let the waste land go for almost nothing. You have twice the land now.") }),
                    Opt(t("Уйти в город", "Go to the town"), fx = { money(6); addTr(o.h, "gor"); t("В городе нужны руки. Живые деньги.", "The town needs hands. Real money.") }),
                    Opt(t("Жить как жили", "Live as before"), fx = { t("Всё как было, только без войны.", "Everything as it was, only without the war.") }),
                )
            }),
    ) + europeMoreHistory
}
