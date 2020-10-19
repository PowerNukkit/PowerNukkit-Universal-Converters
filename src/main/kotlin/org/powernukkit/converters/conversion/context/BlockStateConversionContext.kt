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

package org.powernukkit.converters.conversion.context

import org.powernukkit.converters.conversion.ConversionProblem
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType

/**
 * @author joserobjr
 * @since 2020-10-18
 */
data class BlockStateConversionContext<FromPlatform : Platform<FromPlatform>, ToPlatform : Platform<ToPlatform>>(
    val fromBlockState: PlatformBlockState<FromPlatform>,
    val parentContext: BlockLayersSingleConversionContext<FromPlatform, ToPlatform>,
) : ProblemHolder {
    var toMainBlockType: PlatformBlockType<ToPlatform>? = null
    var toMainBlockPropertyValues: Map<String, PlatformBlockPropertyValue<ToPlatform>>? = null

    var typeRequiresAdapter: Boolean = false
    var valuesRequiresAdapter: Boolean = false

    var toBlockStates: List<PlatformBlockState<ToPlatform>>? = null

    val fromPlatform get() = parentContext.fromPlatform
    val toPlatform get() = parentContext.toPlatform
    val fromBlock get() = parentContext.fromBlock
    val fromPos get() = parentContext.fromPos
    val fromContainer get() = parentContext.fromContainer
    val fromLayers get() = parentContext.fromLayers
    val fromLayer get() = parentContext.fromLayer

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

    var toBlockStateLayers
        get() = parentContext.toBlockStateLayers
        set(value) {
            parentContext.toBlockStateLayers = value
        }

    override val problems get() = parentContext.problems
    override operator fun plusAssign(problem: ConversionProblem) = parentContext.plusAssign(problem)

    fun toCompletedState(): PlatformBlockState<ToPlatform> {
        val type = requireNotNull(toMainBlockType) {
            "The type is not defined"
        }

        val values = requireNotNull(toMainBlockPropertyValues) {
            "The properties are not defined"
        }

        return type.withState(values)
    }
}
