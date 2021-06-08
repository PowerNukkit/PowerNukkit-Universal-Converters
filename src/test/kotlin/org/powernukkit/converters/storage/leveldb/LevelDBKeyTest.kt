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

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.powernukkit.converters.storage.leveldb.iq80.IQ80LevelDB
import java.io.File

/**
 * @author joserobjr
 * @since 2020-11-17
 */
internal class LevelDBKeyTest {
    @Test
    @Disabled
    fun testing() {
        val stringPattern = Regex("^\\w+$")
        val dbDir = File("sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0/db")
        IQ80LevelDB(dbDir).use { db ->
            /*db.keyIterator().use { iter -> 
                iter.forEach { bytes ->
                    var str = String(bytes)
                    if (!str.matches(stringPattern)) {
                        str = "0x" + bytes.joinToString("") { "%02X".format(it) }
                    }
                    
                    println("${bytes.size}\t$str")
                }
            }*/
            /*db.parsedKeyIterator().use { iter ->
                iter.forEach { key ->
                    if (key !is ChunkKey) {
                        println("${key.bufferSize} - $key")
                    }
                }
            }*/
            db.parsedEntryIterator {
                asSequence().filter { it.key is ChunkKey }.forEach { (key, value) ->
                    var v = key.loadValue(value)
                    if (v is ByteArray) {
                        v = "0x" + v.joinToString("") { "%02X".format(it) }
                    }
                    println("$key - $v")
                }
            }
        }
    }
}
