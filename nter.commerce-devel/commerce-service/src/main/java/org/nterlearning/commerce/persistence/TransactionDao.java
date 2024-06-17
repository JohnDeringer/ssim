package org.nterlearning.commerce.persistence;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType;


public interface TransactionDao {

    /**
     * Retrieve a list of Payment Transactions given a transactionId
     * @param transactionId The identifier of the transaction, supplied by the
     * Payment Processor
     * @return A list of PaymentTransaction objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionsById(String transactionId);

    /**
     * Retrieve a list of Child (refunds, etc) Payment Transactions given a transactionId
     * @param transactionId The identifier of the transaction, supplied by the
     * Payment Processor
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getChildTransactionsById(String transactionId);

    /**
     * Retrieve a list of Payment Transactions given a studentId
     * @param studentId The unique identifier of the student who purchased the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionsByStudentId(
            String studentId, Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions given a Content Provider identifier
     * @param institution The Content Provider, or NTER instance, who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionsByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Retrieve a summary list of Payment Transactions grouped by course, given an institution
     * @param institution The Content Provider, or NTER instance, who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionItemSummaryByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Retrieve a summary refund list of Payment Transactions grouped by course given a Content Provider identifier
     * @param institution The Content Provider, or NTER instance, who sold the course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionRefundItemSummaryByInstitution(
            String institution, Date fromDate, Date toDate);

    /**
     * Retrieve the net account balance owed for each NTER instance, if over zero
     * @return A BigDecimal containing the net balance
     */
    Map<String, BigDecimal> getBalanceByNterInstance();

    /**
     * Retrieve the net account balance owed for each Content Provider, over zero
     * @return A BigDecimal containing the net balance
     */
    Map<String, BigDecimal> getBalanceByContentProvider();

    /**
     * Retrieve the net account balance owed to a given an institution
     * @param institution The NTER instance, who sold the course
     * @return A BigDecimal containing the net balance for the given institution
     */
    BigDecimal getTransactionBalanceByNterInstance(String institution);

    /**
     * Retrieve the net account balance owed to a given an institution
     * @param institution The Content Provider who sold the course
     * @return A BigDecimal containing the net balance for the given institution
     */
    BigDecimal getTransactionBalanceByContentProvider(String institution);

    /**
     * Retrieve a list of Payment Transactions given a courseId
     * @param courseId The unique identifier of the purchased course
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionsByCourseId(
            String courseId, Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions given a courseId
     * @param institution The Content Provider, or NTER instance, who sold the course
     * @param transactionType The type of transaction
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionsByTransactionType(
            String institution, TransactionType transactionType,
            Date fromDate, Date toDate);

    /**
     * Retrieve a list of Payment Transactions
     * @param fromDate beginning date range - optional (can be null)
     * @param toDate ending date range - optional (can be null)
     * @return A list of PaymentTransactionEntity objects
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getAllTransactions(Date fromDate, Date toDate);

    /**
     * Retrieves a collection of Payment Transactions.
     * @param studentId The unique identifier of the purchaser of the course.
     * @param courseId The unique identifier of the sold course
     * @return A List of PaymentTransactionEntity objects containing the details of the transaction
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionsByStudentAndCourse(
            String studentId, String courseId);

    /**
     * Retrieves a collection of Payment Transactions.
     * @param institution The institution who provided the course
     * @param studentId The unique identifier of the purchaser of the course.
     * @param courseId The unique identifier of the sold course
     * @return A List of PaymentTransactionEntity objects containing the details of the transaction
     * @see org.nterlearning.commerce.persistence.PaymentTransactionEntity
     */
    List<PaymentTransactionEntity> getTransactionsByStudentAndCourse(
            String institution, String studentId, String courseId);

    /**
     * Create a new Payment Transaction.
     * @param paymentTransaction The object containing the Payment Transaction data
     * @see org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction
     */
    void createTransaction(PaymentTransaction paymentTransaction);

}
