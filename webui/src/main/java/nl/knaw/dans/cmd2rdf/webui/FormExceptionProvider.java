package nl.knaw.dans.cmd2rdf.webui;

import java.io.Serializable;

public class FormExceptionProvider implements Serializable {

	private String target;
	private String comments;
	private String additional;
	
	private static final long serialVersionUID = 8927760560965064498L;

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the additional
	 */
	public String getAdditional() {
		return additional;
	}

	/**
	 * @param additional the additional to set
	 */
	public void setAdditional(String additional) {
		this.additional = additional;
	}

	
}
