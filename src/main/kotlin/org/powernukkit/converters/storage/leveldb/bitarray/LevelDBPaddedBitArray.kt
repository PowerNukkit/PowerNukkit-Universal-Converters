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
class LevelDBPaddedBitArray internal constructor(
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

    override operator fun set(index: Int, value: Int) {
        Preconditions.checkElementIndex(index, size)
        Preconditions.checkArgument(
            value >= 0 && value <= version.maxEntryValue,
            "Max value: %s. Received value", version.maxEntryValue, value
        )
        val arrayIndex: Int = index / version.entriesPerWord
        val offset: Int = index % version.entriesPerWord * version.bits
        words[arrayIndex] =
            words[arrayIndex] and (version.maxEntryValue shl offset).inv() or (value and version.maxEntryValue) shl offset
    }

    override operator fun get(index: Int): Int {
        Preconditions.checkElementIndex(index, size)
        val arrayIndex: Int = index / version.entriesPerWord
        val offset: Int = index % version.entriesPerWord * version.bits
        return words[arrayIndex] ushr offset and version.maxEntryValue
    }

    override fun copy(): LevelDBPaddedBitArray {
        return LevelDBPaddedBitArray(version, size, words.copyOf(words.size))
    }
}
