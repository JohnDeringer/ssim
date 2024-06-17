package org.nterlearning.usermgmt.webapp.utils;

import org.nterlearning.usermgmt.model.User;
import org.nterlearning.usermgmt.persistence.UserDao;
import org.nterlearning.usermgmt.webapp.enumeration.ProcessState;

/**
 * This class is designed to contain methods useful in handling updates to a user's LDAP profile.
 */
public class ProfileUtils {

    /**
     * Updates a user's email address.  This should be done during the verification process or
     * when a user selects to change their email address.
     *
     * @param userDao LDAP instance
     * @param updateUser Updated user information
     *
     * @return True if update successful or not required, false otherwise
     */
    public static ProcessState processMailChangeRequest(UserDao userDao, User updateUser) {

        // verify that something actually has to be done
        if ((updateUser.getMailChange() == null) || updateUser.getMailChange().equals("")) {
            return ProcessState.PROCESS_NOT_NEEDED;
        }

        String uid = updateUser.getUid();
        userDao.updateShadowExpire(uid, "-1");
        userDao.updateEmail(uid, updateUser.getMailChange());

        if (userDao.updateMailChange(uid, "")) {
            return ProcessState.PROCESS_SUCCEEDED;
        }
        else {
            return ProcessState.PROCESS_FAILED;
        }
    }
}
