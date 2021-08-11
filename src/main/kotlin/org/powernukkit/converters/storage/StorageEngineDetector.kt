package org.powernukkit.converters.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.powernukkit.converters.internal.filterNotNull
import org.powernukkit.converters.internal.isNotCaseSensitive
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.dialect.Dialect
import org.powernukkit.converters.storage.api.StorageEngineType
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.converters.storage.api.leveldata.model.LevelVersionData
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Function
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name

/**
 * @author joserobjr
 * @since 2021-08-09
 */
object StorageEngineDetector {
    private val ALPHA_NAMING_PATTERN = Regex("""^c\.-?\d+\.-?\d+\.dat$""", RegexOption.IGNORE_CASE)
    private val POCKET_MINE_NAMING_PATTERN = Regex("""^r\.-?\d+\.-?\d+\.mcapm""", RegexOption.IGNORE_CASE)
    private val MCA_NAMING_PATTERN = Regex("""^r\.-?\d+\.-?\d+\.mca""", RegexOption.IGNORE_CASE)
    private val MCR_NAMING_PATTERN = Regex("""^r\.-?\d+\.-?\d+\.mcr""", RegexOption.IGNORE_CASE)

    suspend fun detectStorageEngine(location: Path): LevelData? = withContext(Dispatchers.IO) io@ {
        val levelDatFile: Path = findLevelDat(location) ?: return@io null
        val originalLevelData = LevelDataIO.readLevelData(levelDatFile.toFile())
        detectStorageEngine(originalLevelData)
    }

    fun detectStorageEngine(originalLevelData: LevelData): LevelData {
        val folder = originalLevelData.folder ?: return originalLevelData
        val regionFolder = folder.resolve("region")
        if (regionFolder.isDirectory()) {
            return detectStorageEngineWithRegion(originalLevelData, regionFolder)
        }

        val dbFolder = folder.resolve("db")
        if (dbFolder.isDirectory()) {
            return detectStorageEngineWithDb(originalLevelData, dbFolder)
        }

        return detectAlphaStorage(originalLevelData, folder)
    }

    private fun detectStorageEngineWithRegion(originalLevelData: LevelData, regionFolder: Path): LevelData {
        return Files.list(regionFolder).use { stream ->
            stream.map { path ->
                when {
                    MCR_NAMING_PATTERN.matches(path.name) -> detectFromMCRegions(originalLevelData)
                    MCA_NAMING_PATTERN.matches(path.name) -> detectFromMCAnvil(originalLevelData)
                    POCKET_MINE_NAMING_PATTERN.matches(path.name) -> detectFromPocketMine(originalLevelData)
                    else -> null
                }
            }.filterNotNull().findFirst().orElse(originalLevelData)
        }
    }

    private fun detectAlphaStorage(originalLevelData: LevelData, folder: Path): LevelData {
        val match = Files.list(folder).use { xStream ->
            xStream.filter { it.isDirectory() }.map { xFolder ->
                Files.list(xFolder).use { zStream ->
                    zStream.filter { it.isDirectory() }.map { zFolder ->
                        Files.list(zFolder).use { contentStream ->
                            contentStream.filter { it.isRegularFile() }
                                .filter { ALPHA_NAMING_PATTERN.matches(it.name) }
                                .findFirst()
                        }
                    }.findFirst().flatMap(Function.identity())
                }
            }.findFirst().flatMap(Function.identity())
        }

        return if (match.isEmpty) {
            originalLevelData
        } else {
            originalLevelData.copy(
                dialect = originalLevelData.dialect ?: Dialect.VANILLA_JAVA_EDITION,
                storageEngineType = originalLevelData.storageEngineType ?: StorageEngineType.ALPHA,
                versionData = (originalLevelData.versionData ?: LevelVersionData()).copy(
                    minecraftEdition = originalLevelData.versionData?.minecraftEdition ?: MinecraftEdition.JAVA,
                )
            )
        }
    }

    private fun detectStorageEngineWithDb(originalLevelData: LevelData, dbFolder: Path): LevelData {
        return originalLevelData.copy(
            storageEngineType = originalLevelData.storageEngineType ?: StorageEngineType.LEVELDB,
            versionData = (originalLevelData.versionData ?: LevelVersionData()).copy(
                minecraftEdition = originalLevelData.versionData?.minecraftEdition ?: MinecraftEdition.BEDROCK
            )
        )
    }

    private fun detectFromPocketMine(originalLevelData: LevelData): LevelData {
        return originalLevelData.copy(
            dialect = originalLevelData.dialect ?: Dialect.POCKET_MINE,
            storageEngineType = originalLevelData.storageEngineType ?: StorageEngineType.POCKET_MINE,
            versionData = (originalLevelData.versionData ?: LevelVersionData()).copy(
                minecraftEdition = originalLevelData.versionData?.minecraftEdition ?: MinecraftEdition.BEDROCK
            )
        )
    }

    private fun detectFromMCAnvil(originalLevelData: LevelData): LevelData {
        return originalLevelData.copy(
            dialect = originalLevelData.dialect ?: Dialect.VANILLA_JAVA_EDITION,
            storageEngineType = originalLevelData.storageEngineType ?: StorageEngineType.ANVIL,
            versionData = (originalLevelData.versionData ?: LevelVersionData()).copy(
                minecraftEdition = originalLevelData.versionData?.minecraftEdition ?: MinecraftEdition.JAVA
            )
        )
    }

    private fun detectFromMCRegions(originalLevelData: LevelData): LevelData {
        return originalLevelData.copy(
            dialect = originalLevelData.dialect ?: Dialect.VANILLA_JAVA_EDITION,
            storageEngineType = originalLevelData.storageEngineType ?: StorageEngineType.REGIONS,
            versionData = (originalLevelData.versionData ?: LevelVersionData()).copy(
                minecraftEdition = originalLevelData.versionData?.minecraftEdition ?: MinecraftEdition.JAVA
            )
        )
    }

    private fun findLevelDat(path: Path): Path? {
        if (path.isRegularFile()) {
            if (path.name.equals("level.dat", ignoreCase = path.isNotCaseSensitive())) {
                return path
            }
            return null
        }

        if (!path.isDirectory()) {
            return null
        }

        return path.resolve("level.dat").takeIf { it.isRegularFile() }
    }
}
