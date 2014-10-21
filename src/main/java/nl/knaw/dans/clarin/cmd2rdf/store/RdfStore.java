/**
 * 
 */
package nl.knaw.dans.clarin.cmd2rdf.store;


/**
 * @author Eko Indarto
 *
 */
public abstract class RdfStore {
	protected String serverURL;
	protected String username;
	protected String password;
	
	protected RdfStore(){}
	
	
	protected RdfStore(String serverURL, String username, String password){
		this.serverURL = serverURL;
		this.username = username;
		this.password = password;
	}
	
	protected String getServerURL() {
		return serverURL;
	}
	protected void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	
	protected String getUsername() {
		return username;
	}
	protected void setUsername(String username) {
		this.username = username;
	}
	protected String getPassword() {
		return password;
	}
	protected void setPassword(String password) {
		this.password = password;
	}
	
}
