package _lpx

import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

class GroupReceiver: Receiver {
    val receivers: ArrayList<LaunchpadReceiver> = arrayListOf()

    override fun send(message: MidiMessage?, timeStamp: Long) {
        receivers.forEach {
            it.send(message, timeStamp)
        }

        if(message?.message?.size == 2) {
            receivers.forEach {
                it.sendPressure(message, timeStamp)
            }
        } else if(message?.message?.size == 3) {
            receivers.forEach {
                it.sendShort(message, timeStamp)
            }
        } else if(message != null && message.message != null){
            receivers.forEach {
                it.sendSysex(message, timeStamp)
            }
        }
    }

    override fun close() {
        receivers.forEach {
            it.close()
        }
    }
}