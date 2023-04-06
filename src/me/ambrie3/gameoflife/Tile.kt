package me.ambrie3.gameoflife

class Tile(val tileManager: TileManager, val width: Int, val height: Int) {
    var curState: Boolean = false
    var nextState: Boolean = false
    val adjacent: ArrayList<Tile> = arrayListOf()
    val history: ArrayList<Boolean> = arrayListOf()

    init {
        tileManager.arrTile[height].add(this)
    }

    fun calcAdjacent(): Unit {
        for(i in width - 1..width + 1)
            for(j in height - 1..height + 1) {
                if (i < 0 || i >= tileManager.bWidth || j < 0 || j >= tileManager.bHeight) continue
                if(i == width && j == height) continue
                adjacent.add(tileManager.arrTile[j][i])
            }
    }

    fun calcNextState(): Unit {
        var i = 0
        adjacent.forEach { i += if(it.curState) 1 else 0 }
        nextState = if(curState) { i == 2 || i == 3 } else { i == 3 }
    }

    fun pushState() {
        history.add(curState)
        if(history.size > 32) history.removeAt(0)
        curState = nextState
    }
    fun pullState() {
        if(history.size > 0) curState = history.removeAt(history.size - 1)
    }
}
