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

import br.com.gamemods.regionmanipulator.ChunkPos
import com.github.michaelbull.logging.InlineLogger
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.powernukkit.converters.TestConstants
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * @author joserobjr
 * @since 2021-06-06
 */
@ExtendWith(MockKExtension::class)
internal class LevelDBChunkTest {
    private val log = InlineLogger()

    @MockK
    lateinit var storageProblemManager: StorageProblemManager
    lateinit var bedrockPlatform: BedrockPlatform
    lateinit var provider: LevelDBProviderWorld<BedrockPlatform>
    lateinit var chunk: LevelDBChunk<BedrockPlatform>

    @BeforeEach
    fun setUp() {
        bedrockPlatform = BedrockPlatform(TestConstants.universalPlatform)
        val testWorld = Paths.get("sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0")
        provider = LevelDBProviderWorld(
            bedrockPlatform,
            testWorld,
            LevelDataIO.readLevelDataBlocking(testWorld.resolve("level.dat").toFile()),
            storageProblemManager,
            LevelDBStorageEngine()
        )
        every { storageProblemManager.handleReadChunkSectionFailure(any(), any<LevelDBFailedChunkSection<*>>()) }
            .returnsArgument(1)
        every {
            storageProblemManager.handleMissingBlockPropertyParsingState(
                world = any<ProviderWorld<BedrockPlatform>>(),
                storage = any(),
                type = any(),
                key = any(),
                valueTag = any()
            )
        }.answers {
            val type = arg<PlatformBlockType<BedrockPlatform>>(2)
            log.warn { "Property ${args[3]}=${args[4]} not found for type ${type.id}" }
            null
        }
        chunk = runBlocking { provider.chunkFlow().first() }
    }

    @AfterEach
    fun tearDown() {
        provider.close()
    }

    @Test
    fun getEntityCount() {
        assertEquals(0, chunk.entityCount)
    }

    @Test
    fun getChunkSectionCount() {
        assertEquals(0, chunk.chunkSectionCount)
    }

    @Test
    fun getBlockEntityCount() {
        assertEquals(0, chunk.blockEntityCount)
    }

    @Test
    fun countNonAirBlocks() {
    }

    @Test
    fun structureFlow() {
    }

    @Test
    fun get() {
    }

    @Test
    fun getChunkPos() {
        assertEquals(ChunkPos(0, 0), chunk.chunkPos)
    }

    @Test
    fun getVersion() {
        assertEquals(0, chunk.version)
    }

    @Test
    fun getChecksum() {
        assertNotNull(chunk.checksum)
    }

    @Test
    fun getFinalized() {
        assertEquals(0, chunk.finalized)
    }

    @Test
    fun getBlockEntities() {
        assertNotNull(chunk.blockEntities)
    }

    @Test
    fun getEntities() {
        assertNotNull(chunk.entities)
    }

    @Test
    fun getBiomes() {
        assertNotNull(chunk.biomes)
    }

    @Test
    fun getPendingTicks() {
        assertNotNull(chunk.pendingTicks)
    }

    @Test
    fun getRandomTicks() {
        assertNotNull(chunk.randomTicks)
    }

    @Test
    fun getBiomeState() {
        assertNotNull(chunk.biomeState)
    }

    @Test
    fun getBorderBlocks() {
        assertNotNull(chunk.borderBlocks)
    }

    @Test
    fun getHardcodedSpawns() {
        assertNotNull(chunk.hardcodedSpawns)
    }

    @Test
    fun getSections() {
        assertNotNull(chunk.sections)
    }
}
