package me.ambrie3.draw

import me.ambrie3.draw.interact.ColorButton
import me.ambrie3.draw.interact.ColorSelectButton
import me.ambrie3.draw.interact.OffButton
import me.ambrie3.lpxapi.*
import java.awt.Point
import java.io.Closeable
import javax.sound.midi.ShortMessage

class DrawingPad public constructor(keyIn: String = searchKeyIn, keyOut: String = searchKeyOut, val debug: Boolean = false): Closeable {
    val lpx: LPX
    val colors: ByteArray = ByteArray(8) { 1.toByte() }
    var focusColor: Int = 0
        public set(value) {
            if(debug) println("${javaClass.name} >> focusColor updated to ${value}.")
            field = value
        }
    private val interact: Array<LaunchpadReceiver> = arrayOf(
        ColorButton(this),
        ColorSelectButton(this),
        OffButton(this)
    )
    public var state: DrawingPadState = DrawingPadState.MAIN
        public set(value) {
            if(debug) println("${javaClass.name} >> state updated to ${value.name}.")
            field = value
            redraw()
        }

    init {
        lpx = LPX(keyIn, keyOut)
        interact.forEach { lpx.addReceiver(it) }
        lpx.sendSysexModeProgrammer()
        redraw()
    }

    companion object {
        fun byteToTwoDim(b: Byte): Point {
            return Point((b - 0x0B) % 10, (b - 0x0B) / 10)
        }
        fun twoDimToByte(x: Int, y: Int): Byte {
            return (0x0B + x + (10 * y)).toByte()
        }
    }

    public fun redraw() {
        if(debug) println("${javaClass.name} >> Redrawing display.")
        lpx.sendStaticColVel(ButtonVal.h9.b(), 5)
        lpx.sendStaticColVel(ButtonVal.i9.b(), 3)
        when(state) {
            DrawingPadState.MAIN -> {
                for(i in 0 until 8) {
                    lpx.sendRaw(ShortMessage(
                        if(i == focusColor) 0x92 else 0x90,
                        (0x13 + (i * 10)),
                        colors[i].toInt()
                    ))
                }

                for(j in arrayOf(0x5B, 0x5C, 0x5D, 0x5E, 0x5F, 0x60, 0x61))
                    lpx.sendSimpleOff(j.toByte())
            }
            DrawingPadState.COLOR_SELECT -> {
                val page = (interact[1] as ColorSelectButton).page
                for(x in 0 until 8) {
                    for(y in 0 until 8) {
                        lpx.sendStaticColVel(twoDimToByte(x, y), ((page * 64) + (x + (8 * y))).toByte())
                    }
                }
                lpx.sendStaticColVel(ButtonVal.c9.b(), if(page == 0) 23 else 21)
                lpx.sendStaticColVel(ButtonVal.d9.b(), if(page == 0) 21 else 23)

                for(b in arrayOf(0x5B, 0x5C, 0x5F, 0x60, 0x61, 0x63, 0x59, 0x4F, 0x45, 0x3B, 0x31, 0x27, 0x1D, 0x13))
                    lpx.sendSimpleOff(b.toByte())
            }
        }
    }

    override fun close() {
        if(debug) println("${javaClass.name} >> Closing program.")
        ButtonVal.values().forEach { lpx.sendSimpleOff(it.b()) }
        lpx.close()
    }
}