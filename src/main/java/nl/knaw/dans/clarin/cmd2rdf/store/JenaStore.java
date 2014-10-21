package nl.knaw.dans.clarin.cmd2rdf.store;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.knaw.dans.clarin.cmd2rdf.exception.ActionException;
import nl.knaw.dans.clarin.cmd2rdf.mt.IAction;
import nl.knaw.dans.clarin.cmd2rdf.util.ActionStatus;
import nl.knaw.dans.clarin.cmd2rdf.util.Misc;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
//import virtuoso.jena.driver.VirtGraph;
//import virtuoso.jena.driver.VirtuosoUpdateFactory;
//import virtuoso.jena.driver.VirtuosoUpdateRequest;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * @author Eko Indarto
 *
 */
public class JenaStore implements IAction{
	private static final int MAX_LINE = 1000;
	private static final Logger log = LoggerFactory.getLogger(JenaStore.class);
	private List<String> replacedPrefixBaseURI = new ArrayList<String>();
	private String prefixBaseURI;
	private ActionStatus act;
	private static int n;
	//VirtGraph set = new VirtGraph ("jdbc:virtuoso://localhost:1111", "dba", "dba");
	Model model = ModelFactory.createDefaultModel();

	public JenaStore(){
	}

	public void startUp(Map<String, String> vars)
			throws ActionException {
		
		String replacedPrefixBaseURIVar = vars.get("replacedPrefixBaseURI");
		prefixBaseURI = vars.get("prefixBaseURI");
		String action = vars.get("action");
		
		if (replacedPrefixBaseURIVar == null || replacedPrefixBaseURIVar.isEmpty())
			throw new ActionException("replacedPrefixBaseURI is null or empty");
		if (prefixBaseURI == null || prefixBaseURI.isEmpty())
			throw new ActionException("prefixBaseURI is null or empty");
		
		
		String replacedPrefixBaseURIVars[] = replacedPrefixBaseURIVar.split(",");
		for (String s:replacedPrefixBaseURIVars) {
			if (!s.trim().isEmpty())
				replacedPrefixBaseURI.add(s);
		}
		
		act = Misc.convertToActionStatus(action);
	
		log.debug("VirtuosoClient variables: ");
		log.debug("replacedPrefixBaseURI: " + replacedPrefixBaseURI);
		log.debug("prefixBaseURI: " + prefixBaseURI);
		log.debug("action: " + action);
		log.debug("Start VirtuosoClient....");
		
	}

	public Object execute(String path, Object object) throws ActionException {
		boolean status = false;
		switch(act){
			case POST: status = uploadRdfToVirtuoso(path, object);
				break; 
			default:
		}

		return status;
	}


private String getGIRI(String path) throws ActionException {
	String gIRI = null;
	for (String s:replacedPrefixBaseURI) {
		if (path.startsWith(s)) {
			gIRI = path.replace(s, this.prefixBaseURI).replace(".xml", ".rdf").replaceAll(" ", "_");
			break;
		}
	}
	if (gIRI==null)
		throw new ActionException("gIRI ERROR: " + path + " is not found as prefix in " + replacedPrefixBaseURI);
	return gIRI;
}

private boolean uploadRdfToVirtuoso(String path, Object object)
		throws ActionException {
	if (object instanceof Node) {
		log.debug("Upload '" + path.replace(".xml", ".rdf") + "'.");
		Node node = (Node)object;
		DOMSource source = new DOMSource(node);
		 StringWriter outWriter = new StringWriter();
		 StreamResult result = new StreamResult( outWriter );
		try {
			long start = System.currentTimeMillis();
			TransformerFactory.newInstance().newTransformer().transform(source,result);
			StringBuffer sb = outWriter.getBuffer(); 
			byte[] bytes = sb.toString().getBytes();
			log.debug(">>> bytes length: " + bytes.length);
			InputStream inputStream = new ByteArrayInputStream(bytes);
			
			model.read(inputStream, null);
			inputStream.close();
				StringWriter outWriter2 = new StringWriter();
				synchronized(outWriter2){
			  RDFDataMgr.write(outWriter2, model, RDFFormat.NTRIPLES);
				}
              StringBuffer sb2 = outWriter2.getBuffer(); 
              //log.debug(sb2.toString());
              String[] sbb = sb2.toString().split("\n");
              int len = sbb.length;
              int x = Math.round(len/MAX_LINE);
              int y = len%MAX_LINE;
              int z = x*MAX_LINE;
              String str = getGIRI(path);
              System.out.println("=====len: " + len + "\tx: " + x + "\ty: " + y + "\tz: " + z + "\t(y+z): " + (y+z));
              if (x==0) {
            	  log.debug("path: " + path);
            	  log.debug(sb.toString());
            	  log.debug(sb2.toString());
              }
              sb.setLength(0);
              
              for (int i=0; i<x; i++) {
            	  StringBuffer sww = new StringBuffer("INSERT INTO GRAPH <" + str + "> {\n");
               	  for (int j=0; j<MAX_LINE; j++) {
               		sww.append(sbb[((i*MAX_LINE) + j)]) ;
               		sww.append("\n");
               	  }
               	  sww.append("}");
            	  //VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(sww.toString(), set);
            	  //vur.exec();
              }
              StringBuffer sww = new StringBuffer("INSERT IN GRAPH <" + str + "> {\n");
              
              if (y > 0) {
                  for (int k=z+1; k<z+y; k++) {
                	  sww.append(sbb[k]);
                  } 
              
	              sww.append("}");
	              //log.debug(sww.toString());
	              //VirtuosoUpdateRequest vur = VirtuosoUpdateFactory.create(sww.toString(), set);
	        	  //vur.exec();   
	          }
              n++;
              
			  log.debug("[" + n + "] is CREATED. Duration: " + (System.currentTimeMillis() - start) + " milliseconds.");
              log.debug(">>>>Upload duration of " + str + " is " + (System.currentTimeMillis() - start) + " millis.");
              ;
		} catch (TransformerConfigurationException e) {
			log.error("ERROR: TransformerConfigurationException, caused by " + e.getMessage());
		} catch (TransformerException e) {
			log.error("ERROR: TransformerException, caused by " + e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			log.error("ERROR: TransformerFactoryConfigurationError, caused by " + e.getMessage());
		} catch (IOException e) {
			log.error("ERROR: IOException, caused by " + e.getMessage());
		}
		
	} else
		throw new ActionException("Unknown input ("+path+", "+object+")");
	return false;
	}
	
	@Override
	public void shutDown() throws ActionException {
	}
	
	@Override
	public String name() {
		
		return this.getClass().getName();
	}
}
