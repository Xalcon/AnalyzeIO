package net.xalcon.analyzeio.client.gui.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fml.client.config.GuiUtils
import net.xalcon.analyzeio.AnalyzeIO
import org.lwjgl.util.Rectangle


class WidgetPowerGauge(posX: Int, posY: Int, energyStorageGetter: () -> IEnergyStorage, color: Int, parent: GuiScreen) : Widget(parent)
{
    private val WIDGET_TEXTURE = ResourceLocation(AnalyzeIO.MODID, "textures/gui/power_gauge.png")
    private val POWER_GAUGE_TOOLTIP_KEY = AnalyzeIO.MODID + ".machine.tooltip.power_gauge"
    private val energyGetter: () -> IEnergyStorage = energyStorageGetter
    private val powerBar: Rectangle = Rectangle(posX, posY, 42, 14)
    private val barColor = color

    override fun renderWidgetBackground()
    {
        Minecraft.getMinecraft().textureManager.bindTexture(WIDGET_TEXTURE)
        Gui.drawModalRectWithCustomSizedTexture(this.powerBar.x, this.powerBar.y, 14f, 0f, 42, 14, 64f, 64f)

        Minecraft.getMinecraft().textureManager.bindTexture(WIDGET_TEXTURE)

        val energy = this.energyGetter()

        val powerPercentage = energy.energyStored.toFloat() / energy.maxEnergyStored
        val powerBarProgressOffset = (powerPercentage * 40).toInt()

        drawGradientMultiply(this.powerBar.x + 1, this.powerBar.y + 1, 40, 12, this.zLevel, this.barColor, this.barColor shr 1)

        drawColoredRectMultiply(this.powerBar.x + powerBarProgressOffset + 1, this.powerBar.y + 1,
                                this.powerBar.x + 42 - 1, this.powerBar.y + 1 + 12,
                                0xFF555555.toInt())

        // drawRect doesn't reset the color, so we need to do it
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun renderWidgetForeground()
    {

    }

    override fun handleMouseOver(mouseX: Int, mouseY: Int)
    {
        if (this.powerBar.contains(mouseX, mouseY))
        {
            val energy = this.energyGetter()
            val tooltip = I18n.format(POWER_GAUGE_TOOLTIP_KEY, energy.energyStored, energy.maxEnergyStored)
            GuiUtils.drawHoveringText(arrayListOf(tooltip), mouseX, mouseY, this.parent.width, this.parent.height, -1, Minecraft.getMinecraft().fontRendererObj)
        }
    }

}