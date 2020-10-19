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

package org.powernukkit.converters.math

/**
 * @author joserobjr
 * @since 2020-10-17
 */
class DoubleRangeExclusive(start: Double, val endExclusive: Double) : ClosedFloatingPointRange<Double> {
    private val _start = start

    override val start: Double get() = _start
    override val endInclusive get() = Double.NaN

    override operator fun contains(value: Double) = value >= _start && value < endExclusive
    override fun isEmpty() = _start >= endExclusive

    override fun lessThanOrEquals(a: Double, b: Double) = a <= b

    override fun equals(other: Any?): Boolean {
        return other is DoubleRangeExclusive &&
                (isEmpty() && other.isEmpty() || _start == other._start && endExclusive == other.endExclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else 31 * _start.hashCode() + endExclusive.hashCode()
    }

    override fun toString(): String = "$_start until $endExclusive"
}
