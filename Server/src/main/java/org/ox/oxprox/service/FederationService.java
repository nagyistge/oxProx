package org.ox.oxprox.service;

import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.ox.oxprox.conf.Configuration;
import org.ox.oxprox.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xdi.oxauth.model.federation.FederationMetadata;
import org.xdi.oxauth.model.federation.FederationOP;
import org.xdi.oxauth.model.federation.FederationRP;

import java.util.Collections;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 24/02/2014
 */

public class FederationService {

    private static final Logger LOG = LoggerFactory.getLogger(FederationService.class);

    @Inject
    LdapEntryManager ldapManager;
    @Inject
    Configuration configuration;

    public List<FederationMetadata> getMetadataList() {
        try {
            final FederationMetadata m = new FederationMetadata();
            m.setDn(configuration.getFederationMetadataBaseDn());

            final List<FederationMetadata> entries = ldapManager.findEntries(m);
            if (entries != null) {
                return entries;
            }
        } catch (Exception e) {
            // catch all exception because ldap manager may throw any exception inside
            LOG.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    public FederationMetadata getMetadataById(String p_federationId, boolean p_loadSatellites) {
        try {
            final FederationMetadata m = new FederationMetadata();
            m.setDn(configuration.getFederationMetadataBaseDn());
            m.setId(p_federationId);

            final List<FederationMetadata> entries = ldapManager.findEntries(m);
            if (entries != null && !entries.isEmpty()) {
                final int size = entries.size();
                if (size == 1) {
                    final FederationMetadata result = entries.get(0);
                    if (p_loadSatellites) {
                        // load rps
                        final List<String> rps = result.getRps();
                        if (rps != null && !rps.isEmpty()) {
                            final Filter rpFilter = Utils.createAnyFilterFromDnList("inum", rps);
                            if (rpFilter != null) {
                                final List<FederationRP> rpList = ldapManager.findEntries(
                                        configuration.getFederationRpBaseDn(),
                                        FederationRP.class, rpFilter);
                                result.setRpList(rpList);
                            } else {
                                LOG.trace("Skip loading of RPs for metadataId: {0}", p_federationId);
                            }
                        }

                        // load ops
                        final List<String> ops = result.getOps();
                        if (ops != null && !ops.isEmpty()) {
                            final Filter opFilter = Utils.createAnyFilterFromDnList("inum", ops);
                            if (opFilter != null) {
                                final List<FederationOP> opList = ldapManager.findEntries(
                                        configuration.getFederationOpBaseDn(),
                                        FederationOP.class, opFilter);
                                result.setOpList(opList);
                            } else {
                                LOG.trace("Skip loading of OPs for metadataId: {0}", p_federationId);
                            }
                        }

                    }
                    return result;
                } else {
                    LOG.error("There is more then one federation metadata object with id {0}", p_federationId);
                }
            } else {
                LOG.error("Invalid federation metadata id: {0}", p_federationId);
            }
        } catch (Exception e) {
            // catch all exception because ldap manager may throw any exception inside
            LOG.error(e.getMessage(), e);
        }
        return null;
    }


    public List<FederationOP> getOpList() {
        return Collections.emptyList();
    }

}
