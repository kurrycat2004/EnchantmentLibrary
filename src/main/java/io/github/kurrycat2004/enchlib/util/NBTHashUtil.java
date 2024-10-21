package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import net.minecraft.nbt.*;

@NonnullByDefault
public class NBTHashUtil {
    private static final int FNV_32_PRIME = 0x01000193;        // 16777619
    private static final int FNV_32_OFFSET_BASIS = 0x811C9DC5; // 2166136261

    public static class NBTTypes {
        public static final byte END = 0;
        public static final byte BYTE = 1;
        public static final byte SHORT = 2;
        public static final byte INT = 3;
        public static final byte LONG = 4;
        public static final byte FLOAT = 5;
        public static final byte DOUBLE = 6;
        public static final byte BYTE_ARRAY = 7;
        public static final byte STRING = 8;
        public static final byte LIST = 9;
        public static final byte COMPOUND = 10;
        public static final byte INT_ARRAY = 11;
        public static final byte LONG_ARRAY = 12;
    }

    public static int getFNVHash(NBTBase nbt) {
        return fnvHashNBT(nbt, FNV_32_OFFSET_BASIS);
    }

    private static int fnvHashNBT(NBTBase nbt, int hash) {
        hash ^= nbt.getId();
        hash *= FNV_32_PRIME;

        switch (nbt.getId()) {
            case NBTTypes.END:
                // No additional data to hash
                break;
            case NBTTypes.BYTE:
                NBTTagByte tagByte = (NBTTagByte) nbt;
                hash ^= tagByte.getByte();
                hash *= FNV_32_PRIME;
                break;
            case NBTTypes.SHORT:
                NBTTagShort tagShort = (NBTTagShort) nbt;
                hash ^= tagShort.getShort();
                hash *= FNV_32_PRIME;
                break;
            case NBTTypes.INT:
                NBTTagInt tagInt = (NBTTagInt) nbt;
                hash ^= tagInt.getInt();
                hash *= FNV_32_PRIME;
                break;
            case NBTTypes.LONG:
                NBTTagLong tagLong = (NBTTagLong) nbt;
                long longValue = tagLong.getLong();
                hash ^= Long.hashCode(longValue);
                hash *= FNV_32_PRIME;
                break;
            case NBTTypes.FLOAT:
                NBTTagFloat tagFloat = (NBTTagFloat) nbt;
                hash ^= Float.floatToIntBits(tagFloat.getFloat());
                hash *= FNV_32_PRIME;
                break;
            case NBTTypes.DOUBLE:
                NBTTagDouble tagDouble = (NBTTagDouble) nbt;
                hash ^= Double.hashCode(tagDouble.getDouble());
                hash *= FNV_32_PRIME;
                break;
            case NBTTypes.BYTE_ARRAY:
                NBTTagByteArray tagByteArray = (NBTTagByteArray) nbt;
                byte[] byteArray = tagByteArray.getByteArray();
                for (byte b : byteArray) {
                    hash ^= b;
                    hash *= FNV_32_PRIME;
                }
                break;
            case NBTTypes.STRING:
                NBTTagString tagString = (NBTTagString) nbt;
                String value = tagString.getString();
                hash ^= value.hashCode();
                hash *= FNV_32_PRIME;
                break;
            case NBTTypes.LIST:
                NBTTagList tagList = (NBTTagList) nbt;
                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTBase element = tagList.get(i);
                    hash = fnvHashNBT(element, hash);
                }
                break;
            case NBTTypes.COMPOUND:
                NBTTagCompound tagCompound = (NBTTagCompound) nbt;
                for (String key : tagCompound.getKeySet()) {
                    hash ^= key.hashCode();
                    hash *= FNV_32_PRIME;
                    NBTBase valueTag = tagCompound.getTag(key);
                    hash = fnvHashNBT(valueTag, hash);
                }
                break;
            case NBTTypes.INT_ARRAY:
                NBTTagIntArray tagIntArray = (NBTTagIntArray) nbt;
                int[] intArray = tagIntArray.getIntArray();
                for (int i : intArray) {
                    hash ^= i;
                    hash *= FNV_32_PRIME;
                }
                break;
            case NBTTypes.LONG_ARRAY:
                NBTTagLongArray tagLongArray = (NBTTagLongArray) nbt;
                long[] longArray = tagLongArray.data;
                for (long l : longArray) {
                    int valueHash = Long.hashCode(l);
                    hash ^= valueHash;
                    hash *= FNV_32_PRIME;
                }
                break;
            default:
                // Unknown NBT type, fallback to vanilla
                hash ^= nbt.hashCode();
                hash *= FNV_32_PRIME;
                break;
        }

        return hash;
    }
}