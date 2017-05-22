package net.xalcon.analyzeio.client

import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.xalcon.analyzeio.common.CommonProxy

class ClientProxy : CommonProxy()
{
    override fun register(item: Item)
    {
        super.register(item)
        ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName, "inventory"))
    }

    override fun register(block: Block)
    {
        super.register(block)
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, ModelResourceLocation(block.registryName, "inventory"))
    }
}