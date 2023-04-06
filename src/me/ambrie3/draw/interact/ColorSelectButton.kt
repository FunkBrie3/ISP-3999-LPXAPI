package me.ambrie3.draw.interact

import me.ambrie3.draw.DrawingPad
import me.ambrie3.draw.DrawingPadState
import me.ambrie3.lpxapi.LaunchpadReceiver
import java.awt.Point
import java.util.*
import javax.sound.midi.MidiMessage

class ColorSelectButton public constructor(val dp: DrawingPad): LaunchpadReceiver {
    var page: Int = 0
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        if(dp.state != DrawingPadState.COLOR_SELECT) return

        val arr: ByteArray = message.message

        if(!(arr[0] == 0xB0.toByte() || arr[0] == 0x90.toByte())) return

        if(arr[2] != 0x00.toByte()) return

        if(dp.debug) println("${javaClass.name} >> Signal received.")

        val point: Point = DrawingPad.byteToTwoDim(arr[1])
        if(point.x < 8 && point.y < 8) {
            if(dp.debug) println("${javaClass.name} >> Color select signal received.")
            dp.colors[dp.focusColor] = ((page * 64) + (point.x + (8 * point.y))).toByte()
            if(dp.debug) println("${javaClass.name} >> Color ${dp.focusColor} set to ${dp.colors[dp.focusColor]}")
            dp.state = DrawingPadState.MAIN
        } else if(point.y == 8) {
            if(point.x == 2 && page == 1) {
                page = 0
                if(dp.debug) println("${javaClass.name} >> Page 0 selected.")
                dp.redraw()
            } else if(point.x == 3 && page == 0) {
                page = 1
                if(dp.debug) println("${javaClass.name} >> Page 1 selected.")
                dp.redraw()
            }
        }
    }
}