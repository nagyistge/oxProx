package org.ox.oxprox.ldap;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapDN;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

import java.util.Date;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2014
 */
@LdapEntry
@LdapObjectClass(values = {"top", "oxProxAccessToken"})
public class AccessTokenMap {
    @LdapDN
    private String dn;
    @LdapAttribute(name = "oxProxyAccessToken")
    private String accessToken;
    @LdapAttribute(name = "oxProxyClientId")
    private String clientId;
    @LdapAttribute(name= "oxAuthCreation")
    private Date creationDate;
    @LdapAttribute(name= "oxAuthExpiration")
    private Date expirationDate;

    public AccessTokenMap() {
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public AccessTokenMap(String accessToken, String clientId) {
        this.accessToken = accessToken;
        this.clientId = clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
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
}
