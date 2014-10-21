/**
 * @author Eko Indarto
 *
 */
package nl.knaw.dans.clarin.cmd2rdf.batch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
@SuppressWarnings("restriction")
@XmlRootElement (name="stylesheet")
@XmlAccessorType(XmlAccessType.FIELD)
public class Stylesheet {
	@XmlAttribute
	private String href;
	
	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
