package nl.knaw.dans.cmd2rdf.rest;

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/")
public class Query {

	/**
	 * Method handling HTTP GET requests. The returned object will be sent to
	 * the client as "text/plain" media type.
	 *
	 * @return 'SPARQL Query' (String) that will be returned as a text/plain
	 *         response.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "SPARQL Query!";
	}

	@GET
	@Path("/sparql")
	public Response addUser(@Context HttpHeaders headers,
			@Context UriInfo uriInfo) {
		
		String query = uriInfo.getRequestUri().getQuery();
		MultivaluedMap<String, String> mm = headers.getRequestHeaders();
		Iterator<String> it = mm.keySet().iterator();

		while (it.hasNext()) {
			String s = it.next();
			System.out.println(s);
			List<String> ls = mm.get(s);
			for (String ss : ls) {
				System.out.println("ss: " + ss);

			}
		}
		String userAgent = headers.getRequestHeader("user-agent").get(0);

		return Response
				.status(200)
				.entity("addUser is called, userAgent : " + userAgent
						+ "\n========\n" + query).build();

	}
}