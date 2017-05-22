package net.xalcon.analyzeio.common.blocks

import crazypants.enderio.EnderIOTab
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.common.tileentity.TileEntityAnalyzer

class BlockAnalyzer : Block(Material.IRON), ITileEntityProvider
{
    init
    {
        this.setRegistryName(AnalyzeIO.MODID, "blockAnalyzer")
        this.unlocalizedName = this.registryName.toString().replace(':', '.')
        this.setHardness(2f)
        this.setCreativeTab(EnderIOTab.tabEnderIOMachines)
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileEntityAnalyzer()
}