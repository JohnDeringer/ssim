package com.sri.ssim.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EncounterResponse implements Serializable, Comparable<EncounterResponse> {

    @XmlElement(name = "encounter-name")
    protected String encounterName;
    @XmlElement(name = "phrase")
    protected String phrase;
    @XmlElement(name = "hits")
    protected int hits;
    protected Set<FileResponse> files;

    public String getEncounterName() {
        return encounterName;
    }
    public void setEncounterName(String value) {
        this.encounterName = value;
    }

    public String getPhrase() {
        return phrase;
    }
    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public int getHits() {
        return hits;
    }
    public void setHits(int hits) {
        this.hits = hits;
    }

    public Set<FileResponse> getFiles() {
        return files;
    }
    public void setFiles(Set<FileResponse> files) {
        this.files = files;
    }

    @Override
    public int compareTo(EncounterResponse encounter) {
        return getEncounterName().compareTo(encounter.getEncounterName());
    }

}
