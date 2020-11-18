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

package org.powernukkit.converters.storage.leveldb.gomint

import io.gomint.leveldb.DB
import io.netty.buffer.Unpooled
import org.powernukkit.converters.storage.leveldb.facade.CloseableIterator
import org.powernukkit.converters.storage.leveldb.facade.LevelDBSnapshot
import org.powernukkit.converters.storage.leveldb.facade.use

/**
 * @author joserobjr
 * @since 2020-11-18
 */
internal class GoMintSnapshot(
    private val db: DB,
) : LevelDBSnapshot {
    private val snapshot = db.snapshot

    override fun close() {
        snapshot.close()
    }

    override fun get(key: ByteArray): ByteArray? {
        return Unpooled.wrappedBuffer(key).use {
            db[snapshot, it]
        }
    }

    override fun entryIterator(): CloseableIterator<Map.Entry<ByteArray, ByteArray>> {
        return GoMintEntryIterator(db.iterator(snapshot))
    }

    override fun keyIterator(): CloseableIterator<ByteArray> {
        return GoMintKeyIterator(db.iterator(snapshot))
    }

}
