package org.ox.oxprox.ws;

import com.google.inject.Inject;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.ox.oxprox.TestAppModule;
import org.ox.oxprox.model.ws.DiscoveryEntity;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/04/2014
 */

@Guice(modules = TestAppModule.class)
public class DiscoveryWSTest {

    @Inject
    DiscoveryWS discoveryWS;

    @Test
    public void discovery() {
        final DiscoveryEntity discoveryEntity = discoveryWS.getDiscoveryEntity();

        Assert.assertNotNull(discoveryEntity);
        notBlank(discoveryEntity.getIssuer());
        notBlank(discoveryEntity.getAuthorizationEndpoint());
        notBlank(discoveryEntity.getTokenEndpoint());
        notBlank(discoveryEntity.getUserinfoEndpoint());
        notBlank(discoveryEntity.getJwksUri());
    }

    private static void notBlank(String string) {
        Assert.assertTrue(StringUtils.isNotBlank(string));
    }
}
