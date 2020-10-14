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

package org.powernukkit.converters.platform.universal

import org.powernukkit.converters.internal.toMapOfList
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.block.*
import org.powernukkit.converters.platform.universal.definitions.model.ModelDefinitions

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class UniversalPlatform internal constructor(
    definitions: ModelDefinitions
) : Platform<UniversalPlatform>("Universal", MinecraftEdition.UNIVERSAL) {
    val optionalBlockPropertyValue = UniversalBlockPropertyValueEmptyOpt(this)

    val blockPropertiesById = definitions.blockProperties
        .map { UniversalBlockProperty(this, it) }
        .associateBy(UniversalBlockProperty::id)

    val blockPropertiesByEditionId = blockPropertiesById.values.asSequence()
        .flatMap { property ->
            MinecraftEdition.values().asSequence()
                .map { it to (property.editionId.getOrDefault(it, property.id) to property) }
        }
        .groupBy { it.first }
        .mapValues { (_, value) ->
            value.map { it.second }.asSequence().toMapOfList()
        }

    val blockEntityTypesById = definitions.blockEntities
        .map { UniversalBlockEntityType(this, it) }
        .associateBy(UniversalBlockEntityType::id)

    val blockEntityTypesByEditionId = blockEntityTypesById.createEditionIdMap(
        UniversalBlockEntityType::id,
        UniversalBlockEntityType::editionId
    )

    val blockTypesById = definitions.blockTypes
        .map { UniversalBlockType(this, it) }
        .associateBy(UniversalBlockType::id)

    val blockTypesByEditionId = blockTypesById.createEditionIdMap(
        UniversalBlockType::id,
        UniversalBlockType::editionId,
    )

    override val airBlockType =
        checkNotNull(blockTypesById[NamespacedId("air")]) { "The minecraft:air block type is not registered" }
    override val airBlockState = UniversalBlockState(airBlockType)

    fun getBlockPropertyByEditionId(edition: MinecraftEdition, propertyId: String): List<UniversalBlockProperty> {
        return blockPropertiesByEditionId[edition]?.get(propertyId) ?: emptyList()
    }

    override fun toString(): String {
        return "UniversalPlatform(name='$name', minecraftEdition=$minecraftEdition, blockPropertiesById=$blockPropertiesById, blockTypesById=$blockTypesById)"
    }

    private fun <K : Any, T> Map<K, T>.createEditionIdMap(
        getId: T.() -> K,
        getEditionIds: T.() -> Map<MinecraftEdition, K>
    ): Map<MinecraftEdition, Map<K, T>> =
        values.asSequence()
            .flatMap { obj ->
                MinecraftEdition.values().asSequence()
                    .map { it to (obj.getEditionIds().getOrElse(it) { obj.getId() } to obj) }
            }
            .groupBy { it.first }
            .mapValues { (_, value) ->
                value.map { it.second }.toMap()
            }
}
