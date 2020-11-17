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

package org.powernukkit.converters.storage.leveldb

import br.com.gamemods.regionmanipulator.ChunkPos
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import org.powernukkit.converters.math.startsWith
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author joserobjr
 * @since 2020-11-17
 */
sealed class LevelDBKey {
    abstract val bufferSize: Int
    abstract fun writeTo(buffer: ByteBuf)

    @ExperimentalContracts
    inline fun <R> withBuffer(action: (ByteBuf) -> R): R {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return PooledByteBufAllocator.DEFAULT.directBuffer(bufferSize).let { buffer ->
            try {
                action(buffer)
            } finally {
                buffer.release()
            }
        }
    }

    companion object {
        private val playerPrefix = "player_".toByteArray()
        private val villagePrefix = "VILLAGE_".toByteArray()
        private val localPlayer = "~local_player".toByteArray()
        private val flatWorldPlayers = "game_flatworldlayers".toByteArray()


        @ExperimentalContracts
        fun createByArray(array: ByteArray): LevelDBKey {
            return try {
                when {
                    array.contentEquals(localPlayer) -> LocalPlayerKey
                    array.contentEquals(flatWorldPlayers) -> FlatWorldLayers
                    array.startsWith(playerPrefix) -> RemotePlayerKey(
                        array.copyOfRange(playerPrefix.size, array.size).toString().toLong()
                    )
                    array.startsWith(villagePrefix) -> {
                        val parts = array.copyOfRange(villagePrefix.size, array.size).toString().split('_', limit = 2)
                        VillageKey(parts[0], VillageKeyType.valueOf(parts[1]))
                    }
                    else -> UnknownKey(array).withBuffer(::ChunkKey)
                }
            } catch (e: Exception) {
                UnknownKey(array)
            }
        }
    }
}

class ChunkKey(val pos: ChunkPos, val type: ChunkKeyType, val dimension: Int? = null, val section: Int? = null) :
    LevelDBKey() {
    constructor(buffer: ByteBuf) : this(
        ChunkPos(buffer.readIntLE(), buffer.readIntLE()),
        dimension = buffer.takeIf { it.readableBytes() > 2 }?.readIntLE(),
        type = buffer.readByte().toInt().let { checkNotNull(ChunkKeyType.byCode[it]) { "Bad ChunkKeyType $it" } },
        section = buffer.takeIf { it.readableBytes() == 1 }?.readByte()?.toInt()
    )

    override val bufferSize = 4 + 4 + 1 + (if (dimension != null) 4 else 0) + (if (section != null) 1 else 0)
    override fun writeTo(buffer: ByteBuf) {
        buffer.writeIntLE(pos.xPos).writeIntLE(pos.zPos)
        dimension?.let { buffer.writeIntLE(it) }
        buffer.writeByte(type.code)
        section?.let { buffer.writeByte(it) }
    }
}

abstract class StringKey(string: String) : LevelDBKey() {
    private val bytes = string.toByteArray()
    override val bufferSize = bytes.size
    override fun writeTo(buffer: ByteBuf) {
        buffer.writeBytes(bytes)
    }
}

class RemotePlayerKey(val clientId: Long) : StringKey("player_$clientId")
object LocalPlayerKey : StringKey("~local_player")
object FlatWorldLayers : StringKey("game_flatworldlayers")
class VillageKey(val id: String, type: VillageKeyType) : StringKey("VILLAGE_${id}_$type")
class UnknownKey(val content: ByteArray) : LevelDBKey() {
    override val bufferSize = content.size
    override fun writeTo(buffer: ByteBuf) {
        buffer.writeBytes(content)
    }
}

enum class VillageKeyType {
    DWELLERS,
    INFO,
    POI,
    PLAYERS
}

enum class ChunkKeyType(val code: Int) {
    DATA_2D(45),
    DATA_2D_LEGACY(46),
    SUB_CHUNK_PREFIX(47),
    LEGACY_TERRAIN(48),
    BLOCK_ENTITY(49),
    ENTITY(50),
    PENDING_TICKS(51),
    BLOCK_EXTRA_DATA(52),
    BIOME_STATE(53),
    FINALIZED_STATE(54),
    UNUSED_0X37(55),
    BORDER_BLOCKS(56),
    HARDCODED_SPAWN_AREAS(57),
    RANDOM_TICKS(58),
    CHECKSUM(59),
    VERSION(118),
    ;

    companion object {
        val byCode = values().associateBy { it.code }
    }
}
