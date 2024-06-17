package com.sri.ssim.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-time-specificType", propOrder = {
    "searchTimeSpecificMatch",
    "searchTimeSpecificValue"
})
public class SearchTimeSpecificType {

    @XmlElement(name = "search-time-specific-match", required = true)
    protected String searchTimeSpecificMatch;
    @XmlElement(name = "search-time-specific-value", required = true)
    protected String searchTimeSpecificValue;

    /**
     * Gets the value of the searchTimeSpecificMatch property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchTimeSpecificMatch() {
        return searchTimeSpecificMatch;
    }

    /**
     * Sets the value of the searchTimeSpecificMatch property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchTimeSpecificMatch(String value) {
        this.searchTimeSpecificMatch = value;
    }

    /**
     * Gets the value of the searchTimeSpecificValue property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchTimeSpecificValue() {
        return searchTimeSpecificValue;
    }

    /**
     * Sets the value of the searchTimeSpecificValue property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchTimeSpecificValue(String value) {
        this.searchTimeSpecificValue = value;
    }

}
