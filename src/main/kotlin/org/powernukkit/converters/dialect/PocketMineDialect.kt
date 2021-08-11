package org.powernukkit.converters.dialect

import org.powernukkit.converters.storage.api.StorageEngineType

/**
 * @author joserobjr
 * @since 2021-08-10
 */
open class PocketMineDialect: BedrockEditionDialect() {
    override val type: Dialect get() = Dialect.POCKET_MINE
    override val defaultStorageEngine get() = StorageEngineType.POCKET_MINE
}
