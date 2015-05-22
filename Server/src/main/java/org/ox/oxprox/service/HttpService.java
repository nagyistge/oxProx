package org.ox.oxprox.service;

import com.google.inject.Inject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.ox.oxprox.Utils;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.model.ws.ResponseErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.model.util.Util;
import org.xdi.util.Pair;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/12/2013
 */

public class HttpService {

    private static final Logger LOG = LoggerFactory.getLogger(HttpService.class);

    @Inject
    Configuration conf;
    @Inject
    ErrorService errorService;

    public HttpService() {
    }

    public Pair<String, String> parseBasicAuthorizationHeader(String authorization) throws IOException {
        if (authorization != null && authorization.startsWith("Basic ")) {
            String base64Token = authorization.substring("Basic ".length());
            String token = new String(org.jboss.resteasy.util.Base64.decode(base64Token), org.xdi.oxauth.model.util.Util.UTF8_STRING_ENCODING);

            int delim = token.indexOf(":");

            if (delim != -1) {
                String username = token.substring(0, delim);
                String password = token.substring(delim + 1);
                return new Pair<String, String>(username, password);
            }
        }
        throw new WebApplicationException(errorService.response(Response.Status.BAD_REQUEST, ResponseErrorType.NO_AUTHORIZATION_HEADER));
    }

    public void setCookieWithRootPath(HttpServletResponse p_response, String p_cookieName, String p_cookieValue) {
        setCookieWithPath(p_response, p_cookieName, p_cookieValue, "/");
    }

    public void setCookieWithPath(HttpServletResponse p_response, String p_cookieName, String p_cookieValue, String p_cookiePath) {
        final Boolean isSecureCookie = conf.getSecureCookie();
        final Cookie cookie = new Cookie(p_cookieName, p_cookieValue);
        //                cookie.setHttpOnly(true); // available only with servlet 3.0!
        cookie.setSecure(isSecureCookie != null && isSecureCookie);
        cookie.setPath(p_cookiePath);
        p_response.addCookie(cookie);
    }

    public String getValueWithCookieSet(HttpServletRequest p_request, HttpServletResponse p_response, String p_cookieName) {
        final String v = Utils.getCookieValue(p_request, p_cookieName);
        if (StringUtils.isBlank(v)) {
            final String paramValue = p_request.getParameter(p_cookieName);
            if (StringUtils.isNotBlank(paramValue)) {
                setCookieWithRootPath(p_response, p_cookieName, paramValue);
                return paramValue;
            }
        }
        return v;
    }

    public String getParameterByName(Map<String, String[]> parametersMap, String parameterName) {
        if (parametersMap != null) {
            final String[] strings = parametersMap.get(parameterName);
            if (strings != null && strings.length > 0) {
                return strings[0];
            }
        }
        return null;
    }

    public boolean contains(String[] params, String toCheck) {
        if (params != null && toCheck != null) {
            for (String param : params) {
                if (param.equalsIgnoreCase(toCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getRedirectUriParameter(Map<String, String[]> parametersMap) {
        return getParameterByName(parametersMap, "redirect_uri");
    }

    public String formParameterValue(String[] value) {
        return formParameterValue(Arrays.asList(value));
    }

    public String formParameterValue(List<String> value) {
        final StringBuilder sb = new StringBuilder();
        if (value != null) {
            for (String s : value) {
                sb.append(s).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public String getEncodedCredentials(String username, String password) {
        try {
            return Base64.encodeBase64String(Util.getBytes(username + ":" + password));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
