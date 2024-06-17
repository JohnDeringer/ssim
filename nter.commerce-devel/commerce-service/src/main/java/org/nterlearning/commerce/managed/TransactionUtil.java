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

package org.nterlearning.commerce.managed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/22/12
 */
public class TransactionUtil {

    private static Logger logger = LoggerFactory.getLogger(TransactionUtil.class);

    @NotNull
    public static BigDecimal getPrice(@NotNull PaymentTransaction transaction) {
        BigDecimal avgPrice = new BigDecimal(0);
        BigDecimal gross =
            transaction.getPaymentGross() != null ?
                transaction.getPaymentGross().setScale(2, RoundingMode.HALF_EVEN) :
                null;

        BigDecimal qty =
            transaction.getQuantity() != null ?
                    new BigDecimal(transaction.getQuantity()).setScale(0, RoundingMode.HALF_EVEN) :
                    null;

        if (gross != null && qty != null && (qty.compareTo(BigDecimal.ZERO) > 0)) {
            try {
                avgPrice = gross.divide(qty, 2, RoundingMode.HALF_UP);
            } catch (Exception e) {
                logger.error("Error determining average price using PaymentGross [" +
                    gross + "] divided by qty [" + qty + "]", e);
            }
        } else {
            logger.debug("Unable to determine average price using PaymentGross [" +
                    gross + "] divided by qty [" + qty +
                    "] for transaction [" + transaction.getTransactionId() + "]");
        }

        return avgPrice;
    }

    @NotNull
    public static BigDecimal getFees(
            @NotNull PaymentTransaction transaction,
            @NotNull InstitutionType institutionType) {

        BigDecimal totalFees = new BigDecimal(0);
        BigDecimal adminFee =
            transaction.getAdminFee() != null ?
                transaction.getAdminFee() : new BigDecimal(0);
        BigDecimal referrerFee =
                    transaction.getReferrerFee() != null ?
                        transaction.getReferrerFee() : new BigDecimal(0);
        BigDecimal paymentFee =
                transaction.getPaymentFee() != null ?
                        transaction.getPaymentFee() : new BigDecimal(0);

        if (institutionType == InstitutionType.CONTENT_PROVIDER) {
            totalFees = totalFees.add(adminFee).add(referrerFee).add(paymentFee);
        } else if (institutionType == InstitutionType.BOTH) {
            totalFees = totalFees.add(adminFee).add(paymentFee);
        }

        return totalFees;
    }

    @NotNull
    public static BigDecimal getFees(
            @NotNull PaymentTransaction transaction,
            @Nullable PaymentTransaction refundTransaction,
            @NotNull InstitutionType institutionType) {

        BigDecimal totalFees = getFees(transaction, institutionType);

        if (refundTransaction != null) {
            BigDecimal refundReferrerFee =
                refundTransaction.getReferrerFee() != null ?
                        refundTransaction.getReferrerFee() : new BigDecimal(0);

            BigDecimal refundPaymentFee =
                refundTransaction.getPaymentFee() != null ?
                        refundTransaction.getPaymentFee() : new BigDecimal(0);

            if (institutionType == InstitutionType.CONTENT_PROVIDER) {
                totalFees = totalFees.add(refundReferrerFee).add(refundPaymentFee);
            } else if (institutionType == InstitutionType.BOTH) {
                totalFees = totalFees.add(refundPaymentFee);
            }
        }

        return totalFees;
    }

    @NotNull
    public static BigDecimal getNetRevenue(
            @NotNull PaymentTransaction transactionSummary,
            @NotNull InstitutionType institutionType) {

        BigDecimal netRevenue;

        BigDecimal gross =
            transactionSummary.getPaymentGross() != null ?
                transactionSummary.getPaymentGross() : new BigDecimal(0);

        if (institutionType == InstitutionType.NTER) {
            netRevenue = transactionSummary.getReferrerFee();
        } else {
            netRevenue =
                gross.subtract(
                    getFees(
                            transactionSummary,
                            institutionType
                    )
            );
        }

        return netRevenue;
    }

    @NotNull
    public static BigDecimal getNetRevenue(
            @NotNull PaymentTransaction transactionSummary,
            @Nullable PaymentTransaction refundTransactionSummary,
            @NotNull InstitutionType institutionType) {

        BigDecimal netRevenue =
                getNetRevenue(transactionSummary, institutionType);

        if (refundTransactionSummary != null) {
            BigDecimal refundGross = refundTransactionSummary.getPaymentGross();
            if (institutionType != InstitutionType.NTER) {
                netRevenue = netRevenue.add(refundGross);
            }
        }

        return netRevenue;
    }

    @NotNull
    public static InstitutionType getInstitutionType(
            @NotNull PaymentTransaction transaction,
            @NotNull String institutionName) {
        InstitutionType institutionType;
        String courseProviderId =
                    transaction.getCourseProviderId() != null ?
                            transaction.getCourseProviderId() : "";
        String nterId =
                    transaction.getNterId() != null ?
                            transaction.getNterId() : "";

        if (nterId.equals(institutionName) &&
                    courseProviderId.equals(institutionName)) {
            institutionType = InstitutionType.BOTH;
        } else if (courseProviderId.equals(institutionName)) {
            institutionType = InstitutionType.CONTENT_PROVIDER;
        } else if (nterId.equals(institutionName)) {
            institutionType = InstitutionType.NTER;
        } else {
            throw new RuntimeException(
                "Invalid transaction [" + transaction.getTransactionId() +
                "] nterId [" + nterId + "] courseProviderId [" + courseProviderId + "]");
        }

        return institutionType;
    }

    public static enum InstitutionType {
        NTER, CONTENT_PROVIDER, BOTH
    }

}
