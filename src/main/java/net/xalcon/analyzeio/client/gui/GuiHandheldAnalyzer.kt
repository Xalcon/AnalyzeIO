package net.xalcon.analyzeio.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.Slot
import net.minecraft.util.ResourceLocation
import net.xalcon.analyzeio.AnalyzeIO
import net.xalcon.analyzeio.common.container.ContainerHandheldAnalyzer

class GuiHandheldAnalyzer(val container: ContainerHandheldAnalyzer) : GuiContainer(container)
{
    companion object
    {
        val PLAYER_INVENTORY_WIDTH = 18 * 9
        val PLAYER_INVENTORY_HEIGHT = 18 * 4 + 4
        val GUI_BORDER_WIDTH = 7
        val GUI_TEXTURE : ResourceLocation = ResourceLocation(AnalyzeIO.MODID, "textures/gui/gui_base.png")
    }

    init
    {
        this.xSize = PLAYER_INVENTORY_WIDTH + 2 * GUI_BORDER_WIDTH;
        this.ySize = GUI_BORDER_WIDTH * 2 + PLAYER_INVENTORY_HEIGHT + this.container.getContentHeight()
    }

    fun renderItemSlot(slot: Slot)
    {
        this.drawTexturedModalRect(slot.xPos - 1, slot.yPos - 1, 176, 0, 18, 18)
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
}