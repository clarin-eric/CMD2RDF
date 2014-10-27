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
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement (name="class")
@XmlAccessorType(XmlAccessType.FIELD)
public class Clazz {
	@XmlAttribute
	private
	String name;
	
	
	@XmlElementWrapper(name = "properties")
	@XmlElement(name="property")
	private
	List<Property> property;


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<Property> getProperty() {
		return property;
	}


	public void setProperty(List<Property> property) {
		this.property = property;
	}
	
	
	
}
