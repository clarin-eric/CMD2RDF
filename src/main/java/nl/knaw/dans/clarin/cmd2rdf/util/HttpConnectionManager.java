
package nl.knaw.dans.clarin.cmd2rdf.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

/**
 * HTTP connection manager using the pooling HTTP client connection manager.
 * 
 * Singleton class; the manager will be created on the first invocation of the
 * execute method.
 * 
 * Note: currently only the CmdiCarcheRecord class uses this manager for HTTP
 * connection. The provider could be extended in a more general way achieving
 * greater efficiency. For example:
 * 
 * - Cache,
 * - DataProvider, and 
 * - ParseImdiTest 
 * 
 * could also use the manager 
 * 
 * @author keeloo based on wilelb's and menwin's work
 */
public class HttpConnectionManager {

    // the connection manager is allowed to exist only once
    private static HttpConnectionManager _instance;
    
    // both the manager and the connection do, after their creation not change anymore
    protected final PoolingHttpClientConnectionManager cManager;
    protected final CloseableHttpClient httpClient;

    /**
     * HTTP request without context
     * 
     * @param request the HTTP request
     * @return the response to the HTTP request
     * @throws IOException 
     */
    public static HttpResponse execute(HttpRequestBase request) throws IOException {
        // without context, invoke use null for it
        return execute(request, null);
    }
    
    /**
     * HTTP request with context 
     * 
     * @param request the HTTP request
     * @param context the context to the HTTP request
     * @return response to the HTTP request
     * @throws IOException 
     */
    public static HttpResponse execute(HttpRequestBase request, HttpContext context) 
            throws IOException {
        
        // create a connection manager only if it does not exist yet
        if (_instance == null) {
            _instance = new HttpConnectionManager();
        }
    
        if (context == null) {
            return _instance.httpClient.execute(request);
        } else {
            return _instance.httpClient.execute(request, context);
        }
    }

    /**
     * HTTP connection manager constructor
     *
     * This constructor will only be invoked from the static methods of this
     * class
     */
    private HttpConnectionManager() {
        // create the manager by creating a pooling connection manager
        this.cManager
                = new PoolingHttpClientConnectionManager();
        // set maximum of connections managed
        this.cManager.setMaxTotal(25);
        // associate a http client with the connection mangager
        this.httpClient
                = HttpClients.custom()
                .setConnectionManager(cManager)
                .build();
    }
}
