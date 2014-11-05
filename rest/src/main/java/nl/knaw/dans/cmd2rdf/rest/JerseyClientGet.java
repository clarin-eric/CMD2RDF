package nl.knaw.dans.cmd2rdf.rest;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriComponent.Type;

public class JerseyClientGet {
	public static void main(String[] args) {
		try {

			Client client = ClientBuilder.newClient().register(
					JacksonFeature.class);
			UriBuilder uriBuilder = UriBuilder.fromUri(new URI(
					"http://localhost:8890"));
			uriBuilder.path("sparql");
			uriBuilder.queryParam("query", UriComponent.encode(
					"select distinct ?Concept where {[] a ?Concept} LIMIT 100",
					Type.QUERY));
			uriBuilder.queryParam("format", "applicationjson");
			WebTarget target = client.target(uriBuilder);

			Response response = target.request().get();

			int status = response.getStatus();

			if (status != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());

			} else {
				InputStream in = response.readEntity(InputStream.class);
				String theString = IOUtils.toString(in, "UTF-8");
				System.out.println(theString);
			}

			System.out.println("Output from Server .... \n");

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
}
