package nl.knaw.dans.cmd2rdf.conversion.action.store;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import nl.knaw.dans.cmd2rdf.conversion.action.ActionException;
import nl.knaw.dans.cmd2rdf.conversion.action.IAction;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eko Indarto
 *
 */
public class RemoveDirectory implements IAction{
	private static final Logger log = LoggerFactory.getLogger(RemoveDirectory.class);
	private static final Logger errLog = LoggerFactory.getLogger("errorlog");
	private String directoryToRemove;

	public RemoveDirectory(){
	}

	public void startUp(Map<String, String> vars)
			throws ActionException {
		directoryToRemove = vars.get("directoryToRemove");
		if (directoryToRemove == null || directoryToRemove.isEmpty())
			throw new ActionException("directoryToRemove is null or empty");
	}

	public Object execute(String path, Object object) throws ActionException {
		log.debug("Deleting directory" + directoryToRemove);
		if (object != null && (Boolean) object) {
			File file = new File(directoryToRemove);
			if (!file.exists() || !file.isDirectory())
				errLog.error("ERROR: >>>>> " + directoryToRemove
						+ " doesn't exist.");

			try {
				FileUtils.deleteDirectory(file);
				// if (false)
				// errLog.error("FATAL ERROR: Deleting '" + directoryToRemove +
				// "' is failed.");
				// else
				// log.debug(directoryToRemove + " is deleted.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			errLog.error("========= VIRTUOSO ERROR");
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
