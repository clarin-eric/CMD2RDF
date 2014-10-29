/**
 * 
 */
package nl.knaw.dans.cmd2rdf.conversion.action.db;

import java.util.Map;

import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.ActionStatus;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;
import nl.knaw.dans.cmd2rdf.conversion.db.ChecksumDb;
import nl.knaw.dans.cmd2rdf.conversion.util.Misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eko Indarto
 *
 */
public class MD5ChecksumRecordStatus implements IAction {
	private static final Logger log = LoggerFactory.getLogger(MD5ChecksumRecordStatus.class);
	private Map<String, String> params;
	private String urlDB;
	private ActionStatus act;
	private String status;
	ChecksumDb db;

	/* (non-Javadoc)
	 * @see nl.knaw.dans.clarin.cmd2rdf.mt.IAction#startUp(java.util.Map)
	 */
	public void startUp(Map<String, String> vars) throws ActionException {
		params = vars;
		checkRequiredVariables();
		db = new ChecksumDb(urlDB);
		
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.clarin.cmd2rdf.mt.IAction#execute(java.lang.String, java.lang.Object)
	 */
	public Object execute(String path, Object object) throws ActionException {
		log.debug("execute, action name: " + act.name());
		log.debug(act.name() + " status of '" + path + "' to " + status);
		if (object instanceof Boolean && !(Boolean)object) {
			log.debug("SKIP updating the " + path);
			db.updateActionStatusByRecord(path, ActionStatus.ERROR);
		} else  {
			db.updateActionStatusByRecord(path, act);
		}
			
		return null;
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.clarin.cmd2rdf.mt.IAction#shutDown()
	 */
	public void shutDown() throws ActionException {
		db.closeDbConnection();

	}
	
	private void checkRequiredVariables() throws ActionException {
		urlDB = params.get("urlDB");
		String action = params.get("action");
		status = params.get("status");
		if (urlDB == null || urlDB.isEmpty())
			throw new ActionException("urlDB is null or empty");
		if (action == null || action.isEmpty())
			throw new ActionException("action is null or empty");
		if (status == null || status.isEmpty())
			throw new ActionException("status is null or empty");
		act = Misc.convertToActionStatus(action);
	}
	
	@Override
	public String name() {
		
		return this.getClass().getName();
	}
}
