package nl.knaw.dans.cmd2rdf.webui.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

public class CookieService {

    public Cookie loadCookie(Request request, String cookieName) {

        List<Cookie> cookies = ((WebRequest) request).getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }

        return null;
    }

    public void saveCookie(Response response, String cookieName, String cookieValue, int expiryTimeInDays) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge((int) TimeUnit.DAYS.toSeconds(expiryTimeInDays));
        ((WebResponse)response).addCookie(cookie);
    }

    public void removeCookieIfPresent(Request request, Response response, String cookieName) {
        Cookie cookie = loadCookie(request, cookieName);

        if(cookie != null) {
            ((WebResponse)response).clearCookie(cookie);
        }
    }
}