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

/**
 * @author joserobjr
 * @since 2020-10-12
 */
abstract class PlatformBlockPropertyValue<P : Platform<P>>(
    val platform: P,
    val default: Boolean
) : IPlatformBlockPropertyValue {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PlatformBlockPropertyValue<*>) return false

        if (platform != other.platform) return false
        if (stringValue != other.stringValue) return false

        return true
    }

    final override fun hashCode(): Int {
        var result = platform.hashCode()
        result = 31 * result + stringValue.hashCode()
        return result
    }

    final override fun toString(): String {
        return "${platform.name}BlockPropertyValue(value=$stringValue)"
    }
}
