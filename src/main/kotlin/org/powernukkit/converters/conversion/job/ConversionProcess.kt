package org.powernukkit.converters.conversion.job

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import org.powernukkit.converters.conversion.converter.PlatformConverter
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.storage.api.ProviderWorld
import org.powernukkit.converters.storage.api.ReceivingWorld

/**
 * @author joserobjr
 * @since 2021-08-11
 */
class ConversionProcess<FromPlatform: Platform<FromPlatform>, ToPlatform: Platform<ToPlatform>>(
    val converter: PlatformConverter<FromPlatform, ToPlatform>,
    val from: ProviderWorld<FromPlatform>,
    val to: ReceivingWorld<ToPlatform>,
    val chunksConverted: StateFlow<Long>,
    val job: Job,
)
