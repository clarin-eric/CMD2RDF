/**
 * 
 */
package nl.knaw.dans.cmd2rdf.webapps.rest.graph;

import static nl.knaw.dans.cmd2rdf.webapps.util.Constants.SUPPORTED_RESPONSE_FORMATS;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.knaw.dans.cmd2rdf.webapps.rest.IQuery;
import nl.knaw.dans.cmd2rdf.webapps.rest.JerseyRestClient;
import nl.knaw.dans.cmd2rdf.webapps.ui.secure.Cmd2RdfSecureApplication;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akmi
 *
 */
@Path("/")
public class GraphQuery extends JerseyRestClient implements IQuery {
	
	private static final Logger LOG = LoggerFactory.getLogger(GraphQuery.class);
	private static final String QUERY_PARAM_FORMAT="format";
	private static final String PARENT_PATH = "PARENT_PATH";
	private static final String QUERY_GET_CHILDREN = "select ?s ?p ?v {graph ?g {?s ?p ?v} . filter(strStarts(str(?g), \""
													+ Cmd2RdfSecureApplication.cofigReader.getPrefixBaseURI() + "/" + PARENT_PATH + "\"))}";
	
	public GraphQuery() {
		super();
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.connectorProvider(new ApacheConnectorProvider());
		HttpAuthenticationFeature authFeature = HttpAuthenticationFeature.digest(VIRTUOSO_USERNAME, VIRTUOSO_PASSWORD);
		register(authFeature);
	}

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
		return "GRAPH GET INFO!\n";
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
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
    private Response getChildrenGraphInZip(final String query, final UriInfo uriInfo, final String headerParam, final String filename)  
            throws IOException
    {
        //we write to the PipedOutputStream
        //that data is then available in the PipedInputStream which we return
    	final PipedOutputStream sink = new PipedOutputStream();
        PipedInputStream source = new PipedInputStream(sink);
 
        //apparently we need to write to the PipedOutputStream in a separate thread
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                //PrintStream => BufferedOutputStream => ZipOutputStream => PipedOutputStream
                ZipOutputStream zip = new ZipOutputStream(sink);
                PrintStream writer = new PrintStream(new BufferedOutputStream(zip));
 
                try
                {
                	Response response = getChildrenGraphs(query, uriInfo, headerParam); 
                    zip.putNextEntry(new ZipEntry(filename + ".rdf"));
                    InputStream in = response.readEntity(InputStream.class);
                    writer.println(IOUtils.toString(in, "UTF-8"));
                    writer.flush();
                    zip.closeEntry();
                }
                catch (IOException e)
                {
                }
                writer.flush();
                writer.close();
            }
        };
        Thread writerThread = new Thread(runnable, "FileGenerator");
        writerThread.start();
 
        return Response.ok(source).type("application/zip")
		        .header("Content-Disposition", "attachment; filename=\""+ filename + ".zip\"").build();
    }
 
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.knaw.dans.cmd2rdf.webapps.rest.RestQuery#localTripleStorePOSTRequest
	 * (javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.MultivaluedMap)
	 * 
	 * Example:
	 * http://localhost:8080/cmd2rdf/graph/The_Language_Archive_s_IMDI_portal/oai_www_mpi_nl_MPI1758566.rdf?format=application%2Fjson
	 * curl -v "http://localhost:8080/cmd2rdf/graph/Te_Archive_s_IMDI_portal/oai_www_mpi_nl_MPI1758566.rdf" -d "format=application/json" -H "Accept: application/json" -X GET
	 */
	
	@GET
	@Path("/{query:.+}")
	@Produces("application/rdf+xml,application/json,application/x-zip-compressed")
	public Response localTripleStoreGETRequest(@QueryParam("pwd") String pwd, @PathParam("query") String incomingQuery, @Context UriInfo uriInfo, @HeaderParam("format")/*Accept*/ String headerParam) {
		String prefixBaseURI = Cmd2RdfSecureApplication.cofigReader.getPrefixBaseURI();
		String query = prefixBaseURI + "/" + incomingQuery;
		String filename = incomingQuery.replaceAll("/", "-");
		String ext = FilenameUtils.getExtension(incomingQuery).trim().toLowerCase();
		String childrenQuery = QUERY_GET_CHILDREN.replace(PARENT_PATH, incomingQuery);
		if (ext.isEmpty()) {
			boolean allowDownloadDir = false;
			LOG.info("Dowloading zip file: '" + query + "'.\tpwd='" + pwd);
			
			String dirDownloadPwd = Cmd2RdfSecureApplication.cofigReader.getDirDownloadPwd();
			
			if (dirDownloadPwd != null && !dirDownloadPwd.trim().isEmpty()) {
				if (pwd !=null && !pwd.isEmpty() && pwd.equals(dirDownloadPwd))
					allowDownloadDir = true;
			} else
				allowDownloadDir = true;
			
			if (allowDownloadDir) {
				try {
					return getChildrenGraphInZip(childrenQuery, uriInfo, headerParam, filename);
				} catch (IOException e) {
					LOG.error(e.getMessage());
				}
			} else
				return  Response.status(Status.FORBIDDEN).build();
			
			
		} else {
			if (ext.toLowerCase().equals("rdf")) {
				return getSingleRDFGraph(query, uriInfo, headerParam); 
			}
		}
		return Response.status(Status.BAD_REQUEST).build();
	}

	private Response getSingleRDFGraph(String query, UriInfo uriInfo, String headerParam) {
		String query2 = uriInfo.getRequestUri().getQuery();
		String headerFormat = null;
		if (headerParam != null && !headerParam.isEmpty())
			headerFormat = headerParam;
		try {
			return getSparqlGetQueryResult(query, query2, headerFormat);
		} catch (IOException | URISyntaxException e) {
			LOG.error("ERROR: " + e.getMessage());
		}
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	private Response getChildrenGraphs(String query, UriInfo uriInfo, String headerParam) {
		String query2 = uriInfo.getRequestUri().getQuery();
		String headerFormat = null;
		if (headerParam != null && !headerParam.isEmpty())
			headerFormat = headerParam;
		try {
			return forwardQueryToSparqlEndpoint(query, query2, headerFormat);
		} catch (IOException | URISyntaxException e) {
			LOG.error("ERROR: " + e.getMessage());
		}
		return Response.status(Status.BAD_REQUEST).build();
	}

	
	private Response getSparqlGetQueryResult(String query, String query2, String headerFormat) throws IOException, URISyntaxException {
		uriBuilder.path("sparql-graph-crud-auth");
		uriBuilder.queryParam("graph-uri", query);
		String formatQuery = "application/rdf+xml";
		if (headerFormat != null) {
			formatQuery = headerFormat;
		} else if (query2 != null && query2.startsWith(QUERY_PARAM_FORMAT)) {
			String fq = query2.replace(QUERY_PARAM_FORMAT + "=", "");
			if (SUPPORTED_RESPONSE_FORMATS.contains(formatQuery)) {
				formatQuery = fq;
			}
		}
		
		uriBuilder.queryParam(QUERY_PARAM_FORMAT, formatQuery);
		WebTarget target = client.target(uriBuilder);

		return target.request().get();
	}
	
	private Response forwardQueryToSparqlEndpoint(String query, String query2, String headerFormat) throws IOException, URISyntaxException {
		uriBuilder.path("sparql");
		String formatQuery = "application/rdf+xml";
		if (headerFormat != null) {
			formatQuery = headerFormat;
		} else if (query2 != null && query2.startsWith(QUERY_PARAM_FORMAT)) {
			String fq = query2.replace(QUERY_PARAM_FORMAT + "=", "");
			if (SUPPORTED_RESPONSE_FORMATS.contains(formatQuery)) {
				formatQuery = fq;
			}
		}
		
		WebTarget target = client.target(uriBuilder);
		Form form = new Form();
		form.param("format", formatQuery);
		form.param("query", query);
		return target.request().post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
	}
	
	
	/* (non-Javadoc)
	 * @see nl.knaw.dans.cmd2rdf.webapps.rest.IQuery#localTripleStorePOSTRequest(javax.ws.rs.core.HttpHeaders, javax.ws.rs.core.MultivaluedMap)
	 */
	@Override
	public Response localTripleStorePOSTRequest(@HeaderParam("Accept") String headerParam,
			@FormParam("query") String query) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
	
	/*
	 * TODO:
	 select ?s ?p ?v {
  graph ?g {?s ?p ?v} 
. filter(strStarts(str(?g), "http://localhost:8000/DAV/PARENT/"))
}

The most efficient approach I guess  is to explicitly state in your data that

<http://localhost:8000/DAV/PARENT> :hasChild <http://localhost:8000/DAV/PARENT/A.rdf> .
<http://localhost:8000/DAV/PARENT> :hasChild <http://localhost:8000/DAV/PARENT/B.rdf> .
<http://localhost:8000/DAV/PARENT> :hasChild <http://localhost:8000/DAV/PARENT/C.rdf> .

and then query 

select * {
  <http://localhost:8000/DAV/PARENT> :hasChild ?child .
  graph ?child {?s ?p ?v} 
}
	 */

}
