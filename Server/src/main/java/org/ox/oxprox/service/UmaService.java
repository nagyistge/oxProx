package org.ox.oxprox.service;

import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.ClientResponse;
import org.ox.oxprox.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.client.uma.AuthorizationRequestService;
import org.xdi.oxauth.client.uma.RequesterPermissionTokenService;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.AuthorizationResponse;
import org.xdi.oxauth.model.uma.MetadataConfiguration;
import org.xdi.oxauth.model.uma.RequesterPermissionTokenResponse;
import org.xdi.oxauth.model.uma.ResourceSetPermissionTicket;
import org.xdi.oxauth.model.uma.RptAuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 26/12/2013
 */

public class UmaService {

    private static final Logger LOG = LoggerFactory.getLogger(UmaService.class);

    public static final String SAML_TOKEN_COOKIE = "saml_token";
    public static final String RPT_COOKIE = "rpt";

    @Inject
    HttpService m_httpService;
    @Inject
    OicDiscoveryService discoveryService;
    @Inject
    AatService aatService;
    @Inject
    Configuration conf;

    public String getSamlToken(HttpServletRequest p_request, HttpServletResponse p_response) {
        return m_httpService.getValueWithCookieSet(p_request, p_response, SAML_TOKEN_COOKIE);
    }

    public String getRpt(HttpServletRequest p_request, HttpServletResponse p_response) {
        return m_httpService.getValueWithCookieSet(p_request, p_response, RPT_COOKIE);
    }

//    public String obtainRpt() {
//        final String aat = obtainAat();
//        return obtainRpt(aat);
//    }

    public String obtainRpt(String p_aat) {
        LOG.debug("Try to obtain RPT with AAT on AS, AAT: {}", p_aat);
        try {
            final String amHost = null;// todo
            final MetadataConfiguration umaDiscovery = discoveryService.getUmaDiscovery(amHost);
            final RequesterPermissionTokenService rptService = UmaClientFactory.instance().createRequesterPermissionTokenService(umaDiscovery);
            final RequesterPermissionTokenResponse rptResponse = null; // todo rptService.getRequesterPermissionToken("Bearer " + p_aat, conf.getAmHost());
            if (rptResponse != null && StringUtils.isNotBlank(rptResponse.getToken())) {
                final String rpt = rptResponse.getToken();
                LOG.debug("RPT is successfully obtained from AS. RPT: " + rpt);
                return rpt;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.debug("Failed to obtain RPT.");
        return null;
    }

    public boolean authorize(String p_targetUrl, String p_rpt) {
        try {
            if (StringUtils.isBlank(p_targetUrl)) {
                LOG.debug("Target url is blank.");
                return false;
            }
            if (StringUtils.isBlank(p_rpt)) {
                LOG.debug("RPT is blank.");
                return false;
            }

            // Start interaction with RS
            final URL url = new URL(p_targetUrl);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setAllowUserInteraction(false);
            connection.setInstanceFollowRedirects(false);
            connection.connect();

            final int respStatus = connection.getResponseCode();
            switch (respStatus) {
                case HttpStatus.SC_FORBIDDEN:
                    final String responseEntity = IOUtils.toString(connection.getInputStream());
                    LOG.trace("RS response entity: {}", responseEntity);
                    return authorizeAgainstRS(p_targetUrl, p_rpt, responseEntity);
                case HttpStatus.SC_OK:
                    return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    private boolean authorizeAgainstRS(String p_targetUrl, String p_rpt, String p_rsTicketResponseEntity) {
        try {
            final String amHost = null;// todo
            final MetadataConfiguration umaDiscovery = discoveryService.getUmaDiscovery(amHost);
            final ResourceSetPermissionTicket ticketWrapper = org.xdi.util.Util.createJsonMapper().readValue(p_rsTicketResponseEntity, ResourceSetPermissionTicket.class);
            final String ticket = ticketWrapper.getTicket();
            final RptAuthorizationRequest authorizationRequest = new RptAuthorizationRequest(p_rpt, ticket);

            LOG.debug("Try to authorize RPT with ticket: {}...", ticket);
            final AuthorizationRequestService rptAuthorizationService = UmaClientFactory.instance().createAuthorizationRequestService(umaDiscovery);
            final String aat = aatService.obtainAat(p_rpt);
            final ClientResponse<AuthorizationResponse> clientAuthorizationResponse = rptAuthorizationService.requestRptPermissionAuthorization(
                    "Bearer " + aat, amHost, authorizationRequest);
            final AuthorizationResponse authorizationResponse = clientAuthorizationResponse.getEntity();
            if (authorizationResponse != null) {
                LOG.trace("RPT is authorized. RPT: {}, targetUrl: ", p_rpt, p_targetUrl);
                return true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }
}
