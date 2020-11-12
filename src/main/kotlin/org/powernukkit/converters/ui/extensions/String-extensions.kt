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

package org.powernukkit.converters.ui.extensions

import kotlin.math.max

/**
 * @author joserobjr
 * @since 2020-11-12
 */
val String.htmlEncoded: String
    get() = buildString(max(16, length)) {
        this@htmlEncoded.forEach { char ->
            when (char) {
                '"', '\'', '<', '>', '&' -> append("&#").append(char.toInt()).append(';')
                else ->
                    char.toInt().let { int ->
                        if (int > 127) append("&#").append(int).append(';')
                        else append(char)
                    }
            }
        }
    }

val String.noHtml get() = "<html>$htmlEncoded</html>"
val String.lineBreaks get() = replace("\n", "<br>")
val String.autoWrapping get() = "<html><p>$this</p></html>"
