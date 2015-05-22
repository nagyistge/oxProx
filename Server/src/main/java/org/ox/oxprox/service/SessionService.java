package org.ox.oxprox.service;

import org.ox.oxprox.external.ExternalOpDiscovery;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

public class SessionService {

    public static final String OP_DOMAIN = "op_domain";
    public static final String PARAMETER_MAP = "parameter_map";
    public static final String OP_INTERCEPTER = "op_intercepter";
    public static final String OP_CLIENT_ID = "op_client_id";

    private final HttpSession session;

    public SessionService(HttpSession session) {
        this.session = session;
    }

    public String getOpClientId() {
        final Object clientId = session.getAttribute(OP_CLIENT_ID);
        return clientId instanceof String ? (String) clientId : null;
    }

    public void setOpClientId(String clientId) {
        session.setAttribute(OP_CLIENT_ID, clientId);
    }

    public boolean hasOpDomain() {
        return getOpDomain() != null;
    }

    public String getOpDomain() {
        final Object opId = session.getAttribute(OP_DOMAIN);
        return opId instanceof String ? (String) opId : null;
    }

    public void setOpDomain(String opDomain) {
        session.setAttribute(OP_DOMAIN, opDomain);
    }

    public void setParameterMap(Map<String, String[]> parameterMap) {
        session.setAttribute(PARAMETER_MAP, parameterMap);
    }

    public Map<String, String[]> getParameterMap() {
        final Object map = session.getAttribute(PARAMETER_MAP);
        return map instanceof Map ? (Map) map : null;
    }

    public void setExternalOpInterceptor(ExternalOpDiscovery externalOpDiscovery) {
        session.setAttribute(OP_INTERCEPTER, externalOpDiscovery);
    }

    public ExternalOpDiscovery getExternalOpInterceptor() {
        final Object interceptor = session.getAttribute(OP_INTERCEPTER);
        return interceptor instanceof ExternalOpDiscovery ? (ExternalOpDiscovery) interceptor : null;
    }
}
