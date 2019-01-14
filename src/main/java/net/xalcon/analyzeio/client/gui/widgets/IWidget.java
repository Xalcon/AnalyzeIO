package net.xalcon.analyzeio.client.gui.widgets;

import org.lwjgl.util.Rectangle;

public interface IWidget
{
    Rectangle getBounds();
    void render();
    void update(int relMouseX, int relMouseY, float partialTicks);
    void handleClick(int relMouseX, int relMouseY, int button);
    void handleScroll(int relMouseX, int relMouseY, int scrollValue);
    String getHoverText();
}
