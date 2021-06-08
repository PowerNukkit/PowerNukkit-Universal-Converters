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

/**
 * @author SupremeMortal, joserobjr (Kotlin port)
 * @since 2021-06-07
 */
enum class LevelDBBitArrayVersion(bits: Int, entriesPerWord: Int, val next: LevelDBBitArrayVersion?) {
    V16(16, 2, null),
    V8(8, 4, V16),
    V6(6, 5, V8),  // 2 bit padding
    V5(5, 6, V6),  // 2 bit padding
    V4(4, 8, V5),
    V3(3, 10, V4),  // 2 bit padding
    V2(2, 16, V3),
    V1(1, 32, V2);

    val bits: Byte = bits.toByte()
    val entriesPerWord: Byte = entriesPerWord.toByte()
    val maxEntryValue: Int = (1 shl this.bits.toInt()) - 1

    fun createPalette(size: Int): LevelDBBitArray {
        return this.createPalette(size, IntArray(getWordsForSize(size)))
    }

    fun getWordsForSize(size: Int): Int {
        return size / entriesPerWord + if (size % entriesPerWord == 0) 0 else 1
    }

    operator fun next(): LevelDBBitArrayVersion? {
        return next
    }

    fun createPalette(size: Int, words: IntArray): LevelDBBitArray {
        return if (this == V3 || this == V5 || this == V6) {
            // Padded palettes aren't able to use bitwise operations due to their padding.
            LevelDBPaddedBitArray(this, size, words)
        } else {
            LevelDBPow2BitArray(this, size, words)
        }
    }

    companion object {
        fun getByBits(bits: Int) = valueOf("V$bits")
    }
}
