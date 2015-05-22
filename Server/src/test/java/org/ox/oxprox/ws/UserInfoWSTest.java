package org.ox.oxprox.ws;

import com.google.inject.Inject;
import junit.framework.Assert;
import org.ox.oxprox.TestAppModule;
import org.ox.oxprox.service.SessionService;
import org.ox.oxprox.testframework.THttpSession;
import org.ox.oxprox.testframework.TToken;
import org.ox.oxprox.testframework.TTokenService;
import org.ox.oxprox.testframework.TUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Guice;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.ResponseType;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/04/2014
 */
@Guice(modules = TestAppModule.class)
public class UserInfoWSTest {

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoWSTest.class);

    @Inject
    TTokenService tokenService;
    @Inject
    UserInfoWS userInfoWS;

    @Parameters({"realUserId", "realUserSecret", "realRedirectUris", "realRedirectUri", "clientId", "opDomain", "scope"})
    @Test
    public void userInfo(final String userId, final String userSecret, final String redirectUris,
                         final String redirectUri, final String clientId, final String opDomain, String scope) {
        final SessionService sessionService = new SessionService(new THttpSession());
        sessionService.setOpDomain(opDomain);

        final TToken tToken = tokenService.authorizationCode(opDomain, scope, clientId, redirectUri, userId, userSecret,
                Arrays.asList(ResponseType.CODE), GrantType.AUTHORIZATION_CODE);
        final String accessToken = tToken.getTokenResponse().getAccessToken();
        final Response response = userInfoWS.requestUserInfo(accessToken, sessionService);

        Assert.assertNotNull(response);
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());

        LOG.trace((String) response.getEntity());
        TUserInfo userInfo = new TUserInfo((String) response.getEntity());
        Assert.assertNotNull(userInfo.getClaims());
        Assert.assertTrue(!userInfo.getClaims().isEmpty());

        final List<String> value = userInfo.getClaims().get("mycountry");// rp claim re-mapped by oxProx (original name country)

        Assert.assertTrue(value != null && !value.isEmpty());
    }
}
