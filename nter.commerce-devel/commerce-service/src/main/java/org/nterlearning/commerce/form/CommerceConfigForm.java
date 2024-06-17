package org.nterlearning.commerce.form;

import java.math.BigDecimal;

/**
 * @author Brian Blonski
 */
public class CommerceConfigForm {

    private BigDecimal adminFee;
    private BigDecimal referrerFee;

    public BigDecimal getAdminFee() {
        return adminFee;
    }

    public void setAdminFee(BigDecimal adminFee) {
        this.adminFee = adminFee;
    }

    public BigDecimal getReferrerFee() {
        return referrerFee;
    }

    public void setReferrerFee(BigDecimal referrerFee) {
        this.referrerFee = referrerFee;
    }
}
