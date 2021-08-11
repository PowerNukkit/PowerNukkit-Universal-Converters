/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.storage.api

import org.powernukkit.converters.storage.alpha.AlphaStorageEngine
import org.powernukkit.converters.storage.anvil.AnvilStorageEngine
import org.powernukkit.converters.storage.leveldb.LevelDBStorageEngine
import org.powernukkit.converters.storage.pocketmine.PocketMineStorageEngine
import org.powernukkit.converters.storage.region.McRegionsStorageEngine

/**
 * @author joserobjr
 * @since 2020-10-19
 */
enum class StorageEngineType(val default: StorageEngine): StorageEngine by default {
    ALPHA(AlphaStorageEngine()),
    REGIONS(McRegionsStorageEngine()),
    ANVIL(AnvilStorageEngine()),
    POCKET_MINE(PocketMineStorageEngine()),
    LEVELDB(LevelDBStorageEngine()),
    ;
    override val type: StorageEngineType? get() = this
}
