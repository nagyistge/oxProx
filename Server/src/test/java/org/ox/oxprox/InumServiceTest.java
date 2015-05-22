package org.ox.oxprox;

import com.google.inject.Inject;
import junit.framework.Assert;
import org.ox.oxprox.service.InumService;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

@Guice(modules = TestAppModule.class)
public class InumServiceTest {

    @Inject
    InumService inumService;

    @Test
    public void scriptInumGen() {
        final String inum = inumService.generateScript();
        Assert.assertNotNull(inum);
        System.out.println(inum);
    }

    @Test
    public void opInumGen() {
        final String inum = inumService.generateOp();
        Assert.assertNotNull(inum);
        System.out.println(inum);
    }
}
