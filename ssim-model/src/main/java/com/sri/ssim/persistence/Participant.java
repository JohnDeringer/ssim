package com.sri.ssim.persistence;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/27/12
 */
@Entity(name = "Participant")
@Table(name = "PARTICIPANT", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "ENCOUNTER_ID", "CODE_NAME"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Participant implements Serializable {

    protected Long id;
    protected Encounter encounter;
    protected ParticipantType participantType;
    protected Gender gender;
    protected String codeName;
    protected Age age;
    protected Rank rank;
    protected Role role;
    protected Ethnicity ethnicity;
    protected GeographicLocation geographicLocation;
    // months TODO: units
    protected int timeInCountry;
    protected String sociometricBadge;
    protected String comments;
    protected Date sysDate;

    // ManyToMany
    protected Set<Language> languages;

    public Participant() {
        setSysDate(new Date());
    }

    @Id
    @Column(name = "PARTICIPANT_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = Encounter.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ENCOUNTER_ID", nullable = false)
    public Encounter getEncounter() {
        return encounter;
    }
    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    @ManyToOne(targetEntity = ParticipantType.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "PARTICIPANT_TYPE_ID", nullable = false)
    public ParticipantType getParticipantType() {
        return participantType;
    }
    public void setParticipantType(ParticipantType participantType) {
        this.participantType = participantType;
    }

    @ManyToOne(targetEntity = Gender.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "GENDER_ID", nullable = true)
    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "CODE_NAME", length = 45, nullable = false)
    public String getCodeName() {
        return codeName;
    }
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @ManyToOne(targetEntity = Age.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "AGE_ID", nullable = true)
    public Age getAge() {
        return age;
    }
    public void setAge(Age age) {
        this.age = age;
    }

    @ManyToOne(targetEntity = Rank.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "RANK_ID", nullable = true)
    public Rank getRank() {
        return rank;
    }
    public void setRank(Rank rank) {
        this.rank = rank;
    }

    @ManyToOne(targetEntity = Role.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ROLE_ID", nullable = true)
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    @ManyToOne(targetEntity = Ethnicity.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ETHNICITY_ID", nullable = true)
    public Ethnicity getEthnicity() {
        return ethnicity;
    }
    public void setEthnicity(Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
    }

    @ManyToOne(targetEntity = GeographicLocation.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "GEO_LOCATION_ID", nullable = true)
    public GeographicLocation getGeographicLocation() {
        return geographicLocation;
    }
    public void setGeographicLocation(GeographicLocation geographicLocation) {
        this.geographicLocation = geographicLocation;
    }

    /**
     * Months
     * @return An int
     */
    @Basic
    @Column(name = "TIME_IN_COUNTRY", precision = 10, scale = 0, nullable = true)
    public int getTimeInCountry() {
        return timeInCountry;
    }
    public void setTimeInCountry(int timeInCountry) {
        this.timeInCountry = timeInCountry;
    }

    /**
     * TODO: FK_ARTIFACT ???
     * @return
     */
    @Basic
    @Column(name = "SOCIOMETRIC_BADGE", length = 100, nullable = true)
    public String getSociometricBadge() {
        return sociometricBadge;
    }
    public void setSociometricBadge(String sociometricBadge) {
        this.sociometricBadge = sociometricBadge;
    }

    @Basic
    @Column(name = "COMMENTS", length = 1000, nullable = true)
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Basic
    @Column(name = "SYS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSysDate() {
        return sysDate;
    }
    public void setSysDate(Date sysDate) {
        this.sysDate = sysDate;
    }

    @ManyToMany
    @JoinTable(name="PARTICIPANT_X_LANGUAGE")
    public Set<Language> getLanguages() {
        if (languages == null) {
            languages = new TreeSet<Language>();
        }
        return languages;
    }
    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Participant that = (Participant) o;

        if (codeName != null ? !codeName.equals(that.codeName) : that.codeName != null)
            return false;
        if (encounter != null ? !encounter.equals(that.encounter) : that.encounter != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = encounter != null ? encounter.hashCode() : 0;
        result = 31 * result + (codeName != null ? codeName.hashCode() : 0);
        return result;
    }

}
