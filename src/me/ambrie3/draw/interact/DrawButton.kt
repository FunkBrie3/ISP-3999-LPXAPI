package me.ambrie3.draw.interact

import me.ambrie3.draw.DrawingPad
import me.ambrie3.draw.DrawingPadState
import me.ambrie3.lpxapi.LaunchpadReceiver
import java.awt.Point
import java.util.Date
import javax.sound.midi.MidiMessage

class DrawButton public constructor(val dp: DrawingPad): LaunchpadReceiver {
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        if(dp.state != DrawingPadState.MAIN) return

        val arr: ByteArray = message.message
        if(arr[0] != 0x90.toByte()) return

        val point: Point = DrawingPad.byteToTwoDim(arr[1])
        if(point.x == 8 || point.y == 8) return

        if(arr[2] == 0.toByte()) return

        if(dp.debug) println("${javaClass.name} >> Draw ${arr[1]} signal detected.")

        dp.defaultFrame.draw(arr[1], dp.colors[dp.focusColor])
    }
}