/*
 *  PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2021  José Roberto de Araújo Júnior
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
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.conversion.context

import org.powernukkit.converters.conversion.converter.ConversionProblem
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockState

/**
 * @author joserobjr
 * @since 2020-10-18
 */
data class BlockLayersFullConversionContext<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromLayers: List<PlatformBlockState<FromPlatform>>,
    val parentContext: BlockConversionContext<FromPlatform, ToPlatform>
) : ProblemHolder {
    var layersRequiresAdapter: Boolean = false
    // TODO Commented because BlockConversionContext already has toLayers, seems to be a good idea to use it instead
    //var toLayers: List<PlatformBlockState<ToPlatform>>? = null

    val fromPlatform get() = parentContext.fromPlatform
    val toPlatform get() = parentContext.toPlatform
    val fromBlock get() = parentContext.fromBlock
    val fromPos get() = parentContext.fromPos
    val fromContainer get() = parentContext.fromContainer

    val toContainer get() = parentContext.toContainer

    var toLayers
        get() = parentContext.toLayers
        set(value) {
            parentContext.toLayers = value
        }

    var toBlockEntity
        get() = parentContext.toBlockEntity
        set(value) {
            parentContext.toBlockEntity = value
        }

    var toEntities
        get() = parentContext.toEntities
        set(value) {
            parentContext.toEntities = value
        }

    override val problems get() = parentContext.problems
    override operator fun plusAssign(problem: ConversionProblem) = parentContext.plusAssign(problem)
}
