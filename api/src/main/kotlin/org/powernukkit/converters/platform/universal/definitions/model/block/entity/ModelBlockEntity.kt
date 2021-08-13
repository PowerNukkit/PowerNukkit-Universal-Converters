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

package org.powernukkit.converters.platform.universal.definitions.model.block.entity

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import javax.xml.bind.annotation.XmlAttribute

/**
 * @author joserobjr
 * @since 2020-10-12
 */
@JsonRootName("block-entity")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
data class ModelBlockEntity(
    @JsonProperty(required = true)
    @XmlAttribute
    val id: String,

    @JsonProperty(required = true)
    @XmlAttribute
    val java: String,

    @JsonProperty(required = true)
    @XmlAttribute
    val bedrock: String,

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("data")
    val dataSpecification: List<ModelData> = emptyList(),
)
