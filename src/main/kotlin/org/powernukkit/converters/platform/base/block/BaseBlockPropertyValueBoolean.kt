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

package org.powernukkit.converters.platform.base.block

import org.powernukkit.converters.platform.api.block.IPlatformBlockPropertyValue

/**
 * @author joserobjr
 * @since 2020-10-13
 */
interface BaseBlockPropertyValueBoolean : IPlatformBlockPropertyValue {
    override val type get() = IPlatformBlockPropertyValue.Type.BOOLEAN
    val value: Boolean

    override val stringValue get() = value.toString()

    override fun intValue() = if (value) 1 else 0
    override fun booleanValue() = value
}
