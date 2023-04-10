package me.ambrie3.draw

import java.awt.Color

class HSVColor public constructor(var h: Int, var s: Int, var v: Int) {
    companion object {
        private fun HtoScale(a: Int): Int {
            return (((a.toFloat() / 60f) % 1) * 255).toInt()
        }
        fun HSVtoRGB(h: Int, s: Int, v: Int): Color {
            // H -> 0-360, S -> 0-100, V -> 0-100

            // Initial hue calculations
            var r: Int = when(h / 60) {
                0 -> 255
                1 -> 255 - HtoScale(h)
                2 -> 0
                3 -> 0
                4 -> HtoScale(h)
                5 -> 255
                else -> 255
            }
            var g: Int = when(h / 60) {
                0 -> HtoScale(h)
                1 -> 255
                2 -> 255
                3 -> 255 - HtoScale(h)
                4 -> 0
                5 -> 0
                else -> 0
            }
            var b: Int = when(h / 60) {
                0 -> 0
                1 -> 0
                2 -> HtoScale(h)
                3 -> 255
                4 -> 255
                5 -> 255 - HtoScale(h)
                else -> 0
            }

            // Saturation calculations
            val sMult: Float = 1 - (s.toFloat() / 100)
            r += ((255 - r) * sMult).toInt()
            g += ((255 - g) * sMult).toInt()
            b += ((255 - b) * sMult).toInt()

            // Value calculations
            r = (r * (v.toFloat() / 100)).toInt()
            g = (g * (v.toFloat() / 100)).toInt()
            b = (b * (v.toFloat() / 100)).toInt()

            return Color(r, g, b)
        }
    }
}