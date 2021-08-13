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

package org.powernukkit.converters.conversion.adapter

import org.powernukkit.converters.platform.api.Platform

/**
 * @author joserobjr
 * @since 2020-10-19
 */
@JvmName("plusAB")
operator fun <A : Platform<A>, B : Platform<B>> FromToAdapter<A, B>.plus(adapters: PlatformAdapters<A, B>) =
    addToAtoB(adapters)

/**
 * @author joserobjr
 * @since 2020-10-19
 */
@JvmName("plusBA")
operator fun <A : Platform<A>, B : Platform<B>> FromToAdapter<A, B>.plus(adapters: PlatformAdapters<B, A>) =
    addToBtoA(adapters)

/**
 * @author joserobjr
 * @since 2020-10-19
 */
@JvmName("plusAB")
operator fun <A : Platform<A>, B : Platform<B>> PlatformAdapters<A, B>.plus(adapter: FromToAdapter<A, B>) =
    adapter + this

/**
 * @author joserobjr
 * @since 2020-10-19
 */
@JvmName("plusBA")
operator fun <A : Platform<A>, B : Platform<B>> PlatformAdapters<A, B>.plus(adapter: FromToAdapter<B, A>) =
    adapter + this
