package com.sri.ssim.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/8/13
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "search-ageType", propOrder = {
    "searchAgeMatch",
    "searchAge",
    "andor"
})
public class SearchAgeType {

    @XmlElement(name = "search-age-match", required = true)
    protected String searchAgeMatch;
    @XmlElement(name = "search-age", required = true)
    protected String searchAge;
    protected String andor;


    public String getSearchAgeMatch() {
        return searchAgeMatch;
    }
    public void setSearchAgeMatch(String value) {
        this.searchAgeMatch = value;
    }

    public String getSearchAge() {
        return searchAge;
    }
    public void setSearchAge(String value) {
        this.searchAge = value;
    }

    public String getAndor() {
        return andor;
    }
    public void setAndor(String value) {
        this.andor = value;
    }

}
