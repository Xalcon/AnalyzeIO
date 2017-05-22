package net.xalcon.analyzeio.common.items

import crazypants.enderio.ModObject
import net.minecraft.entity.Entity
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
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.compat.isEmpty

class ItemHandheldAnalyzer : Item()
{
    init
    {
        this.setRegistryName("handheld_analyzer")
        this.unlocalizedName = this.registryName.toString()
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean)
    {
        val inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        if(inventory.getStackInSlot(0).isEmpty() || inventory.getStackInSlot(0).item != ModObject.itemBasicCapacitor.item) return
        val energy = stack.getCapability(CapabilityEnergy.ENERGY, null)
        if(energy is EnergyStorageAdv)
            energy.useEnegery(10)
    }

    override fun onItemRightClick(itemStackIn: ItemStack?, worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack>
    {
        playerIn.openGui(AnalyzeIO, 0, worldIn, playerIn.position.x, playerIn.position.y, playerIn.position.z)
        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn)
    }

    override fun showDurabilityBar(stack: ItemStack?): Boolean
    {
        return true
    }

    override fun getMaxDamage(stack: ItemStack): Int
    {
        return stack.getCapability(CapabilityEnergy.ENERGY, null).maxEnergyStored
    }

    override fun getDamage(stack: ItemStack): Int
    {
        return this.getMaxDamage(stack) - stack.getCapability(CapabilityEnergy.ENERGY, null).energyStored
    }

    override fun initCapabilities(stack: ItemStack?, nbt: NBTTagCompound?): ICapabilityProvider = object : ICapabilityProvider, ICapabilitySerializable<NBTTagCompound>
    {
        val inventory : ItemStackHandler = ItemStackHandler(1)
        val energy : EnergyStorageAdv = EnergyStorageAdv(32000, 128, 0)

        override fun deserializeNBT(nbt: NBTTagCompound)
        {
            if(nbt.hasKey("inventory"))
                this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
            if(nbt.hasKey("energy"))
                this.energy.deserializeNBT(nbt.getTag("energy"))
        }

        override fun serializeNBT(): NBTTagCompound
        {
            val tag:NBTTagCompound = NBTTagCompound()
            tag.setTag("inventory", this.inventory.serializeNBT())
            tag.setTag("energy", this.energy.serializeNBT())
            return tag
        }

        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T?
        {
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory)
            if(capability == CapabilityEnergy.ENERGY)
                return CapabilityEnergy.ENERGY.cast(this.energy)
            return null
        }

        override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean =
                capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
                capability == CapabilityEnergy.ENERGY
    }
}