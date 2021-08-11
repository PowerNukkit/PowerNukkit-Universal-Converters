package org.powernukkit.converters.internal

import java.nio.file.Path
import kotlin.io.path.isSameFileAs

/**
 * @author joserobjr
 * @since 2021-08-09
 */
internal fun Path.isCaseSensitive(): Boolean {
    return !resolveSibling("a").isSameFileAs(resolveSibling("A"))
}

internal fun Path.isNotCaseSensitive(): Boolean = !isCaseSensitive()
