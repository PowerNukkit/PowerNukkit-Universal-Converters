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
import org.powernukkit.converters.platform.universal.definitions.model.block.entity.ModelData

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class PlatformBlockEntityDataType<P : Platform<P>>(
    val platform: P,
    val name: String,
    val type: ModelData.Type,
    val optional: Boolean,
    val default: String?,
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformBlockEntityDataType<*>

        if (platform != other.platform) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (optional != other.optional) return false
        if (default != other.default) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = platform.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + optional.hashCode()
        result = 31 * result + (default?.hashCode() ?: 0)
        return result
    }

    final override fun toString(): String {
        return "${platform.name}BlockEntityDataType(name='$name', type=$type, optional=$optional, default=$default)"
    }
}
