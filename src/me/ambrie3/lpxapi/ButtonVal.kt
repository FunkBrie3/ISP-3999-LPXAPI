package me.ambrie3.lpxapi

enum class ButtonVal private constructor(private val byte: Int) {
    a1(0x0B), b1(0x0C), c1(0x0D), d1(0x0E), e1(0x0F), f1(0x10), g1(0x11), h1(0x12), i1(0x13),
    a2(0x15), b2(0x16), c2(0x17), d2(0x18), e2(0x19), f2(0x1A), g2(0x1B), h2(0x1C), i2(0x1D),
    a3(0x1F), b3(0x20), c3(0x21), d3(0x22), e3(0x23), f3(0x24), g3(0x25), h3(0x26), i3(0x27),
    a4(0x29), b4(0x2A), c4(0x2B), d4(0x2C), e4(0x2D), f4(0x2E), g4(0x2F), h4(0x30), i4(0x31),
    a5(0x33), b5(0x34), c5(0x35), d5(0x36), e5(0x37), f5(0x38), g5(0x39), h5(0x3A), i5(0x3B),
    a6(0x3D), b6(0x3E), c6(0x3F), d6(0x40), e6(0x41), f6(0x42), g6(0x43), h6(0x44), i6(0x45),
    a7(0x47), b7(0x48), c7(0x49), d7(0x4A), e7(0x4B), f7(0x4C), g7(0x4D), h7(0x4E), i7(0x4F),
    a8(0x51), b8(0x52), c8(0x53), d8(0x54), e8(0x55), f8(0x56), g8(0x57), h8(0x58), i8(0x59),
    a9(0x5B), b9(0x5C), c9(0x5D), d9(0x5E), e9(0x5F), f9(0x60), g9(0x61), h9(0x62), i9(0x63);

    public fun b(): Byte = byte.toByte()
}