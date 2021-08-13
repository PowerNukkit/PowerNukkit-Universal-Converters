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

package org.powernukkit.converters.platform.universal.definitions.model.block.property

import com.fasterxml.jackson.annotation.JsonInclude
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlIDREF
import javax.xml.bind.annotation.XmlRootElement

/**
 * @author joserobjr
 * @since 2020-10-12
 */
@XmlRootElement(name = "copy-values")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
data class ModelCopyValues(
    @XmlAttribute
    @XmlIDREF
    val from: String
)
