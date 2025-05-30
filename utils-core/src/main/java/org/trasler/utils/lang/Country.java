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

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Encapsulation of ISO standard country codes. This implementation specifically
 * does not use an enumeration of codes because that is fragile. While the
 * enumeration does not change often, it is not static.
 *
 * @author Simon Trasler
 */
public class Country {
    private final String alpha2;
    private final String alpha3;

    public static final Country UNKNOWN = new Country("--", "---");

    public static final Set<Country> EU_COUNTRIES = Country.of(CountryCodes.EU_COUNTRY_CODES);

    public static final Set<Country> EFTA_COUNTRIES = Country.of(CountryCodes.EFTA_COUNTRY_CODES);

    public static final Set<Country> EEA_COUNTRIES = Country.of(CountryCodes.EEA_COUNTRY_CODES);

    public static final Set<Country> GDPR_COUNTRIES = Country.of(CountryCodes.GDPR_COUNTRY_CODES);

    private Country(String alpha2, String alpha3) {
        assert(alpha2.length() == 2);
        this.alpha2 = alpha2;

        assert(alpha3.length() == 3);
        this.alpha3 = alpha3;
    }

    /**
     * Get the ISO-3166 alpha-2 code for this country, in uppercase.
     *
     * @return The code
     */
    public String getAlpha2() {
        return alpha2;
    }

    /**
     * Get the ISO-3166 alpha-3 code for this country, in uppercase.
     *
     * @return The code
     */
    public String getAlpha3() {
        return alpha3;
    }

    /**
     * Construct an instance of Country for this ISO-3166 standard code, whether
     * alpha-2 or alpha-3, uppercase or lowercase. The objects created from the
     * same codes are equivalent. If the input code is not recognized, a default
     * object is returned.
     *
     * @param code The code
     * @return The corresponding Country object
     */
    public static Country of(String code) {
        String otherCode;

        if (code != null) {
            // Ensure all strings are in upper case.
            code = code.toUpperCase();

            otherCode = CountryCodes.getAlpha3CountryCode(code);
            if (otherCode != null) {
                return new Country(code, otherCode);
            }

            otherCode = CountryCodes.getAlpha2CountryCode(code);
            if (otherCode != null) {
                return new Country(otherCode, code);
            }
        }

        return UNKNOWN;
    }

    /**
     * Construct a set of Country objects corresponding to the provided list of
     * ISO-3166 standard codes, whether alpha-2 or alpha-3, uppercase or
     * lowercase. The objects created from the same codes are equivalent. If any
     * input code is not recognized, a default object is returned in that case.
     *
     * @param codes A collection of codes
     * @return The set of corresponding Country objects
     */
    public static Set<Country> of(Collection<String> codes) {
        return codes.stream()
            .map(code -> Country.of(code))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(alpha2);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other != null && other instanceof Country that) {
            return this.alpha2.equals(that.alpha2);
        }

        return false;
    }

    /**
     * Return the ISO-3166 alpha-3 code. As OpenRTB uses the alpha-3 code, this
     * code reports the same for the sake of consistency.
     *
     * @return The string representation of this object
     */
    @Override
    public String toString() {
        return alpha3;
    }
}
