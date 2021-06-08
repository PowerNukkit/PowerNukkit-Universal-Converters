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
 * @property version Palette version information
 * @property size Number of entries in this palette (**not** the length of the words array that internally backs this palette)
 * @property words Array used to store data
 */
interface LevelDBBitArray {
    val size: Int
    val words: IntArray
    val version: LevelDBBitArrayVersion

    operator fun set(index: Int, value: Int)

    operator fun get(index: Int): Int

    fun copy(): LevelDBBitArray
}
