package nl.knaw.dans.cmd2rdf.config.xmlmapping;

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
	public List<Record> records;
	
	@XmlElementWrapper(name = "profiles")
	@XmlElement(name="profile")
	public List<Profile> profiles;
	
	@XmlElementWrapper(name = "components")
	@XmlElement(name="component")
	public List<Profile> components;
	
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
	
	 
//	public List<Profile> getComponents() {
//		return components;
//	}
//	public void setComponents(List<Profile> components) {
//		this.components = components;
//	}
}
