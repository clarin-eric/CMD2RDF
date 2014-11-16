package nl.knaw.dans.cmd2rdf.webapps.rest.sparql;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import nl.knaw.dans.cmd2rdf.webapps.rest.JerseyRestClient;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JerseyGetClient extends JerseyRestClient{
	private static final String QUERY_TOTAL_RECORDS = "select count(*) {?s ?p ?o}";
	private static final String QUERY_RDF_RECORDS = "select count(*) {?s <http://www.w3.org/ns/oa#hasBody> ?o}";
	public  JerseyGetClient() {
		super();
		register(JacksonFeature.class);
	}
	public String  getNumberOfTotalRecords() {
		String val = "";
		val = parsingJson(QUERY_TOTAL_RECORDS);
		return val;
		
	};
	
	public String  getNumberRDFRecords() {
		String val = "";
		val = parsingJson(QUERY_RDF_RECORDS);
		return val;
		
	}
	private String parsingJson(String q) {
		JsonFactory jfactory = new JsonFactory();
		JsonParser jp;
		try {
			jp = jfactory.createParser(getRequest(q));
			jp.nextToken();
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String fieldname = jp.getCurrentName();
				String text = jp.getText();
				String value = jp.getValueAsString();
				System.out.println("fieldname: " + fieldname + "\ttext: " + text + "\tvalue: " + value );
				
				if ("value".equals(fieldname)) {
					return jp.getValueAsString();
					
				}
				jp.nextToken();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	};
	
	public InputStream  getRequest(String query) {
		String val="";
		try {
			uriBuilder.path("sparql");
			uriBuilder.queryParam("query", UriComponent.encode(
					query,
					Type.QUERY));
			uriBuilder.queryParam("format", "application/json");
			WebTarget target = client.target(uriBuilder);

			Response response = target.request().get();

			int status = response.getStatus();

			if (status != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());

			} else {
				InputStream in = response.readEntity(InputStream.class);
				return in;
				
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		return null;
	}
}
//MultivaluedMap<String, String> mm = headers.getRequestHeaders();
//Iterator<String> it = mm.keySet().iterator();
//
//while (it.hasNext()) {
//	String s = it.next();
//	System.out.println("s: " + s);
//	List<String> ls = mm.get(s);
//	for (String ss : ls) {
//		System.out.println("ss: " + ss);
//
//	}
//}
//String userAgent = headers.getRequestHeader("user-agent").get(0);
//
//return Response
//		.status(200)
//		.entity("addUser is called, userAgent : " + userAgent
//				+ "\n========\n" + query).build();
