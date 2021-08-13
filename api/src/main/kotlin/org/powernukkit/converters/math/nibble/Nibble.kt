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
 * A signed 4 bits number.
 *
 * @author joserobjr
 * @since 2021-06-06
 */
@Suppress("OVERRIDE_BY_INLINE")
@JvmInline
value class Nibble @PublishedApi internal constructor(
    @PublishedApi internal val data: Byte
) : Comparable<Nibble> {
    companion object {
        /**
         * A constant holding the minimum value an instance of Nibble can have.
         */
        val MIN_VALUE: Nibble = Nibble(-8)

        /**
         * A constant holding the maximum value an instance of Nibble can have.
         */
        val MAX_VALUE: Nibble = Nibble(7)

        /**
         * The number of bytes used to represent an instance of Nibble in a binary form.
         */
        const val SIZE_BYTES: Double = 0.5

        /**
         * The number of bits used to represent an instance of Nibble in a binary form.
         */
        const val SIZE_BITS: Int = 4
    }

    /**
     * Return this value.
     */
    inline fun toNibble(): Nibble = this

    /**
     * Converts this [Nibble] value to [Byte].
     *
     * The resulting `Byte` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `Byte` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 4 bits are filled with the sign bit of this value.
     */
    inline fun toByte(): Byte = data

    /**
     * Converts this [Nibble] value to [Char].
     *
     * If this value is non-negative, the resulting `Char` code is equal to this value.
     *
     * The least significant 4 bits of the resulting `Char` code are the same as the bits of this `Nibble` value,
     * whereas the most significant 12 bits are filled with the sign bit of this value.
     */
    inline fun toChar(): Char = data.toInt().toChar()

    /**
     * Converts this [Nibble] value to [Short].
     *
     * The resulting `Short` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `Short` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 12 bits are filled with the sign bit of this value.
     */
    inline fun toShort(): Short = data.toShort()

    /**
     * Converts this [Nibble] value to [Int].
     *
     * The resulting `Int` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `Int` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 28 bits are filled with the sign bit of this value.
     */
    inline fun toInt(): Int = data.toInt()

    /**
     * Converts this [Nibble] value to [Long].
     *
     * The resulting `Int` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `Long` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 60 bits are filled with the sign bit of this value.
     */
    inline fun toLong(): Long = data.toLong()

    /**
     * Converts this [Nibble] value to [Float].
     *
     * The resulting `Float` value represents the same numerical value as this `Nibble`.
     */
    inline fun toFloat(): Float = data.toFloat()

    /**
     * Converts this [Nibble] value to [Double].
     *
     * The resulting `Double` value represents the same numerical value as this `Nibble`.
     */
    inline fun toDouble(): Double = data.toDouble()

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts this [Nibble] value to [UByte].
     *
     * If this value is positive, the resulting `UByte` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `UByte` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 4 bits are filled with the sign bit of this value.
     */
    @ExperimentalUnsignedTypes
    inline fun toUByte(): UByte = data.toUByte()

    /**
     * Converts this [Nibble] value to [UShort].
     *
     * If this value is positive, the resulting `UShort` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `UShort` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 12 bits are filled with the sign bit of this value.
     */
    @ExperimentalUnsignedTypes
    inline fun toUShort(): UShort = data.toUShort()

    /**
     * Converts this [Nibble] value to [UInt].
     *
     * If this value is positive, the resulting `UInt` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `UInt` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 28 bits are filled with the sign bit of this value.
     */
    @ExperimentalUnsignedTypes
    inline fun toUInt(): UInt = data.toUInt()

    /**
     * Converts this [Nibble] value to [ULong].
     *
     * If this value is positive, the resulting `UByte` value represents the same numerical value as this `Nibble`.
     *
     * The least significant 4 bits of the resulting `ULong` value are the same as the bits of this `Nibble` value,
     * whereas the most significant 60 bits are filled with the sign bit of this value.
     */
    @ExperimentalUnsignedTypes
    inline fun toULong(): ULong = data.toULong()

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    override inline fun compareTo(other: Nibble): Int = toByte().compareTo(other.toByte())

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    inline fun compareTo(other: Byte): Int = toInt().compareTo(other.toInt())

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    inline fun compareTo(other: Short): Int = toInt().compareTo(other.toInt())

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    inline fun compareTo(other: Int): Int = toInt().compareTo(other)

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    inline fun compareTo(other: Long): Int = toLong().compareTo(other)

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    inline fun compareTo(other: Float): Int = toFloat().compareTo(other)

    /**
     * Compares this value with the specified value for order.
     * Returns zero if this value is equal to the specified other value, a negative number if it's less than other,
     * or a positive number if it's greater than other.
     */
    inline fun compareTo(other: Double): Int = toDouble().compareTo(other)

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Adds the other value to this value. */
    inline operator fun plus(other: Nibble): Int = data + other.data

    /** Adds the other value to this value. */
    inline operator fun plus(other: Byte): Int = data + other

    /** Adds the other value to this value. */
    inline operator fun plus(other: Short): Int = data + other

    /** Adds the other value to this value. */
    inline operator fun plus(other: Int): Int = data + other

    /** Adds the other value to this value. */
    inline operator fun plus(other: Long): Long = data + other

    /** Adds the other value to this value. */
    inline operator fun plus(other: Float): Float = data + other

    /** Adds the other value to this value. */
    inline operator fun plus(other: Double): Double = data + other

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Subtracts the other value from this value. */
    inline operator fun minus(other: Nibble): Int = data - other.data

    /** Subtracts the other value from this value. */
    inline operator fun minus(other: Byte): Int = data - other

    /** Subtracts the other value from this value. */
    inline operator fun minus(other: Short): Int = data - other

    /** Subtracts the other value from this value. */
    inline operator fun minus(other: Int): Int = data - other

    /** Subtracts the other value from this value. */
    inline operator fun minus(other: Long): Long = data - other

    /** Subtracts the other value from this value. */
    inline operator fun minus(other: Float): Float = data - other

    /** Subtracts the other value from this value. */
    inline operator fun minus(other: Double): Double = data - other

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Multiplies this value by the other value. */
    inline operator fun times(other: Nibble): Int = data * other.data

    /** Subtracts the other value from this value. */
    inline operator fun times(other: Byte): Int = data * other

    /** Subtracts the other value from this value. */
    inline operator fun times(other: Short): Int = data * other

    /** Subtracts the other value from this value. */
    inline operator fun times(other: Int): Int = data * other

    /** Subtracts the other value from this value. */
    inline operator fun times(other: Long): Long = data * other

    /** Subtracts the other value from this value. */
    inline operator fun times(other: Float): Float = data * other

    /** Subtracts the other value from this value. */
    inline operator fun times(other: Double): Double = data * other

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Divides this value by the other value. */
    inline operator fun div(other: Nibble): Int = data / other.data

    /** Divides this value by the other value. */
    inline operator fun div(other: Byte): Int = data / other

    /** Divides this value by the other value. */
    inline operator fun div(other: Short): Int = data / other

    /** Divides this value by the other value. */
    inline operator fun div(other: Int): Int = data / other

    /** Divides this value by the other value. */
    inline operator fun div(other: Long): Long = data / other

    /** Divides this value by the other value. */
    inline operator fun div(other: Float): Float = data / other

    /** Divides this value by the other value. */
    inline operator fun div(other: Double): Double = data / other

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Calculates the remainder of dividing this value by the other value. */
    inline operator fun rem(other: Nibble): Int = data % other.data

    /** Calculates the remainder of dividing this value by the other value. */
    inline operator fun rem(other: Byte): Int = data % other

    /** Calculates the remainder of dividing this value by the other value. */
    inline operator fun rem(other: Short): Int = data % other

    /** Calculates the remainder of dividing this value by the other value. */
    inline operator fun rem(other: Int): Int = data % other

    /** Calculates the remainder of dividing this value by the other value. */
    inline operator fun rem(other: Long): Long = data % other

    /** Calculates the remainder of dividing this value by the other value. */
    inline operator fun rem(other: Float): Float = data % other

    /** Calculates the remainder of dividing this value by the other value. */
    inline operator fun rem(other: Double): Double = data % other

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /** Increments this value. */
    operator fun inc(): Nibble = (this + 1).toNibble()

    /** Decrements this value. */
    operator fun dec(): Nibble = (this - 1).toNibble()

    /** Returns this value. */
    operator fun unaryPlus(): Int = toInt()

    /** Returns the negative of this value. */
    operator fun unaryMinus(): Int = -toInt()

    /** Creates a range from this value to the specified [other] value. */
    operator fun rangeTo(other: Byte): IntRange = toInt()..other.toInt()

    /** Creates a range from this value to the specified [other] value. */
    operator fun rangeTo(other: Short): IntRange = toInt()..other.toInt()

    /** Creates a range from this value to the specified [other] value. */
    operator fun rangeTo(other: Int): IntRange = toInt()..other

    /** Creates a range from this value to the specified [other] value. */
    operator fun rangeTo(other: Long): LongRange = toLong()..other

    inline infix fun pairedTo(other: Nibble): NibblePair = NibblePair(this, other)
}

inline operator fun Byte.compareTo(other: Nibble): Int = -other.compareTo(this)
inline operator fun Short.compareTo(other: Nibble): Int = -other.compareTo(this)
inline operator fun Int.compareTo(other: Nibble): Int = -other.compareTo(this)
inline operator fun Long.compareTo(other: Nibble): Int = -other.compareTo(this)
inline operator fun Float.compareTo(other: Nibble): Int = -other.compareTo(this)
inline operator fun Double.compareTo(other: Nibble): Int = -other.compareTo(this)

inline operator fun Byte.plus(other: Nibble): Int = other + this
inline operator fun Short.plus(other: Nibble): Int = other + this
inline operator fun Int.plus(other: Nibble): Int = other + this
inline operator fun Long.plus(other: Nibble): Long = other + this
inline operator fun Float.plus(other: Nibble): Float = other + this
inline operator fun Double.plus(other: Nibble): Double = other + this

inline operator fun Byte.minus(other: Nibble): Int = this + -other
inline operator fun Short.minus(other: Nibble): Int = this + -other
inline operator fun Int.minus(other: Nibble): Int = this + -other
inline operator fun Long.minus(other: Nibble): Long = this + -other
inline operator fun Float.minus(other: Nibble): Float = this + -other
inline operator fun Double.minus(other: Nibble): Double = this + -other

inline operator fun Byte.times(other: Nibble): Int = other * this
inline operator fun Short.times(other: Nibble): Int = other * this
inline operator fun Int.times(other: Nibble): Int = other * this
inline operator fun Long.times(other: Nibble): Long = other * this
inline operator fun Float.times(other: Nibble): Float = other * this
inline operator fun Double.times(other: Nibble): Double = other * this

inline operator fun Byte.div(other: Nibble): Int = toInt() / other.toInt()
inline operator fun Short.div(other: Nibble): Int = toInt() / other.toInt()
inline operator fun Int.div(other: Nibble): Int = this / other.toInt()
inline operator fun Long.div(other: Nibble): Long = this / other.toLong()
inline operator fun Float.div(other: Nibble): Float = this / other.toFloat()
inline operator fun Double.div(other: Nibble): Double = this / other.toDouble()

inline operator fun Byte.rem(other: Nibble): Int = toInt() % other.toInt()
inline operator fun Short.rem(other: Nibble): Int = toInt() % other.toInt()
inline operator fun Int.rem(other: Nibble): Int = this % other.toInt()
inline operator fun Long.rem(other: Nibble): Long = this % other.toLong()
inline operator fun Float.rem(other: Nibble): Float = this % other.toFloat()
inline operator fun Double.rem(other: Nibble): Double = this % other.toDouble()

inline operator fun Byte.rangeTo(other: Nibble): IntRange = this..other.toInt()
inline operator fun Short.rangeTo(other: Nibble): IntRange = this..other.toInt()
inline operator fun Int.rangeTo(other: Nibble): IntRange = this..other.toInt()
inline operator fun Long.rangeTo(other: Nibble): LongRange = this..other.toLong()
inline operator fun Float.rangeTo(other: Nibble): ClosedFloatingPointRange<Float> = this..other.toFloat()
inline operator fun Double.rangeTo(other: Nibble): ClosedFloatingPointRange<Double> = this..other.toDouble()

/**
 * Converts this [Byte] value to [Nibble].
 *
 * If this value is in [Nibble.MIN_VALUE]..[Nibble.MAX_VALUE], the resulting `Nibble` value represents
 * the same numerical value as this `Byte`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `Byte` value.
 */
/*inline fun Byte.toNibble(): Nibble = Nibble((this and 0xF).let { 
    if (it and 8 != 0.toByte()) it or -16
    else it 
})*/
inline fun Byte.toNibble(): Nibble = toInt().toNibble()

/**
 * Converts this [Short] value to [Nibble].
 *
 * If this value is in [Nibble.MIN_VALUE]..[Nibble.MAX_VALUE], the resulting `Short` value represents
 * the same numerical value as this `Short`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `Short` value.
 */
//inline fun Short.toNibble(): Nibble = toByte().toNibble()
inline fun Short.toNibble(): Nibble = toInt().toNibble()

/**
 * Converts this [Int] value to [Nibble].
 *
 * If this value is in [Nibble.MIN_VALUE]..[Nibble.MAX_VALUE], the resulting `Int` value represents
 * the same numerical value as this `Int`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `Int` value.
 */
//inline fun Int.toNibble(): Nibble = toByte().toNibble()
inline fun Int.toNibble(): Nibble = Nibble((this and 0xF).let {
    if (it and 0b1000 != 0) it or -16
    else it
}.toByte())

/**
 * Converts this [Long] value to [Nibble].
 *
 * If this value is in [Nibble.MIN_VALUE]..[Nibble.MAX_VALUE], the resulting `Long` value represents
 * the same numerical value as this `Long`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `Long` value.
 */
inline fun Long.toNibble(): Nibble = toByte().toNibble()

/**
 * Converts this [Float] value to [Nibble].
 *
 * The fractional part, if any, is rounded down towards zero.
 * Returns zero if this `Float` value is `NaN`, [Nibble.MIN_VALUE] if it's less than `Nibble.MIN_VALUE`,
 * [Nibble.MAX_VALUE] if it's bigger than `Nibble.MAX_VALUE`.
 */
inline fun Float.toNibble(): Nibble = toInt().toNibble()
inline fun Double.toNibble(): Nibble = toInt().toNibble()

/**
 * Converts this [UByte] value to [Nibble].
 *
 * If this value is less than or equals to [Nibble.MAX_VALUE], the resulting `Nibble` value represents
 * the same numerical value as this `UByte`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `UByte` value.
 * Note that the resulting `Nibble` value may be negative.
 */
inline fun UByte.toNibble(): Nibble = toByte().toNibble()

/**
 * Converts this [UShort] value to [Nibble].
 *
 * If this value is less than or equals to [Nibble.MAX_VALUE], the resulting `Nibble` value represents
 * the same numerical value as this `UShort`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `UShort` value.
 * Note that the resulting `Nibble` value may be negative.
 */
inline fun UShort.toNibble(): Nibble = toShort().toNibble()

/**
 * Converts this [UInt] value to [Nibble].
 *
 * If this value is less than or equals to [Nibble.MAX_VALUE], the resulting `Nibble` value represents
 * the same numerical value as this `UInt`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `UInt` value.
 * Note that the resulting `Nibble` value may be negative.
 */
inline fun UInt.toNibble(): Nibble = toInt().toNibble()

/**
 * Converts this [ULong] value to [Nibble].
 *
 * If this value is less than or equals to [Nibble.MAX_VALUE], the resulting `Nibble` value represents
 * the same numerical value as this `ULong`.
 *
 * The resulting `Nibble` value is represented by the least significant 4 bits of this `ULong` value.
 * Note that the resulting `Nibble` value may be negative.
 */
inline fun ULong.toNibble(): Nibble = toLong().toNibble()

