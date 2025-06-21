package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.config.settings.ClientSettings;
import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;

import java.util.Locale;

@NonnullByDefault
public class ArrLongUtil {
    public static double LOG10_2 = Math.log10(2.0);

    public static String toZillion(ArrLong arrLong) {
        final long[] data = arrLong.data();
        int highestBit = arrLong.highestBit();
        if (highestBit < 64 && Long.compareUnsigned(data[0], 1_000_000) < 0) {
            if (data[0] < 1_000) return Long.toString(data[0]);
            return String.format(Locale.US, "%.2f thousand", data[0] / 1_000.0);
        }

        int startBit = highestBit < 64 ? 0 : highestBit - 63;
        long word = arrLong.unalignedULongFrom(startBit);
        double value = MathUtil.uLongToDouble(word);
        double nLog10 = Math.log10(value) + startBit * LOG10_2;

        int N = (int) Math.floor(nLog10 / 3.0);

        double exponentIn3K = nLog10 - 3.0 * N;
        double X = Math.pow(10.0, exponentIn3K);

        while (X >= 1_000) {
            X /= 1_000;
            N += 1;
        }

        if (N < 0) {
            return "Error. Report this";
        }

        return String.format(Locale.US, "%.2f", X) + " " + Zillion.getZillionName(N, ClientSettings.INSTANCE.modified_conway_wechsler);
    }
}
