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

package org.powernukkit.converters.conversion.job

import org.powernukkit.converters.dialect.IDialect
import org.powernukkit.converters.platform.api.MinecraftEdition
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform
import org.powernukkit.converters.storage.api.StorageEngine
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.api.leveldata.model.LevelData
import java.io.File

/**
 * @author joserobjr
 * @since 2020-11-15
 */
class InputWorld<P: Platform<P>>(
    override val levelFolder: File,
    override val levelData: LevelData,
    override val storageEngine: StorageEngine,
    override val dialect: IDialect,
    override val minecraftEdition: MinecraftEdition,
    override val universalPlatform: UniversalPlatform,
    val problemManager: StorageProblemManager,
): World<P> {
    init {
        require(minecraftEdition != MinecraftEdition.UNIVERSAL) {
            "Input world can't be Universal"
        }
    }
}
