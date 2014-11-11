package nl.knaw.dans.cmd2rdf.webapps.ui.secure.view;

import nl.knaw.dans.cmd2rdf.webapps.ui.Cmd2RdfBasePage;
import nl.knaw.dans.cmd2rdf.webapps.ui.secure.UserSession;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class AdminPage extends Cmd2RdfBasePage {
    private static final long serialVersionUID = 1L;

    public AdminPage(final PageParameters parameters) {
        super(parameters);
       boolean userLogin =  UserSession.get().userLoggedIn();
       boolean userNotLogin = UserSession.get().userNotLoggedIn();

        LoginPanel loginPanel = new LoginPanel("loginPanel");
        loginPanel.setVisible(userNotLogin);
        add(loginPanel);
        StringValue paramvalue = parameters.get("selectedTab");
        int selectedTab = 0;
        if (!paramvalue.isNull() && !paramvalue.isEmpty()) {
        	selectedTab = paramvalue.toInt();
        }
        AdminPanel adminPanel = new AdminPanel("adminPanel", selectedTab);
        adminPanel.setVisible(userLogin);
        add(adminPanel);
    }

}
