package net.xalcon.analyzeio.common

import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry

open class CommonProxy
{
    open fun init(event: FMLInitializationEvent)
    {
    }

    open fun register(item:Item)
    {
        GameRegistry.register(item)
        item.creativeTab = CreativeTabs.MISC
    }

    open fun register(block:Block)
    {
        GameRegistry.register(block)
        GameRegistry.register(ItemBlock(block).setRegistryName(block.registryName))
    }
}