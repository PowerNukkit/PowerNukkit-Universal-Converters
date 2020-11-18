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

import org.powernukkit.converters.storage.leveldb.facade.CloseableIterator
import org.powernukkit.converters.storage.leveldb.facade.LevelDBReadContainer
import java.util.*

/**
 * @author joserobjr
 * @since 2020-11-18
 */
operator fun LevelDBReadContainer.get(key: LevelDBKey) = get(key.toByteArray())

fun LevelDBReadContainer.parsedKeyIterator(): CloseableIterator<LevelDBKey> {
    return object : CloseableIterator<LevelDBKey> {
        val root = keyIterator()
        override fun hasNext(): Boolean {
            return root.hasNext()
        }

        override fun next(): LevelDBKey {
            return LevelDBKey.createByArray(root.next())
        }

        override fun close() {
            root.close()
        }
    }
}

fun LevelDBReadContainer.parsedEntryIterator(): CloseableIterator<Map.Entry<LevelDBKey, ByteArray>> {
    return object : CloseableIterator<Map.Entry<LevelDBKey, ByteArray>> {
        val root = entryIterator()
        override fun hasNext(): Boolean {
            return root.hasNext()
        }

        override fun next(): Map.Entry<LevelDBKey, ByteArray> {
            val entry = root.next()
            return AbstractMap.SimpleEntry(LevelDBKey.createByArray(entry.key), entry.value)
        }

        override fun close() {
            root.close()
        }
    }
}

inline fun <R> LevelDBReadContainer.parsedKeyIterator(action: CloseableIterator<LevelDBKey>.() -> R): R {
    return parsedKeyIterator().use(action)
}

inline fun <R> LevelDBReadContainer.parsedEntryIterator(action: CloseableIterator<Map.Entry<LevelDBKey, ByteArray>>.() -> R): R {
    return parsedEntryIterator().use(action)
}
