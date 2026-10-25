# Seventh Generation · Седьмое колено

A dynasty game in the spirit of Crusader Kings, told as a folk tale. A beggar woman (in Rus), a herbwoman on the pyre (in medieval Europe) or a white fox from an Inari shrine (in Japan) curses your founder's family "to the seventh generation". Carry the blood to the seventh generation and the curse is lifted; if the last of the line dies childless, the family ends.

- Every deed is decided by a bean drawn from a bag: white is luck, black is misfortune, red is the curse. Stats, traits and relics add white beans.
- Children are born with blood traits and grow into tempers; acting against your temper lays stones on the soul.
- When the head of the family dies you choose any living descendant to play next. Pass over the eldest son and he bears a grudge.
- Each land has its own names, estates, events, history and art: Rus as a lubok print, medieval Europe as an illuminated manuscript, Japan as an ukiyo-e woodblock print.
- In English and Russian.

Free, no ads, no account, no internet.

## Build

```
./gradlew testDebugUnitTest assembleDebug
./gradlew -Pdemo assembleDebug                                   # app.seventh.demo with a family three generations in
./gradlew testDebugUnitTest -Prender --tests '*RenderArt*' --tests '*Screens*'   # art and every screen to app/build/art
```

The rules are in `domain/Engine.kt`; each land is one file (`domain/Rus.kt`, `domain/Europe.kt`, `domain/Japan.kt`) with its names, events and history. All pictures are drawn in code in `ui/art`.

Fonts: [Ruslan Display](https://fonts.google.com/specimen/Ruslan+Display), [Kurale](https://fonts.google.com/specimen/Kurale), [Alegreya and Alegreya SC](https://fonts.google.com/specimen/Alegreya), [Zen Old Mincho](https://fonts.google.com/specimen/Zen+Old+Mincho) (subset to Latin and Cyrillic), SIL Open Font License.
