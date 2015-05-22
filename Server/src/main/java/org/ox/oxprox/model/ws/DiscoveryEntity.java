package org.ox.oxprox.model.ws;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/04/2014
 */

public class DiscoveryEntity {

    @JsonProperty(value = "issuer")
    private String issuer;
    @JsonProperty(value = "authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty(value = "token_endpoint")
    private String tokenEndpoint;
    @JsonProperty(value = "userinfo_endpoint")
    private String userinfoEndpoint;
    @JsonProperty(value = "jwks_uri")
    private String jwksUri;

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    public void setUserinfoEndpoint(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
