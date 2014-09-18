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

@XmlAccessorType(XmlAccessType.FIELD)
public class Record{
	
	@XmlAttribute(required = true)
	String filter;
	
	@XmlAttribute(required = true)
	int nThreads;
	
	@XmlAttribute(required = false)
	String xmlLimitSize;
	
	@XmlAttribute(required = false)
	String xmlLimitValue;
	
	@XmlAttribute(required = true)
	String xmlSource;
	
	@XmlElementWrapper(name = "properties")
	@XmlElement(name="property")
	List<Property> property;
	
	@XmlElementWrapper(name = "actions")
	@XmlElement(name="action")
	List<Action> actions;

}

