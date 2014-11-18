package nl.knaw.dans.cmd2rdf.conversion.action.store;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;

import org.apache.commons.io.FileUtils;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * @author Eko Indarto
 *
 */
public class TeeAction implements IAction{
	private static final Logger log = LoggerFactory.getLogger(TeeAction.class);
	private static final Logger errLog = LoggerFactory.getLogger("errorlog");
	private String teeOutputDir;
	private List<String> replacedPrefixBaseURI = new ArrayList<String>();
	private String prefixBaseURI;

	public TeeAction(){
	}

	public void startUp(Map<String, String> vars)
			throws ActionException {
		teeOutputDir = vars.get("teeOutputDir");
		String replacedPrefixBaseURIVar = vars.get("replacedPrefixBaseURI");
		prefixBaseURI = vars.get("prefixBaseURI");
		if (replacedPrefixBaseURIVar == null || replacedPrefixBaseURIVar.isEmpty())
			throw new ActionException("replacedPrefixBaseURI is null or empty");
		if (prefixBaseURI == null || prefixBaseURI.isEmpty())
			throw new ActionException("prefixBaseURI is null or empty");
		if (teeOutputDir == null || teeOutputDir.isEmpty())
			throw new ActionException("teeOutputDir is null or empty");
		String replacedPrefixBaseURIVars[] = replacedPrefixBaseURIVar.split(",");
		for (String s:replacedPrefixBaseURIVars) {
			if (!s.trim().isEmpty())
				replacedPrefixBaseURI.add(s.trim());
		}
		File tee = new File(teeOutputDir);
		if (!tee.exists())
                    try {
			FileUtils.forceMkdir(tee);
                    } catch(IOException e) {
                        throw new ActionException(e.getMessage());
                    }
		if (!tee.isDirectory())
			throw new ActionException("teeOutputDir["+teeOutputDir+"] is not a directory");
		log.debug("Save the tee files to " + teeOutputDir);
	}

	public Object execute(String path, Object object) throws ActionException {
		Split split = SimonManager.getStopwatch("stopwatch.teeaction").start();
		boolean status = saveXMLToFileSystem(path, object);
		split.stop();
		return object;
	}

	private String getGIRI(String path) throws ActionException {
		String gIRI = null;
		for (String s:replacedPrefixBaseURI) {
			if (path.startsWith(s)) {
				gIRI = path.replace(s, this.prefixBaseURI).replaceAll(" ", "_");
				break;
			}
		}
		if (gIRI==null)
			throw new ActionException("gIRI ERROR: " + path + " is not found as prefix in " + replacedPrefixBaseURI);
		return gIRI;
	}

private boolean saveXMLToFileSystem(String path, Object object)
		throws ActionException {
	if (object instanceof Node) {
		log.debug("Save '" + path + "'.");
		Node node = (Node)object;
		DOMSource source = new DOMSource(node);
		try {
			long l = System.currentTimeMillis();
			String gIRI = getGIRI(path);
			String teeFileOutputName = gIRI.replace(prefixBaseURI,  teeOutputDir);
			log.debug("Saving " + teeFileOutputName);
			File teeFile = new File(teeFileOutputName);
			TransformerFactory.newInstance().newTransformer().transform(source,new StreamResult(teeFile));
			long duration = (System.currentTimeMillis() - l );
			log.debug("Save duration: " + duration + " ms. Size: " + FileUtils.byteCountToDisplaySize(teeFile.length()));
			if (duration > 5000) {
				Period p = new Period(duration);
				log.debug("Saving took more than 4 seconds. It took " + p.getSeconds() + " secs.");
			}
			return true;
		} catch (TransformerConfigurationException e) {
			errLog.error("ERROR: TransformerConfigurationException, caused by " + e.getMessage(), e);
		} catch (TransformerException e) {
			errLog.error("ERROR: TransformerException, caused by " + e.getMessage(), e);
		} catch (TransformerFactoryConfigurationError e) {
			errLog.error("ERROR: TransformerFactoryConfigurationError, caused by " + e.getMessage(), e);
		}
	} else
		throw new ActionException("Unknown input ("+path+", "+object+")");
	return false;
	}
	

	public void shutDown() throws ActionException {
	}

	@Override
	public String name() {
		return this.getClass().getName();
	}
}
