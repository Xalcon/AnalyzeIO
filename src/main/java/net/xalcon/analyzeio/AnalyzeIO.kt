package net.xalcon.analyzeio

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.xalcon.analyzeio.common.CommonProxy
import net.xalcon.analyzeio.common.GuiHandler
import net.xalcon.analyzeio.common.init.ModBlocks
import net.xalcon.analyzeio.common.init.ModItems
import net.xalcon.analyzeio.common.init.ModRecipes
import kotlin.jvm.JvmStatic

@Mod(
        modid = AnalyzeIO.MODID,
        name = AnalyzeIO.MODNAME,
        version = AnalyzeIO.VERSION,
        dependencies = AnalyzeIO.DEPENDENCIES,
        acceptedMinecraftVersions = AnalyzeIO.ACCEPTED_MC_VERSIONS
)
object AnalyzeIO
{
    const val MODID = "analyzeio"
    const val MODNAME = "AnalyzeIO"
    const val VERSION = "@VERSION@"
    const val DEPENDENCIES = "required-after:forgelin@[1.4.2,);required-after:EnderIO@[1.10.2-3.1.183,);"
    const val ACCEPTED_MC_VERSIONS = "[1.10.2]"
    const val CLIENT_PROXY = "net.xalcon.analyzeio.client.ClientProxy"
    const val SERVER_PROXY = "net.xalcon.analyzeio.common.CommonProxy"

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    lateinit var PROXY: CommonProxy

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent)
    {
        ModBlocks.init()
        ModItems.init()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent)
    {
        PROXY.init(event)
        ModRecipes.init()
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler)
    }

    @JvmStatic
    @Mod.InstanceFactory
    fun createModInstance() = this
}