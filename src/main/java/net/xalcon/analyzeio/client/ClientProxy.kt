package net.xalcon.analyzeio.client

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.xalcon.analyzeio.common.CommonProxy

class ClientProxy : CommonProxy()
{
    override fun <T : Item> register(item: T): T
    {
        super.register(item)
        ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName, "inventory"))
        return item
    }
}