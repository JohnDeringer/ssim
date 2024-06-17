package org.nterlearning.commerce.persistence;

import java.math.BigDecimal;
import java.util.*;

import org.jetbrains.annotations.NotNull;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.ValidationStatus;

import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Transactional;


public class TransactionDaoImpl
        extends JpaDaoSupport implements TransactionDao {

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<PaymentTransactionEntity> getTransactionsByStudentAndCourse(
            @NotNull String studentId, @NotNull String courseId) {
        List<PaymentTransactionEntity> transactions = new ArrayList<PaymentTransactionEntity>();
        try {
            transactions = getJpaTemplate()
                .find(
                    "select p from PaymentTransactionEntity p where p.studentId = ?1 and p.itemNumber = ?2 and p.validationStatus <> ?3 order by p.ID",
                        studentId, courseId, ValidationStatus.INVALID);
        } catch (Exception e) {
            logger.error(
                "Error retrieving PaymentStatus for " +
                    "studentId [" + studentId + "] " +
                    "courseId [" + courseId + "]"
                    , e);
        }
        return transactions;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public List<PaymentTransactionEntity> getTransactionsByStudentAndCourse(
            @NotNull String institution, @NotNull String studentId,
            @NotNull String courseId) {
        List<PaymentTransactionEntity> transactions = new ArrayList<PaymentTransactionEntity>();
        try {
            transactions = getJpaTemplate()
                .find(
                    "select p from PaymentTransactionEntity p where p.studentId = ?1 and p.itemNumber = ?2 and p.courseProviderId = ?3 and p.validationStatus <> ?4 order by p.ID",
                        studentId, courseId, institution, ValidationStatus.INVALID);
        } catch (Exception e) {
            logger.error(
                "Error retrieving PaymentStatus for " +
                    "institution [" + institution + "] " +
                    "studentId [" + studentId + "] " +
                    "courseId [" + courseId + "]"
                    , e);
        }
        return transactions;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public List<PaymentTransactionEntity> getTransactionsById(
            @NotNull String transactionId) {
        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();
        try {
            transactionList = getJpaTemplate().find(
                "select t from PaymentTransactionEntity t where t.transactionId = ?1",
                transactionId);
        } catch (Exception e) {
            logger.error(
                "Error retrieving PaymentTransactionEntity for TransactionId [" +
                    transactionId + "]", e);
        }
        return transactionList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public List<PaymentTransactionEntity> getChildTransactionsById(
            @NotNull String transactionId) {
        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();
        try {
            transactionList = getJpaTemplate().find(
                "select t from PaymentTransactionEntity t where t.transactionParentId = ?1",
                transactionId);
        } catch (Exception e) {
            logger.error(
                "Error retrieving child PaymentTransactions for TransactionId [" +
                    transactionId + "]", e);
        }
        return transactionList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public List<PaymentTransactionEntity> getTransactionsByStudentId(
            @NotNull String studentId,
            @NotNull Date fromDate, @NotNull Date toDate) {

        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();
        try {
            transactionList = getJpaTemplate().find(
                "select t from PaymentTransactionEntity t where t.studentId = ?1 and t.paymentDate between ?2 and ?3",
                studentId, fromDate, toDate);
        } catch (Exception e) {
            logger.error("Error retrieving PaymentTransactionEntity for StudentId [" +
                    studentId + "]", e);
        }
        return transactionList;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<PaymentTransactionEntity> getTransactionsByInstitution(
            @NotNull String institution, @NotNull Date fromDate, @NotNull Date toDate) {

        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();
        try {
            transactionList = getJpaTemplate().find(
                "select o from PaymentTransactionEntity o where (o.courseProviderId = ?1 or o.nterId = ?1) and o.paymentDate between ?2 and ?3 order by o.paymentDate desc",
                institution, fromDate, toDate);
        } catch (Exception e) {
            logger.error("Error retrieving PaymentTransactionEntity for resourceId [" +
                    institution + "]", e);
        }
        return transactionList;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<PaymentTransactionEntity> getTransactionRefundItemSummaryByInstitution(
            @NotNull String institution, @NotNull Date fromDate, @NotNull Date toDate) {

        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();

        try {
            List<Object[]> objectList = getJpaTemplate().find(
                "select o.itemNumber, o.itemName, sum(o.quantity), sum(o.paymentGross), sum(o.paymentFee), sum(o.referrerFee), sum(o.adminFee) from PaymentTransactionEntity o where (o.courseProviderId = ?1 or o.nterId = ?1) and (o.paymentStatus=?2 or o.paymentStatus=?3 or o.paymentStatus=?4) and o.paymentDate between ?5 and ?6 group by o.itemNumber",
                    institution, PaymentStatus.CANCELED_REVERSAL,
                    PaymentStatus.REFUNDED, PaymentStatus.REVERSED, fromDate, toDate);

            if (!objectList.isEmpty()) {
                Object[] trans = objectList.get(0);
                PaymentTransactionEntity paymentTransaction = new PaymentTransactionEntity();

                paymentTransaction.setItemNumber((String) trans[0]);
                paymentTransaction.setItemName((String) trans[1]);
                Long qty = (Long)trans[2];
                if (qty != null) {
                    paymentTransaction.setQuantity(qty.intValue());
                }

                paymentTransaction.setPaymentGross((BigDecimal) trans[3]);
                paymentTransaction.setPaymentFee((BigDecimal) trans[4]);
                paymentTransaction.setReferrerFee((BigDecimal) trans[5]);
                paymentTransaction.setAdminFee((BigDecimal) trans[6]);

                transactionList.add(paymentTransaction);
            }
        } catch (Exception e) {
            logger.error("Error retrieving PaymentTransactionEntity for institution [" +
                    institution + "]", e);
        }
        return transactionList;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<PaymentTransactionEntity> getTransactionItemSummaryByInstitution(
            @NotNull String institution,
            @NotNull Date fromDate, @NotNull Date toDate) {

        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();

        try {
            List<Object[]> objectList = getJpaTemplate().find(
                "select o.itemNumber, o.itemName, o.courseProviderId, o.nterId, sum(o.quantity), sum(o.paymentGross), sum(o.paymentFee), sum(o.referrerFee), sum(o.adminFee) from PaymentTransactionEntity o where (o.courseProviderId = ?1 or o.nterId = ?1) and o.paymentStatus=?2 and o.transactionType <> ?3 and o.paymentDate between ?4 and ?5 group by o.itemNumber",
                institution, PaymentStatus.COMPLETED, TransactionType.DISBURSEMENT, fromDate, toDate);

            for (Object[] trans : objectList) {
                PaymentTransactionEntity paymentTransaction = new PaymentTransactionEntity();

                paymentTransaction.setItemNumber((String) trans[0]);
                paymentTransaction.setItemName((String)trans[1]);

                String courseProviderId = trans[2] != null ? (String)trans[2] : "";
                if (courseProviderId.equals(institution)) {
                    paymentTransaction.setCourseProviderId(courseProviderId);
                }

                String nterId = trans[3] != null ? (String)trans[3] : "";
                if (nterId.equals(institution)) {
                    paymentTransaction.setNterId(nterId);
                }

                Long qty = (Long)trans[4];
                if (qty != null) {
                    paymentTransaction.setQuantity(qty.intValue());
                }
                paymentTransaction.setPaymentGross((BigDecimal) trans[5]);
                paymentTransaction.setPaymentFee((BigDecimal) trans[6]);
                paymentTransaction.setReferrerFee((BigDecimal) trans[7]);
                paymentTransaction.setAdminFee((BigDecimal) trans[8]);

                transactionList.add(paymentTransaction);
            }
        } catch (Exception e) {
            logger.error("Error retrieving PaymentTransactionEntity for resourceId [" +
                    institution + "]", e);
        }
        return transactionList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public BigDecimal getTransactionBalanceByNterInstance(
            @NotNull String institution) {
        BigDecimal balance = null;
        try {
            List<BigDecimal> objectList =
                getJpaTemplate().find(
                    "select sum(o.referrerFee) from PaymentTransactionEntity o where o.nterId = ?1 and o.validationStatus <> ?2 group by o.nterId",
                    institution, ValidationStatus.INVALID);

            if (!objectList.isEmpty()) {
                balance = objectList.get(0);
            }
        } catch (Exception e) {
            logger.error("Error retrieving Transaction Balance for institution [" +
                    institution + "]", e);
        }

        if (balance == null) {
            balance = new BigDecimal(0);
        }

        return balance;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public BigDecimal getTransactionBalanceByContentProvider(
            @NotNull String institution) {
        BigDecimal balance = null;
        try {
            List<BigDecimal> objectList = getJpaTemplate().find(
                "select (o.paymentGross - o.adminFee - o.paymentFee - o.referrerFee) from PaymentTransactionEntity o where o.courseProviderId = ?1 and o.validationStatus <> ?2 group by o.courseProviderId",
                institution, ValidationStatus.INVALID);

            if (!objectList.isEmpty()) {
                balance = objectList.get(0);
            }
        } catch (Exception e) {
            logger.error("Error retrieving Transaction Balance for institution [" +
                    institution + "]", e);
        }

        if (balance == null) {
            balance = new BigDecimal(0);
        }

        return balance;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public Map<String, BigDecimal> getBalanceByNterInstance() {
        Map<String, BigDecimal> balanceMap = new HashMap<String, BigDecimal>();
        try {
            List<Object[]> objectList =
                getJpaTemplate().find(
                    "select o.nterId, sum(o.referrerFee) from PaymentTransactionEntity o where o.validationStatus <> ?1 group by o.nterId having sum(o.referrerFee) > 0 order by sum(o.paymentGross) desc",
                    ValidationStatus.INVALID);

            for (Object[] trans : objectList) {
                String nterId = (String) trans[0];
                BigDecimal balance = (BigDecimal)trans[1];
                if (nterId != null) {
                    balanceMap.put(nterId, balance);
                } else {
                    logger.warn("getBalanceByNterInstance returned NTER_ID [" + nterId + "]");
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving Transaction Balances", e);
        }

        return balanceMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public Map<String, BigDecimal> getBalanceByContentProvider() {
        Map<String, BigDecimal> balanceMap = new HashMap<String, BigDecimal>();
        try {
            List<Object[]> objectList = getJpaTemplate().find(
                "select o.courseProviderId, (o.paymentGross - o.adminFee - o.paymentFee - o.referrerFee) from PaymentTransactionEntity o where o.validationStatus <> ?1 group by o.courseProviderId having sum(o.paymentGross - o.adminFee - o.paymentFee - o.referrerFee) > 0 order by sum(o.paymentGross) desc",
                    ValidationStatus.INVALID);

            for (Object[] trans : objectList) {
                String cpId = (String) trans[0];
                BigDecimal balance = (BigDecimal)trans[1];
                if (cpId != null) {
                    balanceMap.put(cpId, balance);
                } else {
                    logger.warn("getBalanceByContentProvider returned COURSE_PROVIDER_ID [" + cpId + "]");
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving Transaction Balances", e);
        }

        return balanceMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public List<PaymentTransactionEntity> getTransactionsByCourseId(
            @NotNull String courseId,
            @NotNull Date fromDate, @NotNull Date toDate) {

        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();
        try {
            transactionList = getJpaTemplate().find(
                "select t from PaymentTransactionEntity t where t.itemNumber = ?1 and t.paymentDate between ?2 and ?3",
                courseId, fromDate, toDate);
        } catch (Exception e) {
            logger.error("Error retrieving PaymentTransactionEntity for courseId [" +
                    courseId + "]", e);
        }

        return transactionList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public List<PaymentTransactionEntity> getTransactionsByTransactionType(
            @NotNull String institution, @NotNull TransactionType transactionType,
            @NotNull Date fromDate, @NotNull Date toDate) {

        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();
        try {
            transactionList = getJpaTemplate().find(
                "select o from PaymentTransactionEntity o where (o.courseProviderId = ?1 or o.nterId = ?1) and o.transactionType = ?2 and o.paymentDate between ?3 and ?4",
                institution, transactionType, fromDate, toDate);
        } catch (Exception e) {
            logger.error("Error retrieving PaymentTransactionEntity for institution [" +
                    institution + "] and transactionType [" + transactionType + "]", e);
        }

        return transactionList;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NotNull
    public List<PaymentTransactionEntity> getAllTransactions(
            @NotNull Date fromDate, @NotNull Date toDate) {
        List<PaymentTransactionEntity> transactionList = new ArrayList<PaymentTransactionEntity>();
        try {
            transactionList =
                getJpaTemplate().find(
                    "select t from PaymentTransactionEntity t where t.paymentDate between ?1 and ?2",
                        fromDate, toDate);
        } catch (Exception e) {
            logger.error("Error retrieving PaymentTransactions", e);
        }

        return transactionList;
    }

    @Override
    @Transactional
    public void createTransaction(@NotNull PaymentTransaction paymentTransaction) {
        try {
            PaymentTransactionEntity paymentTransactionEntity =
                    new PaymentTransactionEntity(paymentTransaction);

            paymentTransactionEntity.setSysDate(new Date());
            getJpaTemplate().merge(paymentTransaction);
        } catch (Exception e) {
            logger.error("Error creating PaymentTransactionEntity", e);
        }
    }

}
