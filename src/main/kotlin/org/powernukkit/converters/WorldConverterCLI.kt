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

package org.powernukkit.converters

import com.github.michaelbull.logging.InlineLogger
import kotlinx.cli.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.selectUnbiased
import org.powernukkit.converters.conversion.converter.ConversionProblem
import org.powernukkit.converters.conversion.job.ConversionProcess
import org.powernukkit.converters.conversion.job.PlatformProvider
import org.powernukkit.converters.conversion.job.load
import org.powernukkit.converters.dialect.Dialect
import org.powernukkit.converters.gui.WorldConverterGUI
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.ImmutableStructure
import org.powernukkit.converters.platform.api.block.PositionedStructure
import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.definitions.DefinitionLoader
import org.powernukkit.converters.storage.StorageEngineDetector
import org.powernukkit.converters.storage.api.StorageEngineType
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.LevelDataIO
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.version.Version
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.swing.UIManager
import kotlin.io.path.absolute
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

/**
 * Executes the world conversion from the system's command line.
 *
 * @author joserobjr
 * @since 2020-10-09
 */
object WorldConverterCLI {
    private val log = InlineLogger()

    /**
     * The entry point of the command line interface.
     * @param args The arguments that was given in the command line.
     */
    @ExperimentalCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            gui()
        } else {
            runBlocking {
                cli(args)
            }
        }
    }

    private fun gui() {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            )
        } catch (e: Exception) {
            log.warn(e) { "Could not change the UI look and feel" }
        }
        WorldConverterGUI(Locale.getDefault())
    }

    private suspend fun cli(args: Array<String>) {
        val source = File(WorldConverterCLI::class.java.protectionDomain.codeSource.location.toURI())
        val name = if (source.isFile) "-jar " + source.name else WorldConverterCLI::class.java.name
        val parser = ArgParser("java $name")

        val input by parser.argument(ArgType.String, description = "Input world folder or level.dat file")

        val multipleInputs by parser.option(ArgType.Boolean, shortName = "im",
            description = "Makes the input argument be considered as a root folder of multiple worlds and convert all worlds that are in the folder to the output"
        ).default(false)

        val output by parser.argument(ArgType.String, description = "The folder where the target world file/folder will be created")
        val outputFormat by parser.option(ArgType.Choice<Dialect>(), shortName = "of", description = "Target world format").multiple().required()
        val outputVersion by parser.option(ArgType.String, shortName = "ov", description = "Target Minecraft Version").multiple().required()
        val outputStorageEngine by parser.option(ArgType.Choice<StorageEngineType>(), shortName = "os", description = "Target storage engine, automatically defined when omitted")

        val blocksDefinitions by parser.option(ArgType.String, shortName = "db", description = "A custom universal-blocks XML to be used on this process")

        parser.parse(args)

        coroutineScope {
            val platformProvider = PlatformProvider(loadUniversalPlatformAsync(blocksDefinitions))
            val inputLevelDataListDeferred = loadInputLevelDataListAsync(
                input = getPath(input, "input"),
                multiple = multipleInputs
            )

            val outputFolder = getPath(output, "output")
            val folderCreation = launch(Dispatchers.IO) { Files.createDirectories(outputFolder) }
            val inputLevelDataList = inputLevelDataListDeferred.await()

            if (inputLevelDataList.isEmpty()) {
                val e = CancellationException("Could not find any valid world in $input")
                log.error(e) { "Aborting, no worlds were found." }
                throw e
            }

            folderCreation.join()

            val jobs: List<ConversionProcess<*, *>> = inputLevelDataList.asSequence().flatMap { levelData ->
                val from = async(Dispatchers.IO) { StorageEngineDetector.detectStorageEngine(levelData) }
                sequence {
                    outputFormat.forEach { format ->
                        outputVersion.forEach { version ->
                            yield(async {
                                val fromLevelData = from.await()
                                val problemManager = StorageProblemManager()

                                @Suppress("RemoveExplicitTypeArguments") // False positive warning
                                conversionJob<Nothing, Nothing>(
                                    from = fromLevelData,
                                    toFolder = outputFolder.resolve("${fromLevelData.folder}-$format-$version"),
                                    toDialect = format,
                                    toVersion = Version(version),
                                    using = platformProvider,
                                    forcedStorage = outputStorageEngine,
                                    problemManager = problemManager,
                                )
                            })
                        }
                    }
                }
            }.toList().awaitAll()
        }
    }

    private fun getPath(path: String, type: String): Path {
        return try {
            Paths.get(path).absolute()
        } catch (e: Exception) {
            log.error(e) { "The $type path is not valid: $path" }
            throw e
        }
    }

    private suspend fun <FromPlatform: Platform<FromPlatform>, ToPlatform: Platform<ToPlatform>> conversionJob(
        from: LevelData,
        toFolder: Path,
        toDialect: Dialect,
        toVersion: Version,
        using: PlatformProvider,
        problemManager: StorageProblemManager,
        forcedStorage: StorageEngineType? = null
    ): ConversionProcess<FromPlatform, ToPlatform> = coroutineScope {
        val fromDialect = from.dialect ?: error("The input dialect is undefined")
        val input = async {
            fromDialect.createInputWorld<FromPlatform>(from, using, problemManager)
        }

        val output = async {
            toDialect.createOutputWorld<ToPlatform>(toFolder, toVersion, forcedStorage, using, problemManager)
        }

        val providerWorld = async(Dispatchers.IO) { input.await().load(using) }

        val receivingWorld = async(Dispatchers.IO) {
            input.await().storageEngine.prepareToReceive(providerWorld, output.await(), using)
        }

        val inputWorld = input.await()
        with(fromDialect.createConversionChain(inputWorld, providerWorld, receivingWorld, problemManager)) {
            convertWorldAsync(providerWorld.await(), receivingWorld.await(), problemManager)
        }
    }

    private fun CoroutineScope.loadInputLevelDataListAsync(input: Path, multiple: Boolean): Deferred<List<LevelData>> = async(Dispatchers.IO) {
        if (multiple) {
            return@async if (!input.isDirectory()) {
                log.debug { "This is not a folder: $input" }
                emptyList()
            } else {
                Files.list(input).use { stream ->
                    stream.filter { it.isDirectory() }.map {
                        log.debug { "Scanning subfolder $it" }
                        loadInputLevelDataListAsync(it, false)
                    }.toList().awaitAll().filter { it.isNotEmpty() }.map { it.first() }
                }
            }
        }

        val levelDat = input.takeIf { it.isDirectory() }?.resolve("level.dat")?.takeIf { it.isRegularFile() }
            ?: input.resolve("level.dat").takeIf { it.isRegularFile() }
            ?: run { log.debug { "Level.dat not found in $input" }; return@async emptyList() }

        log.info { "Detecting the input format from $levelDat" }
        listOf(LevelDataIO.readLevelData(levelDat.toFile()).also {
            log.info {
                "Found input with format ${it.dialect?.toString()?.lowercase() ?: "Unknown"} " +
                        "(${it.versionData?.minecraftEdition?.name?.lowercase() ?: "Unknown Edition"}: " +
                        "${it.versionData?.lastOpenedWithVersion ?: "Unknown Version"} ${it.versionData?.worldVersion ?: "#"}) at ${it.folder}"
            }
        })
    }

    private fun CoroutineScope.loadUniversalPlatformAsync(blocksDefinitions: String?) = async(Dispatchers.IO) {
        try {
            val loader = DefinitionLoader()
            val defFile = blocksDefinitions?.let(::File)?.takeIf(File::isFile)

            log.debug { "Loading universal blocks definitions from ${defFile ?: "the builtin XML"}" }
            defFile?.inputStream()?.use {
                loader.loadXml(it)
            } ?: loader.loadBuiltin()

        } catch (e: Exception) {
            log.error(e) { "Could not load the universal blocks definitions. Custom? ${blocksDefinitions.toString()}" }
            throw e
        }
    }

    private fun toyFun(args: Array<String>) {
        val universalPlatform = DefinitionLoader().loadBuiltin()
        val javaPlatform = JavaPlatform(universalPlatform)
        val bedrockPlatform = BedrockPlatform(universalPlatform)

        val converter = javaPlatform.convertToUniversal().convertToPlatform(bedrockPlatform)

        val javaStone = javaPlatform.getBlockType(NamespacedId("stone"))!!.withDefaultState()
        val javaGrass = javaPlatform.getBlockType(NamespacedId("grass"))!!.withDefaultState()
        val javaDirt = javaPlatform.getBlockType(NamespacedId("dirt"))!!.withDefaultState()
        val javaBamboo = javaPlatform.getBlockType(NamespacedId("bamboo"))!!.withState(
            "age" to "1",
            "leaves" to "small",
            "stage" to "1"
        )

        val javaStructures = listOf(
            BlockPos(1, 2, 3) to javaPlatform.airBlockState,
            BlockPos(2, 2, 3) to javaStone,
            BlockPos(3, 3, 3) to javaGrass,
            BlockPos(4, 5, 6) to javaDirt,
            BlockPos(10, 20, 30) to javaBamboo
        ).map { (pos, state) ->
            val block = javaPlatform. createPlatformBlock(state)
            PositionedStructure(
                pos,
                ImmutableStructure(javaPlatform, mapOf(BlockPos.ZERO to block))
            )
        }

        runBlocking {
            val javaChannel = produce {
                javaStructures.forEach {
                    send(it)
                }
            }

            val bedrockChannel = Channel<PositionedStructure<BedrockPlatform>>()
            val problems = Channel<ConversionProblem>()
            val conversionJob = with(converter) {
                convertAllStructures(javaChannel, bedrockChannel, problems)
            }

            val channels = listOf(bedrockChannel, problems)
            while (channels.any { !it.isClosedForReceive }) {
                selectUnbiased<Unit> {
                    bedrockChannel.onReceive { bedrockStructure ->
                        println("Got a structure: $bedrockStructure")
                        println()
                    }

                    problems.onReceive { problem ->
                        System.err.println("Got a problem :(")
                        problem.printStackTrace()
                        System.err.println()
                    }

                    conversionJob.onJoin {
                        bedrockChannel.close()
                        problems.close()
                    }
                }
            }
        }

        println("Completed.")
    }
}
