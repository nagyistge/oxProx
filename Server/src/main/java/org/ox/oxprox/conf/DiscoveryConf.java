package org.ox.oxprox.conf;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/04/2014
 */

public class DiscoveryConf {

    @JsonProperty(value = "issuer")
    private String issuer;
    @JsonProperty(value = "authorization_path")
    private String authorizationPath;
    @JsonProperty(value = "token_path")
    private String tokenPath;
    @JsonProperty(value = "userinfo_path")
    private String userinfoPath;
    @JsonProperty(value = "jwks_path")
    private String jwksPath;

    public String getJwksPath() {
        return jwksPath;
    }

    public void setJwksPath(String jwksPath) {
        this.jwksPath = jwksPath;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAuthorizationPath() {
        return authorizationPath;
    }

    public void setAuthorizationPath(String authorizationPath) {
        this.authorizationPath = authorizationPath;
    }

    public String getTokenPath() {
        return tokenPath;
    }

    public void setTokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
    }

    public String getUserinfoPath() {
        return userinfoPath;
    }

    public void setUserinfoPath(String userinfoPath) {
        this.userinfoPath = userinfoPath;
    }
}
