package com.sri.ssim.file;

import com.sri.ssim.schema.ANNOTATIONDOCUMENT;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/11/12
 */
public class ParserRouter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TimedUtterance parse(@NotNull DocumentWrapper documentWrapper) {
        TimedUtterance timedUtterance = new TimedUtterance();
        String filename = documentWrapper.getFilename();
        if (filename != null) {
            logger.info("Evaluating file [" + filename + "]");
            timedUtterance.setFilename(filename);

            // ELAN file
            if (documentWrapper.getFileType() != null &&
                    documentWrapper.getFileType().equals(ANNOTATIONDOCUMENT.class)) {

                ElanDocumentParser documentParser =
                    new ElanDocumentParser((ANNOTATIONDOCUMENT)documentWrapper.getDocument());

                timedUtterance.setAnnotatedLines(documentParser.getAnnotatedLines());

            }
        }

        return timedUtterance;
    }

}
