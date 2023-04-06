package me.ambrie3.draw.interact

import me.ambrie3.draw.DrawingPad
import me.ambrie3.draw.DrawingPadState
import me.ambrie3.lpxapi.LaunchpadReceiver
import java.util.Date
import javax.sound.midi.MidiMessage

class ColorButton public constructor(val dp: DrawingPad): LaunchpadReceiver {
    private val arrDelay: Array<Long> = Array(8) { Date().time }
    private val delayDifference = 250L
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        if(dp.state != DrawingPadState.MAIN) return

        val arr: ByteArray = message.message
        if(arr[0] != 0xB0.toByte()) return

        if(arr[1] % 10 != 0x09) return
        val num = (arr[1] - 0x13) / 10

        if(dp.debug) println("${javaClass.name} >> Color $num signal received.")

        if(arr[2] == 0.toByte()) {
            dp.focusColor = num
            if(Date().time - arrDelay[num] > delayDifference) {
                if(dp.debug) println("${javaClass.name} >> Color $num long signal press detected.")
                dp.state = DrawingPadState.COLOR_SELECT
            } else {
                if(dp.debug) println("${javaClass.name} >> Color $num short signal press detected.")
                dp.redraw()
            }
        } else {
            arrDelay[num] = Date().time
        }
    }
}