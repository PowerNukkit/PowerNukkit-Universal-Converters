/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2020  José Roberto de Araújo Júnior
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
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.java

import org.powernukkit.converters.api.MinecraftEdition
import org.powernukkit.converters.api.NamespacedId
import org.powernukkit.converters.api.Platform
import org.powernukkit.converters.java.block.JavaBlockEntityType
import org.powernukkit.converters.java.block.JavaBlockProperty
import org.powernukkit.converters.java.block.JavaBlockState
import org.powernukkit.converters.java.block.JavaBlockType
import org.powernukkit.converters.universal.UniversalPlatform

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class JavaPlatform(
    val universal: UniversalPlatform,
    name: String
) : Platform<JavaPlatform>(name, MinecraftEdition.JAVA) {
    //val blockPropertiesByUniversalId =
    //    checkNotNull(universal.blockPropertiesByEditionId[minecraftEdition]) { "The universal platform is missing block properties definitions for $minecraftEdition" }
    //        .entries.associate { (id, universalProperty) -> 
    //            universalProperty.id to JavaBlockProperty(this, id, universalProperty) 
    //        }

    val blockPropertiesByUniversalId = universal.blockPropertiesById
        .mapValues { (_, universalProperty) ->
            JavaBlockProperty(this, universalProperty.getEditionId(minecraftEdition), universalProperty)
        }

    val blockEntityTypesById =
        checkNotNull(universal.blockEntityTypesByEditionId[minecraftEdition]) { "The universal platform is missing block entity types definitions for $minecraftEdition" }
            .mapValues { (id, universalEntityType) ->
                JavaBlockEntityType(this, id, universalEntityType)
            }

    val blockTypesById =
        checkNotNull(universal.blockTypesByEditionId[minecraftEdition]) { "The universal platform is missing block types definitions for $minecraftEdition" }
            .let { universalTypes ->
                val mainTypes = universalTypes.mapValues { (id, universalBlockType) ->
                    JavaBlockType(this, id, universalBlockType)
                }

                val extraTypes = universalTypes.values.asSequence()
                    .flatMap { universalType ->
                        universalType.extraBlocks[minecraftEdition]?.asSequence()
                            ?.map {
                                val id = NamespacedId(it.id)
                                id to JavaBlockType(this, id, universalType, it)
                            } ?: emptySequence()
                    }.toMap()

                mainTypes + extraTypes
            }

    override val airBlockType = checkNotNull(blockTypesById[NamespacedId("air")]) {
        "The minecraft:air block type is not registered"
    }

    override val airBlockState = JavaBlockState(airBlockType)
}
