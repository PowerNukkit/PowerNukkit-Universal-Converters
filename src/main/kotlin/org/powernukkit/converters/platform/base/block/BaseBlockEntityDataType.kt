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

import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockEntityDataType
import org.powernukkit.converters.platform.universal.block.UniversalBlockEntityDataType
import org.powernukkit.converters.platform.universal.definitions.model.block.entity.ModelData

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BaseBlockEntityDataType<P : Platform<P, *>>(
    platform: P,
    name: String,
    type: ModelData.Type,
    optional: Boolean,
    default: String?,
    val universal: UniversalBlockEntityDataType?
) : PlatformBlockEntityDataType<P>(platform, name, type, optional, default) {
    constructor(platform: P, universal: UniversalBlockEntityDataType) : this(
        platform = platform,
        universal = universal,
        name = universal.getEditionId(platform.minecraftEdition),
        type = universal.getEditionType(platform.minecraftEdition),
        optional = universal.isOptionalInEdition(platform.minecraftEdition),
        default = universal.getEditionDefault(platform.minecraftEdition),
    )
}
