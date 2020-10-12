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
import org.powernukkit.converters.universal.definitions.model.block.entity.ModelBlockEntity
import org.powernukkit.converters.universal.definitions.model.block.property.ModelBlockProperty
import org.powernukkit.converters.universal.definitions.model.block.property.ModelValue
import org.powernukkit.converters.universal.definitions.model.block.type.ModelBlockType
import java.io.IOException
import java.io.InputStream

/**
 * @author joserobjr
 * @since 2020-10-11
 */
class DefinitionLoader {
    private var blockPropertiesById = mapOf<String, ModelBlockProperty>()
    private var blockEntitiesById = mapOf<String, ModelBlockEntity>()
    private var blockTypesById = mapOf<String, ModelBlockType>()
    
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
    
    fun loadDefinitions(model: ModelDefinitions) {
        blockPropertiesById = model.blockProperties.associateBy(ModelBlockProperty::id)
        blockEntitiesById = model.blockEntities.associateBy(ModelBlockEntity::id)
        blockTypesById = model.blockTypes.associateBy(ModelBlockType::id)
    }
    
    fun load(model: ModelDefinitions) {
        loadDefinitions(model)
        processCopyValues()
    }
     
    fun processCopyValues() {
        val loopBreaker = mutableMapOf<String, Int>()

        val mutableBlockProperties = blockPropertiesById.toMutableMap()
        val copyValuesQueue = mutableBlockProperties.values.filterNotTo(ArrayDeque()) { it.copyValues.isNullOrEmpty() }
        
        queue@
        while (copyValuesQueue.isNotEmpty()) {
            val blockProperty = copyValuesQueue.removeFirst()
            
            val copyValues = blockProperty.copyValues!!.toMutableList()
            val valuesMap = blockProperty.values.orEmpty().associateByTo(mutableMapOf(), ModelValue::value)
            
            var hasDefault = valuesMap.values.any(ModelValue::default)

            fun addValue(source: ModelBlockProperty, value: ModelValue) {
                valuesMap[value.value]?.let { existing ->
                    check(existing.copy(default = false) == value.copy(default = false)) {
                        "A conflict has been detected while attempting to copy the value $value from ${source.id} to ${blockProperty.id}"
                    }
                    return
                }
                
                if (value.default) {
                    if (!hasDefault) {
                        hasDefault = true
                        valuesMap[value.value] = value
                    } else {
                        valuesMap[value.value] = value.copy(default = false)
                    }
                } else {
                    valuesMap[value.value] = value
                }
            }

            fun addValues(source: ModelBlockProperty, values: Iterable<ModelValue>) {
                values.forEach { value ->
                    addValue(source, value)
                }
            }
            
            val copyValuesIterator = copyValues.iterator()
            while (copyValuesIterator.hasNext()) {
                val copyValue = copyValuesIterator.next()
                val source = mutableBlockProperties[copyValue.from] ?: throw IllegalStateException(
                    "The block property ${blockProperty.id} attempted to copy values from the unknown property ${copyValue.from}"
                )
                
                if (!source.copyValues.isNullOrEmpty()) {
                    loopBreaker.compute(blockProperty.id) { id, attempts ->
                        val currentAttempt = (attempts ?: 0) + 1
                        check(currentAttempt <= 1000) {
                            "A loop has been detected while attempting to copy the property values from ${copyValue.from} to $id"
                        }
                        currentAttempt
                    }
                    copyValuesQueue.addLast(blockProperty.copy(copyValues = copyValues, values = valuesMap.values.toList()))
                    continue@queue
                }
                
                copyValuesIterator.remove()

                when {
                    source.values != null -> addValues(source, source.values)
                    source.booleanValue != null -> {
                        val bool = source.booleanValue
                        addValue(source, ModelValue("false", java = bool.javaFalse, bedrock = bool.bedrockFalse))
                        addValue(source, ModelValue("true", java = bool.javaTrue, bedrock = bool.bedrockTrue))
                    }
                    source.intRangeValue != null -> {
                        for(i in source.intRangeValue.toRange()) {
                            addValue(source, ModelValue(i.toString()))
                        }
                    }
                    else -> throw IllegalStateException("Attempted to copy values from ${source.id} to ${blockProperty.id}")
                }
            }
            
            check(copyValues.isEmpty()) {
                "All copy-values has been processed by the list has not been cleared."
            }
            
            mutableBlockProperties[blockProperty.id] = blockProperty.copy(copyValues = emptyList(), values = valuesMap.values.toList())
        }
        
        blockPropertiesById = mutableBlockProperties
    }
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            DefinitionLoader().loadBuiltin()
        }
    }
}
