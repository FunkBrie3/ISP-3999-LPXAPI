package me.ambrie3.draw

import java.awt.Color

class ColorGroup {
    private val colors: ArrayList<Color> = arrayListOf()
    var color: Color = Color.BLACK

    fun addColor(color: Color) {
        colors.add(color)
        var r: Int = 0; var g: Int = 0; var b: Int = 0
        for(c in colors) {
            r += c.red; g += c.green; b += c.blue
        }
        r /= colors.size; g /= colors.size; b /= colors.size;
        this.color = Color(r, g, b)
    }
    fun erase() {
        colors.clear()
        color = Color.BLACK
    }
}