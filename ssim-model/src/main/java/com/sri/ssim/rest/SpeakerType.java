package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 01/17/13
 * Maps JSON to Java
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "speakerType", propOrder = {
    "speakerMatch",
    "speaker",
    "andor"
})
public class SpeakerType {

    @XmlElement(name = "speaker-match", required = true)
    protected String speakerMatch;
    @XmlElement(required = true)
    protected String speaker;
    protected String andor;

    public String getSpeakerMatch() {
        return speakerMatch;
    }
    public void setSpeakerMatch(String value) {
        this.speakerMatch = value;
    }

    public String getSpeaker() {
        return speaker;
    }
    public void setSpeaker(String value) {
        this.speaker = value;
    }

    public String getAndor() {
        return andor;
    }
    public void setAndor(String value) {
        this.andor = value;
    }

}
