package org.ox.oxprox.beans;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.ox.oxprox.external.AllowedOpRuleContext;
import org.ox.oxprox.external.ExternalOpDiscovery;
import org.ox.oxprox.ldap.Op;
import org.ox.oxprox.service.SessionService;
import org.ox.oxprox.service.OpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/02/2014
 */

@ManagedBean(name = "discovery", eager = true)
@ViewScoped
public class DiscoveryBean extends BaseBean implements java.io.Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(DiscoveryBean.class);

    private OpService opService;

    public List<Op> getOpList() {
        final List<Op> opList = Lists.newArrayList();

        try {
            final ExternalOpDiscovery externalOpDiscovery = getExternalOpDiscovery();
            final List<Op> allOps = opService.getAllOps();
            if (externalOpDiscovery != null) {
                final AllowedOpRuleContext context = new AllowedOpRuleContext(getHttpRequest(), allOps);
                for (Op op : allOps) {
                    if (externalOpDiscovery.isAllowed(op, context)) {
                        opList.add(op);
                    }
                }
            } else {
                LOG.warn("Failed to find externalOpDiscovery, show all OPs.");
                opList.addAll(allOps);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return opList;
    }

    @Inject
    public void setOpService(OpService opService) {
        this.opService = opService;
    }

    public String showPage() {
        try {
            final SessionService sessionService = getSessionService();
            if (sessionService != null) {
                final ExternalOpDiscovery externalOpDiscovery = sessionService.getExternalOpInterceptor();
                if (externalOpDiscovery != null) {
                    int currentStepAsInt = getCurrentStep();
                    currentStepAsInt++;
                    final String pageForStep = externalOpDiscovery.getPageForStep(currentStepAsInt);
                    final String queryString = queryString();
                    return pageForStep + "?faces-redirect=true&" + queryString;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        // default
        return "/discovery";
    }

    private String queryString() {
        final Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        final StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith("j_") || key.startsWith("javax.")) { // skip jsf stuff
                continue;
            }
            sb.append("&").append(key).append("=").append(entry.getValue());
        }
        return sb.toString();
    }

    public int getCurrentStep() {
//        if (currentStep == -1) {
//            try {
//                currentStep = ;
//            } catch (Exception e) {
//                LOG.trace(e.getMessage(), e);
//            }
//        }
        return Integer.parseInt(getRequestParameter("currentStep"));
    }

    public HttpServletRequest getHttpRequest() {
        final FacesContext context = FacesContext.getCurrentInstance();
        if (context != null) {
            final Object request = context.getExternalContext().getRequest();
            if (request instanceof HttpServletRequest) {
                return (HttpServletRequest) request;
            }
        }
        return null;
    }

    public ExternalOpDiscovery getExternalOpDiscovery() {
        final SessionService sessionService = getSessionService();
        if (sessionService != null) {
            return sessionService.getExternalOpInterceptor();
        }
        return null;
    }

    public SessionService getSessionService() {
        final HttpSession session = getHttpRequest().getSession(false);
        if (session != null) {
            return new SessionService(session);
        }
        return null;
    }
}
