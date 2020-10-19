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

package org.powernukkit.converters.conversion.adapter

/**
 * @author joserobjr
 * @since 2020-10-19
 */
fun <A> Adapters<A>?.addFirst(vararg adapters: A): Adapters<A> {
    return this?.copy(
        firstAdapters = adapters.toList() + firstAdapters
    ) ?: Adapters(firstAdapters = adapters.toList())
}

fun <A> Adapters<A>?.addLast(vararg adapters: A): Adapters<A> {
    return this?.copy(
        lastAdapters = lastAdapters + adapters.toList()
    ) ?: Adapters(lastAdapters = adapters.toList())
}

fun <A> Adapters<A>?.addFirstMid(vararg adapters: A): Adapters<A> {
    return this?.copy(
        midAdapters = adapters.toList() + midAdapters
    ) ?: Adapters(midAdapters = adapters.toList())
}
