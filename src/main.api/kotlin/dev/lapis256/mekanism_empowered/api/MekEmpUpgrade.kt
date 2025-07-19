package dev.lapis256.mekanism_empowered.api

import dev.lapis256.mekanism_empowered.api.text.MekEmpAPILang
import dev.lapis256.mekanism_empowered.core.api.upgrade.AdditionalUpgrade.register
import dev.lapis256.mekanism_empowered.core.api.upgrade.IAdditionalUpgrades
import mekanism.api.text.EnumColor


object MekEmpUpgrade : IAdditionalUpgrades {
    @JvmStatic
    val EMPOWERED_SPEED by register(
        "empowered_speed", MekEmpAPILang.UPGRADE_SPEED, MekEmpAPILang.UPGRADE_SPEED_DESCRIPTION, 8, EnumColor.AQUA
    )

    @JvmStatic
    val EMPOWERED_ENERGY by register(
        "empowered_energy", MekEmpAPILang.UPGRADE_ENERGY, MekEmpAPILang.UPGRADE_ENERGY_DESCRIPTION, 8, EnumColor.BRIGHT_PINK
    )

    @JvmStatic
    val FAST_ITEM_EJECT by register(
        "fast_item_eject", MekEmpAPILang.UPGRADE_FAST_ITEM_EJECT, MekEmpAPILang.UPGRADE_FAST_ITEM_EJECT_DESCRIPTION, 8, EnumColor.PURPLE
    )

    @JvmStatic
    val FAST_ITEM_INSERT by register(
        "fast_item_insert", MekEmpAPILang.UPGRADE_FAST_ITEM_INSERT, MekEmpAPILang.UPGRADE_FAST_ITEM_INSERT_DESCRIPTION, 8, EnumColor.BROWN
    )

    @JvmStatic
    val IO_CAPACITY by register(
        "io_capacity", MekEmpAPILang.UPGRADE_IO_CAPACITY, MekEmpAPILang.UPGRADE_IO_CAPACITY_DESCRIPTION, 8, EnumColor.PINK
    )

    @JvmStatic
    val AUTO_INSERTER by register(
        "auto_inserter", MekEmpAPILang.UPGRADE_AUTO_INSERTER, MekEmpAPILang.UPGRADE_AUTO_INSERTER_DESCRIPTION, 1, EnumColor.DARK_RED
    )

    @JvmStatic
    val TANK_CAPACITY by register(
        "tank_capacity", MekEmpAPILang.UPGRADE_TANK_CAPACITY, MekEmpAPILang.UPGRADE_TANK_CAPACITY_DESCRIPTION, 16, EnumColor.DARK_BLUE
    )

    @JvmStatic
    val ENERGY_CAPACITY by register(
        "energy_capacity", MekEmpAPILang.UPGRADE_ENERGY_CAPACITY, MekEmpAPILang.UPGRADE_ENERGY_CAPACITY_DESCRIPTION, 16, EnumColor.YELLOW
    )
}
