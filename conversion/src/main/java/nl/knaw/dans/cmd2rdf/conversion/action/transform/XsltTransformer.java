package nl.knaw.dans.cmd2rdf.conversion.action.transform;

import java.io.File;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;
import nl.knaw.dans.cmd2rdf.conversion.util.ClarinProfileResolver;

import org.apache.directmemory.cache.CacheService;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
/**
 * @author Eko Indarto
 *
 */
public class XsltTransformer implements IAction{
    /** 
     * Simple transformation method. 
     * @param sourcePath - Absolute path to source xml file. 
     * @param xsltPath - Absolute path to xslt file. 
     * @param resultDir - Directory where you want to put resulting files. 
     */  
	private static final Logger errLog = LoggerFactory.getLogger("errorlog");
	private static final Logger log = LoggerFactory.getLogger(XsltTransformer.class);
	private static CacheService<Object, Object> cacheService;
	private Templates cachedXSLT;
	private String xsltSource;
	private String profilesCacheDir;
	private String registry;
	private Map<String, String> params;
		
	public XsltTransformer(CacheService<Object, Object> cacheService){
		XsltTransformer.cacheService = cacheService;
		log.debug("++++++++++++XsltTransformer.cacheService.entries: " + XsltTransformer.cacheService.entries());
	}
	
	public void startUp(Map<String, String> vars)
			throws ActionException {
		params = vars;
		checkRequiredVariables();
		//startUpCacheService();
		TransformerFactory transFact = new net.sf.saxon.TransformerFactoryImpl();
		Source src = new StreamSource(xsltSource);
		try {
			this.cachedXSLT = transFact.newTemplates(src);
		} catch (TransformerConfigurationException e) {
			log.error("ERROR: TransformerConfigurationException, caused by: " + e.getMessage());
		}
	}

	private void checkRequiredVariables() throws ActionException {
		this.xsltSource = params.get("xsltSource");
		this.profilesCacheDir = params.get("profilesCacheDir");
		this.registry = params.get("registry");
		if (xsltSource == null || xsltSource.isEmpty())
			throw new ActionException("xsltSource is null or empty");
		if (profilesCacheDir == null || profilesCacheDir.isEmpty())
			throw new ActionException("profilesCacheDir is null or empty");
//		if (registry == null || registry.isEmpty())
//			throw new ActionException("registry is null or empty");
	}
	
	public Object execute(String p,Object o) throws ActionException {
		Source input = null;
		DOMResult output = null;
		// prepare input
		if (o instanceof File) {
			File file = (File)o;
			log.debug("Converting '" + file.getAbsolutePath() + "'." );
			input = new StreamSource(file);
		} else if (o instanceof Node) {
			Node node = (Node)o;
			input = new DOMSource(node);
		} else
			throw new ActionException("Unknown input ("+p+", "+o+")");
		Split split = null;
		try {
			
			if (xsltSource.endsWith(".xsl"))
				split = SimonManager.getStopwatch("stopwatch.trans1").start();
			else 
				split = SimonManager.getStopwatch("stopwatch.trans2").start();
			URIResolver resolver = (URIResolver) new ClarinProfileResolver(profilesCacheDir, registry, cacheService);
			Transformer transformer = cachedXSLT.newTransformer();	
			transformer.setURIResolver(resolver);
			// set parameters
			for (String param : params.keySet()) {
				transformer.setParameter(param, params.get(param));
			}
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			long start = System.currentTimeMillis();
			output = new DOMResult();
			transformer.transform(input,  
					 output);
			long end = System.currentTimeMillis();
			log.info("Duration of transformation " + ((end-start)) + " milliseconds");
			return output.getNode();
		} catch (TransformerConfigurationException e) {
			errLog.error("ERROR: TransformerConfigurationException, caused by: " + e.getCause(), e);
		} catch (TransformerException e) {
			errLog.error("ERROR: TransformerException, caused by: " + e.getCause(), e);
		} catch (ActionException e) {
			errLog.error("ERROR: ConverterException, caused by: " + e.getCause(), e);
		} finally {
			if (split != null)
				split.stop();
		}
		return false;    
    }     

	public void shutDown() throws ActionException {
	}

	@Override
	public String name() {
		
		return this.getClass().getName();
	}
}  