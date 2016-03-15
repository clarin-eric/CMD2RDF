package nl.knaw.dans.cmd2rdf.webapps.ui.pages;

import nl.knaw.dans.cmd2rdf.webapps.ui.Cmd2RdfBasePage;

import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Eko Indarto
 */
public class BrowsePage extends Cmd2RdfBasePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -243672095955455521L;
	/**
	 * Constructor
	 * 
	 * @param pageParameters
	 */
	public BrowsePage(final PageParameters pageParameters)
	{
		super(pageParameters);

		Url relative = Url.parse("graph/browse");
		String cmd2rdfLdaUrl = getRequestCycle().getUrlRenderer().renderFullUrl(relative).replace("/cmd2rdf", "/cmd2rdf-lda");
		RedirectPage page = new RedirectPage ( "/cmd2rdf-lda/graph/browse" );
		InlineFrame frame = new InlineFrame("cmd2rdfLdaUrl", page);
		add(frame);
		
	}
}
