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

package org.powernukkit.converters.converter

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PlatformStructure

/**
 * @author joserobjr
 * @since 2020-10-15
 */
class StructureConverter<
        FromPlatform : Platform<FromPlatform, FromBlock>,
        FromBlock : PlatformBlock<FromPlatform>,
        FromStructure : PlatformStructure<FromPlatform, FromBlock>,
        ToPlatform : Platform<ToPlatform, ToBlock>,
        ToBlock : PlatformBlock<ToPlatform>,
        ToStructure : PlatformStructure<ToPlatform, ToBlock>,
        >(
    val fromPlatform: FromPlatform,
    val fromStructures: ReceiveChannel<FromStructure>,
    val toPlatform: ToPlatform,
    val toStructures: SendChannel<ToStructure>
) {
    suspend fun convertAll() {
        for (fromStructure in fromStructures) {
            toStructures.send(fromStructure.convert())
        }
    }

    private fun FromStructure.convert(): ToStructure {
        @Suppress("UNCHECKED_CAST")
        val toStructure = toPlatform.createStructure(blocks.size) as ToStructure
        val context = StructureConversionContext(fromPlatform, this, toPlatform, toStructure)
        // TODO Add adapters
        context.convert()
        return toStructure
    }
}
