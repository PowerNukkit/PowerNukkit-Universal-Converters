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

package org.powernukkit.converters.conversion.converter

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.powernukkit.converters.conversion.adapter.Adapters
import org.powernukkit.converters.conversion.adapter.BlockStateAdapter
import org.powernukkit.converters.conversion.context.BlockLayersSingleConversionContext
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import kotlin.test.assertEquals

/**
 * @author joserobjr
 * @since 2020-10-18
 */
@ExtendWith(MockKExtension::class)
internal class BlockStateConverterTest {
    @MockK
    lateinit var fromPlatform: FromPlatform

    @MockK
    lateinit var toPlatform: ToPlatform

    @MockK
    lateinit var blockTypeConverter: BlockTypeConverter<FromPlatform, ToPlatform>

    @MockK
    lateinit var blockPropertyValuesConverter: BlockPropertyValuesConverter<FromPlatform, ToPlatform>

    lateinit var converter: BlockStateConverter<FromPlatform, ToPlatform>

    val firstAdapters = mutableListOf<BlockStateAdapter<FromPlatform, ToPlatform>>()

    @MockK
    lateinit var fromType: PlatformBlockType<FromPlatform>

    @MockK
    lateinit var fromState: PlatformBlockState<FromPlatform>

    @MockK
    lateinit var fromValue1: PlatformBlockPropertyValue<FromPlatform>

    @MockK
    lateinit var fromValue2: PlatformBlockPropertyValue<FromPlatform>

    @MockK
    lateinit var toValue1: PlatformBlockPropertyValue<ToPlatform>

    val fromPropertyValues = mutableMapOf<String, PlatformBlockPropertyValue<FromPlatform>>()
    val toPropertyValues = mutableMapOf<String, PlatformBlockPropertyValue<ToPlatform>>()

    @MockK
    lateinit var toType: PlatformBlockType<ToPlatform>

    @MockK
    lateinit var toState: PlatformBlockState<ToPlatform>

    @MockK
    lateinit var context: BlockLayersSingleConversionContext<FromPlatform, ToPlatform>

    @BeforeEach
    fun setUp() {
        fromPlatform.commonMocks()
        toPlatform.commonMocks()

        firstAdapters.clear()
        fromPropertyValues.clear()
        toPropertyValues.clear()

        fromPropertyValues.putAll(
            mapOf(
                "1" to fromValue1,
                "2" to fromValue2
            )
        )

        toPropertyValues["a"] = toValue1

        every { fromType.id } returns NamespacedId("from_stone")
        every { toType.id } returns NamespacedId("to_dirt")

        every { fromState.type } returns fromType
        every { fromState.values } returns fromPropertyValues

        every { blockTypeConverter.convert(fromType, any()) } returns toType
        every { blockPropertyValuesConverter.convert(fromPropertyValues, toType, any()) } returns toPropertyValues

        converter = BlockStateConverter(
            fromPlatform, toPlatform,
            blockTypeConverter, blockPropertyValuesConverter,
            Adapters(firstAdapters = firstAdapters)
        )
    }

    @Test
    fun convert() {
        val okState = mockk<PlatformBlockState<ToPlatform>>()
        every { toType.withState(toPropertyValues) } returns okState

        assertEquals(listOf(okState), converter.convert(fromState, context))
    }
}
