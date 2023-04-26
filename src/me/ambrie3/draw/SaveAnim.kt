package me.ambrie3.draw

import me.ambrie3.lpxapi.SysLED
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.LinkedList

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
                    outStream.write(DrawingPad.twoDimToByte(x, y).toUByte().toInt())
                    outStream.write(c.red)
                    outStream.write(c.green)
                    outStream.write(c.blue)
                }
            outStream.write(0);
        }
        outStream.close()
    }
    public fun load(fileName: String): ArrayList<ArrayList<SysLED>> {
        val file: File = File(fileName)
        val inStream: FileInputStream = FileInputStream(file)
        val sysledanim: ArrayList<ArrayList<SysLED>> = arrayListOf(arrayListOf())
        var sysledframe: ArrayList<SysLED> = arrayListOf()
        while(inStream.available() > 0) {
            var b: Int = inStream.read()
            if(b == 0) {
                sysledanim.add(sysledframe)
                sysledframe = arrayListOf()
                continue
            }
            sysledframe.add(SysLED.rgb(b.toByte(), Color(inStream.read(),inStream.read(),inStream.read())))
       }
        return sysledanim
    }
}