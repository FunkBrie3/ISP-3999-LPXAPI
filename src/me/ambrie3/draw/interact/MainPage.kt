package me.ambrie3.draw.interact

import me.ambrie3.draw.*
import me.ambrie3.lpxapi.ButtonVal
import me.ambrie3.lpxapi.LaunchpadReceiver
import me.ambrie3.lpxapi.SysLED
import java.awt.Color
import java.awt.Point
import java.awt.desktop.UserSessionEvent
import java.io.File
import java.util.*
import javax.sound.midi.MidiMessage
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.UIManager
import kotlin.collections.ArrayList

class MainPage(val dp: DrawingPad): LaunchpadReceiver {
    // 0x90 = Main
    // 0xB0 = Side
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        if(dp.state != DrawingPadState.MAIN) return
        val arr: ByteArray = message.message
        if(!(arr[0] == 0xB0.toByte() || arr[0] == 0x90.toByte())) return

        if(dp.debug) println("${javaClass.name} >> Signal received.")

        colorSelect(arr)

        drawPixel(arr)

        frameManager(arr)

        export(arr)

        if(arr[0] == 0xB0.toByte() && arr[1] == ButtonVal.i8.b() && arr[2] != 0.toByte())
            dp.focusColor = 7

        if(arr[0] == 0xB0.toByte() && arr[1] == ButtonVal.h9.b() && arr[2] != 0.toByte())
            dp.close()
    }

    fun frameManager(arr: ByteArray) {
        if(arr[0] != 0xB0.toByte() || arr[2] == 0.toByte()) return
        // Clone frame behind
        if(arr[1] == ButtonVal.a9.b())
            if(dp.frameArray.size > 1 && dp.frameIndex != 0) {
                dp.frameArray[dp.frameIndex] = dp.frameArray[dp.frameIndex - 1].clone()
                dp.draw()
            }

        // Clone frame in front
        if(arr[1] == ButtonVal.b9.b())
            if(dp.frameArray.size > 1 && dp.frameIndex != dp.frameArray.size - 1) {
                dp.frameArray[dp.frameIndex] = dp.frameArray[dp.frameIndex + 1].clone()
                dp.draw()
            }

        // Remove frame
        if(arr[1] == ButtonVal.e9.b() && dp.frameArray.size > 1) {
            dp.frameArray.removeAt(dp.frameIndex)
            if(dp.frameIndex != 0) dp.frameIndex--
            dp.draw()
        }

        // Adds frame
        if(arr[1] == ButtonVal.f9.b() && dp.frameArray.size < 128) {
            dp.frameArray.add(dp.frameIndex + 1, Frame())
            dp.frameIndex++
            dp.draw()
        }

        // Back frame
        if(arr[1] == ButtonVal.c9.b() && dp.frameIndex != 0) {
            dp.frameIndex--
            dp.draw()
        }

        // Forward frame
        if(arr[1] == ButtonVal.d9.b() && dp.frameIndex != dp.frameArray.size - 1) {
            dp.frameIndex++
            dp.draw()
        }
    }
    fun drawPixel(arr: ByteArray) {
        if(arr[0] == 0x90.toByte() && arr[2] != 0.toByte()) {
            val point: Point = DrawingPad.byteToTwoDim(arr[1])
            val colorGroup: ColorGroup = dp.frameArray[dp.frameIndex].pixels[point.y][point.x]
            if(dp.focusColor == 7) colorGroup.erase() else colorGroup.addColor(dp.colors[dp.focusColor])
            dp.draw()
        }
    }

    /**
     * Exports to a .lpa file.
     */
    fun export(arr: ByteArray) {
        if(!(arr[0] == 0xB0.toByte() && arr[1] == ButtonVal.g9.b() && arr[2] != 0.toByte())) return
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
        val fileChooser: JFileChooser = JFileChooser()
        val parentFrame: JFrame = JFrame()
        fileChooser.dialogTitle = "Choose a Directory + Name"
        val sel: Int = fileChooser.showSaveDialog(parentFrame)

        if (sel == JFileChooser.APPROVE_OPTION) {
            val file: File = fileChooser.selectedFile
            SaveAnim.save("${file.name}.lpa", dp)
        }
    }

    private val arrColorDelay: Array<Long> = Array(7) { Date().time }
    private val colorDelayDifference = 250L
    fun colorSelect(arr: ByteArray) {
        if(arr[1] % 10 != 0x09) return
        val num = (arr[1] - 0x13) / 10
        if(num >= 7) return

        if(arr[2] == 0.toByte()) {
            dp.focusColor = num
            if(Date().time - arrColorDelay[num] > colorDelayDifference) {
                if(dp.debug) println("${javaClass.name} >> Color $num long signal press detected.")
                dp.state = DrawingPadState.COLOR_SELECT
            } else {
                if(dp.debug) println("${javaClass.name} >> Color $num short signal press detected.")
                dp.draw()
            }
        } else {
            arrColorDelay[num] = Date().time
        }
    }
    fun draw(): ArrayList<SysLED> {
        val arrsysled: ArrayList<SysLED> = dp.frameArray[dp.frameIndex].draw(false)
        for(i in 0 until 7) {
            val hsvcolor: HSVColor = dp.colors[i]
            arrsysled.add(SysLED.rgb(
                (0x13 + (i * 10)).toByte(),
                HSVColor.HSVtoRGB(hsvcolor.h, hsvcolor.s, hsvcolor.v)
            ))
        }
        for(b in ButtonVal.side()) {
            if(b < 0x59.toByte()) continue
            arrsysled.add(
                when(b) {
                    ButtonVal.i8.b() -> SysLED.rgb(b, Color(255, 0, 248))
                    ButtonVal.h9.b() -> SysLED.rgb(b, Color.RED)

                    ButtonVal.a9.b() -> SysLED.rgb(b, if(dp.frameArray.size > 1 && dp.frameIndex != 0)
                        Color.CYAN else Color.BLACK)
                    ButtonVal.b9.b() -> SysLED.rgb(b, if(dp.frameArray.size > 1 && dp.frameIndex != dp.frameArray.size - 1)
                        Color.CYAN else Color.BLACK)
                    ButtonVal.e9.b() -> SysLED.rgb(b, if(dp.frameArray.size > 1)
                        Color.RED.darker().darker() else Color.BLACK)
                    ButtonVal.f9.b() -> SysLED.rgb(b, if(dp.frameArray.size < 128)
                        Color.GREEN.darker().darker() else Color.BLACK)
                    ButtonVal.c9.b() -> SysLED.rgb(b, if(dp.frameIndex != 0)
                        Color.RED else Color.BLACK)
                    ButtonVal.d9.b() -> SysLED.rgb(b, if(dp.frameIndex != dp.frameArray.size - 1)
                        Color.GREEN else Color.BLACK)
                    else -> SysLED.rgb(b, Color.BLACK)
                }
            )
        }
        return arrsysled
    }
}