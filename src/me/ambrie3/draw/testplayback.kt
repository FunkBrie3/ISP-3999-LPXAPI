package me.ambrie3.draw

import me.ambrie3.lpxapi.LPX
import me.ambrie3.lpxapi.SysLED

fun main() {
    val t: ArrayList<ArrayList<SysLED>> = SaveAnim.load("C:\\Users\\Brie\\Desktop\\outerWave.lpa")
    val lpx: LPX = LPX()
    for(a in t) {
        lpx.sendSysexLED(*a.toTypedArray())
        Thread.sleep(50)
    }
    lpx.close()
}