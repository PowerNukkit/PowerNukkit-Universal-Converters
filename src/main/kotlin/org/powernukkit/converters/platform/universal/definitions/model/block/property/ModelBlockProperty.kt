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

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import javax.xml.bind.annotation.XmlAttribute

/**
 * @author joserobjr
 * @since 2020-10-12
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonRootName("block-property")
data class ModelBlockProperty(
    @JsonProperty(required = true)
    @XmlAttribute
    val id: String,

    @XmlAttribute
    val java: String? = null,

    @XmlAttribute
    val bedrock: String? = null,

    @XmlAttribute(name = "java-requires-adapter")
    val javaRequiresAdapter: Boolean = false,

    @XmlAttribute(name = "bedrock-requires-adapter")
    val bedrockRequiresAdapter: Boolean = false,

    @JsonProperty("int-range")
    val intRangeValue: ModelIntRange? = null,

    @JsonProperty("boolean")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    val booleanValue: ModelBoolean? = null,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("value")
    val values: List<ModelValue>? = null,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("copy-values")
    val copyValues: List<ModelCopyValues>? = null
) {
    init {
        var validation = 0
        if (intRangeValue != null) {
            validation++
        }
        
        if (booleanValue != null) {
            validation++
        }
        
        if (!values.isNullOrEmpty() || !copyValues.isNullOrEmpty()) {
            validation++
        }
        
        check(validation <= 1) {
            "Cannot mix int-value, boolean-value and (value|copy-value) together in a property. Offending property: $id"
        }
    }
}
