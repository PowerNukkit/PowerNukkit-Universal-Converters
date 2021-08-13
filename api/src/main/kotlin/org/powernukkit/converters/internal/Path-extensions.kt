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

package org.powernukkit.converters.internal

import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createTempDirectory
import kotlin.io.path.isDirectory
import kotlin.io.path.isSameFileAs

/**
 * @author joserobjr
 * @since 2021-08-09
 */
internal fun Path.isCaseSensitive(): Boolean {
    return !resolveSibling("a").isSameFileAs(resolveSibling("A"))
}

internal fun Path.isNotCaseSensitive(): Boolean = !isCaseSensitive()

/**
 * @author joserobjr
 * @since 2021-08-13
 */
@Throws(IOException::class)
internal fun Path.temporaryCopy(prefix: String? = null, directory: Path? = null): Path {
    if (isDirectory()) {
        val temp = createTempDirectory(directory, prefix)
        if (!toFile().copyRecursively(temp.toFile(), overwrite = true)) {
            throw IOException("The recursive copy from $this to $temp was terminated before completion!")
        }
        return temp
    }

    val temp = kotlin.io.path.createTempFile(directory, prefix)
    return copyTo(temp, overwrite = true)
}
