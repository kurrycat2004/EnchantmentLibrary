package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@NonnullByDefault
@SuppressWarnings("PointlessArithmeticExpression")
public class GuiUtil {
    public static final double X256 = 0.00390625F; // Assuming texture size is 256x256
    public static final double X32 = 0.03125F; // Assuming texture size is 32x32

    public static final int ITEM_SIZE = 16;
    private static final int HOVER_COLOR = 0x80FFFFFF;

    public static void setGlColor(int color) {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, a);
    }

    public static void resetGlColor() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void setStandardBlendFunc() {
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
    }

    public static void drawRightString(FontRenderer fr, String text, double x, double y, double scale, int color, boolean shadow) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0);
        GlStateManager.scale(scale, scale, 1.0);
        fr.drawString(text, -fr.getStringWidth(text), 0, color, shadow);
        GlStateManager.popMatrix();
    }

    public static void drawString(FontRenderer fr, String text, double x, double y, double scale, int color, boolean shadow) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0.0);
        GlStateManager.scale(scale, scale, 1.0);
        fr.drawString(text, 0, 0, color, shadow);
        GlStateManager.popMatrix();
    }

    public static void drawRect(int x, int y, int w, int h, int color, double zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        setStandardBlendFunc();
        setGlColor(color);
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x + 0, y + h, zLevel).endVertex();
        bufferbuilder.pos(x + w, y + h, zLevel).endVertex();
        bufferbuilder.pos(x + w, y + 0, zLevel).endVertex();
        bufferbuilder.pos(x + 0, y + 0, zLevel).endVertex();
        tessellator.draw();
        resetGlColor();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int w, int h, double m, double zLevel) {
        //resetGlColor();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x + 0, y + h, zLevel).tex((float) (u + 0) * m, (float) (v + h) * m).endVertex();
        bufferbuilder.pos(x + w, y + h, zLevel).tex((float) (u + w) * m, (float) (v + h) * m).endVertex();
        bufferbuilder.pos(x + w, y + 0, zLevel).tex((float) (u + w) * m, (float) (v + 0) * m).endVertex();
        bufferbuilder.pos(x + 0, y + 0, zLevel).tex((float) (u + 0) * m, (float) (v + 0) * m).endVertex();
        tessellator.draw();
    }

    /**
     * Renders a horizontally stretched textured rectangle. <br>
     * The stretched pixel column will be at <code>u + l + 1</code> in the texture. <br>
     * The total texture width should be <code>l + 1 + r</code>.
     *
     * @param x      x position
     * @param y      y position
     * @param u      x position in the texture
     * @param v      y position in the texture
     * @param l      width of the left part in the texture
     * @param r      width of the right part in the texture
     * @param w      width of the rect
     * @param h      height of the rect
     * @param m      multiplier for the texture (1 / texture_size), use {@link #X256} or {@link #X32}
     * @param zLevel The z level
     */
    public static void drawXStretchedTexturedModalRect(int x, int y, int u, int v, int l, int r, int w, int h, double m, double zLevel) {
        int e = w - l - r; // The extra width that will be stretched

        //resetGlColor();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        // Left part
        bufferbuilder.pos(x + 0, y + h, zLevel).tex((u + 0) * m, (v + h) * m).endVertex();
        bufferbuilder.pos(x + l, y + h, zLevel).tex((u + l) * m, (v + h) * m).endVertex();
        bufferbuilder.pos(x + l, y + 0, zLevel).tex((u + l) * m, (v + 0) * m).endVertex();
        bufferbuilder.pos(x + 0, y + 0, zLevel).tex((u + 0) * m, (v + 0) * m).endVertex();

        // Middle part (stretched)
        bufferbuilder.pos(x + l + 0, y + h, zLevel).tex((u + l + 0) * m, (v + h) * m).endVertex();
        bufferbuilder.pos(x + l + e, y + h, zLevel).tex((u + l + 1) * m, (v + h) * m).endVertex();
        bufferbuilder.pos(x + l + e, y + 0, zLevel).tex((u + l + 1) * m, (v + 0) * m).endVertex();
        bufferbuilder.pos(x + l + 0, y + 0, zLevel).tex((u + l + 0) * m, (v + 0) * m).endVertex();

        // Right part
        bufferbuilder.pos(x + l + e, y + h, zLevel).tex((u + l + 0 + 1) * m, (v + h) * m).endVertex();
        bufferbuilder.pos(x + w + 0, y + h, zLevel).tex((u + l + r + 1) * m, (v + h) * m).endVertex();
        bufferbuilder.pos(x + w + 0, y + 0, zLevel).tex((u + l + r + 1) * m, (v + 0) * m).endVertex();
        bufferbuilder.pos(x + l + e, y + 0, zLevel).tex((u + l + 0 + 1) * m, (v + 0) * m).endVertex();

        tessellator.draw();
    }

    /**
     * Renders a vertically stretched textured rectangle. <br>
     * The stretched pixel row will be at <code>v + t + 1</code> in the texture. <br>
     * The total texture height should be <code>t + 1 + b</code>.
     *
     * @param x      x position
     * @param y      y position
     * @param u      x position in the texture
     * @param v      y position in the texture
     * @param t      height of the top part in the texture
     * @param b      height of the bottom part in the texture
     * @param w      width of the rect
     * @param h      height of the rect
     * @param m      multiplier for the texture (1 / texture_size), use {@link #X256} or {@link #X32}
     * @param zLevel The z level
     */
    public static void drawYStretchedTexturedModalRect(int x, int y, int u, int v, int t, int b, int w, int h, double m, double zLevel) {
        int e = h - t - b; // The extra height that will be stretched

        //resetGlColor();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        // Top part
        bufferbuilder.pos(x + 0, y + t, zLevel).tex((u + 0) * m, (v + t) * m).endVertex();
        bufferbuilder.pos(x + w, y + t, zLevel).tex((u + w) * m, (v + t) * m).endVertex();
        bufferbuilder.pos(x + w, y + 0, zLevel).tex((u + w) * m, (v + 0) * m).endVertex();
        bufferbuilder.pos(x + 0, y + 0, zLevel).tex((u + 0) * m, (v + 0) * m).endVertex();

        // Middle part (stretched)
        bufferbuilder.pos(x + 0, y + t + e, zLevel).tex((u + 0) * m, (v + t + 1) * m).endVertex();
        bufferbuilder.pos(x + w, y + t + e, zLevel).tex((u + w) * m, (v + t + 1) * m).endVertex();
        bufferbuilder.pos(x + w, y + t + 0, zLevel).tex((u + w) * m, (v + t + 0) * m).endVertex();
        bufferbuilder.pos(x + 0, y + t + 0, zLevel).tex((u + 0) * m, (v + t + 0) * m).endVertex();

        // Bottom part
        bufferbuilder.pos(x + 0, y + h + 0, zLevel).tex((u + 0) * m, (v + t + b + 1) * m).endVertex();
        bufferbuilder.pos(x + w, y + h + 0, zLevel).tex((u + w) * m, (v + t + b + 1) * m).endVertex();
        bufferbuilder.pos(x + w, y + t + e, zLevel).tex((u + w) * m, (v + t + 0 + 1) * m).endVertex();
        bufferbuilder.pos(x + 0, y + t + e, zLevel).tex((u + 0) * m, (v + t + 0 + 1) * m).endVertex();

        tessellator.draw();
    }


    public static void prepareItemRender() {
        /*RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();*/
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        resetGlColor();
        GlStateManager.enableDepth();
    }

    public static void drawItemStackHover(int itemX, int itemY, boolean hovered) {
        if (!hovered) return;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.colorMask(true, true, true, false);
        drawRect(itemX, itemY, ITEM_SIZE, ITEM_SIZE, HOVER_COLOR, 0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private static void drawItemStackTextOverlay(FontRenderer fr, int x, int y, String text, boolean top) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        int yPos = top ? y : y + 6 + 3;
        int xPos = x + 19 - 2;

        drawRightString(fr, text, xPos, yPos, 0.5, Color.WHITE.getRGB(), true);

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
    }

    public static void drawItemStack(ItemStack stack, int itemX, int itemY, boolean hovered) {
        drawItemStack(stack, itemX, itemY, hovered, null, null);
    }

    public static void drawItemStack(ItemStack stack, int itemX, int itemY, boolean hovered, @Nullable String bottomRight, @Nullable String topRight) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderItem ri = mc.getRenderItem();
        FontRenderer fr = mc.fontRenderer;
        ri.zLevel = 100.0F;
        ri.renderItemAndEffectIntoGUI(mc.player, stack, itemX, itemY);
        if (topRight != null) drawItemStackTextOverlay(fr, itemX, itemY, topRight, true);
        ri.renderItemOverlayIntoGUI(mc.fontRenderer, stack, itemX, itemY, bottomRight);
        ri.zLevel = 0.0F;
        drawItemStackHover(itemX, itemY, hovered);
    }

    public static void finishItemRender() {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
    }

    public static void drawSingleItemStack(ItemStack stack, int itemX, int itemY, boolean hovered) {
        drawSingleItemStack(stack, itemX, itemY, hovered, null, null);
    }

    public static void drawSingleItemStack(ItemStack stack, int itemX, int itemY, boolean hovered, @Nullable String bottomRight, @Nullable String topRight) {
        prepareItemRender();
        drawItemStack(stack, itemX, itemY, hovered, bottomRight, topRight);
        finishItemRender();
    }
}
