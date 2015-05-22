package org.ox.oxprox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 03/04/2014
 */

public class CookieService {

    private static final String OP_DOMAIN = "op_domain";

    private static final Logger LOG = LoggerFactory.getLogger(CookieService.class);

    public NewCookie createOpCookie(SessionService sessionService) {
        return new NewCookie(OP_DOMAIN, sessionService.getOpDomain(), "/", null, null, (int) TimeUnit.DAYS.toSeconds(4), false);
    }

    public Cookie createOpCookie(String opDomain) {
        return new Cookie(OP_DOMAIN, opDomain);
    }

    public Cookie getOpCookie(Cookie[] cookies) {
        return getCookieByName(cookies, OP_DOMAIN);
    }

    public javax.servlet.http.Cookie getHttpOpCookie(javax.servlet.http.Cookie[] cookies) {
        return getHttpCookieByName(cookies, OP_DOMAIN);
    }

    public Cookie getCookieByName(Cookie[] cookies, String cookieName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public javax.servlet.http.Cookie getHttpCookieByName(javax.servlet.http.Cookie[] cookies, String cookieName) {
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(cookieName)) {
                    return cookie;
                }
            }
        }
        return null;
    }


}
