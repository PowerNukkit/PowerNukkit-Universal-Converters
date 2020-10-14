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

import org.powernukkit.converters.platform.api.block.PlatformBlockProperty
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockProperty

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BaseBlockProperty<
        P : BasePlatform<P, *, *, *, *, BlockPropertyValue, *>,
        BlockPropertyValue : PlatformBlockPropertyValue<P>
        >(
    platform: P,
    id: String,
    final override val universal: UniversalBlockProperty?,
    final override val values: List<BlockPropertyValue>
) : PlatformBlockProperty<P>(platform, id) {
    init {
        require(values.isNotEmpty()) {
            "The ${platform.name} block property $id value list cannot be empty"
        }
    }

    constructor(platform: P, id: String, universal: UniversalBlockProperty) : this(
        platform, id, universal,
        platform.createBlockPropertyValueList(universal)
    )
}
