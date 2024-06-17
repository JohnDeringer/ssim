package org.nterlearning.commerce.form;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import java.util.List;

/**
 * User: Deringer
 * Date: 8/10/11
 * Time: 12:22 PM
 */
public class AddPaymentProcessorForm {

    private PaymentProcessor name;
    private String accountId;
    private String password;
    private String actionUrl;
    private String notifyUrl;
    private String buttonUrl;
    private String apiVersion;
    private List<PaymentProcessor> names;

    public PaymentProcessor getName() {
        return name;
    }

    public void setName(PaymentProcessor name) {
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getButtonUrl() {
        return buttonUrl;
    }

    public void setButtonUrl(String buttonUrl) {
        this.buttonUrl = buttonUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public List<PaymentProcessor> getNames() {
        return names;
    }

    public void setNames(List<PaymentProcessor> names) {
        this.names = names;
    }
}
