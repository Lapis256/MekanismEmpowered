package dev.lapis256.mekanism_empowered.client.gui.element.button

import dev.lapis256.mekanism_empowered.common.network.to_server.configuration_update.PacketSideInserterData
import dev.lapis256.mekanism_empowered.common.tile.component.TileComponentInserterConfig
import dev.lapis256.mekanism_empowered.extension.inserterConfig
import mekanism.api.RelativeSide
import mekanism.api.text.EnumColor
import mekanism.api.text.TextComponentUtil
import mekanism.client.gui.GuiUtils
import mekanism.client.gui.IGuiWrapper
import mekanism.client.gui.element.GuiElement
import mekanism.client.gui.element.button.BasicColorButton
import mekanism.client.gui.tooltip.TooltipUtils
import mekanism.common.block.BlockBounding
import mekanism.common.network.PacketUtils
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.interfaces.ISideConfiguration
import mekanism.common.util.EnumUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult


class SideInserterButton<TILE>(
    gui: IGuiWrapper,
    x: Int,
    y: Int,
    private val tile: TILE,
    private val slotPos: RelativeSide,
) : BasicColorButton(
    gui, x, y, 22,
    { getColor(tile.inserterConfig, slotPos) }, ::onClick, ::onClick
) where TILE : TileEntityMekanism, TILE : ISideConfiguration {
    private fun shouldActivateByConfig(): Boolean {
        return EnumUtils.TRANSMISSION_TYPES
            .asSequence()
            .mapNotNull { tile.config.getConfig(it) }
            .any { it.isSideEnabled(slotPos) }
    }

    private fun shouldActivateByBounding(): Boolean {
        val level = tile.level ?: return false
        val side = slotPos.getDirection(tile.direction)
        val blockPos = tile.blockPos.relative(side)
        return BlockBounding.getMainBlockPos(level, blockPos) != tile.blockPos
    }

    init {
        this.active = shouldActivateByConfig() || shouldActivateByBounding()
    }

    val otherBlockItem: ItemStack by lazy {
        val level = tile.level ?: return@lazy ItemStack.EMPTY
        val side = slotPos.getDirection(tile.direction)
        val blockPos = tile.blockPos.relative(side)
        val blockState = level.getBlockState(blockPos)

        return@lazy if (blockState.isAir) {
            ItemStack.EMPTY
        } else {
            blockState.getCloneItemStack(
                BlockHitResult(
                    blockPos.center.relative(side.opposite, 0.5),
                    side.opposite,
                    blockPos,
                    false
                ),
                level,
                blockPos,
                Minecraft.getInstance().player ?: return@lazy ItemStack.EMPTY
            )
        }
    }

    private var lastInfo: List<Component> = emptyList()
    private var lastTooltip: Tooltip? = null

    companion object {
        private fun onClick(element: GuiElement, mouseX: Double, mouseY: Double): Boolean {
            val button = element as? SideInserterButton<*> ?: return false

            return PacketUtils.sendToServer(PacketSideInserterData(button.tile.blockPos, button.slotPos))
        }

        private fun getColor(config: TileComponentInserterConfig, slotPos: RelativeSide): EnumColor {
            return if (config.isSideEnabled(slotPos)) {
                EnumColor.RED
            } else {
                EnumColor.GRAY
            }
        }
    }

    override fun drawBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawBackground(guiGraphics, mouseX, mouseY, partialTicks)

        if (otherBlockItem.isEmpty) {
            return
        }

        GuiUtils.renderItem(guiGraphics, otherBlockItem, relativeX + 3, relativeY + 3, 1f, font(), null, true)
    }

    fun getStatusTooltip(): Component {
        return if (tile.inserterConfig.isSideEnabled(slotPos)) {
            TextComponentUtil.build(EnumColor.DARK_RED, "Enabled")
        } else {
            TextComponentUtil.build(EnumColor.DARK_GRAY, "Disabled")
        }
    }

    override fun updateTooltip(mouseX: Int, mouseY: Int) {
        val lines = buildList(3) {
            add(TextComponentUtil.build(slotPos))
            add(getStatusTooltip())
            if (!otherBlockItem.isEmpty) {
                add(otherBlockItem.hoverName)
            }
        }

        if (lines != lastInfo) {
            lastInfo = lines
            lastTooltip = TooltipUtils.create(lines)
        }

        tooltip = lastTooltip
    }
}
