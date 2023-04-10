package me.ambrie3.draw

import java.awt.Color

class ColorGroup {
    private val colors: ArrayList<HSVColor> = arrayListOf()
    var color: Color = Color.BLACK

    fun addColor(hsvColor: HSVColor) {
        colors.add(hsvColor)
        var h: Int = 0; var s: Int = 0; var v: Int = 0
        for(c in colors) {
            h += c.h
            s += c.s
            v += c.v
        }
        h /= colors.size; s /= colors.size; v /= colors.size
        println("Color -> $h $s $v -> ${colors.size}")
        this.color = HSVColor.HSVtoRGB(h, s, v)
    }
    fun erase() {
        colors.clear()
        color = Color.BLACK
    }
}