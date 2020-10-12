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

package org.powernukkit.converters.internal

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @author joserobjr
 * @since 2020-10-12
 */
class InitOnceDelegator<T>: ReadWriteProperty<Any, T> {
    private object EMPTY
    private val default: Any?
    private var value: Any?
    
    constructor(default: T) {
        this.default = default
        this.value = EMPTY
    }
    
    constructor() {
        this.default = EMPTY
        this.value = EMPTY
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return if (value == EMPTY) {
            if (default == EMPTY) {
                throw IllegalStateException("Value isn't initialized")
            } else {
                default as T
            }
        } else {
            value as T
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (this.value != EMPTY) {
            throw IllegalStateException("Value is initialized")
        }
        this.value = value
    }
}
