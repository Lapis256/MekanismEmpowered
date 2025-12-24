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
import java.util.function.Supplier


class GuiSideInserterConfigurationTab<TILE>(
    gui: IGuiWrapper, tile: TILE, elementSupplier: Supplier<GuiSideInserterConfigurationTab<TILE>>
) : GuiWindowCreatorTab<TILE, GuiSideInserterConfigurationTab<TILE>>(
    MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "configuration.png"), gui, tile, gui.xSize, 62, 26, 18, false, elementSupplier
) where TILE : TileEntityMekanism, TILE : ISideConfiguration {
    companion object {
        private val WINDOW_DATA = SelectedWindowData(MekEmpWindowType.INSERTER)
    }

    init {
        setTooltip(MekEmpLang.INSERTER_CONFIG)
    }

    var latestWindow: GuiSideInserterConfiguration<TILE>? = null

    override fun createWindow(windowData: SelectedWindowData) =
        GuiSideInserterConfiguration<TILE>(gui(), (guiWidth - 156) / 2, 15, dataSource, windowData)
            .also { latestWindow = it }

    override fun onWindowClose() {
        super.onWindowClose()
        latestWindow = null
    }

    override fun getNextWindowData() = WINDOW_DATA

    override fun colorTab(guiGraphics: GuiGraphics) {
        MekanismRenderer.color(guiGraphics, SpecialColors.TAB_CONFIGURATION)
    }
}
