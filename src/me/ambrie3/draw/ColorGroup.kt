package me.ambrie3.draw

import java.awt.Color

class ColorGroup {
    private var colors: HSVColor = HSVColor(0, 0, 0)
    var weight: Int = 0
    var color: Color = Color.BLACK

    fun addColor(hsvColor: HSVColor) {
        colors.h += hsvColor.h
        colors.s += hsvColor.s
        colors.v += hsvColor.v
        weight++
        this.color = HSVColor.HSVtoRGB(
            colors.h / weight,
            colors.s / weight,
            colors.v / weight
        )
    }
    fun erase() {
        colors.h = 0; colors.s = 0; colors.v = 0
        weight = 0
        color = Color.BLACK
    }
    fun clone(): ColorGroup {
        val out = ColorGroup()
        out.weight = this.weight
        out.colors = HSVColor(colors.h, colors.s, colors.v)
        out.color = Color(color.red, color.green, color.blue)
        return out
    }
}