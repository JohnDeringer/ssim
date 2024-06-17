/**
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
package org.nterlearning.commerce.model;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType;
import org.nterlearning.xml.commerce.transaction_interface_0_1_0_wsdl.ValidationError;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: Deringer
 * Date: 8/9/11
 * Time: 2:37 PM
 */
public interface TransactionModel {

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
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    List<PaymentTransaction> getTransactions(Date fromDate, Date toDate);

    /**
     * Retrieve a Payment Transaction given a transactionId
     * @param transactionId The identifier of the transaction, supplied by the Payment Processor
     * @return A PaymentTransaction objects
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    PaymentTransaction getTransactionById(String transactionId);

    /**
     * Retrieve a list of Child Payment Transactions (e.g. refunds, etc)  given a transactionId
     * @param transactionId The identifier of the transaction, supplied by the
     * Payment Processor
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    List<PaymentTransaction> getChildTransactionsById(String transactionId);

    /**
     * Retrieve a list of Payment Transactions given a studentId
     * @param studentId The unique identifier of the student who purchased the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionsByStudentId(
            String studentId, Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions given a courseId
     * @param courseId The unique identifier of the purchased course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
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
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionsByTransactionType(
            String institution, TransactionType transactionType, Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions given a resourceId
     * @param institution The NTER or Content provider who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionsByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Retrieve the net account balance owed for each Content Provider and NTER
     * @return A BigDecimal containing the net balance
     */
    Map<String, BigDecimal> getBalanceByInstitution();

    /**
     * Retrieve the net account balance owed to a given an institution
     * @param institution The NTER instance, or ContentProvider, who sold the course
     * @return A BigDecimal containing the net balance for the given institution
     */
    BigDecimal getTransactionBalanceByInstitution(String institution);

    /**
     * Retrieve a summary list of Payment Transactions, grouped by course, given an institution
     * @param institution The NTER or Content provider who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionItemSummaryByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Retrieve a summary refund list of Payment Transactions, grouped by course, given an institution
     * @param institution The NTER or Content provider who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    List<PaymentTransaction> getTransactionRefundItemSummaryByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Returns the payment status of a given course and student purchaser
     * @param institution The NTER or Content provider who sold the course
     * @param studentId The unique identifier of the student who purchased the course
     * @param courseId The unique identifier of the purchased course
     * @param price The listed price of the course, used to compare with the payment amount
     * @return A PaymentStatus enum (see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus)
     * @throws ValidationError When there is a discrepancy validating price, item,
     * seller, or currency
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus
     * @see ValidationError
     */
    PaymentStatus getPaymentStatus(
        String institution, String studentId, String courseId, BigDecimal price)
            throws ValidationError;

}
