package com.sri.ssim.file;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/11/12
 */
public class TimedUtterance implements Serializable {

    private List<AnnotatedLine> annotatedLines;
    private String filename;

    public List<AnnotatedLine> getAnnotatedLines() {
        return annotatedLines;
    }
    public void setAnnotatedLines(List<AnnotatedLine> annotatedLines) {
        this.annotatedLines = annotatedLines;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

}
