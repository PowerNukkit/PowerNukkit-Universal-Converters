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
import org.powernukkit.converters.platform.api.block.*
import org.powernukkit.converters.platform.api.entity.PlatformEntity
import org.powernukkit.converters.platform.base.block.BaseBlockProperty
import org.powernukkit.converters.platform.base.block.BaseBlockType
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.*
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BasePlatform<
        P : BasePlatform<P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue, BlockEntityDataType, Block, Structure, BlockEntity, Entity>,
        BlockProperty : BaseBlockProperty<P, BlockPropertyValue>,
        BlockEntityType : PlatformBlockEntityType<P>,
        BlockType : BaseBlockType<P, BlockProperty, BlockEntityType, BlockPropertyValue>,
        BlockState : PlatformBlockState<P>,
        BlockPropertyValue : PlatformBlockPropertyValue<P>,
        BlockEntityDataType : PlatformBlockEntityDataType<P>,
        Block : PlatformBlock<P>,
        Structure : PlatformStructure<P, Block>,
        BlockEntity : PlatformBlockEntity<P>,
        Entity : PlatformEntity<P>,
        >(
    val universal: UniversalPlatform,
    name: String,
    minecraftEdition: MinecraftEdition,

    ) : Platform<P, Block>(name, minecraftEdition) {
    val blockPropertiesByUniversalId = universal.blockPropertiesById
        .mapValues { (_, universalProperty) ->
            createBlockProperty(universalProperty.getEditionId(minecraftEdition), universalProperty)
        }

    val blockEntityTypesById =
        checkNotNull(universal.blockEntityTypesByEditionId[minecraftEdition]) { "The universal platform is missing block entity types definitions for $minecraftEdition" }
            .mapValues { (id, universalEntityType) ->
                val values = universalEntityType.data.values.asSequence()
                    .map { it.getEditionId(minecraftEdition) to it }
                    .filterNot { (id) -> id == TechnicalValues.MISSING }
                    .associate { (id, universal) -> id to createBlockEntityDataType(universal) }

                createBlockEntityType(id, universalEntityType, values)
            }

    val blockTypesById =
        checkNotNull(universal.blockTypesByEditionId[minecraftEdition]) { "The universal platform is missing block types definitions for $minecraftEdition" }
            .let { universalTypes ->
                val mainTypes = universalTypes.mapValues { (id, universalBlockType) ->
                    createBlockType(id, universalBlockType)
                }

                val extraTypes = universalTypes.values.asSequence()
                    .flatMap { universalType ->
                        universalType.extraBlocks[minecraftEdition]?.asSequence()
                            ?.map {
                                val id = NamespacedId(it.id)
                                id to createBlockType(id, universalType, it)
                            } ?: emptySequence()
                    }.toMap()

                mainTypes + extraTypes
            }

    final override val airBlockType = checkNotNull(blockTypesById[NamespacedId("air")]) {
        "The minecraft:air block type is not registered"
    }

    @Suppress("LeakingThis")
    final override val airBlockState = createBlockState(airBlockType)

    @Suppress("LeakingThis")
    final override val airBlock = createBlock(airBlockState)

    protected abstract fun createBlockProperty(id: String, universal: UniversalBlockProperty): BlockProperty
    protected abstract fun createBlockEntityType(
        id: String,
        universal: UniversalBlockEntityType,
        values: Map<String, BlockEntityDataType>
    ): BlockEntityType

    protected abstract fun createBlockEntityDataType(universal: UniversalBlockEntityDataType): BlockEntityDataType

    protected abstract fun createBlockType(
        id: NamespacedId,
        universal: UniversalBlockType,
        extra: ModelExtraBlock? = null
    ): BlockType

    @Suppress("UNCHECKED_CAST")
    final override fun createPlatformBlock(
        blockLayers: List<PlatformBlockState<P>>,
        blockEntity: PlatformBlockEntity<P>?,
        entities: List<PlatformEntity<P>>
    ) = createBlock(blockLayers as List<BlockState>, blockEntity as BlockEntity?, entities as List<Entity>)

    @Suppress("UNCHECKED_CAST")
    final override fun createPlatformBlock(
        blockState: PlatformBlockState<P>,
        blockEntity: PlatformBlockEntity<P>?,
        entities: List<PlatformEntity<P>>
    ) = createBlock(blockState as BlockState, blockEntity as BlockEntity?, entities as List<Entity>)

    protected abstract fun createBlock(
        blockState: BlockState,
        blockEntity: BlockEntity? = null,
        entities: List<Entity> = emptyList()
    ): Block

    protected abstract fun createBlock(
        blockLayers: List<BlockState>,
        blockEntity: BlockEntity? = null,
        entities: List<Entity> = emptyList()
    ): Block

    protected abstract fun createBlockState(
        blockType: BlockType,
        values: Map<String, BlockPropertyValue> = blockType.defaultPropertyValues()
    ): BlockState

    protected abstract fun createBlockPropertyValue(
        int: Int,
        universalValue: UniversalBlockPropertyValue,
        default: Boolean,
    ): BlockPropertyValue

    protected abstract fun createBlockPropertyValue(
        string: String,
        universalValue: UniversalBlockPropertyValue,
        default: Boolean,
    ): BlockPropertyValue

    protected abstract fun createBlockPropertyValue(
        boolean: Boolean,
        universalValue: UniversalBlockPropertyValue,
        default: Boolean,
    ): BlockPropertyValue

    protected open fun createBlockPropertyValue(universalValue: UniversalBlockPropertyValue): BlockPropertyValue {
        val value = universalValue.getEditionValue(minecraftEdition)
        val int = value.toIntOrNull()
        if (int != null) {
            return createBlockPropertyValue(int, universalValue, universalValue.default)
        }
        if (value == "true" || value == "false") {
            return createBlockPropertyValue(value.toBoolean(), universalValue, universalValue.default)
        }
        return createBlockPropertyValue(value, universalValue, universalValue.default)
    }

    internal fun createBlockPropertyValueList(universal: UniversalBlockProperty): List<BlockPropertyValue> {
        return universal.values.map(this::createBlockPropertyValue)
    }

    abstract override fun createStructure(size: Int): Structure
}
