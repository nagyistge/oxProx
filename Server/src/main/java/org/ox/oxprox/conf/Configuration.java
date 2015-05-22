package org.ox.oxprox.conf;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 24/12/2013
 */

public class Configuration implements Serializable {

    @JsonProperty(value = "inum_prefix")
    private String inumPrefix;
    @JsonProperty(value = "discovery_script_dn")
    private String discoveryScriptDn;
    @JsonProperty(value = "op_base_dn")
    private String opBaseDn;
    @JsonProperty(value = "script_base_dn")
    private String scriptBaseDn;
    @JsonProperty(value = "secure_cookie")
    private Boolean secureCookie;
    @JsonProperty(value = "federation_op_base_dn")
    private String federationOpBaseDn;
    @JsonProperty(value = "federation_metadata_base_dn")
    private String federationMetadataBaseDn;
    @JsonProperty(value = "federation_rp_base_dn")
    private String federationRpBaseDn;
    @JsonProperty(value = "redirect_endpoint")
    private String redirectEndpoint;
    @JsonProperty(value = "client_base_dn")
    private String clientBaseDn;
    @JsonProperty(value = "token_base_dn")
    private String tokenBaseDn;
    @JsonProperty(value="discovery")
    private DiscoveryConf discoveryConf;

    public Configuration() {
    }

    public String getTokenBaseDn() {
        return tokenBaseDn;
    }

    public void setTokenBaseDn(String tokenBaseDn) {
        this.tokenBaseDn = tokenBaseDn;
    }

    public DiscoveryConf getDiscoveryConf() {
        return discoveryConf;
    }
    public void setDiscoveryConf(DiscoveryConf discoveryConf) {
        this.discoveryConf = discoveryConf;
    }

    public String getClientBaseDn() {
        return clientBaseDn;
    }

    public void setClientBaseDn(String clientBaseDn) {
        this.clientBaseDn = clientBaseDn;
    }

    public String getDiscoveryScriptDn() {
        return discoveryScriptDn;
    }

    public void setDiscoveryScriptDn(String discoveryScriptDn) {
        this.discoveryScriptDn = discoveryScriptDn;
    }

    public String getRedirectEndpoint() {
        return redirectEndpoint;
    }

    public void setRedirectEndpoint(String redirectEndpoint) {
        this.redirectEndpoint = redirectEndpoint;
    }

    public String getScriptBaseDn() {
        return scriptBaseDn;
    }

    public void setScriptBaseDn(String scriptBaseDn) {
        this.scriptBaseDn = scriptBaseDn;
    }

    public String getInumPrefix() {
        return inumPrefix;
    }

    public void setInumPrefix(String inumPrefix) {
        this.inumPrefix = inumPrefix;
    }

    public String getFederationOpBaseDn() {
        return federationOpBaseDn;
    }

    public void setFederationOpBaseDn(String federationOpBaseDn) {
        this.federationOpBaseDn = federationOpBaseDn;
    }

    public String getOpBaseDn() {
        return opBaseDn;
    }

    public void setOpBaseDn(String opBaseDn) {
        this.opBaseDn = opBaseDn;
    }

    public Boolean getSecureCookie() {
        return secureCookie;
    }

    public void setSecureCookie(Boolean p_secureCookie) {
        secureCookie = p_secureCookie;
    }

    public String getFederationMetadataBaseDn() {
        return federationMetadataBaseDn;
    }

    public void setFederationMetadataBaseDn(String federationMetadataBaseDn) {
        this.federationMetadataBaseDn = federationMetadataBaseDn;
    }

    public String getFederationRpBaseDn() {
        return federationRpBaseDn;
    }

    public void setFederationRpBaseDn(String federationRpBaseDn) {
        this.federationRpBaseDn = federationRpBaseDn;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Configuration");
        sb.append("{ inumPrefix='").append(inumPrefix).append('\'');
        sb.append(", discoveryScriptDn='").append(discoveryScriptDn).append('\'');
        sb.append(", opBaseDn='").append(opBaseDn).append('\'');
        sb.append(", scriptBaseDn='").append(scriptBaseDn).append('\'');
        sb.append(", secureCookie=").append(secureCookie);
        sb.append(", federationOpBaseDn='").append(federationOpBaseDn).append('\'');
        sb.append(", federationMetadataBaseDn='").append(federationMetadataBaseDn).append('\'');
        sb.append(", federationRpBaseDn='").append(federationRpBaseDn).append('\'');
        sb.append(", redirectEndpoint='").append(redirectEndpoint).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

