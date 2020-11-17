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

package org.powernukkit.converters.internal

import br.com.gamemods.nbtmanipulator.*

/**
 * @author joserobjr
 * @since 2020-10-20
 */
internal val NbtTag?.byteArrayOrNull get() = (this as? NbtByteArray)?.value
internal val NbtTag?.uByteOrNull get() = (this as? NbtByte)?.unsigned
internal val NbtTag?.intOrNull get() = (this as? NbtInt)?.value
internal val NbtTag?.longOrNull get() = (this as? NbtLong)?.value
internal val NbtTag?.doubleOrNull get() = (this as? NbtDouble)?.value
internal val NbtTag?.floatOrNull get() = (this as? NbtFloat)?.value
internal val NbtTag?.stringOrNull get() = (this as? NbtString)?.value
internal val NbtTag?.compoundOrNull get() = this as? NbtCompound
internal val NbtTag?.booleanOrNull get() = if (this !is NbtByte) null else signed == 1.toByte()

internal val NbtTag?.nbtIntListOrNull get() = (this as? NbtList<*>)?.castNullable<NbtInt>()
internal val NbtTag?.nbtDoubleListOrNull get() = (this as? NbtList<*>)?.castNullable<NbtDouble>()
internal val NbtTag?.nbtStringListOrNull get() = (this as? NbtList<*>)?.castNullable<NbtString>()
internal val NbtTag?.compoundListOrNull get() = (this as? NbtList<*>)?.castNullable<NbtCompound>()

internal val NbtTag?.intListOrNull get() = nbtIntListOrNull?.map { it.value }
internal val NbtTag?.doubleListOrNull get() = nbtDoubleListOrNull?.map { it.value }
internal val NbtTag?.stringListOrNull get() = nbtStringListOrNull?.map { it.value }

internal val NbtTag?.byteArray get() = checkNotNull(byteArrayOrNull)
internal val NbtTag?.uByte get() = checkNotNull(uByteOrNull)
internal val NbtTag?.int get() = checkNotNull(intOrNull)
internal val NbtTag?.long get() = checkNotNull(longOrNull)
internal val NbtTag?.double get() = checkNotNull(doubleOrNull)
internal val NbtTag?.float get() = checkNotNull(floatOrNull)
internal val NbtTag?.string get() = checkNotNull(stringOrNull)
internal val NbtTag?.compound get() = checkNotNull(compoundOrNull)
internal val NbtTag?.boolean get() = checkNotNull(booleanOrNull)
internal val NbtTag?.compoundList get() = checkNotNull(compoundListOrNull)

internal operator fun NbtTag?.get(key: String) = compoundOrNull?.get(key)

internal operator fun NbtCompound?.get(key: String) = this?.get(key)

internal inline fun <reified Nbt : NbtTag> NbtList<*>.cast(): NbtList<Nbt> {
    if (isNotEmpty()) {
        check(first()::class == Nbt::class) {
            "Cannot use this list as NbtList<${Nbt::class.simpleName}>"
        }
    }

    @Suppress("UNCHECKED_CAST")
    return this as NbtList<Nbt>
}

internal inline fun <reified Nbt : NbtTag> NbtList<*>.castNullable(): NbtList<Nbt>? {
    if (isNotEmpty() && first()::class != Nbt::class) {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    return this as NbtList<Nbt>
}
