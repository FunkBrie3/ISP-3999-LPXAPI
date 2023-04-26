package me.ambrie3.draw

import me.ambrie3.lpxapi.LPX
import me.ambrie3.lpxapi.SysLED
import java.io.File
import java.util.Scanner

fun drawPlayback(scanner: Scanner) {
    val directory: File = File(System.getProperty("user.dir"))
    val map: HashMap<String, ArrayList<ArrayList<SysLED>>> = hashMapOf()
    val lpx: LPX = LPX()

    directory.listFiles()?.filter { it.isFile && it.name.endsWith(".lpa") }?.forEach {
        map[it.name.substring(0, it.name.length - 4)] = SaveAnim.load(it.name)
    }

    while(true) {
        val scanu: String = scanner.nextLine()
        val scan: List<String> = scanu.split("\\s+".toRegex())
        if(scan[0] == "exit") break
        var delay: Int = 100
        if(scan.size > 1)
            delay = Integer.parseInt(scan[1])

        if(map.containsKey(scan[0])) {
            for(frame in map.get(scan[0])!!) {
                lpx.sendSysexLED(*frame.toTypedArray())
                Thread.sleep(delay.toLong())
            }
        }
    }
    lpx.close()
}