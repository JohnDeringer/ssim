package com.sri.ssim.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 9/7/12
 */
public class ElanErrorHandler implements ErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void warning(SAXParseException exception) throws SAXException {
        //System.out.println("\nWARNING");
        logger.warn(exception.getMessage());
        exception.printStackTrace();
    }

    public void error(SAXParseException exception) throws SAXException {
        //System.out.println("\nERROR");
        logger.error(exception.getMessage());
        exception.printStackTrace();
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        //System.out.println("\nFATAL ERROR");
        logger.error(exception.getMessage());
        exception.printStackTrace();
    }

}
