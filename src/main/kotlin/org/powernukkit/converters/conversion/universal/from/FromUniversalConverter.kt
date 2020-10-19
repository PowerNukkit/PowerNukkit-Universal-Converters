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

package org.powernukkit.converters.conversion.universal.from

import kotlinx.coroutines.flow.Flow
import org.powernukkit.converters.conversion.adapter.*
import org.powernukkit.converters.conversion.converter.*
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.UniversalStructure

/**
 * @author joserobjr
 * @since 2020-10-18
 */
class FromUniversalConverter<ToPlatform : Platform<ToPlatform>>(
    universalPlatform: UniversalPlatform,
    toPlatform: ToPlatform,
    blockTypeAdapters: Adapters<NamespacedId, BlockTypeAdapter<UniversalPlatform, ToPlatform>>? = null,
    blockPropertyValueAdapters: Adapters<NamespacedId, BlockPropertyValuesAdapter<UniversalPlatform, ToPlatform>>? = null,
    blockStateAdapters: Adapters<NamespacedId, BlockStateAdapter<UniversalPlatform, ToPlatform>>? = null,
    blockLayersAdapters: Adapters<NamespacedId, BlockLayersAdapter<UniversalPlatform, ToPlatform>>? = null,
    blockEntityAdapters: Adapters<NamespacedId, BlockEntityAdapter<UniversalPlatform, ToPlatform>>? = null,
    entityAdapters: Adapters<NamespacedId, EntityAdapter<UniversalPlatform, ToPlatform>>? = null,
    blockAdapters: Adapters<NamespacedId, BlockAdapter<UniversalPlatform, ToPlatform>>? = null,
) {
    private val blockTypeConverter = BlockTypeConverter(
        universalPlatform, toPlatform,
        blockTypeAdapters.addFirst(FromUniversalBlockTypeAdapter.default()),
    )

    private val blockPropertyValuesConverter = BlockPropertyValuesConverter(
        universalPlatform, toPlatform,
        blockPropertyValueAdapters.addFirst(FromUniversalBlockPropertyValuesAdapter.default())
    )

    private val blockStateConverter = BlockStateConverter(
        universalPlatform, toPlatform,
        blockTypeConverter, blockPropertyValuesConverter,
        blockStateAdapters
    )

    private val blockLayersConverter = BlockLayersConverter(
        universalPlatform, toPlatform,
        blockStateConverter,
        blockLayersAdapters
    )

    private val blockEntityConverter = BlockEntityConverter(
        universalPlatform, toPlatform,
        blockEntityAdapters,
    )

    private val entityConverter = EntityConverter(
        universalPlatform, toPlatform,
        entityAdapters
    )

    private val blockConverter = BlockConverter(
        universalPlatform, toPlatform,
        blockLayersConverter, blockEntityConverter, entityConverter,
        blockAdapters
    )

    private val structureConverter = StructureConverter(
        universalPlatform, toPlatform,
        blockConverter
    )

    private fun <I, A> Adapters<I, A>?.addFirst(vararg adapters: A): Adapters<I, A> {
        return this?.copy(
            firstAdapters = listOf(*adapters) + firstAdapters
        ) ?: Adapters(firstAdapters = adapters.toList())
    }

    fun convertStructure(from: UniversalStructure) = structureConverter.convert(from)
    fun convertAllStructures(from: Flow<UniversalStructure>) = structureConverter.convertAll(from)
}
