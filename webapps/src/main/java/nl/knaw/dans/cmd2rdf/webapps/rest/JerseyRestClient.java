/**
 * 
 */
package nl.knaw.dans.cmd2rdf.webapps.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;

import nl.knaw.dans.cmd2rdf.webapps.ui.secure.Cmd2RdfSecureApplication;
import nl.knaw.dans.cmd2rdf.webapps.util.Misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akmi
 *
 */
public abstract class JerseyRestClient {
	private static final Logger LOG = LoggerFactory.getLogger(JerseyRestClient.class);
	protected static final String VIRTUOSO_HOST = Cmd2RdfSecureApplication.cofigReader.getTripleStoreServerHost();
	protected static final String VIRTUOSO_USERNAME = Cmd2RdfSecureApplication.cofigReader.getTripleStoreUsername();
	protected static final String VIRTUOSO_PASSWORD = Cmd2RdfSecureApplication.cofigReader.getTripleStorePassword();
	protected UriBuilder uriBuilder;
	
	protected Client client;
	
	public JerseyRestClient(){
		
		try {
			uriBuilder = UriBuilder.fromUri(new URI(VIRTUOSO_HOST));
		} catch (URISyntaxException e) {
			LOG.error(e.getMessage());
		}
	}
	
	protected void register(Object object) {
		client = ClientBuilder.newClient().register(object);
	}
	
}
