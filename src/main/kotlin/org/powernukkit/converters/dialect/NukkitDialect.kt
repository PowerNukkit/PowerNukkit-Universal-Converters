package org.powernukkit.converters.dialect

import org.powernukkit.converters.storage.api.StorageEngineType

/**
 * @author joserobjr
 * @since 2021-08-10
 */
open class NukkitDialect: BedrockEditionDialect() {
    override val type: Dialect get() = Dialect.NUKKIT
    override val defaultStorageEngine = StorageEngineType.ANVIL
}
