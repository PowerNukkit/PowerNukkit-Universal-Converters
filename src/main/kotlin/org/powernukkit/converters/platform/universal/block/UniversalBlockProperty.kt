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

package org.powernukkit.converters.platform.universal.block

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.block.PlatformBlockProperty
import org.powernukkit.converters.internal.enumMapOfNonNullsOrEmpty
import org.powernukkit.converters.internal.enumSetOfNonNullsOrEmpty
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.definitions.model.block.property.ModelBlockProperty

/**
 * @author joserobjr
 * @since 2020-10-10
 */
class UniversalBlockProperty(
    platform: UniversalPlatform,
    id: String,
    val editionId: Map<MinecraftEdition, String> = emptyMap(),
    private val editionRequiresAdapter: Set<MinecraftEdition> = emptySet(),
    override val values: List<UniversalBlockPropertyValue>
) : PlatformBlockProperty<UniversalPlatform>(platform, id) {
    constructor(platform: UniversalPlatform, model: ModelBlockProperty) : this(platform, model.id,
        enumMapOfNonNullsOrEmpty(
            model.bedrock?.let { MinecraftEdition.BEDROCK to it },
            model.java?.let { MinecraftEdition.JAVA to it }
        ),
        enumSetOfNonNullsOrEmpty(
            if (model.javaRequiresAdapter) MinecraftEdition.JAVA else null,
            if (model.bedrockRequiresAdapter) MinecraftEdition.BEDROCK else null
        ),
        when {
            model.booleanValue != null -> UniversalBlockPropertyValue.createList(platform, model.booleanValue)
            model.intRangeValue != null -> UniversalBlockPropertyValue.createList(platform, model.intRangeValue)
            model.values != null -> UniversalBlockPropertyValue.createList(platform, model.values)
            else -> error("The model don't have values: $model")
        }
    )

    override val universal get() = this

    fun getEditionId(minecraftEdition: MinecraftEdition) = editionId.getOrDefault(minecraftEdition, id)
    fun isEditionAdapterRequired(minecraftEdition: MinecraftEdition) = minecraftEdition in editionRequiresAdapter
}
