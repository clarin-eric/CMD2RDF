package nl.knaw.dans.cmd2rdf.config;

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
	String name;
	
	
	@XmlElementWrapper(name = "properties")
	@XmlElement(name="property")
	List<Property> property;
	
	
	
}