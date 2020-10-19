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

package org.powernukkit.converters.conversion.universal.from

import org.powernukkit.converters.conversion.adapter.BlockPropertyValuesAdapter
import org.powernukkit.converters.conversion.context.BlockPropertyValuesConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockPropertyValue
import org.powernukkit.converters.platform.universal.block.UniversalBlockType

/**
 * @author joserobjr
 * @since 2020-10-18
 */
interface FromUniversalBlockPropertyValuesAdapter<ToPlatform : Platform<ToPlatform>> :
    BlockPropertyValuesAdapter<UniversalPlatform, ToPlatform> {

    override fun adaptBlockPropertyValues(context: BlockPropertyValuesConversionContext<UniversalPlatform, ToPlatform>) {
        val universalValues = context.fromValues.mapValues { it as UniversalBlockPropertyValue }

        val universalType = context.fromBlockState.type as UniversalBlockType
        val toType = context.toType

        val toPlatform = context.toPlatform
        val toEdition = toPlatform.minecraftEdition

        if (toEdition in universalType.editionRequiresAdapter) {
            return
        }

        toType.blockProperties.mapValues { (toName, toProperty) ->
            val universalProperty = toProperty.universal
                ?: universalType.findPropertyByEditionId(toEdition, toName)
                ?: context.addProblem(
                    "Could not find $toEdition property $toName in internal mapping of $toType while converting " +
                            "the universal property ${toProperty.id} from the universal type ${universalType.id}"
                ) ?: return

            val universalValue = universalValues[universalProperty.id]
                ?: context.addProblem(
                    "Could not find the universal property ${universalProperty.id} in the universal values: $universalValues"
                ) ?: return

            val toValueInString = universalValue.getEditionValue(toEdition)
            try {
                toProperty.getPlatformValue(toValueInString)
            } catch (e: NoSuchElementException) {
                context.addProblem(
                    "Could not find the $toEdition property value $toValueInString in the property ${toProperty.id}" +
                            " of the ${toPlatform.name} block type ${toType.id} while converting the property" +
                            " ${universalProperty.id} of the universal type ${universalType.id}",
                    e
                )
            }
        }
    }

    companion object {
        private object Default : FromUniversalBlockPropertyValuesAdapter<UniversalPlatform>

        @Suppress("UNCHECKED_CAST")
        fun <ToPlatform : Platform<ToPlatform>> default() =
            Default as FromUniversalBlockPropertyValuesAdapter<ToPlatform>
    }
}
