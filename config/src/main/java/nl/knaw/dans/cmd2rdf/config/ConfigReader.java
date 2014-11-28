/**
 * 
 */
package nl.knaw.dans.cmd2rdf.config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.knaw.dans.cmd2rdf.config.exeception.ConfigException;
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
	private final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
	private Map<String, String> GLOBAL_VARS = new HashMap<String, String>();

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
			throw new ConfigException("'" + xmlSrcFilePath + "'. The given xml file does not exist or not a xml file.");
	
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
			GLOBAL_VARS.put(p.name, p.value);
		}
		//iterate through map, find whether map values contain {val}
		for (Map.Entry<String, String> e : GLOBAL_VARS.entrySet()) {
			String pVal = e.getValue();
			Matcher m = pattern.matcher(pVal);
			if (m.find()) {
				String globalVar = m.group(1);
				if (GLOBAL_VARS.containsKey(globalVar)) {
					pVal = pVal.replace(m.group(0), GLOBAL_VARS.get(globalVar));
					GLOBAL_VARS.put(e.getKey(), pVal);
				}
			}
		}
	}

	public String getTripleStoreServerHost(){
		return GLOBAL_VARS.get("serverHost");
	}

	public String getTripleStoreUsername(){
		return GLOBAL_VARS.get("username");
	}
	
	public String getTripleStorePassword(){
		return GLOBAL_VARS.get("password");
	}
	
	public String getConfigFileLastModifiedDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		return sdf.format(xmlFile.lastModified());
	}
	
	public String getRawXmlContent() {
		return rawXmlContent;
	}
	
	public String getPrefixBaseURI() {
		return GLOBAL_VARS.get("prefixBaseURI");
	}
	
	public String getDirDownloadPwd() {
		return GLOBAL_VARS.get("dirDownloadPwd");
	}
	
}
