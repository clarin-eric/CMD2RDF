package nl.knaw.dans.clarin.cmd2rdf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleErrorHandler implements ErrorHandler {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleErrorHandler.class);
	public SimpleErrorHandler(){
	}
	public void warning(SAXParseException e) throws SAXException {
		log.warn("WARN: " + e.getMessage());
    }

    public void error(SAXParseException e) throws SAXException {
        StringBuffer message = new StringBuffer("ERROR on line number: ");
        message.append(e.getLineNumber());
        message.append(" column number:");
        message.append(e.getColumnNumber());
        message.append(" message: ");
        message.append(e.getMessage());
   	 	throw new SAXException(message.toString());
    }

    public void fatalError(SAXParseException e) throws SAXException {
    	StringBuffer message = new StringBuffer("FATAL ERROR on Line number: ");
        message.append(e.getLineNumber());
        message.append(" column number:");
        message.append(e.getColumnNumber());
        message.append(" message: ");
        message.append(e.getMessage());
   	 	throw new SAXException(message.toString());
    }

}
