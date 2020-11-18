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

package org.powernukkit.converters.storage.leveldb.facade

import org.powernukkit.converters.storage.leveldb.iq80.IQ80Factory
import java.io.Closeable
import java.io.File

/**
 * @author joserobjr
 * @since 2020-11-18
 */
interface LevelDB : Closeable, LevelDBReadContainer, LevelDBWriteContainer {
    fun createSnapshot(): LevelDBSnapshot
    fun createWriteBatch(): LevelDBWriteBatch

    companion object {
        var defaultFactory: LevelDBFactory = IQ80Factory

        operator fun invoke(dbFolder: File, settings: LevelDBSettings = LevelDBSettings()) =
            defaultFactory.open(dbFolder, settings)
    }
}
