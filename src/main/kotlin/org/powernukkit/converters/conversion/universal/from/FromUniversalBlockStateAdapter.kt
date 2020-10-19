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

import org.powernukkit.converters.conversion.adapter.BlockStateAdapter
import org.powernukkit.converters.conversion.context.BlockStateConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockState

/**
 * @author joserobjr
 * @since 2020-10-19
 */
interface FromUniversalBlockStateAdapter<ToPlatform : Platform<ToPlatform>> :
    BlockStateAdapter<UniversalPlatform, ToPlatform> {

    override fun adaptBlockState(context: BlockStateConversionContext<UniversalPlatform, ToPlatform>) {
        if (context.toBlockStates != null || context.valuesRequiresAdapter) {
            return
        }

        val fromEdition = context.fromPlatform.minecraftEdition

        val universalBlockState = context.fromBlockState as UniversalBlockState
        val universalBlockType = universalBlockState.type

        if (fromEdition in universalBlockType.editionRequiresAdapter) {
            context.typeRequiresAdapter = true
        }

        val universalPropertiesToEdition = universalBlockType.editionBlockProperties[fromEdition] ?: return
        if (universalPropertiesToEdition.any { it.isEditionAdapterRequired(fromEdition) }) {
            context.valuesRequiresAdapter = true
        }
    }

    override fun adaptBlockStateList(context: BlockStateConversionContext<UniversalPlatform, ToPlatform>) {
        // Does nothing
    }

    companion object {
        private object Default : FromUniversalBlockStateAdapter<UniversalPlatform>

        @Suppress("UNCHECKED_CAST")
        fun <ToPlatform : Platform<ToPlatform>> default() =
            Default as FromUniversalBlockStateAdapter<ToPlatform>
    }
}
