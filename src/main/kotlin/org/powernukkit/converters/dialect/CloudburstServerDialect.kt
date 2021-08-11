package org.powernukkit.converters.dialect

/**
 * @author joserobjr
 * @since 2021-08-10
 */
open class CloudburstServerDialect: BedrockEditionDialect() {
    override val type: Dialect get() = Dialect.CLOUDBURST_SERVER
}
