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

package org.powernukkit.converters.conversion.universal.to

import org.powernukkit.converters.conversion.adapter.BlockPropertyValuesAdapter
import org.powernukkit.converters.conversion.context.BlockPropertyValuesConversionContext
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.universal.UniversalPlatform

/**
 * @author joserobjr
 * @since 2020-10-19
 */
interface ToUniversalBlockPropertyValuesAdapter<FromPlatform : Platform<FromPlatform>> :
    BlockPropertyValuesAdapter<FromPlatform, UniversalPlatform> {

    override fun adaptBlockPropertyValues(context: BlockPropertyValuesConversionContext<FromPlatform, UniversalPlatform>) {
        TODO("Not yet implemented")
    }

    companion object {
        private object Default : ToUniversalBlockPropertyValuesAdapter<UniversalPlatform>

        @Suppress("UNCHECKED_CAST")
        fun <FromPlatform : Platform<FromPlatform>> default() =
            Default as ToUniversalBlockPropertyValuesAdapter<FromPlatform>
    }
}
