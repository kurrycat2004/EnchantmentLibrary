package io.github.kurrycat2004.enchlib.util;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MethodLookupUtil {
    public static MethodHandles.Lookup lookup(Class<?> clazz) {
        MethodHandles.Lookup lookup;

        try {
            if (isJava9OrAbove()) {
                // For Java 9 and above, use MethodHandles.privateLookupIn
                Method privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
                lookup = MethodHandles.lookup();
                lookup = (MethodHandles.Lookup) privateLookupIn.invoke(null, clazz, lookup);
            } else {
                Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
                implLookupField.setAccessible(true);
                lookup = (MethodHandles.Lookup) implLookupField.get(null);
                lookup = lookup.in(clazz);
            }
            return lookup;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isJava9OrAbove() {
        String version = System.getProperty("java.specification.version");
        if (version == null) return false;
        if (version.startsWith("1.")) version = version.substring(2);
        int versionNumber = Integer.parseInt(version);
        return versionNumber >= 9;
    }
}
