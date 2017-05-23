package net.xalcon.analyzeio;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.util.Arrays;

//@Mod.EventBusSubscriber(Side.CLIENT)
public class ItemTooltipEventHandler
{
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event)
	{
		if(event.getItemStack().getItem() != ModObject.itemBasicCapacitor.getItem()) return;
		NBTTagCompound itemNbt = event.getItemStack().getTagCompound();
		if(itemNbt == null || AnalyzeIOConfig.tooltip == 0) return;

		if(!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
		{
			event.getToolTip().add(I18n.format("analyzeio.lootcaps.usage"));
			return;
		}

		if(AnalyzeIOConfig.tooltip == 2 && !isAnalyzed(itemNbt))
		{
			event.getToolTip().add(I18n.format("analyzeio.lootcaps.not_analyzed"));
			return;
		}

		NBTTagCompound capNbt = itemNbt.getCompoundTag("eiocap");
		int level = capNbt.getInteger("level");
		event.getToolTip().add(I18n.format("analyzeio.lootcaps.level", level > 0 ? "§a"+level+"§r" : "§c"+level+"§r"));
		ICapacitorData data = CapacitorHelper.getCapacitorDataFromItemStack(event.getItemStack());
		for(String key : capNbt.getKeySet())
		{
			if("level".equals(key)) continue;
			if(key == null) continue;
			CapacitorKey capKey = getCapKey(key);

			if(capKey == null)
			{
				CapacitorKeyType keyType = getCapKeyType(key);
				if(keyType == null) continue;
				Scaler scaler = guessScalerFromCapKeyType(keyType);
				if(scaler == null) continue;
				float value = scaler.scaleValue(capNbt.getFloat(key));
				event.getToolTip().add(I18n.format("analyzeio.lootcaps.all." + keyType.getName(), formatBonus(keyType, value)));
			}
			else
			{
				Scaler scaler = getScalerFromCapKey(capKey);
				float value = Float.NaN;
				if(scaler != null)
					value = scaler.scaleValue(data.getUnscaledValue(capKey));
				String ownerName = getLocalizedOwnerName(capKey.getOwner(), event.getItemStack());
				event.getToolTip().add(I18n.format("analyzeio.lootcaps." + capKey.getValueType().getName(), ownerName, formatBonus(capKey.getValueType(), value)));
			}


		}
		if(level <= 0)
		{
			event.getToolTip().add(I18n.format("analyzeio.lootcaps.bonus_inactive"));
		}
	}

	private static boolean isAnalyzed(NBTTagCompound itemNbt)
	{
		return itemNbt.getCompoundTag("eiocap").getBoolean("analyzed");
	}

	static Field scalerFieldAccessor = null;

	private static String formatBonus(CapacitorKeyType keyType, float value)
	{
		if(Float.isNaN(value)) return "§cNaN§r";
		if(value < 0) return "§cERROR§r";
		switch(keyType)
		{
			case ENERGY_BUFFER:
			case ENERGY_INTAKE:
			case SPEED:
			case AREA:
			case AMOUNT:
				return value > 1.0f ? "§a+"+((int)(value * 100))+"%§r" : "§c-"+((int)((1 - value) * 100))+"%§r";
			case ENERGY_USE:
				return value < 1.0f ? "§a-"+((int)((1 - value) * 100))+"%§r" : "§c+"+((int)(value * 100))+"%§r";
		}
		return "+" + value + "%";
	}

	private static Scaler getScalerFromCapKey(CapacitorKey key)
	{
		try
		{
			if(scalerFieldAccessor == null)
			{
				scalerFieldAccessor = CapacitorKey.class.getDeclaredField("scaler");
				scalerFieldAccessor.setAccessible(true);
			}
			return (Scaler) scalerFieldAccessor.get(key);
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static Scaler guessScalerFromCapKeyType(CapacitorKeyType keyType)
	{
		switch(keyType)
		{
			case ENERGY_BUFFER:
				return Scaler.Factory.POWER;
			case ENERGY_INTAKE:
				return Scaler.Factory.POWER;
			case ENERGY_USE:
				return Scaler.Factory.POWER;
			case SPEED:
				return null;
			case AREA:
				return null;
			case AMOUNT:
				return null;
		}
		return null;
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
