package net.xalcon.analyzeio.client.gui.widgets

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats

abstract class Widget(val parent: GuiScreen) : Gui()
{
    companion object
    {
        fun drawColoredRectMultiply(aLeft: Int, aTop: Int, aRight: Int, aBottom: Int, color: Int)
        {
            var left = aLeft
            var top = aTop
            var right = aRight
            var bottom = aBottom
            if (left < right)
            {
                val i = left
                left = right
                right = i
            }

            if (top < bottom)
            {
                val j = top
                top = bottom
                bottom = j
            }

            val f3 = (color shr 24 and 255).toFloat() / 255.0f
            val f = (color shr 16 and 255).toFloat() / 255.0f
            val f1 = (color shr 8 and 255).toFloat() / 255.0f
            val f2 = (color and 255).toFloat() / 255.0f
            val tessellator = Tessellator.getInstance()
            val vertexbuffer = tessellator.buffer
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ZERO,
                                                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.color(f, f1, f2, f3)
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION)
            vertexbuffer.pos(left.toDouble(), bottom.toDouble(), 0.0).endVertex()
            vertexbuffer.pos(right.toDouble(), bottom.toDouble(), 0.0).endVertex()
            vertexbuffer.pos(right.toDouble(), top.toDouble(), 0.0).endVertex()
            vertexbuffer.pos(left.toDouble(), top.toDouble(), 0.0).endVertex()
            tessellator.draw()
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
        }

        fun drawGradientMultiply(left: Int, top: Int, width: Int, height: Int, zLevel: Float, startColor: Int, endColor: Int)
        {
            val f = (startColor shr 24 and 255).toFloat() / 255.0f
            val f1 = (startColor shr 16 and 255).toFloat() / 255.0f
            val f2 = (startColor shr 8 and 255).toFloat() / 255.0f
            val f3 = (startColor and 255).toFloat() / 255.0f
            val f4 = (endColor shr 24 and 255).toFloat() / 255.0f
            val f5 = (endColor shr 16 and 255).toFloat() / 255.0f
            val f6 = (endColor shr 8 and 255).toFloat() / 255.0f
            val f7 = (endColor and 255).toFloat() / 255.0f
            GlStateManager.disableTexture2D()
            GlStateManager.enableBlend()
            GlStateManager.disableAlpha()
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.ZERO,
                                                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
            GlStateManager.shadeModel(7425)
            val tessellator = Tessellator.getInstance()
            val vertexbuffer = tessellator.buffer
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR)
            vertexbuffer.pos(left.toDouble() + width, top.toDouble(), zLevel.toDouble()).color(f1, f2, f3, f).endVertex()
            vertexbuffer.pos(left.toDouble(), top.toDouble(), zLevel.toDouble()).color(f1, f2, f3, f).endVertex()
            vertexbuffer.pos(left.toDouble(), top.toDouble() + height, zLevel.toDouble()).color(f5, f6, f7, f4).endVertex()
            vertexbuffer.pos(left.toDouble() + width, top.toDouble() + height, zLevel.toDouble()).color(f5, f6, f7, f4).endVertex()
            tessellator.draw()
            GlStateManager.shadeModel(7424)
            GlStateManager.disableBlend()
            GlStateManager.enableAlpha()
            GlStateManager.enableTexture2D()
        }
    }

    abstract fun renderWidgetBackground()
    abstract fun renderWidgetForeground()
    abstract fun handleMouseOver(mouseX: Int, mouseY: Int)
}