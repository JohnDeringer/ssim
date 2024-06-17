/*
 * National Training and Education Resource (NTER)
 * Copyright (C) 2012  SRI International
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.nterlearning.commerce.service.paypal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 6/5/12
 */
public class PaymentItem {

    private String receiverEmail;
    private BigDecimal amount;
    private String note;

    public PaymentItem(@NotNull String receiverEmail,
                       @NotNull BigDecimal amount,
                       @Nullable String note) {
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.note = note;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

}
