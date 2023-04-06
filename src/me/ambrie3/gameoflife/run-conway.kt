package me.ambrie3.gameoflife

import me.ambrie3.lpxapi.searchKeyIn
import me.ambrie3.lpxapi.searchKeyOut

fun main(args: Array<String>) {
    var inS: String? = null
    var outS: String? = null
    for(n in args.indices) {
        if(args.size - 1 > n) {
            if(args[n] == "-in" && inS == null) inS = args[n + 1]
            else if(args[n] == "-out" && outS == null) outS = args[n + 1]
        }
    }
    TileManager(inS ?: searchKeyIn, outS ?: searchKeyOut)
}