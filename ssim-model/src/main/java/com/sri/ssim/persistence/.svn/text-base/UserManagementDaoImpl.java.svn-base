package com.sri.ssim.persistence;

import com.sri.ssim.schema.UserRoleEnum;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.*;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/24/12
 */
@Repository("userManagementDao")
@Transactional(readOnly = true)
public class UserManagementDaoImpl implements UserManagementDao {

    private EntityManager entityManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
       this.entityManager = entityManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public User getUser(String username) {
        Assert.notNull(entityManager);

        User entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM User o where o.username = :value");
        query.setParameter("value", username);

        List<User> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @NotNull
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public User createUser(@NotNull User user) {
        Assert.notNull(entityManager);

        // User role
        UserRole userRole = user.getRole();
        if (userRole == null) {
            userRole = findUserRoleByValue(UserRoleEnum.USER);
            user.setRole(userRole);
        } else {
            if (userRole.getId() == null) {
                userRole = findUserRoleByValue(userRole.getEnumValue());
                user.setRole(userRole);
            }
        }

        entityManager.persist(user);

        return user;
    }

    @Override
    @NotNull
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public User updateUser(@NotNull User entity) {
        Assert.notNull(entityManager);

        entityManager.merge(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(@NotNull String username) {
        Assert.notNull(entityManager);

        User user = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM User o where o.username = :value");
        query.setParameter("value", username);

        @SuppressWarnings("unchecked")
        List<User> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            user = entityList.get(0);
        }

        if (user != null) {
            entityManager.remove(user);
        } else {
            logger.warn("Unable to remove user [" + username +
                    "] because I could not find it");
        }

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void addUserRoleEntity(UserRoleEnum userRoleEnum) {
        UserRole entity = findUserRoleByValue(userRoleEnum);
        if (entity == null) {
            Assert.notNull(entityManager);

            entity = new UserRole();
            entity.setRole(userRoleEnum.value());

            entityManager.persist(entity);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public UserRole findUserRoleByValue(UserRoleEnum value) {
        Assert.notNull(entityManager);

        UserRole entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM UserRole o where o.role = :value");
        query.setParameter("value", value.value());

        List<UserRole> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }


}
