package me.ambrie3.lpxapi

import javax.sound.midi.MidiMessage
import javax.sound.midi.Receiver

interface LaunchpadReceiver: Receiver {
    override fun send(message: MidiMessage?, timeStamp: Long) {}
    fun sendShort(message: MidiMessage, timeStamp: Long) {}
    fun sendPressure(message: MidiMessage, timeStamp: Long) {}
    fun sendSysex(message: MidiMessage, timeStamp: Long) {}
    override fun close() {}
}