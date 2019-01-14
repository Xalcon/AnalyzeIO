package net.xalcon.analyzeio;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.xalcon.analyzeio.client.GuiHandler;
import org.apache.logging.log4j.Logger;

@Mod(modid = AnalyzeIO.MODID, name = AnalyzeIO.NAME, version = AnalyzeIO.VERSION)
public class AnalyzeIO
{
    public static final String MODID = "analyzeio";
    public static final String NAME = "AnalyzeIO";
    public static final String VERSION = "0.4";

    private static Logger logger;
    private static AnalyzeIO instance;

    private AnalyzeIO() { }

    @Mod.InstanceFactory
    public static AnalyzeIO getInstance()
    {
        if(instance == null)
            instance = new AnalyzeIO();
        return instance;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public static void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(getInstance(), new GuiHandler());
    }
}
