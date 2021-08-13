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

package org.powernukkit.converters.platform.universal.block

import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.universal.UniversalPlatform

/**
 * @author joserobjr
 * @since 2020-10-10
 */
class UniversalBlockState(
    override val type: UniversalBlockType,
    override val values: Map<String, UniversalBlockPropertyValue> = type.defaultPropertyValues()
) : PlatformBlockState<UniversalPlatform>() {
    override fun getPropertyMap(): Map<UniversalBlockProperty, UniversalBlockPropertyValue> {
        @Suppress("UNCHECKED_CAST")
        return super.getPropertyMap() as Map<UniversalBlockProperty, UniversalBlockPropertyValue>
    }
}
