/**
 * 
 */
package nl.knaw.dans.cmd2rdf.webapps.rest.sparql;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.cmd2rdf.webapps.rest.JerseyRestClient;
import nl.knaw.dans.cmd2rdf.webapps.rest.IQuery;

/**
 * @author akmi
 *
 */
@Path("/")
public class SparqlQuery extends JerseyRestClient implements IQuery {
	private static final Logger log = LoggerFactory
			.getLogger(SparqlQuery.class);

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 * 
	 * example: curl -G "http://localhost:8080/cmd2rdf/sparql/info"
	 *
	 * @return 'Hello GET!' (String) that will be returned as a text/plain
	 *         response.
	 */
	@Override
	public String sayPlainTextHelloGet() {
		return "Hello GET!";
	}

	/**
	 * Method handling HTTP POST requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 * 
	 * example: curl "http://localhost:8080/cmd2rdf/sparql/info" -d ""
	 *
	 * @return 'Hello POST!' (String) that will be returned as a text/plain
	 *         response.
	 */
	@Override
	public String sayPlainTextHelloPost() {
		return "Hello POST!\n";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.knaw.dans.cmd2rdf.webapps.rest.RestQuery#localTripleStoreGETRequest
	 * (javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.UriInfo)
	 * 
	 * Example:
	 * curl -G "http://localhost:8080/sparql" --data-urlencode "query=SELECT count(*) {?s ?p ?o}"
	 * 
	 * http://localhost:8080/cmd2rdf/sparql?
	 * query=select+distinct+%3FConcept+where+%7B%5B%5D+a+%3FConcept%7D+LIMIT+100&format=application%2Fjson
	 */
	@Override
	public Response localTripleStoreGETRequest(HttpHeaders headers,
			UriInfo uriInfo) {
		String query = uriInfo.getRequestUri().getQuery();
		try {
			return getSparqlGetQueryResult(query);
		} catch (IOException | URISyntaxException e) {
			log.error("ERROR: " + e.getMessage());
		}
		return Response.status(400).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.knaw.dans.cmd2rdf.webapps.rest.RestQuery#localTripleStorePOSTRequest
	 * (javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.MultivaluedMap)
	 * 
	 * Example:
	 * curl -v "http://localhost:8080/cmd2rdf/sparql" -d "query=SELECT count(*) {?s ?p ?o}" -H "Accept: application/rdf+xml"
	 * curl -v "http://localhost:8080/cmd2rdf/sparql" -d "query=SELECT count(*) {?s ?p ?o}" -H "Accept: text/n3"
	 */
	@Override
	public Response localTripleStorePOSTRequest(HttpHeaders headers,
			MultivaluedMap<String, String> formParams) {
		Form form = new Form();
		form.param("format", headers.getRequestHeader("Accept").get(0));
		Iterator<String> it = formParams.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next();
			List<String> ls = formParams.get(name);
			for (String value : ls) {
				form.param(name, value);
			}
		}

		try {
			return getSparqlPostQueryResult(form);
		} catch (IOException | URISyntaxException e) {
			log.error("ERROR, caused by " + e.getMessage());
		}
		return Response.status(400).build();
	}

	private Response getSparqlGetQueryResult(String query) throws IOException,
			URISyntaxException {
		Client client = ClientBuilder.newClient()
				.register(JacksonFeature.class);
		if (query == null || query.isEmpty())
			return Response.status(Status.BAD_REQUEST).build();

		UriBuilder uriBuilder = UriBuilder.fromUri(new URI(VIRTUOSO_HOST));
		uriBuilder.path("sparql");
		uriBuilder.replaceQuery(UriComponent.encode(query, Type.QUERY));
		WebTarget target = client.target(uriBuilder);

		return target.request().get();
	}
 
	private Response getSparqlPostQueryResult(Form form) throws IOException,
			URISyntaxException {
		Client client = ClientBuilder.newClient()
				.register(JacksonFeature.class);
		UriBuilder uriBuilder = UriBuilder.fromUri(new URI(VIRTUOSO_HOST));
		uriBuilder.path("sparql");
		WebTarget target = client.target(uriBuilder);
		Response response = target.request()
				.post(Entity.entity(form,
						MediaType.APPLICATION_FORM_URLENCODED_TYPE));
		return response;
	}

}
