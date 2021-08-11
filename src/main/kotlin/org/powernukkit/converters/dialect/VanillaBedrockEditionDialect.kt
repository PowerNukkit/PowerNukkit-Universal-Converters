package org.powernukkit.converters.dialect

/**
 * @author joserobjr
 * @since 2021-08-10
 */
open class VanillaBedrockEditionDialect: BedrockEditionDialect() {
    override val type: Dialect get() = Dialect.VANILLA_BEDROCK_EDITION
}
