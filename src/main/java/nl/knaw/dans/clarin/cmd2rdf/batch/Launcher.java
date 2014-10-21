package nl.knaw.dans.clarin.cmd2rdf.batch;

/**
 * @author Eko Indarto
 *
 */

import java.io.File;
import java.io.InputStream;

import org.easybatch.core.api.EasyBatchReport;
import org.easybatch.core.impl.EasyBatchEngine;
import org.easybatch.core.impl.EasyBatchEngineBuilder;
import org.easybatch.xml.XmlRecordMapper;
import org.easybatch.xml.XmlRecordReader;
import org.javasimon.Manager;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Launcher {
	private static final Logger log = LoggerFactory.getLogger(Launcher.class);
	private static volatile Stopwatch stopwatchTotal = SimonManager.getStopwatch("stopwatch.total");
	private static volatile Stopwatch stopwatchDb = SimonManager.getStopwatch("stopwatch.db");
	private static volatile Stopwatch stopwatchOai = SimonManager.getStopwatch("stopwatch.oai");
	private static volatile Stopwatch stopwatchTrans1 = SimonManager.getStopwatch("stopwatch.trans1");
	private static volatile Stopwatch stopwatchTrans2 = SimonManager.getStopwatch("stopwatch.trans2");
	private static volatile Stopwatch stopwatchFS = SimonManager.getStopwatch("stopwatch.virtuosoUpload");
	private static volatile Stopwatch stopwatchBI = SimonManager.getStopwatch("stopwatch.bulkimport");
	
    public static void main(String[] args) throws Exception {
    	
    	Split split = stopwatchTotal.start();
    	if (args == null || args.length !=1 
    			|| !(new File (args[0]).isFile())
    			|| !(new File (args[0])).getName().endsWith(".xml")) {
    		System.out.println("An XML configuration file is required.");
    		System.exit(1);
    	}
    	
    	ClassLoader classLoader = Thread.currentThread (). getContextClassLoader ();
    	InputStream inputStream = classLoader.getResourceAsStream ("logging.properties");
//    	java.util.logging.Logger  log = java.util.logging.LogManager.getLogManager().getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
//    	for (Handler h : log.getHandlers()) {
//    	    h.setLevel(Level.INFO);
//    	}
    	SLF4JBridgeHandler.removeHandlersForRootLogger();
    	java.util.logging.LogManager.getLogManager().readConfiguration(inputStream);
    	//SLF4JBridgeHandler.removeHandlersForRootLogger();
    	SLF4JBridgeHandler.install();
    	
    	
        // Build an easy batch engine
        EasyBatchEngine easyBatchEngine = new EasyBatchEngineBuilder()
                .registerRecordReader(new XmlRecordReader("CMD2RDF", new File(args[0])))
                .registerRecordMapper(new XmlRecordMapper<Jobs>(Jobs.class))
                .registerRecordProcessor(new JobProcessor())
                .build();

        
        // Run easy batch engine
        EasyBatchReport easyBatchReport = easyBatchEngine.call();
        split.stop();
       
        // Print the batch execution report
        log.info("Start time: " + easyBatchReport.getFormattedStartTime());
        log.info("End time: "+ easyBatchReport.getFormattedEndTime());
        Period p = new Period(easyBatchReport.getBatchDuration());
        log.info("Duration: " + p.getHours() + " hours, " 
        		+ p.getMinutes() + " minutes, " + p.getSeconds() + " seconds, " + p.getMillis() + " ms.");
        Period p2 = new Period(stopwatchTotal.getLastUsage()-stopwatchTotal.getFirstUsage());
        log.debug("Total: " + + p2.getHours() + " hours, " 
        		+ p2.getMinutes() + " minutes, " + p2.getSeconds() + " seconds, " + p2.getMillis() + " ms.");
        log.debug("stopwatchTotal: " + stopwatchTotal);
        log.debug("stopwatchDb: " + stopwatchDb);
        log.debug("stopwatchOai: " + stopwatchOai);
        log.debug("stopwatchTrans1: " + stopwatchTrans1);
        log.debug("stopwatchTrans2: " + stopwatchTrans2);
        log.debug("stopwatchFS: " + stopwatchFS);
        log.debug("stopwatchBI: " + stopwatchBI);

    }

}