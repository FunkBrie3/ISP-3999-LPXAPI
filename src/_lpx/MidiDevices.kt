package _lpx

import javax.sound.midi.MidiDevice
import javax.sound.midi.MidiSystem
import javax.sound.midi.MidiUnavailableException
import kotlin.reflect.KFunction1

const val searchKeyIn: String = "MIDIIN2"
const val searchKeyOut: String = "MIDIOUT2"

// MIDIIN2
fun getLaunchpadInput(key: String = searchKeyIn): MidiDevice {
    return getLaunchpad(key, MidiDevice::getMaxTransmitters)
}

// MIDIOUT2
fun getLaunchpadOutput(key: String = searchKeyOut): MidiDevice {
    return getLaunchpad(key, MidiDevice::getMaxReceivers)
}

// Device ID query through channel arg is not going to be set up as lpx.LPX has a more complicated ID system
private fun getLaunchpad(key: String, f: KFunction1<MidiDevice, Int>): MidiDevice {
    val iterator: Iterator<MidiDevice.Info> = MidiSystem.getMidiDeviceInfo().iterator()
    var i: MidiDevice.Info
    var max: Int
    var dev: MidiDevice? = null
    while(iterator.hasNext()) {
        i = iterator.next()
        if(!i.name.contains(key)) continue

        max = f.invoke(MidiSystem.getMidiDevice(i))
        if(max != -1) continue // Not infinite channels, therefore it can't be the proper MIDI device
        try { dev = MidiSystem.getMidiDevice(i) }
            catch (e: Exception) { e.printStackTrace() }
        break
    }

    if(dev == null) throw MidiUnavailableException()
    else return dev
}