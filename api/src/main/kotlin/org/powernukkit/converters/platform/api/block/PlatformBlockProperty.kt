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

package org.powernukkit.converters.platform.api.block

import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.platform.api.block.IPlatformBlockPropertyValue.Type
import org.powernukkit.converters.platform.universal.block.UniversalBlockProperty

/**
 * @author joserobjr
 * @since 2020-10-11
 */
abstract class PlatformBlockProperty<P : Platform<P>>(
    final override val platform: P,
    val id: String,
) : PlatformObject<P> {
    abstract val universal: UniversalBlockProperty?
    abstract val values: List<PlatformBlockPropertyValue<P>>

    fun getPlatformValue(value: String) = values.first { it.stringValue == value }
    fun getPlatformValue(value: Byte) = value.toInt().let { int ->
        values.firstOrNull { it.type == Type.INT && it.intValue() == int }
            ?: int.takeIf { it in 0..1 }?.let { getPlatformValue(it == 1) }
    }

    fun getPlatformValue(value: Int) = values.first { it.type == Type.INT && it.intValue() == value }
    fun getPlatformValue(value: Boolean) = values.first { it.type == Type.BOOLEAN && it.booleanValue() == value }

    fun getPlatformValue(propertyValue: PlatformBlockPropertyValue<P>): PlatformBlockPropertyValue<P> {
        return values.firstOrNull { it === propertyValue } ?: getPlatformValue(propertyValue.stringValue)
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformBlockProperty<*>

        if (platform != other.platform) return false
        if (id != other.id) return false
        if (values != other.values) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = platform.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }

    final override fun toString(): String {
        return "${platform.name}BlockProperty(id='$id', values=$values)"
    }
}
