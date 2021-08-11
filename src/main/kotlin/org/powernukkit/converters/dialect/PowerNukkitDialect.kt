package org.powernukkit.converters.dialect

/**
 * @author joserobjr
 * @since 2021-08-10
 */
open class PowerNukkitDialect: NukkitDialect() {
    override val type: Dialect get() = Dialect.POWER_NUKKIT
}
