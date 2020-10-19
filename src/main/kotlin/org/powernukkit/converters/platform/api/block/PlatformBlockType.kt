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

import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.platform.universal.block.UniversalBlockType

/**
 * @author joserobjr
 * @since 2020-10-11
 */
abstract class PlatformBlockType<P : Platform<P>>(
    final override val platform: P,
    val id: NamespacedId,
) : PlatformObject<P> {
    abstract val blockProperties: Map<String, PlatformBlockProperty<P>>
    abstract val blockEntityType: PlatformBlockEntityType<P>?
    abstract val universalType: UniversalBlockType?

    abstract fun defaultPropertyValues(): Map<String, PlatformBlockPropertyValue<P>>

    abstract fun withState(values: Map<String, PlatformBlockPropertyValue<P>>): PlatformBlockState<P>

    fun withState(vararg propertyValues: Pair<String, String>): PlatformBlockState<P> {
        return withState(propertyValues.toMap().mapValues { (k, v) ->
            val property = requireNotNull(blockProperties[k]) {
                "Property $k not found"
            }

            try {
                property.getPlatformValue(v)
            } catch (e: NoSuchElementException) {
                throw IllegalArgumentException(
                    "The property ${platform.minecraftEdition}:$k don't have the value $v in the type $id"
                )
            }
        })
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlatformBlockType<*>

        if (platform != other.platform) return false
        if (id != other.id) return false
        if (blockProperties != other.blockProperties) return false
        if (blockEntityType != other.blockEntityType) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = platform.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + blockProperties.hashCode()
        result = 31 * result + (blockEntityType?.hashCode() ?: 0)
        return result
    }

    final override fun toString(): String {
        return "${platform.name}BlockType(id='$id', blockProperties=$blockProperties, blockEntityType=$blockEntityType)"
    }
}
