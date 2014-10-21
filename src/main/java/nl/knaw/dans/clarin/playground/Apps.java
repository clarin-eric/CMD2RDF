package nl.knaw.dans.clarin.playground;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Apps {
	private static final Logger log = LoggerFactory.getLogger(Apps.class);
	public static void main(String[] args) {
		log.debug("---Begin---");
		DateTime start1 = new DateTime();
		int x=0;
		Iterator<File> iter = FileUtils.iterateFiles(new File(args[0]),new String[] {"xml"}, true);
		while (iter.hasNext()) {
			File f = iter.next();
			x++;
			 
		}
		
		DateTime end1 = new DateTime();
		Period duration1 = new Period(start1, end1);
    	log.info("Number of xml files: " + x);
    	int p1 = duration1.getMillis();
    	log.info("duration in Milliseocns: " + p1);
		DateTime start2 = new DateTime();
		
		Collection<File> listFiles = FileUtils.listFiles(new File(args[0]),new String[] {"xml"}, true);
		
		DateTime end2 = new DateTime();
		Period duration2 = new Period(start2, end2);
    	log.info("listFiles size: " + listFiles.size());
    	int p2 = duration2.getMillis();
    	log.info("duration in Milliseocns: " + p2);
    	log.info("Diff (Collection - Iteration): " + (p2-p1));		
    	
    	DateTime start3 = new DateTime();
    	int y=0;
    	for (File f:listFiles) {
    		log.debug(f.getAbsolutePath());
    	}
    	
    	DateTime end3 = new DateTime();
    	Period duration3 = new Period(start3, end3);
    	log.info("y files : " + y);
    	int p3= duration3.getMillis();
    	log.info("duration in Milliseconds: " + p3);;
    	
    	log.info("Diff (For Loop - Iteration): " + (p3-p1));	
    	
    	
    	Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp;
        Serializer out = new Serializer();
//		try {
//           exp = comp.compile(new StreamSource(new File("/Users/akmi/git/cmd2rdf/src/main/resources/xsl/CMDRecord2RDF.xsl")));
//            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File("/Users/akmi/Dropbox/DANS/IN_PROGRESS/CMDI2RDF-Workspace/data/cmd-xml/oai_SinicaCorpus_sinica_edu_tw_SinicaCorpus.xml")));
//            URIResolver resolver = (URIResolver) new ClarinProfileResolver("tmp-cache");
//            out.setOutputProperty(Serializer.Property.METHOD, "xml");
//            out.setOutputProperty(Serializer.Property.INDENT, "yes");
//            out.setOutputFile(new File("/Users/akmi/eko99-1/output/oai_SinicaCorpus_sinica_edu_tw_SinicaCorpus1.rdf"));
//            XsltTransformer trans = exp.load();
//            trans.setInitialContextNode(source);
//            trans.setParameter(new QName("base"), new XdmAtomicValue("http://localhost:8081/DAV/oai_SinicaCorpus_sinica_edu_tw_SinicaCorpus.rdf"));
//            trans.setURIResolver(resolver);
//            trans.setDestination(out);
//            trans.transform();
//		} catch (SaxonApiException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ConverterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
	}
}
