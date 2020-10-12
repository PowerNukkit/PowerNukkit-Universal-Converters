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

package org.powernukkit.converters.universal.definitions.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import org.powernukkit.converters.internal.InitOnceDelegator
import org.powernukkit.converters.universal.definitions.model.block.entity.ModelBlockEntity
import org.powernukkit.converters.universal.definitions.model.block.property.ModelBlockProperty
import org.powernukkit.converters.universal.definitions.model.block.type.ModelBlockType

/**
 * @author joserobjr
 * @since 2020-10-12
 */
@JsonRootName("universal-blocks")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder("blockProperties", "blockEntities", "blockTypes")
class ModelDefinitions {
    @set:JacksonXmlElementWrapper(useWrapping = true, localName = "block-properties")
    @set:JacksonXmlProperty(localName = "block-property")
    var blockProperties: List<ModelBlockProperty> by InitOnceDelegator(emptyList())

    @set:JacksonXmlElementWrapper(useWrapping = true, localName = "block-entities")
    @set:JacksonXmlProperty(localName = "block-entity")
    var blockEntities: List<ModelBlockEntity> by InitOnceDelegator(emptyList())

    @set:JacksonXmlElementWrapper(useWrapping = true, localName = "block-types")
    @set:JacksonXmlProperty(localName = "block-type")
    var blockTypes: List<ModelBlockType> by InitOnceDelegator(emptyList())
    
    override fun toString(): String {
        return "ModelUniversalBlocks(blockProperties=$blockProperties, blockEntities=$blockEntities, blockTypes=)"
    }
}
