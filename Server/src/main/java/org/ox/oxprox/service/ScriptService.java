package org.ox.oxprox.service;

import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Filter;
import org.apache.commons.io.IOUtils;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.ldap.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.model.common.ProgrammingLanguage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

public class ScriptService {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptService.class);

    // todo
//        private static final String DEFAULT_SCRIPT_FILE_NAME = "PythonExternalOpDiscovery.py";
    private static final String DEFAULT_SCRIPT_FILE_NAME = "ByCountryExternalOpDiscovery.py";

    @Inject
    LdapEntryManager ldapManager;
    @Inject
    Configuration conf;

    public List<Script> getScripts() {
        try {
            final Filter filter = Filter.create("&(inum=*)");
            return ldapManager.findEntries(conf.getScriptBaseDn(), Script.class, filter);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public Script getScript(String dn) {
        try {
            return ldapManager.find(Script.class, dn);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public Script getConfScript() {
        return getScript(conf.getDiscoveryScriptDn());
    }

    public Script identifyScript() {
        final Script confScript = null; // todo getConfScript();
        if (confScript != null) {
            return confScript;
        }
        return getDefaultScript();
    }

    public Script getDefaultScript() {
        final Script script = new Script();
        script.setOxScriptType(ProgrammingLanguage.PYTHON.getValue());
        script.setOxScript(loadDefaultScriptFromFile());
        return script;
    }

    public String loadDefaultScriptFromFile() {
        try {
            final InputStream inputStream = ScriptService.class.getClassLoader().getResourceAsStream(DEFAULT_SCRIPT_FILE_NAME);
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

}
