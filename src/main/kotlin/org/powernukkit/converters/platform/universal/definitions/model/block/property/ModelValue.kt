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

package org.powernukkit.converters.platform.universal.definitions.model.block.property

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import org.powernukkit.converters.internal.InitOnceDelegator
import javax.xml.bind.annotation.XmlAttribute

/**
 * @author joserobjr
 * @since 2020-10-12
 */
@JsonRootName("value")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
class ModelValue (
    @XmlAttribute
    val default: Boolean = false,
    
    @XmlAttribute
    val java: String? = null,
    
    @XmlAttribute
    val bedrock: String? = null
) {
    @set:JacksonXmlText
    var value: String by InitOnceDelegator("")
    
    constructor(value: String, default: Boolean = false, java: String? = null, bedrock: String? = null): this(default, java, bedrock) {
        this.value = value
    }
    
    @JvmSynthetic operator fun component1() = value
    @JvmSynthetic operator fun component2() = default
    @JvmSynthetic operator fun component3() = java
    @JvmSynthetic operator fun component4() = bedrock
    
    fun copy(value: String = this.value, default: Boolean = this.default, java: String? = this.java, bedrock: String? = this.bedrock)
        = ModelValue(value, default, java, bedrock)
    
    
    override fun toString(): String {
        return "ModelBlockPropertyValueRaw(value='$value', default=$default, java='$java', bedrock='$bedrock')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelValue

        if (value != other.value) return false
        if (java != other.java) return false
        if (bedrock != other.bedrock) return false
        if (default != other.default) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + java.hashCode()
        result = 31 * result + bedrock.hashCode()
        result = 31 * result + default.hashCode()
        return result
    }
}
