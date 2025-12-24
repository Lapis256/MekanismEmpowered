package dev.lapis256.mekanism_empowered.client.gui.element.window

import dev.lapis256.mekanism_empowered.client.gui.element.button.SideInserterButton
import dev.lapis256.mekanism_empowered.common.MekEmpLang
import dev.lapis256.mekanism_empowered.common.inventory.container.MekEmpWindowType
import mekanism.api.RelativeSide
import mekanism.client.gui.IGuiWrapper
import mekanism.client.gui.element.window.GuiWindow
import mekanism.common.inventory.container.SelectedWindowData
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.interfaces.ISideConfiguration
import net.minecraft.client.gui.GuiGraphics
import java.util.*


class GuiSideInserterConfiguration<TILE>(
    gui: IGuiWrapper, x: Int, y: Int, private val tile: TILE, windowData: SelectedWindowData
) : GuiWindow(gui, x, y, 130, 99, windowData) where TILE : TileEntityMekanism, TILE : ISideConfiguration {

    private val sideConfigButtons = EnumMap<RelativeSide, SideInserterButton<TILE>>(RelativeSide::class.java)

    init {
        require(windowData.type == MekEmpWindowType.INSERTER) { "Side auto insert configs must have a inserter window type" }

        interactionStrategy = InteractionStrategy.ALL

        addSideDataButton(RelativeSide.BOTTOM, 54, 66)
        addSideDataButton(RelativeSide.TOP, 54, 20)
        addSideDataButton(RelativeSide.FRONT, 54, 43)
        addSideDataButton(RelativeSide.BACK, 31, 66)
        addSideDataButton(RelativeSide.LEFT, 31, 43)
        addSideDataButton(RelativeSide.RIGHT, 77, 43)
    }

    private fun addSideDataButton(side: RelativeSide, xPos: Int, yPos: Int) {
        sideConfigButtons[side] = addChild(
            SideInserterButton(
                gui(),
                relativeX + xPos,
                relativeY + yPos,
                tile,
                side
            )
        )
    }

    override fun getTitlePadEnd() = super.titlePadEnd + 18

    override fun renderForeground(guiGraphics: GuiGraphics?, mouseX: Int, mouseY: Int) {
        drawTitleText(guiGraphics, MekEmpLang.INSERTER_CONFIG.translate(), 5)
    }
}
