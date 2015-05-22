package org.ox.oxprox.testframework;

import org.xdi.oxauth.client.AuthorizationResponse;
import org.xdi.oxauth.client.TokenResponse;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2014
 */

public class TToken {

    private AuthorizationResponse authorizationResponse;
    private TokenResponse tokenResponse;

    public TToken(TokenResponse tokenResponse) {
        this.tokenResponse = tokenResponse;
    }

    public TToken(AuthorizationResponse authorizationResponse, TokenResponse tokenResponse) {
        this.authorizationResponse = authorizationResponse;
        this.tokenResponse = tokenResponse;
    }

    public AuthorizationResponse getAuthorizationResponse() {
        return authorizationResponse;
    }

    public void setAuthorizationResponse(AuthorizationResponse authorizationResponse) {
        this.authorizationResponse = authorizationResponse;
    }

    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    public void setTokenResponse(TokenResponse tokenResponse) {
        this.tokenResponse = tokenResponse;
    }
}
