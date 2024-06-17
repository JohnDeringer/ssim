package com.sri.ssim.persistence;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/27/12
 */
@XmlRootElement
@Entity(name = "Encounter")
@Table(name = "ENCOUNTER", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "NAME"
    })
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Encounter implements Serializable {

    protected Long id;
    protected String name;
    protected Domain domain;
    protected EncounterQuality encounterQuality;
    protected EncounterReason encounterReason;
    protected Corpus corpus;
    protected GeographicLocation geographicLocation;
    protected Site site;
    protected VideoQuality videoQuality;
    protected AudioQuality audioQuality;
    protected GeneralTime generalTime;
    protected Collector collector;
    protected Integer numberOfParticipants;
    protected boolean bystanders;
    protected Integer duration;
    protected boolean participantFirstEncounter;
    protected boolean participantFamiliarity;
    protected Date collectionDate;
    protected String comments;
    protected String encounterComments;
    protected String description;
    protected Date sysDate;

    // OneToMany
    protected List<Participant> participants;
    // ManyToMany
    protected Set<Artifact> artifacts;
    // ManyToMany
    protected Set<Interview> interviews;
    // ManyToMany
    protected Set<Language> languages;

    public Encounter() {
        setSysDate(new Date());
    }

    @Id
    @Column(name = "ENCOUNTER_ID", scale = 0)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", length = 255, nullable = false)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(targetEntity = Domain.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "DOMAIN_ID", nullable = false)
    public Domain getDomain() {
        return domain;
    }
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    @Basic
    @Column(name = "DESCRIPTION", length = 1000, nullable = true)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(targetEntity = EncounterQuality.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ENC_QUALITY_ID", nullable = true)
    public EncounterQuality getEncounterQuality() {
        return encounterQuality;
    }
    public void setEncounterQuality(EncounterQuality encounterQuality) {
        this.encounterQuality = encounterQuality;
    }

    @ManyToOne(targetEntity = EncounterReason.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "ENC_REASON_ID", nullable = true)
    public EncounterReason getEncounterReason() {
        return encounterReason;
    }
    public void setEncounterReason(EncounterReason encounterReason) {
        this.encounterReason = encounterReason;
    }

    @ManyToOne(targetEntity = Corpus.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "CORPUS_ID", nullable = true)
    public Corpus getCorpus() {
        return corpus;
    }
    public void setCorpus(Corpus corpus) {
        this.corpus = corpus;
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

    @ManyToOne(targetEntity = GeneralTime.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "GENERAL_TIME_ID", nullable = true)
    public GeneralTime getGeneralTime() {
        return generalTime;
    }
    public void setGeneralTime(GeneralTime generalTime) {
        this.generalTime = generalTime;
    }

    @ManyToOne(targetEntity = Collector.class, cascade = {
        CascadeType.REFRESH
    })
    @JoinColumn(name = "COLLECTOR_ID", nullable = true)
    public Collector getCollector() {
        return collector;
    }
    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    @Basic
    @Column(name = "NUM_PARTICIPANTS", precision = 10, scale = 0, nullable = true)
    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }
    public void setNumberOfParticipants(Integer numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    @Basic
    @Column(name = "BYSTANDERS", columnDefinition = "BINARY", length = 1, nullable = true)
    public boolean isBystanders() {
        return bystanders;
    }
    public void setBystanders(boolean bystanders) {
        this.bystanders = bystanders;
    }

    @Basic
    @Column(name = "DURATION", precision = 10, scale = 0, nullable = true)
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Basic
    @Column(name = "FIRST_ENCOUNTER", columnDefinition = "BINARY", length = 1, nullable = true)
    public boolean isParticipantFirstEncounter() {
        return participantFirstEncounter;
    }
    public void setParticipantFirstEncounter(boolean participantFirstEncounter) {
        this.participantFirstEncounter = participantFirstEncounter;
    }

    @Basic
    @Column(name = "PART_FAMIL", columnDefinition = "BINARY", length = 1, nullable = true)
    public boolean isParticipantFamiliarity() {
        return participantFamiliarity;
    }
    public void setParticipantFamiliarity(boolean participantFamiliarity) {
        this.participantFamiliarity = participantFamiliarity;
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

    @Basic
    @Column(name = "COMMENTS", length = 1000, nullable = true)
    public String getComments() {
        return comments;
    }
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Basic
    @Column(name = "ENC_COMMENTS", length = 1000, nullable = true)
    public String getEncounterComments() {
        return encounterComments;
    }
    public void setEncounterComments(String encounterComments) {
        this.encounterComments = encounterComments;
    }

    @ManyToOne(targetEntity = VideoQuality.class, cascade = {
            CascadeType.REFRESH
    })
    @JoinColumn(name = "VIDEO_QUALITY_ID", nullable = true)
    public VideoQuality getVideoQuality() {
        return videoQuality;
    }
    public void setVideoQuality(VideoQuality videoQuality) {
        this.videoQuality = videoQuality;
    }

    @ManyToOne(targetEntity = AudioQuality.class, cascade = {
            CascadeType.REFRESH
    })
    @JoinColumn(name = "AUDIO_QUALITY_ID", nullable = true)
    public AudioQuality getAudioQuality() {
        return audioQuality;
    }
    public void setAudioQuality(AudioQuality audioQuality) {
        this.audioQuality = audioQuality;
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

    @OneToMany(targetEntity = Participant.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "ENCOUNTER_ID", nullable = true)
    public List<Participant> getParticipants() {
        if (participants == null) {
            participants = new ArrayList<Participant>();
        }
        return participants;
    }
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @ManyToMany(fetch=FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @JoinTable(name="ENCOUNTER_X_ARTIFACT")
    public Set<Artifact> getArtifacts() {
        if (artifacts == null) {
            artifacts = new TreeSet<Artifact>();
        }
        return artifacts;
    }
    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    @ManyToMany
    @JoinTable(name="INTERVIEW_X_ENCOUNTER")
    public Set<Interview> getInterviews() {
        if (interviews == null) {
            interviews = new TreeSet<Interview>();
        }
        return interviews;
    }
    public void setInterviews(Set<Interview> interviews) {
        this.interviews = interviews;
    }

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="ENCOUNTER_X_LANGUAGE")
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

        Encounter that = (Encounter) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
