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

package org.powernukkit.converters.platform.base.block

import org.powernukkit.converters.platform.api.NamespacedId
import org.powernukkit.converters.platform.api.PlatformObject
import org.powernukkit.converters.platform.base.BasePlatform
import org.powernukkit.converters.platform.base.entity.BaseEntity
import org.powernukkit.converters.platform.universal.block.*
import org.powernukkit.converters.platform.universal.definitions.model.block.type.ModelExtraBlock

/**
 * @author joserobjr
 * @since 2020-10-17
 */
class BaseConstructors<
        P : BasePlatform<
                P, BlockProperty, BlockEntityType, BlockType, BlockState,
                BlockPropertyValue, BlockEntityDataType, Block, Structure, BlockEntity, Entity
                >,
        BlockProperty : BaseBlockProperty<P, BlockPropertyValue>,
        BlockEntityType : BaseBlockEntityType<P, BlockEntityDataType>,
        BlockType : BaseBlockType<P, BlockProperty, BlockEntityType, BlockPropertyValue>,
        BlockState : BaseBlockState<P, BlockType, BlockProperty, BlockPropertyValue>,
        BlockPropertyValue : BaseBlockPropertyValue<P>,
        BlockEntityDataType : BaseBlockEntityDataType<P>,
        Block : BaseBlock<P, BlockState, BlockEntity, Entity>,
        Structure : BaseStructure<P, Block>,
        BlockEntity : BaseBlockEntity<P, BlockEntityType>,
        Entity : BaseEntity<P>
        >(

    override val platform: P,

    val blockState: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        BlockType, Map<String, BlockPropertyValue>
    ) -> BlockState,

    val blockProperty: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        id: String, UniversalBlockProperty
    ) -> BlockProperty,


    val blockEntityType: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        id: String, UniversalBlockEntityType,
        Map<String, BlockEntityDataType>
    ) -> BlockEntityType,


    val blockEntityDataType: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        UniversalBlockEntityDataType
    ) -> BlockEntityDataType,


    val blockType: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        NamespacedId, UniversalBlockType, ModelExtraBlock?
    ) -> BlockType,


    val blockSingleLayer: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        BlockState, BlockEntity?, List<Entity>
    ) -> Block,


    val blockMultiLayer: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        List<BlockState>, BlockEntity?, List<Entity>
    ) -> Block,


    val blockPropertyValueInt: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        Int, UniversalBlockPropertyValue, Boolean
    ) -> BlockPropertyValue,


    val blockPropertyValueString: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        String, UniversalBlockPropertyValue, Boolean
    ) -> BlockPropertyValue,


    val blockPropertyValueBoolean: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        Boolean, UniversalBlockPropertyValue, Boolean
    ) -> BlockPropertyValue,


    val structure: (
        BaseConstructors<
                P, BlockProperty, BlockEntityType, BlockType, BlockState, BlockPropertyValue,
                BlockEntityDataType, Block, Structure, BlockEntity, Entity,
                >,
        size: Int
    ) -> Structure


) : PlatformObject<P> {

    fun createBlockProperty(id: String, universal: UniversalBlockProperty) = blockProperty(this, id, universal)

    fun createBlockPropertyValue(universalValue: UniversalBlockPropertyValue): BlockPropertyValue {
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

    fun createBlockPropertyValueList(universal: UniversalBlockProperty): List<BlockPropertyValue> {
        return universal.values.map(this::createBlockPropertyValue)
    }

    fun createStructure(size: Int) = structure(this, size)

    fun createBlockState(
        blockType: BlockType,
        values: Map<String, BlockPropertyValue> = blockType.defaultPropertyValues()
    ) = blockState(this, blockType, values)

    fun createBlockEntityType(
        id: String,
        universal: UniversalBlockEntityType,
        values: Map<String, BlockEntityDataType>
    ) = blockEntityType(this, id, universal, values)

    fun createBlockEntityDataType(universal: UniversalBlockEntityDataType) =
        blockEntityDataType(this, universal)

    fun createBlockType(
        id: NamespacedId,
        universal: UniversalBlockType,
        extra: ModelExtraBlock? = null
    ) = blockType(this, id, universal, extra)

    fun createBlock(
        blockState: BlockState,
        blockEntity: BlockEntity? = null,
        entities: List<Entity> = emptyList()
    ) = blockSingleLayer(this, blockState, blockEntity, entities)

    fun createBlock(
        blockLayers: List<BlockState>,
        blockEntity: BlockEntity? = null,
        entities: List<Entity> = emptyList()
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
