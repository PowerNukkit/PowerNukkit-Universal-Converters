/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 *  Copyright (C) 2020  José Roberto de Araújo Júnior
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
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.api

/**
 * @author joserobjr
 * @since 2020-10-13
 */
data class NamespacedId(
    val namespace: String,
    val id: String
) {
    private val string = "$namespace:$id"

    init {
        check(namespace.matches(Regex("^\\w+$"))) {
            "The namespace \"$namespace\" is invalid. $this"
        }
        check(id.matches(Regex("^\\w+$"))) {
            "The id \"$id\" is invalid. $this"
        }
    }

    private constructor(parts: List<String>) : this(
        parts.takeIf { it.size == 2 }?.first() ?: "minecraft",
        parts.last()
    )

    constructor(id: String) : this(
        requireNotNull(id.takeIf { it.isNotEmpty() }?.split(':', limit = 2)) {
            "Empty strings are not allowed"
        }
    )

    override fun toString() = string
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NamespacedId

        if (string != other.string) return false

        return true
    }

    override fun hashCode(): Int {
        return string.hashCode()
    }

}
