/*
 *  PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2021  José Roberto de Araújo Júnior
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
