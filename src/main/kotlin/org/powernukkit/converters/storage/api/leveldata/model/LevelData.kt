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

package org.powernukkit.converters.storage.api.leveldata.model

import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.nbtmanipulator.NbtTag
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.math.EntityPos
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.version.Version
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.time.Instant

/**
 * @property dataFile The original NBT file structure where the data were extracted
 * @property storageEngine The detected storage engine used to extract the data
 * @property minecraftEdition The detected minecraft edition based on the folder contents
 * @property minecraftVersion The detected version of the minecraft edition
 * @property folder The root folder of the level
 *
 * @property spawn The server spawn location
 * @property randomSeed The random number used to generate the world
 * @property lastPlayed Last time the world was loaded
 * @property time The current in-game time age of the world, used to calculate Minecraft days
 * @property sizeOnDisk A cached value to show the disk usage
 *
 * @property snowCovered Generates a perpetual snow biome in alpha versions of Minecraft Java Edition
 *
 * @property mapFeatures Also known as MapFeatures from JE Beta 1.8 to 1.15,
 * defines if the map generator should place structures such as villages, strongholds, and mineshafts.
 *
 * @author joserobjr
 * @since 2020-10-19
 */
data class LevelData(
    // Internal data
    val dataFile: NbtFile?,
    val storageEngine: StorageEngine,
    val minecraftEdition: MinecraftEdition,
    val minecraftVersion: Version,
    val folder: Path,

    // Since JE inf-dev
    val spawn: BlockPos?,
    val randomSeed: Long?, // Position changed in JE 1.16+
    val lastPlayed: Instant?,
    val time: Long?,
    val sizeOnDisk: Long?, // Removed in JE 1.16+

    // Since Alpha
    val snowCovered: Boolean?, // Removed in JE Beta 1.3

    // Since JE Beta 1.3
    val version: Int?,
    val name: String?,

    // Since JE Beta 1.5
    val raining: Boolean?,
    val thundering: Boolean?,
    val rainTime: Int?,
    val thunderTime: Int?,

    // Since JE Beta 1.8
    val gameType: Int?,
    val mapFeatures: Boolean?,

    // Since JE 1.0
    val hardcore: Boolean?,

    // Since JE 1.1
    val generatorName: String?, //Removed in JE 1.16+

    // Since JE 1.2
    val generatorVersion: Int?, //Removed in JE 1.16+

    // Since JE 1.3
    val allowCommands: Boolean?,
    val initialized: Boolean?,

    // Since JE 1.4
    val gameRules: Map<String, String>?,
    val generatorOptions: String?, //Removed in JE 1.13+

    // Since JE 1.8
    val difficultyLocked: Boolean?,
    val difficulty: Int?,
    val clearWeatherTime: Int,
    val borderSizeLerpTime: Long?,
    val borderCenter: EntityPos?,
    val borderDamageperBlock: Double?,
    val borderSafeZone: Double?,
    val borderSize: Double?,
    val borderSizeLerpTarget: Double?,
    val borderWarningBlocks: Double?,
    val borderWarningTime: Double?,

    // Since JE 1.9
    val endDimensionData: EndDimensionData?, // Path changed in JE 1.16
    val versionData: LevelVersionData?,
    val dataVersion: Int?,

    // Since JE 1.13
    val enabledDataPacks: List<String>?,
    val disabledDataPacks: List<String>?,
    val customBosses: Map<NamespacedId, CustomBossData>?,

    // Since JE 1.14
    val scheduledEvents: List<NbtTag>?, // TODO Find details about this data
    val wanderingTraderSpawnChance: Int?,
    val wanderingTraderSpawnDelay: Int?,

    // Since JE 1.16
    val serverBrands: List<String>?,
    val wasModded: Boolean?,
    val dayTime: Long?,

    // Since JE 1.16.3
    val spawnAngle: Double?,
    val bonusChest: Boolean?,
    val dimensionGeneratorSettings: Map<NamespacedId, DimensionGeneratorData>?,

    val icon: BufferedImage? = null
)
