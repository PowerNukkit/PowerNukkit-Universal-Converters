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

package org.powernukkit.converters.platform.universal.definitions.model.block.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import javax.xml.bind.annotation.XmlAttribute

/**
 * @author joserobjr
 * @since 2020-10-12
 */
@JsonRootName("data")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
data class ModelData (
    @JsonProperty(required = true)
    @XmlAttribute
    val name: String,
    
    @JsonProperty(required = true)
    @XmlAttribute
    val type: Type,
    
    @XmlAttribute
    val optional: Boolean = false,
    
    @XmlAttribute
    val default: String? = null,

    @XmlAttribute(name = "java-name")
    val javaName: String? = null,

    @XmlAttribute(name = "bedrock-name")
    val bedrockName: String? = null,

    @XmlAttribute(name = "java-optional")
    val javaOptional: Boolean? = null,

    @XmlAttribute(name = "bedrock-optional")
    val bedrockOptional: Boolean? = null,

    @XmlAttribute(name = "java-type")
    val javaType: Type? = null,

    @XmlAttribute(name = "bedrock-type")
    val bedrockType: Type? = null,

    @XmlAttribute(name = "java-requires-adapter")
    val javaRequiresAdapter: Boolean = false,
    
    @XmlAttribute(name = "bedrock-requires-adapter")
    val bedrockRequiresAdapter: Boolean = false,
) {
    enum class Type {
        INT,
        LONG,
        STRING,
        TEXT,
        BOOLEAN,
        BLOCK_ID,
        STATUS_EFFECT_ID,
        ITEM_STACK,
        DYE_COLOR,
        ITEM_LIST,
        LOOT_TABLE,
        FINDABLE,
        INT_COORDINATE,
        BEEHIVE_OCCUPANTS,
        BANNER_PATTERNS,
        NONE,
        JSON_TEXT,
        I18N_TEXT,
        INT_LIST;
        
        override fun toString(): String {
            return name.toLowerCase()
        }
    }
}
