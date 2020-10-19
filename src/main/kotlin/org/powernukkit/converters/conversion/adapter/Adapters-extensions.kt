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

import org.powernukkit.converters.internal.merge
import org.powernukkit.converters.internal.toMapOfList
import org.powernukkit.converters.platform.api.NamespacedId

/**
 * @author joserobjr
 * @since 2020-10-19
 */
fun <A> Adapters<A>?.addFirst(vararg adapters: A): Adapters<A> {
    return this?.copy(
        firstAdapters = adapters.toList() + firstAdapters
    ) ?: Adapters(firstAdapters = adapters.toList())
}

fun <A> Adapters<A>?.addToFirstList(vararg adapters: A): Adapters<A> {
    return this?.copy(
        firstAdapters = firstAdapters + adapters.toList()
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

fun <A> Adapters<A>?.addFromAdapters(vararg pairs: Pair<NamespacedId, A>): Adapters<A> {
    val toAdd = pairs.asSequence().toMapOfList()
    if (this == null) {
        return Adapters(fromAdapters = toAdd)
    }

    return this.copy(
        fromAdapters = fromAdapters merge toAdd
    )
}
