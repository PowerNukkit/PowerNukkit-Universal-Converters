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

package org.powernukkit.converters.storage.api

import kotlinx.coroutines.Deferred
import org.powernukkit.converters.conversion.job.InputWorld
import org.powernukkit.converters.conversion.job.OutputWorld
import org.powernukkit.converters.conversion.job.PlatformProvider
import org.powernukkit.converters.platform.api.Platform

/**
 * @author joserobjr
 * @since 2020-10-23
 */
interface StorageEngine {
    val type: StorageEngineType?

    suspend fun <P: Platform<P>> loadWorld(
        inputWorld: InputWorld<P>,
        platform: P,
    ): ProviderWorld<P>

    suspend fun <FromPlatform: Platform<FromPlatform>, ToPlatform: Platform<ToPlatform>> prepareToReceive(
        fromWorld: Deferred<ProviderWorld<FromPlatform>>,
        toWorld: OutputWorld<ToPlatform>,
        using: PlatformProvider,
    ): ReceivingWorld<ToPlatform>
}
