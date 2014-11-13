/**
 * 
 */
package nl.knaw.dans.cmd2rdf.webapps.rest.graph;

import static nl.knaw.dans.cmd2rdf.webapps.misc.Constants.SUPPORTED_RESPONSE_FORMATS;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.cmd2rdf.webapps.rest.IQuery;
import nl.knaw.dans.cmd2rdf.webapps.rest.JerseyRestClient;

/**
 * @author akmi
 *
 */
@Path("/")
public class GraphQuery extends JerseyRestClient implements IQuery {
	
	private static final Logger log = LoggerFactory.getLogger(GraphQuery.class);
	private static final String QUERY_PARAM_FORMAT="format";

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 * 
	 * example: curl -G "http://localhost:8080/cmd2rdf/graph/info"
	 *
	 * @return 'GRAPH GET INFO!' (String) that will be returned as a text/plain
	 *         response.
	 */
	@Override
	public String sayPlainTextHelloGet() {
		return "GRAPH GET INFO!";
	}

	/**
	 * Method handling HTTP POST requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 * 
	 * example: curl "http://localhost:8080/cmd2rdf/graph/info" -d ""
	 *
	 * @return 'GRAPH POST INFO!' (String) that will be returned as a text/plain
	 *         response.
	 */
	@Override
	public String sayPlainTextHelloPost() {
		return "GRAPH POST INFO!\n";
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.cmd2rdf.webapps.rest.IQuery#localTripleStoreGETRequest(javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.UriInfo)
	 */
	@Override
	public Response localTripleStoreGETRequest(HttpHeaders headers,
			UriInfo uriInfo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@GET
	@Path("/{query:.+}")
	@Produces("application/rdf+xml,application/json")
	public Response localTripleStoreGETRequest(@PathParam("query") String query, @Context UriInfo uriInfo) {
		query = "http://localhost:8000/DAV/" + query;
		String query2 = uriInfo.getRequestUri().getQuery();
		try {
			return getSparqlGetQueryResult(query, query2);
		} catch (IOException | URISyntaxException e) {
			log.error("ERROR: " + e.getMessage());
		} 
		return Response.status(400).build();
	}

	/* (non-Javadoc)
	 * @see nl.knaw.dans.cmd2rdf.webapps.rest.IQuery#localTripleStorePOSTRequest(javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.MultivaluedMap)
	 */
	@Override
	public Response localTripleStorePOSTRequest(HttpHeaders headers,
			MultivaluedMap<String, String> formParams) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	private Response getSparqlGetQueryResult(String query, String query2) throws IOException, URISyntaxException {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.connectorProvider(new ApacheConnectorProvider());
		Client client = ClientBuilder.newClient(clientConfig);
		client = ClientBuilder.newClient();
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.digest("dba", "dba");
		client.register(authFeature);

		UriBuilder uriBuilder = UriBuilder.fromUri(new URI(VIRTUOSO_HOST));
		uriBuilder.path("sparql-graph-crud-auth");
		uriBuilder.queryParam("graph-uri", query);
		if (query2 != null && query2.startsWith(QUERY_PARAM_FORMAT)) {
			String formatQuery = query2.replace(QUERY_PARAM_FORMAT + "=", "");
			if (SUPPORTED_RESPONSE_FORMATS.contains(formatQuery)) {
				uriBuilder.queryParam(QUERY_PARAM_FORMAT, formatQuery);
			}
		} else {
			uriBuilder.queryParam("format", "application/rdf+xml");
		}
			
		//uriBuilder.replaceQuery(UriComponent.encode(query2, Type.QUERY));
		//uriBuilder.queryParam("format", query2);
		//uriBuilder.queryParam("format", "application/rdf+xml");
		WebTarget target = client.target(uriBuilder);

		return target.request().get();
	}

}
