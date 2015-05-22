package org.ox.oxprox.ws;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.external.ExternalOpDiscovery;
import org.ox.oxprox.ldap.Script;
import org.ox.oxprox.service.CookieService;
import org.ox.oxprox.service.PythonService;
import org.ox.oxprox.service.ScriptService;
import org.ox.oxprox.service.SessionService;
import org.python.core.PyObject;
import org.python.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.model.util.Util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

@Provider
@ServerInterceptor
public class OpPreProcessInterceptor implements /*ContainerRequestFilter*/ PreProcessInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(OpPreProcessInterceptor.class);

    private static final String PYTHON_CLASS_NAME = "PythonExternalOpDiscovery";

    @Inject
    Configuration conf;
    @Inject
    ScriptService scriptService;
    @Inject
    PythonService pythonService;
    @Inject
    CookieService cookieService;
    @Context
    HttpServletRequest servletRequest;

    // resteasy V 2
    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
        try {
            if (isWellKnown()) {
                LOG.trace("Skip well known request.");
                return null;
            }
            final SessionService sessionService = new SessionService(servletRequest.getSession());
            storeOpIfExist(sessionService);
            sessionService.setParameterMap(Maps.newHashMap(servletRequest.getParameterMap()));
            if (sessionService.hasOpDomain()) {
                LOG.trace("OpId exists");
                return null;
            } else {
                LOG.trace("OpId does not exist, redirect for discovery...");
                return (ServerResponse) Response.seeOther(createRedirectUri(sessionService)).build();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return (ServerResponse) Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    // resteasy V 3
//    @Override
//    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
//        try {
//            if (isWellKnown()) {
//                LOG.trace("Skip well known request.");
//                return;
//            }
//            final HttpSessionService sessionService = new HttpSessionService(servletRequest.getSession());
//            storeOpIfExist(sessionService);
//            if (sessionService.hasOpDomain()) {
//                LOG.trace("OpId exists");
//            } else {
//                LOG.trace("OpId does not exist, redirect for discovery...");
//                containerRequestContext.abortWith(Response.seeOther(createRedirectUri(sessionService)).build());
//            }
//        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
//        }
//    }

    private void storeOpIfExist(SessionService sessionService) {
        final String opDomain = servletRequest.getParameter("op_domain");
        if (!Strings.isNullOrEmpty(opDomain)) {
            sessionService.setOpDomain(opDomain);
        }
        final Cookie opCookie = cookieService.getHttpOpCookie(servletRequest.getCookies());
        if (opCookie != null && !Strings.isNullOrEmpty(opCookie.getValue())) {
            sessionService.setOpDomain(opCookie.getValue());
        }
    }

    private URI createRedirectUri(SessionService sessionService) throws URISyntaxException {
        final Script script = scriptService.identifyScript();
        if (script != null) {
            final ExternalOpDiscovery externalOpDiscovery = createPythonOpDiscovery(script.getOxScript());
            if (externalOpDiscovery != null) {
                sessionService.setExternalOpInterceptor(externalOpDiscovery);
//                final ExternalOpDiscoveryMode mode = externalOpDiscovery.getMode();

                String pageUrl = externalOpDiscovery.getPageForStep(1);
//                if (pageUrl.contains("?")) {
//                    pageUrl = pageUrl + "&" + servletRequest.getQueryString();
//                } else {
//                    pageUrl = pageUrl + "?" + servletRequest.getQueryString();
//                }
                return new URI(pageUrl);
            } else {
                LOG.error("Failed to create ExternalOpDiscovery");
            }
        } else {
            LOG.error("Unable to identify script.");
        }
        LOG.error("Failed to prepare python external op discovery script");
        throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Internal Server Error").build());
    }

    private ExternalOpDiscovery createPythonOpDiscovery(String pythonScript) {
        try {
            if (StringUtils.isNotBlank(pythonScript)) {
                InputStream bis = null;
                try {
                    bis = new ByteArrayInputStream(pythonScript.getBytes(Util.UTF8_STRING_ENCODING));
                    final ExternalOpDiscovery result = pythonService.loadPythonScript(bis, PYTHON_CLASS_NAME, ExternalOpDiscovery.class,
                            new PyObject[]{});
                    if (result == null) {
                        LOG.error("Python script does not implement ExternalOpDiscovery interface or script is corrupted.");
                    }
                    return result;
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                } finally {
                    IOUtils.closeQuietly(bis);
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    public boolean isWellKnown() {
        return servletRequest.getPathInfo().contains("/.well-known");
    }
}
