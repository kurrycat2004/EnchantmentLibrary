package io.github.kurrycat2004.enchlib.gui;

import io.github.kurrycat2004.enchlib.Tags;
import io.github.kurrycat2004.enchlib.common.EnchData;
import io.github.kurrycat2004.enchlib.config.settings.ClientSettings;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.container.ContainerEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.gui.components.GuiList;
import io.github.kurrycat2004.enchlib.gui.components.GuiSearchField;
import io.github.kurrycat2004.enchlib.net.PacketHandler;
import io.github.kurrycat2004.enchlib.net.packet.to_server.PacketClickEnchLib;
import io.github.kurrycat2004.enchlib.tile.TileEnchantmentLibrary;
import io.github.kurrycat2004.enchlib.util.ArrLongUtil;
import io.github.kurrycat2004.enchlib.util.EnchantmentUtil;
import io.github.kurrycat2004.enchlib.util.GuiUtil;
import io.github.kurrycat2004.enchlib.util.LangUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.ITALIC;
import static net.minecraft.util.text.TextFormatting.RED;

@SideOnly(Side.CLIENT)
@NonnullByDefault
public class GuiEnchantmentLibrary extends GuiContainer implements GuiPageButtonList.GuiResponder {
    private static final int SEARCH_COLOR = 0xFFFFFFFF;
    private static final int SEARCH_COLOR_ERROR = 0xFFFF0000;

    private static final String INFO_SCROLL_TIP_KEY = "scroll_tip";
    private static final String INFO_MERGE_TIP_KEY = "merge_tip";
    private static final String INFO_ENCHANTMENT_DIFF_KEY = "enchantment_diff";
    private static final String INFO_NO_ENTRIES = "no_entries";
    private static final String INFO_NO_FILTER_MATCHES = "no_filter_matches";

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MODID, "textures/gui/enchantment_library.png");

    private final ContainerEnchantmentLibrary container;
    private final TileEnchantmentLibrary tile;
    private final EnchList enchList;
    private GuiSearchField searchField;

    // for tooltips
    private EnchListEntry addToHoldingEnchantment = null;

    public GuiEnchantmentLibrary(InventoryPlayer inv, TileEntity tile) {
        super(new ContainerEnchantmentLibrary(inv, tile));
        this.ySize = 188;
        this.container = (ContainerEnchantmentLibrary) this.inventorySlots;
        this.tile = (TileEnchantmentLibrary) tile;
        this.enchList = new EnchList();
    }

    public TileEnchantmentLibrary getTile() {
        return tile;
    }

    public void updateEntries() {
        enchList.updateEntries();
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.searchField = new GuiSearchField(0, this.fontRenderer,
                this.guiLeft + 8, this.guiTop + 92, 160, 11);
        this.searchField.setGuiResponder(this);
        this.searchField.setTextColor(SEARCH_COLOR);
        this.enchList.updateSize(this.guiLeft + 7, this.guiTop + 17, 162, 72);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    @Override
    public void handleMouseInput() throws IOException {
        int delta = Integer.signum(Mouse.getEventDWheel());
        if (delta != 0) {
            int x = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int y = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            this.mouseScrolled(x, y, delta);
        }
        super.handleMouseInput();
    }

    protected void handleEnchLibClick(int enchantmentId, short level, int mouseButton, ClickType clickType) {
        EntityPlayer player = this.mc.player;

        ItemStack clientItemStack = this.container.enchLibClick(enchantmentId, level, mouseButton, clickType, player);

        short actionUid = player.openContainer.getNextTransactionID(player.inventory);
        PacketHandler.INSTANCE.sendToServer(new PacketClickEnchLib(actionUid, enchantmentId, level, (byte) mouseButton, clickType, clientItemStack));
    }

    public void mouseScrolled(int mouseX, int mouseY, int delta) {
        this.enchList.onMouseScroll(mouseX, mouseY, delta);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.searchField.mouseClicked(mouseX, mouseY, mouseButton)) return;
        if (this.enchList.onMouseClick(mouseX, mouseY, mouseButton)) return;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.enchList.onMouseRelease(mouseX, mouseY, state)) return;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void updateScreen() {
        this.searchField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void setEntryValue(int id, String value) {
        this.enchList.setFilter(value.toLowerCase());
        this.searchField.setTextColor(this.enchList.isShowingEntries() ? SEARCH_COLOR : SEARCH_COLOR_ERROR);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.searchField.isFocused()) {
            this.searchField.textboxKeyTyped(typedChar, keyCode);
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.renderHoveredToolTip(mouseX, mouseY);
        this.enchList.drawHovered(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.tile.getDisplayName().getUnformattedText(), 8, 6, Color.DARK_GRAY.getRGB());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GuiUtil.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize, GuiUtil.X256, this.zLevel);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableDepth();
        this.enchList.render(mouseX, mouseY);
        this.searchField.drawTextBox();
    }

    @Override
    public List<String> getItemToolTip(ItemStack stack) {
        List<String> tooltip = super.getItemToolTip(stack);
        if (addToHoldingEnchantment == null) return tooltip;
        if (stack.isEmpty()) return tooltip;

        addToHoldingEnchantment.addDiffToTooltip(stack, tooltip);

        if (ClientSettings.INSTANCE.show_tips) {
            tooltip.add(1, "" + DARK_GRAY + ITALIC + LangUtil.guiInfo(INFO_SCROLL_TIP_KEY));
            if (ServerSettings.INSTANCE.allowEnchantMerging && stack == addToHoldingEnchantment.book) {
                tooltip.add(1, "" + DARK_GRAY + ITALIC + LangUtil.guiInfo(INFO_MERGE_TIP_KEY));
            }
        }
        return tooltip;
    }

    public class EnchList extends GuiList<EnchListEntry> {
        public EnchList() {
            super(
                    (GuiEnchantmentLibrary.this.width - GuiEnchantmentLibrary.this.xSize) / 2 + 7,
                    (GuiEnchantmentLibrary.this.height - GuiEnchantmentLibrary.this.ySize) / 2 + 17,
                    162, 72,
                    24
            );
            updateEntries();
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (super.onMouseClick(mouseX, mouseY, button)) return true;
            if (isMouseOutsideEntries(mouseX, mouseY)) return false;
            handleEnchLibClick(entries.size(), (short) 0, button, ClickType.THROW);
            return true;
        }

        @Override
        protected void renderEntries(int mouseX, int mouseY) {
            super.renderEntries(mouseX, mouseY);
            String info = null;
            if (entries.isEmpty()) info = LangUtil.guiInfo(INFO_NO_ENTRIES);
            else if (filteredEntries.isEmpty()) info = LangUtil.guiInfo(INFO_NO_FILTER_MATCHES);

            if (info != null) {
                int x = this.x + getEntryWidth() / 2;
                int y = this.y + height / 2;
                GuiUtil.drawCenteredStringLines(fontRenderer, info, x, y, 1.0, Color.WHITE.getRGB(), true);
            }
        }

        @Override
        public void updateEntries() {
            Map<Enchantment, EnchListEntry> entryMap = new Reference2ObjectLinkedOpenHashMap<>(tile.data.getEnchantmentCount());

            int id = 0;
            for (Map.Entry<Enchantment, EnchData> entry : tile.data.getMap().entrySet()) {
                EnchListEntry enchEntry = new EnchListEntry(id++, entry.getKey(), entry.getValue());
                entryMap.put(entry.getKey(), enchEntry);
            }
            // update new entries using the old ones
            for (EnchListEntry entry : entries) {
                EnchListEntry newEntry = entryMap.get(entry.enchantment);
                if (newEntry == null) continue;
                newEntry.setLevel(entry.level);
            }

            entries.clear();
            entries.addAll(entryMap.values());

            updateFilter();
        }
    }

    public class EnchListEntry implements GuiList.IListEntry {
        private static final int ITEM_MARGIN = 4;

        /// index of enchantment data in tile.data
        private final int id;
        private final Enchantment enchantment;
        private final EnchData enchData;

        private final ItemStack book;
        private final NBTTagCompound enchNBT;
        private short level = 1;

        public EnchListEntry(int enchantmentId, Enchantment enchantment, EnchData enchData) {
            this.id = enchantmentId;
            this.enchantment = enchantment;
            this.enchData = enchData;
            book = EnchantmentUtil.getBook(enchantment, level, 1);
            NBTTagList enchantmentTags = EnchantmentUtil.getEnchantmentTags(book);
            assert enchantmentTags != null;
            enchNBT = enchantmentTags.getCompoundTagAt(0);
        }

        private void setLevel(short level) {
            this.level = level;
            enchNBT.setShort(EnchantmentUtil.VANILLA_TAG_LEVEL, level);
        }

        private boolean isMouseOverItem(int mouseX, int mouseY) {
            return mouseX >= ITEM_MARGIN && mouseY >= ITEM_MARGIN &&
                   mouseX < ITEM_MARGIN + GuiUtil.ITEM_SIZE &&
                   mouseY < ITEM_MARGIN + GuiUtil.ITEM_SIZE;
        }

        //TODO: cache?
        private String getEnchantmentName() {
            return LangUtil.localize(enchantment.getName());
        }

        private String getFormattedEnchantmentName() {
            String s = getEnchantmentName();
            if (enchantment.isCurse()) s = RED + s;
            return s;
        }

        @Override
        public int matchesFilter(String filter) {
            return getEnchantmentName().toLowerCase().indexOf(filter);
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int w, int h, int mouseX, int mouseY, int filterStart, int filterLen) {
            GuiUtil.resetGlColor();
            Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            GuiUtil.drawXStretchedTexturedModalRect(x, y, 0, 188, 21, 2, w, h, GuiUtil.X256, zLevel);

            boolean hovered = isMouseOverItem(mouseX, mouseY);
            GuiUtil.drawSingleItemStack(book, x + ITEM_MARGIN, y + ITEM_MARGIN, hovered, null, String.valueOf(level));

            GlStateManager.disableLighting();

            int nameX = x + ITEM_MARGIN * 2 + GuiUtil.ITEM_SIZE + 2;
            int nameY = y + 5;
            if (filterStart >= 0) {
                int matchX = fontRenderer.getStringWidth(getEnchantmentName().substring(0, filterStart));
                int matchWidth = fontRenderer.getStringWidth(getEnchantmentName().substring(filterStart, filterStart + filterLen));
                GuiUtil.drawRect(
                        nameX + matchX, nameY - 1,
                        matchWidth, fontRenderer.FONT_HEIGHT + 1,
                        0xFFFFF59D, zLevel
                );
            }
            fontRenderer.drawString(getFormattedEnchantmentName(), nameX, nameY, Color.DARK_GRAY.getRGB());

            String points = ArrLongUtil.toZillion(enchData.data());
            GuiUtil.drawString(fontRenderer, "Points: " + points,
                    nameX, nameY + 10, 0.5, Color.GRAY.getRGB(), false);
        }

        @Override
        public void drawHovered(int slotIndex, int x, int y, int w, int h, int mouseX, int mouseY, int filterStart, int filterLen) {
            if (!isMouseOverItem(mouseX, mouseY)) return;
            ItemStack holding = mc.player.inventory.getItemStack();
            if (!holding.isEmpty() & !(holding.getItem() instanceof ItemEnchantedBook)) return;

            ItemStack tooltipStack = !holding.isEmpty() && ServerSettings.INSTANCE.allowEnchantMerging ? holding : book;

            addToHoldingEnchantment = this;
            renderToolTip(tooltipStack, x + mouseX, y + mouseY);
            addToHoldingEnchantment = null;
        }

        public void addDiffToTooltip(ItemStack stack, List<String> tooltip) {
            if (stack == book) return;

            NBTTagCompound enchTag = EnchantmentUtil.findEnchTagInStack(enchantment, stack);
            if (enchTag != null) {
                int oldLevel = enchTag.getShort(EnchantmentUtil.VANILLA_TAG_LEVEL);
                if (oldLevel >= level) return;

                tooltip.add(1, RED + "- " + enchantment.getTranslatedName(oldLevel));
            }

            tooltip.add(1, GREEN + "+ " + enchantment.getTranslatedName(level));
            tooltip.add(1, "" + GRAY + ITALIC + LangUtil.guiInfo(INFO_ENCHANTMENT_DIFF_KEY));
        }

        @Override
        public boolean onMouseScroll(int mouseX, int mouseY, int delta) {
            if (isMouseOverItem(mouseX, mouseY)) {
                int level = this.level + delta;
                if (level < 1) level = enchData.getMaxLevel();
                if (level > enchData.getMaxLevel()) level = 1;
                setLevel((short) level);
                return true;
            }
            return false;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (!isMouseOverItem(mouseX, mouseY)) return false;

            boolean shiftDown = GuiScreen.isShiftKeyDown();
            ClickType clickType = shiftDown ? ClickType.QUICK_MOVE : ClickType.PICKUP;
            handleEnchLibClick(id, level, button, clickType);

            return true;
        }

        @Override
        public boolean onMouseRelease(int mouseX, int mouseY, int button) {
            return false;
        }
    }

    @Override
    public void setEntryValue(int id, boolean value) {}

    @Override
    public void setEntryValue(int id, float value) {}
}
