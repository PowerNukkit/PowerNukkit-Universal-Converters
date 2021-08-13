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

@file:Suppress("NOTHING_TO_INLINE")

package org.powernukkit.converters.math.nibble

/**
 * @author joserobjr
 * @since 2021-06-06
 */
@JvmInline
value class NibblePair @PublishedApi internal constructor(@PublishedApi internal val data: UByte) {
    constructor(first: Nibble, second: Nibble) : this(((second.toInt() shl 4) or (first.toInt() and 0xF)).toUByte())

    inline val first: Nibble get() = data.toNibble()
    inline val second: Nibble get() = (data.toInt() ushr 4).toNibble()

    @Suppress("NOTHING_TO_INLINE")
    inline fun toUByte(): UByte = data

    inline fun withFirst(first: Nibble): NibblePair = NibblePair(data.toInt().let {
        (it and 0xF0) or (first.toInt() and 0xF)
    }.toUByte())

    inline fun withSecond(second: Nibble): NibblePair = NibblePair(data.toInt().let {
        (it and 0xF) or (second.toInt() and 0xF shl 4)
    }.toUByte())
}

inline fun UByte.toNibblePair(): NibblePair = NibblePair(this)

inline fun Byte.toNibblePair(): NibblePair = NibblePair(toUByte())
