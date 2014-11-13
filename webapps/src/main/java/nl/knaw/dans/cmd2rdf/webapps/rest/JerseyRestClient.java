/**
 * 
 */
package nl.knaw.dans.cmd2rdf.webapps.rest;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author akmi
 *
 */
public abstract class JerseyRestClient {
	protected static final String VIRTUOSO_HOST = virtuosoHost();
	private static String virtuosoHost() {
		String virtuosoHost = "http://localhost:8890";

		try {
			javax.naming.Context env = (javax.naming.Context)new InitialContext().lookup("java:comp/env");
			// Get a single value
			virtuosoHost = (String)env.lookup("virtuoso_host");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return virtuosoHost;
		
	}
}
