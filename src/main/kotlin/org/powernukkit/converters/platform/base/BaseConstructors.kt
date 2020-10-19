/*
 * PowerNukkit Universal Worlds & Converters for Minecraft
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.powernukkit.converters.platform.base

import org.powernukkit.converters.internal.InitOnceDelegator
import org.powernukkit.converters.math.BlockPos
import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.platform.api.block.PlatformStructure
import org.powernukkit.converters.platform.base.block.*
import org.powernukkit.converters.platform.base.entity.BaseEntity
import org.powernukkit.converters.platform.universal.block.*
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-17
 */
class BaseConstructors<P : BasePlatform<P>>(

    val blockState: (
        BaseConstructors<P>,
        BaseBlockType<P>, Map<String, BaseBlockPropertyValue<P>>
    ) -> BaseBlockState<P>,

    val blockProperty: (
        BaseConstructors<P>,
        id: String, UniversalBlockProperty
    ) -> BaseBlockProperty<P>,


    val blockEntityType: (
        BaseConstructors<P>,
        id: String, UniversalBlockEntityType,
        Map<String, BaseBlockEntityDataType<P>>
    ) -> BaseBlockEntityType<P>,


    val blockEntityDataType: (
        BaseConstructors<P>,
        UniversalBlockEntityDataType
    ) -> BaseBlockEntityDataType<P>,


    val blockType: (
        BaseConstructors<P>,
        NamespacedId, UniversalBlockType, ModelExtraBlock?
    ) -> BaseBlockType<P>,


    val blockSingleLayer: (
        BaseConstructors<P>,
        BaseBlockState<P>, BaseBlockEntity<P>?, List<BaseEntity<P>>
    ) -> BaseBlock<P>,


    val blockMultiLayer: (
        BaseConstructors<P>,
        List<BaseBlockState<P>>, BaseBlockEntity<P>?, List<BaseEntity<P>>
    ) -> BaseBlock<P>,


    val blockPropertyValueInt: (
        BaseConstructors<P>,
        Int, UniversalBlockPropertyValue, Boolean
    ) -> BaseBlockPropertyValue<P>,


    val blockPropertyValueString: (
        BaseConstructors<P>,
        String, UniversalBlockPropertyValue, Boolean
    ) -> BaseBlockPropertyValue<P>,


    val blockPropertyValueBoolean: (
        BaseConstructors<P>,
        Boolean, UniversalBlockPropertyValue, Boolean
    ) -> BaseBlockPropertyValue<P>,


    val structure: (
        BaseConstructors<P>,
        BlockPos, size: Int
    ) -> PlatformStructure<P>


) : PlatformObject<P> {

    override var platform: P by InitOnceDelegator(); private set

    @Suppress("UNCHECKED_CAST")
    internal fun assignPlatform(platform: BasePlatform<P>) {
        this.platform = platform as P
    }

    fun createBlockProperty(id: String, universal: UniversalBlockProperty) = blockProperty(this, id, universal)

    fun createBlockPropertyValue(universalValue: UniversalBlockPropertyValue): BaseBlockPropertyValue<P> {
        val value = universalValue.getEditionValue(platform.minecraftEdition)
        val int = value.toIntOrNull()
        if (int != null) {
            return createBlockPropertyValue(int, universalValue, universalValue.default)
        }
        if (value == "true" || value == "false") {
            return createBlockPropertyValue(value.toBoolean(), universalValue, universalValue.default)
        }
        return createBlockPropertyValue(value, universalValue, universalValue.default)
    }

    fun createBlockPropertyValueList(universal: UniversalBlockProperty): List<BaseBlockPropertyValue<P>> {
        return universal.values.map(this::createBlockPropertyValue)
    }

    fun createStructure(worldPos: BlockPos, size: Int) = structure(this, worldPos, size)

    fun createBlockState(
        blockType: BaseBlockType<P>,
        values: Map<String, BaseBlockPropertyValue<P>> = blockType.defaultPropertyValues()
    ) = blockState(this, blockType, values)

    fun createBlockEntityType(
        id: String,
        universal: UniversalBlockEntityType,
        values: Map<String, BaseBlockEntityDataType<P>>
    ) = blockEntityType(this, id, universal, values)

    fun createBlockEntityDataType(universal: UniversalBlockEntityDataType) =
        blockEntityDataType(this, universal)

    fun createBlockType(
        id: NamespacedId,
        universal: UniversalBlockType,
        extra: ModelExtraBlock? = null
    ) = blockType(this, id, universal, extra)

    fun createBlock(
        blockState: BaseBlockState<P>,
        blockEntity: BaseBlockEntity<P>? = null,
        entities: List<BaseEntity<P>> = emptyList()
    ) = blockSingleLayer(this, blockState, blockEntity, entities)

    fun createBlock(
        blockLayers: List<BaseBlockState<P>>,
        blockEntity: BaseBlockEntity<P>? = null,
        entities: List<BaseEntity<P>> = emptyList()
    ) = blockMultiLayer(this, blockLayers, blockEntity, entities)

    fun createBlockPropertyValue(
        int: Int,
        universalValue: UniversalBlockPropertyValue,
        default: Boolean,
    ) = blockPropertyValueInt(this, int, universalValue, default)

    fun createBlockPropertyValue(
        string: String,
        universalValue: UniversalBlockPropertyValue,
        default: Boolean,
    ) = blockPropertyValueString(this, string, universalValue, default)

    fun createBlockPropertyValue(
        boolean: Boolean,
        universalValue: UniversalBlockPropertyValue,
        default: Boolean,
    ) = blockPropertyValueBoolean(this, boolean, universalValue, default)
}
