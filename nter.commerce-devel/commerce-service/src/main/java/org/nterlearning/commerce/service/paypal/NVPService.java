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

package org.nterlearning.commerce.service.paypal;

import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;

import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.services.NVPCallerServices;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.nterlearning.commerce.model.ConfigurationModel;
import org.nterlearning.commerce.service.ServiceUtil;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/18/12
 */
public class NVPService {

    private ConfigurationModel configurationModel;

    private String shortErrorMessage;
    private String longErrorMessage;

    private static final String ENV_SANDBOX = "sandbox";
    private static final String ENV_LIVE = "live";
    private static final String REFUND_TYPE_PARTIAL = "Partial";
    private static final String REFUND_TYPE_FULL = "Full";

    private static final String METHOD_TYPE_REFUND = "RefundTransaction";
    private static final String METHOD_TYPE_PAY = "MassPay";

    public static final String RESPONSE_ACK_SUCCESS = "Success";
    public static final String RESPONSE_ACK_SUCCESS_W = "SuccessWithWarning";
    public static final String RESPONSE_ACK_FAILURE = "Failure";
    public static final String RESPONSE_ACK_FAILURE_W = "FailureWithWarning";

    private Logger logger = LoggerFactory.getLogger(NVPService.class);

    @NotNull
    public String refundTransactionCode(
            @NotNull String merchantName,
            @NotNull String transactionId,
            @Nullable String note) {
        String amount = null;
        return refundTransactionCode(
                merchantName, REFUND_TYPE_FULL, transactionId, amount,
                note);
    }

    @NotNull
    public String refundTransactionCode(
            @NotNull String merchantName,
            @NotNull String refundType,
            @NotNull String transactionId,
            @Nullable String amount,
            @Nullable String note) {

        shortErrorMessage = null;
        longErrorMessage = null;

        String response = RESPONSE_ACK_FAILURE;
        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();
        NVPCallerServices caller;

        try {
            caller = new NVPCallerServices();
            APIProfile profile = ProfileFactory.createSignatureAPIProfile();
            // Set API credentials, PayPal end point, API operation and version.
            PaymentConfig paymentConfig = getPaymentConfig();
            String apiUser = paymentConfig.getApiUsername();
            String apiPassword = paymentConfig.getApiPassword();
            String apiSignature = paymentConfig.getApiSignature();

            if (apiUser != null && !apiUser.isEmpty()) {
                profile.setAPIUsername(apiUser);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API username [" +
                    apiUser + "]");
            }

            if (apiPassword != null && !apiPassword.isEmpty()) {
                profile.setAPIPassword(apiPassword);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API password [" +
                    apiPassword + "]");
            }

            if (apiSignature != null && !apiSignature.isEmpty()) {
                profile.setSignature(apiSignature);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API signature [" +
                    apiSignature + "]");
            }

            profile.setEnvironment(
                    getEnv(paymentConfig)
            );

            profile.setSubject(merchantName);
            caller.setAPIProfile(profile);

            String apiVersion = getPaymentConfig().getApiVersion();
            if (apiVersion == null || apiVersion.isEmpty()) {
                apiVersion = "51.0";
                logger.error("Payment processor configuration has supplied an invalid api version [" +
                    apiVersion + "] defaulting to [" + apiVersion + "]");
            } else {
                encoder.add("VERSION", apiVersion);
            }

            encoder.add("METHOD", METHOD_TYPE_REFUND);
            encoder.add("TRANSACTIONID", transactionId);
            encoder.add("REFUNDTYPE", refundType);
            if (refundType.equalsIgnoreCase(REFUND_TYPE_PARTIAL)) {
                encoder.add("AMT", amount);
            }

            if (note != null) {
                encoder.add("NOTE", note);
            }

            // Execute the API operation and obtain the response.
            String NVPRequest = encoder.encode();
            String NVPResponse = caller.call(NVPRequest);
            decoder.decode(NVPResponse);

            response = decoder.get("ACK");
            if (response.equals(RESPONSE_ACK_FAILURE) ||
                    response.equals(RESPONSE_ACK_FAILURE_W)) {
                String errorCode = decoder.get("L_ERRORCODE0");
                String shortMessage = decoder.get("L_SHORTMESSAGE0");
                String longMessage = decoder.get("L_LONGMESSAGE0");

                shortErrorMessage = shortMessage;
                longErrorMessage = longMessage;

                logger.error("Error response [" + response +
                     "] received from payment processor while requesting refund. Error code [" +
                        errorCode + "]" +
                        (shortMessage != null ? " [" + shortMessage + "] " : "") +
                        (longMessage != null ? " [" + longMessage + "] " : "")
                );
            }
        } catch (Exception e) {
            logger.error("Error processing refund", e);
        }

        return response;
    }

    @NotNull
    public String massPayment(@NotNull String merchantName,
                              @NotNull String subject,
                              @NotNull List<PaymentItem> paymentItems) {

        shortErrorMessage = null;
        longErrorMessage = null;

        String response = "Failure";
        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();
        NVPCallerServices caller;

        try {
            caller = new NVPCallerServices();
            APIProfile profile = ProfileFactory.createSignatureAPIProfile();
            // Set API credentials, PayPal end point, API operation and version.
            PaymentConfig paymentConfig = getPaymentConfig();
            String apiUser = paymentConfig.getApiUsername();
            String apiPassword = paymentConfig.getApiPassword();
            String apiSignature = paymentConfig.getApiSignature();

            if (apiUser != null && !apiUser.isEmpty()) {
                profile.setAPIUsername(apiUser);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API username [" +
                    apiUser + "]");
            }

            if (apiPassword != null && !apiPassword.isEmpty()) {
                profile.setAPIPassword(apiPassword);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API password [" +
                    apiPassword + "]");
            }

            if (apiSignature != null && !apiSignature.isEmpty()) {
                profile.setSignature(apiSignature);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API signature [" +
                    apiSignature + "]");
            }

            profile.setEnvironment(
                    getEnv(paymentConfig)
            );

            profile.setSubject(merchantName);
            caller.setAPIProfile(profile);

            String apiVersion = getPaymentConfig().getApiVersion();
            if (apiVersion == null || apiVersion.isEmpty()) {
                apiVersion = "51.0";
                logger.error("Payment processor configuration has supplied an invalid api version [" +
                    apiVersion + "] defaulting to [" + apiVersion + "]");
            } else {
                encoder.add("VERSION", apiVersion);
            }

            encoder.add("METHOD", METHOD_TYPE_PAY);
            encoder.add("EMAILSUBJECT", subject);
		    encoder.add("RECEIVERTYPE", "EmailAddress");

            int i = 0;
            for (PaymentItem paymentItem : paymentItems) {
                encoder.add("L_EMAIL" + i, paymentItem.getReceiverEmail());
                encoder.add("L_Amt" + i, paymentItem.getAmount().toPlainString());
                encoder.add("L_UNIQUEID" + i, ServiceUtil.getTransactionId());
                String note = paymentItem.getNote();
                if (note != null) {
                    encoder.add("L_NOTE" + i, note);
                }
                i++;
            }

            // Execute the API operation and obtain the response.
            String NVPRequest = encoder.encode();
            String NVPResponse = caller.call(NVPRequest);
            decoder.decode(NVPResponse);

            response = decoder.get("ACK");
            if (response.equals(RESPONSE_ACK_FAILURE) ||
                    response.equals(RESPONSE_ACK_FAILURE_W)) {
                String errorCode = decoder.get("L_ERRORCODE0");
                String shortMessage = decoder.get("L_SHORTMESSAGE0");
                String longMessage = decoder.get("L_LONGMESSAGE0");

                shortErrorMessage = shortMessage;
                longErrorMessage = longMessage;

                logger.error("Error response [" + response +
                     "] received from payment processor while requesting refund. Error code [" +
                        errorCode + "]" +
                        (shortMessage != null ? " [" + shortMessage + "] " : "") +
                        (longMessage != null ? " [" + longMessage + "] " : "")
                );
            }
        } catch (Exception e) {
            logger.error("Error processing payment", e);
        }

        return response;
    }

    @NotNull
    public String payment(@NotNull String merchantName,
                          @NotNull String subject,
                          @NotNull PaymentItem paymentItem) {

        shortErrorMessage = null;
        longErrorMessage = null;

        String response = "Failure";
        NVPEncoder encoder = new NVPEncoder();
        NVPDecoder decoder = new NVPDecoder();
        NVPCallerServices caller;

        try {
            caller = new NVPCallerServices();
            APIProfile profile = ProfileFactory.createSignatureAPIProfile();
            // Set API credentials.
            PaymentConfig paymentConfig = getPaymentConfig();
            String apiUser = paymentConfig.getApiUsername();
            String apiPassword = paymentConfig.getApiPassword();
            String apiSignature = paymentConfig.getApiSignature();

            if (apiUser != null && !apiUser.isEmpty()) {
                profile.setAPIUsername(apiUser);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API username [" +
                    apiUser + "]");
            }

            if (apiPassword != null && !apiPassword.isEmpty()) {
                profile.setAPIPassword(apiPassword);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API password [" +
                    apiPassword + "]");
            }

            if (apiSignature != null && !apiSignature.isEmpty()) {
                profile.setSignature(apiSignature);
            } else {
                throw new RuntimeException(
                    "Unable to process refund transaction, invalid API signature [" +
                    apiSignature + "]");
            }

            profile.setEnvironment(
                    getEnv(paymentConfig)
            );

            profile.setSubject(merchantName);
            caller.setAPIProfile(profile);

            String apiVersion = getPaymentConfig().getApiVersion();
            if (apiVersion == null || apiVersion.isEmpty()) {
                apiVersion = "51.0";
                logger.error("Payment processor configuration has supplied an invalid api version [" +
                    apiVersion + "] defaulting to [" + apiVersion + "]");
            } else {
                encoder.add("VERSION", apiVersion);
            }

            encoder.add("METHOD", METHOD_TYPE_PAY);
            encoder.add("EMAILSUBJECT", subject);
		    encoder.add("RECEIVERTYPE", "EmailAddress");
            encoder.add("L_EMAIL0", paymentItem.getReceiverEmail());
			encoder.add("L_Amt0", paymentItem.getAmount().toPlainString());
            encoder.add("L_UNIQUEID0", ServiceUtil.getTransactionId());
            if (paymentItem.getNote() != null) {
			    encoder.add("L_NOTE0", paymentItem.getNote());
            }

            // Execute the API operation and obtain the response.
            String NVPRequest = encoder.encode();
            String NVPResponse = caller.call(NVPRequest);
            decoder.decode(NVPResponse);

            response = decoder.get("ACK");
            if (response.equals(RESPONSE_ACK_FAILURE) ||
                    response.equals(RESPONSE_ACK_FAILURE_W)) {
                String errorCode = decoder.get("L_ERRORCODE0");
                String shortMessage = decoder.get("L_SHORTMESSAGE0");
                String longMessage = decoder.get("L_LONGMESSAGE0");

                shortErrorMessage = shortMessage;
                longErrorMessage = longMessage;

                logger.error("Error response [" + response +
                     "] received from payment processor while requesting refund. Error code [" +
                        errorCode + "] [" + (shortMessage != null ? shortMessage + ". " : "") +
                        (longMessage != null ? longMessage + "." : "") + "]"
                );
            }
        } catch (Exception e) {
            logger.error("Error processing payment", e);
        }

        return response;
    }

    public String getShortErrorMessage() {
        return shortErrorMessage;
    }

    public String getLongErrorMessage() {
        return longErrorMessage;
    }

    private String getEnv(PaymentConfig paymentConfig) {
        String env;
        boolean actionUrlSandbox =
                paymentConfig.getActionURL().contains(ENV_SANDBOX);
        if (actionUrlSandbox) {
            logger.info("PayPal actionURL is set to [" +
                    paymentConfig.getActionURL() +
                    "] transactions will be processed through the PayPal " +
                    ENV_SANDBOX);
            env = ENV_SANDBOX;
        } else {
            logger.info("PayPal actionURL is set to [" +
                    paymentConfig.getActionURL() +
                    "] transactions will be processed through PayPal " +
                    ENV_LIVE);
            env = ENV_LIVE;
        }

        return env;
    }

    private boolean isSandbox() {
        boolean isSandBox;
        PaymentConfig paymentConfig = getPaymentConfig();
        boolean actionUrlSandbox =
                paymentConfig.getActionURL().contains(ENV_SANDBOX);

        if (actionUrlSandbox) {
            logger.info("PayPal actionURL is set to [" +
                    paymentConfig.getActionURL() +
                    "] transactions will be processed through the PayPal " +
                    ENV_SANDBOX);
            isSandBox = true;
        } else {
            logger.info("PayPal actionURL is set to [" +
                    paymentConfig.getActionURL() +
                    "] transactions will be processed through PayPal " +
                    ENV_LIVE);
            isSandBox = false;
        }

        return isSandBox;
    }

    public void setConfigurationModel(ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }

    private PaymentConfig getPaymentConfig() {
        return configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
    }

}
