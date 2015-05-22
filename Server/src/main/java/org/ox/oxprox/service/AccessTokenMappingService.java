package org.ox.oxprox.service;

import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.util.StaticUtils;
import org.apache.commons.lang.StringUtils;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.ldap.AccessTokenMap;
import org.ox.oxprox.model.server.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 22/05/2014
 */

public class AccessTokenMappingService {

    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenMappingService.class);

    @Inject
    Configuration conf;
    @Inject
    LdapEntryManager ldapEntryManager;

    public AccessTokenMappingService() {
        scheduleCleanupTimer();
    }

    private void scheduleCleanupTimer() {
        ModelUtils.createExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                cleanup(new Date());
            }
        }, 5, 60, TimeUnit.MINUTES);
    }

    public void cleanup(Date now) {
        try {
            final Filter filter = Filter.create(String.format("(oxAuthExpiration<=%s)", StaticUtils.encodeGeneralizedTime(now)));
            final List<AccessTokenMap> entries = ldapEntryManager.findEntries(
                    conf.getTokenBaseDn(), AccessTokenMap.class, filter);
            if (entries != null && !entries.isEmpty()) {
                for (AccessTokenMap p : entries) {
                    ldapEntryManager.remove(p);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void persist(AccessTokenMap map) {
        final String dn = map.getDn();
        if (StringUtils.isBlank(dn)) {
            map.setDn(dn(map.getAccessToken()));
        }
        ldapEntryManager.persist(map);
    }

    private String dn(String accessToken) {
        return String.format("oxProxyAccessToken=%s,%s", accessToken, conf.getTokenBaseDn());
    }

    public AccessTokenMap getMap(String accessToken) {
        return ldapEntryManager.find(AccessTokenMap.class, dn(accessToken));
    }

    public String getClientId(String accessToken) {
        final AccessTokenMap map = getMap(accessToken);
        if (map != null) {
            return map.getClientId();
        }
        return null;
    }

}
