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

import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.base.entity.BaseEntity
import org.powernukkit.converters.platform.universal.block.UniversalBlockType
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BaseBlockType<
        P : BasePlatform<
                P, BlockProperty, BlockEntityType, BlockType, BlockState,
                BlockPropertyValue, BlockEntityDataType, Block, Structure, BlockEntity, Entity
                >,
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
    platform: P,
    private val baseConstructors: BaseConstructors,
    id: NamespacedId,
    final override val blockProperties: Map<String, BlockProperty>,
    final override val blockEntityType: BlockEntityType? = null,
    final override val universalType: UniversalBlockType?
) : PlatformBlockType<P>(platform, id) {
    constructor(
        platform: P,
        baseConstructors: BaseConstructors,
        id: NamespacedId,
        universalType: UniversalBlockType,
        extraBlock: ModelExtraBlock? = null
    ) : this(
        platform, baseConstructors, id,
        universalType = universalType,

        blockProperties = universalType.editionBlockProperties.getOrDefault(platform.minecraftEdition, emptyList())
            .takeUnless { extraBlock?.inheritProperties == false }
            .let { inheritance ->
                val universalProperties = extraBlock?.usesProperties?.map { (name) ->
                    requireNotNull(platform.universal.blockPropertiesById[name]) {
                        "Could not find the universal block property $name for the block type $id in $platform"
                    }
                } ?: emptyList()

                (inheritance ?: emptyList()) + universalProperties
            }
            .map {
                requireNotNull(platform.blockPropertiesByUniversalId[it.id]) {
                    val editionId = it.getEditionId(platform.minecraftEdition)
                    "Could not find the block property $editionId (universal:${it.id}) in the platform ${platform.name}/${platform.minecraftEdition} "
                }
            }
            .let { list ->
                list.associateBy { it.id }
            },

        blockEntityType = universalType.editionBlockEntityType[platform.minecraftEdition]?.let {
            val editionId = it.getEditionId(platform.minecraftEdition)
            requireNotNull(platform.blockEntityTypesById[editionId]) {
                "Could not find the block entity type $editionId (universal:${it.id}) in the platform ${platform.name}/${platform.minecraftEdition} "
            }
        },
    )

    override fun defaultPropertyValues(): Map<String, BlockPropertyValue> {
        return blockProperties.values.associate { property ->
            val value = property.values.firstOrNull { it.default }
                ?: property.values.first()
            property.id to value
        }
    }

    override fun withState(values: Map<String, PlatformBlockPropertyValue<P>>): PlatformBlockState<P> {
        val adjustedValues = blockProperties.mapValues { (propertyName, blockProperty) ->
            requireNotNull(values[propertyName], { "Missing the required property: $propertyName" })
                .let { propertyValue ->
                    @Suppress("UNCHECKED_CAST")
                    blockProperty.getPlatformValue(propertyValue) as BlockPropertyValue
                }
        }

        return baseConstructors.createBlockState(this, adjustedValues)
    }
}
