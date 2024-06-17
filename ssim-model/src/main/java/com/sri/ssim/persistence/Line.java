package com.sri.ssim.persistence;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
@Entity(name = "Line")
@Table(name = "LINE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "FILE_ID", "ANNOTATION_ID"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Line implements Serializable {

    protected Long id;
    protected ArtifactFile artifactFile;
    protected String annotationId;
    @FullText
    protected String utterance;
    @FullText
    protected String annotation;
    protected long startTime;
    protected long endTime;
    protected long turnTime;
    protected long interveningTime;
    protected int numWords;
    protected String participant;
    protected Date sysDate;

    public Line() {
        setSysDate(new Date());
    }

    @Id
    @Column(name = "LINE_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = ArtifactFile.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "FILE_ID", nullable = false)
    public ArtifactFile getArtifactFile() {
        return artifactFile;
    }
    public void setArtifactFile(ArtifactFile artifactFile) {
        this.artifactFile = artifactFile;
    }

    @Basic
    @Column(name = "ANNOTATION_ID", length = 50, nullable = false)
    public String getAnnotationId() {
        return annotationId;
    }
    public void setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
    }

    @Basic
    @Column(name = "UTTERANCE", length = 1000, nullable = true)
    public String getUtterance() {
        return utterance;
    }
    public void setUtterance(String utterance) {
        this.utterance = utterance;
    }

    @Basic
    @Column(name = "ANNOTATION", length = 1000, nullable = true)
    public String getAnnotation() {
        return annotation;
    }
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @Basic
    @Column(name = "START_TIME", precision = 12, scale = 0, nullable = true)
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "END_TIME", precision = 12, scale = 0, nullable = true)
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Basic
    @Column(name = "TURN_TIME", precision = 12, scale = 0, nullable = true)
    public long getTurnTime() {
        return turnTime;
    }
    public void setTurnTime(long turnTime) {
        this.turnTime = turnTime;
    }

    @Basic
    @Column(name = "INTERVENE_TIME", precision = 12, scale = 0, nullable = true)
    public long getInterveningTime() {
        return interveningTime;
    }
    public void setInterveningTime(long interveningTime) {
        this.interveningTime = interveningTime;
    }

    @Basic
    @Column(name = "NUM_WORDS", precision = 10, scale = 0, nullable = true)
    public int getNumWords() {
        return numWords;
    }
    public void setNumWords(int numWords) {
        this.numWords = numWords;
    }

    @Basic
    @Column(name = "PARTICIPANT", length = 50, nullable = true)
    public String getParticipant() {
        return participant;
    }
    public void setParticipant(String participant) {
        this.participant = participant;
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

        Line that = (Line) o;

        if (annotationId != null ? !annotationId.equals(that.annotationId) : that.annotationId != null)
            return false;
        if (artifactFile != null ? !artifactFile.equals(that.artifactFile) : that.artifactFile != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = artifactFile != null ? artifactFile.hashCode() : 0;
        result = 31 * result + (annotationId != null ? annotationId.hashCode() : 0);
        return result;
    }

}
