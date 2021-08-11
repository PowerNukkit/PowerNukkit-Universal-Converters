package org.powernukkit.converters.conversion.job

import org.powernukkit.converters.dialect.IDialect
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import org.powernukkit.converters.storage.api.leveldata.model.LevelVersionData
import java.io.File

/**
 * @author joserobjr
 * @since 2021-08-11
 */
class OutputWorld<P: Platform<P>>(
    override val levelFolder: File,
    override val storageEngine: StorageEngine,
    override val dialect: IDialect,
    override val minecraftEdition: MinecraftEdition,
    override val universalPlatform: UniversalPlatform,
) : World<P> {
    override var levelData: LevelData = LevelData(
        folder = levelFolder.toPath(),
        dialect = dialect,
        storageEngineType = storageEngine,
        versionData = LevelVersionData(
            minecraftEdition = minecraftEdition,
        ),
    )
}
