package org.ox.oxprox.service;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.model.server.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.client.OpenIdConfigurationResponse;
import org.xdi.oxauth.client.TokenClient;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.model.util.Util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 31/12/2013
 */

public class AatService {

    private static final Logger LOG = LoggerFactory.getLogger(AatService.class);

    private static final int AAT_CACHE_LIVE_TIME_IN_MINUTES = 60;

    // key - rpt, value - aat
    private final ConcurrentMap<String, String> m_cacheMap = new ConcurrentHashMap<String, String>();

    @Inject
    OicDiscoveryService discoveryService;
    @Inject
    Configuration conf;

    public String obtainAat(final String p_rpt) {
        final String aat = m_cacheMap.get(p_rpt);
        if (StringUtils.isNotBlank(aat)) {
            return aat;
        }
        final String newAat = obtainAat();
        if (StringUtils.isNotBlank(newAat)) {
            putInCache(newAat, p_rpt);
            return newAat;
        }
        return null;
    }

    public String obtainAat() {
        LOG.debug("Try to obtain AAT...");

        final String amHost = null; // todo
        final OpenIdConfigurationResponse discoveryResponse = discoveryService.getDiscoveryResponse(amHost);
        final TokenClient tokenClient = new TokenClient(discoveryResponse.getTokenEndpoint());
        final TokenResponse response = null;// todo tokenClient.execClientCredentialsGrant(UmaScopeType.AUTHORIZATION.getValue() + " openid", conf.getClientId(), conf.getClientSecret());
        if (response != null) {
            final String patToken = response.getAccessToken();
            if (Util.allNotBlank(patToken)) {
//                final String refreshToken = response.getRefreshToken();
//                final Integer expiresIn = response.getExpiresIn();
                LOG.debug("AAT is successfully obtained.");
                return patToken;
            }
        }
        LOG.debug("Failed to obtain AAT.");
        return null;
    }

    public void putInCache(String p_aat, final String p_rpt) {
        m_cacheMap.put(p_rpt, p_aat);

        // remove from cache after 60 minutes
        ModelUtils.createExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                m_cacheMap.remove(p_rpt);
            }
        }, AAT_CACHE_LIVE_TIME_IN_MINUTES, TimeUnit.MINUTES);
    }
}
