package org.nterlearning.commerce.form;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import java.util.List;

/**
 * User: Deringer
 * Date: 8/10/11
 * Time: 12:22 PM
 */
public class PaymentProcessorStatusForm {

    private PaymentProcessor name;
    private boolean enabled;

    public PaymentProcessor getName() {
        return name;
    }

    public void setName(PaymentProcessor name) {
        this.name = name;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
