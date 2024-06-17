package com.sri.ssim.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 10/1/12
 */
@Entity(name = "Interview")
@Table(name = "INTERVIEW", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "NAME"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Interview implements Serializable, Comparable<Interview> {

    protected Long id;
    protected String name;
    protected InterviewType interviewType;
    protected Date collectionDate;
    protected GeographicLocation geographicLocation;
    protected Site site;
    protected Date sysDate;

    protected String transcript;
    protected String description;

    protected Set<Artifact> artifacts;

    public Interview() {
        setSysDate(new Date());
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "INTERVIEW_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", length = 255, nullable = true)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "DESCRIPTION", length = 1000, nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "TRANSCRIPT", length = 255, nullable = false)
    public String getTranscript() {
        return transcript;
    }
    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    @ManyToOne(targetEntity = InterviewType.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "INTERVIEW_TYPE_ID", nullable = true)
    public InterviewType getInterviewType() {
        return interviewType;
    }
    public void setInterviewType(InterviewType interviewType) {
        this.interviewType = interviewType;
    }

    @Basic
    @Column(name = "COLLECTION_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCollectionDate() {
        return collectionDate;
    }
    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
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

    @ManyToOne(targetEntity = Site.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "SITE_ID", nullable = true)
    public Site getSite() {
        return site;
    }
    public void setSite(Site site) {
        this.site = site;
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
    @JoinTable(name="INTERVIEW_X_ARTIFACT")
    public Set<Artifact> getArtifacts() {
        if (artifacts == null) {
            artifacts = new TreeSet<Artifact>();
        }
        return artifacts;
    }
    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }
/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interview interview = (Interview) o;

        if (name != null ? !name.equals(interview.name) : interview.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public int compareTo(Interview interview) {
        return getName().compareTo(interview.getName());
    }
*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interview interview = (Interview) o;

        if (transcript != null ? !transcript.equals(interview.transcript) :
                interview.transcript != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return transcript != null ? transcript.hashCode() : 0;
    }

    @Override
    public int compareTo(Interview interview) {
        return getTranscript().compareTo(interview.getTranscript());
    }

}
