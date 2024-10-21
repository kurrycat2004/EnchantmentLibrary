package io.github.kurrycat2004.enchlib.gui.components;

import io.github.kurrycat2004.enchlib.util.MouseUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiSearchField extends GuiTextField {
    public GuiSearchField(int componentId, FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(componentId, fontRenderer, x, y, width, height);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean mouseOver = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
        if (this.canLoseFocus) this.setFocused(mouseOver);
        if (this.isFocused() && mouseOver && mouseButton == MouseUtil.RIGHT) {
            this.setText("");
            this.setResponderEntryValue(this.getId(), this.getText());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
