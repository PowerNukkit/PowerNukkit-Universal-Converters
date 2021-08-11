package org.powernukkit.converters.conversion.job

import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.ProviderWorld

/**
 * @author joserobjr
 * @since 2021-08-11
 */
suspend fun <P: Platform<P>> InputWorld<P>.load(platformProvider: PlatformProvider): ProviderWorld<P> {
    val platform = dialect.loadPlatform(this, platformProvider)
    return storageEngine.loadWorld(this, platform)
}

suspend fun InputWorld<*>.loadUnchecked(platformProvider: PlatformProvider): ProviderWorld<*> {
    @Suppress("UNCHECKED_CAST")
    return (this as InputWorld<Nothing>).load(platformProvider)
}
