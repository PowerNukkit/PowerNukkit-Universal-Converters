package org.powernukkit.converters.conversion.job

import org.powernukkit.converters.dialect.IDialect
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File

/**
 * @author joserobjr
 * @since 2021-08-11
 */
interface World<P: Platform<P>> {
    val levelFolder: File
    val levelData: LevelData
    val storageEngine: StorageEngine
    val dialect: IDialect
    val minecraftEdition: MinecraftEdition
    val universalPlatform: UniversalPlatform
}
