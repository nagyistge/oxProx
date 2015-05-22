package org.ox.oxprox.service;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.ox.oxprox.Utils;
import org.ox.oxprox.model.ws.ResponseErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.client.OpenIdConfigurationClient;
import org.xdi.oxauth.client.OpenIdConfigurationResponse;
import org.xdi.oxauth.client.uma.UmaClientFactory;
import org.xdi.oxauth.model.uma.MetadataConfiguration;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 26/12/2013
 */

public class OicDiscoveryService {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(OicDiscoveryService.class);

    private final ConcurrentMap<String, OpenIdConfigurationResponse> m_map = new ConcurrentHashMap<String, OpenIdConfigurationResponse>();
    private final ConcurrentMap<String, MetadataConfiguration> m_umaMap = new ConcurrentHashMap<String, MetadataConfiguration>();

    @Inject
    ErrorService errorService;

    public OpenIdConfigurationResponse getDiscoveryResponseByAmHost(String amHost) throws WebApplicationException {
        final String discoveryUrl = Utils.getDiscoveryUrl(amHost);
        final OpenIdConfigurationResponse discoveryResponse = getDiscoveryResponse(discoveryUrl);
        if (discoveryResponse == null) {
            throw new WebApplicationException(errorService.response(Response.Status.INTERNAL_SERVER_ERROR, ResponseErrorType.NO_DISCOVERY));
        }
        return discoveryResponse;
    }

    public OpenIdConfigurationResponse getDiscoveryResponse(String p_discoveryUrl) {
        try {
            if (StringUtils.isNotBlank(p_discoveryUrl)) {
                final OpenIdConfigurationResponse r = m_map.get(p_discoveryUrl);
                if (r != null) {
                    return r;
                }
                final OpenIdConfigurationClient client = new OpenIdConfigurationClient(p_discoveryUrl);
                final OpenIdConfigurationResponse response = client.execOpenIdConfiguration();
                LOG.trace("Discovery response: {} ", response.getEntity());
                if (StringUtils.isNotBlank(response.getEntity())) {
                    m_map.put(p_discoveryUrl, response);
                } else {
                    LOG.error("No response from discovery!");
                }
                return response;
            } else {
                LOG.error("Discovery URL is null or blank.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.error("Unable to fetch discovery information for url: {}", p_discoveryUrl);
        return null;
    }

    public MetadataConfiguration getUmaDiscoveryByAmHost(String amHost) {
        final String discoveryUrl = Utils.getUmaDiscoveryUrl(amHost);
        return getUmaDiscovery(discoveryUrl);
    }

    public MetadataConfiguration getUmaDiscovery(String p_umaDiscoveryUrl) {
        try {
            if (StringUtils.isNotBlank(p_umaDiscoveryUrl)) {
                final MetadataConfiguration r = m_umaMap.get(p_umaDiscoveryUrl);
                if (r != null) {
                    return r;
                }
                final MetadataConfiguration response = UmaClientFactory.instance().createMetaDataConfigurationService(p_umaDiscoveryUrl).getMetadataConfiguration();
                LOG.trace("Uma discovery response: {} ", response);
                m_umaMap.put(p_umaDiscoveryUrl, response);
                return response;
            } else {
                LOG.error("Uma discovery URL is null or blank.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.error("Unable to fetch UMA discovery information for url: {}", p_umaDiscoveryUrl);
        return null;
    }
}
