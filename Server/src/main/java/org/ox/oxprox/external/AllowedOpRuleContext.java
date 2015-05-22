package org.ox.oxprox.external;

import org.apache.commons.net.util.SubnetUtils;
import org.ox.oxprox.ldap.Op;
import org.ox.oxprox.service.SessionService;
import org.xdi.oxauth.model.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 05/03/2014
 */

public class AllowedOpRuleContext {

    private final HttpServletRequest httpRequest;
    private final List<Op> opList;

    public AllowedOpRuleContext(HttpServletRequest httpRequest, List<Op> opList) {
        this.httpRequest = httpRequest;
        this.opList = opList;
    }

    public List<Op> getOpList() {
        return opList;
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public SessionService getSessionService() {
        return httpRequest != null && httpRequest.getSession(false) != null ? new SessionService(httpRequest.getSession(false)) : null;
    }

    public String getIpAddress() {
        return httpRequest != null ? httpRequest.getRemoteAddr() : "";
    }

    public boolean isInNetwork(String p_cidrNotation) {
        final String ip = getIpAddress();
        if (Util.allNotBlank(ip, p_cidrNotation)) {
            final SubnetUtils utils = new SubnetUtils(p_cidrNotation);
            return utils.getInfo().isInRange(ip);
        }
        return false;
    }
}
