package me.ambrie3.draw

import me.ambrie3.lpxapi.SysLED
import java.awt.Point

class Frame public constructor(val dp: DrawingPad) {
    val pixels: Array<Array<ColorGroup>> = Array(8) { Array(8) { ColorGroup() } }
    fun draw(isFullDraw: Boolean = false): ArrayList<SysLED> {
        val sysledarr: ArrayList<SysLED> = arrayListOf()

        for(x in 0 until if(isFullDraw) 9 else 8)
            for(y in 0 until if(isFullDraw) 9 else 8) {
                sysledarr.add(SysLED.rgb(
                    DrawingPad.twoDimToByte(x, y),
                    pixels[y][x].color
                ))
            }

        return sysledarr
    }
}