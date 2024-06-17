package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-encounter-languageType", propOrder = {
    "searchEncounterLanguage",
    "andor"
})
public class SearchLanguageType {

    @XmlElement(name = "search-encounter-language", required = true)
    protected String searchEncounterLanguage;
    protected String andor;

    public String getLanguage() {
        return searchEncounterLanguage;
    }

    public void setLanguage(String value) {
        this.searchEncounterLanguage = value;
    }

    public String getAndor() {
        return andor;
    }

    public void setAndor(String value) {
        this.andor = value;
    }

    @Override
    public String toString() {
        return "SearchLanguageType{" +
                "language='" + searchEncounterLanguage + '\'' +
                ", andor='" + andor + '\'' +
                '}';
    }

}
