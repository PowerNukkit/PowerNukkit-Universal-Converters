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
import org.powernukkit.converters.storage.api.StorageEngineType

/**
 * @author joserobjr
 * @since 2021-08-11
 */
abstract class BedrockEditionDialect: IDialect {
    override val defaultStorageEngine get() = StorageEngineType.LEVELDB
    final override val defaultMinecraftEdition get() = MinecraftEdition.BEDROCK
}
