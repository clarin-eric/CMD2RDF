package nl.knaw.dans.clarin.playground;

import java.net.URL;

import nl.knaw.dans.clarin.cmd2rdf.util.HttpConnectionManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

public class AppsX {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		 try{
			 
			 URL url = new URL("http://www.yahoo.com");
			 
			 System.out.println(url.toString());
			    HttpRequestBase base = new HttpGet("http://www.google.com");
			    HttpResponse response = HttpConnectionManager.execute(base);
			    HttpEntity entity = response.getEntity();
			    String str = EntityUtils.toString(entity);
		
        System.out.println("----------------------------------------");
        System.out.println(str);
		 }catch (Exception e) {
		    e.printStackTrace();	
		    }
	}

}
