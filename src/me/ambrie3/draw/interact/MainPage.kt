package me.ambrie3.draw.interact

import me.ambrie3.draw.ColorGroup
import me.ambrie3.draw.DrawingPad
import me.ambrie3.draw.DrawingPadState
import me.ambrie3.draw.HSVColor
import me.ambrie3.lpxapi.ButtonVal
import me.ambrie3.lpxapi.LaunchpadReceiver
import me.ambrie3.lpxapi.SysLED
import java.awt.Color
import java.awt.Point
import java.util.*
import javax.sound.midi.MidiMessage
import kotlin.collections.ArrayList

class MainPage(val dp: DrawingPad): LaunchpadReceiver {
    // 0x90 = Main
    // 0xB0 = Side
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        if(dp.state != DrawingPadState.MAIN) return
        val arr: ByteArray = message.message
        if(!(arr[0] == 0xB0.toByte() || arr[0] == 0x90.toByte())) return

        if(dp.debug) println("${javaClass.name} >> Signal received.")

        colorSelect(arr)

        drawPixel(arr)

        if(arr[0] == 0xB0.toByte() && arr[1] == ButtonVal.e9.b() && arr[2] != 0.toByte())
            dp.focusColor = 8

        if(arr[0] == 0xB0.toByte() && arr[1] == ButtonVal.h9.b() && arr[2] != 0.toByte())
            dp.close()
    }

    fun drawPixel(arr: ByteArray) {
        if(arr[0] == 0x90.toByte() && arr[2] != 0.toByte()) {
            val point: Point = DrawingPad.byteToTwoDim(arr[1])
            val colorGroup: ColorGroup = dp.frameArray[dp.frameIndex].pixels[point.y][point.x]
            if(dp.focusColor == 8) colorGroup.erase() else colorGroup.addColor(dp.colors[dp.focusColor])
            dp.draw()
        }
    }

    private val arrColorDelay: Array<Long> = Array(8) { Date().time }
    private val colorDelayDifference = 250L
    fun colorSelect(arr: ByteArray) {
        if(arr[1] % 10 != 0x09) return
        val num = (arr[1] - 0x13) / 10

        if(arr[2] == 0.toByte()) {
            dp.focusColor = num
            if(Date().time - arrColorDelay[num] > colorDelayDifference) {
                if(dp.debug) println("${javaClass.name} >> Color $num long signal press detected.")
                dp.state = DrawingPadState.COLOR_SELECT
            } else {
                if(dp.debug) println("${javaClass.name} >> Color $num short signal press detected.")
                dp.draw()
            }
        } else {
            arrColorDelay[num] = Date().time
        }
    }
    fun draw(): ArrayList<SysLED> {
        val arrsysled: ArrayList<SysLED> = dp.frameArray[dp.frameIndex].draw(false)
        for(i in 0 until 8) {
            val hsvcolor: HSVColor = dp.colors[i]
            arrsysled.add(SysLED.rgb(
                (0x13 + (i * 10)).toByte(),
                HSVColor.HSVtoRGB(hsvcolor.h, hsvcolor.s, hsvcolor.v)
            ))
        }
        arrsysled.add(SysLED.rgb(ButtonVal.e9.b(), Color(255, 0, 240)))
        arrsysled.add(SysLED.rgb(ButtonVal.h9.b(), Color.RED))
        arrsysled.add(SysLED.rgb(ButtonVal.a9.b(), Color.BLACK))
        arrsysled.add(SysLED.rgb(ButtonVal.b9.b(), Color.BLACK))
        arrsysled.add(SysLED.rgb(ButtonVal.c9.b(), Color.BLACK))
        arrsysled.add(SysLED.rgb(ButtonVal.d9.b(), Color.BLACK))
        return arrsysled
    }
}