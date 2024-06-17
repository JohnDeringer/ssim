package com.sri.ssim.persistence;

//import com.sri.ssim.schema.ParticipantTypeEnum;

import javax.persistence.*;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/27/12
 */
@XmlRootElement
@Entity(name = "ParticipantType")
@Table(name = "PARTICIPANT_TYPE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "VALUE"
    })
})
public class ParticipantType implements Serializable {

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
    @Column(name = "PARTICIPANT_TYPE_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

//    @Transient
//    public ParticipantTypeEnum getEnumValue() {
//        ParticipantTypeEnum enumValue = null;
//        String value = getValue();
//        if (value != null) {
//            enumValue = ParticipantTypeEnum.fromValue(value);
//        }
//        return enumValue;
//    }
//    public void setEnumValue(ParticipantTypeEnum enumValue) {
//        setValue(enumValue.value());
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipantType that = (ParticipantType) o;

        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

}
