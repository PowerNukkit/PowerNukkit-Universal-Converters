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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.selectUnbiased
import kotlinx.coroutines.swing.Swing
import org.powernukkit.converters.conversion.converter.ConversionProblem
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.ImmutableStructure
import org.powernukkit.converters.platform.api.block.PositionedStructure
import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.definitions.DefinitionLoader
import org.powernukkit.converters.ui.WorldConverterGUI
import java.util.*
import javax.swing.UIManager

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
            runBlocking(Dispatchers.Swing) {
                try {
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                    )
                } catch (e: Exception) {
                    log.warn(e) { "Could not change the UI look and feel" }
                }
                WorldConverterGUI(Locale.getDefault())
            }
            return
        }

        // TODO CLI mode
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
            val block = javaPlatform.createPlatformBlock(state)
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
