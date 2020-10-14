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

package org.powernukkit.converters.platform.bedrock

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.bedrock.block.*
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockEntityType
import org.powernukkit.converters.platform.universal.block.UniversalBlockProperty
import org.powernukkit.converters.platform.universal.block.UniversalBlockPropertyValue
import org.powernukkit.converters.platform.universal.block.UniversalBlockType
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class BedrockPlatform(
    universal: UniversalPlatform,
    name: String
) : BasePlatform<
        BedrockPlatform, BedrockBlockProperty, BedrockBlockEntityType, BedrockBlockType, BedrockBlockState,
        BedrockBlockPropertyValue
        >(
    universal, name, MinecraftEdition.BEDROCK
) {
    override fun createBlockProperty(id: String, universal: UniversalBlockProperty) =
        BedrockBlockProperty(this, id, universal)

    override fun createBlockEntityType(id: String, universal: UniversalBlockEntityType) =
        BedrockBlockEntityType(this, id, universal)

    override fun createBlockType(id: NamespacedId, universal: UniversalBlockType, extra: ModelExtraBlock?) =
        BedrockBlockType(this, id, universal, extra)

    override fun createBlockState(blockType: BedrockBlockType) =
        BedrockBlockState(blockType)

    override fun createBlockPropertyValue(int: Int, universalValue: UniversalBlockPropertyValue) =
        BedrockBlockPropertyValueInt(this, int, universalValue)

    override fun createBlockPropertyValue(string: String, universalValue: UniversalBlockPropertyValue) =
        BedrockBlockPropertyValueString(this, string, universalValue)

    override fun createBlockPropertyValue(boolean: Boolean, universalValue: UniversalBlockPropertyValue) =
        BedrockBlockPropertyValueBoolean(this, boolean, universalValue)
}
