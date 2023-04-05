package me.ambrie3.draw

import me.ambrie3.lpxapi.LPX
import me.ambrie3.lpxapi.LaunchpadReceiver
import me.ambrie3.lpxapi.searchKeyIn
import me.ambrie3.lpxapi.searchKeyOut
import me.ambrie3.draw.interact.ColorButton
import java.awt.Color

class DrawingPad public constructor(keyIn: String = searchKeyIn, keyOut: String = searchKeyOut, val debug: Boolean = false) {
    val lpx: LPX
    val colors: ByteArray = ByteArray(8) { 0.toByte() }
    var focusColor: Int = 0
    private val interact: Array<LaunchpadReceiver> = arrayOf(
        ColorButton(this)
    )
    public var state: DrawingPadState = DrawingPadState.MAIN
        public set(value) {
            if(debug) println("${javaClass.name} >> DrawingPadState updated to ${value.name}.")
            field = value
            redraw()
        }

    init {
        lpx = LPX(keyIn, keyOut)
        interact.forEach { lpx.addReceiver(it) }
        lpx.sendSysexModeProgrammer()
    }

    public fun redraw() {
        if(debug) println("${javaClass.name} >> Redrawing display.")
    }
}