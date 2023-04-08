package me.ambrie3.draw.interact

import me.ambrie3.draw.ColorGroup
import me.ambrie3.draw.DrawingPad
import me.ambrie3.draw.DrawingPadState
import me.ambrie3.lpxapi.LaunchpadReceiver
import me.ambrie3.lpxapi.SysLED
import java.awt.Point
import javax.sound.midi.MidiMessage

class MainPage(val dp: DrawingPad): LaunchpadReceiver {
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        if(dp.state != DrawingPadState.MAIN) return
        val arr: ByteArray = message.message
        if(!(arr[0] == 0xB0.toByte() || arr[0] == 0x90.toByte())) return
        if(arr[2] == 0x00.toByte()) return

        if(dp.debug) println("${javaClass.name} >> Signal received.")

        if(arr[0] == 0xB0.toByte()) {
            val point: Point = DrawingPad.byteToTwoDim(arr[1])
            val colorGroup: ColorGroup = dp.frameArray[dp.frameIndex].pixels[point.y][point.x]
            colorGroup.addColor(dp.colors[dp.focusColor])
            dp.draw()
        } else if(arr[0] == 0x90.toByte()) {

        }
    }

    fun draw(): ArrayList<SysLED> {
        return dp.frameArray[dp.frameIndex].draw(false)
    }
}