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

package org.nterlearning.commerce.managed;

import org.jetbrains.annotations.NotNull;

import org.nterlearning.commerce.model.TransactionModel;
import org.nterlearning.registry.client.Contact;
import org.nterlearning.registry.client.Institution;
import org.nterlearning.registry.client.RegistryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 6/4/12
 */
@ManagedBean(name = "accountingBean")
public class AccountingBean implements Serializable {

    @ManagedProperty("#{entitlementUtil}")
    private EntitlementUtil entitlementUtil;

    @ManagedProperty("#{transactionModel}")
    private TransactionModel transactionModel;

    @ManagedProperty("#{registryService}")
    private RegistryImpl registryService;

    private List<AccountingItem> accountingItems;
    private Logger logger = LoggerFactory.getLogger(AccountingBean.class);

    public AccountingBean() {
        if (accountingItems == null) {
            accountingItems = new ArrayList<AccountingItem>();
        }
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        if (accountingItems.isEmpty()) {
            Object object =
                BeanUtil.getAttributeFromSession(
                    AccountingItem.class.getCanonicalName()
                );
            if (object != null) {
                accountingItems = (ArrayList<AccountingItem>)object;
            }
        }

        if (accountingItems.isEmpty()) {

            Map<String, BigDecimal> balanceMap =
                    transactionModel.getBalanceByInstitution();
            for(String institutionName : balanceMap.keySet()) {
                BigDecimal balance = balanceMap.get(institutionName);

                AccountingItem accountingItem = new AccountingItem();
                accountingItem.institutionName = institutionName;
                if (balance != null) {
                    accountingItem.amount = balance.setScale(2, RoundingMode.HALF_UP);
                } else {
                    logger.warn("Model returned balance [" + balance +
                            "] for institution [" + institutionName + "]");
                }
                // Email from the registry
                accountingItem.receiverEmail = getEmail(institutionName);

                accountingItems.add(accountingItem);
            }
            BeanUtil.setAttributeInSession(
                    AccountingItem.class.getCanonicalName(), accountingItems);
        }
    }

    public List<AccountingItem> getAccountingItems() {
        return accountingItems;
    }

    private String getEmail(@NotNull String institutionName) {
        String email = "NONE";
        Institution institution =
                registryService.getInstitutionByName(institutionName);
        if (institution != null) {
            Contact contact = institution.getContactInfo();
            if (contact != null) {
                email = contact.getEmail();
            } else {
                logger.error("Unable to find email address for institution [" +
                    institutionName + "]");
            }
        } else {
            logger.error("Unable to find Institution using name [" +
                    institutionName + "]");
        }
        return email;
    }

    public EntitlementUtil getEntitlementUtil() {
        return entitlementUtil;
    }
    public void setEntitlementUtil(EntitlementUtil entitlementUtil) {
        this.entitlementUtil = entitlementUtil;
    }

    public TransactionModel getTransactionModel() {
        return transactionModel;
    }
    public void setTransactionModel(TransactionModel transactionModel) {
        this.transactionModel = transactionModel;
    }

    public RegistryImpl getRegistryService() {
        return registryService;
    }
    public void setRegistryService(RegistryImpl registryService) {
        this.registryService = registryService;
    }

    public static class AccountingItem {
        private String institutionName;
        private String receiverEmail;
        private BigDecimal amount;

        public String getInstitutionName() {
            return institutionName;
        }
        public void setInstitutionName(String institutionName) {
            this.institutionName = institutionName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getReceiverEmail() {
            return receiverEmail;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AccountingItem that = (AccountingItem) o;

            if (institutionName != null ? !institutionName.equals(that.institutionName) : that.institutionName != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return institutionName != null ? institutionName.hashCode() : 0;
        }
    }
}
