package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "phraseType", propOrder = {
    "phraseMatch",
    "phrase",
    "andor"
})
public class PhraseType {

    @XmlElement(name = "phrase-match", required = true)
    protected String phraseMatch;
    @XmlElement(required = true)
    protected String phrase;
    protected String andor;


    public String getPhraseMatch() {
        return phraseMatch;
    }
    public void setPhraseMatch(String value) {
        this.phraseMatch = value;
    }

    public String getPhrase() {
        return phrase;
    }
    public void setPhrase(String value) {
        this.phrase = value;
    }

    public String getAndor() {
        return andor;
    }
    public void setAndor(String value) {
        this.andor = value;
    }

    @Override
    public String toString() {
        return "PhraseType{" +
                "phraseMatch='" + phraseMatch + '\'' +
                ", phrase='" + phrase + '\'' +
                ", andor='" + andor + '\'' +
                '}';
    }

}
