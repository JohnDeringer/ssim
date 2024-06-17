package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps JSON payload from REST API to Java
 *
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 8/28/12
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParticipantsType {

    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected String gender;
    @XmlElement(required = true)
    protected String age;
    @XmlElement(required = true)
    protected String codename;
    @XmlElement(required = true)
    protected String role;
    @XmlElement(required = true)
    protected String ethnicity;
    @XmlElement(name = "participant-languages")
    protected List<String> participantLanguages;
    @XmlElement(required = true)
    protected String origin;
    @XmlElement(name = "time-in-country")
    protected String timeInCountry;
    @XmlElement(name = "sociometric-badge")
    protected String sociometricBadge;
    @XmlElement(name = "participant-comments", required = true)
    protected String participantComments;


    public String getType() {
        return type;
    }
    public void setType(String value) {
        this.type = value;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String value) {
        this.gender = value;
    }

    public String getAge() {
        return age;
    }
    public void setAge(String value) {
        this.age = value;
    }

    public String getCodename() {
        return codename;
    }
    public void setCodename(String value) {
        this.codename = value;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String value) {
        this.role = value;
    }

    public String getEthnicity() {
        return ethnicity;
    }
    public void setEthnicity(String value) {
        this.ethnicity = value;
    }

    public List<String> getParticipantLanguages() {
        if (participantLanguages == null) {
            participantLanguages = new ArrayList<String>();
        }
        return this.participantLanguages;
    }

    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String value) {
        this.origin = value;
    }

    public String getTimeInCountry() {
        return timeInCountry;
    }
    public void setTimeInCountry(String timeInCountry) {
        this.timeInCountry = timeInCountry;
    }

    public String getSociometricBadge() {
        return sociometricBadge;
    }
    public void setSociometricBadge(String value) {
        this.sociometricBadge = value;
    }

    public String getParticipantComments() {
        return participantComments;
    }
    public void setParticipantComments(String value) {
        this.participantComments = value;
    }

    @Override
    public String toString() {
        return "ParticipantsType{" +
                "type='" + type + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", codename='" + codename + '\'' +
                ", role='" + role + '\'' +
                ", ethnicity='" + ethnicity + '\'' +
                ", participantLanguages=" + participantLanguages +
                ", origin='" + origin + '\'' +
                ", timeInCountry='" + timeInCountry + '\'' +
                ", sociometricBadge='" + sociometricBadge + '\'' +
                ", participantComments='" + participantComments + '\'' +
                '}';
    }

}
