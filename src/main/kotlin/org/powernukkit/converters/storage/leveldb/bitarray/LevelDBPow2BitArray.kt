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

package org.powernukkit.converters.storage.leveldb.bitarray

import com.google.common.base.Preconditions
import org.powernukkit.converters.math.intCeil

/**
 * @author SupremeMortal, joserobjr (Kotlin port)
 * @since 2021-06-07
 */
class LevelDBPow2BitArray internal constructor(
    override val version: LevelDBBitArrayVersion,
    override val size: Int,
    override val words: IntArray
) : LevelDBBitArray {

    init {
        val expectedWordsLength: Int = (size.toFloat() / version.entriesPerWord).intCeil()
        require(words.size == expectedWordsLength) {
            "Invalid length given for storage, got: ${words.size} but expected: $expectedWordsLength"
        }
    }

    /**
     * Sets the entry at the given location to the given value
     */
    override operator fun set(index: Int, value: Int) {
        Preconditions.checkElementIndex(index, size)
        Preconditions.checkArgument(
            value >= 0 && value <= version.maxEntryValue,
            "Max value: %s. Received value", version.maxEntryValue, value
        )
        val bitIndex = index * version.bits
        val arrayIndex = bitIndex shr 5
        val offset = bitIndex and 31
        words[arrayIndex] =
            words[arrayIndex] and (version.maxEntryValue shl offset).inv() or (value and version.maxEntryValue) shl offset
    }

    /**
     * Gets the entry at the given index
     */
    override operator fun get(index: Int): Int {
        Preconditions.checkElementIndex(index, size)
        val bitIndex = index * version.bits
        val arrayIndex = bitIndex shr 5
        val wordOffset = bitIndex and 31
        return words[arrayIndex] ushr wordOffset and version.maxEntryValue
    }

    override fun copy(): LevelDBPow2BitArray {
        return LevelDBPow2BitArray(version, size, words.copyOf(words.size))
    }
}
