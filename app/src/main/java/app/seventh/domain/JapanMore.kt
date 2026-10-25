package app.seventh.domain

import app.seventh.domain.Stat.HIT
import app.seventh.domain.Stat.SER
import app.seventh.domain.Stat.SIL
import app.seventh.domain.Stat.UM
import app.seventh.domain.Stat.VER
import app.seventh.domain.Stat.ZDR

/*
 * More of Japan: tempers and ages that make their own trouble, debts and vows that come due years later,
 * folk wonders (kappa, tanuki, a fox bride), two ways off the land (a shop in town, winter paper),
 * and more history, from the Dog Shogun to the air raids.
 *
 * Marks (flags): a chain sets a key with the year; its pay-off fires some years later and calls [done],
 * which leaves "<key>_done" behind. A family's way is "way_shop" or "way_paper"; losing it leaves "way_lost".
 */

/** The mark [k] was set at least [n] years ago. */
private fun Engine.ago(k: String, n: Int) = (since(k) ?: -1) >= n
private fun Engine.done(k: String) { unflag(k); flag(k + "_done") }
private fun Engine.noWay() = !flagged("way_shop") && !flagged("way_paper")
private fun Engine.loseWay(k: String) { unflag("way_$k"); flag("way_lost") }
private fun Engine.edo() = if (g.year < 1868) t("Эдо", "Edo") else t("Токио", "Tokyo")
private fun Engine.hex(n: Int) { g.hex = maxOf(g.hex, g.year + n) }
private fun Engine.ease(p: Person) { if (p.stones > 0) p.stones-- }
private const val BLESS_RU = " В следующем жребии будет лишний белый боб."
private const val BLESS_EN = " There will be an extra white bean in the next draw."

/** A second wife or husband for an old widowed head, near their own age. */
private fun Engine.lateSpouse(h: Person): Person {
    val s = reg(newP(pickName(!h.male, setOf(h.name)), !h.male, born = g.year - age(h) + ri(-4, 6), st = rstats(2, 7), from = pick(land.families)))
    addTr(s, "vdov"); s.grown = true
    s.spouse = h.id; h.spouse = s.id; s.wed = g.year; h.wed = g.year
    h.tr.remove("vdov")
    return s
}

/** Marries [p] to someone already made (a foundling, a girl from the mist). */
private fun Engine.wedTo(p: Person, s: Person) {
    s.spouse = p.id; p.spouse = s.id; s.wed = g.year; p.wed = g.year; s.grown = true
}

/** A grown natural son of [f] who turns up at the gate. */
private fun Engine.lateBastard(f: Person): Person {
    val c = reg(newP(pickName(true, (kidsOf(f).map { it.name } + f.name).toSet()), true, born = g.year - ri(14, 16), father = f.id, blood = true, gen = f.gen + 1, bast = true,
        st = IntArray(6) { (f.st[it] + ri(-2, 1)).coerceIn(1, 9) }, look = Look(f.look.skin, f.look.hair, pick(Looks.CLOTH_M), pick(Looks.KERCH))))
    glory(-1)
    log(t("В дом взят незаконный сын — ${c.name}, ${yrs(age(c))}.", "A natural son was taken into the house: ${c.name}, aged ${age(c)}."), "birth")
    return c
}

internal val japanMoreEvents: List<Event> = listOf(
    // ---------------------------------------------------------------- tempers, ages, family
    Event("tab", "tavern", Tx("Долговая книжка", "The Tab"), cond = { h -> fire(has(h, "pyan") && age(h) >= 18 && !flagged("sake_tab")) },
        txt = { pick(listOf(
            t("Приказчик из сакэварни пришёл с толстой книжкой. Под вашим именем три страницы: чашечка, кувшин, кувшин, кувшин. «Хозяин ждал до Нового года. Новый год прошёл».",
                "The brewery clerk has come with a fat ledger. Under your name are three pages: a cup, a jug, a jug, a jug. “The master waited till New Year. New Year has come and gone.”"),
            t("Хозяин харчевни у моста больше не наливает в долг. Он кладёт перед вами счёт, длинный, как свиток с сутрой, и ждёт.",
                "The keeper of the eating-house by the bridge will pour on credit no longer. He lays your bill in front of you, as long as a sutra scroll, and waits."),
        )) },
        opts = { o ->
            listOf(
                Opt(t("Расплатиться сполна", "Pay it all"), cost = 6, tag = t("6 мон", "6 mon"),
                    fx = { money(-6); t("Отдали всё до последнего мона. Ваше имя вычеркнули одной жирной чертой.", "You paid to the last mon. Your name was struck out with one fat stroke of the brush.") }),
                Opt(t("Заложить осенний рис с дальнего чека", "Pledge the autumn rice from the far paddy"), tag = t("[[осенью придут за рисом]]", "[[they will come for the rice in autumn]]"),
                    fx = { flag("sake_tab"); t("Приказчик записал: осенний рис с дальнего чека — в счёт долга. И налил ещё чашечку, за счёт заведения.", "The clerk wrote it down: the autumn rice from the far paddy, against the debt. Then he poured you one more cup, on the house.") }),
                Opt(t("Дать обет Конпире: ни капли", "Vow to Konpira: not a drop"), roll = Roll(VER, -1, mapOf("krot" to 1, "bogom" to 1)),
                    ok = { o.h.tr.remove("pyan"); money(-3); deed(o.h, t(sx(o.h, "Бросил", "Бросила") + " пить по обету", "Gave up drink by a vow"))
                        t("Вы дали обет у святилища Конпиры: ни капли до самой смерти. Долг отрабатывали всю зиму, но руки больше не трясутся.", "You made your vow at the Konpira shrine: not a drop till the day you die. You worked off the debt all winter, but your hands no longer shake.") },
                    bad = { money(-3); o.h.stones++; t("Обета хватило до праздника урожая. А долг отдавать всё равно.", "The vow lasted till the harvest festival. The debt must be paid all the same.") }),
            )
        }),
    Event("c_tab", "field", Tx("Дальний чек", "The Far Paddy"), w = 3, cond = { fire(ago("sake_tab", 1)) },
        txt = { t("Осень. На меже вашего дальнего чека стоит приказчик из сакэварни, с ним двое работников с серпами. Рис налился, колос к колосу.",
            "Autumn. The brewery clerk stands on the bank of your far paddy with two hired men and their sickles. The rice is full, ear against ear.") },
        opts = { o ->
            listOf(
                Opt(t("Пусть жнут", "Let them reap"), fx = { done("sake_tab"); money(-5); t("Сжали всё до последнего колоса. Долг закрыт, а зима будет длинной.", "They reaped every last ear. The debt is paid, and the winter will be long.") }),
                Opt(t("Просить хозяина об отсрочке", "Ask the brewer for time"), roll = Roll(SER, 0, mapOf("kras" to 1, "krot" to 1)),
                    ok = { done("sake_tab"); money(-2); t("Хозяин махнул рукой: половину рисом, половину — работой у его чанов. Две недели вы мешали сусло и ни разу не пригубили.", "The brewer waved a hand: half in rice, and the other half in work at his vats. For two weeks you stirred the mash and never once took a sip.") },
                    bad = { done("sake_tab"); money(-5); glory(-1); t("Хозяин и слушать не стал. Рис увезли, а про долг узнала вся деревня.", "The brewer would not listen. The rice was carted off, and the whole village heard about the debt.") }),
                Opt(t("Сжать ночью самим", "Reap it yourselves by night"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)),
                    ok = { done("sake_tab"); money(3); addSecret(t("${o.h.name} ночью ${sx(o.h, "сжал", "сжала")} рис, заложенный сакэварне", "${o.h.name} reaped by night the rice pledged to the brewery"), o.h)
                        t("К утру чек стоял голый. Приказчик ругался до хрипоты, но свидетелей нет.", "By morning the paddy was bare. The clerk swore himself hoarse, but there were no witnesses.") },
                    bad = { done("sake_tab"); money(-6); glory(-2); t("Вас застали с серпом при луне. Рис увезли, а ваше имя вывесили на доске у управы.", "You were caught with a sickle under the moon. The rice was carted off, and your name went up on the board outside the office.") }),
            )
        }),

    Event("wrath", "village", Tx("Горячая голова", "A Short Fuse"), w = 2, cond = { h -> fire(has(h, "gnev") && age(h) >= 16) },
        txt = { pick(listOf(
            t("На празднике сын старосты толкнул вас локтем, а вы ответили так, что он улетел в канаву вместе с бочонком сакэ. Староста не забыл.",
                "At the festival the headman's son elbowed you, and you answered so hard that he went into the ditch along with a keg of sake. The headman has not forgotten."),
            t("Носильщик на тракте обругал вас за то, что не посторонились. Вы ответили кулаком. Носильщик оказался из людей старосты, и теперь староста ждёт извинений.",
                "A porter on the highway cursed you for not stepping aside. You answered with your fist. The porter turned out to be one of the headman's men, and now the headman is waiting for an apology."),
        )) },
        opts = { o ->
            listOf(
                Opt(t("Пойти с поклоном и подарком", "Go with a bow and a gift"), cost = 3, tag = t("3 мон", "3 mon"),
                    fx = { money(-3); t("Вы принесли старосте короб сладостей и поклонились до самой циновки. Староста принял и отметил про себя: кланяться вы всё-таки умеете.", "You brought the headman a box of sweets and bowed down to the mat. He accepted, and noted to himself that you can bow after all.") }),
                Opt(t("Стоять на своём: он первый начал", "Stand firm: he started it"), roll = Roll(SER, -1, mapOf("kras" to 1, "chest" to 1)),
                    ok = { glory(1); t("Полдеревни видело, кто начал. Староста поворчал и отступился.", "Half the village saw who started it. The headman grumbled and let it go.") },
                    bad = { money(-3); glory(-1); t("Староста припомнил вам всё, вплоть до недоимки трёхлетней давности.", "The headman remembered everything, down to an arrear from three years back.") }),
                Opt(t("Поклясться у Инари держать себя в руках", "Swear before Inari to keep your temper"), roll = Roll(VER, -1, mapOf("bogom" to 1)),
                    ok = { addTr(o.h, "krot"); t("Месяц вы каждое утро стояли у красных ворот. Теперь, когда закипает, вы считаете до десяти. Обычно хватает.", "For a month you stood at the red gate every morning. Now, when your blood boils, you count to ten. Usually that is enough.") },
                    bad = { o.h.stones++; t("Клятвы хватило до первого спора о воде.", "The oath lasted until the first quarrel over water.") }),
            )
        }),

    Event("girl", "road", Tx("Девочка у ворот", "A Girl at the Gate"), w = 2, cond = { h -> fire(has(h, "dobr") && age(h) >= 20 && !flagged("foundling")) },
        txt = { t("Весной у ворот стоит оборванный крестьянин из горной деревни, с ним девочка лет шести. Подать не уплачена, и он ведёт её в город — продавать в служанки, а может, и куда похуже. «Возьмите вы. Хоть знать буду, где она».",
            "In spring a ragged farmer from a hill village stands at your gate with a girl of about six. His tax is unpaid, and he is taking her to town to sell her into service, or somewhere worse. “You take her. At least I'll know where she is.”") },
        opts = {
            listOf(
                Opt(t("Взять в дом", "Take her in"), cost = 2, tag = t("2 мон · лишний рот", "2 mon · one more mouth"),
                    fx = { money(-2); val f = reg(newP(pickName(false), false, born = g.year - 6)); addTr(f, pick(listOf("dobr", "krot", "chest"))); flag("foundling"); flag("found_id", f.id)
                        t("Её зовут ${f.name}. Первую неделю она спит у очага, свернувшись, как котёнок, и не говорит ни слова.", "Her name is ${f.name}. For the first week she sleeps curled up by the hearth like a kitten and says not a word.") }),
                Opt(t("Дать отцу денег на подать", "Give her father the tax money"), cost = 5, tag = t("5 мон", "5 mon"),
                    fx = { money(-5); glory(1); t("Крестьянин заплакал, поклонился до земли и увёл девочку обратно в горы — домой.", "The farmer wept, bowed to the ground and took the girl back up into the hills, home.") }),
                Opt(t("Закрыть ворота", "Close the gate"), sin = "cruel", fx = { t("Они ушли по дороге к городу. Вы долго не могли уснуть.", "They went off down the road to town. It was a long time before you slept.") }),
            )
        }),
    Event("c_found", "interior", Tx("Приёмыш", "The Foundling"), w = 3, cond = { fire(ago("foundling", 11) && person(g.flags["found_id"])?.let { alive(it) && it.spouse == null } != false) },
        txt = { o -> val n = person(g.flags["found_id"])?.name ?: t("Девочка", "The girl")
            t("$n, которую вы когда-то взяли у ворот, выросла. Работает за двоих, поёт за прялкой и зовёт вас ${sx(o.h, "отцом", "матерью")}. Пора решать её судьбу.",
                "$n, the girl you once took in at the gate, has grown up. She works for two, sings at the spinning wheel and calls you ${sx(o.h, "father", "mother")}. It is time to settle her future.") },
        opts = { o ->
            fun girl() = person(g.flags["found_id"])?.takeIf { alive(it) } ?: reg(newP(pickName(false), false, born = g.year - 18))
            val l = mutableListOf<Opt>()
            sonOf(o.h).firstOrNull()?.let { s ->
                l.add(Opt(t("Женить на ней ${s.name}", "Marry her to ${s.name}"),
                    fx = { val f = girl(); wedTo(s, f); done("foundling"); glory(1)
                        t("Свадьбу сыграли без накодо и без выкупа: невеста и так в доме. ${s.name} давно на неё заглядывался.", "The wedding needed no go-between and no bride price: the bride was in the house already. ${s.name} had had an eye on her for a long time.") }))
            }
            l.add(Opt(t("Дать приданое и выдать замуж", "Give her a dowry and marry her off"), cost = 5, tag = t("5 мон", "5 mon"),
                fx = { money(-5); glory(2); done("foundling"); t("За ${girl().name} посватался сын мельника. У ворот она поклонилась вам в ноги — так же, как когда-то её отец.", "The miller's son asked for ${girl().name}. At the gate she bowed down at your feet, just as her father once did.") }))
            l.add(Opt(t("Отпустить в услужение в ${edo()}", "Let her go into service in ${edo()}"),
                fx = { money(2); done("foundling"); t("Её взяли в купеческий дом в ${edo()}. На каждый Новый год приходит письмо и пара монет.", "A merchant house in ${edo()} took her on. Every New Year a letter comes, with a couple of coins.") }))
            l
        }),

    Event("lend", "interior", Tx("В рост", "Lending at Interest"), w = 2, cond = { h -> fire(has(h, "zhad") && age(h) >= 18 && g.money >= 10 && !flagged("loan")) },
        txt = { t("Сосед Ёсаку просит взаймы десять мон до осени — купить вола. Мнёт шапку в руках: знает, что вы дадите, и знает, под какой процент.",
            "Your neighbor Yosaku asks to borrow ten mon till autumn, to buy an ox. He twists his hat in his hands: he knows you will lend, and he knows at what rate.") },
        opts = {
            listOf(
                Opt(t("Дать под три десятых", "Lend at three in ten"), cost = 10, tag = t("10 мон", "10 mon"), sin = "greed",
                    fx = { money(-10); flag("loan"); t("Ёсаку приложил палец к расписке. К осени — тринадцать мон.", "Yosaku pressed his thumb to the note. Thirteen mon by autumn.") }),
                Opt(t("Дать без процентов", "Lend at no interest"), cost = 10, tag = t("10 мон", "10 mon"),
                    fx = { money(-10); flag("loan"); flag("loan_free"); t("Ёсаку не поверил своим ушам и поклонился трижды.", "Yosaku could not believe his ears and bowed three times.") }),
                Opt(t("Отказать", "Refuse"), fx = { t("Ёсаку пошёл просить к Курокаве.", "Yosaku went to ask the Kurokawas.") }),
            )
        }),
    Event("c_loan", "interior", Tx("Срок долга", "The Loan Falls Due"), w = 3, cond = { fire(ago("loan", 1)) },
        txt = { if (flagged("loan_free")) t("Осень. Ёсаку стоит у порога с мешком риса и связкой монет: «Вол вытянул нам весь урожай. Вот долг — и рис сверху, не спорьте».",
                "Autumn. Yosaku stands at the door with a sack of rice and a string of coins. “The ox pulled us through the whole harvest. Here is the debt, and the rice on top. Don't argue.”")
            else t("Осень. Вол у Ёсаку пал посреди пахоты, урожай не задался. Ёсаку стоит у порога без шапки: тринадцать мон ему не отдать.",
                "Autumn. Yosaku's ox died in the middle of plowing, and the harvest failed. Yosaku stands at the door bareheaded: he cannot pay thirteen mon.") },
        opts = { o ->
            if (flagged("loan_free")) listOf(
                Opt(t("Взять и долг, и рис", "Take the debt and the rice"), fx = { done("loan"); unflag("loan_free"); money(12); t("Теперь Ёсаку первым бежит к вам с ведром, если где задымит.", "Now Yosaku is the first to come running with a bucket whenever there's smoke.") }),
                Opt(t("Взять долг, а рис вернуть", "Take the debt, give back the rice"), fx = { done("loan"); unflag("loan_free"); money(10); glory(2); t("Об этом говорили у колодца до самых снегов.", "They talked about it at the well until the snow came.") }),
            ) else listOf(
                Opt(t("Взять его поле в уплату", "Take his field in payment"), sin = "greed",
                    fx = { done("loan"); money(15); glory(-2); t("Поле Ёсаку теперь ваше. Сам Ёсаку пашет его, как прежде, — только уже арендатором.", "Yosaku's field is yours now. Yosaku plows it as before, only now as your tenant.") }),
                Opt(t("Простить проценты и подождать", "Forgive the interest and wait"),
                    fx = { done("loan"); money(8); glory(1); t("Ёсаку отдавал понемногу до весны, а остальное отработал на вашем чеке.", "Yosaku paid a little at a time till spring and worked off the rest in your paddy.") }),
                Opt(t("Простить всё", "Forgive it all"), fx = { done("loan"); glory(2); ease(o.h); t("Ёсаку не нашёл слов и просто поклонился в землю.", "Yosaku could find no words and simply bowed to the ground.") }),
            )
        }),

    Event("lineage", "interior", Tx("Родословный свиток", "The Family Scroll"), w = 2, cond = { h -> fire(has(h, "lzh") && age(h) >= 20 && !flagged("fake_line") && !flagged("fake_line_done")) },
        txt = { t("Вы так часто рассказывали за чашечкой, будто прадед ваш был самураем и сложил мечи из гордости, что вам начали верить. Бродячий писарь берётся составить родословный свиток: с гербом, печатями и прадедом на коне.",
            "You have told it over sake so often, that your great-grandfather was a samurai who laid down his swords out of pride, that people have begun to believe it. A traveling scribe offers to draw up a family scroll, with a crest, seals and great-grandfather on horseback.") },
        opts = {
            listOf(
                Opt(t("Заказать свиток", "Order the scroll"), cost = 4, tag = t("4 мон", "4 mon"), sin = "lie",
                    fx = { money(-4); glory(2); flag("fake_line"); t("Свиток повесили в нише. Гости кланяются ему ниже, чем вам.", "The scroll hangs in the alcove. Guests bow lower to it than to you.") }),
                Opt(t("Отшутиться", "Laugh it off"), fx = { t("Писарь пожал плечами и ушёл. Прадед остался тем, кем был: крестьянином с мотыгой.", "The scribe shrugged and left. Great-grandfather stayed what he was: a farmer with a hoe.") }),
            )
        }),
    Event("c_line", "barin", Tx("Проверка родословной", "The Pedigree Examined"), w = 3, cond = { fire(ago("fake_line", 8)) },
        txt = { if (g.year < 1871) t("Из замка приехал чиновник: князь велел проверить, кто в округе зовёт себя потомком самураев и по какому праву. Ваш свиток с прадедом на коне ему очень интересен.",
                "An official has come from the castle: the lord has ordered a check on who in the district claims samurai blood, and by what right. Your scroll with great-grandfather on horseback interests him greatly.")
            else t("Новая власть составляет семейные реестры и спрашивает каждого, из какого он сословия. Писарь из управы долго разглядывает ваш свиток с прадедом на коне.",
                "The new government is drawing up family registers and asks everyone which estate they belong to. The clerk from the district office studies your scroll with great-grandfather on horseback for a long time.") },
        opts = {
            listOf(
                Opt(t("Признаться", "Confess"), roll = Roll(VER, 1, mapOf("chest" to 1, "krot" to 1)),
                    ok = { done("fake_line"); glory(-1); t("Чиновник посмеялся, велел снять свиток и штрафа не взял. Прадед вернулся к мотыге.", "The official laughed, had the scroll taken down and took no fine. Great-grandfather went back to his hoe.") },
                    bad = { done("fake_line"); glory(-3); money(-3); t("Свиток сожгли у управы, а вас оштрафовали за подлог. Над вами смеются три деревни.", "The scroll was burned outside the office and you were fined for forgery. Three villages are laughing at you.") }),
                Opt(t("Стоять на своём", "Stick to the story"), roll = Roll(HIT, -2, mapOf("lzh" to 2, "gram" to 1)),
                    ok = { done("fake_line"); glory(2); t("Чиновник покивал и переписал прадеда в книгу. Теперь это правда — на бумаге с печатью.", "The official nodded and copied great-grandfather into his book. Now it is true, on paper, with a seal.") },
                    bad = { done("fake_line"); glory(-3); money(-5); t("Нашлись старики, которые помнили вашего прадеда в соломенном плаще и с мотыгой.", "Some old people turned up who remembered your great-grandfather in a straw raincoat, with a hoe.") }),
                Opt(t("Сунуть ему в рукав монет", "Slip some coins into his sleeve"), cost = 6, tag = t("6 мон", "6 mon"),
                    fx = { money(-6); done("fake_line"); t("Чиновник уехал с тяжёлым рукавом. Свиток висит где висел.", "The official rode off with a heavy sleeve. The scroll hangs where it hung.") }),
            )
        }),

    Event("scribe", "village", Tx("Грамотей", "The Lettered One"), cond = { h -> fire(has(h, "gram") && age(h) >= 18 && !flagged("terakoya")) },
        txt = { t("Прослышав, что вы знаете грамоту, соседи потянулись с просьбами: одной написать сыну в ${edo()}, другому прочесть указ на доске, третьему составить прошение о недоимке. Несут кто яйцо, кто редьку.",
            "Word has got round that you can read and write, and the neighbors keep coming: one to have a letter written to her son in ${edo()}, one to have a notice read, another to have a petition drawn up about his arrears. They bring an egg, or a radish.") },
        opts = {
            listOf(
                Opt(t("Открыть школу для детей", "Open a school for the children"),
                    fx = { flag("terakoya"); glory(2); t("Восемь ребятишек на циновках, у каждого кисть и тушечница. Плата — дрова, рис и почтение.", "Eight children on the mats, each with a brush and an ink stone. The fee is firewood, rice and respect.") }),
                Opt(t("Написать прошение о недоимке", "Write the petition about the arrears"), roll = Roll(UM, 0, mapOf("umn" to 1)),
                    ok = { money(3); glory(1); t("Управа скостила недоимку. Сосед принёс вам целый мешок риса.", "The office cut the arrears. The neighbor brought you a whole sack of rice.") },
                    bad = { money(-2); t("В управе разглядели в прошении дерзость. Досталось и просителю, и писцу.", "The office found the petition impertinent. The one who asked and the one who wrote both paid for it.") }),
                Opt(t("Писать письма за яйца", "Write letters for eggs"), fx = { money(1); t("Яиц хватило на всю зиму, а письма ушли в ${edo()}.", "There were eggs enough for the whole winter, and the letters went off to ${edo()}.") }),
            )
        }),
    Event("c_pupil", "village", Tx("Ученик", "The Pupil"), w = 3, cond = { fire(ago("terakoya", 12)) },
        txt = { t("В ворота входит молодой человек в хакама, с бумагами за пазухой, и кланяется вам до земли. Это ваш ученик, тот, что сажал кляксу на каждую строку. Теперь он ${if (g.year < 1872) "писарь в управе князя" else "учитель в новой школе"}.",
            "A young man in formal trousers comes in at the gate with papers in his robe and bows to the ground. It is your old pupil, the one who blotted every line. Now he is ${if (g.year < 1872) "a clerk in the lord's office" else "a teacher at the new school"}.") },
        opts = {
            listOf(
                Opt(t("Попросить помочь с податью", "Ask for help with the tax"),
                    fx = { done("terakoya"); money(5); t("Он что-то пересчитал в книгах, и подать вышла легче на пять мон. «Вы меня учили считать — вот я и считаю».", "He recounted something in the books, and the tax came out five mon lighter. “You taught me to count, so I count.”") }),
                Opt(t("Попросить учить младших", "Ask him to teach the young ones"),
                    fx = { done("terakoya"); val k = kidIn(6, 16).firstOrNull()
                        if (k != null) { addTr(k, "gram"); k[UM]++; t("Он забрал ${k.name} к себе на зиму. Весной ${k.name} читал${sx(k, "", "а")} указы на доске быстрее старосты.", "He took ${k.name} for the winter. By spring ${k.name} read the notices on the board faster than the headman.") }
                        else { glory(1); t("Учить пока некого. Он обещал вернуться, когда появятся внуки.", "There is no one to teach yet. He promised to come back when there are grandchildren.") } }),
                Opt(t("Просто напоить чаем", "Just give him tea"), fx = { done("terakoya"); glory(1); ease(it.h); t("Пили чай до темноты и вспоминали кляксы. Хороший был вечер.", "You drank tea till dark and remembered the blots. It was a good evening.") }),
            )
        }),

    Event("straw", "interior", Tx("Долгие вечера", "Long Evenings"), cond = { h -> fire(has(h, "kalek") && age(h) >= 16) },
        txt = { t("Нога не держит: на чеках вы больше не работник. Вечерами вы сидите у очага, смотрите, как другие уходят в поле и возвращаются, а руки сами ищут дела.",
            "Your leg will not hold you: you are no use in the paddies now. In the evenings you sit by the hearth and watch the others go out to the fields and come back, and your hands look for something to do.") },
        opts = { o ->
            listOf(
                Opt(t("Плести сандалии для тракта", "Weave sandals for the highway"), roll = Roll(UM, 1),
                    ok = { money(4); t("Ваши сандалии носильщики берут по три пары: не трут и держат до самого Киото.", "The porters buy your sandals three pairs at a time: they don't chafe and they last all the way to Kyoto.") },
                    bad = { money(1); t("Сандалии вышли кривые. Взяли их только из жалости.", "The sandals came out crooked. People bought them out of pity.") }),
                Opt(t("Рассказывать истории на постоялом дворе", "Tell stories at the inn"), roll = Roll(SER, 0, mapOf("gram" to 1, "lzh" to 1)),
                    ok = { money(2); glory(2); t("Про лис, про ронинов, про то, как вы покалечили ногу (каждый раз по-новому). Слушать вас ходят из трёх деревень.", "About foxes, about ronin, about how you hurt your leg (a new way every time). People walk in from three villages to listen.") },
                    bad = { t("Слушатели заснули к середине истории. Хозяин двора попросил больше не приходить.", "Your listeners fell asleep halfway through. The innkeeper asked you not to come again.") }),
                Opt(t("Молиться Каннон о ноге", "Pray to Kannon for your leg"), roll = Roll(VER, -2, mapOf("bogom" to 1, "krot" to 1)),
                    ok = { o.h.tr.remove("kalek"); t("К лету вы ходили без клюки. Монахи говорят — чудо, травница — что срослось.", "By summer you were walking without a stick. The monks say it is a miracle; the herb-woman says it knitted.") },
                    bad = { t("Каннон милосердна, но, видно, занята.", "Kannon is merciful, but busy, it seems.") }),
            )
        }),

    Event("onsen", "road", Tx("Воды Кусацу", "The Waters of Kusatsu"), cond = { h -> fire(has(h, "hvor") && age(h) >= 16) },
        txt = { t("Лекарь говорит: от вашей хвори помогают только горячие воды Кусацу. Три недели сидеть в воде, от которой слезает кожа. Дорога неблизкая, а постоялые дворы у источников недёшевы.",
            "The doctor says only the hot waters of Kusatsu will help your sickness: three weeks in water that takes the skin off you. It is a long road, and the inns at the springs are not cheap.") },
        opts = { o ->
            listOf(
                Opt(t("Ехать на воды", "Go and take the waters"), cost = 5, tag = t("5 мон", "5 mon"), roll = Roll(ZDR, 2),
                    ok = { money(-5); o.h[ZDR] += 2; t("Кожа слезла, как у змеи, зато вы вернулись румяными и спите до утра без кашля.", "Your skin peeled like a snake's, but you came home rosy and sleep till morning without coughing.") },
                    bad = { money(-5); t("Вы вернулись такими же, только беднее.", "You came back the same, only poorer.") }),
                Opt(t("Прижигать полынью дома", "Burn moxa at home"), cost = 1, tag = t("1 мон", "1 mon"), roll = Roll(ZDR, 0),
                    ok = { money(-1); o.h[ZDR]++; t("Сорок прижиганий по спине, и к весне дышится легче.", "Forty burns of moxa down your back, and by spring you breathe easier.") },
                    bad = { money(-1); t("Остались только пятна от прижиганий.", "All that is left is the marks of the moxa.") }),
                Opt(t("Хворь — не повод бросать рис", "Sickness is no reason to leave the rice"), fx = { t("Вы кашляете, но сажаете. Рис не спрашивает, здоровы ли вы.", "You cough, but you plant. The rice does not ask how you are.") }),
            )
        }),

    Event("rewed", "wedding", Tx("Два вдовых очага", "Two Widowed Hearths"), cond = { h -> fire(has(h, "vdov") && !married(h) && age(h) in 46..68) },
        txt = { o -> if (o.h.male) t("Соседская вдова то принесёт рисовых колобков, то заштопает вам куртку. В деревне посмеиваются: двум вдовым у одного очага теплее.",
                "The widow next door brings you rice balls now and then, and mends your jacket. The village smiles: two widowed people are warmer at one hearth.")
            else t("Сосед-вдовец то дров наколет, то починит вам ставень. В деревне посмеиваются: двум вдовым у одного очага теплее.",
                "The widower next door splits your firewood now and then, and mends your shutter. The village smiles: two widowed people are warmer at one hearth.") },
        opts = { o ->
            listOf(
                Opt(t("Сойтись на старости лет", "Marry again in old age"),
                    fx = { val s = lateSpouse(o.h); t("Свадьбу сыграли тихо, без накодо: три чашечки, и ${s.name} перенес${sx(s, "", "ла")} к вам свой ящик с посудой.", "The wedding was quiet, with no go-between: three cups, and ${s.name} moved ${sx(s, "his", "her")} box of dishes into your house.") }),
                Opt(t("Хранить верность покойн${sx(o.h, "ой", "ому")}", "Stay true to the dead"),
                    fx = { o.h[VER]++; t("Каждое утро вы ставите на домашний алтарь чашечку риса для ${sx(o.h, "покойной жены", "покойного мужа")}. Соседи вздохнули и перестали носить колобки.", "Every morning you set a cup of rice on the house altar for your ${sx(o.h, "late wife", "late husband")}. The neighbors sighed and stopped bringing rice balls.") }),
            )
        }),

    Event("print", "fair", Tx("Лицо для гравюры", "A Face for a Print"), cond = { h -> fire(has(h, "kras") && age(h) in 16..45) },
        txt = { t("Художник из ${edo()} ходит по тракту и рисует для гравюр «Виды и лица нашей провинции». Увидев вас на ярмарке, он хватается за кисть: посидеть перед ним час — и ваше лицо уйдёт в доски и в тысячу оттисков.",
            "An artist from ${edo()} is walking the highway, drawing for a set of prints, “Views and Faces of Our Province.” He sees you at the fair and reaches for his brush: sit for him an hour, and your face will go into the woodblocks and a thousand prints.") },
        opts = {
            listOf(
                Opt(t("Посидеть перед ним", "Sit for him"), fx = { glory(2); flag("print_face"); t("Через полгода разносчик привёз гравюру: вы у моста, под зонтом, и в лице больше тайны, чем было на самом деле. Её покупают в трёх уездах.", "Half a year later a pedlar brought the print: you on the bridge, under an umbrella, with more mystery in your face than there ever was. It sells in three districts.") }),
                Opt(t("Потребовать плату", "Ask to be paid"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)),
                    ok = { money(4); glory(1); t("Художник поморщился, но заплатил. Красота — тоже товар.", "The artist winced, but paid. Beauty is goods too.") },
                    bad = { glory(-1); t("Художник нарисовал дочь мельника. Мельник теперь ходит гордый.", "The artist drew the miller's daughter instead. The miller walks proud now.") }),
                Opt(t("Отказаться: лицо не товар", "Refuse: a face is not for sale"), fx = { t("Художник вздохнул и нарисовал мост без вас. Вышло, говорят, скучно.", "The artist sighed and drew the bridge without you. Dull, they say.") }),
            )
        }),

    Event("senba", "field", Tx("Железная гребёнка", "The Iron Comb"), cond = { h -> fire(has(h, "umn") && age(h) >= 16 && g.year in 1690..1880 && !flagged("senba")) },
        txt = { t("Пока женщины обмолачивают рис палками, вы додумались: вбить в брус железные зубья частым гребнем и протаскивать через них колосья. Выходит вдвое быстрее. Кузнец чешет в затылке, а вдовы, что нанимаются на молотьбу, уже косятся.",
            "While the women thresh the rice with sticks, you have hit on something: set iron teeth in a beam like a close comb and draw the ears through it. It goes twice as fast. The smith scratches his head, and the widows who hire out for threshing are already giving you looks.") },
        opts = { o ->
            listOf(
                Opt(t("Показать всей деревне", "Show the whole village"), fx = { flag("senba"); glory(2); money(2); t("К осени гребёнка стояла в каждом дворе. Вдовы прозвали её «вдовьей погибелью» — и не шутили.", "By autumn there was a comb in every yard. The widows named it the widow-killer, and they were not joking.") }),
                Opt(t("Продать придумку кузнецу", "Sell the idea to the smith"), sin = "greed", fx = { flag("senba"); money(6); t("Кузнец куёт гребёнки на три уезда. Вам — шесть мон и его вечная улыбка.", "The smith forges combs for three districts. You got six mon and his undying smile.") }),
                Opt(t("Разобрать — пусть вдовы молотят", "Take it apart: let the widows thresh"),
                    fx = { flag("senba"); ease(o.h); glory(1); t("Гребёнку вы разобрали на гвозди. Вдовы ещё год были при работе, а потом гребёнку придумал кто-то другой.", "You took the comb apart for nails. The widows had work for another year, and then somebody else invented it.") }),
            )
        }),

    Event("sumo", "fair", Tx("Сумо у святилища", "Sumo at the Shrine"), cond = { h -> fire(if (has(h, "bog") && age(h) in 16..40) h else kidIn(16, 30) { has(it, "bog") }.firstOrNull()) },
        txt = { o -> val x = o.xx
            t("На празднике у святилища борются на земляном круге. ${x.name} ${sx(x, "вышел", "вышла")} и ${sx(x, "уложил", "уложила")} троих подряд. Распорядитель бродячей труппы борцов из Осаки уже тут как тут.",
                "At the shrine festival there is wrestling in the earthen ring. ${x.name} stepped in and threw three in a row. The manager of a traveling troupe of wrestlers from Osaka is there at once.") },
        opts = { o ->
            val x = o.xx
            val l = mutableListOf(
                Opt(t("Бороться с силачом труппы", "Wrestle the troupe's champion"), roll = Roll(SIL, 0, ofX = true),
                    ok = { glory(2); money(3); deed(x, t(sx(x, "Уложил", "Уложила") + " осакского силача", "Threw the Osaka champion")); t("Силач из Осаки лёг на спину, как черепаха. Распорядитель заплатил за позор своего борца.", "The Osaka champion went down on his back like a turtle. The manager paid for his wrestler's shame.") },
                    bad = { addTr(x, "shram"); t("Силач из Осаки весил как два вола. ${x.name} неделю ${sx(x, "ходил", "ходила")} боком.", "The Osaka champion weighed as much as two oxen. ${x.name} walked sideways for a week.") }),
                Opt(t("Хватит и праздника", "The festival is enough"), fx = { t("Распорядитель вздохнул и уехал искать силачей подальше.", "The manager sighed and went off to look for strong men further away.") }),
            )
            if (x !== o.h) l.add(1, Opt(t("Отпустить с труппой на сезон", "Let ${sx(x, "him", "her")} tour with the troupe for a season"),
                fx = { money(4); glory(1); x[SIL]++; t("Полгода ${x.name} ездил${sx(x, "", "а")} по ярмаркам и вернул${sx(x, "ся", "ась")} с деньгами и сломанным носом.", "For half a year ${x.name} traveled the fairs, and came home with money and a broken nose.") }))
            l
        }),

    Event("kanreki", "interior", Tx("Красная безрукавка", "The Red Vest"), cond = { h -> fire(age(h) in 60..62 && g.flags["kanreki"] != h.id) },
        txt = { t("Вам шестьдесят один: круг из шестидесяти лет замкнулся, и, как говорят, вы родились заново. Дети принесли красную безрукавку, как на младенца, и ждут, что вы скажете.",
            "You are sixty-one: the circle of sixty years has closed, and you are born again, as they say. The children have brought a red vest, the kind a baby wears, and wait to hear what you will say.") },
        opts = { o ->
            listOf(
                Opt(t("Устроить пир на всю деревню", "Throw a feast for the village"), cost = 4, tag = t("4 мон", "4 mon"),
                    fx = { flag("kanreki", o.h.id); money(-4); glory(2); t("Пили три дня. Вы сидели в красной безрукавке, и даже Курокава подходил чокнуться.", "The drinking went on for three days. You sat in your red vest, and even a Kurokawa came over to raise a cup.") }),
                Opt(t("Отойти от дел, перебраться в задние комнаты", "Step back and move to the back rooms"),
                    fx = { flag("kanreki", o.h.id); o.h.stones = 0; kidIn(16, 60).firstOrNull()?.let { it[UM]++ }
                        t("Счёты и печать теперь у молодых. Вы сидите на веранде, растите карликовую сосну и даёте советы, о которых никто не просит.", "The abacus and the seal belong to the young now. You sit on the veranda, grow a dwarf pine and give advice nobody asks for.") }),
                Opt(t("Расписать доли наследникам", "Write down the heirs' shares"),
                    fx = { flag("kanreki", o.h.id); g.grudge = null; glory(1); t("Доли расписаны, печати поставлены. Спорить будет не о чем — так вы надеетесь.", "The shares are written down and sealed. There will be nothing to quarrel about, or so you hope.") }),
            )
        }),

    Event("genpuku", "church", Tx("Гэмпуку", "Coming of Age"), w = 2, cond = { fire(kidIn(14, 16) { it.male && g.flags["genpuku"] != it.id }.firstOrNull()) },
        txt = { o -> val x = o.xx
            t("${x.name} уже ${yrs(age(x))}: пора совершить гэмпуку. Чёлку сбреют, дадут взрослую одежду и взрослое имя. Нужен названый отец — тот, кто повяжет шнур головного убора и будет опорой всю жизнь.",
                "${x.name} is ${age(x)} now: time for the coming-of-age. The forelock will be shaved, and there will be grown-up clothes and a grown-up name. He needs a sponsor, a man to tie the cord of his cap and stand by him all his life.") },
        opts = { o ->
            val x = o.xx
            val l = mutableListOf(
                Opt(t("Просить старосту", "Ask the headman"), cost = 3, tag = t("3 мон на подарки", "3 mon in gifts"),
                    fx = { flag("genpuku", x.id); money(-3); x[SER]++; glory(1); t("Староста сам повязал шнур и подарил веер. В деревне ${x.name} теперь «крестник старосты».", "The headman tied the cord himself and gave a fan. In the village ${x.name} is “the headman's godson” now.") }),
                Opt(t("Просить дядю", "Ask an uncle"), fx = { flag("genpuku", x.id); x[SIL]++; t("Скромно, по-домашнему: дядя повязал шнур, выпил три чашечки и прослезился.", "Plain and homely: the uncle tied the cord, drank three cups and wept a little.") }),
            )
            if (g.feud > 0) l.add(Opt(t("Просить старика Курокаву", "Ask old Kurokawa"), roll = Roll(SER, -1, mapOf("krot" to 1, "kras" to 1)),
                ok = { flag("genpuku", x.id); feud(-2); glory(1); t("Старик Курокава растрогался так, что забыл, из-за чего вы враждуете. Почти забыл.", "Old Kurokawa was so touched that he forgot what the feud was about. Nearly forgot.") },
                bad = { flag("genpuku", x.id); feud(1); t("Курокава ответил, что своих сыновей ему хватает, и захлопнул ставень.", "Kurokawa answered that he had sons enough of his own, and slammed the shutter.") }))
            l
        }),

    Event("prank", "inari", Tx("Проделка", "Mischief"), w = 2, cond = { fire(kidIn(5, 10).firstOrNull()) },
        txt = { o -> val x = o.xx
            pick(listOf(
                t("${x.name} с соседскими ребятами ${sx(x, "надел", "надела")} на каменную лису у святилища Инари соломенную шляпу и ${sx(x, "сунул", "сунула")} ей в пасть дохлую лягушку. Смотритель святилища стоит у ваших ворот и трясёт бородой.",
                    "${x.name} and the neighbors' children put a straw hat on the stone fox at the Inari shrine and stuck a dead frog in its mouth. The shrine keeper is at your gate, shaking his beard."),
                t("${x.name} ${sx(x, "нарисовал", "нарисовала")} углём усы каменной лисе у святилища Инари. Смотритель святилища стоит у ваших ворот и трясёт бородой.",
                    "${x.name} drew a moustache in charcoal on the stone fox at the Inari shrine. The shrine keeper is at your gate, shaking his beard."),
            )) },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Отвести на поклон и на работу", "March the child there to bow and work"),
                    fx = { x[VER]++; t("Месяц ${x.name} ${sx(x, "подметал", "подметала")} двор святилища на рассвете. Теперь здоровается с каменной лисой первым.", "For a month ${x.name} swept the shrine yard at dawn, and now always greets the stone fox first.") }),
                Opt(t("Отнести лисе жареный тофу", "Take the fox some fried tofu"), cost = 1, tag = t("1 мон", "1 mon"),
                    fx = { money(-1); t("Смотритель смягчился, лиса промолчала. Хороший знак.", "The keeper softened, and the fox said nothing. A good sign.") }),
                Opt(t("Посмеяться втихомолку", "Laugh about it quietly"),
                    fx = { if (chance(.3)) { hex(3); t("Ночью под окном кто-то тявкнул. В мешке с бобами на три года прибавился второй красный.", "In the night something yapped under the window. For three years the bag of beans holds a second red one.") }
                        else t("Смотритель ушёл ворча. Обошлось — кажется.", "The keeper went off grumbling. No harm done, it seems.") }),
            )
        }),

    Event("sutego", "road", Tx("Подкидыш на перекрёстке", "Left at the Crossroads"), cond = { h -> fire(if (h.male && age(h) in 41..43) kidsOf(h).firstOrNull { it.born >= g.year - 1 && alive(it) } else null) },
        txt = { o -> val x = o.xx
            t("${x.name} ${sx(x, "родился", "родилась")} в ваш сорок второй год, а это самый несчастливый год мужчины: «сорок два» звучит как «к смерти». По обычаю младенца надо «подкинуть» на перекрёсток, чтобы его тут же «нашёл» добрый человек и беда отцовского года не перешла на дитя.",
                "${x.name} was born in your forty-second year, the unluckiest year of a man's life: forty-two sounds like “to death.” Custom says the baby must be “abandoned” at a crossroads and “found” at once by a kind neighbor, so that the father's bad year does not pass to the child.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Подкинуть, как велит обычай", "Leave the child as custom says"), cost = 1, tag = t("1 мон на подарок", "1 mon for a gift"),
                    fx = { money(-1); g.bless = 1; t("Сосед подобрал свёрток ровно через три вздоха, поклонился и торжественно вернул вам. Теперь он у ${x.name} названый отец.$BLESS_RU", "The neighbor picked up the bundle after exactly three breaths, bowed and solemnly gave it back. Now he is ${x.name}'s foster father.$BLESS_EN") }),
                Opt(t("Не верить приметам", "Pay no mind to omens"),
                    fx = { if (chance(.3)) { x[ZDR]--; t("Ребёнок растёт хворым, и старухи многозначительно кивают.", "The child grows up sickly, and the old women nod knowingly.") }
                        else t("Ничего не случилось. Старухи всё равно кивают.", "Nothing happened. The old women nod anyway.") }),
            )
        }),

    Event("split", "meja", Tx("Делёж", "Dividing the Land"), cond = { h -> val s = kidIn(18, 50) { it.male && !it.bast }; fire(if (age(h) >= 48 && s.size >= 2) s[1] else null) },
        txt = { o -> val x = o.xx; val a = kidIn(18, 50) { it.male && it !== x }.firstOrNull()?.name ?: t("Старший", "The eldest")
            if (g.year < 1872) t("$a и ${x.name} — оба взрослые, оба меряют глазами ваши чеки. Закон запрещает дробить мелкий надел, да и делить тут нечего: поделишь — оба пойдут по миру.",
                "$a and ${x.name} are both grown, and both are measuring your paddies with their eyes. The law forbids splitting a small holding, and there is nothing to split anyway: divide it, and both will end up begging.")
            else t("$a и ${x.name} — оба взрослые, оба меряют глазами ваши чеки. Делить тут нечего: поделишь — оба пойдут по миру.",
                "$a and ${x.name} are both grown, and both are measuring your paddies with their eyes. There is nothing to split: divide it, and both will end up begging.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Всё — старшему", "Everything to the eldest"), fx = { g.grudge = x.id; t("${x.name} молча собрал узелок. Остался при доме, но обиду держит за пазухой.", "${x.name} said nothing and packed a bundle. He stayed on, but keeps the grudge tucked in his robe.") }),
                Opt(t("Поделить, задобрив писаря", "Divide it and grease the clerk"), cost = 3, tag = t("3 мон, дом беднеет", "3 mon, the house grows poorer"), sin = "lie",
                    fx = { money(-3); if (g.dvor > 0) g.dvor--; glory(1); t("Межу провели посреди чека. Оба сыты — вполсыта.", "The boundary runs through the middle of the paddy. Both are fed, half fed.") }),
                Opt(t("Дать младшему денег на дело в городе", "Give the younger money to start in town"), cost = 6, tag = t("6 мон", "6 mon"),
                    fx = { money(-6); addTr(x, "gor"); x[HIT]++; deed(x, t("Начал своё дело в городе", "Started a trade in town")); t("${x.name} открыл в городе лоток с лапшой. Приезжает на праздники в новых сандалиях.", "${x.name} opened a noodle stall in town. He comes home for the holidays in new sandals.") }),
            )
        }),

    Event("tokeiji", "church", Tx("Храм разводов", "The Divorce Temple"), cond = { val d = kidIn(18, 40) { !it.male && married(it) }.firstOrNull(); fire(if (d != null && chance(.35)) d else null) },
        txt = { o -> val x = o.xx
            t("Муж ${x.name} пьёт и поднимает руку, а развода не даёт: разводное письмо в три с половиной строки пишет только муж. ${x.name} шепчет про храм Токэйдзи в Камакуре: женщина, что добежит до его ворот, отслужит там два года — и свободна.",
                "${x.name}'s husband drinks and raises his hand, and will not grant a divorce: only a husband can write the letter of three and a half lines. ${x.name} whispers about Tokeiji, the temple at Kamakura: a woman who reaches its gate serves there two years and is free.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Довезти её до Камакуры", "Get her to Kamakura"), roll = Roll(HIT, 0, mapOf("dobr" to 1, "hrab" to 1)),
                    ok = { val s = spouseOf(x); if (s != null && s.spouse == x.id) s.spouse = null; x.spouse = null; deed(x, t("Получила развод в храме Токэйдзи", "Won a divorce at Tokeiji"))
                        t("Ночью вы довезли её до ворот, и она бросила через порог сандалию: этого довольно. Через два года ${x.name} вернулась домой — свободная и спокойная.", "You got her to the gate by night, and she threw a sandal over the threshold: that is enough. Two years later ${x.name} came home, free and calm.") },
                    bad = { x.stones++; glory(-1); t("Муж догнал вас на тракте, у самой заставы. ${x.name} вернулась к нему. Все знают, почему.", "The husband caught you on the highway, right at the barrier. ${x.name} went back to him. Everyone knows why.") }),
                Opt(t("Поговорить с зятем по-свойски", "Have a word with the son-in-law"), roll = Roll(SIL, 0, mapOf("bog" to 2, "gnev" to 1)),
                    ok = { t("Зять присмирел. Пить не бросил, но руку больше не поднимает.", "The son-in-law has calmed down. He still drinks, but he raises his hand no more.") },
                    bad = { money(-2); t("Зять пожаловался старосте, и разговор обошёлся вам в два мона.", "The son-in-law complained to the headman, and your word with him cost two mon.") }),
                Opt(t("Велеть терпеть", "Tell her to endure"), sin = "cruel", fx = { x.stones++; t("${x.name} вытерла слёзы и вернулась к мужу. Все терпят.", "${x.name} wiped her eyes and went back to her husband. Everybody endures.") }),
            )
        }),

    // ---------------------------------------------------------------- chains off the old events
    Event("c_ronin", "night_int", Tx("Старый должник", "An Old Debt"), w = 3, cond = { fire(ago("ronin_hid", 8)) },
        txt = { if ((g.flags["ronin_hid"] ?: 0) % 2 == 0) {
                if (g.year < 1868) t("В ворота входит человек в добротном платье, с двумя мечами за поясом. Вы не сразу узнали беглеца, что зимовал у вас на сеновале. Теперь он держит школу фехтования в призамковом городе и привёз подарки.",
                    "A man in good clothes, with two swords in his sash, comes in at the gate. It takes you a moment to know the fugitive who wintered in your loft. Now he keeps a fencing school in the castle town, and he has brought gifts.")
                else t("В ворота входит человек в европейском пальто. Вы не сразу узнали беглеца, что зимовал у вас на сеновале. Теперь он подрядчик на постройке железной дороги и привёз подарки.",
                    "A man in a Western overcoat comes in at the gate. It takes you a moment to know the fugitive who wintered in your loft. Now he builds railways under contract, and he has brought gifts.")
            } else t("У перевала завелась шайка. Вожак прислал к вам мальчишку с запиской: «Помню сеновал. Помню и то, что про сеновал знаете вы. Кормите моих людей — и спите спокойно».",
                "A gang has settled by the pass. Its leader has sent a boy to you with a note: “I remember the loft. I remember too that you know about the loft. Feed my men and sleep soundly.”") },
        opts = { o ->
            val h = o.h
            if ((g.flags["ronin_hid"] ?: 0) % 2 == 0) {
                val l = mutableListOf(
                    Opt(t("Принять подарки", "Accept the gifts"), fx = { done("ronin_hid"); money(8); t("Отрез шёлка, связка монет и ящик сладостей. «Долг платежом красен», — сказал он и ушёл до темноты.", "A bolt of silk, a string of coins and a box of sweets. “A debt is best paid,” he said, and was gone before dark.") }),
                    Opt(t("Отказаться от подарков", "Refuse the gifts"), fx = { done("ronin_hid"); glory(2); t("Он поклонился вам, как кланяются старшим. Об этом поклоне говорили по всей деревне.", "He bowed to you as one bows to an elder. The whole village talked about that bow.") }),
                )
                sonOf(h).firstOrNull()?.let { s -> l.add(1, Opt(t("Попросить взять ${s.name} в ученики", "Ask him to take ${s.name} as a pupil"),
                    fx = { done("ronin_hid"); s[SIL]++; addTr(s, "gor"); deed(s, t("Учился у бывшего беглеца", "Was taught by the one-time fugitive")); t("${s.name} уехал с ним в город. Через год вернулся с прямой спиной и твёрдым взглядом.", "${s.name} went off with him to town. A year later he came back with a straight back and a steady eye.") })) }
                l
            } else listOf(
                Opt(t("Донести стражникам", "Tell the guards"), roll = Roll(HIT, 0, mapOf("hrab" to 1, "umn" to 1)),
                    ok = { done("ronin_hid"); glory(2); t("Шайку переловили у перевала. Вожак, говорят, на допросе про вас промолчал.", "The gang was rounded up at the pass. The leader, they say, said nothing about you when questioned.") },
                    bad = { done("ronin_hid"); money(-5); addTr(h, "shram"); t("Шайка пришла раньше стражи.", "The gang got to you before the guards did.") }),
                Opt(t("Кормить", "Feed them"), sin = "coward",
                    fx = { done("ronin_hid"); money(-4); addSecret(t("${h.name} ${sx(h, "кормил", "кормила")} горную шайку", "${h.name} fed the gang in the hills"), h); t("Мешок риса в месяц, оставленный у камня. До весны шайка ушла дальше.", "A sack of rice a month, left by a certain stone. By spring the gang had moved on.") }),
                Opt(t("Поговорить с ним с глазу на глаз", "Talk to him alone"), roll = Roll(SER, -1, mapOf("dobr" to 1, "hrab" to 1)),
                    ok = { done("ronin_hid"); glory(1); t("Вы просидели у костра до рассвета. Утром шайка ушла за перевал, а вожак оставил на камне ваш старый кувшин для воды.", "You sat by his fire till dawn. In the morning the gang went over the pass, and the leader left your old water jug on the stone.") },
                    bad = { done("ronin_hid"); money(-4); t("Он слушал, кивал — и забрал у вас вола. «Это за сеновал. Теперь мы квиты».", "He listened and nodded, and took your ox. “That's for the loft. Now we're square.”") }),
            )
        }),

    Event("c_paddy", "field", Tx("Мерная верёвка", "The Measuring Rope"), w = 3, cond = { fire(ago("hidden_paddy", 6)) },
        txt = { if (g.year < 1873) t("Новый смотритель не ленится: обходит поля с мерной верёвкой и, похоже, собирается перейти ручей.",
                "The new inspector is a diligent man: he walks the fields with a measuring rope and looks set to cross the stream.")
            else t("Землемеры нового правительства обмеряют каждый клочок: подать теперь с земли, а не с урожая. Завтра они дойдут до вашего ручья.",
                "The new government's surveyors are measuring every scrap of land: the tax is on the land now, not the crop. Tomorrow they will reach your stream.") },
        opts = {
            listOf(
                Opt(t("Признаться и заплатить недоимку", "Own up and pay the arrears"), cost = 6, tag = t("6 мон", "6 mon"),
                    fx = { done("hidden_paddy"); money(-6); glory(1); t("Чек вписали в книги. Подать тяжелее, зато спится лучше.", "The paddy went into the books. The tax is heavier, but you sleep better.") }),
                Opt(t("Выдать его за свежую новь", "Pass it off as newly cleared land"), roll = Roll(UM, 0, mapOf("gram" to 2, "umn" to 1)),
                    ok = { done("hidden_paddy"); money(2); t("Записали как новь: несколько лет без подати, как положено по закону.", "It went down as new land: some years free of tax, as the law allows.") },
                    bad = { done("hidden_paddy"); money(-6); glory(-1); t("На «свежей нови» стоял столетний межевой камень.", "On the “newly cleared land” stood a boundary stone a hundred years old.") }),
                Opt(t("Подкупить мерщика", "Bribe the man with the rope"), cost = 4, tag = t("4 мон", "4 mon"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 1)),
                    ok = { done("hidden_paddy"); money(-4); t("Верёвка почему-то кончилась ровно у ручья.", "Somehow the rope ran out right at the stream.") },
                    bad = { done("hidden_paddy"); money(-10); glory(-2); t("Мерщик взял деньги и всё равно перешёл ручей. Штраф — за чек и за взятку.", "The surveyor took the money and crossed the stream anyway. A fine for the paddy, and another for the bribe.") }),
            )
        }),

    Event("c_taxrice", "village", Tx("Старик у моста", "The Old Man by the Bridge"), w = 3, cond = { fire(ago("tax_rice", 3)) },
        txt = { t("Сторожа налоговой кладовой, старика Дэмбэя, выгнали со службы за недостачу трёх мешков — тех самых. Теперь он сидит с чашкой у моста, а вы ходите через этот мост каждый день.",
            "The keeper of the tax storehouse, old Denbei, was dismissed for a shortage of three sacks. Those three sacks. Now he sits with a bowl by the bridge, and you cross that bridge every day.") },
        opts = { o ->
            listOf(
                Opt(t("Повиниться в управе", "Confess at the office"), roll = Roll(VER, 0, mapOf("chest" to 1, "krot" to 1)),
                    ok = { done("tax_rice"); money(-4); glory(1); ease(o.h); t("Управа взыскала с вас за три мешка, а Дэмбэя вернули на службу. Он не здоровается, но и не проклинает.", "The office made you pay for the three sacks, and Denbei got his post back. He doesn't greet you, but he doesn't curse you either.") },
                    bad = { done("tax_rice"); money(-4); glory(-2); addTr(o.h, "shram"); t("Вас били палками перед управой. Дэмбэя всё равно не вернули: стар.", "You were beaten with staves outside the office. Denbei did not get his post back anyway: too old.") }),
                Opt(t("Тайком носить ему рис", "Bring him rice in secret"), cost = 3, tag = t("3 мон", "3 mon"),
                    fx = { done("tax_rice"); money(-3); t("Каждую неделю в его чашке у моста оказывается горсть риса. Он, кажется, догадывается.", "Every week a handful of rice turns up in his bowl by the bridge. He seems to guess.") }),
                Opt(t("Ходить другой дорогой", "Take another road"), sin = "lie", fx = { done("tax_rice"); t("Крюк небольшой. Привыкаете.", "It is not much of a detour. You get used to it.") }),
            )
        }),

    Event("c_orin", "road", Tx("Сын О-Рин", "O-Rin's Son"), w = 3, cond = { fire(ago("orin_boy", 14)) },
        txt = { o -> val f = person(g.flags["orin_f"]) ?: o.h
            val eyes = if (f === o.h) t("с вашими глазами", "with your eyes") else t("с глазами ${f.name}", "with ${f.name}'s eyes")
            t("В ворота входит рослый парень лет пятнадцати, $eyes. Это сын вдовы О-Рин. «Мать умерла. Перед смертью сказала, чья во мне кровь».",
                "A tall lad of about fifteen comes in at the gate, $eyes. It is the widow O-Rin's son. “Mother has died. Before she died she told me whose blood I have.”") },
        opts = { o ->
            val f = person(g.flags["orin_f"]) ?: o.h
            listOf(
                Opt(t("Принять в дом", "Take him into the house"), fx = { done("orin_boy"); val c = lateBastard(f); t("${c.name} спит у очага с остальными. Работает молча и много, как будто отрабатывает пятнадцать лет.", "${c.name} sleeps by the hearth with the others. He works hard and in silence, as if paying off fifteen years.") }),
                Opt(t("Пристроить в подмастерья", "Find him an apprenticeship"), cost = 4, tag = t("4 мон", "4 mon"),
                    fx = { done("orin_boy"); money(-4); glory(1); t("Его взял к себе бондарь в городе. Парень поклонился коротко, без улыбки.", "A cooper in town took him on. The lad bowed briefly, without a smile.") }),
                Opt(t("Прогнать", "Send him away"), sin = "cruel",
                    fx = { done("orin_boy"); feud(1); t("Он ушёл к Курокаве. Говорят, нанялся к ним в работники и хорошо помнит вашу калитку.", "He went to the Kurokawas. They say he hired on with them and remembers your gate well.") }),
            )
        }),

    Event("c_oda", "road", Tx("Гость из дома Ода", "A Guest from the Oda House"), w = 3,
        cond = { val p = person(g.flags["oda_id"]); fire(if (ago("oda_son", 8) && p != null && alive(p)) p else null) },
        txt = { o -> val x = o.xx
            t("${x.name}, давно уже Ода по имени, приехал верхом с двумя работниками. «Слышал, год у вас выдался худой. Имя я сменил, а чей я сын — помню».",
                "${x.name}, long since an Oda by name, has ridden over with two hired men. “I hear you've had a lean year. I changed my name, but I remember whose son I am.”") },
        opts = {
            listOf(
                Opt(t("Принять помощь", "Accept the help"), fx = { done("oda_son"); money(8); t("Два мешка риса и кошель. Работники до вечера чинили вам ограду.", "Two sacks of rice and a purse. The hired men mended your fence till evening.") }),
                Opt(t("Попросить помирить с Курокавой", "Ask him to make peace with the Kurokawas"),
                    fx = { done("oda_son"); if (g.feud > 0) { feud(-2); glory(1); t("Дом Ода уважают по всей долине. Курокава выслушали его и впервые за много лет кивнули вам у колодца.", "The Oda house is respected the length of the valley. The Kurokawas listened to him, and for the first time in years nodded to you at the well.") }
                        else { glory(1); t("Мириться не с кем: Курокава и так тихи. Посидели, вспомнили детство.", "There is no one to make peace with: the Kurokawas are quiet as it is. You sat and talked about the old days.") } }),
                Opt(t("Гордо отказаться", "Refuse, proudly"), fx = { done("oda_son"); glory(1); money(3); t("Он понял и уехал. А мешок риса всё-таки «забыл» у ворот.", "He understood and rode off. But he “forgot” a sack of rice at the gate.") }),
            )
        }),

    Event("c_ikki", "barin", Tx("Память управы", "The Office Remembers"), w = 3, cond = { fire(ago("ikki", 8)) },
        txt = { t("Прошли годы с тех пор, как деревня стояла у замка с бамбуковыми копьями. В управе их не забыли. Писарь шепчет: подать вам скостят, если назовёте, кто тогда нёс знамя и кто писал прошение.",
            "Years have passed since the village stood at the castle with bamboo spears. The office has not forgotten. A clerk whispers: your tax will be cut if you name who carried the banner that day and who wrote the petition.") },
        opts = { o ->
            listOf(
                Opt(t("Промолчать", "Say nothing"), roll = Roll(HIT, 0, mapOf("hrab" to 1, "chest" to 1)),
                    ok = { done("ikki"); glory(2); t("Писарь подождал, вздохнул и ушёл ни с чем. Деревня узнала об этом раньше, чем он дошёл до моста.", "The clerk waited, sighed and left with nothing. The village knew of it before he reached the bridge.") },
                    bad = { done("ikki"); money(-5); t("Писарь ушёл ни с чем, а через месяц у вас нашлась «недоимка» за три года.", "The clerk left with nothing, and a month later you were found to owe “arrears” for three years.") }),
                Opt(t("Назвать имена", "Give the names"), sin = "cruel",
                    fx = { done("ikki"); money(6); glory(-3); addSecret(t("${o.h.name} ${sx(o.h, "выдал", "выдала")} управе зачинщиков старого бунта", "${o.h.name} gave the office the names of the old rising's leaders"), o.h)
                        t("Подать скостили. Двоих стариков увели в замок. Соседи пока не знают почему.", "Your tax was cut. Two old men were taken to the castle. The neighbors do not yet know why.") }),
                Opt(t("Назвать покойников", "Name only the dead"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 2, "umn" to 1)),
                    ok = { done("ikki"); money(4); t("Вы назвали троих, что давно лежат на кладбище. Писарь записал, довольный: бумага любит имена.", "You named three men long since in the graveyard. The clerk wrote them down, satisfied: paper likes names.") },
                    bad = { done("ikki"); money(-3); glory(-1); t("Писарь сверился с книгами и всё понял. Подать подняли, а вас записали в смутьяны.", "The clerk checked the books and understood. Your tax went up, and you went down as a troublemaker.") }),
            )
        }),
    Event("c_gimin", "church", Tx("Камень у перекрёстка", "The Stone at the Crossroads"), w = 3, cond = { fire(ago("gimin", 12)) },
        txt = { val n = person(g.flags["gimin_id"])?.name ?: t("ваш предок", "your ancestor")
            t("Деревня поставила у перекрёстка, где казнили зачинщиков, каменный алтарь. На нём выбито имя: $n — тот, кто заплатил головой за всю деревню. Туда носят рисовые лепёшки и просят заступничества, а вас зовут к открытию первыми.",
                "The village has set up a stone altar at the crossroads where the ringleaders were executed. Cut into it is a name: $n, who gave a life for the whole village. People leave rice cakes there and ask for protection, and you are asked to the dedication first of all.") },
        opts = {
            listOf(
                Opt(t("Взять заботу об алтаре на себя", "Take on the care of the altar"),
                    fx = { done("gimin"); unflag("gimin_id"); glory(2); addRelic(t("Камешек с алтаря", "A pebble from the altar"), "omamori", mapOf(SER to 1), t("с алтаря того, кто заплатил головой за деревню", "from the altar of the one who gave a life for the village")); t("Каждое утро вы метёте у камня. Сердце +1 в жребиях.", "Every morning you sweep around the stone. Heart +1 in draws.") }),
                Opt(t("Устроить праздник поминовения", "Hold a remembrance festival"), cost = 3, tag = t("3 мон", "3 mon"),
                    fx = { money(-3); done("gimin"); unflag("gimin_id"); glory(3); t("Барабаны, фонари, дети в новых куртках. Даже из соседних деревень пришли поклониться.", "Drums, lanterns, children in new jackets. People came from the next villages to bow.") }),
                Opt(t("Поклониться и вернуться к делам", "Bow and go back to work"), fx = { done("gimin"); unflag("gimin_id"); glory(1); t("Вы поклонились камню, как кланяются живым. Дома ждал рис.", "You bowed to the stone as one bows to the living. The rice was waiting at home.") }),
            )
        }),

    // ---------------------------------------------------------------- everyday
    Event("kaiko", "interior", Tx("Шелкопряды", "Silkworms"), cond = { h -> fire(age(h) >= 16) },
        txt = { if (g.year < 1859) t("Весной соседки берут у торговца лист бумаги с яичками шелкопряда. Месяц кормить червей шелковицей днём и ночью — и коконы уйдут скупщику. Хлопотно, зато живые деньги.",
                "In spring the women next door buy a sheet of silkworm eggs from a dealer. A month of feeding the worms mulberry leaves day and night, and the cocoons go to the buyer. A lot of bother, but real money.")
            else t("Шёлк-сырец теперь идёт за море, и скупщики платят за коконы втрое против прежнего. Вся деревня спит на чердаках, уступив дом червям.",
                "Raw silk goes overseas now, and the buyers pay three times the old price for cocoons. The whole village sleeps in the lofts and leaves the houses to the worms.") },
        opts = {
            val boom = g.year in 1859..1929
            listOf(
                Opt(t("Выкармливать червей", "Raise the worms"), cost = 2, tag = t("2 мон", "2 mon"), roll = Roll(UM, 1, mapOf("dobr" to 1, "umn" to 1)),
                    ok = { money(if (boom) 6 else 3); t("Черви шуршали, как дождь по крыше, и свили коконы плотные, как рисовые колобки. Скупщик не торговался.", "The worms rustled like rain on the roof and spun cocoons as firm as rice balls. The buyer did not haggle.") },
                    bad = { money(-2); t("На червей напала белая хворь: окаменели и рассыпались мелом. Лист шелковицы пропал зря.", "The white blight got into the worms: they went hard and crumbled like chalk. All that mulberry for nothing.") }),
                Opt(t("Не связываться", "Leave it alone"), fx = { t("Ваш дом остался вашим, а не червячьим.", "Your house stays yours and not the worms'.") }),
            )
        }),

    Event("drought", "field_dry", Tx("Засуха", "Drought"), cond = { yes },
        txt = { t("Сорок дней без дождя. Рисовые чеки потрескались, как старая лаковая чашка. Воды в канале на донышке, и Курокава ниже по течению уже запрудили её к себе.",
            "Forty days without rain. The paddies have cracked like an old lacquer bowl. There is only a trickle left in the canal, and the Kurokawas downstream have already dammed it off for themselves.") },
        opts = { o ->
            listOf(
                Opt(t("Ночью разобрать запруду Курокавы", "Break the Kurokawas' dam by night"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 1, "hrab" to 1)),
                    ok = { money(3); feud(1); t("Вода пошла к вам. Курокава подозревают, но не знают.", "The water came your way. The Kurokawas suspect, but they don't know.") },
                    bad = { feud(2); addTr(o.h, "shram"); t("Курокава сторожили запруду с мотыгами.", "The Kurokawas were guarding the dam with hoes.") }),
                Opt(t("Обещать Инари красные ворота за дождь", "Promise Inari a red gate for rain"), tag = t("[[обет придётся исполнить]]", "[[a vow must be kept]]"),
                    fx = { flag("vow_torii"); if (chance(.6)) t("На третий день с гор пришла туча, и дождь лил до утра. Теперь за вами ворота.", "On the third day a cloud came down off the hills and it rained till morning. Now you owe a gate.")
                        else { money(-3); t("Дождь пришёл через неделю, когда половина риса уже пожухла. Но пришёл — и ворота за вами.", "The rain came a week later, when half the rice had already withered. But it came, and you owe a gate.") } }),
                Opt(t("Носить воду вёдрами из реки", "Carry water from the river"), roll = Roll(SIL, 0, mapOf("bog" to 2, "hrab" to 1)),
                    ok = { t("Всей семьёй носили вёдра от реки до темноты — и рис дотянул.", "The whole family carried buckets from the river till dark, and the rice held out.") },
                    bad = { money(-3); o.h[ZDR]--; t("Надорвали спину, а рис всё равно пожух наполовину.", "You strained your back, and half the rice withered anyway.") }),
            )
        }),
    Event("c_vow", "inari", Tx("Обет Инари", "The Vow to Inari"), w = 3, cond = { fire(ago("vow_torii", 2)) },
        txt = { t("Дождь тогда пошёл, рис уродился. А красные ворота, обещанные Инари, всё не стоят. Ночью под окном кто-то тявкает — коротко, с укором.",
            "The rain came that year, and the rice grew. But the red gate you promised Inari has not been raised. At night something yaps under the window, short and reproachful.") },
        opts = {
            listOf(
                Opt(t("Поставить большие ворота", "Raise a big gate"), cost = 8, tag = t("8 мон", "8 mon"),
                    fx = { money(-8); done("vow_torii"); glory(2); g.bless = 1; t("Ворота покрасили киноварью и вырезали на столбе ваше имя. Утром на пороге лежала красная нитка.$BLESS_RU", "The gate was painted vermilion, with your name cut into the post. In the morning a red thread lay on the doorstep.$BLESS_EN") }),
                Opt(t("Поставить маленькие, деревянные", "Raise a small wooden one"), cost = 3, tag = t("3 мон", "3 mon"), roll = Roll(VER, 0, mapOf("bogom" to 1, "chest" to 1)),
                    ok = { money(-3); done("vow_torii"); t("Ворота в половину роста, но крашеные, как положено. Каменная лиса у святилища, кажется, кивнула.", "A gate half a man high, but painted as it should be. The stone fox at the shrine seemed to nod.") },
                    bad = { money(-3); done("vow_torii"); hex(4); t("Ночью маленькие ворота повалил ветер. В мешке с бобами на четыре года прибавился второй красный.", "In the night the wind blew the little gate down. For four years the bag of beans holds a second red one.") }),
                Opt(t("Забыть про обет", "Forget the vow"), sin = "lie", fx = { done("vow_torii"); hex(6); t("Тявканье под окном стихло. В мешке с бобами на шесть лет прибавился второй красный.", "The yapping under the window stopped. For six years the bag of beans holds a second red one.") }),
            )
        }),

    Event("obon", "night_int", Tx("Обон", "Obon"), cond = { h -> fire(age(h) >= 14 && chance(.6)) },
        txt = { pick(listOf(
            t("Середина седьмого месяца: предки возвращаются домой. У ворот горит костерок, чтобы они не заблудились, на алтаре — огурец-лошадка, чтобы скорей пришли, и баклажан-вол, чтобы не спешили уходить.",
                "The middle of the seventh month: the ancestors come home. A little fire burns at the gate so they won't lose their way, and on the altar stand a cucumber horse, so they come quickly, and an eggplant ox, so they are slow to leave."),
            t("Обон. Вся деревня танцует по кругу у храма под барабан, а на реку пускают бумажные фонарики — провожать предков обратно.",
                "Obon. The whole village dances in a ring by the temple to the drum, and paper lanterns are set on the river to see the ancestors off again."),
        )) },
        opts = { o ->
            listOf(
                Opt(t("Танцевать до рассвета", "Dance till dawn"), roll = Roll(SER, 1, mapOf("kras" to 1)),
                    ok = { glory(1); val k = kidIn(17, 28) { it.spouse == null }.firstOrNull()
                        if (k != null && chance(.5)) { val s = mkSpouse(k); t("${k.name} весь вечер ${sx(k, "танцевал", "танцевала")} в кругу с ${s.name} из ${s.from}, а к зиме сыграли свадьбу.", "${k.name} danced in the ring all evening with ${s.name} of ${s.from}, and by winter there was a wedding.") }
                        else t("Танцевали до рассвета. Даже покойники, говорят, притопывали.", "You danced till dawn. Even the dead, they say, were tapping their feet.") },
                    bad = { t("Вы оттоптали ноги половине деревни и ушли домой до полуночи.", "You trod on half the village's feet and went home before midnight.") }),
                Opt(t("Пустить фонарики по реке", "Set lanterns on the river"), fx = { ease(o.h); t("Фонарик с именами ваших покойных уплыл за поворот. На душе стало тише.", "The lantern with the names of your dead floated round the bend. Your heart is quieter.") }),
                Opt(t("Заказать службу в храме", "Pay for a service at the temple"), cost = 2, tag = t("2 мон", "2 mon"),
                    fx = { money(-2); glory(1); ease(o.h); t("Монах читал сутру, а вы смотрели, как дым уходит в потолок. Соседи заметили, сколько вы дали.", "The monk read the sutra and you watched the smoke rise to the rafters. The neighbors noticed how much you gave.") }),
            )
        }),

    Event("toyama", "road", Tx("Аптекарь из Тоямы", "The Toyama Medicine Man"), cond = { h -> fire(age(h) >= 16 && !flagged("toyama")) },
        txt = { t("К воротам подходит разносчик лекарств из Тоямы с коробом за спиной. Обычай у них такой: оставляет ящичек с порошками и пилюлями, а через год приходит и берёт плату только за то, что истрачено.",
            "A medicine man from Toyama comes to the gate with a chest on his back. That is their custom: he leaves a box of powders and pills, and a year later comes back and charges only for what has been used.") },
        opts = {
            listOf(
                Opt(t("Оставить ящичек", "Keep the box"), tag = t("[[через год он придёт за платой]]", "[[in a year he will come to be paid]]"),
                    fx = { flag("toyama"); val k = kidIn(0, 12).firstOrNull()
                        if (k != null) { k[ZDR]++; t("Зимой порошки от жара подняли на ноги ${k.name}. Ящичек висит на столбе у очага, и пакетиков в нём всё меньше.", "In winter the fever powders got ${k.name} back on ${sx(k, "his", "her")} feet. The box hangs on the post by the hearth, with fewer packets in it every month.") }
                        else t("Ящичек висит на столбе у очага. Спокойнее, когда он рядом.", "The box hangs on the post by the hearth. It is a comfort to have it there.") }),
                Opt(t("Расспросить о новостях с тракта", "Ask for news from the road"), roll = Roll(UM, 0, mapOf("gor" to 1, "gram" to 1)),
                    ok = { money(3); t("Он знает, где рис дороже, а где дешевле. Осенью вы продали там, где дороже.", "He knows where rice is dear and where it is cheap. In autumn you sold where it was dear.") },
                    bad = { t("Он рассказал много, но всё про чужие края. Интересно, а толку нет.", "He told you a great deal, all of it about faraway places. Interesting, but no use.") }),
                Opt(t("Отказаться", "Decline"), fx = { t("Аптекарь поклонился и пошёл к Курокаве.", "The medicine man bowed and went on to the Kurokawas.") }),
            )
        }),
    Event("c_toyama", "road", Tx("Расчёт с аптекарем", "Settling with the Medicine Man"), w = 3, cond = { fire(ago("toyama", 1)) },
        txt = { t("Год прошёл, и аптекарь из Тоямы снова у ворот. Он снимает ящичек со столба, пересчитывает пакетики и щёлкает на счётах.",
            "A year has passed, and the Toyama medicine man is back at the gate. He takes the box down from the post, counts the packets and clicks his abacus.") },
        opts = { o ->
            listOf(
                Opt(t("Заплатить за истраченное", "Pay for what was used"), cost = 2, tag = t("2 мон", "2 mon"),
                    fx = { money(-2); done("toyama"); g.bless = 1; t("Аптекарь доложил свежих порошков, оставил ящичек на год и подарил детям бумажный шарик. Говорят, это к удаче.$BLESS_RU", "The medicine man topped up the powders, left the box for another year and gave the children a paper balloon. That brings luck, they say.$BLESS_EN") }),
                Opt(t("Поторговаться", "Haggle"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "gor" to 1)),
                    ok = { money(-1); done("toyama"); t("Сошлись на одном моне и чашке чая. Аптекарь смеялся: в Тояме так не торгуются.", "You settled on one mon and a cup of tea. The medicine man laughed: they don't haggle like that in Toyama.") },
                    bad = { money(-3); done("toyama"); t("Аптекарь пересчитал всё ещё раз, вслух и при соседях. Вышло дороже.", "The medicine man counted it all again, out loud, in front of the neighbors. It came to more.") }),
                Opt(t("Сказать, что ящичек был пуст", "Say the box was empty"), sin = "lie",
                    fx = { done("toyama"); glory(-1); t("Аптекарь молча поклонился и ушёл. Больше к вам из Тоямы не ходят.", "The medicine man bowed without a word and left. No one from Toyama comes to you any more.") }),
            )
        }),

    Event("yobai", "night_int", Tx("Шорох у ставня", "A Rustle at the Shutter"), cond = { fire(kidIn(16, 23) { !it.male && it.spouse == null }.firstOrNull()) },
        txt = { o -> val x = o.xx
            t("Третью ночь кто-то тихо отодвигает ставень в комнате, где спит ${x.name}. По деревенскому обычаю парни ходят к девушкам ночью, а родители делают вид, что спят. Утром у колодца сын бондаря что-то уж больно весел.",
                "For the third night someone has quietly slid back the shutter of the room where ${x.name} sleeps. By village custom the lads visit the girls at night and the parents pretend to be asleep. At the well this morning the cooper's son is suspiciously cheerful.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Делать вид, что спите", "Pretend to be asleep"),
                    fx = { if (chance(.6)) { val s = mkSpouse(x); t("К осени от бондаря пришла накодо. Свадьбу сыграли без лишних хлопот: ${x.name} и ${s.name} всё решили сами, ночами.", "By autumn the cooper sent a go-between. The wedding was no trouble: ${x.name} and ${s.name} had settled it all themselves, by night.") }
                        else t("Визиты кончились так же тихо, как начались. ${x.name} неделю ходила грустная.", "The visits stopped as quietly as they had begun. ${x.name} was sad for a week.") }),
                Opt(t("Встретить гостя с палкой", "Meet the visitor with a stick"), roll = Roll(SIL, 0, mapOf("gnev" to 1, "bog" to 1)),
                    ok = { x[SER]--; t("Сын бондаря удирал без сандалий. ${x.name} с вами не разговаривает.", "The cooper's son ran off without his sandals. ${x.name} is not speaking to you.") },
                    bad = { t("В темноте вы налетели на корыто. Гость ушёл через другое окно.", "In the dark you ran into the washtub. The visitor left by another window.") }),
                Opt(t("Пойти к бондарю потолковать", "Go and talk to the cooper"), roll = Roll(SER, 0, mapOf("kras" to 1, "shch" to 1)),
                    ok = { val s = mkSpouse(x); t("Бондарь почесал в затылке и согласился: свадьба осенью. ${s.name} краснел до ушей.", "The cooper scratched his head and agreed: a wedding in autumn. ${s.name} blushed to the ears.") },
                    bad = { glory(-1); t("Бондарь заявил, что его сын по ночам спит, и захлопнул дверь. Деревня смеётся.", "The cooper declared that his son sleeps at night, and shut the door. The village is laughing.") }),
            )
        }),

    Event("kfire", "fire", Tx("Горит дом Курокавы", "The Kurokawas' House Is Burning"), cond = { fire(g.feud >= 1) },
        txt = { t("Ночью над крышей Курокавы встало зарево. Их дети в одних рубашках выбегают на холод, старик Курокава кричит, чтобы несли вёдра. Ваш дом — следующий по ветру.",
            "In the night a glow rose over the Kurokawas' roof. Their children run out into the cold in their shirts, and old Kurokawa is shouting for buckets. Your house is next downwind.") },
        opts = { o ->
            listOf(
                Opt(t("Бежать к ним с вёдрами", "Run over with buckets"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "dobr" to 1)),
                    ok = { feud(-2); glory(2); t("Отстояли половину дома и весь их рис. Утром старик Курокава пришёл кланяться — впервые на вашей памяти.", "You saved half the house and all their rice. In the morning old Kurokawa came to bow, the first time anyone can remember.") },
                    bad = { feud(-1); addTr(o.h, "shram"); t("Дом Курокавы сгорел, а вам опалило руки. Но они видели, кто прибежал первым.", "The Kurokawas' house burned and your hands were scorched. But they saw who came running first.") }),
                Opt(t("Поливать свою крышу", "Wet down your own roof"), fx = { t("Свою крышу вы отстояли. Курокава это видели.", "You saved your own roof. The Kurokawas saw that too.") }),
                Opt(t("Взять их детей к себе", "Take their children in"), cost = 2, tag = t("2 мон", "2 mon"),
                    fx = { money(-2); feud(-1); glory(1); t("До весны у вашего очага спали трое маленьких Курокав. Их мать теперь носит вам первую редьку с огорода.", "Till spring three little Kurokawas slept by your hearth. Now their mother brings you the first radish from her garden.") }),
            )
        }),

    Event("hawaii", "road", Tx("За море", "Across the Sea"), cond = { h -> fire(if (g.year in 1885..1934 && !flagged("emig")) sonOf(h).firstOrNull() else null) },
        txt = { o -> val x = o.xx
            if (g.year < 1908) t("По деревням ездит вербовщик: на Гавайях, на сахарных плантациях, за месяц платят столько, сколько здесь за год. Контракт на три года, проезд в долг. ${x.name} уже держит в руках листок с пальмами.",
                "A recruiter is going round the villages: on the sugar plantations of Hawaii a month's pay is what you earn here in a year. A three-year contract, passage on credit. ${x.name} is already holding the leaflet with the palm trees.")
            else t("Вербовщик эмиграционной компании набирает людей в Бразилию, на кофейные плантации: проезд оплачен, а через несколько лет, говорят, можно купить свою землю — столько, что за день не обойти. ${x.name} не спит третью ночь.",
                "An emigration company's recruiter is signing people up for Brazil, for the coffee plantations: the passage is paid, and in a few years, they say, you can buy land of your own, more than you could walk round in a day. ${x.name} has not slept for three nights.") },
        opts = { o ->
            val x = o.xx
            val far = if (g.year < 1908) t("на Гавайи", "to Hawaii") else t("в Бразилию", "to Brazil")
            listOf(
                Opt(t("Отпустить", "Let him go"), tag = t("уходит из рода", "leaves the family"),
                    fx = { x.monk = true; x.adopted = true; flag("emig"); flag("emig_id", x.id); deed(x, t("Уплыл за море, $far", "Sailed across the sea, $far"))
                        t("В порту Иокогамы пароход дал гудок. ${x.name} махал с палубы шапкой, пока не стал точкой.", "At Yokohama the steamer sounded its whistle. ${x.name} waved his hat from the deck until he was a speck.") }),
                Opt(t("Не пускать", "Forbid it"), fx = { x.stones++; t("${x.name} остался. Но листок с пальмами хранит под циновкой.", "${x.name} stayed. But he keeps the leaflet with the palm trees under his mat.") }),
            )
        }),
    Event("c_emig", "dawn", Tx("Письмо из-за моря", "A Letter from Across the Sea"), w = 3,
        cond = { val p = person(g.flags["emig_id"]); fire(if (ago("emig", 4) && p != null && alive(p)) p else null) },
        txt = { o -> val x = o.xx
            val work = if ((g.flags["emig"] ?: 0) < 1908) t("тростник режет руки", "the cane cuts your hands") else t("кофейные деревья тянутся до горизонта", "the coffee trees run to the horizon")
            t("Из-за моря пришло письмо и денежный перевод. ${x.name} пишет, что солнце там злое, $work, но платят исправно. И просит найти ему невесту: пусть пришлёт свою фотографию, а он пришлёт свою. Так теперь женятся все наши за морем.",
                "A letter has come from across the sea, with money. ${x.name} writes that the sun there is cruel and $work, but the pay is regular. And he asks you to find him a bride: let her send her photograph, and he will send his. That is how all our people over there marry now.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Найти ему невесту по фотографии", "Find him a picture bride"), cost = 2, tag = t("2 мон на фотографа", "2 mon for the photographer"),
                    fx = { done("emig"); money(4); glory(1); t("Нашли девушку из соседней деревни, сняли в городе в лучшем кимоно. Через год пришла свадебная фотография — и перевод.", "You found a girl from the next village and had her photographed in town in her best kimono. A year later a wedding photograph came back, and money with it.") }),
                Opt(t("Звать его домой", "Call him home"), roll = Roll(SER, 0, mapOf("kras" to 1, "dobr" to 1)),
                    ok = { done("emig"); x.monk = false; x.adopted = false; money(10); deed(x, t("Вернулся из-за моря с деньгами", "Came home from across the sea with money"))
                        t("${x.name} вернулся загорелым дочерна, с часами на цепочке и деньгами на новое поле.", "${x.name} came home burned black by the sun, with a watch on a chain and money for a new field.") },
                    bad = { done("emig"); money(3); t("Он ответил, что останется: там у него земля и друзья. Перевод приходит каждый Новый год.", "He wrote back that he will stay: he has land there, and friends. Money comes every New Year.") }),
                Opt(t("Взять деньги и благословить", "Take the money and send a blessing"), fx = { done("emig"); money(6); t("Перевод спрятали в шкатулку, а письмо — к табличкам предков.", "The money went into the box, and the letter beside the ancestor tablets.") }),
            )
        }),

    // ---------------------------------------------------------------- rare and strange
    Event("comet", "dawn", Tx("Хвостатая звезда", "The Broom Star"), cond = { h -> fire(age(h) >= 14 && chance(.2)) },
        txt = { t("Всю осень над горами по утрам висит звезда с хвостом, как метла. Старики говорят: она выметает старое — быть мору, войне или большой перемене. Монахи бьют в колокола, а разносчики уже торгуют оберегами «от метлы».",
            "All autumn a star with a tail like a broom has hung over the hills at dawn. The old people say it sweeps away the old: there will be plague, or war, or a great change. The monks ring the bells, and the pedlars are already selling charms against the broom.") },
        opts = { o ->
            listOf(
                Opt(t("Молиться всем домом", "Pray, the whole household"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)),
                    ok = { g.bless = 1; ease(o.h); t("Звезда ушла за горы так же тихо, как пришла. Утром на пороге лежала красная нитка.$BLESS_RU", "The star went down behind the hills as quietly as it came. In the morning a red thread lay on the doorstep.$BLESS_EN") },
                    bad = { t("Звезда ушла. Беда, если и была, прошла мимо — или ещё в пути.", "The star went away. Trouble, if there was any, passed you by, or is still on its way.") }),
                Opt(t("Торговать оберегами «от метлы»", "Sell charms against the broom"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2, "gor" to 1)),
                    ok = { money(5); t("Обереги из старой бумаги разошлись за неделю. Звезда ушла — значит, помогли.", "Charms made of old paper sold out in a week. The star went away, so they must have worked.") },
                    bad = { money(-1); glory(-1); t("Монахи узнали, чья это бумага. Пришлось раздавать даром и краснеть.", "The monks found out whose paper it was. You had to give them away and blush.") }),
                Opt(t("Смотреть на неё по ночам", "Watch it at night"), fx = { o.h[UM]++; t("Вы просидели на крыше полсотни ночей и поняли про небо больше, чем за всю жизнь. Соседи считают, что вы тронулись.", "You sat on the roof for fifty nights and learned more about the sky than in all your life. The neighbors think you have gone soft in the head.") }),
            )
        }),

    Event("kappa", "field", Tx("Каппа", "The Kappa"), cond = { h -> fire(age(h) >= 14 && chance(.2)) },
        txt = { t("У оросительного пруда что-то схватило вашего вола за ногу и потянуло в воду. Вы рубанули серпом — и в руке осталась зелёная перепончатая лапа. Ночью у двери плачет кто-то мокрый, ростом с ребёнка, с блюдцем на макушке. Просит лапу назад.",
            "At the irrigation pond something seized your ox by the leg and dragged it toward the water. You struck with your sickle, and a green webbed arm was left in your hand. In the night someone wet, the size of a child, with a dish on the top of its head, is weeping at your door. It wants the arm back.") },
        opts = { o ->
            listOf(
                Opt(t("Вернуть лапу за тайну мази", "Give back the arm for the secret of his salve"),
                    fx = { addRelic(t("Мазь каппы", "The kappa's salve"), "inro", mapOf(ZDR to 1), t("${o.h.name}: рецепт от каппы из оросительного пруда", "${o.h.name}: the recipe from the kappa in the irrigation pond"))
                        t("Каппа приставил лапу к плечу — приросла. В уплату он нашептал, как варить мазь, от которой срастаются кости. Здоровье +1 в жребиях.", "The kappa set the arm to his shoulder, and it grew back on. In payment he whispered how to make a salve that knits bones. Health +1 in draws.") }),
                Opt(t("Поклониться ему", "Bow to him"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "umn" to 1)),
                    ok = { glory(2); t("Вы поклонились, каппа из вежливости поклонился в ответ — и вода выплеснулась из блюдца. Без воды каппа слаб, как мокрая тряпка. Он поклялся, что в вашей деревне больше никто не утонет.", "You bowed, and the kappa, being polite, bowed back, and the water spilled from his dish. Without it a kappa is as weak as a wet rag. He swore that nobody in your village will ever drown again.") },
                    bad = { addTr(o.h, "shram"); t("Каппа кланяться не стал: он вежливый, но не дурак. Отбивались до рассвета.", "The kappa did not bow: he is polite, but no fool. You fought him off till dawn.") }),
                Opt(t("Продать лапу аптекарю", "Sell the arm to an apothecary"), sin = "greed",
                    fx = { money(5); hex(3); t("Аптекарь из города заплатил щедро. А в пруду с тех пор по ночам кто-то плачет, и в мешке с бобами на три года прибавился второй красный.", "The apothecary in town paid well. But since then something weeps in the pond at night, and for three years the bag of beans holds a second red one.") }),
            )
        }),

    Event("tanuki", "fair", Tx("Листья вместо монет", "Leaves for Coins"), cond = { h -> fire(age(h) >= 16 && chance(.2)) },
        txt = { t("Вчера заезжий монах с круглым брюхом купил у вас мешок риса и расплатился, не торгуясь. Утром в кошельке — дубовые листья, а у ворот следы, как от собаки, только косолапые. Тануки, не иначе.",
            "Yesterday a traveling monk with a round belly bought a sack of rice from you and paid without haggling. This morning there are oak leaves in your purse, and tracks by the gate like a dog's, only pigeon-toed. A tanuki, no doubt about it.") },
        opts = {
            listOf(
                Opt(t("Выследить по следам", "Follow the tracks"), roll = Roll(HIT, 0, mapOf("umn" to 1, "hrab" to 1)),
                    ok = { money(4); t("Под старой сосной в норе нашёлся ваш мешок и три настоящие монеты сверху — видно, за беспокойство.", "In a burrow under the old pine you found your sack, with three real coins on top, for your trouble, it seems.") },
                    bad = { money(-2); t("Следы кружили по лесу до вечера. Тануки, говорят, смеётся до сих пор.", "The tracks led you round the woods till evening. The tanuki, they say, is laughing still.") }),
                Opt(t("Оставить под сосной кувшинчик сакэ", "Leave a flask of sake under the pine"), cost = 1, tag = t("1 мон", "1 mon"),
                    fx = { money(-1); if (chance(.5)) { money(6); t("Через неделю у ворот лежала связка настоящих монет. Тануки любят сакэ и платят долги — когда протрезвеют.", "A week later a string of real coins lay by the gate. Tanuki love sake, and they pay their debts, once they sober up.") }
                        else t("Сакэ выпили. Кто — неизвестно.", "The sake was drunk. By whom, nobody knows.") }),
                Opt(t("Махнуть рукой и молчать", "Shrug it off and keep quiet"), fx = { money(-2); t("Мешок риса — не беда. Беда, если узнает деревня.", "A sack of rice is no great loss. The loss would be if the village found out.") }),
            )
        }),

    Event("kamikakushi", "forest", Tx("Унесённый богами", "Spirited Away"), cond = { val k = kidIn(4, 12); fire(if (k.isNotEmpty() && chance(.2)) k.first() else null) },
        txt = { o -> val x = o.xx
            t("${x.name} ${sx(x, "ушёл", "ушла")} за хворостом в лес за святилищем и не ${sx(x, "вернулся", "вернулась")}. Третий день вся деревня ходит по горам с барабанами и кличет по имени: так ищут тех, кого унесли боги.",
                "${x.name} went for firewood in the woods behind the shrine and did not come back. For three days the whole village has walked the hills with drums, calling the name: that is how you search for those the gods have taken.") },
        opts = { o ->
            val x = o.xx
            val found: Txt = { x[UM] += 2; t("${x.name} нашли на седьмой день у водопада: ${sx(x, "сидел", "сидела")} на камне, ${sx(x, "сытый и спокойный", "сытая и спокойная")}. Говорит, что длинноносый тэнгу учил летать и считать звёзды. С тех пор ${x.name} всё понимает с полуслова.",
                "${x.name} was found on the seventh day by the waterfall, sitting on a rock, well fed and calm. The child says a long-nosed tengu taught flying and counting the stars. Since then ${x.name} understands everything at half a word.") }
            val lost: Txt = { if (chance(.35)) { kill(x, t(sx(x, "унесён", "унесена") + " богами", "spirited away by the gods")); t("Нашли только сандалию на краю обрыва.", "All they found was a sandal at the edge of a cliff.") }
                else { x[SER]--; t("${x.name} нашли на девятый день. Молчит и смотрит сквозь людей; заговорить ${sx(x, "смог", "смогла")} только к зиме.", "${x.name} was found on the ninth day, silent, looking through people. Words came back only by winter.") } }
            listOf(
                Opt(t("Искать в горах с барабанами", "Search the hills with drums"), roll = Roll(UM, 0, mapOf("hrab" to 1, "umn" to 1)), ok = found, bad = lost),
                Opt(t("Отнести рисовые лепёшки к кедру тэнгу", "Take rice cakes to the tengu's cedar"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)), ok = found, bad = lost),
            )
        }),

    Event("ame", "night_int", Tx("Женщина за леденцами", "The Woman Who Bought Candy"), cond = { h -> fire(age(h) >= 16 && chance(.15)) },
        txt = { t("Шесть ночей подряд, пока вы сторожите ярмарочный лоток, к нему подходит бледная женщина в белом и покупает леденец за мон. На седьмую ночь монеты у неё нет, и она протягивает свой гребень.",
            "Six nights running, while you watch the fair stall, a pale woman in white comes and buys a sweet for one mon. On the seventh night she has no coin, and she holds out her comb.") },
        opts = { o ->
            listOf(
                Opt(t("Дать леденец и пойти следом", "Give her the sweet and follow her"), roll = Roll(VER, 0, mapOf("hrab" to 1, "dobr" to 1)),
                    ok = { glory(3); deed(o.h, t(sx(o.h, "Вырыл", "Вырыла") + " живого младенца из могилы", "Dug a living baby out of a grave"))
                        t("Она привела вас на кладбище и пропала у свежей могилы. Из-под земли плакал ребёнок. Вы раскопали: младенец жив, во рту леденец. Мать умерла родами, и её похоронили вместе с ним. Мальчика отдали в храм, монахи растят его как своего.",
                            "She led you to the graveyard and vanished at a fresh grave. A child was crying under the earth. You dug, and there was a baby, alive, with a sweet in its mouth. The mother had died in childbirth and was buried with the child. The boy was given to the temple, and the monks are raising him as their own.") },
                    bad = { o.h.stones++; t("У кладбищенской ограды вы оробели и повернули назад. Утром в храме, говорят, слышали плач из-под земли — и опоздали.", "At the graveyard fence your nerve failed and you turned back. In the morning, they say, the temple heard crying under the earth, and came too late.") }),
                Opt(t("Взять гребень и дать леденец", "Take the comb and give her the sweet"),
                    fx = { g.bless = 1; t("Утром гребень оказался старым и сырым, будто из земли. Больше она не приходила.$BLESS_RU", "In the morning the comb was old and damp, as if from the earth. She did not come again.$BLESS_EN") }),
                Opt(t("Запереть лоток", "Shut up the stall"), fx = { t("До утра возле лотка кто-то плакал.", "Till morning someone was weeping by the stall.") }),
            )
        }),

    Event("foxwife", "inari", Tx("Невеста из тумана", "A Bride from the Mist"), cond = { h -> val s = sonOf(h).firstOrNull(); fire(if (s != null && !flagged("foxwife") && chance(.15)) s else null) },
        txt = { o -> val x = o.xx
            t("${x.name} привёл из утреннего тумана девушку без родни и без приданого. Зовут её Кудзуха. Она красива, тиха и работает за троих, но кошка на неё шипит, а собаки поджимают хвосты. Старухи шепчутся: лиса.",
                "${x.name} has brought a girl home out of the morning mist, with no family and no dowry. Her name is Kuzunoha. She is beautiful and quiet and works for three, but the cat hisses at her and the dogs put their tails between their legs. The old women whisper: a fox.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Благословить", "Give your blessing"),
                    fx = { val s = reg(newP(t("Кудзуха", "Kuzunoha"), false, born = g.year - ri(17, 20), st = rstats(4, 8), from = t("тумана", "the mist"))); addTr(s, "kras"); addTr(s, "dobr")
                        wedTo(x, s); flag("foxwife"); flag("fox_id", s.id); g.bless = 1
                        t("Свадьбу сыграли в сумерках. В доме с тех пор пахнет травой, а в мешке, говорят, белых бобов прибавилось.$BLESS_RU", "The wedding was held at dusk. Since then the house smells of grass, and the bag, they say, holds more white beans.$BLESS_EN") }),
                Opt(t("Позвать жреца испытать её", "Have the priest test her"), roll = Roll(VER, 0, mapOf("bogom" to 1, "chest" to 1)),
                    ok = { val s = mkSpouse(x); t("Жрец окурил её дымом, она чихнула и засмеялась. Просто сирота из сгоревшей деревни, зовут ${s.name}. Свадьбу сыграли по-людски.", "The priest smoked her with incense; she sneezed and laughed. Just an orphan from a burned village, ${s.name} by name. The wedding was done properly.") },
                    bad = { x.stones++; t("В ночь перед испытанием она исчезла. ${x.name} каждый вечер ходит к святилищу.", "The night before the test she vanished. ${x.name} walks to the shrine every evening.") }),
                Opt(t("Не позволить", "Forbid it"), fx = { x.stones++; t("Девушка поклонилась и ушла в туман. ${x.name} долго смотрел ей вслед.", "The girl bowed and walked off into the mist. ${x.name} watched after her for a long time.") }),
            )
        }),
    Event("c_fox", "inari", Tx("Лес Синода", "Shinoda Forest"), w = 3,
        cond = { val s = person(g.flags["fox_id"]); fire(if (ago("foxwife", 4) && s != null && alive(s) && !s.monk) s else null) },
        txt = { o -> val s = o.xx
            t("Ребёнок проснулся ночью и увидел, как мать, задремав у прялки, обмахивается белым хвостом. Утром ${s.name} собрала узелок, а на ширме оставила стихи: «Если любишь — приходи в лес Синода, в Идзуми».",
                "A child woke in the night and saw ${s.name}, dozing at the spinning wheel, fanning herself with a white tail. In the morning she packed a bundle, and on the screen she left a poem: “If you love me, come to Shinoda Forest, in Izumi.”") },
        opts = { o ->
            val s = o.xx
            val go: Engine.() -> Unit = { s.monk = true; person(s.spouse)?.let { if (it.spouse == s.id) it.spouse = null }; deed(s, t("Ушла в лес Синода", "Went back to Shinoda Forest")); done("foxwife") }
            listOf(
                Opt(t("Отпустить", "Let her go"), fx = { go(); g.bless = 1; glory(1); t("Она поклонилась и ушла в туман. На пороге осталась красная нитка.$BLESS_RU", "She bowed and walked into the mist. A red thread was left on the doorstep.$BLESS_EN") }),
                Opt(t("Умолять остаться", "Beg her to stay"), roll = Roll(SER, -1, mapOf("kras" to 1, "dobr" to 1)),
                    ok = { done("foxwife"); t("Она осталась. Хвоста больше никто не видел — а может, просто перестали смотреть.", "She stayed. No one saw the tail again, or perhaps they stopped looking.") },
                    bad = { go(); t("Она плакала, но ушла. Лисы не остаются там, где их узнали.", "She wept, but she went. Foxes do not stay where they have been seen.") }),
                Opt(t("Пойти за ней в лес Синода", "Follow her to Shinoda Forest"), roll = Roll(VER, -1, mapOf("bogom" to 1, "hrab" to 1)),
                    ok = { go(); addRelic(t("Лисья жемчужина", "The fox's pearl"), "shell", mapOf(VER to 1), t("подарена в лесу Синода", "given in Shinoda Forest"))
                        t("В лесу вас встретила белая лиса и положила к ногам жемчужину. Вернуться она не обещала. Вера +1 в жребиях.", "In the forest a white fox met you and laid a pearl at your feet. She did not promise to come back. Faith +1 in draws.") },
                    bad = { go(); o.h[ZDR]--; t("Вы проплутали в лесу неделю и вернулись ни с чем, в горячке.", "You were lost in the forest for a week and came home with nothing but a fever.") }),
            )
        }),

    // ---------------------------------------------------------------- the family's way: a shop in town
    Event("w_shop", "village", Tx("Лавка в городе", "A Shop in Town"), cond = { h -> fire(noWay() && age(h) in 20..55 && g.money >= 12 && g.year < 1935) },
        txt = { if (g.year < 1868) t("Дальний родственник держал в призамковом городе лавку: соевый соус, масло для ламп, сушёная рыба. Он умер, и вдова продаёт лавку с товаром и с вывеской-занавеской за двенадцать мон. Купцы — сословие низкое, зато сытое.",
                "A distant relative kept a shop in the castle town: soy sauce, lamp oil, dried fish. He has died, and his widow is selling the shop, stock and shop curtain and all, for twelve mon. Merchants rank low, but they eat well.")
            else t("В уездном городе продаётся лавка: соевый соус, керосин, спички, сушёная рыба. Вдова хозяина просит двенадцать мон и обещает оставить постоянных покупателей.",
                "A shop is for sale in the district town: soy sauce, kerosene, matches, dried fish. The owner's widow wants twelve mon and promises to leave you her regular customers.") },
        opts = { o ->
            listOf(
                Opt(t("Купить лавку", "Buy the shop"), cost = 12, tag = t("12 мон · семья уходит в торговлю", "12 mon · the family goes into trade"),
                    fx = { money(-12); flag("way_shop"); unflag("way_paper"); addTr(o.h, "gor"); deed(o.h, t(sx(o.h, "Завёл", "Завела") + " лавку в городе", "Opened a shop in town"))
                        t("Над входом синяя занавеска с вашим знаком. Поле сдали соседу в аренду, и теперь вы встаёте до света не к рису, а к счётам.", "Over the door hangs a blue curtain with your mark. The field is let to a neighbor, and now you get up before light not for the rice but for the abacus.") }),
                Opt(t("Остаться на земле", "Stay on the land"), fx = { t("Земля не сгорит и не разорится. Ну, почти.", "Land does not burn down or go bankrupt. Well, hardly ever.") }),
            )
        }),
    Event("s_samurai", "barin", Tx("Долг самурая", "A Samurai's Debt"), w = 2, cond = { fire(flagged("way_shop") && g.year < 1871 && !flagged("sam_debt")) },
        txt = { t("В лавку заходит самурай из замка: сорок мешков риса жалованья и пятеро детей. Просит в долг под будущий паёк. Отказать самураю — обида, а дать… в соседнем княжестве недавно объявили, что долги самураев купцам прощены.",
            "A samurai from the castle comes into the shop: forty sacks of rice a year in stipend, and five children. He asks for a loan against his next allowance. Refusing a samurai is an insult, but lending… in the next domain they have just declared all samurai debts to merchants forgiven.") },
        opts = {
            listOf(
                Opt(t("Дать в долг", "Lend"), cost = 6, tag = t("6 мон", "6 mon"), fx = { money(-6); flag("sam_debt"); t("Самурай поклонился едва заметно — так кланяются тем, кому должны.", "The samurai bowed, barely: the bow one gives to the people one owes.") }),
                Opt(t("Отказать с поклонами", "Refuse with many bows"), roll = Roll(SER, 0, mapOf("kras" to 1, "krot" to 1)),
                    ok = { t("Вы так кланялись и так сокрушались, что самурай ещё и извинился.", "You bowed and lamented so much that the samurai ended up apologizing.") },
                    bad = { money(-2); glory(-1); t("Самурай побагровел. Теперь вся его родня берёт масло у соседа.", "The samurai went crimson. Now his whole family buys their oil next door.") }),
            )
        }),
    Event("c_samurai", "barin", Tx("Самурай возвращается", "The Samurai Returns"), w = 3, cond = { fire(ago("sam_debt", 3)) },
        txt = { if ((g.flags["sam_debt"] ?: 0) % 2 == 0) t("Самурай вернул долг сполна — и привёл с собой казначея замка. Княжескому дому нужен поставщик масла и соуса, честный и непугливый.",
                "The samurai has paid back every mon, and brought the castle treasurer with him. The lord's household needs a supplier of oil and soy sauce, someone honest and not easily frightened.")
            else t("Вышел указ: долги самураев купцам прощены, расписки — в огонь. Самурай пришёл сам, смущённый, с длинным свёртком в руках.",
                "A decree has come out: samurai debts to merchants are forgiven, the notes to be burned. The samurai has come himself, embarrassed, with a long bundle in his arms.") },
        opts = {
            if ((g.flags["sam_debt"] ?: 0) % 2 == 0) listOf(
                Opt(t("Стать поставщиком замка", "Supply the castle"), fx = { done("sam_debt"); money(8); glory(3); t("Над лавкой теперь табличка «Поставщик княжеского дома». Покупатели кланяются вывеске.", "A board over the shop now says “Purveyor to the Lord's Household.” Customers bow to the sign.") }),
                Opt(t("Взять только долг", "Take only the debt"), fx = { done("sam_debt"); money(7); t("Торговать с замком — что спать с тигром. Вы взяли свои деньги и поклонились.", "Trading with the castle is like sleeping with a tiger. You took your money and bowed.") }),
            ) else listOf(
                Opt(t("Взять его меч в уплату", "Take his sword in payment"),
                    fx = { done("sam_debt"); addRelic(t("Меч должника", "The debtor's sword"), "katana", mapOf(SIL to 1), t("отдан самураем в уплату долга", "given by a samurai to pay a debt")); t("Меч старый, но клинок без единой зазубрины. Сила +1 в жребиях.", "An old sword, but the blade without a single nick. Strength +1 in draws.") }),
                Opt(t("Простить и отпустить", "Forgive him and let him go"), fx = { done("sam_debt"); glory(2); t("Самурай поклонился вам так низко, как не кланялся никому из купцов. Об этом говорили на всей улице.", "The samurai bowed lower to you than he had ever bowed to a merchant. The whole street talked of it.") }),
                Opt(t("Жаловаться в замок", "Complain to the castle"), roll = Roll(SER, -1, mapOf("gram" to 1, "kras" to 1)),
                    ok = { done("sam_debt"); money(6); t("Казначей, кряхтя, вернул половину. Указ указом, а торговцы замку ещё нужны.", "The treasurer, grunting, paid back half. A decree is a decree, but the castle still needs its merchants.") },
                    bad = { done("sam_debt"); glory(-2); t("Вам велели знать своё место.", "You were told to know your place.") }),
            )
        }),
    Event("s_banto", "interior", Tx("Старший приказчик", "The Head Clerk"), w = 2, cond = { fire(flagged("way_shop") && !flagged("noren")) },
        txt = { t("Приказчик Тёсукэ служит у вас двенадцать лет — с мальчишки на побегушках. Он просит по обычаю «разделить занавеску»: позволить ему открыть свою лавку под вашим знаком, как ветку от ствола.",
            "Chosuke the clerk has served you twelve years, since he was an errand boy. He asks, as custom allows, to “share the curtain”: to open a shop of his own under your mark, like a branch from the trunk.") },
        opts = {
            listOf(
                Opt(t("Разделить занавеску", "Share the curtain"), cost = 5, tag = t("5 мон на обзаведение", "5 mon to set him up"),
                    fx = { money(-5); glory(2); flag("noren"); t("Над новой лавкой висит занавеска с вашим знаком, только поуже. Тёсукэ каждый Новый год приходит кланяться с подарком.", "Over the new shop hangs a curtain with your mark, only narrower. Every New Year Chosuke comes to bow, with a gift.") }),
                Opt(t("Сперва проверить книги", "Check the ledgers first"), roll = Roll(UM, 0, mapOf("gram" to 2, "umn" to 1)),
                    ok = { flag("noren"); if (chance(.5)) { glory(2); t("Книги чисты, как первый снег. Вы отпустили его с благословением.", "The books are as clean as first snow. You let him go with your blessing.") }
                        else { money(5); t("В книгах нашлась недостача, небольшая, но регулярная. Тёсукэ вернул всё и ушёл без занавески.", "The books showed a shortfall, small but regular. Chosuke paid it all back and left without the curtain.") } },
                    bad = { t("В книгах вы ничего не поняли. Тёсукэ терпеливо ждал.", "You could make nothing of the books. Chosuke waited patiently.") }),
                Opt(t("Отказать", "Refuse"), roll = Roll(SER, -1, mapOf("kras" to 1, "shch" to 1)),
                    ok = { t("Тёсукэ поклонился и остался ещё на пять лет.", "Chosuke bowed and stayed on another five years.") },
                    bad = { flag("noren"); money(-4); t("Тёсукэ ушёл к соседу и увёл половину покупателей.", "Chosuke went over to a rival and took half your customers with him.") }),
            )
        }),
    Event("s_dojima", "village", Tx("Рис на бумаге", "Rice on Paper"), w = 2, cond = { fire(flagged("way_shop") && g.year >= 1730) },
        txt = { t("Свояк вернулся из Осаки: на рисовой бирже в Додзиме торгуют рисом, которого никто не видел, — бумагами на будущий урожай. Если осень выйдет худой, цена взлетит. Он зовёт вложиться.",
            "Your brother-in-law is back from Osaka: at the Dojima rice exchange they trade rice that nobody has seen, paper on next year's crop. If the autumn is poor, the price will soar. He wants you to come in with him.") },
        opts = {
            listOf(
                Opt(t("Вложить немного", "Put in a little"), cost = 5, tag = t("5 мон", "5 mon"), roll = Roll(UM, 0, mapOf("umn" to 1, "gram" to 1)),
                    ok = { money(7); t("Осень вышла дождливой, и бумага на рис подорожала вдвое.", "The autumn was wet, and the rice paper doubled in price.") },
                    bad = { money(-5); t("Урожай вышел на славу, а ваша бумага — на растопку.", "The harvest was splendid, and your paper went to light the stove.") }),
                Opt(t("Вложить всё", "Put in everything"), cost = 12, tag = t("12 мон · [[можно лишиться лавки]]", "12 mon · [[the shop is at risk]]"), roll = Roll(UM, -2, mapOf("umn" to 1, "gram" to 1)),
                    ok = { money(18); glory(1); t("Вы угадали погоду лучше осакских купцов. Свояк называет вас «сэнсэй».", "You guessed the weather better than the Osaka merchants. Your brother-in-law calls you “master” now.") },
                    bad = { money(-12); if (chance(.4)) { loseWay("shop"); t("Лавку пришлось продать за долги. Вы вернулись в деревню, к земле.", "The shop had to be sold for the debts. You went back to the village and the land.") } else t("Пропало всё вложенное. Лавку удалось удержать — на одних поклонах кредиторам.", "Everything you put in is gone. You kept the shop, on nothing but bows to your creditors.") }),
                Opt(t("Рис, которого нельзя потрогать, — не рис", "Rice you can't touch isn't rice"), fx = { t("Свояк назвал вас деревенщиной и уехал богатеть один.", "Your brother-in-law called you a country bumpkin and went off to get rich alone.") }),
            )
        }),
    Event("s_fire", "fire", Tx("Цветы Эдо", "The Flowers of Edo"), w = 2, cond = { fire(flagged("way_shop")) },
        txt = { if (g.year < 1868) t("«Пожары и драки — цветы Эдо», говорят горожане, и в вашем городе цветут те же цветы. Ночью загорелся квартал ниже по улице, и ветер гонит огонь к лавкам.",
                "“Fires and brawls are the flowers of Edo,” townsfolk say, and the same flowers bloom in your town. In the night the block down the street caught fire, and the wind is driving it toward the shops.")
            else t("Ночью загорелась улица ниже по склону. Пожарные с баграми не поспевают, и ветер гонит огонь к лавкам.",
                "In the night the street below caught fire. The firemen with their hooks can't keep up, and the wind is driving it toward the shops.") },
        opts = {
            listOf(
                Opt(t("Замазать двери кладовой глиной", "Seal the storehouse doors with clay"), roll = Roll(UM, 1, mapOf("gor" to 1, "umn" to 1)),
                    ok = { money(-2); t("Двери каменной кладовой замазали глиной пополам с соевой пастой, как делают старые купцы. Лавка сгорела, кладовая цела. Через месяц вы снова торговали.", "You sealed the stone storehouse doors with clay and soy paste, the way old merchants do. The shop burned, the storehouse held. A month later you were trading again.") },
                    bad = { money(-part(.5)); loseWay("shop"); t("Огонь добрался и до кладовой. Сгорело всё. Вы вернулись в деревню с одной счётной книгой.", "The fire got into the storehouse too. Everything burned. You went back to the village with nothing but one account book.") }),
                Opt(t("Заплатить пожарным, чтобы разобрали соседние сараи", "Pay the firemen to pull down the sheds next door"), cost = 4, tag = t("4 мон", "4 mon"), roll = Roll(SER, 0, mapOf("shch" to 1, "kras" to 1)),
                    ok = { money(-4); glory(1); t("Пожарные растащили баграми два сарая, и огонь встал в двух домах от вас.", "The firemen hauled two sheds apart with their hooks, and the fire stopped two houses short of yours.") },
                    bad = { money(-4 - part(.4)); loseWay("shop"); t("Пожарные взяли деньги и не успели. Лавка сгорела дотла, и вы вернулись к земле.", "The firemen took the money and were too late. The shop burned to the ground, and you went back to the land.") }),
                Opt(t("Спасать детей и счётные книги", "Save the children and the ledgers"),
                    fx = { money(-part(.4)); if (chance(.5)) { loseWay("shop"); t("Все живы. По книгам вам должны многие — но и должники погорели. Лавку не поднять, вы вернулись к земле.", "Everyone is alive. The books say many people owe you, but the debtors burned out too. The shop can't be rebuilt; you have gone back to the land.") }
                        else t("Все живы, книги целы. По книгам собрали долги и к лету отстроились.", "Everyone is alive and the books are safe. You collected what was owed and rebuilt by summer.") }),
            )
        }),

    // ---------------------------------------------------------------- the family's way: winter paper
    Event("w_paper", "winter", Tx("Бумага зимой", "Winter Paper"), cond = { h -> fire(noWay() && age(h) in 16..50 && g.year < 1900) },
        txt = { t("Зимой поля спят, а в соседней долине в каждом доме делают бумагу: варят кору бумажной шелковицы, бьют её колотушками и черпают сеткой из ледяной воды. Мастер Гэндзо берётся научить — за зиму работы задаром.",
            "In winter the fields sleep, and in the next valley every house makes paper: they boil the bark of the paper mulberry, beat it with mallets and scoop it from ice water with a screen. Master Genzo offers to teach you, for a winter's work unpaid.") },
        opts = { o ->
            listOf(
                Opt(t("Учиться", "Learn"), tag = t("семья займётся ремеслом", "the family takes up a craft"), roll = Roll(UM, 0, mapOf("umn" to 1, "krot" to 1)),
                    ok = { flag("way_paper"); unflag("way_shop"); deed(o.h, t(sx(o.h, "Выучился", "Выучилась") + " делать бумагу", "Learned to make paper"))
                        t("К весне ваш первый лист просвечивал на солнце, как облако. Мастер кивнул — у него это вместо похвалы.", "By spring your first sheet glowed in the sun like a cloud. The master nodded, which is how he praises.") },
                    bad = { o.h[ZDR]--; t("Мастер выгнал вас к Новому году: «Руки как грабли». Пальцы ломит до сих пор.", "The master sent you packing by New Year: “Hands like rakes.” Your fingers ache to this day.") }),
                Opt(t("Не наше это дело", "That's not our trade"), fx = { t("Зимой вы плетёте мешки и рассказываете детям про лис. Тоже дело.", "In winter you weave sacks and tell the children about foxes. That's work too.") }),
            )
        }),
    Event("k_monopoly", "barin", Tx("Казённая бумага", "The Domain Monopoly"), w = 2, cond = { fire(flagged("way_paper") && g.year < 1871) },
        txt = { t("Князь объявил бумагу монополией: всю, что сделаете, сдавать в казённую контору по казённой цене, а цена вдвое ниже рыночной. Перекупщики из Осаки шепчут, что заплатят настоящую — только тихо.",
            "The lord has made paper a domain monopoly: every sheet must go to the domain office at the office's price, and that is half the market price. The Osaka dealers whisper that they will pay the real price, if it's done quietly.") },
        opts = { o ->
            listOf(
                Opt(t("Сдавать в контору", "Sell to the office"), fx = { money(2); t("Контора платит мало, зато без хлопот и без палок.", "The office pays little, but there's no trouble and no beatings.") }),
                Opt(t("Продавать тайком в Осаку", "Sell to Osaka on the quiet"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1, "gor" to 1)),
                    ok = { money(7); addSecret(t("${o.h.name} ${sx(o.h, "продавал", "продавала")} казённую бумагу перекупщикам", "${o.h.name} sold monopoly paper to the dealers"), o.h); t("Бумагу увозили ночью под мешками с редькой.", "The paper went out by night under sacks of radishes.") },
                    bad = { money(-4); glory(-2); t("Груз взяли на заставе. Бумагу забрали, а вас записали в книгу.", "The load was stopped at the barrier. The paper was taken, and your name went into the book.") }),
                Opt(t("Подписать прошение бумажных деревень", "Sign the paper villages' petition"), roll = Roll(SER, -1, mapOf("hrab" to 1, "gram" to 1)),
                    ok = { money(4); glory(2); t("Двенадцать деревень встали заодно, и князь сбавил: часть бумаги можно продавать вольно.", "Twelve villages stood together, and the lord gave way: part of the paper may be sold freely.") },
                    bad = { addTr(o.h, "shram"); t("Подписавших первыми били палками перед управой. Монополия осталась.", "Those who signed first were beaten in front of the office. The monopoly stayed.") }),
            )
        }),
    Event("k_order", "interior", Tx("Бумага для сёдзи", "Paper for the Screens"), w = 2, cond = { fire(flagged("way_paper")) },
        txt = { t("Из ${if (g.year < 1868) "замка" else "города"} пришёл заказ: бумага для раздвижных дверей новой гостиной — тонкая, ровная, без единого узелка. Сорок листов к первому снегу.",
            "An order has come from the ${if (g.year < 1868) "castle" else "town"}: paper for the sliding doors of a new reception room, thin and even, without a single knot. Forty sheets by the first snow.") },
        opts = { o ->
            listOf(
                Opt(t("Взяться", "Take it on"), roll = Roll(UM, 0, mapOf("krot" to 1, "umn" to 1)),
                    ok = { money(8); glory(1); t("Листы ушли все до одного. Говорят, в новой гостиной свет как в облаке.", "Every sheet was taken. They say the light in the new room is like the inside of a cloud.") },
                    bad = { money(-2); t("Половину листов забраковали: узелки. Кору в этот год вы взяли не ту.", "Half the sheets were rejected: knots. You used the wrong bark this year.") }),
                Opt(t("Посадить за чаны и детей", "Put the children to the vats too"), roll = Roll(UM, 1),
                    ok = { money(6); kidIn(8, 16).firstOrNull()?.let { it[UM]++ }; t("Дети черпали не хуже вас. Старшая уже ворчит на младших, как мастер Гэндзо.", "The children scooped as well as you do. The eldest already grumbles at the little ones like Master Genzo.") },
                    bad = { money(1); kidIn(4, 12).firstOrNull()?.let { it[ZDR]-- }; t("Дети простыли у ледяных чанов, а листы вышли кое-какие.", "The children caught cold at the icy vats, and the sheets came out middling.") }),
                Opt(t("Отказаться", "Turn it down"), fx = { t("Заказ ушёл в соседнюю долину. Мастер Гэндзо улыбался.", "The order went to the next valley. Master Genzo smiled.") }),
            )
        }),
    Event("k_cold", "winter", Tx("Ледяная вода", "Ice Water"), w = 2, cond = { fire(flagged("way_paper")) },
        txt = { t("Бумагу черпают зимой: летом кора гниёт, а в холодной воде волокно ложится ровно. Руки у вас красные до локтей и не разгибаются по утрам.",
            "Paper is made in winter: in summer the bark rots, but in cold water the fibres lie flat. Your arms are red to the elbows and won't straighten in the mornings.") },
        opts = { o ->
            listOf(
                Opt(t("Работать дальше", "Keep at it"), roll = Roll(ZDR, 0, mapOf("bog" to 1, "krot" to 1)),
                    ok = { money(5); t("К весне сушильные доски стояли вдоль всего забора, и скупщик забрал всё.", "By spring the drying boards stood along the whole fence, and the buyer took everything.") },
                    bad = { money(2); o.h[ZDR]--; t("Руки так и не отошли до лета.", "Your hands never recovered till summer.") }),
                Opt(t("Нанять девушку из деревни", "Hire a girl from the village"), cost = 2, tag = t("2 мон", "2 mon"),
                    fx = { money(3); t("Девушка черпает, вы сушите. Выходит меньше, зато руки целы.", "The girl scoops and you dry. Less comes of it, but your hands are spared.") }),
                Opt(t("Переждать холода", "Wait out the cold"), fx = { t("Зиму вы просидели у очага. Мастер Гэндзо качал головой.", "You sat out the winter by the hearth. Master Genzo shook his head.") }),
            )
        }),
    Event("k_machine", "village", Tx("Машинная бумага", "Machine Paper"), w = 3, cond = { fire(flagged("way_paper") && g.year >= 1890) },
        txt = { t("В городе построили фабрику: машина с паровым котлом гонит бумагу рулонами, и стоит она вчетверо дешевле вашей. Перекупщики больше не приезжают.",
            "They have built a mill in town: a machine with a steam boiler turns out paper by the roll, at a quarter of the price of yours. The dealers come no more.") },
        opts = { o ->
            listOf(
                Opt(t("Делать тонкую бумагу для художников", "Make fine paper for artists"), roll = Roll(UM, 0, mapOf("gram" to 1, "umn" to 1)),
                    ok = { money(6); glory(2); t("Машина гонит рулоны, а за вашими листами приезжают художники и каллиграфы. Таких листов машина не умеет.", "The machine turns out rolls, and painters and calligraphers come for your sheets. The machine can't make those.") },
                    bad = { loseWay("paper"); t("Художников на всех мастеров не хватило. Чаны рассохлись, и вы вернулись к рису.", "There weren't enough artists for all the paper-makers. The vats dried out, and you went back to the rice.") }),
                Opt(t("Пойти работать на фабрику", "Go to work at the mill"), fx = { loseWay("paper"); addTr(o.h, "gor"); money(5); t("Двенадцать часов у ревущей машины. Платят каждую неделю, и руки больше не мёрзнут.", "Twelve hours beside a roaring machine. The pay comes every week, and your hands don't freeze any more.") }),
                Opt(t("Делать как делали", "Go on as before"), fx = { money(-3); t("Бумага лучше прежнего, а покупают всё меньше.", "The paper is better than ever, and fewer and fewer buy it.") }),
            )
        }),
)

internal val japanMoreHistory: List<Event> = listOf(
    Event("inu", "village", Tx("Собачий сёгун", "The Dog Shogun"), y0 = 1687, y1 = 1708, p = .3, cond = { h -> fire(age(h) >= 14) },
        txt = { t("Сёгун Цунаёси родился в год Собаки и запретил обижать живых тварей, а собак — пуще всех. В Эдо для бродячих псов выстроили псарни на десятки тысяч голов и кормят их за счёт деревень. А к вам в курятник второй месяц наведывается пёс с рыжими ушами.",
            "The shogun Tsunayoshi was born in the year of the Dog and has forbidden harm to any living creature, dogs above all. In Edo they have built kennels for tens of thousands of strays and feed them at the villages' expense. And for two months a red-eared dog has been raiding your hen-house.") },
        opts = { o ->
            listOf(
                Opt(t("Кормить пса, пусть живёт", "Feed the dog and let it stay"), tag = t("−2 мон", "−2 mon"),
                    fx = { money(-2); if (chance(.5)) { g.bless = 1; t("Пёс растолстел, перестал таскать кур и сторожит ворота лучше всякой собаки. Впрочем, он и есть собака.$BLESS_RU", "The dog grew fat, stopped taking hens and guards the gate better than any dog. Well, he is a dog.$BLESS_EN") }
                        else t("Пёс растолстел и по-прежнему таскает кур, только теперь сытым.", "The dog grew fat and still takes hens, only now on a full stomach.") }),
                Opt(t("Отвести в казённую псарню", "Take it to the official kennel"), cost = 1, tag = t("1 мон", "1 mon"),
                    fx = { money(-1); t("Чиновник записал пса в книгу с приметами, как человека.", "The official entered the dog in his book with a description, as if it were a person.") }),
                Opt(t("Тихо прибить", "Quietly kill it"), sin = "cruel", roll = Roll(HIT, 0, mapOf("lzh" to 1, "zhest" to 1)),
                    ok = { money(1); addSecret(t("${o.h.name} ${sx(o.h, "убил", "убила")} собаку при Собачьем сёгуне", "${o.h.name} killed a dog in the Dog Shogun's day"), o.h); t("Пса закопали за сараем. Куры целы.", "The dog is buried behind the shed. The hens are safe.") },
                    bad = { money(-8); glory(-2); addTr(o.h, "shram"); t("Курокава донесли. Вас били палками и взяли штраф, на который можно было купить три курятника.", "The Kurokawas informed on you. You were beaten and fined enough to buy three hen-houses.") }),
            )
        }),
    Event("kyoho", "field_dry", Tx("Саранча Кёхо", "The Kyoho Locusts"), y0 = 1732, y1 = 1734, p = 1.0, cond = { yes },
        txt = { t("На западе саранча съела рис до корня, и в Эдо горожане впервые громят рисовые лавки. До вас саранча не дошла, но рис дорожает с каждой неделей. Торговец с юга привёз клубни сладкого картофеля: растёт на любой земле, даже в сушь.",
            "In the west the locusts have eaten the rice to the root, and in Edo the townsfolk are smashing rice shops for the first time ever. The locusts did not reach you, but rice costs more every week. A dealer from the south has brought sweet potato tubers: they grow in any soil, even in a drought.") },
        opts = {
            listOf(
                Opt(t("Посадить сладкий картофель", "Plant the sweet potatoes"), roll = Roll(UM, 1, mapOf("umn" to 1, "gram" to 1)),
                    ok = { money(3); flag("imo"); t("Клубни взошли на склоне, где и сорняк не рос. Теперь в недород у вас будет чем кормить детей.", "The tubers came up on a slope where not even weeds grew. Now, in a lean year, you will have something to feed the children.") },
                    bad = { t("Клубни сгнили в сырой земле. Торговец уверяет, что вы сажали вверх ногами.", "The tubers rotted in the wet ground. The dealer swears you planted them upside down.") }),
                Opt(t("Продать рис в Эдо втридорога", "Sell rice to Edo at three times the price"), sin = "greed", fx = { money(6); glory(-1); t("Деньги хорошие. Соседи запомнили.", "Good money. The neighbors remembered.") }),
                Opt(t("Послать рис родне на запад", "Send rice to kin in the west"), cost = 3, tag = t("3 мон", "3 mon"), fx = { money(-3); glory(2); t("Через полгода пришло письмо: ваш рис спас двоих детей.", "Half a year later a letter came: your rice saved two children.") }),
            )
        }),
    Event("okage", "road", Tx("Окагэ-маири", "The Thanksgiving Pilgrimage"), y0 = 1771, y1 = 1771, p = 1.0, cond = { yes },
        txt = { t("Весной вдруг сорвались с места тысячи: идут в Исэ без пропусков, без спроса, без денег — «по милости богини». Работники бросают хозяев, дети — родителей, и по дороге их кормят даром. Мимо ваших ворот течёт белая река паломников с черпаками за поясом.",
            "In spring thousands suddenly took to the road: they are walking to Ise with no passes, no leave and no money, “by the goddess's grace.” Servants are leaving their masters and children their parents, and along the way they are fed for nothing. A white river of pilgrims, ladles at their belts, flows past your gate.") },
        opts = { o ->
            listOf(
                Opt(t("Идти со всеми", "Go with them"), roll = Roll(ZDR, 1, mapOf("krot" to 1, "bogom" to 1)),
                    ok = { addTr(o.h, "bogom"); o.h.stones = 0; glory(1); t("Шли без денег, ели что подадут, спали под навесами храмов. Вернулись через месяц с оберегом и с лёгкой душой.", "You walked without money, ate what was given, slept under temple eaves. You came home a month later with a charm and a light heart.") },
                    bad = { o.h[ZDR]--; t("В давке у переправы вас сбили с ног. Вернулись с полпути, хромая.", "In the crush at a ferry you were knocked down. You came back from halfway, limping.") }),
                Opt(t("Кормить паломников у ворот", "Feed the pilgrims at the gate"), cost = 3, tag = t("3 мон", "3 mon"),
                    fx = { money(-3); glory(2); g.bless = 1; t("Говорят, кормить паломников — то же паломничество, только ноги целы.$BLESS_RU", "Feeding pilgrims, they say, is a pilgrimage too, only easier on the feet.$BLESS_EN") }),
                Opt(t("Запереть ворота: рис не ждёт", "Bar the gate: the rice won't wait"),
                    fx = { val k = kidIn(10, 17).firstOrNull()
                        if (k != null) { addTr(k, "bogom"); t("${k.name} ночью ${sx(k, "сбежал", "сбежала")} с паломниками, без спроса, с черпаком за поясом. Через месяц ${sx(k, "вернулся", "вернулась")} загорел${sx(k, "ым", "ой")} и с оберегом.", "${k.name} ran off with the pilgrims in the night, without asking, a ladle at the belt. A month later ${k.name} came home sunburned, with a charm.") }
                        else t("Ворота на засов. Мимо прошла половина провинции, распевая.", "The gate stayed barred. Half the province went past, singing.") }),
            )
        }),
    Event("ansei", "fire", Tx("Землетрясение Ансэй", "The Ansei Earthquake"), y0 = 1855, y1 = 1855, p = 1.0, cond = { yes },
        txt = { t("Ночью в десятом месяце тряхнуло так, что в Эдо рухнули тысячи домов и заполыхали целые кварталы. У вас повело крышу и треснул очаг. Через неделю разносчики продают гравюры: огромный сом под землёй, горожане колотят его, а плотники и кровельщики сому кланяются — им работа на годы.",
            "One night in the tenth month the earth shook so hard that thousands of houses fell in Edo and whole districts burned. Your roof is twisted and the hearth cracked. A week later the pedlars are selling prints: a huge catfish under the earth, the townsfolk beating it, and the carpenters and roofers bowing to it, since it has given them years of work.") },
        opts = { o ->
            listOf(
                Opt(t("Идти в Эдо на стройки", "Go to Edo to rebuild"), fx = { money(5); addTr(o.h, "gor"); t("Полгода таскали балки на пепелищах. Сом, как и обещала гравюра, кормит плотников.", "Half a year hauling beams on burned ground. The catfish, just as the print promised, feeds the carpenters.") }),
                Opt(t("Чинить свою крышу", "Mend your own roof"), roll = Roll(SIL, 0, mapOf("bog" to 1, "umn" to 1)),
                    ok = { t("Крышу выправили до снега, очаг замазали глиной.", "The roof was straightened before the snow, and the hearth patched with clay.") },
                    bad = { money(-4); t("Крыша обвалилась при первом снеге. Пришлось нанимать плотника, а плотники нынче в цене.", "The roof fell in with the first snow. You had to hire a carpenter, and carpenters cost a fortune now.") }),
                Opt(t("Купить гравюру с сомом", "Buy a catfish print"), cost = 1, tag = t("1 мон", "1 mon"),
                    fx = { money(-1); ease(o.h); t("Сом висит над очагом. Дети смеются над ним и меньше боятся, когда дом скрипит.", "The catfish hangs over the hearth. The children laugh at it and are less afraid when the house creaks.") }),
            )
        }),
    Event("eejanaika", "village", Tx("Эдзя най ка", "Ee Ja Nai Ka"), y0 = 1867, y1 = 1867, p = 1.0, cond = { yes },
        txt = { t("С неба падают бумажные обереги из Исэ — на крыши, на дороги, в колодцы. Люди бросают работу и пляшут по улицам днём и ночью, мужчины в женских платьях, женщины в мужских, и поют: «Эдзя най ка! А чем не хорошо?» Сёгунат доживает последние месяцы, и все это чувствуют.",
            "Paper charms from Ise are falling from the sky, onto roofs, onto roads, into wells. People drop their work and dance in the streets day and night, men in women's clothes and women in men's, singing “Ee ja nai ka! What's wrong with that?” The shogunate has months left, and everyone can feel it.") },
        opts = { o ->
            listOf(
                Opt(t("Плясать со всеми", "Dance with everyone"), roll = Roll(SER, 1, mapOf("kras" to 1, "gnev" to 1)),
                    ok = { o.h.stones = 0; glory(1); t("Три дня и три ночи. Вернулись без сандалий, но с лёгкой душой.", "Three days and three nights. You came home without your sandals, but with a light heart.") },
                    bad = { money(-3); t("Пока вы плясали, кто-то вынес из дома мешок риса. А чем не хорошо?", "While you danced, someone carried a sack of rice out of the house. What's wrong with that?") }),
                Opt(t("Поднять оберег, упавший на крышу", "Pick up the charm that fell on your roof"), roll = Roll(VER, 0, mapOf("bogom" to 1, "chest" to 1)),
                    ok = { addRelic(t("Оберег с неба", "The charm from the sky"), "omamori", mapOf(VER to 1), t("упал на крышу в год «Эдзя най ка»", "fell on the roof in the year of Ee ja nai ka")); t("Настоящий, из Исэ, с печатью. Как он попал на вашу крышу — не спрашивайте. Вера +1 в жребиях.", "A real one, from Ise, with the seal. How it got onto your roof, don't ask. Faith +1 in draws.") },
                    bad = { t("Оберег оказался листком из лавки, что торгует зубным порошком.", "The charm turned out to be a handbill from a shop that sells tooth powder.") }),
                Opt(t("Сидеть дома с палкой", "Stay home with a stick"), fx = { t("Вы сидели на пороге с палкой. Мимо, приплясывая, прошла вся провинция.", "You sat on the doorstep with a stick. The whole province went by, dancing.") }),
            )
        }),
    Event("nisshin", "war", Tx("Война с Цин", "The War with China"), y0 = 1894, y1 = 1895, p = .8, cond = { h -> fire(age(h) >= 16) },
        txt = { t("Война с Цинским Китаем. Газеты печатают цветные гравюры побед, станции провожают полки с фонарями, дети играют в войну. Управа собирает пожертвования и набирает носильщиков в армейский обоз.",
            "War with Qing China. The newspapers print color pictures of victories, the stations see the regiments off with lanterns, and the children play at war. The district office is collecting donations and hiring porters for the army's baggage train.") },
        opts = { o ->
            listOf(
                Opt(t("Пожертвовать", "Donate"), cost = 3, tag = t("3 мон", "3 mon"), fx = { money(-3); glory(2); t("Ваше имя напечатали в уездной газете, среди жертвователей.", "Your name was printed in the district paper, among the donors.") }),
                Opt(t("Наняться носильщиком в обоз", "Hire on as a porter"), roll = Roll(ZDR, -1, mapOf("bog" to 1, "hrab" to 1)),
                    ok = { money(6); glory(1); deed(o.h, t(sx(o.h, "Ходил", "Ходила") + " с армейским обозом в Китай", "Went to China with the army's baggage train")); t("Вернулись через год, с деньгами и с рассказами про Ляодун.", "You came back a year later with money and stories of Liaodong.") },
                    bad = { if (chance(.3)) { kill(o.h, t(sx(o.h, "умер", "умерла") + " от холеры в обозе", "died of cholera with the baggage train")); t("В обозе больше умирали от холеры, чем от пуль.", "More porters died of cholera than of bullets.") }
                        else { o.h[ZDR] -= 2; t("Вернулись живыми, но с лихорадкой, которая теперь приходит каждую весну.", "You came back alive, but with a fever that returns every spring.") } }),
                Opt(t("Жить своим", "Mind your own life"), fx = { t("Война далеко, а рис рядом.", "The war is far away, and the rice is close.") }),
            )
        }),
    Event("showa", "field_dry", Tx("Кризис Сёва", "The Showa Slump"), y0 = 1930, y1 = 1932, p = 1.0, cond = { yes },
        txt = { t("В Америке рухнула биржа, и шёлк больше никому не нужен: коконы втрое дешевле прошлогоднего, а рис дешевле семян. Газеты пишут, что в северных деревнях продают дочерей. К вам приехал вербовщик с прядильной фабрики и раскладывает на циновке аванс.",
            "The stock market has crashed in America, and nobody wants silk: cocoons fetch a third of last year's price, and rice is cheaper than seed. The papers say that in the northern villages people are selling their daughters. A recruiter from a spinning mill has come to you and is laying out an advance on the mat.") },
        opts = { o ->
            val l = mutableListOf<Opt>()
            kidIn(14, 22) { !it.male && it.spouse == null }.firstOrNull()?.let { d ->
                l.add(Opt(t("Отпустить ${d.name} на фабрику", "Let ${d.name} go to the mill"), tag = t("+6 мон", "+6 mon"),
                    fx = { money(6); d[ZDR]--; deed(d, t("Три года работала на прядильной фабрике", "Worked three years at a spinning mill")); t("${d.name} уехала в общежитие за высоким забором. Письма короткие, почерк чужой: пишет за неё грамотная подруга.", "${d.name} went off to a dormitory behind a high fence. Her letters are short, in someone else's hand: a friend who can write writes them for her.") }))
            }
            l.add(Opt(t("Перебиться редькой и долгами", "Get by on radishes and debt"), fx = { money(-part(.3)); t("Ели редьку, жмых и обещания. Никого не отдали.", "You lived on radish, oil cake and promises. You gave no one away.") }))
            l.add(Opt(t("Уйти на заработки в Осаку", "Go to Osaka for work"), fx = { addTr(o.h, "gor"); money(3); t("Ночлежка, подёнщина в порту, чашка лапши раз в день. Но домой каждый месяц уходит перевод.", "A doss-house, day work at the docks, a bowl of noodles a day. But money goes home every month.") }))
            l
        }),
    Event("kushu", "night_int", Tx("Налёты", "The Air Raids"), y0 = 1945, y1 = 1945, p = 1.0, cond = { yes },
        txt = { t("Весна сорок пятого. Серебряные бомбардировщики приходят по ночам, и город в долине сгорел за одну ночь: горели дома из дерева и бумаги, горела река. По дорогам идут погорельцы с узлами, а в деревенский храм привезли школьников из Токио — без родителей.",
            "Spring, 1945. The silver bombers come by night, and the town in the valley burned in a single night: houses of wood and paper burned, the river burned. Burned-out families walk the roads with their bundles, and schoolchildren from Tokyo have been brought to the village temple without their parents.") },
        opts = { o ->
            listOf(
                Opt(t("Взять к себе детей из храма", "Take in children from the temple"), cost = 3, tag = t("3 мон", "3 mon"),
                    fx = { money(-3); glory(3); deed(o.h, t(sx(o.h, "Приютил", "Приютила") + " детей из Токио в войну", "Took in Tokyo children in the war")); t("Двое мальчиков из Токио спят у вашего очага. Младший по ночам зовёт маму, старший делает вид, что не слышит.", "Two boys from Tokyo sleep by your hearth. The younger calls for his mother at night; the elder pretends not to hear.") }),
                Opt(t("Идти в сгоревший город искать родню", "Go into the burned town to find kin"), roll = Roll(ZDR, -1, mapOf("hrab" to 1, "dobr" to 1)),
                    ok = { glory(1); t("Двоюродную сестру нашли в школьном зале среди сотни таких же. Привели домой.", "You found your cousin in a school hall among a hundred others like her, and brought her home.") },
                    bad = { if (chance(.3)) { kill(o.h, t(sx(o.h, "погиб", "погибла") + " при налёте", "killed in an air raid")); t("Налёт повторился на вторую ночь.", "The raid came again on the second night.") }
                        else { addTr(o.h, "shram"); t("Вы вернулись с ожогами и без вестей.", "You came back burned, and with no news.") } }),
                Opt(t("Вырыть щель и ждать", "Dig a trench and wait"), fx = { t("До лета вы ночевали в щели за домом. Самолёты летели над деревней дальше — к большим городам.", "Till summer you slept in a trench behind the house. The planes flew on over the village, toward the big cities.") }),
            )
        }),
)
