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

package org.powernukkit.converters.math.nibble

/**
 * @author joserobjr
 * @since 2021-06-06
 */
@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
data class InlineNibbleNumber(@PublishedApi internal val nibble: Nibble) : Comparable<InlineNibbleNumber>, Number() {
    override inline fun compareTo(other: InlineNibbleNumber): Int = nibble.compareTo(other.nibble)
    override inline fun toByte(): Byte = nibble.toByte()
    override inline fun toChar(): Char = nibble.toChar()
    override inline fun toDouble(): Double = nibble.toDouble()
    override inline fun toFloat(): Float = nibble.toFloat()
    override inline fun toInt(): Int = nibble.toInt()
    override inline fun toLong(): Long = nibble.toLong()
    override inline fun toShort(): Short = nibble.toShort()
    override fun toString(): String = nibble.toString()
}
