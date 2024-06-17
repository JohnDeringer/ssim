package com.sri.ssim.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-durationType", propOrder = {
    "searchDurationMatch",
    "searchDuration"
})
public class SearchDurationType {

    @XmlElement(name = "search-duration-match", required = true)
    protected String searchDurationMatch;
    @XmlElement(name = "search-duration", required = true)
    protected String searchDuration;

    /**
     * Gets the value of the searchDurationMatch property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchDurationMatch() {
        return searchDurationMatch;
    }

    /**
     * Sets the value of the searchDurationMatch property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchDurationMatch(String value) {
        this.searchDurationMatch = value;
    }

    /**
     * Gets the value of the searchDuration property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchDuration() {
        return searchDuration;
    }

    /**
     * Sets the value of the searchDuration property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchDuration(String value) {
        this.searchDuration = value;
    }

}
