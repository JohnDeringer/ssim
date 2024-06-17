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

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import javax.persistence.*;


@Entity(name = "PaymentConfigEntity")
@Table(name = "PAYMENT_CONFIG", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "CONFIGURATION_NAME"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class PaymentConfigEntity {

    protected PaymentProcessor configId;
    protected boolean activeStatus;
    protected String sellerId;
    protected String sellerPassword;
    protected String sellerEmail;
    protected String actionURL;
    protected String notifyURL;
    protected String buttonURL;
    protected String apiVersion;
    protected String apiUsername;
    protected String apiPassword;
    protected String apiSignature;
    protected Long id;

    public PaymentConfigEntity() {}

    public PaymentConfigEntity(PaymentConfig paymentConfig) {
        setPaymentConfig(paymentConfig);
    }

    @Transient
    public PaymentConfig getPaymentConfig() {
        PaymentConfig paymentConfig = new PaymentConfig();
        paymentConfig.setConfigId(configId);
        paymentConfig.setActiveStatus(activeStatus);
        paymentConfig.setSellerId(sellerId);
        paymentConfig.setSellerPassword(sellerPassword);
        paymentConfig.setSellerEmail(sellerEmail);
        paymentConfig.setActionURL(actionURL);
        paymentConfig.setNotifyURL(notifyURL);
        paymentConfig.setButtonURL(buttonURL);
        paymentConfig.setApiVersion(apiVersion);
        paymentConfig.setApiUsername(apiUsername);
        paymentConfig.setApiPassword(apiPassword);
        paymentConfig.setApiSignature(apiSignature);
        paymentConfig.setID(id);

        return paymentConfig;
    }

    public void setPaymentConfig(@NotNull PaymentConfig paymentConfig) {
        configId = paymentConfig.getConfigId();
        activeStatus = paymentConfig.isActiveStatus();
        sellerId = paymentConfig.getSellerId();
        sellerPassword = paymentConfig.getSellerPassword();
        sellerEmail = paymentConfig.getSellerEmail();
        actionURL = paymentConfig.getActionURL();
        notifyURL = paymentConfig.getNotifyURL();
        buttonURL = paymentConfig.getButtonURL();
        apiVersion = paymentConfig.getApiVersion();
        apiUsername = paymentConfig.getApiUsername();
        apiPassword = paymentConfig.getApiPassword();
        apiSignature = paymentConfig.getApiSignature();
        id = paymentConfig.getID();
    }

    /**
     * Payment Processor name (e.g. PayPal, Amazon).
     *
     * @return
     *     configId
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor }
     *
     */
    @Basic
    @Column(name = "CONFIGURATION_NAME", length = 50)
    @Enumerated(EnumType.STRING)
    public PaymentProcessor getConfigId() {
        return configId;
    }

    /**
     * Sets the value of the configId property.
     *
     * @param configId
     *     allowed object is
     *     {@link org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor }
     *
     */
    public void setConfigId(@NotNull PaymentProcessor configId) {
        this.configId = configId;
    }

    /**
     * Active status of the Payment Processor.
     *
     * @return
     *     activeStatus
     *     {@link boolean }
     *
     */
    @Basic
    @Column(name = "ACTIVE")
    public boolean isActiveStatus() {
        return activeStatus;
    }

    /**
     * Sets the value of the activeStatus property.
     *
     * @param activeStatus
     *     allowed object is
     *     {@link boolean }
     *
     */
    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    /**
     * NTER's merchant account name/ID.
     *
     * @return
     *     sellerId
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "SELLER_ID", length = 255)
    public String getSellerId() {
        return sellerId;
    }

    /**
     * Sets the value of the sellerId property.
     *
     * @param sellerId
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * NTER's merchant account password.
     *
     * @return
     *     sellerPassword
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "SELLER_PASSWORD", length = 50)
    public String getSellerPassword() {
        return sellerPassword;
    }

    /**
     * Sets the value of the sellerPassword property.
     *
     * @param sellerPassword
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSellerPassword(String sellerPassword) {
        this.sellerPassword = sellerPassword;
    }

    /**
     * NTER's merchant account email.
     *
     * @return
     *     sellerEmail
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "SELLER_EMAIL", length = 255)
    public String getSellerEmail() {
        return sellerEmail;
    }

    /**
     * Sets the value of the sellerEmail property.
     *
     * @param sellerEmail
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    /**
     * The payment processor transaction form post URL.
     *
     * @return
     *     actionURL
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "ACTION_URL", length = 255)
    public String getActionURL() {
        return actionURL;
    }

    /**
     * Sets the value of the actionURL property.
     *
     * @param actionURL
     *     allowed object is
     *     {@link String }
     *
     */
    public void setActionURL(String actionURL) {
        this.actionURL = actionURL;
    }

    /**
     * URL to receive async transaction notifications.
     *
     * @return
     *     notifyURL
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "NOTIFY_URL", length = 255)
    public String getNotifyURL() {
        return notifyURL;
    }

    /**
     * Sets the value of the notifyURL property.
     *
     * @param notifyURL
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
    }

    /**
     * The Payment Processor button URL to display.
     *
     * @return
     *     buttonURL
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "BUTTON_URL", length = 255)
    public String getButtonURL() {
        return buttonURL;
    }

    /**
     * Sets the value of the buttonURL property.
     *
     * @param buttonURL
     *     allowed object is
     *     {@link String }
     *
     */
    public void setButtonURL(String buttonURL) {
        this.buttonURL = buttonURL;
    }

    /**
     * The Payment Processor API version number.
     *
     * @return
     *     apiVersion
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "API_VERSION", length = 25)
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Sets the value of the apiVersion property.
     *
     * @param apiVersion
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * The Payment Processor API username.
     *
     * @return
     *     apiUsername
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "API_USER", length = 255)
    public String getApiUsername() {
        return apiUsername;
    }

    /**
     * Sets the value of the apiUsername property.
     *
     * @param apiUsername
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApiUsername(String apiUsername) {
        this.apiUsername = apiUsername;
    }

    /**
     * The Payment Processor API password.
     *
     * @return
     *     apiPassword
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "API_PASSWORD", length = 255)
    public String getApiPassword() {
        return apiPassword;
    }

    /**
     * Sets the value of the apiPassword property.
     *
     * @param apiPassword
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    /**
     * The Payment Processor API signature.
     *
     * @return
     *     apiSignature
     *     {@link String }
     *
     */
    @Basic
    @Column(name = "API_SIGNATURE", length = 255)
    public String getApiSignature() {
        return apiSignature;
    }

    /**
     * Sets the value of the apiSignature property.
     *
     * @param apiSignature
     *     allowed object is
     *     {@link String }
     *
     */
    public void setApiSignature(String apiSignature) {
        this.apiSignature = apiSignature;
    }

    /**
     * The unique identifier provided by the data store.
     *
     * @return
     *     possible object is
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
