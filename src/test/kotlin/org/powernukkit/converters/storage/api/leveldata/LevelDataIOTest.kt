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

package org.powernukkit.converters.storage.api.leveldata

import org.junit.jupiter.api.Test
import java.io.File

/**
 * @author joserobjr
 * @since 2020-10-20
 */
internal class LevelDataIOTest {
    @Test
    fun testJavaEdition() {
        val levelFile = File("sample-worlds/Fresh default worlds/Java Edition/1.16.3/level.dat")
        println(LevelDataIO.readLevelDataBlocking(levelFile))
    }

    @Test
    fun testWindows10Edition() {
        val levelFile = File("sample-worlds/Fresh default worlds/Windows 10 Edition/1.16.40.2.0/level.dat")
        println(LevelDataIO.readLevelDataBlocking(levelFile))
    }

    @Test
    fun testPowerNukkit() {
        val levelFile = File("sample-worlds/Fresh default worlds/PowerNukkit/1.3.1.5-PN/level.dat")
        println(LevelDataIO.readLevelDataBlocking(levelFile))
    }
}
