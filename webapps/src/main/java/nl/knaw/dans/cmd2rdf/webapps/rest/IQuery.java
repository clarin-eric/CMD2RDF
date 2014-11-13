/**
 * 
 */
package nl.knaw.dans.cmd2rdf.webapps.rest;

import static nl.knaw.dans.cmd2rdf.webapps.util.Constants.SUPPORTED_RESPONSE_FORMATS;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author akmi
 *
 */
public interface IQuery {
	
	@GET
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHelloGet();

	@POST
	@Path("/info")
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHelloPost();

	@GET
	public Response localTripleStoreGETRequest(@Context HttpHeaders headers,
			@Context UriInfo uriInfo);

	@POST
	@Produces(SUPPORTED_RESPONSE_FORMATS)
	public Response localTripleStorePOSTRequest(@HeaderParam("Accept") String headerParam,
			MultivaluedMap<String, String> formParams);
}
