package net.xalcon.analyzeio.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.xalcon.analyzeio.client.gui.GuiHandheldAnalyzer
import net.xalcon.analyzeio.client.gui.GuiMachineAnalyzer
import net.xalcon.analyzeio.common.container.ContainerHandheldAnalyzer
import net.xalcon.analyzeio.common.container.ContainerMachineAnalyzer
import net.xalcon.analyzeio.common.tileentity.TileEntityAnalyzer

object GuiHandler : IGuiHandler
{
    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any?
    {
        return when(ID)
        {
            0 -> ContainerHandheldAnalyzer(player)
            1 -> ContainerMachineAnalyzer(player, world.getTileEntity(BlockPos(x, y, z)) as? TileEntityAnalyzer ?: return null)
            else -> null
        }
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any?
    {
        return when (ID)
        {
            0 -> GuiHandheldAnalyzer(ContainerHandheldAnalyzer(player))
            1 -> GuiMachineAnalyzer(ContainerMachineAnalyzer(player, world.getTileEntity(BlockPos(x, y, z)) as? TileEntityAnalyzer ?: return null))
            else -> null
        }
    }
}