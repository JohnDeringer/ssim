package com.sri.ssim.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 * Maps JSON payload from REST API to Java
 *
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArtifactsType {

    @XmlElement(name = "interview-transcript")
    protected List<String> interviewTranscript;
    @XmlElement(name = "original-video", required = true)
    protected String originalVideo;
    @XmlElement(name = "converted-video", required = false)
    protected String convertedVideo;
    @XmlElement(name = "video-audio", required = false)
    protected String videoAudio;
    @XmlElement(name = "alternate-audio", required = false)
    protected String alternateAudio;
    @XmlElement(required = true)
    protected String corpus;
    @XmlElement(name = "collection-date", required = false)
    protected String collectionDate;
    @XmlElement(name = "collection-time", required = false)
    protected String collectionTime;
    @XmlElement(name = "general-collection-time", required = false)
    protected String generalCollectionTime;
    @XmlElement(name = "duration", required = false)
    protected String duration;
    @XmlElement(name = "geographic-location", required = false)
    protected String geographicLocation;
    @XmlElement(name = "site-location", required = false)
    protected String siteLocation;
    @XmlElement(name = "video-quality", required = false)
    protected String videoQuality;
    @XmlElement(name = "audio-quality", required = false)
    protected String audioQuality;
    @XmlElement(required = false)
    protected String collector;
    @XmlElement(name = "data-descriptor", required = false)
    protected String dataDescriptor;
    @XmlElement(name = "elan-transcript-orthographic", required = false)
    protected String elanTranscriptOrthographic;
    @XmlElement(name = "traditional-trascript-orthographic", required = false)
    protected String traditionalTrascriptOrthographic;
    @XmlElement(name = "tab-delimited-transcript-orthographic", required = false)
    protected String tabDelimitedTranscriptOrthographic;
    @XmlElement(name = "elan-transcript-ca", required = false)
    protected String elanTranscriptCa;
    @XmlElement(name = "traditional-transcript-ca", required = false)
    protected String traditionalTranscriptCa;
    @XmlElement(name = "tab-delimited-transcript-ca", required = false)
    protected String tabDelimitedTranscriptCa;
    @XmlElement(name = "other-transcript-ca", required = false)
    protected String otherTranscriptCa;
    @XmlElement(name = "comments", required = false)
    protected String comments;
    @XmlElement(name = "stimulated-recall-interviews", required = false)
    protected StimulatedRecallInterviewsType stimulatedRecallInterviews;
    protected List<EncountersType> encounters;

    public List<String> getInterviewTranscript() {
        if (interviewTranscript == null) {
            interviewTranscript = new ArrayList<String>();
        }
        return this.interviewTranscript;
    }

    public String getOriginalVideo() {
        return originalVideo;
    }
    public void setOriginalVideo(String value) {
        this.originalVideo = value;
    }

    public String getConvertedVideo() {
        return convertedVideo;
    }
    public void setConvertedVideo(String value) {
        this.convertedVideo = value;
    }

    public String getVideoAudio() {
        return videoAudio;
    }
    public void setVideoAudio(String value) {
        this.videoAudio = value;
    }

    public String getAlternateAudio() {
        return alternateAudio;
    }
    public void setAlternateAudio(String value) {
        this.alternateAudio = value;
    }

    public String getDuration() {
        return duration;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCorpus() {
        return corpus;
    }
    public void setCorpus(String value) {
        this.corpus = value;
    }

    public String getCollectionDate() {
        return collectionDate;
    }
    public void setCollectionDate(String value) {
        this.collectionDate = value;
    }

    public String getCollectionTime() {
        return collectionTime;
    }
    public void setCollectionTime(String value) {
        this.collectionTime = value;
    }

    public String getGeneralCollectionTime() {
        return generalCollectionTime;
    }

    public String getGeographicLocation() {
        return geographicLocation;
    }
    public void setGeographicLocation(String value) {
        this.geographicLocation = value;
    }

    public String getSiteLocation() {
        return siteLocation;
    }
    public void setSiteLocation(String value) {
        this.siteLocation = value;
    }

    public String getVideoQuality() {
        return videoQuality;
    }
    public void setVideoQuality(String value) {
        this.videoQuality = value;
    }

    public String getAudioQuality() {
        return audioQuality;
    }
    public void setAudioQuality(String value) {
        this.audioQuality = value;
    }

    public String getCollector() {
        return collector;
    }
    public void setCollector(String value) {
        this.collector = value;
    }

    public String getDataDescriptor() {
        return dataDescriptor;
    }
    public void setDataDescriptor(String value) {
        this.dataDescriptor = value;
    }

    public String getElanTranscriptOrthographic() {
        return elanTranscriptOrthographic;
    }
    public void setElanTranscriptOrthographic(String value) {
        this.elanTranscriptOrthographic = value;
    }

    public String getTraditionalTrascriptOrthographic() {
        return traditionalTrascriptOrthographic;
    }
    public void setTraditionalTrascriptOrthographic(String value) {
        this.traditionalTrascriptOrthographic = value;
    }

    public String getTabDelimitedTranscriptOrthographic() {
        return tabDelimitedTranscriptOrthographic;
    }
    public void setTabDelimitedTranscriptOrthographic(String value) {
        this.tabDelimitedTranscriptOrthographic = value;
    }

    public String getElanTranscriptCa() {
        return elanTranscriptCa;
    }
    public void setElanTranscriptCa(String value) {
        this.elanTranscriptCa = value;
    }

    public String getTraditionalTranscriptCa() {
        return traditionalTranscriptCa;
    }
    public void setTraditionalTranscriptCa(String value) {
        this.traditionalTranscriptCa = value;
    }

    public String getTabDelimitedTranscriptCa() {
        return tabDelimitedTranscriptCa;
    }
    public void setTabDelimitedTranscriptCa(String value) {
        this.tabDelimitedTranscriptCa = value;
    }

    public String getOtherTranscriptCa() {
        return otherTranscriptCa;
    }
    public void setOtherTranscriptCa(String value) {
        this.otherTranscriptCa = value;
    }

    public String getComments() {
        return comments;
    }
    public void setComments(String value) {
        this.comments = value;
    }

    public StimulatedRecallInterviewsType getStimulatedRecallInterviews() {
        return stimulatedRecallInterviews;
    }
    public void setStimulatedRecallInterviews(StimulatedRecallInterviewsType value) {
        this.stimulatedRecallInterviews = value;
    }

    public List<EncountersType> getEncounters() {
        if (encounters == null) {
            encounters = new ArrayList<EncountersType>();
        }
        return this.encounters;
    }

    @Override
    public String toString() {
        return "ArtifactsType{" +
                "interviewTranscript=" + interviewTranscript +
                ", originalVideo='" + originalVideo + '\'' +
                ", convertedVideo='" + convertedVideo + '\'' +
                ", videoAudio='" + videoAudio + '\'' +
                ", alternateAudio='" + alternateAudio + '\'' +
                ", corpus='" + corpus + '\'' +
                ", collectionDate='" + collectionDate + '\'' +
                ", collectionTime='" + collectionTime + '\'' +
                ", generalCollectionTime='" + generalCollectionTime + '\'' +
                ", duration='" + duration + '\'' +
                ", geographicLocation='" + geographicLocation + '\'' +
                ", siteLocation='" + siteLocation + '\'' +
                ", videoQuality='" + videoQuality + '\'' +
                ", audioQuality='" + audioQuality + '\'' +
                ", collector='" + collector + '\'' +
                ", dataDescriptor='" + dataDescriptor + '\'' +
                ", elanTranscriptOrthographic='" + elanTranscriptOrthographic + '\'' +
                ", traditionalTrascriptOrthographic='" + traditionalTrascriptOrthographic + '\'' +
                ", tabDelimitedTranscriptOrthographic='" + tabDelimitedTranscriptOrthographic + '\'' +
                ", elanTranscriptCa='" + elanTranscriptCa + '\'' +
                ", traditionalTranscriptCa='" + traditionalTranscriptCa + '\'' +
                ", tabDelimitedTranscriptCa='" + tabDelimitedTranscriptCa + '\'' +
                ", otherTranscriptCa='" + otherTranscriptCa + '\'' +
                ", comments='" + comments + '\'' +
                ", stimulatedRecallInterviews=" + stimulatedRecallInterviews +
                ", encounters=" + encounters +
                '}';
    }
}
