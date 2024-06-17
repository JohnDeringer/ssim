package com.sri.ssim.persistence;

import com.sri.ssim.rest.EncounterResponse;
import com.sri.ssim.rest.SearchType;

import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
public interface EncounterDao {

    Artifact createArtifact(Artifact entity);

    Collector createCollector(Collector entity);

    Corpus createCorpus(Corpus entity);

    Encounter createEncounter(Encounter encounter);

    EncounterQuality createEncounterQuality(EncounterQuality entity);

    EncounterReason createEncounterReason(EncounterReason entity);

    Ethnicity createEthnicity(Ethnicity entity);

    FileType createFileType(FileType entity);

    GeneralTime createGeneralTime(GeneralTime entity);

    GeographicLocation createGeographicLocation(GeographicLocation entity);

    Interview createInterview(Interview entity);

    Language createLanguage(Language entity);

    Participant createParticipant(Participant entity);

    Rank createRank(Rank entity);

    Role createRole(Role entity);

    Site createSite(Site entity);

    Artifact findArtifactByArtifactName(String artifactName);

    Artifact findArtifactByFilename(String filename);

    Artifact findArtifactById(Long artifactId);

    List<Artifact> findArtifactsByEncounterId(Long encounterId);

    ArtifactFile findArtifactFileByName(String filename);

    List<ArtifactFile> findFilesForProcessing();

    AudioQuality findAudioQualityByValue(String value);

    AudioQuality findAudioQualityById(Long id);

    List<AudioQuality> findAudioQualities();

    Collector findCollectorByValue(String value);

    List<Collector> findCollectors();

    List<Collector> findCollectors(String root);

    List<Corpus> findCorpus();

    List<Corpus> findCorpus(String root);

    Corpus findCorpusByValue(String value);

    Domain findDomainByValue(String value);

    List<Encounter> findEncountersByArtifactId(Long artifactId);

    List<Encounter> findEncountersByFilename(String filename);

    Encounter findEncounterById(Long encounterId);

    Encounter findEncounterByName(String encounterName);

    List<Encounter> findEncounterByName(String encounterName, WildCardType wildCard);

    EncounterQuality findEncounterQualityByValue(String value);

    List<EncounterQuality> findEncounterQualities();

    List<EncounterQuality> findEncounterQualities(String root);

    EncounterReason findEncounterReasonByValue(String value);

    List<EncounterReason> findEncounterReasons();

    List<EncounterReason> findEncounterReasons(String root);

    List<Encounter> findEncounters(SearchType searchType, List<Long> lineIds);

    Ethnicity findEthnicityByValue(String value);

    List<Ethnicity> findEthnicities();

    List<Ethnicity> findEthnicities(String root);

    ArtifactFile findFileByFilename(String filename);

    ArtifactFile findFileById(Long fileId);

    List<ArtifactFile> findArtifactFilesLike(String like, int limit);

    FileType findFileTypeByValue(String value);

    Gender findGenderByValue(String value);

    GeneralTime findGeneralTimeByValue(String value);

    List<GeneralTime> findGeneralTimes();

    List<GeneralTime> findGeneralTimes(String root);

    GeographicLocation findGeographicLocationByValue(String value);

    List<GeographicLocation> findGeographicLocations();

    List<GeographicLocation> findGeographicLocations(String root);

    Interview findInterviewById(Long interviewId);

    Interview findInterviewByName(String name);

    InterviewType findInterviewTypeByValue(String value);

    Language findLanguageByValue(String name);

    List<Language> findLanguages();

    List<Language> findLanguages(String root);

    List<Line> findFileLinesByCriteria(String criteria);

    Participant findParticipantByEncounterIdAndCode(Long encounterId, String codeName);

    List<Participant> findParticipantsByEncounterId(Long encounterId);

    ParticipantType findParticipantTypeByValue(String value);

    Rank findRankByValue(String value);

    List<Rank> findRanks();

    List<Rank> findRanks(String root);

    Role findRoleByValue(String value);

    List<Role> findRoles();

    List<Role> findRoles(String root);

    Site findSiteByValue(String value);

    List<Site> findSites();

    List<Site> findSites(String root);

    VideoQuality findVideoQualityByValue(String value);

    List<VideoQuality> findVideoQualities();

    VideoQuality findVideoQualityById(Long id);

    void markFileAsProcessed(Long artifactId);

    Artifact updateArtifact(Artifact entity);

    ArtifactFile updateArtifactFile(ArtifactFile entity);

    Encounter updateEncounter(Encounter entity);

    //updateEncounterArtifact

    Interview updateInterview(Interview interview);

    void addAudioQualityEntity(Long id, String value);

    void addVideoQualityEntity(Long id, String value);
}
