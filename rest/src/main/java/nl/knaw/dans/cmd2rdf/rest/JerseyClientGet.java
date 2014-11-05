package nl.knaw.dans.cmd2rdf.rest;

import java.net.URI;
import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jackson.JacksonFeature;

public class JerseyClientGet {
	public static void main(String[] args) {
		try {
	 
			Client client = ClientBuilder.newClient().register(JacksonFeature.class);
	 
			UriBuilder uriBuilder = UriBuilder.fromUri(new URI("http://zandbak01.dans.knaw.nl:8000"));
			uriBuilder.path("sparql");
			uriBuilder.queryParam("query", "select distinct %3FConcept where {[] a %3FConcept} LIMIT 100");
			uriBuilder.queryParam("format", "application/sparql-results+json");
			WebTarget target = client.target(uriBuilder.build());
			
			Response response = target.request().get();
			int status = response.getStatus();
	 
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}
	 
			String output = response.getEntity().toString();
	 
			System.out.println("Output from Server .... \n");
			System.out.println(output);
	 
		  } catch (Exception e) {
	 
			e.printStackTrace();
	 
		  }
	 
		}
}
