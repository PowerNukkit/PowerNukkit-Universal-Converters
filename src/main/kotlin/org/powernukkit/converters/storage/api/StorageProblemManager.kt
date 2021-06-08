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

package org.powernukkit.converters.storage.api

import br.com.gamemods.nbtmanipulator.NbtCompound
import br.com.gamemods.nbtmanipulator.NbtTag
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockProperty
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.storage.leveldb.LevelDBChunkSection
import org.powernukkit.converters.storage.leveldb.LevelDBFailedChunkSection

/**
 * @author joserobjr
 * @since 2020-11-16
 */
open class StorageProblemManager {
    fun handleReadChunkIssue(providerWorld: ProviderWorld<*>, exception: Throwable) {
        TODO("Not yet implemented")
    }

    fun <P : Platform<P>> handleMissingBlockTypeParsingState(
        world: ProviderWorld<P>,
        storage: NbtCompound
    ): PlatformBlockType<P> {
        TODO("Not yet implemented")
    }

    fun <P : Platform<P>> handleMissingBlockPropertyParsingState(
        world: ProviderWorld<P>,
        storage: NbtCompound,
        type: PlatformBlockType<P>,
        key: String,
        valueTag: NbtTag
    ): PlatformBlockProperty<P>? {
        TODO("Not yet implemented")
    }

    fun <P : Platform<P>> handleMissingBlockPropertyValueParsingState(
        world: ProviderWorld<P>,
        storage: NbtCompound,
        key: String,
        valueTag: NbtTag,
        property: PlatformBlockProperty<P>
    ): PlatformBlockPropertyValue<P> {
        TODO("Not yet implemented")
    }

    fun <P : Platform<P>> handleExceptionBuildingState(
        world: ProviderWorld<P>,
        storage: NbtCompound,
        type: PlatformBlockType<P>,
        properties: Map<String, PlatformBlockPropertyValue<P>>
    ): PlatformBlockState<P> {
        TODO("Not yet implemented")
    }

    fun <P : Platform<P>> handleReadChunkSectionFailure(
        e: Exception?, chunkSection: LevelDBFailedChunkSection<P>
    ): LevelDBChunkSection<P> {
        //TODO
        return chunkSection
    }
}
