package nl.knaw.dans.cmd2rdf.conversion.action.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;

import org.apache.commons.io.FileUtils;
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
	private static final Logger ERROR_LOG = LoggerFactory.getLogger("errorlog");
	private static final Logger log = LoggerFactory.getLogger(VirtuosoBulkImporter.class);
	private static String VIRTUOSO_BULK_IMPORT_SH;
	private boolean skip;
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
			throw new ActionException(this.name() + ": bulkImportShellPath is null or empty");
		else {
			VIRTUOSO_BULK_IMPORT_SH = bulkImportShellPath.trim();
			//Check whether the virtuoso_bulk_import.sh is executable or not.
			File file = new File(VIRTUOSO_BULK_IMPORT_SH);
			if (!file.exists() || !file.isFile())
				throw new ActionException(this.name() + "'" + VIRTUOSO_BULK_IMPORT_SH + "' doesn't exist or not a file.");
			if (!file.canExecute())
				throw new ActionException(this.name() + "'" + VIRTUOSO_BULK_IMPORT_SH + "' is not executeable file. Try: chmod a+x to the file.");
		}
		if (virtuosoHomeDir == null || virtuosoHomeDir.isEmpty())
			throw new ActionException(this.name() + ": virtuosoHomeDir is null or empty");
		if (port == null || port.isEmpty())
			throw new ActionException(this.name() + ": port is null or empty");
		if (username == null || username.isEmpty())
			throw new ActionException(this.name() + ": username is null or empty");
		if (rdfDir == null || rdfDir.isEmpty())
			throw new ActionException(this.name() + ": rdfDir is null or empty");
		
		File file = new File(rdfDir);
		if (!file.exists() || !file.isDirectory()) {
			skip=true;
			ERROR_LOG.error("Directory '" + rdfDir + "' doen't exist.");
		} 
		
		//"/data/cmdi2rdf/virtuoso/bin/isql 1111  dba dba exec="ld_dir_all('/data/cmdi2rdf/BIG-files/rdf-output/','*.rdf','http://eko.indarto/tst.rdf');"
		
		if(!skip)
			virtuosoBulkImport = new String[]{VIRTUOSO_BULK_IMPORT_SH, virtuosoHomeDir, port, username, password, rdfDir};
	}

	public Object execute(String path, Object object) throws ActionException {
		if (!skip) {
			Split split = SimonManager.getStopwatch("stopwatch.bulkimport").start();
			boolean status = excuteBulkImport();
			split.stop();
			if (!status) {
				ERROR_LOG.error("FATAL ERROR, THE BULK IMPORT IS FAILED ---> SYSTEM TERMINATED.");
				System.exit(1);
			}
			return status;
		} 
		return skip;
	}


private boolean excuteBulkImport() throws ActionException {
	boolean ok=false;
		log.info("######## START EXCUTING BULK IMPORT ###############");
		for (String s:virtuosoBulkImport)
			log.info("BULK COMMAND: " + s);
		
		long start = System.currentTimeMillis();
		Collection<File> cf = FileUtils.listFiles(new File(virtuosoBulkImport[5]), new String[]{"rdf", "graph"}, true);
		log.info("============= Trying to import '" + cf.size() + "' files.");
		ok = executeIsql(virtuosoBulkImport);
		
		if (!ok)
			ERROR_LOG.error("ERROR>>>>> BULK IMPORT EXECUTION IS FAILED");
		
		long duration = System.currentTimeMillis() - start;
		Period p = new Period(duration);
		log.info("######## END BULK IMPORT ###############");
		log.info("DURATION: " + p.getHours() + " hours, " + p.getMinutes() + " minutes, " + p.getSeconds() + " secs, " + p.getMillis() + " msec.");
		return ok;
	}

private boolean executeIsql(String[] args) throws ActionException {
	boolean ok = false;
	StringBuffer output = new StringBuffer();
	Process process;
	try {
		process = Runtime.getRuntime().exec(args);
		while (process.waitFor() != 0) {
			log.info("process...");
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";			
		while ((line = reader.readLine())!= null) {
			output.append(line + "\n");
		}
		String outputstr = output.toString();
		log.info(outputstr);
		ok = outputstr.contains("Done.") && outputstr.contains("msec.");
 
	} catch (Exception e) {
		ERROR_LOG.error("ERROR: " + e.getMessage(), e);
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
