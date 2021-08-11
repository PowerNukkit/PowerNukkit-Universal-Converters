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

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.powernukkit.converters.TestConstants
import org.powernukkit.converters.dialect.Dialect
import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * @author joserobjr
 * @since 2021-06-06
 */
@ExtendWith(MockKExtension::class)
internal class LevelDBProviderWorldTest {

    @MockK
    lateinit var storageProblemManager: StorageProblemManager
    lateinit var bedrockPlatform: BedrockPlatform
    lateinit var provider: LevelDBProviderWorld<BedrockPlatform>

    @BeforeEach
    fun setUp() {
        bedrockPlatform = BedrockPlatform(TestConstants.universalPlatform)
        val testWorld = Paths.get("sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0")
        provider = LevelDBProviderWorld(
            bedrockPlatform,
            testWorld,
            LevelDataIO.readLevelDataBlocking(testWorld.resolve("level.dat").toFile()),
            storageProblemManager,
            LevelDBStorageEngine(),
            Dialect.VANILLA_BEDROCK_EDITION
        )
    }

    @AfterEach
    fun tearDown() {
        provider.close()
    }

    @Test
    internal fun testCountChunks() {
        val final = runBlocking {
            provider.countChunks()
                .onEach {
                    assertEquals(485, it)
                }.reduce { a, b -> a + b }
        }
        assertEquals(485, final)
    }

    @Test
    internal fun testCountChunkSections() {
        val final = runBlocking {
            provider.countChunkSections()
                .onEach {
                    assertEquals(16, it)
                }.reduce { a, b -> a + b }
        }
        assertEquals(485 * 16, final)
    }

    @Test
    @Disabled
    internal fun testCountBlocks() {
        val final = runBlocking {
            provider.countBlocks()
                .onEach {
                    assertTrue("$it is not in range") { it in 0..16 * 16 * 16 }
                }.reduce { a, b -> a + b }
        }
        assertEquals(485 * 16, final)
    }

    @Test
    internal fun testCountBlockEntities() {
        val final = runBlocking {
            provider.countBlockEntities()
                .onEach {
                    assertTrue("$it is not in range") { it in 0..16 * 16 * 16 }
                }.reduce { a, b -> a + b }
        }
        assertEquals(27, final)
    }
}
