package nl.knaw.dans.cmd2rdf.webapps.ui.secure;

import nl.knaw.dans.cmd2rdf.config.ConfigReader;
import nl.knaw.dans.cmd2rdf.config.exeception.ConfigException;
import nl.knaw.dans.cmd2rdf.webapps.ui.ExceptionPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.ApiPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.ContactPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.HomePage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.HowItWorkPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.pages.PublicationPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.secure.view.AdminPage;
import nl.knaw.dans.cmd2rdf.webapps.ui.service.CookieService;
import nl.knaw.dans.cmd2rdf.webapps.ui.service.SessionProvider;
import nl.knaw.dans.cmd2rdf.webapps.ui.service.UserService;
import nl.knaw.dans.cmd2rdf.webapps.util.Misc;

import org.apache.wicket.Session;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cmd2RdfSecureApplication extends WebApplication {
	private static final Logger LOG = LoggerFactory.getLogger(Cmd2RdfSecureApplication.class);
    private UserService userService = new UserService();
    private CookieService cookieService = new CookieService();
    private SessionProvider sessionProvider = new SessionProvider(userService, cookieService);
    
    public static ConfigReader cofigReader;
    public Cmd2RdfSecureApplication() {
    	 // In case of unhandled exception redirect it to a custom page
		  this.getRequestCycleListeners().add(new AbstractRequestCycleListener() {
			  @Override
		      public IRequestHandler onException(RequestCycle cycle, Exception e) {
				  return new RenderPageRequestHandler(new PageProvider(new ExceptionPage(e)));
			  }
		  });
    }
    
    @Override
    public Class<HomePage> getHomePage() {
        return HomePage.class;
    }

    @Override
    public void init() {
        super.init();
        try {
			cofigReader = new ConfigReader(Misc.getEnvValue("job_xml_path"));
		} catch (ConfigException e) {
			LOG.error(e.getMessage());
		}
        mountPage("/admin", AdminPage.class);
        mountPage("/how", HowItWorkPage.class);
        mountPage("/api", ApiPage.class);
        mountPage("/pub", PublicationPage.class);
        mountPage("/contact", ContactPage.class);
		getDebugSettings().setDevelopmentUtilitiesEnabled(false);
		getDebugSettings().setAjaxDebugModeEnabled(false);

    }

    public static Cmd2RdfSecureApplication get() {
        return (Cmd2RdfSecureApplication) WebApplication.get();
    }

    @Override
    public Session newSession(Request request, Response response) {
        return sessionProvider.createNewSession(request);
    }



    public UserService getUserService() {
        return userService;
    }

    public CookieService getCookieService() {
        return cookieService;
    }

    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }


}
