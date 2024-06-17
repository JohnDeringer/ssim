package com.sri.ssim.rest;

import java.io.Serializable;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 2/1/13
 */
public class FileResponse implements Serializable, Comparable<FileResponse> {

    protected FormatType format;
    protected String filename;

    public FormatType getFormat() {
        return format;
    }
    public void setFormat(FormatType format) {
        this.format = format;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public int compareTo(FileResponse fileResponse) {
        String name = fileResponse.getFilename();
        if (name == null) {
            return -1;
        }

        return filename.compareTo(fileResponse.getFilename());
    }
}
