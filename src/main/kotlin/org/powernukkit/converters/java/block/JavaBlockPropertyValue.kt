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

package org.powernukkit.converters.java.block

import org.powernukkit.converters.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.java.JavaPlatform
import org.powernukkit.converters.universal.block.UniversalBlockProperty
import org.powernukkit.converters.universal.block.UniversalBlockPropertyValue

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class JavaBlockPropertyValue(
    platform: JavaPlatform,
    val universalValue: UniversalBlockPropertyValue?
) : PlatformBlockPropertyValue<JavaPlatform>(platform) {
    companion object {
        fun createList(platform: JavaPlatform, universal: UniversalBlockProperty): List<JavaBlockPropertyValue> {
            return universal.values.map { value ->
                createValue(platform, value)
            }
        }

        fun createValue(platform: JavaPlatform, universalValue: UniversalBlockPropertyValue): JavaBlockPropertyValue {
            val value = universalValue.getEditionValue(platform.minecraftEdition)
            val int = value.toIntOrNull()
            if (int != null) {
                return JavaBlockPropertyValueInt(platform, int, universalValue)
            }
            if (value == "true" || value == "false") {
                return JavaBlockPropertyValueBoolean(platform, value.toBoolean(), universalValue)
            }
            return JavaBlockPropertyValueString(platform, value, universalValue)
        }
    }
}
