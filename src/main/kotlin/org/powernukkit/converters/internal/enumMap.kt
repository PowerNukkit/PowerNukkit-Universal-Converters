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

package org.powernukkit.converters.internal

import java.util.*

/**
 * @author joserobjr
 * @since 2020-10-12
 */
inline fun <reified K: Enum<K>, V> enumMapOf() = EnumMap<K,V>(K::class.java)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <K: Enum<K>, V> enumMapOf(pair: Pair<K, V>): EnumMap<K, V> {
    val (k, v) = pair
    
    @Suppress("UNCHECKED_CAST")
    val `class`: Class<K> = k::class.java as Class<K>
    val enumMap = EnumMap<K, V>(`class`)
    
    enumMap[k] = v
    
    return enumMap
}

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <K: Enum<K>, V> enumMapOf(first: Pair<K, V>, vararg others: Pair<K, V>) = enumMapOf(first).also { it += others }

/**
 * @author joserobjr
 * @since 2020-10-12
 */
inline fun <reified K: Enum<K>, V> enumMapOfNonNulls(vararg pairs: Pair<K, V>?) = EnumMap<K,V>(K::class.java).also { map->
    for (pair in pairs) {
        pair?.let(map::plusAssign)
    }
}

/**
 * @author joserobjr
 * @since 2020-10-12
 */
inline fun <reified K: Enum<K>, V> enumMapOfNonNullsOrEmpty(vararg pairs: Pair<K, V>?): Map<K, V> {
    if (pairs.all { it == null }) {
        return emptyMap()
    }

    return enumMapOfNonNulls(*pairs)
}
