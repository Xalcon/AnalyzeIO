package net.xalcon.analyzeio.common

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

open class CommonProxy
{
    open fun init(event: FMLInitializationEvent)
    {
    }

    open fun <T : Item> register(item:T):T
    {
        GameRegistry.register(item)
        item.creativeTab = CreativeTabs.MISC
        return item
    }
}