package nl.knaw.dans.clarin.cmd2rdf.batch;

/**
 * @author Eko Indarto
 *
 */

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("restriction")
@XmlRootElement(name="CMD2RDF")
public class Jobs {
	private Config config;
	private Prepare prepare;
	private Cleanup cleanup;
	@XmlElementWrapper(name = "records")
	@XmlElement(name="record")
	List<Record> records;
	
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
