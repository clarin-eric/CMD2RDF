package nl.knaw.dans.clarin.cmd2rdf.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.omg.CORBA.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class WellFormedValidator {
	private static final Logger log = LoggerFactory.getLogger(WellFormedValidator.class);

	/**
	   * This is parsing code
	   *
	   * @param xml The input argument to check.
	   * @throws SAXException
	   *             If the input xml is invalid
	   *
	   * @throws SystemException
	   *             Thrown if the input string cannot be read
	   */
	
	  public static boolean validate(String xml){
		  SAXParserFactory factory = SAXParserFactory.newInstance();
		  factory.setValidating(false);
		  factory.setNamespaceAware(true);

		  SAXParser parser;
		try {
			parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setErrorHandler(new SimpleErrorHandler());
			reader.parse(new InputSource(xml));
			return true;
		} catch (ParserConfigurationException e) {
			log.error("Validation ERROR (ParserConfigurationException) of '" + xml + "'. Ccaused by " + e.getCause() );
		} catch (SAXException e) {
			log.error("Validation ERROR (SAXException) of '" + xml + "'. Caused by " + e.getMessage() );
		} catch (IOException e) {
			log.error("Validation ERROR (IOException) of '" + xml + "'. Caused by " + e.getMessage() );
		}
		return false;
	  }
	  
	  public static void main(String args[]) {
			  log.debug("===BEGIN===");
			  DateTime start = new DateTime();
			  int i=0;
			  int x = 0;
			  String path="";
			
			  Iterator<File> iter = FileUtils.iterateFiles(new File(args[0]),new String[] {"rdf"}, true);
	    	while (iter.hasNext()) {
	    		i++;
	    		File f = iter.next();
	    		path = f.getAbsolutePath();
	    		//log.debug("Validating " + f.getAbsolutePath());
	    		if (!validate(path))
	    			x++;
	    	}	
		
		  DateTime end = new DateTime();
		  Period duration = new Period(start, end);
	    	log.info("Number of rdf files: " + i);
	    	log.info("Number of invalid rdf files: " + x);
	    	log.info("duration in Hours: " + duration.getHours());
	    	log.info("duration in Minutes: " + duration.getMinutes());
	    	log.info("duration in Seconds: " + duration.getSeconds());
		  log.debug("===END===");
	  }
	  
	}
