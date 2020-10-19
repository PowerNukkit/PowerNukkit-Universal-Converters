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

package org.powernukkit.converters.conversion.converter

import org.powernukkit.converters.conversion.adapter.Adapters
import org.powernukkit.converters.conversion.adapter.EntityAdapter
import org.powernukkit.converters.conversion.context.BlockConversionContext
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.entity.ConvertedEntity
import org.powernukkit.converters.platform.api.entity.PlatformEntity

/**
 * @author joserobjr
 * @since 2020-10-17
 */
open class EntityConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromPlatform: FromPlatform,
    val toPlatform: ToPlatform,
    val adapters: Adapters<NamespacedId, EntityAdapter<FromPlatform, ToPlatform>>?
) {
    open fun convert(
        fromEntity: PlatformEntity<FromPlatform>,
        context: BlockConversionContext<FromPlatform, ToPlatform>,
    ): ConvertedEntity<ToPlatform> {
        // TODO Implement entity conversion
        return ConvertedEntity()
    }

    open fun convertList(
        fromEntityList: List<PlatformEntity<FromPlatform>>,
        context: BlockConversionContext<FromPlatform, ToPlatform>
    ): List<PlatformEntity<ToPlatform>> {
        // TODO Implement entity conversion
        return emptyList()
    }

}
