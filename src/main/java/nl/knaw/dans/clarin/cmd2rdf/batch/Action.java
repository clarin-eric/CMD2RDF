package nl.knaw.dans.clarin.cmd2rdf.batch;

/**
 * @author Eko Indarto
 *
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("restriction")
@XmlAccessorType(XmlAccessType.FIELD)
public class Action {
	@XmlAttribute
	String name;

	@XmlElement(name="class")
	Clazz clazz;
}
