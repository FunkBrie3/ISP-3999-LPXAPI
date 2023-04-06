package me.ambrie3.gameoflife

fun main(args: Array<String>) {
    if(args.size >= 2) TileManager(args[0], args[1]) else TileManager()
}