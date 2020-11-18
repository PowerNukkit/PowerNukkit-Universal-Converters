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

package org.powernukkit.converters.conversion.job

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.storage.api.Dialect
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.StorageEngineType
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File

/**
 * @author joserobjr
 * @since 2020-11-15
 */
class InputWorld(
    val levelFolder: File,
    val levelData: LevelData,
    val storageEngine: StorageEngineType,
    val dialect: Dialect,
    val minecraftEdition: MinecraftEdition,
    val universalPlatform: UniversalPlatform,
    val problemManager: StorageProblemManager
) {
    val platform = when (minecraftEdition) {
        MinecraftEdition.UNIVERSAL -> throw UnsupportedOperationException("Input world can't be Universal")
        MinecraftEdition.JAVA -> JavaPlatform(universalPlatform, dialect)
        MinecraftEdition.BEDROCK -> BedrockPlatform(universalPlatform, dialect)
    }

    suspend fun load(storage: StorageEngine = storageEngine.default) = storage.loadWorld(this@InputWorld)
}
