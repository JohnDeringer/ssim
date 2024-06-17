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

import org.nterlearning.commerce.model.ConfigurationModel;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentConfig;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.PaymentProcessor;
import org.nterlearning.xml.commerce.domain_objects_0_1_0.ValidationStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.ws.rs.core.MultivaluedMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 5/18/12
 */
public class PayPalValidation {

    private static final String CHAR_ENCODE = "UTF-8";
    private static final String CONTENT_TYPE_NAME = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String PAYPAL_CMD_VALIDATE = "cmd=_notify-validate";
    private ConfigurationModel configurationModel;
    private Logger logger = LoggerFactory.getLogger(PayPalValidation.class);

    public String verifyNotificationWithPayPal(MultivaluedMap<String, String> map)
            throws ServletException {

        String validationResult = ValidationStatus.INVALID.value();
        BufferedReader inputStream = null;

        PaymentConfig paymentConfig =
                configurationModel.getPaymentConfig(PaymentProcessor.PAY_PAL);
        String actionUrl = paymentConfig.getActionURL();

        StringBuilder validationResponse = new StringBuilder();
        PrintWriter outputStream = null;

        try {
            for (String key : map.keySet()) {
                String paramValue = map.getFirst(key);
                validationResponse.append(key);
                validationResponse.append("=");
                validationResponse.append(URLEncoder.encode(paramValue,
                        CHAR_ENCODE));
                validationResponse.append("&");
            }
            validationResponse.append(PAYPAL_CMD_VALIDATE);

            // Post back to PayPal to validate
            if (actionUrl != null) {
                URL url = new URL(actionUrl);

                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty(CONTENT_TYPE_NAME,
                        CONTENT_TYPE_VALUE);
                outputStream = new PrintWriter(urlConnection.getOutputStream());
                outputStream.println(validationResponse);

                logger.debug("Sending validation payload to PayPal [" +
                    validationResponse.toString() + "] to URL [" +
                    actionUrl + "]");

                outputStream.flush();

                inputStream = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                validationResult = inputStream.readLine();
                logger.debug("Received validation result from PayPal [" +
                        validationResult + "]");
            } else {
                logger.error("Unable to send validation post to PayPal using action URL [" +
                    actionUrl + "]");
            }
        } catch (Exception e) {
            throw new ServletException(
                    "Error sending validation post to Payment Processor using URL [" +
                    actionUrl + "]", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return validationResult;
    }

    public void setConfigurationModel(ConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }
}
