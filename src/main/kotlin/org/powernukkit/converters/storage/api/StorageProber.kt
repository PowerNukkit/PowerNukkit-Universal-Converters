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

package org.powernukkit.converters.storage.api

import br.com.gamemods.nbtmanipulator.NbtCompound
import br.com.gamemods.regionmanipulator.RegionIO
import com.github.michaelbull.logging.InlineLogger
import io.gomint.leveldb.DB
import kotlinx.coroutines.*
import org.intellij.lang.annotations.Language
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.storage.alpha.AlphaStorageEngine
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.coroutines.CoroutineContext

/**
 * @author joserobjr
 * @since 2020-11-15
 */
class StorageProber(
    val levelDatFile: File,
    val originalLevelData: LevelData,
    parent: Job
) : CoroutineScope {
    private val job = Job(parent)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    val storageEngines = detectStorageEngineAsync()
    val dialects = detectDialectsAsync()
    val edition = async {
        dialects.await().map { dialect ->
            when (dialect) {
                Dialect.VANILLA_BEDROCK_EDITION -> MinecraftEdition.BEDROCK
                Dialect.VANILLA_JAVA_EDITION -> MinecraftEdition.JAVA
                Dialect.MODDED_JAVA_EDITION -> MinecraftEdition.JAVA
                Dialect.NUKKIT -> MinecraftEdition.BEDROCK
                Dialect.POWER_NUKKIT -> MinecraftEdition.BEDROCK
                Dialect.CLOUDBURST_SERVER -> MinecraftEdition.BEDROCK
                Dialect.POCKET_MINE -> MinecraftEdition.BEDROCK
            }
        }.toSet().also {
            job.complete()
        }
    }

    fun cancel() {
        job.cancel()
    }

    private fun detectDialectsAsync() = async {
        storageEngines.await().flatMap { engine ->
            when (engine) {
                StorageEngineType.ALPHA -> listOf(Dialect.VANILLA_JAVA_EDITION)
                StorageEngineType.REGIONS -> detectRegionsDialect()
                StorageEngineType.ANVIL -> detectAnvilDialect()
                StorageEngineType.POCKET_MINE -> listOf(Dialect.POCKET_MINE)
                StorageEngineType.LEVELDB -> detectLevelDBDialect()
            }
        }.toSet()
    }

    private fun detectLevelDBDialect(): List<Dialect> {
        return listOf(Dialect.VANILLA_BEDROCK_EDITION)
    }

    private fun detectRegionsDialect(): List<Dialect> {
        originalLevelData.versionData?.minecraftEdition?.let { edition ->
            if (edition == MinecraftEdition.JAVA) {
                return listOf(Dialect.VANILLA_JAVA_EDITION)
            } else if (edition == MinecraftEdition.BEDROCK) {
                return listOf(Dialect.NUKKIT)
            }
        }

        return listOf(Dialect.VANILLA_JAVA_EDITION)
    }

    private fun detectAnvilDialect(): List<Dialect> {
        originalLevelData.serverBrands?.let { brands ->
            if (brands == listOf("vanilla")) {
                return listOf(Dialect.MODDED_JAVA_EDITION)
            }
        }

        originalLevelData.dataFile?.let { dataFile ->
            if ((dataFile.tag as? NbtCompound)?.getNullableCompound("fml") != null) {
                return listOf(Dialect.MODDED_JAVA_EDITION)
            }
        }

        originalLevelData.versionData?.minecraftEdition?.let { edition ->
            if (edition == MinecraftEdition.JAVA) {
                return listOf(Dialect.VANILLA_JAVA_EDITION)
            } else if (edition == MinecraftEdition.BEDROCK) {
                return listOf(Dialect.POWER_NUKKIT)
            }
        }

        return listOf(Dialect.VANILLA_JAVA_EDITION)
    }

    private fun attemptToOpenAnvilLikeRegion(regionFile: Path): Boolean {
        return try {
            RegionIO.readRegion(regionFile.toFile())
            true
        } catch (e: IOException) {
            log.debug(e) { "Invalid anvil region file: $regionFile" }
            false
        } catch (e: InvalidPathException) {
            log.debug(e) { "Invalid anvil region file path: $regionFile" }
            false
        }
    }

    private fun attemptToOpenLevelDBDir(dbFolder: Path): Boolean {
        return try {
            DB(dbFolder.toFile()).use { db ->
                db.open()
            }
            true
        } catch (e: Exception) {
            log.debug(e) { "Could not open LevelDB at $dbFolder" }
            false
        }
    }

    private fun detectStorageEngineAsync() = async result@{
        val regions = async { getFolder("regions") }
        val db = async { getFolder("db") }
        val possibilities = mapOf(
            StorageEngineType.ALPHA to async { isAlphaStorage() },
            StorageEngineType.REGIONS to async { isRegionsStorage(regions.await() ?: return@async false) },
            StorageEngineType.ANVIL to async { isAnvilStorage(regions.await() ?: return@async false) },
            StorageEngineType.POCKET_MINE to async { isPocketMineStorage(regions.await() ?: return@async false) },
            StorageEngineType.LEVELDB to async { isLevelDBStorage(db.await() ?: return@async false) },
        )

        possibilities.entries
            .filter { (_, v) -> v.await() }
            .map { (storageEngine) -> storageEngine }
            .toSet()
    }

    private fun isPocketMineStorage(regionsFolder: Path): Boolean {
        return listFiles(regionsFolder).use { stream ->
            stream.filter { it.fileName.toString().matches(POCKET_MINE_PATTERN) }
                .anyMatch(this::attemptToOpenAnvilLikeRegion)
        }
    }

    private fun isAnvilStorage(regionsFolder: Path): Boolean {
        return listFiles(regionsFolder).use { stream ->
            stream.filter { it.fileName.toString().matches(ANVIL_PATTERN) }
                .anyMatch(this::attemptToOpenAnvilLikeRegion)
        }
    }

    private fun isRegionsStorage(regionsFolder: Path): Boolean {
        return listFiles(regionsFolder).use { stream ->
            stream.filter { it.fileName.toString().matches(REGIONS_PATTERN) }
                .anyMatch(this::attemptToOpenAnvilLikeRegion)
        }
    }


    @Suppress("SimplifyBooleanWithConstants")
    private fun isLevelDBStorage(dbFolder: Path): Boolean {
        if (!Files.isRegularFile(dbFolder.resolve("CURRENT"))) {
            return false
        }

        val valid = runBlocking(coroutineContext) {
            val requirements = listOf(
                async {
                    listFiles(dbFolder).use { stream ->
                        stream.anyMatch { it.fileName.toString().matches(LEVELDB_PATTERN) }
                    }
                },
                async {
                    listFiles(dbFolder).use { stream ->
                        stream.anyMatch { it.fileName.toString().matches(LEVELDB_LOG_PATTERN) }
                    }
                },
                async {
                    listFiles(dbFolder).use { stream ->
                        stream.anyMatch { it.fileName.toString().matches(LEVELDB_MANIFEST_PATTERN) }
                    }
                }
            )
            requirements.awaitAll()
                .all { it == true }
        }

        if (!valid) {
            return false
        }

        return attemptToOpenLevelDBDir(dbFolder)
    }

    private fun isAlphaStorage(): Boolean {
        val folder = try {
            levelDatFile.toPath().parent.toRealPath()
        } catch (e: IOException) {
            log.debug(e) { "Could not find the real path of the folder that holds the level.dat file: $levelDatFile" }
            return false
        } catch (e: InvalidPathException) {
            log.debug(e) { "The folder that contains the level.dat file have an invalid path: $levelDatFile" }
            return false
        }

        return AlphaStorageEngine.isAlphaStorage(folder)
    }

    private fun listFiles(folder: Path): Stream<Path> =
        Files.list(folder).filter(Files::isRegularFile)

    private fun getFolder(name: String): Path? {
        val regions = try {
            levelDatFile.toPath().resolveSibling(name).toRealPath()
        } catch (e: IOException) {
            log.debug(e) { "Could not find the real path of the sibling $name to the file $levelDatFile" }
            return null
        } catch (e: InvalidPathException) {
            log.debug(e) { "Could not find the real path of the sibling $name to the file $levelDatFile" }
            return null
        }

        return regions.takeIf(Files::isDirectory)
    }

    companion object {
        private val log = InlineLogger()

        @Language("RegExp")
        const val NUM = """[0-9]|-?[1-9][0-9]*"""

        @Language("RegExp")
        private const val ANVIL_NAMING_PATTERN = """^r\.($NUM)\.($NUM)"""

        private val ANVIL_PATTERN = Regex("""$ANVIL_NAMING_PATTERN\.mca$""")
        private val REGIONS_PATTERN = Regex("""$ANVIL_NAMING_PATTERN\.mcr$""")
        private val POCKET_MINE_PATTERN = Regex("""$ANVIL_NAMING_PATTERN\.mcapm$""")

        private val LEVELDB_PATTERN = Regex("""^[0-9]{6}\.ldb$""")
        private val LEVELDB_MANIFEST_PATTERN = Regex("""^MANIFEST-[0-9]{6}\.ldb$""")
        private val LEVELDB_LOG_PATTERN = Regex("""^[0-9]{6}\.log$""")
    }
}
