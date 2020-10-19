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

package org.powernukkit.converters.conversion.universal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.powernukkit.converters.conversion.converter.ConversionProblem
import org.powernukkit.converters.conversion.converter.PlatformConverter
import org.powernukkit.converters.conversion.universal.from.FromUniversalConverter
import org.powernukkit.converters.conversion.universal.to.ToUniversalConverter
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformStructure
import org.powernukkit.converters.platform.universal.UniversalPlatform

/**
 * @author joserobjr
 * @since 2020-10-19
 */
class ChainedConverter<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    fromPlatform: FromPlatform,
    toPlatform: ToPlatform,
    private val toUniversalConverter: ToUniversalConverter<FromPlatform>,
    private val fromUniversalConverter: FromUniversalConverter<ToPlatform>,
) : PlatformConverter<FromPlatform, ToPlatform>(fromPlatform, toPlatform) {
    override fun convertStructure(from: PlatformStructure<FromPlatform>): Pair<PlatformStructure<ToPlatform>, List<ConversionProblem>> {
        val totalProblems = mutableListOf<ConversionProblem>()
        val (universalStructure, universalProblems) = toUniversalConverter.convertStructure(from)
        totalProblems += universalProblems

        val (toStructure, moreProblems) = fromUniversalConverter.convertStructure(universalStructure)
        totalProblems += moreProblems
        return toStructure to totalProblems
    }

    override fun CoroutineScope.convertAllStructures(
        from: ReceiveChannel<PlatformStructure<FromPlatform>>,
        to: SendChannel<PlatformStructure<ToPlatform>>,
        problems: SendChannel<ConversionProblem>?
    ) = launch {
        val universalStructures = Channel<PlatformStructure<UniversalPlatform>>()
        try {
            listOf(
                with(toUniversalConverter) {
                    convertAllStructures(from, universalStructures, problems)
                },
                with(fromUniversalConverter) {
                    convertAllStructures(universalStructures, to, problems)
                }
            ).joinAll()
        } finally {
            universalStructures.close()
        }
    }
}
