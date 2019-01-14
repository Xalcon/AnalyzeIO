package net.xalcon.analyzeio.client.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.xalcon.analyzeio.AnalyzeIO;
import org.lwjgl.util.Rectangle;

import java.util.ArrayList;

public class WidgetHorizontalIconBar<T> implements IWidget
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AnalyzeIO.MODID, "textures/gui/gui_base.png");

    private static class Entry<TState>
    {
        public String name;
        public ItemStack icon;
        public TState state;

        public Entry(String displayName, ItemStack icon, TState state)
        {
            this.name = displayName;
            this.icon = icon;
            this.state = state;
        }
    }

    private ArrayList<Entry<T>> list = new ArrayList<>();
    private Rectangle bounds;
    private Rectangle leftButton;
    private Rectangle rightButton;
    private int hoverIndex;
    private int activeIndex = 1;

    private int scrollOffset;
    private int scrollTargetOffset;
    private int maxScrollOffset;
    private IWidgetIconBarClickCallback<T> callback;

    public WidgetHorizontalIconBar(int x, int y, int maxLength, IWidgetIconBarClickCallback<T> callback)
    {
        this.setBounds(x, y, maxLength);
        this.callback = callback;
    }

    private void setBounds(int x, int y, int maxLength)
    {
        bounds = new Rectangle(x, y, maxLength, 18);
        this.leftButton = new Rectangle(x, y, 10, 18);
        this.rightButton = new Rectangle(x + maxLength - 10, y, 10, 18);
    }

    public void clear()
    {
        this.list.clear();
        this.maxScrollOffset = 0;
        this.scrollOffset = 0;
        this.scrollTargetOffset = 0;
        this.activeIndex = 0;
        this.hoverIndex = 0;
    }

    public void add(String displayName, ItemStack icon, T state)
    {
        this.list.add(new Entry<>(displayName, icon, state));
        this.maxScrollOffset = (this.list.size() * 17) - this.bounds.getWidth() + 19;
        if(this.maxScrollOffset < 0)
            this.maxScrollOffset = 0;
    }

    @Override
    public Rectangle getBounds()
    {
        return this.bounds;
    }

    @Override
    public void render()
    {
        RenderItem r = Minecraft.getMinecraft().getRenderItem();
        int i = 0;
        RenderHelper.enableGUIStandardItemLighting();
        for(Entry e : list)
        {
            int x = 9 + i * 17 + 1 + this.bounds.getX() - this.scrollOffset;
            int y = 1 + this.bounds.getY();
            Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
            if(this.activeIndex == i)
                Gui.drawModalRectWithCustomSizedTexture(x -1, y - 1, 176, 62 + 36, 18, 18, 256, 256);
            else if(hoverIndex == i)
                Gui.drawModalRectWithCustomSizedTexture(x -1, y - 1, 176, 62 + 18, 18, 18, 256, 256);
            else
                Gui.drawModalRectWithCustomSizedTexture(x -1, y - 1, 176, 62, 18, 18, 256, 256);
            r.renderItemAndEffectIntoGUI(e.icon, x, y);

            i++;
        }
        RenderHelper.disableStandardItemLighting();

        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
        GlStateManager.disableDepth();

        if(maxScrollOffset > 0)
        {
            if(hoverIndex == -1)
                Gui.drawModalRectWithCustomSizedTexture(this.leftButton.getX(), this.leftButton.getY(), 194, 62 + 18, 10, 18, 256, 256);
            else
                Gui.drawModalRectWithCustomSizedTexture(this.leftButton.getX(), this.leftButton.getY(), 194, 62, 10, 18, 256, 256);

            if(hoverIndex == -2)
                Gui.drawModalRectWithCustomSizedTexture(this.rightButton.getX(), this.rightButton.getY(), 204, 62 + 18, 10, 18, 256, 256);
            else
                Gui.drawModalRectWithCustomSizedTexture(this.rightButton.getX(), this.rightButton.getY(), 204, 62, 10, 18, 256, 256);
        }
        else
        {
            Gui.drawModalRectWithCustomSizedTexture(this.leftButton.getX(), this.leftButton.getY(), 194, 62 - 18, 10, 18, 256, 256);
            Gui.drawModalRectWithCustomSizedTexture(this.rightButton.getX(), this.rightButton.getY(), 204, 62 - 18, 10, 18, 256, 256);
        }

        GlStateManager.enableDepth();
    }

    @Override
    public void update(int relMouseX, int relMouseY, float partialTicks)
    {
        if(!this.bounds.contains(relMouseX, relMouseY))
            hoverIndex = -1000;
        else if(this.leftButton.contains(relMouseX, relMouseY))
            hoverIndex = -1;
        else if(this.rightButton.contains(relMouseX, relMouseY))
            hoverIndex = -2;
        else
        {
            this.hoverIndex = (relMouseX - this.bounds.getX() - this.leftButton.getWidth() + this.scrollOffset) / 17;
        }

        if(this.scrollTargetOffset != this.scrollOffset)
        {
            int delta = this.scrollOffset - this.scrollTargetOffset;
            if(Math.abs(delta) > 1)
                this.scrollOffset -= (delta / 2);
            else
                this.scrollOffset = this.scrollTargetOffset;
        }
    }

    @Override
    public void handleClick(int relMouseX, int relMouseY, int button)
    {
        if(this.hoverIndex >= 0 && this.hoverIndex < this.list.size())
        {
            if(activeIndex != hoverIndex)
            {
                activeIndex = hoverIndex;
                Entry<T> entry = this.list.get(this.activeIndex);
                this.callback.onClick(entry.state);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            return;
        }

        if(this.maxScrollOffset > 0)
        {
            if(this.hoverIndex == -1)
            {
                this.scrollTargetOffset -= 40;
                if(this.scrollTargetOffset < 0)
                    this.scrollTargetOffset = 0;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            else if(this.hoverIndex == -2)
            {
                this.scrollTargetOffset += 40;
                if(this.scrollTargetOffset >= this.maxScrollOffset)
                    this.scrollTargetOffset = this.maxScrollOffset;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
    }

    @Override
    public void handleScroll(int relMouseX, int relMouseY, int scrollValue)
    {
        if(this.maxScrollOffset <= 0 || !this.bounds.contains(relMouseX, relMouseY))
            return;
        if(scrollValue < 0)
        {
            this.scrollTargetOffset += 40;
            if(this.scrollTargetOffset >= this.maxScrollOffset)
                this.scrollTargetOffset = this.maxScrollOffset;
        }
        else if(scrollValue > 0)
        {
            this.scrollTargetOffset -= 40;
            if(this.scrollTargetOffset < 0)
                this.scrollTargetOffset = 0;
        }
    }

    @Override
    public String getHoverText()
    {
        if(hoverIndex >= 0 && hoverIndex < this.list.size())
        {
            return this.list.get(hoverIndex).name;
        }
        return null;
    }
}
