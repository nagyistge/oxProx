package org.ox.oxprox;

import org.ox.oxprox.guice.AppModule;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 12/03/2014
 */

public class TestAppModule extends AppModule {

    public String getConfigFileLocation() {
//        return ConfigurationFactory.CONFIG_FILE_NAME;
        return "oxProx-config-dev.json";
    }
}
