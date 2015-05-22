package org.ox.oxprox.admin.shared;

import org.ox.oxprox.model.gwt.ClientMapping;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class Client implements Serializable {

    private String dn;
    private String inum;
    private String displayName;
    private Map<String, String> scopeMappingMap;
    private Map<String, String> claimMappingMap;
    private ClientMapping clientMapping;

    public Client() {
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public ClientMapping getClientMapping() {
        return clientMapping;
    }

    public void setClientMapping(ClientMapping clientMapping) {
        this.clientMapping = clientMapping;
    }

    public Map<String, String> getClaimMappingMap() {
        return claimMappingMap;
    }

    public void setClaimMappingMap(Map<String, String> claimMappingMap) {
        this.claimMappingMap = claimMappingMap;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getScopeMappingMap() {
        return scopeMappingMap;
    }

    public void setScopeMappingMap(Map<String, String> scopeMappingMap) {
        this.scopeMappingMap = scopeMappingMap;
    }
}
