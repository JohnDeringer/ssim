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

package org.nterlearning.commerce.client;

import org.nterlearning.commerce.transaction.client.PaymentStatus;
import org.nterlearning.commerce.transaction.client.PaymentTransaction;
import org.nterlearning.commerce.transaction.client.TransactionType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/11/12
 */
public interface TransactionClient {

    /**
     * Create a new Payment Transaction. This method is generally called by the
     * Notification Servlet. The Notification Servlet is called by the Payment Processor.
     * @param paymentTransaction The object containing the Payment Transaction data.
     */
    void createPaymentTransaction(PaymentTransaction paymentTransaction);

    /**
     * Retrieve a list of Payment Transactions
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    List<PaymentTransaction> getTransactions(Date fromDate, Date toDate);

    /**
     * Retrieve a Payment Transaction given a transactionId
     * @param transactionId The identifier of the transaction, supplied by the Payment Processor
     * @return A PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    PaymentTransaction getTransactionById(String transactionId);

    /**
     * Retrieve a list of Child Payment Transactions (e.g. refunds, etc)  given a transactionId
     * @param transactionId The identifier of the transaction, supplied by the
     * Payment Processor
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    List<PaymentTransaction> getChildTransactionsById(String transactionId);

    /**
     * Retrieve a list of Payment Transactions given a studentId
     * @param studentId The unique identifier of the student who purchased the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionsByStudentId(
            String studentId, Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions given a courseId
     * @param courseId The unique identifier of the purchased course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionsByCourseId(
            String courseId, Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions given a courseId
     * @param institution The NTER or Content provider who sold the course
     * @param transactionType The type of transaction (e.g. express_checkout, nter_disbursement)
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionsByTransactionType(
            String institution, TransactionType transactionType,
            Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions given a resourceId
     * @param institution The NTER or Content provider who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionsByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Retrieve a summary list of Payment Transactions, grouped by course, given an institution
     * @param institution The NTER or Content provider who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.transaction.client.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionSummaryByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Returns the payment status of a given course and student purchaser
     * @param institution The NTER or Content provider who sold the course
     * @param studentId The unique identifier of the student who purchased the course
     * @param courseId The unique identifier of the purchased course
     * @param price The listed price of the course, used to compare with the payment amount
     * @return A PaymentStatus enum (see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus)
     * @see org.nterlearning.commerce.transaction.client.PaymentStatus
     */
    PaymentStatus getPaymentStatus(
        String institution, String studentId, String courseId, BigDecimal price);

    /**
     * Returns the user used in the SOAP header
     * @return A String containing the WS-Security username
     */
    String getUser();
}
