package net.xalcon.analyzeio.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.xalcon.analyzeio.client.gui.GuiHandheldAnalyzer
import net.xalcon.analyzeio.common.container.ContainerHandheldAnalyzer

object GuiHandler : IGuiHandler
{
    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any?
    {
        return if(player != null) ContainerHandheldAnalyzer(player) else null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any?
    {
        return if(player != null) GuiHandheldAnalyzer(ContainerHandheldAnalyzer(player)) else null
    }
}