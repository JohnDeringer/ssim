package org.nterlearning.commerce.persistence;

import org.jetbrains.annotations.NotNull;
import org.nterlearning.entitlement.client.Action;
import org.nterlearning.entitlement.client.EntitlementPolicy;
import org.nterlearning.entitlement.client.EntitlementService;

import org.nterlearning.usermgmt.service.jaxws.IdentityService;
import org.nterlearning.usermgmt.service.jaxws.UserImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 4/9/12
 */
public class DataInit {

    private EntitlementService entitlement;
    private IdentityService identityService;
    private String adminUID;
    private String commerceRealm;

    private static final String GLOBAL_ADMIN_RESOURCE = "*";

    private Logger logger = LoggerFactory.getLogger(DataInit.class);

    public DataInit(IdentityService identityService,
                    EntitlementService entitlement,
                    String commerceAdminEmail,
                    String commerceRealm) {

        logger.info("Commerce Service data initialization");

        this.identityService = identityService;
        this.entitlement = entitlement;
        this.commerceRealm = commerceRealm;

        // Add global admin policy
        addAdminUser(commerceAdminEmail);
    }

    public String getDefaultAdminUID() {
        return adminUID;
    }

    public String getUidFromEmail(String email) {
        String uid = null;
        UserImpl user = null;
        if (identityService != null) {
            try {
                user = identityService.getUserByEmail(email);
            } catch (Exception e) {
                logger.error("Error contacting Identity Service", e);
            }
        } else {
            logger.info("Unable to contact identityService [" + identityService + "]");
        }

        if (user != null) {
            uid = user.getUid();
        }
        return uid;
    }

    // Add entitlement for Admin user
    private void addAdminUser(String commerceAdminEmail) {

        logger.info("Adding entitlement for Admin user [" + commerceAdminEmail + "]");

        if (commerceAdminEmail != null) {
            adminUID = getUidFromEmail(commerceAdminEmail);

            if (adminUID != null) {
                if (!doesGlobalAdminExists(adminUID)) {
                    logger.info("Adding entitlement subject [" + commerceAdminEmail +
                            "] as the Commerce Global Admin");
                    if (entitlement != null) {
                        try {
                            entitlement.createPolicy(
                                    commerceRealm, adminUID,
                                    GLOBAL_ADMIN_RESOURCE, Action.GLOBAL_ADMIN);
                        } catch (Exception e) {
                            logger.error("Error contacting Entitlement Service", e);
                        }
                    } else {
                        logger.warn("Unable to contact Entitlement Service [" + entitlement + "]");
                    }
                } else {
                    logger.info("Global Admin [" + commerceAdminEmail + "] already exist, will not add");
                }
            } else {
                logger.error("Unable to retrieve UID [" + adminUID +
                        " ] for Admin user [" + commerceAdminEmail +
                        "] from IDP");
            }
        } else {
            logger.error("Unable to add entitlement for Admin user [" + commerceAdminEmail + "]");
        }
    }

    private boolean doesGlobalAdminExists(@NotNull String uid) {
        EntitlementPolicy policy = null;
        if (entitlement != null) {
            try {
                policy = entitlement.getPolicy(commerceRealm, uid, GLOBAL_ADMIN_RESOURCE);
            } catch (Exception e) {
                logger.error("Error contacting Entitlement Service", e);
            }
        } else {
            logger.info("Unable to contact Entitlement Service [" + entitlement + "]");
        }
        return (policy != null);
    }

}
