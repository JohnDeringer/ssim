package org.nterlearning.commerce.util;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentTransaction;

import java.math.BigDecimal;

/**
 * @author bblonski
 */
public class TransactionUtil {

	public static BigDecimal getRevenue(PaymentTransaction trans, String institution) {
		BigDecimal revenue = new BigDecimal(0);
		if(trans.getNterId().equals(institution)) {
			// this institution sold the course and gets the referrer fee
			BigDecimal referrerPayment = trans.getReferrerFee();
			revenue = revenue.add(referrerPayment);
		}
		if(trans.getCourseProviderId().equals(institution)) {
			// this institution made the course and gets the paymentGross minus the referrer fee and admin fee
			//BigDecimal referrerPayment = trans.getReferrerFee();
			revenue = revenue.add(trans.getPaymentGross().subtract(trans.getReferrerFee()).subtract(trans.getAdminFee()));
		}
		return revenue;
	}
}
