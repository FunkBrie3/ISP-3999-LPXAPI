package me.ambrie3.gameoflife

import me.ambrie3.lpxapi.ButtonVal
import me.ambrie3.lpxapi.LaunchpadReceiver
import me.ambrie3.lpxapi.SysLED
import java.awt.Point
import javax.sound.midi.MidiMessage

class TileReceiver(val tm: TileManager): LaunchpadReceiver {
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        val byte: Byte = message.message[1]
        if(message.message[2] == 0x00.toByte()) return
        when(tm.tileManagerState) {
            TileManagerState.SELECT -> {
                when(byte) {
                    ButtonVal.a9.b() -> {
                        tm.tileManagerState = TileManagerState.TOGGLE
                        tm.updateDisplay()
                    }
                    ButtonVal.h9.b() -> {
                        val sysLEDarr: ArrayList<SysLED> = arrayListOf()
                        for(value in ButtonVal.values()) {
                            sysLEDarr.add(SysLED.static(value.b(), 0))
                        }
                        tm.lpx.sendSysexLED(*sysLEDarr.toTypedArray())
                        tm.close()
                    }
                    ButtonVal.c9.b() -> {
                        tm.stepBack()
                    }
                    ButtonVal.d9.b() -> {
                        tm.step()
                    }
                    else -> {}
                }
            }
            TileManagerState.TOGGLE -> {
                if(byte == ButtonVal.a9.b()) {
                    tm.tileManagerState = TileManagerState.SELECT
                    tm.updateDisplay()
                }
                else if(byte >= ButtonVal.a1.b() && byte <= ButtonVal.h8.b() && (byte.toUInt().toInt() - 0x0B) % 10 != 8) {
                    val p: Point = TileManager.byteToTwoDim(byte)
                    val t: Tile = tm.arrTile[p.y][p.x]
                    t.curState = t.curState.not()
                    tm.lpx.sendStaticColVel(byte, if(t.curState) 3 else 0)
                }
            }
        }
    }
    override fun close() {

    }
}