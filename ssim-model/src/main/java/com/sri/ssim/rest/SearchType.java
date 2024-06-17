package com.sri.ssim.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchTypeType", propOrder = {
    "encounter",
    "filename",
    "transcript",
    "participants"
})
public class SearchType {

//    @XmlElement(required = true)
//    protected EncounterType encounter;
//    @XmlElement(required = true)
//    protected SearchParticipants participants;
//    @XmlElement(required = true)
//    protected FilenameType filename;
//    protected List<TranscriptType> transcript;

    @XmlElement
    protected EncounterType encounter;
    @XmlElement
    protected FilenameType filename;
    protected List<TranscriptType> transcript;
    protected List<SearchParticipants> participants;


    public FilenameType getFilename() {
        return filename;
    }
    public void setFilename(FilenameType value) {
        this.filename = value;
    }

    public List<TranscriptType> getTranscript() {
        if (transcript == null) {
            transcript = new ArrayList<TranscriptType>();
        }
        return this.transcript;
    }

//    public SearchParticipants getParticipants() {
//        return participants;
//    }
//    public void setParticipants(SearchParticipants value) {
//        this.participants = value;
//    }
    public List<SearchParticipants> getParticipants() {
        if (participants == null) {
            participants = new ArrayList<SearchParticipants>();
        }
        return this.participants;
    }

    public EncounterType getEncounter() {
        return encounter;
    }
    public void setEncounter(EncounterType value) {
        this.encounter = value;
    }

}
