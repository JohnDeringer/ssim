package com.sri.ssim.rest;

import javax.xml.bind.annotation.*;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 12/13/12
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FilenameType {

    @XmlElement(name = "filename-match", required = true)
    protected String filenameMatch;
    @XmlElement(required = true)
    protected String filename;

    /**
     * Gets the value of the filenameMatch property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilenameMatch() {
        return filenameMatch;
    }

    /**
     * Sets the value of the filenameMatch property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilenameMatch(String value) {
        this.filenameMatch = value;
    }

    /**
     * Gets the value of the filename property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the value of the filename property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFilename(String value) {
        this.filename = value;
    }

    @Override
    public String toString() {
        return "FilenameType{" +
                "filenameMatch='" + filenameMatch + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }

}
