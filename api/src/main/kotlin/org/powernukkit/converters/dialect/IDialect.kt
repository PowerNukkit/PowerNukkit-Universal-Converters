/*
 *  PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.dialect

import kotlinx.coroutines.Deferred
import org.powernukkit.converters.conversion.job.InputWorld
import org.powernukkit.converters.conversion.job.OutputWorld
import org.powernukkit.converters.conversion.job.PlatformProvider
import org.powernukkit.converters.conversion.universal.ChainedConverter
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.*
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.version.Version
import java.nio.file.Path

/**
 * @author joserobjr
 * @since 2021-08-10
 */
interface IDialect {
    val type: Dialect?

    val defaultStorageEngine: StorageEngineType?
    val defaultMinecraftEdition: MinecraftEdition?

    fun storageEngineForVersion(version: Version): StorageEngine? = null
    fun minecraftEditionForVersion(version: Version): MinecraftEdition? = null

    suspend fun <FromPlatform: Platform<FromPlatform>, ToPlatform: Platform<ToPlatform>> createConversionChain(
        input: InputWorld<FromPlatform>,
        providerWorldAsync: Deferred<ProviderWorld<FromPlatform>>,
        receivingWorld: Deferred<ReceivingWorld<ToPlatform>>,
        problemManager: StorageProblemManager,
    ): ChainedConverter<FromPlatform, ToPlatform> {
        val providerWorld = providerWorldAsync.await()
        val providerPlatform = providerWorld.platform
        val universalConversion = providerPlatform.convertToUniversal()
        return receivingWorld.await().platform.convertFromUniversal(universalConversion)
    }

    suspend fun <ToPlatform: Platform<ToPlatform>> createOutputWorld(
        toFolder: Path,
        toVersion: Version,
        forcedStorage: StorageEngineType?,
        using: PlatformProvider,
        problemManager: StorageProblemManager
    ): OutputWorld<ToPlatform> {
        return OutputWorld(
            levelFolder = toFolder.toFile(),
            dialect = this,
            minecraftEdition = minecraftEditionForVersion(toVersion)
                ?: defaultMinecraftEdition
                ?: error("Unable to define the output minecraft edition"),

            storageEngine = forcedStorage ?: storageEngineForVersion(toVersion)
                ?: defaultStorageEngine
                ?: error("Unable to define the output storage engine"),

            universalPlatform = using.universal()
        )
    }

    suspend fun <FromPlatform: Platform<FromPlatform>> createInputWorld(
        fromLevelData: LevelData,
        using: PlatformProvider,
        problemManager: StorageProblemManager,
    ): InputWorld<FromPlatform> {
        return InputWorld(
            levelData = fromLevelData,
            problemManager = problemManager,
            dialect = this,

            levelFolder = fromLevelData.folder?.toFile()
                ?: error("The input folder is undefined"),

            storageEngine = fromLevelData.storageEngineType
                ?: defaultStorageEngine
                ?: error("The input storage engine is undefined"),

            minecraftEdition = fromLevelData.versionData?.minecraftEdition
                ?: defaultMinecraftEdition
                ?: error("The input Minecraft Edition is undefined"),

            universalPlatform = using.universal(),
        )
    }

    suspend fun <P: Platform<P>> loadPlatform(world: InputWorld<P>, using: PlatformProvider): P {
        val edition = world.levelData.versionData?.baseGameVersion?.let { minecraftEditionForVersion(it) }
            ?: defaultMinecraftEdition
            ?: error("Could not determine the Minecraft Edition for the input world")

        return loadPlatform(edition, using)
    }

    suspend fun <P: Platform<P>> loadPlatform(edition: MinecraftEdition, using: PlatformProvider): P {
        require(edition != MinecraftEdition.UNIVERSAL) {  "The edition cannot be Universal!" }
        if (defaultMinecraftEdition != null) {
            require(edition == defaultMinecraftEdition) { "The dialect $this only support the $defaultMinecraftEdition edition, got $edition" }
        }


        @Suppress("UNCHECKED_CAST")
        return using.get(edition) as P
    }
}
