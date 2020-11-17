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

package org.powernukkit.converters.storage.alpha

import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.regionmanipulator.ChunkPos
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.Deferred
import org.intellij.lang.annotations.Language
import org.powernukkit.converters.conversion.job.InputWorld
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.ReceivingWorld
import org.powernukkit.converters.storage.api.StorageEngine
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.util.stream.Stream

/**
 * @author joserobjr
 * @since 2020-10-23
 */
class AlphaStorageEngine : StorageEngine() {

    override suspend fun loadWorld(inputWorld: InputWorld) = AlphaProviderWorld<Platform<*>>(this, inputWorld)

    override suspend fun prepareToReceive(
        toFile: File,
        fromWorld: Deferred<ProviderWorld<*>>,
        universalPlatformLoader: Deferred<UniversalPlatform>
    ): ReceivingWorld<*> {
        TODO("Not yet implemented")
    }

    companion object {
        private val log = InlineLogger()

        @Language("RegExp")
        const val NUM_B36 = """^[0-9a-z]|-?[1-9a-z][0-9a-z]*"""

        private val ALPHA_FILE_PATTERN = Regex("""^c\.($NUM_B36)\.($NUM_B36)\.dat$""")
        private val ALPHA_FOLDER_PATTERN = Regex("""^[0-9a-z]|[1-9a-z][0-9a-z]*""")

        fun parseChunkPos(fileName: String): AlphaChunkPos {
            val match = requireNotNull(ALPHA_FILE_PATTERN.matchEntire(fileName)?.groupValues) {
                "Invalid file name: $fileName"
            }

            return AlphaChunkPos(match[1], match[2])
        }

        fun Stream<Path>.filterValidFolder(): Stream<Path> =
            filter(Files::isDirectory).filter { isFolderNameValid(it.fileName.toString()) }

        fun Stream<Path>.filterValidPath(worldDir: Path): Stream<Path> =
            filter(Files::isRegularFile)
                .filter { isFileNameValid(it.fileName.toString()) }
                .filter { isChunkFilePathValid(worldDir, it) }

        fun isFolderNameValid(name: String) = name.matches(ALPHA_FOLDER_PATTERN) && name.toIntOrNull(36) != null
        fun isFileNameValid(name: String) = name.matches(ALPHA_FILE_PATTERN)
        fun isChunkFilePathValid(rootPath: Path, chunkPath: Path): Boolean {
            val chunkFileName = chunkPath.fileName.toString()
            val zFolderFileName = chunkPath.parent.fileName.toString()
            val xFolderFileName = chunkPath.parent.parent.fileName.toString()

            if (!isFileNameValid(chunkFileName)
                || rootPath != chunkPath.parent.parent.parent
                || !isFolderNameValid(xFolderFileName)
                || !isFolderNameValid(zFolderFileName)
            ) {
                return false
            }

            val match = ALPHA_FILE_PATTERN.matchEntire(chunkFileName)?.groupValues ?: return false

            val chunkPos = ChunkPos(
                match[1].toInt(36),
                match[2].toInt(36)
            )

            val expectedFolderX = (chunkPos.xPos % 64).toString(36)
            val expectedFolderZ = (chunkPos.zPos % 64).toString(36)

            return expectedFolderX == xFolderFileName && expectedFolderZ == zFolderFileName
        }

        fun isAlphaChunkFileValid(nbtFilePath: Path): Boolean {
            return try {
                NbtIO.readNbtFile(nbtFilePath.toFile())
                true
            } catch (e: IOException) {
                log.debug(e) { "Invalid alpha chunk file: $nbtFilePath" }
                false
            } catch (e: InvalidPathException) {
                log.debug(e) { "Invalid alpha chunk file path: $nbtFilePath" }
                false
            }
        }

        fun isAlphaStorage(folder: Path): Boolean {
            return Files.list(folder).use { levelDir ->
                levelDir.filter(Files::isDirectory)
                    .filter { isFolderNameValid(it.fileName.toString()) }
                    .anyMatch { xDir ->
                        Files.list(xDir).use { xDirStream ->
                            xDirStream.filter(Files::isDirectory)
                                .filter { isFolderNameValid(it.fileName.toString()) }
                                .anyMatch { zDir ->
                                    Files.list(zDir).use { zDirStream ->
                                        zDirStream.filter(Files::isRegularFile)
                                            .filter { isFileNameValid(it.fileName.toString()) }
                                            .filter { isChunkFilePathValid(folder, it) }
                                            .anyMatch(this::isAlphaChunkFileValid)
                                    }
                                }
                        }
                    }
            }
        }
    }


}
