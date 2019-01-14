package net.xalcon.analyzeio.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.xalcon.analyzeio.AnalyzeIO;
import net.xalcon.analyzeio.common.init.ModItems;

@Mod.EventBusSubscriber(modid = AnalyzeIO.MODID)
@SideOnly(Side.CLIENT)
public class ClientRegistryEvents
{
    @SubscribeEvent
    public static void onRegisterModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(ModItems.analyzer, 0, new ModelResourceLocation(ModItems.analyzer.getRegistryName(), "inventory"));
    }
}
