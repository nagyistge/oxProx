package org.ox.oxprox;

import com.google.inject.Inject;
import junit.framework.Assert;
import org.ox.oxprox.ldap.Script;
import org.ox.oxprox.service.ScriptService;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

@Guice(modules = TestAppModule.class)
public class ScriptServiceTest {

    @Inject
    ScriptService scriptService;

    @Test
    public void discoveryScript() {
        final Script confScript = scriptService.getConfScript();
        Assert.assertNotNull(confScript);
    }

    @Test
    public void defaultScript() {
        final Script defaultScript = scriptService.getDefaultScript();
        Assert.assertNotNull(defaultScript);
    }
}
