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

package org.powernukkit.converters.platform.base.block

import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.PlatformBlockEntityType
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.platform.base.BaseConstructors
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockType
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BaseBlockType<P : BasePlatform<P>>(
    private val constructors: BaseConstructors<P>,
    id: NamespacedId,
    final override val blockProperties: Map<String, BaseBlockProperty<P>>,
    final override val blockEntityType: PlatformBlockEntityType<P>? = null,
    final override val universalType: UniversalBlockType?
) : PlatformBlockType<P>(constructors.platform, id) {
    constructor(
        constructors: BaseConstructors<P>,
        id: NamespacedId,
        universalType: UniversalBlockType,
        extraBlock: ModelExtraBlock? = null
    ) : this(
        constructors, id,
        universalType = universalType,

        blockProperties = universalType
            .editionBlockProperties.getOrDefault(constructors.platform.minecraftEdition, emptyList())
            .takeUnless { extraBlock?.inheritProperties == false }
            .let { inheritance ->
                val platform = constructors.platform
                val universalProperties = extraBlock?.usesProperties?.map { (name) ->
                    requireNotNull(platform.universal.blockPropertiesById[name]) {
                        "Could not find the universal block property $name for the block type $id in $platform"
                    }
                } ?: emptyList()

                (inheritance ?: emptyList()) + universalProperties
            }
            .map {
                val platform = constructors.platform
                requireNotNull(platform.blockPropertiesByUniversalId[it.id]) {
                    val editionId = it.getEditionId(platform.minecraftEdition)
                    "Could not find the block property $editionId (universal:${it.id}) in the platform ${platform.name}/${platform.minecraftEdition} "
                }
            }
            .let { list ->
                list.associateBy { it.id }
            },

        blockEntityType = universalType.editionBlockEntityType[constructors.platform.minecraftEdition]?.let {
            val platform = constructors.platform
            val editionId = it.getEditionId(platform.minecraftEdition)
            requireNotNull(platform.blockEntityTypesById[editionId]) {
                "Could not find the block entity type $editionId (universal:${it.id}) in the platform ${platform.name}/${platform.minecraftEdition} "
            }
        },
    )

    override fun defaultPropertyValues(): Map<String, BaseBlockPropertyValue<P>> {
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
                    blockProperty.getPlatformValue(propertyValue) as BaseBlockPropertyValue<P>
                }
        }

        return constructors.createBlockState(this, adjustedValues)
    }

    fun withDefaultState() = withState(defaultPropertyValues())
}
