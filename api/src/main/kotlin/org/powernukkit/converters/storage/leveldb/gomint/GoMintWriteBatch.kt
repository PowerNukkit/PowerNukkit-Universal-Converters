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

package org.powernukkit.converters.storage.leveldb.gomint

import io.gomint.leveldb.WriteBatch
import io.netty.buffer.Unpooled
import org.powernukkit.converters.storage.leveldb.facade.LevelDBWriteBatch
import org.powernukkit.converters.storage.leveldb.facade.use

/**
 * @author joserobjr
 * @since 2020-11-18
 */
class GoMintWriteBatch : LevelDBWriteBatch {
    private val batch = WriteBatch()
    override fun close() {
        batch.clear()
    }

    override fun set(key: ByteArray, value: ByteArray) {
        Unpooled.wrappedBuffer(key).use { k ->
            Unpooled.wrappedBuffer(key).use { v ->
                batch.put(k, v)
            }
        }
    }

    override fun delete(key: ByteArray) {
        Unpooled.wrappedBuffer(key).use { k ->
            batch.delete(k)
        }
    }

    override fun clear() {
        batch.clear()
    }
}
