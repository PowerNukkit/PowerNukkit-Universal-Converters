package org.powernukkit.converters.dialect

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.storage.api.StorageEngineType

/**
 * @author joserobjr
 * @since 2021-08-11
 */
abstract class BedrockEditionDialect: IDialect {
    override val defaultStorageEngine get() = StorageEngineType.LEVELDB
    final override val defaultMinecraftEdition get() = MinecraftEdition.BEDROCK
}
