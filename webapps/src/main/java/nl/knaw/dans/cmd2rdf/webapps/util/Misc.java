/**
 * 
 */
package nl.knaw.dans.cmd2rdf.webapps.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akmi
 *
 */
public class Misc {
	
	private static final Logger LOG = LoggerFactory.getLogger(Misc.class);
	
	public static String getEnvValue(String envName) {
		return getEnvValue(envName, "");
	}
	public static String getEnvValue(String envName, String defaultVal) {
		String val = defaultVal;
		try {
			javax.naming.Context env = (javax.naming.Context)new InitialContext().lookup("java:comp/env");
			val = (String)env.lookup(envName);
		} catch (NamingException e) {
			LOG.error(e.getMessage());
		}
		return val;
	}
}
