package net.xalcon.analyzeio.common.container;

import crazypants.enderio.api.capacitor.CapabilityCapacitorData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotAnalyzer extends SlotItemHandler
{
    public SlotAnalyzer(IItemHandler itemHandler, int index, int xPosition, int yPosition)
    {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return stack.hasCapability(CapabilityCapacitorData.INSTANCE, null);
    }

}
