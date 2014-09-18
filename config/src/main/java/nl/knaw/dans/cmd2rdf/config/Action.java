package nl.knaw.dans.cmd2rdf.config;

/**
 * @author Eko Indarto
 *
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Action {
	@XmlAttribute
	String name;

	@XmlElement(name="class")
	Clazz clazz;
}
