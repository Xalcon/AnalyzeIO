package net.xalcon.analyzeio.client.gui

import crazypants.enderio.ModObject
import crazypants.enderio.capacitor.CapacitorKey
import crazypants.enderio.capacitor.CapacitorKeyType
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.inventory.Slot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.items.CapabilityItemHandler
import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.common.container.ContainerHandheldAnalyzer
import net.xalcon.analyzeio.compat.isEmpty

class GuiHandheldAnalyzer(val container: ContainerHandheldAnalyzer) : GuiContainer(container)
{
    companion object
    {
        val PLAYER_INVENTORY_WIDTH = 18 * 9
        val PLAYER_INVENTORY_HEIGHT = 18 * 4 + 4
        val GUI_BORDER_WIDTH = 7
        val GUI_TEXTURE : ResourceLocation = ResourceLocation(AnalyzeIO.MODID, "textures/gui/gui_base.png")
    }

    val machineMap:Map<String, List<CapacitorKey>> = CapacitorKey.values().groupBy { k -> k.owner.unlocalisedName }.toMap()
    val defaultTypes:List<CapacitorKeyType> = arrayListOf(CapacitorKeyType.ENERGY_BUFFER, CapacitorKeyType.ENERGY_INTAKE, CapacitorKeyType.ENERGY_USE)

    init
    {
        this.xSize = PLAYER_INVENTORY_WIDTH + 2 * GUI_BORDER_WIDTH;
        this.ySize = GUI_BORDER_WIDTH * 2 + PLAYER_INVENTORY_HEIGHT + this.container.getContentHeight()
    }

    override fun initGui()
    {
        super.initGui()
        var id = 0
        val guiLeft = (this.width - this.xSize) / 2
        for((machine, entries) in CapacitorKey.values().groupBy { k -> k.owner })
        {
            if(machine.block == null) continue
            this.addButton(GuiItemButton(id, guiLeft + 7 + (id % 9) * 18, 112 + 18 * (id / 9), ItemStack(machine.block), machine.unlocalisedName, this))
            id++
        }
    }

    fun renderItemSlot(slot: Slot)
    {
        this.drawTexturedModalRect(slot.xPos - 1, slot.yPos - 1, 176, 0, 18, 18)
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int)
    {
        if(this.container.analyzer.isEmpty()) return
        val itemHandler = this.container.analyzer.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) ?: return
        val itemStack = itemHandler.getStackInSlot(0)
        if(itemStack.isEmpty() || itemStack.item != ModObject.itemBasicCapacitor.item) return

        val activeButton = this.buttonList.firstOrNull { b -> b is GuiItemButton && b.isActive } as? GuiItemButton
        val keys:List<CapacitorKeyType> = if(activeButton != null) this.machineMap[activeButton.identifier]?.map { c -> c.valueType } ?: this.defaultTypes else this.defaultTypes

        this.mc.renderItem.renderItemAndEffectIntoGUI(ItemStack(ModObject.itemBasicCapacitor.item, 1, 0), 104, 8)
        this.mc.renderItem.renderItemAndEffectIntoGUI(ItemStack(ModObject.itemBasicCapacitor.item, 1, 1), 122, 8)
        this.mc.renderItem.renderItemAndEffectIntoGUI(ItemStack(ModObject.itemBasicCapacitor.item, 1, 2), 140, 8)
        this.drawHorizontalLine(93, 165, 24, 0xFF606060.toInt())
        this.drawVerticalLine(93, 24, 98, 0xFF606060.toInt())
        this.drawVerticalLine(111, 24, 98, 0xFFA0A0A0.toInt())
        this.drawVerticalLine(129, 24, 98, 0xFFA0A0A0.toInt())
        this.drawVerticalLine(147, 24, 98, 0xFFA0A0A0.toInt())
        this.drawVerticalLine(165, 24, 98, 0xFF606060.toInt())
        this.drawHorizontalLine(93, 165, 98, 0xFF606060.toInt())

        this.fontRendererObj.drawString(I18n.format("analyzeio.capacitor.base_level"), 8, 28, 0x404040)
        var offset = 28

        for (type in CapacitorKeyType.values())
        {
            offset += 10
            val hasKey = keys.contains(type)
            this.fontRendererObj.drawString(I18n.format("analyzeio.capacitor.${type.getName()}"), 8, offset, if(hasKey) 0x404040 else 0xA0A0A0)
            if(hasKey)
            {
                Gui.drawRect(111, offset + 2, 111 + 20, offset + 6, 0xFFFF6666.toInt())
            }
        }

        val button = this.buttonList.firstOrNull { b -> b.isMouseOver }
        if(button is GuiItemButton)
            button.drawToolTip(this.mc, mouseX - guiLeft, mouseY - guiTop)
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
        GlStateManager.popMatrix()
    }

    fun resetActiveButtons()
    {
        this.buttonList.forEach { b -> if(b is GuiItemButton) b.isActive = false }
    }
}