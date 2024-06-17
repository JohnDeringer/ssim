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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class ApplicationContextUtil {

    public static final String CONFIGURATION_BEAN_NAME = "configuration";

    private static final char CONFIGURATION_LIST_DELIMITER = ',';

    private static final String CONFIGURATION_FILE_NAME = "config.properties";

    private static final String NTER_PROPS_ENV_VAR = "NTER_PROPS";

    @NotNull
    public static ApplicationContext newApplicationContext(@NotNull String... beanConfigClasses)
        throws IOException, ConfigurationException, ClassNotFoundException {

        List<Class<?>> _beanConfigClasses = new ArrayList<Class<?>>();
        for (String beanConfigClass : beanConfigClasses) {

            _beanConfigClasses.add(Class.forName(beanConfigClass));

        }

        return newApplicationContext(_beanConfigClasses, null);

    }

    @NotNull
    public static ApplicationContext newApplicationContext(@NotNull Class<?>... beanConfigClasses)
        throws IOException, ConfigurationException {

        return newApplicationContext(Arrays.asList(beanConfigClasses), null);

    }

    @NotNull
    public static ApplicationContext newApplicationContext(@NotNull List<Class<?>> beanConfigClasses)
        throws IOException, ConfigurationException {

        return newApplicationContext(beanConfigClasses, null);

    }

    @NotNull
    public static ApplicationContext newApplicationContext(@NotNull List<Class<?>> beanConfigClasses,
                                                           @Nullable Map<String, Object> contextSingletons)
        throws IOException, ConfigurationException {

        Assert.notEmpty(beanConfigClasses);

        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        compositeConfiguration.setDelimiterParsingDisabled(false);
        compositeConfiguration.setListDelimiter(CONFIGURATION_LIST_DELIMITER);
        compositeConfiguration.setThrowExceptionOnMissing(false);

        compositeConfiguration.addConfiguration(new SystemConfiguration());

        String nterProperties = System.getenv(NTER_PROPS_ENV_VAR);
        if (nterProperties != null) {

            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(nterProperties);
            compositeConfiguration.addConfiguration(propertiesConfiguration);

        }

        compositeConfiguration.addConfiguration(new EnvironmentConfiguration());

        List<String> basePackages = new ArrayList<String>();
        for (Class<?> beanConfigClass : beanConfigClasses) {

            Annotation configurationAnnotation = beanConfigClass.getAnnotation(
                org.springframework.context.annotation.Configuration.class);
            Assert.notNull(configurationAnnotation,
                beanConfigClass + " is not a Spring Framework @Configuration annotated class");

            basePackages.add(beanConfigClass.getPackage().getName());

            Resource configurationResource = ResourceUtil.getClassPathResource(
                beanConfigClass, CONFIGURATION_FILE_NAME);
            if (configurationResource.isReadable()) {

                PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
                    configurationResource.getURL());
                compositeConfiguration.addConfiguration(propertiesConfiguration);

            }

        }

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton(CONFIGURATION_BEAN_NAME, compositeConfiguration);

        if (contextSingletons != null) {

            for (String key : contextSingletons.keySet()) {

                beanFactory.registerSingleton(key, contextSingletons.get(key));

            }

        }

        applicationContext.scan(basePackages.toArray(new String[basePackages.size()]));
        applicationContext.refresh();

        return applicationContext;

    }

}