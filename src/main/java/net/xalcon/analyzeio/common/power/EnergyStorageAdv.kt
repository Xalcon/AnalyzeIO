package net.xalcon.analyzeio.common.power

import net.minecraft.nbt.NBTBase
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.energy.EnergyStorage

open class EnergyStorageAdv : EnergyStorage, INBTSerializable<NBTBase>
{
    constructor(capacity: Int) : super(capacity)
    constructor(capacity: Int, maxTransfer: Int) : super(capacity, maxTransfer)
    constructor(capacity: Int, maxReceive: Int, maxExtract: Int) : super(capacity, maxReceive, maxExtract)

    fun useEnegery(amount:Int)
    {
        val old = this.energy

        if(this.energy > amount)
            this.energy -= amount
        else
            this.energy = 0

        if(old != this.energy)
            this.onContentsChanged()
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int
    {
        val amount = super.extractEnergy(maxExtract, simulate)
        if(amount > 0)
            this.onContentsChanged()
        return amount
    }

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int
    {
        val amount = super.receiveEnergy(maxReceive, simulate)
        if(amount > 0)
            this.onContentsChanged()
        return amount
    }

    override fun deserializeNBT(nbt: NBTBase)
    {
        CapabilityEnergy.ENERGY.storage.readNBT(CapabilityEnergy.ENERGY, this, null, nbt)
    }

    override fun serializeNBT(): NBTBase
    {
        return CapabilityEnergy.ENERGY.storage.writeNBT(CapabilityEnergy.ENERGY, this, null)
    }

    open fun onContentsChanged() { }
}