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

package org.powernukkit.converters.platform.api

import org.powernukkit.converters.conversion.adapter.PlatformAdapters
import org.powernukkit.converters.conversion.universal.ChainedConverter
import org.powernukkit.converters.conversion.universal.from.FromUniversalConverter
import org.powernukkit.converters.conversion.universal.to.ToUniversalConverter
import org.powernukkit.converters.platform.api.block.*
import org.powernukkit.converters.platform.api.entity.PlatformEntity
import org.powernukkit.converters.platform.api.entity.PlatformEntityType
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
    fun <FromPlatform : Platform<FromPlatform>> convertFromUniversal(
        fromToConversion: ToUniversalConverter<FromPlatform>,
        adapters: PlatformAdapters<UniversalPlatform, P>? = null,
    ): ChainedConverter<FromPlatform, P> {
        return ChainedConverter(
            fromToConversion.fromPlatform, this as P,
            fromToConversion, convertFromUniversal(adapters)
        )
    }

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
    abstract fun getBlockType(legacyId: Int): PlatformBlockType<P>?
    abstract fun getBlockEntityType(id: String): PlatformBlockEntityType<P>?
    abstract fun getEntityType(id: String): PlatformEntityType<P>?

    override fun toString(): String {
        return "Platform(name='$name', minecraftEdition=$minecraftEdition)"
    }
}
