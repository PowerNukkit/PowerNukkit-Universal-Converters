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

package org.powernukkit.converters.conversion.universal.from

import org.junit.jupiter.api.Test
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.platform.universal.definitions.DefinitionLoader
import kotlin.test.assertEquals

/**
 * @author joserobjr
 * @since 2020-10-18
 */
internal class FromUniversalConverterTest {
    @Test
    fun convertStructure() {
        val universalPlatform = DefinitionLoader().loadBuiltin()
        val bedrockPlatform = BedrockPlatform(universalPlatform)

        val converter = FromUniversalConverter(universalPlatform, bedrockPlatform)

        val universalStructure = universalPlatform.createStructure(1)
        universalStructure[BlockPos.ZERO] = universalPlatform.airBlock

        val (bedrockStructure, problems) = converter.convertStructure(universalStructure)

        problems.forEach { it.printStackTrace() }
        
        assertEquals(emptyList(), problems)
        assertEquals(1, bedrockStructure.blocks.size)
        assertEquals(BlockPos.ZERO, bedrockStructure.blocks.keys.first())
        assertEquals(bedrockPlatform.airBlock, bedrockStructure.blocks.values.first())
    }
}
