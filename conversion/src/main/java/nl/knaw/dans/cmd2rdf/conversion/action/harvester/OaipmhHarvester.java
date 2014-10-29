package nl.knaw.dans.cmd2rdf.conversion.action.harvester;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;

import org.apache.commons.io.FileUtils;
import org.apache.directmemory.cache.CacheService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.XMLWriter;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.Record;
import se.kb.oai.pmh.RecordsList;
import se.kb.oai.pmh.ResumptionToken;

/**
 * @author Eko Indarto
 *
 */
public class OaipmhHarvester implements IAction{
	private static final Logger log = LoggerFactory.getLogger(OaipmhHarvester.class);
	private static CacheService<Object, Object> cacheService;
	private static boolean asRoot = true;
	private String oaipmhBaseURL;
	private String prefix;
	private String set;
	private String outputFile;
	private Map<String, String> params;
	
//	public OaipmhHarvester() {
//	}
	public OaipmhHarvester(CacheService<Object, Object> cacheService) {
		OaipmhHarvester.cacheService = cacheService;
	}
	
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	private boolean harvest(){
		Split split = SimonManager.getStopwatch("stopwatch.oai").start();
		log.debug("Harvesting... ");
		log.debug("baseUrl: " + oaipmhBaseURL);
		log.debug("prefix: " + prefix);
		log.debug("SET: " + set);
		boolean ok = true;
		Document doc = DocumentFactory.getInstance()
				.createDocument();
		Element rootElement = null;
		OaiPmhServer server = new OaiPmhServer(oaipmhBaseURL);
		
		try {
			RecordsList records = server.listRecords(
					prefix, null, null,
					set);
			boolean more = true;
			while (more && ok) {
				for (Record record : records.asList()) {
					if (record != null) {
						Element element = record.getMetadata();
						
						if (element != null) {
							Node node = element.selectSingleNode("rdf:Description");
							if (asRoot){
								rootElement = node.getParent();
								boolean b = rootElement.remove(node);
								if (b) {
									rootElement = rootElement.createCopy();
									asRoot = false;
								} else {
									log.error("ERROR on harvesting.");
									ok = false;
								}
							} 
							rootElement.add(node.detach());
						}
					}
				}
				if (records.getResumptionToken() != null) {
					log.debug("Harvest the next token.");
					ResumptionToken rt = records.getResumptionToken();
					Thread.sleep(1000);
					records = server.listRecords(rt);
				} else {
					more = false;
				}
			}
			log.debug("Harvesting is finish.");
		} catch (OAIException e) {
			log.error("ERROR: OAIException, caused by " + e.getMessage());
			return false;
		} catch (InterruptedException e) {
			log.error("ERROR: InterruptedException, caused by " + e.getMessage());
			return false;
		}
		doc.add(rootElement);
		 // lets write to a file
        ok = writeRdfDocumentToFile(doc);
        split.stop();
		return ok;
	}
	
	public void startUp(Map<String, String> vars) throws ActionException {
		params = vars;
		checkRequiredVariables();
		File file = new File(outputFile);
		removeOutputFileOnExist(file);
		
	}
	
	public Object execute(String path, Object object) throws ActionException {
		return harvest();
	}
	public void shutDown() throws ActionException {
		File file = new File(outputFile);
		String filename = file.getName();
		log.debug("Read cache from file and put in the cache service. Filename:  " + filename + "\tFile abspath: " + file.getAbsolutePath());
		try {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			cacheService.putByteArray(filename, bytes);
		} catch (IOException e) {
			log.error("FATAL ERROR: could not put the profile (filename: '" + filename + "') to the cache. Caused by IOException, msg: " + e.getMessage());
		}  
	}
	
	private void checkRequiredVariables() throws ActionException {
		this.oaipmhBaseURL = params.get("oaipmhBaseURL");
		this.prefix = params.get("prefix");
		this.set = params.get("set");
		this.outputFile = params.get("outputFile");
		
		if (oaipmhBaseURL == null || oaipmhBaseURL.isEmpty())
			throw new ActionException("xsltSource is null or empty");
		if (prefix == null || prefix.isEmpty())
			throw new ActionException("profilesCacheDir is null or empty");
		if (set == null || set.isEmpty())
			throw new ActionException("outputFile is null or empty");
		if (outputFile == null || outputFile.isEmpty())
			throw new ActionException("outputFile is null or empty");
	}
	private void removeOutputFileOnExist(File file) {
		if (file.exists()) {
			log.debug(outputFile + "is exists.");
			log.debug("Deleting file...");
			boolean ok = file.delete();
			if (ok)
				log.debug(outputFile + " is deleted.");
			else
				log.error("Cannot delete the " + outputFile + " file.");
		}
	}
	private boolean writeRdfDocumentToFile(Document doc) {
		XMLWriter writer;
		try {
			writer = new XMLWriter(
			    new FileWriter( outputFile )
			);
			log.debug("Writing rdf file to " + outputFile);
			writer.write( doc );
			writer.close();
		} catch (IOException e) {
			log.error("ERROR: IOException, caused by " + e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String name() {
		return this.getClass().getName();
	}
}
