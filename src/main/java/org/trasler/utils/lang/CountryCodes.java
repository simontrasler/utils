/**
 * The MIT License
 *
 * Copyright 2025 Simon Trasler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.trasler.utils.lang;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * Helper class for ISO-3166 country codes. It provides canned collections of
 * country codes, and a bi-directional map between alpha-2 and alpha-3 codes.
 * All codes are in uppercase.
 *
 * @author Simon Trasler
 */
public class CountryCodes {
    public static final Set<String> EU_COUNTRY_CODES = Set.of(
            "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE",
            "FI", "FR", "DE", "GR", "HU", "IE", "IT", "LV",
            "LT", "LU", "MT", "NE", "PL", "PT", "RO", "SK",
            "SI", "ES", "SE");

    public static final Set<String> EFTA_COUNTRY_CODES = Set.of("IS", "LI", "NO");

    public static final Set<String> EEA_COUNTRY_CODES = CollectionUtils.union(EU_COUNTRY_CODES, EFTA_COUNTRY_CODES);

    public static final Set<String> GDPR_COUNTRY_CODES = CollectionUtils.union(EEA_COUNTRY_CODES, Set.of("GB"));

    private static final BidiMap<String, String> MAP = new DualHashBidiMap<>();

    static {
        MAP.clear();

        Locale[] availableLocales = Locale.getAvailableLocales();

        for (Locale locale : availableLocales) {
            String alpha2 = locale.getCountry();

            if (alpha2.length() == 2) {
                try {
                    MAP.put(alpha2, locale.getISO3Country());
                } catch (MissingResourceException e) {
                    // Suppress, as this exception will occur for some codes that
                    // are reserved in ISO-3166 though not formally recognized,
                    // e.g., CS for Czechoslovakia and XK for Kosovo.
                }
            }
        }
    }

    /**
     * Map the ISO-3166 alpha-2 country code to its alpha-3 equivalent.
     *
     * @param alpha2 The alpha-2 country code in upper case
     * @return The corresponding alpha-3 country code in upper case, if
     * available, null otherwise
     */
    public static String getAlpha3CountryCode(String alpha2) {
        return MAP.get(alpha2);
    }

    /**
     * Map the ISO-3166 alpha-3 country code to its alpha-2 equivalent.
     *
     * @param alpha3 The alpha-3 country code in upper case
     * @return The corresponding alpha-2 country code in upper case, if
     * available, null otherwise
     */
    public static String getAlpha2CountryCode(String alpha3) {
        return MAP.inverseBidiMap().get(alpha3);
    }
}
