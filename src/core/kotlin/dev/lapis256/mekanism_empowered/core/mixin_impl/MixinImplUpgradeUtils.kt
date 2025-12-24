package dev.lapis256.mekanism_empowered.core.mixin_impl

import dev.lapis256.mekanism_empowered.core.common.upgrade.UpgradeInfoHandler
import mekanism.api.Upgrade
import mekanism.common.tile.interfaces.ITileUpgradable
import net.minecraft.network.chat.Component


fun modifyAdditionalUpgradeInfo(tile: Upgrade.IUpgradeInfoHandler, upgrade: Upgrade, original: List<Component>) =
    UpgradeInfoHandler.getInfo(tile as ITileUpgradable, upgrade) ?: original
