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

package org.powernukkit.converters.platform.java.adapters.block

import org.powernukkit.converters.conversion.adapter.BlockPropertyValuesAdapter
import org.powernukkit.converters.conversion.adapter.FromToAdapter
import org.powernukkit.converters.conversion.adapter.PlatformAdapters
import org.powernukkit.converters.conversion.adapter.addToFirstList
import org.powernukkit.converters.conversion.context.BlockPropertyValuesConversionContext
import org.powernukkit.converters.conversion.converter.ConversionProblem
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.UniversalBlockId.STONE
import org.powernukkit.converters.platform.universal.UniversalBlockPropertyId.STONE_TYPE
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockState

/**
 * @author joserobjr
 * @since 2020-10-19
 */
class JavaStoneAdapter : FromToAdapter<JavaPlatform, UniversalPlatform> {
    val fromUniversal = object : BlockPropertyValuesAdapter<UniversalPlatform, JavaPlatform> {
        override fun adaptBlockPropertyValues(context: BlockPropertyValuesConversionContext<UniversalPlatform, JavaPlatform>) {
            val fromState = context.fromBlockState as UniversalBlockState
            if (fromState.type.id != STONE) {
                return
            }

            val toPlatform = context.toPlatform

            val stoneType = fromState.values[STONE_TYPE]
                ?: context.addProblem("The universal block property $STONE_TYPE is not defined in $fromState")
                ?: return

            val javaTypeId = NamespacedId(stoneType.getEditionValue(toPlatform.minecraftEdition))
            val toType = toPlatform.getBlockType(javaTypeId)
                ?: context.addProblem("Could not find the block type $javaTypeId in the ${toPlatform.name} platform")
                ?: return

            if (toType.blockProperties.isNotEmpty()) {
                context += ConversionProblem("Expected the ${toPlatform.name} block type to have no properties.")
                return
            }

            context.toBlockType = toType
            context.toBlockPropertyValues = emptyMap()
        }
    }

    val toUniversal = object : BlockPropertyValuesAdapter<JavaPlatform, UniversalPlatform> {
        override fun adaptBlockPropertyValues(context: BlockPropertyValuesConversionContext<JavaPlatform, UniversalPlatform>) {
            val universalPlatform = context.toPlatform
            val stoneTypes = universalPlatform.blockPropertiesById[STONE_TYPE]
                ?: context.addProblem("Could not find the universal block property $STONE_TYPE in the universal platform")
                ?: return

            val fromState = context.fromBlockState
            if (fromState.values.isNotEmpty()) {
                context += ConversionProblem(
                    "Expected the ${context.fromPlatform.name} block state " +
                            "${fromState.type.id} to have no properties but it has ${fromState.values}"
                )
                return
            }

            val fromTypeStringId = fromState.type.id.name
            val propertyValue = stoneTypes.values.firstOrNull { it.stringValue == fromTypeStringId }
                ?: context.addProblem("Could not find the $STONE_TYPE property value $fromTypeStringId")
                ?: return

            val toType = universalPlatform.blockTypesById[STONE]
                ?: context.addProblem("Could not find the universal block type $STONE")
                ?: return

            context.toBlockType = toType
            context.toBlockPropertyValues = mapOf(STONE_TYPE to propertyValue)
        }
    }

    override fun addToAtoB(adapters: PlatformAdapters<JavaPlatform, UniversalPlatform>) = with(adapters) {
        copy(
            blockPropertyValueAdapters = blockPropertyValueAdapters.addToFirstList(toUniversal)
        )
    }

    override fun addToBtoA(adapters: PlatformAdapters<UniversalPlatform, JavaPlatform>) = with(adapters) {
        copy(
            blockPropertyValueAdapters = blockPropertyValueAdapters.addToFirstList(fromUniversal)
        )
    }
}
