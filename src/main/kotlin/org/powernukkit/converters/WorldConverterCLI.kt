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

import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.definitions.DefinitionLoader

/**
 * Executes the world conversion from the system's command line.
 *
 * @author joserobjr
 * @since 2020-10-09
 */
object WorldConverterCLI {
    /**
     * The entry point of the command line interface.
     * @param args The arguments that was given in the command line.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val universalPlatform = DefinitionLoader().loadBuiltin()
        val javaPlatform = JavaPlatform(universalPlatform, "Java")
        val bedrockPlatform = BedrockPlatform(universalPlatform, "Bedrock")
        println(bedrockPlatform.blockEntityTypesById["Chest"])
        println(javaPlatform.blockEntityTypesById["chest"])
    }
}
