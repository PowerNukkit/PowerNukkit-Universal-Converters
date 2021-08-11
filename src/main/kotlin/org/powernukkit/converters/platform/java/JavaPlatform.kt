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

package org.powernukkit.converters.platform.java

import org.powernukkit.converters.conversion.adapter.PlatformAdapters
import org.powernukkit.converters.conversion.adapter.plus
import org.powernukkit.converters.conversion.universal.from.FromUniversalConverter
import org.powernukkit.converters.conversion.universal.to.ToUniversalConverter
import org.powernukkit.converters.dialect.Dialect
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.platform.base.BaseConstructors
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.java.adapters.block.JavaStoneAdapter
import org.powernukkit.converters.platform.java.block.*
import org.powernukkit.converters.platform.java.entity.JavaEntity
import org.powernukkit.converters.platform.java.entity.JavaEntityType
import org.powernukkit.converters.platform.universal.UniversalPlatform
import java.util.*

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class JavaPlatform(
    universal: UniversalPlatform,
    val dialect: Dialect? = null,
    name: String = dialect?.name ?: "Java"
) : BasePlatform<JavaPlatform>(
    BaseConstructors(
        ::JavaBlockState, ::JavaBlockProperty, ::JavaBlockEntityType, ::JavaBlockEntityDataType,
        ::JavaBlockType, ::JavaBlock, ::JavaBlock, ::JavaBlockPropertyValueInt, ::JavaBlockPropertyValueString,
        ::JavaBlockPropertyValueBoolean,
        ::JavaBlockEntity, ::JavaEntityType, ::JavaEntity
    ),
    universal, name, MinecraftEdition.JAVA
) {
    override fun getBlockType(legacyId: Int): PlatformBlockType<JavaPlatform>? {
        return getBlockType(legacyBlockTypeIds[legacyId] ?: return null)
    }

    override fun convertToUniversal(adapters: PlatformAdapters<JavaPlatform, UniversalPlatform>?): ToUniversalConverter<JavaPlatform> {
        var adjustedAdapters = (adapters ?: PlatformAdapters())
        adjustedAdapters += JavaStoneAdapter()
        return ToUniversalConverter(this, universal, adjustedAdapters)
    }

    override fun convertFromUniversal(adapters: PlatformAdapters<UniversalPlatform, JavaPlatform>?): FromUniversalConverter<JavaPlatform> {
        var adjustedAdapters = (adapters ?: PlatformAdapters())
        adjustedAdapters += JavaStoneAdapter()
        return FromUniversalConverter(universal, this, adjustedAdapters)
    }

    companion object {
        val legacyBlockTypeIds: Map<Int, NamespacedId> by lazy {
            val props =
                JavaPlatform::class.java.getResourceAsStream("definitions/java-numeric-block-type-ids.properties")
                    ?.use { Properties().apply { load(it) } }
                    ?: return@lazy emptyMap()

            props.entries.asSequence()
                .mapNotNull { (k, v) ->
                    (k.toString().toIntOrNull() ?: return@mapNotNull null) to NamespacedId(v.toString())
                }
                .toMap()
        }
    }
}
