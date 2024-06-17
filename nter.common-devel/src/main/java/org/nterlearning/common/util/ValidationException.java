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
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class ValidationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationException() {

        super();

    }

    public ValidationException(@NotNull String message) {

        super(message);
        Assert.hasText(message);

    }

    public ValidationException(@NotNull String message, @NotNull BindException cause) {

        super(message, cause);
        Assert.hasText(message);
        Assert.notNull(cause);

    }

    public ValidationException(@NotNull Throwable cause) {

        super(cause);
        Assert.notNull(cause);

    }

    public BindingResult getBindingResult() {

        return (BindingResult) getCause();

    }

}
