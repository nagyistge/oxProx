package org.ox.oxprox.service;

import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.ldap.Op;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 04/03/2014
 */

public class OpService {

    private static final Logger LOG = LoggerFactory.getLogger(OpService.class);

    @Inject
    LdapEntryManager ldapManager;
    @Inject
    Configuration conf;

    public List<Op> getAllOps() {
        try {
            final Filter filter = Filter.create("&(inum=*)");
            return ldapManager.findEntries(conf.getOpBaseDn(), Op.class, filter);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}
