package org.ox.oxprox.client;

import com.google.inject.Inject;
import junit.framework.Assert;
import org.ox.oxprox.TestAppModule;
import org.ox.oxprox.testframework.TToken;
import org.ox.oxprox.testframework.TTokenService;
import org.testng.annotations.Guice;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.ResponseType;

import java.util.Arrays;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 30/04/2014
 */

@Guice(modules = TestAppModule.class)
public class AuthorizationCodeWSTest {

    @Inject
    TTokenService tokenService;

    @Parameters({"realUserId", "realUserSecret", "realRedirectUris", "realRedirectUri", "clientId", "opDomain", "scope"})
    @Test
    public void requestAuthorizationCode(final String userId, final String userSecret, final String redirectUris,
                                         final String redirectUri, final String clientId, final String opDomain, String scope) {

        final TToken tToken = tokenService.authorizationCode(opDomain, scope, clientId, redirectUri, userId, userSecret,
                Arrays.asList(ResponseType.CODE), GrantType.AUTHORIZATION_CODE);
        Assert.assertNotNull(tToken);
    }
}
