package net.xalcon.analyzeio.common.blocks

import crazypants.enderio.EnderIOTab
import net.minecraft.block.Block
import net.minecraft.block.BlockSlab
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.common.tileentity.TileEntityAnalyzer

class BlockAnalyzer : Block(Material.IRON), ITileEntityProvider
{
    val AABB:AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)

    init
    {
        this.setRegistryName(AnalyzeIO.MODID, "blockAnalyzer")
        this.unlocalizedName = this.registryName.toString().replace(':', '.')
        this.setHardness(2f)
        this.setCreativeTab(EnderIOTab.tabEnderIOMachines)
    }

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileEntityAnalyzer()
    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB = AABB
    override fun isFullBlock(state: IBlockState?): Boolean = false
    override fun isOpaqueCube(state: IBlockState?): Boolean = false
    override fun isFullCube(state: IBlockState?): Boolean = false

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, heldItem: ItemStack?, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean
    {
        playerIn.openGui(AnalyzeIO, 1, worldIn, pos.x, pos.y, pos.z)
        return true
    }
}