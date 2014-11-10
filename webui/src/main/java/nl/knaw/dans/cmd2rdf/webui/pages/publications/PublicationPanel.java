package nl.knaw.dans.cmd2rdf.webui.pages.publications;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * @author akmi
 * 
 */
public class PublicationPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1973574682018245001L;

	public PublicationPanel(String id) {
		super(id);
        
		// create a list of ITab objects used to feed the tabbed panel
		List<ITab> tabs = new ArrayList<ITab>();
		
		
		tabs.add(new AbstractTab(new Model<String>("Posters"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new TabPosterPublicationPanel(panelId);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Papers"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new TabPaperPublicationPanel(panelId);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Others Publications"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new TabOtherPublicationPanel(panelId);
			}
		});
		
		add(new AjaxTabbedPanel("tabs", tabs));
	}

}
