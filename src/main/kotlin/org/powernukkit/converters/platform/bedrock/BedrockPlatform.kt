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

package org.powernukkit.converters.platform.bedrock

import org.powernukkit.converters.conversion.adapter.PlatformAdapters
import org.powernukkit.converters.conversion.universal.from.FromUniversalConverter
import org.powernukkit.converters.conversion.universal.to.ToUniversalConverter
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.platform.base.BaseConstructors
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.bedrock.block.*
import org.powernukkit.converters.platform.bedrock.entity.BedrockEntity
import org.powernukkit.converters.platform.bedrock.entity.BedrockEntityType
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.storage.api.Dialect

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class BedrockPlatform(
    universal: UniversalPlatform,
    val dialect: Dialect? = null,
    name: String = dialect?.name ?: "Bedrock"
) : BasePlatform<BedrockPlatform>(
    BaseConstructors(
        ::BedrockBlockState, ::BedrockBlockProperty, ::BedrockBlockEntityType,
        ::BedrockBlockEntityDataType, ::BedrockBlockType, ::BedrockBlock, ::BedrockBlock,
        ::BedrockBlockPropertyValueInt, ::BedrockBlockPropertyValueString, ::BedrockBlockPropertyValueBoolean,
        ::BedrockBlockEntity, ::BedrockEntityType, ::BedrockEntity
    ),
    universal, name, MinecraftEdition.BEDROCK
) {
    override fun getBlockType(legacyId: Int): PlatformBlockType<BedrockPlatform>? {
        return null
    }

    override fun convertToUniversal(adapters: PlatformAdapters<BedrockPlatform, UniversalPlatform>?): ToUniversalConverter<BedrockPlatform> {
        return ToUniversalConverter(this, universal)
    }

    override fun convertFromUniversal(adapters: PlatformAdapters<UniversalPlatform, BedrockPlatform>?): FromUniversalConverter<BedrockPlatform> {
        return FromUniversalConverter(universal, this)
    }
}
