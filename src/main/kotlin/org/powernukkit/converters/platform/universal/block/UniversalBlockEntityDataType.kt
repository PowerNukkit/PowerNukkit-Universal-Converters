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

package org.powernukkit.converters.platform.universal.block

import org.powernukkit.converters.internal.enumMapOfNonNullsOrEmpty
import org.powernukkit.converters.internal.enumSetOfNonNullsOrEmpty
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.block.PlatformBlockEntityDataType
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.definitions.model.block.entity.ModelData

/**
 * @author joserobjr
 * @since 2020-10-13
 */
class UniversalBlockEntityDataType(
    platform: UniversalPlatform,
    name: String,
    type: ModelData.Type,
    optional: Boolean,
    default: String?,

    val editionId: Map<MinecraftEdition, String> = emptyMap(),
    val editionOptional: Set<MinecraftEdition>,
    val editionType: Map<MinecraftEdition, ModelData.Type> = emptyMap(),
    val editionRequiresAdapter: Set<MinecraftEdition>,
) : PlatformBlockEntityDataType<UniversalPlatform>(
    platform, name, type, optional, default
) {
    constructor(platform: UniversalPlatform, model: ModelData) : this(
        platform, model.name, model.type, model.optional, model.default,

        editionId = enumMapOfNonNullsOrEmpty(
            model.bedrockName?.let { MinecraftEdition.BEDROCK to it },
            model.javaName?.let { MinecraftEdition.JAVA to it },
        ),

        editionOptional = enumSetOfNonNullsOrEmpty(
            if (model.bedrockOptional ?: model.optional) MinecraftEdition.BEDROCK else null,
            if (model.javaOptional ?: model.optional) MinecraftEdition.JAVA else null,
        ),

        editionType = enumMapOfNonNullsOrEmpty(
            model.bedrockType?.let { MinecraftEdition.BEDROCK to it },
            model.javaType?.let { MinecraftEdition.JAVA to it },
        ),

        editionRequiresAdapter = enumSetOfNonNullsOrEmpty(
            if (model.bedrockRequiresAdapter) MinecraftEdition.BEDROCK else null,
            if (model.javaRequiresAdapter) MinecraftEdition.JAVA else null,
        )
    )

    fun getEditionId(edition: MinecraftEdition) = editionId.getOrDefault(edition, name)
    fun isOptionalInEdition(edition: MinecraftEdition) = edition in editionOptional
    fun getEditionType(edition: MinecraftEdition) = editionType.getOrDefault(edition, type)
    fun isAdapterRequiredInEdition(edition: MinecraftEdition) = edition in editionRequiresAdapter
    fun getEditionDefault(edition: MinecraftEdition) = default
}
