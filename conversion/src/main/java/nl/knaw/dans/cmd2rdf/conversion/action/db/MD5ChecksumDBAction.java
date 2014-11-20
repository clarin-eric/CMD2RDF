package nl.knaw.dans.cmd2rdf.conversion.action.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.ActionStatus;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;
import nl.knaw.dans.cmd2rdf.conversion.db.ChecksumDb;
import nl.knaw.dans.cmd2rdf.conversion.util.Misc;

import org.apache.commons.io.FileUtils;
import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5ChecksumDBAction implements IAction {
	private static final Logger log = LoggerFactory.getLogger(MD5ChecksumDBAction.class);
	private ChecksumDb db;
	private String urlDB;
    private String xmlSourceDir;
	private ActionStatus act;
	
	public void startUp(Map<String, String> vars) throws ActionException {
		urlDB = vars.get("urlDB");
		xmlSourceDir = vars.get("xmlSourceDir");
		String action = vars.get("action");
		
		if (urlDB == null || urlDB.isEmpty())
			throw new ActionException("urlDB is null or empty");
		if (action == null || action.isEmpty())
			throw new ActionException("action is null or empty");
		db = new ChecksumDb(urlDB);
		act = Misc.convertToActionStatus(action);
		
		log.debug("startUp MD5ChecksumDBAction");
		log.debug("urlDB: " + urlDB);
		log.debug("xmlSourceDir: " + xmlSourceDir);
	}

	public Object execute(String path, Object object) throws ActionException {
		log.debug("excute, action name: " + act.name());
		
		switch(act){
			case CHECKSUM_DIFF: checksumDiff();
				break;
			case DELETE: updateAllDoneToDeleteStatus();
				break;
			case CLEANUP: deleteAllRecordsWithStatusPurge();
				break;
			default:
		}		
		
		return null;
	}
	
	private void deleteAllRecordsWithStatusPurge() {
		log.info("Delete all records those have status 'PURGE'");
		db.deleteActionStatus(ActionStatus.PURGE);
	}

	private void updateAllDoneToDeleteStatus() {
		log.info("Update all records those have status 'DONE' to status 'DELETE'");
		db.updateStatusOfDoneStatus(ActionStatus.DELETE);
	}

	private void checksumDiff() throws ActionException {
		Split split = SimonManager.getStopwatch("stopwatch.db").start();
		if (xmlSourceDir == null || xmlSourceDir.isEmpty())
			throw new ActionException("xmlSourceDir is null or empty");
		
		log.debug("Process checksum diff. xmlSourceDir: " + xmlSourceDir);
		String xmlSourcesDirs[] = xmlSourceDir.split(",");
		Collection<File> allFiles = new ArrayList<File>();
		for (String xmlSrcDir:xmlSourcesDirs) {
			String xmlFileSrcDir = xmlSrcDir.trim();
			if (!xmlFileSrcDir.isEmpty()) {
				Collection<File> files = FileUtils.listFiles(new File(xmlFileSrcDir),new String[] {"xml"}, true);
				allFiles.addAll(files);
			}
		}
		split.stop();
		log.info("===== Number of files TOTAL FILES : " + allFiles.size());
		
		try {
			log.debug("==================================");
			log.debug("Number of records before process: " + db.getTotalNumberOfRecords());
			db.process(xmlSourceDir, allFiles);
			log.debug("Number of records after process: " + db.getTotalNumberOfRecords());
			log.info("==================================");
			log.info("\n");
			log.info("---------- Counter reports ---------");
			log.info("Total processed records: " + ChecksumDb.getnRecords());
			log.info("Total inserted records: " + ChecksumDb.getnInsert());
			log.info("Total updated records: " + ChecksumDb.getnUpdate());
			log.info("Total skipped records: " + ChecksumDb.getnSkip());
			log.info("Total Query DURATION: " + ChecksumDb.getTotalQueryDuration() + " milliseconds");
			log.info("Total MD5 HASHING DURATION: " + ChecksumDb.getTotalMD5GeneratedTime() + " milliseconds");
			log.info("Total DB PROCESSING DURATION: " + ChecksumDb.getTotalDbProcessingTime() + " milliseconds");
			log.info("----------------------------------");
			log.info("\n");
			log.info("============= DB QUERY Reports ===============");
			log.info("Records with status NEW: " + db.getTotalNumberOfNewRecords());
			log.info("Records with status UPDATE: " + db.getTotalNumberOfUpdatedRecords());
			log.info("Records with status DONE: " + db.getTotalNumberOfDoneRecords());
			log.info("Records with status NONE: " + db.getTotalNumberOfNoneRecords());
			log.info("Records with status DELETE: " + db.getTotalNumberOfDeleteRecords());
			log.info("==================================");
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutDown() throws ActionException {
		if(act == ActionStatus.CLEANUP)
			db.shutdown();
		else
			db.closeDbConnection();
	}

	@Override
	public String name() {
		
		return this.getClass().getName();
	}
}
