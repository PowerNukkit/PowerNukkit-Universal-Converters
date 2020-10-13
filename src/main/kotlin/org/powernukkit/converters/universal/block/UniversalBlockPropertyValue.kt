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

package org.powernukkit.converters.universal.block

import org.powernukkit.converters.api.MinecraftEdition
import org.powernukkit.converters.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.universal.UniversalPlatform
import org.powernukkit.converters.universal.definitions.model.block.property.ModelBoolean
import org.powernukkit.converters.universal.definitions.model.block.property.ModelIntRange
import org.powernukkit.converters.universal.definitions.model.block.property.ModelValue

/**
 * @author joserobjr
 * @since 2020-10-12
 */
abstract class UniversalBlockPropertyValue(
    platform: UniversalPlatform,
    val editionValue: Map<MinecraftEdition, String>
) : PlatformBlockPropertyValue<UniversalPlatform>(platform) {
    companion object {
        fun createList(platform: UniversalPlatform, model: ModelBoolean): List<UniversalBlockPropertyValue> {
            val `false` = UniversalBlockPropertyValueBoolean(platform, false, model)
            val `true`  = UniversalBlockPropertyValueBoolean(platform, true,  model)
            return listOf(`false`, `true`)
        }

        fun createList(platform: UniversalPlatform, model: ModelIntRange): List<UniversalBlockPropertyValue> {
            return model.toRange().map { value -> 
                UniversalBlockPropertyValueInt(platform, value)
            }
        }

        fun createList(platform: UniversalPlatform, values: List<ModelValue>): List<UniversalBlockPropertyValue> {
            return values.map { model ->
                UniversalBlockPropertyValueString(platform, model)
            }
        }
    }
}
