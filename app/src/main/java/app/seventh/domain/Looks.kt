package app.seventh.domain

/** The colours a person can be drawn in. Kept as ARGB ints so the save file and the art agree. */
object Looks {
    val SKIN = intArrayOf(0xFFECC6A0.toInt(), 0xFFE0B48A.toInt(), 0xFFD4A27A.toInt())
    val HAIR = intArrayOf(0xFF2B1D14.toInt(), 0xFF4A2D18.toInt(), 0xFF6B4423.toInt(), 0xFF8A5A2A.toInt(), 0xFFB8862F.toInt(), 0xFFD6B56E.toInt())
    val CLOTH_M = intArrayOf(0xFFB8322A.toInt(), 0xFFE8E0CC.toInt(), 0xFF3A5A8C.toInt(), 0xFF6F7A4A.toInt(), 0xFF8A5A3A.toInt())
    val CLOTH_F = intArrayOf(0xFFB8322A.toInt(), 0xFF3A5A8C.toInt(), 0xFF4E7D3A.toInt(), 0xFF7A1F1A.toInt(), 0xFFC8902F.toInt())
    val KERCH = intArrayOf(0xFFF4ECD8.toInt(), 0xFFB8322A.toInt(), 0xFFD9A441.toInt(), 0xFF3A5A8C.toInt(), 0xFFE07A2A.toInt())

    /** Grey hair comes with age. */
    fun hairAt(hair: Int, age: Int): Int = when {
        age > 62 -> 0xFFD8D2C4.toInt()
        age > 48 -> 0xFF9A9488.toInt()
        else -> hair
    }
}
