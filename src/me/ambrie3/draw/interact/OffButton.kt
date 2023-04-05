package me.ambrie3.draw.interact

import me.ambrie3.draw.DrawingPad
import me.ambrie3.lpxapi.ButtonVal
import me.ambrie3.lpxapi.LaunchpadReceiver
import javax.sound.midi.MidiMessage

class OffButton(val dp: DrawingPad): LaunchpadReceiver {
    override fun sendShort(message: MidiMessage, timeStamp: Long) {
        val arr: ByteArray = message.message
        if(arr[0] != 0xB0.toByte()) return
        if(arr[1] != ButtonVal.h9.b()) return

        dp.close()
    }
}