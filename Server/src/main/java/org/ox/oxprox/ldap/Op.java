package org.ox.oxprox.ldap;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapDN;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 04/03/2014
 */

@LdapEntry
@LdapObjectClass(values = {"top", "oxProxOp"})
public class Op {
    @LdapDN
    private String dn;
    @LdapAttribute(name = "inum")
    private String id;
    @LdapAttribute(name = "displayName")
    private String displayName;
    @LdapAttribute(name = "oxDomain")
    private String domain;
    @LdapAttribute(name = "c")
    private String country;
    @LdapAttribute(name = "l")
    private String city;
    @LdapAttribute(name = "oxId")
    private String opId;
    @LdapAttribute(name = "oxX509PEM")
    private String x509PEM;
    @LdapAttribute(name = "oxX509URL")
    private String x509URL;

    public String getDn() {
        return dn;
    }

    public void setDn(String p_dn) {
        dn = p_dn;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    public String getX509PEM() {
        return x509PEM;
    }

    public void setX509PEM(String x509PEM) {
        this.x509PEM = x509PEM;
    }

    public String getX509URL() {
        return x509URL;
    }

    public void setX509URL(String x509URL) {
        this.x509URL = x509URL;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LdapOp");
        sb.append("{displayName='").append(displayName).append('\'');
        sb.append(", dn='").append(dn).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", domain='").append(domain).append('\'');
        sb.append(", opId='").append(opId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
