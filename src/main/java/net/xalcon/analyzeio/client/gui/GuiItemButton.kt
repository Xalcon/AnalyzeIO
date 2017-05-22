package net.xalcon.analyzeio.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.config.GuiUtils
import net.xalcon.analyzeio.AnalyzeIO

class GuiItemButton(buttonId:Int, x:Int, y:Int, val item:ItemStack, val identifier:String,
                    val parent: GuiScreen,
                    val onButtonClicked: (button:GuiItemButton) -> Unit,
                    val isButtonSpecial: (button:GuiItemButton) -> Boolean)
    : GuiButton(buttonId, x, y, 18, 18, "")
{
    val GUI_TEXTURE : ResourceLocation = ResourceLocation(AnalyzeIO.MODID, "textures/gui/gui_base.png")
    var isActive:Boolean = false

    init
    {
    }

    override fun mousePressed(mc: Minecraft?, mouseX: Int, mouseY: Int): Boolean
    {
        if(super.mousePressed(mc, mouseX, mouseY))
        {
            val wasActive = this.isActive
            this.onButtonClicked(this)
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
            mc.textureManager.bindTexture(GUI_TEXTURE)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height
            val i = if(this.hovered) 2 else if(this.isActive) 1 else if(this.isButtonSpecial(this)) 3 else 0

            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 176, 44 + i * 18, 18, 18)
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