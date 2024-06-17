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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

public abstract class ResourceUtil {

    private static final char DOT_CHAR = '.';
    private static final char FORWARD_SLASH_CHAR = '/';

    private static final String PROPERTIES_FILE_EXTENSION = ".properties";

    private static final String UTF_8_CHARACTER_ENCODING = "UTF-8";

    @NotNull
    public static Resource getClassPathResource(@NotNull Object object, @NotNull String name) {

        Assert.notNull(object);
        Assert.hasText(name);

        return getClassPathResource(object.getClass(), name);

    }

    @NotNull
    public static Resource getClassPathResource(@NotNull Class<?> clazz, @NotNull String name) {

        Assert.notNull(clazz);
        Assert.hasText(name);

        String packagePath = clazz.getPackage().getName().replace(DOT_CHAR, FORWARD_SLASH_CHAR);

        return new ClassPathResource(packagePath + FORWARD_SLASH_CHAR + name);

    }

    @NotNull
    public static Properties getClassPathProperties(@NotNull Object object, @NotNull String name)
        throws IOException {

        Assert.notNull(object);
        Assert.hasText(name);

        return getClassPathProperties(object.getClass(), name);

    }

    @NotNull
    public static Properties getClassPathProperties(@NotNull Class<?> clazz, @NotNull String name)
        throws IOException {

        Assert.notNull(clazz);
        Assert.hasText(name);

        Resource propertiesResource = getClassPathResource(clazz, name + PROPERTIES_FILE_EXTENSION);
        Properties properties = new Properties();
        properties.load(propertiesResource.getInputStream());

        return properties;

    }

    @NotNull
    public static String getTextClassPathResource(@NotNull Class<?> clazz, @NotNull String name)
        throws IOException {

        Assert.notNull(clazz);
        Assert.hasText(name);

        Resource resource = getClassPathResource(clazz, name);
        byte[] bytes = new byte[(int) resource.contentLength()];
        int length = resource.getInputStream().read(bytes);
        Assert.isTrue(length == resource.contentLength());

        return new String(bytes, UTF_8_CHARACTER_ENCODING);

    }

    @NotNull
    public static String getTextClassPathResource(@NotNull Object object, @NotNull String name)
        throws IOException {

        Assert.notNull(object);
        Assert.hasText(name);

        return getTextClassPathResource(object.getClass(), name);

    }

}
