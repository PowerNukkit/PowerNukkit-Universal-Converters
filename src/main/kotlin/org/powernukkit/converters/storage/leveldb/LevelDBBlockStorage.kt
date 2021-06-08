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

import br.com.gamemods.nbtmanipulator.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.*
import org.powernukkit.converters.internal.compoundOrNull
import org.powernukkit.converters.internal.string
import org.powernukkit.converters.math.nibble.isEven
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.Platform
import org.powernukkit.converters.platform.api.block.PlatformBlockState
import org.powernukkit.converters.storage.api.StorageProblemManager
import org.powernukkit.converters.storage.leveldb.bitarray.LevelDBBitArray
import org.powernukkit.converters.storage.leveldb.bitarray.LevelDBBitArrayVersion
import kotlin.math.ceil
import kotlin.math.floor

/**
 * @author joserobjr
 * @since 2021-06-07
 */
class LevelDBBlockStorage<P : Platform<P>>(
    world: LevelDBProviderWorld<P>,
    problemManager: StorageProblemManager,
    data: ByteReadPacket
) {
    private val storage: LevelDBBitArray
    private val palette: Array<PlatformBlockState<P>>

    init {
        val bitArrayVersion = data.readByte().toInt().let { header ->
            require(header.isEven) { "The block storage is corrupted" }
            LevelDBBitArrayVersion.getByBits(header ushr 1)
        }

        val blocksPerWord = floor(32.0 / bitArrayVersion.bits).toInt()
        val wordCount = ceil(4096.0 / blocksPerWord).toInt()
        val words = IntArray(wordCount)
        data.readFully(words)
        storage = bitArrayVersion.createPalette(4096, words)
        palette = Array(data.readIntLittleEndian()) {
            NbtIO.readNbtFile(data.inputStream(), littleEndian = true, readHeaders = false, compressed = false).let {
                world.decodeBlockState(8.toByte(), it.hashCode()) {
                    parseBlockState(world, problemManager, it.compound)
                }
            }
        }
    }

    private fun parseBlockState(
        world: LevelDBProviderWorld<P>,
        problemManager: StorageProblemManager,
        storage: NbtCompound
    ): PlatformBlockState<P> {
        val name = NamespacedId(storage["name"].string)
        val type = world.platform.getBlockType(name)
            ?: problemManager.handleMissingBlockTypeParsingState(world, storage)
        val statesCompound = storage["states"].compoundOrNull
        if (statesCompound != null) {
            val properties = statesCompound.entries.asSequence().mapNotNull { (key, valueTag) ->
                val property = type.blockProperties[key]
                    ?: problemManager.handleMissingBlockPropertyParsingState(world, storage, type, key, valueTag)
                    ?: return@mapNotNull null


                val value = when (valueTag) {
                    is NbtInt -> property.getPlatformValue(valueTag.value)
                    is NbtByte -> property.getPlatformValue(valueTag.signed)
                    is NbtString -> property.getPlatformValue(valueTag.value)
                    is NbtShort -> property.getPlatformValue(valueTag.value.toInt())
                    else -> property.getPlatformValue(valueTag.stringValue)
                } ?: problemManager.handleMissingBlockPropertyValueParsingState(world, storage, key, valueTag, property)

                key to value
            }.toMap()

            return try {
                type.withState(properties)
            } catch (e: Exception) {
                problemManager.handleExceptionBuildingState(world, storage, type, properties)
            }
        } else {
            val dataValue = storage["val"]?.stringValue?.toInt()
            return if (dataValue == null) {
                type.withState(emptyMap())
            } else {
                type.withStateFromLegacy(dataValue)
            }
        }
    }
}
