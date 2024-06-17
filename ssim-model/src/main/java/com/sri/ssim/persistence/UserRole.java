package com.sri.ssim.persistence;

import com.sri.ssim.schema.UserRoleEnum;

import javax.persistence.*;

import java.io.Serializable;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/24/12
 */
@Entity(name = "UserRole")
@Table(name = "USER_ROLE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "ROLE"
    })
})
public class UserRole implements Serializable {

    protected String role;
    protected Long id;

    @Basic
    @Column(name = "ROLE", length = 50, nullable = false)
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "USER_ROLE_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public UserRoleEnum getEnumValue() {
        UserRoleEnum userRoleEnum = UserRoleEnum.USER;
        if (getRole() != null) {
            userRoleEnum = UserRoleEnum.fromValue(getRole());
        }
        return userRoleEnum;
    }
    public void setEnumValue(UserRoleEnum userRoleEnum) {
        setRole(userRoleEnum.value());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRole userRole = (UserRole) o;

        if (role != null ? !role.equals(userRole.role) : userRole.role != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return role != null ? role.hashCode() : 0;
    }

}
