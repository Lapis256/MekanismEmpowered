package dev.lapis256.mekanism_empowered.core.mixin_impl.ducks

import mekanism.common.content.blocktype.BlockType


/**
 * Duck interface for tracking whether this mod has already replaced a BlockType's upgrade support.
 */
@Suppress("FunctionName")
interface UpgradeSupportOverrideTracker {
    fun `mekanismEmpowered$hasUpgradeSupportOverride`(): Boolean

    fun `mekanismEmpowered$setUpgradeSupportOverride`(overridden: Boolean)
}

private val BlockType.upgradeSupportOverrideTracker get() = this as? UpgradeSupportOverrideTracker

var BlockType.hasUpgradeSupportOverride
    get() = upgradeSupportOverrideTracker?.`mekanismEmpowered$hasUpgradeSupportOverride`() ?: false
    set(value) {
        upgradeSupportOverrideTracker?.`mekanismEmpowered$setUpgradeSupportOverride`(value)
    }
