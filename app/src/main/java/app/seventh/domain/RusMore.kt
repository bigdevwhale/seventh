package app.seventh.domain

import app.seventh.domain.Stat.HIT
import app.seventh.domain.Stat.SER
import app.seventh.domain.Stat.SIL
import app.seventh.domain.Stat.UM
import app.seventh.domain.Stat.VER
import app.seventh.domain.Stat.ZDR

/*
 * More of Rus: the head's own temper and age, chains that come back years later, rare folk wonders,
 * two ways off the plow (the peddler's pack and trade, the icon painters' craft) and more history.
 *
 * Marks (flags): zarok/zarok_by (a drunkard's vow), deserter, grain, agr_son, skit (set in Rus.kt), found/found_done (foundling),
 * debt, vow (set in Rus.kt «sick»), koz_saved/koz_hurt (the rival's boy), way_trade/guild, way_icon.
 */

private fun Engine.wifeOf(h: Person): Person? = if (!h.male) h else spouseOf(h)?.takeIf { alive(it) && !it.monk }

private fun Engine.fertile(h: Person): Boolean {
    val s = spouseOf(h) ?: return false
    return married(h) && h.away == null && !has(h, "bespl") && !has(s, "bespl")
}

private fun Engine.newborn(par: Person): Person {
    val c = birth(par)
    log(t("${par.name}: ${sx(c, "родился сын", "родилась дочь")} ${c.name}.", "${par.name}: a ${sx(c, "son", "daughter")}, ${c.name}, is born.") +
        (if (c.tr.isNotEmpty()) " (" + c.tr.joinToString(", ") { tname(it, c) } + ")" else ""), "birth")
    return c
}

/** A second marriage late in life: a widowed match of the head's own years. */
private fun Engine.lateMatch(h: Person): Person {
    val male = !h.male
    val s = reg(newP(pickName(male, setOf(h.name)), male, born = g.year - ri(48, 60), st = rstats(2, 7), from = pick(land.families)))
    s.grown = true; addTr(s, "vdov")
    s.spouse = h.id; h.spouse = s.id; s.wed = g.year; h.wed = g.year
    return s
}

private fun Engine.koz(): String? = if (flagged("koz_saved")) "koz_saved" else if (flagged("koz_hurt")) "koz_hurt" else null

private fun Engine.sonsGrown() = kidIn(18, 50) { it.male }

internal val rusMoreEvents: List<Event> = listOf(
    // ---------------------------------------------------------------- the head's temper
    Event("zarok", "tavern", Tx("Зарок", "The Pledge"), w = 2, cond = { h -> fire(has(h, "pyan") && age(h) >= 20 && !flagged("zarok") && !flagged("zarok_done")) },
        txt = { t("Проснулись в канаве у кабака — без шапки и без лошади. Лошадь, говорят, вы вчера сами и пропили: Кожемякину, за полведра.",
            "You woke in the ditch by the alehouse, without your cap and without your horse. The horse, they say, you drank away yourself last night: to Kozhemyakin, for half a bucket.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Дать зарок перед образом: год ни капли", "Swear before the icon: not a drop for a year"), fx = { flag("zarok"); flag("zarok_by", h.id)
                    t("Батюшка взял с вас слово при свидетелях и записал число на обороте святцев. Через год посмотрим.", "The priest took your word before witnesses and wrote the date on the back of the church calendar. A year from now we shall see.") }),
                Opt(t("Выкупить лошадь", "Buy the horse back"), cost = 5, tag = t("5 сер.", "5 silver"), fx = { money(-5); feud(1)
                    t("Кожемякин взял пять рублей за лошадь, доставшуюся ему за полведра. И улыбнулся.", "Kozhemyakin took five rubles for a horse he had got for half a bucket. And he smiled.") }),
                Opt(t("Похмелиться, а там видно будет", "Have a hair of the dog and see"), fx = { money(-2); glory(-1)
                    t("Где полведра, там и ведро. Лошадь теперь пашет у Кожемякиных, и вы её узнаёте издали.", "Where half a bucket goes, a whole one follows. The horse plows for the Kozhemyakins now, and you know her from afar.") }),
            )
        }),
    Event("zarok_end", "tavern", Tx("Год зарока", "The Year of the Pledge"), w = 10, cond = { h -> fire(flagged("zarok") && (since("zarok") ?: 0) >= 1 && g.flags["zarok_by"] == h.id) },
        txt = { t("Год зарока вышел день в день. Кум с утра стоит у ворот со штофом за пазухой: «Ну, теперь-то можно».",
            "The year of the pledge is up to the day. Your crony has been at the gate since morning with a flask in his coat: “Well, now you can.”") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Продлить зарок навек", "Make the pledge for life"), roll = Roll(VER, 0, mapOf("krot" to 1, "bogom" to 1)),
                    ok = { unflag("zarok"); unflag("zarok_by"); flag("zarok_done"); h.tr.remove("pyan"); glory(1); deed(h, t(sx(h, "Бросил", "Бросила") + " пить", "Gave up drink"))
                        t("Кум постоял, вздохнул и ушёл. Больше не приходил. В доме завелись деньги, а у детей — сапоги.", "Your crony stood a while, sighed and went away. He never came again. Money began to stay in the house, and the children got boots.") },
                    bad = { unflag("zarok"); unflag("zarok_by"); flag("zarok_done"); money(-3)
                        t("Навек вышло до Троицы. Потом кум принёс второй штоф.", "For life lasted until Trinity Sunday. Then your crony brought a second flask.") }),
                Opt(t("Выпить одну — за сдержанное слово", "Drink just one, to a promise kept"), fx = { unflag("zarok"); unflag("zarok_by"); flag("zarok_done"); money(-2)
                    t("Одна, как водится, потянула за собой остальных. Зато слово сдержано — целый год.", "One, as is the custom, pulled the rest along behind it. Still, the word was kept: a whole year.") }),
            )
        }),
    Event("gnev", "interior", Tx("Бредень", "The Drag Net"), w = 2, cond = { h -> val k = kidIn(10, 25); fire(if (has(h, "gnev") && k.isNotEmpty()) pick(k) else null) },
        txt = { o -> val x = o.xx; t("${x.name} упустил${sx(x, "", "а")} в реку ваш лучший бредень. Кровь ударила в голову, и в руке уже вожжи.",
            "${x.name} let your best drag net go down the river. The blood has rushed to your head, and the reins are already in your hand.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Проучить вожжами", "Teach ${sx(x, "him", "her")} with the reins"), sin = "cruel", fx = { x[SER] -= 1; if (chance(.4)) addTr(x, "zhest")
                    t("${x.name} не плакал${sx(x, "", "а")}. Только с тех пор глядит мимо вас.", "${x.name} did not cry. Only, since then, ${sx(x, "he", "she")} looks past you.") }),
                Opt(t("Выйти во двор и колоть дрова до темноты", "Go out and split wood until dark"), roll = Roll(VER, 0, mapOf("bogom" to 1)),
                    ok = { money(1); t("Накололи на всю зиму. Бредень потом выловили мальчишки ниже по реке и принесли за пятак.", "You split enough for the whole winter. The net was fished out by boys downstream, who brought it back for a five-kopeck piece.") },
                    bad = { addTr(o.h, "shram"); t("Топор соскочил с топорища. Палец так и не сросся как надо.", "The axe flew off its handle. The finger never did heal straight.") }),
                Opt(t("Послать нырять за ним", "Send ${sx(x, "him", "her")} diving after it"), roll = Roll(SIL, 0, ofX = true),
                    ok = { money(2); t("Достал${sx(x, "", "а")}. И сом в придачу запутался — на полпуда.", "${sx(x, "He", "She")} got it back. And a catfish of twenty pounds was tangled in it to boot.") },
                    bad = { x[ZDR] -= 1; t("Бредень не нашли. ${x.name} еле выплыл${sx(x, "", "а")} и кашлял${sx(x, "", "а")} до Покрова.", "The net was never found. ${x.name} barely swam out and coughed until Intercession Day.") }),
            )
        }),
    Event("pogorel", "winter", Tx("Погорельцы", "Burned Out"), w = 2, cond = { h -> fire(has(h, "dobr") && age(h) >= 18 && !flagged("pogorel")) },
        txt = { t("Погорельцы с того берега — мужик, баба и четверо ребят — просятся перезимовать. От их избы осталась одна печная труба.",
            "A burned-out family from across the river (a man, his wife and four children) ask to winter with you. Of their hut only the chimney is left.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Пустить до весны", "Take them in till spring"), tag = t("−4 сер.", "−4 silver"), fx = { money(-4); glory(2); flag("pogorel"); deed(h, t(sx(h, "Приютил", "Приютила") + " погорельцев", "Sheltered a burned-out family"))
                    if (chance(.5)) { g.bless = 1; t("Спали вповалку, ели из одного котла. Весной мужик поставил вам новый хлев, а баба оставила на пороге красную нитку. В следующем жребии — лишний белый боб.", "You slept side by side and ate from one pot. In spring the man built you a new cattle shed, and his wife left a red thread on the threshold. The next draw will hold an extra white bean.") }
                    else t("Спали вповалку, ели из одного котла. Весной они ушли ставить сруб, и вся деревня знает, кто их выручил.", "You slept side by side and ate from one pot. In spring they left to raise a new frame, and the whole village knows who saw them through.") }),
                Opt(t("Дать мешок муки и проводить", "Give them a sack of flour and see them off"), cost = 2, tag = t("2 сер.", "2 silver"), fx = { money(-2); glory(1)
                    t("Мужик поклонился в пояс и взвалил мешок на плечо. Ребятишки шли следом гуськом.", "The man bowed from the waist and shouldered the sack. The children followed in single file.") }),
                Opt(t("Отказать: самим тесно", "Refuse: you are cramped yourselves"), sin = "cruel", fx = { glory(-1)
                    t("Их пустили Кожемякины. Теперь об этом знает вся деревня, и Кожемякины ходят именинниками.", "The Kozhemyakins took them in. Now the whole village knows it, and the Kozhemyakins strut about like it was their name day.") }),
            )
        }),
    Event("kubyshka", "klad", Tx("Кубышка", "The Crock"), w = 2, cond = { h -> fire(has(h, "zhad") && g.money >= 6) },
        txt = { t("Серебро вы держите в кубышке под овином. Нынче ночью пошли пересчитать — а кубышки на месте нет. То ли сами перепрятали и забыли, то ли кто подглядел.",
            "You keep your silver in a crock under the drying barn. Last night you went to count it, and the crock is gone. Either you hid it somewhere else and forgot, or someone was watching.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Перекопать весь огород", "Dig up the whole garden"), roll = Roll(UM, 0, mapOf("umn" to 1)),
                    ok = { t("Нашлась под третьей яблоней. Вы сами перепрятали её на Пасху — и забыли.", "It turned up under the third apple tree. You moved it yourself at Easter, and forgot.") },
                    bad = { money(-part(.3)); t("Перекопали всё до самой межи. Кубышки нет, зато огород вспахан на славу.", "You dug all the way to the boundary. No crock, but the garden has never been so well turned.") }),
                Opt(t("Обвинить Кожемякиных", "Blame the Kozhemyakins"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 1)),
                    ok = { feud(1); money(3); t("Кожемякины, чтобы не срамиться на миру, сунули вам три рубля. Кубышка потом нашлась сама. Об этом вы промолчали.", "To avoid shame before the commune, the Kozhemyakins pressed three rubles on you. The crock turned up later on its own. You kept quiet about that.") },
                    bad = { feud(1); glory(-2); t("А кубышка нашлась под яблоней, при свидетелях. Над вами смеётся весь уезд.", "And then the crock turned up under the apple tree, in front of witnesses. The whole district is laughing at you.") }),
                Opt(t("Махнуть рукой", "Let it go"), fx = { money(-part(.25))
                    if (chance(.4)) { h.tr.remove("zhad"); t("Пропало — значит, пропало. И вдруг на душе стало легко, будто кубышку сняли с шеи.", "Gone is gone. And all at once your heart felt light, as if the crock had been hung around your neck all along.") }
                    else t("Пропало — значит, пропало. Новую кубышку вы закопали глубже и никому не сказали где.", "Gone is gone. The new crock you buried deeper and told no one where.") }),
            )
        }),
    Event("konoval", "barin", Tx("Заговорённый жеребец", "The Charmed Stallion"), w = 2, cond = { h -> fire(has(h, "lzh") && age(h) >= 18) },
        txt = { t("На ярмарке вы прихвастнули, что умеете заговаривать лошадей. Сегодня во двор привели ${if (g.year < 1861) "барского" else "купеческого"} жеребца: хромает, а конюх смотрит на вас с надеждой.",
            "At the fair you boasted that you can charm a sick horse well. Today they led the ${if (g.year < 1861) "barin's" else "merchant's"} stallion into your yard: it is lame, and the groom looks at you full of hope.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Пошептать и намазать дёгтем", "Mutter over it and daub it with tar"), roll = Roll(HIT, -1, mapOf("lzh" to 1, "umn" to 1)),
                    ok = { money(4); glory(1); t("Через неделю жеребец пошёл ровно. Шептали вы или нет, а лечит время. Четыре рубля за науку.", "A week later the stallion walked sound. Whether it was the muttering or not, time heals. Four rubles for your learning.") },
                    bad = { money(-4); glory(-1); t("Жеребец лёг и не встал. Платили вы.", "The stallion lay down and did not get up. You paid for it.") }),
                Opt(t("Сознаться, что приврали", "Own up that you were boasting"), fx = { glory(-1)
                    if (chance(.4)) { addTr(h, "chest"); t("Конюх сплюнул и увёл жеребца. А вы с того дня будто зареклись врать.", "The groom spat and led the stallion away. And from that day you seem to have sworn off lying.") }
                    else t("Конюх сплюнул и увёл жеребца к настоящему коновалу.", "The groom spat and led the stallion off to a real horse doctor.") }),
                Opt(t("Сказаться хворым", "Plead sickness"), sin = "coward", fx = { t("Три дня лежали на печи и охали. Конюх ушёл, но на ярмарке вас теперь зовут «Заговорённый».", "For three days you lay on the stove and groaned. The groom left, but at the fair they call you “the Charmer” now.") }),
            )
        }),
    Event("krestny", "field_dry", Tx("Крестный ход", "The Procession"), w = 2, cond = { h -> fire(has(h, "bogom") && age(h) >= 20) },
        txt = { t("Третью неделю ни капли. Мир просит вас — человека, хожалого по святым местам, — вести крестный ход вокруг полей.",
            "Three weeks without a drop. The commune asks you, who have walked to the holy places, to lead the procession of the cross around the fields.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Вести ход", "Lead the procession"), roll = Roll(VER, 1, mapOf("krot" to 1, "chest" to 1)),
                    ok = { glory(2); money(2); deed(h, t(sx(h, "Вымолил", "Вымолила") + " дождь", "Prayed down the rain")); t("На обратном пути всех накрыло ливнем. Шли домой мокрые, с хоругвями, и пели.", "On the way back a downpour caught everyone. You walked home soaked, with the banners, singing.") },
                    bad = { glory(-1); t("Пыль да солнце. Бабы косятся: не тот, видно, богомолец.", "Dust and sun. The women look sidelong: not the right sort of pilgrim, it seems.") }),
                Opt(t("Отказаться: не нам указывать небу", "Refuse: it is not for us to give orders to heaven"), fx = { t("Ход повёл дьячок. Дождь пошёл через неделю, сам по себе.", "The sexton led the procession. The rain came a week later, all on its own.") }),
            )
        }),
    Event("chelobit", "barin", Tx("Челобитная", "The Petition"), w = 2, cond = { h -> fire(has(h, "gram") && g.year < 1861 && age(h) >= 18) },
        txt = { t("Пол-деревни ходит к вам писать: кому прошение, кому письмо сыну в полк. Нынче мир просит составить челобитную на приказчика: ворует.",
            "Half the village comes to you to have things written: a petition, a letter to a son in the regiment. Today the commune asks you to draw up a complaint against the steward. He steals.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Написать и подписаться первым", "Write it and sign first"), roll = Roll(UM, -1, mapOf("hrab" to 1, "chest" to 1)),
                    ok = { glory(3); deed(h, t(sx(h, "Написал", "Написала") + " челобитную на приказчика", "Wrote the petition against the steward")); t("Приказчика сменили. Новый тоже ворует, но меньше и с оглядкой.", "The steward was replaced. The new one steals too, but less, and looking over his shoulder.") },
                    bad = { glory(1); addTr(h, "shram"); t("Приказчик узнал руку. Выпороли одного вас — остальные-то неграмотные.", "The steward knew the hand. Only you were flogged: the rest cannot write.") }),
                Opt(t("Написать без подписи", "Write it unsigned"), sin = "coward", roll = Roll(HIT, 0, mapOf("lzh" to 1)),
                    ok = { glory(1); t("Бумага ушла без имени. Приказчика сменили, а кто писал — неизвестно.", "The paper went off without a name. The steward was replaced, and who wrote it nobody knows.") },
                    bad = { glory(-1); t("Бумага вернулась к тому же приказчику. Почерк он узнал.", "The paper came back to the same steward. He knew the handwriting.") }),
                Opt(t("Отказаться", "Refuse"), fx = { t("Писал дьячок, с ошибками и кляксой. Бумагу никто не стал читать.", "The sexton wrote it, with mistakes and a blot. Nobody read it.") }),
            )
        }),
    Event("lapti", "village", Tx("Надел калеки", "The Cripple's Strip"), w = 2, cond = { h -> fire(has(h, "kalek")) },
        txt = { t("С больной ногой в поле не работник. Сход косится: не отписать ли ваш надел тому, кто его поднимет?",
            "With a bad leg you are no worker in the field. The commune mutters: why not give your strip to someone who can work it?") },
        opts = { o ->
            listOf(
                Opt(t("Плести лапти и короба на продажу", "Plait bast shoes and baskets to sell"), roll = Roll(UM, 1, mapOf("umn" to 1, "krot" to 1)),
                    ok = { money(4); t("Ваши лапти носят до дыр и просят ещё. Надел оставили: человек при деле.", "People wear your bast shoes into holes and ask for more. They left you the strip: the man has his trade.") },
                    bad = { money(-2); t("Лапти выходят кривые, короба — косые. Надел урезали.", "The shoes come out crooked and the baskets lopsided. Your strip was cut down.") }),
                Opt(t("Нанять батрака", "Hire a farmhand"), cost = 4, tag = t("4 сер.", "4 silver"), fx = { money(-4); t("Батрак пашет, вы считаете. Сход отстал.", "The hand plows, you keep the count. The commune let it be.") }),
                Opt(t("Отдать надел соседу исполу", "Let a neighbor work the strip for half"), fx = { money(-1); t("Сосед пашет ваш надел за половину урожая. Хлеба меньше, хлопот тоже.", "A neighbor works your strip for half the crop. Less bread, and less bother.") }),
            )
        }),
    Event("vdovy", "interior", Tx("Вдовий век", "A Widowed Age"), w = 2, cond = { h -> fire(has(h, "vdov") && !married(h) && age(h) in 50..72) },
        txt = { o -> val h = o.h; if (h.male) t("Одному в избе зябко. Вдовая просвирня Пелагея всё чаще заходит «на огонёк» — то с пирогом, то с кислыми щами.",
            "It is chilly in the hut alone. Pelageya, the widow who bakes the church bread, drops by more and more often, now with a pie, now with sour cabbage soup.")
        else t("Одной в избе зябко. Вдовый мельник Ермолай всё чаще заходит «на огонёк» — то с мешком муки, то починить крыльцо.",
            "It is chilly in the hut alone. Yermolai, the widowed miller, drops by more and more often, now with a sack of flour, now to mend the porch.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Обвенчаться", "Marry"), fx = { val s = lateMatch(h); glory(1)
                    t("Сыграли тихую свадьбу. Дети ворчали, внуки ели пироги. ${s.name} перевез${sx(s, "", "ла")} к вам свой сундук.", "A quiet wedding. The children grumbled, the grandchildren ate pies. ${s.name} moved ${sx(s, "his", "her")} trunk into your house.") }),
                Opt(t("Остаться верн${sx(h, "ым", "ой")} покойн${sx(h, "ой", "ому")}", "Stay true to the dead"), fx = { h[VER] += 1
                    t("Пироги брать можно, а под венец — поздно. По субботам вы ходите на кладбище, и там вам не зябко.", "A pie you can accept, but a wedding comes too late. On Saturdays you go to the churchyard, and there it is not chilly.") }),
            )
        }),
    Event("barsky", "barin", Tx("Барский глаз", "The Master's Eye"), w = 2,
        cond = { fire(if (g.year < 1861) kidIn(15, 22) { !it.male && has(it, "kras") && it.spouse == null }.firstOrNull() else null) },
        txt = { o -> val x = o.xx; t("Барин заметил ${x.name} на покосе. Приказчик заходил уже дважды: зовут в горничные, в барский дом.",
            "The barin noticed ${x.name} at the haymaking. The steward has called twice already: they want her for a housemaid up at the manor.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Спешно выдать замуж", "Marry her off in haste"), tag = t("−3 сер.", "−3 silver"), fx = { money(-3); val s = mkSpouse(x)
                    t("Через две недели обвенчали с ${s.name} из ${s.from}. Замужнюю в горничные не берут.", "Two weeks later she was wed to ${s.name} of ${s.from}. A married woman is not taken for a housemaid.") }),
                Opt(t("Отпустить в барский дом", "Let her go to the manor"), sin = "greed", fx = { money(5); glory(-1); addTr(x, "gor"); x[HIT] += 1
                    t("Барин прислал пять рублей. ${x.name} приходит по праздникам — в городских ботинках — и смотрит мимо.", "The barin sent five rubles. ${x.name} comes home on feast days in town boots, and looks past you.") }),
                Opt(t("Спрятать у тётки за болотом", "Hide her at her aunt's beyond the marsh"), roll = Roll(HIT, 0, mapOf("lzh" to 1)),
                    ok = { t("Приказчик походил-походил и забыл. К осени барин уехал в Петербург.", "The steward came round and round, then forgot. By autumn the barin had gone back to Petersburg.") },
                    bad = { money(-4); glory(-1); t("Нашли. Барин осерчал и прибавил оброку, а ${x.name} всё равно увезли.", "They found her. The barin grew angry and raised the quitrent, and ${x.name} was taken all the same.") }),
            )
        }),
    Event("koleso", "village", Tx("Колесо на ручье", "The Wheel on the Brook"), w = 2,
        cond = { fire(house().firstOrNull { has(it, "umn") && age(it) in 12..40 && it.away == null }) },
        txt = { o -> val x = o.xx; t("${x.name} смастерил${sx(x, "", "а")} на ручье колесо с черпаками: само воду на огород подаёт. Мужики ходят смотреть и крестятся.",
            "${x.name} has built a wheel with scoops on the brook: it lifts water to the garden all by itself. The men come to look, and cross themselves.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Ставить мельницу", "Build a mill"), cost = 8, tag = t("8 сер.", "8 silver"), roll = Roll(UM, 1, ofX = true),
                    ok = { money(-8); money(16); glory(2); deed(x, t(sx(x, "Поставил", "Поставила") + " мельницу на ручье", "Built a mill on the brook")); t("Мельница мелет на три деревни. Плата — гарнец с мешка, и мешки всё везут.", "The mill grinds for three villages. The fee is a measure from every sack, and the sacks keep coming.") },
                    bad = { money(-8); t("Весной мельницу снесло половодьем. Колесо нашли у Кожемякиных на огороде.", "The spring flood carried the mill away. The wheel was found in the Kozhemyakins' garden.") }),
                Opt(t("Отдать учиться к землемеру", "Send ${sx(x, "him", "her")} to learn from the land surveyor"), cost = 3, tag = t("3 сер.", "3 silver"), fx = { money(-3); addTr(x, "gram"); x[UM] += 1
                    t("${x.name} учится в уезде и приезжает по праздникам с циркулем в кармане.", "${x.name} studies in the district town and comes home on feast days with a pair of compasses in a pocket.") }),
                Opt(t("Разобрать: от бесовщины подальше", "Take it apart: keep clear of devilry"), fx = { x.stones += 1
                    t("Колесо пустили на дрова. ${x.name} ночью плакал${sx(x, "", "а")} на сеновале.", "The wheel went for firewood. ${x.name} cried in the hayloft that night.") }),
            )
        }),
    Event("kulachki", "winter", Tx("Стенка на стенку", "Wall Against Wall"), w = 2,
        cond = { fire(house().firstOrNull { has(it, "bog") && it.male && age(it) in 16..45 && it.away == null }) },
        txt = { o -> val x = o.xx; t("Масленица. На речном льду сходятся стенка на стенку: ваш конец против кожемякинского. Все ждут, когда выйдет ${x.name}.",
            "Shrovetide. On the river ice the two ends of the village line up wall against wall: yours against the Kozhemyakins'. Everyone is waiting for ${x.name} to step out.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Биться честно", "Fight fair"), roll = Roll(SIL, 1, ofX = true),
                    ok = { glory(2); deed(x, t("Сломал кожемякинскую стенку", "Broke the Kozhemyakins' wall")); t("Кожемякинская стенка побежала. Вечером ${x.name} поили на всём конце.", "The Kozhemyakins' wall broke and ran. That evening the whole end of the village stood ${x.name} drinks.") },
                    bad = { glory(1); addTr(x, "shram"); t("Свернули нос. Красоты поубавилось, славы — нет.", "A broken nose. Less handsome, no less famous.") }),
                Opt(t("Вложить в рукавицу свинчатку", "Slip a lead weight into the mitten"), sin = "lie", roll = Roll(HIT, 0, ofX = true),
                    ok = { glory(1); feud(2); flag("koz_hurt"); unflag("koz_saved"); t("Победа. Только младший Кожемякин неделю не вставал — и запомнил.", "Victory. Only the youngest Kozhemyakin did not get up for a week, and he remembered.") },
                    bad = { glory(-3); feud(1); t("Свинчатку вытряхнули из рукавицы при всём честном народе. Прогнали со льда.", "The lead was shaken out of the mitten before all the good people. Driven off the ice.") }),
                Opt(t("Не пустить: зашибёт кого", "Keep him out: he'll kill someone"), fx = { t("Ваш конец проиграл. ${x.name} весь вечер колол дрова — чтобы хоть что-нибудь побить.", "Your end lost. ${x.name} split wood all evening, just to have something to hit.") }),
            )
        }),
    // ---------------------------------------------------------------- ages of life
    Event("pokos", "field", Tx("Последний покос", "The Last Haymaking"), w = 2, cond = { h -> fire(age(h) >= 60 && h.away == null) },
        txt = { o -> val h = o.h; t("Вам за шестьдесят, а вы опять отбиваете косу. Сын отнимает: «${sx(h, "Тятя", "Мама")}, полежи на печи, без тебя скосим».",
            "You are past sixty, and here you are again, hammering the scythe blade. Your son takes it away: “${sx(h, "Father", "Mother")}, lie on the stove, we'll mow without you.”") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Косить наравне с молодыми", "Mow alongside the young ones"), roll = Roll(ZDR, 0, mapOf("bog" to 2, "dolg" to 2)),
                    ok = { glory(1); deed(h, t("В шестьдесят косил${sx(h, "", "а")} наравне с молодыми", "Mowed with the young ones at sixty")); t("Прошли ряд не хуже молодых. Вечером внуки щупали вам руки — железные ли.", "You cut a swath as well as the young ones. In the evening the grandchildren felt your arms to see if they were iron.") },
                    bad = { if (chance(.25)) { kill(h, t("надорвал" + sx(h, "ся", "ась") + " на покосе", "worn out at the haymaking")); t("Прошли полряда и легли в траву. Так и не встали.", "You cut half a swath and lay down in the grass. You never got up.") }
                        else { h[ZDR] -= 1; t("Прошли полряда, и в глазах потемнело. Домой довезли на телеге, на сене.", "Half a swath, and everything went dark. They brought you home in the cart, on the hay.") } }),
                Opt(t("Сесть на печь и учить внуков", "Sit on the stove and teach the grandchildren"), fx = { kidIn(3, 12).forEach { it[UM] += 1 }
                    t("К зиме внуки знали все сказки, а к весне — какую землю под рожь, а какую под лён.", "By winter the grandchildren knew all the tales, and by spring which ground is for rye and which for flax.") }),
            )
        }),
    Event("posidelki", "night_int", Tx("Посиделки", "The Evening Gathering"), w = 2, cond = { val k = kidIn(14, 17); fire(if (k.isEmpty()) null else pick(k)) },
        txt = { o -> val x = o.xx; if (x.male) t("${x.name} пропадает на посиделках до петухов, а нынче вернулся с подбитым глазом и довольный.",
            "${x.name} stays out at the evening gatherings till cockcrow, and tonight came home with a black eye, looking pleased.")
        else t("${x.name} пропадает на посиделках до петухов, а нынче вернулась с чужим колечком на пальце.",
            "${x.name} stays out at the evening gatherings till cockcrow, and tonight came home with somebody's ring on her finger.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Не ругать: сами молоды были", "No scolding: you were young once"), fx = { x[SER] += 1
                    t("Через неделю ${x.name} уже пел${sx(x, "", "а")} на всю улицу.", "Within a week ${x.name} was singing all down the street.") }),
                Opt(t("Посадить дома за работу", "Keep ${sx(x, "him", "her")} home at work"), fx = { x[UM] += 1; x.stones += 1
                    t(if (x.male) "Сидит, дуется, но сбрую починил всю, до последней пряжки." else "Сидит, дуется, но напряла за зиму на три рубахи.",
                        if (x.male) "Sits and sulks, but has mended the harness down to the last buckle." else "Sits and sulks, but has spun enough over the winter for three shirts.") }),
                Opt(t("Разузнать, в чём дело", "Find out what's what"), roll = Roll(HIT, 0, mapOf("umn" to 1)),
                    ok = { glory(1); t("Разузнали: дело молодое, парень и девка хорошие. Посмеялись и отпустили.", "You found out: young folks' business, the lad and the lass both decent. You laughed and let it go.") },
                    bad = { feud(1); t("Оказалось — Кожемякины. Слово за слово, и вот уже отцы стоят у плетня с кольями.", "It was the Kozhemyakins. One word led to another, and now the fathers stand at the wattle fence with stakes.") }),
            )
        }),
    Event("zabludil", "forest", Tx("Заблудились", "Lost in the Woods"), w = 2, cond = { val k = kidIn(4, 9); fire(if (k.isEmpty()) null else pick(k)) },
        txt = { o -> val x = o.xx; t("${x.name} ${sx(x, "ушёл", "ушла")} с бабами по грибы и не вернул${sx(x, "ся", "ась")}. Солнце садится за ельник.",
            "${x.name} went mushrooming with the women and did not come back. The sun is setting behind the firs.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Всей деревней — с факелами, аукать", "The whole village, with torches, calling out"), roll = Roll(SER, 1, mapOf("dobr" to 1, "kras" to 1)),
                    ok = { glory(1); t("Нашли под елью — спит, в обнимку с лукошком рыжиков.", "They found ${sx(x, "him", "her")} under a fir, asleep, hugging a basket of saffron milk caps.") },
                    bad = { x[ZDR] -= 1; t("Нашли под утро, ${sx(x, "озябшего", "озябшую")} до синевы. Кашель не проходит до сих пор.", "They found ${sx(x, "him", "her")} toward morning, blue with cold. The cough has not gone yet.") }),
                Opt(t("Вывернуть рубахи наизнанку — от лешего", "Turn your shirts inside out against the wood spirit"), roll = Roll(VER, 1, mapOf("bogom" to 1)),
                    ok = { t("Только вывернули рубахи — ${x.name} сам${sx(x, "", "а")} выш${sx(x, "ел", "ла")} к околице: «Дедушка с бородой вывел».", "No sooner were the shirts turned than ${x.name} walked out to the village fence alone: “A grandfather with a beard showed me the way.”") },
                    bad = { x.stones += 1; t("Леший так просто не отдал. Нашли через два дня у самого болота — жив${sx(x, "ого", "ую")}, но молчит до сих пор.", "The wood spirit would not give ${sx(x, "him", "her")} up so easily. ${sx(x, "He", "She")} was found two days later at the very edge of the bog, alive, and still will not talk about it.") }),
            )
        }),
    Event("slovo", "interior", Tx("Первое слово", "The First Word"), cond = { val k = kidIn(1, 2); fire(if (k.isEmpty()) null else pick(k)) },
        txt = { o -> val x = o.xx; t("${x.name} сказал${sx(x, "", "а")} первое слово. Не «мама» и не «тятя», а — ясно и с чувством — «Кожемякин».",
            "${x.name} has said ${sx(x, "his", "her")} first word. Not “mama” and not “papa,” but, clearly and with feeling, “Kozhemyakin.”") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Смеяться всей избой", "The whole hut laughs"), fx = { x[SER] += 1; t("Смеялись до слёз. Даже кот вышел посмотреть.", "You laughed till you cried. Even the cat came out to look.") }),
                Opt(t("Отнести к батюшке: не к добру", "Take the child to the priest: it bodes no good"), cost = 1, tag = t("1 сер.", "1 silver"), fx = { money(-1); x[VER] += 1
                    t("Батюшка окропил, подумал и сказал, что дитя, видно, будет миротворцем.", "The priest sprinkled holy water, thought a while and said the child will be a peacemaker, it seems.") }),
                Opt(t("Сходить к Кожемякиным, рассказать", "Go and tell the Kozhemyakins"), roll = Roll(SER, 0, mapOf("kras" to 1, "dobr" to 1)),
                    ok = { feud(-1); t("Старый Кожемякин хохотал до кашля и прислал ребёнку медовый пряник.", "Old Kozhemyakin laughed till he coughed and sent the child a honey cake.") },
                    bad = { feud(1); t("Кожемякины решили, что вы над ними смеётесь.", "The Kozhemyakins decided you were mocking them.") }),
            )
        }),
    Event("poskreb", "night_int", Tx("Поскрёбыш", "The Last Scrapings"), w = 2,
        cond = { h -> val w = wifeOf(h); fire(if (w != null && fertile(h) && age(w) in 40..46 && g.year - w.last >= 2 && chance(.35)) w else null) },
        txt = { o -> val w = o.xx; t("Думали, отрожались. А ${w.name} снова в тягости — в ${yrs(age(w))}. Бабы шепчутся: поскрёбыш будет либо счастливый, либо хворый.",
            "You thought those days were over. But ${w.name} is with child again, at ${age(w)}. The women whisper: the last scrapings of the bowl are either lucky or sickly.") },
        opts = { o ->
            val h = o.h
            val w = o.xx
            listOf(
                Opt(t("Звать повитуху из соседнего села", "Send for the midwife from the next village"), cost = 2, tag = t("2 сер.", "2 silver"), roll = Roll(ZDR, 2, ofX = true),
                    ok = { money(-2); val c = newborn(h); t("${sx(c, "Родился", "Родилась")} ${c.name} — крепк${sx(c, "ий", "ая")}, горласт${sx(c, "ый", "ая")}. Старшие дети ходят смотреть, как на чудо.", "${c.name} is born, strong and loud. The older children come to look, as at a wonder.") },
                    bad = { money(-2); val c = newborn(h); addTr(c, "hvor"); t("${sx(c, "Родился", "Родилась")} ${c.name} — слабеньк${sx(c, "ий", "ая")}, еле пищит. Выхаживали всю зиму.", "${c.name} is born, a weak little thing that barely squeaks. You nursed the baby all winter.") }),
                Opt(t("Молиться Параскеве Пятнице", "Pray to St Paraskeva"), roll = Roll(VER, 0, mapOf("bogom" to 1, "krot" to 1)),
                    ok = { val c = newborn(h); t("${sx(c, "Родился", "Родилась")} ${c.name} — в пятницу, как по заказу.", "${c.name} is born, on a Friday, as if ordered.") },
                    bad = { val c = newborn(h); if (chance(.3)) { kill(w, t("родильная горячка", "childbed fever")); t("${sx(c, "Родился", "Родилась")} ${c.name}. Мать не встала после родов.", "${c.name} is born. The mother never rose from childbed.") }
                        else { addTr(c, "hvor"); t("${sx(c, "Родился", "Родилась")} ${c.name}, синеньк${sx(c, "ий", "ая")}. Выходили, но здоровья бог не дал.", "${c.name} is born, blue as a plum. You pulled the baby through, but God gave no great health.") } }),
            )
        }),
    Event("dvoinya", "night_int", Tx("Двойня", "Twins"), w = 2,
        cond = { h -> val w = wifeOf(h); fire(if (w != null && fertile(h) && age(w) in 18..36 && g.year - w.last >= 2 && chance(.25)) w else null) },
        txt = { t("Повитуха выходит из бани с двумя свёртками: «Двойня! Кормить вдвое, крестить вдвое, а счастья, говорят, тоже вдвое».",
            "The midwife comes out of the bathhouse with two bundles: “Twins! Twice the feeding, twice the christening, and twice the luck, they say.”") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Звать в кумовья мельника", "Ask the miller to stand godfather"), roll = Roll(SER, 0, mapOf("kras" to 1, "shch" to 1)),
                    ok = { val a = newborn(h); val b = newborn(h); money(4); glory(1); t("${a.name} и ${b.name}. Мельник положил каждому на зубок по два рубля серебром.", "${a.name} and ${b.name}. The miller gave each a christening gift of two silver rubles.") },
                    bad = { val a = newborn(h); val b = newborn(h); t("${a.name} и ${b.name}. Мельник отговорился, пришлось звать пастуха. Пастух, впрочем, был трезв и рад.", "${a.name} and ${b.name}. The miller made excuses, so you asked the herdsman. The herdsman, though, was sober and glad.") }),
                Opt(t("Крестить тихо, в своей церкви", "A quiet christening in your own church"), cost = 1, tag = t("1 сер.", "1 silver"), fx = { money(-1); val a = newborn(h); val b = newborn(h)
                    t("${a.name} и ${b.name}. Одна люлька на двоих, и кричат по очереди.", "${a.name} and ${b.name}. One cradle for the two, and they take turns crying.") }),
            )
        }),
    Event("dochka", "night_int", Tx("Ушла от мужа", "She Left Her Husband"), w = 2, cond = { fire(kidIn(17, 40) { !it.male && married(it) }.firstOrNull()) },
        txt = { o -> val x = o.xx; val s = spouseOf(x); t("${x.name} прибежала ночью, в одной рубахе, с синяком под глазом: «Не вернусь к нему, хоть убейте». К обеду следом явился муж${if (s != null) ", " + s.name else ""} — трезвый и тихий.",
            "${x.name} came running in the night in nothing but her shift, with a bruise under her eye: “I won't go back to him if you kill me.” By noon the husband${if (s != null) ", " + s.name + "," else ""} turned up after her, sober and quiet.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Оставить дочь дома", "Keep your daughter at home"), fx = { glory(-1); x[SER] += 1
                    t("По деревне судачат: от мужа ушла. Пусть судачат. ${x.name} снова поёт за прялкой.", "The village gossips: she left her husband. Let them gossip. ${x.name} sings at the spinning wheel again.") }),
                Opt(t("Вернуть мужу: стерпится", "Send her back: she'll get used to it"), sin = "cruel", fx = { x[ZDR] -= 1; x.stones += 1
                    t("${x.name} ушла, не оглядываясь. С тех пор приходит только на Пасху.", "${x.name} left without looking back. She only comes at Easter now.") }),
                Opt(t("Поговорить с зятем", "Have a word with the son-in-law"), roll = Roll(SER, 0, mapOf("hrab" to 1, "bog" to 1, "gnev" to 1)),
                    ok = { t("Зять божился на образах. Говорят, с тех пор пальцем её не трогает.", "The son-in-law swore on the icons. They say he has not laid a finger on her since.") },
                    bad = { x[ZDR] -= 1; t("Зять выслушал, покивал и вечером напился снова. ${x.name} вернулась к нему сама — куда ж деваться.", "The son-in-law listened, nodded, and got drunk again that evening. ${x.name} went back to him herself: where else could she go?") }),
            )
        }),
    Event("razdel", "meja", Tx("Раздел", "Dividing the Farm"), w = 2, cond = { val l = sonsGrown(); fire(if (l.size >= 2) l[0] else null) },
        txt = { o -> val a = o.xx; val b = sonsGrown().firstOrNull { it.id != a.id }; t("${a.name} и ${b?.name ?: "младший"} не поделили покос у реки — сцепились прямо на меже. Бабы растаскивают, а все глядят на вас: делить ли двор?",
            "${a.name} and ${b?.name ?: "the younger one"} could not settle the meadow by the river and came to blows right on the boundary. The women pull them apart, but everyone is looking at you: is the farm to be divided?") },
        opts = { o ->
            val a = o.xx
            val b = sonsGrown().firstOrNull { it.id != a.id } ?: a
            listOf(
                Opt(t("Разделить двор", "Divide the farm"), fx = { money(-part(.3))
                    t("Поставили вторую избу через забор. Ругаться стали реже: далеко кричать.", "A second hut went up on the other side of the fence. They quarrel less now: too far to shout.") }),
                Opt(t("Жить одним домом, а слово — моё", "One house, and my word is law"), roll = Roll(SER, 0, mapOf("gnev" to 1, "krot" to 1, "kras" to 1)),
                    ok = { glory(1); t("Стукнули кулаком по столу, и сыновья помирились. Покос косят вместе, по очереди ругая погоду.", "You banged your fist on the table and the sons made it up. They mow the meadow together, taking turns to curse the weather.") },
                    bad = { if (b !== a) g.grudge = b.id; t("Помирились на словах. ${b.name} затаил обиду — не на брата, а на вас.", "They made it up in words. ${b.name} bears a grudge: not against his brother, but against you.") }),
                Opt(t("Бросить жребий шапкой", "Draw lots from a cap"), fx = { val l = if (chance(.5)) a else b; addTr(l, "gor"); money(-2)
                    t("Жребий выпал ${l.name}: уходить. Он ушёл в город на заработки и, кажется, не жалеет.", "The lot fell on ${l.name} to go. He went to town to earn his bread, and does not seem sorry.") }),
            )
        }),
    // ---------------------------------------------------------------- chains: the past comes back
    Event("soldat", "road", Tx("Старый знакомый", "An Old Acquaintance"), w = 8, cond = { fire(flagged("deserter") && (since("deserter") ?: 0) >= 8) },
        txt = { t("К воротам подъезжает телега, на ней — седой человек в суконном кафтане. «Не признали? Я тот солдат, что зимовал у вас в овине».",
            "A cart pulls up at the gate, and in it a gray-haired man in a broadcloth coat. “Don't you know me? I'm the soldier who wintered in your drying barn.”") },
        opts = { o ->
            listOf(
                Opt(t("Позвать в избу, накрыть стол", "Ask him in and set the table"), roll = Roll(SER, 1, mapOf("dobr" to 1, "shch" to 1)),
                    ok = { unflag("deserter"); flag("deserter_done"); money(10); glory(1); t("Он теперь подрядчик, гоняет барки по Волге. Оставил десять рублей — «за овин и за хлеб».", "He is a contractor now, running barges on the Volga. He left ten rubles: “for the barn and the bread.”") },
                    bad = { unflag("deserter"); flag("deserter_done"); money(-part(.3)); t("Ночью он уехал, а с ним — серебро из-под половицы. Видно, лес из него так и не вышел.", "In the night he drove off, and your silver from under the floorboard went with him. The forest never quite left him, it seems.") }),
                Opt(t("Не пускать: мало ли кем он стал", "Keep him out: who knows what he's become"), fx = { unflag("deserter"); flag("deserter_done")
                    t("Он постоял, кивнул и уехал. Наутро на воротах висел новый замок — подарок, и ключ в нём.", "He stood a while, nodded and drove off. In the morning a new padlock hung on the gate, a gift, with the key in it.") }),
            )
        }),
    Event("ambar", "barin", Tx("Амбарная книга", "The Granary Book"), w = 8, cond = { fire(flagged("grain") && (since("grain") ?: 0) >= 4 && g.year < 1870) },
        txt = { t("Приказчик сверял старые амбарные книги и досчитался: в тот голодный год недоставало трёх мешков. Ищет, кто брал.",
            "The steward was checking the old granary books and has counted it out: in that hungry year three sacks were missing. He is looking for who took them.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Повиниться и отработать", "Confess and work it off"), fx = { unflag("grain"); flag("grain_done"); money(-4); if (h.stones > 0) h.stones -= 1
                    t("Три лета по лишнему дню на барском поле. Зато чисто — и спится лучше.", "Three summers of an extra day in the master's field. But it's clean now, and you sleep better.") }),
                Opt(t("Свалить на мышей и сырость", "Blame the mice and the damp"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { unflag("grain"); flag("grain_done"); t("Приказчик записал: «мышами погрызено». Мыши спорить не стали.", "The steward wrote: “gnawed by mice.” The mice did not argue.") },
                    bad = { unflag("grain"); flag("grain_done"); addTr(h, "shram"); glory(-2); t("Мыши, оказалось, мешков не носят. Высекли на конюшне.", "Mice, it turns out, do not carry sacks. You were flogged in the stables.") }),
                Opt(t("Поднести приказчику", "Grease the steward's palm"), cost = 5, tag = t("5 сер.", "5 silver"), fx = { unflag("grain"); flag("grain_done"); money(-5)
                    t("Приказчик перевернул страницу и больше к ней не возвращался.", "The steward turned the page and never turned it back.") }),
            )
        }),
    Event("agrsyn", "road", Tx("Сын вдовы", "The Widow's Son"), w = 8, cond = { h -> fire(flagged("agr_son") && (since("agr_son") ?: 0) >= 16 && h.male && age(h) < 75) },
        txt = { t("Сын вдовы Аграфены вырос. Пришёл к воротам — плечистый, с глазами вашего рода: «Признайте меня. Мне не надела надо — имя».",
            "The widow Agrafena's son is grown. He came to the gate, broad in the shoulder, with your family's eyes: “Own me. I don't want land. I want a name.”") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Признать при всём мире", "Own him before the whole commune"), fx = {
                    val n = since("agr_son") ?: 16
                    unflag("agr_son"); flag("agr_son_done"); glory(-1)
                    val c = reg(newP(pickName(true, (kidsOf(h).map { it.name } + h.name).toSet()), true, born = g.year - n, blood = true, gen = h.gen + 1, father = h.id, bast = true,
                        st = IntArray(6) { (h.st[it] + ri(-2, 1)).coerceIn(1, 9) }))
                    grow(c, h)
                    log(t("${c.name}, сын вдовы Аграфены, признан и взят в род.", "${c.name}, the widow Agrafena's son, was owned and taken into the family."), "birth")
                    t("${c.name} ${surname(c)}. Он поклонился в землю — сначала вам, потом образам. ${wifeOf(h)?.let { it.name + " промолчала. " } ?: ""}Вся деревня смотрела.", "${c.name} ${surname(c)}. He bowed to the ground, first to you, then to the icons. ${wifeOf(h)?.let { it.name + " said nothing. " } ?: ""}The whole village watched.") }),
                Opt(t("Дать денег и проститься", "Give him money and say goodbye"), cost = 5, tag = t("5 сер.", "5 silver"), fx = { unflag("agr_son"); flag("agr_son_done"); money(-5)
                    t("Он взял, поклонился и ушёл бурлачить на Волгу. Глаза у него были сухие.", "He took it, bowed, and went off to haul barges on the Volga. His eyes were dry.") }),
                Opt(t("Прогнать", "Drive him away"), sin = "cruel", fx = { unflag("agr_son"); flag("agr_son_done"); feud(1)
                    t("Он нанялся в работники к Кожемякиным. Там рады всякому, кто на вас зол.", "He hired himself out to the Kozhemyakins. They welcome anyone who bears you a grudge.") }),
            )
        }),
    Event("naiden", "church", Tx("Подкидыш", "The Foundling"), cond = { h -> fire(!flagged("found") && !flagged("found_done") && age(h) >= 20) },
        txt = { t("На паперти после заутрени — корзина, а в ней младенец в чистой рубашонке и записка: «Крещён. Наречён Никифором».",
            "On the church porch after matins there is a basket, and in it a baby in a clean little shirt with a note: “Baptized. Named Nikifor.”") },
        opts = { o ->
            listOf(
                Opt(t("Взять в дом", "Take him home"), tag = t("−2 сер.", "−2 silver"), fx = { money(-2); glory(1); flag("found")
                    t("Никифора кормили всей избой. ${wifeOf(o.h)?.let { it.name + " ворчала, но первой вставала к люльке." } ?: "Ворчали, но к люльке вставали по очереди."}",
                        "The whole hut fed Nikifor. ${wifeOf(o.h)?.let { it.name + " grumbled, but was first up to the cradle." } ?: "Everyone grumbled, but took turns at the cradle."}") }),
                Opt(t("Отнести в монастырь", "Take him to the convent"), fx = { flag("found_done", 0)
                    t("Монахини приняли корзину вместе с запиской и перекрестили вас на дорогу.", "The nuns took the basket and the note, and blessed you for the road.") }),
                Opt(t("Пройти мимо", "Walk on by"), sin = "cruel", fx = { flag("found_done", 0)
                    t("К обеду корзину забрала просвирня. А вы потом долго слышали тот писк.", "By noon the woman who bakes the church bread had taken the basket. And you heard that squeaking for a long while after.") }),
            )
        }),
    Event("naiden2", "village", Tx("Никифор вырос", "Nikifor Grown"), w = 8, cond = { fire(flagged("found") && (since("found") ?: 0) >= 16) },
        txt = { t("Подкидыш Никифор вырос — тихий, работящий, вам как родной. Нынче стоит у печи, мнёт шапку: хочет своей жизни.",
            "Nikifor the foundling is grown: quiet, hard-working, as good as your own. Today he stands by the stove, twisting his cap: he wants a life of his own.") },
        opts = { o ->
            listOf(
                Opt(t("Отделить и помочь обзавестись", "Set him up on his own"), cost = 6, tag = t("6 сер.", "6 silver"), fx = { unflag("found"); flag("found_done", 0); money(-6); glory(2); g.bless = 1
                    t("Никифор поставил избу на краю деревни и в первый же праздник принёс вам каравай. В следующем жребии — лишний белый боб.", "Nikifor raised a hut at the end of the village and on the very first feast day brought you a loaf. The next draw will hold an extra white bean.") }),
                Opt(t("Оставить при доме", "Keep him with the family"), fx = { unflag("found"); flag("found_done", 0); money(4)
                    t("Никифор остался. Работает за двоих и зовёт вас как родных.", "Nikifor stayed. He works for two and calls you his own folk.") }),
                Opt(t("Отпустить в город", "Let him go to town"), fx = { unflag("found"); flag("found_done", 0)
                    if (chance(.5)) { money(6); t("Через пять лет пришло письмо: Никифор — приказчик в Ярославле. В письме — шесть рублей.", "Five years later a letter came: Nikifor is a shop steward in Yaroslavl. There were six rubles in the letter.") }
                    else t("Он ушёл с обозом. Каждую Пасху приходит письмо, неровным почерком, но приходит.", "He left with a wagon train. Every Easter a letter comes, in a shaky hand, but it comes.") }),
            )
        }),
    Event("dolg", "village", Tx("В рост", "At Interest"), cond = { h -> fire(!flagged("debt") && !flagged("debt_done") && age(h) >= 18) },
        txt = { t("Лошадь пала перед самой пахотой. Старший Кожемякин тут как тут, даёт в рост: двадцать рублей сейчас, тридцать — через пять лет. Под расписку, при свидетелях.",
            "The horse dropped dead just before the plowing. The eldest Kozhemyakin is right there, lending at interest: twenty rubles now, thirty in five years. On a signed note, before witnesses.") },
        opts = { o ->
            listOf(
                Opt(t("Взять", "Take it"), tag = t("+20 сер. · долг", "+20 silver · a debt"), fx = { money(20); flag("debt")
                    t("Двадцать рублей серебром. Кожемякин улыбался так, что ныли зубы.", "Twenty rubles in silver. Kozhemyakin smiled in a way that made your teeth ache.") }),
                Opt(t("Не брать", "Don't take it"), fx = { t("Обойдётесь. Как-нибудь. Лебеда тоже трава.", "You'll manage. Somehow. Goosefoot is grass too.") }),
            )
        }),
    Event("dolg2", "interior", Tx("Срок вышел", "The Note Falls Due"), w = 8, cond = { fire(flagged("debt") && (since("debt") ?: 0) >= 5) },
        txt = { t("Пять лет прошли. Кожемякин на пороге, с распиской: «Тридцать рублей. Или покос у реки».",
            "Five years have gone by. Kozhemyakin is on the doorstep with the note: “Thirty rubles. Or the meadow by the river.”") },
        opts = { o ->
            listOf(
                Opt(t("Отдать тридцать", "Pay the thirty"), cost = 30, tag = t("30 сер.", "30 silver"), fx = { unflag("debt"); flag("debt_done"); money(-30); feud(-1)
                    t("Кожемякин пересчитал дважды и, кажется, огорчился.", "Kozhemyakin counted it twice and seemed, if anything, disappointed.") }),
                Opt(t("Отдать покос", "Give up the meadow"), fx = { unflag("debt"); flag("debt_done"); feud(1); glory(-1); if (g.dvor > 0) g.dvor -= 1 else money(-part(.5))
                    t("Покос у реки теперь кожемякинский. Каждый сенокос — как нож.", "The meadow by the river belongs to the Kozhemyakins now. Every haymaking cuts like a knife.") }),
                Opt(t("«Расписка-то… сгорела»", "“That note… it burned”"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 2)),
                    ok = { unflag("debt"); flag("debt_done"); feud(2); t("Кожемякин побагровел. Свидетели, как назло, померли оба.", "Kozhemyakin went purple. Both witnesses, as luck would have it, had died.") },
                    bad = { unflag("debt"); flag("debt_done"); money(-part(.6)); glory(-2); feud(1); t("Список с расписки лежал у писаря. Суд взыскал вдвое.", "The clerk had a copy of the note. The court took twice the sum.") }),
            )
        }),
    Event("obet", "dream", Tx("Неисполненный обет", "A Vow Unkept"), w = 8, cond = { h -> fire(flagged("vow") && (since("vow") ?: 0) >= 3 && age(h) >= 16) },
        txt = { t("Дитя, за которое давали обет, давно бегает босиком. А до Соловков так никто и не дошёл. Ночью снилось: монах стучит посохом в окно.",
            "The child the vow was made for has long been running about barefoot. But nobody ever walked to Solovki. Last night you dreamed a monk was knocking at the window with his staff.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Идти на Соловки", "Walk to Solovki"), roll = Roll(ZDR, 1, mapOf("bogom" to 1, "bog" to 1)),
                    ok = { unflag("vow"); flag("vow_done"); addTr(h, "bogom"); h.stones = 0; glory(2); deed(h, t(sx(h, "Сходил", "Сходила") + " пешком на Соловки", "Walked to Solovki"))
                        if (chance(.5)) addRelic(t("Соловецкий крестик", "Cross from Solovki"), "crossr", mapOf(VER to 1), t("${h.name}: принесён по обету", "${h.name}: brought back for a vow"))
                        t("Два месяца пешком, неделя по Белому морю. Вернулись налегке, будто полжизни скинули.", "Two months on foot, a week across the White Sea. You came back light, as if you had shed half a life.") },
                    bad = { unflag("vow"); flag("vow_done"); h[ZDR] -= 1; t("Дошли до Онеги и слегли в горячке. Монахи сказали: Бог видел и полпути.", "You got as far as Onega and fell sick with fever. The monks said God saw the half you walked.") }),
                Opt(t("Откупиться вкладом в монастырь", "Pay it off with a gift to the monastery"), cost = 6, tag = t("6 сер.", "6 silver"), fx = { unflag("vow"); flag("vow_done"); money(-6)
                    t("Монах взял деньги и сказал, что так тоже можно. Сны стали тише.", "The monk took the money and said that would do too. The dreams grew quieter.") }),
                Opt(t("Забыть", "Forget it"), fx = { unflag("vow"); flag("vow_done"); g.hex = g.year + 4
                    t("Вы забыли. Обет — нет. Четыре года в мешке лишний красный боб.", "You forgot. The vow did not. For four years a second red bean lies in the bag.") }),
            )
        }),
    Event("zaprud", "field", Tx("На запруде", "At the Millpond"), cond = { h -> fire(koz() == null && !flagged("koz_done") && g.feud >= 1 && age(h) in 16..60) },
        txt = { t("На мельничной запруде тонет мальчишка — младший Кожемякин. Отец его на том берегу, сапоги снять не успевает.",
            "A boy is drowning in the millpond: the youngest Kozhemyakin. His father is on the far bank and hasn't even got his boots off.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Прыгнуть в воду", "Jump in"), roll = Roll(SIL, 0, mapOf("hrab" to 2, "bog" to 1, "trus" to -2)),
                    ok = { flag("koz_saved"); feud(-1); glory(2); deed(h, t(sx(h, "Вытащил", "Вытащила") + " из запруды кожемякинского сына", "Pulled the Kozhemyakin boy out of the millpond"))
                        t("Вытащили за вихор. Старый Кожемякин стоял мокрый по пояс и не знал, куда девать руки.", "You hauled him out by the forelock. Old Kozhemyakin stood soaked to the waist, not knowing what to do with his hands.") },
                    bad = { h[ZDR] -= 1; t("Вытащили вас обоих мужики с мельницы. Вы откашливались до вечера, а мальчишка — уже к обеду.", "The men from the mill pulled you both out. You coughed till evening; the boy was fine by noon.") }),
                Opt(t("Кинуть жердь и звать мельника", "Throw him a pole and shout for the miller"), roll = Roll(UM, 0),
                    ok = { flag("koz_saved"); feud(-1); t("Мальчишка вцепился в жердь, а вы тянули, пока не подоспели. Кожемякин потом прислал меду.", "The boy grabbed the pole and you pulled until help came. Kozhemyakin sent honey afterward.") },
                    bad = { t("Жердь не доставала. Мальчишку вытащил отец. На вас он даже не взглянул.", "The pole fell short. The father got the boy out himself. He did not even glance at you.") }),
                Opt(t("Не лезть: Кожемякины сами", "Stay out of it: the Kozhemyakins can manage"), sin = "coward", fx = { flag("koz_hurt")
                    t("Мальчишку откачали. Он видел, как вы стояли на берегу и смотрели.", "They pumped the water out of him. He saw you standing on the bank, watching.") }),
            )
        }),
    Event("starosta", "village", Tx("Передел", "The Redivision"), w = 8, cond = { val k = koz(); fire(k != null && (since(k) ?: 0) >= 12) },
        txt = { if (flagged("koz_saved")) t("Младший Кожемякин вырос и стал старостой — тот самый, которого тащили из запруды. Нынче в общине передел земли.",
            "The youngest Kozhemyakin is grown and has become village elder: the very one who was pulled out of the millpond. Today the commune redivides the land.")
        else t("Младший Кожемякин вырос и стал старостой — тот самый, что не забыл. Нынче в общине передел земли.",
            "The youngest Kozhemyakin is grown and has become village elder: the very one who did not forget. Today the commune redivides the land.") },
        opts = { o ->
            val good = flagged("koz_saved")
            listOf(
                Opt(t("Просить лучшую полосу", "Ask for the best strip"), roll = Roll(SER, if (good) 3 else -3, mapOf("kras" to 1)),
                    ok = { unflag("koz_saved"); unflag("koz_hurt"); flag("koz_done"); money(5); glory(1); if (good) feud(-1)
                        t("Полоса у реки, жирная, без камня. Староста отмерил сам, шагами.", "A strip by the river, rich and free of stones. The elder paced it out himself.") },
                    bad = { unflag("koz_saved"); unflag("koz_hurt"); flag("koz_done"); money(-3)
                        t("Вам досталась полоса у болота, вся в кочках. Староста развёл руками: жребий.", "You got a strip by the bog, all tussocks. The elder spread his hands: that's the lot.") }),
                Opt(t("Не просить ничего", "Ask for nothing"), fx = { unflag("koz_saved"); unflag("koz_hurt"); flag("koz_done")
                    if (good) { feud(-2); money(3); t("Он сам дал вам лучшую полосу. «Долг платежом красен», — сказал и ушёл.", "He gave you the best strip himself. “One good turn,” he said, and walked off.") }
                    else { money(-2); t("Дали полосу у болота. Он помнит.", "You got a strip by the bog. He remembers.") } }),
            )
        }),
    Event("skit", "forest", Tx("Старец из скита", "The Elder from the Hermitage"), w = 8, cond = { fire(flagged("skit") && (since("skit") ?: 0) >= 3) },
        txt = { t("Ночью пришёл старец из того самого скита, где ваши когда-то пересидели набор: «Едут солдаты жечь обитель. Спрячьте книги — старые, дониконовские».",
            "In the night the elder came from that very hermitage where your people once sat out the levy: “Soldiers are coming to burn our cloister. Hide the books: the old ones, from before Nikon.”") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Спрятать книги в подполе", "Hide the books in the cellar"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "dobr" to 1)),
                    ok = { unflag("skit"); flag("skit_done"); addRelic(t("Старопечатная Псалтирь", "Old-Print Psalter"), "reliq", mapOf(UM to 1), t("${h.name}: укрыта от солдат, подарена старцем", "${h.name}: hidden from the soldiers, a gift from the elder"))
                        t("Книги пережили обыск. Старец оставил вам Псалтирь в медных застёжках. Ум +1 в жребиях.", "The books came through the search. The elder left you a Psalter with brass clasps. Wits +1 in draws.") },
                    bad = { unflag("skit"); flag("skit_done"); glory(-2); addTr(h, "shram"); t("Книги нашли. Вас высекли как раскольника, хоть вы и крестились тремя перстами.", "The books were found. You were flogged as a schismatic, though you cross yourself with three fingers.") }),
                Opt(t("Отказать: долг отдан", "Refuse: the debt is paid"), sin = "coward", fx = { unflag("skit"); flag("skit_done")
                    t("Старец ушёл, не сказав ни слова. Через неделю над лесом стоял дым.", "The elder left without a word. A week later smoke stood over the forest.") }),
            )
        }),
    Event("klad2", "klad", Tx("Чужой с планом", "The Stranger with a Plan"), w = 8, cond = { fire(flagged("klad") && (since("klad") ?: 0) >= 5) },
        txt = { t("В деревню пришёл чужой, в городском платье, с бумагой: план поля, крестик и подпись его прадеда. Спрашивает, не находил ли кто в той борозде горшка с серебром.",
            "A stranger in town clothes has come to the village with a paper: a plan of the field, a cross, and his great-grandfather's signature. He asks whether anyone found a pot of silver in that furrow.") },
        opts = { o ->
            listOf(
                Opt(t("Знать не знаем", "Never heard of it"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { unflag("klad"); flag("klad_done"); t("Чужой постоял над пустой ямой, вздохнул и уехал. План оставил на заборе.", "The stranger stood over the empty hole, sighed and left. He left the plan on the fence.") },
                    bad = { unflag("klad"); flag("klad_done"); money(-6); glory(-2); t("На дне ямы он нашёл черепок с той же меткой, что на плане. Пришлось отдать, и с лихвой — за враньё.", "At the bottom of the hole he found a shard with the same mark as on his plan. You had to pay it back, with something extra for the lie.") }),
                Opt(t("Отдать половину", "Give him half"), tag = t("−7 сер.", "−7 silver"), fx = { unflag("klad"); flag("klad_done"); money(-7); glory(1)
                    t("Чужой взял половину, пожал руку и оставил вам план — на память. Говорит, крестиков на нём, вообще-то, два.", "The stranger took half, shook your hand, and left you the plan as a keepsake. There are, he says, actually two crosses on it.") }),
                Opt(t("Отдать всё, что осталось, и повиниться", "Give back all that is left and confess"), fx = { unflag("klad"); flag("klad_done"); money(-part(.5)); glory(2); o.h.stones = maxOf(0, o.h.stones - 1)
                    t("Чужой долго молчал, потом отсчитал вам рубль «за честность». На душе полегчало.", "The stranger was silent a long while, then counted out a ruble for you “for honesty.” Your heart felt lighter.") }),
            )
        }),
    Event("gorod2", "village", Tx("На побывку", "Home on a Visit"), w = 8,
        cond = { val x = person(g.flags["town_id"]); fire(if (flagged("town") && (since("town") ?: 0) >= 6 && x != null && alive(x) && x.away == null) x else null) },
        txt = { o -> val x = o.xx; t("${x.name} приехал${sx(x, "", "а")} из города на побывку: в сапогах со скрипом, с гостинцами и с городскими словечками. За ужином зовёт с собой младших.",
            "${x.name} has come home from town on a visit: creaking boots, presents, and town words. At supper ${sx(x, "he", "she")} asks the younger ones to come back too.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Взять гостинцы и благословить", "Take the presents and give your blessing"), fx = { unflag("town"); unflag("town_id"); flag("town_done"); money(5)
                    t("Пять рублей серебром и платок матери. ${x.name} уехал${sx(x, "", "а")} обратно с первым обозом.", "Five silver rubles and a shawl for mother. ${x.name} went back with the first wagon train.") }),
                Opt(t("Уговорить остаться дома", "Talk ${sx(x, "him", "her")} into staying home"), roll = Roll(SER, 0, mapOf("kras" to 1, "dobr" to 1)),
                    ok = { unflag("town"); unflag("town_id"); flag("town_done"); x[UM] += 1; t("${x.name} остал${sx(x, "ся", "ась")}. Городские словечки вывелись к Пасхе, а счёт и сметка остались.", "${x.name} stayed. The town words wore off by Easter, but the head for figures stayed.") },
                    bad = { unflag("town"); unflag("town_id"); flag("town_done"); x.stones += 1; t("Наутро ${x.name} уехал${sx(x, "", "а")}, не попрощавшись.", "In the morning ${x.name} left without saying goodbye.") }),
                Opt(t("Отпустить с ${sx(x, "ним", "ней")} младших", "Let the younger ones go along"), fx = { unflag("town"); unflag("town_id"); flag("town_done")
                    val k = kidIn(13, 20) { it.id != x.id && it.spouse == null }.firstOrNull()
                    if (k != null) { addTr(k, "gor"); k[HIT] += 1; t("${k.name} уехал${sx(k, "", "а")} с ${x.name}. В избе стало просторно и тихо.", "${k.name} left with ${x.name}. The hut grew roomy and quiet.") }
                    else t("Младшие оказались малы. ${x.name} обещал${sx(x, "", "а")} приехать за ними через год.", "The young ones were too small yet. ${x.name} promised to come back for them in a year.") }),
            )
        }),
    Event("usmir", "barin", Tx("Усмирение", "The Punitive Squad"), w = 8, cond = { h -> fire(flagged("bunt") && (since("bunt") ?: 0) >= 1 && g.year < 1870) },
        txt = { t("Из губернии пришла воинская команда — «усмирять». Барин вернулся вместе с ней, и приказчик ходит по дворам со списком.",
            "A detachment of soldiers has come from the province to “pacify” the village. The barin has come back with it, and the steward goes from house to house with a list.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Выйти и взять всё на себя", "Step out and take it all on yourself"), roll = Roll(ZDR, 0, mapOf("hrab" to 2, "bog" to 1)),
                    ok = { unflag("bunt"); flag("bunt_done"); addTr(h, "shram"); glory(2); deed(h, t(sx(h, "Принял", "Приняла") + " розги за весь мир", "Took the rods for the whole commune"))
                        t("Высекли одного вас, остальных простили. Мир этого не забудет.", "Only you were flogged; the rest were pardoned. The commune will not forget it.") },
                    bad = { unflag("bunt"); flag("bunt_done"); addTr(h, "kalek"); glory(1); t("Секли долго. Встали вы через месяц, и ходите теперь с палкой.", "The flogging went on a long time. You got up a month later, and walk with a stick now.") }),
                Opt(t("Откупиться всем миром", "Buy them off with the whole commune"), cost = 10, tag = t("10 сер.", "10 silver"), fx = { unflag("bunt"); flag("bunt_done"); money(-10)
                    t("Барин взял деньги, команда — гуся и водку. Уехали через неделю.", "The barin took the money, the soldiers a goose and vodka. They left in a week.") }),
                Opt(t("Пересидеть в лесу", "Sit it out in the forest"), sin = "coward", fx = { unflag("bunt"); flag("bunt_done"); money(-part(.4))
                    t("Отсиделись в лесу до осени. Двор за это время разорили, а соседей пороли за вас.", "You hid in the forest till autumn. Meanwhile the farm was stripped, and the neighbors were flogged in your place.") }),
            )
        }),
    // ---------------------------------------------------------------- rare wonders
    Event("yurod", "village", Tx("Юродивый", "The Holy Fool"), cond = { h -> fire(age(h) >= 16 && chance(.2)) },
        txt = { t("По деревне ходит юродивый Гришенька — босой по снегу, в веригах. У ваших ворот остановился, засмеялся и кинул в окно горсть земли.",
            "Grishenka the holy fool walks through the village, barefoot in the snow, in chains. At your gate he stopped, laughed, and threw a handful of earth at the window.") },
        opts = { o ->
            listOf(
                Opt(t("Позвать в избу и спросить, к чему", "Ask him in and ask what it means"), roll = Roll(VER, 1, mapOf("bogom" to 1, "krot" to 1)),
                    ok = { g.bless = 1; t("Гришенька поел, перекрестил печь и сказал: «Земля к земле. Не бойся». В следующем жребии — лишний белый боб.", "Grishenka ate, blessed the stove with the cross and said: “Earth to earth. Have no fear.” The next draw will hold an extra white bean.") },
                    bad = { o.h.stones += 1; t("Гришенька только смеялся и плакал. К чему — так и не поняли, и от этого не спится.", "Grishenka only laughed and wept. What it meant you never learned, and it keeps you awake.") }),
                Opt(t("Окатить водой, чтоб не накликал", "Douse him with water so he brings no ill"), sin = "cruel", fx = { g.hex = g.year + 3
                    t("Гришенька ушёл мокрый, приговаривая: «Холодно тебе будет, холодно». Три года в мешке лишний красный боб.", "Grishenka went off dripping, murmuring: “You'll be cold, so cold.” For three years a second red bean lies in the bag.") }),
                Opt(t("Подать копейку", "Give him a kopeck"), cost = 1, tag = t("1 сер.", "1 silver"), fx = { money(-1)
                    t("Копейку он взял, а землю под окном велел не выметать. Не выметали.", "He took the kopeck and told you not to sweep away the earth under the window. You didn't.") }),
            )
        }),
    Event("kometa", "dawn", Tx("Хвостатая звезда", "The Tailed Star"), cond = { fire(chance(.15)) },
        txt = { t("Над лесом встала хвостатая звезда, хвост на полнеба. Старики говорят — к войне или к мору. Бабы не выпускают детей на двор.",
            "A tailed star has risen over the forest, its tail across half the sky. The old men say it means war or pestilence. The women won't let the children out.") },
        opts = { o ->
            listOf(
                Opt(t("Отслужить молебен всем миром", "Have the whole commune pray"), cost = 1, tag = t("1 сер.", "1 silver"), fx = { money(-1); glory(1)
                    t("Звезда ушла через месяц. Ни войны, ни мора — только орехов в тот год уродилось видимо-невидимо.", "The star left after a month. No war and no pestilence: only a year of more hazelnuts than anyone could count.") }),
                Opt(t("Смотреть по ночам и записывать", "Watch it at night and write it down"), roll = Roll(UM, 0, mapOf("gram" to 2, "umn" to 1)),
                    ok = { o.h[UM] += 1; t("Записали, где она шла и когда ушла. Дьячок переписал к себе в святцы, с вашим именем.", "You wrote down where it went and when it left. The sexton copied it into his church calendar, with your name.") },
                    bad = { o.h[ZDR] -= 1; t("Простыли на крыше. А звезда ушла сама, не спросясь.", "You caught a chill on the roof. And the star left on its own, without asking.") }),
                Opt(t("Распродать всё, пока не поздно", "Sell everything before it's too late"), fx = { money(-part(.3))
                    t("Распродали за бесценок. Звезда ушла, а бесценок остался.", "You sold it all for a song. The star left; the song remained.") }),
            )
        }),
    Event("domovoi", "night_int", Tx("Домовой", "The House Spirit"), cond = { fire(chance(.25)) },
        txt = { t("Лошадь каждое утро вся в мыле, а грива заплетена в косички. Старики говорят: домовой невзлюбил масть.",
            "Every morning the horse is lathered with sweat, its mane plaited into little braids. The old folk say the house spirit has taken against its color.") },
        opts = { o ->
            listOf(
                Opt(t("Поставить домовому кашу за печку", "Leave porridge behind the stove for the house spirit"), fx = { flag("domovoi")
                    if (chance(.5)) t("Каша к утру съедена, лошадь сухая, грива расчёсана.", "By morning the porridge was eaten, the horse dry, the mane combed.")
                    else t("Каша съедена, а лошадь опять в мыле. Видно, мало масла положили.", "The porridge was eaten, and the horse lathered again. Not enough butter in it, it seems.") }),
                Opt(t("Продать лошадь, купить другой масти", "Sell the horse and buy one of another color"), cost = 3, tag = t("3 сер.", "3 silver"), fx = { money(-3)
                    t("Гнедую домовой принял: гриву ей чешет, а не путает.", "The bay mare the house spirit accepted: he combs her mane instead of tangling it.") }),
                Opt(t("Засесть ночью в конюшне", "Lie in wait in the stable at night"), roll = Roll(HIT, 0, mapOf("hrab" to 1, "umn" to 1)),
                    ok = { money(2); t("Под утро поймали соседского парня: катался на вашей лошади к невесте за реку. Отец его заплатил за овёс.", "Toward dawn you caught the neighbor's lad: he had been riding your horse across the river to see his girl. His father paid for the oats.") },
                    bad = { o.h.stones += 1; t("Задремали. Сквозь сон кто-то дунул в ухо. Утром — косички и мыло.", "You dozed off. Something blew in your ear in your sleep. In the morning: braids and lather.") }),
            )
        }),
    Event("yavlen", "forest", Tx("Явленная икона", "The Icon in the Pine"), cond = { h -> fire(age(h) >= 16 && !flagged("chapel") && chance(.2)) },
        txt = { t("Пастушонок прибежал белый как мел: в лесу у родника, на сосне — икона. Старая, тёмная, а лик проступает.",
            "The herd boy came running, white as chalk: in the forest by the spring, on a pine tree, there is an icon. Old and dark, but the face shows through.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Взять в избу, в красный угол", "Take it home to the icon corner"), roll = Roll(VER, 0, mapOf("bogom" to 2, "krot" to 1)),
                    ok = { addRelic(t("Явленный образ", "The Icon from the Pine"), "icon", mapOf(VER to 1), t("${h.name}: найден на сосне у родника", "${h.name}: found on a pine by the spring"))
                        t("Образ прижился в красном углу, и лампада перед ним не гаснет даже без масла. Вера +1 в жребиях.", "The icon settled in the corner, and the lamp before it never goes out, even without oil. Faith +1 in draws.") },
                    bad = { t("Наутро угол пуст, а икона снова на сосне. Её не удержать.", "In the morning the corner was empty, and the icon back on the pine. It will not be kept.") }),
                Opt(t("Ставить у родника часовню", "Build a chapel by the spring"), cost = 5, tag = t("5 сер.", "5 silver"), fx = { money(-5); glory(3); flag("chapel"); deed(h, t(sx(h, "Поставил", "Поставила") + " часовню у родника", "Built a chapel by the spring"))
                    t("Часовню срубили за лето. Теперь к роднику ходят с трёх уездов, и всем говорят, чья часовня.", "The chapel was raised in a summer. Now people come to the spring from three districts, and everyone is told whose chapel it is.") }),
                Opt(t("Отдать батюшке", "Give it to the priest"), fx = { glory(1)
                    t("Трижды икону переносили в церковь, трижды она возвращалась на сосну. Батюшка махнул рукой и стал служить в лесу.", "Three times the icon was carried to the church; three times it went back to the pine. The priest gave up and holds services in the forest.") }),
            )
        }),
    Event("paporot", "forest", Tx("Иванова ночь", "St John's Eve"), cond = { h -> fire(age(h) in 16..45 && chance(.2)) },
        txt = { t("Иванова ночь. Говорят, в полночь папоротник цветёт огнём, и кто сорвёт цвет — тому все клады откроются. Только нечисть его стережёт.",
            "St John's Eve. They say that at midnight the fern blooms with fire, and whoever plucks the flower will find every buried treasure. Only the unclean ones guard it.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Идти в лес за цветом", "Go into the forest for the flower"), roll = Roll(VER, -2, mapOf("hrab" to 2, "trus" to -2, "bogom" to 1)),
                    ok = { money(8); t("Цвет ли это был или светляк, а под тем папоротником нашёлся горшок с медными деньгами.", "Whether it was the flower or a glowworm, under that fern you found a pot of copper coins.") },
                    bad = { h.stones += 1; t("Всю ночь водило по лесу кругами. Вышли к утру за три версты, с седой прядью.", "All night something led you round in circles. You came out at dawn three versts away, with a streak of gray in your hair.") }),
                Opt(t("Прыгать через костёр с молодыми", "Jump over the bonfire with the young ones"), fx = { h[SER] += 1
                    t("До утра у костров, в венке. Цвета не нашли, а помолодели.", "Until dawn by the fires, in a wreath. No flower, but younger for it.") }),
                Opt(t("Спать: сказки это", "Sleep: those are tales"), fx = { t("Спали. Утром Кожемякины хвастали, что видели цвет. Врут, наверное.", "You slept. In the morning the Kozhemyakins boasted they had seen the flower. Lying, most likely.") }),
            )
        }),
    Event("rusalka", "field", Tx("Русальная неделя", "Rusalka Week"), cond = { val k = kidIn(15, 25) { it.male && it.spouse == null }; fire(if (k.isNotEmpty() && chance(.2)) k[0] else null) },
        txt = { o -> val x = o.xx; t("Русальная неделя. ${x.name} вернулся под утро сам не свой: звала, говорит, из ржи девка с зелёными волосами — и смеялась.",
            "Rusalka week. ${x.name} came home toward dawn not himself: a girl with green hair, he says, was calling him out of the rye, and laughing.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Окурить полынью и не пускать в поле", "Smoke him with wormwood and keep him out of the fields"), fx = {
                    t("Неделю сидел дома и маялся. Девка, говорят, звала потом других.", "He sat at home a week, restless. The girl, they say, called others after that.") }),
                Opt(t("Отпустить: пусть сам разберётся", "Let him go: he can sort it out"), roll = Roll(VER, 0, ofX = true),
                    ok = { if (x.spouse == null) mkSpouse(x, t("Мельниковых", "the Melnikovs")); t("Вернулся с невестой — дочкой мельника. Волосы у неё, оказалось, просто рыжие, а во ржи она пряталась от отца.", "He came back with a bride: the miller's daughter. Her hair, it turned out, was only red, and she had been hiding in the rye from her father.") },
                    bad = { x.stones += 1; x[ZDR] -= 1; t("Ходил неделю, исхудал. Потом отпустило, но песни петь перестал.", "He went out every night for a week and grew thin. Then it let him go, but he stopped singing.") }),
                Opt(t("Позвать батюшку", "Call the priest"), cost = 1, tag = t("1 сер.", "1 silver"), fx = { money(-1); x[VER] += 1
                    t("Батюшка отслужил молебен на меже и посоветовал парню жениться.", "The priest sang a prayer service at the edge of the field and advised the lad to marry.") }),
            )
        }),
    // ---------------------------------------------------------------- the family's way: the peddler's pack and trade
    Event("ofenya", "road", Tx("Офеня", "The Peddler"), cond = { h -> fire(!flagged("way_trade") && age(h) in 18..55 && g.money >= 8 && g.year >= 1700) },
        txt = { t("Офеня с коробом из-под Владимира — ситец, иглы, лубочные картинки — ищет товарища: «Мне ноги нужны, тебе — деньги. Бери короб, ходи по сёлам. Научу и говорить по-нашему, по-офенски».",
            "A peddler from the Vladimir country with his pack (calico, needles, cheap prints) is looking for a partner: “I need legs, you need money. Take a pack and walk the villages. I'll even teach you our peddlers' cant.”") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Войти в долю", "Go in with him"), cost = 8, tag = t("8 сер. · род уходит в торговлю", "8 silver · the family takes up trade"), fx = { money(-8); flag("way_trade"); unflag("way_icon")
                    deed(h, t(sx(h, "Ушёл", "Ушла") + " в торговлю", "Took up trade")); log(t("Род ушёл в торговлю.", "The family took up trade."), "big")
                    t("Короб на спину — и по сёлам. Через три года у вас своя лавка в селе: ситец, соль, дёготь и пряники. Теперь вы торговые люди.", "A pack on your back and off through the villages. Three years on you have your own shop in the village: calico, salt, tar and gingerbread. You are trading folk now.") }),
                Opt(t("Пахарь и есть пахарь", "A plowman is a plowman"), fx = { t("Офеня ушёл к Кожемякиным. Те подумали три дня — и тоже отказались.", "The peddler went on to the Kozhemyakins. They thought it over three days and said no as well.") }),
            )
        }),
    Event("gildia", "fair", Tx("Гильдия", "The Guild"), w = 3, cond = { fire(flagged("way_trade") && !flagged("guild") && (since("way_trade") ?: 0) >= 5 && g.year >= 1775) },
        txt = { t("Дела в лавке идут. Городской голова намекает: запишитесь в купцы третьей гильдии. Объявленный капитал, подать — зато почёт, и рекрутов с купцов не берут.",
            "Business is good. The town head drops a hint: why not enroll as merchants of the third guild? A declared capital and a levy, but respect too, and merchants give no recruits.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Записаться в купцы", "Enroll as merchants"), cost = 15, tag = t("15 сер. · рекрутов не берут", "15 silver · no more recruit levies"), fx = { money(-15); glory(4); flag("guild")
                    deed(h, t("Записал${sx(h, "ся", "ась")} в купцы третьей гильдии", "Enrolled as a merchant of the third guild"))
                    t("Вы теперь купцы. Сапоги со скрипом, борода расчёсана, и кланяются вам первыми — даже Кожемякины.", "You are merchants now. Creaking boots, a combed beard, and people bow to you first: even the Kozhemyakins.") }),
                Opt(t("Рано ещё", "Too soon"), fx = { t("Капитал любит тишину. Подождём.", "Capital likes quiet. We'll wait.") }),
            )
        }),
    Event("makariy", "fair", Tx("Большая ярмарка", "The Great Fair"), w = 3, cond = { fire(flagged("way_trade")) },
        txt = { if (g.year < 1817) t("Летом — Макарьевская ярмарка под Нижним: со всей России купцы, персидский шёлк, сибирская пушнина. Везти ли обоз?",
            "In summer comes the Makaryev Fair down the Volga from Nizhny: merchants from all over Russia, Persian silk, Siberian furs. Will you send a wagon train?")
        else t("Летом — Нижегородская ярмарка: со всей России купцы, чай из Кяхты, сибирская пушнина. Везти ли обоз?",
            "In summer comes the Nizhny Novgorod Fair: merchants from all over Russia, tea from Kyakhta, Siberian furs. Will you send a wagon train?") },
        opts = { o ->
            listOf(
                Opt(t("Везти большой обоз льна", "Send a great train of flax"), roll = Roll(HIT, 0, mapOf("gor" to 1, "lzh" to 1, "umn" to 1)),
                    ok = { money(14); glory(1); t("Лён ушёл англичанам по хорошей цене. Вернулись с прибылью и с самоваром.", "The flax went to English buyers at a good price. You came home with a profit and a samovar.") },
                    bad = { money(-8); t("На лесной дороге обоз встретили лихие люди. Вернулись живыми — и на том спасибо.", "On the forest road the wagons met with robbers. You came home alive, and thank God for that.") }),
                Opt(t("Везти малый воз, без риска", "A small cart, no risk"), fx = { money(3); t("Продали, купили, вернулись. Три рубля сверху.", "Sold, bought, came home. Three rubles to the good.") }),
                Opt(t("Не ехать: лавка не пустует", "Don't go: the shop needs minding"), fx = { t("Лето простояли за прилавком. Мухи, гири и разговоры о ценах.", "You spent the summer behind the counter. Flies, weights and talk about prices.") }),
            )
        }),
    Event("progor", "interior", Tx("Должник сбежал", "The Debtor Has Fled"), w = 2, cond = { fire(flagged("way_trade") && (since("way_trade") ?: 0) >= 3) },
        txt = { t("Купец, которому вы отпустили товар в долг, сбежал не то в Сибирь, не то к староверам. Ваши собственные заимодавцы уже у ворот.",
            "The merchant you sold goods to on credit has fled, to Siberia or to the Old Believers, nobody knows. Your own creditors are already at the gate.") },
        opts = { o ->
            listOf(
                Opt(t("Расплатиться всем, что есть", "Pay with everything you have"), fx = { money(-part(.6)); glory(1)
                    t("Лавка уцелела, сундук пуст. Зато слово ваше по-прежнему твёрдое.", "The shop survived, the chest is empty. But your word is still good.") }),
                Opt(t("Закрыть лавку и вернуться к земле", "Close the shop and go back to the land"), fx = { unflag("way_trade"); unflag("guild"); money(-part(.2))
                    log(t("Род вернулся к земле.", "The family went back to the land."), "big")
                    t("Вывеску сняли, прилавок пошёл на дрова. Земля, по крайней мере, никуда не сбегает.", "The sign came down and the counter went for firewood. The land, at least, never runs away.") }),
                Opt(t("Объявиться несостоятельным, а товар — к шурину", "Declare yourself ruined and hide the goods at a brother-in-law's"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 2)),
                    ok = { glory(-1); t("Товар у шурина, долги — у заимодавцев, а вы — чистый. Над этим все смеются, но никто не может доказать.", "The goods are at your brother-in-law's, the debts with the creditors, and you are clean. Everyone laughs, and nobody can prove a thing.") },
                    bad = { unflag("way_trade"); unflag("guild"); glory(-3); money(-part(.5)); log(t("Род вернулся к земле.", "The family went back to the land."), "big")
                        t("Обман вскрылся. Лавку опечатали, и ходить вам теперь опять за сохой.", "The fraud came out. The shop was sealed, and it's back behind the plow for you.") }),
            )
        }),
    // ---------------------------------------------------------------- the family's way: the icon painters' craft
    Event("bogomaz", "church", Tx("Богомазы", "The Icon Painters"), cond = {
            fire(if (!flagged("way_icon") && g.money >= 4) kidIn(9, 20) { eff(it, UM) >= 6 }.firstOrNull() else null) },
        txt = { o -> val x = o.xx; t("Через село шли богомазы из Холуя — расписывать новую церковь. Старший мастер увидел, как ${x.name} углём рисует на печи, и долго смотрел. «Отдайте в науку. Через семь лет будет писать лики».",
            "Icon painters from Kholui passed through the village on their way to paint a new church. The master saw ${x.name} drawing on the stove with charcoal and looked a long time. “Give ${sx(x, "him", "her")} to me to learn. In seven years ${sx(x, "he", "she")}'ll be painting faces.”") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Отдать в науку, а там — всем домом в промысел", "Apprentice ${sx(x, "him", "her")}, and the whole house will take up the craft"), cost = 4, tag = t("4 сер. · род уходит в иконописцы", "4 silver · the family takes up icon painting"),
                    fx = { money(-4); flag("way_icon"); unflag("way_trade"); unflag("guild"); x[UM] += 1; x[VER] += 1; deed(x, t("Учил${sx(x, "ся", "ась")} у холуйских богомазов", "Apprenticed to the Kholui icon painters"))
                        log(t("Род взялся за иконописное дело.", "The family took up icon painting."), "big")
                        t("Через семь лет ${x.name} вернул${sx(x, "ся", "ась")} мастером. Теперь в избе пахнет олифой и яичным желтком, и зимой вы пишете иконы на продажу.", "Seven years later ${x.name} came home a master. Now the hut smells of linseed oil and egg yolk, and in winter you paint icons to sell.") }),
                Opt(t("В поле руки нужнее", "Hands are needed more in the field"), fx = { x.stones += 1
                    t("Мастер пожал плечами. ${x.name} рисует теперь только пальцем на замёрзшем окне.", "The master shrugged. ${x.name} draws only with a finger on the frosted window now.") }),
            )
        }),
    Event("ikonostas", "church", Tx("Иконостас", "The Iconostasis"), w = 3, cond = { fire(flagged("way_icon")) },
        txt = { t("Помещица заказывает иконостас в новую каменную церковь: сорок ликов, к Покрову. Деньги большие, сроки малые.",
            "A lady landowner orders an iconostasis for her new stone church: forty faces, by Intercession Day. Big money, little time.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Взяться", "Take it on"), roll = Roll(UM, 0, mapOf("umn" to 1, "gram" to 1, "bogom" to 1)),
                    ok = { money(14); glory(2); deed(h, t("Расписал${sx(h, "", "а")} иконостас в каменной церкви", "Painted an iconostasis for a stone church"))
                        t("Поспели к Покрову, и архиерей, говорят, перекрестился на вашего Николу дважды.", "You finished by Intercession, and the bishop, they say, crossed himself twice before your St Nicholas.") },
                    bad = { money(-3); glory(-1); t("Не поспели к Покрову. Помещица заплатила половину и жалуется на вас всей губернии.", "You missed Intercession Day. The lady paid half and complains of you to the whole province.") }),
                Opt(t("Отказаться: не по силам", "Decline: too much for us"), fx = { t("Заказ ушёл в Палех. Палешане, конечно, тоже не поспели.", "The order went to Palekh. The Palekh men missed the date too, of course.") }),
            )
        }),
    Event("staroobr", "night_int", Tx("Старое письмо", "The Old Style"), w = 3, cond = { fire(flagged("way_icon") && g.year < 1905) },
        txt = { t("Ночью постучали староверы: нужен образ старого письма — с двоеперстием, без новшеств. Платят серебром. Но если узнает благочинный…",
            "In the night Old Believers knocked: they need an icon in the old manner, with the two-fingered blessing and none of the new ways. They pay in silver. But if the dean finds out…") },
        opts = { o ->
            listOf(
                Opt(t("Написать", "Paint it"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "trus" to 1)),
                    ok = { money(10); t("Образ унесли до света, завёрнутым в холстину. Десять рублей и молчание с обеих сторон.", "They carried the icon off before dawn, wrapped in sacking. Ten rubles and silence on both sides.") },
                    bad = { money(-4); glory(-2); if (chance(.5)) { unflag("way_icon"); log(t("Род оставил иконописное дело.", "The family gave up icon painting."), "big")
                        t("Благочинный узнал. Ваши образа велено снять со всех церквей уезда, и заказов больше не будет. Кисти убрали в сундук.", "The dean found out. Your icons are to be taken down from every church in the district, and no more orders will come. The brushes went into the chest.") }
                        else t("Благочинный узнал. Штраф и епитимья, но промысел оставили.", "The dean found out. A fine and a penance, but you kept the craft.") }),
                Opt(t("Отказать", "Refuse"), fx = { t("Староверы ушли. Через месяц тот же образ написал кто-то в Мстёре.", "The Old Believers left. A month later someone in Mstyora painted the same icon.") }),
            )
        }),
    Event("lubok", "fair", Tx("Лубок", "Cheap Prints"), w = 3, cond = { fire(flagged("way_icon")) },
        txt = { t("Офени заказывают тысячу дешёвых картинок — «Мыши кота хоронят» да «Бова-королевич». Работа быстрая, деньги верные, а старый мастер морщится: руку испортите.",
            "Peddlers order a thousand cheap prints: “The Mice Bury the Cat” and “Prince Bova.” Quick work and sure money, but the old master winces: you'll ruin your hand.") },
        opts = { o ->
            listOf(
                Opt(t("Раскрашивать лубки", "Color the prints"), fx = { money(6); t("Тысяча котов, тысяча мышей. Рука и правда стала быстрее, а вот тоньше — нет.", "A thousand cats, a thousand mice. The hand did grow quicker, though not finer.") }),
                Opt(t("Писать только иконы", "Paint only icons"), fx = { glory(1); t("Мастер одобрил. Кошелёк — не очень.", "The master approved. The purse less so.") }),
                Opt(t("Нарисовать на лубке Кожемякиных", "Put the Kozhemyakins in a print"), roll = Roll(HIT, 0, mapOf("lzh" to 1, "umn" to 1)),
                    ok = { money(4); glory(2); feud(1); t("Картинку «Как Кожемякин козу брил» раскупили в трёх губерниях.", "The print “How Kozhemyakin Shaved the Goat” sold out in three provinces.") },
                    bad = { feud(2); t("Кожемякины узнали себя — по носам — и пришли с кольями.", "The Kozhemyakins knew themselves by their noses and came with stakes.") }),
            )
        }),
    // ---------------------------------------------------------------- everyday life
    Event("groza", "field", Tx("Туча над покосом", "A Storm over the Hay"), cond = { h -> fire(age(h) >= 14) },
        txt = { o -> pick(listOf(
            t("Сено скошено, лежит в валках, а с запада идёт туча — чёрная, с белым брюхом. Не успеете сметать — сгниёт.",
                "The hay is cut and lies in windrows, and from the west comes a cloud, black, with a white belly. If you can't stack it in time, it will rot."),
            t("Сено высохло, звенит, и тут над лесом громыхнуло. Ласточки летают низко, бабы бегут с граблями.",
                "The hay is dry and rustling, and then thunder rolls over the forest. The swallows fly low; the women run with their rakes."))) },
        opts = { o ->
            listOf(
                Opt(t("Метать стог всем домом", "The whole house stacks the rick"), roll = Roll(SIL, 0, mapOf("bog" to 2, "hrab" to 1)),
                    ok = { money(2); t("Последний навильник лёг на стог, когда упали первые капли.", "The last forkful went on the rick as the first drops fell.") },
                    bad = { money(-3); t("Не успели. Сено промокло насквозь и почернело к Ильину дню.", "You were too late. The hay was soaked through and black by St Elijah's Day.") }),
                Opt(t("Звать соседей на помочь", "Call the neighbors to help"), roll = Roll(SER, 1, mapOf("dobr" to 1, "shch" to 1)),
                    ok = { glory(1); money(1); t("Сбежалась вся улица. Сметали вмиг, а потом пили квас под навесом и слушали дождь.", "The whole street came running. It was stacked in no time, and then you drank kvass under the eaves and listened to the rain.") },
                    bad = { money(-2); t("Соседи спасали своё. Своё и спасли.", "The neighbors were saving their own. And they did.") }),
                Opt(t("Сгрести в копны как попало", "Rake it into heaps any old way"), fx = { money(-1); t("Половину спасли, половина преет. Корова не жалуется.", "Half was saved, half is moldering. The cow doesn't complain.") }),
            )
        }),
    Event("roi", "village", Tx("Рой", "The Swarm"), cond = { h -> fire(age(h) >= 14) },
        txt = { t("На вашу черёмуху сел чужой пчелиный рой. По обычаю — на чьём дереве, того и рой. Но следом уже бежит пасечник Кожемякин с решетом.",
            "A stray swarm of bees has settled on your bird-cherry tree. By custom, whose tree, his swarm. But here comes Kozhemyakin the beekeeper running after it with a sieve.") },
        opts = { o ->
            listOf(
                Opt(t("Снять рой себе, по обычаю", "Take the swarm, as custom allows"), roll = Roll(HIT, 0, mapOf("hrab" to 1, "umn" to 1)),
                    ok = { money(4); feud(1); t("Рой в колоде, колода в саду. Осенью — мёд, а Кожемякин — в обиде.", "The swarm is in a hollow log, the log in the garden. Honey in autumn, and Kozhemyakin sulking.") },
                    bad = { o.h[ZDR] -= 1; feud(1); t("Пчёлы рассудили сами: улетели, а вас изжалили так, что неделю смотрели в щёлочку.", "The bees settled it themselves: they flew off, and stung you so that for a week you looked out through slits.") }),
                Opt(t("Отдать хозяину", "Give it back to its owner"), fx = { feud(-1)
                    t("Кожемякин снял рой молча, а осенью прислал горшок мёда. Тоже молча.", "Kozhemyakin took the swarm without a word, and in autumn sent a pot of honey. Also without a word.") }),
            )
        }),
    Event("konokrad", "night_int", Tx("Конокрады", "Horse Thieves"), cond = { h -> fire(age(h) >= 16) },
        txt = { o -> pick(listOf(
            t("Ночью залаяли собаки, скрипнули ворота конюшни. Кто-то выводит вашу лошадь — тихо, по-хозяйски.",
                "In the night the dogs barked and the stable gate creaked. Someone is leading out your horse, quietly, like he owns it."),
            t("Под утро — топот за околицей. Пастух прибежал: в ночном угнали трёх коней, и вашего тоже.",
                "Toward morning, hoofbeats beyond the village fence. The herdsman came running: three horses were driven off from the night pasture, yours among them."))) },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("В погоню, на соседской кобыле", "Give chase on the neighbor's mare"), roll = Roll(SIL, -1, mapOf("hrab" to 2, "bog" to 1, "trus" to -2)),
                    ok = { glory(2); deed(h, t(sx(h, "Отбил", "Отбила") + " коня у конокрадов", "Took a horse back from thieves")); t("Нагнали у брода. Конокрады бросили коней и ушли за реку.", "You caught them at the ford. The thieves dropped the horses and fled across the river.") },
                    bad = { money(-5); addTr(h, "shram"); t("Нагнали — на свою голову. Кистенём по плечу, и коня как не бывало.", "You caught them, to your sorrow. A flail across the shoulder, and the horse was gone for good.") }),
                Opt(t("Искать по ярмаркам, через барышников", "Look for it at the fairs, through the horse dealers"), cost = 2, tag = t("2 сер.", "2 silver"), roll = Roll(UM, 0, mapOf("gor" to 1)),
                    ok = { money(-2); t("Через месяц нашли своего гнедого в Ростове, перекрашенного в вороного. Отдали за полцены.", "A month later you found your bay at Rostov, dyed black. You got it back for half the price.") },
                    bad = { money(-6); t("Объездили три ярмарки. Коня не нашли, а пришлось покупать нового.", "You rode round three fairs. No horse, and you had to buy a new one.") }),
                Opt(t("Самосуд всем миром", "Mob justice with the whole commune"), sin = "cruel", fx = { glory(-1); h.stones += 1
                    t("Одного поймали. Что с ним сделали у околицы, в деревне потом не говорили. Конь вернулся.", "One was caught. What was done to him by the village fence nobody spoke of afterward. The horse came back.") }),
            )
        }),
    Event("svyatki", "winter", Tx("Святки", "Yuletide"), cond = { yes },
        txt = { o -> pick(listOf(
            t("Святки. Ряженые с козой ходят по дворам: «Коляда-моляда!» Девки в бане льют воск в воду и ждут суженого в зеркале.",
                "Yuletide. Mummers with a goat's head go from yard to yard singing carols. In the bathhouse the girls pour wax into water and wait for their destined one in the mirror."),
            t("Святки. Мороз трещит, а по улице идут ряженые — медведь, журавль, мертвец в простыне. Стучат в окно: пустите погреться!",
                "Yuletide. The frost crackles, and down the street come the mummers: a bear, a crane, a dead man in a sheet. They tap at the window: let us in to warm up!"))) },
        opts = { o ->
            val girl = kidIn(15, 24) { !it.male && it.spouse == null }.firstOrNull()
            listOfNotNull(
                Opt(t("Пустить ряженых, угостить", "Let the mummers in and treat them"), cost = 1, tag = t("1 сер.", "1 silver"), fx = { money(-1); glory(1)
                    t("Коза бодала печь, медведь плясал с дедом, а под маской мертвеца оказался дьячков сын.", "The goat butted the stove, the bear danced with grandfather, and under the dead man's mask was the sexton's son.") }),
                if (girl != null) Opt(t("Гадать: ${girl.name} льёт воск", "Tell fortunes: ${girl.name} pours the wax"), roll = Roll(VER, 0, mapOf("bogom" to -1, "kras" to 1)),
                    ok = { girl[SER] += 1; t("Воск застыл кольцом. ${girl.name} потом всю зиму краснела, когда мимо проходил мельников сын.", "The wax set in a ring. All winter ${girl.name} blushed whenever the miller's son walked by.") },
                    bad = { girl.stones += 1; t("Воск застыл гробиком. ${girl.name} проплакала до Крещения, хоть бабка и божилась, что это сундук с приданым.", "The wax set in the shape of a coffin. ${girl.name} cried till Epiphany, though grandmother swore it was a dowry chest.") }) else null,
                Opt(t("Не пускать: бесовские игрища", "Keep them out: devilish games"), fx = { o.h[VER] += 1
                    t("Ряженые спели под окном что-то обидное про вашу бороду и ушли к Кожемякиным.", "The mummers sang something rude under the window about your beard and moved on to the Kozhemyakins.") }),
            )
        }),
)

internal val rusMoreHistory: List<Event> = listOf(
    Event("borod", "village", Tx("Бородовой знак", "The Beard Token"), y0 = 1705, y1 = 1722, p = .3, cond = { h -> fire(h.male && age(h) >= 16) },
        txt = { t("Указ государя: бороды брить. Кто не хочет — плати пошлину и носи медный знак «деньги взяты». С мужика — копейка у городской заставы, всякий раз.",
            "A decree from the Tsar: beards are to be shaved. Whoever won't must pay a duty and wear a copper token that says “money taken.” For a peasant, a kopeck at the town gate, every time.") },
        opts = { o ->
            listOf(
                Opt(t("Брить, по указу", "Shave, as decreed"), fx = { o.h[VER] -= 1
                    t("Жена не узнала. Батюшка отвернулся. Зато в городе вы ходите как немец.", "Your wife didn't know you. The priest turned away. But in town you walk about like a German.") }),
                Opt(t("Платить, а бороду не трогать", "Pay, and leave the beard alone"), tag = t("−1 сер.", "−1 silver"), fx = { money(-1)
                    addRelic(t("Бородовой знак", "Beard Token"), "medal", mapOf(VER to 1), t("${o.h.name}: плачено за бороду", "${o.h.name}: paid for a beard"))
                    t("Медный знак с усами и бородой — на шнурке, рядом с крестом. Вера +1 в жребиях.", "A copper token stamped with a beard and mustache, on a cord beside your cross. Faith +1 in draws.") }),
                Opt(t("В город не ездить вовсе", "Stop going to town at all"), fx = { money(-2)
                    t("Торгуете теперь через кума. Кум, конечно, берёт своё.", "You trade through your crony now. He takes his cut, of course.") }),
            )
        }),
    Event("piter", "war", Tx("Новая столица", "The New Capital"), y0 = 1704, y1 = 1716, p = .3, cond = { h -> fire(sonOf(h).firstOrNull()) },
        txt = { o -> t("С губернии гонят работных людей на Неву — на болоте ставить новый город, Петербург. С вашего двора — ${o.xx.name}. На полгода, а там как Бог даст.",
            "Workmen are being driven from the province to the Neva, to raise a new city on the marsh: Petersburg. From your household, ${o.xx.name}. For half a year, and after that as God wills.") },
        opts = { o ->
            val x = o.xx
            listOf(
                Opt(t("Отпустить", "Let him go"), roll = Roll(ZDR, 0, ofX = true),
                    ok = { addTr(x, "gor"); money(3); deed(x, t("Строил Петербург", "Helped build Petersburg")); t("Вернулся к зиме, худой, с тремя рублями и рассказами про море, которое солёное.", "He came back by winter, thin, with three rubles and tales of a sea that is salty.") },
                    bad = { if (chance(.5)) { kill(x, t("лихорадка на невских болотах", "fever in the Neva marshes")); t("Не вернулся. Говорят, город стоит на костях, и его косточки там же.", "He did not come back. They say the city stands on bones, and his are among them.") }
                        else { addTr(x, "hvor"); t("Вернулся с лихорадкой, которая так и не ушла.", "He came back with a fever that never left.") } }),
                Opt(t("Нанять за себя охочего", "Hire a willing man in his place"), cost = 6, tag = t("6 сер.", "6 silver"), fx = { money(-6); t("Охочий ушёл за шесть рублей. Вернулся ли — вы не узнали.", "The willing man went for six rubles. Whether he came back, you never learned.") }),
                Opt(t("Сказать, что хвор", "Say he is sick"), sin = "lie", roll = Roll(HIT, -1, mapOf("lzh" to 1)),
                    ok = { t("Староста поверил. ${x.name} полгода не выходил днём за ворота.", "The elder believed it. For half a year ${x.name} never went past the gate by day.") },
                    bad = { money(-4); glory(-1); t("Староста не поверил. Штраф, а ${x.name} всё равно ушёл — с партией следующей весной и вернулся живым.", "The elder didn't believe it. A fine, and ${x.name} went all the same, with next spring's party, and came back alive.") }),
            )
        }),
    Event("stuzha", "winter", Tx("Лютая зима", "The Great Frost"), y0 = 1740, y1 = 1740, p = 1.0, cond = { yes },
        txt = { t("Такой зимы старики не помнят: птицы замерзают на лету, колодцы промёрзли до дна. В Петербурге, говорят, для шутовской свадьбы построили дом изо льда.",
            "The old folk remember no such winter: birds freeze in flight, the wells are frozen to the bottom. In Petersburg, they say, a house of ice has been built for a jesters' wedding.") },
        opts = { o ->
            listOf(
                Opt(t("Взять скотину в избу", "Bring the animals into the hut"), roll = Roll(ZDR, 1, mapOf("krot" to 1)),
                    ok = { t("Телёнок спал у печи, куры под лавкой. Дух тяжёлый, зато все живы.", "The calf slept by the stove, the hens under the bench. The air was thick, but everyone lived.") },
                    bad = { val c = killOne(true); if (c != null) { kill(c, t("простуда в лютую зиму", "a chill in the great frost")); t("Скотину сберегли. Не уберегли: ${c.name}.", "The animals were saved. ${c.name} was not.") } else t("Переболели все, но к Сретенью отошли.", "Everyone fell ill, but all were well by Candlemas.") }),
                Opt(t("Рубить барский лес на дрова", "Cut the master's forest for firewood"), sin = "lie", roll = Roll(HIT, 0, mapOf("lzh" to 1, "hrab" to 1)),
                    ok = { money(2); t("Топили до звона. Лишние дрова продали соседям.", "You stoked the stove till it rang. The extra wood you sold to the neighbors.") },
                    bad = { addTr(o.h, "shram"); money(-3); t("Объездчик поймал с поличным. Высекли прямо на морозе.", "The forester caught you red-handed. You were flogged right there in the frost.") }),
                Opt(t("Топить чем есть: плетнём, сараем", "Burn whatever there is: fence, shed"), fx = { money(-3); t("Весной ставили всё заново. Но весну увидели все.", "In spring everything had to be built again. But everyone lived to see the spring.") }),
            )
        }),
    Event("ospa", "village", Tx("Оспу прививают", "Inoculation"), y0 = 1768, y1 = 1805, p = .2, cond = { val k = kidIn(1, 12); fire(if (k.isEmpty()) null else k[0]) },
        txt = { t("Государыня сама привила себе оспу и велит прививать по губерниям. В село приехал лекарь с ланцетом: царапина на руке — и, говорят, оспы больше не будет. Бабы крестятся: кровь бесовская.",
            "The Empress has had herself inoculated against smallpox and orders the provinces to follow. A doctor with a lancet has come to the village: a scratch on the arm and, they say, no more smallpox. The women cross themselves: devil's blood.") },
        opts = { o ->
            listOf(
                Opt(t("Привить детей", "Have the children inoculated"), roll = Roll(UM, 2, mapOf("gram" to 1, "gor" to 1)),
                    ok = { kidIn(1, 12).forEach { it[ZDR] += 1 }; glory(1); t("Поболели три дня. Когда оспа пришла в уезд, ваших она обошла.", "They were ill for three days. When the smallpox came to the district, it passed yours by.") },
                    bad = { val k = kidIn(1, 12); k.firstOrNull()?.let { it[ZDR] -= 1 }; t("Привили, да поболели крепко. Рябинки на лбу остались — но все живы.", "They were inoculated, and fell quite ill. A few pockmarks on the brow remain, but everyone lives.") }),
                Opt(t("Не дать: Бог дал — Бог взял", "Refuse: the Lord gives, the Lord takes"), fx = {
                    val k = kidIn(0, 12)
                    if (k.isNotEmpty() && chance(.35)) { val c = pick(k); kill(c, t("оспа", "smallpox")); t("Через год оспа прошла по деревне. Не стало: ${c.name}.", "A year later smallpox went through the village. Gone: ${c.name}.") }
                    else t("Оспа прошла мимо. Пока.", "The smallpox passed you by. For now.") }),
            )
        }),
    Event("holera", "plague", Tx("Холера", "Cholera"), y0 = 1830, y1 = 1831, p = .7, cond = { h -> fire(age(h) >= 14) },
        txt = { t("Холера. На дорогах карантины, мужиков не пускают на базар. По уезду слух: лекари травят колодцы. В соседнем селе мужики разнесли больницу.",
            "Cholera. Quarantine posts on the roads, and the peasants are kept from market. A rumor runs through the district: the doctors are poisoning the wells. In the next village the men wrecked the hospital.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Пить только кипячёную воду, как велит лекарь", "Drink only boiled water, as the doctor says"), roll = Roll(UM, 1, mapOf("gram" to 2, "gor" to 1)),
                    ok = { t("Над вами смеялись всё лето. Потом перестали: на вашем конце никто не умер.", "They laughed at you all summer. Then they stopped: nobody at your end of the village died.") },
                    bad = { val c = killOne(true); if (c != null) { kill(c, t("холера", "cholera")); t("Кипятили, да не уберегли: ${c.name}.", "You boiled the water, but could not save ${c.name}.") } else t("Холера заглянула, но ушла ни с чем.", "The cholera looked in, but left empty-handed.") }),
                Opt(t("Идти с мужиками громить карантин", "Go with the men to smash the quarantine post"), sin = "cruel", roll = Roll(SIL, -1, mapOf("gnev" to 2, "hrab" to 1)),
                    ok = { addSecret(t("${h.name} ${sx(h, "громил", "громила")} холерный карантин", "${h.name} helped wreck the cholera quarantine"), h); t("Заставу разметали. Зачинщиков потом искали, но вас не нашли. А холера прошла в ту же дыру.", "The post was torn down. They hunted for the ringleaders afterward but never found you. And the cholera came in through the same gap.") },
                    bad = { if (chance(.4)) { kill(h, t("холера", "cholera")); t("На второй день после бунта вас свалила та самая холера.", "Two days after the riot the very same cholera struck you down.") } else { addTr(h, "shram"); glory(-1); t("Пришли солдаты. Зачинщиков секли, вас — тоже.", "The soldiers came. The ringleaders were flogged, and you with them.") } }),
                Opt(t("Молиться и окуривать избу", "Pray and smoke out the hut"), roll = Roll(VER, 0, mapOf("bogom" to 1)),
                    ok = { t("Холера обошла ваш двор.", "The cholera passed your house by.") },
                    bad = { val c = killOne(true); if (c != null) { kill(c, t("холера", "cholera")); t("Не стало: ${c.name}.", "Gone: ${c.name}.") } else t("Переболели, но выжили.", "You fell ill, but lived.") }),
            )
        }),
    Event("krym", "war", Tx("Крымская война", "The Crimean War"), y0 = 1854, y1 = 1855, p = .6, cond = { h -> fire(if (!flagged("guild")) sonOf(h).firstOrNull() else null) },
        txt = { o -> t("Война с турком, англичанином и французом сразу. Севастополь в осаде. Объявлено государственное ополчение, и мир назвал ${o.xx.name}.",
            "War with the Turk, the Englishman and the Frenchman all at once. Sevastopol is besieged. A state militia has been called, and the commune has named ${o.xx.name}.") },
        opts = { o -> Rus.recruitOpts(this, o.xx, t("Крымская война", "the Crimean War")) }),
    Event("hutor", "field", Tx("Выход на хутор", "Leaving the Commune"), y0 = 1907, y1 = 1911, p = .6, cond = { h -> fire(age(h) >= 18) },
        txt = { t("Землемер нарезает отруба и хутора: кто хочет, может выйти из общины и взять свою землю одним куском, поставить двор прямо в поле и жить своим умом.",
            "The surveyor is marking out consolidated farms: whoever wishes may leave the commune and take his land in one piece, build his house right in the field and live by his own wits.") },
        opts = { o ->
            listOf(
                Opt(t("Выйти на хутор", "Take a farm of your own"), cost = 12, tag = t("12 сер. · двор растёт", "12 silver · the farm grows"), fx = { money(-12); g.dvor = minOf(4, g.dvor + 1); glory(1)
                    t("Своя земля одним клином, свой колодец, своя ветла у крыльца. Община смотрит косо, зато хлеб — свой.", "Your own land in one wedge, your own well, your own willow by the porch. The commune looks askance, but the bread is yours.") }),
                Opt(t("Взять отруб, а жить в селе", "Take the land in one piece but stay in the village"), fx = { money(4)
                    t("Полоски свели в одно поле. До него три версты, зато не надо делить межи с Кожемякиными.", "The strips were joined into one field. It's three versts away, but no more sharing boundaries with the Kozhemyakins.") }),
                Opt(t("Остаться в общине", "Stay in the commune"), fx = { t("Мир он и есть мир. Вместе пашем, вместе и горюем.", "The commune is the commune. We plow together, we grieve together.") }),
            )
        }),
    Event("golod33", "field_dry", Tx("Голодный год", "The Hungry Year"), y0 = 1932, y1 = 1933, p = .8, cond = { yes },
        txt = { t("Голод. Хлеб сдан по плану до зерна, на полях — объездчики: за горсть колосков с колхозного поля дают срок. Едят лебеду, жмых и кору.",
            "Famine. The grain has all gone to meet the plan, and riders patrol the fields: a handful of ears from the kolkhoz field earns a prison term. People eat goosefoot, oil cake and bark.") },
        opts = { o ->
            val h = o.h
            listOf(
                Opt(t("Собирать колоски ночью", "Glean ears at night"), roll = Roll(HIT, -1, mapOf("lzh" to 1, "trus" to 1)),
                    ok = { t("Карман колосков за ночь. Дети дотянули до нового хлеба.", "A pocketful of ears a night. The children held out till the new harvest.") },
                    bad = { if (chance(.4)) { kill(h, t("умер" + sx(h, "", "ла") + " в лагере", "died in a labor camp")); t("Поймали объездчики. Суд, вагон, лагерь. Писем не было.", "The riders caught you. A court, a freight car, a camp. No letters came.") }
                        else { money(-g.money); t("Поймали, но пожалели: отобрали всё, что было в доме, и отпустили.", "You were caught, but spared: they took everything in the house and let you go.") } }),
                Opt(t("Отнести в Торгсин бабушкины серьги", "Take grandmother's earrings to the Torgsin shop"), fx = { money(-part(.6))
                    t("Серьги ушли за пуд муки. Дотянули.", "The earrings went for a pood of flour. You held out.") }),
                Opt(t("Отдать детям свою долю", "Give your share to the children"), fx = { h[ZDR] -= 2; glory(1)
                    if (chance(.25)) { kill(h, t("голод", "hunger")); t("Дети выжили. ${sx(h, "Отец", "Мать")} — нет.", "The children lived. Their ${sx(h, "father", "mother")} did not.") }
                    else t("Дети выжили. Вы — тоже, хоть и стали на себя не похожи.", "The children lived. So did you, though you were no longer like yourself.") }),
            )
        }),
)
