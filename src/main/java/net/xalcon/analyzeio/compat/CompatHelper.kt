package net.xalcon.analyzeio.compat

import net.minecraft.item.ItemStack

fun ItemStack?.isEmpty():Boolean = this == null || this.stackSize <= 0