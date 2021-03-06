package nl.knaw.dans.cmd2rdf.webapps.ui;

import nl.knaw.dans.cmd2rdf.webapps.ui.pages.ApiPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.BrowsePage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.ContactPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.HomePage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.HowItWorkPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.PublicationPage;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Navigation panel for the CMD2RDF project.
 * 
 * @author Eko Indarto
 */
public final class Cmd2RdfPageHeader extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 970185289293886277L;
	/**
	 * Construct.
	 * 
	 * @param id
	 *            id of the component
	 * @param exampleTitle
	 *            title of the example
	 * @param page
	 *            The example page
	 */
	public Cmd2RdfPageHeader(String id, WebPage page) {
		super(id);
		add(new BookmarkablePageLink<HomePage>("home", HomePage.class).add(new Cmd2RdfNavigationLabel("homeLabel", "Home")));
		add(new BookmarkablePageLink<HowItWorkPage>("how", HowItWorkPage.class).add(new Cmd2RdfNavigationLabel("howLabel", "How it works")));
		add(new BookmarkablePageLink<BrowsePage>("browse", BrowsePage.class).add(new Cmd2RdfNavigationLabel("browseLabel", "Browse")));
		add(new BookmarkablePageLink<ApiPage>("api", ApiPage.class).add(new Cmd2RdfNavigationLabel("apiLabel", "API")));
		add(new BookmarkablePageLink<PublicationPage>("pub",PublicationPage.class).add(new Cmd2RdfNavigationLabel("pubLabel", "Publications")));
		add(new BookmarkablePageLink<ContactPage>("contact", ContactPage.class).add(new Cmd2RdfNavigationLabel("contactLabel", "Contact")));
	}
}
