package org.powernukkit.converters.internal

import java.util.stream.Stream

/**
 * @author joserobjr
 * @since 2021-08-09
 */
internal fun <T: Any> Stream<T?>.filterNotNull(): Stream<T> {
    @Suppress("UNCHECKED_CAST")
    return filter { it != null } as Stream<T>
}
