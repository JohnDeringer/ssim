package com.sri.ssim.persistence;

import javax.persistence.*;

import java.io.Serializable;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/24/12
 */
@Entity(name = "User")
@Table(name = "USER", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "USER_NAME"
    })
})
public class User implements Serializable {

    protected String username;
    protected byte[] password;
    protected UserRole role;
    protected Long id;

    @Basic
    @Column(name = "USER_NAME", length = 100, nullable = false)
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "PASSWORD", nullable = false)
    public byte[] getPassword() {
        return password;
    }
    public void setPassword(byte[] password) {
        this.password = password;
    }

    @ManyToOne(targetEntity = UserRole.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "USER_ROLE_ID", nullable = false)
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "USER_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (username != null ? !username.equals(user.username) : user.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

}
