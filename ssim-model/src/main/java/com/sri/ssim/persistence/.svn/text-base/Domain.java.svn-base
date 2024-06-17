package com.sri.ssim.persistence;

//import com.sri.ssim.schema.DomainEnum;

import javax.persistence.*;

import java.io.Serializable;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/27/12
 */

/**
 * Generally military or police
 */
@Entity(name = "Domain")
@Table(name = "DOMAIN", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "VALUE"
    })
})
public class Domain implements Serializable {

    protected String value;
    protected Long id;

    @Basic
    @Column(name = "VALUE", length = 100, nullable = false)
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "DOMAIN_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

//    @Transient
//    public DomainEnum getEnumValue() {
//        DomainEnum enumValue = null;
//        String value = getValue();
//        if (value != null) {
//            enumValue = DomainEnum.fromValue(value);
//        }
//        return enumValue;
//    }
//    public void setEnumValue(DomainEnum enumValue) {
//        setValue(enumValue.value());
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domain that = (Domain) o;

        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

}
