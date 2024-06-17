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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Map;
import java.util.Set;

public class BeanValidator implements ConstraintValidatorFactory, InitializingBean,
    org.springframework.validation.Validator {

    @Autowired
    private ApplicationContext applicationContext;

    private Validator validator;

    public void afterPropertiesSet() throws Exception {

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
            .constraintValidatorFactory(this).buildValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {

        T validator;

        Map beansByNames = applicationContext.getBeansOfType(key);
        if (beansByNames.isEmpty()) {
            try {
                validator = key.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("could not instantiate constraint validator class '" +
                    key.getName() + "'", e);
            }
        } else if (beansByNames.size() > 1) {
            throw new RuntimeException("only one bean of type '" + key.getName() +
                "' is allowed in the application context");
        } else {
            validator = (T) beansByNames.values().iterator().next();
        }

        return validator;

    }

    public boolean supports(Class<?> clazz) {

        return true;

    }

    public void validate(Object target, Errors errors) {

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target);
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {

            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);

        }

    }

    public void validate(@NotNull Object bean) throws ValidationException {

        Assert.notNull(bean);

        String name = bean.getClass().getName();
        BindException bindException = new BindException(bean, name);
        validate(bean, bindException);
        if (bindException.hasErrors()) {

            throw new ValidationException("failed to validate bean " + bean, bindException);

        }

    }

}
