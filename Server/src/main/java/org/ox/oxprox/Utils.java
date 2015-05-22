package org.ox.oxprox;

import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.RDN;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.python.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.util.INumGenerator;
import org.xdi.util.Util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 24/12/2013
 */

public class Utils {


    public static final Response INTERNAL_ERROR = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }


    public static String getCookieValue(HttpServletRequest p_request, String p_cookieName) {
        final Cookie[] cookies = p_request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies) && StringUtils.isNotBlank(p_cookieName)) {
            for (Cookie c : cookies) {
                if (p_cookieName.equalsIgnoreCase(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public static String urlDecode(String p_str) {
        if (StringUtils.isNotBlank(p_str)) {
            try {
                return URLDecoder.decode(p_str, Util.UTF8);
            } catch (UnsupportedEncodingException e) {
                LOG.trace(e.getMessage(), e);
            }
        }
        return p_str;
    }

    public static String getDiscoveryUrl(String p_amHost) {
        return String.format("https://%s/.well-known/openid-configuration", p_amHost);
    }

    public static String getUmaDiscoveryUrl(String p_amHost) {
        return String.format("https://%s/.well-known/uma-configuration", p_amHost);
    }

    public static Filter createAnyFilterFromDnList(String p_filterAttributeName, List<String> p_dnList) {
        try {
            if (p_dnList != null && !p_dnList.isEmpty()) {
                final StringBuilder sb = new StringBuilder("(|");
                for (String dn : p_dnList) {
                    final DN dnObj = new DN(dn);
                    final RDN rdn = dnObj.getRDN();
                    if (rdn.getAttributeNames()[0].equals(p_filterAttributeName)) {
                        final String[] values = rdn.getAttributeValues();
                        if (values != null && values.length == 1) {
                            sb.append("(");
                            sb.append(p_filterAttributeName).append("=");
                            sb.append(values[0]);
                            sb.append(")");
                        }
                    }
                }
                sb.append(")");
                final String filterAsString = sb.toString();
                LOG.trace("dnList: " + p_dnList + ", ldapFilter: " + filterAsString);
                return Filter.create(filterAsString);
            }
        } catch (LDAPException e) {
            LOG.trace(e.getMessage(), e);
        }
        return null;
    }

    public static String generateInum() {
        return INumGenerator.generate(2);
    }

    public static String mapAsString(Map<String, String[]> map) {
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(Joiner.on("").join(entry.getValue())).append(",");
        }
        return sb.toString();
    }
}
