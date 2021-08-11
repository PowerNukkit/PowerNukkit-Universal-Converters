package org.powernukkit.converters.dialect

/**
 * @author joserobjr
 * @since 2021-08-10
 */
open class ForgeJavaEditionDialect: JavaEditionDialect() {
    override val type: Dialect get() = Dialect.FORGE_JAVA_EDITION
}
