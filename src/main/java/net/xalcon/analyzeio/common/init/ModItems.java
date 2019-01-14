package net.xalcon.analyzeio.common.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.xalcon.analyzeio.AnalyzeIO;
import net.xalcon.analyzeio.common.item.ItemHandheldAnalyzer;

@Mod.EventBusSubscriber(modid = AnalyzeIO.MODID)
@GameRegistry.ObjectHolder(AnalyzeIO.MODID)
public class ModItems
{
    @GameRegistry.ObjectHolder(ItemHandheldAnalyzer.INTERNAL_NAME)
    public static ItemHandheldAnalyzer analyzer;

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemHandheldAnalyzer());
    }
}
