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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.powernukkit.converters.conversion.adapter.Adapters
import org.powernukkit.converters.conversion.adapter.BlockAdapter
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.BlockContainer
import org.powernukkit.converters.platform.api.MutableBlockContainer
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PlatformBlockEntity
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.platform.api.block.PlatformBlockType
import org.powernukkit.converters.platform.api.entity.PlatformEntity
import org.powernukkit.converters.platform.base.block.BaseBlock
import kotlin.test.assertEquals

/**
 * @author joserobjr
 * @since 2020-10-18
 */
@ExtendWith(MockKExtension::class)
internal class BlockConverterTest {
    @MockK
    lateinit var fromPlatform: FromPlatform

    @MockK
    lateinit var toPlatform: ToPlatform

    @MockK
    lateinit var blockLayersConverter: BlockLayersConverter<FromPlatform, ToPlatform>

    @MockK
    lateinit var blockEntityConverter: BlockEntityConverter<FromPlatform, ToPlatform>

    @MockK
    lateinit var entityConverter: EntityConverter<FromPlatform, ToPlatform>

    lateinit var converter: BlockConverter<FromPlatform, ToPlatform>

    val firstAdapters = mutableListOf<BlockAdapter<FromPlatform, ToPlatform>>()

    private val fromContainer = SimpleContainer<FromPlatform>()
    private val toContainer = MutableSimpleContainer<ToPlatform>()

    @MockK
    lateinit var fromBlock: PlatformBlock<FromPlatform>

    @MockK
    lateinit var fromState1: PlatformBlockState<FromPlatform>

    @MockK
    lateinit var fromType1: PlatformBlockType<FromPlatform>

    @MockK
    lateinit var fromState2: PlatformBlockState<FromPlatform>

    @MockK
    lateinit var fromType2: PlatformBlockType<FromPlatform>

    val fromBlockLayers = mutableListOf<PlatformBlockState<FromPlatform>>()

    @MockK
    lateinit var toState1: PlatformBlockState<ToPlatform>

    @MockK
    lateinit var toType1: PlatformBlockType<ToPlatform>

    @MockK
    lateinit var toState2: PlatformBlockState<ToPlatform>

    @MockK
    lateinit var toType2: PlatformBlockType<ToPlatform>

    @MockK
    lateinit var toState3: PlatformBlockState<ToPlatform>

    @MockK
    lateinit var toType3: PlatformBlockType<ToPlatform>

    val toBlockLayers = mutableListOf<PlatformBlockState<ToPlatform>>()

    @MockK
    lateinit var fromBlockEntity: PlatformBlockEntity<FromPlatform>

    @MockK
    lateinit var toBlockEntity: PlatformBlockEntity<ToPlatform>

    @MockK
    lateinit var fromEntity1: PlatformEntity<FromPlatform>

    @MockK
    lateinit var fromEntity2: PlatformEntity<FromPlatform>

    val fromEntities = mutableListOf<PlatformEntity<FromPlatform>>()

    @MockK
    lateinit var toEntity1: PlatformEntity<ToPlatform>

    @MockK
    lateinit var toEntity2: PlatformEntity<ToPlatform>

    @MockK
    lateinit var toEntity3: PlatformEntity<ToPlatform>

    val toEntities = mutableListOf<PlatformEntity<ToPlatform>>()

    @MockK
    lateinit var toBlock: BaseBlock<ToPlatform>

    @BeforeEach
    fun setUp() {
        fromPlatform.commonMocks()
        toPlatform.commonMocks()

        firstAdapters.clear()
        fromContainer.content.clear()
        toContainer.content.clear()
        fromBlockLayers.clear()
        toBlockLayers.clear()
        fromEntities.clear()
        toEntities.clear()

        fromBlockLayers += fromState1
        fromBlockLayers += fromState2

        toBlockLayers += toState1
        toBlockLayers += toState2
        toBlockLayers += toState3

        every { toState1.type } returns toType1
        every { toState2.type } returns toType2
        every { toState3.type } returns toType3

        every { toType1.id } returns NamespacedId("to_1")
        every { toType2.id } returns NamespacedId("to_2")
        every { toType3.id } returns NamespacedId("to_3")

        every { fromState1.type } returns fromType1
        every { fromState2.type } returns fromType2

        every { fromType1.id } returns NamespacedId("from_1")
        every { fromType2.id } returns NamespacedId("from_2")

        every { fromBlock.mainState } returns fromState1
        every { fromBlock.blockLayers } returns fromBlockLayers

        every { fromBlock.entities } returns fromEntities
        every { fromBlock.blockEntity } returns fromBlockEntity
        every { fromBlock.isBlockAir } returns false

        every { blockEntityConverter.convert(fromBlockEntity, any()) } returns toBlockEntity
        every { blockLayersConverter.convert(fromBlockLayers, any()) } returns toBlockLayers
        every { entityConverter.convertList(fromEntities, any()) } returns toEntities

        every { toPlatform.createPlatformBlock(toBlockLayers, toBlockEntity, toEntities) } returns toBlock

        converter = BlockConverter(
            fromPlatform, toPlatform,
            blockLayersConverter, blockEntityConverter, entityConverter,
            Adapters(firstAdapters = firstAdapters)
        )
    }

    @Test
    fun convert() {
        val fromPos = BlockPos(1, 2, 3)
        converter.convert(fromBlock, fromPos, fromContainer, toContainer)
        assertEquals(toBlock, toContainer.getBlock(fromPos))
    }

    private open class SimpleContainer<P : Platform<P>> : BlockContainer<P> {
        val content = mutableMapOf<BlockPos, PlatformBlock<P>>()
        override val mainBlock get() = content[BlockPos.ZERO]!!
        override fun getBlock(pos: BlockPos) = content[pos]
        override fun contains(key: BlockPos) = key in content
    }

    private open class MutableSimpleContainer<P : Platform<P>> : SimpleContainer<P>(), MutableBlockContainer<P> {
        override var mainBlock: PlatformBlock<P>
            get() = super.mainBlock
            set(value) {
                content[BlockPos.ZERO] = value
            }

        override fun set(pos: BlockPos, block: PlatformBlock<P>) {
            content[pos] = block
        }
    }
}
