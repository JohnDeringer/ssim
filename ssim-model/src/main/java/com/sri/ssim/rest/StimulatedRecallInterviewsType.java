package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StimulatedRecallInterviewsType {

    @XmlElement(name = "stimulated-recall-transcript", required = true)
    protected String stimulatedRecallTranscript;
    @XmlElement(name = "stimulated-recall-description", required = true)
    protected String stimulatedRecallDescription;

    /**
     * Gets the value of the stimulatedRecallTranscript property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStimulatedRecallTranscript() {
        return stimulatedRecallTranscript;
    }

    /**
     * Sets the value of the stimulatedRecallTranscript property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStimulatedRecallTranscript(String value) {
        this.stimulatedRecallTranscript = value;
    }

    /**
     * Gets the value of the stimulatedRecallDescription property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStimulatedRecallDescription() {
        return stimulatedRecallDescription;
    }

    /**
     * Sets the value of the stimulatedRecallDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStimulatedRecallDescription(String value) {
        this.stimulatedRecallDescription = value;
    }

    @Override
    public String toString() {
        return "StimulatedRecallInterviewsType{" +
                "stimulatedRecallTranscript='" + stimulatedRecallTranscript + '\'' +
                ", stimulatedRecallDescription='" + stimulatedRecallDescription + '\'' +
                '}';
    }

}
