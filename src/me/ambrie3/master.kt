package me.ambrie3

import me.ambrie3.draw.DrawingPad
import me.ambrie3.draw.drawPlayback
import me.ambrie3.gameoflife.TileManager
import me.ambrie3.lpxapi.searchKeyIn
import me.ambrie3.lpxapi.searchKeyOut
import java.util.Scanner

fun main(args: Array<String>) {
    val sc: Scanner = Scanner(System.`in`)
    println("Available Projects:")
    println("\tdraw")
    println("\tconway")
    println("\tdraw-playback")
    println("Args (-in INKEY) (-out OUTKEY) <-debug> may be used.")
    val s: String = sc.nextLine()
    val sarr: List<String> = s.split("\\s+".toRegex())

    var inS: String? = null
    var outS: String? = null
    var debug: Boolean = false

    var id: Int = -1
    for(n in sarr.indices) {
        if(sarr[n] == "-debug") debug = true
        if(sarr.size - 1 > n) {
            if(sarr[n] == "-in" && inS == null) inS = sarr[n + 1]
            else if(sarr[n] == "-out" && outS == null) outS = sarr[n + 1]
        }
        if(sarr[n] == "conway") id = 0
        if(sarr[n] == "draw") id = 1
        if(sarr[n] == "draw-playback") id = 2
    }
    when(id) {
        0 -> TileManager(inS ?: searchKeyIn, outS ?: searchKeyOut)
        1 -> DrawingPad(inS ?: searchKeyIn, outS ?: searchKeyOut, debug)
        2 -> drawPlayback(sc)
        else -> println("No program value selected. Terminating.")
    }
    sc.close()
}