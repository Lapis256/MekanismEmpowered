package dev.lapis256.mekanism_empowered.common.tile.component

import dev.lapis256.mekanism_empowered.api.MekEmpSerializationConstants
import dev.lapis256.mekanism_empowered.common.tile.component.config.InserterConfigInfo
import dev.lapis256.mekanism_empowered.core.common.tile.component.IAdditionalTileComponent
import mekanism.api.NBTConstants
import mekanism.api.RelativeSide
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.component.ITileComponent
import mekanism.common.tile.interfaces.ISideConfiguration
import mekanism.common.util.EnumUtils
import mekanism.common.util.NBTUtils
import net.minecraft.nbt.CompoundTag


class TileComponentInserterConfig(val tile: TileEntityMekanism) : ITileComponent, IAdditionalTileComponent {
    val configInfo = InserterConfigInfo()

    init {
        tile.addComponent(this)
    }

    fun toggleSideConfig(relativeSide: RelativeSide) {
        configInfo.toggleSideConfig(relativeSide)
        tile.markForSave()
        tile.sendUpdatePacket()
    }

    fun isSideEnabled(relativeSide: RelativeSide) = configInfo.isSideEnabled(relativeSide)

    private fun readFromNBT(componentTag: CompoundTag) {
        val key = when {
            componentTag.contains(componentKey) -> componentKey
            componentTag.contains(NBTConstants.CONFIG) -> NBTConstants.CONFIG // TODO: Remove this after major version
            else -> return
        }
        componentTag
            .getByteArray(key)
            .forEachIndexed { i, byte -> configInfo.setSideConfig(RelativeSide.byIndex(i), byte == 1.toByte()) }
    }

    override fun readFromUpdateTag(updateTag: CompoundTag) {
        NBTUtils.setCompoundIfPresent(updateTag, componentKey, ::readFromNBT)
    }

    override fun read(componentTag: CompoundTag) {
        readFromNBT(componentTag)
    }

    private fun writeToNBT(componentTag: CompoundTag) {
        componentTag.putByteArray(
            componentKey,
            EnumUtils.SIDES.sortedBy(RelativeSide::ordinal).map { if (configInfo.isSideEnabled(it)) 1 else 0 }
        )
    }

    override fun write(componentTag: CompoundTag) {
        writeToNBT(componentTag)
    }

    override fun addToUpdateTag(updateTag: CompoundTag) {
        updateTag.put(componentKey, CompoundTag().also(::writeToNBT))
    }

    // IAdditionalTileComponent
    override val componentKey = MekEmpSerializationConstants.COMPONENT_INSERTER_CONFIG

    override fun loadComponentNBT(tile: TileEntityMekanism, dataMap: CompoundTag) {
        if (tile is ISideConfiguration) {
            read(dataMap)
        }
    }
}
