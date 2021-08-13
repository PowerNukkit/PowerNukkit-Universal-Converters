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

package org.powernukkit.converters.storage.leveldb

import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.StorageProblemManager

/**
 * @author joserobjr
 * @since 2021-06-07
 */
class LevelDBBlockLayers<P : Platform<P>>(
    world: LevelDBProviderWorld<P>,
    problemManager: StorageProblemManager,
    version: Byte,
    data: Memory
) {
    private val layers = if (version == 1.toByte()) {
        ByteReadPacket(data.buffer).use { input ->
            arrayOf(LevelDBBlockStorage(world, problemManager, input))
        }
    } else {
        ByteReadPacket(data.buffer.slice()).use { input ->
            Array(input.readByte().toInt()) {
                LevelDBBlockStorage(world, problemManager, input)
            }
        }
    }
}
