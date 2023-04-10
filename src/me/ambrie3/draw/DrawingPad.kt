package me.ambrie3.draw

import me.ambrie3.draw.interact.ColorSelectPage
import me.ambrie3.draw.interact.MainPage
import me.ambrie3.lpxapi.*
import java.awt.Color
import java.awt.Point
import java.io.Closeable
import java.util.*
import javax.sound.midi.ShortMessage
import kotlin.collections.ArrayList

class DrawingPad public constructor(keyIn: String = searchKeyIn, keyOut: String = searchKeyOut, val debug: Boolean = false): Closeable {
    // The launchpad I/O for the program.
    val lpx: LPX

    // Each individual color stored actively in the program as usable.
    val colors: Array<HSVColor> = Array(8) { HSVColor(0, 0, 100) }
    // The color being focused on (index of colors ByteArray)
    var focusColor: Int = 7
        public set(value) {
            if(debug) println("${javaClass.name} >> focusColor updated to ${value}.")
            field = value
        }

    // Each LaunchpadReceiver is listed below in a group.
    private val mainPage: MainPage = MainPage(this)
    private val colorSelectPage: ColorSelectPage = ColorSelectPage(this)

    // The current state of the drawing program. Used with LaunchpadReceivers and drawing. Setting var calls redraw()
    public var state: DrawingPadState = DrawingPadState.MAIN
        public set(value) {
            if(debug) println("${javaClass.name} >> state updated to ${value.name}.")
            field = value
            draw()
        }

    // The current frame via index + The frame array
    val frameArray: ArrayList<Frame> = arrayListOf(Frame(this))
    val frameIndex: Int = 0

    // Initializer (called with constructor)
    init {
        lpx = LPX(keyIn, keyOut)

        lpx.addReceiver(mainPage)
        lpx.addReceiver(colorSelectPage)

        lpx.sendSysexModeProgrammer()
        draw()
    }

    // Companion object is the same as "static" in Java, it's simply a singleton companion to the class.
    companion object {
        /**
         * @param b The byte to convert
         * @return A Point object, with x and y
         */
        fun byteToTwoDim(b: Byte): Point {
            return Point((b - 0x0B) % 10, (b - 0x0B) / 10)
        }

        /**
         * @param x The x value
         * @param y The y value
         * @return A byte representing the corresponding value on LPX. (0, 0) is bottom left, (9, 9) is top right.
         */
        fun twoDimToByte(x: Int, y: Int): Byte {
            return (0x0B + x + (10 * y)).toByte()
        }
    }

    public fun draw() {
        if(debug) println("${javaClass.name} >> Redrawing display.")
        when(state) {
            DrawingPadState.MAIN -> {
                lpx.sendSysexLED(*mainPage.draw().toTypedArray())
                /*
                for(x in 0 until 8)
                    for(y in 0 until 8) {
                        if(frame != null) lpx.sendStaticColVel(twoDimToByte(x, y), frame.aa[y][x])
                        else lpx.sendStaticColVel(twoDimToByte(x, y), 0)
                    }

                for(i in 0 until 8) {
                    lpx.sendRaw(ShortMessage(
                        if(i == focusColor) 0x92 else 0x90,
                        (0x13 + (i * 10)),
                        colors[i].toInt()
                    ))
                }

                for(j in arrayOf(0x5B, 0x5C, 0x5D, 0x5E, 0x5F, 0x60, 0x61))
                    lpx.sendSimpleOff(j.toByte())

                 */
            }
            DrawingPadState.COLOR_SELECT -> {
                lpx.sendSysexLED(*colorSelectPage.draw().toTypedArray())
            }
        }
    }

    override fun close() {
        if(debug) println("${javaClass.name} >> Closing program.")
        ButtonVal.values().forEach { lpx.sendSimpleOff(it.b()) }
        lpx.close()
    }
}