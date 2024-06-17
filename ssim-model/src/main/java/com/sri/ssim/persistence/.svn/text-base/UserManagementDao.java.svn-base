package com.sri.ssim.persistence;

import com.sri.ssim.schema.UserRoleEnum;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/24/12
 */
public interface UserManagementDao {

    User getUser(String username);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String username);

    UserRole findUserRoleByValue(UserRoleEnum value);

    void addUserRoleEntity(UserRoleEnum userRoleEnum);

}
