package net.xalcon.analyzeio.common.init

import crazypants.enderio.ModObject
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.ShapedOreRecipe

object ModRecipes
{
    fun init()
    {
        GameRegistry.addRecipe(ShapedOreRecipe(ModBlocks.blockAnalyzer,
                "cCc", "sss", "sms",
                'C', ItemStack(ModObject.itemBasicCapacitor.item, 1, 3),
                'c', ItemStack(ModObject.itemBasicCapacitor.item, 1, 1),
                's', ItemStack(ModObject.itemAlloy.item, 1, 7),
                'm', ItemStack(ModObject.itemMachinePart.item, 1, 0)))
    }
}