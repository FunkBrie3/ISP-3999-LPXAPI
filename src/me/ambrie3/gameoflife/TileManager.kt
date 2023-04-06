package me.ambrie3.gameoflife

import me.ambrie3.lpxapi.*
import java.awt.Point
import java.io.Closeable

class TileManager(val searchIn: String = searchKeyIn, val searchOut: String = searchKeyOut): Closeable {
    var lpx: LPX
    val bHeight = 8
    val bWidth = 8
    val arrTile: Array<ArrayList<Tile>> = Array(bHeight) { arrayListOf() }
    var tileReceiver: TileReceiver = TileReceiver(this)
    var tileManagerState: TileManagerState = TileManagerState.SELECT

    companion object {
        fun twoDimToByte(w: Int, h: Int): Byte {
            return (0x0B + w + (10 * h)).toByte()
        }

        fun byteToTwoDim(byte: Byte): Point {
            return Point((byte - 0x0B) % 10, (byte - 0x0B) / 10)
        }
    }

    init {
        for(x in 0 until bWidth)
            for(y in 0 until bHeight) {
                Tile(this, x, y)
            }
        arrTile.forEach { i -> i.forEach { j -> j.calcAdjacent() } }

        lpx = LPX(searchIn, searchOut)
        lpx.addReceiver(tileReceiver)
        lpx.sendSysexModeProgrammer()
        updateDisplay()
    }

    fun stepBack() {
        arrTile.forEach {i -> i.forEach {j -> j.pullState()}}
        updateDisplay()
    }

    fun step() {
        arrTile.forEach { i -> i.forEach { j -> j.calcNextState() } }
        for(y in 0 until bHeight)
            for(x in 0 until bWidth) {
                arrTile[y][x].pushState()
            }

        updateDisplay()
    }

    fun updateDisplay() {
        val sysLEDarr: ArrayList<SysLED> = arrayListOf()
        when(tileManagerState) {
            TileManagerState.SELECT -> {
                sysLEDarr.add(SysLED.static(ButtonVal.a9.b(), 43))
                sysLEDarr.add(SysLED.static(ButtonVal.c9.b(), 23))
                sysLEDarr.add(SysLED.static(ButtonVal.d9.b(), 23))
                sysLEDarr.add(SysLED.static(ButtonVal.h9.b(), 5))
            }
            TileManagerState.TOGGLE -> {
                sysLEDarr.add(SysLED.flash(ButtonVal.a9.b(), 41, 43))
                sysLEDarr.add(SysLED.static(ButtonVal.c9.b(), 0))
                sysLEDarr.add(SysLED.static(ButtonVal.d9.b(), 0))
                sysLEDarr.add(SysLED.static(ButtonVal.h9.b(), 0))
            }
        }

        for(y in 0 until bHeight)
            for(x in 0 until bWidth) {
                sysLEDarr.add(SysLED.static(twoDimToByte(x, y), if(arrTile[y][x].curState) 3 else 0))
            }

        if(sysLEDarr.size > 0) lpx.sendSysexLED(*sysLEDarr.toTypedArray())
    }

    override fun close() {
        lpx.close()
    }
}