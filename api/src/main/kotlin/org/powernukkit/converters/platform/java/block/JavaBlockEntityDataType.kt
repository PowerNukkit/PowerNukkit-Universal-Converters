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

package org.powernukkit.converters.platform.java.block

import org.powernukkit.converters.platform.base.BaseConstructors
import org.powernukkit.converters.platform.base.block.BaseBlockEntityDataType
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockEntityDataType
import org.powernukkit.converters.platform.universal.definitions.model.block.entity.ModelData

/**
 * @author joserobjr
 * @since 2020-10-13
 */
class JavaBlockEntityDataType : BaseBlockEntityDataType<JavaPlatform> {
    constructor(
        constructors: BaseConstructors<JavaPlatform>,
        name: String,
        type: ModelData.Type,
        optional: Boolean,
        default: String?,
        universal: UniversalBlockEntityDataType?
    ) : super(
        constructors, name, type, optional, default, universal
    )

    constructor(
        constructors: BaseConstructors<JavaPlatform>,
        universal: UniversalBlockEntityDataType
    ) : super(constructors, universal)
}
