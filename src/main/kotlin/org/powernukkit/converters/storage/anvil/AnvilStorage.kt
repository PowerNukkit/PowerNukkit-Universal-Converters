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

package org.powernukkit.converters.storage.anvil

import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.*
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-10-19
 */
open class AnvilStorage<P : Platform<P>>(
    final override val platform: P,
    val folder: File
) : CoroutineScope, PlatformObject<P> {
    private val log = InlineLogger()
    open val storageName = "Anvil"
    private val job = Job()
    override val coroutineContext: CoroutineContext by lazy {
        job + Dispatchers.IO + CoroutineName("$storageName Storage: $folder")
    }

    private var isLoaded = AtomicBoolean()

    protected lateinit var loadedData: LevelData

    protected fun load(): Job {
        check(isLoaded.compareAndSet(false, true)) {
            "This storage loading has already started or has already completed"
        }
        return launch {
            with(LevelDataIO) {
                readLevelData(folder.resolve("level.dat")).await()
            }
        }
    }

    open fun close() {
        job.cancel()
    }
}
