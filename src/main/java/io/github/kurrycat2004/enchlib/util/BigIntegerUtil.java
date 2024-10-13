package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.config.settings.ClientSettings;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;

import java.math.BigInteger;

@NonnullByDefault
public class BigIntegerUtil {
    public static String toZillionCompressed(BigInteger n) {
        String s = n.toString();
        int l = s.length();
        if (l < 4) return s;
        int N = (l - 1) / 3;
        int R = l - N * 3;
        String zillionName = l < 7 ? "thousand" : Zillion.getZillionName(N, ClientSettings.INSTANCE.modified_conway_wechsler);
        zillionName = zillionName.substring(0, 1).toUpperCase() + zillionName.substring(1);
        return s.substring(0, R) + "." + s.substring(R, R + 2) + " " + zillionName;
    }
}
