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

package org.powernukkit.converters.platform.api.block

import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.platform.universal.block.UniversalBlockEntityType

/**
 * @author joserobjr
 * @since 2020-10-11
 */
abstract class PlatformBlockEntityType<P : Platform<P>>(
    final override val platform: P,
    val id: String
): PlatformObject<P> {
    abstract val universalType: UniversalBlockEntityType?
    abstract val data: Map<String, PlatformBlockEntityDataType<P>>

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformBlockEntityType<*>

        if (platform != other.platform) return false
        if (id != other.id) return false
        if (universalType != other.universalType) return false
        if (data != other.data) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = platform.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (universalType?.hashCode() ?: 0)
        result = 31 * result + data.hashCode()
        return result
    }

    final override fun toString(): String {
        return "${platform.name}BlockEntityType(id='$id', data=$data)"
    }
}
