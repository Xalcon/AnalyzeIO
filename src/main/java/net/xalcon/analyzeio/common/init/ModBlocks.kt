package net.xalcon.analyzeio.common.init

import net.minecraftforge.fml.common.registry.GameRegistry
import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.common.blocks.BlockAnalyzer
import net.xalcon.analyzeio.common.tileentity.TileEntityAnalyzer

object ModBlocks
{
    val blockAnalyzer:BlockAnalyzer = BlockAnalyzer()

    fun init()
    {
        AnalyzeIO.PROXY.register(this.blockAnalyzer)
        GameRegistry.registerTileEntity(TileEntityAnalyzer::class.java, this.blockAnalyzer.registryName.toString())
    }
}