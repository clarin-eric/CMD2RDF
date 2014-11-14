/**
 * 
 */
package nl.knaw.dans.cmd2rdf.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.List;

import nl.knaw.dans.cmd2rdf.config.exeception.ConfigException;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Config;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Jobs;
import nl.knaw.dans.cmd2rdf.config.xmlmapping.Property;

import org.easybatch.core.api.Record;
import org.easybatch.xml.XmlRecordMapper;
import org.easybatch.xml.XmlRecordReader;

/**
 * @author akmi
 *
 */
public class ConfigReader {
	
	private File xmlFile;
	private String rawXmlContent;
	private String serverHost;
	private String username;
	private String password;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public ConfigReader(String xmlSrcFilePath) throws ConfigException {
		xmlFile = new File(xmlSrcFilePath);
		if (xmlFile == null || xmlSrcFilePath.isEmpty() || !xmlFile.exists() 
				|| !xmlFile.isFile() || !xmlSrcFilePath.endsWith(".xml"))
			throw new ConfigException("The given xml file does not exist or not a xml file.");
	
		init();
	}

	private void init() throws ConfigException{
		// Build an easy batch engine
    	XmlRecordReader xrr = new XmlRecordReader("CMD2RDF", xmlFile);
    	try {
			xrr.open();
			boolean b = xrr.hasNextRecord();
	    	if (b) {
		    	Record<String> r = xrr.readNextRecord();
		    	rawXmlContent = r.getRawContent();
		    	XmlRecordMapper<Jobs> xrm = new XmlRecordMapper<Jobs>(Jobs.class);
		    	Jobs j = xrm.mapRecord(r);
		    	fetchConfigProperties(j.getConfig().getProperty());
	    	} else
	    		throw new ConfigException("The " + xmlFile.getAbsolutePath() + " isn't valid CMD2RDF config file.");
	    	xrr.close();
		} catch (Exception e) {
			throw new ConfigException("Cannot open CMD2RDF FIle. " + e.getMessage());
		}
    	
	}
	
	private void fetchConfigProperties(List<Property> configPropertyList) {
		for (Property p : configPropertyList) {
			if (p.name.equals("serverHost")) 
				serverHost = p.value;
			else if (p.name.equals("username")) 
				username = p.value;
			else if (p.name.equals("password"))
				password = p.value;
		}
		
	}

	public String getTripleStoreServerHost(){
		return serverHost;
	}

	public String getTripleStoreUsername(){
		return username;
	}
	
	public String getTripleStorePassword(){
		return password;
	}
	
	public String getConfigFileLastModifiedDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		return sdf.format(xmlFile.lastModified());
	}
	
	public String getRawXmlContent() {
		return rawXmlContent;
	}
	
}
