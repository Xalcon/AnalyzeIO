package net.xalcon.analyzeio.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ContainerAnalyzer extends Container
{
    private final EntityPlayer player;

    private final IItemHandler analyzerInventory;

    public ContainerAnalyzer(EntityPlayer player, IItemHandler analyzerInventory)
    {
        this.player = player;
        this.addPlayerInventory();
        this.analyzerInventory = analyzerInventory;
        if(analyzerInventory != null)
            this.addAnalyzerSlots(analyzerInventory);
    }

    private void addAnalyzerSlots(IItemHandler itemHandler)
    {
        this.addSlotToContainer(new SlotAnalyzer(itemHandler, 0, -18, 7));
        this.addSlotToContainer(new SlotAnalyzer(itemHandler, 1, -18, 7 + 18));
    }

    private void addPlayerInventory()
    {
        int contentHeight = this.getContentHeight();
        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, k * 18 + contentHeight + 7));
            }
        }

        for (int l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 3 * 18 + contentHeight + 7 + 4));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return true;
    }

    public int getContentHeight()
    {
        return 140;
    }

    public ItemStack getAnalyzableItem()
    {
        if(this.analyzerInventory != null)
            return this.analyzerInventory.getStackInSlot(0);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index > 35)
            {
                if (!this.mergeItemStack(itemstack1, 0, 36, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 36, 38, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }
}
