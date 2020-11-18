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

import org.powernukkit.converters.storage.leveldb.facade.CloseableIterator
import kotlin.contracts.ExperimentalContracts
import io.gomint.leveldb.Iterator as NativeIterator

/**
 * @author joserobjr
 * @since 2020-11-18
 */
internal abstract class GoMintIterator<out T>(protected val iterator: NativeIterator) : CloseableIterator<T> {
    init {
        iterator.seekToFirst()
    }

    private var hasNext = iterator.isValid

    override fun hasNext(): Boolean {
        return hasNext
    }

    protected abstract fun readCurrent(): T

    @ExperimentalContracts
    override fun next(): T {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        val result = readCurrent()
        iterator.next()
        if (!iterator.isValid) {
            hasNext = false
            close()
        }
        return result
    }

    override fun close() {
        iterator.close()
    }
}
