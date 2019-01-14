package net.xalcon.analyzeio.common.item;

import crazypants.enderio.base.EnderIOTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.xalcon.analyzeio.AnalyzeIO;
import net.xalcon.analyzeio.common.container.AnalyzerCapabilityProvider;
import net.xalcon.analyzeio.common.init.ModItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemHandheldAnalyzer extends Item
{
    public static final String INTERNAL_NAME = "handheld_analyzer";

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new AnalyzerCapabilityProvider();
    }

    public ItemHandheldAnalyzer()
    {
        this.setRegistryName(AnalyzeIO.MODID, INTERNAL_NAME);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(EnderIOTab.tabEnderIOItems);
        this.setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        playerIn.openGui(AnalyzeIO.getInstance(), 1, worldIn, EnumHand.MAIN_HAND.ordinal(), 0, 0);
        return new ActionResult<>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }
}
