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

/**
 * @author joserobjr
 * @since 2020-10-11
 */
abstract class PlatformBlockState<P: Platform<P>>: PlatformObject<P> {
    abstract val type: PlatformBlockType<P>
    override val platform: P get() = type.platform
    abstract val values: Map<String, PlatformBlockPropertyValue<P>>

    open fun getPropertyMap(): Map<out PlatformBlockProperty<P>, PlatformBlockPropertyValue<P>> {
        val properties = type.blockProperties
        val values = values
        check(properties.size == values.size) {
            "The state property size mismatches the defined block type properties. $this"
        }
        return values.mapKeys { (id) -> checkNotNull(properties[id]) { "The state has an unknown property '$id'. $this" } }
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformBlockState<*>

        if (type != other.type) return false
        if (values != other.values) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + values.hashCode()
        return result
    }

    final override fun toString(): String {
        return "${platform.name}BlockState(type=$type, values=$values)"
    }
}
