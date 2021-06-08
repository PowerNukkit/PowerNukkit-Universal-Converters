/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
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
fun Input.readVarInt(): Int = decodeZigZag32(readUnsignedVarInt())

fun Input.readVarLong(): Long = decodeZigZag64(readUnsignedVarLong())

private fun Input.readUnsignedVarInt(): Long = read(5)

private fun Input.readUnsignedVarLong(): Long = read(10)

private fun Input.read(maxSize: Int): Long {
    var value: Long = 0
    var size = 0
    var b: Int
    while (readByte().toInt().also { b = it } and 0x80 == 0x80) {
        value = value or ((b and 0x7F).toLong() shl size++ * 7)
        require(size < maxSize) { "VarLong too big" }
    }
    return value or ((b and 0x7F).toLong() shl size * 7)
}

private fun decodeZigZag64(v: Long): Long {
    return v ushr 1 xor -(v and 1)
}

private fun decodeZigZag32(v: Long): Int {
    return (v shr 1).toInt() xor (-(v and 1)).toInt()
}
