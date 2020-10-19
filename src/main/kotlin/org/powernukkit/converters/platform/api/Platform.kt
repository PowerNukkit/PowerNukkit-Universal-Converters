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

package org.powernukkit.converters.platform.api

import org.powernukkit.converters.conversion.adapter.PlatformAdapters
import org.powernukkit.converters.conversion.universal.from.FromUniversalConverter
import org.powernukkit.converters.conversion.universal.to.ToUniversalConverter
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.block.*
import org.powernukkit.converters.platform.api.entity.PlatformEntity
import org.powernukkit.converters.platform.universal.UniversalPlatform

abstract class Platform<P : Platform<P>>(
    val name: String,
    val minecraftEdition: MinecraftEdition
) {
    abstract val airBlockType: PlatformBlockType<P>
    abstract val airBlockState: PlatformBlockState<P>
    abstract val airBlock: PlatformBlock<P>

    abstract fun convertToUniversal(adapters: PlatformAdapters<P, UniversalPlatform>? = null): ToUniversalConverter<P>
    abstract fun convertFromUniversal(adapters: PlatformAdapters<UniversalPlatform, P>? = null): FromUniversalConverter<P>

    abstract fun createStructure(worldPos: BlockPos, size: Int = 1): PlatformStructure<P>

    abstract fun createPlatformBlock(
        blockState: PlatformBlockState<P>,
        blockEntity: PlatformBlockEntity<P>? = null,
        entities: List<PlatformEntity<P>> = emptyList()
    ): PlatformBlock<P>

    abstract fun createPlatformBlock(
        blockLayers: List<PlatformBlockState<P>>,
        blockEntity: PlatformBlockEntity<P>? = null,
        entities: List<PlatformEntity<P>> = emptyList()
    ): PlatformBlock<P>

    abstract fun getBlockType(id: NamespacedId): PlatformBlockType<P>?

    override fun toString(): String {
        return "Platform(name='$name', minecraftEdition=$minecraftEdition)"
    }
}
