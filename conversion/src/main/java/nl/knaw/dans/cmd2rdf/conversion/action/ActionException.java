/**
 * 
 */
package nl.knaw.dans.cmd2rdf.conversion.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eko Indarto
 *
 */
public class ActionException extends Exception {
	private static final Logger ERROR_LOG = LoggerFactory.getLogger("errorlog");
	public ActionException(String message) {
		super(message);
		ERROR_LOG.error(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4960231011637536603L;

}
