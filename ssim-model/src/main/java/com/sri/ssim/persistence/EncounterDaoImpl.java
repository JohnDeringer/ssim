package com.sri.ssim.persistence;

import com.sri.ssim.common.MatchType;
import com.sri.ssim.common.ModelUtil;
import com.sri.ssim.rest.*;

import org.apache.commons.lang.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.persistence.criteria.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
@Repository("encounterDao")
@Transactional(readOnly = true)
public class EncounterDaoImpl implements EncounterDao {

    private EntityManager entityManager;

    private static final String AND = "and";
    private static final String OR = "or";
    private static final String YES = "yes";
    private static final String NO = "no";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
       this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Artifact createArtifact(@NotNull Artifact artifact) {

        Assert.notNull(entityManager);

        // ArtifactFile
        // Clear the artifactFile and add to artifact entity after artifact is committed
        List<ArtifactFile> artifactFiles = artifact.getArtifactFiles();
        if (artifactFiles.size() > 0) {
            artifactFiles = new ArrayList<ArtifactFile>();
            artifactFiles.addAll(artifact.getArtifactFiles());
            artifact.getArtifactFiles().clear();
        }

        logger.debug("Persisting artifact [" + artifact.getId() + "]");

        // persist the artifact
        entityManager.persist(artifact);

        // Add Participants after committing encounter
        for (ArtifactFile artifactFile : artifactFiles) {
            artifactFile.setArtifact(artifact);
            // Create artifact file
            //createArtifactFile(artifactFile);
            createOrUpdateArtifactFile(artifactFile);
        }

        logger.debug("Merging artifact files with artifact [" + artifact.getId() + "]");

        // Persist many-to-many relationship
        entityManager.merge(artifact);

        return artifact;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    private Artifact createOrUpdateArtifact(@NotNull Artifact artifact) {
        Assert.notNull(entityManager);
        Assert.notNull(artifact.getArtifactName());

        Artifact artifactDB = findArtifactByArtifactName(artifact.getArtifactName());
        if (artifactDB != null) {
            artifact.setId(artifactDB.getId());
        }

        // ArtifactFile
        // Clear the artifactFile and add to artifact entity after artifact is committed
        List<ArtifactFile> artifactFiles = artifact.getArtifactFiles();
        if (artifactFiles.size() > 0) {
            artifactFiles = new ArrayList<ArtifactFile>();
            artifactFiles.addAll(artifact.getArtifactFiles());
            artifact.getArtifactFiles().clear();
        }

        // persist the artifact
        if (artifact.getId() == null) {
            entityManager.persist(artifact);
        } else {
            entityManager.merge(artifact);
        }

        // Add Participants after committing encounter
        for (ArtifactFile artifactFile : artifactFiles) {
            artifactFile.setArtifact(artifact);
            // Create artifact file
            createOrUpdateArtifactFile(artifactFile);
        }

        // Persist many-to-many relationship
        entityManager.merge(artifact);

        return artifact;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    private ArtifactFile createArtifactFile(ArtifactFile artifactFile) {
         Assert.notNull(entityManager);

        logger.debug("Persisting artifactFile [" + artifactFile.getFileName() + "]");

        // FileType
        FileType fileTypeEnc = artifactFile.getFileType();
        if (fileTypeEnc != null) {
            FileType fileTypeEntity =
                    findFileTypeByValue(
                            fileTypeEnc.getValue()
                    );
            // If not found, create it
            if (fileTypeEntity == null) {
                fileTypeEntity = createFileType(fileTypeEnc);
            }
            artifactFile.setFileType(fileTypeEntity);
        }

        entityManager.persist(artifactFile);

        return artifactFile;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    private ArtifactFile createOrUpdateArtifactFile(ArtifactFile artifactFile) {
        Assert.notNull(entityManager);
        Assert.notNull(artifactFile.getFileName());

        ArtifactFile artifactFileDb =
                findArtifactFileByName(artifactFile.getFileName());
        if (artifactFileDb != null) {
            // Assign PK
            artifactFile.setId(artifactFileDb.getId());
        }

        // FileType
        FileType fileTypeEnc = artifactFile.getFileType();
        if (fileTypeEnc != null) {
            FileType fileTypeEntity =
                    findFileTypeByValue(
                            fileTypeEnc.getValue()
                    );
            // If not found, create it
            if (fileTypeEntity == null) {
                fileTypeEntity = createFileType(fileTypeEnc);
            }
            artifactFile.setFileType(fileTypeEntity);
        }

        if (artifactFile.getId() == null) {
            entityManager.persist(artifactFile);
        } else {
            entityManager.merge(artifactFile);
        }

        return artifactFile;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private Age createAge(@NotNull Age entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private AudioQuality createAudioQuality(@NotNull AudioQuality entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Collector createCollector(@NotNull Collector entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Corpus createCorpus(@NotNull Corpus entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private Domain createDomain(@NotNull Domain entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Encounter createEncounter(@NotNull Encounter encounter) {
        Assert.notNull(entityManager);

        // Domain - Enum
        Domain domain = encounter.getDomain();
        if (domain != null) {
            Domain domainDB =
                    findDomainByValue(
                            domain.getValue()
                    );
            // If not found, create it
            if (domainDB == null) {
                domainDB = createDomain(domain);
            }
            encounter.setDomain(domainDB);

        } else {
            throw new RuntimeException("Domain is required");
        }
        // EncounterQuality
        EncounterQuality encounterQuality =
                encounter.getEncounterQuality();
        if (encounterQuality != null) {
            EncounterQuality encounterQualityDB =
                    findEncounterQualityByValue(
                            encounterQuality.getValue()
                    );
            // If not found, create it
            if (encounterQualityDB == null) {
                encounterQualityDB = createEncounterQuality(encounterQuality);
            }
            encounter.setEncounterQuality(encounterQualityDB);
        }
        // EncounterReason
        EncounterReason encounterReason =
                encounter.getEncounterReason();
        if (encounterReason != null) {
            EncounterReason encounterReasonDB =
                    findEncounterReasonByValue(
                            encounterReason.getValue()
                    );
            // If not found, create it
            if (encounterReasonDB == null) {
                encounterReasonDB = createEncounterReason(encounterReason);
            }
            encounter.setEncounterReason(encounterReasonDB);
        }
        // Corpus
        Corpus corpus = encounter.getCorpus();
        if (corpus != null) {
            Corpus corpusDB =
                    findCorpusByValue(
                            corpus.getValue()
                    );
            // If not found, create it
            if (corpusDB == null) {
                corpusDB = createCorpus(corpus);
            }
            encounter.setCorpus(corpusDB);
        }
        // Site
        Site site = encounter.getSite();
        if (site != null) {
            Site siteDB =
                    findSiteByValue(
                            site.getValue()
                    );
            // If not found, create it
            if (siteDB == null) {
                siteDB = createSite(site);
            }
            encounter.setSite(siteDB);
        }
        // GeneralTime
        GeneralTime generalTime = encounter.getGeneralTime();
        if (generalTime != null) {
            GeneralTime generalTimeDB =
                    findGeneralTimeByValue(
                            generalTime.getValue()
                    );
            // If not found, create it
            if (generalTimeDB == null) {
                generalTimeDB = createGeneralTime(generalTime);
            }
            encounter.setGeneralTime(generalTimeDB);
        }

        // GeoOrigin
        GeographicLocation geoLocation = encounter.getGeographicLocation();
        if (geoLocation != null) {
            GeographicLocation geoLocationDB =
                    findGeographicLocationByValue(
                            geoLocation.getValue()
                    );
            // If not found, create it
            if (geoLocationDB == null) {
                geoLocationDB = createGeographicLocation(geoLocation);
            }
            encounter.setGeographicLocation(geoLocationDB);
        }
        // VideoQuality
        VideoQuality videoQuality = encounter.getVideoQuality();
        if (videoQuality != null) {
            VideoQuality videoQualityDB =
                    findVideoQualityByValue(
                            videoQuality.getValue()
                    );
            // If not found...
            if (videoQualityDB == null) {
                //videoQualityDB = createVideoQuality(videoQuality);
                logger.error("Unable to fin VideoQuality [" +
                        videoQuality.getValue() + "]");
            } else {
                encounter.setVideoQuality(videoQualityDB);
            }
        }
        // AudioQuality
        AudioQuality audioQuality = encounter.getAudioQuality();
        if (audioQuality != null) {
            AudioQuality audioQualityDB =
                    findAudioQualityByValue(
                            audioQuality.getValue()
                    );
            // If not found...
            if (audioQualityDB == null) {
               // audioQualityDB = createAudioQuality(audioQuality);
                logger.error("Unable to fin AudioQuality [" +
                        audioQuality.getValue() + "]");
            } else {
                encounter.setAudioQuality(audioQualityDB);
            }
        }
        // Collector
        Collector collector = encounter.getCollector();
        if (collector != null) {
            Collector collectorDB =
                    findCollectorByValue(
                            collector.getValue()
                    );
            // If not found, create it
            if (collectorDB == null) {
                collectorDB = createCollector(collector);
            }
            encounter.setCollector(collectorDB);
        }

        // Participants
        // Clear the participants and add to encounter entity after encounter is committed
        List<Participant> encounterParticipants = encounter.getParticipants();
        if (encounterParticipants.size() > 0) {
            encounterParticipants = new ArrayList<Participant>();
            encounterParticipants.addAll(encounter.getParticipants());
            encounter.getParticipants().clear();
        }

        // Artifacts
        for (Artifact artifact : encounter.getArtifacts()) {
            Artifact artifactDB =
                    findArtifactByArtifactName(artifact.getArtifactName());
            if (artifactDB == null) {
                artifactDB = createArtifact(artifact);
                artifact.setId(artifactDB.getId());
            }
        }

        // Interviews
        for (Interview interview : encounter.getInterviews()) {
            Interview interviewDB = findInterviewByName(interview.getName());
            if (interviewDB == null) {
                createInterview(interview);
            } else {
                interview.setId(interviewDB.getId());
            }
        }

        // Languages
        for (Language language : encounter.getLanguages()) {
            Language languageDB = findLanguageByValue(language.getValue());
            if (languageDB == null) {
                createLanguage(language);
            } else {
                language.setId(languageDB.getId());
            }
        }

        entityManager.persist(encounter);

        // Add Participants after committing encounter
        for (Participant participant : encounterParticipants) {
            participant.setEncounter(encounter);
            createParticipant(participant);
        }

        return encounter;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public EncounterQuality createEncounterQuality(
            @NotNull EncounterQuality entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public EncounterReason createEncounterReason(
            @NotNull EncounterReason entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Ethnicity createEthnicity(
            @NotNull Ethnicity entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public FileType createFileType(@NotNull FileType entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public GeneralTime createGeneralTime(@NotNull GeneralTime entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public GeographicLocation createGeographicLocation(
            @NotNull GeographicLocation entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private Gender createGender(Gender entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Interview createInterview(
            @NotNull Interview interview) {

        Assert.notNull(entityManager);

        // InterviewType
        InterviewType interviewType = interview.getInterviewType();
        if (interviewType != null) {
            InterviewType interviewTypeDB =
                    findInterviewTypeByValue(
                            interviewType.getValue()
                    );
            // If not found, create it
            if (interviewTypeDB == null) {
                interviewTypeDB = createInterviewType(interviewType);
            }
            interview.setInterviewType(interviewTypeDB);
        }

        // GeoLocation
        GeographicLocation geographicLocation = interview.getGeographicLocation();
        if (geographicLocation != null) {
            GeographicLocation geoLocationDB =
                    findGeographicLocationByValue(
                            geographicLocation.getValue()
                    );
            // If not found, create it
            if (geoLocationDB == null) {
                geoLocationDB = createGeographicLocation(geographicLocation);
            }
            interview.setGeographicLocation(geoLocationDB);
        }

        // Site
        Site site = interview.getSite();
        if (site != null) {
            Site siteDB =
                    findSiteByValue(
                            site.getValue()
                    );
            // If not found, create it
            if (siteDB == null) {
                siteDB = createSite(site);
            }
            interview.setSite(siteDB);
        }

        // Artifacts
        for (Artifact artifact : interview.getArtifacts()) {
            createArtifact(artifact);
        }

        entityManager.persist(interview);

        return interview;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private InterviewType createInterviewType(@NotNull InterviewType entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Language createLanguage(@NotNull Language entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Participant createParticipant(
            @NotNull Participant participant) {

        Assert.notNull(entityManager);

        logger.info("Creating participant with name [" +
                participant.getCodeName() + "] for encounter [" +
                participant.getEncounter().getName() + "]");

        // ParticipantType
        ParticipantType participantTypeEnc = participant.getParticipantType();
        if (participantTypeEnc != null) {
            ParticipantType participantTypeEntity =
                    findParticipantTypeByValue(
                            participantTypeEnc.getValue()
                    );
            // If not found, create it
            if (participantTypeEntity == null) {
                participantTypeEntity = createParticipantType(participantTypeEnc);
            }
            participant.setParticipantType(participantTypeEntity);
        }

        // Gender - Enum
        Gender genderEnc = participant.getGender();
        if (genderEnc != null) {
            Gender genderEntity =
                    findGenderByValue(
                            genderEnc.getValue()
                    );

            // If not found, create it
            if (genderEntity == null) {
                genderEntity = createGender(genderEnc);
            }
            participant.setGender(genderEntity);
        }

        // Rank
        Rank rankEntityEnc = participant.getRank();
        if (rankEntityEnc != null) {
            Rank rankEntity =
                    findRankByValue(
                            rankEntityEnc.getValue()
                    );
            // If not found, create it
            if (rankEntity == null) {
                rankEntity = createRank(rankEntityEnc);
            }
            participant.setRank(rankEntity);
        }

        // Role
        Role roleEntityEnc = participant.getRole();
        if (roleEntityEnc != null) {
            Role roleEntity =
                    findRoleByValue(
                            roleEntityEnc.getValue()
                    );
            // If not found, create it
            if (roleEntity == null) {
                roleEntity = createRole(roleEntityEnc);
            }
            participant.setRole(roleEntity);
        }

        // Ethnicity
        Ethnicity ethnicityEnc = participant.getEthnicity();
        if (ethnicityEnc != null) {
            Ethnicity ethnicityEntity =
                    findEthnicityByValue(
                            ethnicityEnc.getValue()
                    );
            // If not found, create it
            if (ethnicityEntity == null) {
                ethnicityEntity = createEthnicity(ethnicityEnc);
            }
            participant.setEthnicity(ethnicityEntity);
        }

        // GeoLocation
        GeographicLocation geoLocationEntityEnc = participant.getGeographicLocation();
        if (geoLocationEntityEnc != null) {
            GeographicLocation geoLocationEntity =
                    findGeographicLocationByValue(
                            geoLocationEntityEnc.getValue()
                    );
            // If not found, create it
            if (geoLocationEntity == null) {
                geoLocationEntity = createGeographicLocation(geoLocationEntityEnc);
            }
            participant.setGeographicLocation(geoLocationEntity);
        }

        // Languages
        for (Language pLanguage : participant.getLanguages()) {
            Language language = findLanguageByValue(pLanguage.getValue());
            if (language == null) {
                createLanguage(pLanguage);
            } else {
                pLanguage.setId(language.getId());
            }
        }

        // Age
        Age ageEnc = participant.getAge();
        if (ageEnc != null) {
            Age ageEntity =
                    findAgeByValue(ageEnc.getValue());
            // If not found, create it
            if (ageEntity == null) {
                ageEntity = createAge(ageEnc);
            }
            participant.setAge(ageEntity);
        }

        entityManager.persist(participant);

        return participant;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    private Participant createOrUpdateParticipant(
            @NotNull Participant participant) {

        Assert.notNull(entityManager);

        if (participant.getCodeName() == null) {
            logger.warn("Unable to create or update Participant with code name [" +
                participant.getCodeName() + "]");
            return null;
        }

        logger.info("Update participant with name [" +
                participant.getCodeName() + "] for encounter [" +
                participant.getEncounter().getName() + "]");

        Participant participantDb = findParticipantByEncounterIdAndCode(
                participant.getEncounter().getId(), participant.getCodeName());
        if (participantDb != null) {
            // Assign PK
            participant.setId(participantDb.getId());
        }

        // ParticipantType
        ParticipantType participantType = participant.getParticipantType();
        if (participantType != null) {
            ParticipantType participantTypeDB =
                    findParticipantTypeByValue(
                            participantType.getValue()
                    );
            // If not found, create it
            if (participantTypeDB == null) {
                participantTypeDB = createParticipantType(participantType);
            }
            participant.setParticipantType(participantTypeDB);
        }

        // Gender - Enum
        Gender gender = participant.getGender();
        if (gender != null) {
            Gender genderDB =
                    findGenderByValue(
                            gender.getValue()
                    );

            // If not found, create it
            if (genderDB == null) {
                genderDB = createGender(gender);
            }
            participant.setGender(genderDB);
        }

        // Rank
        Rank rank = participant.getRank();
        if (rank != null) {
            Rank rankDB =
                    findRankByValue(
                            rank.getValue()
                    );
            // If not found, create it
            if (rankDB == null) {
                rankDB = createRank(rank);
            }
            participant.setRank(rankDB);
        }

        // Role
        Role role = participant.getRole();
        if (role != null) {
            Role roleDB =
                    findRoleByValue(
                            role.getValue()
                    );
            // If not found, create it
            if (roleDB == null) {
                roleDB = createRole(role);
            }
            participant.setRole(roleDB);
        }

        // Ethnicity
        Ethnicity ethnicity = participant.getEthnicity();
        if (ethnicity != null) {
            Ethnicity ethnicityDB =
                    findEthnicityByValue(
                            ethnicity.getValue()
                    );
            // If not found, create it
            if (ethnicityDB == null) {
                ethnicityDB = createEthnicity(ethnicity);
            }
            participant.setEthnicity(ethnicityDB);
        }

        // GeoLocation
        GeographicLocation geoLocation = participant.getGeographicLocation();
        if (geoLocation != null) {
            GeographicLocation geoLocationDB =
                    findGeographicLocationByValue(
                            geoLocation.getValue()
                    );
            // If not found, create it
            if (geoLocationDB == null) {
                geoLocationDB = createGeographicLocation(geoLocation);
            }
            participant.setGeographicLocation(geoLocationDB);
        }

        // Languages
        for (Language language : participant.getLanguages()) {
            Language languageDB = findLanguageByValue(language.getValue());
            if (languageDB == null) {
                createLanguage(language);
            } else {
                language.setId(languageDB.getId());
            }
        }

        // Age
        Age age = participant.getAge();
        if (age != null) {
            Age ageDB =
                    findAgeByValue(age.getValue());
            // If not found, create it
            if (ageDB == null) {
                ageDB = createAge(age);
            }
            participant.setAge(ageDB);
        }

        if (participant.getId() == null) {
            entityManager.persist(participant);
        } else {
            entityManager.merge(participant);
        }

        return participant;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private ParticipantType createParticipantType(@NotNull ParticipantType entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Rank createRank(@NotNull Rank entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Role createRole(@NotNull Role entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Site createSite(
            @NotNull Site entity) {
        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private VideoQuality createVideoQuality(
            @NotNull VideoQuality entity) {

        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    private Line createLine(
            @NotNull Line entity) {

        Assert.notNull(entityManager);

        entityManager.persist(entity);

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Artifact findArtifactByArtifactName(@NotNull String name) {
        Assert.notNull(entityManager);

        Artifact artifactEntity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Artifact o where o.artifactName = :name");
        query.setParameter("name", name);

        List<Artifact> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            artifactEntity = entityList.get(0);
        }

        return artifactEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Artifact findArtifactByFilename(@NotNull String name) {
        Assert.notNull(entityManager);

        Artifact artifactEntity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select a FROM Artifact a left join fetch a.artifactFiles f where f.fileName = :name");
        query.setParameter("name", name);

        List<Artifact> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            artifactEntity = entityList.get(0);
        }

        return artifactEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Artifact findArtifactById(@NotNull Long artifactId) {
        Assert.notNull(entityManager);

        Artifact artifactEntity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Artifact o where o.id = :id");
        query.setParameter("id", artifactId);

        List<Artifact> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            artifactEntity = entityList.get(0);
        }

        return artifactEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ArtifactFile findArtifactFileByName(String filename) {
        Assert.notNull(entityManager);

        ArtifactFile artifactEntity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM ArtifactFile o where o.fileName = :filename");
        query.setParameter("filename", filename);

        List<ArtifactFile> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            artifactEntity = entityList.get(0);
        }

        return artifactEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<Artifact> findArtifactsByEncounterId(
            @NotNull Long encounterId) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select a FROM Encounter o left join fetch o.artifacts a where o.id = :id");
        query.setParameter("id", encounterId);

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<ArtifactFile> findFilesForProcessing() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM ArtifactFile o where o.processed = :analyzed");
        query.setParameter("analyzed", false);

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public Age findAgeByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Age entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Age o where o.value = :value");
        query.setParameter("value", value);

        List<Age> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public AudioQuality findAudioQualityByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        AudioQuality entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM AudioQuality o where o.value = :value");
        query.setParameter("value", value);

        List<AudioQuality> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public AudioQuality findAudioQualityById(@NotNull Long id) {
        Assert.notNull(entityManager);

        AudioQuality entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM AudioQuality o where o.id = :id");
        query.setParameter("id", id);

        List<AudioQuality> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<AudioQuality> findAudioQualities() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM AudioQuality o");


        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Collector findCollectorByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Collector entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Collector o where o.value = :value");
        query.setParameter("value", value);

        List<Collector> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Collector> findCollectors() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Collector o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Collector> findCollectors(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Collector o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Corpus> findCorpus() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Corpus o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Corpus> findCorpus(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Corpus o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Corpus findCorpusByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Corpus entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Corpus o where o.value = :value");
        query.setParameter("value", value);

        List<Corpus> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Encounter> findEncountersByArtifactId(Long artifactId) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Encounter o left join fetch o.artifacts a where a.id = :id");
        query.setParameter("id", artifactId);

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Domain findDomainByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Domain entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Domain o where o.value = :value");
        query.setParameter("value", value);

        List<Domain> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Encounter> findEncountersByFilename(@NotNull String filename) {
        Assert.notNull(entityManager);

        logger.debug("Find Encounter using Filename [" + filename + "]");

        List<Encounter> encounters = new ArrayList<Encounter>();

        Artifact artifact = findArtifactByFilename(filename);
        if (artifact != null) {
            Query query =
                entityManager
                    .createQuery(
                            "select o FROM Encounter o left join fetch o.languages l left join fetch o.artifacts a where a.id = :id");
            query.setParameter("id", artifact.getId());

            encounters = query.getResultList();

        } else {
            logger.info("Unable to find artifact using filename [" + filename + "]");
        }

        return encounters;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Encounter findEncounterById(@NotNull Long encounterId) {
        Assert.notNull(entityManager);

        logger.debug("Find Encounter using id [" + encounterId + "]");

        Encounter encounterEntity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Encounter o where o.id = :id");
        query.setParameter("id", encounterId);

        List<Encounter> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            encounterEntity = entityList.get(0);
        }

        return encounterEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Encounter findEncounterByName(@NotNull String encounterName) {
        Assert.notNull(entityManager);

        logger.debug("Find Encounter using name [" + encounterName + "]");

        Encounter encounterEntity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Encounter o left join fetch o.artifacts a left join fetch a.artifactFiles where o.name = :name");
        query.setParameter("name", encounterName);

        List<Encounter> encounterList = query.getResultList();
        if (!encounterList.isEmpty()) {
            encounterEntity = encounterList.get(0);
            // Find participants for this encounter
            List<Participant> participants =
                    findParticipantsByEncounterId(encounterEntity.getId());
            if (!participants.isEmpty()) {
                encounterEntity.setParticipants(participants);
            }
        } else {
            logger.info("Unable to find Encounter using name [" + encounterName + "]");
        }

        return encounterEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<Encounter> findEncounterByName(
            String encounterName, WildCardType wildCard)  {
        Assert.notNull(entityManager);

        logger.debug("Find Encounter using name [" + encounterName +
                "] using wildCard [" + wildCard + "]");

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Encounter o where o.name like :name");

        switch (wildCard) {
            case beginning:
                logger.info("BEGINNING [" + "%" + encounterName + "]");
                query.setParameter("name", "%" + encounterName);
                break;
            case both:
                logger.info("BOTH [" + "%" + encounterName + "%" + "]");
                query.setParameter("name", "%" + encounterName + "%");
                break;
            case end:
                logger.info("END [" + encounterName + "%" + "]");
                query.setParameter("name", encounterName + "%");
                break;
            default:
                logger.info("default [" + encounterName + "]");
                query.setParameter("name", encounterName);
        }

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public EncounterQuality findEncounterQualityByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        EncounterQuality entity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM EncounterQuality o where o.value = :value");
        query.setParameter("value", value);

        List<EncounterQuality> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<EncounterQuality> findEncounterQualities() {
        Assert.notNull(entityManager);

        Query query =
            entityManager
                .createQuery(
                    "select o FROM EncounterQuality o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<EncounterQuality> findEncounterQualities(String root) {
        Assert.notNull(entityManager);

        Query query =
            entityManager
                .createQuery(
                    "select o FROM EncounterQuality o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public EncounterReason findEncounterReasonByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        EncounterReason entity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM EncounterReason o where o.value = :value");
        query.setParameter("value", value);

        List<EncounterReason> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<EncounterReason> findEncounterReasons() {
        Assert.notNull(entityManager);

        Query query =
            entityManager
                .createQuery(
                    "select o FROM EncounterReason o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<EncounterReason> findEncounterReasons(String root) {
        Assert.notNull(entityManager);

        Query query =
            entityManager
                .createQuery(
                    "select o FROM EncounterReason o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<Encounter> findEncounters(SearchType search, List<Long> lineIds) {

        FilenameType filenameType = search.getFilename();
        List<TranscriptType> transcripts = search.getTranscript();
        EncounterType encounterType = search.getEncounter();
        List<SearchParticipants> participants = search.getParticipants();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<Encounter> encounterRoot = criteriaQuery.from(Encounter.class);
        CriteriaQuery selectCriteria = criteriaQuery.select(encounterRoot);

        Join artifactsJoin = encounterRoot.join(Encounter_.artifacts);
        Join artifactFilesJoin = artifactsJoin.join(Artifact_.artifactFiles);

        List<Predicate> predicateList = new ArrayList<Predicate>();
        Expression<String> encounterNameExp = encounterRoot.get(Encounter_.name);

        // Transcript-lineIds
        if (!lineIds.isEmpty()) {
            Join linesJoin = artifactFilesJoin.join(ArtifactFile_.lines);
            Expression<Long> linePkExp = linesJoin.get(Line_.id);
            logger.debug("Searching for File lines [" + lineIds + "]");
            Predicate predicate = linePkExp.in(lineIds);
            predicateList.add(predicate);
        } // End Transcript-lineIds

        if (!transcripts.isEmpty()) {
            Join fileLinesJoin =
                            artifactFilesJoin.join(ArtifactFile_.lines);
            for (TranscriptType transcriptType : transcripts) {
                // Speaker
                for (SpeakerType speakerType : transcriptType.getSpeaker()) {
                    String speaker = speakerType.getSpeaker();
                    if (StringUtils.isNotEmpty(speaker)) {
                        Expression<String> expression =
                            fileLinesJoin.get(Line_.participant);
                        Predicate predicate;
                        String match = speakerType.getSpeakerMatch();
                        String andOr = speakerType.getAndor();
                        MatchType matchType = MatchType.valueOf(match);
                        switch (matchType) {
                        case includes:
                            logger.debug("Includes [" + matchType + "]");
                            if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                predicate =
                                        criteriaBuilder.like(expression, speaker);
                                predicate = criteriaBuilder.or(predicate);
                            } else {
                                predicate =
                                        criteriaBuilder.like(expression, speaker);
                                predicate = criteriaBuilder.and(predicate);
                            }
                            break;
                        case notincludes:
                            logger.debug("notIncludes [" + matchType + "]");
                            if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                predicate =
                                        criteriaBuilder.notLike(expression, speaker);
                                predicate = criteriaBuilder.or(predicate);
                            } else {
                                predicate =
                                        criteriaBuilder.notLike(expression, speaker);
                                predicate = criteriaBuilder.and(predicate);
                            }
                            break;
                        default:
                            logger.debug("Matches [" + matchType + "]");
                            if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                predicate =
                                        criteriaBuilder.equal(expression, speaker);
                                predicate = criteriaBuilder.or(predicate);
                            } else {
                                predicate =
                                        criteriaBuilder.equal(expression, speaker);
                                predicate = criteriaBuilder.and(predicate);
                            }
                        }
                        predicateList.add(predicate);
                    }
                }
                // TurnTime
                TurnTimeType turnTimeType = transcriptType.getTurnTime();
                if (turnTimeType != null) {
                    String turnTime = turnTimeType.getTurnTime();
                    if (StringUtils.isNotEmpty(turnTime) &&
                            StringUtils.isNumeric(turnTime)) {
                        Long tTime = Long.valueOf(turnTime);
                        Expression<Long> expression =
                            fileLinesJoin.get(Line_.turnTime);
                        Predicate predicate;
                        String match = turnTimeType.getTurnTimeMatch();
                        logger.debug("TurnTime.Match [" + match + "]");
                        MatchType matchType = MatchType.valueOf(match);
                        logger.debug("TurnTime.MatchType [" + matchType + "]");
                        switch (matchType) {
                            case more:
                                logger.debug("More [" + matchType + "]");
                                predicate = criteriaBuilder.greaterThan(
                                                expression, tTime);
                                break;
                            case less:
                                logger.debug("Less [" + matchType + "]");
                                predicate = criteriaBuilder.lessThan(
                                                expression, tTime);
                                break;
                            default:
                                logger.debug("Equal [" + matchType + "]");
                                predicate = criteriaBuilder.equal(
                                                expression, tTime);
                        }
                        predicateList.add(predicate);
                    }
                }
                // NumberOfWords
                WordsType wordsType = transcriptType.getWords();
                if (wordsType != null) {
                    String words = wordsType.getWords();
                    if (StringUtils.isNotEmpty(words) &&
                            StringUtils.isNumeric(words)) {
                        Integer numWords = Integer.valueOf(words);
                        Expression<Integer> expression =
                            fileLinesJoin.get(Line_.numWords);
                        Predicate predicate;
                        String match = wordsType.getWordsMatch();
                        logger.debug("NumWords.Match [" + match + "]");
                        MatchType matchType = MatchType.valueOf(match);
                        logger.debug("NumWords.MatchType [" + matchType + "]");
                        switch (matchType) {
                            case more:
                                logger.debug("More [" + matchType + "]");
                                predicate = criteriaBuilder.greaterThan(
                                                expression, numWords);
                                break;
                            case less:
                                logger.debug("Less [" + matchType + "]");
                                predicate = criteriaBuilder.lessThan(
                                                expression, numWords);
                                break;
                            default:
                                logger.debug("Equal [" + matchType + "]");
                                predicate = criteriaBuilder.equal(
                                                expression, numWords);
                        }
                        predicateList.add(predicate);
                    }
                }

            } // End for - TranscriptType

        } // End Transcripts

        // Filename
        if (filenameType != null) {
//            String encounterName = filenameType.getFilename();
//            if (StringUtils.isNotEmpty(encounterName)) {
//                logger.debug("search by encounter name [" + encounterName + "]");
            String fileName = filenameType.getFilename();
            if (StringUtils.isNotEmpty(fileName)) {
                logger.debug("search by file name [" + fileName + "]");

                Expression<String> artifactFileNameExp =
                        artifactFilesJoin.get(ArtifactFile_.fileName);

                String matchType = filenameType.getFilenameMatch();

                logger.debug("matchType [" + matchType + "]");

                WildCardType wildCard = WildCardType.beginning;
                try {
                    wildCard = WildCardType.valueOf(matchType);
                } catch (IllegalArgumentException e) {
                    logger.error("Error converting String [" + matchType + "] to enum", e);
                }
                switch (wildCard) {
                    case beginning:
//                        logger.info("BEGINNING [" + encounterName + "%" + "]");
//                        Predicate beginPredicate =
//                                criteriaBuilder.like(
//                                        encounterNameExp, encounterName + "%");

                        logger.info("BEGINNING [" + fileName + "%" + "]");
                        Predicate beginPredicate =
                                criteriaBuilder.like(
                                        artifactFileNameExp, fileName + "%");

                        predicateList.add(beginPredicate);

                        break;
                    case both:
//                        logger.info("BOTH [" + "%" + encounterName + "%" + "]");
//                        criteriaQuery.where(
//                                criteriaBuilder.like(
//                                        encounterNameExp, "%" + encounterName + "%"));
//                        Predicate bothPredicate =
//                                criteriaBuilder.like(
//                                        encounterNameExp, "%" + encounterName + "%");

                        logger.info("BOTH [" + "%" + fileName + "%" + "]");
                        Predicate bothPredicate =
                                criteriaBuilder.like(
                                        artifactFileNameExp, "%" + fileName + "%");

                        predicateList.add(bothPredicate);

                        break;
                    case end:
//                        logger.info("END [" + "%" + encounterName + "]");
//                        Predicate endPredicate =
//                                criteriaBuilder.like(
//                                        encounterNameExp, "%" + encounterName);

                        logger.info("END [" + "%" + fileName + "]");
                        Predicate endPredicate =
                                criteriaBuilder.like(
                                        artifactFileNameExp, "%" + fileName);

                        predicateList.add(endPredicate);

                        break;
                    default:
//                        logger.info("default [" + encounterName + "]");
//                        Predicate defaultPredicate =
//                                criteriaBuilder.equal(
//                                        encounterNameExp, encounterName);

                        logger.info("default [" + fileName + "]");
                        Predicate defaultPredicate =
                                criteriaBuilder.equal(
                                        artifactFileNameExp, fileName);

                        predicateList.add(defaultPredicate);
                }
            }
        }

        if (encounterType != null) {
            // DataDescriptor
            String dataDescriptor = encounterType.getSearchDataDescriptor();
            logger.debug("dataDescriptor [" + dataDescriptor + "]");
            if (StringUtils.isNotEmpty(dataDescriptor)) {
                Expression<String> expression =
                        artifactsJoin.get(Artifact_.descriptor);
                Predicate predicate =
                        criteriaBuilder.equal(expression, dataDescriptor);
                predicateList.add(predicate);
            }
            // Domain
            List<String> domains = encounterType.getSearchDomain();
            if (!domains.isEmpty()) {
                Expression<String> expression =
                        encounterRoot.get(Encounter_.domain).get(Domain_.value);
                if (domains.size() > 1) {
                    logger.debug("domains [" + domains + "]");
                    Predicate predicate = expression.in(domains);
                    predicateList.add(predicate);
                } else {
                    String domain = domains.get(0);
                    logger.debug("domain [" + domain + "]");
                    if (StringUtils.isNotEmpty(domain)) {
                        predicateList.add(criteriaBuilder.equal(expression, domain));
                    }
                }
            }
            // Reason
            String searchReason = encounterType.getSearchReason();
            logger.debug("searchReason [" + searchReason + "]");
            if (StringUtils.isNotEmpty(searchReason)) {
                Expression<String> expression =
                        encounterRoot
                                .get(Encounter_.encounterReason)
                                .get(EncounterReason_.value);
                Predicate predicate =
                        criteriaBuilder.equal(expression, searchReason);
                predicateList.add(predicate);
            }
            // Quality TODO: Wait until data type is determined
//            SearchQualityType searchQualityType = encounterType.getSearchQuality();
//            logger.info("searchQuality [" + searchQualityType + "]");
//            if (searchQualityType != null) {
//                Expression<String> qualityExp = encounterRoot.get("encounterQuality");
//                String searchQuality = searchQualityType.getSearchQuality();
//                String searchQualityMatch = searchQualityType.getSearchQualityMatch();
//                MatchType matchType = MatchType.valueOf(searchQualityMatch);
//
//                //EncounterQuality encounterQuality =
//                //        findEncounterQualityByValue(searchQuality);
//                // ToDo - Quality is currently a String - What is GreaterThan
//                Predicate predicate;
//                switch (matchType) {
//                    case more:
//                        logger.info("More [" + matchType + "]");
//                        predicate = criteriaBuilder.greaterThan(
//                                        qualityExp, searchQuality);
//                        break;
//                    case less:
//                        logger.info("More [" + matchType + "]");
//                        predicate = criteriaBuilder.lessThan(
//                                        qualityExp, searchQuality);
//                        break;
//                    default:
//                        logger.info("Equal [" + matchType + "]");
//                        predicate = criteriaBuilder.equal(
//                                        qualityExp, searchQuality);
//                }
//
//                predicateList.add(predicate);
//            }
            // VideoQuality
            SearchVideoQualityType searchVideoQualityType = encounterType.getSearchVideoQuality();
            logger.debug("SearchVideoQuality [" + searchVideoQualityType + "]");
            if (searchVideoQualityType != null) {
                String searchQuality =
                        searchVideoQualityType.getSearchVideoQuality();
                if (StringUtils.isNotEmpty(searchQuality)) {
                    Expression<Long> expression =
                        encounterRoot
                                .get(Encounter_.videoQuality)
                                .get(VideoQuality_.id);
                    String searchQualityMatch =
                            searchVideoQualityType.getSearchVideoQualityMatch();
                    MatchType matchType = MatchType.valueOf(searchQualityMatch);
                    // Entity
                    VideoQuality videoQuality =
                            findVideoQualityByValue(searchQuality);
                    if (videoQuality != null) {
                        Predicate predicate;
                        switch (matchType) {
                            case more:
                                logger.debug("More [" + matchType + "]");
                                predicate = criteriaBuilder.greaterThan(
                                                expression, videoQuality.getId());
                                break;
                            case less:
                                logger.debug("Less [" + matchType + "]");
                                predicate = criteriaBuilder.lessThan(
                                                expression, videoQuality.getId());
                                break;
                            default:
                                logger.debug("Equal [" + matchType + "]");
                                predicate = criteriaBuilder.equal(
                                                expression, videoQuality.getId());
                        }
                        predicateList.add(predicate);
                    } else {
                        logger.error("Unable to find videoQuality [" + searchQuality + "]");
                    }
                }
            }
            // AudioQuality
            SearchAudioQualityType searchAudioQualityType =
                    encounterType.getSearchAudioQuality();
            logger.debug("SearchAudioQuality [" + searchAudioQualityType + "]");
            if (searchAudioQualityType != null) {
                String searchQuality = searchAudioQualityType.getSearchAudioQuality();
                if (StringUtils.isNotEmpty(searchQuality)) {
                    Expression<Long> expression =
                            encounterRoot
                                    .get(Encounter_.audioQuality)
                                    .get(AudioQuality_.id);
                    String searchQualityMatch =
                            searchAudioQualityType.getSearchAudioQualityMatch();
                    MatchType matchType = MatchType.valueOf(searchQualityMatch);
                    // Entity
                    AudioQuality audioQuality =
                            findAudioQualityByValue(searchQuality);
                    Predicate predicate;
                    if (audioQuality != null) {
                        switch (matchType) {
                            case more:
                                logger.debug("More [" + matchType + "]");
                                predicate = criteriaBuilder.greaterThan(
                                                expression, audioQuality.getId());
                                break;
                            case less:
                                logger.info("debug [" + matchType + "]");
                                predicate = criteriaBuilder.lessThan(
                                                expression, audioQuality.getId());
                                break;
                            default:
                                logger.info("debug [" + matchType + "]");
                                predicate = criteriaBuilder.equal(
                                                expression, audioQuality.getId());
                        }
                        predicateList.add(predicate);
                    } else {
                        logger.error("Unable to find audioQuality [" + searchQuality + "]");
                    }
                }
            }
            // GeoLocation
            String searchGeoLocation = encounterType.getSearchGeoLocation();
            logger.debug("GeoLocation [" + searchGeoLocation + "]");
            if (StringUtils.isNotEmpty(searchGeoLocation)) {
                Expression<String> expression =
                        encounterRoot
                                .get(Encounter_.geographicLocation)
                                .get(GeographicLocation_.value);
                predicateList.add(
                        criteriaBuilder.equal(expression, searchGeoLocation));
            }
            // Site
            String searchSite = encounterType.getSearchSite();
            logger.debug("SearchSite [" + searchSite + "]");
            if (StringUtils.isNotEmpty(searchSite)) {
                Expression<String> expression =
                        encounterRoot.get(Encounter_.site).get(Site_.value);
                predicateList.add(
                        criteriaBuilder.equal(expression, searchSite));
            }
            // NumParticipants
            SearchNumParticipantsType searchNumParticipantsType =
                    encounterType.getSearchNumParticipants();
            logger.debug("SearchNumParticipants [" + searchNumParticipantsType + "]");
            if (searchNumParticipantsType != null) {
                String searchNumParticipants =
                        searchNumParticipantsType.getSearchNumParticipants();
                String searchNumParticipantsMatch =
                        searchNumParticipantsType.getSearchNumParticipantsMatch();

                if (StringUtils.isNotEmpty(searchNumParticipants) &&
                        StringUtils.isNotEmpty(searchNumParticipantsMatch)) {
                    Expression<Integer> expression =
                        encounterRoot.get(Encounter_.numberOfParticipants);
                    Integer numParticipants = Integer.valueOf(searchNumParticipants);
                    MatchType matchType = MatchType.valueOf(searchNumParticipantsMatch);
                    Predicate predicate;
                    switch (matchType) {
                        case more:
                            logger.debug("More [" + matchType + "]");
                            predicate = criteriaBuilder.greaterThan(
                                            expression, numParticipants);
                            break;
                        case less:
                            logger.debug("Less [" + matchType + "]");
                            predicate = criteriaBuilder.lessThan(
                                            expression, numParticipants);
                            break;
                        default:
                            logger.debug("Equal [" + matchType + "]");
                            predicate = criteriaBuilder.equal(
                                            expression, numParticipants);
                    }
                    predicateList.add(predicate);
                }
            }
            // bystanders
            String searchBystanders = encounterType.getSearchBystanders();
            logger.debug("searchBystanders [" + searchBystanders + "]");
            if (StringUtils.isNotEmpty(searchBystanders)) {
                Expression<Boolean> expression =
                        encounterRoot.get(Encounter_.bystanders);
                boolean bystanders = false;
                if (searchBystanders.equals(YES)) {
                    bystanders = true;
                }
                Predicate predicate =
                        criteriaBuilder.equal(expression, bystanders);
                predicateList.add(predicate);
            }
            // participantFirstEncounter
            String searchFirstEncounter = encounterType.getSearchFirstEncounter();
            logger.debug("searchFirstEncounter [" + searchFirstEncounter + "]");
            if (StringUtils.isNotEmpty(searchFirstEncounter)) {
                Expression<Boolean> expression =
                        encounterRoot.get(Encounter_.participantFirstEncounter);
                Boolean firstEncounter = null;
                if (searchFirstEncounter.equals(YES)) {
                    firstEncounter = true;
                } else if (searchFirstEncounter.equals(NO)) {
                    firstEncounter = false;
                }

                if (firstEncounter != null) {
                    Predicate predicate =
                            criteriaBuilder.equal(expression, firstEncounter);
                    predicateList.add(predicate);
                }
            }
            // participantFamiliarity
            String searchFamiliar = encounterType.getSearchFamiliar();
            logger.debug("searchFamiliar [" + searchFamiliar + "]");
            if (StringUtils.isNotEmpty(searchFamiliar)) {
                Expression<Boolean> expression =
                        encounterRoot.get(Encounter_.participantFamiliarity);
                Boolean familiar = null;
                if (searchFirstEncounter.equals(YES)) {
                    familiar = true;
                } else if (searchFirstEncounter.equals(NO)) {
                    familiar = false;
                }

                if (familiar != null) {
                    Predicate predicate =
                            criteriaBuilder.equal(expression, familiar);
                    predicateList.add(predicate);
                }
            }
            // duration
            SearchDurationType searchDurationType = encounterType.getSearchDuration();
            logger.debug("SearchDuration [" + searchDurationType + "]");
            if (searchDurationType != null) {
                String searchDuration = searchDurationType.getSearchDuration();
                String match =
                        searchDurationType.getSearchDurationMatch();
                if (StringUtils.isNotEmpty(searchDuration) &&
                        StringUtils.isNotEmpty(match)) {
                    Expression<Integer> expression =
                        encounterRoot.get(Encounter_.duration);
                    Integer duration = Integer.valueOf(searchDuration);
                    MatchType matchType = MatchType.valueOf(match);
                    Predicate predicate;
                    switch (matchType) {
                        case more:
                            logger.debug("More [" + matchType + "]");
                            predicate = criteriaBuilder.greaterThan(
                                            expression, duration);
                            break;
                        case less:
                            logger.info("debug [" + matchType + "]");
                            predicate = criteriaBuilder.lessThan(
                                            expression, duration);
                            break;
                        default:
                            logger.info("debug [" + matchType + "]");
                            predicate = criteriaBuilder.equal(
                                            expression, duration);
                    }
                    predicateList.add(predicate);
                }
            }
            // collectionDate
            List<SearchDateType> searchDateTypes = encounterType.getSearchDate();
            logger.debug("SearchDateType [" + searchDateTypes + "]");
            if (searchDateTypes != null) {
                for (SearchDateType searchDateType : searchDateTypes) {
                    Predicate predicate;
                    String searchDate = searchDateType.getSearchDate();
                    Date date = ModelUtil.getDate(searchDate);
                    if (date != null) {
                        Expression<Date> expression =
                                    encounterRoot.get(Encounter_.collectionDate);
                        String andOr = searchDateType.getAndor();
                        String match = searchDateType.getSearchDateMatch();
                        MatchType matchType = MatchType.valueOf(match);
                        switch (matchType) {
                        case after:
                            logger.debug("After [" + matchType + "]");
                            if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                predicate = criteriaBuilder.greaterThan(expression, date);
                                predicate = criteriaBuilder.or(predicate);
                            } else {
                                predicate = criteriaBuilder.greaterThan(expression, date);
                                predicate = criteriaBuilder.and(predicate);
                            }
                            break;
                        case before:
                            logger.debug("Before [" + matchType + "]");
                            if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                predicate = criteriaBuilder.lessThan(expression, date);
                                predicate = criteriaBuilder.or(predicate);
                            } else {
                                predicate = criteriaBuilder.lessThan(expression, date);
                                predicate = criteriaBuilder.and(predicate);
                            }
                            break;
                        default:
                            logger.debug("On [" + matchType + "]");
                            if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                predicate = criteriaBuilder.equal(expression, date);
                                predicate = criteriaBuilder.or(predicate);
                            } else {
                                predicate = criteriaBuilder.equal(expression, date);
                                predicate = criteriaBuilder.and(predicate);
                            }
                        }
                        predicateList.add(predicate);
                    }
                }
            }
            // SearchTime / generalTime
            List<String> searchTimeList = encounterType.getSearchTime();
            logger.debug("SearchTime [" + searchTimeList + "]");
            if (!searchTimeList.isEmpty()) {
                Expression<String> expression =
                        encounterRoot
                                .get(Encounter_.generalTime)
                                .get(GeneralTime_.value);
                Predicate predicate = expression.in(searchTimeList);
                predicateList.add(predicate);
            }
            // EncounterLanguage
            List<SearchLanguageType> searchLanguageTypes =
                    encounterType.getSearchEncounterLanguage();
            logger.debug("SearchLanguageType [" + searchLanguageTypes + "]");
            if (!searchLanguageTypes.isEmpty()) {
                Join languagesJoin = encounterRoot.join(Encounter_.languages);
                Expression<String> expression =
                        languagesJoin.get(Language_.value);
                Predicate predicate;
                for (SearchLanguageType searchLanguageType : searchLanguageTypes) {
                    String language = searchLanguageType.getLanguage();
                    logger.debug("language [" + language + "]");
                    if (StringUtils.isNotEmpty(language)) {
                        String andOr = searchLanguageType.getAndor();
                        logger.debug("language.andOr [" + andOr + "]");
                        if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                            predicate = criteriaBuilder.equal(expression, language);
                            predicate = criteriaBuilder.or(predicate);
                        } else {
                            predicate = criteriaBuilder.equal(expression, language);
                            predicate = criteriaBuilder.and(predicate);
                        }

                        predicateList.add(predicate);
                    }
                }
            }
            // corpus
            String searchCorpus = encounterType.getSearchCorpus();
            logger.debug("searchCorpus [" + searchCorpus + "]");
            if (StringUtils.isNotEmpty(searchCorpus)) {
                Expression<String> expression =
                        encounterRoot.get(Encounter_.corpus).get(Corpus_.value);
                Predicate predicate = criteriaBuilder.equal(expression, searchCorpus);
                predicateList.add(predicate);
            }
            // collector
            String searchCollector = encounterType.getSearchCollector();
            logger.debug("searchCollector [" + searchCollector + "]");
            if (StringUtils.isNotEmpty(searchCollector)) {
                Expression<String> expression =
                        encounterRoot
                                .get(Encounter_.collector)
                                .get(Collector_.value);
                Predicate predicate =
                        criteriaBuilder.equal(expression, searchCollector);
                predicateList.add(predicate);
            }

        } // End EncounterType

        // SearchParticipants
        if (!participants.isEmpty()) {
            Join participantsJoin =
                    encounterRoot.join(Encounter_.participants, JoinType.LEFT);
            for (SearchParticipants participant : participants) {
                // ParticipantType
                String participantType = participant.getSearchParticipantType();
                logger.debug("ParticipantType [" + participantType + "]");
                if (StringUtils.isNotEmpty(participantType)) {
                    // ToDo: Remove this hack
                    if (participantType.equals("institution")) {
                        participantType = "institutional";
                    }
                    Expression<String> expression =
                            participantsJoin
                                    .get(Participant_.participantType)
                                    .get(ParticipantType_.value);
                    Predicate predicate =
                            criteriaBuilder.equal(expression, participantType);
                    predicateList.add(predicate);
                }
                // Gender
                String gender = participant.getSearchGender();
                logger.debug("Gender [" + gender + "]");
                if (StringUtils.isNotEmpty(gender)) {
                    Expression<String> expression =
                            participantsJoin
                                    .get(Participant_.gender)
                                    .get(Gender_.value);
                    Predicate predicate =
                            criteriaBuilder.equal(expression, gender);
                    predicateList.add(predicate);
                }
                // Age
                List<SearchAgeType> searchAgeTypes =
                        participant.getSearchAge();
                logger.debug("SearchAgeType [" + searchAgeTypes + "]");
                if (!searchAgeTypes.isEmpty()) {
                    Expression<String> expression =
                            participantsJoin.get(Participant_.age).get(Age_.value);
                    Predicate predicate;
                    for (SearchAgeType searchAgeType : searchAgeTypes) {
                        String searchAge = searchAgeType.getSearchAge();
                        logger.debug("searchAge [" + searchAge + "]");
                        if (StringUtils.isNotEmpty(searchAge)) {
                            String andOr = searchAgeType.getAndor();
                            String match = searchAgeType.getSearchAgeMatch();
                            MatchType matchType = MatchType.valueOf(match);
                            switch (matchType) {
                            case more:
                                logger.debug("More [" + matchType + "]");
                                if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                    predicate =
                                            criteriaBuilder
                                                    .greaterThan(expression, searchAge);
                                    predicate = criteriaBuilder.or(predicate);
                                } else {
                                    predicate =
                                            criteriaBuilder
                                                    .greaterThan(expression, searchAge);
                                    predicate = criteriaBuilder.and(predicate);
                                }
                                break;
                            case less:
                                logger.debug("Less [" + matchType + "]");
                                if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                    predicate =
                                            criteriaBuilder
                                                    .lessThan(expression, searchAge);
                                    predicate = criteriaBuilder.or(predicate);
                                } else {
                                    predicate =
                                            criteriaBuilder
                                                    .lessThan(expression, searchAge);
                                    predicate = criteriaBuilder.and(predicate);
                                }
                                break;
                            default:
                                logger.debug("On [" + matchType + "]");
                                if (andOr != null && andOr.equalsIgnoreCase(OR)) {
                                    predicate =
                                            criteriaBuilder.equal(expression, searchAge);
                                    predicate = criteriaBuilder.or(predicate);
                                } else {
                                    predicate =
                                            criteriaBuilder.equal(expression, searchAge);
                                    predicate = criteriaBuilder.and(predicate);
                                }
                            }
                            predicateList.add(predicate);
                        }
                    }
                }
                // Code name
                String codeName = participant.getSearchCodename();
                logger.debug("Codename [" + codeName + "]");
                if (StringUtils.isNotEmpty(codeName)) {
                    Expression<String> expression =
                        participantsJoin
                                .get(Participant_.codeName);
                    Predicate predicate =
                            criteriaBuilder.equal(expression, codeName);
                    predicateList.add(predicate);
                }
                // MilitaryRank
                // TODO: GreaterThan?
                String militaryRank = participant.getSearchMilRank();
                if (StringUtils.isNotEmpty(militaryRank)) {
                    Expression<String> expression =
                        participantsJoin
                                .get(Participant_.rank);
                    Predicate predicate =
                            criteriaBuilder.equal(expression, militaryRank);
                    predicateList.add(predicate);
                }
                // PoliceRank
                // TODO: GreaterThan?
                String policeRank = participant.getSearchMilRank();
                if (StringUtils.isNotEmpty(policeRank)) {
                    Expression<String> expression =
                        participantsJoin
                                .get(Participant_.rank);
                    Predicate predicate =
                            criteriaBuilder.equal(expression, policeRank);
                    predicateList.add(predicate);
                }
                // Ethnicity
                String ethnicity = participant.getSearchEthnicity();
                if (StringUtils.isNotEmpty(ethnicity)) {
                    Expression<String> expression =
                        participantsJoin
                                .get(Participant_.ethnicity).get(Ethnicity_.value);
                    Predicate predicate =
                            criteriaBuilder.equal(expression, ethnicity);
                    predicateList.add(predicate);
                }
                // FirstLanguage
                String firstLanguage = participant.getSearchFirstLanguage();
                if (StringUtils.isNotEmpty(firstLanguage)) {
                    Join languagesJoin =
                            participantsJoin.join(Participant_.languages);
                    Expression<String> expression =
                        languagesJoin.get(Language_.value);
                    Predicate predicate =
                            criteriaBuilder.equal(expression, firstLanguage);
                    predicateList.add(predicate);
                }
                // GeoOrigin / GeoLocation
                String searchGeoLocation = participant.getSearchGeoOrigin();
                logger.debug("GeoOrigin [" + searchGeoLocation + "]");
                if (StringUtils.isNotEmpty(searchGeoLocation)) {
                    Expression<String> expression =
                            participantsJoin
                                    .get(Participant_.geographicLocation)
                                    .get(GeographicLocation_.value);
                    predicateList.add(
                            criteriaBuilder.equal(expression, searchGeoLocation));
                }
                // TimeInCountry
                String timeInCountry = participant.getSearchTimeInCountry();
                logger.debug("TimeInCountry [" + timeInCountry + "]");
                if (StringUtils.isNotEmpty(timeInCountry) &&
                        StringUtils.isNumeric(timeInCountry)) {
                    Integer _timeInCountry = null;
                    try {
                        _timeInCountry = Integer.valueOf(timeInCountry);
                    } catch (NumberFormatException e) {
                        logger.error(
                                "Error converting TimeInCountry String to Integer [" +
                            timeInCountry + "]");
                    }
                    if (_timeInCountry != null) {
                        Expression<Integer> expression =
                                participantsJoin
                                        .get(Participant_.timeInCountry);
                        //String units = participant.getSearchTimeInCountryUnits();
                        String match = participant.getSearchTimeInCountryMatch();
                        MatchType matchType = MatchType.valueOf(match);
                        switch (matchType) {
                        case more:
                            logger.debug("More [" + matchType + "]");
                            predicateList.add(
                                criteriaBuilder.greaterThan(
                                        expression, _timeInCountry));
                            break;
                        case less:
                            logger.debug("Less [" + matchType + "]");
                            predicateList.add(
                                criteriaBuilder.lessThan(
                                        expression, _timeInCountry));
                            break;
                        default:
                            logger.debug("On [" + matchType + "]");
                            predicateList.add(
                                criteriaBuilder.equal(
                                        expression, _timeInCountry));
                        }
                    }
                }
                // Comments
                String comments = participant.getSearchComments();
                logger.debug("Comments [" + comments + "]");
                if (StringUtils.isNotEmpty(comments)) {
                    Expression<String> expression =
                            participantsJoin
                                    .get(Participant_.comments);
                    predicateList.add(
                            criteriaBuilder.equal(expression, comments));
                }

            } // End for - participants
        }

        // Query
        Predicate[] predicateArray =
                predicateList.toArray(new Predicate[predicateList.size()]);
        criteriaQuery.where(predicateArray);

        // Order by
        selectCriteria.orderBy(criteriaBuilder.desc(encounterNameExp));

        TypedQuery<Encounter> typedQuery =
                entityManager.createQuery(selectCriteria);

        return typedQuery.getResultList();

    }

//    @SuppressWarnings("unchecked")
//    @Nullable
//    public FileType findDataDescriptorByValue(@NotNull String value) {
//        Assert.notNull(entityManager);
//
//        FileType entity = null;
//        Query query =
//                entityManager
//                    .createQuery(
//                        "select o FROM FileType o where o.value = :value");
//        query.setParameter("value", value);
//
//        List<FileType> entityList = query.getResultList();
//        if (!entityList.isEmpty()) {
//            entity = entityList.get(0);
//        }
//
//        return entity;
//    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Ethnicity findEthnicityByValue(
            @NotNull String value) {
        Assert.notNull(entityManager);

        Ethnicity entity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Ethnicity o where o.value = :value");
        query.setParameter("value", value);

        List<Ethnicity> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ArtifactFile findFileByFilename(@NotNull String name) {
        Assert.notNull(entityManager);

        ArtifactFile entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM ArtifactFile o where o.fileName = :name");
        query.setParameter("name", name);

        List<ArtifactFile> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ArtifactFile findFileById(@NotNull Long fileId) {
        Assert.notNull(entityManager);

        ArtifactFile artifactEntity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM ArtifactFile o where o.id = :id");
        query.setParameter("id", fileId);

        List<ArtifactFile> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            artifactEntity = entityList.get(0);
        }

        return artifactEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Line> findFileLinesByCriteria(final String criteria) {
        Assert.notNull(entityManager);

        String jpaQL = "select o from Line o " + criteria;

        logger.info("findFileLinesByCriteria [" + jpaQL + "]");

        Query query = entityManager.createQuery(jpaQL);

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<ArtifactFile> findArtifactFilesLike(String like, int limit) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM ArtifactFile o where o.fileName like :value");
        query.setParameter("value", "%" + like + "%");
        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Ethnicity> findEthnicities() {
        Assert.notNull(entityManager);

        Query query = entityManager.createQuery("select o FROM Ethnicity o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Ethnicity> findEthnicities(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Ethnicity o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public FileType findFileTypeByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        FileType entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM FileType o where o.value = :value");
        query.setParameter("value", value);

        List<FileType> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Gender findGenderByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Gender entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Gender o where o.value = :value");
        query.setParameter("value", value);

        List<Gender> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public GeneralTime findGeneralTimeByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        GeneralTime entity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM GeneralTime o where o.value = :value");
        query.setParameter("value", value);

        List<GeneralTime> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<GeneralTime> findGeneralTimes() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM GeneralTime o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<GeneralTime> findGeneralTimes(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM GeneralTime o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public GeographicLocation findGeographicLocationByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        GeographicLocation entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM GeographicLocation o where o.value = :value");
        query.setParameter("value", value);

        List<GeographicLocation> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<GeographicLocation> findGeographicLocations() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM GeographicLocation o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<GeographicLocation> findGeographicLocations(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM GeographicLocation o where o.value LIKE :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Interview findInterviewById(Long interviewId) {
        Assert.notNull(entityManager);

        Interview interviewEntity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Interview o where o.id = :id");
        query.setParameter("id", interviewId);

        List<Interview> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            interviewEntity = entityList.get(0);
        }

        return interviewEntity;
    }

//    @Override
//    @SuppressWarnings("unchecked")
//    @Nullable
//    public Interview findInterviewByInterviewTypeGeoLocationAndSite(
//            InterviewTypeEnum interviewType, String geoLocation, String site) {
//        Assert.notNull(entityManager);
//
//        Interview entity = null;
//        Query query =
//                entityManager
//                    .createQuery(
//                        "select o FROM Interview o " +
//                        "left join o.interviewType t " +
//                        "left join o.geographicLocation g " +
//                        "left join o.site s " +
//                        "where t.value = :interviewType " +
//                        "and g.value = :geoLocation " +
//                        "and s.value = :site");
//        query.setParameter("interviewType", interviewType.value());
//        query.setParameter("geoLocation", geoLocation);
//        query.setParameter("site", site);
//
//        List<Interview> entityList = query.getResultList();
//        if (!entityList.isEmpty()) {
//            entity = entityList.get(0);
//        }
//
//        return entity;
//    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Interview findInterviewByName(String name) {
        Assert.notNull(entityManager);

        Interview entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Interview o where o.name = :name");
        query.setParameter("name", name);

        List<Interview> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public InterviewType findInterviewTypeByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        InterviewType entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM InterviewType o where o.value = :value");
        query.setParameter("value", value);

        List<InterviewType> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Language findLanguageByValue(@NotNull String name) {
        Assert.notNull(entityManager);

        Language entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Language o where o.value = :name");
        query.setParameter("name", name);

        List<Language> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Language> findLanguages() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Language o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Language> findLanguages(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Language o where o.value like :name");
        query.setParameter("name", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Participant findParticipantByEncounterIdAndCode(
            @NotNull Long encounterId, @NotNull String codeName) {
        Assert.notNull(entityManager);

        Participant participantEntity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM Encounter o left join fetch o.participants p " +
                                "where o.id = :id and p.codeName = :name");
        query.setParameter("id", encounterId);
        query.setParameter("name", codeName);

        List<Encounter> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            Encounter encounter = entityList.get(0);
            // TODO: TEST efficiency
            for (Participant participant : encounter.getParticipants()) {
                if (participant.getCodeName().equals(codeName)) {
                    participantEntity = participant;
                    break;
                }
            }
        }

        return participantEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Participant> findParticipantsByEncounterId(Long encounterId) {
        Assert.notNull(entityManager);
        List<Participant> participants = new ArrayList<Participant>();

        Query query =
            entityManager
                .createQuery(
                    "select o FROM Encounter o left join fetch o.participants p " +
                            "left join fetch p.languages where o.id = :id");
        query.setParameter("id", encounterId);

        List<Encounter> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            Encounter encounter = entityList.get(0);
            participants = encounter.getParticipants();
        }

        return participants;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public ParticipantType findParticipantTypeByValue(
            @NotNull String value) {
        Assert.notNull(entityManager);

        ParticipantType entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM ParticipantType o where o.value = :value");
        query.setParameter("value", value);

        List<ParticipantType> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Rank findRankByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Rank entity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Rank o where o.value = :value");
        query.setParameter("value", value);

        List<Rank> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<Rank> findRanks() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Rank o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<Rank> findRanks(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Rank o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Role findRoleByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Role entity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Role o where o.value = :value");
        query.setParameter("value", value);

        List<Role> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<Role> findRoles() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Role o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public List<Role> findRoles(String root) {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Role o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public Site findSiteByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        Site entity = null;
        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Site o where o.value = :value");
        query.setParameter("value", value);

        List<Site> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Site> findSites() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Site o");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<Site> findSites(String root) {
         Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                            "select o FROM Site o where o.value like :value");
        query.setParameter("value", root + "%");

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public VideoQuality findVideoQualityByValue(@NotNull String value) {
        Assert.notNull(entityManager);

        VideoQuality entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM VideoQuality o where o.value = :value");
        query.setParameter("value", value);

        List<VideoQuality> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public VideoQuality findVideoQualityById(@NotNull Long id) {
        Assert.notNull(entityManager);

        VideoQuality entity = null;
        Query query =
                entityManager
                    .createQuery(
                        "select o FROM VideoQuality o where o.id = :id");
        query.setParameter("id", id);

        List<VideoQuality> entityList = query.getResultList();
        if (!entityList.isEmpty()) {
            entity = entityList.get(0);
        }

        return entity;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NotNull
    public List<VideoQuality> findVideoQualities() {
        Assert.notNull(entityManager);

        Query query =
                entityManager
                    .createQuery(
                        "select o FROM VideoQuality o");


        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void markFileAsProcessed(@NotNull Long fileId) {
        logger.info("Marking file as processed [" + fileId + "]");
        ArtifactFile artifactFile = findFileById(fileId);
        if (artifactFile != null) {
            Assert.notNull(entityManager);

            artifactFile.setProcessed(true);

            entityManager.merge(artifactFile);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Artifact updateArtifact(@NotNull Artifact artifact) {
        Assert.notNull(entityManager);

        logger.info("Updating artifact name [" + artifact.getArtifactName() +
                "] id [" + artifact.getId() + "]");

        // Look-up existing encounter
        Assert.notNull(artifact.getId());
        Artifact dbArtifactEntity =
                findArtifactById(artifact.getId());
        if (dbArtifactEntity == null) {
            throw new RuntimeException(
                    "Unable to update artifact with id [" + artifact.getId() +
                            "] because I was unable to find it.");
        }

        entityManager.merge(artifact);

        return artifact;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public ArtifactFile updateArtifactFile(@NotNull ArtifactFile artifactFile) {
        Assert.notNull(entityManager);

        String filename = artifactFile.getFileName();
        logger.info("Updating artifactFile name [" + filename +
                "] id [" + artifactFile.getId() + "]");

        // Truncate line if too large for DB - Has no real effect on metrics or searching
        for (Line line : artifactFile.getLines()) {
            if (line.getUtterance().length() > 999) {
                String utterance = line.getUtterance().substring(0, 999);
                line.setUtterance(utterance);
            }
        }

        // FileType
        FileType fileTypeEnc = artifactFile.getFileType();
        if (fileTypeEnc != null) {
            FileType fileTypeEntity =
                    findFileTypeByValue(
                            fileTypeEnc.getValue()
                    );
            // If not found, create it
            if (fileTypeEntity == null) {
                fileTypeEntity = createFileType(fileTypeEnc);
            }
            artifactFile.setFileType(fileTypeEntity);
        }

        entityManager.merge(artifactFile);

        // Retrieve the fileId's to send to Solr
        ArtifactFile artifact = findArtifactFileByName(filename);
        if (artifact == null) {
            throw new RuntimeException("File [" + filename +
                    "] failed to properly persist");
        } else {
            artifactFile = artifact;
        }

        return artifactFile;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    // ToDo: Test
    public Encounter updateEncounter(@NotNull Encounter encounter) {
        Assert.notNull(entityManager);

        logger.info("Updating encounter name [" + encounter.getName() +
                "] id [" + encounter.getId() + "]");

        // Look-up existing encounter
        Assert.notNull(encounter.getName());
        Encounter encounterDB =
                findEncounterByName(encounter.getName());

        if (encounterDB != null) {
            // Assign PK
            encounter.setId(encounterDB.getId());

            // Domain
            Domain domain = encounter.getDomain();
            if (domain != null) {
                Domain domainDB =
                        findDomainByValue(
                                domain.getValue()
                        );
                // If not found, create it
                if (domainDB == null) {
                    domainDB = createDomain(domain);
                }
                encounter.setDomain(domainDB);
            } else {
                throw new RuntimeException("Domain is required");
            }
            // EncounterQuality
            EncounterQuality encounterQuality =
                    encounter.getEncounterQuality();
            if (encounterQuality != null) {
                EncounterQuality encounterQualityDB =
                        findEncounterQualityByValue(
                                encounterQuality.getValue()
                        );
                // If not found, create it
                if (encounterQualityDB == null) {
                    encounterQualityDB =
                            createEncounterQuality(encounterQuality);
                }
                encounter.setEncounterQuality(encounterQualityDB);
            }
            // EncounterReason
            EncounterReason encounterReason =
                    encounter.getEncounterReason();
            if (encounterReason != null) {
                EncounterReason encounterReasonDB =
                        findEncounterReasonByValue(
                                encounterReason.getValue()
                        );
                // If not found, create it
                if (encounterReasonDB == null) {
                    encounterReasonDB =
                            createEncounterReason(encounterReason);
                }
                encounter.setEncounterReason(encounterReasonDB);
            }
            // Corpus
            Corpus corpus = encounter.getCorpus();
            if (corpus != null) {
                Corpus corpusDB =
                        findCorpusByValue(
                                corpus.getValue()
                        );
                // If not found, create it
                if (corpusDB == null) {
                    corpusDB = createCorpus(corpus);
                }
                encounter.setCorpus(corpusDB);
            }
            // Site
            Site site = encounter.getSite();
            if (site != null) {
                Site siteDB =
                        findSiteByValue(
                                site.getValue()
                        );
                // If not found, create it
                if (siteDB == null) {
                    siteDB = createSite(site);
                }
                encounter.setSite(siteDB);
            }
            // GeneralTime
            GeneralTime generalTime = encounter.getGeneralTime();
            if (generalTime != null) {
                GeneralTime generalTimeDB =
                        findGeneralTimeByValue(
                                generalTime.getValue()
                        );
                // If not found, create it
                if (generalTimeDB == null) {
                    generalTimeDB = createGeneralTime(generalTime);
                }
                encounter.setGeneralTime(generalTimeDB);
            }
            // GeoLocation
            GeographicLocation geoLocation =
                    encounter.getGeographicLocation();
            if (geoLocation != null) {
                GeographicLocation geoLocationDB =
                        findGeographicLocationByValue(
                                geoLocation.getValue()
                        );
                // If not found, create it
                if (geoLocationDB == null) {
                    geoLocationDB =
                            createGeographicLocation(geoLocation);
                }
                encounter.setGeographicLocation(geoLocationDB);
            }
            // VideoQuality - Enum
            VideoQuality videoQuality = encounter.getVideoQuality();
            if (videoQuality != null) {
                VideoQuality videoQualityDB =
                        findVideoQualityByValue(
                                videoQuality.getValue()
                        );
                // If not found...
                if (videoQualityDB == null) {
                    logger.error("Unable to fin VideoQuality [" +
                            videoQuality.getValue() + "]");
                } else {
                    encounter.setVideoQuality(videoQualityDB);
                }
            }
            // AudioQuality
            AudioQuality audioQuality = encounter.getAudioQuality();
            if (audioQuality != null) {
                AudioQuality audioQualityDB =
                        findAudioQualityByValue(
                                audioQuality.getValue()
                        );
                // If not found...
                if (audioQualityDB == null) {
                    logger.error("Unable to fin AudioQuality [" +
                            audioQuality.getValue() + "]");
                } else {
                    encounter.setAudioQuality(audioQualityDB);
                }
            }
            // Collector
            Collector collector = encounter.getCollector();
            if (collector != null) {
                Collector collectorDB =
                        findCollectorByValue(
                                collector.getValue()
                        );
                // If not found, create it
                if (collectorDB == null) {
                    collectorDB = createCollector(collector);
                }
                encounter.setCollector(collectorDB);
            }

            // Participants
            for (Participant participant : encounter.getParticipants()) {
                createOrUpdateParticipant(participant);
            }

            // Artifacts
            for (Artifact artifact : encounter.getArtifacts()) {
                Artifact artifactDB =
                        findArtifactByArtifactName(artifact.getArtifactName());
                if (artifactDB == null) {
                    createOrUpdateArtifact(artifact);
                }
            }

            // Interviews
            for (Interview interview : encounter.getInterviews()) {
                Interview interviewDB =
                        findInterviewByName(interview.getName());
                if (interviewDB == null) {
                    createInterview(interview);
                } else {
                    interview.setId(interviewDB.getId());
                }
            }
            // Languages
            for (Language language : encounter.getLanguages()) {
                Language languageDB = findLanguageByValue(language.getValue());
                if (languageDB == null) {
                    createLanguage(language);
                } else {
                    language.setId(languageDB.getId());
                }
            }

            logger.info("Updating encounter Name [" +  encounter.getName() + "]");

            entityManager.merge(encounter);
        } else {
            logger.warn("Unable to update encounter [" + encounter.getName() +
                            "] because I was unable to find it.");
        }

        return encounter;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @NotNull
    public Interview updateInterview(
            @NotNull Interview interview) {

        Assert.notNull(entityManager);

        logger.info("Updating interview id [" + interview.getId() + "]");

        // Look-up existing encounter
        Assert.notNull(interview.getId());
        Interview dbInterviewEntity =
                findInterviewById(interview.getId());
        if (dbInterviewEntity == null) {
            throw new RuntimeException(
                    "Unable to update encounter with id [" + interview.getId() +
                            "] because I was unable to find it.");
        }

        // InterviewType - Enum
        InterviewType interviewTypeEnc = interview.getInterviewType();
        if (interviewTypeEnc != null) {
            InterviewType interviewTypeEntity =
                    findInterviewTypeByValue(
                            interviewTypeEnc.getValue()
                    );
            // InterviewType has pre-populated values
            if (interviewTypeEntity != null) {
                interview.setInterviewType(interviewTypeEntity);
            } else {
                throw new RuntimeException(
                        "Unable to find InterviewType entity using value [" +
                                interviewTypeEnc.getValue() + "]");
            }
        } else {
            throw new RuntimeException("InterviewType is required");
        }

        // GeoLocation
        GeographicLocation geoLocationEntityEnc = interview.getGeographicLocation();
        if (geoLocationEntityEnc != null) {
            GeographicLocation geoLocationEntity =
                    findGeographicLocationByValue(
                            geoLocationEntityEnc.getValue()
                    );
            // If not found, create it
            if (geoLocationEntity == null) {
                geoLocationEntity = createGeographicLocation(geoLocationEntityEnc);
            }
            interview.setGeographicLocation(geoLocationEntity);
        }

        // Site
        Site siteEntityEnc = interview.getSite();
        if (siteEntityEnc != null) {
            Site siteEntity =
                    findSiteByValue(
                            siteEntityEnc.getValue()
                    );
            // If not found, create it
            if (siteEntity == null) {
                siteEntity = createSite(siteEntityEnc);
            }
            interview.setSite(siteEntity);
        }

        // Artifacts
        for (Artifact artifact : interview.getArtifacts()) {
            createArtifact(artifact);
        }

        entityManager.persist(interview);

        return interview;
    }


    // Data Initialization methods - Called from DataInit

//    @Override
//    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
//    public void addDomainEntity(@NotNull DomainEnum domainEnum) {
//        Domain entity = findDomainByValue(domainEnum);
//        if (entity == null) {
//            Assert.notNull(entityManager);
////
//            entity = new Domain();
//            entity.setValue(domainEnum.value());
//
//            entityManager.persist(entity);
//
//        }
//    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void addVideoQualityEntity(@NotNull Long id, @NotNull String value) {
        VideoQuality entity = findVideoQualityById(id);
        if (entity == null) {
            Assert.notNull(entityManager);

            entity = new VideoQuality();
            entity.setId(id);
            entity.setValue(value);

            entityManager.persist(entity);
        }
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void addAudioQualityEntity(@NotNull Long id, @NotNull String value) {
        AudioQuality entity = findAudioQualityById(id);
        if (entity == null) {
            Assert.notNull(entityManager);

            entity = new AudioQuality();
            entity.setId(id);
            entity.setValue(value);

            entityManager.persist(entity);
        }
    }

//    @Override
//    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
//    public void addAudioQualityEntity(@NotNull AudioQualityEnum audioQualityEnum) {
//        AudioQuality entity = findAudioQualityByValue(audioQualityEnum);
//        if (entity == null) {
//            Assert.notNull(entityManager);
//
//            entity = new AudioQuality();
//            entity.setValue(audioQualityEnum.value());
//
//            entityManager.persist(entity);
//
//        }
//    }

//    @Override
//    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
//    public void addParticipantTypeEntity(@NotNull ParticipantTypeEnum participantTypeEnum) {
//        ParticipantType entity = findParticipantTypeByValue(participantTypeEnum);
//        if (entity == null) {
//            Assert.notNull(entityManager);
//
//            entity = new ParticipantType();
//            entity.setValue(participantTypeEnum.value());
//
//            entityManager.persist(entity);
//
//        }
//    }

//    @Override
//    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
//    public void addGenderEntity(@NotNull GenderEnum genderEnum) {
//        Gender entity = findGenderByValue(genderEnum);
//        if (entity == null) {
//            Assert.notNull(entityManager);
//
//            entity = new Gender();
//            entity.setValue(genderEnum.value());
//
//            entityManager.persist(entity);
//
//        }
//    }

//    @Override
//    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
//    public void addInterviewTypeEntity(@NotNull InterviewTypeEnum interviewTypeEnum) {
//        InterviewType entity = findInterviewTypeByValue(interviewTypeEnum);
//        if (entity == null) {
//            Assert.notNull(entityManager);
//
//            entity = new InterviewType();
//            entity.setValue(interviewTypeEnum.value());
//
//            entityManager.persist(entity);
//
//        }
//    }

    private boolean isFullTextSearch(@NotNull Class clazz, @NotNull String column) {
        boolean isFullTextSearch = false;

        for (Field field : clazz.getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof FullText &&
                        field.getName().equals(column)) {
                    isFullTextSearch = true;
                    break;
                }
            }
        }
        return isFullTextSearch;
    }

}
