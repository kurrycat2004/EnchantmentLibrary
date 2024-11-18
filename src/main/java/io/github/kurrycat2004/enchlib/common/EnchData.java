package io.github.kurrycat2004.enchlib.common;

import io.github.kurrycat2004.enchlib.EnchLibMod;
import io.github.kurrycat2004.enchlib.util.ArrLong;
import io.github.kurrycat2004.enchlib.util.MathUtil;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import io.github.kurrycat2004.enchlib.util.interfaces.INBTSerDe;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;

@NonnullByDefault
public class EnchData implements INBTSerDe {
    private static final int DATA_SIZE = MathUtil.ceilDiv(Short.MAX_VALUE, Long.SIZE) + 1;

    private static final String TAG_DATA = "data";
    private static final String TAG_MAX_LEVEL = "max";

    private final ArrLong data;
    private short maxLevel;

    public EnchData() {
        this.data = new ArrLong(DATA_SIZE);
        this.maxLevel = 0;
    }

    public ArrLong data() {
        return data;
    }

    public EnchData copy() {
        EnchData copy = new EnchData();
        copy.data.copyFrom(data);
        copy.maxLevel = maxLevel;
        return copy;
    }

    public void copyFrom(EnchData other) {
        data.copyFrom(other.data);
        maxLevel = other.maxLevel;
    }

    public short getMaxLevelExtractable() {
        return (short) Math.min(Math.min(data.highestBit() + 1, Short.MAX_VALUE), maxLevel);
    }

    public int getNumberOfLevelExtractable(short level) {
        if (level <= 0) return 0;
        if (level > maxLevel) return 0;

        int lvl = level - 1;
        if (data.highestBit() >= lvl + Integer.SIZE - 1) return Integer.MAX_VALUE;
        return MathUtil.clampULongToInt(data.unalignedULongFrom(lvl));
    }

    public short getMaxLevel() {
        return maxLevel;
    }

    public int addN(int amount, short level, boolean simulate) {
        if (level <= 0 || amount <= 0) return amount;
        if (!simulate) {
            if (level > maxLevel) maxLevel = level;
            return data.addAt(level - 1, amount);
        }

        final ArrLong copy = new ArrLong(this.data);
        return copy.addAt(level - 1, amount);
    }

    public int removeN(int amount, short level, boolean simulate) {
        if (level <= 0 || amount <= 0) return amount;
        if (!simulate) return this.data.subAt(level - 1, amount);

        final ArrLong copy = new ArrLong(this.data);
        return copy.subAt(level - 1, amount);
    }

    @Override
    public String toString() {
        return "EnchData{" +
               "data=" + data.toHexString() +
               ", maxLevel=" + maxLevel +
               '}';
    }

    @Override
    public boolean readNBT(NBTTagCompound nbt) {
        maxLevel = nbt.getShort(TAG_MAX_LEVEL);

        final long[] data = this.data.data();
        final byte[] bytes = nbt.getByteArray(TAG_DATA);
        Arrays.fill(data, 0);
        if (bytes.length % 8 != 0) {
            EnchLibMod.LOGGER.error("Corrupted enchantment data found");
            return false;
        }
        for (int i = 0; i < bytes.length; i += 8) {
            long value = 0;
            for (int j = 0; j < 8; j++) {
                value |= (bytes[i + j] & 0xFFL) << (j * 8);
            }
            data[i / 8] = value;
        }
        return true;
    }

    @Override
    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        nbt.setShort(TAG_MAX_LEVEL, maxLevel);
        final int len = this.data.nonZeroLength();

        if (len == 0) return nbt;

        final int BYTES_PER_LONG = Long.SIZE / Byte.SIZE;
        final long[] data = this.data.data();
        final byte[] bytes = new byte[len * BYTES_PER_LONG];

        for (int i = 0; i < len; i++) {
            for (int j = 0; j < BYTES_PER_LONG; j++) {
                bytes[i * BYTES_PER_LONG + j] = (byte) ((data[i] >>> (j * Byte.SIZE)) & 0xFF);
            }
        }

        nbt.setByteArray(TAG_DATA, bytes);
        return nbt;
    }
}
