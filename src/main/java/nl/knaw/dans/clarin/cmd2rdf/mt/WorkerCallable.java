package nl.knaw.dans.clarin.cmd2rdf.mt;

/**
 * @author Eko Indarto
 *
 */

import java.io.File;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.Callable;

import nl.knaw.dans.clarin.cmd2rdf.exception.ActionException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerCallable implements Callable<String> {

	private static final Logger log = LoggerFactory.getLogger(WorkerCallable.class);
    private String path;
	private List<IAction> actions;
	private int i;
	

    public WorkerCallable(String path, List<IAction> actions, int i){
        this.path = path;
        this.actions = actions;
        this.i = i;
    }
    
    private String executeActions(String path) throws ActionException {
    	StringBuffer sb = new StringBuffer();
		File file = new File(path);
		if (file.exists()) {
			log.debug("Number of file [" + i + "]: " + file.getName() + " has size of " + file.length() + " bytes (" + FileUtils.byteCountToDisplaySize(BigInteger.valueOf(file.length())) +" ).");
			Object object = file;
			//Do conversion
			for(IAction action : actions) {
				object = action.execute(path,object);
				sb.append("[" + i + "]" + action.name() + " is done.\n");
			}
		} else {
			sb.append("ERROR: '" + path + "' does not exist.");
			log.error("ERROR: '" + path + "' does not exist.");
		}
		return sb.toString();
    }

	@Override
	public String call() throws Exception {
		String msg = "";
		log.debug("Run worker for '" + path + "'.");
        try {
			msg = executeActions(path);
		} catch (ActionException e) {
			log.error("ERROR WorkerThread, caused by " + e.getMessage());
		}
		return msg;
	}

}
