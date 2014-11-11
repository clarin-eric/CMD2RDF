package nl.knaw.dans.cmd2rdf.webapps.ui;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * Base class for all CMD2RDF pages.
 * 
 * @author Eko Indarto
 * 
 */
public class Cmd2RdfBasePage extends WebPage
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final CssResourceReference MYPAGE_CSS = new CssResourceReference(Cmd2RdfBasePage.class, "styles.css");
	private static final JavaScriptResourceReference MYPAGE_JS = new JavaScriptResourceReference(Cmd2RdfBasePage.class, "cmd2rdf.js");
	
	/**
	 * Constructor
	 */
	public Cmd2RdfBasePage()
	{
		this(new PageParameters());
	}

	/**
	 * Constructor
	 * 
	 * @param pageParameters
	 */
	public Cmd2RdfBasePage(final PageParameters pageParameters)
	{
		super(pageParameters);
		add(new Cmd2RdfPageHeader("header", this));
	}


	/**
	 * Construct.
	 * 
	 * @param model
	 */
	public Cmd2RdfBasePage(IModel<?> model)
	{
		super(model);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
	  response.render(CssReferenceHeaderItem.forReference(MYPAGE_CSS));
	  response.render(JavaScriptReferenceHeaderItem.forReference(MYPAGE_JS));
	}

}
