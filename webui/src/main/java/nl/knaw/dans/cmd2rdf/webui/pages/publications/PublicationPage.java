package nl.knaw.dans.cmd2rdf.webui.pages.publications;

import nl.knaw.dans.cmd2rdf.webui.Cmd2RdfBasePage;

import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Eko Indarto
 */
public class PublicationPage extends Cmd2RdfBasePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -243672095955455501L;
	/**
	 * Constructor
	 * 
	 * @param pageParameters
	 */
	public PublicationPage(final PageParameters pageParameters)
	{
		super(pageParameters);
		add(new PublicationPanel("publicationPanel"));
		
	}

	
}
