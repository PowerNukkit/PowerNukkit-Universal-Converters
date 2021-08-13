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

package org.powernukkit.converters.storage.api.leveldata

/**
 * @author joserobjr
 * @since 2020-10-20
 */
@Suppress("EnumEntryName")
enum class VanillaGameRule(
    val defaultValue: Int = 1,
    val defaultBedrockValue: Int = defaultValue,
    val isBoolean: Boolean = true,
    val inJava: Boolean = true,
    val inBedrock: Boolean = true,
    bedrockName: String? = null
) {
    announceAdvancements(inBedrock = false),
    commandBlocksEnabled(inJava = false),
    commandBlockOutput,
    disableElytraMovementCheck(defaultValue = 0, inBedrock = false),
    disableRaids(defaultValue = 0, inBedrock = false),
    doDaylightCycle,
    doEntityDrops,
    doFireTick,
    doInsomnia,
    doImmediateRespawn(defaultValue = 0),
    doLimitedCrafting(defaultValue = 0, inBedrock = false),
    doMobLoot,
    doMobSpawning,
    doPatrolSpawning(inBedrock = false),
    doTileDrops,
    doTraderSpawning(inBedrock = false),
    doWeatherCycle,
    drowningDamage,
    fallDamage,
    fireDamage,
    forgiveDeadPlayers(inBedrock = false),
    keepInventory(defaultValue = 0),
    logAdminCommands(inBedrock = false),
    maxCommandChainLength(defaultValue = 65536, defaultBedrockValue = 65535, isBoolean = false),
    maxEntityCramming(defaultValue = 24, isBoolean = false, inBedrock = false),
    mobGriefing,
    naturalRegeneration,
    pvp(inJava = false),
    randomTickSpeed(defaultValue = 3, defaultBedrockValue = 1, isBoolean = false),
    reducedDebugInfo(defaultValue = 0, inBedrock = false),
    sendCommandFeedback,
    showCoordinates(inJava = false),
    showDeathMessages,
    spawnRadius(defaultValue = 10, defaultBedrockValue = 5, isBoolean = false),
    spectatorsGenerateChunks(inBedrock = false),
    tntExplodes(inJava = false),
    universalAnger(defaultValue = 0, inBedrock = false),
    showTags(inJava = false),
    functionCommandLimit(defaultValue = 10_000, inJava = false, isBoolean = false),
    experimentalGameplay(defaultValue = 0, inJava = false),
    ;

    val bedrockName = bedrockName ?: name.toLowerCase()

    val defaultBedrockStringValue = if (isBoolean) {
        if (defaultBedrockValue == 1) "true" else "false"
    } else {
        defaultBedrockValue.toString()
    }

    val defaultJavaStringValue = if (isBoolean) {
        if (defaultValue == 1) "true" else "false"
    } else {
        defaultValue.toString()
    }

    companion object {
        private val byName = values().associateBy { it.bedrockName }
        fun withName(name: String) = byName[name]
    }
}
