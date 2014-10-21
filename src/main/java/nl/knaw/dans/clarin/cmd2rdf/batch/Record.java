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

@XmlAccessorType(XmlAccessType.FIELD)
public class Record{
	
	@XmlAttribute(required = false)
	String desc;
	
	@XmlAttribute(required = true)
	String filter;
	
	@XmlAttribute(required = true)
	int nThreads;
	
	@XmlAttribute(required = false)
	String xmlLimitSizeMin;
	
	@XmlAttribute(required = false)
	String xmlLimitSizeMax;
	
	@XmlAttribute(required = true)
	String xmlSource;
	
	@XmlElementWrapper(name = "properties")
	@XmlElement(name="property")
	List<Property> property;
	
	@XmlElementWrapper(name = "actions")
	@XmlElement(name="action")
	List<Action> actions;
	
	@XmlElement(name="cleanup")
	Cleanup cleanup;

}

