package me.ambrie3.draw

import me.ambrie3.lpxapi.SysLED
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object SaveAnim {
    public fun Color.equalsC(other: Color): Boolean {
        return this.red == other.red && this.blue == other.blue && this.green == other.green
    }

    public fun save(fileName: String, dp: DrawingPad) {
        val file: File = File("${fileName}.lpa")
        if(!file.exists()) file.createNewFile()
        val outStream: FileOutputStream = FileOutputStream(file)
        val virtualPng: Array<Array<Color>> = Array(9) { Array(9) { Color.BLACK } }
        for(frame in dp.frameArray) {
            for(x in 0 until 9)
                for(y in 0 until 9) {
                    val c: Color = frame.pixels[y][x].color
                    if(virtualPng[y][x] == c) continue
                    virtualPng[y][x] = c
                    outStream.write(byteArrayOf(DrawingPad.twoDimToByte(x, y)))
                    outStream.write(c.rgb)
                }
            outStream.write(byteArrayOf(-1))
        }
        outStream.close()
    }
    public fun load(fileName: String): Array<Array<SysLED>> {
        val file: File = File("${fileName}.lpa")
        val inStream: FileInputStream = FileInputStream(file)

    }
}