package me.ambrie3.lpxapi

import java.awt.Color
import javax.sound.midi.MidiDevice
import javax.sound.midi.ShortMessage
import javax.sound.midi.SysexMessage
import kotlin.experimental.and

/**
 * Create an instance of the lpx.LPX class, by either passing in Midi In/Midi Out search keys, or pass in pre-defined
 * MidiDevices for input/output.
 * For reading feedback, use a lpx.LaunchpadReceiver class and add it by using lpx.LPX::addReceiver(receiver: lpx.LaunchpadReceiver)
 * Before using Sysex or MidiMessage signals, boot the launchpad into programmer mode by running lpx.LPX::sendSysexModeProgrammer()
 * Raw messages (MIDIMessages/ShortMessages) use built in velocity values for the lpx.LPX (No RGB, etc.)
 * Sysex messages are used for more complicated features (Text scrolling, RGB, etc.)
 */
class LPX {
    private var input: MidiDevice
        private fun getTransmitter() = input.transmitter
    private var output: MidiDevice
        private fun getReceiver() = output.receiver
    private lateinit var groupReceiver: GroupReceiver
    companion object {
    }

    /**
     * Constructor using search keys, automatically detecting midiin and midiout. Uses MidiDevices.kt.
     * @param keyIn The Midi In search key.
     * @param keyOut The Midi Out search key.
     */
    constructor(keyIn: String = searchKeyIn, keyOut: String = searchKeyOut) {
        input = getLaunchpadInput(keyIn)
        output = getLaunchpadOutput(keyOut)
        setup()
    }

    /**
     * Constructor for no params.
     */
    constructor() {
        input = getLaunchpadInput()
        output = getLaunchpadOutput()
        setup()
    }

    /**
     * Constructor using pre-defined MidiDevices.
     * @param input The IN MidiDevice.
     * @param output The OUT MidiDevice.
     */
    constructor(input: MidiDevice, output: MidiDevice) {
        this.input = input
        this.output = output
        setup()
    }

    private fun setup() {
        output.open()
        input.open()
        groupReceiver = GroupReceiver()
        getTransmitter().receiver = groupReceiver
    }

    public fun addReceiver(receiver: LaunchpadReceiver): Boolean = groupReceiver.receivers.add(receiver)
    public fun removeReceiver(receiver: LaunchpadReceiver): Boolean = groupReceiver.receivers.remove(receiver)
    public fun listReceivers(): ArrayList<LaunchpadReceiver> = ArrayList(groupReceiver.receivers)

    /**
     * The internal output command. Pass in a unit function to execute between opening and closing the stream.
     * @param function The function to run in between stream open/close.
     */
    private fun output(function: (Unit) -> Unit) {
        try {
            output.open()
            function.invoke(Unit)
            output.close()
        } catch(_: Exception) { }
    }

    public fun sendRaw(msg: ShortMessage) = output { getReceiver().send(msg, -1) }
    public fun sendStaticColVel(note: Byte, velocity: Byte) = sendRaw(ShortMessage(0x90, note.toInt(), velocity.toInt()))
    public fun sendFlashColVel(note: Byte, velocity: Byte) = sendRaw(ShortMessage(0x91, note.toInt(), velocity.toInt()))
    public fun sendPulseColVel(note: Byte, velocity: Byte) = sendRaw(ShortMessage(0x92, note.toInt(), velocity.toInt()))
    public fun sendSimpleOff(note: Byte) = sendRaw(ShortMessage(0x80, note.toInt(), 0))

    public fun sendSysexRaw(vararg bytes: Byte) = output { getReceiver().send(SysexMessage(bytes, bytes.size), -1) }
    /**
     * Allows readback. For readback either sendSysexModeLive signal or sendSysexRaw(*lpx.LPXSignal.SYSEX_MODE_LIVE.bytes).
     * Readback sent to lpx.LaunchpadReceiver.
     */
    public fun sendSysexModeLive() = sendSysexRaw(*LPXSignal.SYSEX_MODE_LIVE.bytes)
    /**
     * Allows readback. For readback either sendSysexModeProgrammer signal or sendSysexRaw(*lpx.LPXSignal.SYSEX_MODE_PROGRAMMER.bytes).
     * Readback sent to lpx.LaunchpadReceiver.
     */
    public fun sendSysexModeProgrammer() = sendSysexRaw(*LPXSignal.SYSEX_MODE_PROGRAMMER.bytes)
    /**
     * Allows readback of sleep state by using sendSysexRaw(*lpx.LPXSignal.SYSEX_SLEEP_INS.bytes).
     * Readback sent to lpx.LaunchpadReceiver.
     */
    public fun sendSysexSleepOn() = sendSysexRaw(*LPXSignal.SYSEX_SLEEP_INS.byteIns(0x01.toByte()))
    /**
     * Allows readback of sleep state by using sendSysexRaw(*lpx.LPXSignal.SYSEX_SLEEP_INS.bytes).
     * Readback sent to lpx.LaunchpadReceiver.
     */
    public fun sendSysexSleepOff() = sendSysexRaw(*LPXSignal.SYSEX_SLEEP_INS.byteIns(0x00.toByte()))
    /**
     * Allows readback of brightness by using sendSysexRaw(*lpx.LPXSignal.SYSEX_BRIGHTNESS_INS.bytes).
     * Readback sent to lpx.LaunchpadReceiver.
     */
    public fun sendSysexBrightness(brightness: Byte) = sendSysexRaw(*LPXSignal.SYSEX_BRIGHTNESS_INS.byteIns(brightness and 0x7F.toByte()))
    /**
     * Does not allow readback.
     */
    public fun sendSysexTextScrVel(loop: Boolean, speed: Byte, velocity: Byte, text: String) = sendSysexRaw(*LPXSignal.SYSEX_TEST_SCROLL_INS.byteIns(if(loop) 0x01.toByte() else 0x00.toByte(), speed, 0x00.toByte(), velocity, *text.toByteArray()))
    /**
     * Does not allow readback.
     */
    public fun sendSysexTextScrRGB(loop: Boolean, speed: Byte, color: Color, text: String) = sendSysexRaw(*LPXSignal.SYSEX_TEST_SCROLL_INS.byteIns(if(loop) 0x01.toByte() else 0x00.toByte(), speed, 0x01.toByte(), color.red.toByte(), color.green.toByte(), color.green.toByte(), *text.toByteArray()))
    /**
     * Does not allow readback.
     */
    public fun sendSysexLED(vararg sysLED: SysLED) = sendSysexRaw(*LPXSignal.SYSEX_LED_INS.byteIns(*(sysLED.map{ it.byte.toList() }.flatten().toByteArray())))

    /**
     * Closes receivers and MidiDevices once done with the lpx.LPX. Once run the lpx.LPX input/receivers are effectively useless.
     */
    public fun close() {
        groupReceiver.close()
        input.close()
    }
}