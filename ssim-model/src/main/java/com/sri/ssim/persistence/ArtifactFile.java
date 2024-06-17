package com.sri.ssim.persistence;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 11/12/12
 */
//@XmlRootElement
@Entity(name = "ArtifactFile")
@Table(name = "FILE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "FILE_NAME"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
@NamedQuery(name="ArtifactFile.findUnprocessed", query="SELECT o FROM ArtifactFile o where o.processed = false")
public class ArtifactFile {
    protected Long id;
    protected String fileName;
    protected boolean processed;
    protected FileType fileType;
    protected Artifact artifact;
    protected Date sysDate;

    protected List<Line> lines;

    public ArtifactFile() {
        setSysDate(new Date());
        processed = false;
    }

    /**
     * Primary key
     * @return A auto-generated primary key of type Long
     */
    @Id
    @Column(name = "FILE_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "FILE_NAME", length = 255, nullable = false)
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic
    @Column(name = "PROCESSED", columnDefinition = "BINARY", length = 1, nullable = true)
    public boolean isProcessed() {
        return processed;
    }
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @ManyToOne(targetEntity = FileType.class, cascade = {
            CascadeType.REFRESH
    })
    @JoinColumn(name = "FILE_TYPE_ID", nullable = false)
    public FileType getFileType() {
        return fileType;
    }
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @ManyToOne(targetEntity = Artifact.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ARTIFACT_ID", nullable = false)
    public Artifact getArtifact() {
        return artifact;
    }
    public void setArtifact(Artifact artifact) {
        this.artifact = artifact;
    }

    @OneToMany(targetEntity = Line.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "FILE_ID", nullable = true)
    public List<Line> getLines() {
        if (lines == null) {
            lines = new ArrayList<Line>();
        }
        return lines;
    }
    public void setLines(List<Line> lines) {
        this.lines = lines;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArtifactFile artifactFile = (ArtifactFile) o;

        if (fileName != null ? !fileName.equals(artifactFile.fileName) : artifactFile.fileName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fileName != null ? fileName.hashCode() : 0;
    }

}
