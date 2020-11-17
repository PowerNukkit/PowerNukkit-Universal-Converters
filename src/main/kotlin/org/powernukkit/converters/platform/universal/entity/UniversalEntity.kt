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

package org.powernukkit.converters.platform.universal.entity

import br.com.gamemods.nbtmanipulator.NbtCompound
import org.powernukkit.converters.math.EntityPos
import org.powernukkit.converters.platform.api.entity.PlatformEntity
import org.powernukkit.converters.platform.universal.UniversalPlatform

/**
 * @author joserobjr
 * @since 2020-10-10
 */
class UniversalEntity(
    override val type: UniversalEntityType,
    pos: EntityPos,
    private val nbt: NbtCompound,
) : PlatformEntity<UniversalPlatform>(pos) {
    override fun withPos(entityPos: EntityPos) = UniversalEntity(type, entityPos, nbt.deepCopy())
}
