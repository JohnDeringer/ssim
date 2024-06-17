package org.nterlearning.commerce.controller;

import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Controls displaying the details of a single payment processor.
 *
 * @author Brian Blonski
 */
@ManagedBean
public class PaymentProcessorDetailsController implements Serializable {

	//** Private Variables **//

	private String name;
	private String accountNumber;
	private boolean status;
	private PaymentProcessor processor;

	//** Getters & Setters **//

	public PaymentProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(PaymentProcessor processor) {
		this.processor = processor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	//** Overrides **//

	@Override
	public String toString() {
		return name;
	}
}
