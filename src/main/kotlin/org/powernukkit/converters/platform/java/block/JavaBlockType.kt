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

package org.powernukkit.converters.platform.java.block

import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.base.block.BaseBlockType
import org.powernukkit.converters.platform.java.JavaPlatform
import org.powernukkit.converters.platform.universal.block.UniversalBlockType
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class JavaBlockType : BaseBlockType<
        JavaPlatform, JavaBlockProperty, JavaBlockEntityType, JavaBlockPropertyValue
        > {
    constructor(
        platform: JavaPlatform,
        id: NamespacedId,
        blockProperties: List<JavaBlockProperty>,
        blockEntityType: JavaBlockEntityType? = null,
        universalType: UniversalBlockType?
    ) : super(
        platform, id, blockProperties, blockEntityType, universalType
    )

    constructor(
        platform: JavaPlatform,
        id: NamespacedId,
        universalType: UniversalBlockType,
        extraBlock: ModelExtraBlock? = null
    ) : super(platform, id, universalType, extraBlock)
}
