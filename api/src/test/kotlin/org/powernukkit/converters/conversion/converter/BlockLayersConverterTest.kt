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
import org.powernukkit.converters.conversion.adapter.BlockLayersAdapter
import org.powernukkit.converters.conversion.context.BlockConversionContext
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import kotlin.test.assertEquals

/**
 * @author joserobjr
 * @since 2020-10-18
 */
@ExtendWith(MockKExtension::class)
internal class BlockLayersConverterTest {
    @MockK
    lateinit var fromPlatform: FromPlatform

    @MockK
    lateinit var toPlatform: ToPlatform

    @MockK
    lateinit var blockStateConverter: BlockStateConverter<FromPlatform, ToPlatform>

    lateinit var converter: BlockLayersConverter<FromPlatform, ToPlatform>

    val firstAdapters = mutableListOf<BlockLayersAdapter<FromPlatform, ToPlatform>>()

    @MockK
    lateinit var fromType1: PlatformBlockType<FromPlatform>

    @MockK
    lateinit var fromState1: PlatformBlockState<FromPlatform>

    @MockK
    lateinit var fromType2: PlatformBlockType<FromPlatform>

    @MockK
    lateinit var fromState2: PlatformBlockState<FromPlatform>

    @MockK
    lateinit var toType1: PlatformBlockType<ToPlatform>

    @MockK
    lateinit var toState1: PlatformBlockState<ToPlatform>

    @MockK
    lateinit var toType2: PlatformBlockType<ToPlatform>

    @MockK
    lateinit var toState2: PlatformBlockState<ToPlatform>

    @MockK
    lateinit var toType3: PlatformBlockType<ToPlatform>

    @MockK
    lateinit var toState3: PlatformBlockState<ToPlatform>

    val toState1List = mutableListOf<PlatformBlockState<ToPlatform>>()
    val toState2List = mutableListOf<PlatformBlockState<ToPlatform>>()

    val fromLayers = mutableListOf<PlatformBlockState<FromPlatform>>()

    @BeforeEach
    fun setUp() {
        fromPlatform.commonMocks()
        toPlatform.commonMocks()

        firstAdapters.clear()
        fromLayers.clear()
        toState1List.clear()
        toState2List.clear()

        fromLayers += fromState1
        fromLayers += fromState2

        toState1List += toState1
        toState1List += toState2
        toState2List += toState3

        every { blockStateConverter.convert(fromState1, any()) } returns toState1List
        every { blockStateConverter.convert(fromState2, any()) } returns toState2List

        every { fromState1.type } returns fromType1
        every { fromState2.type } returns fromType2
        every { toState1.type } returns toType1
        every { toState2.type } returns toType2
        every { toState3.type } returns toType3

        every { fromType1.id } returns NamespacedId("from_1")
        every { fromType2.id } returns NamespacedId("from_2")
        every { toType1.id } returns NamespacedId("to_1")
        every { toType2.id } returns NamespacedId("to_2")
        every { toType3.id } returns NamespacedId("to_3")

        converter = BlockLayersConverter(
            fromPlatform, toPlatform, blockStateConverter,
            Adapters(firstAdapters = firstAdapters)
        )
    }

    @Test
    fun convert() {
        val context = BlockConversionContext(
            fromPlatform, toPlatform,
            mockk(), BlockPos.ZERO, mockk(), mockk()
        )

        assertEquals(
            listOf(toState1, toState2, toState3),
            converter.convert(fromLayers, context)
        )
    }
}
