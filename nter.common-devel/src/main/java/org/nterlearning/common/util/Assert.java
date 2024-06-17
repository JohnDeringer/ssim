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

import org.hibernate.validator.constraints.impl.EmailValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public abstract class Assert extends org.springframework.util.Assert {

    private static final EmailValidator EMAIL_ADDRESS_VALIDATOR = new EmailValidator();

    public static void hasSize(@NotNull Object[] array, int size) {

        hasSize(array, size, null);

    }

    public static void hasSize(@NotNull Object[] array, int size, @Nullable String message) {

        isPositiveInteger(size);
        if (array.length != size) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void hasSize(@NotNull Collection<?> collection, int size) {

        hasSize(collection, size, null);

    }

    public static void hasSize(@NotNull Collection<?> collection, int size, @Nullable String message) {

        isPositiveInteger(size);
        if (collection.size() != size) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void isValidEmailAddress(@NotNull String emailAddress) {

        isValidEmailAddress(emailAddress, null);

    }

    public static void isValidEmailAddress(@NotNull String emailAddress, @Nullable String message) {

        hasText(emailAddress);
        if (!EMAIL_ADDRESS_VALIDATOR.isValid(emailAddress, null)) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void areValidEmailAddresses(@NotNull String... emailAddresses) {

        areValidEmailAddresses(Arrays.asList(emailAddresses), null);

    }

    public static void areValidEmailAddresses(@NotNull List<String> emailAddresses) {

        areValidEmailAddresses(emailAddresses, null);

    }

    public static void areValidEmailAddresses(@NotNull List<String> emailAddresses, @Nullable String message) {

        for (String emailAddress : emailAddresses) {

            isValidEmailAddress(emailAddress, message);

        }

    }

    public static void isValidInet4Address(@NotNull String inet4Address) {

        isValidInet4Address(inet4Address, null);

    }

    public static void isValidInet4Address(@NotNull String inet4Address, @Nullable String message) {

        hasText(inet4Address);
        if (!InetAddressUtil.isValidInet4Address(inet4Address)) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void isValidURL(@NotNull String url) {

        isValidURL(url, null);

    }

    public static void isValidURL(@NotNull String url, @Nullable String message) {

        hasText(url);
        try {

            new URL(url);

        } catch (MalformedURLException e) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void isValidDomainName(@NotNull String domainName) {

        isValidDomainName(domainName, null);

    }

    public static void isValidDomainName(@NotNull String domainName, @Nullable String message) {

        isValidDomainName(domainName, -1, null);

    }

    public static void isValidDomainName(@NotNull String domainName, int levels) {

        isValidDomainName(domainName, levels, null);

    }

    public static void isValidDomainName(@NotNull String domainName, int levels, @Nullable String message) {

        hasText(domainName);
        if (!DomainNameUtil.isValidDomainName(domainName, levels)) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void areValidDomainNames(@NotNull List<String> domainNames) {

        areValidDomainNames(domainNames, null);

    }

    public static void areValidDomainNames(@NotNull List<String> domainNames, @Nullable String message) {

        notEmpty(domainNames);
        if (!DomainNameUtil.areValidDomainNames(domainNames)) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void isValidSubdomainName(@NotNull String subdomainName) {

        isValidSubdomainName(subdomainName, null);

    }

    public static void isValidSubdomainName(@NotNull String subdomainName, @Nullable String message) {

        if (!DomainNameUtil.isValidSubdomainName(subdomainName)) {

            throw new IllegalArgumentException(message);

        }

    }


    public static void isPositiveNumber(@NotNull Number number) {

        isPositiveNumber(number, null);

    }

    public static void isPositiveNumber(@NotNull Number number, @Nullable String message) {

        notNull(number, message);
        if (number.doubleValue() < 1) {

            throw new IllegalArgumentException(message);

        }
    }

    public static void isPositiveInteger(int integer) {

        isPositiveInteger(integer, null);

    }

    public static void isPositiveInteger(int integer, @Nullable String message) {

        if (integer < 1) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void isPositiveLongInteger(@NotNull Long longInteger) {

        isPositiveLongInteger(longInteger, null);

    }

    public static void isPositiveLongInteger(@NotNull Long longInteger, @Nullable String message) {

        notNull(longInteger, message);
        if (longInteger < 1) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void isFalse(boolean expression) {

        isTrue(!expression);

    }

    public static void isFalse(boolean expression, @Nullable String message) {

        isTrue(!expression, message);

    }

    public static void notEmpty(@Nullable Properties properties) {

        notEmpty(properties, null);

    }

    public static void notEmpty(@Nullable Properties properties, @Nullable String message) {

        notNull(properties, message);
        if (properties != null && properties.isEmpty()) {

            throw new IllegalArgumentException(message);

        }

    }

    public static void notEmpty(@Nullable String value) {

        notEmpty(value, null);

    }

    public static void notEmpty(@Nullable String value, @Nullable String message) {

        notNull(value, message);
        if (value != null && value.isEmpty()) {

            throw new IllegalArgumentException(message);

        }

    }

}