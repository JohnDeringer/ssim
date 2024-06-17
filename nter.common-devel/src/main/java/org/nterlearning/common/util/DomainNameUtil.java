/*
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012 SRI International
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 */

package org.nterlearning.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DomainNameUtil {

    private static final String DOT = ".";
    private static final String DOT_DELIMITER = "\\.";

    private static final int MAX_DOMAIN_NAME_LENGTH = 255;
    private static final int MAX_DOMAIN_NAME_LABEL_LENGTH = 63;

    @NotNull
    public static String getFullDomainName(@NotNull String fullyQualifiedDomainName) {

        Assert.hasText(fullyQualifiedDomainName);
        Assert.isTrue(fullyQualifiedDomainName.endsWith(DOT));

        return fullyQualifiedDomainName.substring(0, fullyQualifiedDomainName.length() - 1);

    }

    @NotNull
    public static String getFullyQualifiedDomainName(@NotNull String domainName) {

        Assert.hasText(domainName);
        Assert.isFalse(domainName.endsWith(DOT));

        return domainName + DOT;

    }

    @NotNull
    public static String getFullDomainName(@NotNull String subdomainName,
                                           @NotNull String secondLevelDomainName) {

        Assert.hasText(subdomainName);
        Assert.hasText(secondLevelDomainName);

        return subdomainName + DOT + secondLevelDomainName;

    }

    @NotNull
    public static List<String> getFullDomainNames(@NotNull List<String> fullyQualifiedDomainNames) {

        Assert.notEmpty(fullyQualifiedDomainNames);

        List<String> fullDomainNames = new ArrayList<String>();
        for (String fullyQualifiedDomainName : fullyQualifiedDomainNames) {

            fullDomainNames.add(getFullDomainName(fullyQualifiedDomainName));

        }

        return fullDomainNames;

    }

    @NotNull
    public static String getSecondLevelDomainName(@NotNull String fullDomainName) {

        Assert.hasText(fullDomainName);
        String[] labels = fullDomainName.split(DOT_DELIMITER);
        Assert.isTrue(labels.length >= 2);

        return labels[labels.length - 2] + DOT + labels[labels.length - 1];

    }

    @Nullable
    public static String getSubdomainName(@NotNull String fullDomainName) {

        Assert.hasText(fullDomainName);
        String fullSubdomainName = null;
        String[] labels = fullDomainName.split(DOT_DELIMITER);
        Assert.isTrue(labels.length >= 2);
        if (labels.length > 2) {

            StringBuilder builder = new StringBuilder();
            for (int index = 0; index < labels.length - 2; index++) {

                builder.append(labels[index]);
                if (index != labels.length - 3) {

                    builder.append(DOT);

                }

            }

            fullSubdomainName = builder.toString();

        }

        return fullSubdomainName;

    }

    public static boolean isValidDomainNameLabel(@NotNull String domainNameLabel) {

        Assert.hasText(domainNameLabel);

        if (domainNameLabel.length() > MAX_DOMAIN_NAME_LABEL_LENGTH) {

            return false;

        }

        Matcher matcher = DOMAIN_LABEL_PATTERN.matcher(domainNameLabel);
        return matcher.matches();

    }

    public static boolean isValidSubdomainName(@NotNull String subdomainName) {

        Assert.hasText(subdomainName);
        String[] labels = subdomainName.split(DOT_DELIMITER);
        for (String label : labels) {

            if (!isValidDomainNameLabel(label)) {

                return false;

            }

        }

        return true;

    }

    public static boolean isValidDomainName(@NotNull String domainName) {

        return isValidDomainName(domainName, -1, true);

    }

    public static boolean isValidDomainName(@NotNull String domainName, int levels) {

        return isValidDomainName(domainName, levels, true);

    }

    public static boolean isValidDomainName(@NotNull String domainName, int levels, boolean allowLocal) {

        Assert.hasText(domainName);
        Assert.isTrue(levels == -1 || levels > 0);

        if (domainName.length() > MAX_DOMAIN_NAME_LENGTH) {

            return false;

        }

        boolean isValid = false;
        Matcher matcher = DOMAIN_NAME_PATTERN.matcher(domainName);
        if (matcher.matches()) {

            String[] labels = domainName.split(DOT_DELIMITER);
            if (levels != -1 && labels.length != levels) {

                return false;

            }

            for (String label : labels) {

                if (!isValidDomainNameLabel(label)) {

                    return false;

                }

            }

            String tld = matcher.group(matcher.groupCount());
            isValid = isValidTld(tld, allowLocal);

        } else if (allowLocal) {

            matcher = DOMAIN_LABEL_PATTERN.matcher(domainName);
            isValid = matcher.matches();

        }

        return isValid;

    }

    public static boolean areValidDomainNames(@NotNull List<String> domainNames) {

        Assert.notEmpty(domainNames);

        boolean areValidDomainNames = true;
        for (String domainName : domainNames) {

            if (!isValidDomainName(domainName)) {

                areValidDomainNames = false;
                break;

            }

        }

        return areValidDomainNames;

    }

    public static boolean isValidTld(@NotNull String tld) {

        Assert.hasText(tld);
        return isValidTld(tld, false);

    }

    public static boolean isValidTld(@NotNull String tld, boolean allowLocal) {

        Assert.hasText(tld);
        return allowLocal && isValidLocalTld(tld) || isValidGenericTld(tld) || isValidCountryCodeTld(tld);

    }

    public static boolean isValidGenericTld(@NotNull String gTld) {

        Assert.hasText(gTld);
        return GENERIC_TLD_LIST.contains(chompLeadingDot(gTld.toLowerCase()));

    }

    public static boolean isValidCountryCodeTld(@NotNull String ccTld) {

        Assert.hasText(ccTld);
        return COUNTRY_CODE_TLD_LIST.contains(chompLeadingDot(ccTld.toLowerCase()));

    }

    public static boolean isValidLocalTld(@NotNull String lTld) {

        Assert.hasText(lTld);
        return LOCAL_TLD_LIST.contains(chompLeadingDot(lTld.toLowerCase()));

    }

    private static String chompLeadingDot(String str) {

        String newStr = str;
        if (str.startsWith(".")) {

            newStr = str.substring(1);

        }

        return newStr;

    }

    private static final String DOMAIN_LABEL_REGEX = "\\p{Alnum}(?>[\\p{Alnum}-]*\\p{Alnum})*";
    private static final String TOP_LABEL_REGEX = "\\p{Alpha}{2,}";
    private static final String DOMAIN_NAME_REGEX =
        "^(?:" + DOMAIN_LABEL_REGEX + "\\.)+" + "(" + TOP_LABEL_REGEX + ")$";

    private static final Pattern DOMAIN_LABEL_PATTERN = Pattern.compile(DOMAIN_LABEL_REGEX);
    private static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile(DOMAIN_NAME_REGEX);

    // ---------------------------------------------
    // ----- TLDs defined by IANA
    // ----- Authoritative and comprehensive list at:
    // ----- http://data.iana.org/TLD/tlds-alpha-by-domain.txt

    private static final String[] GENERIC_TLDS = new String[]{
        "aero",               // air transport industry
        "asia",               // Pan-Asia/Asia Pacific
        "biz",                // businesses
        "cat",                // Catalan linguistic/cultural community
        "com",                // commercial enterprises
        "coop",               // cooperative associations
        "info",               // informational sites
        "jobs",               // Human Resource managers
        "mobi",               // mobile products and services
        "museum",             // museums, surprisingly enough
        "name",               // individuals' sites
        "net",                // internet support infrastructure/business
        "org",                // noncommercial organizations
        "pro",                // credentialed professionals and entities
        "tel",                // contact data for businesses and individuals
        "travel",             // entities in the travel industry
        "gov",                // United States Government
        "edu",                // accredited postsecondary US education entities
        "mil",                // United States Military
        "int"                 // organizations established by international treaty
    };

    private static final String[] COUNTRY_CODE_TLDS = new String[]{
        "ac",                 // Ascension Island
        "ad",                 // Andorra
        "ae",                 // United Arab Emirates
        "af",                 // Afghanistan
        "ag",                 // Antigua and Barbuda
        "ai",                 // Anguilla
        "al",                 // Albania
        "am",                 // Armenia
        "an",                 // Netherlands Antilles
        "ao",                 // Angola
        "aq",                 // Antarctica
        "ar",                 // Argentina
        "as",                 // American Samoa
        "at",                 // Austria
        "au",                 // Australia (includes Ashmore and Cartier Islands and Coral Sea Islands)
        "aw",                 // Aruba
        "ax",                 // Ã…land
        "az",                 // Azerbaijan
        "ba",                 // Bosnia and Herzegovina
        "bb",                 // Barbados
        "bd",                 // Bangladesh
        "be",                 // Belgium
        "bf",                 // Burkina Faso
        "bg",                 // Bulgaria
        "bh",                 // Bahrain
        "bi",                 // Burundi
        "bj",                 // Benin
        "bm",                 // Bermuda
        "bn",                 // Brunei Darussalam
        "bo",                 // Bolivia
        "br",                 // Brazil
        "bs",                 // Bahamas
        "bt",                 // Bhutan
        "bv",                 // Bouvet Island
        "bw",                 // Botswana
        "by",                 // Belarus
        "bz",                 // Belize
        "ca",                 // Canada
        "cc",                 // Cocos (Keeling) Islands
        "cd",                 // Democratic Republic of the Congo (formerly Zaire)
        "cf",                 // Central African Republic
        "cg",                 // Republic of the Congo
        "ch",                 // Switzerland
        "ci",                 // CÃ´te d'Ivoire
        "ck",                 // Cook Islands
        "cl",                 // Chile
        "cm",                 // Cameroon
        "cn",                 // China, mainland
        "co",                 // Colombia
        "cr",                 // Costa Rica
        "cu",                 // Cuba
        "cv",                 // Cape Verde
        "cx",                 // Christmas Island
        "cy",                 // Cyprus
        "cz",                 // Czech Republic
        "de",                 // Germany
        "dj",                 // Djibouti
        "dk",                 // Denmark
        "dm",                 // Dominica
        "do",                 // Dominican Republic
        "dz",                 // Algeria
        "ec",                 // Ecuador
        "ee",                 // Estonia
        "eg",                 // Egypt
        "er",                 // Eritrea
        "es",                 // Spain
        "et",                 // Ethiopia
        "eu",                 // European Union
        "fi",                 // Finland
        "fj",                 // Fiji
        "fk",                 // Falkland Islands
        "fm",                 // Federated States of Micronesia
        "fo",                 // Faroe Islands
        "fr",                 // France
        "ga",                 // Gabon
        "gb",                 // Great Britain (United Kingdom)
        "gd",                 // Grenada
        "ge",                 // Georgia
        "gf",                 // French Guiana
        "gg",                 // Guernsey
        "gh",                 // Ghana
        "gi",                 // Gibraltar
        "gl",                 // Greenland
        "gm",                 // The Gambia
        "gn",                 // Guinea
        "gp",                 // Guadeloupe
        "gq",                 // Equatorial Guinea
        "gr",                 // Greece
        "gs",                 // South Georgia and the South Sandwich Islands
        "gt",                 // Guatemala
        "gu",                 // Guam
        "gw",                 // Guinea-Bissau
        "gy",                 // Guyana
        "hk",                 // Hong Kong
        "hm",                 // Heard Island and McDonald Islands
        "hn",                 // Honduras
        "hr",                 // Croatia (Hrvatska)
        "ht",                 // Haiti
        "hu",                 // Hungary
        "id",                 // Indonesia
        "ie",                 // Ireland (Ã‰ire)
        "il",                 // Israel
        "im",                 // Isle of Man
        "in",                 // India
        "io",                 // British Indian Ocean Territory
        "iq",                 // Iraq
        "ir",                 // Iran
        "is",                 // Iceland
        "it",                 // Italy
        "je",                 // Jersey
        "jm",                 // Jamaica
        "jo",                 // Jordan
        "jp",                 // Japan
        "ke",                 // Kenya
        "kg",                 // Kyrgyzstan
        "kh",                 // Cambodia (Khmer)
        "ki",                 // Kiribati
        "km",                 // Comoros
        "kn",                 // Saint Kitts and Nevis
        "kp",                 // North Korea
        "kr",                 // South Korea
        "kw",                 // Kuwait
        "ky",                 // Cayman Islands
        "kz",                 // Kazakhstan
        "la",                 // Laos (currently being marketed as the official domain for Los Angeles)
        "lb",                 // Lebanon
        "lc",                 // Saint Lucia
        "li",                 // Liechtenstein
        "lk",                 // Sri Lanka
        "lr",                 // Liberia
        "ls",                 // Lesotho
        "lt",                 // Lithuania
        "lu",                 // Luxembourg
        "lv",                 // Latvia
        "ly",                 // Libya
        "ma",                 // Morocco
        "mc",                 // Monaco
        "md",                 // Moldova
        "me",                 // Montenegro
        "mg",                 // Madagascar
        "mh",                 // Marshall Islands
        "mk",                 // Republic of Macedonia
        "ml",                 // Mali
        "mm",                 // Myanmar
        "mn",                 // Mongolia
        "mo",                 // Macau
        "mp",                 // Northern Mariana Islands
        "mq",                 // Martinique
        "mr",                 // Mauritania
        "ms",                 // Montserrat
        "mt",                 // Malta
        "mu",                 // Mauritius
        "mv",                 // Maldives
        "mw",                 // Malawi
        "mx",                 // Mexico
        "my",                 // Malaysia
        "mz",                 // Mozambique
        "na",                 // Namibia
        "nc",                 // New Caledonia
        "ne",                 // Niger
        "nf",                 // Norfolk Island
        "ng",                 // Nigeria
        "ni",                 // Nicaragua
        "nl",                 // Netherlands
        "no",                 // Norway
        "np",                 // Nepal
        "nr",                 // Nauru
        "nu",                 // Niue
        "nz",                 // New Zealand
        "om",                 // Oman
        "pa",                 // Panama
        "pe",                 // Peru
        "pf",                 // French Polynesia With Clipperton Island
        "pg",                 // Papua New Guinea
        "ph",                 // Philippines
        "pk",                 // Pakistan
        "pl",                 // Poland
        "pm",                 // Saint-Pierre and Miquelon
        "pn",                 // Pitcairn Islands
        "pr",                 // Puerto Rico
        "ps",                 // Palestinian territories (PA-controlled West Bank and Gaza Strip)
        "pt",                 // Portugal
        "pw",                 // Palau
        "py",                 // Paraguay
        "qa",                 // Qatar
        "re",                 // RÃ©union
        "ro",                 // Romania
        "rs",                 // Serbia
        "ru",                 // Russia
        "rw",                 // Rwanda
        "sa",                 // Saudi Arabia
        "sb",                 // Solomon Islands
        "sc",                 // Seychelles
        "sd",                 // Sudan
        "se",                 // Sweden
        "sg",                 // Singapore
        "sh",                 // Saint Helena
        "si",                 // Slovenia
        "sj",                 // Svalbard and Jan Mayen Islands Not in use (Norwegian dependencies; see .no)
        "sk",                 // Slovakia
        "sl",                 // Sierra Leone
        "sm",                 // San Marino
        "sn",                 // Senegal
        "so",                 // Somalia
        "sr",                 // Suriname
        "st",                 // SÃ£o TomÃ© and PrÃ­ncipe
        "su",                 // Soviet Union (deprecated)
        "sv",                 // El Salvador
        "sy",                 // Syria
        "sz",                 // Swaziland
        "tc",                 // Turks and Caicos Islands
        "td",                 // Chad
        "tf",                 // French Southern and Antarctic Lands
        "tg",                 // Togo
        "th",                 // Thailand
        "tj",                 // Tajikistan
        "tk",                 // Tokelau
        "tl",                 // East Timor (deprecated old code)
        "tm",                 // Turkmenistan
        "tn",                 // Tunisia
        "to",                 // Tonga
        "tp",                 // East Timor
        "tr",                 // Turkey
        "tt",                 // Trinidad and Tobago
        "tv",                 // Tuvalu
        "tw",                 // Taiwan, Republic of China
        "tz",                 // Tanzania
        "ua",                 // Ukraine
        "ug",                 // Uganda
        "uk",                 // United Kingdom
        "um",                 // United States Minor Outlying Islands
        "us",                 // United States of America
        "uy",                 // Uruguay
        "uz",                 // Uzbekistan
        "va",                 // Vatican City State
        "vc",                 // Saint Vincent and the Grenadines
        "ve",                 // Venezuela
        "vg",                 // British Virgin Islands
        "vi",                 // U.S. Virgin Islands
        "vn",                 // Vietnam
        "vu",                 // Vanuatu
        "wf",                 // Wallis and Futuna
        "ws",                 // Samoa (formerly Western Samoa)
        "ye",                 // Yemen
        "yt",                 // Mayotte
        "yu",                 // Serbia and Montenegro (originally Yugoslavia)
        "za",                 // South Africa
        "zm",                 // Zambia
        "zw",                 // Zimbabwe
    };

    private static final String[] LOCAL_TLDS = new String[]{
        "localhost",           // RFC2606 defined
        "localdomain"          // Also widely used as localhost.localdomain
    };

    private static final List<String> GENERIC_TLD_LIST = Arrays.asList(GENERIC_TLDS);
    private static final List<String> COUNTRY_CODE_TLD_LIST = Arrays.asList(COUNTRY_CODE_TLDS);
    private static final List<String> LOCAL_TLD_LIST = Arrays.asList(LOCAL_TLDS);

}