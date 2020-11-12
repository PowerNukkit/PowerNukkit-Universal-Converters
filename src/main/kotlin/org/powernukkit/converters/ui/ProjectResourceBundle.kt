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

package org.powernukkit.converters.ui

import java.util.*

/**
 * @author joserobjr
 * @since 2020-11-12
 */
class ProjectResourceBundle(val parents: List<ResourceBundle>) : ResourceBundle() {
    constructor(vararg parents: ResourceBundle) : this(parents.toList())
    constructor(baseName: String, vararg parents: ResourceBundle) : this(getBundle(baseName), *parents)
    constructor(vararg baseNames: String) : this(baseNames.map(ResourceBundle::getBundle))

    override fun handleGetObject(key: String): String? {
        val bundle = parents.firstOrNull { it.containsKey(key) } ?: return null
        val rawValue = bundle.getString(key)
        if ('{' !in rawValue || '}' !in rawValue) {
            return rawValue
        }

        var last = rawValue
        while (true) {
            val current = KEY_PATTERN.findAll(last)
                .distinctBy { it.groupValues[1] }
                .fold(last) { current, match ->
                    val matchedKey = match.groupValues[1]
                    if (matchedKey == key || !containsKey(matchedKey)) {
                        current
                    } else {
                        val keyValue = getString(matchedKey)
                        current.replace(match.value, keyValue)
                    }
                }
            if (current == last) {
                break
            }
            last = current
        }
        return last
    }
    
    override fun getKeys(): Enumeration<String> {
        val iterator = parents.asSequence()
            .flatMap { it.keys.asSequence() }
            .distinct()
            .iterator()

        return object : Enumeration<String> {
            override fun hasMoreElements() = iterator.hasNext()
            override fun nextElement() = iterator.next()
        }
    }

    companion object {
        private val KEY_PATTERN = Regex("""\{([a-z]+(?:\.[a-z]+))}""")
    }
}
