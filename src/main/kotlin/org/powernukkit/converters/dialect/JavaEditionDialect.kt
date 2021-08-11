package org.powernukkit.converters.dialect

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.StorageEngineType
import org.powernukkit.version.Version

/**
 * @author joserobjr
 * @since 2021-08-11
 */
abstract class JavaEditionDialect: IDialect {
    override val defaultStorageEngine get() = StorageEngineType.ANVIL
    final override val defaultMinecraftEdition get() = MinecraftEdition.JAVA

    override fun storageEngineForVersion(version: Version): StorageEngine {
        return when {
            version >= FIRST_ANVIL_VERSION -> StorageEngineType.ANVIL
            version >= FIRST_REGIONS_VERSION -> StorageEngineType.REGIONS
            else -> StorageEngineType.ALPHA
        }
    }

    private companion object {
        private val FIRST_REGIONS_VERSION = Version("Beta 1.3")
        private val FIRST_ANVIL_VERSION = Version("1.1")
    }
}
