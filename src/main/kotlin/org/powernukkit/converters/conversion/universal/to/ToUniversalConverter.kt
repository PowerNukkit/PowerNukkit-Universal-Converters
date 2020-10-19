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

package org.powernukkit.converters.conversion.universal.to

import org.powernukkit.converters.conversion.adapter.PlatformAdapters
import org.powernukkit.converters.conversion.adapter.addFirst
import org.powernukkit.converters.conversion.converter.DirectPlatformConverter
import org.powernukkit.converters.conversion.universal.ChainedConverter
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform

/**
 * @author joserobjr
 * @since 2020-10-18
 */
class ToUniversalConverter<FromPlatform : Platform<FromPlatform>>(
    fromPlatform: FromPlatform,
    universalPlatform: UniversalPlatform,
    platformAdapters: PlatformAdapters<FromPlatform, UniversalPlatform>? = null,
) : DirectPlatformConverter<FromPlatform, UniversalPlatform>(
    fromPlatform,
    universalPlatform,
    (platformAdapters ?: PlatformAdapters()).run {
        copy(
            blockTypeAdapters = blockTypeAdapters
                .addFirst(ToUniversalBlockTypeAdapter.default()),

            blockPropertyValueAdapters = blockPropertyValueAdapters
                .addFirst(ToUniversalBlockPropertyValuesAdapter.default())
        )
    }
) {
    fun <ToPlatform : Platform<ToPlatform>> convertToPlatform(
        toPlatform: ToPlatform,
        adapters: PlatformAdapters<UniversalPlatform, ToPlatform>? = null,
    ): ChainedConverter<FromPlatform, ToPlatform> {
        return ChainedConverter(
            fromPlatform, toPlatform,
            this, toPlatform.convertFromUniversal(adapters)
        )
    }
}
