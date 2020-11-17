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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.leveldata.model.LevelData

/**
 * @author joserobjr
 * @since 2020-10-23
 */
abstract class ProviderWorld<P : Platform<P>>(protected val problemManager: StorageProblemManager) {
    abstract val platform: P
    abstract val levelData: LevelData
    abstract val storageEngine: StorageEngine

    open fun countChunks() = chunkFlow().map { 1 }
    open fun countBlocks() = chunkFlow().map { it.countNonAirBlocks() }
    open fun countChunkSections() = chunkFlow().map { it.entityCount }
    open fun countEntities() = chunkFlow().map { it.entityCount }
    open fun countBlockEntities() = chunkFlow().map { it.blockEntityCount }

    abstract fun chunkFlow(): Flow<Chunk<P>>
}
