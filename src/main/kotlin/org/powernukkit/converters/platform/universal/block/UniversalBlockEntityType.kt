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

import org.powernukkit.converters.internal.enumMapOf
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.block.PlatformBlockEntityType
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.definitions.model.block.entity.ModelBlockEntity

/**
 * @author joserobjr
 * @since 2020-10-10
 */
class UniversalBlockEntityType(
    platform: UniversalPlatform,
    id: String,
    val editionId: Map<MinecraftEdition, String> = emptyMap(),

    override val data: Map<String, UniversalBlockEntityDataType>,
): PlatformBlockEntityType<UniversalPlatform>(platform, id) {
    override val universalType get() = this

    constructor(platform: UniversalPlatform, model: ModelBlockEntity) : this(
        platform, model.id,

        editionId = enumMapOf(
            MinecraftEdition.BEDROCK to model.bedrock,
            MinecraftEdition.JAVA to model.java
        ),

        data = model.dataSpecification.associate { spec ->
            spec.name to UniversalBlockEntityDataType(platform, spec)
        }
    )

    fun getEditionId(edition: MinecraftEdition) = editionId.getOrDefault(edition, id)
}
