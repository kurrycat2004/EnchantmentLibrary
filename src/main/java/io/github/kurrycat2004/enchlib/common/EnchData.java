package io.github.kurrycat2004.enchlib.common;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import io.github.kurrycat2004.enchlib.util.interfaces.INBTSerDe;
import net.minecraft.nbt.NBTTagCompound;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.TreeSet;


@NonnullByDefault
public class EnchData implements INBTSerDe {
    private static final short MAX_LOW_LEVEL = Long.SIZE;

    private static final String TAG_LOW = "low";
    private static final String TAG_HIGH = "high";
    private static final String TAG_MAX_LEVEL = "max";

    private long low;
    // TODO: replace with a more efficient data structure, maybe store groups with start and end?
    private final TreeSet<Short> high;
    private short maxLevel;

    public EnchData() {
        this.low = 0;
        this.high = new TreeSet<>();
        this.maxLevel = 0;
    }

    public EnchData copy() {
        EnchData copy = new EnchData();
        copy.low = low;
        copy.high.addAll(high);
        copy.maxLevel = maxLevel;
        return copy;
    }

    public void copyFrom(EnchData other) {
        low = other.low;
        high.clear();
        high.addAll(other.high);
        maxLevel = other.maxLevel;
    }

    private BigInteger fromUnsignedLong(long value) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return new BigInteger(1, bytes);
    }

    public BigInteger getPointsB() {
        if (high.isEmpty() && low > 0) return BigInteger.valueOf(low);
        if (high.isEmpty()) return fromUnsignedLong(low);

        int maxValue = high.last();
        int byteArrayLength = (maxValue / 8) + 1;

        byte[] byteArray = new byte[byteArrayLength];

        for (short value : high) {
            int byteIndex = value / 8;
            int bitIndex = value % 8;
            byteArray[byteArrayLength - byteIndex] |= (byte) (1 << bitIndex);
        }

        return new BigInteger(1, byteArray);
    }

    public long getPoints() {
        if (high.isEmpty() && low > 0) return low;
        return Long.MAX_VALUE;
    }

    public int getPointsI() {
        if (high.isEmpty() && low > 0 && low < Integer.MAX_VALUE) return (int) low;
        return Integer.MAX_VALUE;
    }

    public short getMaxLevelExtractable() {
        return (short) Math.min(
                high.isEmpty() ? (MAX_LOW_LEVEL - Long.numberOfLeadingZeros(low)) : high.last(),
                maxLevel
        );
    }

    public int getNumberOfLevelExtractable(short level) {
        if (level <= 0) return 0;
        short shift = (short) (level - 1);
        // early return if amount would guarantee to overflow int (-1 to account for the sign bit)
        if (!high.isEmpty() && high.last() - shift >= Integer.SIZE - 1) return Integer.MAX_VALUE;

        long longExtractable = low >>> shift;
        // if casting to int would overflow, return max value (+1 to account for the sign bit)
        if (Long.numberOfLeadingZeros(longExtractable) <= Integer.SIZE + 1) return Integer.MAX_VALUE;
        // this is safe, because we checked above that it cant overflow
        int extractable = (int) longExtractable;
        // if high is empty, we're done
        if (high.isEmpty()) return extractable;

        // if high is not empty, we need to set the bits in extractable to 1 for each (level - shift) in high
        // we know that last() - shift < Integer.SIZE - 1 because of the first check, so we can safely set the bits

        for (Iterator<Short> it = high.descendingIterator(); it.hasNext(); ) {
            short h = it.next();
            // can't set bits with negative index
            if (h - shift < 0) break;
            // this will never set bits already set by low, because
            // high.first() > MAX_LOW_LEVEL => high.first() - shift > MAX_LOW_LEVEL - shift
            extractable |= 1 << (h - shift);
        }

        return extractable;
    }

    public short getMaxLevel() {
        return maxLevel;
    }

    public void addN(int amount, short level) {
        if (level <= 0) return;
        for (int i = Integer.SIZE - 1 - Integer.numberOfLeadingZeros(amount); i >= 0; i--) {
            if ((amount & (1 << i)) == 0) continue;
            add(level, (short) i);
        }
    }

    private void add(short level, short amountExtra) {
        if (level > maxLevel) maxLevel = level;
        level += amountExtra;

        if (level <= MAX_LOW_LEVEL) addLow(level);
        else addHigh(level);
    }

    private void addLow(short level) {
        long bit = 1L << (level - 1);
        long mask = -bit;  // Mask for the current bit and all bits above it

        // If not all the bits at or above the current bit are set, simply add the bit
        if ((low & mask) != mask) {
            low += bit;
            return;
        }

        low &= ~mask; // Clear current bit and all bits above
        addHigh((short) (MAX_LOW_LEVEL + 1));
    }

    private void addHigh(short level) {
        if (high.contains(level)) {
            high.remove(level);
            addHigh((short) (level + 1));
        } else {
            high.add(level);
        }
    }

    public int removeN(int amount, short level, boolean simulate) {
        if (level <= 0) return amount;
        if (!simulate) return removeN(amount, level);

        long low = this.low;
        final TreeSet<Short> high = new TreeSet<>(this.high);
        int remaining = removeN(amount, level);
        this.low = low;
        this.high.clear();
        this.high.addAll(high);
        return remaining;
    }

    private int removeN(int amount, short level) {
        for (int i = Integer.SIZE - 1 - Integer.numberOfLeadingZeros(amount); i >= 0; i--) {
            if (amount < (1 << i)) continue;
            if (remove((short) (level + i))) amount -= 1 << i;
        }
        return amount;
    }

    private boolean remove(short level) {
        if (level <= MAX_LOW_LEVEL) return removeLow(level);
        return removeHigh(level);
    }

    private boolean removeLow(short level) {
        long bit = 1L << (level - 1);
        long mask = -bit;  // Mask for the current bit and all bits above it

        // If any bit at or above the current bit is set, simply subtract the bit
        if ((low & mask) != 0) {
            low -= bit;
            return true;
        }
        if (!removeHigh((short) (MAX_LOW_LEVEL + 1))) return false;

        low |= mask;  // Set all bits covered by the mask to 1
        return true;
    }

    private boolean removeHigh(short level) {
        if (high.remove(level)) return true;

        Short nextHigher = high.higher(level);
        if (nextHigher == null) return false;

        high.remove(nextHigher);

        for (int i = level; i < nextHigher; i++) {
            high.add((short) i);
        }

        return true;
    }

    private static String toBinaryString(long value) {
        String bin = Long.toBinaryString(value);
        return String.format("%64s", bin).replace(' ', '0');
    }

    @Override
    public String toString() {
        return "EnchData{" +
               "low=" + toBinaryString(low) +
               ", high=" + high +
               ", maxLevel=" + maxLevel +
               '}';
    }

    @Override
    public void readNBT(NBTTagCompound nbt) {
        low = nbt.getLong(TAG_LOW);
        maxLevel = nbt.getShort(TAG_MAX_LEVEL);
        high.clear();
        int[] highIntArray = nbt.getIntArray(TAG_HIGH);
        for (int h : highIntArray) {
            high.add((short) h);
        }
    }

    @Override
    public NBTTagCompound writeNBT(NBTTagCompound nbt) {
        nbt.setLong(TAG_LOW, low);
        nbt.setShort(TAG_MAX_LEVEL, maxLevel);
        int[] highIntArray = new int[high.size()];
        int i = 0;
        for (short s : high) {
            highIntArray[i] = s;
            i++;
        }
        nbt.setIntArray(TAG_HIGH, highIntArray);
        return nbt;
    }
}
