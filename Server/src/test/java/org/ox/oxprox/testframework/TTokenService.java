package org.ox.oxprox.testframework;

import com.google.inject.Inject;
import org.ox.oxprox.model.ws.DiscoveryEntity;
import org.ox.oxprox.service.CookieService;
import org.ox.oxprox.ws.DiscoveryWS;
import org.xdi.oxauth.BaseTest;
import org.xdi.oxauth.client.AuthorizationRequest;
import org.xdi.oxauth.client.AuthorizationResponse;
import org.xdi.oxauth.client.AuthorizeClient;
import org.xdi.oxauth.client.TokenClient;
import org.xdi.oxauth.client.TokenRequest;
import org.xdi.oxauth.client.TokenResponse;
import org.xdi.oxauth.model.common.AuthenticationMethod;
import org.xdi.oxauth.model.common.GrantType;
import org.xdi.oxauth.model.common.Prompt;
import org.xdi.oxauth.model.common.ResponseType;
import org.xdi.oxauth.model.util.Util;

import java.util.List;

import static org.testng.Assert.*;

/**
 *
 *
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2014
 */

public class TTokenService {

    @Inject
    DiscoveryWS discoveryWS;
    @Inject
    CookieService cookieService;

    public TToken authorizationCode(String opDomain, String scopesAsString, String clientId, String redirectUri, String userId, String userSecret, List<ResponseType> responseTypes, GrantType grantType) {
        final DiscoveryEntity discoveryEntity = discoveryWS.getDiscoveryEntity();

        List<String> scopes = Util.splittedStringAsList(scopesAsString, " ");

        AuthorizationRequest request = new AuthorizationRequest(responseTypes, clientId, scopes, redirectUri, null);
        request.setState("STATE_XYZ");
        request.setAuthUsername(userId);
        request.setAuthPassword(userSecret);
        request.getPrompts().add(Prompt.NONE);

        AuthorizeClient authorizeClient = new AuthorizeClient(discoveryEntity.getAuthorizationEndpoint());
        authorizeClient.setRequest(request);
        authorizeClient.getCookies().add(cookieService.createOpCookie(opDomain));

        AuthorizationResponse authorizationResponse = authorizeClient.exec();

        BaseTest.showClient(authorizeClient);
        assertEquals(authorizationResponse.getStatus(), 302, "Unexpected response code: " + authorizationResponse.getStatus());
        assertNotNull(authorizationResponse.getLocation(), "The location is null");
        assertNotNull(authorizationResponse.getCode(), "The authorization code is null");
        assertNotNull(authorizationResponse.getState(), "The state is null");
        assertNotNull(authorizationResponse.getScope(), "The scope is null");

        // check whether call was really proxied
        // 1. location should match original rp redirect_uri
        // 2. scopes must be from rp (not op)
        assertTrue(authorizationResponse.getLocation().contains(redirectUri));
        assertTrue(authorizationResponse.getScope().contains(scopesAsString));

        // 3. Request access token using the authorization code.
        TokenRequest tokenRequest = new TokenRequest(grantType);
        tokenRequest.setCode(authorizationResponse.getCode());
        tokenRequest.setRedirectUri(redirectUri);
        tokenRequest.setAuthUsername(clientId);

        // ATTENTION : we do not specify client password here because real OP password is going to be mapped on oxProx side
        // here we are forced to specify dummy password to force oxAuth client construct Authorization header
        tokenRequest.setAuthPassword("dummy");
        tokenRequest.setAuthenticationMethod(AuthenticationMethod.CLIENT_SECRET_BASIC);

        TokenClient tokenClient = new TokenClient(discoveryEntity.getTokenEndpoint());
        tokenClient.getCookies().add(cookieService.createOpCookie(opDomain));
        tokenClient.setRequest(tokenRequest);
        TokenResponse tokenResponse = tokenClient.exec();

        BaseTest.showClient(tokenClient);
        assertEquals(tokenResponse.getStatus(), 200, "Unexpected response code: " + tokenResponse.getStatus());
        assertNotNull(tokenResponse.getEntity(), "The entity is null");
        assertNotNull(tokenResponse.getAccessToken(), "The access token is null");
        assertNotNull(tokenResponse.getExpiresIn(), "The expires in value is null");
        assertNotNull(tokenResponse.getTokenType(), "The token type is null");
        assertNotNull(tokenResponse.getRefreshToken(), "The refresh token is null");

        return new TToken(authorizationResponse, tokenResponse);
    }
}
