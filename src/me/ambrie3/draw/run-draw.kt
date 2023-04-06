package me.ambrie3.draw

import me.ambrie3.lpxapi.searchKeyIn
import me.ambrie3.lpxapi.searchKeyOut

fun main(args: Array<String>) {
    var inS: String? = null
    var outS: String? = null
    var debug: Boolean = false
    for(n in args.indices) {
        if(args[n] == "-debug") debug = true
        if(args.size - 1 > n) {
            if(args[n] == "-in" && inS == null) inS = args[n + 1]
            else if(args[n] == "-out" && outS == null) outS = args[n + 1]
        }
    }
    //DrawingPad()
    DrawingPad(inS ?: searchKeyIn, outS ?: searchKeyOut, debug)
}