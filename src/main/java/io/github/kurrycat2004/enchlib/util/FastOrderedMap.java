package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("SuspiciousMethodCalls")
@NonnullByDefault
public class FastOrderedMap<K, V> implements Map<K, V> {
    private final Object2ObjectOpenHashMap<K, V> hashMap;
    private final List<K> sortedKeys;
    private final Comparator<? super K> comparator;

    public FastOrderedMap(Comparator<? super K> comparator) {
        this.hashMap = new Object2ObjectOpenHashMap<>();
        this.sortedKeys = new ArrayList<>();
        this.comparator = comparator;
    }

    /// <code>O(log n)</code>
    public int indexOfKey(K key) {
        int index = Collections.binarySearch(sortedKeys, key, comparator);
        return index >= 0 ? index : -1;
    }

    /// <code>O(1)</code>
    public K keyAt(int index) {
        return sortedKeys.get(index);
    }

    /// <code>O(1)</code>
    public @Nullable K tryKeyAt(int index) {
        if (index < 0 || index >= sortedKeys.size()) return null;
        return keyAt(index);
    }

    /// <code>O(1)</code>
    public V valueAt(int index) {
        K key = keyAt(index);
        return hashMap.get(key);
    }

    /// <code>O(1)</code>
    public @Nullable V tryValueAt(int index) {
        K key = tryKeyAt(index);
        if (key == null) return null;
        return hashMap.get(key);
    }

    /// <code>O(1)</code>
    public Map.Entry<K, V> entryAt(int index) {
        K key = keyAt(index);
        return new AbstractMap.SimpleEntry<>(key, hashMap.get(key));
    }

    /// <code>O(1)</code>
    public @Nullable Map.Entry<K, V> tryEntryAt(int index) {
        K key = tryKeyAt(index);
        if (key == null) return null;
        return new AbstractMap.SimpleEntry<>(key, hashMap.get(key));
    }

    /// <code>O(1)</code>
    @Override
    public int size() {
        return hashMap.size();
    }

    /// <code>O(1)</code>
    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    /// <code>O(1)</code>
    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    /// <code>O(n)</code>
    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    /// <code>O(1)</code>
    @Override
    public @Nullable V get(Object key) {
        return hashMap.get(key);
    }

    /// <code>O(log n)</code>
    @Override
    public V put(K key, V value) {
        if (!hashMap.containsKey(key)) {
            int insertionPoint = Collections.binarySearch(sortedKeys, key, comparator);
            if (insertionPoint < 0) insertionPoint = -insertionPoint - 1;
            sortedKeys.add(insertionPoint, key);
        }
        return hashMap.put(key, value);
    }

    /// <code>O(n)</code>
    @Override
    public V remove(Object key) {
        sortedKeys.remove(key);
        return hashMap.remove(key);
    }

    /// <code>O(m log n)</code>
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /// <code>O(n)</code>
    @Override
    public void clear() {
        sortedKeys.clear();
        hashMap.clear();
    }

    /// <code>O(1)</code>
    @Override
    public Set<K> keySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<>() {
                    private final Iterator<K> keyIterator = sortedKeys.iterator();
                    private K currentKey;

                    @Override
                    public boolean hasNext() {
                        return keyIterator.hasNext();
                    }

                    @Override
                    public K next() {
                        currentKey = keyIterator.next();
                        return currentKey;
                    }

                    @Override
                    public void remove() {
                        keyIterator.remove();
                        hashMap.remove(currentKey);
                    }
                };
            }

            @Override
            public int size() {
                return sortedKeys.size();
            }

            @Override
            public boolean remove(Object key) {
                if (hashMap.containsKey(key)) {
                    sortedKeys.remove(key);
                    hashMap.remove(key);
                    return true;
                }
                return false;
            }

            @Override
            public void clear() {
                sortedKeys.clear();
                hashMap.clear();
            }
        };
    }

    /// <code>O(1)</code>
    @Override
    public Collection<V> values() {
        return new AbstractCollection<>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<>() {
                    private final Iterator<K> keyIterator = sortedKeys.iterator();
                    private K currentKey;

                    @Override
                    public boolean hasNext() {
                        return keyIterator.hasNext();
                    }

                    @Override
                    public V next() {
                        currentKey = keyIterator.next();
                        return hashMap.get(currentKey);
                    }

                    @Override
                    public void remove() {
                        keyIterator.remove();
                        hashMap.remove(currentKey);
                    }
                };
            }

            @Override
            public int size() {
                return hashMap.size();
            }

            @Override
            public void clear() {
                sortedKeys.clear();
                hashMap.clear();
            }
        };
    }

    /// <code>O(1)</code>
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<>() {
                    private final Iterator<K> keyIterator = sortedKeys.iterator();
                    private K currentKey;

                    @Override
                    public boolean hasNext() {
                        return keyIterator.hasNext();
                    }

                    @Override
                    public Map.Entry<K, V> next() {
                        currentKey = keyIterator.next();
                        return new AbstractMap.SimpleEntry<>(currentKey, hashMap.get(currentKey));
                    }

                    @Override
                    public void remove() {
                        keyIterator.remove();
                        hashMap.remove(currentKey);
                    }
                };
            }

            @Override
            public int size() {
                return hashMap.size();
            }

            @Override
            public void clear() {
                sortedKeys.clear();
                hashMap.clear();
            }
        };
    }
}