package org.ox.oxprox.ws;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.ox.oxprox.TestAppModule;
import org.ox.oxprox.service.SessionService;
import org.ox.oxprox.testframework.THttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 16/04/2014
 */

@Guice(modules = TestAppModule.class)
public class AuthorizationWSTest {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationWSTest.class);

    @Inject
    AuthorizationWS authorizationWS;

    @Test
    @Parameters({"opDomain", "clientId", "scope", "opClientId"})
    public void authorization(String opDomain, String clientId, String scope, String opClientId) throws UnsupportedEncodingException {
        final Map<String, String[]> parameters = Maps.newHashMap();
        parameters.put("client_id", new String[]{clientId});
        parameters.put("scope", new String[]{scope});

        final SessionService sessionService = new SessionService(new THttpSession());
        sessionService.setOpDomain(opDomain);
        sessionService.setParameterMap(parameters);

        final Response response = authorizationWS.requestAuthorization(sessionService, "");

        Assert.assertNotNull(response);

        final String locationString = response.getMetadata().getFirst("Location").toString();
        LOG.trace("Response location " + locationString);
        Assert.assertEquals(sessionService.getOpClientId(), opClientId);
        Assert.assertEquals(response.getStatus(), Response.Status.SEE_OTHER.getStatusCode());
        Assert.assertTrue(StringUtils.contains(locationString, "address")); // scope re-mapped
        Assert.assertTrue(StringUtils.contains(locationString, URLEncoder.encode(opClientId, "UTF-8"))); // op client_id present

    }
}
