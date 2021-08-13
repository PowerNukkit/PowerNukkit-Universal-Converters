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

package org.powernukkit.converters.math.nibble

/**
 * @author joserobjr
 * @since 2021-06-06
 */
@ExperimentalUnsignedTypes
@JvmInline
value class NibbleArray @PublishedApi internal constructor(
    @PublishedApi internal val data: ByteArray
) : Iterable<Byte> {
    val size: Int get() = data.size * 2
    override fun iterator(): ByteIterator {
        return object : ByteIterator() {
            val iterator = data.iterator()
            var pair = NibblePair(0u)
            var pendingSecond = false
            override fun hasNext(): Boolean {
                return pendingSecond || iterator.hasNext()
            }

            override fun nextByte(): Byte {
                return if (!pendingSecond) {
                    pair = iterator.nextByte().toNibblePair()
                    pendingSecond = true
                    pair.first.toByte()
                } else {
                    pendingSecond = false
                    pair.second.toByte()
                }
            }
        }
    }

    fun clone(): NibbleArray = data.clone().toNibbleArray()
}

@ExperimentalUnsignedTypes
fun ByteArray.toNibbleArray(): NibbleArray = NibbleArray(this)
