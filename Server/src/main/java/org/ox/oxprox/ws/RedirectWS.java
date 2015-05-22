package org.ox.oxprox.ws;

import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.service.CookieService;
import org.ox.oxprox.service.HttpService;
import org.ox.oxprox.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Builds redirect uri for RP based on fragment from OP.
 *
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/02/2014
 */

@Path("/rest")
public class RedirectWS {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectWS.class);

    @Inject
    Configuration conf;
    @Inject
    CookieService cookieService;
    @Inject
    HttpService httpService;

    @POST
    @Path("/redirect")
    @Produces({MediaType.TEXT_PLAIN})
    public Response redirect(@Context HttpServletRequest httpRequest) {
        try {
            final String fragment = IOUtils.toString(httpRequest.getReader());
            if (StringUtils.isNotBlank(fragment)) {
                LOG.trace("Fragment: {}", fragment);
                final SessionService sessionService = new SessionService(httpRequest.getSession());

                final String rpRedirectUri = httpService.getRedirectUriParameter(sessionService.getParameterMap());
                final String url = rpRedirectUri + fragment;

                LOG.debug("Redirect to {}", url);
                return Response.ok(url).cookie(cookieService.createOpCookie(sessionService)).build();
            } else {
                LOG.error("Fragment is blank.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Response.serverError().build();
    }
}
