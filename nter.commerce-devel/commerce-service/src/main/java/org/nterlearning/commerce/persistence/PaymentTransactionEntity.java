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

package org.nterlearning.commerce.persistence;

import org.jetbrains.annotations.NotNull;

import org.jvnet.hyperjaxb3.xml.bind.annotation.adapters.XMLGregorianCalendarAsDate;
import org.jvnet.hyperjaxb3.xml.bind.annotation.adapters.XmlAdapterUtils;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.*;

import javax.persistence.*;
import javax.xml.datatype.XMLGregorianCalendar;

import java.math.BigDecimal;
import java.util.Date;


@Entity(name = "PaymentTransactionEntity")
@Table(name = "PAYMENT_TRANSACTION")
@Inheritance(strategy = InheritanceType.JOINED)
public class PaymentTransactionEntity {

    protected String transactionId;
    protected String transactionParentId;
    protected PaymentProcessor paymentProcessor;
    protected String itemName;
    protected String itemNumber;
    protected Integer quantity;
    protected Date paymentDate;
    protected PaymentStatus paymentStatus;
    protected PaymentType paymentType;
    protected TransactionType transactionType;
    protected String currencyType;
    protected String transactionSubject;
    protected BigDecimal paymentGross;
    protected BigDecimal tax;
    protected BigDecimal paymentFee;
    protected BigDecimal handlingAmount;
    protected BigDecimal shipping;
    protected String receiverEmail;
    protected String payerEmail;
    protected String studentId;
    protected String payerId;
    protected String payerStatus;
    protected String receiverId;
    protected String protectionEligibility;
    protected BigDecimal notifyVersion;
    protected String verifySign;
    protected String testIpn;
    protected ValidationStatus validationStatus;
    protected Date sysDate;
    protected String nterId;
    protected String courseProviderId;
    protected BigDecimal referrerFee;
    protected BigDecimal adminFee;
    protected ReasonCode reasonCode;
    protected Long id;

    public PaymentTransactionEntity() {}

    public PaymentTransactionEntity(PaymentTransaction paymentTransaction){
        setPaymentTransaction(paymentTransaction);
    }

    @Transient
    public PaymentTransaction getPaymentTransaction() {
        PaymentTransaction paymentTransaction = new PaymentTransaction();

        paymentTransaction.setTransactionId(transactionId);
        paymentTransaction.setTransactionParentId(transactionParentId);
        paymentTransaction.setPaymentProcessor(paymentProcessor);
        paymentTransaction.setItemName(itemName);
        paymentTransaction.setItemNumber(itemNumber);
        paymentTransaction.setQuantity(quantity);
        if (paymentDate != null) {
            paymentTransaction.setPaymentDate(
                    XmlAdapterUtils.marshall(XMLGregorianCalendarAsDate.class,
                            paymentDate));
        }
        paymentTransaction.setPaymentStatus(paymentStatus);
        paymentTransaction.setPaymentType(paymentType);
        paymentTransaction.setTransactionType(transactionType);
        paymentTransaction.setCurrencyType(currencyType);
        paymentTransaction.setTransactionSubject(transactionSubject);
        paymentTransaction.setPaymentGross(paymentGross);
        paymentTransaction.setTax(tax);
        paymentTransaction.setPaymentFee(paymentFee);
        paymentTransaction.setHandlingAmount(handlingAmount);
        paymentTransaction.setShipping(shipping);
        paymentTransaction.setReceiverEmail(receiverEmail);
        paymentTransaction.setPayerEmail(payerEmail);
        paymentTransaction.setStudentId(studentId);
        paymentTransaction.setPayerId(payerId);
        paymentTransaction.setPayerStatus(payerStatus);
        paymentTransaction.setReceiverId(receiverId);
        paymentTransaction.setProtectionEligibility(protectionEligibility);
        paymentTransaction.setNotifyVersion(notifyVersion);
        paymentTransaction.setVerifySign(verifySign);
        paymentTransaction.setTestIpn(testIpn);
        paymentTransaction.setValidationStatus(validationStatus);
        if (sysDate != null) {
            paymentTransaction.setSysDate(
                    XmlAdapterUtils.marshall(XMLGregorianCalendarAsDate.class,
                            sysDate));
        }
        paymentTransaction.setNterId(nterId);
        paymentTransaction.setCourseProviderId(courseProviderId);
        paymentTransaction.setReferrerFee(referrerFee);
        paymentTransaction.setAdminFee(adminFee);
        paymentTransaction.setReasonCode(reasonCode);
        paymentTransaction.setID(id);

        return paymentTransaction;
    }

    public void setPaymentTransaction(@NotNull PaymentTransaction paymentTransaction) {
        transactionId = paymentTransaction.getTransactionId();
        transactionParentId = paymentTransaction.getTransactionParentId();
        paymentProcessor = paymentTransaction.getPaymentProcessor();
        itemName = paymentTransaction.getItemName();
        itemNumber = paymentTransaction.getItemNumber();
        quantity = paymentTransaction.getQuantity();
        XMLGregorianCalendar paymentDateCal = paymentTransaction.getPaymentDate();
        if (paymentDateCal != null) {
            paymentDate =
                    XmlAdapterUtils.unmarshall(
                            XMLGregorianCalendarAsDate.class,
                            paymentDateCal);
        }
        paymentStatus = paymentTransaction.getPaymentStatus();
        paymentType = paymentTransaction.getPaymentType();
        transactionType = paymentTransaction.getTransactionType();
        currencyType = paymentTransaction.getCurrencyType();
        transactionSubject = paymentTransaction.getTransactionSubject();
        paymentGross = paymentTransaction.getPaymentGross();
        tax = paymentTransaction.getTax();
        paymentFee = paymentTransaction.getPaymentFee();
        handlingAmount = paymentTransaction.getHandlingAmount();
        shipping = paymentTransaction.getShipping();
        receiverEmail = paymentTransaction.getReceiverEmail();
        payerEmail = paymentTransaction.getPayerEmail();
        studentId = paymentTransaction.getStudentId();
        payerId = paymentTransaction.getPayerId();
        payerStatus = paymentTransaction.getPayerStatus();
        receiverId = paymentTransaction.getReceiverId();
        protectionEligibility = paymentTransaction.getProtectionEligibility();
        notifyVersion = paymentTransaction.getNotifyVersion();
        verifySign = paymentTransaction.getVerifySign();
        testIpn = paymentTransaction.getTestIpn();
        validationStatus = paymentTransaction.getValidationStatus();
        XMLGregorianCalendar sysDateCal = paymentTransaction.getSysDate();
        if (sysDateCal != null) {
            sysDate =
                    XmlAdapterUtils.unmarshall(
                            XMLGregorianCalendarAsDate.class,
                            sysDateCal);
        } else {
            sysDate = new Date();
        }
        nterId = paymentTransaction.getNterId();
        courseProviderId = paymentTransaction.getCourseProviderId();
        referrerFee = paymentTransaction.getReferrerFee();
        adminFee = paymentTransaction.getAdminFee();
        reasonCode = paymentTransaction.getReasonCode();
        id = paymentTransaction.getID();
    }

    /**
     * The merchant’s original transaction identification number for the
     *  payment from the buyer, against which the case was registered.
     *
     * @return
     *     transactionId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "TRANSACTION_ID", length = 255)
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     *
     * @param transactionId
     *     {@link String }
     *
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * The merchant’s parent transaction identification number for the payment
     *   from the buyer, against which the case was registered.
     *   e.g. A refund transaction will reference the original transaction using
     *        the parentTransactionId
     *
     * @return
     *     transactionParentId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "TRANSACTION_PARENT_ID", length = 255)
    public String getTransactionParentId() {
        return transactionParentId;
    }

    /**
     * Sets the value of the transactionParentId property.
     *
     * @param transactionParentId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransactionParentId(String transactionParentId) {
        this.transactionParentId = transactionParentId;
    }

    /**
     * The Payment Processor handling the transaction.
     *
     * @return
     *     paymentProcessor
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor }
     *
     */
    @Basic
    @Column(name = "PAYMENT_PROCESSOR", length = 255)
    @Enumerated(EnumType.STRING)
    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    /**
     * Sets the value of the paymentProcessor property.
     *
     * @param paymentProcessor
     *     allowed object is
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor }
     *
     */
    public void setPaymentProcessor(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    /**
     * Item name as passed by you, the merchant. Or, if
                        not passed by you, as entered by your customer.
     *
     * @return
     *     itemName
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "ITEM_NAME", length = 255)
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the value of the itemName property.
     *
     * @param itemName
     *     allowed object is
     *     {@link String }
     *
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Pass-through variable to track purchased items.
     *
     * @return
     *     itemNumber
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "ITEM_NUMBER", length = 255)
    public String getItemNumber() {
        return itemNumber;
    }

    /**
     * Sets the value of the itemNumber property.
     *
     * @param itemNumber
     *     allowed object is
     *     {@link String }
     *
     */
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * Quantity as entered by your customer or as passed by you, the merchant.
     *
     * @return
     *     quantity
     *     {@link Integer }
     *
     */
    @Basic
    @Column(name = "QUANTITY", precision = 10, scale = 0)
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     *
     * @param quantity
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Time/Date stamp generated by the payment processor,
     *   in the following format: HH:MM:SS DD Mmm YY, YYYY PST
     *
     * @return
     *     paymentDate
     *     {@link java.util.Date }
     *
     */
    @Basic
    @Column(name = "PAYMENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the value of the paymentDate property.
     *
     * @param paymentDate
     *     allowed object is
     *     {@link java.util.Date }
     *
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the value of the paymentStatus property.
     *  i.e. COMPLETED, REFUNDED, PENDING
     * @return
     *     paymentStatus
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus }
     *
     */
    @Basic
    @Column(name = "PAYMENT_STATUS", length = 255)
    @Enumerated(EnumType.STRING)
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets the value of the paymentStatus property.
     *
     * @param paymentStatus
     *     allowed object is
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentStatus }
     *
     */
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    /**
     * The payment type used by the customer.
     *
     * @return
     *     paymentType
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentType }
     *
     */
    @Basic
    @Column(name = "PAYMENT_TYPE", length = 255)
    @Enumerated(EnumType.STRING)
    public PaymentType getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the value of the paymentType property.
     *
     * @param paymentType
     *     allowed object is
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentType }
     *
     */
    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * Gets the value of the transactionType property.
     *
     * @return
     *     transactionType
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType }
     *
     */
    @Basic
    @Column(name = "TRANSACTION_TYPE", length = 255)
    @Enumerated(EnumType.STRING)
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Sets the value of the transactionType property.
     *
     * @param transactionType
     *     allowed object is
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.TransactionType }
     *
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Gets the value of the currencyType property.
     *
     * @return
     *     currencyType
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "CURRENCY_TYPE", length = 255)
    public String getCurrencyType() {
        return currencyType;
    }

    /**
     * Sets the value of the currencyType property.
     *
     * @param currencyType
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    /**
     * Gets the value of the transactionSubject property.
     *
     * @return
     *     transactionSubject
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "TRANSACTION_SUBJECT", length = 255)
    public String getTransactionSubject() {
        return transactionSubject;
    }

    /**
     * Sets the value of the transactionSubject property.
     *
     * @param transactionSubject
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTransactionSubject(String transactionSubject) {
        this.transactionSubject = transactionSubject;
    }

    /**
     * Full amount of the customer's payment, before transaction fee is subtracted.
     * Equivalent to payment_gross for USD payments. If this amount is negative,
     * it signifies a refund or reversal, and either of those payment statuses
     * can be for the full  or partial amount of the original transaction.
     *
     * @return
     *     paymentGross
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "PAYMENT_GROSS", precision = 20, scale = 10)
    public BigDecimal getPaymentGross() {
        return paymentGross;
    }

    /**
     * Sets the value of the paymentGross property.
     *
     * @param paymentGross
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setPaymentGross(BigDecimal paymentGross) {
        this.paymentGross = paymentGross;
    }

    /**
     * Gets the value of the tax property.
     *
     * @return
     *     tax
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "SALES_TAX", precision = 20, scale = 10)
    public BigDecimal getTax() {
        return tax;
    }

    /**
     * Sets the value of the tax property.
     *
     * @param tax
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    /**
     * USD transaction fee associated with the payment.
     * payment_gross minus payment_fee equals the amount deposited into the
     * receiver email account. Is empty for non-USD payments.
     * If this amount is negative, it signifies a refund or reversal,
     * and either of those payment statuses can be for the full or
     * partial amount of the original transaction fee.
     *
     * @return
     *     paymentFee
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "PAYMENT_FEE", precision = 20, scale = 10)
    public BigDecimal getPaymentFee() {
        return paymentFee;
    }

    /**
     * Sets the value of the paymentFee property.
     *
     * @param paymentFee
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setPaymentFee(BigDecimal paymentFee) {
        this.paymentFee = paymentFee;
    }

    /**
     * Product handling charge.
     *
     * @return
     *     handlingAmount
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "HANDLING", precision = 20, scale = 10)
    public BigDecimal getHandlingAmount() {
        return handlingAmount;
    }

    /**
     * Sets the value of the handlingAmount property.
     *
     * @param handlingAmount
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setHandlingAmount(BigDecimal handlingAmount) {
        this.handlingAmount = handlingAmount;
    }

    /**
     * Shipping charges associated with this transaction.
     *  Format: unsigned, no currency symbol, two decimal places.
     *
     * @return
     *     shipping
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "SHIPPING", precision = 20, scale = 10)
    public BigDecimal getShipping() {
        return shipping;
    }

    /**
     * Sets the value of the shipping property.
     *
     * @param shipping
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    /**
     * Email address or account ID of the payment recipient
     *  (that is, the merchant). Equivalent to the values of receiver_email
     *  (if payment is sent to primary account) and business set in the
     *  Website Payment HTML.
     *
     * @return
     *     receiverEmail
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "RECEIVER_EMAIL", length = 255)
    public String getReceiverEmail() {
        return receiverEmail;
    }

    /**
     * Sets the value of the receiverEmail property.
     *
     * @param receiverEmail
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    /**
     * Customer’s primary email address. Use this email to provide any credits.
     *
     * @return
     *     payerEmail
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "PAYER_EMAIL", length = 255)
    public String getPayerEmail() {
        return payerEmail;
    }

    /**
     * Sets the value of the payerEmail property.
     *
     * @param payerEmail
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    /**
     * The unique identifier of the student purchasing the course.
     *
     * @return
     *     studentId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "STUDENT_ID", length = 255)
    public String getStudentId() {
        return studentId;
    }

    /**
     * Sets the value of the studentId property.
     *
     * @param studentId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    /**
     * Unique customer ID related to the Payment Processor.
     *
     * @return
     *     payerId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "PAYER_ID", length = 255)
    public String getPayerId() {
        return payerId;
    }

    /**
     * Sets the value of the payerId property.
     *
     * @param payerId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    /**
     * Whether the customer has a verified PayPal account.
     *  verified – Customer has a verified PayPal account.
     *  unverified – Customer has an unverified PayPal account.
     *
     * @return
     *     payerStatus
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "PAYER_STATUS", length = 255)
    public String getPayerStatus() {
        return payerStatus;
    }

    /**
     * Sets the value of the payerStatus property.
     *
     * @param payerStatus
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPayerStatus(String payerStatus) {
        this.payerStatus = payerStatus;
    }

    /**
     * Unique account ID of the payment recipient (i.e., the merchant).
     *  This is the same as the recipient's referral ID.
     *
     * @return
     *     receiverId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "RECEIVER_ID", length = 255)
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * Sets the value of the receiverId property.
     *
     * @param receiverId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Seller protection
     *   <ul>
     *       <li>ExpandedSellerProtection: Seller is protected by Expanded
     *           seller protection
     *       </li>
     *       <li>SellerProtection: Seller is protected by PayPal’s Seller
     *           Protection Policy
     *       </li>
     *       <li>None: Seller is not protected under Expanded seller
     *           protection nor the Seller Protection Policy
     *       </li>
     *   </ul>
     *
     * @return
     *     protectionEligibility
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "PROTECTION_ELIGIBILITY", length = 255)
    public String getProtectionEligibility() {
        return protectionEligibility;
    }

    /**
     * Sets the value of the protectionEligibility property.
     *
     * @param protectionEligibility
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProtectionEligibility(String protectionEligibility) {
        this.protectionEligibility = protectionEligibility;
    }

    /**
     * Message’s version number
     *
     * @return
     *     notifyVersion
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "NOTIFY_VERSION", precision = 20, scale = 10)
    public BigDecimal getNotifyVersion() {
        return notifyVersion;
    }

    /**
     * Sets the value of the notifyVersion property.
     *
     * @param notifyVersion
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setNotifyVersion(BigDecimal notifyVersion) {
        this.notifyVersion = notifyVersion;
    }

    /**
     * Encrypted string used to validate the authenticity of the transaction.
     *
     * @return
     *     verifySign
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "VERIFY_SIGN", length = 255)
    public String getVerifySign() {
        return verifySign;
    }

    /**
     * Sets the value of the verifySign property.
     *
     * @param verifySign
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVerifySign(String verifySign) {
        this.verifySign = verifySign;
    }

    /**
     * Whether the message is a test message.
     *  It is one of the following values:
     *   1 – the message is directed to the Sandbox
     *
     * @return
     *     testIpn
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "TEST_IPN", length = 255)
    public String getTestIpn() {
        return testIpn;
    }

    /**
     * Sets the value of the testIpn property.
     *
     * @param testIpn
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTestIpn(String testIpn) {
        this.testIpn = testIpn;
    }

    /**
     * Payment processor has verified the sent transaction.
     *
     * @return
     *     validationStatus
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.ValidationStatus }
     *
     */
    @Basic
    @Column(name = "VALIDATION_STATUS", length = 255)
    @Enumerated(EnumType.STRING)
    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    /**
     * Sets the value of the validationStatus property.
     *
     * @param validationStatus
     *     allowed object is
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.ValidationStatus }
     *
     */
    public void setValidationStatus(ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    /**
     * System timestamp.
     *
     * @return
     *     sysDate
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *
     */
    @Basic
    @Column(name = "SYS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSysDate() {
        return sysDate;
    }

    /**
     * Sets the value of the sysDate property.
     *
     * @param sysDate
     *     allowed object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *
     */
    public void setSysDate(Date sysDate) {
        this.sysDate = sysDate;
    }

    /**
     * The unique identifier of the NTER instance facilitating the purchase of the course.
     *
     * @return
     *     nterId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "NTER_ID", length = 255)
    public String getNterId() {
        return nterId;
    }

    /**
     * Sets the value of the nterId property.
     *
     * @param nterId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNterId(String nterId) {
        this.nterId = nterId;
    }

    /**
     * The unique identifier of the Content Provider selling/hosting the course.
     *
     * @return
     *     courseProviderId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "COURSE_PROVIDER_ID", length = 255)
    public String getCourseProviderId() {
        return courseProviderId;
    }

    /**
     * Sets the value of the courseProviderId property.
     *
     * @param courseProviderId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCourseProviderId(String courseProviderId) {
        this.courseProviderId = courseProviderId;
    }

    /**
     * Amount of the NTER instance referrer fee
     *
     * @return
     *     referrerFee
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "REFERRER_FEE", precision = 20, scale = 10)
    public BigDecimal getReferrerFee() {
        return referrerFee;
    }

    /**
     * Sets the value of the referrerFee property.
     *
     * @param referrerFee
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setReferrerFee(BigDecimal referrerFee) {
        this.referrerFee = referrerFee;
    }

    /**
     * Amount of the commerce administration fee.
     *
     * @return
     *     adminFee
     *     {@link java.math.BigDecimal }
     *
     */
    @Basic
    @Column(name = "ADMIN_FEE", precision = 20, scale = 10)
    public BigDecimal getAdminFee() {
        return adminFee;
    }

    /**
     * Sets the value of the adminFee property.
     *
     * @param adminFee
     *     allowed object is
     *     {@link java.math.BigDecimal }
     *
     */
    public void setAdminFee(BigDecimal adminFee) {
        this.adminFee = adminFee;
    }

    /**
     * Whether this transaction is a chargeback, partial, or reversal.
     *
     * @return
     *     reasonCode
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.ReasonCode }
     *
     */
    @Basic
    @Column(name = "REASON_CODE", length = 255)
    @Enumerated(EnumType.STRING)
    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    /**
     * Sets the value of the reasonCode property.
     *
     * @param reasonCode
     *     allowed object is
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.ReasonCode }
     *
     */
    public void setReasonCode(ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    /**
     * The unique identifier provided by the data store.
     *
     * @return
     *     id
     *     {@link Long }
     *
     */
    @Id
    @Column(name = "ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param id
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setID(Long id) {
        this.id = id;
    }

}
