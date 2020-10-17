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

package org.powernukkit.converters.platform.base

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.TechnicalValues
import org.powernukkit.converters.platform.api.block.PlatformBlockEntity
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.entity.PlatformEntity
import org.powernukkit.converters.platform.base.block.*
import org.powernukkit.converters.platform.base.entity.BaseEntity
import org.powernukkit.converters.platform.universal.UniversalPlatform

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BasePlatform<
        P : BasePlatform<
                P, BlockProperty, BlockEntityType, BlockType, BlockState,
                BlockPropertyValue, BlockEntityDataType, Block, Structure, BlockEntity, Entity>,
        BlockProperty : BaseBlockProperty<P, BlockPropertyValue>,
        BlockEntityType : BaseBlockEntityType<P, BlockEntityDataType>,
        BlockType : BaseBlockType<P, BlockProperty, BlockEntityType, BlockPropertyValue>,
        BlockState : BaseBlockState<P, BlockType, BlockProperty, BlockPropertyValue>,
        BlockPropertyValue : BaseBlockPropertyValue<P>,
        BlockEntityDataType : BaseBlockEntityDataType<P>,
        Block : BaseBlock<P, BlockState, BlockEntity, Entity>,
        Structure : BaseStructure<P, Block>,
        BlockEntity : BaseBlockEntity<P, BlockEntityType>,
        Entity : BaseEntity<P>
        >(
    val universal: UniversalPlatform,
    name: String,
    minecraftEdition: MinecraftEdition,

    ) : Platform<P, Block>(name, minecraftEdition) {

    @Suppress("LeakingThis")
    private val constructors = createBaseConstructors()

    protected abstract fun createBaseConstructors(): BaseConstructors<
            P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
            BlockEntityDataType, Block, Structure, BlockEntity, Entity
            >

    val blockPropertiesByUniversalId = universal.blockPropertiesById
        .mapValues { (_, universalProperty) ->
            constructors.createBlockProperty(universalProperty.getEditionId(minecraftEdition), universalProperty)
        }

    val blockEntityTypesById =
        checkNotNull(universal.blockEntityTypesByEditionId[minecraftEdition]) { "The universal platform is missing block entity types definitions for $minecraftEdition" }
            .mapValues { (id, universalEntityType) ->
                val values = universalEntityType.data.values.asSequence()
                    .map { it.getEditionId(minecraftEdition) to it }
                    .filterNot { (id) -> id == TechnicalValues.MISSING }
                    .associate { (id, universal) -> id to constructors.createBlockEntityDataType(universal) }

                constructors.createBlockEntityType(id, universalEntityType, values)
            }

    val blockTypesById =
        checkNotNull(universal.blockTypesByEditionId[minecraftEdition]) { "The universal platform is missing block types definitions for $minecraftEdition" }
            .let { universalTypes ->
                val mainTypes = universalTypes.mapValues { (id, universalBlockType) ->
                    constructors.createBlockType(id, universalBlockType)
                }

                val extraTypes = universalTypes.values.asSequence()
                    .flatMap { universalType ->
                        universalType.extraBlocks[minecraftEdition]?.asSequence()
                            ?.map {
                                val id = NamespacedId(it.id)
                                id to constructors.createBlockType(id, universalType, it)
                            } ?: emptySequence()
                    }.toMap()

                mainTypes + extraTypes
            }

    final override val airBlockType = checkNotNull(blockTypesById[NamespacedId("air")]) {
        "The minecraft:air block type is not registered"
    }

    final override val airBlockState = constructors.createBlockState(airBlockType)

    final override val airBlock = constructors.createBlock(airBlockState)

    @Suppress("UNCHECKED_CAST")
    final override fun createPlatformBlock(
        blockLayers: List<PlatformBlockState<P>>,
        blockEntity: PlatformBlockEntity<P>?,
        entities: List<PlatformEntity<P>>
    ) = constructors.createBlock(
        blockLayers as List<BlockState>,
        blockEntity as BlockEntity?,
        entities as List<Entity>
    )

    @Suppress("UNCHECKED_CAST")
    final override fun createPlatformBlock(
        blockState: PlatformBlockState<P>,
        blockEntity: PlatformBlockEntity<P>?,
        entities: List<PlatformEntity<P>>
    ) = constructors.createBlock(
        blockState as BlockState, blockEntity as BlockEntity?,
        entities as List<Entity>
    )
}
