/*
 *  PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.storage.leveldb

import io.ktor.utils.io.core.*

/**
 * @author joserobjr
 * @since 2021-06-07
 */
fun Output.writeVarInt(value: Int) {
    writeUnsignedVarInt(encodeZigZag32(value))
}

private fun Output.writeUnsignedVarInt(value: Long) {
    write(value)
}

private fun Output.write(value: Long) {
    var pending = value
    do {
        var temp = (pending and 0b01111111).toByte()
        // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
        pending = pending ushr 7
        if (pending != 0L) {
            temp = (temp.toInt() or 0b10000000).toByte()
        }
        writeByte(temp)
    } while (pending != 0L)
}

private fun encodeZigZag64(v: Long): Long {
    return v shl 1 xor (v shr 63)
}

private fun encodeZigZag32(v: Int): Long {
    // Note:  the right-shift must be arithmetic
    return (v shl 1 xor (v shr 31)).toLong() and 0xFFFFFFFFL
}
