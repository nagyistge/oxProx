package org.ox.oxprox.external;

import org.ox.oxprox.ldap.Op;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 11/02/2014
 */

public interface ExternalOpDiscovery {

    public ExternalOpDiscoveryMode getMode();

    public boolean initPage(int step, AllowedOpRuleContext context);

    public boolean isAllowed(Op op, AllowedOpRuleContext context);

    public String getPageForStep(int step);

    public int getStepsCount();
}
