package org.ox.oxprox.admin.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * @author Yuriy Zabrovarnyy
 * @version 0.9, 18/06/2014
 */

public class GuiceServletConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {

            @Override
            protected void configureServlets() {
                serve("/admin/oxproxadmin").with(ServiceImpl.class);
            }
        }, new AdminAppModule());
    }
}
