package org.ox.oxprox.external;

import org.ox.oxprox.ldap.Op;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 11/03/2014
 */

public class DefaultExternalOpDiscovery implements ExternalOpDiscovery {

    @Override
    public ExternalOpDiscoveryMode getMode() {
        return ExternalOpDiscoveryMode.INTERACTIVE;
    }

    @Override
    public boolean initPage(int step, AllowedOpRuleContext context) {
        return false;
    }

    @Override
    public boolean isAllowed(Op op, AllowedOpRuleContext context) {
        return true;
    }

    @Override
    public String getPageForStep(int step) {
        return "/discovery.xhtml";
    }

    @Override
    public int getStepsCount() {
        return 1;
    }
}
