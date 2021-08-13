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

package org.powernukkit.converters.dialect

import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.StorageEngineType
import org.powernukkit.version.Version

/**
 * @author joserobjr
 * @since 2021-08-11
 */
abstract class JavaEditionDialect: IDialect {
    override val defaultStorageEngine get() = StorageEngineType.ANVIL
    final override val defaultMinecraftEdition get() = MinecraftEdition.JAVA

    override fun storageEngineForVersion(version: Version): StorageEngine {
        return when {
            version >= FIRST_ANVIL_VERSION -> StorageEngineType.ANVIL
            version >= FIRST_REGIONS_VERSION -> StorageEngineType.REGIONS
            else -> StorageEngineType.ALPHA
        }
    }

    private companion object {
        private val FIRST_REGIONS_VERSION = Version("Beta 1.3")
        private val FIRST_ANVIL_VERSION = Version("1.1")
    }
}
