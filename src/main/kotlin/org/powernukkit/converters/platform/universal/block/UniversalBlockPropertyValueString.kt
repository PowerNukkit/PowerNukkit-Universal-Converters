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
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.block.IPlatformBlockPropertyValue
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.definitions.model.block.property.ModelValue

/**
 * @author joserobjr
 * @since 2020-10-12
 */
class UniversalBlockPropertyValueString(
    platform: UniversalPlatform,
    override val stringValue: String,
    editionValue: Map<MinecraftEdition, String>,
    default: Boolean,
) : UniversalBlockPropertyValue(platform, editionValue, default) {
    override val type get() = IPlatformBlockPropertyValue.Type.STRING

    constructor(platform: UniversalPlatform, model: ModelValue) : this(
        platform = platform,
        default = model.default,
        stringValue = model.value,

        editionValue = enumMapOfNonNullsOrEmpty(
            model.java?.let { MinecraftEdition.JAVA to it },
            model.bedrock?.let { MinecraftEdition.BEDROCK to it }
        )
    )
}
