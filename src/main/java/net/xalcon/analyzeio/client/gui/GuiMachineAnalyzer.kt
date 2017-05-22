package net.xalcon.analyzeio.client.gui

import crazypants.enderio.ModObject
import crazypants.enderio.capacitor.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.items.CapabilityItemHandler
import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.client.gui.widgets.Widget
import net.xalcon.analyzeio.client.gui.widgets.WidgetPowerGauge
import net.xalcon.analyzeio.common.container.ContainerMachineAnalyzer
import net.xalcon.analyzeio.compat.isEmpty
import java.util.*

class GuiMachineAnalyzer(val container: ContainerMachineAnalyzer) : GuiContainer(container)
{
    companion object
    {
        val PLAYER_INVENTORY_WIDTH = 18 * 9
        val PLAYER_INVENTORY_HEIGHT = 18 * 4 + 4
        val GUI_BORDER_WIDTH = 7
        val GUI_TEXTURE : ResourceLocation = ResourceLocation(AnalyzeIO.MODID, "textures/gui/gui_base.png")
        val MACHINE_BLACKLIST = hashSetOf(
                ModObject.blockTransceiver,
                ModObject.blockBuffer,
                ModObject.blockWeatherObelisk,
                ModObject.blockPowerMonitor,
                ModObject.blockPowerMonitorv2,
                ModObject.blockInventoryPanelSensor)
    }

    // missing from the mappings: Relocator Obelisk, Inhibitor Obelisk
    val machineMap:Map<String, List<CapacitorKey>> = CapacitorKey.values().groupBy { k -> k.owner.unlocalisedName }
    var widgets: ArrayList<Widget> = ArrayList()

    init
    {
        this.xSize = PLAYER_INVENTORY_WIDTH + 2 * GUI_BORDER_WIDTH
        this.ySize = GUI_BORDER_WIDTH * 2 + PLAYER_INVENTORY_HEIGHT + this.container.getContentHeight()
        this.widgets.add(WidgetPowerGauge(32, 9, { this.container.tile.getCapability(CapabilityEnergy.ENERGY, null) }, 0xFF00FFFF.toInt(), this))
    }

    override fun initGui()
    {
        super.initGui()
        var id = 0
        val guiLeft = (this.width - this.xSize) / 2
        for((machine, entries) in CapacitorKey.values().filter{ k -> !MACHINE_BLACKLIST.contains(k.owner) }.groupBy { k -> k.owner })
        {
            if(machine.block == null) continue
            this.addButton(GuiItemButton(id, guiLeft + 7 + (id % 9) * 18, 112 + 18 * (id / 9), ItemStack(machine.block), machine.unlocalisedName, this, this::resetActiveButtons, this::isMachineAffected))
            id++
        }
    }

    fun renderItemSlot(slot: Slot)
    {
        this.drawTexturedModalRect(slot.xPos - 1, slot.yPos - 1, 176, 0, 18, 18)
    }

    fun isMachineAffected(button:GuiItemButton):Boolean
    {
        val itemHandler = this.container.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
        val itemStack = itemHandler.getStackInSlot(0)
        if(itemStack.isEmpty() || itemStack.item != ModObject.itemBasicCapacitor.item) return false

        val entries = this.machineMap[button.identifier]?.map { c -> c.getName() }?.toHashSet() ?: return false

        val tag = itemStack.tagCompound?.getCompoundTag("eiocap") ?: return false
        return tag.keySet.any { entries.contains(it) }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int)
    {

        val energy = this.container.tile.getCapability(CapabilityEnergy.ENERGY, null) ?: return
        if(energy.energyStored > 0)
        {
            renderAnalysis()
        }
        else
        {
            this.fontRendererObj.drawString(I18n.format("${AnalyzeIO.MODID}.analyzer.out_of_energy"), 8f, 28f, 0, true)
        }

        val relMouseX = mouseX - this.guiLeft
        val relMouseY = mouseY - this.guiTop
        this.widgets.forEach { w -> w.renderWidgetForeground() }
        this.widgets.forEach { w -> w.handleMouseOver(relMouseX, relMouseY) }

        val button = this.buttonList.firstOrNull { b -> b.isMouseOver }
        if(button is GuiItemButton)
            button.drawToolTip(this.mc, relMouseX, relMouseY)
    }

    private fun renderAnalysis()
    {
        val itemHandler = this.container.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ?: return
        val itemStack = itemHandler.getStackInSlot(0)
        if (itemStack.isEmpty() || itemStack.item != ModObject.itemBasicCapacitor.item) return

        val capData: ICapacitorData = CapacitorHelper.getCapacitorDataFromItemStack(itemStack)
        val activeButton = this.buttonList.firstOrNull { b -> b is GuiItemButton && b.isActive } as? GuiItemButton
        val keys: List<CapacitorKey>? = if (activeButton != null) this.machineMap[activeButton.identifier] else null

        this.mc.renderItem.renderItemAndEffectIntoGUI(ItemStack(ModObject.itemBasicCapacitor.item, 1, 0), 104, 8)
        this.mc.renderItem.renderItemAndEffectIntoGUI(ItemStack(ModObject.itemBasicCapacitor.item, 1, 1), 122, 8)
        this.mc.renderItem.renderItemAndEffectIntoGUI(ItemStack(ModObject.itemBasicCapacitor.item, 1, 2), 140, 8)
        this.drawHorizontalLine(93, 165, 24, 0xFF606060.toInt())
        this.drawVerticalLine(111, 24, 98, 0xFFA0A0A0.toInt())
        this.drawVerticalLine(129, 24, 98, 0xFFA0A0A0.toInt())
        this.drawVerticalLine(147, 24, 98, 0xFFA0A0A0.toInt())
        this.drawVerticalLine(165, 24, 98, 0xFF606060.toInt())
        this.drawHorizontalLine(93, 165, 98, 0xFF606060.toInt())

        this.fontRendererObj.drawString(I18n.format("analyzeio.capacitor.base_level"), 8, 28, 0x404040)
        var offset = 28
        val level: Int = itemStack.tagCompound?.getCompoundTag("eiocap")?.getInteger("level") ?: capData.baseLevel

        if (capData.baseLevel > 0)
        {
            Gui.drawRect(93, offset + 2, (93 + 18 * level), offset + 6, 0xFFCC4040.toInt())
        }

        for (type in CapacitorKeyType.values())
        {
            offset += 10

            var color: Int = 0xA0A0A0
            var barColor: Int = 0xFFA0A0A0.toInt()
            var f: Float = level.toFloat()

            if (keys != null)
            {
                val key = keys.lastOrNull { k -> k.valueType == type }
                if (key != null)
                {
                    val lv1 = DefaultCapacitorData.BASIC_CAPACITOR.getUnscaledValue(key)
                    val lv2 = DefaultCapacitorData.ACTIVATED_CAPACITOR.getUnscaledValue(key)
                    val lv3 = DefaultCapacitorData.ENDER_CAPACITOR.getUnscaledValue(key)
                    val lvC = capData.getUnscaledValue(key)
                    f = when
                    {
                        lvC > lv3 -> lvC / lv3 * 3
                        lvC > lv2 -> lvC / lv2 * 2
                        else -> lvC / lv1 * 1
                    }

                    color = 0x404040

                    if (level > 0)
                        barColor = 0xFFCC4040.toInt()
                }
            }
            Gui.drawRect(93, offset + 2, (93 + 18 * f).toInt(), offset + 6, barColor)
            this.fontRendererObj.drawString(I18n.format("analyzeio.capacitor.${type.getName()}"), 8, offset, color)
        }

        this.drawVerticalLine(93, 24, 98, 0xFF606060.toInt())
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)
        this.mc.textureManager.bindTexture(GUI_TEXTURE)
        val guiLeft = (this.width - this.xSize) / 2
        val guiTop = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, PLAYER_INVENTORY_WIDTH + GUI_BORDER_WIDTH * 2, container.getContentHeight() + PLAYER_INVENTORY_HEIGHT + GUI_BORDER_WIDTH)
        this.drawTexturedModalRect(guiLeft, guiTop + container.getContentHeight() + PLAYER_INVENTORY_HEIGHT + GUI_BORDER_WIDTH, 0, 256 - GUI_BORDER_WIDTH, PLAYER_INVENTORY_WIDTH + GUI_BORDER_WIDTH * 2, GUI_BORDER_WIDTH)
        GlStateManager.pushMatrix()
        GlStateManager.translate(this.guiLeft.toDouble(), this.guiTop.toDouble(), 0.0)
        this.container.inventorySlots.forEach({ s -> this.renderItemSlot(s) })
        this.widgets.forEach { w -> w.renderWidgetBackground() }
        GlStateManager.popMatrix()
    }

    fun resetActiveButtons(button:GuiItemButton)
    {
        this.buttonList.forEach { b -> if(b is GuiItemButton) b.isActive = false }
    }
}