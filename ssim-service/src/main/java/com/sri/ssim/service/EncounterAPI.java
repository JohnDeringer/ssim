package com.sri.ssim.service;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.sri.ssim.model.EncounterModel;
import com.sri.ssim.model.UserManagement;
import com.sri.ssim.persistence.*;

import com.sri.ssim.rest.*;

import com.sri.ssim.schema.EncounterFile;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
public class EncounterAPI implements EncounterRestInterface {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String uploadDirectory = null;
    private String sessionCookieName;
    private String sessionCookiePath;
    private String sessionCookieDomain;
    private boolean sessionCookieSecure;

    @Autowired
    EncounterModel encounterModel;

    @Autowired
    UserManagement userManagement;

    @Override
    public Response login(MultivaluedMap<String, String> map) {

        String username = map.getFirst("username");
        String password = map.getFirst("password");

        String token = userManagement.login(username, password);

        ResponseBuilderImpl builder = new ResponseBuilderImpl();
        builder.cookie(createCookie(token));

        return builder.build();
    }

    @Override
    public Response uploadFile(List<Attachment> attachments,
                               HttpServletRequest request) {
        String filename = "unknown";
        for(Attachment attachment : attachments) {
            DataHandler handler = attachment.getDataHandler();
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = handler.getInputStream();
                MultivaluedMap<String, String> map = attachment.getHeaders();
                filename = getFileName(map);
                logger.info("Writing file [" + filename + "]");

                outputStream = new FileOutputStream(
                        new File(uploadDirectory + "/" + filename));

                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch(Exception e) {
                logger.error("Unexpected error handling file [" + filename + "]", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.error("Unexpected error closing inputStream", e);
                    }
                }

                if (outputStream != null) {
                    try {
                        outputStream.flush();
                        outputStream.close();
                    } catch (IOException e) {
                        logger.error("Unexpected error closing outputStream", e);
                    }
                }
            }
        }

        return Response.ok("file [" + filename + "] uploaded").build();
    }

    private String getNameFromFileName(String filename) {
        String name = null;
        if (filename != null) {
            name = filename.split("\\.")[0];
        }
        return name;
    }

    @Override
    public Response createEncounters(ArtifactsType artifactsType) {

        logger.info("createEncounters [" + artifactsType + "]");

        // Artifact
        Artifact artifact = null;

        // InterviewTranscripts
        for (String interviewTranscript : artifactsType.getInterviewTranscript()) {
            logger.info("interviewTranscript [" + interviewTranscript + "]");
            ArtifactFile artifactFile = new ArtifactFile();
            artifactFile.setFileName(interviewTranscript);
            // FileType
            FileType fileType = new FileType();
            FormatType formatType = FormatType.OTHER_TRANSCRIPT_CA;
            try {
                formatType =
                        FormatType.fromValue(artifactsType.getDataDescriptor());
            } catch (IllegalArgumentException e) {
                logger.error("Error creating enum from value [" +
                        artifactsType.getDataDescriptor() + "]");
            }
            fileType.setValue(formatType.value());
            artifactFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(interviewTranscript));
            }
            artifact.getArtifactFiles().add(artifactFile);
        }
        // OriginalVideo
        String _originalVideo = artifactsType.getOriginalVideo();
        ArtifactFile originalVideoFile;
        if (StringUtils.isNotEmpty(_originalVideo)) {
            logger.info("originalVideo [" + _originalVideo + "]");
            originalVideoFile = new ArtifactFile();
            originalVideoFile.setFileName(_originalVideo);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ORIGINAL_VIDEO.value());
            originalVideoFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_originalVideo));
            }
            artifact.getArtifactFiles().add(originalVideoFile);
        }
        // ConvertedVideo
        String _convertedVideo = artifactsType.getConvertedVideo();
        ArtifactFile convertedVideoFile;
        if (StringUtils.isNotEmpty(_convertedVideo)) {
            logger.info("convertedVideoFile [" + _convertedVideo + "]");
            convertedVideoFile = new ArtifactFile();
            convertedVideoFile.setFileName(_convertedVideo);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.CONVERTED_VIDEO.value());
            convertedVideoFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_convertedVideo));
            }
            artifact.getArtifactFiles().add(convertedVideoFile);
        }
        // VideoAudio
        String _videoAudio = artifactsType.getVideoAudio();
        ArtifactFile videoAudioFile;
        if (StringUtils.isNotEmpty(_videoAudio)) {
            logger.info("videoAudioFile [" + _videoAudio + "]");
            videoAudioFile = new ArtifactFile();
            videoAudioFile.setFileName(_videoAudio);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.VIDEO_AUDIO.value());
            videoAudioFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_videoAudio));
            }
            artifact.getArtifactFiles().add(videoAudioFile);
        }
        // AlternateAudio
        String _alternateAudio = artifactsType.getAlternateAudio();
        ArtifactFile alternateAudioFile;
        if (StringUtils.isNotEmpty(_alternateAudio)) {
            logger.info("alternateAudioFile [" + _alternateAudio + "]");
            alternateAudioFile = new ArtifactFile();
            alternateAudioFile.setFileName(_alternateAudio);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ALTERNATIVE_AUDIO.value());
            alternateAudioFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_alternateAudio));
            }
            artifact.getArtifactFiles().add(alternateAudioFile);
        }
        // ElanTranscriptOrthographic
        String _elanTranscriptOrthographic = artifactsType.getElanTranscriptOrthographic();
        ArtifactFile elanTranscriptOrthographicFile;
        if (StringUtils.isNotEmpty(_elanTranscriptOrthographic)) {
            logger.info("elanTranscriptOrthographicFile [" + _elanTranscriptOrthographic + "]");
            elanTranscriptOrthographicFile = new ArtifactFile();
            elanTranscriptOrthographicFile.setFileName(_elanTranscriptOrthographic);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ELAN_TRANSCRIPT_ORTHOGRAPHIC.value());
            elanTranscriptOrthographicFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_elanTranscriptOrthographic));
            }
            artifact.getArtifactFiles().add(elanTranscriptOrthographicFile);
        }
        // TraditionalTranscriptOrthographic
        String _traditionalTranscriptOrthographic = artifactsType.getTraditionalTrascriptOrthographic();
        ArtifactFile traditionalTranscriptOrthographicFile;
        if (StringUtils.isNotEmpty(_traditionalTranscriptOrthographic)) {
            logger.info("traditionalTranscriptOrthographicFile [" + _traditionalTranscriptOrthographic + "]");
            traditionalTranscriptOrthographicFile = new ArtifactFile();
            traditionalTranscriptOrthographicFile.setFileName(_traditionalTranscriptOrthographic);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TRADITIONAL_TRANSCRIPT_ORTHOGRAPHIC.value());
            traditionalTranscriptOrthographicFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_traditionalTranscriptOrthographic));
            }
            artifact.getArtifactFiles().add(traditionalTranscriptOrthographicFile);
        }
        // DelimitedTranscriptOrthographic
        String _delimitedTranscriptOrthographic = artifactsType.getTabDelimitedTranscriptOrthographic();
        ArtifactFile delimitedTranscriptOrthographicFile;
        if (StringUtils.isNotEmpty(_delimitedTranscriptOrthographic)) {
            logger.info("delimitedTranscriptOrthographicFile [" + _delimitedTranscriptOrthographic + "]");
            delimitedTranscriptOrthographicFile = new ArtifactFile();
            delimitedTranscriptOrthographicFile.setFileName(_delimitedTranscriptOrthographic);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TAB_DELIMITED_TRANSCRIPT_ORTHOGRAPHIC.value());
            delimitedTranscriptOrthographicFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_delimitedTranscriptOrthographic));
            }
            artifact.getArtifactFiles().add(delimitedTranscriptOrthographicFile);
        }
        // ElanTranscriptCa
        String _elanTranscriptCa = artifactsType.getElanTranscriptCa();
        ArtifactFile elanTranscriptCaFile;
        if (StringUtils.isNotEmpty(_elanTranscriptCa)) {
            logger.info("elanTranscriptCaFile [" + _elanTranscriptCa + "]");
            elanTranscriptCaFile = new ArtifactFile();
            elanTranscriptCaFile.setFileName(_elanTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ELAN_TRANSCRIPT_CA.value());
            elanTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_elanTranscriptCa));
            }
            artifact.getArtifactFiles().add(elanTranscriptCaFile);
        }
        // TraditionalTranscriptCa
        String _traditionalTranscriptCa = artifactsType.getTraditionalTranscriptCa();
        ArtifactFile traditionalTranscriptCaFile;
        if (StringUtils.isNotEmpty(_traditionalTranscriptCa)) {
            logger.info("traditionalTranscriptCaFile [" + _traditionalTranscriptCa + "]");
            traditionalTranscriptCaFile = new ArtifactFile();
            traditionalTranscriptCaFile.setFileName(_traditionalTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TRADITIONAL_TRANSCRIPT_CA.value());
            traditionalTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_traditionalTranscriptCa));
            }
            artifact.getArtifactFiles().add(traditionalTranscriptCaFile);
        }
        // TabDelimitedTranscriptCa
        String _tabDelimitedTranscriptCa = artifactsType.getTabDelimitedTranscriptCa();
        ArtifactFile tabDelimitedTranscriptCaFile;
        if (StringUtils.isNotEmpty(_tabDelimitedTranscriptCa)) {
            logger.info("tabDelimitedTranscriptCaFile [" + _tabDelimitedTranscriptCa + "]");
            tabDelimitedTranscriptCaFile = new ArtifactFile();
            tabDelimitedTranscriptCaFile.setFileName(_tabDelimitedTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TAB_DELIMITED_TRANSCRIPT_CA.value());
            tabDelimitedTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_tabDelimitedTranscriptCa));
            }
            artifact.getArtifactFiles().add(tabDelimitedTranscriptCaFile);
        }
        // OtherTranscriptCa
        String _otherTranscriptCa = artifactsType.getOtherTranscriptCa();
        ArtifactFile otherTranscriptCaFile;
        if (StringUtils.isNotEmpty(_otherTranscriptCa)) {
            logger.info("otherTranscriptCaFile [" + _otherTranscriptCa + "]");
            otherTranscriptCaFile = new ArtifactFile();
            otherTranscriptCaFile.setFileName(_otherTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.OTHER_TRANSCRIPT_CA.value());
            otherTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_otherTranscriptCa));
            }
            artifact.getArtifactFiles().add(otherTranscriptCaFile);
        }

        if (artifact != null) {
            // TODO
            // Populate interview entity or use artifact entity
            // Interview Comments,
            // Videoed Encounter comments,
            // Encounter Metadata comments
            /*
            Payload: {"artifactsType":{"interview-transcript":
            ["encounter.json","SFPD_AnythingElse_UK_05212012-tab.txt"],
            "data-descriptor":"cta","corpus":"Corpus",
            "collection-date":"01/29/2013","collection-time":"11:05:02",
            "geographic-location":"Location","site-location":"Site",
            "collector":"Collector","comments":"Add an Interview Comments"
             */
        }

        // Interview
        Interview interview = null;
        StimulatedRecallInterviewsType stimulatedRecallInterviews =
                            artifactsType.getStimulatedRecallInterviews();
        if (stimulatedRecallInterviews != null) {
            String file = stimulatedRecallInterviews.getStimulatedRecallTranscript();
            if (StringUtils.isNotEmpty(file)) {
                interview = new Interview();
                interview.setTranscript(file);
                // description
                String description = stimulatedRecallInterviews.getStimulatedRecallDescription();
                if (StringUtils.isNotEmpty(description)) {
                    interview.setDescription(description);
                }
            }
        }

        for (EncountersType encountersType : artifactsType.getEncounters()) {

            // Encounter
            Encounter encounter = null;
            // EncounterName
            String encounterName = encountersType.getEncounterName();
            if (StringUtils.isNotEmpty(encounterName)) {
                encounter = new Encounter();
                encounter.setName(encounterName);
            } else {
                logger.warn("Unable to create an Encounter using name [" +
                    encounterName + "]");
            }

            if (encounter != null) {
                // Corpus
                String corpus = artifactsType.getCorpus();
                if (StringUtils.isNotEmpty(corpus)) {
                    Corpus aCorpus = new Corpus();
                    aCorpus.setValue(corpus);
                    encounter.setCorpus(aCorpus);
                }
                // Collection Date and Time
                String _collectionDate = artifactsType.getCollectionDate();
                String collectionTime = artifactsType.getCollectionTime();
                if (StringUtils.isNotEmpty(_collectionDate)) {
                    Date collectionDate;
                    if (StringUtils.isNotEmpty(collectionTime)) {
                        collectionDate = getDateAndTime(_collectionDate, collectionTime);
                    } else {
                        collectionDate = getDate(_collectionDate);
                    }
                    if (collectionDate != null) {
                        encounter.setCollectionDate(collectionDate);
                    } else {
                        logger.warn("Unable to parse Date [" + _collectionDate +
                                "] and/or Time [" + collectionTime + "]");
                    }
                }
                // DaylightConditions
                GeneralTime generalTime;
                String generalCollectionTime = artifactsType.getGeneralCollectionTime();
                if (StringUtils.isNotEmpty(generalCollectionTime)) {
                    generalTime = new GeneralTime();
                    generalTime.setValue(generalCollectionTime);
                    encounter.setGeneralTime(generalTime);
                }
                // GeographicLocation
                GeographicLocation geographicLocation;
                String _geographicLocation = artifactsType.getGeographicLocation();
                if (StringUtils.isNotEmpty(_geographicLocation)) {
                    geographicLocation = new GeographicLocation();
                    geographicLocation.setValue(_geographicLocation);
                    encounter.setGeographicLocation(geographicLocation);
                }
                // Site
                Site site;
                String siteLocation = artifactsType.getSiteLocation();
                if (StringUtils.isNotEmpty(siteLocation)) {
                    site = new Site();
                    site.setValue(siteLocation);
                    encounter.setSite(site);
                }
                // Audio Quality
                AudioQuality audioQuality;
                String _audioQuality = artifactsType.getAudioQuality();
                if (StringUtils.isNotEmpty(_audioQuality)) {
                    logger.info("AudioQuality [" + _audioQuality + "]");
                    audioQuality = new AudioQuality();
                    audioQuality.setValue(_audioQuality);
                    encounter.setAudioQuality(audioQuality);
                }
                // Video Quality
                VideoQuality videoQuality;
                String _videoQuality = artifactsType.getVideoQuality();
                if (StringUtils.isNotEmpty(_videoQuality)) {
                    logger.info("VideoQuality [" + _videoQuality + "]");
                    videoQuality = new VideoQuality();
                    videoQuality.setValue(_videoQuality);
                    encounter.setVideoQuality(videoQuality);
                }
                // Collector
                Collector collector;
                String _collector = artifactsType.getCollector();
                if (StringUtils.isNotEmpty(_collector)) {
                    logger.info("collector [" + _collector + "]");
                    collector = new Collector();
                    collector.setValue(_collector);
                    encounter.setCollector(collector);
                }
                // Comments
                String comments = artifactsType.getComments();
                if (StringUtils.isNotEmpty(comments)) {
                    encounter.setComments(comments);
                }
                // TODO: Stimulated Recall Interview??? or Artifact
                // Description
                String dataDescriptor = artifactsType.getDataDescriptor();
                if (StringUtils.isNotEmpty(dataDescriptor)) {
                    encounter.setDescription(dataDescriptor);
                }

                // ** EncountersType ** //

                // EncounterComments
                String encounterComments = encountersType.getEncounterComments();
                if (StringUtils.isNotEmpty(encounterComments)) {
                    encounter.setEncounterComments(encounterComments);
                }
                // Domain
                Domain domain;
                String encounterDomain = encountersType.getEncounterDomain();
                if (StringUtils.isNotEmpty(encounterDomain)) {
                    domain = new Domain();
                    domain.setValue(encounterDomain);
                    encounter.setDomain(domain);
                }
                // NumParticipants
                Integer numParticipants;
                String encounterNumParticipants = encountersType.getEncounterNumParticipants();
                if (StringUtils.isNotEmpty(encounterNumParticipants) &&
                        StringUtils.isNumeric(encounterNumParticipants)) {
                    numParticipants = new Integer(encounterNumParticipants);
                    encounter.setNumberOfParticipants(numParticipants);
                }
                // Bystanders
                Boolean bystanders = null;
                String encounterBystanders = encountersType.getEncounterBystanders();
                if (StringUtils.isNotEmpty(encounterBystanders)) {
                    if (encounterBystanders.equalsIgnoreCase("yes")) {
                        bystanders = Boolean.TRUE;
                    } else if (encounterBystanders.equalsIgnoreCase("no")) {
                        bystanders = Boolean.FALSE;
                    } else {
                        logger.error("Unrecognized EncounterBystanders value [" +
                                encounterBystanders + "]");
                    }
                    if (bystanders != null) {
                        encounter.setBystanders(bystanders);
                    }
                }
                // EncounterReason
                EncounterReason encounterReason;
                String _encounterReason = encountersType.getEncounterReason();
                if (StringUtils.isNotEmpty(_encounterReason)) {
                    encounterReason = new EncounterReason();
                    encounterReason.setValue(_encounterReason);
                    encounter.setEncounterReason(encounterReason);
                }
                // Duration
                Integer duration;
                String _encounterDuration = encountersType.getEncounterDuration();
                if (StringUtils.isNotEmpty(_encounterDuration) &&
                        StringUtils.isNumeric(_encounterDuration)) {
                    try {
                        duration = new Integer(_encounterDuration);
                        encounter.setDuration(duration);
                    } catch (Exception e) {
                        logger.error("Error marshaling duration value [" +
                            _encounterDuration + "]");
                    }
                }
                // EncounterQuality
                EncounterQuality encounterQuality;
                String _encounterQuality = encountersType.getEncounterQuality();
                if (StringUtils.isNotEmpty(_encounterQuality)) {
                    encounterQuality = new EncounterQuality();
                    encounterQuality.setValue(_encounterQuality);
                    encounter.setEncounterQuality(encounterQuality);
                }
                // ParticipantFirstEncounter
                Boolean participantFirstEncounter = null;
                String _encounterFirstEncounter = encountersType.getEncounterFirstEncounter();
                if (StringUtils.isNotEmpty(_encounterFirstEncounter)) {
                    if (_encounterFirstEncounter.equalsIgnoreCase("yes")) {
                        participantFirstEncounter = Boolean.TRUE;
                    } else if (_encounterFirstEncounter.equalsIgnoreCase("no")) {
                        participantFirstEncounter = Boolean.FALSE;
                    } else {
                        logger.error("Unrecognized EncounterFirstEncounter value [" +
                                _encounterFirstEncounter + "]");
                    }
                    if (participantFirstEncounter != null) {
                        encounter.setParticipantFirstEncounter(participantFirstEncounter);
                    }
                }
                // ParticipantFamiliarity
                Boolean participantFamiliarity = null;
                String _encounterFamiliar = encountersType.getEncounterFamiliar();
                if (StringUtils.isNotEmpty(_encounterFamiliar)) {
                    if (_encounterFamiliar.equalsIgnoreCase("yes")) {
                        participantFamiliarity = Boolean.TRUE;
                    } else if (_encounterFamiliar.equalsIgnoreCase("no")) {
                        participantFamiliarity = Boolean.FALSE;
                    } else {
                        logger.error("Unrecognized EncounterFamiliar value [" +
                                _encounterFamiliar + "]");
                    }
                    if (participantFamiliarity != null) {
                        encounter.setParticipantFamiliarity(participantFamiliarity);
                    }
                }
                // Languages
                Set<Language> languages = new TreeSet<Language>();
                for (String _language : encountersType.getEncounterLanguages()) {
                    Language language = new Language();
                    language.setValue(_language);
                    languages.add(language);
                }
                if (!languages.isEmpty()) {
                    encounter.setLanguages(languages);
                }
                // Participants
                List<Participant> participants = new ArrayList<Participant>();
                for (ParticipantsType participantsType : encountersType.getParticipants()) {
                    Participant participant = new Participant();
                    // Age
                    String _age = participantsType.getAge();
                    if (StringUtils.isNotEmpty(_age)) {
                        Age age = new Age();
                        age.setValue(_age);
                        participant.setAge(age);
                    }
                    // Codename
                    String _codename = participantsType.getCodename();
                    if (StringUtils.isNotEmpty(_codename)) {
                        participant.setCodeName(_codename);
                    }
                    // Ethnicity
                    String _ethnicity = participantsType.getEthnicity();
                    if (StringUtils.isNotEmpty(_ethnicity)) {
                        Ethnicity ethnicity = new Ethnicity();
                        ethnicity.setValue(_ethnicity);
                        participant.setEthnicity(ethnicity);
                    }
                    // Gender
                    String _gender = participantsType.getGender();
                    if (StringUtils.isNotEmpty(_gender)) {
                        Gender gender = new Gender();
                        gender.setValue(_gender);
                        participant.setGender(gender);
                    }
                    // GeographicOrigin
                    String _origin = participantsType.getOrigin();
                    if (StringUtils.isNotEmpty(_origin)) {
                        GeographicLocation geoLocation = new GeographicLocation();
                        geoLocation.setValue(_origin);
                        participant.setGeographicLocation(geoLocation);
                    }
                    // ParticipantComments
                    String _participantComments =
                            participantsType.getParticipantComments();
                    if (StringUtils.isNotEmpty(_participantComments)) {
                        participant.setComments(_participantComments);
                    }
                    // Role
                    String _role = participantsType.getRole();
                    if (StringUtils.isNotEmpty(_role)) {
                        Role role = new Role();
                        role.setValue(_role);
                        participant.setRole(role);
                    }
                    // ParticipantsType
                    String _type = participantsType.getType();
                    if (StringUtils.isNotEmpty(_type)) {
                        ParticipantType participantType = new ParticipantType();
                        participantType.setValue(_type);
                        participant.setParticipantType(participantType);
                    }
                    // TimeInCountry
                    String timeInCountry = participantsType.getTimeInCountry();
                    if (StringUtils.isNotEmpty(timeInCountry) &&
                        StringUtils.isNumeric(timeInCountry)) {
                        try {
                            int _timeInCountry = new Integer(timeInCountry);
                            participant.setTimeInCountry(_timeInCountry);
                        } catch (Exception e) {
                            logger.error("Error marshaling timeInCountry value [" +
                                timeInCountry + "]");
                        }
                    }
                    // Sociometric Badge
                    String sociometricBadge = participantsType.getSociometricBadge();
                    if (StringUtils.isNotEmpty(sociometricBadge)) {
                        participant.setSociometricBadge(sociometricBadge);
                    }
                    // Languages
                    for (String participantLanguage :
                            participantsType.getParticipantLanguages()) {
                        Language language = new Language();
                        language.setValue(participantLanguage);
                        participant.getLanguages().add(language);
                    }
                    // Add participant to participants collection
                    participants.add(participant);
                }

                // Add participant to encounter
                if (!participants.isEmpty()) {
                     encounter.setParticipants(participants);
                }

                // Relate artifact to encounter
                if (artifact != null) {
                    encounter.getArtifacts().add(artifact);
                }

                // Relate interview to encounter
                if (interview != null) {
                    encounter.getInterviews().add(interview);
                }

                encounterModel.createEncounter(encounter);

            } // End encounter
        }

        if (artifactsType.getEncounters().isEmpty() && artifact != null ) {
            encounterModel.createArtifact(artifact);
        }

        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    public Response updateEncounters(ArtifactsType artifactsType) {

        logger.info("updateEncounters [" + artifactsType + "]");

        // Artifact
        Artifact artifact = null;

        // InterviewTranscripts
        for (String interviewTranscript : artifactsType.getInterviewTranscript()) {
            logger.info("interviewTranscript [" + interviewTranscript + "]");
            ArtifactFile artifactFile = new ArtifactFile();
            artifactFile.setFileName(interviewTranscript);
            // FileType
            FileType fileType = new FileType();
            FormatType formatType = FormatType.OTHER_TRANSCRIPT_CA;
            try {
                formatType =
                        FormatType.fromValue(artifactsType.getDataDescriptor());
            } catch (IllegalArgumentException e) {
                logger.error("Error creating enum from value [" +
                        artifactsType.getDataDescriptor() + "]");
            }
            fileType.setValue(formatType.value());
            artifactFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(interviewTranscript));
            }
            artifact.getArtifactFiles().add(artifactFile);
        }

        // OriginalVideo
        String _originalVideo = artifactsType.getOriginalVideo();
        ArtifactFile originalVideoFile;
        if (StringUtils.isNotEmpty(_originalVideo)) {
            logger.info("originalVideo [" + _originalVideo + "]");
            originalVideoFile = new ArtifactFile();
            originalVideoFile.setFileName(_originalVideo);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ORIGINAL_VIDEO.value());
            originalVideoFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_originalVideo));
            }
            artifact.getArtifactFiles().add(originalVideoFile);
        }
        // ConvertedVideo
        String _convertedVideo = artifactsType.getConvertedVideo();
        ArtifactFile convertedVideoFile;
        if (StringUtils.isNotEmpty(_convertedVideo)) {
            logger.info("convertedVideoFile [" + _convertedVideo + "]");
            convertedVideoFile = new ArtifactFile();
            convertedVideoFile.setFileName(_convertedVideo);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.CONVERTED_VIDEO.value());
            convertedVideoFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_convertedVideo));
            }
            artifact.getArtifactFiles().add(convertedVideoFile);
        }
        // VideoAudio
        String _videoAudio = artifactsType.getVideoAudio();
        ArtifactFile videoAudioFile;
        if (StringUtils.isNotEmpty(_videoAudio)) {
            logger.info("videoAudioFile [" + _videoAudio + "]");
            videoAudioFile = new ArtifactFile();
            videoAudioFile.setFileName(_videoAudio);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.VIDEO_AUDIO.value());
            videoAudioFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_videoAudio));
            }
            artifact.getArtifactFiles().add(videoAudioFile);
        }
        // AlternateAudio
        String _alternateAudio = artifactsType.getAlternateAudio();
        ArtifactFile alternateAudioFile;
        if (StringUtils.isNotEmpty(_alternateAudio)) {
            logger.info("alternateAudioFile [" + _alternateAudio + "]");
            alternateAudioFile = new ArtifactFile();
            alternateAudioFile.setFileName(_alternateAudio);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ALTERNATIVE_AUDIO.value());
            alternateAudioFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_alternateAudio));
            }
            artifact.getArtifactFiles().add(alternateAudioFile);
        }
        // ElanTranscriptOrthographic
        String _elanTranscriptOrthographic = artifactsType.getElanTranscriptOrthographic();
        ArtifactFile elanTranscriptOrthographicFile;
        if (StringUtils.isNotEmpty(_elanTranscriptOrthographic)) {
            logger.info("elanTranscriptOrthographicFile [" + _elanTranscriptOrthographic + "]");
            elanTranscriptOrthographicFile = new ArtifactFile();
            elanTranscriptOrthographicFile.setFileName(_elanTranscriptOrthographic);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ELAN_TRANSCRIPT_ORTHOGRAPHIC.value());
            elanTranscriptOrthographicFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_elanTranscriptOrthographic));
            }
            artifact.getArtifactFiles().add(elanTranscriptOrthographicFile);
        }
        // TraditionalTranscriptOrthographic
        String _traditionalTranscriptOrthographic = artifactsType.getTraditionalTrascriptOrthographic();
        ArtifactFile traditionalTranscriptOrthographicFile;
        if (StringUtils.isNotEmpty(_traditionalTranscriptOrthographic)) {
            logger.info("traditionalTranscriptOrthographicFile [" + _traditionalTranscriptOrthographic + "]");
            traditionalTranscriptOrthographicFile = new ArtifactFile();
            traditionalTranscriptOrthographicFile.setFileName(_traditionalTranscriptOrthographic);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TRADITIONAL_TRANSCRIPT_ORTHOGRAPHIC.value());
            traditionalTranscriptOrthographicFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_traditionalTranscriptOrthographic));
            }
            artifact.getArtifactFiles().add(traditionalTranscriptOrthographicFile);
        }
        // DelimitedTranscriptOrthographic
        String _delimitedTranscriptOrthographic = artifactsType.getTabDelimitedTranscriptOrthographic();
        ArtifactFile delimitedTranscriptOrthographicFile;
        if (StringUtils.isNotEmpty(_delimitedTranscriptOrthographic)) {
            logger.info("delimitedTranscriptOrthographicFile [" + _delimitedTranscriptOrthographic + "]");
            delimitedTranscriptOrthographicFile = new ArtifactFile();
            delimitedTranscriptOrthographicFile.setFileName(_delimitedTranscriptOrthographic);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TAB_DELIMITED_TRANSCRIPT_ORTHOGRAPHIC.value());
            delimitedTranscriptOrthographicFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_delimitedTranscriptOrthographic));
            }
            artifact.getArtifactFiles().add(delimitedTranscriptOrthographicFile);
        }
        // ElanTranscriptCa
        String _elanTranscriptCa = artifactsType.getElanTranscriptCa();
        ArtifactFile elanTranscriptCaFile;
        if (StringUtils.isNotEmpty(_elanTranscriptCa)) {
            logger.info("elanTranscriptCaFile [" + _elanTranscriptCa + "]");
            elanTranscriptCaFile = new ArtifactFile();
            elanTranscriptCaFile.setFileName(_elanTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.ELAN_TRANSCRIPT_CA.value());
            elanTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_elanTranscriptCa));
            }
            artifact.getArtifactFiles().add(elanTranscriptCaFile);
        }
        // TraditionalTranscriptCa
        String _traditionalTranscriptCa = artifactsType.getTraditionalTranscriptCa();
        ArtifactFile traditionalTranscriptCaFile;
        if (StringUtils.isNotEmpty(_traditionalTranscriptCa)) {
            logger.info("traditionalTranscriptCaFile [" + _traditionalTranscriptCa + "]");
            traditionalTranscriptCaFile = new ArtifactFile();
            traditionalTranscriptCaFile.setFileName(_traditionalTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TRADITIONAL_TRANSCRIPT_CA.value());
            traditionalTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_traditionalTranscriptCa));
            }
            artifact.getArtifactFiles().add(traditionalTranscriptCaFile);
        }
        // TabDelimitedTranscriptCa
        String _tabDelimitedTranscriptCa = artifactsType.getTabDelimitedTranscriptCa();
        ArtifactFile tabDelimitedTranscriptCaFile;
        if (StringUtils.isNotEmpty(_tabDelimitedTranscriptCa)) {
            logger.info("tabDelimitedTranscriptCaFile [" + _tabDelimitedTranscriptCa + "]");
            tabDelimitedTranscriptCaFile = new ArtifactFile();
            tabDelimitedTranscriptCaFile.setFileName(_tabDelimitedTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.TAB_DELIMITED_TRANSCRIPT_CA.value());
            tabDelimitedTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_tabDelimitedTranscriptCa));
            }
            artifact.getArtifactFiles().add(tabDelimitedTranscriptCaFile);
        }
        // OtherTranscriptCa
        String _otherTranscriptCa = artifactsType.getOtherTranscriptCa();
        ArtifactFile otherTranscriptCaFile;
        if (StringUtils.isNotEmpty(_otherTranscriptCa)) {
            logger.info("otherTranscriptCaFile [" + _otherTranscriptCa + "]");
            otherTranscriptCaFile = new ArtifactFile();
            otherTranscriptCaFile.setFileName(_otherTranscriptCa);
            // FileType
            FileType fileType = new FileType();
            fileType.setValue(FormatType.OTHER_TRANSCRIPT_CA.value());
            otherTranscriptCaFile.setFileType(fileType);
            if (artifact == null) {
                artifact = new Artifact();
                artifact.setArtifactName(getNameFromFileName(_otherTranscriptCa));
            }
            artifact.getArtifactFiles().add(otherTranscriptCaFile);
        }

        if (artifact != null) {
            // TODO
            // Populate interview entity or use artifact entity
            // Interview Comments,
            // Videoed Encounter comments,
            // Encounter Metadata comments
            /*
            Payload: {"artifactsType":{"interview-transcript":
            ["encounter.json","SFPD_AnythingElse_UK_05212012-tab.txt"],
            "data-descriptor":"cta","corpus":"Corpus",
            "collection-date":"01/29/2013","collection-time":"11:05:02",
            "geographic-location":"Location","site-location":"Site",
            "collector":"Collector","comments":"Add an Interview Comments"
             */
        }

        // Interview
        Interview interview = null;
        StimulatedRecallInterviewsType stimulatedRecallInterviews =
                            artifactsType.getStimulatedRecallInterviews();
        if (stimulatedRecallInterviews != null) {
            String file = stimulatedRecallInterviews.getStimulatedRecallTranscript();
            if (StringUtils.isNotEmpty(file)) {
                interview = new Interview();
                interview.setTranscript(file);
                // description
                String description = stimulatedRecallInterviews.getStimulatedRecallDescription();
                if (StringUtils.isNotEmpty(description)) {
                    interview.setDescription(description);
                }
            }
        }

        for (EncountersType encountersType : artifactsType.getEncounters()) {

            // Encounter
            Encounter encounter = null;
            // EncounterName
            String encounterName = encountersType.getEncounterName();
            if (StringUtils.isNotEmpty(encounterName)) {
                encounter = new Encounter();
                encounter.setName(encounterName);
            } else {
                logger.warn("Unable to update Encounter using name [" +
                    encounterName + "]");
            }

            if (encounter != null) {
                // Corpus
                String corpus = artifactsType.getCorpus();
                if (StringUtils.isNotEmpty(corpus)) {
                    Corpus aCorpus = new Corpus();
                    aCorpus.setValue(corpus);
                    encounter.setCorpus(aCorpus);
                }
                // Collection Date and Time
                String _collectionDate = artifactsType.getCollectionDate();
                String collectionTime = artifactsType.getCollectionTime();
                if (StringUtils.isNotEmpty(_collectionDate)) {
                    Date collectionDate;
                    if (StringUtils.isNotEmpty(collectionTime)) {
                        collectionDate = getDateAndTime(_collectionDate, collectionTime);
                    } else {
                        collectionDate = getDate(_collectionDate);
                    }
                    if (collectionDate != null) {
                        encounter.setCollectionDate(collectionDate);
                    } else {
                        logger.warn("Unable to parse Date [" + _collectionDate +
                                "] and/or Time [" + collectionTime + "]");
                    }
                }
                // DaylightConditions
                GeneralTime generalTime;
                String generalCollectionTime = artifactsType.getGeneralCollectionTime();
                if (StringUtils.isNotEmpty(generalCollectionTime)) {
                    generalTime = new GeneralTime();
                    generalTime.setValue(generalCollectionTime);
                    encounter.setGeneralTime(generalTime);
                }
                // GeographicLocation
                GeographicLocation geographicLocation;
                String _geographicLocation = artifactsType.getGeographicLocation();
                if (StringUtils.isNotEmpty(_geographicLocation)) {
                    geographicLocation = new GeographicLocation();
                    geographicLocation.setValue(_geographicLocation);
                    encounter.setGeographicLocation(geographicLocation);
                }
                // Site
                Site site;
                String siteLocation = artifactsType.getSiteLocation();
                if (StringUtils.isNotEmpty(siteLocation)) {
                    site = new Site();
                    site.setValue(siteLocation);
                    encounter.setSite(site);
                }
                // Audio Quality
                AudioQuality audioQuality;
                String _audioQuality = artifactsType.getAudioQuality();
                if (StringUtils.isNotEmpty(_audioQuality)) {
                    logger.info("AudioQuality [" + _audioQuality + "]");
                    audioQuality = new AudioQuality();
                    audioQuality.setValue(_audioQuality);
                    encounter.setAudioQuality(audioQuality);
                }
                // Video Quality
                VideoQuality videoQuality;
                String _videoQuality = artifactsType.getVideoQuality();
                if (StringUtils.isNotEmpty(_videoQuality)) {
                    logger.info("VideoQuality [" + _videoQuality + "]");
                    videoQuality = new VideoQuality();
                    videoQuality.setValue(_videoQuality);
                    encounter.setVideoQuality(videoQuality);
                }
                // Collector
                Collector collector;
                String _collector = artifactsType.getCollector();
                if (StringUtils.isNotEmpty(_collector)) {
                    logger.info("collector [" + _collector + "]");
                    collector = new Collector();
                    collector.setValue(_collector);
                    encounter.setCollector(collector);
                }
                // Comments
                String comments = artifactsType.getComments();
                if (StringUtils.isNotEmpty(comments)) {
                    encounter.setComments(comments);
                }
                // TODO: Stimulated Recall Interview??? or Artifact
                // Description
                String dataDescriptor = artifactsType.getDataDescriptor();
                if (StringUtils.isNotEmpty(dataDescriptor)) {
                    encounter.setDescription(dataDescriptor);
                }

                // ** EncountersType ** //

                // EncounterComments
                String encounterComments = encountersType.getEncounterComments();
                if (StringUtils.isNotEmpty(encounterComments)) {
                    encounter.setEncounterComments(encounterComments);
                }
                // Domain
                Domain domain;
                String encounterDomain = encountersType.getEncounterDomain();
                if (StringUtils.isNotEmpty(encounterDomain)) {
                    domain = new Domain();
                    domain.setValue(encounterDomain);
                    encounter.setDomain(domain);
                }
                // NumParticipants
                Integer numParticipants;
                String encounterNumParticipants =
                        encountersType.getEncounterNumParticipants();
                if (StringUtils.isNotEmpty(encounterNumParticipants) &&
                        StringUtils.isNumeric(encounterNumParticipants)) {
                    numParticipants = new Integer(encounterNumParticipants);
                    encounter.setNumberOfParticipants(numParticipants);
                }
                // Bystanders
                Boolean bystanders = null;
                String encounterBystanders = encountersType.getEncounterBystanders();
                if (StringUtils.isNotEmpty(encounterBystanders)) {
                    if (encounterBystanders.equalsIgnoreCase("yes")) {
                        bystanders = Boolean.TRUE;
                    } else if (encounterBystanders.equalsIgnoreCase("no")) {
                        bystanders = Boolean.FALSE;
                    } else {
                        logger.error("Unrecognized EncounterBystanders value [" +
                                encounterBystanders + "]");
                    }
                    if (bystanders != null) {
                        encounter.setBystanders(bystanders);
                    }
                }
                // EncounterReason
                EncounterReason encounterReason;
                String _encounterReason = encountersType.getEncounterReason();
                if (StringUtils.isNotEmpty(_encounterReason)) {
                    encounterReason = new EncounterReason();
                    encounterReason.setValue(_encounterReason);
                    encounter.setEncounterReason(encounterReason);
                }
                // Duration
                Integer duration;
                String _encounterDuration = encountersType.getEncounterDuration();
                if (StringUtils.isNotEmpty(_encounterDuration) &&
                        StringUtils.isNumeric(_encounterDuration)) {
                    try {
                        duration = new Integer(_encounterDuration);
                        encounter.setDuration(duration);
                    } catch (Exception e) {
                        logger.error("Error marshaling duration value [" +
                            _encounterDuration + "]");
                    }
                }
                // EncounterQuality
                EncounterQuality encounterQuality;
                String _encounterQuality = encountersType.getEncounterQuality();
                if (StringUtils.isNotEmpty(_encounterQuality)) {
                    encounterQuality = new EncounterQuality();
                    encounterQuality.setValue(_encounterQuality);
                    encounter.setEncounterQuality(encounterQuality);
                }
                // ParticipantFirstEncounter
                Boolean participantFirstEncounter = null;
                String _encounterFirstEncounter =
                        encountersType.getEncounterFirstEncounter();
                if (StringUtils.isNotEmpty(_encounterFirstEncounter)) {
                    if (_encounterFirstEncounter.equalsIgnoreCase("yes")) {
                        participantFirstEncounter = Boolean.TRUE;
                    } else if (_encounterFirstEncounter.equalsIgnoreCase("no")) {
                        participantFirstEncounter = Boolean.FALSE;
                    } else {
                        logger.error("Unrecognized EncounterFirstEncounter value [" +
                                _encounterFirstEncounter + "]");
                    }
                    if (participantFirstEncounter != null) {
                        encounter.setParticipantFirstEncounter(
                                participantFirstEncounter);
                    }
                }
                // ParticipantFamiliarity
                Boolean participantFamiliarity = null;
                String _encounterFamiliar = encountersType.getEncounterFamiliar();
                if (StringUtils.isNotEmpty(_encounterFamiliar)) {
                    if (_encounterFamiliar.equalsIgnoreCase("yes")) {
                        participantFamiliarity = Boolean.TRUE;
                    } else if (_encounterFamiliar.equalsIgnoreCase("no")) {
                        participantFamiliarity = Boolean.FALSE;
                    } else {
                        logger.error("Unrecognized EncounterFamiliar value [" +
                                _encounterFamiliar + "]");
                    }
                    if (participantFamiliarity != null) {
                        encounter.setParticipantFamiliarity(participantFamiliarity);
                    }
                }
                // Languages
                Set<Language> languages = new TreeSet<Language>();
                for (String _language : encountersType.getEncounterLanguages()) {
                    Language language = new Language();
                    language.setValue(_language);
                    languages.add(language);
                }
                if (!languages.isEmpty()) {
                    encounter.setLanguages(languages);
                }
                // Participants
                List<Participant> participants = new ArrayList<Participant>();
                for (ParticipantsType participantsType : encountersType.getParticipants()) {
                    Participant participant = new Participant();
                    // Age
                    String _age = participantsType.getAge();
                    if (StringUtils.isNotEmpty(_age)) {
                        Age age = new Age();
                        age.setValue(_age);
                        participant.setAge(age);
                    }
                    // Codename
                    String _codename = participantsType.getCodename();
                    if (StringUtils.isNotEmpty(_codename)) {
                        participant.setCodeName(_codename);
                    }
                    // Ethnicity
                    String _ethnicity = participantsType.getEthnicity();
                    if (StringUtils.isNotEmpty(_ethnicity)) {
                        Ethnicity ethnicity = new Ethnicity();
                        ethnicity.setValue(_ethnicity);
                        participant.setEthnicity(ethnicity);
                    }
                    // Gender
                    String _gender = participantsType.getGender();
                    if (StringUtils.isNotEmpty(_gender)) {
                        Gender gender = new Gender();
                        gender.setValue(_gender);
                        participant.setGender(gender);
                    }
                    // GeographicOrigin
                    String _origin = participantsType.getOrigin();
                    if (StringUtils.isNotEmpty(_origin)) {
                        GeographicLocation geoLocation = new GeographicLocation();
                        geoLocation.setValue(_origin);
                        participant.setGeographicLocation(geoLocation);
                    }
                    // ParticipantComments
                    String _participantComments = participantsType.getParticipantComments();
                    if (StringUtils.isNotEmpty(_participantComments)) {
                        participant.setComments(_participantComments);
                    }
                    // Role
                    String _role = participantsType.getRole();
                    if (StringUtils.isNotEmpty(_participantComments)) {
                        Role role = new Role();
                        role.setValue(_role);
                        role.setValue(_role);
                    }
                    // ParticipantsType
                    String _type = participantsType.getType();
                    if (StringUtils.isNotEmpty(_type)) {
                        ParticipantType participantType = new ParticipantType();
                        participantType.setValue(_type);
                        participant.setParticipantType(participantType);
                    }
                    // TimeInCountry
                    String timeInCountry = participantsType.getTimeInCountry();
                    if (StringUtils.isNotEmpty(timeInCountry) &&
                        StringUtils.isNumeric(timeInCountry)) {
                        try {
                            int _timeInCountry = new Integer(timeInCountry);
                            participant.setTimeInCountry(_timeInCountry);
                        } catch (Exception e) {
                            logger.error("Error marshaling timeInCountry value [" +
                                timeInCountry + "]");
                        }
                    }
                    // Sociometric Badge
                    String sociometricBadge = participantsType.getSociometricBadge();
                    if (StringUtils.isNotEmpty(sociometricBadge)) {
                        participant.setSociometricBadge(sociometricBadge);
                    }
                    // Languages
                    for (String participantLanguage :
                            participantsType.getParticipantLanguages()) {
                        Language language = new Language();
                        language.setValue(participantLanguage);
                        participant.getLanguages().add(language);
                    }
                    // Add participant to participants collection
                    participants.add(participant);
                }

                // Add participant to encounter
                if (!participants.isEmpty()) {
                     encounter.setParticipants(participants);
                }

                // Relate artifact to encounter
                if (artifact != null) {
                    encounter.getArtifacts().add(artifact);
                }

                // Relate interview to encounter
                if (interview != null) {
                    encounter.getInterviews().add(interview);
                }

                encounterModel.updateEncounter(encounter);

            } // End encounter
        }

        if (artifactsType.getEncounters().isEmpty() && artifact != null ) {
            encounterModel.updateArtifact(artifact);
        }

        return Response.status(Response.Status.OK).build();
    }

    @Nullable
    private Date getDate(@NotNull String dateString) {
        // Fri Apr 01 00:00:00 PDT 2011
        //DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("MM/dd/yy");
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            date = null;
            logger.error("Exception parsing Date String [" + dateString + "]");
        }
        return date;
    }

    @Nullable
    private Date getDateAndTime(@NotNull String dateString, @NotNull String timeString) {
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("MM/dd/yy hh:mm:ss");
        Date date;
        try {
            date = dateFormat.parse(dateString + " " + timeString);
        } catch (ParseException e) {
            date = null;
            logger.error("Exception parsing DateTime String [" + dateString + " " + timeString + "]");
        }
        return date;
    }

    private boolean isBoolean(String value) {
        boolean isBoolean = false;
        if (StringUtils.isNotEmpty(value)) {
            if (value.equalsIgnoreCase("TRUE") ||
                    value.equalsIgnoreCase("FALSE")) {
                isBoolean = true;
            }
        }
        return isBoolean;
    }

    private String getEntityNameFromFile(Map<String, String> fileNames) {
        String entityName = null;
        for (String filename : fileNames.values()) {
            if (StringUtils.isNotEmpty(filename)) {
                entityName = getEntityName(filename);
                break;
            }
        }
        return entityName;
    }

    private String getEntityName(@NotNull String filename) {
        String entityName = filename;
        String[] names = filename.split("\\.");
        if (names.length > 1) {
            entityName = names[0];
        }
        return entityName;
    }

    @Override
    public Response createUser(MultivaluedMap<String, String> map) {
        String username = map.getFirst("username");
        String password = map.getFirst("password");
        String role = map.getFirst("role");

        User user = userManagement.createUser(username, password, role);
        if (user == null) {
            throw new RuntimeException(
                    "There was an unexpected error creating user [" +
                        username + "]");
        }

        // login
        String token = userManagement.login(username, password);

        ResponseBuilderImpl builder = new ResponseBuilderImpl();
        // Add session cookie to response
        builder.cookie(createCookie(token));

        return builder.build();
    }

    @Override
    public Response encFileRequest(String[] fileNames, String[] formats) {
        FileRequest fileRequest = new FileRequest();

        // Filename
        for (String fileName : fileNames) {
            logger.info("FileName [" + fileName + "]");
            fileRequest.getFile().add(fileName);
        }

        // Formats
        for (String format : formats) {
            logger.info("Format [" + format + "]");
            FormatType formatType = FormatType.fromValue(format);
            fileRequest.getFormat().add(formatType);
        }

        // Zip filename
        String tmpFileName = String.valueOf(System.currentTimeMillis());
        if (tmpFileName.length() > 10) {
            //tmpFileName = tmpFileName.substring(0, 9);
            tmpFileName = tmpFileName.substring(
                    tmpFileName.length() - 9, tmpFileName.length() - 1);
        }
        tmpFileName = tmpFileName + ".zip";

        File zipFile =
                encounterModel.getFileForDownload(fileRequest, tmpFileName);

        Response response;
        if (zipFile != null) {
            response = Response.ok(zipFile, MediaType.APPLICATION_OCTET_STREAM)
               .header("content-disposition", "attachment; filename=" + tmpFileName)
                .cookie(createCookie("fileDownload=true")).build();
        } else {
            response = Response.status(Response.Status.NO_CONTENT).build();
        }

        return response;
    }

    @Override
    public Response fileRequest(FileRequest fileRequest) {
        // Zip filename
        String tmpFileName = System.currentTimeMillis() + "";
        if (tmpFileName.length() > 10) {
            tmpFileName = tmpFileName.substring(0, 9);
        }
        tmpFileName = tmpFileName + ".zip";

        File zipFile =
                encounterModel.getFileForDownload(fileRequest, tmpFileName);

        return Response.ok(zipFile, MediaType.APPLICATION_OCTET_STREAM)
               .header("content-disposition", "attachment; filename=" + tmpFileName)
                .cookie(createCookie("fileDownload=true")).build();
    }

    @Override
    public List<EncounterFile> getFileNames(String lookup, int limit) {
        List<EncounterFile> encounterFiles = new ArrayList<EncounterFile>();

        for (ArtifactFile artifactFile :
                encounterModel.getArtifactFilesLike(lookup, limit)) {

            EncounterFile encounterFile = new EncounterFile();
            encounterFile.setFileName(artifactFile.getFileName());

            // Is there any related files
            for (ArtifactFile relatedArtifactFile :
                    artifactFile.getArtifact().getRelatedFiles()) {

                encounterFile.getRelatedFile().add(
                        relatedArtifactFile.getFileName());
            }

            encounterFiles.add(encounterFile);
        }

        return encounterFiles;
    }

    @Override
    public List<Collector> getCollectors() {
        return encounterModel.getCollectors();
    }

    @Override
    public List<Collector> getCollectors(String root) {
        logger.info("getCollectors using value [" + root + "]");
        return encounterModel.getCollectors(root);
    }

    @Override
    public Collector addCollector(@NotNull Collector collectorEntity) {
        logger.info("addCollector [" + collectorEntity.getValue() + "]");
        return encounterModel.createCollector(collectorEntity);
    }

    @Override
    public List<EncounterQuality> getEncounterQualities() {
        return encounterModel.getEncounterQualities();
    }

    @Override
    public List<EncounterQuality> getEncounterQualities(String root) {
        return encounterModel.getEncounterQualities(root);
    }

    @Override
    public EncounterQuality addEncounterQuality(
            EncounterQuality encounterQualityEntity) {
        return encounterModel.createEncounterQuality(encounterQualityEntity);
    }

    @Override
    public List<EncounterReason> getEncounterReasons() {
        return encounterModel.getEncounterReasons();
    }

    @Override
    public List<EncounterReason> getEncounterReasons(String root) {
        return encounterModel.getEncounterReasons(root);
    }

    @Override
    public EncounterReason addEncounterReason(
            EncounterReason encounterReason) {
        return encounterModel.createEncounterReason(encounterReason);
    }

    @Override
    public List<Ethnicity> getEthnicities() {
        return encounterModel.getEthnicities();
    }

    @Override
    public List<Ethnicity> getEthnicities(String root) {
        return encounterModel.getEthnicities(root);
    }

    @Override
    public Ethnicity addEthnicityEntity(Ethnicity ethnicity) {
        return encounterModel.createEthnicity(ethnicity);
    }

    @Override
    public List<GeneralTime> getGeneralTimes() {
        return encounterModel.getGeneralTimes();
    }

    @Override
    public List<GeneralTime> getGeneralTimes(String root) {
        return encounterModel.getGeneralTimes(root);
    }

    @Override
    public GeneralTime addGeneralTime(GeneralTime generalTime) {
        return encounterModel.createGeneralTime(generalTime);
    }

    @Override
    public List<GeographicLocation> getGeoLocations() {
        return encounterModel.getGeographicOrigins();
    }

    @Override
    public List<GeographicLocation> getGeoLocations(String root) {
        return encounterModel.getGeographicOrigins(root);
    }

    @Override
    public Interview getInterview(String name) {
        return encounterModel.getInterview(name);
    }

    @Override
    public GeographicLocation addGeoLocation(
            GeographicLocation geographicLocation) {
        return encounterModel.createGeographicOrigin(geographicLocation);
    }

    @Override
    public List<Language> getLanguages() {
        return encounterModel.getLanguages();
    }

    @Override
    public List<Language> getLanguages(String root) {
        return encounterModel.getLanguages(root);
    }

    @Override
    public Language addLanguage(Language language) {
        return encounterModel.createLanguage(language);
    }

    @Override
    public List<Rank> getRanks() {
        return encounterModel.getRanks();
    }

    @Override
    public List<Rank> getRanks(String root) {
        return encounterModel.getRanks(root);
    }

    @Override
    public Rank addRank(Rank rankEntity) {
        return encounterModel.createRank(rankEntity);
    }

    @Override
    public List<Role> getRoles() {
        return encounterModel.getRoles();
    }

    @Override
    public List<Role> getRoles(String root) {
        return encounterModel.getRoles(root);
    }

    @Override
    public Role addRole(Role role) {
        return encounterModel.createRole(role);
    }

    @Override
    public List<Site> getSites() {
        return encounterModel.getSites();
    }

    @Override
    public List<Site> getSites(String root) {
        return encounterModel.getSites(root);
    }

    @Override
    public Site addSite(Site site) {
        return encounterModel.createSite(site);
    }

    @Override
    public List<Corpus> getCorpus() {
        return encounterModel.getCorpus();
    }

    @Override
    public List<Corpus> getCorpus(String root) {
        return encounterModel.getCorpus(root);
    }

    @Override
    public ArtifactsType getEncounter(String encounterName) {
        // Retrieve encounter from DB
        Encounter encounter = encounterModel.getEncounter(encounterName);

        ArtifactsType artifactsType = new ArtifactsType();

        if (encounter != null) {
            // Date-Time
            Date collectionDate = encounter.getCollectionDate();
            if (collectionDate != null) {
                // Date
                DateFormat dateFormat =
                    new SimpleDateFormat("MM/dd/yyyy");
                String stringDate = dateFormat.format(collectionDate);
                artifactsType.setCollectionDate(stringDate);
                // Time
                dateFormat =
                    new SimpleDateFormat("hh:mm:ss");
                String stringTime = dateFormat.format(collectionDate);
                artifactsType.setCollectionTime(stringTime);
            }
            // Collector
            Collector collector = encounter.getCollector();
            if (collector != null) {
                artifactsType.setCollector(collector.getValue());
            }
            // Comments
            // TODO: many comments
            artifactsType.setComments(encounter.getComments());
            // Corpus
            Corpus corpus = encounter.getCorpus();
            if (corpus != null) {
                artifactsType.setCorpus(corpus.getValue());
            }
            // Site
            Site site = encounter.getSite();
            if (site != null) {
                artifactsType.setSiteLocation(site.getValue());
            }
            // GeographicLocation
            GeographicLocation geographicLocation = encounter.getGeographicLocation();
            if (geographicLocation != null) {
                artifactsType.setGeographicLocation(geographicLocation.getValue());
            }
            // VideoQuality
            VideoQuality videoQuality = encounter.getVideoQuality();
            if (videoQuality != null) {
                artifactsType.setVideoAudio(videoQuality.getValue());
            }
            // AudioQuality
            AudioQuality audioQuality = encounter.getAudioQuality();
            if (audioQuality != null) {
                artifactsType.setAudioQuality(audioQuality.getValue());
            }

            /** EncountersType **/

            EncountersType encountersType = new EncountersType();
            // EncounterComments
            // TODO: many comments
            encountersType.setEncounterComments(encounter.getEncounterComments());
            // Bystanders
            Boolean bystanders = encounter.isBystanders();
            if (bystanders != null) {
                encountersType.setEncounterBystanders(String.valueOf(bystanders));
            }
            // TODO: many comments
            encountersType.setEncounterComments(encounter.getComments());
            // Domain
            Domain domain = encounter.getDomain();
            if (domain != null) {
                encountersType.setEncounterDomain(domain.getValue());
            }
            // Duration
            Integer duration = encounter.getDuration();
            if (duration != null) {
                encountersType.setEncounterDuration(String.valueOf(duration));
            }
            // ParticipantFamiliarity
            Boolean familiarity = encounter.isParticipantFamiliarity();
            if (familiarity != null) {
                encountersType.setEncounterFamiliar(String.valueOf(familiarity));
            }
            // FirstEncounter
            Boolean firstEncounter = encounter.isParticipantFirstEncounter();
            if (firstEncounter != null) {
                encountersType.setEncounterFirstEncounter(
                        String.valueOf(firstEncounter));
            }
            // EncounterName
            encountersType.setEncounterName(encounter.getName());
            // NumParticipants
            Integer numParticipants = encounter.getNumberOfParticipants();
            if (numParticipants != null) {
                encountersType.setEncounterNumParticipants(
                        String.valueOf(numParticipants));
            }
            // EncounterQuality
            EncounterQuality encounterQuality = encounter.getEncounterQuality();
            if (encounterQuality != null) {
                encountersType.setEncounterQuality(encounterQuality.getValue());
            }
            // EncounterReason
            EncounterReason encounterReason = encounter.getEncounterReason();
            if (encounterReason != null) {
                encountersType.setEncounterReason(encounterReason.getValue());
            }

            // Languages
            for (Language language : encounter.getLanguages()) {
                encountersType.getEncounterLanguages().add(language.getValue());
            }

            // Interviews???

            // Participants
            for (Participant participant :
                    encounterModel.getParticipantsByEncounterId(encounter.getId())) {
                ParticipantsType participantsType = new ParticipantsType();
                // Age
                Age age = participant.getAge();
                if (age != null) {
                    participantsType.setAge(age.getValue());
                }
                // CodeName
                participantsType.setCodename(participant.getCodeName());
                // Ethnicity
                Ethnicity ethnicity = participant.getEthnicity();
                if (ethnicity != null) {
                    participantsType.setEthnicity(ethnicity.getValue());
                }
                // Gender
                Gender gender = participant.getGender();
                if (gender != null) {
                    participantsType.setGender(gender.getValue());
                }
                // Origin
                GeographicLocation geoLocation =
                        participant.getGeographicLocation();
                if (geoLocation != null) {
                    participantsType.setOrigin(geoLocation.getValue());
                }
                // ParticipantComments
                participantsType.setParticipantComments(participant.getComments());
                // ParticipantLanguages
                for (Language language : participant.getLanguages()) {
                    participantsType.getParticipantLanguages().add(language.getValue());
                }
                // Role
                Role role = participant.getRole();
                if (role != null) {
                    participantsType.setRole(role.getValue());
                }
                // Rank
                // ToDo: Not yet implemented
//                Rank rank = participant.getRank();
//                if (rank != null) {
//                    participantsType.setRank(rank.getValue());
//                }
                // TimeInCountry
                int timeInCountry = participant.getTimeInCountry();
                if (timeInCountry > 0) {
                    participantsType.setTimeInCountry(
                            String.valueOf(timeInCountry)
                    );
                }
                // SociometricBadge
                participantsType.setSociometricBadge(participant.getSociometricBadge());
                // Type
                ParticipantType participantType = participant.getParticipantType();
                if (participantType != null) {
                    participantsType.setType(participantType.getValue());
                }
                // Add participantsType to Participants collection
                encountersType.getParticipants().add(participantsType);
            }

            // Add EncountersType to ArtifactsType
            artifactsType.getEncounters().add(encountersType);

            // Artifacts
            Set<Artifact> artifacts = encounter.getArtifacts();
            if (!artifacts.isEmpty()) {
                Artifact artifact = artifacts.iterator().next();
                for (ArtifactFile artifactFile : artifact.getArtifactFiles()) {
                    String fileType = artifactFile.getFileType() != null ?
                            artifactFile.getFileType().getValue() : "";
                    String filename = artifactFile.getFileName();
                    if (fileType.equals("original-video")) {
                        artifactsType.setOriginalVideo(filename);
                    } else if (fileType.equals("converted-video")) {
                        artifactsType.setConvertedVideo(filename);
                    } else if (fileType.equals("video-audio")) {
                        artifactsType.setVideoAudio(filename);
                    } else if (fileType.equals("alternate-audio")) {
                        artifactsType.setAlternateAudio(filename);
                    } else if (fileType.equals("stimulated-recall-transcript")) {
                        // TODO: stimulated-recall-description
                        StimulatedRecallInterviewsType stimulatedRecallInterviewsType =
                                new StimulatedRecallInterviewsType();
                        stimulatedRecallInterviewsType.setStimulatedRecallTranscript(filename);
                        artifactsType.setStimulatedRecallInterviews(stimulatedRecallInterviewsType);
                    } else if (fileType.equals("sociometric-badge")) {
                        logger.error("Unable to set SOCIOMETRIC_BADGE [" + fileType + "]");
                    } else if (fileType.equals("elan-transcript-orthographic")) {
                        artifactsType.setElanTranscriptOrthographic(filename);
                    } else if (fileType.equals("traditional-transcript-orthographic")) {
                        artifactsType.setElanTranscriptOrthographic(filename);
                    } else if (fileType.equals("tab-delimited-transcript-orthographic")) {
                        artifactsType.setTabDelimitedTranscriptOrthographic(filename);
                    } else if (fileType.equals("elan-transcript-ca")) {
                        artifactsType.setElanTranscriptCa(filename);
                    } else if (fileType.equals("traditional-transcript-ca")) {
                        artifactsType.setTraditionalTranscriptCa(filename);
                    } else if (fileType.equals("tab-delimited-transcript-ca")) {
                        artifactsType.setTabDelimitedTranscriptCa(filename);
                    } else if (fileType.equals("ethnographic")) {
                        logger.error("Unable to set ETHNOGRAPHIC [" + fileType + "]");
                    } else if (fileType.equals("cta")) {
                        logger.error("Unable to set COGNITIVE_TASK_ANALYSIS [" + fileType + "]");
                    } else if (fileType.equals("other-transcript-ca")) {
                        artifactsType.setOtherTranscriptCa(filename);
                    } else {
                        logger.warn("Unrecognized fileType [" + fileType + "]");
                    }
                }
            }
        } else {
            logger.info("Unable to find Encounter by name [" + encounterName + "]");
        }

        return artifactsType;
    }

    @Override
    public ArtifactsType getEncounters(String artifactFilename) {
        // Retrieve encounter from DB
        List<Encounter> encounters = encounterModel.getEncounters(artifactFilename);

        ArtifactsType artifactsType = new ArtifactsType();

        if (!encounters.isEmpty()) {
            for (Encounter encounter : encounters) {
                // Date-Time
                Date collectionDate = encounter.getCollectionDate();
                if (collectionDate != null) {
                    // Date
                    DateFormat dateFormat =
                        new SimpleDateFormat("MM/dd/yyyy");
                    String stringDate = dateFormat.format(collectionDate);
                    artifactsType.setCollectionDate(stringDate);
                    // Time
                    dateFormat =
                        new SimpleDateFormat("hh:mm:ss");
                    String stringTime = dateFormat.format(collectionDate);
                    artifactsType.setCollectionTime(stringTime);
                }
                // Collector
                Collector collector = encounter.getCollector();
                if (collector != null) {
                    artifactsType.setCollector(collector.getValue());
                }
                // Comments
                // TODO: many comments
                artifactsType.setComments(encounter.getComments());
                // Corpus
                Corpus corpus = encounter.getCorpus();
                if (corpus != null) {
                    artifactsType.setCorpus(corpus.getValue());
                }
                // Site
                Site site = encounter.getSite();
                if (site != null) {
                    artifactsType.setSiteLocation(site.getValue());
                }
                // GeographicLocation
                GeographicLocation geographicLocation = encounter.getGeographicLocation();
                if (geographicLocation != null) {
                    artifactsType.setGeographicLocation(geographicLocation.getValue());
                }
                // VideoQuality
                VideoQuality videoQuality = encounter.getVideoQuality();
                if (videoQuality != null) {
                    artifactsType.setVideoAudio(videoQuality.getValue());
                }
                // AudioQuality
                AudioQuality audioQuality = encounter.getAudioQuality();
                if (audioQuality != null) {
                    artifactsType.setAudioQuality(audioQuality.getValue());
                }

                /** EncountersType **/

                EncountersType encountersType = new EncountersType();
                // EncounterComments
                // TODO: many comments
                encountersType.setEncounterComments(encounter.getEncounterComments());
                // Bystanders
                Boolean bystanders = encounter.isBystanders();
                if (bystanders != null) {
                    encountersType.setEncounterBystanders(String.valueOf(bystanders));
                }
                // TODO: many comments
                encountersType.setEncounterComments(encounter.getComments());
                // Domain
                Domain domain = encounter.getDomain();
                if (domain != null) {
                    encountersType.setEncounterDomain(domain.getValue());
                }
                // Duration
                Integer duration = encounter.getDuration();
                if (duration != null) {
                    encountersType.setEncounterDuration(String.valueOf(duration));
                }
                // ParticipantFamiliarity
                Boolean familiarity = encounter.isParticipantFamiliarity();
                if (familiarity != null) {
                    encountersType.setEncounterFamiliar(String.valueOf(familiarity));
                }
                // FirstEncounter
                Boolean firstEncounter = encounter.isParticipantFirstEncounter();
                if (firstEncounter != null) {
                    encountersType.setEncounterFirstEncounter(
                            String.valueOf(firstEncounter));
                }
                // EncounterName
                encountersType.setEncounterName(encounter.getName());
                // NumParticipants
                Integer numParticipants = encounter.getNumberOfParticipants();
                if (numParticipants != null) {
                    encountersType.setEncounterNumParticipants(
                            String.valueOf(numParticipants));
                }
                // EncounterQuality
                EncounterQuality encounterQuality = encounter.getEncounterQuality();
                if (encounterQuality != null) {
                    encountersType.setEncounterQuality(encounterQuality.getValue());
                }
                // EncounterReason
                EncounterReason encounterReason = encounter.getEncounterReason();
                if (encounterReason != null) {
                    encountersType.setEncounterReason(encounterReason.getValue());
                }

                // Languages
                for (Language language : encounter.getLanguages()) {
                    encountersType.getEncounterLanguages().add(language.getValue());
                }

                // Interviews???

                // Participants
                for (Participant participant :
                        encounterModel.getParticipantsByEncounterId(encounter.getId())) {
                    ParticipantsType participantsType = new ParticipantsType();
                    // Age
                    Age age = participant.getAge();
                    if (age != null) {
                        participantsType.setAge(age.getValue());
                    }
                    // CodeName
                    participantsType.setCodename(participant.getCodeName());
                    // Ethnicity
                    Ethnicity ethnicity = participant.getEthnicity();
                    if (ethnicity != null) {
                        participantsType.setEthnicity(ethnicity.getValue());
                    }
                    // Gender
                    Gender gender = participant.getGender();
                    if (gender != null) {
                        participantsType.setGender(gender.getValue());
                    }
                    // Origin
                    GeographicLocation geoLocation =
                            participant.getGeographicLocation();
                    if (geoLocation != null) {
                        participantsType.setOrigin(geoLocation.getValue());
                    }
                    // ParticipantComments
                    participantsType.setParticipantComments(participant.getComments());
                    // ParticipantLanguages
                    for (Language language : participant.getLanguages()) {
                        participantsType.getParticipantLanguages().add(language.getValue());
                    }
                    // Role
                    Role role = participant.getRole();
                    if (role != null) {
                        participantsType.setRole(role.getValue());
                    }
                    // Rank
                    // ToDo: Not yet implemented
    //                Rank rank = participant.getRank();
    //                if (rank != null) {
    //                    participantsType.setRank(rank.getValue());
    //                }
                    // TimeInCountry
                    int timeInCountry = participant.getTimeInCountry();
                    if (timeInCountry > 0) {
                        participantsType.setTimeInCountry(
                                String.valueOf(timeInCountry)
                        );
                    }
                    // SociometricBadge
                    participantsType.setSociometricBadge(participant.getSociometricBadge());
                    // Type
                    ParticipantType participantType = participant.getParticipantType();
                    if (participantType != null) {
                        participantsType.setType(participantType.getValue());
                    }
                    // Add participantsType to Participants collection
                    encountersType.getParticipants().add(participantsType);
                }

                // Add EncountersType to ArtifactsType
                artifactsType.getEncounters().add(encountersType);

                // Artifacts
                Set<Artifact> artifacts = encounter.getArtifacts();
                if (!artifacts.isEmpty()) {
                    Artifact artifact = artifacts.iterator().next();
                    for (ArtifactFile artifactFile : artifact.getArtifactFiles()) {
                        String fileType = artifactFile.getFileType() != null ?
                                artifactFile.getFileType().getValue() : "";
                        String filename = artifactFile.getFileName();
                        if (fileType.equals("original-video")) {
                            artifactsType.setOriginalVideo(filename);
                        } else if (fileType.equals("converted-video")) {
                            artifactsType.setConvertedVideo(filename);
                        } else if (fileType.equals("video-audio")) {
                            artifactsType.setVideoAudio(filename);
                        } else if (fileType.equals("alternate-audio")) {
                            artifactsType.setAlternateAudio(filename);
                        } else if (fileType.equals("stimulated-recall-transcript")) {
                            // TODO: stimulated-recall-description
                            StimulatedRecallInterviewsType stimulatedRecallInterviewsType =
                                    new StimulatedRecallInterviewsType();
                            stimulatedRecallInterviewsType.setStimulatedRecallTranscript(filename);
                            artifactsType.setStimulatedRecallInterviews(stimulatedRecallInterviewsType);
                        } else if (fileType.equals("sociometric-badge")) {
                            logger.error("Unable to set SOCIOMETRIC_BADGE [" + fileType + "]");
                        } else if (fileType.equals("elan-transcript-orthographic")) {
                            artifactsType.setElanTranscriptOrthographic(filename);
                        } else if (fileType.equals("traditional-transcript-orthographic")) {
                            artifactsType.setElanTranscriptOrthographic(filename);
                        } else if (fileType.equals("tab-delimited-transcript-orthographic")) {
                            artifactsType.setTabDelimitedTranscriptOrthographic(filename);
                        } else if (fileType.equals("elan-transcript-ca")) {
                            artifactsType.setElanTranscriptCa(filename);
                        } else if (fileType.equals("traditional-transcript-ca")) {
                            artifactsType.setTraditionalTranscriptCa(filename);
                        } else if (fileType.equals("tab-delimited-transcript-ca")) {
                            artifactsType.setTabDelimitedTranscriptCa(filename);
                        } else if (fileType.equals("ethnographic")) {
                            logger.error("Unable to set ETHNOGRAPHIC [" + fileType + "]");
                        } else if (fileType.equals("cta")) {
                            logger.error("Unable to set COGNITIVE_TASK_ANALYSIS [" + fileType + "]");
                        } else if (fileType.equals("other-transcript-ca")) {
                            artifactsType.setOtherTranscriptCa(filename);
                        } else {
                            logger.warn("Unrecognized fileType [" + fileType + "]");
                        }
                    }
                }
            }
        } else {
            logger.info("Unable to find Encounters by filename [" + artifactFilename + "]");
        }

        return artifactsType;
    }

    @Override
    public Corpus addCorpus(Corpus corpus) {
        return encounterModel.createCorpus(corpus);
    }

    @Override
    public Set<EncounterResponse> searchEncounters(@NotNull SearchType search) {
        return encounterModel.getEncountersBySearch(search);
    }

    @Override
    public List<AudioQuality> getAudioQuality() {
        return encounterModel.getAudioQuality();
    }

    @Override
    public List<VideoQuality> getVideoQuality() {
        return encounterModel.getVideoQuality();
    }

    public String getUploadDirectory() {
        return uploadDirectory;
    }
    public void setUploadDirectory(String uploadDirectory) {
        this.uploadDirectory = uploadDirectory;
    }

    public String getSessionCookieName() {
        return sessionCookieName;
    }
    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public String getSessionCookiePath() {
        return sessionCookiePath;
    }
    public void setSessionCookiePath(String sessionCookiePath) {
        this.sessionCookiePath = sessionCookiePath;
    }

    public String getSessionCookieDomain() {
        return sessionCookieDomain;
    }
    public void setSessionCookieDomain(String sessionCookieDomain) {
        this.sessionCookieDomain = sessionCookieDomain;
    }

    public boolean isSessionCookieSecure() {
        return sessionCookieSecure;
    }
    public void setSessionCookieSecure(boolean sessionCookieSecure) {
        this.sessionCookieSecure = sessionCookieSecure;
    }

    private String getFileName(MultivaluedMap<String, String> header) {

       String[] contentDisposition =
            header.getFirst("Content-Disposition").split(";");

       for (String filename : contentDisposition) {
           if ((filename.trim().startsWith("filename"))) {

               String[] name = filename.split("=");

               return name[1].trim().replaceAll("\"", "");
           }
       }
       return "unknown";
    }

    private NewCookie createCookie(String token) {
        String comment = "";
        int maxAge = -1;
        boolean secure = isSessionCookieSecure();

        return new javax.ws.rs.core.NewCookie(
                getSessionCookieName(), token, getSessionCookiePath(),
                getSessionCookieDomain(), comment, maxAge, secure);
    }

}
