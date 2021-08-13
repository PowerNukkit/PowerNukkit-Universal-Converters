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

package org.powernukkit.converters.platform.bedrock.block

import org.powernukkit.converters.platform.base.BaseConstructors
import org.powernukkit.converters.platform.base.block.BaseBlockPropertyValue
import org.powernukkit.converters.platform.base.block.BaseBlockState
import org.powernukkit.converters.platform.base.block.BaseBlockType
import org.powernukkit.converters.platform.bedrock.BedrockPlatform

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class BedrockBlockState(
    constructors: BaseConstructors<BedrockPlatform>,
    type: BaseBlockType<BedrockPlatform>,
    values: Map<String, BaseBlockPropertyValue<BedrockPlatform>>,
) : BaseBlockState<BedrockPlatform>(constructors, type, values)
