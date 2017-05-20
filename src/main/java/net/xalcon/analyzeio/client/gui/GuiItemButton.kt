package net.xalcon.analyzeio.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.config.GuiUtils

class GuiItemButton(buttonId:Int, x:Int, y:Int, val item:ItemStack, val identifier:String, val parent: GuiHandheldAnalyzer) : GuiButton(buttonId, x, y, 18, 18, "")
{
    var isActive:Boolean = false

    init
    {
    }

    override fun mousePressed(mc: Minecraft?, mouseX: Int, mouseY: Int): Boolean
    {
        if(super.mousePressed(mc, mouseX, mouseY))
        {
            val wasActive = this.isActive
            parent.resetActiveButtons()
            if(!wasActive)
                this.isActive = true
            return true
        }
        return false
    }

    override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int)
    {
        if (this.visible)
        {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            mc.textureManager.bindTexture(BUTTON_TEXTURES)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height
            val i = if(this.hovered) 2 else if(this.isActive) 1 else 0

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + i * 20, this.width / 2, this.height)
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height)
            RenderHelper.enableGUIStandardItemLighting()
            GlStateManager.enableDepth()
            mc.renderItem.zLevel = 1f
            mc.renderItem.renderItemAndEffectIntoGUI(item, this.xPosition + 1, this.yPosition + 1)

        }
    }

    fun drawToolTip(mc: Minecraft, mouseX: Int, mouseY: Int)
    {
        if(this.hovered)
        {
            GuiUtils.drawHoveringText(arrayListOf(I18n.format("${item.unlocalizedName}.name")), mouseX, 1 + mouseY, parent.width, parent.height, -1, mc.fontRendererObj)
        }
    }
}