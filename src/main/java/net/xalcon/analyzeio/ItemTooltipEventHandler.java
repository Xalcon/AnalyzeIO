package net.xalcon.analyzeio;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.capacitor.CapacitorKeyType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@Mod.EventBusSubscriber
@SideOnly(Side.CLIENT)
public class ItemTooltipEventHandler
{
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event)
	{
		if(event.getItemStack().getItem() != ModObject.itemBasicCapacitor.getItem()) return;
		NBTTagCompound itemNbt = event.getItemStack().getTagCompound();
		if(itemNbt == null) return;

		if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
		{
			event.getToolTip().add(I18n.format("analyzeio.lootcaps.usage"));
			return;
		}

		NBTTagCompound capNbt = itemNbt.getCompoundTag("eiocap");
		event.getToolTip().add(I18n.format("analyzeio.lootcaps.level", capNbt.getInteger("level")));
		for(String key : capNbt.getKeySet())
		{
			if("level".equals(key)) continue;
			if(key == null) continue;
			CapacitorKey capKey = getCapKey(key);
			float value = capNbt.getFloat(key);
			if(capKey == null)
			{
				CapacitorKeyType keyType = getCapKeyType(key);
				if(keyType != null)
					event.getToolTip().add(I18n.format("analyzeio.lootcaps.all." + keyType.getName(), (int)(value * 100)));
				else
					event.getToolTip().add("UNKNOWN: " + key + " ("+value+")");
				continue;
			}

			String ownerName = getLocalizedOwnerName(capKey.getOwner(), event.getItemStack());
			event.getToolTip().add(I18n.format("analyzeio.lootcaps." + capKey.getValueType().getName(), ownerName, (int)(value * 100)));
		}
	}

	private static String getLocalizedOwnerName(ModObject owner, ItemStack itemStack)
	{
		if(owner.getBlock() != null)
			return I18n.format(owner.getBlock().getUnlocalizedName() + ".name");
		if(owner.getItem() != null)
			return I18n.format(owner.getItem().getUnlocalizedName(itemStack) + ".name");
		return owner.getUnlocalisedName();
	}

	private static CapacitorKeyType getCapKeyType(String name)
	{
		return Arrays.stream(CapacitorKeyType.values()).filter(k -> k.getName().equals(name)).findFirst().orElse(null);
	}

	private static CapacitorKey getCapKey(String name)
	{
		return Arrays.stream(CapacitorKey.values()).filter(k -> k.getName().equals(name)).findFirst().orElse(null);
	}
}
