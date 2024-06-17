package com.sri.ssim.persistence;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/27/12
 */
@Entity(name = "Artifact")
@Table(name = "ARTIFACT", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "ARTIFACT_NAME"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Artifact implements Serializable, Comparable<Artifact> {

    protected Long id;
    protected String artifactName;
    protected String descriptor;
    protected String comments;
    protected Date sysDate;

    protected List<ArtifactFile> artifactFiles;
    protected Set<ArtifactFile> relatedFiles;

    public Artifact() {
        setSysDate(new Date());
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "ARTIFACT_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ARTIFACT_NAME", length = 255, nullable = false)
    public String getArtifactName() {
        return artifactName;
    }
    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    @Column(name = "DESCRIPTOR", length = 1000, nullable = true)
    public String getDescriptor() {
        return descriptor;
    }
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
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
    @JoinTable(name="RELATED_FILE")
    public Set<ArtifactFile> getRelatedFiles() {
        if (relatedFiles == null) {
            relatedFiles = new TreeSet<ArtifactFile>();
        }
        return relatedFiles;
    }
    public void setRelatedFiles(Set<ArtifactFile> relatedFiles) {
        this.relatedFiles = relatedFiles;
    }

    @OneToMany(fetch=FetchType.EAGER, targetEntity = ArtifactFile.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "ARTIFACT_ID", nullable = true)
    public List<ArtifactFile> getArtifactFiles() {
        if (artifactFiles == null) {
            artifactFiles = new ArrayList<ArtifactFile>();
        }
        return artifactFiles;
    }
    public void setArtifactFiles(List<ArtifactFile> lines) {
        this.artifactFiles = lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Artifact that = (Artifact) o;

        if (artifactName != null ? !artifactName.equals(that.artifactName) : that.artifactName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return artifactName != null ? artifactName.hashCode() : 0;
    }

    @Override
    public int compareTo(Artifact artifact) {
        return getArtifactName().compareTo(artifact.getArtifactName());
    }

}
