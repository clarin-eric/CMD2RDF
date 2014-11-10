package nl.knaw.dans.cmd2rdf.webui.secure;

import nl.knaw.dans.cmd2rdf.webui.secure.model.User;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class UserSession extends WebSession {

    private User user;

    public UserSession(Request request) {
        super(request);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean userLoggedIn() {
        return user != null;
    }

    public boolean userNotLoggedIn() {
        return user == null;
    }

    public static UserSession get() {
    	Object o = WebSession.get();
        return (UserSession) WebSession.get();
    }


}
