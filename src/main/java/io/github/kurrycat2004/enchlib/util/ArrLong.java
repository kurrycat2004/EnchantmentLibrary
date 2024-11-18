package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;

@NonnullByDefault
public class ArrLong {
    private static final long MAX = 0xFFFFFFFFFFFFFFFFL;

    /**
     * Data array representing the number, from least to most significant longs.
     */
    private final long[] data;

    public ArrLong(int size) {
        data = new long[size];
    }

    @SuppressWarnings("IncompleteCopyConstructor")
    public ArrLong(final ArrLong other) {
        this.data = new long[other.data.length];
        System.arraycopy(other.data, 0, data, 0, data.length);
    }

    private boolean underflows(long result, long original) {
        return Long.compareUnsigned(result, original) > 0;
    }

    private boolean overflows(long result, long original) {
        return Long.compareUnsigned(result, original) < 0;
    }

    public int addAt(int bitIndex, int value) {
        final long val = value & 0xFFFFFFFFL;
        final int bitOffset = bitIndex % Long.SIZE;
        final int wordOffset = bitIndex / Long.SIZE;
        int wordIndex = wordOffset;

        if (wordIndex >= data.length) return value;

        final long lowerPart = val << bitOffset;
        final long upperPart = bitOffset == 0 ? 0 : (val >>> (Long.SIZE - bitOffset));
        long remaining = val;

        // lower part
        long oldValue = data[wordIndex];
        long sum = oldValue + lowerPart;

        data[wordIndex] = sum;

        boolean overflow = overflows(sum, oldValue);
        long added = (overflow ? MAX : sum) - oldValue;
        remaining -= added >>> bitOffset;

        wordIndex++;
        if (wordIndex >= data.length) {
            if (overflow) data[wordIndex - 1] = MAX;
            return (int) remaining;
        }

        // upper part
        oldValue = data[wordIndex];
        sum = oldValue + upperPart + (overflow ? 1 : 0);

        data[wordIndex] = sum;

        overflow = overflows(sum, oldValue);
        added = (overflow ? MAX : sum) - oldValue;
        remaining -= added << (Long.SIZE - bitOffset);

        wordIndex++;
        // propagate carry
        long carry = overflow ? 1 : 0;
        while (carry != 0 && wordIndex < data.length) {
            sum = data[wordIndex] + carry;
            carry = overflows(sum, data[wordIndex]) ? 1 : 0;
            data[wordIndex] = sum;
            wordIndex++;
        }

        if (carry == 0) return 0;
        for (int i = wordOffset + 1; i < data.length; i++) {
            data[i] = MAX;
        }
        data[wordOffset] |= MAX << bitOffset;
        return (int) remaining;
    }

    public int subAt(int bitIndex, int value) {
        final long val = value & 0xFFFFFFFFL;
        final int bitOffset = bitIndex % Long.SIZE;
        final int wordOffset = bitIndex / Long.SIZE;
        int wordIndex = wordOffset;

        if (wordIndex >= data.length) return value;

        final long lowerPart = val << bitOffset;
        final long upperPart = bitOffset == 0 ? 0 : (val >>> (Long.SIZE - bitOffset));
        long remaining = val;

        // lower part
        long oldValue = data[wordIndex];
        long diff = oldValue - lowerPart;
        data[wordIndex] = diff;

        boolean underflow = underflows(diff, oldValue);
        long subtracted = oldValue - (underflow ? 0 : diff);
        remaining -= (subtracted >>> bitOffset);

        wordIndex++;
        if (wordIndex >= data.length) {
            if (underflow) data[wordIndex - 1] = 0;
            return (int) remaining;
        }

        // upper part
        oldValue = data[wordIndex];
        diff = oldValue - upperPart - (underflow ? 1 : 0);
        data[wordIndex] = diff;

        underflow = underflows(diff, oldValue);
        subtracted = oldValue - (underflow ? 0 : diff);
        remaining -= (subtracted << (Long.SIZE - bitOffset));

        wordIndex++;
        // Propagate borrow
        long borrow = underflow ? 1 : 0;
        while (borrow != 0 && wordIndex < data.length) {
            diff = data[wordIndex] - borrow;
            borrow = underflows(diff, data[wordIndex]) ? 1 : 0;
            data[wordIndex] = diff;
            wordIndex++;
        }

        if (borrow == 0) return 0;
        for (int i = wordOffset + 1; i < data.length; i++) {
            data[i] = 0;
        }
        data[wordOffset] &= ~(MAX << bitOffset);
        return (int) remaining;
    }

    public long wordAtBit(int bitIndex, long defaultValue) {
        final int wordIndex = bitIndex / Long.SIZE;
        if (wordIndex >= data.length) return defaultValue;
        return data[wordIndex];
    }

    public long wordAt(int index, long defaultValue) {
        if (index < 0 || index >= data.length) return defaultValue;
        return data[index];
    }

    public void copyFrom(ArrLong other) {
        System.arraycopy(other.data, 0, data, 0, Math.min(data.length, other.data.length));
        if (data.length > other.data.length) {
            for (int i = other.data.length; i < data.length; i++) {
                data[i] = 0;
            }
        }
    }

    public int numberOfLeadingZeros() {
        for (int i = data.length - 1; i >= 0; i--) {
            if (data[i] != 0) {
                return Long.numberOfLeadingZeros(data[i]) + (data.length - i - 1) * Long.SIZE;
            }
        }
        return data.length * Long.SIZE;
    }

    public int highestBit() {
        return Long.SIZE * data.length - numberOfLeadingZeros() - 1;
    }

    public int nonZeroLength() {
        for (int i = data.length - 1; i >= 0; i--) {
            if (data[i] != 0) return i + 1;
        }
        return 0;
    }

    public long unalignedULongFrom(int bit) {
        int index = bit / Long.SIZE;
        int shift = bit % Long.SIZE;
        return (wordAt(index, 0) >>> shift) | (wordAt(index + 1, 0) << (Long.SIZE - shift));
    }

    public long unalignedULongTo(int bit) {
        int index = bit / Long.SIZE;
        int shift = bit % Long.SIZE;
        return (wordAt(index, 0) << (Long.SIZE - (shift + 1))) | (wordAt(index - 1, 0) >>> (shift + 1));
    }

    /**
     * Returns the backing long array. <br>
     * The returned value is <strong>not</strong> a copy of the backing array.
     */
    public long[] data() {
        return data;
    }

    public String toHexString() {
        StringBuilder sb = new StringBuilder();
        for (int i = data.length - 1; i >= 0; i--) {
            if (i != data.length - 1) sb.append(" ");
            sb.append(String.format("%016X", data[i]));
        }
        return sb.toString();
    }

    public static String longToBinaryString(long value) {
        String bin = Long.toBinaryString(value);
        return String.format("%64s", bin).replace(' ', '0');
    }

    public String toBinaryString() {
        StringBuilder sb = new StringBuilder();
        for (int i = data.length - 1; i >= 0; i--) {
            if (i != data.length - 1) sb.append(" ");
            sb.append(longToBinaryString(data[i]));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toHexString();
    }
}
