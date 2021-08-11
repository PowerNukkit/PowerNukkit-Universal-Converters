package org.powernukkit.converters.conversion.job

import kotlinx.coroutines.Deferred
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.bedrock.BedrockPlatform
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import java.util.concurrent.ConcurrentHashMap

/**
 * @author joserobjr
 * @since 2021-08-11
 */
class PlatformProvider(val universal: Deferred<UniversalPlatform>) {
    private val platforms = ConcurrentHashMap<MinecraftEdition, Platform<*>>(3)

    suspend fun universal() = universal.await()
    suspend fun java() = get(MinecraftEdition.JAVA)
    suspend fun bedrock() = get(MinecraftEdition.BEDROCK)

    suspend fun get(edition: MinecraftEdition): Platform<*> {
        return platforms.getOrPut(edition) {
            when (edition) {
                MinecraftEdition.JAVA -> JavaPlatform(universal.await())
                MinecraftEdition.BEDROCK -> BedrockPlatform(universal.await())
                MinecraftEdition.UNIVERSAL -> universal.await()
            }
        }
    }
}
