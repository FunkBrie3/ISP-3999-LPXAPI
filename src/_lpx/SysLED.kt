package _lpx

import java.awt.Color

class SysLED private constructor(vararg val byte: Byte) {
    companion object {
        public fun static(note: Byte, velocity: Byte): SysLED = SysLED(0x00.toByte(), note, velocity)
        public fun flash(note: Byte, velocityA: Byte, velocityB: Byte): SysLED = SysLED(0x01.toByte(), note, velocityA, velocityB)
        public fun pulse(note: Byte, velocity: Byte): SysLED = SysLED(0x02.toByte(), note, velocity)

        /**
         * RGB range (0-255)
         */
        public fun rgb(note: Byte, r: Int, g: Int, b: Int): SysLED = SysLED(0x03.toByte(), note, r.shr(1).toByte(), g.shr(1).toByte(), b.shr(1).toByte())
        public fun rgb(note: Byte, color: Color) : SysLED = SysLED(0x03.toByte(), note, (color.red shr 1).toByte(), (color.green shr 1).toByte(), (color.blue shr 1).toByte())
    }
}