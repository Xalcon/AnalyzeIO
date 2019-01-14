package net.xalcon.analyzeio.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.xalcon.analyzeio.client.gui.GuiAnalyzer;
import net.xalcon.analyzeio.common.container.ContainerAnalyzer;
import net.xalcon.analyzeio.common.init.ModItems;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler
{
    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if(id == 1)
        {
            ItemStack itemStack = player.getHeldItem(EnumHand.values()[x]);
            if(itemStack.getItem() == ModItems.analyzer)
            {
                IItemHandler inv = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if(inv != null)
                    return new ContainerAnalyzer(player, inv);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if(id == 1)
        {
            ItemStack itemStack = player.getHeldItem(EnumHand.values()[x]);
            if(itemStack.getItem() == ModItems.analyzer)
            {
                IItemHandler inv = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if(inv != null)
                    return new GuiAnalyzer(new ContainerAnalyzer(player, inv));
            }
        }
        return null;
    }
}
