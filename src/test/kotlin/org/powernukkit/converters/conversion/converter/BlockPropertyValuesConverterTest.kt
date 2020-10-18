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
import org.powernukkit.converters.conversion.adapter.BlockPropertyValuesAdapter
import org.powernukkit.converters.conversion.context.BlockPropertyValuesConversionContext
import org.powernukkit.converters.conversion.context.BlockStateConversionContext
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.block.PlatformBlockPropertyValue
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * @author joserobjr
 * @since 2020-10-18
 */
@ExtendWith(MockKExtension::class)
internal class BlockPropertyValuesConverterTest {

    @MockK
    lateinit var fromPlatform: FromPlatform

    @MockK
    lateinit var toPlatform: ToPlatform

    @MockK
    lateinit var context: BlockStateConversionContext<FromPlatform, ToPlatform>

    @MockK
    lateinit var toType: PlatformBlockType<ToPlatform>

    @MockK
    lateinit var fromValue1: PlatformBlockPropertyValue<FromPlatform>

    @MockK
    lateinit var fromValue2: PlatformBlockPropertyValue<FromPlatform>

    @MockK
    lateinit var fromValue3: PlatformBlockPropertyValue<FromPlatform>

    @MockK
    lateinit var toValue1: PlatformBlockPropertyValue<ToPlatform>

    @MockK
    lateinit var toValue2: PlatformBlockPropertyValue<ToPlatform>

    lateinit var converter: BlockPropertyValuesConverter<FromPlatform, ToPlatform>

    val firstAdapters = mutableListOf<BlockPropertyValuesAdapter<FromPlatform, ToPlatform>>()

    val values = mutableMapOf<String, PlatformBlockPropertyValue<FromPlatform>>()

    @BeforeEach
    fun setUp() {
        fromPlatform.commonMocks()
        toPlatform.commonMocks()

        firstAdapters.clear()
        values.clear()
        values.putAll(
            mapOf(
                "1" to fromValue1,
                "2" to fromValue2,
                "3" to fromValue3,
            )
        )

        every { toType.id } returns NamespacedId("to_dirt")

        converter = BlockPropertyValuesConverter(
            fromPlatform, toPlatform, Adapters(
                firstAdapters = firstAdapters
            )
        )
    }

    @Test
    fun convert() {
        val fromType = mockk<PlatformBlockType<FromPlatform>>()
        every { fromType.id } returns NamespacedId("from_stone")

        val fromState = mockk<PlatformBlockState<FromPlatform>>()
        every { fromState.type } returns fromType

        every { context.fromBlockState } returns fromState
        assertFails { converter.convert(values, toType, context) }

        val okValues = mapOf(
            "a" to toValue1,
            "b" to toValue2
        )

        firstAdapters += object : BlockPropertyValuesAdapter<FromPlatform, ToPlatform> {
            override fun adaptBlockPropertyValues(context: BlockPropertyValuesConversionContext<FromPlatform, ToPlatform>) {
                if (context.fromValues == values && context.toType == toType) {
                    context.result = okValues
                }
            }
        }

        assertEquals(okValues, converter.convert(values, toType, context))
    }
}
