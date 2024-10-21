package io.github.kurrycat2004.enchlib.common;

import com.jaquadro.minecraft.storagedrawers.api.capabilities.IItemRepository;
import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.config.settings.ServerSettings;
import io.github.kurrycat2004.enchlib.util.EnchantmentUtil;
import io.github.kurrycat2004.enchlib.util.FastOrderedMap;
import io.github.kurrycat2004.enchlib.util.MathUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import io.github.kurrycat2004.enchlib.util.interfaces.INBTSerDe;
import io.github.kurrycat2004.enchlib.util.interfaces.ISavable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@NonnullByDefault
public class EnchLibData implements INBTSerDe, ISavable, IItemHandler, IItemRepository {
    public static final String TAG_ENCHANTMENTS = "enchlib";

    private static final Comparator<Enchantment> enchantmentComparator = (e1, e2) -> {
        ResourceLocation res1 = e1.getRegistryName();
        ResourceLocation res2 = e2.getRegistryName();
        if (res1 != null && res2 != null) return res1.compareTo(res2);
        if (res1 == null && res2 != null) return -1;
        if (res1 != null) return 1;
        return e1.getName().compareTo(e2.getName());
    };

    private final FastOrderedMap<Enchantment, EnchData> data = new FastOrderedMap<>(enchantmentComparator);
    private final ISavable savable;

    public EnchLibData(ISavable savable) {
        this.savable = savable;
    }

    @Override
    public void markForSave() {
        this.savable.markForSave();
    }

    public FastOrderedMap<Enchantment, EnchData> getMap() {
        return data;
    }

    private @Nullable String getEnchantmentName(Enchantment enchantment) {
        ResourceLocation res = enchantment.getRegistryName();
        if (res != null) return res.toString();
        EnchLibMod.LOGGER.warn("Enchantment {} ({}) has no registry name", enchantment, enchantment.getName());
        return null;
    }

    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        NBTTagCompound tag = new NBTTagCompound();
        for (Map.Entry<Enchantment, EnchData> entry : data.entrySet()) {
            String name = getEnchantmentName(entry.getKey());
            if (name == null) continue;

            NBTTagCompound enchTag = new NBTTagCompound();
            entry.getValue().writeNBT(enchTag);
            tag.setTag(name, enchTag);
        }
        if (!tag.isEmpty()) nbt.setTag(TAG_ENCHANTMENTS, tag);
        return nbt;
    }


    public void readNBT(NBTTagCompound nbt) {
        data.clear();
        NBTTagCompound enchTagList = nbt.getCompoundTag(TAG_ENCHANTMENTS);
        for (String key : enchTagList.getKeySet()) {
            Enchantment enchantment = Enchantment.getEnchantmentByLocation(key);
            if (enchantment == null) {
                EnchLibMod.LOGGER.warn("Enchantment {} not found", key);
                continue;
            }
            NBTTagCompound ench = enchTagList.getCompoundTag(key);
            EnchData points = new EnchData();
            points.readNBT(ench);
            data.put(enchantment, points);
        }
    }

    public @Nullable Enchantment getEnchantment(int index) {
        return data.tryKeyAt(index);
    }

    public int getEnchantmentCount() {
        return data.size();
    }

    public boolean isItemValid(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemEnchantedBook)) return false;
        List<EnchantmentData> enchantments = EnchantmentUtil.getEnchantments(stack);
        return !enchantments.isEmpty();
    }

    public ItemStack insertEnchantedBook(ItemStack stack, boolean simulate) {
        if (!(stack.getItem() instanceof ItemEnchantedBook)) return stack;
        List<EnchantmentData> enchantments = EnchantmentUtil.getEnchantments(stack);
        if (enchantments.isEmpty()) return stack;
        if (!ServerSettings.INSTANCE.allowEnchantSplitting && enchantments.size() > 1) return stack;

        if (simulate) return ItemStack.EMPTY;

        for (EnchantmentData enchData : enchantments) {
            EnchData data = this.data.computeIfAbsent(enchData.enchantment, e -> new EnchData());
            data.addN(stack.getCount(), MathUtil.clampIntToShort(enchData.enchantmentLevel));
        }

        this.markForSave();

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlots() {
        return data.size() + 1;
    }

    @Override
    public ItemStack getStackInSlot(int enchantmentIndex) {
        if (enchantmentIndex == data.size()) return ItemStack.EMPTY;
        Map.Entry<Enchantment, EnchData> entry = data.tryEntryAt(enchantmentIndex);
        if (entry == null) return ItemStack.EMPTY;

        EnchData enchData = entry.getValue();
        short maxExtractLevel = enchData.getMaxLevelExtractable();
        if (maxExtractLevel == 0) return ItemStack.EMPTY;

        int amount = enchData.getNumberOfLevelExtractable(maxExtractLevel);
        return EnchantmentUtil.getBook(entry.getKey(), maxExtractLevel, amount);
    }

    @Override
    public ItemStack insertItem(int _slot, ItemStack stack, boolean simulate) {
        return insertEnchantedBook(stack, simulate);
    }

    @Override
    public ItemStack extractItem(int enchantmentIndex, int amount, boolean simulate) {
        return extractMaxEnchantedBook(enchantmentIndex, amount, simulate);
    }

    public ItemStack extractMaxEnchantedBook(int enchantmentIndex, int amount, boolean simulate) {
        Map.Entry<Enchantment, EnchData> entry = data.tryEntryAt(enchantmentIndex);
        if (entry == null) return ItemStack.EMPTY;

        short maxExtractLevel = entry.getValue().getMaxLevelExtractable();
        if (maxExtractLevel == 0) return ItemStack.EMPTY;

        return extractEnchantedBook(entry.getKey(), entry.getValue(), amount, maxExtractLevel, simulate);
    }

    public ItemStack extractEnchantedBook(int enchantmentIndex, int amount, short level, boolean simulate) {
        Map.Entry<Enchantment, EnchData> entry = data.tryEntryAt(enchantmentIndex);
        if (entry == null) return ItemStack.EMPTY;

        return extractEnchantedBook(entry.getKey(), entry.getValue(), amount, level, simulate);
    }

    public ItemStack extractEnchantedBook(Enchantment enchantment, EnchData enchData, int amount, short level, boolean simulate) {
        ItemStack stack = EnchantmentUtil.getBook(enchantment, level, 1);
        amount = Math.min(amount, stack.getMaxStackSize());

        int remaining = enchData.removeN(amount, level, simulate);
        stack.setCount(amount - remaining);
        if (stack.isEmpty()) return ItemStack.EMPTY;

        if (!simulate) this.markForSave();
        return stack;
    }

    public int removeN(int enchantmentIndex, int amount, short level) {
        Map.Entry<Enchantment, EnchData> entry = data.tryEntryAt(enchantmentIndex);
        if (entry == null) return amount;

        int remaining = entry.getValue().removeN(amount, level, false);
        if (remaining != 0) this.markForSave();
        return remaining;
    }

    public ItemStack extractEnchantmentOntoBook(ItemStack stack, int enchantmentIndex, short level) {
        if (!(stack.getItem() instanceof ItemEnchantedBook)) return stack;
        Map.Entry<Enchantment, EnchData> entry = data.tryEntryAt(enchantmentIndex);
        if (entry == null) return stack;

        Enchantment enchantment = entry.getKey();
        ItemStack newStack = stack.copy();
        NBTTagList nbttaglist = ItemEnchantedBook.getEnchantments(newStack);
        NBTTagCompound existing = EnchantmentUtil.findEnchTagInStoredEnchantments(enchantment, nbttaglist);

        short existingLevel = existing == null ? 0 : existing.getShort(EnchantmentUtil.VANILLA_TAG_LEVEL);
        if (existingLevel >= level) return stack;

        EnchData enchData = entry.getValue();
        EnchData copy = enchData.copy();

        copy.addN(stack.getCount(), existingLevel);
        int remaining = copy.removeN(stack.getCount(), level, false);
        if (remaining != 0) return stack;

        enchData.copyFrom(copy);

        if (existing == null) {
            existing = new NBTTagCompound();
            existing.setShort(EnchantmentUtil.VANILLA_TAG_ID, (short) Enchantment.getEnchantmentID(enchantment));
            nbttaglist.appendTag(existing);
        }

        existing.setShort(EnchantmentUtil.VANILLA_TAG_LEVEL, level);
        this.markForSave();
        return newStack;
    }

    @Override
    public int getSlotLimit(int enchantmentIndex) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int _slot, ItemStack stack) {
        return isItemValid(stack);
    }

    @Override
    public @NotNull NonNullList<ItemRecord> getAllItems() {
        NonNullList<ItemRecord> list = NonNullList.create();
        for (Map.Entry<Enchantment, EnchData> entry : data.entrySet()) {
            short maxExtractLevel = entry.getValue().getMaxLevelExtractable();
            if (maxExtractLevel == 0) continue;

            int amount = entry.getValue().getNumberOfLevelExtractable(maxExtractLevel);
            ItemStack stack = EnchantmentUtil.getBookProto(entry.getKey(), maxExtractLevel);
            list.add(new ItemRecord(stack, amount));
        }

        return list;
    }

    @Override
    public @NotNull ItemStack insertItem(ItemStack stack, boolean simulate, @Nullable Predicate<ItemStack> predicate) {
        ItemStack remaining = stack.copy();
        if (predicate != null && !predicate.test(ItemStack.EMPTY)) return remaining;
        return insertEnchantedBook(remaining, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(ItemStack stack, int amount, boolean simulate, @Nullable Predicate<ItemStack> predicate) {
        ItemStack remaining = stack.copy();
        if (predicate != null && !predicate.test(remaining)) return ItemStack.EMPTY;
        if (!(stack.getItem() instanceof ItemEnchantedBook)) return ItemStack.EMPTY;
        List<EnchantmentData> enchantments = EnchantmentUtil.getEnchantments(stack);
        if (enchantments.size() != 1) return ItemStack.EMPTY;

        EnchantmentData enchantmentData = enchantments.get(0);
        Enchantment enchantment = enchantmentData.enchantment;
        short level = MathUtil.clampIntToShort(enchantmentData.enchantmentLevel);
        EnchData enchData = data.get(enchantment);
        if (enchData == null) return ItemStack.EMPTY;

        return extractEnchantedBook(enchantment, enchData, amount, level, simulate);
    }
}
