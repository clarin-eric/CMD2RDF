package nl.knaw.dans.cmd2rdf.webui.pages;

import nl.knaw.dans.cmd2rdf.webui.Cmd2RdfBasePage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends Cmd2RdfBasePage {
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters) {
		super();

		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		// TODO Add your page's components here

    }
}
