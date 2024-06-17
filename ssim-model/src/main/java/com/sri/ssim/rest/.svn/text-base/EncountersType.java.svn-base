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
public class EncountersType {

    @XmlElement(name = "encounter-domain", required = true)
    protected String encounterDomain;
    @XmlElement(name = "encounter-num-participants", required = true)
    protected String encounterNumParticipants;
    @XmlElement(name = "encounter-bystanders", required = true)
    protected String encounterBystanders;
    @XmlElement(name = "encounter-reason", required = true)
    protected String encounterReason;
    @XmlElement(name = "encounter-duration", required = true)
    protected String encounterDuration;
    @XmlElement(name = "encounter-quality", required = true)
    protected String encounterQuality;
    @XmlElement(name = "encounter-first-encounter", required = true)
    protected String encounterFirstEncounter;
    @XmlElement(name = "encounter-familiar", required = true)
    protected String encounterFamiliar;
    @XmlElement(name = "encounter-languages")
    protected List<String> encounterLanguages;
    @XmlElement(name = "encounter-comments", required = true)
    protected String encounterComments;
    protected List<ParticipantsType> participants;
    @XmlElement(name = "encounter-name")
    protected String encounterName;


    public String getEncounterDomain() {
        return encounterDomain;
    }
    public void setEncounterDomain(String value) {
        this.encounterDomain = value;
    }

    public String getEncounterNumParticipants() {
        return encounterNumParticipants;
    }
    public void setEncounterNumParticipants(String value) {
        this.encounterNumParticipants = value;
    }

    public String getEncounterBystanders() {
        return encounterBystanders;
    }
    public void setEncounterBystanders(String value) {
        this.encounterBystanders = value;
    }

    public String getEncounterReason() {
        return encounterReason;
    }
    public void setEncounterReason(String value) {
        this.encounterReason = value;
    }

    public String getEncounterDuration() {
        return encounterDuration;
    }
    public void setEncounterDuration(String value) {
        this.encounterDuration = value;
    }

    public String getEncounterQuality() {
        return encounterQuality;
    }
    public void setEncounterQuality(String value) {
        this.encounterQuality = value;
    }

    public String getEncounterFirstEncounter() {
        return encounterFirstEncounter;
    }
    public void setEncounterFirstEncounter(String value) {
        this.encounterFirstEncounter = value;
    }

    public String getEncounterFamiliar() {
        return encounterFamiliar;
    }
    public void setEncounterFamiliar(String value) {
        this.encounterFamiliar = value;
    }

    /**
     * Gets the value of the encounterLanguages property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the encounterLanguages property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEncounterLanguages().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getEncounterLanguages() {
        if (encounterLanguages == null) {
            encounterLanguages = new ArrayList<String>();
        }
        return this.encounterLanguages;
    }

    public String getEncounterComments() {
        return encounterComments;
    }
    public void setEncounterComments(String value) {
        this.encounterComments = value;
    }

    /**
     * Gets the value of the participants property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participants property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipants().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParticipantsType }
     *
     *
     */
    public List<ParticipantsType> getParticipants() {
        if (participants == null) {
            participants = new ArrayList<ParticipantsType>();
        }
        return this.participants;
    }

    public String getEncounterName() {
        return encounterName;
    }
    public void setEncounterName(String value) {
        this.encounterName = value;
    }

    @Override
    public String toString() {
        return "EncountersType{" +
                "encounterDomain='" + encounterDomain + '\'' +
                ", encounterNumParticipants='" + encounterNumParticipants + '\'' +
                ", encounterBystanders='" + encounterBystanders + '\'' +
                ", encounterReason='" + encounterReason + '\'' +
                ", encounterDuration='" + encounterDuration + '\'' +
                ", encounterQuality='" + encounterQuality + '\'' +
                ", encounterFirstEncounter='" + encounterFirstEncounter + '\'' +
                ", encounterFamiliar='" + encounterFamiliar + '\'' +
                ", encounterLanguages=" + encounterLanguages +
                ", encounterComments='" + encounterComments + '\'' +
                ", participants=" + participants +
                ", encounterName='" + encounterName + '\'' +
                '}';
    }

}
