package nl.knaw.dans.cmd2rdf.config;

/**
 * @author Eko Indarto
 *
 */

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="CMD2RDF")
public class Jobs {
	private Config config;
	private Prepare prepare;
	private Cleanup cleanup;
	@XmlElementWrapper(name = "records")
	@XmlElement(name="record")
	List<Record> records;
	
	@XmlElementWrapper(name = "profiles")
	@XmlElement(name="profile")
	List<Profile> profiles;
	
	@XmlElementWrapper(name = "components")
	@XmlElement(name="component")
	List<Profile> components;
	
	public void setConfig(Config config) {
		this.config = config;
	}
	public Config getConfig() {
		return config;
	}
	public Prepare getPrepare() {
		return prepare;
	}
	public void setPrepare(Prepare prepare) {
		this.prepare = prepare;
	}
	public Cleanup getCleanup() {
		return cleanup;
	}
	public void setCleanup(Cleanup cleanup) {
		this.cleanup = cleanup;
	}
}
