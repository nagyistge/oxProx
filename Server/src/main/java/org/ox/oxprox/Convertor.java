package org.ox.oxprox;

import org.ox.oxprox.ldap.Op;
import org.xdi.oxauth.model.federation.FederationOP;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 24/02/2014
 */

public class Convertor {

    private Convertor() {
    }

    public static List<Op> convert(List<FederationOP> list) {
        final List<Op> result = new ArrayList<Op>();
        for (FederationOP op: list) {
            result.add(convert(op));
        }
        return result;
    }

    private static Op convert(FederationOP op) {
        final Op result = new Op();
        result.setDomain(op.getDomain());
        result.setDisplayName(op.getDisplayName());
        return result;
    }
}
