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

import org.powernukkit.converters.conversion.adapter.BlockTypeAdapter
import org.powernukkit.converters.conversion.context.BlockTypeConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockType

/**
 * @author joserobjr
 * @since 2020-10-18
 */
interface FromUniversalBlockTypeAdapter<ToPlatform : Platform<ToPlatform>> :
    BlockTypeAdapter<UniversalPlatform, ToPlatform> {

    override fun adaptBlockType(context: BlockTypeConversionContext<UniversalPlatform, ToPlatform>) {
        val universalType = context.fromBlockType as UniversalBlockType

        val toPlatform = context.toPlatform
        val edition = toPlatform.minecraftEdition

        if (edition in universalType.editionRequiresAdapter) {
            return
        }

        val editionId = universalType.editionId[edition] ?: universalType.id

        val toBlockType = toPlatform.getBlockType(editionId)
            ?: context.addProblem(
                "The $edition edition don't have the block type $editionId registered but is associated to " +
                        "the universal block type ${universalType.id}"
            ) ?: return

        context.toBlockType = toBlockType
    }

    companion object {
        private object Default : FromUniversalBlockTypeAdapter<UniversalPlatform>

        @Suppress("UNCHECKED_CAST")
        fun <ToPlatform : Platform<ToPlatform>> default() =
            Default as FromUniversalBlockTypeAdapter<ToPlatform>
    }
}
