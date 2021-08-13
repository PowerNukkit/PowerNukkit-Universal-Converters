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

package org.powernukkit.converters.storage.alpha

import br.com.gamemods.nbtmanipulator.NbtCompound
import com.github.michaelbull.logging.InlineLogger
import kotlinx.coroutines.flow.Flow
import org.powernukkit.converters.internal.*
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.math.EntityPos
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlock
import org.powernukkit.converters.platform.api.block.PositionedStructure
import org.powernukkit.converters.storage.api.Chunk
import org.powernukkit.converters.storage.api.StorageProblemManager
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author joserobjr
 * @since 2020-11-16
 */
class AlphaChunk<P : Platform<P>>(
    val world: AlphaProviderWorld<P>,
    val pos: AlphaChunkPos,
    private val data: NbtCompound = NbtCompound(),
    problemManager: StorageProblemManager,
) : Chunk<P>(problemManager) {
    val platform get() = world.platform
    override val chunkPos get() = pos.asChunkPos

    private val entitiesNbt get() = data["Level"]["Entities"].compoundListOrNull
    private val blockEntitiesNbt get() = data["Level"]["TileEntities"].compoundListOrNull

    private val blocksArray get() = data["Level"]["Blocks"].byteArrayOrNull
    private val blockDataArray get() = data["Level"]["Data"].byteArrayOrNull

    override val entityCount get() = entitiesNbt?.size ?: 0
    override val chunkSectionCount get() = 8
    override val blockEntityCount get() = blockEntitiesNbt?.size ?: 0

    private val entitiesNbtByPos by lazy {
        (entitiesNbt ?: emptyList())
            .asSequence()
            .map {
                val posList = it["Pos"].doubleListOrNull ?: emptyList()
                BlockPos(
                    posList.getOrNull(0)?.toInt() ?: (pos.xPos shl 4),
                    posList.getOrNull(1)?.toInt() ?: 64,
                    posList.getOrNull(2)?.toInt() ?: (pos.zPos shl 4),
                ) to it
            }.toMapOfList()
    }

    private val blockEntitiesNbtByPos by lazy {
        (blockEntitiesNbt ?: emptyList()).associateBy { nbt ->
            BlockPos(
                nbt["x"].intOrNull ?: (pos.xPos shl 4),
                nbt["y"].intOrNull ?: 64,
                nbt["z"].intOrNull ?: (pos.zPos shl 4),
            )
        }
    }

    override fun countNonAirBlocks() = data["Level"]["Blocks"]?.byteArrayOrNull?.count { it != 0.toByte() } ?: 0

    override fun structureFlow(): Flow<PositionedStructure<P>> {
        TODO("Not yet implemented")
    }

    override fun set(blockInWorld: BlockPos, block: PlatformBlock<P>) {
        TODO("Not yet implemented")
    }

    @ExperimentalContracts
    override fun get(blockInWorld: BlockPos): PlatformBlock<P> {
        if (blockInWorld !in this) {
            throw NoSuchElementException("$blockInWorld is not part of the chunk $pos")
        }

        val blockIndex = blockInWorld.index
        val type = if (blockInWorld.yPos.let { it < 0 || it > 128 }) {
            platform.airBlockType
        } else {
            blocksArray?.getOrNull(blockIndex)
                ?.let {
                    platform.getBlockType(it.toInt())
                        .ifNull { log.warn { "Could not find the block id $it at $blockInWorld" } }
                }
                ?: platform.airBlockType
        }

        val blockData = blockDataArray?.getOrNull(blockIndex / 2)?.toInt()?.let { byte ->
            if (blockIndex % 2 == 0) {
                byte and 0xF
            } else {
                byte shr 4 and 0xF
            }
        }

        val blockState = type.withStateFromLegacy(blockData ?: 0)

        val blockEntity = blockEntitiesNbtByPos[blockInWorld]?.let { nbt ->
            val id = nbt["id"].stringOrNull ?: return@let null
            platform.getBlockEntityType(id)?.createBlockEntity(nbt)
        }

        val entities = entitiesNbtByPos[blockInWorld]?.mapNotNull { entityNbt ->
            val pos = (entitiesNbt["Pos"].doubleListOrNull ?: emptyList()).let {
                EntityPos(
                    it.getOrNull(0) ?: (pos.xPos shl 4).toDouble(),
                    it.getOrNull(1) ?: 64.0,
                    it.getOrNull(2) ?: (pos.zPos shl 4).toDouble(),
                )
            }
            platform.getEntityType(entityNbt["id"].stringOrNull ?: return@mapNotNull null)
                ?.createEntity(pos, entityNbt)
        } ?: emptyList()

        return platform.createPlatformBlock(blockState, blockEntity, entities)
    }

    private val BlockPos.index: Int
        get() {
            val x = xPos and 0xF
            val z = zPos and 0xF
            val y = yPos and 0x7F
            return (x shl 11) or (z shl 7) or y
        }

    companion object {
        private val log = InlineLogger()

        @ExperimentalContracts
        private fun <T> T?.ifNull(block: () -> Unit): T? {
            contract {
                callsInPlace(block, InvocationKind.AT_MOST_ONCE)
            }
            if (this == null) {
                block()
            }
            return this
        }
    }
}
