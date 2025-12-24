package dev.lapis256.mekanism_empowered.mixin_impl

import dev.lapis256.mekanism_empowered.api.MekEmpUpgrade.AUTO_INSERTER
import dev.lapis256.mekanism_empowered.client.gui.element.tab.window.GuiSideInserterConfigurationTab
import dev.lapis256.mekanism_empowered.core.extension.isUpgradeInstalled
import mekanism.client.gui.IGuiWrapper
import mekanism.client.gui.element.GuiElement
import mekanism.common.inventory.container.tile.MekanismTileContainer
import mekanism.common.tile.base.TileEntityMekanism
import mekanism.common.tile.interfaces.ISideConfiguration
import java.util.function.Consumer


class MixinImplGuiConfigurableTile<TILE, CONTAINER>(
    screen: IGuiWrapper,
    private val tile: TILE,
    private val addRenderableWidget: (widget: GuiElement) -> GuiElement,
    private val removeWidget: Consumer<GuiElement>,
) where TILE : TileEntityMekanism, TILE : ISideConfiguration, CONTAINER : MekanismTileContainer<TILE> {

    private val sideConfigTab: GuiSideInserterConfigurationTab<TILE> by lazy {
        GuiSideInserterConfigurationTab(screen, tile) { sideConfigTab }
    }

    private var added = false

    private fun addSideConfigTab() {
        addRenderableWidget(sideConfigTab)
        added = true
    }

    private fun removeSideConfigTab() {
        sideConfigTab.latestWindow?.close()
        removeWidget.accept(sideConfigTab)
        added = false
    }

    fun containerTick() {
        if (!tile.supportsUpgrades()) {
            return
        }

        val installed = tile.isUpgradeInstalled(AUTO_INSERTER)
        if (!added && installed) {
            addSideConfigTab()
        } else if (added && !installed) {
            removeSideConfigTab()
        }
    }

    fun addGuiElements() {
        if (tile.supportsUpgrades() && tile.isUpgradeInstalled(AUTO_INSERTER)) {
            addSideConfigTab()
        }
    }
}
