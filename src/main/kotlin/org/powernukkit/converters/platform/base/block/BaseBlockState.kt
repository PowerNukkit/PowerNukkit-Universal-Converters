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

package org.powernukkit.converters.platform.base.block

import org.powernukkit.converters.platform.api.block.PlatformBlockProperty
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.java.block.JavaBlockType

/**
 * @author joserobjr
 * @since 2020-10-13
 */
abstract class BaseBlockState<
        P: BasePlatform<P, BlockProperty, *, BlockType, *, BlockPropertyValue>,
        BlockType: PlatformBlockType<P>,
        BlockProperty: PlatformBlockProperty<P>,
        BlockPropertyValue: PlatformBlockPropertyValue<P>
        >(
    final override val type: JavaBlockType
): PlatformBlockState<JavaPlatform>()
