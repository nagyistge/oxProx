package org.ox.oxprox.client;

import com.google.inject.Inject;
import org.ox.oxprox.TestAppModule;
import org.ox.oxprox.model.ws.DiscoveryEntity;
import org.ox.oxprox.service.CookieService;
import org.ox.oxprox.ws.DiscoveryWS;
import org.testng.annotations.Guice;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.client.AuthorizationRequest;
import org.xdi.oxauth.client.AuthorizationResponse;
import org.xdi.oxauth.client.AuthorizeClient;
import org.xdi.oxauth.model.common.Prompt;
import org.xdi.oxauth.model.common.ResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 27/05/2014
 */
@Guice(modules = TestAppModule.class)
public class ImplicitFlowWsTest {

    @Inject
    DiscoveryWS discoveryWS;
    @Inject
    CookieService cookieService;

    @Parameters({"realUserId", "realUserSecret", "realRedirectUris", "realRedirectUri", "clientId", "opDomain", "scope"})
    @Test
    public void implicitFlow(final String userId, final String userSecret, final String redirectUris,
                             final String redirectUri, final String clientId, final String opDomain, String scope) {

        final DiscoveryEntity discoveryEntity = discoveryWS.getDiscoveryEntity();

        List<ResponseType> responseTypes = new ArrayList<ResponseType>();
        responseTypes.add(ResponseType.TOKEN);
        responseTypes.add(ResponseType.ID_TOKEN);
        List<String> scopes = new ArrayList<String>();
        scopes.add("openid");
        scopes.add("profile");
        scopes.add("address");
        scopes.add("email");
        String nonce = UUID.randomUUID().toString();
        String state = "af0ifjsldkj";

        AuthorizationRequest request = new AuthorizationRequest(responseTypes, clientId, scopes, redirectUri, nonce);
        request.setState(state);
        request.setAuthUsername(userId);
        request.setAuthPassword(userSecret);
        request.getPrompts().add(Prompt.NONE);

        AuthorizeClient authorizeClient = new AuthorizeClient(discoveryEntity.getAuthorizationEndpoint());
        authorizeClient.setRequest(request);
        authorizeClient.getCookies().add(cookieService.createOpCookie(opDomain));

        AuthorizationResponse response1 = authorizeClient.exec();

        BaseTest.showClient(authorizeClient);
        assertEquals(response1.getStatus(), 302, "Unexpected response code: " + response1.getStatus());
        assertNotNull(response1.getLocation(), "The location is null");
        assertNotNull(response1.getAccessToken(), "The access token is null");
        assertNotNull(response1.getState(), "The state is null");
        assertNotNull(response1.getTokenType(), "The token type is null");
        assertNotNull(response1.getExpiresIn(), "The expires in value is null");
        assertNotNull(response1.getScope(), "The scope must be null");
        assertNotNull(response1.getIdToken(), "The id token must be null");
    }
}
