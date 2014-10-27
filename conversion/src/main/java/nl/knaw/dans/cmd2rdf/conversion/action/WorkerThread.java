package nl.knaw.dans.cmd2rdf.conversion.action;

/**
 * @author Eko Indarto
 *
 */

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerThread implements Runnable {

	private static final Logger log = LoggerFactory.getLogger(WorkerThread.class);
    private String path;
	private List<IAction> actions;
	private static int n;
	
	

    public WorkerThread(String path, List<IAction> actions){
        this.path = path;
        this.actions = actions;
    }
    
	public void run() {
    	
    	log.debug("Run worker for '" + path + "'.");
        try {
			executeActions(path);
		} catch (ActionException e) {
			log.error("ERROR WorkerThread, caused by " + e.getMessage());
		}

    }
    
    private void executeActions(String path) throws ActionException {
			File file = new File(path);
			if (file.exists()) {
				n++;
				log.debug("Number of file [" + n + "]: " + file.getName() + " has size of " + file.length() + " bytes (" + (file.length()/1024) + " MB).");
				Object object = file;
				//Do conversion
				for(IAction action : actions) {
					object = action.execute(path,object);
				}
			} else {
				log.error("ERROR: '" + path + "' does not exist.");
			}

    }

}
