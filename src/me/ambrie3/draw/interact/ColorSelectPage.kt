package me.ambrie3.draw.interact

import me.ambrie3.draw.DrawingPad
import me.ambrie3.draw.DrawingPadState
import me.ambrie3.lpxapi.ButtonVal
import me.ambrie3.lpxapi.LaunchpadReceiver
import me.ambrie3.lpxapi.SysLED
import java.awt.Color
import java.awt.Point
import javax.sound.midi.MidiMessage
import kotlin.collections.ArrayList

class ColorSelectPage public constructor(val dp: DrawingPad): LaunchpadReceiver {
    var s: Int = 100
    var v: Int = 100

    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        if(dp.state != DrawingPadState.COLOR_SELECT) return
        val arr: ByteArray = message.message
        if(!(arr[0] == 0xB0.toByte() || arr[0] == 0x90.toByte())) return
        if(arr[2] != 0x00.toByte()) return

        if(dp.debug) println("${javaClass.name} >> Signal received.")

        if(arr[0] == 0xB0.toByte()) {
            val point: Point = DrawingPad.byteToTwoDim(arr[1])
            var h: Int = 0
            for(x in 0 until 8)
                for(y in 0 until 8) {
                    if((x == 0 || x == 7) && (y == 0 || y == 7)) continue
                    if(point.x == x && point.y == y) {
                        dp.colors[dp.focusColor] = DrawingPad.HSVtoRGB(h, s, v)
                        dp.state = DrawingPadState.MAIN
                        return
                    }
                    h += 6
                }
        } else {
            when(arr[1]) {
                ButtonVal.a9.b() -> if(s != 0) s -= 10
                ButtonVal.b9.b() -> if(s != 100) s += 10
                ButtonVal.c9.b() -> if(v != 0) v -= 10
                ButtonVal.d9.b() -> if(v != 100) v += 10
                ButtonVal.h9.b() -> {
                    dp.close()
                    return
                }
                else -> return
            }
            dp.draw()
        }
    }

    fun draw(): ArrayList<SysLED> {
        val sysledarr: ArrayList<SysLED> = arrayListOf()
        var h: Int = 0
        for(x in 0 until 8)
            for(y in 0 until 8) {
                if((x == 0 || x == 7) && (y == 0 || y == 7)) {
                    sysledarr.add(SysLED.rgb(
                        DrawingPad.twoDimToByte(x, y),
                        Color.BLACK
                    ))
                } else {
                    sysledarr.add(SysLED.rgb(
                        DrawingPad.twoDimToByte(x, y),
                        DrawingPad.HSVtoRGB(h, s, v)
                    ))
                    h += 6
                }
            }
        for(b in ButtonVal.side()) {
            when(b) {
                ButtonVal.a9.b() -> sysledarr.add(SysLED.rgb(b,
                    if(s == 0) Color(0, 0, 0) else Color(0, 255, 0)))
                ButtonVal.b9.b() -> sysledarr.add(SysLED.rgb(b,
                    if(s == 100) Color(0, 0, 0) else Color(0, 255, 0)))
                ButtonVal.c9.b() -> sysledarr.add(SysLED.rgb(b,
                    if(v == 0) Color(0, 0, 0) else Color(0, 255, 0)))
                ButtonVal.d9.b() -> sysledarr.add(SysLED.rgb(b,
                    if(v == 100) Color(0, 0, 0) else Color(0, 255, 0)))
                ButtonVal.h9.b() -> sysledarr.add(SysLED.rgb(ButtonVal.h9.b(), Color.RED))
                else -> sysledarr.add(SysLED.rgb(b, Color.BLACK))
            }
        }

        return sysledarr
    }
}