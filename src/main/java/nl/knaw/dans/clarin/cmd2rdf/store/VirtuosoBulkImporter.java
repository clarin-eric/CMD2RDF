package nl.knaw.dans.clarin.cmd2rdf.store;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import nl.knaw.dans.clarin.cmd2rdf.exception.ActionException;
import nl.knaw.dans.clarin.cmd2rdf.mt.IAction;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eko Indarto
 *
 */
public class VirtuosoBulkImporter implements IAction{
	private static final Logger errLog = LoggerFactory.getLogger("errorlog");
	private static final Logger log = LoggerFactory.getLogger(VirtuosoBulkImporter.class);
	private static final String VIRTUOSO_BULK_IMPORT_SH = "/virtuoso_bulk_import.sh";
	private String[] virtuosoBulkImport;
	public VirtuosoBulkImporter(){
	}

	public void startUp(Map<String, String> vars)
			throws ActionException {
		String bulkImportShellPath = vars.get("bulkImportShellPath");
		String virtuosoHomeDir = vars.get("virtuosoHomeDir");
		String port = vars.get("port");
		String username = vars.get("username");
		String password = vars.get("password");
		String rdfDir = vars.get("rdfDir");
		if (bulkImportShellPath == null || bulkImportShellPath.isEmpty())
			throw new ActionException("bulkImportShellPath is null or empty");
		if (virtuosoHomeDir == null || virtuosoHomeDir.isEmpty())
			throw new ActionException("virtuosoHomeDir is null or empty");
		if (port == null || port.isEmpty())
			throw new ActionException("port is null or empty");
		if (username == null || username.isEmpty())
			throw new ActionException("username is null or empty");
		if (rdfDir == null || rdfDir.isEmpty())
			throw new ActionException("rdfDir is null or empty");
		//"/data/cmdi2rdf/virtuoso/bin/isql 1111  dba dba exec="ld_dir_all('/data/cmdi2rdf/BIG-files/rdf-output/','*.rdf','http://eko.indarto/tst.rdf');"
		
		virtuosoBulkImport = new String[]{bulkImportShellPath + VIRTUOSO_BULK_IMPORT_SH, virtuosoHomeDir, port, username, password, rdfDir};
	}

	public Object execute(String path, Object object) throws ActionException {
		Split split = SimonManager.getStopwatch("stopwatch.bulkimport").start();
		boolean status = excuteBulkImport();
		split.stop();
		if (!status) {
			errLog.debug("FATAL ERROR, THE BULK IMPORT IS FAILED ---> SYSTEM TERMINATED.");
			System.exit(1);
		}
		return status;
	}


private boolean excuteBulkImport() throws ActionException {
	boolean ok=false;
		log.debug("######## START EXCUTING BULK IMPORT ###############");
		for (String s:virtuosoBulkImport)
			log.debug("BULK COMMAND: " + s);
		
		long start = System.currentTimeMillis();
		
		ok = executeIsql(virtuosoBulkImport);
		
		if (!ok)
			errLog.error("ERROR>>>>> BULK IMPORT EXECUTION IS FAILED");
		
		long duration = System.currentTimeMillis() - start;
		Period p = new Period(duration);
		log.debug("######## END BULK IMPORT ###############");
		log.debug("DURATION: " + p.getHours() + " hours, " + p.getMinutes() + " minutes, " + p.getSeconds() + " secs, " + p.getMillis() + " msec.");
		return ok;
	}

private boolean executeIsql(String[] args) throws ActionException {
	boolean ok = false;
	StringBuffer output = new StringBuffer();
	Process process;
	try {
		process = Runtime.getRuntime().exec(args);
		while (process.waitFor() != 0) {
			log.debug("process...");
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";			
		while ((line = reader.readLine())!= null) {
			output.append(line + "\n");
		}
		String outputstr = output.toString();
		log.debug(outputstr);
		ok = outputstr.contains("Done.") && outputstr.contains("msec.");
 
	} catch (Exception e) {
		errLog.error("ERROR: " + e.getMessage(), e);
		throw new ActionException("ERROR: " + e.getMessage());
	}
	return ok;
}
	public void shutDown() throws ActionException {
	}

	@Override
	public String name() {
		
		return this.getClass().getName();
	}
}
