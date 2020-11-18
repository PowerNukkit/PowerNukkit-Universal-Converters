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

import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.powernukkit.converters.conversion.job.InputWorld
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.universal.definitions.DefinitionLoader
import org.powernukkit.converters.storage.api.Dialect
import org.powernukkit.converters.storage.api.StorageEngineType
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import java.io.File

/**
 * @author joserobjr
 * @since 2020-11-18
 */
internal class LevelDBStorageEngineTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun loadWorld() {
        val dbDir = File("sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0")
        runBlocking {
            InputWorld(
                dbDir,
                LevelDataIO.readLevelDataBlocking(dbDir.resolve("level.dat")),
                StorageEngineType.LEVELDB,
                Dialect.VANILLA_BEDROCK_EDITION,
                MinecraftEdition.BEDROCK,
                DefinitionLoader().loadBuiltin(),
                StorageProblemManager()
            ).load().use { provider ->
                println("Loaded")
                var current = 0
                val total = provider.countChunks()
                    .onEach {
                        current += it
                        println("Fount: $it, Have: $current")
                    }
                    .fold(0, { acc, v -> acc + v })
                println("Total: $total")
            }
        }
    }
}
