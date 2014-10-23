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
public class Profile{
	
	@XmlAttribute(required = false)
	String desc;
	
	@XmlAttribute(required = true)
	String filter;
	
	@XmlAttribute(required = true)
	String xmlSource;
	
	@XmlElementWrapper(name = "actions")
	@XmlElement(name="action")
	List<Action> actions;
	
}

