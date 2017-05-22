package net.xalcon.analyzeio.common.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler
import net.xalcon.analyzeio.common.init.ModItems
import net.xalcon.analyzeio.common.tileentity.TileEntityAnalyzer
import net.xalcon.analyzeio.compat.isEmpty
import java.lang.UnsupportedOperationException

class ContainerMachineAnalyzer(val player:EntityPlayer, val tile:TileEntityAnalyzer) : Container()
{
    private val SLOT_SIZE = 18
    private val ACTION_BAR_Y_OFFSET = 58
    private val BORDER_OFFSET = 8

    val playerInvenory : InventoryPlayer = player.inventory

    fun getContentHeight() = 136

    init
    {
        val cap = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ?: throw UnsupportedOperationException("TileEntityAnalyzer is not providing an IItemHandler")
        this.bindPlayerInventory()
        this.addSlotToContainer(SlotItemHandler(cap, 0, 8, 8))
    }

    private fun bindPlayerInventory()
    {
        val offsetX = BORDER_OFFSET
        val offsetY = this.getContentHeight() + BORDER_OFFSET
        // bind action bar
        for (x in 0..8)
            this.addSlotToContainer(Slot(this.playerInvenory, x, offsetX + x * SLOT_SIZE, offsetY + ACTION_BAR_Y_OFFSET))

        // bind upper inventory
        for (y in 0..2)
            for (x in 0..8)
                this.addSlotToContainer(Slot(this.playerInvenory, x + y * 9 + 9, offsetX + x * SLOT_SIZE, offsetY + y * SLOT_SIZE))
    }

    override fun canInteractWith(playerIn: EntityPlayer?): Boolean
    {
        return true
    }
}