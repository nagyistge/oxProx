package org.ox.oxprox.conf;

import org.ox.oxprox.model.ws.DiscoveryEntity;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/04/2014
 */

public class DiscoverEntityBuilder {

    private final DiscoveryConf discoveryConf;

    public DiscoverEntityBuilder(Configuration configuration) {
        this.discoveryConf = configuration.getDiscoveryConf();
    }

    public DiscoveryEntity build() {
        final DiscoveryEntity entity = new DiscoveryEntity();
        entity.setIssuer(discoveryConf.getIssuer());
        entity.setAuthorizationEndpoint(endpoint(discoveryConf.getAuthorizationPath()));
        entity.setTokenEndpoint(endpoint(discoveryConf.getTokenPath()));
        entity.setUserinfoEndpoint(endpoint(discoveryConf.getUserinfoPath()));
        entity.setJwksUri(endpoint(discoveryConf.getJwksPath()));
        return entity;
    }

    private String endpoint(String authorizationPath) {
        return discoveryConf.getIssuer() + authorizationPath;
    }
}
