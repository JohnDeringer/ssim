package com.sri.ssim.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 12/13/12
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileRequest {

    protected List<FormatType> format;
    protected List<String> file;

    public List<FormatType> getFormat() {
        if (format == null) {
            format = new ArrayList<FormatType>();
        }
        return this.format;
    }

    public List<String> getFile() {
        if (file == null) {
            file = new ArrayList<String>();
        }
        return this.file;
    }

}
