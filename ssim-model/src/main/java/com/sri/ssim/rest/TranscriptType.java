package com.sri.ssim.rest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transcriptType", propOrder = {
    "followedBy",
    "interveningTime",
    "phrase",
    "speaker",
    "annotation",
    "turnTime",
    "words"
})
public class TranscriptType {

    @XmlElement(name = "followed-by")
    protected String followedBy;
    @XmlElement(name = "intervening-time")
    protected InterveningTimeType interveningTime;
    protected List<PhraseType> phrase;
    protected List<SpeakerType> speaker;
    protected List<AnnotationsType> annotation;
    @XmlElement(name = "turn-time", required = true)
    protected TurnTimeType turnTime;
    protected WordsType words;


    public String getFollowedBy() {
        return followedBy;
    }
    public void setFollowedBy(String value) {
        this.followedBy = value;
    }

    public InterveningTimeType getInterveningTime() {
        return interveningTime;
    }
    public void setInterveningTime(InterveningTimeType value) {
        this.interveningTime = value;
    }

    public List<PhraseType> getPhrases() {
        if (phrase == null) {
            phrase = new ArrayList<PhraseType>();
        }
        return this.phrase;
    }

    public List<SpeakerType> getSpeaker() {
        if (speaker == null) {
            speaker = new ArrayList<SpeakerType>();
        }
        return this.speaker;
    }

    public List<AnnotationsType> getAnnotations() {
        if (annotation == null) {
            annotation = new ArrayList<AnnotationsType>();
        }
        return this.annotation;
    }

    public TurnTimeType getTurnTime() {
        return turnTime;
    }
    public void setTurnTime(TurnTimeType value) {
        this.turnTime = value;
    }

    public WordsType getWords() {
        return words;
    }
    public void setWords(WordsType value) {
        this.words = value;
    }

    @Override
    public String toString() {
        return "TranscriptType{" +
                "followedBy='" + followedBy + '\'' +
                ", interveningTime=" + interveningTime +
                ", phrase=" + phrase +
                ", speaker=" + speaker +
                ", annotation=" + annotation +
                ", turnTime=" + turnTime +
                ", words=" + words +
                '}';
    }

}
