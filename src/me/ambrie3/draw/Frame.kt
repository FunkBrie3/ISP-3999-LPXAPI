package me.ambrie3.draw

import java.awt.Point

class Frame public constructor(val dp: DrawingPad) {
    val aa: Array<Array<Byte>> = Array(8) { Array(8) { 0.toByte() } }
    fun draw(note: Byte, velocity: Byte) {
        dp.lpx.sendStaticColVel(note, velocity)
        val point: Point = DrawingPad.byteToTwoDim(note)
        aa[point.y][point.x] = velocity
    }

    /*
    fun isEmpty(): Boolean {
        for(x in 0 until 8)
            for(y in 0 until 8)
                if(aa[y][x] == 255.toByte()) return true
        return false
    }*/
}