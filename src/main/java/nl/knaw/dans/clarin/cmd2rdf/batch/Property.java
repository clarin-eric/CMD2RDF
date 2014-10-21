package nl.knaw.dans.clarin.cmd2rdf.batch;

/**
 * @author Eko Indarto
 *
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
/**
 * @author akmi
 *
 */
@SuppressWarnings("restriction")
@XmlAccessorType(XmlAccessType.FIELD)
public class Property {
	@XmlAttribute
	public String name;
	
	@XmlValue
	public String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
