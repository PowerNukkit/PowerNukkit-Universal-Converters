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

import br.com.gamemods.nbtmanipulator.LittleEndianDataInputStream
import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.nbtmanipulator.NbtIO
import br.com.gamemods.regionmanipulator.ChunkPos
import com.github.michaelbull.logging.InlineLogger
import io.netty.buffer.ByteBuf
import io.netty.buffer.PooledByteBufAllocator
import io.netty.buffer.Unpooled
import org.powernukkit.converters.math.startsWith
import org.powernukkit.converters.storage.leveldb.facade.use
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @author joserobjr
 * @since 2020-11-17
 */
sealed class LevelDBKey {
    abstract val bufferSize: Int

    @OptIn(ExperimentalContracts::class)
    open fun toByteArray() = withBuffer { buf ->
        ByteArray(buf.readableBytes()).also {
            buf.readBytes(it)
        }
    }

    abstract fun writeTo(buffer: ByteBuf)
    abstract fun loadValue(value: ByteArray): Any

    @ExperimentalContracts
    inline fun <R> withBuffer(action: (ByteBuf) -> R): R {
        contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
        return PooledByteBufAllocator.DEFAULT.directBuffer(bufferSize).let { buffer ->
            try {
                writeTo(buffer)
                action(buffer)
            } finally {
                buffer.release()
            }
        }
    }

    override fun toString(): String {
        return buildString {
            append(this@LevelDBKey::class.java.simpleName)
            append('[')
            val bytes = this@LevelDBKey.toByteArray()
            val str = String(bytes)
            if (!str.matches(stringPattern)) {
                append("0x")
                bytes.forEach {
                    append("%02X".format(it))
                }
            } else {
                append(str)
            }
            append(']')
        }
    }

    private data class ByteArrayBox(val byteArray: ByteArray) {
        constructor(string: String) : this(string.toByteArray())

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ByteArrayBox

            if (!byteArray.contentEquals(other.byteArray)) return false

            return true
        }

        override fun hashCode(): Int {
            return byteArray.contentHashCode()
        }
    }

    companion object {
        private val log = InlineLogger()
        private val stringPattern = Regex("^[\\w~_-]+$")

        private val constantStringKeys = StringKey::class.sealedSubclasses.asSequence()
            .mapNotNull { it.objectInstance }
            .associateBy { ByteArrayBox(it.toByteArray()) } + mapOf(
            ByteArrayBox("Overworld") to DimensionKey("Overworld"),
            ByteArrayBox("Nether") to DimensionKey("Nether"),
        )

        private val posTrackDBPrefix = "PosTrackDB-0x".toByteArray()
        private val playerPrefix = "player_".toByteArray()
        private val villagePrefix = "VILLAGE_".toByteArray()


        @OptIn(ExperimentalContracts::class)
        fun createByArray(array: ByteArray): LevelDBKey {
            return try {
                constantStringKeys[ByteArrayBox(array)]?.let { return it }
                when {
                    array.startsWith(posTrackDBPrefix) -> PosTrackDBKey(
                        String(array.copyOfRange(posTrackDBPrefix.size, array.size)).toLong()
                    )
                    array.startsWith(playerPrefix) -> RemotePlayerKey(
                        String(array.copyOfRange(playerPrefix.size, array.size))
                    )
                    array.startsWith(villagePrefix) -> {
                        val parts = String(array.copyOfRange(villagePrefix.size, array.size)).split('_', limit = 2)
                        VillageKey(parts[0], VillageKeyType.valueOf(parts[1]))
                    }
                    else -> UnknownKey(array).withBuffer(::ChunkKey)
                }
            } catch (e: Exception) {
                log.debug(e) { "Error while parsing a LevelDBKey: ${array.toList()}" }
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

    override fun loadValue(value: ByteArray) = type.loadValue(value)
    override fun toString(): String {
        return super.toString() + "(pos=$pos, type=$type, dimension=$dimension, section=$section)"
    }
}

sealed class StringKey(string: String) : LevelDBKey() {
    private val bytes = string.toByteArray()
    override val bufferSize = bytes.size

    fun matches(key: ByteArray) = bytes.contentEquals(key)

    override fun writeTo(buffer: ByteBuf) {
        buffer.writeBytes(bytes)
    }

    override fun toByteArray() = bytes.clone()

    override fun toString(): String {
        return super.toString() + "(${String(bytes)})"
    }
}

class RemotePlayerKey(clientId: String) : StringKey("player_$clientId") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object LocalPlayerKey : StringKey("~local_player") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object FlatWorldLayersKey : StringKey("game_flatworldlayers") {
    override fun loadValue(value: ByteArray): IntArray {
        return String(value, Charsets.US_ASCII)
            .removeSurrounding("[", "]")
            .split(",")
            .let { list ->
                IntArray(list.size) {
                    list[it].toInt()
                }
            }
    }
}

class DimensionKey(val type: String) : StringKey(type) {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

class VillageKey(val id: String, type: VillageKeyType) : StringKey("VILLAGE_${id}_$type") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object ScoreboardKey : StringKey("scoreboard") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object ScheduledWTKey : StringKey("schedulerWT") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object AutonomousEntitiesKey : StringKey("AutonomousEntities") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object BiomeDataKey : StringKey("BiomeData") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object MobEventsKey : StringKey("mobevents") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

class PosTrackDBKey(id: Long) : StringKey("PosTrackDB-0x%08d".format(id)) {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object PosTrackDBLastKey : StringKey("PositionTrackDB-LastId") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

object PortalsKey : StringKey("portals") {
    override fun loadValue(value: ByteArray) = readNbt(value)
}

class UnknownKey(private val content: ByteArray) : LevelDBKey() {
    override val bufferSize = content.size
    override fun toByteArray() = content.clone()

    override fun writeTo(buffer: ByteBuf) {
        buffer.writeBytes(content)
    }

    override fun loadValue(value: ByteArray) = value

    override fun toString(): String {
        return super.toString() + "(${String(content)})"
    }
}

enum class VillageKeyType {
    DWELLERS,
    INFO,
    POI,
    PLAYERS
}

sealed class ChunkKeyType(val code: Int) {
    abstract fun loadValue(value: ByteArray): Any

    /**
     * Holds the biome information.
     */
    object DATA_2D : ChunkKeyType(45) {
        override fun loadValue(value: ByteArray) = value
    }

    object DATA_2D_LEGACY : ChunkKeyType(46) {
        override fun loadValue(value: ByteArray) = value
    }

    /**
     * Holds the section data.
     */
    object SUB_CHUNK_PREFIX : ChunkKeyType(47) {
        override fun loadValue(value: ByteArray) = value
    }

    object LEGACY_TERRAIN : ChunkKeyType(48) {
        override fun loadValue(value: ByteArray) = readNbt(value)
    }

    object BLOCK_ENTITY : ChunkKeyType(49) {
        override fun loadValue(value: ByteArray): List<NbtFile> {
            val bIn = value.inputStream()
            return sequence {
                while (bIn.available() > 0) {
                    yield(NbtIO.readNbtFileDirectly(LittleEndianDataInputStream(bIn)))
                }
            }.toList()
        }
    }

    object ENTITY : ChunkKeyType(50) {
        override fun loadValue(value: ByteArray): List<NbtFile> {
            val bIn = value.inputStream()
            return sequence {
                while (bIn.available() > 0) {
                    yield(NbtIO.readNbtFileDirectly(LittleEndianDataInputStream(bIn)))
                }
            }.toList()
        }
    }

    object PENDING_TICKS : ChunkKeyType(51) {
        override fun loadValue(value: ByteArray) = readNbt(value)
    }

    object BLOCK_EXTRA_DATA : ChunkKeyType(52) {
        override fun loadValue(value: ByteArray) = readNbt(value)
    }

    object BIOME_STATE : ChunkKeyType(53) {
        override fun loadValue(value: ByteArray): ByteArray {
            return value
        }
    }

    object FINALIZED_STATE : ChunkKeyType(54) {
        override fun loadValue(value: ByteArray): Int {
            return Unpooled.wrappedBuffer(value).use { byteBuf ->
                byteBuf.readIntLE()
            }
        }
    }

    object UNUSED_0X37 : ChunkKeyType(55) {
        override fun loadValue(value: ByteArray) = value
    }

    object BORDER_BLOCKS : ChunkKeyType(56) {
        override fun loadValue(value: ByteArray) = readNbt(value)
    }

    object HARDCODED_SPAWN_AREAS : ChunkKeyType(57) {
        override fun loadValue(value: ByteArray) = readNbt(value)
    }

    object RANDOM_TICKS : ChunkKeyType(58) {
        override fun loadValue(value: ByteArray) = readNbt(value)
    }

    object CHECKSUM : ChunkKeyType(59) {
        override fun loadValue(value: ByteArray) = value
    }

    object VERSION : ChunkKeyType(118) {
        override fun loadValue(value: ByteArray) = value[0]
    }

    override fun toString(): String {
        return this::class.java.simpleName
    }

    companion object {
        val byCode = ChunkKeyType::class.sealedSubclasses.asSequence()
            .mapNotNull { it.objectInstance }
            .associateBy { it.code }
    }
}

private fun readNbt(value: ByteArray): NbtFile {
    return NbtIO.readNbtFile(value.inputStream(), compressed = false, littleEndian = true)
}
