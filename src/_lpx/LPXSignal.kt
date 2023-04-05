package _lpx

enum class LPXSignal private constructor(vararg val bytes: Byte) {
    SYSEX_HEADER(0xF0.toByte(), 0x00.toByte(), 0x20.toByte(), 0x29.toByte(), 0x02.toByte(), 0x0C.toByte()),
    SYSEX_FOOTER(0xF7.toByte()),

    SYSEX_MODE_LIVE(*SYSEX_HEADER.bytes, 0x0E.toByte(), 0x00.toByte(), *SYSEX_FOOTER.bytes),
    SYSEX_MODE_PROGRAMMER(*SYSEX_HEADER.bytes, 0x0E.toByte(), 0x01.toByte(), *SYSEX_FOOTER.bytes),

    SYSEX_SLEEP_INS(*SYSEX_HEADER.bytes, 0x09.toByte(), /*Ins, */ *SYSEX_FOOTER.bytes),

    SYSEX_BRIGHTNESS_INS(*SYSEX_HEADER.bytes, 0x08.toByte(), /*Ins, */ *SYSEX_FOOTER.bytes),

    SYSEX_TEST_SCROLL_INS(*SYSEX_HEADER.bytes, 0x07.toByte(), /*InsN, */ *SYSEX_FOOTER.bytes),

    SYSEX_LED_INS(*SYSEX_HEADER.bytes, 0x03.toByte(), /*InsN, */ *SYSEX_FOOTER.bytes);

    public fun byteIns(vararg insert: Byte): ByteArray {
        val l: MutableList<Byte> = bytes.toMutableList()
        l.addAll(bytes.size - 1, insert.toMutableList())
        return l.toByteArray()
    }
}