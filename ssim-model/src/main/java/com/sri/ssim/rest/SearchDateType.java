package com.sri.ssim.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-dateType", propOrder = {
    "searchDateMatch",
    "searchDate",
    "andor"
})
public class SearchDateType {

    @XmlElement(name = "search-date-match", required = true)
    protected String searchDateMatch;
    @XmlElement(name = "search-date", required = true)
    protected String searchDate;
    protected String andor;

    /**
     * Gets the value of the searchDateMatch property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchDateMatch() {
        return searchDateMatch;
    }

    /**
     * Sets the value of the searchDateMatch property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchDateMatch(String value) {
        this.searchDateMatch = value;
    }

    /**
     * Gets the value of the searchDate property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSearchDate() {
        return searchDate;
    }

    /**
     * Sets the value of the searchDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSearchDate(String value) {
        this.searchDate = value;
    }

    public String getAndor() {
        return andor;
    }

    public void setAndor(String value) {
        this.andor = value;
    }

}
