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
inline fun <reified E: Enum<E>> enumSetOf(): EnumSet<E> = EnumSet.noneOf(E::class.java)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <E: Enum<E>> enumSetOf(single: E): EnumSet<E> = EnumSet.of(single)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <E: Enum<E>> enumSetOf(e1: E, e2: E): EnumSet<E> = EnumSet.of(e1, e2)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <E: Enum<E>> enumSetOf(e1: E, e2: E, e3: E): EnumSet<E> = EnumSet.of(e1, e2, e3)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <E: Enum<E>> enumSetOf(e1: E, e2: E, e3: E, e4: E): EnumSet<E> = EnumSet.of(e1, e2, e3, e4)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <E: Enum<E>> enumSetOf(e1: E, e2: E, e3: E, e4: E, e5: E): EnumSet<E> = EnumSet.of(e1, e2, e3, e4, e5)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
fun <E: Enum<E>> enumSetOf(first: E, vararg others: E): EnumSet<E> = EnumSet.of(first, *others)

/**
 * @author joserobjr
 * @since 2020-10-12
 */
inline fun <reified E: Enum<E>> enumSetOfNonNulls(vararg values: E?) = enumSetOf<E>().also { set -> 
    for (value in values) {
        value?.let(set::plusAssign)
    }
}

/**
 * @author joserobjr
 * @since 2020-10-12
 */
inline fun <reified E: Enum<E>> enumSetOfNonNullsOrEmpty(vararg values: E?): Set<E> {
    if (values.all { it == null }) {
        return emptySet()
    }
    
    val set = enumSetOf<E>()
    for (value in values) {
        value?.let(set::plusAssign)
    }
    return set
}
