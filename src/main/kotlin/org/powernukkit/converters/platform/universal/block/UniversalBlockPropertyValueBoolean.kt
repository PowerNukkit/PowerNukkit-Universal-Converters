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
import org.powernukkit.converters.platform.api.block.IPlatformBlockPropertyValue
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.definitions.model.block.property.ModelBoolean

/**
 * @author joserobjr
 * @since 2020-10-12
 */
class UniversalBlockPropertyValueBoolean(
    platform: UniversalPlatform,
    private val value: Boolean,
    editionValue: Map<MinecraftEdition, String>,
) : UniversalBlockPropertyValue(platform, editionValue, false) {
    override val type get() = IPlatformBlockPropertyValue.Type.BOOLEAN

    constructor(platform: UniversalPlatform, value: Boolean, model: ModelBoolean) : this(
        platform, value,
        enumMapOf(
            MinecraftEdition.JAVA to (if (value) model.javaTrue else model.javaFalse),
            MinecraftEdition.BEDROCK to (if (value) model.bedrockTrue else model.bedrockFalse),
        )
    )

    override val stringValue get() = value.toString()
    override fun intValue() = if (value) 1 else 0
    override fun booleanValue() = value
}
