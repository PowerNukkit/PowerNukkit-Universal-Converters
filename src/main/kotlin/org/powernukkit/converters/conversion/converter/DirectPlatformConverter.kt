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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.powernukkit.converters.conversion.adapter.Adapters
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformStructure

/**
 * @author joserobjr
 * @since 2020-10-18
 */
open class DirectPlatformConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    fromPlatform: FromPlatform,
    toPlatform: ToPlatform,
    platformAdapters: PlatformAdapters<FromPlatform, ToPlatform>,
) : PlatformConverter<FromPlatform, ToPlatform>(fromPlatform, toPlatform) {
    private val blockTypeConverter = BlockTypeConverter(
        fromPlatform, toPlatform,
        platformAdapters.blockTypeAdapters ?: Adapters(),
    )

    private val blockPropertyValuesConverter = BlockPropertyValuesConverter(
        fromPlatform, toPlatform,
        platformAdapters.blockPropertyValueAdapters ?: Adapters()
    )

    private val blockStateConverter = BlockStateConverter(
        fromPlatform, toPlatform,
        blockTypeConverter, blockPropertyValuesConverter,
        platformAdapters.blockStateAdapters
    )

    private val blockLayersConverter = BlockLayersConverter(
        fromPlatform, toPlatform,
        blockStateConverter,
        platformAdapters.blockLayersAdapters
    )

    private val blockEntityConverter = BlockEntityConverter(
        fromPlatform, toPlatform,
        platformAdapters.blockEntityAdapters,
    )

    private val entityConverter = EntityConverter(
        fromPlatform, toPlatform,
        platformAdapters.entityAdapters
    )

    private val blockConverter = BlockConverter(
        fromPlatform, toPlatform,
        blockLayersConverter, blockEntityConverter, entityConverter,
        platformAdapters.blockAdapters
    )

    private val structureConverter = StructureConverter(
        fromPlatform, toPlatform,
        blockConverter
    )

    final override fun convertStructure(
        from: PlatformStructure<FromPlatform>
    ) = structureConverter.convert(from)

    override fun CoroutineScope.convertAllStructures(
        from: ReceiveChannel<PlatformStructure<FromPlatform>>,
        to: SendChannel<PlatformStructure<ToPlatform>>,
        problems: SendChannel<ConversionProblem>?
    ) = with(structureConverter) {
        convertAll(from, to, problems)
    }
}
