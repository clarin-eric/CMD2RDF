package nl.knaw.dans.cmd2rdf.webapps.rest.graph;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JerseyClient {
	public static void main(String[] args) {
		getRequest();
		
		
		//postRequest();
	}
	
	//curl -v "http://localhost:8080/sparql" --data-urlencode "query=SELECT count(*) {?s ?p ?o}" -H "Accept: application/json"
	private static void postRequest() {
		try {

			Client client = ClientBuilder.newClient().register(
					JacksonFeature.class);
			UriBuilder uriBuilder = UriBuilder.fromUri(new URI(
					"http://localhost:8890/"));
			uriBuilder.path("sparql");
//			uriBuilder.queryParam("query", UriComponent.encode(
//					"select distinct ?Concept where {[] a ?Concept} LIMIT 100",
//					Type.QUERY));
//			uriBuilder.queryParam("format", "application/json");
			WebTarget target = client.target(uriBuilder);
			
			Form form = new Form();
			form.param("query", "select distinct ?Concept where {[] a ?Concept} LIMIT 100");
			form.param("format", "application/json");
			Response response = target.request()
				    .post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED_TYPE));
			
			int status = response.getStatus();

			if (status != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());

			} else {
				InputStream in = response.readEntity(InputStream.class);
				String theString = IOUtils.toString(in, "UTF-8");
				//System.out.println(theString);
			}

			//System.out.println("Output from Server .... \n");

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	
	private static void getRequest() {
		try {

			Client client = ClientBuilder.newClient().register(
					JacksonFeature.class);
			UriBuilder uriBuilder = UriBuilder.fromUri(new URI(
					"http://zandbak01.dans.knaw.nl:8000"));
			uriBuilder.path("sparql");
			uriBuilder.queryParam("query", UriComponent.encode(
					"select count(*) {?s <http://www.w3.org/ns/oa#hasBody> ?o}",
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
				
				JsonFactory jfactory = new JsonFactory();
				JsonParser jp = jfactory.createParser(in);
				jp.nextToken();
				while (jp.nextToken() != JsonToken.END_OBJECT) {
					String fieldname = jp.getCurrentName();
					String text = jp.getText();
					String value = jp.getValueAsString();
					//System.out.println("fieldname: " + fieldname + "\ttext: " + text + "\tvalue: " + value );
					jp.nextToken();
//					if ("name".equals(fieldname)) {
//						
//					}
				}
				String theString = IOUtils.toString(in, "UTF-8");
				//System.out.println(theString);
			}

			//System.out.println("Output from Server .... \n");

		} catch (Exception e) {

			e.printStackTrace();

		}
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
