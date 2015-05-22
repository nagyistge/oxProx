package org.ox.oxprox.ldap;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapDN;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;
import org.ox.oxprox.model.gwt.ClientMapping;
import org.ox.oxprox.model.server.ClientMappingUtils;
import org.ox.oxprox.service.ClientService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/03/2014
 */
@LdapEntry
@LdapObjectClass(values = {"top", "oxProxClient"})
public class oxProxClient {

    @LdapDN
    private String dn;
    @LdapAttribute(name = "inum")
    private String clientId;
    @LdapAttribute(name = "displayName")
    private String displayName;
    @LdapAttribute(name = "oxProxyToOpClientMapping")
    private String proxyToOpClientMapping;
    @LdapAttribute(name = "oxProxyScope")
    private List<String> scopeMapping;
    @LdapAttribute(name = "oxProxyClaimMapping")
    private List<String> claimMapping;

    private final AtomicReference<ClientMapping> clientMappingMap = new AtomicReference<ClientMapping>();
    private final AtomicReference<Map<String, String>> scopeMappingMap = new AtomicReference<Map<String, String>>();
    private final AtomicReference<Map<String, String>> claimMappingMap = new AtomicReference<Map<String, String>>();

    public oxProxClient() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public List<String> getScopeMapping() {
        return scopeMapping;
    }

    public void setScopeMapping(List<String> scopeMapping) {
        this.scopeMapping = scopeMapping;
    }

    public String getProxyToOpClientMapping() {
        return proxyToOpClientMapping;
    }

    public void setProxyToOpClientMapping(String proxyToOpClientMapping) {
        this.proxyToOpClientMapping = proxyToOpClientMapping;
    }

    public ClientMapping getClientMapping() throws IOException {
        final ClientMapping map = clientMappingMap.get();
        if (map == null) {
            final ClientMapping parsed = ClientMappingUtils.parse(proxyToOpClientMapping);
            clientMappingMap.set(parsed);
            return parsed;
        }
        return map;
    }

    public List<String> getClaimMapping() {
        return claimMapping;
    }

    public void setClaimMapping(List<String> claimMapping) {
        this.claimMapping = claimMapping;
    }

    public Map<String, String> getScopeMappingMap() {
        final Map<String, String> map = scopeMappingMap.get();
        if (map == null) {
            final Map<String, String> parsedMap = ClientService.parseMap(scopeMapping);
            scopeMappingMap.set(parsedMap);
            return parsedMap;
        }
        return map;
    }

    public Map<String, String> getClaimMappingMap() {
        final Map<String, String> map = claimMappingMap.get();
        if (map == null) {
            final Map<String, String> parsedMap = ClientService.parseMap(claimMapping);
            claimMappingMap.set(parsedMap);
            return parsedMap;
        }
        return map;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("oxProxClient");
        sb.append("{clientId='").append(clientId).append('\'');
        sb.append(", dn='").append(dn).append('\'');
        sb.append(", proxyToOpClientMapping=").append(proxyToOpClientMapping);
        sb.append('}');
        return sb.toString();
    }
}
