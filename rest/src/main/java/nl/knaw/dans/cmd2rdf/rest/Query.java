package nl.knaw.dans.cmd2rdf.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class Query {
	private static final Logger log = LoggerFactory.getLogger(Query.class);
	private static final String VIRTUOSO_HOST = virtuosoHost();
	
	
	/**
	 * Method handling HTTP GET requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 * 
	 * example: curl -G "http://localhost:8080/cmd2rdf/rest/"
	 *
	 * @return 'Hello GET!' (String) that will be returned as a text/plain
	 *         response.
	 */
	@GET
	@Path("/eko")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHelloGet() {
		return "Hello GET!";
	}
	
	/**
	 * Method handling HTTP POST requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 * 
	 * example: curl "http://localhost:8080/cmd2rdf/rest/" -d ""
	 *
	 * @return 'Hello POST!' (String) that will be returned as a text/plain
	 *         response.
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHelloPost() {
		return "Hello POST!";
	}
	
	
	//curl -G "http://localhost:8080/cmd2rdf/rest/sparql" --data-urlencode "query=SELECT count(*) {?s ?p ?o}"
	@GET
	@Path("/sparql")
	public Response forwardGetRequest(@Context HttpHeaders headers,
			@Context UriInfo uriInfo) {
		String query = uriInfo.getRequestUri().getQuery();
		try {
			return getSparqlGetQueryResult(query);
		} catch (IOException | URISyntaxException e) {
			log.error("ERROR: " + e.getMessage());
		} 
		return Response.status(400).build();
	}
	
	
	//curl -v "http://localhost:8080/cmd2rdf/rest/sparql" -d "query=SELECT count(*) {?s ?p ?o}" -H "Accept: text/n3"
	@POST
	@Path("/sparql")
	@Produces("application/xml,application/json,application/sparql-results+xml,text/rdf+n3,text/rdf+ttl,text/rdf+turtle"
			+ ",text/turtle,text/n3,application/turtle,application/x-turtle,application/x-nice-turtle,text/rdf+nt,text/plain")
	public Response forwardPostRequest(@Context HttpHeaders headers, MultivaluedMap<String, String> formParams) {
		Form form = new Form();
		Iterator<String> it = formParams.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next();
			List<String> ls = formParams.get(name);
			for (String value : ls) {
				form.param(name, value);
			}
		}
		
		try {
			Response r = getSparqlPostQueryResult(form);
			return r;
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(400).build();
	}
	
	private Response getSparqlPostQueryResult(Form form) throws IOException, URISyntaxException {
		Client client = ClientBuilder.newClient().register(
				JacksonFeature.class);
		UriBuilder uriBuilder = UriBuilder.fromUri(new URI(VIRTUOSO_HOST));
		uriBuilder.path("sparql");
		WebTarget target = client.target(uriBuilder);

		return target.request().post(Entity.entity(form, MediaType.APPLICATION_JSON_TYPE));
	}
	
	private Response getSparqlGetQueryResult(String query) throws IOException, URISyntaxException {
		Client client = ClientBuilder.newClient().register(
				JacksonFeature.class);
		UriBuilder uriBuilder = UriBuilder.fromUri(new URI(VIRTUOSO_HOST));
		uriBuilder.path("sparql");
		uriBuilder.replaceQuery(UriComponent.encode(query, Type.QUERY));
		WebTarget target = client.target(uriBuilder);

		return target.request().get();
	}
	
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