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

@file:Suppress("NOTHING_TO_INLINE")

package org.powernukkit.converters.math.nibble

import io.ktor.utils.io.bits.*

/**
 * @author joserobjr
 * @since 2021-06-07
 */
@JvmInline
value class NibbleMemory @PublishedApi internal constructor(@PublishedApi internal val data: Memory) {
    inline val size get() = Math.multiplyExact(data.size, 2)
    inline val size32 get() = size.toIntOrFail("size32")

    inline fun loadAt(index: Int) = data.loadAt(index / 2).toUByte().toNibblePair().getForIndex(index)
    inline fun loadAt(index: Long) = data.loadAt(index / 2).toUByte().toNibblePair().getForIndex(index)

    inline fun storeAt(index: Int, value: Nibble) = storeAt(index.toLong(), value)
    fun storeAt(index: Long, value: Nibble) {
        val dataIndex = index / 2
        val current = data[dataIndex].toUByte().toNibblePair()
        val new = if (index.isEven) {
            current.withFirst(value)
        } else {
            current.withSecond(value)
        }
        data.storeAt(index, new.toUByte())
    }

    /**
     * Returns memory's subrange. On some platforms it could do range checks but it is not guaranteed to be safe.
     * It also could lead to memory allocations on some platforms.
     */
    fun slice(offset: Int, length: Int): NibbleMemory {
        offset.requireEven("offset")
        length.requireEven("length")
        return NibbleMemory(data.slice(offset.toLong() * 2, length.toLong() * 2))
    }

    /**
     * Returns memory's subrange. On some platforms it could do range checks but it is not guaranteed to be safe.
     * It also could lead to memory allocations on some platforms.
     */
    fun slice(offset: Long, length: Long): NibbleMemory {
        offset.requireEven("offset")
        length.requireEven("length")
        return NibbleMemory(data.slice(Math.multiplyExact(offset, 2L), Math.multiplyExact(length, 2)))
    }
}

inline fun Memory.toNibbleMemory() = NibbleMemory(this)

@PublishedApi
internal inline fun NibblePair.getForIndex(index: Int): Nibble {
    return if (index.isEven) first.toNibble()
    else second.toNibble()
}

@PublishedApi
internal inline fun NibblePair.getForIndex(index: Long): Nibble {
    return if (index.isEven) first.toNibble()
    else second.toNibble()
}

inline operator fun NibbleMemory.get(index: Int) = loadAt(index)

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Long.toIntOrFail(name: String): Int {
    if (this >= Int.MAX_VALUE) failLongToIntConversion(this, name)
    return toInt()
}

@PublishedApi
internal fun failLongToIntConversion(value: Long, name: String): Nothing =
    throw IllegalArgumentException("Long value $value of $name doesn't fit into 32-bit integer")

@PublishedApi
internal inline val Int.isEven: Boolean
    get() = this and 1 == 0

@PublishedApi
internal inline val Long.isEven: Boolean
    get() = this and 1L == 0L

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Int.requireEven(name: String) {
    if (!this.isEven) failOddSlice(toLong(), name)
}

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Long.requireEven(name: String) {
    if (!this.isEven) failOddSlice(this, name)
}

@PublishedApi
internal fun failOddSlice(value: Long, name: String): Nothing =
    throw IllegalArgumentException("Slices with odd $name ($value) are not supported")

