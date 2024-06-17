package com.sri.ssim.model;

import com.sri.ssim.file.TimedUtterance;
import com.sri.ssim.persistence.*;
import com.sri.ssim.rest.ArtifactsType;
import com.sri.ssim.rest.EncounterResponse;
import com.sri.ssim.rest.FileRequest;
import com.sri.ssim.rest.SearchType;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
public interface EncounterModel {

    Artifact createArtifact(Artifact artifactEntity);

    Encounter createEncounter(Encounter encounterEntity);

    Collector createCollector(Collector collectorEntity);

    Corpus createCorpus(Corpus entity);

    EncounterQuality createEncounterQuality(EncounterQuality entity);

    EncounterReason createEncounterReason(EncounterReason entity);

    Ethnicity createEthnicity(Ethnicity entity);

    FileType createFileType(FileType entity);

    GeneralTime createGeneralTime(GeneralTime entity);

    GeographicLocation createGeographicOrigin(GeographicLocation entity);

    Language createLanguage(Language entity);

    Rank createRank(Rank entity);

    Role createRole(Role entity);

    Site createSite(Site entity);

    List<ArtifactFile> getArtifactsForProcessing();

    List<Collector> getCollectors();

    List<Collector> getCollectors(String root);

    List<Corpus> getCorpus();

    List<Corpus> getCorpus(String root);

    Domain getDomainByValue(String value);

    Encounter getEncounter(String encounterName);

    List<Encounter> getEncounters(String filename);

    Set<EncounterResponse> getEncountersBySearch(SearchType search);

    List <Encounter> getEncountersByArtifact(String artifactName);

    List<EncounterQuality> getEncounterQualities();

    List<EncounterQuality> getEncounterQualities(String root);

    List<EncounterReason> getEncounterReasons();

    List<EncounterReason> getEncounterReasons(String root);

    List<ArtifactFile> getArtifactFilesLike(String like, int limit);

    List<Ethnicity> getEthnicities();

    List<Ethnicity> getEthnicities(String root);

    File getFileForDownload(FileRequest fileRequest, String tmpFileName);

    Gender getGenderByValue(String value);

    List<GeneralTime> getGeneralTimes();

    List<GeneralTime> getGeneralTimes(String root);

    List<GeographicLocation> getGeographicOrigins();

    List<GeographicLocation> getGeographicOrigins(String root);

    Interview getInterview(String name);

    List<Language> getLanguages();

    List<Language> getLanguages(String root);

    ParticipantType getParticipantTypeByValue(String value);

    List<Participant> getParticipantsByEncounterId(Long encounterId);

    List<Rank> getRanks();

    List<Rank> getRanks(String root);

    List<Role> getRoles();

    List<Role> getRoles(String root);

    List<Site> getSites();

    List<Site> getSites(String root);

    List<AudioQuality> getAudioQuality();

    List<VideoQuality> getVideoQuality();

    void markFileAsProcessed(Long FileId);

    ArtifactFile processArtifact(TimedUtterance timedUtterance);

    Encounter updateEncounter(Encounter encounterEntity);

    Artifact updateArtifact(Artifact artifactEntity);

}
