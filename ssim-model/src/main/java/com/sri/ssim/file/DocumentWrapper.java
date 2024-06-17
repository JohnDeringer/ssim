package com.sri.ssim.file;

import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/11/12
 */
public class DocumentWrapper {

    private Object document = null;
    private String filename = null;
    private Class fileType = null;

    public DocumentWrapper() {
    }

    public DocumentWrapper(
            @NotNull Object document,
            @NotNull String filename,
            @NotNull Class fileType) {
        this.document = document;
        this.filename = filename;
        this.fileType = fileType;
    }

    public Object getDocument() {
        return document;
    }
    public void setDocument(Object document) {
        this.document = document;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Class getFileType() {
        return fileType;
    }
    public void setFileType(Class fileType) {
        this.fileType = fileType;
    }

}
