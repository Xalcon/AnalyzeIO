package net.xalcon.analyzeio.common.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.capabilities.ICapabilitySerializable
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import net.xalcon.analyzeio.AnalyzeIO

class ItemHandheldAnalyzer : Item()
{
    init
    {
        this.setRegistryName("handheld_analyzer")
        this.unlocalizedName = this.registryName.toString()
    }

    override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack>
    {
        playerIn.openGui(AnalyzeIO, 0, worldIn, playerIn.position.x, playerIn.position.y, playerIn.position.z)
        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn)
    }

    override fun initCapabilities(stack: ItemStack?, nbt: NBTTagCompound?): ICapabilityProvider = object : ICapabilityProvider, ICapabilitySerializable<NBTTagCompound>
    {
        val inventory : ItemStackHandler = ItemStackHandler(1)

        override fun deserializeNBT(nbt: NBTTagCompound?)
        {
            this.inventory.deserializeNBT(nbt)
        }

        override fun serializeNBT(): NBTTagCompound
        {
            return this.inventory.serializeNBT()
        }

        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T?
        {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory)
            return null
        }

        override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean = capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    }
}