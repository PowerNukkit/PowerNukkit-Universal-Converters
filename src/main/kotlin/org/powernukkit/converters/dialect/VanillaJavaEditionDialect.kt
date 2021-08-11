package org.powernukkit.converters.dialect

/**
 * @author joserobjr
 * @since 2021-08-10
 */
open class VanillaJavaEditionDialect : JavaEditionDialect() {
    override val type: Dialect get() = Dialect.VANILLA_JAVA_EDITION
}
