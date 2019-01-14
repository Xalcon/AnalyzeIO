package net.xalcon.analyzeio.client.gui;

import crazypants.enderio.api.capacitor.ICapacitorData;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.capacitor.CapacitorHelper;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.xalcon.analyzeio.AnalyzeIO;
import net.xalcon.analyzeio.client.gui.widgets.IWidget;
import net.xalcon.analyzeio.client.gui.widgets.WidgetHorizontalIconBar;
import net.xalcon.analyzeio.common.container.ContainerAnalyzer;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GuiAnalyzer extends GuiContainer
{
    private static final int PLAYER_INVENTORY_WIDTH = 18 * 9;
    private static final int PLAYER_INVENTORY_HEIGHT = 18 * 4 + 4;
    private static final int GUI_BORDER_WIDTH = 7;
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(AnalyzeIO.MODID, "textures/gui/gui_base.png");
    private final ContainerAnalyzer container;

    private List<IWidget> widgets = new ArrayList<>();
    private WidgetHorizontalIconBar<IconState> iconBar;

    private ItemStack cachedItemStack = ItemStack.EMPTY;

    private int scaleFactor;
    private Block showDetailsFor;

    public GuiAnalyzer(ContainerAnalyzer containerAnalyzer)
    {
        super(containerAnalyzer);
        this.container = containerAnalyzer;
        this.xSize = PLAYER_INVENTORY_WIDTH + 2 * GUI_BORDER_WIDTH;
        this.ySize = GUI_BORDER_WIDTH * 2 + PLAYER_INVENTORY_HEIGHT + this.container.getContentHeight();
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.widgets.clear();
        int len = 162;

        this.iconBar = new WidgetHorizontalIconBar<>(7, 7, len, this::onIconClick);
        this.widgets.add(iconBar);

        ScaledResolution scr = new ScaledResolution(this.mc);
        this.scaleFactor = scr.getScaleFactor();
    }

    private void renderItemSlot(Slot slot)
    {
        this.drawTexturedModalRect(slot.xPos - 1, slot.yPos - 1, 176, 0, 18, 18);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        int dWheelChange = Mouse.getDWheel();
        if(dWheelChange != 0)
        {
            int rmx = mouseX - guiLeft;
            int rmy = mouseY - guiTop;
            this.widgets.forEach(w -> w.handleScroll(rmx, rmy, dWheelChange));
        }

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        for(IWidget widget : this.widgets)
        {
            String text = widget.getHoverText();
            if(text == null) continue;
            this.drawHoveringText(text, mouseX, mouseY);
            return;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        ItemStack itemStack = this.container.getAnalyzableItem();
        ensureCurrent(itemStack);

        if(!itemStack.isEmpty())
        {
            this.renderDetails();
        }

        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        GlStateManager.color(1f, 1f, 1f, 1f);
        renderWidgets();
    }

    private void renderDetails()
    {
        if(this.showDetailsFor == null)
        {
            float level = CapacitorHelper.getCapLevelRaw(this.cachedItemStack);
            ITextComponent levelText = new TextComponentTranslation(AnalyzeIO.MODID + ".capacitor.level");
            this.drawString(this.fontRenderer, levelText.getFormattedText(), 10, 30, 0xFFFFFFFF);
            this.drawStringRight("" + Math.round(level * 100) / 100f, 170, 30, 0xFFFFFFFF);

            IForgeRegistry<ICapacitorKey> keys = GameRegistry.findRegistry(ICapacitorKey.class);
            if(keys == null) return;

            List<Pair<String, Float>> data = CapacitorHelper.getCapDataRaw(this.cachedItemStack);
            int i = 1;
            for(Pair<String, Float> pair : data)
            {
                Optional<ICapacitorKey> key = keys.getEntries().stream()
                        .filter(k -> k.getKey().toString().equals(pair.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst();

                if(key.isPresent())
                {
                    ITextComponent text = new TextComponentTranslation(key.get().getRegistryName().toString());
                    this.drawString(this.fontRenderer, text.getFormattedText(), 10, 30 + i * 11, 0xFFFFFFFF);
                    this.drawStringRight(FormatPct(pair.getValue()), 170, 30 + i * 11, 0xFFFFFFFF);
                }
                else
                {
                    ITextComponent text = new TextComponentTranslation(AnalyzeIO.MODID + ".capacitor.generic." + pair.getKey());
                    this.drawString(this.fontRenderer, text.getFormattedText(), 10, 30 + i * 11, 0xFFFFFFFF);
                    this.drawStringRight(FormatPct(pair.getValue()), 170, 30 + i * 11, 0xFFFFFFFF);
                }

                i++;
            }
        }
        else
        {

        }
    }

    private void renderWidgets()
    {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        for(IWidget widget : widgets)
        {
            Rectangle bounds = widget.getBounds();

            int left = ((this.guiLeft + bounds.getX()) * scaleFactor);
            int top = (height * scaleFactor) - ((this.guiTop + bounds.getY() + bounds.getHeight()) * scaleFactor);
            int width = (bounds.getWidth() * scaleFactor);
            int height = (bounds.getHeight() * scaleFactor);

            GL11.glScissor(left, top, width, height);
            widget.render();
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void ensureCurrent(ItemStack itemStack)
    {
        if(!ItemStack.areItemStackTagsEqual(itemStack, this.cachedItemStack))
        {
            this.cachedItemStack = itemStack.copy();
            this.iconBar.clear();

            if(!this.cachedItemStack.isEmpty())
            {
                this.iconBar.add("General", new ItemStack(Items.DIAMOND), new IconState(null));
                IForgeRegistry<ICapacitorKey> keys = GameRegistry.findRegistry(ICapacitorKey.class);
                if(keys != null)
                {
                    HashSet<Block> blocks = new HashSet<>();
                    for(ICapacitorKey key : keys)
                    {
                        Block owner = key.getOwner().getBlock();
                        if(owner != null)
                        {
                            if(blocks.contains(owner) || !owner.hasTileEntity())
                                continue;
                            blocks.add(owner);
                            this.iconBar.add(owner.getLocalizedName(), new ItemStack(owner), new IconState(owner));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        int relMouseX = mouseX - guiLeft;
        int relMouseY = mouseY - guiTop;
        for(IWidget widget : this.widgets)
        {
            if(widget.getBounds().contains(relMouseX, relMouseY))
            {
                widget.handleClick(relMouseX, relMouseY, mouseButton);
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;

        int containerHeight = this.container.getContentHeight();
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, PLAYER_INVENTORY_WIDTH + GUI_BORDER_WIDTH * 2, containerHeight + PLAYER_INVENTORY_HEIGHT + GUI_BORDER_WIDTH);
        this.drawTexturedModalRect(guiLeft, guiTop + containerHeight + PLAYER_INVENTORY_HEIGHT + GUI_BORDER_WIDTH, 0, 256 - GUI_BORDER_WIDTH, PLAYER_INVENTORY_WIDTH + GUI_BORDER_WIDTH * 2, GUI_BORDER_WIDTH);

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft, this.guiTop, 0.0);
        for(Slot slot : this.container.inventorySlots)
            this.renderItemSlot(slot);
        GlStateManager.popMatrix();


        for(IWidget widget : widgets)
        {
            widget.update(mouseX - guiLeft, mouseY - guiTop, partialTicks);
        }
    }

    private void onIconClick(IconState iconState)
    {
        this.showDetailsFor = iconState.block;
    }

    private static class IconState
    {
        public Block block;

        public IconState(Block block)
        {
            this.block = block;
        }
    }

    private void drawStringRight(String string, int x, int y, int color)
    {
        int len = this.fontRenderer.getStringWidth(string);
        this.drawString(this.fontRenderer, string, x - len, y, color);
    }

    private static String FormatPct(float value)
    {
        int d = Math.round((value - 1f) * 100);
        if(d > 0)
        {
            return TextFormatting.GREEN + "+" + d + "%";
        }
        else
        {
            return TextFormatting.RED + "" + d + "%";
        }
    }
}
