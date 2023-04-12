package me.ambrie3.draw

import me.ambrie3.lpxapi.SysLED

class Frame public constructor() {
    val pixels: Array<Array<ColorGroup>> = Array(9) { Array(9) { ColorGroup() } }
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
    fun clone(): Frame {
        val out = Frame()
        for(x in 0 until 9) {
            for(y in 0 until 9) {
                out.pixels[y][x] = this.pixels[y][x].clone()
            }
        }
        return out
    }
}