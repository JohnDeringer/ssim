package com.sri.ssim.persistence;

import javax.persistence.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/27/12
 */
//@Entity(name = "EncounterTypeEntity")
//@Table(name = "ENCOUNTER_TYPE", uniqueConstraints = {
//    @UniqueConstraint(columnNames = {
//        "NAME"
//    })
//})
@Deprecated
public class EncounterTypeEntity {

    protected String name;
    protected Long id;

//    @Basic
//    @Column(name = "NAME", length = 255)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
//    @Id
//    @Column(name = "ENCOUNTER_TYPE_ID", scale = 0)
//    @GeneratedValue(strategy = GenerationType.AUTO)
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

        EncounterTypeEntity that = (EncounterTypeEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
