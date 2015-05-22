package org.ox.oxprox.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Filter;
import org.apache.commons.lang.StringUtils;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.ldap.oxProxClient;
import org.ox.oxprox.model.gwt.ClientMapping;
import org.ox.oxprox.model.ws.ResponseErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/03/2014
 */

public class ClientService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientService.class);

    private final Cache<String, oxProxClient> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    @Inject
    Configuration conf;
    @Inject
    LdapEntryManager ldapEntryManager;
    @Inject
    ErrorService errorService;

    public oxProxClient getClientWithException(String clientId) {
        final oxProxClient client = getClient(clientId);
        if (client != null) {
            return client;
        } else {
            LOG.warn("Failed to resolve oxProx client, client_id: {}.", clientId);
            throw new WebApplicationException(errorService.response(Response.Status.BAD_REQUEST, ResponseErrorType.INVALID_CLIENT));
        }
    }

    public ClientMapping.Client getOpClient(String opDomain, oxProxClient client) {
        try {
            final Map<String, ClientMapping.Client> map = client.getClientMapping().getMap();
            if (map != null) {
                final ClientMapping.Client result = map.get(opDomain);
                if (result != null) {
                    return result;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.warn("Failed to resolve OP client.");
        throw new WebApplicationException(errorService.response(Response.Status.BAD_REQUEST, ResponseErrorType.NO_MAPPING_TO_OP_CLIENT));
    }

    public oxProxClient getClient(String clientId) {
        if (clientId != null && !clientId.isEmpty()) {
            oxProxClient result = getClientByDn(buildClientDn(clientId));
            LOG.debug("Found {} entries for client id = {}", result != null ? 1 : 0, clientId);

            return result;
        }
        return null;
    }

    public oxProxClient getClientByDn(String dn) {
        oxProxClient client = cache.getIfPresent(dn);
        if (client == null) {
            client = ldapEntryManager.find(oxProxClient.class, dn);
            cache.put(dn, client);
        } else {
            LOG.trace("Get client from cache by Dn '{0}'", dn);
        }

        return client;
    }

    public List<oxProxClient> getAllClients() {
        try {
            final Filter filter = Filter.create("&(inum=*)");
            return ldapEntryManager.findEntries(conf.getClientBaseDn(), oxProxClient.class, filter);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public String buildClientDn(String p_clientId) {
        final StringBuilder dn = new StringBuilder();
        dn.append(String.format("inum=%s,", p_clientId));
        dn.append(conf.getClientBaseDn()); // ou=client,ou=oxProx,o=@!1111,o=gluu
        return dn.toString();
    }

    /**
     * Parse list of strings:
     * a=b
     * c=d
     * <p/>
     * into map, keys: a,c; values: b,d
     *
     * @param stringList list of strings with "=" as separator
     * @return parsed map
     */
    public static Map<String, String> parseMap(List<String> stringList) {
        final Map<String, String> result = Maps.newHashMap();
        if (stringList != null && !stringList.isEmpty()) {
            for (String entryString : stringList) {
                final Map.Entry<String, String> entry = parseEntry(entryString);
                if (entry != null) {
                    result.put(entry.getKey().trim(), entry.getValue());
                }
            }
        }
        return result;
    }

    private static Map.Entry<String, String> parseEntry(String entry) {
        final String[] split = StringUtils.split(entry, "=");
        if (split != null && split.length == 2) {
            return new Map.Entry<String, String>() {
                @Override
                public String getKey() {
                    return split[0];
                }

                @Override
                public String getValue() {
                    return split[1];
                }

                @Override
                public String setValue(String value) {
                    return null;
                }
            };
        }
        return null;
    }
}
