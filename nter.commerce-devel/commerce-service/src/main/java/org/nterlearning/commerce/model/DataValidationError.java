package org.nterlearning.commerce.model;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * User: Deringer
 * Date: 8/16/11
 * Time: 9:38 AM
 */
public class DataValidationError implements ErrorHandler {

    public void warning(SAXParseException exception) throws SAXException {
        throw new RuntimeException(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        throw new RuntimeException(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw new RuntimeException(exception);
    }


}
