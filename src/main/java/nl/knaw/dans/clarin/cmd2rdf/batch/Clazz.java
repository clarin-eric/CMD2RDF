package nl.knaw.dans.clarin.cmd2rdf.batch;

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


@SuppressWarnings("restriction")
@XmlRootElement (name="class")
@XmlAccessorType(XmlAccessType.FIELD)
public class Clazz {
	@XmlAttribute
	String name;
	
	
	@XmlElementWrapper(name = "properties")
	@XmlElement(name="property")
	List<Property> property;
	
	
	
}
