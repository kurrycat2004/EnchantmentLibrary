package io.github.kurrycat2004.enchlib.gui.components;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.util.GuiUtil;
import io.github.kurrycat2004.enchlib.util.MathUtil;
import io.github.kurrycat2004.enchlib.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiList<T extends GuiList.IListEntry> implements IMouseHandler {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MODID, "textures/gui/gui_list.png");
    private static final int SCROLLBAR_WIDTH = 9;
    private static final int SCROLL_BUTTON_WIDTH = 7;
    private static final int SCROLLBAR_MIN_HEIGHT = 10;

    private static final int SCROLL_SPEED = 8;

    protected int x, y;
    protected int width, height;
    protected final int slotHeight;

    protected List<T> entries;
    protected final List<Pair<T, Integer>> filteredEntries;
    protected String filter = "";

    protected int amountScrolled = 0;

    protected int maxRenderCount;

    private int scrollDragAmount = -1;
    private int scrollDragOffset = -1;

    protected double zLevel = 0.0;

    protected int hovered = -1;

    public GuiList(int x, int y, int width, int height, int slotHeight) {
        this.slotHeight = slotHeight;
        updateSize(x, y, width, height);
        this.entries = new ArrayList<>();
        this.filteredEntries = new ArrayList<>();
    }

    private boolean showScrollbar() {
        return getTotalHeight() > height;
    }

    public void updateSize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.maxRenderCount = MathUtil.ceilDiv(height, slotHeight);
    }

    public abstract void updateEntries();

    public void setFilter(String filter) {
        this.filter = filter;
        updateFilter();
    }

    public void setScroll(int amount) {
        this.amountScrolled = MathUtil.clamp(amount, 0, getTotalHeight() - height);
    }

    public void updateFilter() {
        filteredEntries.clear();
        for (T entry : entries) {
            if (filter.isEmpty()) {
                filteredEntries.add(new Pair<>(entry, -1));
                continue;
            }
            int startIndex = entry.matchesFilter(filter);
            if (startIndex < 0) continue;
            filteredEntries.add(new Pair<>(entry, startIndex));
        }
        this.setScroll(amountScrolled);
    }

    public boolean isShowingEntries() {
        return !filteredEntries.isEmpty();
    }

    private static void enableScissor(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int w = mc.displayWidth, h = mc.displayHeight;
        ScaledResolution res = new ScaledResolution(mc);
        double scaleW = w / res.getScaledWidth_double();
        double scaleH = h / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) Math.round(x * scaleW), (int) Math.round(h - ((y + height) * scaleH)),
                (int) Math.round(width * scaleW), (int) Math.round(height * scaleH)
        );
    }

    private static void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private int getStartIndex() {
        return Math.min(amountScrolled / slotHeight, filteredEntries.size() - 1);
    }

    private int getEntryY(int index) {
        return y + (index - getStartIndex()) * slotHeight - amountScrolled % slotHeight;
    }

    private int getScrollbarY(int scrollbarHeight) {
        return y + 1 + MathUtil.mapClamp(amountScrolled,
                0, getTotalHeight() - height,
                0, height - 2 - scrollbarHeight);
    }

    private int getScrollbarHeight() {
        return Math.max(MathUtil.ceilDiv(height * (height - 2), getTotalHeight()), SCROLLBAR_MIN_HEIGHT);
    }

    private int getEntryWidth() {
        return showScrollbar() ? width - SCROLLBAR_WIDTH : width;
    }

    private int getTotalHeight() {
        return filteredEntries.size() * slotHeight;
    }

    protected boolean isMouseOutsideEntries(int mouseX, int mouseY) {
        return mouseX < x || mouseX >= x + getEntryWidth() || mouseY < y || mouseY >= y + height;
    }

    private int getEntryUnderMouse(int mouseX, int mouseY) {
        if (isMouseOutsideEntries(mouseX, mouseY)) return -1;

        int scrollOffset = amountScrolled % slotHeight;
        int yOff = mouseY - y + scrollOffset;

        int index = getStartIndex() + yOff / slotHeight;
        if (index >= filteredEntries.size()) return -1;
        return index;
    }

    public void render(int mouseX, int mouseY) {
        int start = getStartIndex();
        int end = Math.min(start + maxRenderCount, filteredEntries.size() - 1); // Inclusive

        int entryWidth = getEntryWidth();
        enableScissor(x, y, entryWidth, height);

        for (int i = start; i >= 0 && i <= end; i++) {
            Pair<T, Integer> entry = filteredEntries.get(i);
            int entryY = getEntryY(i);
            if (mouseX >= x && mouseX < x + entryWidth && mouseY >= entryY && mouseY < entryY + slotHeight) {
                hovered = i;
            }
            GuiUtil.resetGlColor();
            entry.first().drawEntry(i, x, entryY, entryWidth, slotHeight,
                    mouseX - x, mouseY - entryY, entry.second(), filter.length());
        }

        disableScissor();

        if (showScrollbar()) {
            int scrollbarHeight = getScrollbarHeight();
            int scrollbarY = getScrollbarY(scrollbarHeight);

            if (scrollDragOffset >= 0) {
                int newScroll = MathUtil.map(mouseY - scrollDragOffset,
                        y + 1, y + height - 1 - scrollbarHeight,
                        0, getTotalHeight() - height);
                this.setScroll(scrollDragAmount + newScroll);
            }

            GuiUtil.resetGlColor();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            // Border
            GuiUtil.drawYStretchedTexturedModalRect(
                    x + width - SCROLLBAR_WIDTH, y,
                    0, 0, 1, 1,
                    SCROLLBAR_WIDTH, height,
                    GuiUtil.X32, zLevel
            );
            // Scroll button
            GuiUtil.drawYStretchedTexturedModalRect(
                    x + width - SCROLLBAR_WIDTH + 1, scrollbarY,
                    0, 3, 1, 1,
                    SCROLL_BUTTON_WIDTH, scrollbarHeight,
                    GuiUtil.X32, zLevel
            );
        }
    }

    public void drawHovered(int mouseX, int mouseY) {
        if (hovered < 0 || hovered >= filteredEntries.size()) return;
        if (isMouseOutsideEntries(mouseX, mouseY)) return;

        Pair<T, Integer> entry = filteredEntries.get(hovered);
        int entryY = getEntryY(hovered);
        GuiUtil.resetGlColor();
        entry.first().drawHovered(hovered, x, entryY, getEntryWidth(), slotHeight,
                mouseX - x, mouseY - entryY, entry.second(), filter.length());
    }

    @Override
    public boolean onMouseScroll(int mouseX, int mouseY, int delta) {
        int entry = getEntryUnderMouse(mouseX, mouseY);
        if (entry >= 0 && filteredEntries.get(entry).first().onMouseScroll(mouseX - x, mouseY - getEntryY(entry), delta)) {
            return true;
        }
        if (!showScrollbar()) return false;

        this.setScroll(amountScrolled - delta * SCROLL_SPEED);

        return true;
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int button) {
        int entry = getEntryUnderMouse(mouseX, mouseY);
        if (entry >= 0 && filteredEntries.get(entry).first().onMouseClick(mouseX - x, mouseY - getEntryY(entry), button)) {
            return true;
        }

        if (!showScrollbar()) return false;

        if (mouseX < x + width - SCROLLBAR_WIDTH + 1 || mouseX >= x + width - 1) return false;
        int scrollbarHeight = getScrollbarHeight();
        int scrollbarY = getScrollbarY(scrollbarHeight);
        if (mouseY < scrollbarY || mouseY >= scrollbarY + scrollbarHeight) return false;
        scrollDragAmount = amountScrolled;
        scrollDragOffset = mouseY - y;

        return true;
    }

    @Override
    public boolean onMouseRelease(int mouseX, int mouseY, int button) {
        if (scrollDragOffset >= 0) {
            scrollDragAmount = -1;
            scrollDragOffset = -1;
            return true;
        }
        int entry = getEntryUnderMouse(mouseX, mouseY);
        return entry >= 0 && filteredEntries.get(entry).first().onMouseRelease(mouseX - x, mouseY - getEntryY(entry), button);
    }

    public interface IListEntry extends IMouseHandler {
        int matchesFilter(String filter);

        /**
         * @param slotIndex Index of the entry
         * @param x         X position of the entry
         * @param y         Y position of the entry
         * @param w         Can be less than width, depending on whether the scroll bar is rendered
         * @param h         Always equal to slotHeight
         * @param mouseX    Relative to the entry
         * @param mouseY    Relative to the entry
         */
        void drawEntry(int slotIndex, int x, int y, int w, int h, int mouseX, int mouseY, int filterStart, int filterLen);

        void drawHovered(int slotIndex, int x, int y, int w, int h, int mouseX, int mouseY, int filterStart, int filterLen);
    }
}
