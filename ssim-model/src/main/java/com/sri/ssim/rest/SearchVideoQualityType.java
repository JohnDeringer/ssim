package com.sri.ssim.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-video-qualityType", propOrder = {
    "searchVideoQualityMatch",
    "searchVideoQuality"
})
public class SearchVideoQualityType {

    @XmlElement(name = "search-video-quality-match", required = true)
    protected String searchVideoQualityMatch;
    @XmlElement(name = "search-video-quality", required = true)
    protected String searchVideoQuality;

    /**
     * Gets the value of the searchVideoQualityMatch property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchVideoQualityMatch() {
        return searchVideoQualityMatch;
    }

    /**
     * Sets the value of the searchVideoQualityMatch property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchVideoQualityMatch(String value) {
        this.searchVideoQualityMatch = value;
    }

    /**
     * Gets the value of the searchVideoQuality property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchVideoQuality() {
        return searchVideoQuality;
    }

    /**
     * Sets the value of the searchVideoQuality property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchVideoQuality(String value) {
        this.searchVideoQuality = value;
    }

}
