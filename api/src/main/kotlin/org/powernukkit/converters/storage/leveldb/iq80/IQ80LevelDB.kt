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

package org.powernukkit.converters.storage.leveldb.iq80

import org.iq80.leveldb.Options
import org.iq80.leveldb.impl.Iq80DBFactory.factory
import org.powernukkit.converters.storage.leveldb.facade.*
import java.io.File
import java.nio.file.Path

/**
 * @author joserobjr
 * @since 2020-11-18
 */
class IQ80LevelDB(dbDir: File, settings: LevelDBSettings = LevelDBSettings()) : LevelDB {
    private val db = factory.open(dbDir, Options())

    override val folder: Path = dbDir.toPath()

    override fun createSnapshot(): LevelDBSnapshot = IQ80Snapshot(db, folder)
    override fun createWriteBatch(): LevelDBWriteBatch = IQ80WriteBatch(db.createWriteBatch())

    override fun get(key: ByteArray) = db[key]
    override fun set(key: ByteArray, value: ByteArray) {
        db.put(key, value)
    }

    override fun delete(key: ByteArray) {
        db.delete(key)
    }

    override fun keyIterator(): CloseableIterator<ByteArray> {
        return IQ80KeyIterator(db.iterator())
    }

    override fun entryIterator(): CloseableIterator<Map.Entry<ByteArray, ByteArray>> {
        return IQ80EntryIterator(db.iterator())
    }

    override fun close() {
        db.close()
    }
}
