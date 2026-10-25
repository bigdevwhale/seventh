package app.seventh.domain

import app.seventh.domain.Stat.HIT
import app.seventh.domain.Stat.SER
import app.seventh.domain.Stat.SIL
import app.seventh.domain.Stat.UM
import app.seventh.domain.Stat.VER
import app.seventh.domain.Stat.ZDR

/** Russia 1690–1950, drawn as a lubok: the beggar woman's curse, the barin, the recruit levy, 1812, emancipation, collectivisation. */
object Rus : Land("rus") {
    override val maleNames = listOf("Иван", "Фрол", "Степан", "Михей", "Савва", "Пётр", "Тимофей", "Еремей", "Кузьма", "Лука", "Никита", "Гаврила", "Демьян", "Прохор", "Ефим", "Яков", "Афанасий", "Захар", "Митрофан", "Игнат", "Данила", "Осип", "Сидор", "Фома", "Терентий", "Василий", "Григорий", "Матвей", "Семён", "Архип")
    override val femaleNames = listOf("Марфа", "Агафья", "Устинья", "Пелагея", "Дарья", "Арина", "Аксинья", "Евдокия", "Татьяна", "Матрёна", "Федосья", "Анна", "Варвара", "Василиса", "Прасковья", "Ульяна", "Фёкла", "Мавра", "Ирина", "Лукерья", "Акулина", "Глафира", "Степанида", "Ефросинья", "Настасья")
    override val stems = listOf("Воронов", "Лапин", "Ершов", "Сорокин", "Бородин", "Кречетов", "Глухов", "Рябов", "Зимин", "Поздеев", "Сычугов", "Огарков")
    override val places = listOf("Тверская губерния, село Медное", "Вологодский уезд, деревня Кубенка", "Рязанская губерния, село Ижевское", "Вятский край, починок Ключи", "Ярославская губерния, село Вощажниково", "Костромской уезд, деревня Ширяиха")
    override val families = listOf("Михеевых", "Пахомовых", "Лукиных", "Сычёвых", "Зуевых", "Хлыновых", "Кожемякиных")
    override val dvor = listOf("курная изба", "изба с трубой", "крепкий двор", "хутор", "усадьба")
    override val years = 1688..1712
    override val edu0 = "В поле и на покос"
    override val eduBias = mapOf("В поле и на покос" to listOf("hrab", "gnev"), "К дьячку" to listOf("chest", "krot"), "На торг" to listOf("lzh", "zhad"), "К знахарке" to listOf("dobr", "shch"))
    override val rival = Rival("Кожемякиных", "Кожемякиными", "Кожемякины")
    override val cur = "руб."
    override val moneyLabel = "Серебро"
    override val introScene = "winter"
    override val nakaz = NakazText({ "Скопите $it рублей серебром — хватит жить впроголодь" }, "Пусть о нас говорят по всему уезду", { "Поставьте новый двор — $it" }, "Пусть в роду будет грамотный", "Помиритесь с Кожемякиными")
    override val helpFeud = "Вражда с соседями Кожемякиными. Чем она выше, тем чаще они мстят. Помирить может свадьба или уступка."
    override val helpHeart = "Сердце — люди, барин, торг, мир с соседями."

    override fun family(g: Game) = g.stem + "ых"
    override fun surname(g: Game, p: Person) = if (p.male) g.stem else g.stem + "а"
    override fun curseLog(e: Engine, f: Person) = "Нищенка прокляла род ${f.name}${e.sx(f, "а", "ы")} до седьмого колена."
    override fun cause(e: Engine, p: Person): String = with(e) {
        val a = age(p)
        when {
            a < 5 -> pick(listOf("горячка", "оспа", "родимчик", "корь", "глотошная"))
            a < 15 -> pick(listOf("горячка", "оспа", "утонул" + sx(p, "", "а") + " в омуте", "скарлатина"))
            a > 66 -> pick(listOf("старость", "старость", "удар", "сердце"))
            else -> pick(listOf("чахотка", "лихорадка", "водянка", "простуда", "надорвал" + sx(p, "ся", "ась") + " на покосе", "антонов огонь"))
        }
    }
    override fun mort(year: Int) = (if (year > 1900) .75 else 1.0) * (if (year > 1945) .55 else 1.0)
    override fun income(year: Int) = if (year > 1861) 1 else 0
    override fun intro(e: Engine, f: Person) = with(e) {
        listOf(
            "${g.year} год. ${g.place}.",
            "Жребий выпал: **${f.name} ${surname(f)}**, ${yrs(age(f))}, ${sx(f, "крестьянский сын", "крестьянская дочь")}. ${cap(f.tr.joinToString(", ") { tname(it, f) })}.",
            "Зимой в избу постучалась нищенка. ${f.name} не ${sx(f, "подал", "подала")} ей хлеба. «Будь проклят твой род до седьмого колена», — сказала старуха и ушла в метель.",
        )
    }

    private fun recruitOpts(e: Engine, x: Person, war: String) = with(e) {
        listOf(
            Opt("Откупиться", cost = 12, tag = "12 руб.", fx = { money(-12); "Сход взял деньги и отправил вместо вашего охочего сироту." }),
            Opt("Отдать: ${x.name}", fx = { sendAway(x, war); "${x.name} уходит с партией рекрутов. Мать выла у околицы до темноты." }),
            Opt("Спрятать в лесу", sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1, "trus" to 1)),
                ok = { "${x.name} пересидел${sx(x, "", "а")} в скиту у раскольников. Набор прошёл мимо." },
                bad = { sendAway(x, war); addTr(h, "shram"); "${x.name} нашли и забрили, а вас высекли на миру." }),
        )
    }

    private fun plagueOpts(e: Engine) = with(e) {
        listOf(
            Opt("Уйти всей семьёй на заимку", roll = Roll(UM, 0, mapOf("gram" to 1)),
                ok = { "Пересидели в лесу до морозов. Вернулись все." },
                bad = { val c = killOne(true); if (c != null) { kill(c, "моровое поветрие"); "Мор пришёл и в лес. Не стало: ${c.name}." } else "Мор пришёл и в лес, но обошёл вас стороной." }),
            Opt("Молиться и окуривать избу можжевельником", roll = Roll(VER, 0, mapOf("bogom" to 1, "chest" to 1)),
                ok = { "Мор обошёл ваш двор стороной." },
                bad = { val c = killOne(true); if (c != null) { kill(c, "моровое поветрие"); "Не уберегли: ${c.name}." } else "Переболели все, но выжили." }),
            Opt("Ходить за больными соседями", roll = Roll(ZDR, -2, mapOf("dobr" to 2, "hrab" to 1)),
                ok = { o -> glory(3); deed(o.h, sx(o.h, "Выхаживал", "Выхаживала") + " соседей в мор"); "Вас обходит даже мор. По деревне говорят — святой человек." },
                bad = { o -> if (chance(.5)) { kill(o.h, "моровое поветрие"); "Заразились у соседей." } else { addTr(o.h, "hvor"); "Заразились, но выжили. Здоровье уже не то." } }),
        )
    }

    override val forced = listOf(
        Event("match", "wedding", "Сваха", cond = { h -> fire(age(h) in 16..49 && !married(h) && h.away == null && g.year >= g.noMatch) },
            txt = { o -> "Сваха Матрёна раскладывает на столе три платка — по одному на ${sx(o.h, "каждую невесту", "каждого жениха")}. «Выбирай, ${o.h.name}. Только помни: что видно — то видно, а что под платком — узнаешь после венца»." },
            opts = { o -> matchOpts(this, o, "сер.", "что-то под платком") { p, h -> var s = "Сыграли свадьбу. ${p.name} ${sx(p, "вошёл в дом", "вошла в дом")}${if (p.dw > 0) " с приданым" else ""}."; if (p.from == rival.g) { feud(-2); s += " Кожемякины пришли на свадьбу — впервые за много лет." }; s } +
                Opt("Повременить", tag = "Сваха придёт через три года", fx = { g.noMatch = g.year + 3; "Сваха поджала губы и ушла." }) }),
        Event("edu", "interior", "К какому делу?", cond = { fire(kidIn(7, 11) { it.edu == null }.firstOrNull()) },
            txt = { o -> val x = o.xx; "${x.name}${if (x.bast) " (незаконн" + sx(x, "ый", "ая") + ")" else ""} уже ${yrs(age(x))}. Пора решать, к какому делу ${sx(x, "его", "её")} приставить. От этого зависит, ${sx(x, "каким он", "какой она")} вырастет." },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt("В поле и на покос", tag = "Сила +2, Здоровье +1 · чаще храбрые и гневные", fx = { x.edu = "В поле и на покос"; x[SIL] += 2; x[ZDR] += 1; "${x.name} с утра до ночи в поле. Руки уже в мозолях." }),
                    Opt("К дьячку, учиться", cost = 2, tag = "2 сер. · Ум +2, может выучиться грамоте · чаще честные и кроткие", fx = { money(-2); x.edu = "К дьячку"; x[UM] += 2; if (chance(.6)) { addTr(x, "gram"); "${x.name} уже читает Псалтирь по складам." } else "Буквы ${x.name} не даются, зато счёт — хорошо." }),
                    Opt("На торг, при лавке", tag = "Хитрость +2 · чаще лживые и жадные", fx = { x.edu = "На торг"; x[HIT] += 2; "${x.name} бегает при лавке и уже знает, где обвесить, а где улыбнуться." }),
                    Opt("К бабке-знахарке", tag = "Сердце +1, Вера +1, Здоровье +1 · чаще добрые и щедрые", fx = { x.edu = "К знахарке"; x[SER]++; x[VER]++; x[ZDR]++; "${x.name} знает уже сорок трав и все заговоры от лихоманки." }),
                )
            }),
        Event("stones", "night_int", "Камни на душе", cond = { h -> fire(h.stones >= 5 && age(h) >= 14) },
            txt = { o -> "Пять камней лежат на душе. Ночами не спится, а днём всё валится из рук. Так долго не протянуть." },
            opts = { o ->
                val l = mutableListOf(
                    Opt("Залить вином", tag = "станет пьяницей", fx = { addTr(o.h, "pyan"); o.h.stones = 2; "Кабак принял вас как родного. Камни полегчали, кошелёк тоже." }),
                    Opt("Сорвать злобу на домашних", tag = "станет гневным, слава −1", fx = { addTr(o.h, "gnev"); o.h.stones = 1; glory(-1); "Дети прячутся на печи, когда вы входите в избу." }),
                    Opt("Каяться на богомолье", roll = Roll(VER, -1, mapOf("krot" to 1, "chest" to 1)),
                        ok = { o.h.stones = 0; o.h[VER]++; addTr(o.h, "bogom"); "Сорок дней пешком до Троицы. Вернулись налегке." },
                        bad = { o.h.stones = 4; o.h[ZDR]--; "В дороге продуло. Камни остались, а кашель прибавился." }),
                )
                if (desc(o.h).any { alive(it) && it.away == null }) l.add(monkOpt(this, o, "Ворота обители закрылись за вами. Двор и род теперь — на детях."))
                l
            }),
        Event("grudge", "interior", "Обида старшего", cond = { val gp = person(g.grudge); fire(if (gp != null && alive(gp) && !gp.monk && chance(.35)) gp else null) },
            txt = { o -> val x = o.xx; "${x.name} так и не ${sx(x, "простил", "простила")}, что род перешёл не к ${sx(x, "нему", "ней")}. «Отцово добро надо делить по-честному!» — кричит ${sx(x, "он", "она")} на весь двор." },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt("Отдать треть серебра", tag = "−${g.money / 3} сер.", fx = { money(-(g.money / 3)); g.grudge = null; deed(x, sx(x, "Ушёл", "Ушла") + " со своей долей в город"); "${x.name} ${sx(x, "забрал", "забрала")} долю и ${sx(x, "уехал", "уехала")} в город. Расстались без слёз, но и без проклятий." }),
                    Opt("Отказать", roll = Roll(SER, -1, mapOf("kras" to 1, "shch" to 1)),
                        ok = { g.grudge = null; "${x.name} поворчал${sx(x, "", "а")} и остал${sx(x, "ся", "ась")} при доме. Кажется, обида прошла." },
                        bad = { money(-part(.3)); feud(2); g.grudge = null; "Ночью сгорел амбар. ${x.name} ушл${sx(x, "", "а")} к Кожемякиным и там рассказывает про вас всякое." }),
                    Opt("Выгнать со двора", sin = "cruel", fx = { g.grudge = null; g.hex = g.year + 6; "${x.name} ушл${sx(x, "", "а")}, прокляв вас у ворот. В мешке с бобами на шесть лет прибавился второй красный." }),
                )
            }),
        Event("dream", "dream", "Сон", cond = { val n = g.nakaz; fire(n != null && !n.done && g.year - n.y >= 6 && g.year - (n.nag ?: n.y) >= 5 && chance(.4)) },
            txt = { val n = g.nakaz!!; n.nag = g.year; "Снится покойный предок — ${n.by}. Стоит у печи, смотрит с укором: «${n.t}». И уходит, не оглядываясь." },
            opts = { o ->
                listOf(
                    Opt("Проснуться в холодном поту", tag = "камень на душу", fx = { o.h.stones++; "До утра так и не уснули." }),
                    Opt("Заказать панихиду", cost = 2, tag = "2 сер.", fx = { money(-2); "Батюшка отпел, свечи догорели. Сны прекратились — пока." }),
                )
            }),
    )

    override val events = listOf(
        Event("bear", "forest", "Медведь у околицы", w = 2, cond = { h -> fire(age(h) >= 15) },
            txt = { "Шатун задрал корову у Михеевых и теперь кружит возле вашего двора. Бабы не пускают детей за ворота." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Выйти с рогатиной", roll = Roll(SIL, -1, mapOf("hrab" to 2, "bog" to 1, "trus" to -2)),
                        ok = { glory(2); money(3); deed(h, sx(h, "Взял", "Взяла") + " медведя на рогатину"); var s = "Рогатина вошла под лопатку. Шкуру продали в городе за три рубля, а про вас теперь говорят на три деревни."; if (chance(.3) && addTr(h, "shram")) s += " На память остался шрам через всю щёку."; s },
                        bad = { if (chance(.2)) { kill(h, "задрал медведь"); "Медведь оказался сильнее рогатины. Мужики подоспели слишком поздно." } else { addTr(h, "kalek"); "Зверь подмял вас под себя. Мужики отбили, но левая рука с тех пор не гнётся." } }),
                    Opt("Пугнуть огнём и трещотками", roll = Roll(UM), ok = { glory(1); "Зверь ушёл за реку и больше не возвращался." }, bad = { money(-4); "Медведь не испугался трещоток и задрал вашу корову." }),
                    Opt("Запереться и переждать", sin = "coward", fx = { money(-3); "Через три дня зверь ушёл сам, прихватив вашу тёлку." }),
                )
            }),
        Event("fire", "fire", "Пожар", w = 2, cond = { yes },
            txt = { "Ночью занялась соседская баня. Ветер несёт искры прямо на вашу соломенную крышу." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Лезть на крышу, сбивать огонь", roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)),
                        ok = { deed(h, sx(h, "Отстоял", "Отстояла") + " двор от пожара"); "Крышу отстояли. Обгорели брови, зато цел двор." },
                        bad = { money(-part(.4)); if (g.dvor > 0) g.dvor--; addTr(h, "shram"); "Двор сгорел дотла, вы сами еле спрыгнули. Ставить всё заново." }),
                    Opt("Выносить детей и икону", roll = Roll(VER, 2, mapOf("dobr" to 1)),
                        ok = { money(-part(.3)); "Все живы, икона цела. Двор сгорел, но соседи помогли лесом." },
                        bad = { money(-part(.3)); val v = house().filter { it.id != g.head && age(it) < 10 }; if (v.isNotEmpty()) { val c = pick(v); kill(c, "сгорел" + sx(c, "", "а") + " на пожаре"); "В дыму не нашли ребёнка — ${c.name}…" } else "Икона сгорела. Говорят, это худая примета." }),
                    Opt("Будить всю деревню", roll = Roll(SER, 0, mapOf("dobr" to 1, "kras" to 1)), ok = { glory(1); "Деревня встала цепью с вёдрами. Сгорела только баня." }, bad = { money(-part(.3)); "Пока собирались, занялся и ваш двор." }),
                )
            }),
        Event("fair", "fair", "Ярмарка", w = 2, cond = { h -> fire(age(h) >= 14) },
            txt = { "На Покров в уезде ярмарка: лён, кожи, свистульки, цыгане с конями, карусель и бородатая баба за копейку." },
            opts = { o ->
                listOf(
                    Opt("Торговаться до хрипоты", roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)), ok = { money(6); "Продали лён втридорога. Купец ещё и руку жал." }, bad = { money(-2); "Цыган сменял вам хромую кобылу на здоровую. То есть наоборот." }),
                    Opt("Продать честно", roll = Roll(SER, 2, mapOf("chest" to 1)), ok = { money(3); glory(1); "Купцы запомнили честного продавца и звали приезжать ещё." }, bad = { money(1); "Честность нынче не в цене. Продали за бесценок." }),
                    Opt("Гулять на все", sin = "greed", fx = { money(-minOf(5, g.money)); if (chance(.35) && addTr(o.h, "pyan")) "Гуляли три дня. С ярмарки вы вернулись с больной головой и новой привычкой." else "Гуляли три дня. Купили детям петушков на палочке." }),
                )
            }),
        Event("beggar", "road", "Странница у ворот", w = 2, cond = { h -> fire(age(h) >= 14) },
            txt = { "Старуха в лохмотьях просит хлеба. Глаза у неё точь-в-точь как, говорят, были у той, что прокляла основателя рода." },
            opts = { o ->
                listOf(
                    Opt("Накормить и оставить ночевать", cost = 1, tag = "1 сер.", fx = { money(-1); glory(1); if (chance(.5)) { g.bless = 1; "Утром её не было, а на пороге лежала красная нитка. В следующем жребии будет лишний белый боб." } else "Старуха поела, перекрестила избу и ушла до света." }),
                    Opt("Дать корку и проводить", fx = { "Старуха поклонилась и ушла, не оглядываясь." }),
                    Opt("Прогнать собаками", sin = "cruel", fx = { g.hex = g.year + 5; "У околицы старуха обернулась и что-то сказала. В мешке с бобами на пять лет прибавился второй красный." }),
                )
            }),
        Event("meja", "meja", "Межа", w = 2, cond = { h -> fire(age(h) >= 15) },
            txt = { "Кожемякины ночью перенесли межевые колья на сажень в ваш покос." },
            opts = { o ->
                listOf(
                    Opt("Идти к барину судиться", roll = Roll(UM, 0, mapOf("gram" to 2)), ok = { feud(1); glory(1); "Барин велел вернуть колья. Кожемякины затаили злобу." }, bad = { money(-3); "Приказчик взял рубль с вас и два с них. Колья остались где стояли." }),
                    Opt("Выдернуть колья, а Кожемякину — вбить ума", sin = "cruel", roll = Roll(SIL, 0, mapOf("gnev" to 1, "bog" to 2)), ok = { feud(2); glory(1); "Покос ваш. Старший Кожемякин теперь ходит кривой." }, bad = { feud(1); addTr(o.h, "shram"); "Кожемякиных было четверо." }),
                    Opt("Уступить сажень", sin = "coward", fx = { feud(-1); glory(-1); "Мир дороже покоса. Над вами посмеиваются, зато Кожемякины кланяются." }),
                )
            }),
        Event("peace", "interior", "Мировая", w = 3, cond = { fire(if (g.feud >= 2) kidIn(15, 24) { it.spouse == null }.firstOrNull() else null) },
            txt = { o -> val x = o.xx; "Старый Кожемякин пришёл без шапки: «Хватит нам грызться. У меня ${sx(x, "внучка на выданье", "внук в женихах")}. Поженим их — и дело с концом». Речь о том, кто у вас зовётся ${x.name}." },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt("Сосватать", fx = { val s = mkSpouse(x, rival.g); g.feud = 0; glory(1); "Сыграли свадьбу на две деревни. ${x.name} и ${s.name} — и вражде конец." }),
                    Opt("Выставить за дверь", fx = { feud(1); "Вражда достанется и внукам." }),
                )
            }),
        Event("revenge", "night_int", "Месть Кожемякиных", w = 3, cond = { fire(g.feud >= 3) },
            txt = { "Ночью кто-то подпёр вашу дверь колом и бросил в окно горящую паклю." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Выломать дверь и догнать", roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)), ok = { feud(-1); glory(1); "Догнали младшего Кожемякина у реки. Больше не сунутся." }, bad = { money(-4); addTr(h, "shram"); "В темноте вы налетели на вилы." }),
                    Opt("Подать урядникам", roll = Roll(UM, 0, mapOf("gram" to 2)), ok = { feud(-1); money(2); "Кожемякиных оштрафовали в вашу пользу." }, bad = { feud(1); "Свидетелей нет. Кожемякины посмеиваются." }),
                    Opt("Ответить тем же", sin = "cruel", roll = Roll(HIT, 0, mapOf("lzh" to 1, "zhest" to 1)), ok = { money(3); addSecret("${h.name} ${sx(h, "поджёг", "подожгла")} овин Кожемякиных", h); "Овин Кожемякиных сгорел. Никто ничего не видел." }, bad = { feud(2); glory(-2); "Вас видели. Теперь об этом знает весь уезд." }),
                )
            }),
        Event("recruit", "war", "Рекрутский набор", cond = { h -> fire(if (g.year in 1722..1873) sonOf(h).firstOrNull() else null) },
            txt = { o -> "Из уезда пришла бумага: с общины одного рекрута. Мир решил — ${o.xx.name}. Служба — двадцать пять лет, а то и навек." },
            opts = { o -> recruitOpts(this, o.xx, "") }),
        Event("plague", "plague", "Моровое поветрие", cond = { yes }, txt = { "В уезде мор. В соседнем селе за неделю вынесли сорок гробов." }, opts = { plagueOpts(this) }),
        Event("hunger", "field_dry", "Недород", w = 2, cond = { yes },
            txt = { "Рожь выгорела на корню. До весны не дотянуть." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Продать скотину", tag = "−4 сер.", fx = { money(-4); "Корову свели на ярмарку. Детей кормили лебедой и молоком от соседки." }),
                    Opt("Просить у барина в долг", roll = Roll(SER, 0, mapOf("krot" to 1, "kras" to 1)), ok = { money(2); "Барин дал зерна под отработку." },
                        bad = { money(-3); val c = killOne(true); if (c != null && chance(.5)) { kill(c, "голод"); "Барин был не в духе. К весне не стало: ${c.name}." } else "Барин был не в духе. Пережили на мякине." }),
                    Opt("Ночью накопать в барском амбаре", sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)),
                        ok = { money(4); addSecret("${h.name} в недород ${sx(h, "воровал", "воровала")} зерно из барского амбара", h); "Три мешка. Никто не хватился." },
                        bad = { money(-3); addTr(h, "shram"); glory(-2); "Поймали. Высекли на конюшне при всей деревне." }),
                )
            }),
        Event("klad", "klad", "Горшок в борозде", cond = { h -> fire(age(h) >= 14) },
            txt = { "Плуг выворотил глиняный горшок, а в нём — серебро времён царя Алексея Михайловича." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Унести тайком", sin = "lie", fx = { money(15); addSecret("${h.name} ${sx(h, "утаил", "утаила")} клад из поля", h); "Пятнадцать рублей серебром — под половицу." }),
                    Opt("Отнести барину", fx = { money(3); glory(2); deed(h, sx(h, "Отдал", "Отдала") + " барину найденный клад"); "Барин похвалил и отсыпал горсть монет." }),
                    Opt("Отдать на храм", fx = { glory(3); addRelic("Образ Николы", "icon", mapOf(VER to 1), "${h.name}: клад отдан на храм, батюшка подарил образ"); "Батюшка благословил и подарил вам образ Николы Угодника. Вера +1 в жребиях." }),
                )
            }),
        Event("secret", "interior", "Старая тайна", w = 3, cond = { val s = g.secrets.firstOrNull(); if (s != null && g.year - s.y >= 3) Fire(s = s) else null },
            txt = { o -> val x = o.s!!; "По деревне шепчутся: ${x.t}. Дело было ${yrs(g.year - x.y)} назад${if (x.who != o.h.name) ", и того человека давно нет, но спрашивают с вас" else ""}." },
            opts = { o ->
                listOf(
                    Opt("Отпереться", sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)), ok = { g.secrets.removeAt(0); "Пошептались и забыли." }, bad = { g.secrets.removeAt(0); glory(-3); feud(1); "Нашлись свидетели. Слава рода поблёкла." }),
                    Opt("Покаяться на миру", roll = Roll(VER, 2, mapOf("chest" to 1, "krot" to 1)), ok = { g.secrets.removeAt(0); glory(1); o.h.stones = maxOf(0, o.h.stones - 1); "Мир простил. Даже уважать стали." }, bad = { g.secrets.removeAt(0); glory(-2); "Мир не простил, но хоть на душе легче." }),
                    Opt("Заплатить, чтобы молчали", cost = 8, tag = "8 сер.", fx = { money(-8); g.secrets.removeAt(0); "Деньги заткнули самые громкие рты." }),
                )
            }),
        Event("bastard", "road", "Ребёнок вдовы", cond = { h -> fire(h.male && married(h) && age(h) in 20..50) },
            txt = { "У вдовы Аграфены с края деревни родился сын. Люди говорят, что глаза у него ваши." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Признать и взять в дом", fx = { val c = bastard(this, h); val s = spouseOf(h); "${c.name} теперь спит на печи с остальными.${if (s != null) " " + s.name + " неделю не разговаривала с вами." else ""}" }),
                    Opt("Отпереться", sin = "lie", fx = { addSecret("сын вдовы Аграфены — от ${h.name}", h); "Аграфена промолчала. Но мальчик растёт, и глаза у него всё те же." }),
                    Opt("Тайком помогать деньгами", cost = 4, tag = "4 сер.", fx = { money(-4); addSecret("кто-то из ${g.stem}ых тайком носит деньги вдове Аграфене", h); "Аграфена взяла молча." }),
                )
            }),
        Event("kabak", "tavern", "Кабак", w = 2, cond = { h -> fire(age(h) >= 16) },
            txt = { o -> if (has(o.h, "pyan")) "Ноги сами несут к кабаку. Кум уже машет из окна." else "Кум зовёт в кабак «на одну чарку». Вы знаете, чем кончаются одни чарки." },
            opts = { o ->
                listOf(
                    Opt("Пойти", roll = Roll(VER, 0, mapOf("pyan" to -3, "krot" to 1, "bogom" to 1)), ok = { "Выпили по одной, поговорили о погоде и разошлись." }, bad = { if (!addTr(o.h, "pyan")) { money(-4); "Пропили сапоги и полушубок." } else "Одна чарка, вторая, десятая. Кабак стал вам вторым домом." }),
                    Opt("Отказаться", fx = { if (has(o.h, "pyan")) { o.h.stones++; "Отказались. Руки трясутся до вечера. Камень на душу." } else "Кум обиделся, но к Пасхе отошёл." }),
                )
            }),
        Event("gramota", "church", "Дьячок", cond = { h -> fire(!has(h, "gram") && age(h) in 12..45) },
            txt = { "Дьячок Никодим берётся выучить вас грамоте за три рубля и мешок овса." },
            opts = { o ->
                listOf(
                    Opt("Учиться", cost = 3, tag = "3 сер.", roll = Roll(UM, 1, mapOf("umn" to 1)), ok = { money(-3); addTr(o.h, "gram"); deed(o.h, sx(o.h, "Выучился", "Выучилась") + " грамоте"); "К весне вы уже читали Псалтирь и считали оброк лучше приказчика." }, bad = { money(-3); "Буквы так и не сложились в слова. Овса жалко." }),
                    Opt("Не до того", fx = { "Дьячок ушёл учить Кожемякиных." }),
                )
            }),
        Event("wolves", "winter", "Волчья зима", w = 2, cond = { yes },
            txt = { "Зима лютая. Ночами волки воют прямо под окнами и роют подкоп под хлев." },
            opts = { o ->
                listOf(
                    Opt("Сторожить с топором", roll = Roll(SIL, 0, mapOf("hrab" to 2, "trus" to -2)), ok = { glory(1); money(1); "Положили двух волков. Шкуры — на тулуп младшим." }, bad = { money(-4); if (chance(.3)) addTr(o.h, "shram"); "Волки вырезали овец, пока вы отбивались от вожака." }),
                    Opt("Поставить капканы", roll = Roll(UM), ok = { money(3); "Три волка в капканах. Шкуры продали." }, bad = { money(-2); "В капкан попала соседская собака." }),
                    Opt("Заложить ворота и молиться", fx = { money(-3); "Утром хлев был пуст." }),
                )
            }),
        Event("sick", "night_int", "Жар", w = 3, cond = { val k = kidIn(0, 9); fire(if (k.isEmpty()) null else pick(k)) },
            txt = { o -> "${o.xx.name} горит в жару третью ночь. Не ест, не узнаёт мать." },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt("Позвать знахарку", cost = 1, tag = "1 сер.", roll = Roll(ZDR, 1, ofX = true), ok = { money(-1); "Знахарка отпоила малиной и шептала до рассвета. Жар спал." }, bad = { money(-1); kill(x, "горячка"); "Знахарка развела руками." }),
                    Opt("Молиться всю ночь", roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)), ok = { "К утру ${x.name} ${sx(x, "попросил", "попросила")} молока." }, bad = { kill(x, "горячка"); "Свеча догорела раньше, чем кончилась ночь." }),
                    Opt("Везти в город к лекарю", cost = 6, tag = "6 сер.", roll = Roll(UM, 4, mapOf("gram" to 1, "gor" to 1)), ok = { money(-6); "Немец-лекарь дал порошков. Через неделю — на ногах." }, bad = { money(-6); kill(x, "горячка"); "Не довезли." }),
                )
            }),
        Event("deserter", "night_int", "Беглый солдат", cond = { h -> fire(age(h) >= 16) },
            txt = { "Ночью в окно стучит беглый солдат. Просит спрятать до весны." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Спрятать в овине", roll = Roll(HIT, 0, mapOf("dobr" to 1, "lzh" to 1)), ok = { addRelic("Солдатский штык", "sword", mapOf(SIL to 1), "беглый солдат оставил его в благодарность"); deed(h, sx(h, "Спрятал", "Спрятала") + " беглого солдата"); "Весной он ушёл и оставил вам штык. Сила +1 в жребиях." }, bad = { addTr(h, "shram"); glory(-1); "Его нашли у вас. Вас высекли." }),
                    Opt("Выдать уряднику", sin = "cruel", fx = { money(3); "Дали три рубля. Солдата прогнали сквозь строй." }),
                    Opt("Дать хлеба и прогнать", fx = { "Он ушёл в метель." }),
                )
            }),
        Event("obrok", "barin", "Новый барин", w = 2, cond = { h -> fire(g.year < 1861 && age(h) >= 16) },
            txt = { "Новый барин приехал из Петербурга и поднял оброк вдвое." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Платить", tag = "−8 сер.", fx = { money(-8); "Затянули пояса." }),
                    Opt("Идти просить всем миром", roll = Roll(SER, 0, mapOf("kras" to 1, "krot" to 1)), ok = { money(-4); glory(1); "Барин уступил половину. Мир кланялся вам в пояс." }, bad = { money(-8); "Барин даже не вышел на крыльцо." }),
                    Opt("Подбить мужиков на бунт", roll = Roll(SIL, -3, mapOf("hrab" to 2, "gnev" to 1, "bog" to 1)), ok = { glory(3); deed(h, sx(h, "Поднял", "Подняла") + " мужиков против барина"); "Приказчика выгнали, барин уехал обратно в Петербург." },
                        bad = { if (chance(.4)) { kill(h, "запорот" + sx(h, "", "а") + " на конюшне"); "Зачинщиков пороли до смерти." } else { addTr(h, "kalek"); glory(1); "Зачинщиков пороли. Вы выжили, но ходите с палкой." } }),
                )
            }),
        Event("pilgrim", "church", "Богомолье", cond = { h -> fire(age(h) >= 16 && !has(h, "bogom")) },
            txt = { "Соседи идут пешком к Троице-Сергию. Зовут с собой." },
            opts = { o ->
                listOf(
                    Opt("Идти", roll = Roll(ZDR, 2, mapOf("krot" to 1)), ok = { addTr(o.h, "bogom"); o.h.stones = 0; glory(1); if (chance(.4)) addRelic("Лаврская иконка", "icon", mapOf(VER to 1), "принесена с богомолья"); "Вернулись через два месяца налегке. Камни с души как рукой сняло." }, bad = { o.h[ZDR]--; "Простудились в дороге и вернулись с полпути." }),
                    Opt("Не время", fx = { "Покос не ждёт." }),
                )
            }),
        Event("smith", "village", "Кузнец Аким", cond = { fire(g.relics.none { it.ic == "axe" }) },
            txt = { "Кузнец Аким берётся сковать топор с вашим клеймом. «Такой и внуки не сточат»." },
            opts = { o ->
                listOf(
                    Opt("Заказать", cost = 4, tag = "4 сер.", fx = { money(-4); addRelic("Топор с клеймом", "axe", mapOf(SIL to 1), "скован для главы рода"); "Топор лёг в руку как влитой. Сила +1 в жребиях." }),
                    Opt("Обойдёмся старым", fx = { "Старый топор ещё послужит." }),
                )
            }),
        Event("town", "village", "В город", cond = { fire(kidIn(16, 25) { !has(it, "gor") && it.spouse == null }.firstOrNull()) },
            txt = { o -> "${o.xx.name} рвётся в город: «Там фабрики, там жизнь. Что мне тут — коровам хвосты крутить?»" },
            opts = { o ->
                val x = o.xx
                listOf(
                    Opt("Отпустить", fx = { addTr(x, "gor"); x[HIT] += 1; x.away = g.year + ri(3, 7); "${x.name} ${sx(x, "уехал", "уехала")} с обозом. Пишет редко." }),
                    Opt("Не пускать", fx = { x.stones++; "${x.name} остал${sx(x, "ся", "ась")}, но смотрит волком." }),
                )
            }),
    )

    override val history = listOf(
        Event("north", "war", "Великая Северная война", y0 = 1700, y1 = 1721, p = .25, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> "Государь воюет со шведом. Рекрутов берут с каждых двадцати дворов, и жребий пал на вашего: ${o.xx.name}." }, opts = { o -> recruitOpts(this, o.xx, "Северная война") }),
        Event("chuma", "plague", "Чума", y0 = 1770, y1 = 1772, p = .6, cond = { yes }, txt = { "Из Москвы пришла чума. Заставы на дорогах, костры на перекрёстках, по деревням возят мертвецов." }, opts = { plagueOpts(this) }),
        Event("pugach", "war", "Пугачёвщина", y0 = 1773, y1 = 1775, p = .6, cond = { h -> fire(age(h) >= 16) },
            txt = { "По Волге идёт «царь Пётр Фёдорович», вешает бар и жжёт усадьбы. Его люди уже в соседнем уезде." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Пристать к Пугачёву", roll = Roll(SIL, -2, mapOf("hrab" to 2, "gnev" to 1)), ok = { money(6); glory(2); addSecret("${h.name} ходил${sx(h, "", "а")} с Пугачёвым", h); "Вернулись с барским добром. Об этом лучше молчать." },
                        bad = { if (chance(.5)) { kill(h, "казнён" + sx(h, "", "а") + " за бунт"); "Отряд разбили. Зачинщиков повесили на воротах." } else { addTr(h, "kalek"); "Отряд разбили. Еле ушли, с перебитой ногой." } }),
                    Opt("Спрятать барина в погребе", roll = Roll(HIT, 0, mapOf("dobr" to 1)), ok = { money(5); glory(2); addRelic("Барский перстень", "ring", mapOf(HIT to 1), "подарен спасённым барином"); "Барин выжил и подарил перстень. Хитрость +1 в жребиях." }, bad = { addTr(h, "shram"); "Пугачёвцы нашли барина и выпороли вас за компанию." }),
                    Opt("Сидеть тихо", fx = { "Беда прошла стороной." }),
                )
            }),
        Event("y1812", "war", "Двенадцатый год", y0 = 1812, y1 = 1812, p = 1.0, cond = { h -> fire(age(h) in 16..50) },
            txt = { "Француз под Смоленском. Губерния собирает ополчение: с каждых ста душ — десять ратников." },
            opts = { o ->
                val h = o.h
                listOf(
                    Opt("Идти в ополчение", roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1)), ok = { glory(4); addRelic("Медаль 1812 года", "medal", mapOf(SER to 1), "${h.name} — ополченец"); deed(h, sx(h, "Воевал", "Воевала") + " с французом"); "Дошли до Парижа и вернулись с медалью. Сердце +1 в жребиях." },
                        bad = { if (chance(.5)) { kill(h, "пал" + sx(h, "", "а") + " под Бородином"); "Под Бородином." } else { addTr(h, "kalek"); glory(2); "Вернулись без ноги, но с почётом." } }),
                    Opt("Дать обоз сена и лошадь", cost = 5, tag = "5 сер.", fx = { money(-5); glory(1); "Обоз ушёл к армии." }),
                    Opt("Прятаться в лесу", sin = "coward", fx = { "Француз до вас не дошёл. А соседи запомнили." }),
                )
            }),
        Event("volya", "village", "Воля", y0 = 1861, y1 = 1863, p = 1.0, cond = { yes },
            txt = { "В церкви читают манифест: крепостной неволе конец. Землю — за выкуп, на сорок девять лет." },
            opts = { o ->
                listOf(
                    Opt("Выкупать надел", cost = 10, tag = "10 сер., двор растёт", fx = { money(-10); g.dvor = minOf(4, g.dvor + 1); glory(1); "Земля теперь ваша. Своя!" }),
                    Opt("Податься в город на фабрику", fx = { money(6); addTr(o.h, "gor"); "Ткацкая фабрика, двенадцать часов в день. Зато живые деньги." }),
                    Opt("Остаться при общине", fx = { "Всё как было, только барина нет." }),
                )
            }),
        Event("golod", "field_dry", "Царь-голод", y0 = 1891, y1 = 1892, p = 1.0, cond = { yes },
            txt = { "Засуха на всю Россию. Земство раздаёт муку, но её не хватает." },
            opts = {
                listOf(
                    Opt("Стоять в очереди за земской мукой", roll = Roll(SER, 0, mapOf("gram" to 1)), ok = { "Дотянули до весны." }, bad = { val c = killOne(true); if (c != null) { kill(c, "голод"); "Не стало: ${c.name}." } else "Дотянули, но еле." }),
                    Opt("Продать всё и уехать за Урал", fx = { money(8); g.dvor = maxOf(1, g.dvor); g.place = "Сибирь, переселенческий участок"; "Переселенческий вагон, три недели пути. Новая земля, чёрная и жирная." }),
                )
            }),
        Event("ww1", "war", "Германская", y0 = 1914, y1 = 1916, p = .7, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> "Мобилизация. ${o.xx.name} получил повестку." },
            opts = { o -> val x = o.xx; listOf(Opt("Проводить", fx = { sendAway(x, "Германская"); "Гармошка, слёзы, эшелон." }), Opt("Спрятать", sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)), ok = { "Отсиделся в погребе." }, bad = { sendAway(x, "Германская"); "Нашли. Отправили в маршевую роту." })) }),
        Event("rev", "war", "Продотряд", y0 = 1918, y1 = 1921, p = .7, cond = { h -> fire(age(h) >= 16) },
            txt = { "Во двор въезжает продотряд: «Хлеб — государству. Излишки сдать»." },
            opts = { o ->
                listOf(
                    Opt("Отдать", fx = { money(-part(.5)); "Выгребли до зёрнышка." }),
                    Opt("Спрятать зерно в яме под хлевом", roll = Roll(HIT, 0, mapOf("lzh" to 1)), ok = { "Не нашли." }, bad = { money(-g.money); addTr(o.h, "shram"); "Нашли. Забрали всё, вас били прикладами." }),
                    Opt("Взять вилы", sin = "cruel", roll = Roll(SIL, -4, mapOf("hrab" to 2)), ok = { glory(2); "Отряд уехал ни с чем. Надолго ли?" }, bad = { kill(o.h, "расстрелян" + sx(o.h, "", "а") + " продотрядом"); "Вилы против винтовок." }),
                )
            }),
        Event("kolhoz", "village", "Колхоз", y0 = 1929, y1 = 1932, p = 1.0, cond = { h -> fire(age(h) >= 16) },
            txt = { if (g.dvor >= 3) "Ваш двор записали в кулацкие. Скоро придут описывать." else "В селе организуют колхоз. Всех зовут, а кто не идёт — тех на заметку." },
            opts = { o ->
                listOf(
                    Opt("Сдать всё в колхоз", fx = { g.dvor = minOf(g.dvor, 1); money(-part(.7)); "Корову увели на общий двор. Ходите на работу за трудодни." }),
                    Opt("Уехать ночью в город", roll = Roll(HIT, 0, mapOf("gor" to 2, "gram" to 1)), ok = { g.dvor = 1; addTr(o.h, "gor"); g.place = "город, барак у завода"; "Справка из сельсовета, поезд, завод. Живы." },
                        bad = { if (chance(.5)) { kill(o.h, "умер" + sx(o.h, "", "ла") + " в ссылке"); "Сняли с поезда. Нарым." } else { g.dvor = 0; money(-g.money); "Вернули обратно и отобрали всё." } }),
                )
            }),
        Event("ww2", "war", "Война", y0 = 1941, y1 = 1943, p = .8, cond = { h -> fire(sonOf(h).firstOrNull()) },
            txt = { o -> "Двадцать второго июня по радио объявили войну. ${o.xx.name} уходит на фронт." },
            opts = { o -> listOf(Opt("Проводить", fx = { sendAway(o.xx, "Великая Отечественная"); "Ушёл с первым эшелоном." })) }),
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
        Opt("${p.name} из ${p.from}, ${yrs(age(p))}",
            tag = "${p.tr.joinToString(", ") { tname(it, p) }} · ${STAT_NAME[top]} ${eff(p, top)} · ${if (p.dw >= 0) "приданое +" + p.dw else "выкуп " + (-p.dw)} $cur${if (p.hidden != null) " · [[$hiddenNote]]" else ""}",
            cost = if (p.dw < 0) -p.dw else 0, cand = p,
            fx = { reg(p); p.spouse = h.id; h.spouse = p.id; p.wed = g.year; h.wed = g.year; money(p.dw); wed(p, h) })
    }
}

fun monkOpt(e: Engine, o: Occ, text: String) = with(e) {
    Opt("Уйти в монастырь", tag = "[[вы выбираете наследника]]", fx = { o.h.monk = true; deed(o.h, sx(o.h, "Ушёл", "Ушла") + " в монастырь"); log("${o.h.name} ${sx(o.h, "ушёл", "ушла")} в монастырь.", "big"); text })
}

/** A son born on the side, taken into the house. */
fun bastard(e: Engine, h: Person): Person = with(e) {
    val c = reg(newP(pickName(true, (kidsOf(h).map { it.name } + h.name).toSet()), true, father = h.id, blood = true, gen = h.gen + 1, bast = true,
        st = IntArray(6) { (h.st[it] + ri(-2, 1)).coerceIn(1, 9) }, look = Look(h.look.skin, h.look.hair, pick(Looks.CLOTH_M), pick(Looks.KERCH))))
    glory(-1)
    log("В дом взят незаконный сын главы рода — ${c.name}.", "birth")
    c
}
