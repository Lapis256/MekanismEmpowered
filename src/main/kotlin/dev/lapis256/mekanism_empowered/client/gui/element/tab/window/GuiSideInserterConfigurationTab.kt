package dev.lapis256.mekanism_empowered.client.gui.element.tab.window

import dev.lapis256.mekanism_empowered.client.gui.element.window.GuiSideInserterConfiguration
import dev.lapis256.mekanism_empowered.common.MekEmpLang
import dev.lapis256.mekanism_empowered.common.inventory.container.MekEmpWindowType
import mekanism.client.SpecialColors
import mekanism.client.gui.IGuiWrapper
import mekanism.client.gui.element.tab.window.GuiWindowCreatorTab
import mekanism.client.render.MekanismRenderer
import mekanism.common.inventory.container.SelectedWindowData
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.interfaces.ISideConfiguration
import mekanism.common.util.MekanismUtils
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.narration.NarrationElementOutput
import java.util.function.Supplier


class GuiSideInserterConfigurationTab<TILE>(
    gui: IGuiWrapper, tile: TILE, elementSupplier: Supplier<GuiSideInserterConfigurationTab<TILE>>
) : GuiWindowCreatorTab<TILE, GuiSideInserterConfigurationTab<TILE>>(
    MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "configuration.png"), gui, tile, gui.width, 62, 26, 18, false, elementSupplier
) where TILE : TileEntityMekanism, TILE : ISideConfiguration {
    companion object {
        private val WINDOW_DATA = SelectedWindowData(MekEmpWindowType.INSERTER)
    }

    override fun renderToolTip(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.renderToolTip(guiGraphics, mouseX, mouseY)
        displayTooltips(guiGraphics, mouseX, mouseY, MekEmpLang.INSERTER_CONFIG.translate())
    }

    var latestWindow: GuiSideInserterConfiguration<TILE>? = null

    override fun createWindow() =
        GuiSideInserterConfiguration<TILE>(gui(), (guiWidth - 156) / 2, 15, dataSource, WINDOW_DATA)
            .also { latestWindow = it }

    override fun onWindowClose() {
        super.onWindowClose()
        latestWindow = null
    }

    override fun colorTab(guiGraphics: GuiGraphics) {
        MekanismRenderer.color(guiGraphics, SpecialColors.TAB_CONFIGURATION)
    }

    override fun renderWidget(p0: GuiGraphics, p1: Int, p2: Int, p3: Float) {
    }

    override fun updateWidgetNarration(p0: NarrationElementOutput) {
    }
}
