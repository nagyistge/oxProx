package org.ox.oxprox.admin.server;

import com.google.common.collect.Lists;
import org.ox.oxprox.admin.shared.Client;
import org.ox.oxprox.admin.shared.OP;
import org.ox.oxprox.ldap.Op;
import org.ox.oxprox.ldap.oxProxClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class Convertor {

    private static final Logger LOG = LoggerFactory.getLogger(Convertor.class);

    private Convertor() {
    }

    public static List<OP> convertOps(List<Op> allOps) {
        final List<OP> result = Lists.newArrayList();
        if (allOps != null) {
            for (Op op : allOps) {
                result.add(convert(op));
            }
        }
        return result;
    }

    public static List<Client> convertClients(List<oxProxClient> clients) {
        final List<Client> result = Lists.newArrayList();
        if (clients != null) {
            for (oxProxClient c : clients) {
                result.add(convert(c));
            }
        }
        return result;
    }

    private static Client convert(oxProxClient client) {
        final Client result = new Client();
        result.setDn(client.getDn());
        result.setDisplayName(client.getDisplayName());
        result.setClaimMappingMap(client.getClaimMappingMap());
        result.setScopeMappingMap(client.getScopeMappingMap());
        result.setInum(client.getClientId());
        try {
            result.setClientMapping(client.getClientMapping());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    private static OP convert(Op op) {
        final OP result = new OP();
        result.setDn(op.getDn());
        result.setCity(op.getCity());
        result.setCountry(op.getCountry());
        result.setDisplayName(op.getDisplayName());
        result.setDomain(op.getDomain());
        result.setInum(op.getId());
        result.setOpId(op.getOpId());
        return result;
    }
}

