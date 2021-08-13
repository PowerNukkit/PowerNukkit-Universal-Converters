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

package org.powernukkit.converters.conversion.converter

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.powernukkit.converters.conversion.adapter.Adapters
import org.powernukkit.converters.conversion.adapter.BlockTypeAdapter
import org.powernukkit.converters.conversion.context.BlockLayersSingleConversionContext
import org.powernukkit.converters.conversion.context.BlockStateConversionContext
import org.powernukkit.converters.conversion.context.BlockTypeConversionContext
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import kotlin.test.assertEquals
import kotlin.test.assertFails


/**
 * @author joserobjr
 * @since 2020-10-18
 */
@ExtendWith(MockKExtension::class)
internal class BlockTypeConverterTest {
    @MockK
    lateinit var fromPlatform: FromPlatform

    @MockK
    lateinit var toPlatform: ToPlatform

    lateinit var converter: BlockTypeConverter<FromPlatform, ToPlatform>

    val firstAdapters = mutableListOf<BlockTypeAdapter<FromPlatform, ToPlatform>>()
    val lastAdapters = mutableListOf<BlockTypeAdapter<FromPlatform, ToPlatform>>()

    @MockK
    lateinit var fromState: PlatformBlockState<FromPlatform>

    @MockK
    lateinit var parentContext: BlockLayersSingleConversionContext<FromPlatform, ToPlatform>

    @BeforeEach
    internal fun setUp() {
        fromPlatform.commonMocks()
        toPlatform.commonMocks()

        firstAdapters.clear()
        lastAdapters.clear()

        converter = BlockTypeConverter(
            fromPlatform, toPlatform, Adapters(
                firstAdapters = firstAdapters,
                lastAdapters = lastAdapters
            )
        )
    }

    @Test
    fun convert() {
        val fromType = mockk<PlatformBlockType<FromPlatform>>()
        every { fromType.id } returns NamespacedId("from_stone")

        val context = BlockStateConversionContext(fromState, parentContext)
        assertFails { converter.convert(fromType, context) }

        val toType = mockk<PlatformBlockType<ToPlatform>>()
        every { toType.id } returns NamespacedId("to_dirt")

        firstAdapters += object : BlockTypeAdapter<FromPlatform, ToPlatform> {
            override fun adaptBlockType(context: BlockTypeConversionContext<FromPlatform, ToPlatform>) {
                if (context.fromBlockType == fromType) {
                    context.toBlockType = toType
                }
            }
        }

        assertEquals(toType, converter.convert(fromType, context))
    }
}
