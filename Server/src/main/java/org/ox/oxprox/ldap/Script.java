package org.ox.oxprox.ldap;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapDN;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;
import org.xdi.oxauth.model.common.ProgrammingLanguage;

import java.io.Serializable;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 05/03/2014
 */
@LdapEntry
@LdapObjectClass(values = {"top", "oxScript"})
public class Script implements Serializable {

    @LdapDN
    private String dn;
    @LdapAttribute(name = "inum")
    private String inum;
    @LdapAttribute(name = "oxScript")
    private String oxScript;
    @LdapAttribute(name = "oxScriptType")
    private String oxScriptType;

    public Script() {
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

    public String getOxScript() {
        return oxScript;
    }

    public void setOxScript(String oxScript) {
        this.oxScript = oxScript;
    }

    public String getOxScriptType() {
        return oxScriptType;
    }

    public void setOxScriptType(String oxScriptType) {
        this.oxScriptType = oxScriptType;
    }

    public ProgrammingLanguage getType() {
        return ProgrammingLanguage.fromString(oxScriptType);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Script");
        sb.append("{dn='").append(dn).append('\'');
        sb.append(", inum='").append(inum).append('\'');
        sb.append(", oxScript='").append(oxScript).append('\'');
        sb.append(", oxScriptType='").append(oxScriptType).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
