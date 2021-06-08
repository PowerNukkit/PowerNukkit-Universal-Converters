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

package org.powernukkit.converters.storage.leveldb

import br.com.gamemods.regionmanipulator.ChunkPos
import org.powernukkit.converters.platform.api.Platform

/**
 * @author joserobjr
 * @since 2021-06-08
 */
class LevelDBEmptyChunkSection<P : Platform<P>>(
    override val world: LevelDBProviderWorld<P>,
    override val chunkPos: ChunkPos,
    override val sectionNumber: Int
) : LevelDBChunkSection<P> {
    override val version: Byte get() = 8
}
