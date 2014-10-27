package nl.knaw.dans.cmd2rdf.config.xmlmapping;

/**
 * @author Eko Indarto
 *
 */

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
public class Record{
	
	@XmlAttribute(required = false)
	private
	String desc;
	
	@XmlAttribute(required = true)
	private
	String filter;
	
	@XmlAttribute(required = true)
	private
	int nThreads;
	
	@XmlAttribute(required = false)
	private
	String xmlLimitSizeMin;
	
	@XmlAttribute(required = false)
	private
	String xmlLimitSizeMax;
	
	@XmlAttribute(required = true)
	private
	String xmlSource;
	
	@XmlElementWrapper(name = "properties")
	@XmlElement(name="property")
	List<Property> property;
	
	@XmlElementWrapper(name = "actions")
	@XmlElement(name="action")
	private
	List<Action> actions;
	
	@XmlElement(name="cleanup")
	private
	Cleanup cleanup;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getXmlSource() {
		return xmlSource;
	}

	public void setXmlSource(String xmlSource) {
		this.xmlSource = xmlSource;
	}

	public String getXmlLimitSizeMin() {
		return xmlLimitSizeMin;
	}

	public void setXmlLimitSizeMin(String xmlLimitSizeMin) {
		this.xmlLimitSizeMin = xmlLimitSizeMin;
	}

	public String getXmlLimitSizeMax() {
		return xmlLimitSizeMax;
	}

	public void setXmlLimitSizeMax(String xmlLimitSizeMax) {
		this.xmlLimitSizeMax = xmlLimitSizeMax;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public int getnThreads() {
		return nThreads;
	}

	public void setnThreads(int nThreads) {
		this.nThreads = nThreads;
	}

	public Cleanup getCleanup() {
		return cleanup;
	}

	public void setCleanup(Cleanup cleanup) {
		this.cleanup = cleanup;
	}

}