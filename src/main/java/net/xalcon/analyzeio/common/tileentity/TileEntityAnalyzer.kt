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

class TileEntityAnalyzer : TileEntity(), ITickable
{

    private val inventory:ItemStackHandler = ItemStackHandler(1)
    private val energy: EnergyStorage = EnergyStorage(32000, 512, 0)

    override fun update()
    {
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean =
            capability == ENERGY || capability == ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing)

    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T
    {
        if(capability == ENERGY) return ENERGY.cast(energy)
        if(capability == ITEM_HANDLER_CAPABILITY) return ITEM_HANDLER_CAPABILITY.cast(inventory)
        return super.getCapability(capability, facing)
    }

    //region storage | tile entity was markDirty() and needs to be saved to disk | load from disk
    override fun readFromNBT(compound: NBTTagCompound?)
    {
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound
    {
        return super.writeToNBT(compound)
    }
    //endregion

    //region Full update | i.e.: Chunk send to client
    override fun getUpdateTag(): NBTTagCompound
    {
        return super.getUpdateTag()
    }

    override fun handleUpdateTag(tag: NBTTagCompound?)
    {
        super.handleUpdateTag(tag)
    }
    //endregion

    //region Partial update | i.e. notifyBlockUpdate was called with flag 2
    override fun getUpdatePacket(): SPacketUpdateTileEntity?
    {
        return super.getUpdatePacket()
    }

    override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity?)
    {
        super.onDataPacket(net, pkt)
    }
    //endregion
}