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

package org.powernukkit.converters.universal.definitions

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.powernukkit.converters.universal.definitions.model.ModelDefinitions
import java.io.IOException
import java.io.InputStream

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class DefinitionLoader {
    private fun createMapper() = XmlMapper.builder()
        .addModule(KotlinModule())
        .addModule(JaxbAnnotationModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .enable(SerializationFeature.WRAP_ROOT_VALUE)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        .defaultUseWrapper(false)
        .serializationInclusion(JsonInclude.Include.NON_DEFAULT)
        .build()
    
    @Throws(IOException::class)
    private fun parseXml(input: InputStream): ModelDefinitions = createMapper().readValue(input)

    @Throws(IOException::class)
    fun loadXml(input: InputStream) = load(parseXml(input))
    
    fun loadString(input: String) = load(createMapper().readValue(input))

    fun loadBuiltin() = load(DefinitionLoader::class.java.getResourceAsStream("universal-blocks.xml").use(this::parseXml))
    
    fun load(model: ModelDefinitions) {
        model.blockProperties.first().bedrock
        println(model)
        print(createMapper().writeValueAsString(model))
    }
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            DefinitionLoader().loadBuiltin()
            if (true) return
            //language=XML
            DefinitionLoader().loadString("""<universal-blocks>
                |<block-properties>
                |   <block-property id="asd">
                |       <boolean/>
                |   </block-property>
                |</block-properties>
            </universal-blocks>""".trimMargin())
        }
    }
}
