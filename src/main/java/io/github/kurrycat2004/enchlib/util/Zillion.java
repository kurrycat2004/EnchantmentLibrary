/*
 * This class ("Zillion.java") is a java version of the python script provided by https://kyodaisuu.github.io/illion/ licensed under the MIT License
 * Permalink: https://github.com/kyodaisuu/kyodaisuu.github.io/blob/b7b9536220c53cc5d1934425300e68003abebd07/illion/zillion.py
 **/

package io.github.kurrycat2004.enchlib.util;

import io.github.kurrycat2004.enchlib.util.annotations.NonnullByDefault;

@NonnullByDefault
public class Zillion {
    private static final String[] ISOLATE = {"", "ni", "mi", "bi", "tri", "quadri", "quinti", "sexti", "septi", "octi", "noni"};
    private static final String[] CW_UNI = {"", "un", "duo", "tre", "quattuor", "quin", "se", "septe", "octo", "nove"};
    private static final String[] TEN = {"", "deci", "viginti", "triginta", "quadraginta", "quinquaginta", "sexaginta", "septuaginta", "octoginta", "nonaginta"};
    private static final String[] HUN = {"", "centi", "ducenti", "trecenti", "quadringenti", "quingenti", "sescenti", "septingenti", "octingenti", "nongenti"};
    private static final String[] SIMP_UNI = {"", "un", "duo", "tre", "quattuor", "quin", "sex", "septen", "octo", "novem"};
    private static final String[] PREC_TEN = {"", "N", "MS", "NS", "NS", "NS", "N", "N", "MX", ""};
    private static final String[] PREC_HUN = {"", "NX", "N", "NS", "NS", "NS", "N", "N", "MX", ""};

    public static String getZillionName(int n, boolean modified) {
        if (n < 1) {
            throw new IllegalArgumentException("N<1 is not defined");
        }
        String name = "";
        while (n > 999) {
            name = concat(n % 1000, name, modified);
            n = n / 1000;
        }
        name = concat(n, name, modified);
        return name;
    }

    private static String concat(int n, String suffix, boolean modified) {
        String result = base(n, modified) + suffix;
        if (suffix.isEmpty()) {
            result += "on";
        }
        return result;
    }

    private static String base(int n, boolean modified) {
        if (n < 10) {
            return ISOLATE[n] + "lli";
        }

        int unit = n % 10;
        int ten = (n / 10) % 10;
        int hun = n / 100;
        String prec = ten == 0 ? PREC_HUN[hun] : PREC_TEN[ten];

        String name;
        if (modified) {
            name = SIMP_UNI[unit];
        } else {
            name = CW_UNI[unit];
            if (unit == 3 || unit == 6) {
                if (prec.contains("S")) {
                    name += "s";
                }
                if (prec.contains("X")) {
                    if (unit == 3) {
                        name = "tres";
                    } else {
                        name = "sex";
                    }
                }
            }
            if (unit == 7 || unit == 9) {
                if (prec.contains("M")) {
                    name += "m";
                }
                if (prec.contains("N")) {
                    name += "n";
                }
            }
        }
        name += TEN[ten];
        name += HUN[hun];

        return name.substring(0, name.length() - 1) + "illi";  // Replace the final vowel
    }
}