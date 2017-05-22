package net.xalcon.analyzeio.common.tileentity

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.energy.CapabilityEnergy.ENERGY
import net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.items.ItemStackHandler
import net.xalcon.analyzeio.common.power.EnergyStorageAdv
import net.xalcon.analyzeio.compat.isEmpty

class TileEntityAnalyzer : TileEntity(), ITickable
{
    private var itemInSlotTime:Int = 0

    private val inventory:ItemStackHandler = object : ItemStackHandler(1)
    {
        override fun onContentsChanged(slot: Int)
        {
            super.onContentsChanged(slot)
            val state = this@TileEntityAnalyzer.getWorld().getBlockState(this@TileEntityAnalyzer.getPos())
            this@TileEntityAnalyzer.getWorld().notifyBlockUpdate(this@TileEntityAnalyzer.getPos(), state, state, 3)
            this@TileEntityAnalyzer.markDirty()
        }
    }

    private val energy: EnergyStorageAdv = object : EnergyStorageAdv(32000, 512, 0)
    {
        override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int
        {
            if(!simulate)
            {
                val state = this@TileEntityAnalyzer.getWorld().getBlockState(this@TileEntityAnalyzer.getPos())
                this@TileEntityAnalyzer.getWorld().notifyBlockUpdate(this@TileEntityAnalyzer.getPos(), state, state, 3)
                this@TileEntityAnalyzer.markDirty()
            }
            return super.extractEnergy(maxExtract, simulate)
        }

        override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int
        {
            if(!simulate)
            {
                val state = this@TileEntityAnalyzer.getWorld().getBlockState(this@TileEntityAnalyzer.getPos())
                this@TileEntityAnalyzer.getWorld().notifyBlockUpdate(this@TileEntityAnalyzer.getPos(), state, state, 3)
                this@TileEntityAnalyzer.markDirty()
            }
            return super.receiveEnergy(maxReceive, simulate)
        }
    }


    override fun update()
    {
        if(!this.inventory.getStackInSlot(0).isEmpty())
        {
            this.itemInSlotTime++
            if(this.itemInSlotTime % 5 == 0)
            {
                this.energy.useEnegery(50)
            }
        }
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean =
            capability == ENERGY || capability == ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)

    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T
    {
        if(capability == ENERGY) return ENERGY.cast(energy)
        if(capability == ITEM_HANDLER_CAPABILITY) return ITEM_HANDLER_CAPABILITY.cast(inventory)
        return super.getCapability(capability, facing)
    }

    fun readSyncNbt(nbt:NBTTagCompound, isFullSync:Boolean)
    {
        this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        this.energy.deserializeNBT(nbt.getTag("energy"))
    }

    fun writeSyncNbt(nbt:NBTTagCompound, isFullSync:Boolean): NBTTagCompound
    {
        nbt.setTag("inventory", this.inventory.serializeNBT())
        nbt.setTag("energy", this.energy.serializeNBT())
        return nbt
    }

    //region storage | tile entity was markDirty() and needs to be saved to disk | load from disk
    override fun readFromNBT(compound: NBTTagCompound)
    {
        super.readFromNBT(compound)
        this.readSyncNbt(compound, true)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound
    {
        return writeSyncNbt(super.writeToNBT(compound), true)
    }
    //endregion

    //region Full update | i.e.: Chunk send to client
    override fun handleUpdateTag(tag: NBTTagCompound)
    {
        super.handleUpdateTag(tag)
        this.readSyncNbt(tag, true)
    }

    override fun getUpdateTag(): NBTTagCompound
    {
        return writeSyncNbt(super.getUpdateTag(), true)
    }
    //endregion

    //region Partial update | i.e. notifyBlockUpdate was called with flag 2
    override fun getUpdatePacket(): SPacketUpdateTileEntity?
    {
        return SPacketUpdateTileEntity(this.getPos(), 0, this.writeSyncNbt(NBTTagCompound(), false))
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity)
    {
        super.onDataPacket(net, pkt)
        this.readSyncNbt(pkt.nbtCompound, false)
    }
    //endregion
}