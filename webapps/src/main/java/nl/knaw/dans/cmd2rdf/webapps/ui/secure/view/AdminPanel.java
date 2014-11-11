package nl.knaw.dans.cmd2rdf.webapps.ui.secure.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knaw.dans.cmd2rdf.webapps.ui.secure.Cmd2RdfSecureApplication;
import nl.knaw.dans.cmd2rdf.webapps.ui.secure.UserSession;
import nl.knaw.dans.cmd2rdf.webapps.ui.service.CookieService;
import nl.knaw.dans.cmd2rdf.webapps.ui.service.SessionProvider;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * @author akmi
 * 
 */
public class AdminPanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1973574682018245001L;

	public AdminPanel(String id, int selectedTabe) {
		super(id);
		final String cmd2rdfConfFile = null;
        String userName = UserSession.get().userLoggedIn() ? UserSession.get().getUser().getLogin() : "Anonymous user";
        Label userNameLabel = new Label("userName", userName);
        add(userNameLabel);
        final String cmd2rdfHomeDisplay = null;
		AjaxEditableLabel ael = new AjaxEditableLabel("editableLable"){
				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					// TODO Auto-generated method stub
					super.onSubmit(target);
					Collection c = target.getComponents();
					org.apache.wicket.Component cc = (org.apache.wicket.Component) c.iterator().next();
					if (cc instanceof AjaxEditableLabel) {
						Object scd = cc.getDefaultModelObject();
						//cmd2rdfHomeDisplay = scd.toString();
						// cmd2rdfConfFile = "" + cmd2rdfHomeDisplay;
					}
				}
        };
        ael.setDefaultModel(new Model<String>(cmd2rdfHomeDisplay));
        add(ael);
        
        add(new Link<Void>("logout") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void onClick() {
                CookieService cookieService = Cmd2RdfSecureApplication.get().getCookieService();
                cookieService.removeCookieIfPresent(getRequest(), getResponse(), SessionProvider.REMEMBER_ME_LOGIN_COOKIE);
                cookieService.removeCookieIfPresent(getRequest(), getResponse(), SessionProvider.REMEMBER_ME_PASSWORD_COOKIE);

                UserSession.get().setUser(null);
                UserSession.get().invalidate();
            }
        });
        
		// create a list of ITab objects used to feed the tabbed panel
		List<ITab> tabs = new ArrayList<ITab>();
		
		tabs.add(new AbstractTab(new Model<String>("Statistics"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new TabStatisticsPanel(panelId, cmd2rdfConfFile);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Configuration"))
		{
			

			@Override
			public Panel getPanel(String panelId)
			{
				return new TabConfigurationPanel(panelId, cmd2rdfConfFile);
			}
		});
		
		tabs.add(new AbstractTab(new Model<String>("Current Process"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new TabProcessInProgressPanel(panelId, cmd2rdfConfFile);
			}
		});
		
		/*tabs.add(new AbstractTab(new Model<String>("TM in Progress"))
		{
			@Override
			public Panel getPanel(String panelId)
			{
				return new TabProcessInProgressPanel(panelId, cmd2rdfConfFile);
			}
		});*/
		
		AjaxTabbedPanel atp = new AjaxTabbedPanel("tabs", tabs);
		atp.setSelectedTab(selectedTabe);
		add(atp);
	}

}
