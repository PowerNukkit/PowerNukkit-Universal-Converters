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

package org.powernukkit.converters.platform.java

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.java.block.*
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
class JavaPlatform(
    universal: UniversalPlatform,
    name: String
) : BasePlatform<
        JavaPlatform, JavaBlockProperty, JavaBlockEntityType, JavaBlockType, JavaBlockState,
        JavaBlockPropertyValue
        >(
    universal, name, MinecraftEdition.JAVA
) {
    override fun createBlockProperty(id: String, universal: UniversalBlockProperty) =
        JavaBlockProperty(this, id, universal)

    override fun createBlockEntityType(id: String, universal: UniversalBlockEntityType) =
        JavaBlockEntityType(this, id, universal)

    override fun createBlockType(id: NamespacedId, universal: UniversalBlockType, extra: ModelExtraBlock?) =
        JavaBlockType(this, id, universal, extra)

    override fun createBlockState(blockType: JavaBlockType) = 
        JavaBlockState(blockType)
    
    override fun createBlockPropertyValue(int: Int, universalValue: UniversalBlockPropertyValue) =
        JavaBlockPropertyValueInt(this, int, universalValue)

    override fun createBlockPropertyValue(string: String, universalValue: UniversalBlockPropertyValue) =
        JavaBlockPropertyValueString(this, string, universalValue)

    override fun createBlockPropertyValue(boolean: Boolean, universalValue: UniversalBlockPropertyValue) =
        JavaBlockPropertyValueBoolean(this, boolean, universalValue)
}
